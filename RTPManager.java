package it.staffplugin.listeners;

import it.staffplugin.StaffPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    private final StaffPlugin plugin;

    public ChatListener(StaffPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (plugin.getMuteManager().isMuted(player.getUniqueId())) {
            event.setCancelled(true);
            String reason = plugin.getMuteManager().getMuteReason(player.getUniqueId());
            player.sendMessage(StaffPlugin.colorize("&cSei mutato! Motivo: &e" + reason));
        }
    }
}
