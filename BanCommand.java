package it.staffplugin.gui;

import it.staffplugin.StaffPlugin;
import it.staffplugin.managers.ShopManager;
import it.staffplugin.utils.GuiUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShopGui {

    public static final String SHOP_MAIN_PREFIX = "§8» §6Shop §8«";
    public static final String SHOP_CAT_PREFIX = "§8» §6";
    public static final String SHOP_ITEM_PREFIX = "§8» §6Item: ";

    public static void openMain(StaffPlugin plugin, Player player) {
        String title = plugin.getConfig().getString("shop.main-title", SHOP_MAIN_PREFIX);
        Inventory inv = GuiUtils.createGui(title, 3);
        GuiUtils.fillBorder(inv);

        ConfigurationSection cats = plugin.getConfig().getConfigurationSection("shop.categories");
        if (cats != null) {
            for (String catKey : cats.getKeys(false)) {
                String displayName = plugin.getConfig().getString("shop.categories." + catKey + ".display-name", catKey);
                String iconStr = plugin.getConfig().getString("shop.categories." + catKey + ".icon", "CHEST");
                int slot = plugin.getConfig().getInt("shop.categories." + catKey + ".slot", 13);
                List<String> lore = plugin.getConfig().getStringList("shop.categories." + catKey + ".lore");
                lore.add("");
                lore.add("§eClicca §7per aprire");

                Material icon;
                try { icon = Material.valueOf(iconStr); }
                catch (IllegalArgumentException e) { icon = Material.CHEST; }

                inv.setItem(slot, GuiUtils.createItem(icon, displayName, lore));
            }
        }

        inv.setItem(4, GuiUtils.createItem(Material.GOLD_INGOT, "§6§lShop",
                "§7Compra e vendi oggetti",
                "§7Scegli una categoria!"));

        inv.setItem(26, GuiUtils.createItem(Material.BARRIER, "§cChiudi"));

        player.openInventory(inv);
    }

    public static void openCategory(StaffPlugin plugin, Player player, String category) {
        String catTitle = plugin.getConfig().getString("shop.categories." + category + ".title",
                SHOP_CAT_PREFIX + category + " §8«");
        Inventory inv = GuiUtils.createGui(catTitle, 6);
        GuiUtils.fillBorder(inv);

        Map<Material, ShopManager.ShopItem> items = plugin.getShopManager().getItemsByCategory(category);

        int slot = 10;
        for (ShopManager.ShopItem item : items.values()) {
            if (slot >= inv.getSize() - 9) break;
            if (slot % 9 == 8) { slot += 2; }

            List<String> lore = new ArrayList<>();
            lore.add("§8§m-------------------");
            if (item.buyPrice() > 0) lore.add("§7Acquisto: §a$" + String.format("%.2f", item.buyPrice()));
            if (item.sellPrice() > 0) lore.add("§7Vendita:  §e$" + String.format("%.2f", item.sellPrice()));
            lore.add("§8§m-------------------");
            lore.add("§aClick Sx §7per comprare x1");
            lore.add("§aShift+Click Sx §7per comprare x64");
            lore.add("§cClick Dx §7per vendere x1");
            lore.add("§cShift+Click Dx §7per vendere tutto");

            inv.setItem(slot, GuiUtils.createItem(item.material(),
                    "§6" + formatMaterialName(item.material().name()), lore));
            slot++;
        }

        inv.setItem(inv.getSize() - 9, GuiUtils.createItem(Material.ARROW, "§7« Indietro"));

        inv.setItem(inv.getSize() - 1, GuiUtils.createItem(Material.BARRIER, "§cChiudi"));

        player.openInventory(inv);
    }

    public static void openItemConfirm(StaffPlugin plugin, Player player, Material material, boolean buying, int amount) {
        ShopManager.ShopItem shopItem = plugin.getShopManager().getItem(material);
        if (shopItem == null) return;

        double price = buying ? shopItem.buyPrice() * amount : shopItem.sellPrice() * amount;
        String action = buying ? "§aACQUISTARE" : "§cVENDERE";
        String title = SHOP_ITEM_PREFIX + formatMaterialName(material.name());
        Inventory inv = GuiUtils.createGui(title, 3);
        GuiUtils.fillBorder(inv);

        List<String> itemLore = new ArrayList<>();
        itemLore.add("§7Quantità: §e" + amount);
        itemLore.add("§7Prezzo totale: §a$" + String.format("%.2f", price));
        inv.setItem(13, GuiUtils.createItem(material, "§6" + formatMaterialName(material.name()), itemLore));

        List<String> confirmLore = new ArrayList<>();
        confirmLore.add("§7Clicca per " + action);
        confirmLore.add("§7" + amount + "x " + formatMaterialName(material.name()));
        confirmLore.add("§7per §a$" + String.format("%.2f", price));
        inv.setItem(11, GuiUtils.createItem(Material.LIME_CONCRETE, "§a✔ Conferma", confirmLore));

        inv.setItem(15, GuiUtils.createItem(Material.RED_CONCRETE, "§c✘ Annulla",
                "§7Torna alla categoria"));

        player.openInventory(inv);
    }

    private static String formatMaterialName(String name) {
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
