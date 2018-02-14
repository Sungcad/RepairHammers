/*
 * Copyright (C) 2018  Sungcad
 */
package me.sungcad.repairhammers;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public enum Files {
	HAMMER("hammers.yml");

	String name;
	File file;
	YamlConfiguration config;

	Files(String name) {
		this.name = name;
	}

	public YamlConfiguration getConfig() {
		return config;
	}

	public boolean load(RepairHammerPlugin plugin) {
		file = new File(plugin.getDataFolder(), name);
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			plugin.saveResource(name, false);
		}
		config = new YamlConfiguration();
		try {
			config.load(file);
			plugin.getLogger().info("File " + name + " loaded");
		} catch (IOException | InvalidConfigurationException e) {
			plugin.getLogger().severe("Error loading file " + name);
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean reload(RepairHammerPlugin plugin) {
		file = new File(plugin.getDataFolder(), name);
		config = new YamlConfiguration();
		try {
			config.load(file);
			plugin.getLogger().info("File " + name + " loaded");
		} catch (IOException | InvalidConfigurationException e) {
			plugin.getLogger().severe("Error loading file " + name);
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void save(RepairHammerPlugin plugin) {
		try {
			config.save(file);
		} catch (IOException e) {
			plugin.getLogger().severe("Error file " + name + " could not be saved");
			e.printStackTrace();
		}
	}
}
