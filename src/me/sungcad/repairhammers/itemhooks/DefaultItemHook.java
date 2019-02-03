/*
 * Copyright (C) 2019  Sungcad
 */
package me.sungcad.repairhammers.itemhooks;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

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
        ItemMeta meta = item.getItemMeta();
        ((Damageable) meta).setDamage(Math.max(0, ((Damageable) item.getItemMeta()).getDamage() - amount));
        item.setItemMeta((ItemMeta) meta);
        return item;
    }

    @Override
    public int getDamage(ItemStack item) {
        Damageable d = (Damageable) item.getItemMeta();
        return d.getDamage();
    }

    @Override
    public int getMaxDurability(ItemStack item) {
        return item.getType().getMaxDurability();
    }

    @Override
    public boolean isCustomItem(ItemStack item) {
        return item.getItemMeta() instanceof Damageable;
    }

    @Override
    public boolean isDamaged(ItemStack item) {
        if (item.getItemMeta() instanceof Damageable) {
            Damageable d = (Damageable) item.getItemMeta();
            return d.hasDamage();
        }
        return false;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean setDamage(ItemStack item, int amount) {
        ((Damageable) item.getItemMeta()).setDamage(amount);
        return true;
    }
}
