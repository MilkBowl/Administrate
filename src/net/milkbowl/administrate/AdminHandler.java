/**
 * 
 */
package net.milkbowl.administrate;

import java.util.HashMap;
import java.util.Map;

import net.milkbowl.administrate.AdminPermissions.Perms;
import net.milkbowl.administrate.runnable.UpdateInvisibilityTask;
import net.minecraft.server.Packet20NamedEntitySpawn;
import net.minecraft.server.Packet29DestroyEntity;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
/**
 * @author sleaker
 *
 */
public final class AdminHandler {
	private static Map<String, PlayerData> admins = new HashMap<String, PlayerData>();

	private static Administrate plugin;

	protected AdminHandler(Administrate plugin) {
		AdminHandler.plugin = plugin;
	}

	public static String infoString(Player player) {
		String playerName = player.getName();
		if (!admins.containsKey(playerName))
			return "That player is not an admin, or has no modes currently active.";
		else {
			String message = playerName + " has the following options: ";
			//Colorize our Settings for output
			if (AdminPermissions.has(player, Perms.STEALTH))
				message += colorize(isStealthed(playerName)) + " StealthMode ";
			if (AdminPermissions.has(player, Perms.VANISH)) {
				message += colorize(isInvisible(playerName)) + " Invisible ";
				message += colorize(isNoPickup(playerName)) + " NoPickup ";
			}
			if (AdminPermissions.has(player, Perms.GOD))
				message += colorize(isGod(playerName)) + " GodMode ";
			if (AdminPermissions.has(player, Perms.ADMINMODE))
				message += colorize(isAdminMode(playerName)) + " AdminMode ";
			return message;
		}
	}

	/**
	 * colorize a string
	 * 
	 * @param tf
	 * @return
	 */
	public static ChatColor colorize (boolean tf) {
		if (tf)
			return ChatColor.GREEN;
		else
			return ChatColor.RED;
	}

	/**
	 * Checks if a player is in the admin map
	 * 
	 * @param playerName
	 * @return boolean - true if player is a key in the admin map.
	 */
	public static boolean contains(String playerName) {
		return admins.containsKey(playerName);
	}

	/**
	 * @param playerName
	 * @return boolean - true if the player was added to the map properly. false if the player is already in the mapping.
	 */
	public static boolean add(String playerName) {
		if (contains(playerName))
			return false;
		else {
			admins.put(playerName, new PlayerData());
			return true;
		}
	}

	/**
	 * @param playerName
	 * @param origin - Location
	 * @return - true if the player location was set in the admin map
	 */
	public static boolean setOrigin(String playerName, Location origin) {
		if (!contains(playerName))
			return false;
		else {
			admins.get(playerName).setWorld(origin.getWorld().getName());
			admins.get(playerName).setXyz(new int[] {origin.getBlockX(), origin.getBlockY(), origin.getBlockZ()});
			return true;
		}
	}

	/**
	 * Sets whether the player is currently in god mode or not
	 * 
	 * @param playerName
	 * @param val - boolean t/f
	 * @return true if setting was successful
	 */
	public static boolean setGod(String playerName, boolean val) {
		if (!contains(playerName))
			return false;
		else {
			admins.get(playerName).setGod(val);
			return true;
		}
	}

	/**
	 * Sets whether the player is currently invisible or not
	 * turns the player invisible to other players
	 * 
	 * @param playerName
	 * @param val - boolean t/f
	 * @return true if setting was successful
	 */
	public static boolean setInvis(String playerName, boolean val) {
		if (!contains(playerName))
			return false;
		else {
			admins.get(playerName).setInvisible(val);
			return true;
		}
	}

	/**
	 * Sets whether the player has nopickup enabled or not
	 * 
	 * Makes it so the player can not pickup any items
	 * 
	 * @param playerName
	 * @param val - boolean t/f
	 * @return true if setting was successful
	 */
	public static boolean setNoPickup(String playerName, boolean val) {
		if (!contains(playerName))
			return false;
		else {
			admins.get(playerName).setNoPickup(val);
			return true;
		}
	}

	/**
	 * Sets whether the player is currently in stealth mode or not
	 * 
	 * Stealth mode makes the player log in and out without displaying messages
	 * 
	 * @param playerName
	 * @param val - boolean t/f
	 * @return true if setting was successful
	 */
	public static boolean setStealthed(String playerName, boolean val) {
		if (!contains(playerName))
			return false;
		else {
			admins.get(playerName).setStealthed(val);
			return true;
		}
	}

