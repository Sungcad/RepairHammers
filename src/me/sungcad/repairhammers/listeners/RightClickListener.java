/*
 * Copyright (C) 2018  Sungcad
 */
package me.sungcad.repairhammers.listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import me.sungcad.repairhammers.RepairHammerPlugin;
import me.sungcad.repairhammers.hammers.Hammer;
import me.sungcad.repairhammers.itemhooks.CustomItemHook;

public class RightClickListener implements Listener {
	RepairHammerPlugin plugin;
	Map<Player, Hammer> players;
	static boolean enabled;

	public RightClickListener(RepairHammerPlugin plugin, boolean used) {
		this.plugin = plugin;
		enabled = used;
		players = new HashMap<>();
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		if (!enabled)
			return;
		if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;
		Player player = event.getPlayer();
		if (players.containsKey(player)) {
			Hammer hammer = players.get(player);
			players.remove(player);
			event.setCancelled(true);
			int slot = getSlot(player, hammer);
			if (slot == -1) {
				plugin.getConfig().getStringList("rightclick.notfound").forEach(line -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', line)));
				return;
			}
			ItemStack target = player.getItemInHand();
			if (!hammer.canFix(target) || !hammer.canUse(player)) {
				hammer.getCantUseMessage().forEach(s -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', s)));
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
			if (hammer.getUseCost() > 0) {
				if (plugin.getEconomy().isLoaded()) {
					if (plugin.getEconomy().getEconomy().has(player, hammer.getUseCost())) {
						plugin.getEconomy().getEconomy().withdrawPlayer(player, hammer.getUseCost());
					} else {
						player.sendMessage(
								ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("error.bal.use").replace("<cost>", plugin.getFormat().format(hammer.getUseCost()))));
						return;
					}
				} else {
					plugin.getLogger().warning("Vault not found");
					plugin.getLogger().warning("Hammers with a cost are free");
				}
			}
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
			hammer.getUseMessage().forEach(line -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', line)));
		} else {
			Optional<Hammer> ohammer = plugin.getHammerManager().getHammer(player.getItemInHand());
			if (ohammer.isPresent()) {
				event.setCancelled(true);
				players.put(player, ohammer.get());
				plugin.getConfig().getStringList("rightclick.start").forEach(line -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', line)));
			}
		}
	}

	@EventHandler
	public void onLogout(PlayerQuitEvent e) {
		players.remove(e.getPlayer());
	}

	void removeHammer(Player player, Hammer hammer) {
		for (ItemStack item : player.getInventory()) {
			if (hammer.equals(item)) {
				if (item.getAmount() == 1)
					item.setType(Material.AIR);
				else
					item.setAmount(item.getAmount() - 1);
			}
		}
	}

	static public void setEnabled(boolean used) {
		enabled = used;
	}

	int getSlot(Player player, Hammer hammer) {
		for (int i = 0; i < player.getInventory().getSize(); i++) {
			ItemStack item = player.getInventory().getItem(i);
			if (item != null && hammer.equals(item)) {
				return i;
			}
		}
		return -1;
	}
}
