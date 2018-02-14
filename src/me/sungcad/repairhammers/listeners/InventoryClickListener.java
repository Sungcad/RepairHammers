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
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import me.sungcad.repairhammers.RepairHammerPlugin;
import me.sungcad.repairhammers.gui.ShopHolder;
import me.sungcad.repairhammers.hammers.Hammer;
import me.sungcad.repairhammers.hooks.AdditionApiHook;
import me.sungcad.repairhammers.hooks.RPGItemsHook;

public class InventoryClickListener implements Listener {
	RepairHammerPlugin plugin;
	static boolean enable;

	public InventoryClickListener(RepairHammerPlugin plugin, boolean enabled) {
		this.plugin = plugin;
		enable = enabled;
	}

	public static void setEnabeld(boolean enabled) {
		enable = enabled;
	}

	@EventHandler
	public void onClickEvent(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		if (e.getInventory().getHolder() instanceof ShopHolder) {
			e.setCancelled(true);
			if (e.getSlot() == e.getRawSlot()) {
				ItemStack item = e.getCurrentItem();
				Optional<Hammer> ohammer = plugin.getHammerController().getHammer(item);
				if (ohammer.isPresent()) {
					Hammer hammer = ohammer.get();
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
		} else if (player.getOpenInventory().getType().equals(InventoryType.CRAFTING)) {
			if (!enable)
				return;
			ItemStack cursor = e.getCursor();
			Optional<Hammer> ohammer = plugin.getHammerController().getHammer(cursor);
			if (ohammer.isPresent()) {
				Hammer hammer = ohammer.get();
				ItemStack target = e.getCurrentItem();
				if (hammer.canFix(target)) {
					if (hammer.canUse(player)) {
						e.setCancelled(true);
						if (hammer.getUseCost() > 0) {
							if (plugin.getEconomy().isLoaded()) {
								if (plugin.getEconomy().getEconomy().has(player, hammer.getUseCost())) {
									plugin.getEconomy().getEconomy().withdrawPlayer(player, hammer.getUseCost());
								} else {
									player.sendMessage(ChatColor.translateAlternateColorCodes('&',
											plugin.getConfig().getString("error.bal.use").replace("<cost>", plugin.getFormat().format(hammer.getUseCost()))));
									return;
								}
							} else {
								plugin.getLogger().warning("Vault not found");
								plugin.getLogger().warning("Hammers with a cost are free");
							}
						}
						if (hammer.isConsume()) {
							if (cursor.getAmount() == 1)
								player.setItemOnCursor(null);
							else
								player.getItemOnCursor().setAmount(cursor.getAmount() - 1);
						}
						if (hammer.isFixall()) {
							player.getInventory().forEach(item -> {
								if (hammer.canFix(item))
									fix(item, hammer);
							});
							for (ItemStack item : player.getInventory().getArmorContents())
								if (hammer.canFix(item))
									fix(item, hammer);
						} else {
							fix(target, hammer);
						}
						hammer.getUse().forEach(s -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', s)));
					} else {
						hammer.getCantUse().forEach(s -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', s)));
					}
				}
			}
		}
	}

	void fix(ItemStack item, Hammer hammer) {
		if (RPGItemsHook.isRPGItem(item)) {
			RPGItemsHook.repairItem(item, hammer);
		} else if (AdditionApiHook.isAdditionItem(item)) {
			AdditionApiHook.fixItem(item, hammer);
		} else {
			short targetdamage = item.getDurability();
			short targetmax = item.getType().getMaxDurability();
			if (hammer.isPercent())
				targetdamage -= (targetmax * hammer.getFixAmount() * .01);
			else
				targetdamage -= hammer.getFixAmount();
			if (targetdamage < 0)
				targetdamage = 0;
			item.setDurability(targetdamage);
		}
	}

}