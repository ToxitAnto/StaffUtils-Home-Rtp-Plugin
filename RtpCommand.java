package it.staffplugin.commands;

import it.staffplugin.StaffPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class BalanceCommand implements CommandExecutor {
    private final StaffPlugin plugin;
    public BalanceCommand(StaffPlugin p) { this.plugin = p; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) { sender.sendMessage(plugin.msg("player-only")); return true; }

        Player target;
        if (args.length > 0 && sender.hasPermission("staffplugin.staff.*")) {
            target = Bukkit.getPlayer(args[0]);
            if (target == null) { sender.sendMessage(plugin.msg("player-not-found").replace("{player}", args[0])); return true; }
        } else {
            target = player;
        }

        double bal = plugin.getDataManager().getBalance(target.getUniqueId().toString());
        if (target.equals(player)) {
            player.sendMessage(StaffPlugin.colorize("&6Il tuo saldo: &e$" + String.format("%.2f", bal)));
        } else {
            player.sendMessage(StaffPlugin.colorize("&6Saldo di &e" + target.getName() + "&6: &e$" + String.format("%.2f", bal)));
        }
        return true;
    }
}
