/*
 * Copyright (C) 2019  Sungcad
 */
package me.sungcad.repairhammers.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import me.sungcad.repairhammers.RepairHammerPlugin;

public class PlaceListener implements Listener {
    RepairHammerPlugin plugin;

    public PlaceListener(RepairHammerPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(BlockPlaceEvent event) {
        if (plugin.getHammerManager().getHammer(event.getItemInHand()).isPresent()) {
            event.setCancelled(true);
        }
    }

}
