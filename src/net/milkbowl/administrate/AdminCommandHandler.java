/**
 * 
 */
package net.milkbowl.administrate;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import net.milkbowl.administrate.AdminPermissions.Perms;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 * @author sleaker
 *
 */
public class AdminCommandHandler implements CommandExecutor {
	protected static final Logger log = Logger.getLogger("Minecraft");

	private static Administrate plugin;
	private AdminHandler admins;

	AdminCommandHandler(Administrate plugin) {
		AdminCommandHandler.plugin = plugin;
		admins = plugin.getAdminHandler();
	}


	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		//Ignore the console and spit out the not implemented message
		if (sender instanceof ConsoleCommandSender) {
			sender.sendMessage("Console is not implemented for this command");
		} else if (sender instanceof Player) {
			//Otherwise lets try and do some stuff!
			Player player = (Player) sender;
			if (command.getName().equals("stealth")) {
				stealth(player);
				return true;
			} else if (command.getName().equals("god")) {
				godMode(player);
				return true;
			} else if (command.getName().equals("vanish")) {
				vanish(player);
				return true;
			} else if (command.getName().equals("admin")) {
				adminMode(player);
				return true;
			} else if (command.getName().equals("saveloc")) {
				saveLoc(player);
				return true;
			} else if (command.getName().equals("return")) {
				returnLoc(player);
				return true;
			} else if (command.getName().equals("adminstatus")) {
				adminStatus(player, args);
				return true;
			} else if (command.getName().equals("fakeout")) {
				fakeLogout(player);
				return true;
			} else if (command.getName().equals("fakein")) {
				fakeLogin(player);
				return true;
			} else if (command.getName().equals("admin_bring")) {
				bring(player, args);
				return true;
			} else if (command.getName().equals("admin_put")) {
				put(player, args);
				return true;
			} else if (command.getName().equals("admin_tp")) {
				tp(player, args);
				return true;
			} else if (command.getName().equals("admin_heal")) {
				heal(player, args);
				return true;
			}
		}
		return false;
	}

	private void heal(Player player, String[] args) {
		if (!AdminPermissions.has(player, Perms.ADMINHEAL) && !AdminPermissions.has(player, Perms.ANYHEAL)) {
			player.sendMessage("You don't have permission to use that command.");
			return;
		} else if (!AdminHandler.isAdminMode(player.getName()) && !AdminPermissions.has(player, Perms.ANYHEAL)) {
			player.sendMessage("You must be in admin mode to heal players.");
			return;
		} else if (args.length == 0) {
			heal(player, player, true);
			return;
		}
		if (args[0].equalsIgnoreCase("all")) {
			for ( Player target : plugin.getServer().getOnlinePlayers() )
				heal(player, target, false);
			
			player.sendMessage("You have healed all online players.");
			return;
		}
		List<String> players = new ArrayList<String>();
		for (Player target : plugin.getServer().getOnlinePlayers()) {
			if (target.getName().contains(args[0])) {
				heal(player, target, false);
				players.add(target.getName());
			}
		}

		if (players.isEmpty())
			player.sendMessage("Could not find any players named " + args[0] + ".");
		else {
			String message = "You have healed:";
			for (String pName : players)
				message += "  " + pName;
			
			player.sendMessage(message);
		}
	}

	private void heal (Player healer, Player target, boolean report) {
		target.setHealth(20);
		target.sendMessage("You have been healed.");
		if (healer != target && report)
			healer.sendMessage("You have healed " + target.getName());
	}

	private void bring(Player player, String[] args) {
		if (!AdminPermissions.has(player, Perms.ADMINTP) && !AdminPermissions.has(player, Perms.ANYTP)) {
			player.sendMessage("You don't have permission to use that command.");
			return;
		} else if (!AdminHandler.isAdminMode(player.getName()) && !AdminPermissions.has(player, Perms.ANYTP)) {
			player.sendMessage("You must be in admin mode to teleport.");
			return;
		} else if (args.length < 1) {
			player.sendMessage("You must specify a player to bring to your location.");
			return;
		} else {
			if (args[0].equalsIgnoreCase("all")) {
				teleportAll(player, player.getLocation());
				player.sendMessage("You have teleported everyone to your location.");
				return;
			} else {
				boolean match = false;
				for (Player p : plugin.getServer().getOnlinePlayers()) {
					if (p.getName().contains(args[0])) {
						match = true;
						p.teleport(player);
						p.sendMessage(player.getName() + " has brought you to their location.");
						player.sendMessage("You have brought " + p.getName() + " to your locations.");
					}
				}
				if (!match)
					player.sendMessage("Could not find any players named " + args[0] + ".");

				return;
			}
		}
	}

	private void put(Player player, String[] args) {
		Block block = player.getTargetBlock(null, 50).getRelative(BlockFace.UP);
		if (!AdminPermissions.has(player, Perms.ADMINTP) && !AdminPermissions.has(player, Perms.ANYTP)) {
			player.sendMessage("You don't have permission to use that command");
			return;
		} else if (!AdminHandler.isAdminMode(player.getName()) && !AdminPermissions.has(player, Perms.ANYTP)) {
			player.sendMessage("You must be in admin mode to teleport");
			return;
		} else if (args.length < 1) {
			player.sendMessage("You must specify a player to bring to your location");
			return;
		} else if (block == null){
			player.sendMessage("Could not determine a block to teleport players to, aborting teleport.");
		} else if (args[0].equalsIgnoreCase("all")) {
			teleportAll(player, block.getLocation());
			player.sendMessage("You have teleported everyone to the target.");
		} else {
			List<String> players = new ArrayList<String>();
			for (Player p : plugin.getServer().getOnlinePlayers()) {
				if (p.getName().toLowerCase().contains(args[0].toLowerCase())) {
					p.teleport(block.getLocation());
					p.sendMessage(player.getName() + " has teleported you.");
					players.add(p.getName());
				}
			}

			if (players.isEmpty())
				player.sendMessage("Could not find any players named " + args[0]);
			else {
				String message = "You have brought to your location:";
				for (String pName : players) 
					message += "  " + pName;
				
				player.sendMessage(message);
			}
			return;
		}
	}

	private void tp(Player player, String[] args) {
		if (!AdminPermissions.has(player, Perms.ADMINTP) && !AdminPermissions.has(player, Perms.ANYTP)) {
			player.sendMessage("You don't have permission to use that command");
			return;
		} else if (!AdminHandler.isAdminMode(player.getName()) && !AdminPermissions.has(player, Perms.ANYTP)) {
			player.sendMessage("You must be in admin mode to teleport");
			return;
		} else if (args.length < 1) {
			player.sendMessage("You must specify at least 1 argument for teleportation");
			return;
		}
		//Assume player or world if there is only 1 arg.
		if (args.length == 1) {
			Player target = null;
			World targetWorld = plugin.getServer().getWorld(args[0]);
			for (Player p : plugin.getServer().getOnlinePlayers()) {
				if (p.getName().toLowerCase().contains(args[0].toLowerCase()) && target == null && targetWorld == null) {
					target = p;
					break;
				}
			}
			if (targetWorld == null && target == null) {
				player.sendMessage("Could not find that target");
			} else if (targetWorld != null) {
				player.teleport(targetWorld.getSpawnLocation());
			} else {
				player.teleport(target.getLocation());
			}
		} 
		//Assume teleporting one player to another if 2 args
		else if (args.length == 2) {
			Player target = null;
			Player sent = null;
			World targetWorld = plugin.getServer().getWorld(args[1]);
			for (Player p : plugin.getServer().getOnlinePlayers()) {
				if (p.getName().toLowerCase().contains(args[1].toLowerCase()) && target == null && targetWorld == null)
					target = p;
				
				if (p.getName().toLowerCase().contains(args[0].toLowerCase()) && (sent == null || (p != target && target != null))) 
					sent = p;
				
				if (sent != null && (targetWorld != null || target != null))
					break;
			}
			if (sent == null) {
				player.sendMessage("Could not find a player named " + args[0]);
			} else if (target == null && targetWorld == null) {
				player.sendMessage("Could not find that target.");
			} else if (target == sent) {
				player.sendMessage("You can not send a player to themselves.");
			} else if (targetWorld != null){
				sent.teleport(targetWorld.getSpawnLocation());
				player.sendMessage("You have teleported " + sent.getName() + " to " + targetWorld.getName());
			} else {
				sent.teleport(target.getLocation());
				player.sendMessage("You have teleported " + sent.getName() + " to " + target.getName());
			}
		}
		//Assume coordinate if 3 args
		else if (args.length == 3) {
			try {
				int x = Integer.parseInt(args[0]);
				int y = Integer.parseInt(args[1]);
				int z = Integer.parseInt(args[2]);
				player.teleport(new Location(player.getWorld(), x, y, z));
			} catch (Exception e) {
				player.sendMessage("The proper format for coordinate teleporting is /tp x y z");
			}
			return;
		}
	}

	private void teleportAll(Player player, Location loc) {
		for (Player p : plugin.getServer().getOnlinePlayers()) {
			if (player.equals(p))
				continue;

			p.teleport(loc);
			p.sendMessage(player.getName() + " has brought you to their location.");
		}
	}

	/**
	 * Sends a fake logout message to all players connected that do not have all-messages permission
	 * 
	 */
	public void fakeLogout(Player player) {
		if (AdminPermissions.has(player, Perms.FAKELOG)) {
			AdminHandler.fakeLog(player.getName(), false);
		} else {
			AdminPermissions.noPermsMessage(player);
		}
	}

	/**
	 * Sends a fake login message to all players connected that do not have all-messages permission
	 * 
	 */
	public void fakeLogin(Player player) {
		if (AdminPermissions.has(player, Perms.FAKELOG)) {
			AdminHandler.fakeLog(player.getName(), true);
		} else {
			AdminPermissions.noPermsMessage(player);
		}
	}

	/**
	 * Lists what admin modes a player currently has.
	 * 
	 * @param player
	 * @param args
	 */
	public void adminStatus (Player player, String[] args) {
		if (AdminPermissions.hasAny(player)) {
			if (args.length < 1) {
				//send string of info for this player;
				player.sendMessage(AdminHandler.infoString(player));
			} else if (AdminPermissions.has(player, Perms.STATUS)) {
				for (Player pStatus : plugin.getServer().getOnlinePlayers()) {
					if (pStatus.getName().equalsIgnoreCase(args[0])){
						//Send infostring for this player
						player.sendMessage(AdminHandler.infoString(pStatus));
						player.sendMessage(pStatus.getName() + " is currently saved to: " + AdminHandler.getLocationString(pStatus.getName()));
					} else {
						player.sendMessage("Could not find a player named " + args[0]);
					}
				}
			}
		} else {
			AdminPermissions.noPermsMessage(player);
		}
	}

	/**
	 * Checks permissions for using /return
	 * then attempts to return the player to a saved location
	 */
	public void returnLoc (Player player) {
		if (AdminPermissions.has(player, Perms.RETURN)) {
			if (!admins.returnPlayer(player)) 
				player.sendMessage("You don't have a location saved to return to!");
		} else {
			//If they don't have permissions let them know
			AdminPermissions.noPermsMessage(player);
		}
	}

	/**
	 * Checks permissions then: 
	 * Saves a players current location to the admin map
	 * 
	 * @param player
	 */
	public void saveLoc (Player player) {
		String playerName = player.getName();
		if (AdminPermissions.has(player, Perms.RETURN)) {
			if (!AdminHandler.contains(playerName))
				AdminHandler.add(playerName);

			AdminHandler.setOrigin(playerName, player.getLocation());
			AdminHandler.savePlayer(playerName);
			player.sendMessage("You have saved your location at: " + ChatColor.DARK_AQUA + AdminHandler.getLocationString(playerName));
		} else {
			//If they don't have permissions let them know
			AdminPermissions.noPermsMessage(player);
		}
	}

	/**
	 * Toggles admin-mode for a player
	 * God, NoPickup, Stealth, Invis + Saves Location or
	 * Disables them and returns to the saved location
	 * 
	 * @param player
	 */
	public void adminMode (Player player) {
		String playerName = player.getName();
		if (AdminPermissions.has(player, Perms.ADMINMODE)) {
			//If this player is already in admin-mode - toggle it off.
			if(AdminHandler.isAdminMode(playerName)) {
				AdminHandler.setAdminMode(playerName, false);
				if( AdminPermissions.has(player, Perms.FAKELOG) )
					AdminHandler.fakeLog(playerName, true);
				admins.goVisible(player);
				player.sendMessage(ChatColor.RED + "Admin-Mode " + ChatColor.WHITE + "is now " + ChatColor.RED + "disabled.");
				//Return the player a half-second later
				admins.returnPlayer(player);
			} else {
				//Check if this player is in the admin map - add if necessary
				if (!AdminHandler.contains(playerName))
					AdminHandler.add(playerName);
				//Enable adminmode and send the message
				AdminHandler.setAdminMode(playerName, true);
				if( AdminPermissions.has(player, Perms.FAKELOG) )
					AdminHandler.fakeLog(playerName, true);
				admins.goInvisibleInitial(player);
				player.sendMessage(ChatColor.GREEN + "Admin-Mode " + ChatColor.WHITE + "is now " + ChatColor.GREEN + "enabled.");
				//Save the players current location as their origin.
				AdminHandler.setOrigin(playerName, player.getLocation());
				player.sendMessage("You have saved your location at: " + ChatColor.BLUE + AdminHandler.getLocationString(playerName));
			}
			AdminHandler.savePlayer(playerName);
		} else {
			//If they don't have permissions let them know
			AdminPermissions.noPermsMessage(player);
		}
	}

	/**
	 * Toggles god-mode for the player
	 * 
	 * @param player
	 */
	public void godMode (Player player) {
		String playerName = player.getName();
		if (AdminPermissions.has(player, Perms.GOD)) {
			//If this player is already in god-mode - toggle it off.
			if(AdminHandler.isGod(playerName)) {
				AdminHandler.setGod(playerName, false);
				player.sendMessage(ChatColor.RED + "God-Mode " + ChatColor.WHITE + "is now " + ChatColor.RED + "disabled.");
			} else {
				//Check if this player is in the admin map - add if necessary
				if (!AdminHandler.contains(playerName)) {
					AdminHandler.add(playerName);
					log.info("Added player to admin mapping");
				}

				//Now enable it and send the message
				AdminHandler.setGod(playerName, true);
				player.sendMessage(ChatColor.GREEN + "God-Mode " + ChatColor.WHITE + "is now " + ChatColor.GREEN + "enabled.");
			}
			AdminHandler.savePlayer(playerName);
		} else {
			//If they don't have permissions let them know
			AdminPermissions.noPermsMessage(player);
		}
	}

	/**
	 * Toggles stealth login/logoff mode
	 * 
	 * 
	 * @param player
	 */
	public void stealth (Player player) {
		String playerName = player.getName();
		//Check permissions to use the command
		if (AdminPermissions.has(player, Perms.STEALTH)) {
			//If the player is already stealthed, disable and send them a message
			if (AdminHandler.isStealthed(playerName)) {
				AdminHandler.setStealthed(playerName, false);
				AdminHandler.fakeLog(playerName, true);
				player.sendMessage(ChatColor.RED + "Stealth-Mode " + ChatColor.WHITE + "is now " + ChatColor.RED + "disabled.");
			} else {
				//Check if this player is in the admin map - add if necessary
				if (!AdminHandler.contains(playerName))
					AdminHandler.add(playerName);
				//Now enable stealth mode and send the messages
				AdminHandler.setStealthed(playerName, true);
				AdminHandler.fakeLog(playerName, false);
				player.sendMessage(ChatColor.GREEN + "Stealth-Mode " + ChatColor.WHITE + "is now " + ChatColor.GREEN + "enabled.");
			}
			AdminHandler.savePlayer(playerName);
		} else {
			//If they don't have permissions let them know
			AdminPermissions.noPermsMessage(player);
		}
	}

	/**
	 * Toggles the player being invisible/visible to players
	 * Also toggles off Picking up of items while invisibility is on.
	 * 
	 * @param player
	 */
	public void vanish (Player player) {
		String playerName = player.getName();
		//Check permissions to use the command
		if (AdminPermissions.has(player, Perms.VANISH)) {
			//If the player is already vanished - unvanish them.
			if (AdminHandler.isInvisible(playerName)) {
				AdminHandler.setInvis(playerName, false);
				AdminHandler.setNoPickup(playerName, false);
				admins.goVisible(player);
				player.sendMessage(ChatColor.RED + " No-Pickup & Invisibility " + ChatColor.WHITE + "are now " + ChatColor.RED + "disabled.");
			} else {
				//Check if the player is in the admin map - add if not
				if (!AdminHandler.contains(playerName))
					AdminHandler.add(playerName);
				//Now enable invis, no pickup and send the message
				AdminHandler.setInvis(playerName, true);
				AdminHandler.setNoPickup(playerName, true);
				admins.goInvisibleInitial(player);
				player.sendMessage(ChatColor.GREEN + "No-Pickup & Invisibility " + ChatColor.WHITE + "are now " + ChatColor.GREEN + "enabled.");
			}
			AdminHandler.savePlayer(playerName);
		} else {
			//Let the player know they don't have permission
			AdminPermissions.noPermsMessage(player);
		}
	} 
}
