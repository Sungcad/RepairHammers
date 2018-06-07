/*
 * Copyright (C) 2018  Sungcad
 */
package me.sungcad.repairhammers.hammers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.sungcad.repairhammers.RepairHammerPlugin;

public class DefaultHammer implements Hammer {
	private final String name;
	private boolean consume, destroy, enchanted, fixall, percent;
	private String displayname, listbuycan, listbuycant;
	private List<String> cantuse, fixlist, lore, use;
	private short data;
	private int fixamount, shoprow, shopcolumn;
	private double buycost, usecost;
	private Material type;

	protected DefaultHammer(String name) {
		this.name = name;
		setData((byte) 0);
		setEnchanted(true);
		setFixall(false);
		setPercent(true);
		setConsume(true);
		setDestroy(true);
		setFixamount(50);
		setUseCost(0);
		setBuyCost(0);
		setShopRow(0);
		setShopColumn(0);
		setFixlist(new ArrayList<String>());
		setType(Material.IRON_INGOT);
		setDisplayname("Hammer");
		setListBuyCan("&2Hammer &7fix one item by 50%");
		setListBuyCant("&4Hammer &7fix one item by 50%");
		setCantuse(new ArrayList<String>());
		setLore(new ArrayList<String>());
		setUse(new ArrayList<String>());
	}

	protected DefaultHammer(String name, ConfigurationSection config, RepairHammerPlugin plugin) {
		this.name = name;
		setData((byte) config.getInt("data", 0));
		setPercent(config.getString("amount", "50%").endsWith("%"));
		setFixamount(Integer.valueOf(config.getString("amount", "50%").replace("%", "")));
		setConsume(config.getBoolean("consume", true));
		setDestroy(config.getBoolean("destroy", true));
		setEnchanted(config.getBoolean("enchanted", true));
		setFixall(config.getBoolean("fixall", false));
		setUseCost(config.getDouble("cost.use", 0.0));
		setBuyCost(config.getDouble("cost.buy", 0.0));
		setShopRow(config.getInt("shop.row", 0));
		setShopColumn(config.getInt("shop.column", 0));
		setFixlist(plugin.getConfig().getStringList("fixlist." + config.getString("fixlist", "all")));
		setType(Material.valueOf(config.getString("type", "IRON_INGOT")));
		setDisplayname(config.getString("name", "Hammer"));
		setListBuyCan(config.getString("listgive.can", "&2" + name + " &7fix one item by " + fixamount + (percent ? "%" : "")));
		setListBuyCant(config.getString("listgive.cant", "&4" + name + " &7fix one item by " + fixamount + (percent ? "%" : "")));
		setCantuse(config.getStringList("cantuse"));
		setLore(config.getStringList("lore"));
		setUse(config.getStringList("use"));
	}

	@Override
	public boolean canBuy(CommandSender sender) {
		return sender.hasPermission("hammers.buy." + name) || sender.hasPermission("hammers.buyall");
	}

	@Override
	public boolean canFix(ItemStack item) {
		if (item != null) {
			return fixlist.stream().filter(str -> str.equals(item.getType().toString())).findAny().isPresent();
		}
		return false;
	}

	@Override
	public boolean canGive(CommandSender sender) {
		return (sender.hasPermission("hammers.give." + name) && sender.hasPermission("hammers.give")) || sender.hasPermission("hammers.giveall");
	}

	@Override
	public boolean canUse(Player player) {
		return player.hasPermission("hammers.use." + name) || player.hasPermission("hammers.useall");
	}

