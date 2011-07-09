/**
 * 
 */
package net.milkbowl.administrate;

import net.milkbowl.administrate.AdminPermissions.Perms;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * @author sleaker
 *
 */
public class AdminPlayerListener extends PlayerListener {
	private static Administrate plugin;

	AdminPlayerListener(Administrate instance) {
		plugin = instance;
	}

	private AdminHandler admins = new AdminHandler(plugin);

	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		String playerName = player.getName();
		if (AdminHandler.isInvisible(playerName))
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new UpdateInvisibilityTask(player));


		//Makes it so respawning players can't see invisible players
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new ResetVisiblesForPlayer(player));
	}

	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		String playerName = player.getName();
		//Try to load the player object from file
		if (AdminPermissions.hasAny(player)) {
			if (AdminHandler.loadPlayer(playerName)) {
				String message = ChatColor.RED + "You have the following AdminToggles: ";
				//If this player object is stealthed we don't want to output that they are logging in to everyone.
				if (AdminHandler.isStealthed(playerName)) {
					event.setJoinMessage(null);
					message += ChatColor.GREEN + " StealthLog ";
					//Check each player online for permission to receive the login message
					for (Player p : plugin.getServer().getOnlinePlayers()) 
						if (AdminPermissions.has(p, Perms.ALL_MESSAGES)) 
							p.sendMessage(ChatColor.GREEN + playerName + ChatColor.WHITE + " has logged in stealthily.");
				} else if (AdminPermissions.has(player, Perms.STEALTH))
					message += ChatColor.RED + " StealthLog ";

				//Colorize our Settings for output
				if (AdminPermissions.has(player, Perms.VANISH)) {
					message += AdminHandler.colorize(AdminHandler.isInvisible(playerName)) + " Invisible ";
					message += AdminHandler.colorize(AdminHandler.isNoPickup(playerName)) + " NoPickup ";
				}
				if (AdminPermissions.has(player, Perms.GOD))
					message += AdminHandler.colorize(AdminHandler.isGod(playerName)) + " GodMode ";
				if (AdminPermissions.has(player, Perms.ADMINMODE))
					message += AdminHandler.colorize(AdminHandler.isAdminMode(playerName)) + " AdminMode ";

				//Send the player a delayed message of what options they have selected
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new DelayedMessage(player, message), 10);

				//Make the player go invisible if they have the toggle
				if (AdminHandler.isInvisible(playerName))
					plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new UpdateInvisibilityTask(player));
			} 
		}

		//Makes it so players can't rejoin the server to see invisible players
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new ResetVisiblesForPlayer(player));
	}

	public void onPlayerQuit(PlayerQuitEvent event) {
		String playerName = event.getPlayer().getName();
		//Check to see if we should try to save the player object
		if (AdminHandler.contains(playerName)) {
			//Attempt to save the player object to file
			AdminHandler.savePlayer(playerName);
			//if this player is stealthed then make sure to not output the standard quit message
			if (AdminHandler.isStealthed(playerName)) {
				event.setQuitMessage(null);
				//Check each player online for permission to receive the logout message.
				for (Player p : plugin.getServer().getOnlinePlayers())
					if (AdminPermissions.has(p, Perms.ALL_MESSAGES))
						p.sendMessage(ChatColor.GREEN + playerName + ChatColor.WHITE + " has logged out stealthily");
			}
		}
	}

	public void onPlayerTeleport (PlayerTeleportEvent event) {
		if (event.isCancelled())
			return;

		Player player = event.getPlayer();
		if (!AdminHandler.isInvisible(player.getName())) {
			//For non-invis players just update their sight 10 ticks later.
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new AfterTeleInvis(player, event.getTo(), false), 10);
			return;
		} else {
			//For Invisible players lets teleport them to a special location first if they are a long ways away or on a different world
			if (!event.getFrom().getWorld().equals(event.getTo().getWorld()) || AdminHandler.getDistance(event.getFrom(), event.getTo()) > 80)
			{
				Location toLoc = event.getTo();
				//Instead send them to the top of the world in the same chunk
				event.setTo(new Location(toLoc.getWorld(), toLoc.getX(), 127, toLoc.getZ()));

				//Make the player invulnerable for 20 ticks - just in case they teleport into walls
				player.setNoDamageTicks(40);
				//Create the actual location we want to send the player to in this teleport.
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new AfterTeleInvis(player, toLoc, true), 10);

			} else {
				//update this players view
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new AfterTeleInvis(player, event.getTo(), false), 10);
				return;
			}
		}
	}

	public void onPlayerPickupItem (PlayerPickupItemEvent event) {
		if (event.isCancelled())
			return;
		//Check if we should straight up disallow the pickup
		if (AdminHandler.isNoPickup(event.getPlayer().getName())) 
			event.setCancelled(true);
	}

	//Runnable to send a player a delayed message.
	public class DelayedMessage implements Runnable {
		private String message = null;
		private Player player;
		DelayedMessage(Player player, String message) {
			this.message = message;
			this.player = player;
		}

		public void run() {
			player.sendMessage(message);
		}
	}

	private class AfterTeleInvis implements Runnable {
		private Player player;
		private Location loc;
		private boolean isInvis;

		public AfterTeleInvis(Player player, Location loc, boolean val) {
			this.player = player;
			this.loc = loc;
			this.isInvis = val;
		}

		public void run() {
			//If this player is invisible lets teleport them to a temporary location before teleporting them to their specified location
			if (isInvis) {
				World world = player.getWorld();
				if (world.getPlayers().contains(player)) {
					//Set them invisible
					admins.goInvisible(player);
					//teleport them to the actual location
					player.teleport(loc);
					//remove their fall distance just in case
					player.setFallDistance(0);
				}   
			} else {
				admins.updateInvisibles(player);
			}

		}
	}
	private class UpdateInvisibilityTask implements Runnable {

		private Player player;

		UpdateInvisibilityTask(Player player) {
			this.player = player;
		}

		public void run() {
			if (player == null)
				admins.goAllInvisible();
			else
				admins.goInvisible(player);
		}
	}

	private class ResetVisiblesForPlayer implements Runnable {
		private Player player;

		ResetVisiblesForPlayer(Player player) {
			this.player = player;
		}

		public void run() {
			if (player == null)
				return;
			else
				admins.updateInvisibles(player);
		}
	}
}
