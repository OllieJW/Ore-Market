package com.olliejw.oremarket.Events;

import com.olliejw.oremarket.OreMarket;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.Objects;

public class MarketCrash implements Listener {
    public void forceCrash() {
        String notification = OreMarket.main().getConfig().getString("marketcrash.message");
        for (Player player: Bukkit.getOnlinePlayers()) {
            assert notification != null;
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', notification).replace("[amount]", Objects.requireNonNull(OreMarket.main().getConfig().getString("marketcrash.amount"))));
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 10.0F, 1);
        }
        for (String key : Objects.requireNonNull(OreMarket.main().getGuiConfig().getConfigurationSection("items")).getKeys(false)) {
            ConfigurationSection keySection = Objects.requireNonNull(OreMarket.main().getGuiConfig().getConfigurationSection("items")).getConfigurationSection(key);
            assert keySection != null;
            double value = keySection.getDouble("value");
            double amount = OreMarket.main().getConfig().getDouble("marketcrash.amount");
            keySection.set("value", (value*(1-(amount/100))));
            OreMarket.main().saveGuiConfig();
        }
    }

    public void startCrash() {
        if (OreMarket.main().getConfig().getBoolean("marketcrash.enabled")) {
            Bukkit.getScheduler().scheduleSyncRepeatingTask(OreMarket.main(), this::forceCrash, (OreMarket.main().getConfig().getInt("marketcrash.time") * 20L * 60 * 60), (OreMarket.main().getConfig().getInt("marketcrash.time") * 20L * 60 * 60));
        }
    }
}
