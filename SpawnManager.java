package it.staffplugin.listeners;

import it.staffplugin.StaffPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.*;

public class PlayerJoinListener implements Listener {

    private final StaffPlugin plugin;

    public PlayerJoinListener(StaffPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();

        if (player.getAddress() != null) {
            String ip = player.getAddress().getAddress().getHostAddress();
            plugin.getDataManager().registerIp(uuid, ip);

            if (plugin.getDataManager().isIpBanned(ip)) {
                var info = plugin.getDataManager().getIpBanInfo(ip);
                String msg = plugin.msgRaw("ban-message")
                        .replace("{reason}", "[IP BAN] " + info.get("reason"))
                        .replace("{by}", info.get("by"));
                Bukkit.getScheduler().runTask(plugin, () -> player.kickPlayer(msg));
                return;
            }
        }

        if (plugin.getDataManager().isBanned(uuid)) {
            var info = plugin.getDataManager().getBanInfo(uuid);
            String msg = plugin.msgRaw("ban-message")
                    .replace("{reason}", info.get("reason"))
                    .replace("{by}", info.get("by"));
            Bukkit.getScheduler().runTask(plugin, () -> player.kickPlayer(msg));
            return;
        }

        plugin.getVanishManager().applyVanishOnJoin(player);

        boolean tpOnJoin = plugin.getConfig().getBoolean("spawn.teleport-on-join", true);
        if (tpOnJoin && plugin.getSpawnManager().hasSpawn()) {
            Bukkit.getScheduler().runTask(plugin, () ->
                    player.teleport(plugin.getSpawnManager().getSpawn()));
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (plugin.getVanishManager().isVanished(player)) {
            event.setQuitMessage(null);
        }
    }
}
