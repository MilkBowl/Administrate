package net.milkbowl.administrate.runnable;

import net.milkbowl.administrate.AdminHandler;

import org.bukkit.entity.Player;

public class UpdateInvisibilityTask implements Runnable {
	private Player player;
	AdminHandler aHandler;
	public UpdateInvisibilityTask(AdminHandler aHandler) {
		this(null, aHandler);
	}

	public UpdateInvisibilityTask(Player player, AdminHandler aHandler) {
		this.player = player;
		this.aHandler = aHandler;
	}

	public void run() {
		if (player == null)
			aHandler.goAllInvisible();
		else
			aHandler.goInvisible(player);
	}
}