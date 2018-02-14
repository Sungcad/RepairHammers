/*
 * Copyright (C) 2018  Sungcad
 */
package me.sungcad.repairhammers.hammers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bukkit.inventory.ItemStack;

import me.sungcad.repairhammers.RepairHammerPlugin;
import me.sungcad.repairhammers.Files;
public class HammerController {
	List<Hammer> hammers;
	RepairHammerPlugin plugin;

	public HammerController(RepairHammerPlugin plugin) {
		this.plugin = plugin;
		hammers = new ArrayList<Hammer>();
		Files.HAMMER.getConfig().getKeys(false).forEach(cnf -> hammers.add(new DefaultHammer(cnf, Files.HAMMER.getConfig().getConfigurationSection(cnf), plugin)));
		plugin.getLogger().info(hammers.size() + " hammers have been loaded.");
	}

	public Optional<Hammer> getHammer(String name) {
		return hammers.stream().filter(hammer -> hammer.equals(name)).findFirst();
	}

	public Optional<Hammer> getHammer(ItemStack item) {
		return hammers.stream().filter(hammer -> hammer.equals(item)).findFirst();
	}

	public List<Hammer> getHammers() {
		return new ArrayList<Hammer>(hammers);
	}

	public void addHammer(Hammer hammer) {
		hammers.add(hammer);
	}

	public void reload() {
		for (Hammer hammer : hammers) {
			if (hammer instanceof DefaultHammer) {
				hammers.remove(hammer);
			}
		}
		Files.HAMMER.getConfig().getKeys(false).stream().forEach(cnf -> hammers.add(new DefaultHammer(cnf, Files.HAMMER.getConfig().getConfigurationSection(cnf), plugin)));
		plugin.getLogger().info(hammers.size() + " hammers have been loaded.");
	}
}