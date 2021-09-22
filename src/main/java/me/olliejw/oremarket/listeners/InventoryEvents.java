package me.olliejw.oremarket.listeners;

import me.olliejw.oremarket.OreMarket;
import me.olliejw.oremarket.menus.MainGUI;
import me.olliejw.oremarket.utils.Placeholders;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class InventoryEvents implements Listener {
    private double calculateTotalWithTax(double price, boolean operation) {
        double tax = OreMarket.main().getConfig().getDouble("tax", 0.0);
        double total;
        if (tax == 0.0) {
            total = price;
        } else {
            if (operation) {
                    total = (price + (price * (tax/100)));
            } else {
                total = (price - (price * (tax/100)));
            }
        }
        return total;
    }
    private void changePlayerBalance(double Money, HumanEntity player, boolean operation, int Slot) { // Buy
        double total = calculateTotalWithTax(Money, operation);
        if (Money > 0) {
            if (operation) { // true is take, false is give
                OreMarket.getEconomy().withdrawPlayer((OfflinePlayer) player, total);
            } else {
                OreMarket.getEconomy().depositPlayer((OfflinePlayer) player, total);
            }
        }
        OreMarket.main().getGuiConfig().set("items." + Slot + ".value", total+(total*(OreMarket.main().getConfig().getDouble("multiplier", 0.01))));
        OreMarket.main().saveGuiConfig();
    }
    private double balance (HumanEntity Player) {
        return OreMarket.getEconomy().getBalance((OfflinePlayer) Player);
    }

    String title = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(OreMarket.main().getGuiConfig().getString("gui.title")));
    Placeholders plh = new Placeholders();
    MainGUI mainGUI = new MainGUI();

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
            assert itemConfig != null;
            ItemStack clickedItem = new ItemStack(Objects.requireNonNull(Material.matchMaterial(itemConfig))); // Item that user clicked
            int slot = event.getSlot();

            if (OreMarket.main().getGuiConfig().contains("items." + event.getSlot() + ".commands")) {
                for (String command : Objects.requireNonNull(OreMarket.main().getGuiConfig().getStringList("items." + event.getSlot() + ".commands"))) {
                    if (command != null) {
                        assert keySection != null;
                        String toSend = plh.format(command, player, keySection);
                        if (toSend.equals("[close]")) {
                            player.closeInventory();
                            return;
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
                if (OreMarket.main().getGuiConfig().getBoolean("items." + event.getSlot() + ".buyonly")) {
                    // You cannot sell this, Buy only item
                    String message = OreMarket.main().getMsgConfig().getString("messages.buy-only", "&cThis item can only be bought");
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                    return;
                }

                if (!(playerInventory.containsAtLeast(clickedItem, 1))) {
                    String message = OreMarket.main().getMsgConfig().getString("messages.no-item", "&cYou don't have that item!");
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                    return;
                }

                double itemValue = OreMarket.main().getGuiConfig().getDouble("items." + slot + ".value");
                int itemStock = OreMarket.main().getGuiConfig().getInt("items." + slot + ".stock");

                if (OreMarket.main().getGuiConfig().getBoolean("items." + slot + ".copymeta")) {
                    // Take any type of the clicked item (e.g. custom name)
                    playerInventory.removeItem(event.getCurrentItem());
                }
                else {
                    // Take the EXACT item (e.g. exact custom name)
                    playerInventory.removeItem(clickedItem);
                }

                changePlayerBalance(itemValue, player, false, slot);
                OreMarket.main().getGuiConfig().set("items." + slot + ".stock", itemStock+1);
                OreMarket.main().saveGuiConfig();

                String message = OreMarket.main().getMsgConfig().getString("messages.successfully-sold", "&aYou have successfully sold this item!");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            }
            if ((event.getClick() == ClickType.RIGHT)) { // Buy Mode
                if (OreMarket.main().getGuiConfig().getBoolean("items." + event.getSlot() + ".sellonly")) {
                    // You cannot buy this, Sell only item
                    String message = OreMarket.main().getMsgConfig().getString("messages.sell-only", "&cThis item can only be sold");
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                    return;
                }

                double itemValue = OreMarket.main().getGuiConfig().getDouble("items." + slot + ".value");
                int itemStock = OreMarket.main().getGuiConfig().getInt("items." + slot + ".stock");

                if (balance(player) < itemValue) {
                    String message = OreMarket.main().getMsgConfig().getString("messages.insufficient-balance", "&cYou don't have enough money to buy this item!");
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                    return;
                }

                if (OreMarket.main().getGuiConfig().getBoolean("items." + slot + ".copymeta")) {
                    // Take any type of the clicked item (e.g. custom name)
                    playerInventory.addItem(event.getCurrentItem());
                }
                else {
                    // Take the EXACT item (e.g. exact custom name)
                    playerInventory.addItem(clickedItem);
                }

                changePlayerBalance(itemValue, player, true, slot);
                OreMarket.main().getGuiConfig().set("items." + slot + ".stock", itemStock-1);
                OreMarket.main().saveGuiConfig();

                String message = OreMarket.main().getMsgConfig().getString("messages.successfully-bought", "&aYou have successfully bought the item!");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            }
            mainGUI.createGUI((Player) player); // Reload GUI
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
