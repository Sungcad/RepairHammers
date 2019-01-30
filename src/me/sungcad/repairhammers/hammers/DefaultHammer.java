/*
 * Copyright (C) 2019  Sungcad
 */
package me.sungcad.repairhammers.hammers;

import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.translateAlternateColorCodes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import com.chrismin13.additionsapi.items.CustomItemStack;

import me.sungcad.repairhammers.Files;
import me.sungcad.repairhammers.RepairHammerPlugin;
import me.sungcad.repairhammers.costs.Cost;
import me.sungcad.repairhammers.costs.LevelCost;
import me.sungcad.repairhammers.costs.MoneyCost;
import me.sungcad.repairhammers.costs.NoneCost;
import me.sungcad.repairhammers.costs.XPCost;
import me.sungcad.repairhammers.itemhooks.AdditionsAPIHook;

public class DefaultHammer implements EditableHammer {
    private final String name;
    private boolean consume, destroy, enchanted, fixall, percent;
    private String displayname, listcan, listcant;
    private List<String> cantuse, fixlist, lore, use;
    private int fixamount, shoprow, shopcolumn;
    private double buycost, usecost;
    private Material type;
    private Cost buycosttype, usecosttype;
    private RepairHammerPlugin plugin;

    protected DefaultHammer(String name, ConfigurationSection config, RepairHammerPlugin plugin) {
        this.name = name;
        this.plugin = plugin;
        setPercent(config.getString("amount", "50%").endsWith("%"));
        setFixAmount(Integer.valueOf(config.getString("amount", "50%").replace("%", "")));
        setConsume(config.getBoolean("consume", true));
        setDestroy(config.getBoolean("destroy", true));
        setEnchanted(config.getBoolean("enchanted", true));
        setFixAll(config.getBoolean("fixall", false));
        setUseCost(config.getDouble("cost.use", 0.0), config.getString("cost.type.use", "m"));
        setBuyCost(config.getDouble("cost.buy", 0.0), config.getString("cost.type.buy", "m"));
        setShopLocation(config.getInt("shop.row", 0), config.getInt("shop.column", 0));
        setFixlist(plugin.getConfig().getStringList("fixlist." + config.getString("fixlist", "all")));
        setMaterial(Material.valueOf(config.getString("type", "IRON_INGOT")));
        setItemName(config.getString("name", "Hammer"));
        setBuyListCan(config.getString("listgive.can", "&2" + name + " &7fix one item by " + fixamount + (percent ? "%" : "")));
        setBuyListCant(config.getString("listgive.cant", "&4" + name + " &7fix one item by " + fixamount + (percent ? "%" : "")));
        setCantUseMessage(config.getStringList("cantuse"));
        setLore(config.getStringList("lore"));
        setUseMessage(config.getStringList("use"));
        setupCrafting(config);
    }

    private void setupCrafting(ConfigurationSection config) {
        if (config.getBoolean("crafting.craftable", false)) {
            ItemStack hammer = getHammerItem(1);
            NamespacedKey key = new NamespacedKey(plugin, name);
            ShapedRecipe recipe = new ShapedRecipe(key, hammer);
            recipe.shape(config.getString("crafting.shape.1"), config.getString("crafting.shape.2"), config.getString("crafting.shape.3"));
            for (String s : config.getConfigurationSection("crafting.material").getKeys(false)) {
                recipe.setIngredient(s.charAt(0), Material.valueOf(config.getString("crafting.material." + s, "AIR")));
            }
            Bukkit.addRecipe(recipe);
        }
    }

