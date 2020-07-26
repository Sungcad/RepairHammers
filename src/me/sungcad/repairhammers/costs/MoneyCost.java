/*
 * Copyright (C) 2019  Sungcad
 */
package me.sungcad.repairhammers.costs;

import org.bukkit.entity.Player;

import me.sungcad.repairhammers.RepairHammerPlugin;

public class MoneyCost implements Cost {

    @Override
    public boolean playerHas(Player player, double price) {
        if(price == 0)
            return true;
        else if (!RepairHammerPlugin.getPlugin().getEconomy().isLoaded())
            return false;
        return RepairHammerPlugin.getPlugin().getEconomy().getEconomy().has(player, price);
    }

    @Override
    public boolean playerSpend(Player player, double price) {
        return price == 0 || RepairHammerPlugin.getPlugin().getEconomy().getEconomy().withdrawPlayer(player, price).transactionSuccess();
    }
}