	@Override
	public boolean equals(ItemStack item) {
		if (item != null) {
			if (type.equals(item.getType())) {
				if (item.getDurability() == data) {
					if (displayname.equals(item.getItemMeta().getDisplayName())) {
						if (item.getItemMeta().getLore().containsAll(lore)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean equals(String name) {
		return this.name.equals(name);
	}

	@Override
	public double getBuyCost() {
		return buycost;
	}

	@Override
	public List<String> getCantUseMessage() {
		return cantuse;
	}

	@Override
	public String getDisplayName() {
		return displayname;
	}

	@Override
	public int getFixAmount() {
		return fixamount;
	}

	@Override
	public ItemStack getHammerItem(int number) {
		ItemStack item = new ItemStack(type, number, data);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(displayname);
		if (consume) {
			List<String> newlore = new ArrayList<String>(lore);
			newlore.add("§7" + fixamount + "/" + fixamount);
			meta.setLore(newlore);
		} else
			meta.setLore(lore);
		if (enchanted) {
			meta.addEnchant(Enchantment.DURABILITY, 1, true);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		}
		item.setItemMeta(meta);
		return item;
	}

	@Override
	public String getListMessage(CommandSender sender) {
		return (sender instanceof Player ? (canBuy(sender) ? listbuycan : listbuycant) : listbuycan);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getShopColumn() {
		return shopcolumn;
	}

	@Override
	public int getShopRow() {
		return shoprow;
	}

	@Override
	public List<String> getUseMessage() {
		return use;
	}

	@Override
	public double getUseCost() {
		return usecost;
	}

	@Override
	public boolean isConsume() {
		return consume;
	}

	@Override
	public boolean isFixall() {
		return fixall;
	}

	@Override
	public boolean isPercent() {
		return percent;
	}

	public void setBuyCost(double buycost) {
		this.buycost = buycost;
	}

	public void setCantuse(List<String> cantuse) {
		this.cantuse = new ArrayList<String>(cantuse.size());
		cantuse.forEach(line -> this.cantuse.add(ChatColor.translateAlternateColorCodes('&', line)));
	}

	public void setConsume(boolean consume) {
		if (percent)
			this.consume = false;
		else
			this.consume = consume;
	}

	public void setData(short data) {
		this.data = data;
	}

	public void setDisplayname(String displayname) {
		this.displayname = ChatColor.translateAlternateColorCodes('&', displayname);
	}

	public void setEnchanted(boolean enchanted) {
		this.enchanted = enchanted;
	}

	public void setFixall(boolean fixall) {
		this.fixall = fixall;
	}

	public void setFixamount(int fixamount) {
		this.fixamount = fixamount;
	}

	public void setFixlist(List<String> fixlist) {
		this.fixlist = fixlist;
	}

	public void setListBuyCan(String listcan) {
		this.listbuycan = ChatColor.translateAlternateColorCodes('&', listcan);
	}

	public void setListBuyCant(String listcant) {
		this.listbuycant = ChatColor.translateAlternateColorCodes('&', listcant);
	}

	public void setLore(List<String> lore) {
		this.lore = new ArrayList<String>(lore.size());
		lore.forEach(line -> this.lore.add(ChatColor.translateAlternateColorCodes('&', line)));
	}

	public void setPercent(boolean percent) {
		this.percent = percent;
	}

	public void setShopColumn(int shopcolumn) {
		this.shopcolumn = shopcolumn;
	}

	public void setShopRow(int shoprow) {
		this.shoprow = shoprow;
	}

	public void setType(Material type) {
		this.type = type;
	}

	public void setUse(List<String> use) {
		this.use = new ArrayList<String>(use.size());
		use.forEach(line -> this.use.add(ChatColor.translateAlternateColorCodes('&', line)));
	}

	public void setUseCost(double usecost) {
		this.usecost = usecost;
	}

	@Override
	public boolean isDestroy() {
		return destroy;
	}

	public void setDestroy(boolean destroy) {
		this.destroy = destroy;
	}

	@Override
	public ItemStack useDurability(ItemStack hammer, int amount) {
		if (consume) {
			ItemMeta meta = hammer.getItemMeta();
			List<String> lore = meta.getLore();
			String line = lore.get(lore.size() - 1);
			if (line.matches("§7+\\d+\\/\\d+")) {
				line = line.replaceAll("§7|\\/(\\d+)", "");
				int left = Integer.parseInt(line);
				left = Math.min(left - amount, fixamount);
				if (left <= 0) {
					if (destroy) {
						if (hammer.getAmount() > 1) {
							hammer.setAmount(hammer.getAmount() - 1);
							left = fixamount;
						} else {
							hammer = null;
							return hammer;
						}
					} else {
						left = 0;
					}
				}
				line = "§7" + left + "/" + fixamount;
				lore.set(lore.size() - 1, line);
				meta.setLore(lore);
				hammer.setItemMeta(meta);
			}
		} else if (destroy) {
			if (hammer.getAmount() > 1) {
				hammer.setAmount(hammer.getAmount() - 1);
			} else {
				hammer = null;
			}
		}
		return hammer;
	}

	@Override
	public int getFixAmount(ItemStack hammer) {
		ItemMeta meta = hammer.getItemMeta();
		List<String> lore = meta.getLore();
		String line = lore.get(lore.size() - 1);
		if (line.matches("§7+\\d+\\/\\d+")) {
			line = line.replaceAll("§7|\\/(\\d+)", "");
			try {
				return Integer.parseInt(line);
			} catch (NumberFormatException e) {

			}
		}
		return fixamount;
	}
}