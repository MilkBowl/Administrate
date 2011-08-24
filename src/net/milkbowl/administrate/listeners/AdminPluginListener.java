package net.milkbowl.administrate.listeners;

import net.milkbowl.administrate.Administrate;

import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.getspout.spout.PluginListener;

public class AdminPluginListener extends PluginListener {

	private Administrate plugin;

	public AdminPluginListener(Administrate plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void onPluginDisable(PluginDisableEvent event) {
		if (event.getPlugin().getDescription().getName().equals("Spout")) {
			Administrate.useSpout = false;
			plugin.getPacketManager().disable();
		}
	}

	@Override
	public void onPluginEnable(PluginEnableEvent event) {
		if (event.getPlugin().getDescription().getName().equals("Spout")) {
			Administrate.useSpout = true;
			plugin.getPacketManager().enable();
		}
	}

}
