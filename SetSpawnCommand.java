package it.staffplugin.commands;

import it.staffplugin.StaffPlugin;
import it.staffplugin.gui.ShopGui;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class ShopCommand implements CommandExecutor {
    private final StaffPlugin plugin;
    public ShopCommand(StaffPlugin p) { this.plugin = p; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) { sender.sendMessage(plugin.msg("player-only")); return true; }
        if (!player.hasPermission("staffplugin.shop")) { player.sendMessage(plugin.msg("no-permission")); return true; }
        ShopGui.openMain(plugin, player);
        return true;
    }
}
