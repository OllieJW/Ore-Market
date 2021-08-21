package me.olliejw.oremarket;

import me.olliejw.oremarket.chat.ValueUpdates;
import me.olliejw.oremarket.commands.CrashMarket;
import me.olliejw.oremarket.commands.OpenMarket;
import me.olliejw.oremarket.commands.Reload;
import me.olliejw.oremarket.commands.StatsCommands;
import me.olliejw.oremarket.events.MarketCrash;
import me.olliejw.oremarket.listeners.InventoryEvents;
import me.olliejw.oremarket.utils.Placeholders;
import me.olliejw.oremarket.utils.Stats;
import me.olliejw.oremarket.utils.Updates;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

public final class OreMarket extends JavaPlugin implements Listener {
    ValueUpdates valueUpdates = new ValueUpdates();
    MarketCrash mkCrash = new MarketCrash();
    Stats stats = new Stats();

    private static OreMarket instance;
    private static final Logger log = Logger.getLogger("Minecraft");
    private static Economy econ = null;

    public void onEnable() {
        saveDefaultConfig();
        createGuiConfig();

        Logger logger = this.getLogger();
        instance = this;

        // Spigot and bStats
        new Updates(this, 91015).getVersion(version -> {
            if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
                logger.info("OreMarket v" + version + " is up-to-date!");
            }
            else {
                logger.info("============================");
                logger.info("OreMarket v" + version + " is now available!");
                logger.info("Your on still on v" + this.getDescription().getVersion());
                logger.info("spigotmc.org/resources/91015");
                logger.info("============================");
            }
        });
        final Metrics metrics = new Metrics(this, 10961);
        metrics.addCustomChart(new Metrics.SingleLineChart("total_values", new Callable<Integer>() {
            @Override
            public Integer call() {
                return stats.totalValues();
            }
        }));
        metrics.addCustomChart(new Metrics.SingleLineChart("total_items", new Callable<Integer>() {
            @Override
            public Integer call() {
                return stats.totalItems();
            }
        }));
        metrics.addCustomChart(new Metrics.SimplePie("market_crash_enabled", () -> {
            return String.valueOf(this.getConfig().getBoolean("marketcrash.enabled"));
        }));
        // Commands and Events
        this.getServer().getPluginManager().registerEvents(new InventoryEvents(), this);
        Objects.requireNonNull(this.getCommand("openmarket")).setExecutor(new OpenMarket());
        Objects.requireNonNull(this.getCommand("om-reload")).setExecutor(new Reload());
        Objects.requireNonNull(this.getCommand("om-stats")).setExecutor(new StatsCommands());
        Objects.requireNonNull(this.getCommand("om-crash")).setExecutor(new CrashMarket());

        valueUpdates.announceValue();
        mkCrash.startCrash();
        stats.reloadStats();

        // Economy
        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
        }

        // Placeholder API
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
            new Placeholders().register();
        } else {
            log.severe("OreMarket failed to load! Missing PlaceholderAPI dependency.");
        }
    }


    private File guiFile;
    private FileConfiguration guiConfig;
    public FileConfiguration getGuiConfig() {
        return this.guiConfig;
    }
    private void createGuiConfig() {
        guiFile = new File(getDataFolder(), "gui.yml");
        if (!guiFile.exists()) {
            guiFile.getParentFile().mkdirs();
            saveResource("gui.yml", false);
        }

        guiConfig = new YamlConfiguration();
        try {
            guiConfig.load(guiFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
    public void saveGuiConfig() {
        try {
            guiConfig.save(guiFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void reloadGuiConfig() {
        try {
            guiConfig.load(guiFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static OreMarket main(){
        return instance;
    }
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            log.severe("OreMarket failed to load! Missing Vault dependency.");
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return true;
    }
    public static Economy getEconomy() {
        return econ;
    }
}

