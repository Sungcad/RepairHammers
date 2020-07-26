/*
 * Copyright (C) 2019  Sungcad
 */
package me.sungcad.repairhammers.itemhooks;

import org.bukkit.inventory.ItemStack;

public interface CustomItemHook {
    // fix the item by the amount
    ItemStack fixItem(ItemStack item, int amount);

    // get the amount of damage the item has
    int getDamage(ItemStack item);

    // get the max durability of the item
    int getMaxDurability(ItemStack item);

    // is the item a custom item
    boolean isCustomItem(ItemStack item);

    // is the item damaged
    boolean isDamaged(ItemStack item);

    // is this hook enabled
    boolean isEnabled();

    // set the durability of the item to amount
    boolean setDamage(ItemStack item, int amount);
}
