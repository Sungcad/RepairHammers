/*
 * Copyright (C) 2018  Sungcad
 */
package me.sungcad.repairhammers.listeners;

import java.util.Optional;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import me.sungcad.repairhammers.RepairHammerPlugin;
import me.sungcad.repairhammers.gui.ShopHolder;
import me.sungcad.repairhammers.hammers.Hammer;

public class ShopListener implements Listener {
    RepairHammerPlugin plugin;

    public ShopListener(RepairHammerPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClickEvent(InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof ShopHolder))
            return;
        e.setCancelled(true);
        if (e.getRawSlot() != e.getSlot())
            return;
        Optional<Hammer> ohammer = plugin.getHammerManager().getHammer(e.getCurrentItem());
        if (!ohammer.isPresent())
            return;
        Hammer hammer = ohammer.get();
        ItemStack item = hammer.getHammerItem(1);
        Player player = (Player) e.getWhoClicked();
        if (hammer.getBuyCost() > 0) {
            if (plugin.getEconomy().isLoaded()) {
                if (plugin.getEconomy().getEconomy().has(player, hammer.getBuyCost())) {
                    player.closeInventory();
                    plugin.getEconomy().getEconomy().withdrawPlayer(player, hammer.getBuyCost());
                    giveHammer(player, item);
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("error.bal.buy").replace("<cost>", plugin.getFormat().format(hammer.getBuyCost()))));
                    return;
                }
            } else {
                plugin.getLogger().warning("Vault not found");
                plugin.getLogger().warning("Hammers with a cost are free");
                player.closeInventory();
                giveHammer(player, item);
            }
        } else {
            player.closeInventory();
            giveHammer(player, item);
        }
    }

    private void giveHammer(Player player, ItemStack hammer) {
        if (player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(hammer);
        } else {
            player.getWorld().dropItem(player.getLocation(), hammer);
        }
    }
}