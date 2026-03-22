package it.staffplugin.commands;

import it.staffplugin.StaffPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class EcoCommand implements CommandExecutor {
    private final StaffPlugin plugin;
    public EcoCommand(StaffPlugin p) { this.plugin = p; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("staffplugin.staff.*")) { sender.sendMessage(plugin.msg("no-permission")); return true; }
        if (args.length < 3) {
            sender.sendMessage(StaffPlugin.colorize("&cUso: /eco <give|take|set> <player> <importo>"));
            return true;
        }

        String action = args[0].toLowerCase();
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) { sender.sendMessage(plugin.msg("player-not-found").replace("{player}", args[1])); return true; }

        double amount;
        try { amount = Double.parseDouble(args[2]); }
        catch (NumberFormatException e) { sender.sendMessage(StaffPlugin.colorize("&cImporto non valido!")); return true; }

        if (amount < 0) { sender.sendMessage(StaffPlugin.colorize("&cL'importo deve essere positivo!")); return true; }

        String uuid = target.getUniqueId().toString();
        switch (action) {
            case "give" -> {
                plugin.getDataManager().addBalance(uuid, amount);
                sender.sendMessage(StaffPlugin.colorize("&aAggiunti &e$" + String.format("%.2f", amount) + " &aa &e" + target.getName()));
                target.sendMessage(StaffPlugin.colorize("&aHai ricevuto &e$" + String.format("%.2f", amount) + " &ada &e" + sender.getName() + "&a!"));
            }
            case "take" -> {
                plugin.getDataManager().removeBalance(uuid, amount);
                sender.sendMessage(StaffPlugin.colorize("&aRimossi &e$" + String.format("%.2f", amount) + " &ada &e" + target.getName()));
                target.sendMessage(StaffPlugin.colorize("&cTi sono stati rimossi &e$" + String.format("%.2f", amount) + " &cda &e" + sender.getName() + "&c."));
            }
            case "set" -> {
                plugin.getDataManager().setBalance(uuid, amount);
                sender.sendMessage(StaffPlugin.colorize("&aSaldo di &e" + target.getName() + " &aimpostato a &e$" + String.format("%.2f", amount)));
                target.sendMessage(StaffPlugin.colorize("&aIl tuo saldo è stato impostato a &e$" + String.format("%.2f", amount)));
            }
            default -> sender.sendMessage(StaffPlugin.colorize("&cAzione non valida! Usa: give, take, set"));
        }
        return true;
    }
}
