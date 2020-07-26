/*
 * Copyright (C) 2019  Sungcad
 */
package me.sungcad.repairhammers.hammers;

import java.util.List;

import org.bukkit.Material;

public interface EditableHammer extends Hammer {

    // edit cost to buy the hammer
    void setBuyCost(double cost);

    // edit how the hammer looks in /hammers list
    void setBuyListCan(String text);

    // edit how the hammer looks in /hammers list
    void setBuyListCant(String text);

    // edit message sent to the player when they try to use a hammer they can't
    void setCantUseMessage(List<String> message);

    // edit if hammer consumed durability
    void setConsume(boolean consume);

    // edit hammer item durability
//    @Deprecated
//    public void setData(short data);

    // edit hammer item damage
    void setDamage(int data);
    
    // edit if the hammer is destroyed on use
    void setDestroy(boolean destroy);

    // edit if the hammer item is enchanted
    void setEnchanted(boolean enchanted);

    // edit if the hammer should fix all items
    void setFixAll(boolean fixall);

    // edit amount the hammer fixes
    void setFixAmount(int fixamount);

    // edit amount the hammer fixes and if it is a percentage
    void setFixAmount(String fixamount);

    // edit name of the item
    void setItemName(String name);

    // edit the lore of the hammer item
    void setLore(List<String> lore);

    // edit Material of the hammer item
    void setMaterial(Material material);

    // edit the fix amount to a percent
    void setPercent(boolean percentage);

    // edit the location of the hammer in the hammer shop
    void setShopLocation(int row, int column);

    // edit price to use the hammer
    void setUseCost(double cost);

    // edit the message sent to the player when they use the hammer
    void setUseMessage(List<String> message);

    // save edits made to the hammer
    void save();

    // reset edits made to the hammer
    void reload();
}
