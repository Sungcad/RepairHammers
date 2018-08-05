package me.sungcad.repairhammers.hammers;

import java.util.List;

import org.bukkit.Material;

public interface EditableHammer extends Hammer {
	
	//edit cost to buy the hammer 
	public void setBuyCost(double cost);
	
	//edit how the hammer looks in /hammers list
	public void setBuyListCan(String text);
	
	//edit how the hammer looks in /hammers list
	public void setBuyListCant(String text);
	
	//edit message sent to the player when they try to use a hammer they can't
	public void setCantUseMessage(List<String> message);
	
	//edit if hammer consumed durability
	public void setConsume(boolean consume);
	
	//edit hammer item durability
	public void setData(short data);
	
	//edit if the hammer is destroyed on use
	public void setDestroy(boolean destroy);
	
	//edit if the hammer item is enchanted
	public void setEnchanted(boolean enchanted);
	
	//edit if the hammer should fix all items
	public void setFixAll(boolean fixall);
	
	//edit amount the hammer fixes
	public void setFixAmount(int fixamount);
	
	//edit amount the hammer fixes and if it is a percentage
	public void setFixAmount(String fixamount);
	
	//edit name of the item
	public void setItemName(String name);
	
	//edit the lore of the hammer item
	public void setLore(List<String> lore);

	//edit Material of the hammer item
	public void setMaterial(Material material);
	
	//edit the fix amount to a percent 
	public void setPercent(boolean percentage);
	
	//edit the location of the hammer in the hammer shop
	public void setShopLocation(int row, int column);
	
	//edit price to use the hammer
	public void setUseCost(double cost);
	
	//edit the message sent to the player when they use the hammer
	public void setUseMessage(List<String> message);
	
	//save edits made to the hammer
	public void save();
	
	//reset edits made to the hammer
	public void reload();
}
