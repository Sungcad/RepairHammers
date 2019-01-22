/*
 * Copyright (C) 2018  Sungcad
 */
package me.sungcad.repairhammers.itemhooks;

import org.bukkit.inventory.ItemStack;

public class DefaultItemHook implements CustomItemHook {

    static boolean enabled;

    public static void setEnabled(boolean enable) {
        enabled = enable;
    }

    public DefaultItemHook(boolean enable) {
        enabled = enable;
    }

    @Override
    public ItemStack fixItem(ItemStack item, int amount) {
        item.setDurability((short) Math.max(0, item.getDurability() - amount));
        return item;
    }

    @Override
    public int getDamage(ItemStack item) {
        return item.getDurability();
    }

    @Override
    public int getMaxDurability(ItemStack item) {
        return item.getType().getMaxDurability();
    }

    @Override
    public boolean isCustomItem(ItemStack item) {
        return true;
    }

    @Override
    public boolean isDamaged(ItemStack item) {
        return item.getDurability() > 0;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean setDamage(ItemStack item, int amount) {
        item.setDurability((short) amount);
        return true;
    }
}
