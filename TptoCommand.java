package it.staffplugin.commands;

import it.staffplugin.StaffPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class IpBanCommand implements CommandExecutor {
    private final StaffPlugin plugin;
    public IpBanCommand(StaffPlugin p) { this.plugin = p; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("staffplugin.staff.ipban")) { sender.sendMessage(plugin.msg("no-permission")); return true; }
        if (args.length < 1) { sender.sendMessage(StaffPlugin.colorize("&cUso: /ipban <player> [motivo]")); return true; }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) { sender.sendMessage(plugin.msg("player-not-found").replace("{player}", args[0])); return true; }

        String ip = plugin.getDataManager().getPlayerIp(target.getUniqueId().toString());
        if (ip == null && target.getAddress() != null) {
            ip = target.getAddress().getAddress().getHostAddress();
        }
        if (ip == null) {
            sender.sendMessage(StaffPlugin.colorize("&cNon è stato possibile trovare l'IP di &e" + target.getName()));
            return true;
        }

        String reason = args.length > 1 ? String.join(" ", Arrays.copyOfRange(args, 1, args.length)) : "Nessun motivo";
        plugin.getDataManager().ipBan(ip, reason, sender.getName());
        plugin.getDataManager().ban(target.getUniqueId().toString(), "[IP BAN] " + reason, sender.getName());

        String banMsg = plugin.msgRaw("ban-message").replace("{reason}", reason).replace("{by}", sender.getName());
        target.kickPlayer(banMsg);

        sender.sendMessage(StaffPlugin.colorize("&aIP di &e" + target.getName() + " &a(" + ip + ") &abannato per: &e" + reason));
        return true;
    }
}
