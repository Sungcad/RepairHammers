/*
 * Copyright (C) 2018  Sungcad
 */
package me.sungcad.repairhammers.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import me.sungcad.repairhammers.RepairHammerPlugin;

public class HammerTabCompleter implements TabCompleter {
	private final List<String> arg1 = Arrays.asList("buy", "give", "help", "info", "list", "reload", "shop");
	private RepairHammerPlugin plugin;

	public HammerTabCompleter(RepairHammerPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> list = new ArrayList<String>();
		if (args.length == 0 || (args.length == 1 && !arg1.contains(args[0])))
			StringUtil.copyPartialMatches(args[0], arg1, list);
		else if (args.length >= 1 && args[0].equalsIgnoreCase("give")) {
			List<String> players = new ArrayList<String>();
			Bukkit.getOnlinePlayers().forEach(p -> players.add(p.getName()));
			if (args.length == 1)
				list.addAll(players);
			else if (args.length == 2 && !players.contains(args[1])) {
				StringUtil.copyPartialMatches(args[1], players, list);
			} else {
				List<String> hammers = new ArrayList<String>();
				plugin.getHammerManager().getHammers().forEach(hammer -> {
					if (hammer.canGive(sender))
						hammers.add(hammer.getName());
				});
				if (args.length == 2)
					list.addAll(players);
				else if (args.length == 3 && !hammers.contains(args[2])) {
					StringUtil.copyPartialMatches(args[2], hammers, list);
				}
			}
		} else if (args.length >= 1 && args[0].equalsIgnoreCase("buy")) {
			List<String> hammers = new ArrayList<String>();
			plugin.getHammerManager().getHammers().forEach(hammer -> {
				if (hammer.canBuy(sender))
					hammers.add(hammer.getName());
			});
			if (args.length == 1) {
				list.addAll(hammers);
			} else if (args.length == 2 && !hammers.contains(args[1])) {
				StringUtil.copyPartialMatches(args[1], hammers, list);
			}
		}
		return list;
	}
}