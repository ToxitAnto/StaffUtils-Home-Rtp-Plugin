package it.staffplugin.commands;

import it.staffplugin.StaffPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CheckAltCommand implements CommandExecutor {
    private final StaffPlugin plugin;
    public CheckAltCommand(StaffPlugin p) { this.plugin = p; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("staffplugin.staff.checkalt")) { sender.sendMessage(plugin.msg("no-permission")); return true; }
        if (args.length < 1) { sender.sendMessage(StaffPlugin.colorize("&cUso: /checkalt <player>")); return true; }

        @SuppressWarnings("deprecation")
        var offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
        String uuid = offlinePlayer.getUniqueId().toString();
        String targetIp = plugin.getDataManager().getPlayerIp(uuid);

        sender.sendMessage(StaffPlugin.colorize("&8&m-----------------------------"));
        sender.sendMessage(StaffPlugin.colorize("&6Controllo Alt di &e" + args[0]));

        if (targetIp == null) {
            sender.sendMessage(StaffPlugin.colorize("&cNessun IP registrato per questo giocatore."));
            sender.sendMessage(StaffPlugin.colorize("&8&m-----------------------------"));
            return true;
        }

        sender.sendMessage(StaffPlugin.colorize("&7IP: &e" + targetIp));

        List<String> alts = new ArrayList<>();
        Map<String, String> allIps = plugin.getDataManager().getAllPlayerIps();
        for (Map.Entry<String, String> entry : allIps.entrySet()) {
            if (entry.getValue().equals(targetIp) && !entry.getKey().equals(uuid)) {
                var op = Bukkit.getOfflinePlayer(java.util.UUID.fromString(entry.getKey()));
                String name = op.getName() != null ? op.getName() : entry.getKey();
                boolean banned = plugin.getDataManager().isBanned(entry.getKey());
                alts.add(name + (banned ? " &c[BANNATO]" : " &a[OK]"));
            }
        }

        if (alts.isEmpty()) {
            sender.sendMessage(StaffPlugin.colorize("&7Nessun alt trovato per questo IP."));
        } else {
            sender.sendMessage(StaffPlugin.colorize("&eAlt trovati (&c" + alts.size() + "&e):"));
            for (String alt : alts) {
                sender.sendMessage(StaffPlugin.colorize("  &7- &f" + alt));
            }
        }
        sender.sendMessage(StaffPlugin.colorize("&8&m-----------------------------"));
        return true;
    }
}
