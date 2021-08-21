package me.olliejw.oremarket.utils;

import me.olliejw.oremarket.OreMarket;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.text.Format;
import java.util.Objects;

public class Placeholders extends PlaceholderExpansion {

    // Return placeholders
    public String format(String string, HumanEntity player, ConfigurationSection configurationSection) {
        Player playerObj = (Player) player;
        String name = configurationSection.getString("name");
        final Format DECIMAL_FORMAT = new DecimalFormat("#0.0#");
        int stock = configurationSection.getInt("stock");
        double value = configurationSection.getDouble("value");
        double cost = configurationSection.getDouble("cost");
        double difference = (value - cost);
        double change = (value / cost);
        double percent = ((change * 100) - 100);
        double balance = OreMarket.getEconomy().getBalance(playerObj);

        assert name != null;
        String formatted = ChatColor.translateAlternateColorCodes('&', string
                .replace("[name]", name)
                .replace("[stock]", String.valueOf(stock))
                .replace("[value]", DECIMAL_FORMAT.format(value))
                .replace("[cost]", DECIMAL_FORMAT.format(cost))
                .replace("[change]", DECIMAL_FORMAT.format(difference))
                .replace("[percent]", DECIMAL_FORMAT.format(percent))
                .replace("[balance]", DECIMAL_FORMAT.format(balance))
        );
        return PlaceholderAPI.setPlaceholders(playerObj, formatted);
    }


    // Placeholder API

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getAuthor(){
        return OreMarket.main().getDescription().getAuthors().toString();
    }

    @Override
    public String getIdentifier() {
        return "oremarket";
    }

    @Override
    public String getVersion() {
        return OreMarket.main().getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier){
        for (String key : Objects.requireNonNull(OreMarket.main().getGuiConfig().getConfigurationSection("items")).getKeys(false)) {
            ConfigurationSection keySection = Objects.requireNonNull(OreMarket.main().getGuiConfig().getConfigurationSection("items")).getConfigurationSection(key);
            assert keySection != null;

            final Format df = new DecimalFormat("#0.0#");
            double difference = (keySection.getDouble("value") - keySection.getDouble("cost"));
            double change = (keySection.getDouble("value") / keySection.getDouble("cost"));
            double percent = ((change * 100) - 100);;

            // %oremarket_{identifier}_{slot}%
            if (identifier.equals("value_" + key)) {
                return String.valueOf(keySection.getDouble("value"));
            }
            if (identifier.equals("name_" + key)) {
                return keySection.getString("name");
            }
            if (identifier.equals("stock_" + key)) {
                return String.valueOf(keySection.getInt("stock"));
            }
            if (identifier.equals("cost_" + key)) {
                return String.valueOf(keySection.getDouble("cost"));
            }
            if (identifier.equals("change_" + key)) {
                return df.format(difference);
            }
            if (identifier.equals("percent_" + key)) {
                return df.format(percent);
            }
            if (identifier.equals("test")) {
                return "Oremarket PAPI works!";
            }
        }
        return null;
    }
}
