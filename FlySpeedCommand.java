package it.staffplugin.commands;

import it.staffplugin.StaffPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class UnmuteCommand implements CommandExecutor {
    private final StaffPlugin plugin;
    public UnmuteCommand(StaffPlugin p) { this.plugin = p; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("staffplugin.staff.unmute")) { sender.sendMessage(plugin.msg("no-permission")); return true; }
        if (args.length < 1) { sender.sendMessage(StaffPlugin.colorize("&cUso: /unmute <player>")); return true; }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) { sender.sendMessage(plugin.msg("player-not-found").replace("{player}", args[0])); return true; }

        if (!plugin.getMuteManager().isMuted(target.getUniqueId())) {
            sender.sendMessage(plugin.msg("not-muted"));
            return true;
        }

        plugin.getMuteManager().unmute(target.getUniqueId());
        target.sendMessage(StaffPlugin.colorize("&aSei stato smutato!"));
        sender.sendMessage(StaffPlugin.colorize("&aHai smutato &e" + target.getName() + "&a!"));
        return true;
    }
}
