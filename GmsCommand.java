package it.staffplugin.managers;

import it.staffplugin.StaffPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class VanishManager {

    private final StaffPlugin plugin;
    private final Set<UUID> vanished = new HashSet<>();

    public VanishManager(StaffPlugin plugin) {
        this.plugin = plugin;
    }

    public void vanish(Player player) {
        vanished.add(player.getUniqueId());
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.hasPermission("staffplugin.staff.vanish")) {
                p.hidePlayer(plugin, player);
            }
        }
        player.sendMessage(StaffPlugin.colorize("&aOra sei in vanish."));
    }

    public void unvanish(Player player) {
        vanished.remove(player.getUniqueId());
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.showPlayer(plugin, player);
        }
        player.sendMessage(StaffPlugin.colorize("&cNon sei più in vanish."));
    }

    public boolean isVanished(Player player) {
        return vanished.contains(player.getUniqueId());
    }

    public void toggle(Player player) {
        if (isVanished(player)) unvanish(player);
        else vanish(player);
    }

    public void applyVanishOnJoin(Player joining) {

        for (UUID uuid : vanished) {
            Player vanishedPlayer = Bukkit.getPlayer(uuid);
            if (vanishedPlayer != null && !joining.hasPermission("staffplugin.staff.vanish")) {
                joining.hidePlayer(plugin, vanishedPlayer);
            }
        }
    }
}
