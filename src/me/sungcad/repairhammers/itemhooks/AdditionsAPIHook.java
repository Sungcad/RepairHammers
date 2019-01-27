/*
 * Copyright (C) 2019  Sungcad
 */
package me.sungcad.repairhammers.itemhooks;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import com.chrismin13.additionsapi.AdditionsAPI;
import com.chrismin13.additionsapi.items.CustomItemStack;

public class AdditionsAPIHook implements CustomItemHook {
    static boolean enabled;

    public static void setEnabled(boolean enable) {
        enabled = enable;
    }

    public AdditionsAPIHook(boolean enable) {
        enabled = enable;
    }

    @Override
    public ItemStack fixItem(ItemStack item, int amount) {
        CustomItemStack citem = new CustomItemStack(item);
        int dur = citem.getFakeDurability();
        int max = citem.getMaxFakeDurability();
        int value = Math.min(max, dur + amount);
        citem.setFakeDurability(value);
        return citem.getItemStack();
    }

    @Override
    public int getDamage(ItemStack item) {
        CustomItemStack citem = new CustomItemStack(item);
        return citem.getMaxFakeDurability() - citem.getFakeDurability();
    }

    @Override
    public int getMaxDurability(ItemStack item) {
        CustomItemStack citem = new CustomItemStack(item);
        return citem.getMaxFakeDurability();
    }

    @Override
    public boolean isCustomItem(ItemStack item) {
        if (Bukkit.getPluginManager().isPluginEnabled("AdditionsAPI")) {
            return AdditionsAPI.isCustomItem(item);
        }
        return false;
    }

    @Override
    public boolean isDamaged(ItemStack item) {
        CustomItemStack citem = new CustomItemStack(item);
        return citem.getFakeDurability() < citem.getMaxFakeDurability();
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean setDamage(ItemStack item, int amount) {
        CustomItemStack citem = new CustomItemStack(item);
        citem.setFakeDurability(amount);
        return false;
    }
}
