/*
 * Copyright (C) 2019  Sungcad
 */
package me.sungcad.repairhammers.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

import me.sungcad.repairhammers.RepairHammerPlugin;

public class CraftingListener implements Listener {
    final RepairHammerPlugin plugin;

    public CraftingListener(RepairHammerPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void craftItem(CraftItemEvent e) {
        ItemStack[] items = e.getInventory().getMatrix();
        for (ItemStack item : items) {
            if (plugin.getHammerManager().getHammer(item).isPresent()) {
                e.setCancelled(true);
                return;
            }
        }
    }
}
