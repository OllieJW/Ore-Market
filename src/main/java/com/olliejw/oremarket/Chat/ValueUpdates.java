package com.olliejw.oremarket.Chat;

import com.olliejw.oremarket.OreMarket;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.text.DecimalFormat;
import java.text.Format;
import java.util.Objects;

public class ValueUpdates {
    public void announceValue() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(OreMarket.main(), new Runnable() {
            @Override
            public void run() {
                for (String oreItem : Objects.requireNonNull(OreMarket.main().getGuiConfig().getConfigurationSection("items")).getKeys(false)) {
                    ConfigurationSection keySection = Objects.requireNonNull(OreMarket.main().getGuiConfig().getConfigurationSection("items")).getConfigurationSection(oreItem);

                    assert keySection != null;
                    String name = Objects.requireNonNull(keySection.getString("name"));
                    int stock = keySection.getInt("stock");
                    double value = keySection.getDouble("value");
                    double cost = keySection.getDouble("cost");
                    double difference = (value-cost);
                    double change = (value/cost);
                    double percent = (change*100);

                    final Format DECIMAL_FORMAT = new DecimalFormat("#0.0#");
                    String format = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(OreMarket.main().getConfig().getString("valueupdates.format"))
                        .replace("[name]", name)
                        .replace("[stock]", String.valueOf(stock))
                        .replace("[value]", DECIMAL_FORMAT.format(value))
                        .replace("[cost]", DECIMAL_FORMAT.format(cost))
                        .replace("[change]", DECIMAL_FORMAT.format(difference))
                        .replace("[percent]", DECIMAL_FORMAT.format(percent))
                    );
                    Bukkit.broadcastMessage(format);
                }

            }
        }, 0L, (OreMarket.main().getConfig().getInt("valueupdates.time")* 20L*60));
    }
}
