/*
 * Copyright (C) 2018  Sungcad
 */
package me.sungcad.repairhammers.hammers;

import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.translateAlternateColorCodes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.sungcad.repairhammers.Files;
import me.sungcad.repairhammers.RepairHammerPlugin;

public class DefaultHammer implements EditableHammer {
    private final String name;
    private boolean consume, destroy, enchanted, fixall, percent;
    private String displayname, listcan, listcant;
    private List<String> cantuse, fixlist, lore, use;
    private short data;
    private int fixamount, shoprow, shopcolumn;
    private double buycost, usecost;
    private Material type;
    private RepairHammerPlugin plugin;

    protected DefaultHammer(String name, RepairHammerPlugin plugin) {
        this.name = name;
        this.plugin = plugin;
        setData((byte) 0);
        setEnchanted(true);
        setFixAll(false);
        setPercent(true);
        setConsume(true);
        setDestroy(true);
        setFixAmount(50);
        setUseCost(0);
        setBuyCost(0);
        setShopLocation(-1, -1);
        setFixlist(new ArrayList<String>());
        setMaterial(Material.IRON_INGOT);
        setItemName("Hammer");
        setBuyListCan("&2Hammer &7fix one item by 50%");
        setBuyListCant("&4Hammer &7fix one item by 50%");
        setCantUseMessage(new ArrayList<String>());
        setLore(new ArrayList<String>());
        setUseMessage(new ArrayList<String>());
    }

