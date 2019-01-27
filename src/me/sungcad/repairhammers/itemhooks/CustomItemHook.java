/*
 * Copyright (C) 2019  Sungcad
 */
package me.sungcad.repairhammers.itemhooks;

import org.bukkit.inventory.ItemStack;

public interface CustomItemHook {
    // fix the item by the amount
    public ItemStack fixItem(ItemStack item, int amount);

    // get the amount of damage the item has
    public int getDamage(ItemStack item);

    // get the max durability of the item
    public int getMaxDurability(ItemStack item);

    // is the item a custom item
    public boolean isCustomItem(ItemStack item);

    // is the item damaged
    public boolean isDamaged(ItemStack item);

    // is this hook enabled
    public boolean isEnabled();

    // set the durability of the item to amount
    public boolean setDamage(ItemStack item, int amount);
}
