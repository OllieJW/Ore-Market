package me.olliejw.oremarket.listeners;

import me.olliejw.oremarket.OreMarket;
import me.olliejw.oremarket.utils.Updates;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {
    Updates updates = new Updates(OreMarket.main(), 91015);

    @EventHandler
    public void playerJoin (PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if(player.isOp()) {
            new Updates(OreMarket.main(), 91015).getVersion(version -> {
                updates.notify(player, OreMarket.main().getDescription().getVersion(), version);
            });
        }

    }
}

