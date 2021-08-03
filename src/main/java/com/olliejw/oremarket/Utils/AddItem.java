package com.olliejw.oremarket.Utils;

import com.olliejw.oremarket.OreMarket;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddItem implements Listener {
    String title = OreMarket.main().getGuiConfig().getString("gui.title");
    int rows = OreMarket.main().getGuiConfig().getInt("gui.rows");
    Inventory inv = Bukkit.createInventory(null, rows*9, ChatColor.translateAlternateColorCodes('&', title));
    Placeholders plh = new Placeholders();

    public void createGUI (Player player) {
        for (String key : Objects.requireNonNull(OreMarket.main().getGuiConfig().getConfigurationSection("items")).getKeys(false)) {
            ConfigurationSection keySection = Objects.requireNonNull(OreMarket.main().getGuiConfig().getConfigurationSection("items")).getConfigurationSection(key);

            assert keySection != null;

            ItemStack item = new ItemStack(Objects.requireNonNull(Material.matchMaterial(Objects.requireNonNull(keySection.getString("item")))));
            ItemMeta meta = item.getItemMeta();

            String name = keySection.getString("name");
            assert meta != null;
            assert name != null;
            meta.setDisplayName(plh.format(name, player, keySection));

            List<String> lore = new ArrayList<>();
            for (String loreItem : Objects.requireNonNull(keySection.getStringList("lore"))) {
                String string = plh.format(loreItem, player, keySection);
                lore.add(string);
            }

            meta.setLore(lore);

            item.setItemMeta(meta);
            inv.setItem(Integer.parseInt(key), item);
            player.openInventory(inv);
        }
    }
}

