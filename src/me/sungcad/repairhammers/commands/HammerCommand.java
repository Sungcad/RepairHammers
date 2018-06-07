/*
 * Copyright (C) 2018  Sungcad
 */
package me.sungcad.repairhammers.commands;

import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import me.sungcad.repairhammers.Files;
import me.sungcad.repairhammers.RepairHammerPlugin;
import me.sungcad.repairhammers.hammers.Hammer;
import me.sungcad.repairhammers.itemhooks.AdditionsAPIHook;
import me.sungcad.repairhammers.itemhooks.DefaultItemHook;
import me.sungcad.repairhammers.itemhooks.HammerItemHook;
import me.sungcad.repairhammers.itemhooks.RPGItemsHook;
import me.sungcad.repairhammers.listeners.InventoryClickListener;
import me.sungcad.repairhammers.listeners.RightClickListener;

public class HammerCommand implements CommandExecutor {

	RepairHammerPlugin plugin;

	public HammerCommand(RepairHammerPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdname, String[] args) {
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("list")) {
				if (sender.hasPermission("hammers.listall")) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("list")));
					plugin.getHammerManager().getHammers().forEach(hammer -> sender.sendMessage(hammer.getListMessage(sender)));

				} else if (sender.hasPermission("hammers.list")) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("list")));
					plugin.getHammerManager().getHammers().stream().forEach(hammer -> {
						if (hammer.canBuy(sender)) {
							sender.sendMessage(hammer.getListMessage(sender));
						}
					});
				} else {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("error.np.list")));
				}
				return true;
			} else if (args[0].equalsIgnoreCase("buy")) {
				if (sender.hasPermission("hammers.buy")) {
					if ((sender instanceof Player)) {
						if (args.length == 2 || args.length == 3) {
							Optional<Hammer> ohammer = plugin.getHammerManager().getHammer(args[1]);
							if (ohammer.isPresent()) {
								Hammer hammer = ohammer.get();
								Player player = (Player) sender;
								if (hammer.canBuy(sender)) {
									int amount = 1;
									try {
										amount = args.length == 3 ? Math.max(Integer.parseInt(args[2]), 1) : 1;
									} catch (NumberFormatException e) {
										sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("error.nan")));
										return true;
									}
									double cost = hammer.getBuyCost() * amount;
									if (plugin.getEconomy().isLoaded() && cost > 0) {
										if (plugin.getEconomy().getEconomy().has(player, cost)) {
											plugin.getEconomy().getEconomy().withdrawPlayer(player, cost);
										} else {
											player.sendMessage(
													ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("error.bal.buy").replace("<cost>", plugin.getFormat().format(cost))));
											return true;
										}
									}
									ItemStack item = hammer.getHammerItem(amount);
									if (player.getInventory().firstEmpty() != -1)
										player.getInventory().addItem(item);
									else
										player.getWorld().dropItem(player.getLocation(), item);
								} else
									sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("error.np.buy")));
							} else
								sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("error.nf.hammer")));
						} else
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("error.args.buy")));
					} else
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("error.sender")));
				} else
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("error.np.buycmd")));
				return true;
			} else if (args[0].equalsIgnoreCase("give")) {
				if (sender.hasPermission("hammers.give") || sender.hasPermission("hammers.giveall")) {
					if (args.length == 3 || args.length == 4) {
						Player player = Bukkit.getPlayer(args[1]);
						if (player != null) {
							Optional<Hammer> ohammer = plugin.getHammerManager().getHammer(args[2]);
							if (ohammer.isPresent()) {
								Hammer hammer = ohammer.get();
								if (hammer.canGive(sender)) {
									int amount;
									try {
										amount = args.length == 4 ? Math.max(Integer.parseInt(args[3]), 1) : 1;
									} catch (NumberFormatException e) {
										sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("error.nan")));
										return true;
									}
									ItemStack item = hammer.getHammerItem(amount);
									if (player.getInventory().firstEmpty() != -1)
										player.getInventory().addItem(item);
									else
										player.getWorld().dropItem(player.getLocation(), item);
								} else
									sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("error.np.give")));
							} else
								sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("error.nf.hammer")));
						} else
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("error.nf.player")));
					} else
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("error.args.give")));
				} else
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("error.np.givecmd")));
				return true;
			} else if (args[0].equalsIgnoreCase("reload")) {
				if (sender.hasPermission("hammers.reload")) {
					plugin.reloadConfig();
					Files.HAMMER.reload(plugin);
					plugin.getHammerManager().reload();
					InventoryClickListener.setEnabeld(plugin.getConfig().getBoolean("use.inventory", true));
					RightClickListener.setEnabled(plugin.getConfig().getBoolean("use.rightclick", true));
					AdditionsAPIHook.setEnabled(plugin.getConfig().getBoolean("items.additionsapi", false));
					DefaultItemHook.setEnabled(plugin.getConfig().getBoolean("items.minecraft", true));
					HammerItemHook.setEnabled(plugin.getConfig().getBoolean("items.hammers", false));
					RPGItemsHook.setEnabled(plugin.getConfig().getBoolean("items.rpgitems", false));
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("reload")));
					return true;
				} else {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("error.np.reload")));
					return true;
				}
			} else if (args[0].equalsIgnoreCase("info")) {
				if (sender.hasPermission("hammers.info")) {
					sender.sendMessage("§e" + plugin.getDescription().getFullName() + " §7by §6Sungcad");
					sender.sendMessage("§7website: §6" + plugin.getDescription().getWebsite());
					return true;
				} else {
					sender.sendMessage(plugin.getConfig().getString("error.np.info").replace('&', '§'));
					return true;
				}
			} else if (args[0].equalsIgnoreCase("shop")) {
				Bukkit.dispatchCommand(sender, "hammershop");
				return true;
			}
		}
		if (sender.hasPermission("hammers.help")) {
			plugin.getConfig().getStringList("help").forEach(s -> sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s)));
		} else {
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("error.np.help")));
		}
		return true;
	}
}