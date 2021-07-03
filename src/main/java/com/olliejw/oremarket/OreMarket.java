package com.olliejw.oremarket;

import com.olliejw.oremarket.Commands.Reload;
import com.olliejw.oremarket.Utils.MarketGUI;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class OreMarket extends JavaPlugin {

    private static OreMarket instance;
    private static final Logger log = Logger.getLogger("Minecraft");
    private static Economy econ = null;

    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        this.getServer().getPluginManager().registerEvents(new MarketGUI(), this);
        this.getCommand("openmarket").setExecutor(new MarketGUI());
        this.getCommand("om-reload").setExecutor(new Reload());

        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
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
        return econ != null;
    }

    public static Economy getEconomy() {
        return econ;
    }
}

