package com.olliejw.oremarket.Listeners;

import com.olliejw.oremarket.Inventory.CreateGUI;
import com.olliejw.oremarket.OreMarket;
import com.olliejw.oremarket.Utils.Placeholders;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.text.DecimalFormat;
import java.text.Format;
import java.util.Objects;
import java.lang.Math;

public class InventoryEvents implements Listener {
    public void message (String Message, HumanEntity Player) {
        Player.sendMessage(ChatColor.translateAlternateColorCodes('&', OreMarket.main().getConfig().getString("prefix") + Message));
    }

    public void changePlayerBalance(double Money, HumanEntity Player, boolean operation) { // Buy
        double total = calculateTotalWithTax(Money);
        if (Money > 0) {
            if (operation) // true is buy, false is sell
                OreMarket.getEconomy().withdrawPlayer((OfflinePlayer) Player, total);
            else
                OreMarket.getEconomy().depositPlayer((OfflinePlayer) Player, total);
        } else if (Money < 0) {
            if (operation)
                OreMarket.getEconomy().depositPlayer((OfflinePlayer) Player, Math.abs(total));
            else
                OreMarket.getEconomy().withdrawPlayer((OfflinePlayer) Player, Math.abs(total));
        }
    }

    private double calculateTotalWithTax(double price) {
        double tax = OreMarket.main().getConfig().getDouble("tax", 0.0);
        double total;
        if (tax == 0.0) {
            total = price;
        } else {
            total = (price + (Math.abs(price)*tax));
        }
        return total;
    }

    public double balance (HumanEntity Player) {
        return OreMarket.getEconomy().getBalance((OfflinePlayer) Player);
    }
    public void valueChange (int Slot, double Current, boolean Positive) {
        double multiplier = OreMarket.main().getConfig().getDouble("multiplier");
        double currentIncrease = Current + (Math.abs(Current) * (multiplier / 100.0));
        double currentDecrease = Current - (Math.abs(Current) * (multiplier / 100.0));
        if (Positive) {
            OreMarket.main().getGuiConfig().set("items." + Slot + ".value", currentIncrease);
        } else {
            OreMarket.main().getGuiConfig().set("items." + Slot + ".value", currentDecrease);
        }
        OreMarket.main().saveGuiConfig();
    }
    public void stockChange (int Slot, int Current, int Amount, boolean Add) {
        if (OreMarket.main().getGuiConfig().getDouble("items." + Slot + ".stock") == -1) {
            return;
        }
        if (Add) {
            OreMarket.main().getGuiConfig().set("items." + Slot + ".stock", Current + Amount);
        }
        else {
            OreMarket.main().getGuiConfig().set("items." + Slot + ".stock", Current - Amount);
        }
        OreMarket.main().saveGuiConfig();
    }

