package com.olliejw.oremarket;

import com.olliejw.oremarket.Chat.ValueUpdates;
import com.olliejw.oremarket.Commands.Reload;
import com.olliejw.oremarket.Listeners.InventoryEvents;
import com.olliejw.oremarket.Utils.AddItem;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Logger;

public final class OreMarket extends JavaPlugin {
    ValueUpdates valueUpdates = new ValueUpdates();
    private static OreMarket instance;
    private static final Logger log = Logger.getLogger("Minecraft");
    private static Economy econ = null;

    private File guiFile;
    private FileConfiguration guiConfig;

    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        createGuiConfig();

        this.getServer().getPluginManager().registerEvents(new InventoryEvents(), this);
        Objects.requireNonNull(this.getCommand("openmarket")).setExecutor(new AddItem());
        Objects.requireNonNull(this.getCommand("om-reload")).setExecutor(new Reload());

        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
        }

        valueUpdates.announceValue();
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

