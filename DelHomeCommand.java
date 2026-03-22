package it.staffplugin.managers;

import it.staffplugin.StaffPlugin;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MuteManager {

    private final StaffPlugin plugin;
    private final Map<UUID, String> mutedPlayers = new HashMap<>(); 

    private File muteFile;
    private YamlConfiguration muteConfig;

    public MuteManager(StaffPlugin plugin) {
        this.plugin = plugin;
        load();
    }

    private void load() {
        muteFile = new File(plugin.getDataFolder(), "mutes.yml");
        if (!muteFile.exists()) {
            try { muteFile.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
        muteConfig = YamlConfiguration.loadConfiguration(muteFile);
        if (muteConfig.isConfigurationSection("mutes")) {
            for (String uuid : muteConfig.getConfigurationSection("mutes").getKeys(false)) {
                mutedPlayers.put(UUID.fromString(uuid), muteConfig.getString("mutes." + uuid, "Nessun motivo"));
            }
        }
    }

    public void save() {
        mutedPlayers.forEach((uuid, reason) -> muteConfig.set("mutes." + uuid.toString(), reason));

        if (muteConfig.isConfigurationSection("mutes")) {
            for (String uuid : muteConfig.getConfigurationSection("mutes").getKeys(false)) {
                if (!mutedPlayers.containsKey(UUID.fromString(uuid))) {
                    muteConfig.set("mutes." + uuid, null);
                }
            }
        }
        try { muteConfig.save(muteFile); } catch (IOException e) { e.printStackTrace(); }
    }

    public void mute(UUID uuid, String reason) {
        mutedPlayers.put(uuid, reason);
        save();
    }

    public void unmute(UUID uuid) {
        mutedPlayers.remove(uuid);
        save();
    }

    public boolean isMuted(UUID uuid) {
        return mutedPlayers.containsKey(uuid);
    }

    public String getMuteReason(UUID uuid) {
        return mutedPlayers.getOrDefault(uuid, "Nessun motivo");
    }
}
