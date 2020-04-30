/*
 * Copyright (C) 2019  Sungcad
 */
package me.sungcad.repairhammers.costs;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import me.sungcad.repairhammers.RepairHammerPlugin;
import net.milkbowl.vault.economy.Economy;

public class VaultHook {
	private Economy econ;

	public VaultHook(RepairHammerPlugin plugin) {
		if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
			RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
			if (economyProvider != null) {
				econ = economyProvider.getProvider();
				return;
			}
		}
		plugin.getLogger().warning("Vault not found all hammers with a costs are free");
	}

	public Economy getEconomy() {
		return econ;
	}

	public boolean isLoaded() {
		return econ != null;
	}
}