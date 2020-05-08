/*
 * Copyright (C) 2019  Sungcad
 */
package me.sungcad.repairhammers.listeners;

import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import me.sungcad.repairhammers.RepairHammerPlugin;
import me.sungcad.repairhammers.events.HammerBuyEvent;
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
		Player player = (Player) e.getWhoClicked();
		if (hammer.canAfford(player, true)) {
			HammerBuyEvent hbe = new HammerBuyEvent(hammer, player);
			Bukkit.getPluginManager().callEvent(hbe);
			if (!hbe.isCancelled()) {
				player.closeInventory();
				hammer.payCost(player, true);
				giveHammer(player, hammer.getHammerItem(1));
			}
		} else {
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("error.bal.buy").replace("<cost>", plugin.getFormat().format(hammer.getBuyCost()))));
			return;
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