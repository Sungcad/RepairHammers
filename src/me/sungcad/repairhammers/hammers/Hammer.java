package me.sungcad.repairhammers.hammers;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface Hammer {
	
	public boolean canBuy(CommandSender sender);
	public boolean canFix(ItemStack item);
	public boolean canGive(CommandSender sender);
	public boolean canUse(Player player);
	public boolean equals(ItemStack item);
	public boolean equals(String name);
	public boolean isConsume();
	public boolean isFixall();
	public boolean isPercent();
	public double getBuyCost();
	public double getUseCost();
	public List<String> getCantUse();
	public List<String> getFixList();
	public List<String> getUse();
	public ItemStack getItem(int amount);
	public String getDisplayName();
	public String getList(CommandSender sender);
	public String getName();
	public int getFixAmount();
	public int getShopColumn();
	public int getShopRow();

}
