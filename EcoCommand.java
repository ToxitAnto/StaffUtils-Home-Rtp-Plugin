package it.staffplugin.commands;

import it.staffplugin.StaffPlugin;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class DelHomeCommand implements CommandExecutor {
    private final StaffPlugin plugin;
    public DelHomeCommand(StaffPlugin p) { this.plugin = p; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) { sender.sendMessage(plugin.msg("player-only")); return true; }
        if (!player.hasPermission("staffplugin.home")) { player.sendMessage(plugin.msg("no-permission")); return true; }
        if (args.length < 1) { player.sendMessage(StaffPlugin.colorize("&cUso: /delhome <nome>")); return true; }

        String name = args[0];
        boolean deleted = plugin.getHomeManager().deleteHome(player.getUniqueId(), name);
        if (!deleted) {
            player.sendMessage(plugin.msg("home-not-found").replace("{home}", name));
        } else {
            player.sendMessage(StaffPlugin.colorize(
                    plugin.getConfig().getString("messages.home-deleted", "&aHome {home} eliminata!").replace("{home}", name)));
        }
        return true;
    }
}