	/**
	 *  Sets whether the player is currently in admin mode or not.
	 *  
	 *  Admin mode will toggle All options (God, Stealth, Invis, NoPickup)
	 * 
	 * @param playerName
	 * @param val - boolean t/f
	 * @return true if setting was successful
	 */
	public static boolean setAdminMode(String playerName, boolean val) {
		if (!contains(playerName))
			return false;
		else {
			admins.get(playerName).setAdminMode(val);
			return true;
		}
	}
	/**
	 * Gets the playerdata object from the map
	 * 
	 * @param playerName
	 * @return PlayerData
	 */
	public static PlayerData get(String playerName) {
		if (admins.containsKey(playerName))
			return admins.get(playerName);
		else 
			return null;
	}

	/**
	 * @param playerName
	 * @return true if the player is currently in God mode
	 */
	public static boolean isGod(String playerName) {
		if (contains(playerName))
			return admins.get(playerName).isGod();
		else
			return false;
	}

	/**
	 * @param playerName
	 * @return true if the player is currently in admin mode
	 */
	public static boolean isAdminMode(String playerName) {
		if (contains(playerName))
			return admins.get(playerName).isAdminMode();
		else
			return false;
	}

	/**
	 * @param playerName
	 * @return true if the player is currently invisible
	 */
	public static boolean isInvisible(String playerName) {
		if (contains(playerName))
			return admins.get(playerName).isInvisible();
		else
			return false;
	}

	/**
	 * 
	 * @param playerName
	 * @return true if the player is currently in nopickup mode
	 */
	public static boolean isNoPickup(String playerName) {
		if (contains(playerName))
			return admins.get(playerName).isNoPickup();
		else
			return false;
	}

	/**
	 * @param playerName
	 * @return true if the player is currently in stealth mode
	 */
	public static boolean isStealthed (String playerName) {
		if (contains(playerName))
			return admins.get(playerName).isStealthed();
		else
			return false;
	}
	
	/**
	 * Takes in a playerName to check if stealthed, and the Player that is attempting to see playerName
	 * If the Player has permission to see all messages the method returns false - allowing playerName 
	 * to be seen in a list.  Use this for hooking from other plugins that don't have their own admin 
	 * permission system.
	 * 
	 * @param playerName
	 * @param player 
	 * @return true if the player is currently in stealth mode and Player does not have permission to see everyone
	 */
	public static boolean isStealthed (String playerName, Player player) {
		if (AdminPermissions.has(player, Perms.ALL_MESSAGES))
			return false;
		else if (contains(playerName)) {
			return admins.get(playerName).isStealthed();
		}
		else
			return false;
	}

	public static String getLocationString (String playerName) {
		if (contains(playerName))
			return admins.get(playerName).locationString();
		else
			return null;
	}

	/**
	 * Loads player data from a previously saved file
	 * 
	 * @param playerName
	 * @return boolean - true if the load was successful
	 */
	public static boolean loadPlayer (String playerName) {
		if (!contains(playerName))
			add(playerName);

		return admins.get(playerName).load(playerName);
	}

	/**
	 * Saves player data to file
	 * 
	 * @param playerName
	 * @return boolean - true if the save process was successful
	 */
	public static boolean savePlayer (String playerName) {
		if (!contains(playerName))
			return false;
		else
			return admins.get(playerName).save(playerName);
	}

