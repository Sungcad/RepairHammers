/*
 * Copyright (C) 2018  Sungcad
 */
package me.sungcad.repairhammers.hooks;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import me.sungcad.repairhammers.hammers.Hammer;
import think.rpgitems.api.RPGItems;
import think.rpgitems.item.RPGItem;

public class RPGItemsHook {
	static RPGItems items;

	public static boolean isRPGItem(ItemStack item) {
		if (Bukkit.getPluginManager().isPluginEnabled("RPGItems")) {
			if (items == null) {
				items = new RPGItems();
			}
			return items.toRPGItem(item) != null;
		}
		return false;
	}

	public static boolean needsRepair(ItemStack item) {
		RPGItem rpgitem = items.toRPGItem(item);
		return Math.min(rpgitem.getMaxDurability(), rpgitem.durabilityUpperBound) > rpgitem.getDurability(item);
	}

	public static void repairItem(ItemStack item, Hammer hammer) {
		RPGItem rpgitem = items.toRPGItem(item);
		if (hammer.isPercent()) {
			rpgitem.consumeDurability(item, -rpgitem.getMaxDurability() * hammer.getFixAmount() / 100);
		} else {
			rpgitem.consumeDurability(item, -hammer.getFixAmount());
		}
	}
}
