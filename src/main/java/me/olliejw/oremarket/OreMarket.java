package me.olliejw.oremarket;

import me.olliejw.oremarket.chat.ValueUpdates;
import me.olliejw.oremarket.commands.CrashMarket;
import me.olliejw.oremarket.commands.OpenMarket;
import me.olliejw.oremarket.commands.Reload;
import me.olliejw.oremarket.commands.StatsCommands;
import me.olliejw.oremarket.events.MarketCrash;
import me.olliejw.oremarket.listeners.InventoryEvents;
import me.olliejw.oremarket.listeners.PlayerJoin;
import me.olliejw.oremarket.utils.Placeholders;
import me.olliejw.oremarket.utils.Stats;
import me.olliejw.oremarket.utils.Updates;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
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
        createMsgConfig();
        instance = this;

        logToFile("Started OreMarket");

        // Spigot and bStats
        new Updates(this, 91015).getVersion(version -> {
            if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
                this.getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "   ___   __  __ ");
                this.getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "  / _ \\ |  \\/  |     " + ChatColor.GREEN +"OreMarket v"+this.getDescription().getVersion());
                this.getServer().getConsoleSender().sendMessage(ChatColor.AQUA + " | (_) || |\\/| |     " + ChatColor.GREEN +"Up to date!");
                this.getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "  \\___/ |_|  |_|");
                this.getServer().getConsoleSender().sendMessage("");
            }
            else {
                this.getServer().getConsoleSender().sendMessage(ChatColor.RED + "   ___   __  __ ");
                this.getServer().getConsoleSender().sendMessage(ChatColor.RED + "  / _ \\ |  \\/  |     " + ChatColor.RED +"OreMarket v"+this.getDescription().getVersion());
                this.getServer().getConsoleSender().sendMessage(ChatColor.RED + " | (_) || |\\/| |     " + ChatColor.GREEN + "v" + version + " now available!");
                this.getServer().getConsoleSender().sendMessage(ChatColor.RED + "  \\___/ |_|  |_|     " + ChatColor.GREEN + "Download at spigotmc.org");
                this.getServer().getConsoleSender().sendMessage("");
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
        this.getServer().getPluginManager().registerEvents(new PlayerJoin(), this);
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

    private File msgFile;
    private FileConfiguration msgConfig;
    public FileConfiguration getMsgConfig() {
        return this.msgConfig;
    }
    private void createMsgConfig() {
        msgFile = new File(getDataFolder(), "messages.yml");
        if (!msgFile.exists()) {
            msgFile.getParentFile().mkdirs();
            saveResource("messages.yml", false);
        }

        msgConfig = new YamlConfiguration();
        try {
            msgConfig.load(msgFile);
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

    public void logToFile(String string) {
        try {
            File dataFolder = getDataFolder();
            if(!dataFolder.exists()) {
                dataFolder.mkdir();
            }
            File logsFile = new File(getDataFolder(), "logs.txt");
            if (!logsFile.exists()) {
                logsFile.createNewFile();
            }

            SimpleDateFormat formatter = new SimpleDateFormat("[dd/MM/yyyy HH:mm:ss]: ");
            Date date = new Date(System.currentTimeMillis());

            FileWriter fw = new FileWriter(logsFile, true);
            PrintWriter pw = new PrintWriter(fw);
            pw.println(formatter.format(date) + ChatColor.stripColor(string.replaceAll("&", "§")));
            pw.flush();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

