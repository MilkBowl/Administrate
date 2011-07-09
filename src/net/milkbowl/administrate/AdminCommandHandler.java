/**
 * 
 */
package net.milkbowl.administrate;


import java.util.logging.Logger;

import net.milkbowl.administrate.AdminPermissions.Perms;

import org.bukkit.ChatColor;
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
            }
        }
        return false;
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
                AdminHandler.fakeLog(playerName, true);
                player.sendMessage(ChatColor.RED + "Admin-Mode " + ChatColor.WHITE + "is now " + ChatColor.RED + "disabled.");
                //Return the player a half-second later
                admins.returnPlayer(player);
            } else {
                //Check if this player is in the admin map - add if necessary
                if (!AdminHandler.contains(playerName))
                    AdminHandler.add(playerName);
                //Enable adminmode and send the message
                AdminHandler.setAdminMode(playerName, true);
                AdminHandler.fakeLog(playerName, false);
                player.sendMessage(ChatColor.GREEN + "Admin-Mode " + ChatColor.WHITE + "is now " + ChatColor.GREEN + "enabled.");
                //Save the players current location as their origin.
                AdminHandler.setOrigin(playerName, player.getLocation());
                player.sendMessage("You have saved your location at: " + ChatColor.BLUE + AdminHandler.getLocationString(playerName));
            }
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
                admins.goInvisible(player);
                player.sendMessage(ChatColor.GREEN + "No-Pickup & Invisibility " + ChatColor.WHITE + "are now " + ChatColor.GREEN + "enabled.");
            }
        } else {
            //Let the player know they don't have permission
            AdminPermissions.noPermsMessage(player);
        }
    } 
}
