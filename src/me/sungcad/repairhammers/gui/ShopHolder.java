/*
 * Copyright (C) 2019  Sungcad
 */
package me.sungcad.repairhammers.gui;

import java.util.ArrayList;
import java.util.List;

import me.sungcad.repairhammers.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.sungcad.repairhammers.RepairHammerPlugin;
import me.sungcad.repairhammers.hammers.Hammer;

public class ShopHolder implements InventoryHolder {
    final RepairHammerPlugin plugin;

    public ShopHolder(RepairHammerPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }

    public Inventory getInventory(Player player) {
        List<Hammer> hammers = new ArrayList<>();
        plugin.getHammerManager().getHammers().stream().filter(hammer -> hammer.canBuy(player)).forEach(hammers::add);
        Inventory inv;
        if (plugin.getConfig().getBoolean("shop.default", true)) {
            int size = ((hammers.size() / 9) * 9) + (hammers.size() % 9 > 0 ? 9 : 0);
            inv = Bukkit.createInventory(this, size, ColorUtil.translateColors(plugin.getConfig().getString("shop.name")));
            for (Hammer hammer : hammers) {
                inv.addItem(getItem(hammer));
            }
        } else {
            inv = Bukkit.createInventory(this, plugin.getConfig().getInt("shop.size", 6) * 9, ColorUtil.translateColors(plugin.getConfig().getString("shop.name")));
            for (Hammer hammer : hammers) {
                int location = hammer.getShopColumn() + (hammer.getShopRow() * 9);
                if (location < inv.getSize()) {
                    inv.setItem(location, getItem(hammer));
                } else {
                    plugin.getLogger().warning(ColorUtil.translateColors(plugin.getConfig().getString("error.shop.oob").replace("{hammer}", hammer.getName())));
                }
            }
        }
        return inv;
    }

    ItemStack getItem(Hammer hammer) {
        ItemStack item = hammer.getHammerItem(1);
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        for (String s : plugin.getConfig().getStringList("shop.addedlore")) {
            lore.add(ColorUtil.translateColors(s.replace("<cost>", plugin.getFormat().format(hammer.getBuyCost()))));
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
}