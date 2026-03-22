package it.staffplugin;

import it.staffplugin.commands.*;
import it.staffplugin.listeners.*;
import it.staffplugin.managers.*;
import org.bukkit.plugin.java.JavaPlugin;

public class StaffPlugin extends JavaPlugin {

    private static StaffPlugin instance;
    private DataManager dataManager;
    private VanishManager vanishManager;
    private MuteManager muteManager;
    private HomeManager homeManager;
    private SpawnManager spawnManager;
    private RTPManager rtpManager;
    private ShopManager shopManager;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        dataManager = new DataManager(this);
        vanishManager = new VanishManager(this);
        muteManager = new MuteManager(this);
        homeManager = new HomeManager(this);
        spawnManager = new SpawnManager(this);
        rtpManager = new RTPManager(this);
        shopManager = new ShopManager(this);

        registerCommand("gmc", new GmcCommand(this));
        registerCommand("gms", new GmsCommand(this));
        registerCommand("gmsp", new GmspCommand(this));
        registerCommand("vanish", new VanishCommand(this));
        registerCommand("tpto", new TptoCommand(this));
        registerCommand("flyspeed", new FlySpeedCommand(this));
        registerCommand("ban", new BanCommand(this));
        registerCommand("unban", new UnbanCommand(this));
        registerCommand("mute", new MuteCommand(this));
        registerCommand("unmute", new UnmuteCommand(this));
        registerCommand("ipban", new IpBanCommand(this));
        registerCommand("invsee", new InvseeCommand(this));
        registerCommand("banhistory", new BanHistoryCommand(this));
        registerCommand("checkalt", new CheckAltCommand(this));
        registerCommand("dupeip", new DupeIpCommand(this));

        registerCommand("home", new HomeCommand(this));
        registerCommand("sethome", new SetHomeCommand(this));
        registerCommand("delhome", new DelHomeCommand(this));

        registerCommand("setspawn", new SetSpawnCommand(this));
        registerCommand("spawn", new SpawnCommand(this));

        registerCommand("shop", new ShopCommand(this));

        registerCommand("rtp", new RtpCommand(this));

        registerCommand("balance", new BalanceCommand(this));
        registerCommand("eco", new EcoCommand(this));

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getPluginManager().registerEvents(new InvseeListener(this), this);
        getServer().getPluginManager().registerEvents(new HomeGuiListener(this), this);
        getServer().getPluginManager().registerEvents(new ShopGuiListener(this), this);

        getLogger().info("StaffPlugin abilitato correttamente!");
    }

    @Override
    public void onDisable() {
        if (dataManager != null) dataManager.saveAll();
        if (homeManager != null) homeManager.saveAll();
        if (spawnManager != null) spawnManager.save();
        getLogger().info("StaffPlugin disabilitato.");
    }

    private void registerCommand(String name, org.bukkit.command.CommandExecutor executor) {
        var cmd = getCommand(name);
        if (cmd != null) cmd.setExecutor(executor);
    }

    public static StaffPlugin getInstance() { return instance; }
    public DataManager getDataManager() { return dataManager; }
    public VanishManager getVanishManager() { return vanishManager; }
    public MuteManager getMuteManager() { return muteManager; }
    public HomeManager getHomeManager() { return homeManager; }
    public SpawnManager getSpawnManager() { return spawnManager; }
    public RTPManager getRTPManager() { return rtpManager; }
    public ShopManager getShopManager() { return shopManager; }

    public String prefix() {
        return colorize(getConfig().getString("prefix", "&8[&bStaff&8] &r"));
    }

    public String msg(String key) {
        return colorize(prefix() + getConfig().getString("messages." + key, "&cMessaggio non trovato: " + key));
    }

    public String msgRaw(String key) {
        return colorize(getConfig().getString("messages." + key, "&cMessaggio non trovato: " + key));
    }

    public static String colorize(String s) {
        return net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', s);
    }
}
