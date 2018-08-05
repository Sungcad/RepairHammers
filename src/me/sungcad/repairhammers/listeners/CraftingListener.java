package me.sungcad.repairhammers.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

import me.sungcad.repairhammers.RepairHammerPlugin;

public class CraftingListener implements Listener {
	RepairHammerPlugin plugin;

	public CraftingListener(RepairHammerPlugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void craftItem(CraftItemEvent e) {
		ItemStack[] item = e.getInventory().getMatrix();
		for (int i = 0; i < 9; i++) {
			if (plugin.getHammerManager().getHammer(item[i]).isPresent()) {
				e.setCancelled(true);
				return;
			}
		}
	}
}
