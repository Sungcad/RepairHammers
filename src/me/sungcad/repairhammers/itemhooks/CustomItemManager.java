/*
 * Copyright (C) 2018  Sungcad
 */
package me.sungcad.repairhammers.itemhooks;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.inventory.ItemStack;

import me.sungcad.repairhammers.RepairHammerPlugin;

public class CustomItemManager {
    List<CustomItemHook> hooks;
    RepairHammerPlugin plugin;

    public CustomItemManager(RepairHammerPlugin plugin) {
        this.plugin = plugin;
        hooks = new LinkedList<CustomItemHook>();
        addItemHook(new DefaultItemHook(plugin.getConfig().getBoolean("items.minecraft", true)));
        addItemHook(new HammerItemHook(plugin, plugin.getConfig().getBoolean("items.rpgitems", false)));
        addItemHook(new AdditionsAPIHook(plugin.getConfig().getBoolean("items.additionsapi", false)));
        addItemHook(new RPGItemsHook(plugin.getConfig().getBoolean("items.rpgitems", false)));
    }

    public void addItemHook(CustomItemHook hook) {
        hooks.add(0, hook);
    }

    public ItemStack fixItem(ItemStack item, int amount) {
        for (CustomItemHook hook : hooks) {
            if (hook.isEnabled() && hook.isCustomItem(item) && hook.isDamaged(item)) {
                return hook.fixItem(item, amount);
            }
        }
        return null;
    }

    public List<CustomItemHook> getAllHooks() {
        return hooks;
    }

    public CustomItemHook getHook(ItemStack item) {
        for (CustomItemHook hook : hooks) {
            if (hook.isEnabled() && hook.isCustomItem(item) && hook.isDamaged(item)) {
                return hook;
            }
        }
        return null;
    }

    public boolean removeHook(CustomItemHook hook) {
        return hooks.remove(hook);
    }
}