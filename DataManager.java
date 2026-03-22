package it.staffplugin.listeners;

import it.staffplugin.StaffPlugin;
import it.staffplugin.gui.HomeGui;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class HomeGuiListener implements Listener {

    private final StaffPlugin plugin;

    private final Map<UUID, String> pendingDelete = new HashMap<>();

    public HomeGuiListener(StaffPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        Inventory inv = event.getInventory();
        String title = event.getView().getTitle();

        if (title.startsWith(stripColor(HomeGui.HOME_GUI_TITLE_PREFIX)) ||
                title.equals(stripColor(plugin.getConfig().getString("home.menu-title", HomeGui.HOME_GUI_TITLE_PREFIX)))) {

            event.setCancelled(true);
            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;

            ItemStack clicked = event.getCurrentItem();
            String itemName = getDisplayName(clicked);
            if (itemName == null) return;

            if (clicked.getType() == Material.BARRIER) {
                player.closeInventory();
                return;
            }

            if (clicked.getType() == Material.BED || clicked.getType().name().endsWith("_BED")
                    && clicked.getType() != Material.LIGHT_GRAY_BED) {

                String homeName = net.md_5.bungee.api.ChatColor.stripColor(itemName);

                if (event.isShiftClick()) {

                    pendingDelete.put(player.getUniqueId(), homeName);
                    HomeGui.openConfirmDelete(plugin, player, homeName);
                } else {

                    Location loc = plugin.getHomeManager().getHome(player.getUniqueId(), homeName);
                    if (loc == null) {
                        player.sendMessage(StaffPlugin.colorize("&cHome non trovata!"));
                        player.closeInventory();
                        return;
                    }
                    player.closeInventory();
                    int delay = plugin.getConfig().getInt("home.teleport-delay", 3);
                    String finalHomeName = homeName;
                    if (delay > 0) {
                        player.sendMessage(StaffPlugin.colorize("&aTeleportazione a &e" + homeName + " &atra &e" + delay + " &asecondi..."));
                        Location startLoc = player.getLocation().clone();
                        org.bukkit.Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            if (!player.isOnline()) return;
                            if (player.getLocation().distance(startLoc) > 1) {
                                player.sendMessage(StaffPlugin.colorize("&cTeleportazione annullata!"));
                                return;
                            }
                            player.teleport(loc);
                            player.sendMessage(StaffPlugin.colorize(
                                    plugin.getConfig().getString("home.teleport-message", "&aTeleportato!").replace("{home}", finalHomeName)));
                        }, delay * 20L);
                    } else {
                        player.teleport(loc);
                        player.sendMessage(StaffPlugin.colorize(
                                plugin.getConfig().getString("home.teleport-message", "&aTeleportato!").replace("{home}", finalHomeName)));
                    }
                }
                return;
            }

            if (clicked.getType() == Material.LIGHT_GRAY_BED) {
                player.closeInventory();
                player.sendMessage(StaffPlugin.colorize("&7Usa &e/sethome <nome> &7per impostare una nuova home!"));
            }
        }

        else if (title.startsWith(stripColor(HomeGui.CONFIRM_DELETE_PREFIX))) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;

            ItemStack clicked = event.getCurrentItem();

            if (clicked.getType() == Material.LIME_CONCRETE) {

                String homeName = pendingDelete.remove(player.getUniqueId());
                if (homeName != null) {
                    plugin.getHomeManager().deleteHome(player.getUniqueId(), homeName);
                    player.sendMessage(StaffPlugin.colorize(
                            plugin.getConfig().getString("messages.home-deleted", "&aHome {home} eliminata!")
                                    .replace("{home}", homeName)));
                }
                HomeGui.open(plugin, player);
            } else if (clicked.getType() == Material.RED_CONCRETE || clicked.getType() == Material.BARRIER) {
                pendingDelete.remove(player.getUniqueId());
                HomeGui.open(plugin, player);
            } else if (clicked.getType() == Material.BED) {

                pendingDelete.remove(player.getUniqueId());
                HomeGui.open(plugin, player);
            }
        }
    }

    private String getDisplayName(ItemStack item) {
        if (item == null) return null;
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return null;
        return meta.getDisplayName();
    }

    private String stripColor(String s) {
        return net.md_5.bungee.api.ChatColor.stripColor(StaffPlugin.colorize(s));
    }
}
