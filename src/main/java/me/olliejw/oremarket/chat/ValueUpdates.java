package me.olliejw.oremarket.chat;

import me.olliejw.oremarket.OreMarket;
import me.olliejw.oremarket.utils.Placeholders;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Objects;

public class ValueUpdates {
    Placeholders plh = new Placeholders();

    public void announceValue() {
        if(OreMarket.main().getConfig().getBoolean("valuemessage.enabled")) {
            Bukkit.getScheduler().scheduleSyncRepeatingTask(OreMarket.main(), () -> {
                for (String key : Objects.requireNonNull(OreMarket.main().getGuiConfig().getConfigurationSection("items")).getKeys(false)) {
                    ConfigurationSection keySection = Objects.requireNonNull(OreMarket.main().getGuiConfig().getConfigurationSection("items")).getConfigurationSection(key);
                    String message = OreMarket.main().getConfig().getString("valuemessage.format");

                    assert keySection != null;
                    assert message != null;

                    for (Player player: Bukkit.getOnlinePlayers()) {
                        if (keySection.getBoolean(".flags.hide")) {
                            return;
                        }
                        player.sendMessage(plh.format(message, player, keySection));
                    }
                }

            }, 0L, (OreMarket.main().getConfig().getInt("valuemessage.time")* 20L*60));
        }
    }
}
