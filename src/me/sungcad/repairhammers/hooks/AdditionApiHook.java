package me.sungcad.repairhammers.hooks;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import com.chrismin13.additionsapi.AdditionsAPI;
import com.chrismin13.additionsapi.items.CustomItemStack;

import me.sungcad.repairhammers.hammers.Hammer;

public class AdditionApiHook {

	public static boolean isAdditionItem(ItemStack item) {
		if (Bukkit.getPluginManager().isPluginEnabled("AdditionsAPI")) {
			return AdditionsAPI.isCustomItem(item);
		}
		return false;
	}

	public static boolean needsFixed(ItemStack item) {
		CustomItemStack citem = new CustomItemStack(item);
		return citem.getFakeDurability() < citem.getMaxFakeDurability();
	}

	public static void fixItem(ItemStack item, Hammer hammer) {
		CustomItemStack citem = new CustomItemStack(item);
		int dur = citem.getFakeDurability(), max = citem.getMaxFakeDurability(), value;
		if (hammer.isPercent()) {
			value = Math.min(dur + max * hammer.getFixAmount() / 100, max);
			citem.setFakeDurability(value);
		} else {
			value = Math.min(max, dur + hammer.getFixAmount());
			citem.setFakeDurability(value);
		}
	}
}