    protected DefaultHammer(String name, RepairHammerPlugin plugin) {
        this.name = name;
        this.plugin = plugin;
        setEnchanted(true);
        setFixAll(false);
        setPercent(true);
        setConsume(true);
        setDestroy(true);
        setFixAmount(50);
        setUseCost(0, "n");
        setBuyCost(0, "n");
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

    @Override
    public boolean canAfford(Player player, boolean buy) {
        return buy ? buycosttype.playerHas(player, buycost) : usecosttype.playerHas(player, usecost);
    }

    @Override
    public boolean canBuy(CommandSender sender) {
        return sender.hasPermission("hammers.buy." + name) || sender.hasPermission("hammers.buyall");
    }

    @Override
    public boolean canFix(ItemStack item) {
        if (item != null) {
            if (plugin.getCustomItemManager().getHook(item) instanceof AdditionsAPIHook) {
                return fixlist.stream().filter(str -> str.equals(new CustomItemStack(item).getCustomItem().getIdName()) || str.equals(item.getType().toString())).findAny().isPresent();
            }
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
                if (displayname.equals(item.getItemMeta().getDisplayName())) {
                    if (item.getItemMeta().getLore().containsAll(lore)) {
                        return true;
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
    public int getFixAmount(ItemStack hammer) {
        ItemMeta meta = hammer.getItemMeta();
        List<String> lore = meta.getLore();
        String line = lore.get(lore.size() - 1);
        if (line.matches(GRAY + "+\\d+\\/\\d+")) {
            line = line.replaceAll(GRAY + "|\\/(\\d+)", "");
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {

            }
        }
        return fixamount;
    }

    @Override
    public ItemStack getHammerItem(int number) {
        ItemStack item = new ItemStack(type, number);
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
    public double getUseCost() {
        return usecost;
    }

    @Override
    public List<String> getUseMessage() {
        return use;
    }

    @Override
    public boolean isConsume() {
        return consume;
    }

    @Override
    public boolean isDestroy() {
        return destroy;
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
    public boolean payCost(Player player, boolean buy) {
        return buy ? buycosttype.playerSpend(player, buycost) : usecosttype.playerSpend(player, usecost);
    }

    @Override
    public void reload() {
        ConfigurationSection config = Files.HAMMER.getConfig().getConfigurationSection(name);
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
    public void save() {
        ConfigurationSection config = Files.HAMMER.getConfig().getConfigurationSection(name);
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
    public void setBuyCost(double buycost) {
        this.buycost = buycost;
    }

    public void setBuyCost(double buycost, String type) {
        this.buycost = buycost;
        switch (type.toLowerCase()) {
        case "money":
        case "$":
        case "m":
            buycosttype = new MoneyCost();
            break;
        case "levels":
        case "level":
        case "lvl":
        case "lv":
        case "l":
            buycosttype = new LevelCost();
            break;
        case "experience":
        case "exp":
        case "xp":
        case "x":
            buycosttype = new XPCost();
            break;
        default:
            buycosttype = new NoneCost();
        }

    }

    public void setBuyCostType(Cost cost) {
        buycosttype = cost;
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
    public void setDestroy(boolean destroy) {
        this.destroy = destroy;
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
    public void setItemName(String displayname) {
        this.displayname = translateAlternateColorCodes('&', displayname);
    }

    @Override
    public void setLore(List<String> lore) {
        this.lore = new ArrayList<String>(lore.size());
        lore.forEach(line -> this.lore.add(translateAlternateColorCodes('&', line)));
    }

    @Override
    public void setMaterial(Material type) {
        this.type = type;
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
    public void setUseCost(double usecost) {
        this.usecost = usecost;
    }

    public void setUseCost(double usecost, String type) {
        this.usecost = usecost;
        switch (type.toLowerCase()) {
        case "money":
        case "$":
        case "m":
            usecosttype = new MoneyCost();
            break;
        case "levels":
        case "level":
        case "lvl":
        case "lv":
        case "l":
            usecosttype = new LevelCost();
            break;
        case "experience":
        case "exp":
        case "xp":
        case "x":
            usecosttype = new XPCost();
            break;
        default:
            usecosttype = new NoneCost();
        }
    }

    public void setUseCostType(Cost cost) {
        usecosttype = cost;
    }

    @Override
    public void setUseMessage(List<String> use) {
        this.use = new ArrayList<String>(use.size());
        use.forEach(line -> this.use.add(translateAlternateColorCodes('&', line)));
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
    public void setData(short data) {
    }
}