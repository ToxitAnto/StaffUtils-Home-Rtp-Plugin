package it.staffplugin.managers;

import it.staffplugin.StaffPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class RTPManager {

    private final StaffPlugin plugin;
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final Random random = new Random();

    public RTPManager(StaffPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean isOnCooldown(Player player) {
        if (!cooldowns.containsKey(player.getUniqueId())) return false;
        long cooldownSec = plugin.getConfig().getLong("rtp.cooldown", 300);
        long elapsed = (System.currentTimeMillis() - cooldowns.get(player.getUniqueId())) / 1000;
        return elapsed < cooldownSec;
    }

    public long getRemainingCooldown(Player player) {
        if (!cooldowns.containsKey(player.getUniqueId())) return 0;
        long cooldownSec = plugin.getConfig().getLong("rtp.cooldown", 300);
        long elapsed = (System.currentTimeMillis() - cooldowns.get(player.getUniqueId())) / 1000;
        return Math.max(0, cooldownSec - elapsed);
    }

    public void teleport(Player player) {
        int delay = plugin.getConfig().getInt("rtp.teleport-delay", 5);
        if (delay > 0) {
            player.sendMessage(StaffPlugin.colorize("&aTeleportazione casuale tra &e" + delay + " &asecondi... Non muoverti!"));
            Location startLoc = player.getLocation().clone();
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (!player.isOnline()) return;
                if (player.getLocation().distance(startLoc) > 1) {
                    player.sendMessage(StaffPlugin.colorize("&cTeleportazione annullata perché ti sei mosso!"));
                    return;
                }
                doTeleport(player);
            }, delay * 20L);
        } else {
            doTeleport(player);
        }
    }

    private void doTeleport(Player player) {
        Location dest = findSafeLocation();
        if (dest == null) {
            player.sendMessage(StaffPlugin.colorize("&cNon è stato possibile trovare una posizione sicura. Riprova!"));
            return;
        }
        player.teleport(dest);
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
        String msg = plugin.getConfig().getString("rtp.message", "&aTeleportato in una posizione casuale!");
        player.sendMessage(StaffPlugin.colorize(msg));
    }

    private Location findSafeLocation() {
        String worldName = plugin.getConfig().getString("rtp.world", "world");
        World world = Bukkit.getWorld(worldName);
        if (world == null) return null;

        int minRadius = plugin.getConfig().getInt("rtp.min-radius", 500);
        int maxRadius = plugin.getConfig().getInt("rtp.max-radius", 10000);
        int maxAttempts = plugin.getConfig().getInt("rtp.max-attempts", 15);

        for (int i = 0; i < maxAttempts; i++) {
            int angle = random.nextInt(360);
            int distance = minRadius + random.nextInt(maxRadius - minRadius);
            int x = (int) (Math.cos(Math.toRadians(angle)) * distance);
            int z = (int) (Math.sin(Math.toRadians(angle)) * distance);
            int y = world.getHighestBlockYAt(x, z) + 1;

            Location loc = new Location(world, x + 0.5, y, z + 0.5);
            if (isSafe(loc)) return loc;
        }
        return null;
    }

    private boolean isSafe(Location loc) {
        Material below = loc.clone().subtract(0, 1, 0).getBlock().getType();
        Material feet = loc.getBlock().getType();
        Material head = loc.clone().add(0, 1, 0).getBlock().getType();

        if (!below.isSolid()) return false;
        if (below == Material.LAVA || below == Material.FIRE) return false;
        if (!feet.isAir() || !head.isAir()) return false;
        return true;
    }
}
