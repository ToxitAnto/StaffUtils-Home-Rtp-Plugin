package it.staffplugin.listeners;

import it.staffplugin.StaffPlugin;
import it.staffplugin.gui.ShopGui;
import it.staffplugin.managers.ShopManager;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ShopGuiListener implements Listener {

    private final StaffPlugin plugin;

    private final Map<UUID, PendingTransaction> pending = new HashMap<>();

    record PendingTransaction(Material material, boolean buying, int amount, String category) {}

    public ShopGuiListener(StaffPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        String title = event.getView().getTitle();
        String strippedTitle = stripColor(title);

        String mainTitle = stripColor(plugin.getConfig().getString("shop.main-title", ShopGui.SHOP_MAIN_PREFIX));
        if (strippedTitle.equals(mainTitle)) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;

            ItemStack clicked = event.getCurrentItem();
            if (clicked.getType() == Material.BARRIER) { player.closeInventory(); return; }
            if (clicked.getType() == Material.GOLD_INGOT) return; 

            ConfigurationSection cats = plugin.getConfig().getConfigurationSection("shop.categories");
            if (cats != null) {
                for (String catKey : cats.getKeys(false)) {
                    int slot = plugin.getConfig().getInt("shop.categories." + catKey + ".slot", -1);
                    if (slot == event.getRawSlot()) {
                        ShopGui.openCategory(plugin, player, catKey);
                        return;
                    }
                }
            }
        }

        else if (strippedTitle.startsWith(stripColor(ShopGui.SHOP_CAT_PREFIX))) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;

            ItemStack clicked = event.getCurrentItem();

            if (clicked.getType() == Material.BARRIER) { player.closeInventory(); return; }
            if (clicked.getType() == Material.ARROW) { ShopGui.openMain(plugin, player); return; }
            if (clicked.getType() == Material.GRAY_STAINED_GLASS_PANE) return;

            String catKey = findCategoryByTitle(strippedTitle);

            ShopManager.ShopItem shopItem = plugin.getShopManager().getItem(clicked.getType());
            if (shopItem == null) return;

            ClickType clickType = event.getClick();
            boolean buying = !clickType.isRightClick();
            int amount = clickType.isShiftClick() ? 64 : 1;

            if (buying && shopItem.buyPrice() <= 0) {
                player.sendMessage(StaffPlugin.colorize("&cQuesto oggetto non può essere acquistato!"));
                return;
            }
            if (!buying && shopItem.sellPrice() <= 0) {
                player.sendMessage(StaffPlugin.colorize("&cQuesto oggetto non può essere venduto!"));
                return;
            }

            if (!buying && clickType.isShiftClick()) {
                int playerAmount = countPlayerItems(player, shopItem.material());
                if (playerAmount == 0) {
                    player.sendMessage(StaffPlugin.colorize("&cNon hai &e" + formatName(shopItem.material().name()) + " &cnel tuo inventario!"));
                    return;
                }
                amount = playerAmount;
            }

            pending.put(player.getUniqueId(), new PendingTransaction(shopItem.material(), buying, amount, catKey));
            ShopGui.openItemConfirm(plugin, player, shopItem.material(), buying, amount);
        }

        else if (strippedTitle.startsWith(stripColor(ShopGui.SHOP_ITEM_PREFIX))) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;

            ItemStack clicked = event.getCurrentItem();
            PendingTransaction tx = pending.get(player.getUniqueId());

            if (clicked.getType() == Material.LIME_CONCRETE && tx != null) {

                if (tx.buying()) {
                    processBuy(player, tx);
                } else {
                    processSell(player, tx);
                }
                pending.remove(player.getUniqueId());
                if (tx.category() != null) ShopGui.openCategory(plugin, player, tx.category());
                else ShopGui.openMain(plugin, player);
            } else if (clicked.getType() == Material.RED_CONCRETE) {
                pending.remove(player.getUniqueId());
                if (tx != null && tx.category() != null) ShopGui.openCategory(plugin, player, tx.category());
                else ShopGui.openMain(plugin, player);
            }
        }
    }

    private void processBuy(Player player, PendingTransaction tx) {
        ShopManager.ShopItem item = plugin.getShopManager().getItem(tx.material());
        if (item == null) return;

        double totalCost = item.buyPrice() * tx.amount();
        double balance = plugin.getDataManager().getBalance(player.getUniqueId().toString());

        if (balance < totalCost) {
            player.sendMessage(StaffPlugin.colorize(
                    "&cNon hai abbastanza soldi! Ti servono &e$" + String.format("%.2f", totalCost) +
                    " &cma hai solo &e$" + String.format("%.2f", balance)));
            return;
        }

        plugin.getDataManager().setBalance(player.getUniqueId().toString(), balance - totalCost);
        player.getInventory().addItem(new ItemStack(tx.material(), tx.amount()));
        player.sendMessage(StaffPlugin.colorize(
                "&aHai acquistato &e" + tx.amount() + "x " + formatName(tx.material().name()) +
                " &aper &e$" + String.format("%.2f", totalCost) +
                " &a| Saldo: &e$" + String.format("%.2f", balance - totalCost)));
    }

    private void processSell(Player player, PendingTransaction tx) {
        ShopManager.ShopItem item = plugin.getShopManager().getItem(tx.material());
        if (item == null) return;

        int inInventory = countPlayerItems(player, tx.material());
        int toSell = Math.min(tx.amount(), inInventory);

        if (toSell == 0) {
            player.sendMessage(StaffPlugin.colorize("&cNon hai &e" + formatName(tx.material().name()) + " &cda vendere!"));
            return;
        }

        double totalEarned = item.sellPrice() * toSell;
        double balance = plugin.getDataManager().getBalance(player.getUniqueId().toString());

        removeItems(player, tx.material(), toSell);

        plugin.getDataManager().setBalance(player.getUniqueId().toString(), balance + totalEarned);
        player.sendMessage(StaffPlugin.colorize(
                "&aHai venduto &e" + toSell + "x " + formatName(tx.material().name()) +
                " &aper &e$" + String.format("%.2f", totalEarned) +
                " &a| Saldo: &e$" + String.format("%.2f", balance + totalEarned)));
    }

    private int countPlayerItems(Player player, Material mat) {
        int count = 0;
        for (ItemStack stack : player.getInventory().getContents()) {
            if (stack != null && stack.getType() == mat) count += stack.getAmount();
        }
        return count;
    }

    private void removeItems(Player player, Material mat, int amount) {
        int remaining = amount;
        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length && remaining > 0; i++) {
            ItemStack stack = contents[i];
            if (stack != null && stack.getType() == mat) {
                if (stack.getAmount() <= remaining) {
                    remaining -= stack.getAmount();
                    player.getInventory().setItem(i, null);
                } else {
                    stack.setAmount(stack.getAmount() - remaining);
                    remaining = 0;
                }
            }
        }
    }

    private String findCategoryByTitle(String strippedTitle) {
        ConfigurationSection cats = plugin.getConfig().getConfigurationSection("shop.categories");
        if (cats == null) return null;
        for (String catKey : cats.getKeys(false)) {
            String catTitle = stripColor(plugin.getConfig().getString("shop.categories." + catKey + ".title", ""));
            if (catTitle.equals(strippedTitle)) return catKey;
        }
        return null;
    }

    private String stripColor(String s) {
        return net.md_5.bungee.api.ChatColor.stripColor(StaffPlugin.colorize(s));
    }

    private String formatName(String name) {
        String[] parts = name.split("_");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (!sb.isEmpty()) sb.append(" ");
            sb.append(Character.toUpperCase(part.charAt(0)));
            if (part.length() > 1) sb.append(part.substring(1).toLowerCase());
        }
        return sb.toString();
    }
}
