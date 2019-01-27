/*
 * Copyright (C) 2019  Sungcad
 */
package me.sungcad.repairhammers.itemhooks;

import org.bukkit.inventory.ItemStack;

import me.sungcad.repairhammers.RepairHammerPlugin;
import me.sungcad.repairhammers.hammers.Hammer;

public class HammerItemHook implements CustomItemHook {
    static boolean enabled;

    public static void setEnabled(boolean enable) {
        enabled = enable;
    }

    RepairHammerPlugin plugin;

    public HammerItemHook(RepairHammerPlugin plugin, boolean enable) {
        this.plugin = plugin;
        enabled = enable;
    }

    @Override
    public ItemStack fixItem(ItemStack item, int amount) {
        Hammer hammer = plugin.getHammerManager().getHammer(item).get();
        return hammer.useDurability(item, amount);
    }

    @Override
    public int getDamage(ItemStack item) {
        Hammer hammer = plugin.getHammerManager().getHammer(item).get();
        return hammer.getFixAmount() - hammer.getFixAmount(item);
    }

    @Override
    public int getMaxDurability(ItemStack item) {
        Hammer hammer = plugin.getHammerManager().getHammer(item).get();
        return hammer.getFixAmount();
    }

    @Override
    public boolean isCustomItem(ItemStack item) {
        return plugin.getHammerManager().getHammer(item).isPresent();
    }

    @Override
    public boolean isDamaged(ItemStack item) {
        Hammer hammer = plugin.getHammerManager().getHammer(item).get();
        return hammer.isConsume() && hammer.getFixAmount(item) < hammer.getFixAmount();
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean setDamage(ItemStack item, int amount) {
        Hammer hammer = plugin.getHammerManager().getHammer(item).get();
        hammer.useDurability(item, hammer.getFixAmount() - amount);
        return true;
    }

}
