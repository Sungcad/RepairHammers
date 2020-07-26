package me.sungcad.repairhammers.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import me.sungcad.repairhammers.RepairHammerPlugin;

public class UpdateChecker {

	private URL url;
	private String latest;
	private final RepairHammerPlugin plugin;
	private boolean error = false;

	public UpdateChecker(RepairHammerPlugin plugin) {
		this.plugin = plugin;
		latest = plugin.getDescription().getVersion();
		try {
			url = new URL("https://api.spigotmc.org/legacy/update.php?resource=44699");
		} catch (MalformedURLException e) {
			error = true;
		}
	}

	public boolean checkForUpdates() {
		if (!plugin.getConfig().getBoolean("update", true) || error)
			return false;
		try {
			URLConnection con = url.openConnection();
			latest = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
			return !plugin.getDescription().getVersion().equals(latest);
		} catch (IOException ioe) {
			plugin.getLogger().warning("error checking for update");
			return plugin.getDescription().getVersion().equals(latest);
		}
	}

	public String getLatest() {
		return latest;
	}

}
