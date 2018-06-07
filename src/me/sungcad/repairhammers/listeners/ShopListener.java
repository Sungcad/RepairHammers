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
		Player player = (Player) e.getWhoClicked();
		if (e.getInventory().getHolder() instanceof ShopHolder) {
			e.setCancelled(true);
			if (e.getSlot() == e.getRawSlot()) {
				ItemStack item = e.getCurrentItem();
				Optional<Hammer> ohammer = plugin.getHammerManager().getHammer(item);
				if (ohammer.isPresent()) {
					Hammer hammer = ohammer.get();
					item = hammer.getHammerItem(1);
					if (hammer.getBuyCost() > 0) {
						if (plugin.getEconomy().isLoaded()) {
							if (plugin.getEconomy().getEconomy().has(player, hammer.getBuyCost())) {
								player.closeInventory();
								plugin.getEconomy().getEconomy().withdrawPlayer(player, hammer.getBuyCost());
								if (player.getInventory().firstEmpty() != -1) {
									player.getInventory().addItem(item);
								} else {
									player.getWorld().dropItem(player.getLocation(), item);
								}
							} else {
								player.sendMessage(
										ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("error.bal.buy").replace("<cost>", plugin.getFormat().format(hammer.getBuyCost()))));
								return;
							}
						} else {
							plugin.getLogger().warning("Vault not found");
							plugin.getLogger().warning("Hammers with a cost are free");
							player.closeInventory();
							if (player.getInventory().firstEmpty() != -1) {
								player.getInventory().addItem(item);
							} else {
								player.getWorld().dropItem(player.getLocation(), item);
							}
						}
					} else {
						player.closeInventory();
						if (player.getInventory().firstEmpty() != -1) {
							player.getInventory().addItem(item);
						} else {
							player.getWorld().dropItem(player.getLocation(), item);
						}
					}
				}
			}
		}
	}
}
