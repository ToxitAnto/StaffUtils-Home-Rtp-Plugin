package it.staffplugin.commands;

import it.staffplugin.StaffPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class BanHistoryCommand implements CommandExecutor {
    private final StaffPlugin plugin;
    public BanHistoryCommand(StaffPlugin p) { this.plugin = p; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("staffplugin.staff.banhistory")) { sender.sendMessage(plugin.msg("no-permission")); return true; }
        if (args.length < 1) { sender.sendMessage(StaffPlugin.colorize("&cUso: /banhistory <player>")); return true; }

        @SuppressWarnings("deprecation")
        var offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
        String uuid = offlinePlayer.getUniqueId().toString();

        List<Map<String, String>> history = plugin.getDataManager().getBanHistory(uuid);

        sender.sendMessage(StaffPlugin.colorize("&8&m-----------------------------"));
        sender.sendMessage(StaffPlugin.colorize("&6Storico ban di &e" + args[0] + " &6(" + history.size() + " ban)"));
        sender.sendMessage(StaffPlugin.colorize("&8&m-----------------------------"));

        if (history.isEmpty()) {
            sender.sendMessage(StaffPlugin.colorize("&7Nessun ban trovato."));
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            for (int i = 0; i < history.size(); i++) {
                Map<String, String> entry = history.get(i);
                String timestamp = entry.get("timestamp");
                String dateStr = "N/A";
                try {
                    if (timestamp != null && !timestamp.isEmpty()) {
                        dateStr = sdf.format(new Date(Long.parseLong(timestamp)));
                    }
                } catch (NumberFormatException ignored) {}

                sender.sendMessage(StaffPlugin.colorize(
                        "&e#" + (i + 1) + " &7- &cMotivo: &f" + entry.get("reason") +
                        " &7| &cDa: &f" + entry.get("by") +
                        " &7| &cData: &f" + dateStr
                ));
            }
        }
        sender.sendMessage(StaffPlugin.colorize("&8&m-----------------------------"));

        if (plugin.getDataManager().isBanned(uuid)) {
            Map<String, String> info = plugin.getDataManager().getBanInfo(uuid);
            sender.sendMessage(StaffPlugin.colorize("&cSTATO ATTUALE: &4BANNATO &7- &e" + info.get("reason")));
        } else {
            sender.sendMessage(StaffPlugin.colorize("&aSTATO ATTUALE: &2Non bannato"));
        }

        return true;
    }
}
