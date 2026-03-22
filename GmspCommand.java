package it.staffplugin.commands;

import it.staffplugin.StaffPlugin;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class SetSpawnCommand implements CommandExecutor {
    private final StaffPlugin plugin;
    public SetSpawnCommand(StaffPlugin p) { this.plugin = p; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) { sender.sendMessage(plugin.msg("player-only")); return true; }
        if (!player.hasPermission("staffplugin.setspawn")) { player.sendMessage(plugin.msg("no-permission")); return true; }

        plugin.getSpawnManager().setSpawn(player.getLocation());
        player.sendMessage(StaffPlugin.colorize(
                plugin.getConfig().getString("messages.spawn-set", "&aSpawn impostato!")));
        return true;
    }
}
