package me.olliejw.oremarket.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Consumer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class Updates {

    private final JavaPlugin plugin;
    private final int resourceId;

    public Updates (JavaPlugin plugin, int resourceId) {
        this.plugin = plugin;
        this.resourceId = resourceId;
    }

    public void getVersion (final Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId).openStream(); Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNext()) {
                    consumer.accept(scanner.next());
                }
            } catch (IOException exception) {
                this.plugin.getLogger().info("Cannot look for updates: " + exception.getMessage());
            }
        });
    }

    public void notify(Player player, String version, String newVersion) {
        if (player.isOp()) {
            if (version.contains("DEV")) {
                player.sendMessage(ChatColor.RED + "" + ChatColor.STRIKETHROUGH + "============================");
                player.sendMessage(ChatColor.RED + "• You are currently using an");
                player.sendMessage(ChatColor.RED + "• unreleased version of OreMarket!");
                player.sendMessage(ChatColor.RED + "• Download a stable version here:");
                player.sendMessage(ChatColor.RED + "• spigotmc.org/resources/91015");
                player.sendMessage(ChatColor.RED + "" + ChatColor.STRIKETHROUGH + "============================");
            }
            else if (version.equals(newVersion)) {
                player.sendMessage(ChatColor.GREEN + "" + ChatColor.STRIKETHROUGH + "============================");
                player.sendMessage(ChatColor.GREEN + "• OreMarket v" + version + " is up-to-date!");
                player.sendMessage(ChatColor.GREEN + "" + ChatColor.STRIKETHROUGH + "============================");
            }
            else {
                player.sendMessage(ChatColor.GOLD + "" + ChatColor.STRIKETHROUGH + "============================");
                player.sendMessage(ChatColor.GOLD + "• OreMarket v" + newVersion + " is now available!");
                player.sendMessage(ChatColor.GOLD + "• Your on still on v" + version);
                player.sendMessage(ChatColor.GOLD + "• spigotmc.org/resources/91015");
                player.sendMessage(ChatColor.GOLD + "" + ChatColor.STRIKETHROUGH + "============================");
            }
        }
    }
}
