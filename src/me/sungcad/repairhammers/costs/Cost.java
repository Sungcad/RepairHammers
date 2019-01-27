/*
 * Copyright (C) 2019  Sungcad
 */
package me.sungcad.repairhammers.costs;

import org.bukkit.entity.Player;

public interface Cost {
    public boolean playerHas(Player player, double price);

    public boolean playerSpend(Player player, double price);

}
