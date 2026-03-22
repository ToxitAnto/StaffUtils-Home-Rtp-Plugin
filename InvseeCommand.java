package it.staffplugin.commands;

import it.staffplugin.StaffPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.*;

import java.util.*;

public class DupeIpCommand implements CommandExecutor {
    private final StaffPlugin plugin;
    public DupeIpCommand(StaffPlugin p) { this.plugin = p; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("staffplugin.staff.dupeip")) { sender.sendMessage(plugin.msg("no-permission")); return true; }
        if (args.length < 1) { sender.sendMessage(StaffPlugin.colorize("&cUso: /dupeip <player>")); return true; }

        @SuppressWarnings("deprecation")
        var offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
        String uuid = offlinePlayer.getUniqueId().toString();
        String targetIp = plugin.getDataManager().getPlayerIp(uuid);

        sender.sendMessage(StaffPlugin.colorize("&8&m-----------------------------"));
        sender.sendMessage(StaffPlugin.colorize("&6DupeIP per &e" + args[0]));

        if (targetIp == null) {
            sender.sendMessage(StaffPlugin.colorize("&cNessun IP registrato per questo giocatore."));
            sender.sendMessage(StaffPlugin.colorize("&8&m-----------------------------"));
            return true;
        }

        sender.sendMessage(StaffPlugin.colorize("&7IP: &e" + targetIp));

        Map<String, List<String>> ipMap = new HashMap<>();
        Map<String, String> allIps = plugin.getDataManager().getAllPlayerIps();
        for (Map.Entry<String, String> entry : allIps.entrySet()) {
            ipMap.computeIfAbsent(entry.getValue(), k -> new ArrayList<>());
            var op = Bukkit.getOfflinePlayer(java.util.UUID.fromString(entry.getKey()));
            String name = op.getName() != null ? op.getName() : entry.getKey();
            boolean banned = plugin.getDataManager().isBanned(entry.getKey());
            ipMap.get(entry.getValue()).add(name + (banned ? " &c[BANNATO]" : ""));
        }

        List<String> accounts = ipMap.getOrDefault(targetIp, new ArrayList<>());
        sender.sendMessage(StaffPlugin.colorize("&eAccount con questo IP (&b" + accounts.size() + "&e):"));
        for (String account : accounts) {
            sender.sendMessage(StaffPlugin.colorize("  &7» &f" + account));
        }
        sender.sendMessage(StaffPlugin.colorize("&8&m-----------------------------"));
        return true;
    }
}
