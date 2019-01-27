/*
 * Copyright (C) 2019  Sungcad
 */
package me.sungcad.repairhammers.costs;

import org.bukkit.entity.Player;

public class NoneCost implements Cost {

    @Override
    public boolean playerHas(Player player, double price) {
        return true;
    }

    @Override
    public boolean playerSpend(Player player, double price) {
        return true;
    }

}
