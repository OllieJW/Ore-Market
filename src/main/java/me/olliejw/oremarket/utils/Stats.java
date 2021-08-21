package me.olliejw.oremarket.utils;

import me.olliejw.oremarket.OreMarket;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Objects;

public class Stats {

    public int totalItems () {
        int items = 0;
        for (String key : Objects.requireNonNull(OreMarket.main().getGuiConfig().getConfigurationSection("items")).getKeys(false)) {
            items++;
        }
        return items;
    }

    public int totalValues() {
        int values = 0;
        for (String key : Objects.requireNonNull(OreMarket.main().getGuiConfig().getConfigurationSection("items")).getKeys(false)) {
            ConfigurationSection keySection = Objects.requireNonNull(OreMarket.main().getGuiConfig().getConfigurationSection("items")).getConfigurationSection(key);
            assert keySection != null;
            values = (values + keySection.getInt("value"));
        }
        return values;
    }

    public void reloadStats() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(OreMarket.main(), () -> {
            totalValues();
            totalItems();

        }, 0L, 10*20);
    }

}
