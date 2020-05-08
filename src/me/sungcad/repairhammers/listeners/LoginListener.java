package me.sungcad.repairhammers.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.sungcad.repairhammers.RepairHammerPlugin;
import me.sungcad.repairhammers.utils.UpdateChecker;

public class LoginListener implements Listener {
	private final static String MESSAGE = ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + "RepairHammers" + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + " Update found download at https://www.spigotmc.org/resources/44699";

	private UpdateChecker update;
	private RepairHammerPlugin plugin;

	public LoginListener(RepairHammerPlugin plugin) {
		update = plugin.getUpdateChecker();
		this.plugin = plugin;
	}

	@EventHandler
	public void onLogin(PlayerLoginEvent event) {
		Player p = event.getPlayer();
		if (p.hasPermission("hammers.update")) {
			new BukkitRunnable() {
				@Override
				public void run() {
					if (update.checkForUpdates()) {
						p.sendMessage(MESSAGE);
					}
				}
			}.runTaskAsynchronously(plugin);
		}
	}

}
