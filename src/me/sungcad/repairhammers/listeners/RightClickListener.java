/*
 * Copyright (C) 2019  Sungcad
 */
package me.sungcad.repairhammers.listeners;

import static org.bukkit.ChatColor.translateAlternateColorCodes;
import static org.bukkit.event.block.Action.RIGHT_CLICK_AIR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.sungcad.repairhammers.RepairHammerPlugin;
import me.sungcad.repairhammers.hammers.Hammer;
import me.sungcad.repairhammers.itemhooks.CustomItemHook;

public class RightClickListener implements Listener {
    RepairHammerPlugin plugin;
    Map<Player, Hammer> players;
    Map<Player, Long> timeouts;
    static boolean enabled;
    static int timeout;

    public RightClickListener(RepairHammerPlugin plugin, boolean used) {
        this.plugin = plugin;
        enabled = used;
        timeout = plugin.getConfig().getInt("rightclick.timeout.time", 10);
        players = new HashMap<>();
        timeouts = new HashMap<>();
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (!enabled)
            return;
        if (event.getAction() != RIGHT_CLICK_AIR && event.getAction() != RIGHT_CLICK_BLOCK)
            return;
        Player player = event.getPlayer();
        if (players.containsKey(player)) {
            Hammer hammer = players.get(player);
            players.remove(player);
            timeouts.remove(player);
            event.setCancelled(true);
            int slot = getSlot(player, hammer);
            if (slot == -1) {
                plugin.getConfig().getStringList("rightclick.notfound").forEach(line -> player.sendMessage(translateAlternateColorCodes('&', line)));
                return;
            }
            ItemStack target = player.getItemInHand();
            if (!hammer.canFix(target) || !hammer.canUse(player)) {
                hammer.getCantUseMessage().forEach(s -> player.sendMessage(translateAlternateColorCodes('&', s)));
                return;
            }
            CustomItemHook hook = plugin.getCustomItemManager().getHook(target);
            if (hook == null) {
                return;
            }
            int damage;
            if (hammer.isPercent())
                damage = (int) Math.min(hook.getMaxDurability(target) * hammer.getFixAmount() * .01, hook.getDamage(target));
            else
                damage = Math.min(hook.getDamage(target), hammer.getFixAmount(player.getInventory().getItem(slot)));
            if (damage <= 0)
                return;
            if (hammer.canAfford(player, false)) {
                hammer.payCost(player, false);
            } else {
                player.sendMessage(translateAlternateColorCodes('&', plugin.getConfig().getString("error.bal.use").replace("<cost>", plugin.getFormat().format(hammer.getUseCost()))));
                return;
            }
            if (hammer.useDurability(player.getInventory().getItem(slot), damage) == null)
                player.getInventory().setItem(slot, null);
            if (hammer.isFixall()) {
                for (ItemStack item : player.getInventory()) {
                    if (hammer.canFix(item)) {
                        plugin.getCustomItemManager().fixItem(item, damage);
                    }
                }
            } else {
                hook.fixItem(target, damage);
            }
            hammer.getUseMessage().forEach(line -> player.sendMessage(translateAlternateColorCodes('&', line)));
        } else {
            Optional<Hammer> ohammer = plugin.getHammerManager().getHammer(player.getItemInHand());
            if (ohammer.isPresent()) {
                event.setCancelled(true);
                players.put(player, ohammer.get());
                long time = System.currentTimeMillis();
                timeouts.put(player, time);
                plugin.getConfig().getStringList("rightclick.start").forEach(line -> player.sendMessage(translateAlternateColorCodes('&', line)));
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (timeouts.get(player).equals(time)) {
                            timeouts.remove(player);
                            players.remove(player);
                            plugin.getConfig().getStringList("rightclick.timeout.message").forEach(line -> player.sendMessage(translateAlternateColorCodes('&', line)));
                        }
                    }
                }.runTaskLater(plugin, 20 * timeout);
            }
        }
    }

    @EventHandler
    public void onLogout(PlayerQuitEvent e) {
        players.remove(e.getPlayer());
    }

    static public void setEnabled(boolean used) {
        enabled = used;
    }

    private int getSlot(Player player, Hammer hammer) {
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item != null && hammer.equals(item)) {
                return i;
            }
        }
        return -1;
    }
}
