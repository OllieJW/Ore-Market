package com.olliejw.oremarket.Utils;

import com.olliejw.oremarket.OreMarket;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddItem implements Listener, CommandExecutor {
    String title = OreMarket.main().getGuiConfig().getString("gui.title");
    int rows = OreMarket.main().getGuiConfig().getInt("gui.rows");
    Inventory inv = Bukkit.createInventory(null, rows*9, ChatColor.translateAlternateColorCodes('&', title));


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("openmarket")) {
            Player player = (Player) sender;

            for (String key : Objects.requireNonNull(OreMarket.main().getGuiConfig().getConfigurationSection("items")).getKeys(false)) {
                ConfigurationSection keySection = Objects.requireNonNull(OreMarket.main().getGuiConfig().getConfigurationSection("items")).getConfigurationSection(key);

                assert keySection != null;
                int stock = keySection.getInt("stock");

                final Format DECIMAL_FORMAT = new DecimalFormat("#0.0#");
                double value = keySection.getDouble("value");
                double cost = keySection.getDouble("cost");
                double change = (value/cost);
                double percent = (change*100);

                ItemStack item = new ItemStack(Objects.requireNonNull(Material.matchMaterial(Objects.requireNonNull(keySection.getString("item")))));
                ItemMeta meta = item.getItemMeta();

                assert meta != null;
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(keySection.getString("name")))
                    .replace("[stock]", DECIMAL_FORMAT.format(stock))
                    .replace("[percent]", DECIMAL_FORMAT.format(percent))
                    .replace("[value]", DECIMAL_FORMAT.format(value))
                    .replace("[cost]", DECIMAL_FORMAT.format(cost))
                );

                List<String> lore = new ArrayList<>();
                for (String loreItem : Objects.requireNonNull(keySection.getStringList("lore"))) {
                    String string = ChatColor.translateAlternateColorCodes('&', loreItem)
                        .replace("[stock]", DECIMAL_FORMAT.format(stock))
                        .replace("[percent]", DECIMAL_FORMAT.format(percent))
                        .replace("[value]", DECIMAL_FORMAT.format(value))
                        .replace("[cost]", DECIMAL_FORMAT.format(cost))
                    ;
                    lore.add(string);
                }

                meta.setLore(lore);

                item.setItemMeta(meta);
                inv.setItem(Integer.parseInt(key), item);
                player.openInventory(inv);


            }
        }
        return true;
    }
}