    String title = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(OreMarket.main().getGuiConfig().getString("gui.title")));
    Placeholders plh = new Placeholders();
    CreateGUI createGUI = new CreateGUI();

    @EventHandler
    public void clickEvent (InventoryClickEvent event) {
        Inventory playerInventory = event.getWhoClicked().getInventory(); // Player's inventory
        InventoryView playerView = event.getView(); // Player's inventory view
        HumanEntity player = event.getWhoClicked(); // Player that clicked

        ConfigurationSection keySection = null;
        for (String key : Objects.requireNonNull(OreMarket.main().getGuiConfig().getConfigurationSection("items")).getKeys(false)) {
            keySection = Objects.requireNonNull(OreMarket.main().getGuiConfig().getConfigurationSection("items")).getConfigurationSection(key);
        }
        if (event.getCurrentItem() == null) { return; } // Null check. Prevents errors
        if (playerView.getTitle().equals(ChatColor.translateAlternateColorCodes('&', title))) {
            event.setCancelled(true); // I know. Its a bad way of checking.
            String itemConfig = OreMarket.main().getGuiConfig().getString("items." + event.getSlot() + ".item"); // Config location of item
            ItemStack clickedItem;

            assert itemConfig != null;
            clickedItem = new ItemStack(Objects.requireNonNull(Material.matchMaterial(itemConfig))); // Item that user clicked
            int slot = event.getSlot();

            if (OreMarket.main().getGuiConfig().contains("items." + event.getSlot() + ".commands")) {
                assert keySection != null;
                for (String command : Objects.requireNonNull(OreMarket.main().getGuiConfig().getStringList("items." + event.getSlot() + ".commands"))) {
                    if (command != null) {
                        String toSend = plh.format(command, player, keySection);
                        if (toSend.equals("[close]")) {
                            player.closeInventory();
                        }
                        else if (toSend.contains("[msg]")) {
                            player.sendMessage(toSend.replace("[msg] ", ""));
                        }
                        else {
                            Bukkit.dispatchCommand(player, toSend);
                        }
                    }
                }
                return;
            }
                
            if ((event.getClick() == ClickType.LEFT)) { // Sell Mode
                if (!OreMarket.main().getGuiConfig().getBoolean("items." + event.getSlot() + ".buyonly")) {
                    double value = OreMarket.main().getGuiConfig().getDouble("items." + slot + ".value");
                    int stock = OreMarket.main().getGuiConfig().getInt("items." + slot + ".stock");
                    if (playerInventory.containsAtLeast(clickedItem, 1) || OreMarket.main().getGuiConfig().getBoolean("items." + slot + ".copymeta")) {
                        if (OreMarket.main().getGuiConfig().getBoolean("items." + slot + ".copymeta")) {
                            playerInventory.removeItem(event.getCurrentItem());
                        }
                        else {
                            playerInventory.removeItem(clickedItem);
                        }
                        changePlayerBalance(value, player, false);
                        valueChange(slot, value, false);
                        stockChange(slot, stock, 1, true);

                        String message = OreMarket.main().getGuiConfig().getString("messages.successfully-sold", "&aYou have successfully sold the item!");
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                    } else {
                        String message = OreMarket.main().getGuiConfig().getString("messages.no-item", "&cYou don't have that item!");
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                    }
                } else {
                    String message = OreMarket.main().getGuiConfig().getString("messages.buy-only", "&cThis item can only be bought");
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                }
            }
            if ((event.getClick() == ClickType.RIGHT)) { // Buy Mode
                if (!OreMarket.main().getGuiConfig().getBoolean("items." + event.getSlot() + ".sellonly")) {
                    double value = OreMarket.main().getGuiConfig().getDouble("items." + slot + ".value");
                    int stock = OreMarket.main().getGuiConfig().getInt("items." + slot + ".stock");
                    if (balance(player) > value) {
                        if (stock > 1 || stock == -1) {
                            if (OreMarket.main().getGuiConfig().getBoolean("items." + slot + ".copymeta")) {
                                playerInventory.addItem(event.getCurrentItem());
                            }
                            else {
                                playerInventory.addItem(clickedItem);
                            }
                            changePlayerBalance(value, player, true);
                            valueChange(slot, value, true);
                            stockChange(slot, stock, 1, false);
                            String message = OreMarket.main().getGuiConfig().getString("messages.successfully-bought", "&aYou have successfully bought the item!");
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                        } else {
                            String message = OreMarket.main().getGuiConfig().getString("messages.no-stocks", "&cMarket is run out of item stocks!");
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                        }
                    } else {
                        String message = OreMarket.main().getGuiConfig().getString("messages.insufficient-balance", "&cYou don't have enough money to buy this item!");
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                    }
                } else {
                    String message = OreMarket.main().getGuiConfig().getString("messages.sell-only", "&cThis item can only be sold");
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                }
            }
        }
    }

    @EventHandler
    public void moveEvent (InventoryDragEvent event) {
        InventoryView playerView = event.getView(); // Player's inventory view
        if (playerView.getTitle().equals(ChatColor.translateAlternateColorCodes('&', title))) { // Using our GUI
            event.setCancelled(true);
        }
    }
}
