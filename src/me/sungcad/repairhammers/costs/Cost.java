/*
 * Copyright (C) 2019  Sungcad
 */
package me.sungcad.repairhammers.costs;

import org.bukkit.entity.Player;

public interface Cost {
    boolean playerHas(Player player, double price);

    boolean playerSpend(Player player, double price);

}
