/*
 * Copyright (C) 2020  Sungcad
 */
package me.sungcad.repairhammers.listeners;

import java.util.ArrayList;
import java.util.List;

import me.sungcad.repairhammers.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.sungcad.repairhammers.RepairHammerPlugin;
import me.sungcad.repairhammers.events.HammerUseEvent;
import me.sungcad.repairhammers.hammers.Hammer;
import me.sungcad.repairhammers.itemhooks.CustomItemHook;

public class AnvilListener implements Listener {
	final RepairHammerPlugin plugin;
	private static boolean enabled;
	private final List<Player> cooldowns = new ArrayList<>();

	public AnvilListener(RepairHammerPlugin plugin, boolean enable) {
		this.plugin = plugin;
		enabled = enable;
	}

	@EventHandler
	public void prepareAnvil(PrepareAnvilEvent e) {
		if (!enabled)
			return;
		if (e.getViewers().size() < 1)
			return;
		Player player = (Player) e.getViewers().get(0);
		ItemStack slot0 = e.getInventory().getItem(0);
		ItemStack slot1 = e.getInventory().getItem(1);
		if (slot0 == null || slot1 == null)
			return;
		Hammer hammer = plugin.getHammerManager().getHammer(slot1).orElse(null);
		if (hammer == null)
			return;
		if (!hammer.canFix(slot0) || !hammer.canUse(player)) {
			if (!cooldowns.contains(player)) {
				hammer.getCantUseMessage().forEach(s -> player.sendMessage(ColorUtil.translateColors(s)));
				cooldowns.add(player);
				new BukkitRunnable() {
					@Override
					public void run() {
						cooldowns.remove(player);
					}
				}.runTaskLater(plugin, 5);
			}
			return;
		}
		CustomItemHook hook = plugin.getCustomItemManager().getHook(slot0);
		if (hook == null) {
			return;
		}
		int damage;
		if (hammer.isPercent())
			damage = (int) Math.min(hook.getMaxDurability(slot0) * hammer.getFixAmount() * .01, hook.getDamage(slot0));
		else
			damage = Math.min(hook.getDamage(slot0), hammer.getFixAmount(slot1));
		if (damage <= 0)
			return;
		ItemStack result = hook.fixItem(slot0.clone(), damage);
		e.getInventory().setRepairCost(0);
		e.setResult(result);

	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if (!enabled)
			return;
		if (!(e.getInventory() instanceof AnvilInventory))
			return;
		if (e.getRawSlot() != 2)
			return;
		AnvilInventory ai = (AnvilInventory) e.getInventory();
		if (ai.getItem(0) == null || ai.getItem(1) == null || ai.getItem(2) == null)
			return;
		Hammer hammer = plugin.getHammerManager().getHammer(e.getInventory().getItem(1)).orElse(null);
		if (hammer == null)
			return;
		Player player = (Player) e.getWhoClicked();
		if (!hammer.canAfford(player, false)) {
			player.sendMessage(ColorUtil.translateColors(plugin.getConfig().getString("error.bal.use").replace("<cost>", plugin.getFormat().format(hammer.getUseCost()))));
			e.setCancelled(true);
			return;
		}
		e.setCancelled(true);
		HammerUseEvent hue = new HammerUseEvent(hammer, player, 1, ai);
		Bukkit.getPluginManager().callEvent(hue);
		if (hue.isCancelled()) {
			return;
		}
		hammer.payCost(player, false);
		if (plugin.getConfig().getBoolean("sound.enabled", false)) {
			try {
				Sound sound = Sound.valueOf(plugin.getConfig().getString("sound.sound", "BLOCK_ANVIL_USE").toUpperCase());
				player.stopSound(sound);
				player.playSound(player.getEyeLocation(), sound, 1, 1);
			} catch (IllegalArgumentException iae) {
				plugin.getLogger().warning("error unable to play sound " + this.plugin.getConfig().getString("sound.sound").toUpperCase());
			}
		}
		hammer.getUseMessage().forEach(line -> player.sendMessage(ColorUtil.translateColors(line)));
		ItemStack slot0 = ai.getItem(0);
		ItemStack slot1 = ai.getItem(1);
		ItemStack slot2 = ai.getItem(2);
		CustomItemHook hook = plugin.getCustomItemManager().getHook(slot0);
		int damage;
		if (hammer.isPercent())
			damage = (int) Math.min(hook.getMaxDurability(slot0) * hammer.getFixAmount() * .01, hook.getDamage(slot0));
		else
			damage = Math.min(hook.getDamage(slot0), hammer.getFixAmount(slot1));
		if (hammer.useDurability(ai.getItem(1), damage) == null)
			ai.setItem(1, null);
		ai.setItem(0, slot2);
		ai.setItem(2, null);
		if (hammer.isFixall()) {
			for (ItemStack item : player.getInventory()) {
				if (hammer.canFix(item)) {
					plugin.getCustomItemManager().fixItem(item, damage);
				}
			}
		}
	}

	static public void setEnabled(boolean used) {
		enabled = used;
	}
}
