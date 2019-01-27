/*
 * Copyright (C) 2019  Sungcad
 */
package me.sungcad.repairhammers.costs;

import org.bukkit.entity.Player;

public class XPCost implements Cost {

    public static int getExp(Player player) {
        return getExpFromLevel(player.getLevel()) + Math.round(getExpToNext(player.getLevel()) * player.getExp());
    }

    public static int getExpFromLevel(int level) {
        if (level > 30) {
            return (int) (4.5 * level * level - 162.5 * level + 2220);
        }
        if (level > 15) {
            return (int) (2.5 * level * level - 40.5 * level + 360);
        }
        return level * level + 6 * level;
    }

    public static double getLevelFromExp(long exp) {
        if (exp > 1395) {
            return (Math.sqrt(72 * exp - 54215) + 325) / 18;
        }
        if (exp > 315) {
            return Math.sqrt(40 * exp - 7839) / 10 + 8.1;
        }
        if (exp > 0) {
            return Math.sqrt(exp + 9) - 3;
        }
        return 0;
    }

    private static int getExpToNext(int level) {
        if (level > 30) {
            return 9 * level - 158;
        }
        if (level > 15) {
            return 5 * level - 38;
        }
        return 2 * level + 7;
    }

    public static void changeExp(Player player, int exp) {
        exp += getExp(player);

        if (exp < 0) {
            exp = 0;
        }

        double levelAndExp = getLevelFromExp(exp);

        int level = (int) levelAndExp;
        player.setLevel(level);
        player.setExp((float) (levelAndExp - level));
    }

    @Override
    public boolean playerHas(Player player, double price) {
        return getExp(player) >= price;
    }

    @Override
    public boolean playerSpend(Player player, double price) {
        changeExp(player, (int) -price);
        return true;
    }

}