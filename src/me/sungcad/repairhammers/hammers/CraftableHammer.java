package me.sungcad.repairhammers.hammers;

import org.bukkit.inventory.Recipe;

public interface CraftableHammer extends Hammer{

    void removeRecipe();

    Recipe getRecipe();
}