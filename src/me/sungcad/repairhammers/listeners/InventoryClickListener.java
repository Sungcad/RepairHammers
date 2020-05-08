/*
 * Copyright (C) 2019  Sungcad
 */
package me.sungcad.repairhammers.listeners;

import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import me.sungcad.repairhammers.RepairHammerPlugin;
import me.sungcad.repairhammers.events.HammerUseEvent;
import me.sungcad.repairhammers.hammers.Hammer;
import me.sungcad.repairhammers.itemhooks.CustomItemHook;

public class InventoryClickListener implements Listener {
	private RepairHammerPlugin plugin;
	private static boolean enabled;

	public InventoryClickListener(RepairHammerPlugin plugin, boolean enable) {
		this.plugin = plugin;
		enabled = enable;
	}

	public static void setEnabeld(boolean enable) {
		enabled = enable;
	}

	@EventHandler
	public void onClickEvent(InventoryClickEvent e) {
		if (!enabled)
			return;
		Player player = (Player) e.getWhoClicked();
		if (!player.getOpenInventory().getType().equals(InventoryType.CRAFTING))
			return;
		ItemStack cursor = e.getCursor();
		Optional<Hammer> ohammer = plugin.getHammerManager().getHammer(cursor);
		if (!ohammer.isPresent())
			return;
		Hammer hammer = ohammer.get();
		if (!hammer.canUse(player))
			return;
		ItemStack target = e.getCurrentItem();
		if (target == null)
			return;
		CustomItemHook hook = plugin.getCustomItemManager().getHook(target);
		if (hook == null)
			return;
		if (!hammer.canFix(target))
			return;
		int damage;
		if (hammer.isPercent())
			damage = (int) Math.min(hook.getMaxDurability(target) * hammer.getFixAmount() * .01, hook.getDamage(target));
		else
			damage = Math.min(hook.getDamage(target), hammer.getFixAmount(cursor));
		if (damage <= 0)
			return;
		e.setCancelled(true);
		if (!hammer.canAfford(player, false)) {
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("error.bal.use").replace("<cost>", plugin.getFormat().format(hammer.getUseCost()))));
			return;
		}
		HammerUseEvent hue = new HammerUseEvent(hammer, player, e.getSlot());
		Bukkit.getPluginManager().callEvent(hue);
		if (hue.isCancelled())
			return;
		hammer.payCost(player, false);
		if (hammer.useDurability(cursor, damage) == null)
			player.setItemOnCursor(null);
		if (hammer.isFixall()) {
			for (ItemStack item : player.getInventory()) {
				if (hammer.canFix(item)) {
					plugin.getCustomItemManager().fixItem(item, damage);
				}
			}
		} else {
			hook.fixItem(target, damage);
		}
		if (plugin.getConfig().getBoolean("sound.enabled", false)) {
			try {
				Sound sound = Sound.valueOf(plugin.getConfig().getString("sound.sound", "BLOCK_ANVIL_USE"));
				player.playSound(player.getEyeLocation(), sound, 1, 1);
			} catch (IllegalArgumentException iae) {
				plugin.getLogger().warning("error unable to play sound " + this.plugin.getConfig().getString("sound.sound").toUpperCase());
			}
		}
		hammer.getUseMessage().forEach(line -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', line)));
	}
}