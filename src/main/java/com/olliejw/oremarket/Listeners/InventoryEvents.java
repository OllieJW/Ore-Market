package com.olliejw.oremarket.Listeners;

import com.olliejw.oremarket.OreMarket;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class InventoryEvents implements Listener {

    public void message (String Message, HumanEntity Player) {
        Player.sendMessage(ChatColor.translateAlternateColorCodes('&', OreMarket.main().getConfig().getString("prefix") + Message));
    }
    public void addMoney (double Money, HumanEntity Player) { // Sold
        OreMarket.getEconomy().depositPlayer((OfflinePlayer) Player, Money);
    }
    public void takeMoney (double Money, HumanEntity Player) { // Buy
        double tax = OreMarket.main().getConfig().getDouble("tax");
        double total = (Money + (Money/tax));
        OreMarket.getEconomy().withdrawPlayer((OfflinePlayer) Player, total);
    }
    public double balance (HumanEntity Player) {
        return OreMarket.getEconomy().getBalance((OfflinePlayer) Player);
    }
    public void valueChange (int Slot, double Current, boolean Positive) {
        double multiplier = OreMarket.main().getConfig().getDouble("multiplier");
        double currentIncrease = ((Current / 100.0) * (100 + multiplier));
        double currentDecrease = ((Current / 100.0) * (100 - multiplier));
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

    @EventHandler
    public void clickEvent (InventoryClickEvent event) {
        org.bukkit.inventory.Inventory playerInventory = event.getWhoClicked().getInventory(); // Player's inventory
        InventoryView playerView = event.getView(); // Player's inventory view
        HumanEntity player = event.getWhoClicked(); // Player that clicked

        if (event.getCurrentItem() == null) { return; } // Null check. Prevents errors
        if (playerView.getTitle().equals(ChatColor.translateAlternateColorCodes('&', title))) { event.setCancelled(true); // I know. Its a bad way of checking.
            double value = OreMarket.main().getGuiConfig().getDouble("items." + event.getSlot() + ".value"); // Ore Value
            int stock = OreMarket.main().getGuiConfig().getInt("items." + event.getSlot() + ".stock"); // Ore stock
            String itemConfig = OreMarket.main().getGuiConfig().getString("items." + event.getSlot() + ".item"); // Config location of item
            assert itemConfig != null;
            ItemStack clickedItem = new ItemStack(Objects.requireNonNull(Material.matchMaterial(itemConfig))); // Item that user clicked

            if (event.getClick() == ClickType.LEFT) { // Sell Mode
                if (playerInventory.containsAtLeast(clickedItem, 1)) {
                    playerInventory.removeItem(clickedItem);
                    addMoney(value, player);
                    valueChange(event.getSlot(), value, true);
                    stockChange(event.getSlot(), stock, 1, true);
                }
            }
            if (event.getClick() == ClickType.RIGHT) { // Buy Mode
                if (balance(player) > value) {
                    if (stock > 1 || stock == -1) { if (value < 1) { return; }
                        playerInventory.addItem(clickedItem);
                        takeMoney(value, player);
                        valueChange(event.getSlot(), value, false);
                        stockChange(event.getSlot(), stock, 1, false);
                    }
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
