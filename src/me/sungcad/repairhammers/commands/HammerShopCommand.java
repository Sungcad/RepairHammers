/*
 * Copyright (C) 2019  Sungcad
 */
package me.sungcad.repairhammers.commands;

import me.sungcad.repairhammers.utils.ColorUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.sungcad.repairhammers.RepairHammerPlugin;
import me.sungcad.repairhammers.gui.ShopHolder;

public class HammerShopCommand implements CommandExecutor {

    final RepairHammerPlugin plugin;
    final ShopHolder shopholder;

    public HammerShopCommand(RepairHammerPlugin plugin) {
        this.plugin = plugin;
        shopholder = new ShopHolder(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String cmdname, String[] args) {
        if (plugin.getConfig().getBoolean("shop.enabled")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                player.openInventory(shopholder.getInventory(player));
            } else {
                sender.sendMessage(ColorUtil.translateColors(plugin.getConfig().getString("error.sender")));
            }
        } else {
            sender.sendMessage(ColorUtil.translateColors(plugin.getConfig().getString("shop.disabledmessage")));
        }
        return true;
    }
}
