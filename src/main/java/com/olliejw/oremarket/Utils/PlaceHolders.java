package com.olliejw.oremarket.Utils;

import com.olliejw.oremarket.OreMarket;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.text.Format;

public class PlaceHolders {

    public String format (String string, HumanEntity player, ConfigurationSection configurationSection) {
        Player playerObj = (Player) player;
        String name = configurationSection.getString("name");
        final Format DECIMAL_FORMAT = new DecimalFormat("#0.0#");
        int stock = configurationSection.getInt("stock");
        double value = configurationSection.getDouble("value");
        double cost = configurationSection.getDouble("cost");
        double difference = (value-cost);
        double change = (value/cost);
        double percent = ((change*100)-100);
        double balance = OreMarket.getEconomy().getBalance(playerObj);

        assert name != null;
        return ChatColor.translateAlternateColorCodes('&', string
                .replace("[name]", name)
                .replace("[stock]", String.valueOf(stock))
                .replace("[value]", DECIMAL_FORMAT.format(value))
                .replace("[cost]", DECIMAL_FORMAT.format(cost))
                .replace("[change]", DECIMAL_FORMAT.format(difference))
                .replace("[percent]", DECIMAL_FORMAT.format(percent))
                .replace("[balance]", DECIMAL_FORMAT.format(balance))
        );
    }
}
