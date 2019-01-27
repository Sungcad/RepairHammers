/*
 * Copyright (C) 2019  Sungcad
 */
package me.sungcad.repairhammers.costs;

import org.bukkit.entity.Player;

public class LevelCost implements Cost {

    @Override
    public boolean playerHas(Player player, double price) {
        return player.getLevel() >= price;
    }

    @Override
    public boolean playerSpend(Player player, double price) {
        player.setLevel(player.getLevel() - (int) price);
        return true;
    }

}
