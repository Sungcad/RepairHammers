/*
 * 
 *  Copyright (C) 2018  Sungcad
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package me.sungcad.repairhammers;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import me.sungcad.repairhammers.commands.HammerCommand;
import me.sungcad.repairhammers.commands.HammerShopCommand;
import me.sungcad.repairhammers.commands.HammerTabCompleter;
import me.sungcad.repairhammers.hammers.HammerManager;
import me.sungcad.repairhammers.itemhooks.CustomItemManager;
import me.sungcad.repairhammers.listeners.CraftingListener;
import me.sungcad.repairhammers.listeners.InventoryClickListener;
import me.sungcad.repairhammers.listeners.PlaceListener;
import me.sungcad.repairhammers.listeners.RightClickListener;
import me.sungcad.repairhammers.listeners.ShopListener;

public class RepairHammerPlugin extends JavaPlugin {
	HammerManager hammers;
	CustomItemManager items;
	VaultHook economy;
	NumberFormat money;

	@Override
	public void onEnable() {
		saveDefaultConfig();
		Files.HAMMER.load(this);
		money = new DecimalFormat(getConfig().getString("format"));
		hammers = new HammerManager(this);
		items = new CustomItemManager(this);
		economy = new VaultHook(this);
		setupCommands();
		setupListeners();
	}

	@Override
	public void onDisable() {

	}

	public HammerManager getHammerManager() {
		return hammers;
	}

	public CustomItemManager getCustomItemManager() {
		return items;
	}

	public VaultHook getEconomy() {
		return economy;
	}

	public NumberFormat getFormat() {
		return money;
	}

	public void reloadFormat() {
		money = new DecimalFormat(getConfig().getString("format"));
	}

	void setupCommands() {
		getCommand("hammer").setExecutor(new HammerCommand(this));
		getCommand("hammer").setTabCompleter(new HammerTabCompleter(this));
		getCommand("hammershop").setExecutor(new HammerShopCommand(this));
	}

	void setupListeners() {
		Bukkit.getPluginManager().registerEvents(new ShopListener(this), this);
		Bukkit.getPluginManager().registerEvents(new CraftingListener(this), this);
		Bukkit.getPluginManager().registerEvents(new InventoryClickListener(this, getConfig().getBoolean("use.inventory", true)), this);
		Bukkit.getPluginManager().registerEvents(new RightClickListener(this, getConfig().getBoolean("use.rightclick", false)), this);
		Bukkit.getPluginManager().registerEvents(new PlaceListener(this), this);
	}
}