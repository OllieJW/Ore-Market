package com.olliejw.oremarket;
import com.olliejw.oremarket.Chat.ValueUpdates;
import com.olliejw.oremarket.Commands.CrashMarket;
import com.olliejw.oremarket.Commands.OpenMarket;
import com.olliejw.oremarket.Commands.Reload;
import com.olliejw.oremarket.Commands.StatsCommands;
import com.olliejw.oremarket.Events.MarketCrash;
import com.olliejw.oremarket.Listeners.InventoryEvents;
import com.olliejw.oremarket.Utils.Stats;
import com.olliejw.oremarket.Utils.UpdateChecker;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

public final class OreMarket extends JavaPlugin {
    ValueUpdates valueUpdates = new ValueUpdates();
    MarketCrash mkCrash = new MarketCrash();
    Stats stats = new Stats();

    private static OreMarket instance;
    private static final Logger log = Logger.getLogger("Minecraft");
    private static Economy econ = null;

    private File guiFile;
    private FileConfiguration guiConfig;

    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        createGuiConfig();
        Logger logger = this.getLogger();

        // Spigot and bStats
        new UpdateChecker(this, 91015).getVersion(version -> {
            if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
                logger.info("You are up to date!");
            } else {
                logger.info("New updates available!");
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.hasPermission("oremarket.reload")) { player.sendMessage(ChatColor.GREEN + "New OreMarket version available!"); }
                }
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
    }

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

