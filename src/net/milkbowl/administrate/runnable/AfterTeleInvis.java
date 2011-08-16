package net.milkbowl.administrate.runnable;

import net.milkbowl.administrate.AdminHandler;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;


public class AfterTeleInvis implements Runnable {
	private Player player;
	private Location loc;
	private boolean isInvis;
	private AdminHandler aHandler;
	private boolean update;

	public AfterTeleInvis(Player player, Location loc, boolean update, AdminHandler aHandler) {
		this.player = player;
		this.loc = loc;
		this.isInvis = AdminHandler.isInvisible(player.getName());
		this.aHandler = aHandler;
	}

	public void run() {
		//If this player is invisible lets teleport them to a temporary location before teleporting them to their specified location
		if (isInvis) {
			World world = player.getWorld();
			if (world.getPlayers().contains(player)) {
				//Set them invisible
				aHandler.goInvisibleInitial(player);
				//teleport them to the actual location
					player.teleport(loc);
					//remove their fall distance just in case
					player.setFallDistance(0);
			}   
		} 
		if (update) {
			aHandler.updateInvisibles(player);
		}

	}
}