package it.staffplugin.commands;

import it.staffplugin.StaffPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class InvseeCommand implements CommandExecutor {
    private final StaffPlugin plugin;
    public InvseeCommand(StaffPlugin p) { this.plugin = p; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) { sender.sendMessage(plugin.msg("player-only")); return true; }
        if (!player.hasPermission("staffplugin.staff.invsee")) { player.sendMessage(plugin.msg("no-permission")); return true; }
        if (args.length < 1) { player.sendMessage(StaffPlugin.colorize("&cUso: /invsee <player>")); return true; }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(plugin.msg("invsee-offline"));
            return true;
        }

        player.openInventory(target.getInventory());
        player.sendMessage(StaffPlugin.colorize("&aStai vedendo l'inventario di &e" + target.getName()));
        return true;
    }
}
