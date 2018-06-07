/*
 * Copyright (C) 2018  Sungcad
 */
package me.sungcad.repairhammers.itemhooks;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import think.rpgitems.api.RPGItems;
import think.rpgitems.item.RPGItem;

public class RPGItemsHook implements CustomItemHook {
	RPGItems items;
	static boolean enabled;

	public RPGItemsHook(boolean enable) {
		if (Bukkit.getPluginManager().isPluginEnabled("RPGItems")) {
			items = new RPGItems();
		}
		enabled = enable;
	}

	@Override
	public ItemStack fixItem(ItemStack item, int amount) {
		RPGItem rpgitem = items.toRPGItem(item);
		rpgitem.consumeDurability(item, -amount);
		return item;
	}

	@Override
	public int getDamage(ItemStack item) {
		RPGItem rpgitem = items.toRPGItem(item);
		return rpgitem.getMaxDurability() - rpgitem.getDurability(item);
	}

	@Override
	public boolean isDamaged(ItemStack item) {
		RPGItem rpgitem = items.toRPGItem(item);
		return Math.min(rpgitem.getMaxDurability(), rpgitem.durabilityUpperBound) > rpgitem.getDurability(item);
	}

	@Override
	public boolean isCustomItem(ItemStack item) {
		if (!(items == null)) {
			return items.toRPGItem(item) != null;
		}
		return false;
	}

	@Override
	public boolean setDamage(ItemStack item, int amount) {
		RPGItem rpgitem = items.toRPGItem(item);
		rpgitem.consumeDurability(item, rpgitem.getDurability(item) - amount);
		return false;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	public static void setEnabled(boolean enable) {
		enabled = enable;
	}

	@Override
	public int getMaxDurability(ItemStack item) {
		RPGItem rpgitem = items.toRPGItem(item);
		return rpgitem.getMaxDurability();
	}

}
