/*
 * Copyright (C) 2019  Sungcad
 */
package me.sungcad.repairhammers.commands;

import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.YELLOW;
import static org.bukkit.ChatColor.translateAlternateColorCodes;

import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.chrismin13.additionsapi.items.CustomItemStack;

import me.sungcad.repairhammers.Files;
import me.sungcad.repairhammers.RepairHammerPlugin;
import me.sungcad.repairhammers.hammers.Hammer;
import me.sungcad.repairhammers.itemhooks.AdditionsAPIHook;
import me.sungcad.repairhammers.itemhooks.CustomItemHook;
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
                    sender.sendMessage(translateAlternateColorCodes('&', plugin.getConfig().getString("list")));
                    plugin.getHammerManager().getHammers().forEach(hammer -> sender.sendMessage(hammer.getListMessage(sender)));

                } else if (sender.hasPermission("hammers.list")) {
                    sender.sendMessage(translateAlternateColorCodes('&', plugin.getConfig().getString("list")));
                    plugin.getHammerManager().getHammers().stream().forEach(hammer -> {
                        if (hammer.canBuy(sender)) {
                            sender.sendMessage(hammer.getListMessage(sender));
                        }
                    });
                } else {
                    sender.sendMessage(translateAlternateColorCodes('&', plugin.getConfig().getString("error.np.list")));
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
                                        sender.sendMessage(translateAlternateColorCodes('&', plugin.getConfig().getString("error.nan")));
                                        return true;
                                    }
                                    if (!hammer.canAfford(player, true)) {
                                        player.sendMessage(translateAlternateColorCodes('&', plugin.getConfig().getString("error.bal.buy").replace("<cost>", plugin.getFormat().format(hammer.getBuyCost() * amount))));
                                        return true;
                                    }
                                    hammer.payCost(player, true);
                                    ItemStack item = hammer.getHammerItem(amount);
                                    if (player.getInventory().firstEmpty() != -1)
                                        player.getInventory().addItem(item);
                                    else
                                        player.getWorld().dropItem(player.getLocation(), item);
                                } else
                                    sender.sendMessage(translateAlternateColorCodes('&', plugin.getConfig().getString("error.np.buy")));
                            } else
                                sender.sendMessage(translateAlternateColorCodes('&', plugin.getConfig().getString("error.nf.hammer")));
                        } else
                            sender.sendMessage(translateAlternateColorCodes('&', plugin.getConfig().getString("error.args.buy")));
                    } else
                        sender.sendMessage(translateAlternateColorCodes('&', plugin.getConfig().getString("error.sender")));
                } else
                    sender.sendMessage(translateAlternateColorCodes('&', plugin.getConfig().getString("error.np.buycmd")));
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
                                        sender.sendMessage(translateAlternateColorCodes('&', plugin.getConfig().getString("error.nan")));
                                        return true;
                                    }
                                    ItemStack item = hammer.getHammerItem(amount);
                                    if (player.getInventory().firstEmpty() != -1)
                                        player.getInventory().addItem(item);
                                    else
                                        player.getWorld().dropItem(player.getLocation(), item);
                                } else
                                    sender.sendMessage(translateAlternateColorCodes('&', plugin.getConfig().getString("error.np.give")));
                            } else
                                sender.sendMessage(translateAlternateColorCodes('&', plugin.getConfig().getString("error.nf.hammer")));
                        } else
                            sender.sendMessage(translateAlternateColorCodes('&', plugin.getConfig().getString("error.nf.player")));
                    } else
                        sender.sendMessage(translateAlternateColorCodes('&', plugin.getConfig().getString("error.args.give")));
                } else
                    sender.sendMessage(translateAlternateColorCodes('&', plugin.getConfig().getString("error.np.givecmd")));
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
                    sender.sendMessage(translateAlternateColorCodes('&', plugin.getConfig().getString("reload")));
                    return true;
                } else {
                    sender.sendMessage(translateAlternateColorCodes('&', plugin.getConfig().getString("error.np.reload")));
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("info")) {
                if (sender.hasPermission("hammers.info")) {
                    sender.sendMessage(YELLOW + plugin.getDescription().getFullName() + GRAY + " by " + GOLD + "Sungcad");
                    sender.sendMessage(GRAY + "website: " + GOLD + plugin.getDescription().getWebsite());
                    sender.sendMessage(GRAY + "hammers: " + GOLD + plugin.getHammerManager().getHammers().size());
                    return true;
                } else {
                    sender.sendMessage(translateAlternateColorCodes('&', plugin.getConfig().getString("error.np.info")));
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("shop")) {
                Bukkit.dispatchCommand(sender, "hammershop");
                return true;
            } else if (args[0].equalsIgnoreCase("debug")) {
                if (sender.hasPermission("hammers.debug")) {
                    if (sender instanceof Player && args.length >= 2 && args[1].equalsIgnoreCase("item")) {
                        ItemStack item = ((Player) sender).getInventory().getItemInMainHand();
                        CustomItemHook h = plugin.getCustomItemManager().getHook(item);
                        boolean aapi = h instanceof AdditionsAPIHook, rpgi = h instanceof RPGItemsHook, rhi = h instanceof HammerItemHook;
                        sender.sendMessage(GRAY + "Item type: " + GREEN + (aapi ? "AdditionsAPI item" : rpgi ? "RPGItems item" : rhi ? "RepairHammer item" : "vanilla item"));
                        sender.sendMessage(GRAY + "name for fixlist: " + GOLD + (aapi ? new CustomItemStack(item).getCustomItem().getIdName() : item.getType()));
                    }
                } else
                    sender.sendMessage(translateAlternateColorCodes('&', plugin.getConfig().getString("error.np.debug")));
                return true;
            }
        }
        if (sender.hasPermission("hammers.help")) {
            plugin.getConfig().getStringList("help").forEach(s -> sender.sendMessage(translateAlternateColorCodes('&', s)));
        } else {
            sender.sendMessage(translateAlternateColorCodes('&', plugin.getConfig().getString("error.np.help")));
        }
        return true;
    }
}