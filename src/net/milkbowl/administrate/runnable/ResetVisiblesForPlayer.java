package net.milkbowl.administrate.runnable;

import net.milkbowl.administrate.AdminHandler;

import org.bukkit.entity.Player;

public class ResetVisiblesForPlayer implements Runnable {
	private Player player;
	private AdminHandler aHandler;
	
	public ResetVisiblesForPlayer(Player player, AdminHandler aHandler) {
		this.player = player;
		this.aHandler = aHandler;
	}

	public void run() {
		if (player == null)
			return;
		else
			aHandler.updateInvisibles(player);
	}
}