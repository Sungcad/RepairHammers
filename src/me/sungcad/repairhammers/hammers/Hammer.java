/*
 * Copyright (C) 2019  Sungcad
 */
package me.sungcad.repairhammers.hammers;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface Hammer {

    // check if the command sender has permission to buy the hammer
    public boolean canBuy(CommandSender sender);

    // check if the hammer can fix the item in the item stack
    public boolean canFix(ItemStack item);

    // check if the command sender has permission to give the hammer to a player
    public boolean canGive(CommandSender sender);

    // check if the player has permission to use the hammer
    public boolean canUse(Player player);

    // check if an item stack contains the hammer
    public boolean equals(ItemStack item);

    // check if a string contains the hammers name
    public boolean equals(String name);

    // get the price to buy the hammer
    public double getBuyCost();

    // get the text sent to a player when they can't use the hammer
    public List<String> getCantUseMessage();

    // get the name of the hammer item
    public String getDisplayName();

    // get the amount the hammer fixes
    public int getFixAmount();

    // get the amount the hammer in the item stack can fix
    public int getFixAmount(ItemStack item);

    // get the hammer as an item stack
    public ItemStack getHammerItem(int amount);

    // use durability from the hammer item
    public ItemStack useDurability(ItemStack hammer, int amount);

    // get message sent to command sender for /rh list
    public String getListMessage(CommandSender sender);

    // get the name of the hammer
    public String getName();

    // get hammer location in shop gui
    public int getShopColumn();

    // get hammer location in shop gui
    public int getShopRow();

    // get the cost to use the hammer
    public double getUseCost();

    // get the text sent to a player when they use the hammer
    public List<String> getUseMessage();

    // check if the hammer is consumes durability on use
    public boolean isConsume();

    // check if the hammer is destroyed on use
    public boolean isDestroy();

    // check if the hammer fixes all items in the player inventory or just one
    public boolean isFixall();

    // check if the hammers fix amount is a percentage
    public boolean isPercent();
    
    // check if the player is able to pay for the hammer
    // set buy to false for use cost
    public boolean canAfford(Player player, boolean buy);
 
    // make the player pay for the hammer
    // set buy to false for use cost
    public boolean payCost(Player player, boolean buy);
    
}