	/**
	 * Schedules the teleport event if the player has a saved return location
	 * 
	 * @param Player - the player to teleport.
	 * @return boolean - if the task was scehduled
	 */
	protected boolean returnPlayer (Player player) {
		if (!contains(player.getName()))
			return false;
		else if (admins.get(player.getName()).getWorld() == null || admins.get(player.getName()).getXyz() == null )
			return false;
		else {
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new ReturnToOrigin(player), 10);
			return true;
		}
	}

	/**
	 * Runnable Class to return a player back to their saved origin.
	 * 
	 */
	private class ReturnToOrigin implements Runnable {

		private Player player;

		public ReturnToOrigin(Player player) {
			this.player = player;
		}

		public void run() {
			try {
				//Try to get the world + location at the players origin and teleport them there.
				int[] xyz = admins.get(player.getName()).getXyz();
				Location loc = new Location(plugin.getServer().getWorld(admins.get(player.getName()).getWorld()), xyz[0], xyz[1], xyz[2]);
				loc.getWorld().loadChunk(loc.getWorld().getChunkAt(loc));
				player.sendMessage("Returning you to: " + ChatColor.AQUA + admins.get(player.getName()).locationString());
				player.teleport(loc);
			} catch (Exception e) {
				player.sendMessage("There was an error returning you to your location");
				return;
			}   
		}
	}

	/**
	 * Initial on-command invisibility.  ONLY call this from a command as it initiates a schedule task to go invisible.
	 * 
	 */
	public void goInvisibleInitial(Player player) {
		if (player == null || !player.isOnline())
			return;

		//Run our normal go invisible method
		goInvisible(player);
		
		//Schedule us to remove another entity half a second later (Why is the Client trying to re-add the entity?)
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new UpdateInvisibilityTask(player, this), 8);
	}
	
	/**
	 * Makes a player go invisible for all online players
	 * 
	 */
	public void goInvisible(Player player) {
		if (player == null || !player.isOnline())
			return;

		for (Player pDummy : plugin.getServer().getOnlinePlayers())
			invisible(player, pDummy);
	}

	/**
	 * Makes a player become visible for all players online
	 * 
	 * @param player
	 */
	protected void goVisible(Player player) {
		if (player == null || !player.isOnline())
			return;

		for (Player pDummy : plugin.getServer().getOnlinePlayers())
			visible(player, pDummy);
	}
	
	/**
	 * Checks all players on the server and updates their visibilty
	 * 
	 */
	public void goAllInvisible() {
		for (Player pInvis : plugin.getServer().getOnlinePlayers()) {
			if (isInvisible(pInvis.getName()))
				goInvisible(pInvis);
		}
	}

	/**
	 * Handles whether to send the packet to destroy the entity or not on the client
	 * 
	 * @param pInvis
	 * @param pDummy
	 */
	protected void invisible (Player pInvis, Player pDummy) {
		//If either object is null or if we selected the same player - ignore this set.
		if (pInvis == null || pDummy == null || pInvis.equals(pDummy) )
			return;

		//If the players are too far apart or Dummy has permission to see invis - ignore this set
		if (getDistance(pInvis.getLocation(), pDummy.getLocation()) > 1000 || AdminPermissions.has(pDummy, Perms.SEE_INVIS) )
			return;

		//Send the packet to destroy the entity object
		((CraftPlayer) pDummy).getHandle().netServerHandler.sendPacket(new Packet29DestroyEntity(((CraftPlayer) pInvis).getEntityId())); 
		
	}

	/**
	 * Makes other players invisible to this player
	 * Used to prevent player from rejoining/respawning and being able to see invisible players
	 * 
	 * @param pDummy
	 */
	public void updateInvisibles(Player pDummy) {
		for (Player pInvis : plugin.getServer().getOnlinePlayers())
			if (isInvisible(pInvis.getName()))
				invisible(pInvis, pDummy);
	}

	/**
	 * Handles whether to send the packet to re-create the entity or not on the client
	 * 
	 * @param pInvis
	 * @param pDummy
	 */
	private void visible (Player pInvis, Player pDummy) {
		//If either object is null or if we selected the same player - ignore this set.
		if (pInvis == null || pDummy == null || pInvis.equals(pDummy) )
			return;

		//If we ignore permissions, shouldn't we ignore distances? Too much server load with the extra packets?

		//Remove the player before unhiding - makes sure we don't have 2 entities popping up for thos that were still able to see
		// We might be better off just exiting the method if the player has the permissions to see.
		((CraftPlayer) pDummy).getHandle().netServerHandler.sendPacket(new Packet29DestroyEntity(((CraftPlayer) pInvis).getEntityId()));
		//Send the creation packet
		((CraftPlayer) pDummy).getHandle().netServerHandler.sendPacket(new Packet20NamedEntitySpawn(((CraftPlayer) pInvis).getHandle()));
	}

	public static double getDistance (Location loc1, Location loc2) {
		double distX = Math.pow(loc1.getBlockX() - loc2.getBlockX(), 2);
		double distY = Math.pow(loc1.getBlockY() - loc2.getBlockY(), 2);

		return distX + distY; 
	}

	/**
	 * Sends spoof logout messages to players that don't have see-message permissions
	 * Also send a notification to anyone that has proper permission to see all messages
	 * 
	 * @param string playerName
	 * @param boolean in true/false
	 */
	public static void fakeLog(String playerName, boolean in) {
		for (Player dumby : plugin.getServer().getOnlinePlayers()) {
			if (dumby.getName().equals(playerName) || AdminPermissions.has(dumby, Perms.ALL_MESSAGES)) {
				if (in)
					dumby.sendMessage(ChatColor.YELLOW + playerName + " has faked a login.");
				else
					dumby.sendMessage(ChatColor.YELLOW + playerName + " has faked a logout.");
			} else {
				if (in)
					dumby.sendMessage(ChatColor.YELLOW + playerName + " joined the game.");
				else
					dumby.sendMessage(ChatColor.YELLOW + playerName + " left the game.");
			}
		}
	}
}
