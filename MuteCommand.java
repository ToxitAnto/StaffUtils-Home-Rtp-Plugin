package it.staffplugin.commands;

import it.staffplugin.StaffPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class BanCommand implements CommandExecutor {
    private final StaffPlugin plugin;
    public BanCommand(StaffPlugin p) { this.plugin = p; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("staffplugin.staff.ban")) { sender.sendMessage(plugin.msg("no-permission")); return true; }
        if (args.length < 1) { sender.sendMessage(StaffPlugin.colorize("&cUso: /ban <player> [motivo]")); return true; }

        Player target = Bukkit.getPlayer(args[0]);
        String reason = args.length > 1 ? String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length)) : "Nessun motivo";

        String uuid;
        String name;

        if (target != null) {
            uuid = target.getUniqueId().toString();
            name = target.getName();
        } else {

            @SuppressWarnings("deprecation")
            var offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
            if (!offlinePlayer.hasPlayedBefore()) {
                sender.sendMessage(plugin.msg("player-not-found").replace("{player}", args[0]));
                return true;
            }
            uuid = offlinePlayer.getUniqueId().toString();
            name = offlinePlayer.getName() != null ? offlinePlayer.getName() : args[0];
        }

        plugin.getDataManager().ban(uuid, reason, sender.getName());

        String banMsg = plugin.msgRaw("ban-message")
                .replace("{reason}", reason)
                .replace("{by}", sender.getName());

        if (target != null) {
            target.kickPlayer(banMsg);
        }

        Bukkit.broadcastMessage(StaffPlugin.colorize("&c" + name + " &7è stato bannato da &c" + sender.getName() + " &7per: &e" + reason));
        return true;
    }
}
