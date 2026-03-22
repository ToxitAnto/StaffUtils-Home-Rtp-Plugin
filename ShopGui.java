package it.staffplugin.managers;

import it.staffplugin.StaffPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class SpawnManager {

    private final StaffPlugin plugin;
    private Location spawnLocation;
    private File spawnFile;
    private YamlConfiguration spawnConfig;

    public SpawnManager(StaffPlugin plugin) {
        this.plugin = plugin;
        load();
    }

    private void load() {
        spawnFile = new File(plugin.getDataFolder(), "spawn.yml");
        if (!spawnFile.exists()) {
            try { spawnFile.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
        spawnConfig = YamlConfiguration.loadConfiguration(spawnFile);

        if (spawnConfig.contains("spawn.world")) {
            String worldName = spawnConfig.getString("spawn.world");
            var world = Bukkit.getWorld(worldName != null ? worldName : "world");
            if (world != null) {
                spawnLocation = new Location(
                        world,
                        spawnConfig.getDouble("spawn.x"),
                        spawnConfig.getDouble("spawn.y"),
                        spawnConfig.getDouble("spawn.z"),
                        (float) spawnConfig.getDouble("spawn.yaw"),
                        (float) spawnConfig.getDouble("spawn.pitch")
                );
            }
        }
    }

    public void save() {
        if (spawnLocation != null) {
            spawnConfig.set("spawn.world", spawnLocation.getWorld().getName());
            spawnConfig.set("spawn.x", spawnLocation.getX());
            spawnConfig.set("spawn.y", spawnLocation.getY());
            spawnConfig.set("spawn.z", spawnLocation.getZ());
            spawnConfig.set("spawn.yaw", spawnLocation.getYaw());
            spawnConfig.set("spawn.pitch", spawnLocation.getPitch());
            try { spawnConfig.save(spawnFile); } catch (IOException e) { e.printStackTrace(); }
        }
    }

    public Location getSpawn() { return spawnLocation; }

    public void setSpawn(Location loc) {
        this.spawnLocation = loc;
        save();
    }

    public boolean hasSpawn() { return spawnLocation != null; }
}
