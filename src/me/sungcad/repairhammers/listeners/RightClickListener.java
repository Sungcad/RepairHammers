/*
 * Copyright (C) 2019  Sungcad
 */
package me.sungcad.repairhammers.listeners;

import static org.bukkit.event.block.Action.RIGHT_CLICK_AIR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import me.sungcad.repairhammers.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.sungcad.repairhammers.RepairHammerPlugin;
import me.sungcad.repairhammers.events.HammerUseEvent;
import me.sungcad.repairhammers.hammers.Hammer;
import me.sungcad.repairhammers.itemhooks.CustomItemHook;

public class RightClickListener implements Listener {
	private final RepairHammerPlugin plugin;
	private final Map<Player, Hammer> players;
	private final Map<Player, Long> timeouts;
	private static boolean enabled;
	private static int timeout;

	public RightClickListener(RepairHammerPlugin plugin, boolean used) {
		this.plugin = plugin;
		enabled = used;
		timeout = plugin.getConfig().getInt("rightclick.timeout.time", 10);
		players = new HashMap<>();
		timeouts = new HashMap<>();
	}

	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		if (!enabled)
			return;
		if (event.getAction() != RIGHT_CLICK_AIR && event.getAction() != RIGHT_CLICK_BLOCK)
			return;
		Player player = event.getPlayer();
		if (players.containsKey(player)) {
			Hammer hammer = players.get(player);
			players.remove(player);
			timeouts.remove(player);
			event.setCancelled(true);
			int slot = getSlot(player, hammer);
			if (slot == -1) {
				plugin.getConfig().getStringList("rightclick.notfound").forEach(line -> player.sendMessage(ColorUtil.translateColors(line)));
				return;
			}
			ItemStack target = player.getInventory().getItemInMainHand();
			if (!hammer.canFix(target) || !hammer.canUse(player)) {
				hammer.getCantUseMessage().forEach(s -> player.sendMessage(ColorUtil.translateColors(s)));
				return;
			}
			CustomItemHook hook = plugin.getCustomItemManager().getHook(target);
			if (hook == null) {
				return;
			}
			int damage;
			if (hammer.isPercent())
				damage = (int) Math.min(hook.getMaxDurability(target) * hammer.getFixAmount() * .01, hook.getDamage(target));
			else
				damage = Math.min(hook.getDamage(target), hammer.getFixAmount(player.getInventory().getItem(slot)));
			if (damage <= 0)
				return;
			if (!hammer.canAfford(player, false)) {
				player.sendMessage(ColorUtil.translateColors(plugin.getConfig().getString("error.bal.use").replace("<cost>", plugin.getFormat().format(hammer.getUseCost()))));
				return;
			}
			HammerUseEvent hue = new HammerUseEvent(hammer, player, player.getInventory().getHeldItemSlot());
			Bukkit.getPluginManager().callEvent(hue);
			if (hue.isCancelled())
				return;
			hammer.payCost(player, false);
			if (hammer.useDurability(player.getInventory().getItem(slot), damage) == null)
				player.getInventory().setItem(slot, null);
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
					Sound sound = Sound.valueOf(plugin.getConfig().getString("sound.sound", "BLOCK_ANVIL_USE").toUpperCase());
					player.stopSound(sound);
					player.playSound(player.getEyeLocation(), sound, 1, 1);
				} catch (IllegalArgumentException iae) {
					plugin.getLogger().warning("error unable to play sound " + this.plugin.getConfig().getString("sound.sound").toUpperCase());
				}
			}
			hammer.getUseMessage().forEach(line -> player.sendMessage(ColorUtil.translateColors(line)));
		} else {
			Optional<Hammer> ohammer = plugin.getHammerManager().getHammer(player.getInventory().getItemInMainHand());
			if (ohammer.isPresent()) {
				event.setCancelled(true);
				players.put(player, ohammer.get());
				long time = System.currentTimeMillis();
				timeouts.put(player, time);
				plugin.getConfig().getStringList("rightclick.start").forEach(line -> player.sendMessage(ColorUtil.translateColors(line)));
				new BukkitRunnable() {
					@Override
					public void run() {
						if (timeouts.containsKey(player) && timeouts.get(player).equals(time)) {
							timeouts.remove(player);
							players.remove(player);
							plugin.getConfig().getStringList("rightclick.timeout.message").forEach(line -> player.sendMessage(ColorUtil.translateColors(line)));
						}
					}
				}.runTaskLater(plugin, 20 * timeout);
			}
		}
	}

	@EventHandler
	public void onLogout(PlayerQuitEvent e) {
		players.remove(e.getPlayer());
	}

	static public void setEnabled(boolean used) {
		enabled = used;
	}

	static public void setTimeout(int time) {
		timeout = time;
	}

	private int getSlot(Player player, Hammer hammer) {
		for (int i = 0; i < player.getInventory().getSize(); i++) {
			ItemStack item = player.getInventory().getItem(i);
			if (item != null && hammer.equals(item)) {
				return i;
			}
		}
		return -1;
	}
}
