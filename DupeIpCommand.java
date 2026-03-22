package it.staffplugin.managers;

import it.staffplugin.StaffPlugin;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class DataManager {

    private final StaffPlugin plugin;

    private final Map<String, Map<String, String>> banData = new HashMap<>();

    private final Map<String, List<Map<String, String>>> banHistory = new HashMap<>();

    private final Map<String, Map<String, String>> ipBanData = new HashMap<>();

    private final Map<String, String> playerIps = new HashMap<>();

    private File dataFile;
    private YamlConfiguration dataConfig;

    public DataManager(StaffPlugin plugin) {
        this.plugin = plugin;
        load();
    }

    private void load() {
        dataFile = new File(plugin.getDataFolder(), "data.yml");
        if (!dataFile.exists()) {
            try { dataFile.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);

        if (dataConfig.isConfigurationSection("bans")) {
            for (String uuid : dataConfig.getConfigurationSection("bans").getKeys(false)) {
                Map<String, String> entry = new HashMap<>();
                entry.put("reason", dataConfig.getString("bans." + uuid + ".reason", "Nessun motivo"));
                entry.put("by", dataConfig.getString("bans." + uuid + ".by", "Console"));
                entry.put("timestamp", dataConfig.getString("bans." + uuid + ".timestamp", ""));
                banData.put(uuid, entry);
            }
        }

        if (dataConfig.isConfigurationSection("history")) {
            for (String uuid : dataConfig.getConfigurationSection("history").getKeys(false)) {
                List<Map<String, String>> list = new ArrayList<>();
                var section = dataConfig.getConfigurationSection("history." + uuid);
                if (section != null) {
                    for (String idx : section.getKeys(false)) {
                        Map<String, String> entry = new HashMap<>();
                        entry.put("reason", dataConfig.getString("history." + uuid + "." + idx + ".reason", ""));
                        entry.put("by", dataConfig.getString("history." + uuid + "." + idx + ".by", ""));
                        entry.put("timestamp", dataConfig.getString("history." + uuid + "." + idx + ".timestamp", ""));
                        list.add(entry);
                    }
                }
                banHistory.put(uuid, list);
            }
        }

        if (dataConfig.isConfigurationSection("ipbans")) {
            for (String ip : dataConfig.getConfigurationSection("ipbans").getKeys(false)) {
                Map<String, String> entry = new HashMap<>();
                entry.put("reason", dataConfig.getString("ipbans." + ip + ".reason", ""));
                entry.put("by", dataConfig.getString("ipbans." + ip + ".by", ""));
                ipBanData.put(ip, entry);
            }
        }

        if (dataConfig.isConfigurationSection("player_ips")) {
            for (String uuid : dataConfig.getConfigurationSection("player_ips").getKeys(false)) {
                playerIps.put(uuid, dataConfig.getString("player_ips." + uuid, ""));
            }
        }
    }

    public void saveAll() {

        banData.forEach((uuid, map) -> {
            dataConfig.set("bans." + uuid + ".reason", map.get("reason"));
            dataConfig.set("bans." + uuid + ".by", map.get("by"));
            dataConfig.set("bans." + uuid + ".timestamp", map.get("timestamp"));
        });

        banHistory.forEach((uuid, list) -> {
            for (int i = 0; i < list.size(); i++) {
                dataConfig.set("history." + uuid + "." + i + ".reason", list.get(i).get("reason"));
                dataConfig.set("history." + uuid + "." + i + ".by", list.get(i).get("by"));
                dataConfig.set("history." + uuid + "." + i + ".timestamp", list.get(i).get("timestamp"));
            }
        });

        ipBanData.forEach((ip, map) -> {
            dataConfig.set("ipbans." + ip + ".reason", map.get("reason"));
            dataConfig.set("ipbans." + ip + ".by", map.get("by"));
        });

        playerIps.forEach((uuid, ip) -> dataConfig.set("player_ips." + uuid, ip));

        try { dataConfig.save(dataFile); } catch (IOException e) { e.printStackTrace(); }
    }

    public void ban(String uuid, String reason, String by) {
        Map<String, String> entry = new HashMap<>();
        entry.put("reason", reason);
        entry.put("by", by);
        entry.put("timestamp", String.valueOf(System.currentTimeMillis()));
        banData.put(uuid, entry);
        addToHistory(uuid, reason, by);
        saveAll();
    }

    public void unban(String uuid) {
        banData.remove(uuid);
        saveAll();
    }

    public boolean isBanned(String uuid) { return banData.containsKey(uuid); }

    public Map<String, String> getBanInfo(String uuid) { return banData.get(uuid); }

    private void addToHistory(String uuid, String reason, String by) {
        banHistory.computeIfAbsent(uuid, k -> new ArrayList<>())
                .add(Map.of("reason", reason, "by", by, "timestamp", String.valueOf(System.currentTimeMillis())));
    }

    public List<Map<String, String>> getBanHistory(String uuid) {
        return banHistory.getOrDefault(uuid, new ArrayList<>());
    }

    public void ipBan(String ip, String reason, String by) {
        ipBanData.put(ip, Map.of("reason", reason, "by", by));
        saveAll();
    }

    public boolean isIpBanned(String ip) { return ipBanData.containsKey(ip); }

    public Map<String, String> getIpBanInfo(String ip) { return ipBanData.get(ip); }

    public void registerIp(String uuid, String ip) {
        playerIps.put(uuid, ip);
        saveAll();
    }

    public String getPlayerIp(String uuid) { return playerIps.getOrDefault(uuid, null); }

    public Map<String, String> getAllPlayerIps() { return Collections.unmodifiableMap(playerIps); }

    private final Map<String, Double> balances = new HashMap<>();

    public double getBalance(String uuid) {
        if (!balances.containsKey(uuid)) {
            double def = balances.getOrDefault("__default__", 0.0);

            balances.put(uuid, dataConfig.getDouble("balances." + uuid, def));
        }
        return balances.getOrDefault(uuid, 0.0);
    }

    public void setBalance(String uuid, double amount) {
        balances.put(uuid, amount);
        dataConfig.set("balances." + uuid, amount);
        try { dataConfig.save(dataFile); } catch (java.io.IOException e) { e.printStackTrace(); }
    }

    public void addBalance(String uuid, double amount) {
        setBalance(uuid, getBalance(uuid) + amount);
    }

    public void removeBalance(String uuid, double amount) {
        setBalance(uuid, Math.max(0, getBalance(uuid) - amount));
    }
}
