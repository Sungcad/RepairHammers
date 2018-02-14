package me.sungcad.repairhammers.listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import me.sungcad.repairhammers.RepairHammerPlugin;
import me.sungcad.repairhammers.hammers.Hammer;
import me.sungcad.repairhammers.hooks.AdditionApiHook;
import me.sungcad.repairhammers.hooks.RPGItemsHook;

public class RightClickListener implements Listener {
	RepairHammerPlugin plugin;
	Map<Player, Hammer> players;
	static boolean enabled;

	public RightClickListener(RepairHammerPlugin plugin, boolean used) {
		this.plugin = plugin;
		enabled = used;
		players = new HashMap<>();
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		if (!enabled)
			return;
		if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;
		Player player = event.getPlayer();
		if (players.containsKey(player)) {
			Hammer hammer = players.get(player);
			int slot = getSlot(player, hammer);
			if (slot == -1) {
				players.remove(player);
				plugin.getConfig().getStringList("rightclick.notfound").forEach(line -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', line)));
				return;
			}
			ItemStack target = player.getItemInHand();
			if (hammer.canFix(target)) {
				if (hammer.canUse(player)) {
					event.setCancelled(true);
					if (hammer.getUseCost() > 0) {
						if (plugin.getEconomy().isLoaded()) {
							if (plugin.getEconomy().getEconomy().has(player, hammer.getUseCost())) {
								plugin.getEconomy().getEconomy().withdrawPlayer(player, hammer.getUseCost());
							} else {
								player.sendMessage(
										ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("error.bal.use").replace("<cost>", plugin.getFormat().format(hammer.getUseCost()))));
								players.remove(player);
								return;
							}
						} else {
							plugin.getLogger().warning("Vault not found");
							plugin.getLogger().warning("Hammers with a cost are free");
						}
					}
					if (hammer.isConsume()) {
						int amount = player.getInventory().getItem(slot).getAmount();
						if (amount > 1)
							player.getInventory().getItem(slot).setAmount(amount - 1);
						else {
							player.getInventory().clear(slot);
						}
					}
					if (hammer.isFixall()) {
						player.getInventory().forEach(item -> {
							if (hammer.canFix(item))
								fix(item, hammer);
						});
						for (ItemStack item : player.getInventory().getArmorContents())
							if (hammer.canFix(item))
								fix(item, hammer);
					} else {
						fix(target, hammer);
					}
					hammer.getUse().forEach(s -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', s)));
				} else {
					hammer.getCantUse().forEach(s -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', s)));
				}
			} else {
				hammer.getCantUse().forEach(s -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', s)));
			}
			players.remove(player);
		} else {
			Optional<Hammer> ohammer = plugin.getHammerController().getHammer(player.getItemInHand());
			if (ohammer.isPresent()) {
				players.put(player, ohammer.get());
				plugin.getConfig().getStringList("rightclick.start").forEach(line -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', line)));
			}
		}

	}

	@EventHandler
	public void onLogout(PlayerQuitEvent e) {
		players.remove(e.getPlayer());
	}

	void removeHammer(Player player, Hammer hammer) {
		for (ItemStack item : player.getInventory()) {
			if (hammer.equals(item)) {
				if (item.getAmount() == 1)
					item.setType(Material.AIR);
				else
					item.setAmount(item.getAmount() - 1);
			}
		}
	}

	void fix(ItemStack item, Hammer hammer) {
		if (RPGItemsHook.isRPGItem(item)) {
			RPGItemsHook.repairItem(item, hammer);
		} else if (AdditionApiHook.isAdditionItem(item)) {
			AdditionApiHook.fixItem(item, hammer);
		} else {
			short targetdamage = item.getDurability();
			short targetmax = item.getType().getMaxDurability();
			if (hammer.isPercent())
				targetdamage -= (targetmax * hammer.getFixAmount() * .01);
			else
				targetdamage -= hammer.getFixAmount();
			if (targetdamage < 0)
				targetdamage = 0;
			item.setDurability(targetdamage);
		}
	}

	static public void setEnabled(boolean used) {
		enabled = used;
	}

	int getSlot(Player player, Hammer hammer) {
		for (int i = 0; i < player.getInventory().getSize(); i++) {
			ItemStack item = player.getInventory().getItem(i);
			if (item != null && hammer.equals(item)) {
				return i;
			}
		}
		return -1;
	}
}