    protected DefaultHammer(String name, ConfigurationSection config, RepairHammerPlugin plugin) {
        this.name = name;
        this.plugin = plugin;
        setData((byte) config.getInt("data", 0));
        setPercent(config.getString("amount", "50%").endsWith("%"));
        setFixAmount(Integer.valueOf(config.getString("amount", "50%").replace("%", "")));
        setConsume(config.getBoolean("consume", true));
        setDestroy(config.getBoolean("destroy", true));
        setEnchanted(config.getBoolean("enchanted", true));
        setFixAll(config.getBoolean("fixall", false));
        setUseCost(config.getDouble("cost.use", 0.0));
        setBuyCost(config.getDouble("cost.buy", 0.0));
        setShopLocation(config.getInt("shop.row", 0), config.getInt("shop.column", 0));
        setFixlist(plugin.getConfig().getStringList("fixlist." + config.getString("fixlist", "all")));
        setMaterial(Material.valueOf(config.getString("type", "IRON_INGOT")));
        setItemName(config.getString("name", "Hammer"));
        setBuyListCan(config.getString("listgive.can", "&2" + name + " &7fix one item by " + fixamount + (percent ? "%" : "")));
        setBuyListCant(config.getString("listgive.cant", "&4" + name + " &7fix one item by " + fixamount + (percent ? "%" : "")));
        setCantUseMessage(config.getStringList("cantuse"));
        setLore(config.getStringList("lore"));
        setUseMessage(config.getStringList("use"));
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
            newlore.add(GRAY.toString() + fixamount + "/" + fixamount);
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
        return (sender instanceof Player ? (canBuy(sender) ? listcan : listcant) : listcan);
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

    @Override
    public void setBuyCost(double buycost) {
        this.buycost = buycost;
    }

    @Override
    public void setCantUseMessage(List<String> cantuse) {
        this.cantuse = new ArrayList<String>(cantuse.size());
        cantuse.forEach(line -> this.cantuse.add(translateAlternateColorCodes('&', line)));
    }

    @Override
    public void setConsume(boolean consume) {
        if (percent)
            this.consume = false;
        else
            this.consume = consume;
    }

    @Override
    public void setData(short data) {
        this.data = data;
    }

    @Override
    public void setItemName(String displayname) {
        this.displayname = translateAlternateColorCodes('&', displayname);
    }

    @Override
    public void setEnchanted(boolean enchanted) {
        this.enchanted = enchanted;
    }

    @Override
    public void setFixAll(boolean fixall) {
        this.fixall = fixall;
    }

    @Override
    public void setFixAmount(int fixamount) {
        this.fixamount = fixamount;
    }

    @Override
    public void setFixAmount(String fixamount) {
        percent = fixamount.endsWith("%");
        setFixAmount(Integer.parseInt(fixamount.replace("%", "")));
    }

    public void setFixlist(List<String> fixlist) {
        this.fixlist = fixlist;
    }

    @Override
    public void setBuyListCan(String listcan) {
        this.listcan = translateAlternateColorCodes('&', listcan);
    }

    @Override
    public void setBuyListCant(String listcant) {
        this.listcant = translateAlternateColorCodes('&', listcant);
    }

    @Override
    public void setLore(List<String> lore) {
        this.lore = new ArrayList<String>(lore.size());
        lore.forEach(line -> this.lore.add(translateAlternateColorCodes('&', line)));
    }

    @Override
    public void setPercent(boolean percent) {
        this.percent = percent;
    }

    @Override
    public void setShopLocation(int row, int column) {
        shopcolumn = column;
        shoprow = row;
    }

    @Override
    public void setMaterial(Material type) {
        this.type = type;
    }

    @Override
    public void setUseMessage(List<String> use) {
        this.use = new ArrayList<String>(use.size());
        use.forEach(line -> this.use.add(translateAlternateColorCodes('&', line)));
    }

    @Override
    public void setUseCost(double usecost) {
        this.usecost = usecost;
    }

    @Override
    public boolean isDestroy() {
        return destroy;
    }

    @Override
    public void setDestroy(boolean destroy) {
        this.destroy = destroy;
    }

    @Override
    public ItemStack useDurability(ItemStack hammer, int amount) {
        if (consume) {
            ItemMeta meta = hammer.getItemMeta();
            List<String> lore = meta.getLore();
            String line = lore.get(lore.size() - 1);
            if (line.matches(GRAY + "+\\d+\\/\\d+")) {
                line = line.replaceAll(GRAY + "|\\/(\\d+)", "");
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
                line = GRAY.toString() + left + "/" + fixamount;
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
        if (line.matches(GRAY+"+\\d+\\/\\d+")) {
            line = line.replaceAll(GRAY+"|\\/(\\d+)", "");
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {

            }
        }
        return fixamount;
    }

    @Override
    public void save() {
        ConfigurationSection config = Files.HAMMER.getConfig().getConfigurationSection(name);
        config.set("data", data);
        config.set("type", type);
        config.set("consume", consume);
        config.set("destroy", destroy);
        config.set("enchanted", enchanted);
        config.set("cost.use", usecost);
        config.set("cost.buy", buycost);
        config.set("name", name);
        config.set("shop.row", shoprow);
        config.set("shop.column", shopcolumn);
        config.set("fixall", fixall);
        config.set("amount", fixamount + (percent ? "%" : ""));
        config.set("listgive.can", listcan);
        config.set("listgive,cant", listcant);
        config.set("cantuse", cantuse);
        config.set("lore", lore);
        config.set("use", use);
        Files.HAMMER.save(plugin);
    }

    @Override
    public void reload() {
        ConfigurationSection config = Files.HAMMER.getConfig().getConfigurationSection(name);
        setData((byte) config.getInt("data", 0));
        setPercent(config.getString("amount", "50%").endsWith("%"));
        setFixAmount(Integer.valueOf(config.getString("amount", "50%").replace("%", "")));
        setConsume(config.getBoolean("consume", true));
        setDestroy(config.getBoolean("destroy", true));
        setEnchanted(config.getBoolean("enchanted", true));
        setFixAll(config.getBoolean("fixall", false));
        setUseCost(config.getDouble("cost.use", 0.0));
        setBuyCost(config.getDouble("cost.buy", 0.0));
        setShopLocation(config.getInt("shop.row", 0), config.getInt("shop.column", 0));
        setFixlist(plugin.getConfig().getStringList("fixlist." + config.getString("fixlist", "all")));
        setMaterial(Material.valueOf(config.getString("type", "IRON_INGOT")));
        setItemName(config.getString("name", "Hammer"));
        setBuyListCan(config.getString("listgive.can", "&2" + name + " &7fix one item by " + fixamount + (percent ? "%" : "")));
        setBuyListCant(config.getString("listgive.cant", "&4" + name + " &7fix one item by " + fixamount + (percent ? "%" : "")));
        setCantUseMessage(config.getStringList("cantuse"));
        setLore(config.getStringList("lore"));
        setUseMessage(config.getStringList("use"));
    }
}