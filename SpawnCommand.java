package it.staffplugin.commands;

import it.staffplugin.StaffPlugin;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class GmsCommand implements CommandExecutor {
    private final StaffPlugin plugin;
    public GmsCommand(StaffPlugin p) { this.plugin = p; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("staffplugin.staff.gms")) {
            sender.sendMessage(plugin.msg("no-permission")); return true;
        }
        Player target;
        if (args.length > 0) {
            target = Bukkit.getPlayer(args[0]);
            if (target == null) { sender.sendMessage(plugin.msg("player-not-found").replace("{player}", args[0])); return true; }
        } else if (sender instanceof Player p) {
            target = p;
        } else {
            sender.sendMessage(plugin.msg("player-only")); return true;
        }
        target.setGameMode(GameMode.SURVIVAL);
        target.sendMessage(StaffPlugin.colorize("&aGamemode &eSurvival &aimpostato!"));
        if (!sender.getName().equals(target.getName()))
            sender.sendMessage(StaffPlugin.colorize("&aHai impostato il gamemode di &e" + target.getName() + " &ain Survival!"));
        return true;
    }
}
