package com.olliejw.oremarket.Utils;

import com.olliejw.oremarket.OreMarket;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class Events implements Listener {

    public void debug (String Message, HumanEntity Player) {
        Player.sendMessage(ChatColor.translateAlternateColorCodes('&', Message));
    }

    public void addMoney (double Money, HumanEntity Player) {
        EconomyResponse r = OreMarket.getEconomy().depositPlayer((OfflinePlayer) Player, Money);
        if(r.transactionSuccess()) {
            Player.sendMessage(String.format("You were given %s and now have %s", OreMarket.getEconomy().format(r.amount), OreMarket.getEconomy().format(r.balance)));
        } else {
            Player.sendMessage(String.format("An error occured: %s", r.errorMessage));
        }
    }
    public void takeMoney (double Money, HumanEntity Player) {
        EconomyResponse r = OreMarket.getEconomy().withdrawPlayer((OfflinePlayer) Player, Money);
        if(r.transactionSuccess()) {
            Player.sendMessage(String.format("You paid %s and now have %s", OreMarket.getEconomy().format(r.amount), OreMarket.getEconomy().format(r.balance)));
        } else {
            Player.sendMessage(String.format("An error occured: %s", r.errorMessage));
        }
    }
    public double balance (HumanEntity Player) {
        double bal = OreMarket.getEconomy().getBalance((OfflinePlayer) Player);
        return bal;
    }

    @EventHandler
    public void clickEvent (InventoryClickEvent event) {
        Inventory playerInventory = event.getWhoClicked().getInventory(); // Player's inventory
        InventoryView playerView = event.getView(); // Player's inventory view
        HumanEntity player = event.getWhoClicked(); // Player that clicked

        if (event.getCurrentItem() == null) { // Removes the giant error when clicking an empty slot
            return;
        } // Null check. Prevents errors

        if (playerView.getTitle().equals("OreMarket Coming soon!")) { event.setCancelled(true); // I know. Its a bad way of checking.
            Double value = OreMarket.main().getConfig().getDouble("items." + event.getSlot() + ".value"); // Ore Value
            String itemConfig = OreMarket.main().getConfig().getString("items." + event.getSlot() + ".item"); // Config location of item
            ItemStack clickedItem = new ItemStack(Material.matchMaterial(itemConfig)); // Item that user clicked
            ItemStack clicked = event.getCurrentItem(); // The exact item the player clicked
            Material clickedType = event.getCurrentItem().getType(); // Clicked item as material ("Material.DIAMOND" rather than a long list of json crap)

            if (event.getClick() == ClickType.LEFT) { // Sell Mode
                if (playerInventory.containsAtLeast(clickedItem, 1)) { // Do they have this ore?
                    playerInventory.removeItem(clickedItem);
                    addMoney(value, player);
                }
            }
            if (event.getClick() == ClickType.RIGHT) { // Buy Mode
                if (balance(player) > value) { // Do they have enough money
                    playerInventory.addItem(clickedItem);
                    takeMoney(value, player);
                } else {
                    debug("&4You do not have enough money!", player);
                }
            }
        }
    }

    @EventHandler
    public void moveEvent (InventoryDragEvent event) {
        InventoryView playerView = event.getView(); // Player's inventory view
        if (playerView.getTitle().equals("OreMarket Coming soon!")) { // Using our GUI
            event.setCancelled(true);
        }
    }
}
