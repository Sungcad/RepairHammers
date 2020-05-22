/*
 *
 *  Copyright (C) 2019  Sungcad
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
import java.util.concurrent.Callable;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import me.sungcad.repairhammers.commands.HammerCommand;
import me.sungcad.repairhammers.commands.HammerShopCommand;
import me.sungcad.repairhammers.commands.HammerTabCompleter;
import me.sungcad.repairhammers.costs.VaultHook;
import me.sungcad.repairhammers.hammers.HammerManager;
import me.sungcad.repairhammers.itemhooks.CustomItemManager;
import me.sungcad.repairhammers.listeners.AnvilListener;
import me.sungcad.repairhammers.listeners.CraftingListener;
import me.sungcad.repairhammers.listeners.InventoryClickListener;
import me.sungcad.repairhammers.listeners.LoginListener;
import me.sungcad.repairhammers.listeners.PlaceListener;
import me.sungcad.repairhammers.listeners.RightClickListener;
import me.sungcad.repairhammers.listeners.ShopListener;
import me.sungcad.repairhammers.utils.Metrics;
import me.sungcad.repairhammers.utils.UpdateChecker;

public class RepairHammerPlugin extends JavaPlugin {
	private HammerManager hammers;
	private CustomItemManager items;
	private VaultHook economy;
	private NumberFormat money;
	private UpdateChecker update;
	private static RepairHammerPlugin plugin;

	@Override
	public void onEnable() {
		plugin = this;
		saveDefaultConfig();
		Files.HAMMER.load(this);
		money = new DecimalFormat(getConfig().getString("format"));
		hammers = new HammerManager(this);
		items = new CustomItemManager(this);
		economy = new VaultHook(this);
		update = new UpdateChecker(this);
		setupCommands();
		setupListeners();
		setupMetrics();
	}

	@Override
	public void onDisable() {
		plugin = null;
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

	public UpdateChecker getUpdateChecker() {
		return update;
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
		Bukkit.getPluginManager().registerEvents(new AnvilListener(this, getConfig().getBoolean("use.anvil", false)), this);
		Bukkit.getPluginManager().registerEvents(new PlaceListener(this), this);
		Bukkit.getPluginManager().registerEvents(new LoginListener(this), this);
	}

	void setupMetrics() {
		Metrics metrics = new Metrics(this, 7430);
		metrics.addCustomChart(new Metrics.SimplePie("number_of_hammers", new Callable<String>() {
			@Override
			public String call() throws Exception {
				return String.valueOf(hammers.getHammers().size());
			}
		}));
	}

	public static RepairHammerPlugin getPlugin() {
		return plugin;
	}
}
