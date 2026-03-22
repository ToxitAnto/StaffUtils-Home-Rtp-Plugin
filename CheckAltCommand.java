package it.staffplugin.managers;

import it.staffplugin.StaffPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HomeManager {

    private final StaffPlugin plugin;

    private final Map<UUID, Map<String, Location>> homes = new HashMap<>();

    private File homeFile;
    private YamlConfiguration homeConfig;

    public HomeManager(StaffPlugin plugin) {
        this.plugin = plugin;
        load();
    }

    private void load() {
        homeFile = new File(plugin.getDataFolder(), "homes.yml");
        if (!homeFile.exists()) {
            try { homeFile.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
        homeConfig = YamlConfiguration.loadConfiguration(homeFile);

        if (homeConfig.isConfigurationSection("homes")) {
            for (String uuid : homeConfig.getConfigurationSection("homes").getKeys(false)) {
                Map<String, Location> playerHomes = new HashMap<>();
                var section = homeConfig.getConfigurationSection("homes." + uuid);
                if (section != null) {
                    for (String homeName : section.getKeys(false)) {
                        String worldName = homeConfig.getString("homes." + uuid + "." + homeName + ".world");
                        double x = homeConfig.getDouble("homes." + uuid + "." + homeName + ".x");
                        double y = homeConfig.getDouble("homes." + uuid + "." + homeName + ".y");
                        double z = homeConfig.getDouble("homes." + uuid + "." + homeName + ".z");
                        float yaw = (float) homeConfig.getDouble("homes." + uuid + "." + homeName + ".yaw");
                        float pitch = (float) homeConfig.getDouble("homes." + uuid + "." + homeName + ".pitch");
                        var world = Bukkit.getWorld(worldName != null ? worldName : "world");
                        if (world != null) {
                            playerHomes.put(homeName, new Location(world, x, y, z, yaw, pitch));
                        }
                    }
                }
                homes.put(UUID.fromString(uuid), playerHomes);
            }
        }
    }

    public void saveAll() {
        homes.forEach((uuid, map) -> map.forEach((name, loc) -> {
            String path = "homes." + uuid + "." + name;
            homeConfig.set(path + ".world", loc.getWorld().getName());
            homeConfig.set(path + ".x", loc.getX());
            homeConfig.set(path + ".y", loc.getY());
            homeConfig.set(path + ".z", loc.getZ());
            homeConfig.set(path + ".yaw", loc.getYaw());
            homeConfig.set(path + ".pitch", loc.getPitch());
        }));
        try { homeConfig.save(homeFile); } catch (IOException e) { e.printStackTrace(); }
    }

    public int getMaxHomes(Player player) {
        if (player.hasPermission("staffplugin.staff.*")) return plugin.getConfig().getInt("home.max-homes-staff", 10);
        if (player.hasPermission("staffplugin.vip")) return plugin.getConfig().getInt("home.max-homes-vip", 5);
        return plugin.getConfig().getInt("home.max-homes", 3);
    }

    public Map<String, Location> getHomes(UUID uuid) {
        return homes.computeIfAbsent(uuid, k -> new HashMap<>());
    }

    public Location getHome(UUID uuid, String name) {
        return getHomes(uuid).get(name);
    }

    public boolean setHome(UUID uuid, String name, Location loc) {
        var playerHomes = getHomes(uuid);
        playerHomes.put(name, loc);
        saveAll();
        return true;
    }

    public boolean deleteHome(UUID uuid, String name) {
        var playerHomes = getHomes(uuid);
        if (!playerHomes.containsKey(name)) return false;
        playerHomes.remove(name);
        homeConfig.set("homes." + uuid + "." + name, null);
        saveAll();
        return true;
    }

    public boolean hasHome(UUID uuid, String name) {
        return getHomes(uuid).containsKey(name);
    }
}
