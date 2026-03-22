package it.staffplugin.managers;

import it.staffplugin.StaffPlugin;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

public class ShopManager {

    private final StaffPlugin plugin;

    public record ShopItem(Material material, double buyPrice, double sellPrice, String category) {}

    private final Map<Material, ShopItem> items = new HashMap<>();

    public ShopManager(StaffPlugin plugin) {
        this.plugin = plugin;
        load();
    }

    private void load() {
        items.clear();
        ConfigurationSection itemsSection = plugin.getConfig().getConfigurationSection("shop.items");
        if (itemsSection == null) return;

        for (String key : itemsSection.getKeys(false)) {
            try {
                Material mat = Material.valueOf(key.toUpperCase());
                double buy = itemsSection.getDouble(key + ".buy", 0);
                double sell = itemsSection.getDouble(key + ".sell", 0);
                String category = itemsSection.getString(key + ".category", "other");
                items.put(mat, new ShopItem(mat, buy, sell, category));
            } catch (IllegalArgumentException ignored) {
                plugin.getLogger().warning("Material non valido in shop.items: " + key);
            }
        }
    }

    public Map<Material, ShopItem> getItems() { return items; }

    public ShopItem getItem(Material mat) { return items.get(mat); }

    public boolean hasItem(Material mat) { return items.containsKey(mat); }

    public Map<Material, ShopItem> getItemsByCategory(String category) {
        Map<Material, ShopItem> result = new HashMap<>();
        items.forEach((mat, item) -> {
            if (item.category().equalsIgnoreCase(category)) result.put(mat, item);
        });
        return result;
    }

    public void reload() { load(); }
}
