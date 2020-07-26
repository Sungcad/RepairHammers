/*
 * Copyright (C) 2019  Sungcad
 */
package me.sungcad.repairhammers.hammers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import me.sungcad.repairhammers.Files;
import me.sungcad.repairhammers.RepairHammerPlugin;

public class HammerManager {
	private final List<Hammer> hammers;
	private final RepairHammerPlugin plugin;
	final NamespacedKey key;

	public HammerManager(RepairHammerPlugin plugin) {
		this.plugin = plugin;
		key = new NamespacedKey(plugin, "hammer");
		hammers = new ArrayList<>();
		Files.HAMMER.getConfig().getKeys(false).forEach(cnf -> hammers.add(new DefaultHammer(cnf, Files.HAMMER.getConfig().getConfigurationSection(cnf), plugin, key)));
		plugin.getLogger().info(hammers.size() + " hammers have been loaded.");
	}

	public Optional<Hammer> getHammer(String name) {
		return hammers.stream().filter(hammer -> hammer.equals(name)).findFirst();
	}

	public Optional<Hammer> getHammer(ItemStack item) {
		return hammers.stream().filter(hammer -> hammer.equals(item)).findFirst();
	}

	public List<Hammer> getHammers() {
		return new ArrayList<>(hammers);
	}

	public void addHammer(Hammer hammer) {
		hammers.add(hammer);
	}

	public boolean removeHammer(Hammer hammer) {
		return hammers.remove(hammer);
	}

	public void reload() {
		List<Hammer> newhammers = new ArrayList<>(hammers);
		for (Hammer hammer : newhammers) {
			if (hammer instanceof DefaultHammer) {
				hammers.remove(hammer);
				((DefaultHammer) hammer).removeRecipe();
			}
		}
		Files.HAMMER.getConfig().getKeys(false).forEach(cnf -> hammers.add(new DefaultHammer(cnf, Files.HAMMER.getConfig().getConfigurationSection(cnf), plugin, key)));
		plugin.getLogger().info(hammers.size() + " hammers have been loaded.");
	}

	public void unload(){
		List<Hammer> newhammers = new ArrayList<>(hammers);
		for (Hammer hammer : newhammers) {
			hammers.remove(hammer);
			if(hammer instanceof CraftableHammer){
				((CraftableHammer)hammer).removeRecipe();
			}
		}
	}
}