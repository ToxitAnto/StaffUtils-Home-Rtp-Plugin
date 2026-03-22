package it.staffplugin.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import net.md_5.bungee.api.ChatColor;

public class GuiUtils {

    public static Inventory createGui(String title, int rows) {
        return Bukkit.createInventory(null, rows * 9, colorize(title));
    }

    public static ItemStack createItem(Material mat, String name, String... lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(colorize(name));
            if (lore.length > 0) {
                meta.setLore(Arrays.stream(lore).map(GuiUtils::colorize).toList());
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    public static ItemStack createItem(Material mat, String name, List<String> lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(colorize(name));
            meta.setLore(lore.stream().map(GuiUtils::colorize).toList());
            item.setItemMeta(meta);
        }
        return item;
    }

    public static ItemStack fillerItem() {
        return createItem(Material.GRAY_STAINED_GLASS_PANE, " ");
    }

    public static void fillBorder(Inventory inv) {
        int size = inv.getSize();
        int rows = size / 9;
        for (int i = 0; i < size; i++) {
            int row = i / 9;
            int col = i % 9;
            if (row == 0 || row == rows - 1 || col == 0 || col == 8) {
                inv.setItem(i, fillerItem());
            }
        }
    }

    public static String colorize(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}
