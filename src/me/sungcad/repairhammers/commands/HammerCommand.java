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
					plugin.getHammerController().getHammers().forEach(hammer -> sender.sendMessage(hammer.getList(sender)));

				} else if (sender.hasPermission("hammers.list")) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("list")));
					plugin.getHammerController().getHammers().stream().filter(hammer -> hammer.canBuy(sender)).forEach(hammer -> sender.sendMessage(hammer.getList(sender)));
				} else {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("error.np.list")));
				}
				return true;
			} else if (args[0].equalsIgnoreCase("buy")) {
				if (sender.hasPermission("hammers.buy")) {
					if ((sender instanceof Player)) {
						if (args.length == 2 || args.length == 3) {
							Optional<Hammer> ohammer = plugin.getHammerController().getHammer(args[1]);
							if (ohammer.isPresent()) {
								Hammer hammer = ohammer.get();
								Player player = (Player) sender;
								if (hammer.canBuy(player)) {
									int amount = 1;
									try {
										amount = args.length == 3 ? Math.max(Integer.parseInt(args[2]), 1) : 1;
									} catch (NumberFormatException e) {
										sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("error.nan")));
										return true;
									}
									if (plugin.getEconomy().isLoaded() && hammer.getBuyCost() > 0) {
										if (plugin.getEconomy().getEconomy().has(player, amount * hammer.getBuyCost())) {
											plugin.getEconomy().getEconomy().withdrawPlayer(player, amount * hammer.getBuyCost());
										} else {
											player.sendMessage(ChatColor.translateAlternateColorCodes('&',
													plugin.getConfig().getString("error.bal.buy").replace("<cost>", plugin.getFormat().format(amount * hammer.getBuyCost()))));
											return true;
										}
									}
									ItemStack item = hammer.getItem(amount);
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
							Optional<Hammer> ohammer = plugin.getHammerController().getHammer(args[2]);
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
									ItemStack item = hammer.getItem(amount);
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
					plugin.getHammerController().reload();
					boolean listener = plugin.getConfig().getString("use", "rightclick").equalsIgnoreCase("rightclick");
					InventoryClickListener.setEnabeld(!listener);
					RightClickListener.setEnabled(listener);
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