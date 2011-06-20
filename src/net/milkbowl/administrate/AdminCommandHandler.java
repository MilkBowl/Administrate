/**
 * 
 */
package net.milkbowl.administrate;


import java.util.logging.Logger;

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
    
    @SuppressWarnings("static-access")
    AdminCommandHandler(Administrate plugin) {
        this.plugin = plugin;
        admins = new AdminHandler(this.plugin);
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
            }
        }
        return false;
    }
    
    /**
     * Checks permissions for using /return
     * then attempts to return the player to a saved location
     */
    public void returnLoc (Player player) {
        if (AdminPermissions.has(player, AdminPermissions.returnPerm)) {
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
        if (AdminPermissions.has(player, AdminPermissions.returnPerm)) {
            if (!admins.contains(playerName))
                admins.add(playerName);
            
            admins.setOrigin(playerName, player.getLocation());
            player.sendMessage("You have saved your location at: " + ChatColor.DARK_AQUA + admins.getLocationString(playerName));
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
        if (AdminPermissions.has(player, AdminPermissions.adminModePerm)) {
            //If this player is already in admin-mode - toggle it off.
            if(admins.isAdminMode(playerName)) {
                admins.setAdminMode(playerName, false);
                player.sendMessage(ChatColor.RED + "Admin-Mode " + ChatColor.WHITE + "is now " + ChatColor.RED + "disabled.");
                //Return the player a half-second later
                admins.returnPlayer(player);
            } else {
                //Check if this player is in the admin map - add if necessary
                if (!admins.contains(playerName))
                    admins.add(playerName);
                //Enable adminmode and send the message
                admins.setAdminMode(playerName, true);
                player.sendMessage(ChatColor.GREEN + "Admin-Mode " + ChatColor.WHITE + "is now " + ChatColor.GREEN + "enabled.");
                //Save the players current location as their origin.
                admins.setOrigin(playerName, player.getLocation());
                player.sendMessage("You have saved your location at: " + ChatColor.BLUE + admins.getLocationString(playerName));
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
        if (AdminPermissions.has(player, AdminPermissions.godPerm)) {
            //If this player is already in god-mode - toggle it off.
            if(admins.isGod(playerName)) {
                admins.setGod(playerName, false);
                player.sendMessage(ChatColor.RED + "God-Mode " + ChatColor.WHITE + "is now " + ChatColor.RED + "disabled.");
            } else {
                //Check if this player is in the admin map - add if necessary
                if (!admins.contains(playerName)) {
                    admins.add(playerName);
                    log.info("Added player to admin mapping");
                }

                //Now enable it and send the message
                admins.setGod(playerName, true);
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
        if (AdminPermissions.has(player, AdminPermissions.stealthPerm)) {
            //If the player is already stealthed, disable and send them a message
            if (admins.isStealthed(playerName)) {
                admins.setStealthed(playerName, false);
                player.sendMessage(ChatColor.RED + "Stealth-Mode " + ChatColor.WHITE + "is now " + ChatColor.RED + "disabled.");
            } else {
                //Check if this player is in the admin map - add if necessary
                if (!admins.contains(playerName))
                    admins.add(playerName);
                //Now enable stealth mode and send the message
                admins.setStealthed(playerName, true);
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
        if (AdminPermissions.has(player, AdminPermissions.vanishPerm)) {
            //If the player is already vanished - unvanish them.
            if (admins.isInvisible(playerName)) {
                admins.setInvis(playerName, false);
                admins.setNoPickup(playerName, false);
                admins.goVisible(player);
                player.sendMessage(ChatColor.RED + " No-Pickup & Invisibility " + ChatColor.WHITE + "are now " + ChatColor.RED + "disabled.");
            } else {
                //Check if the player is in the admin map - add if not
                if (!admins.contains(playerName))
                    admins.add(playerName);
                //Now enable invis, no pickup and send the message
                admins.setInvis(playerName, true);
                admins.setNoPickup(playerName, true);
                admins.goInvisible(player);
                player.sendMessage(ChatColor.GREEN + "No-Pickup & Invisibility " + ChatColor.WHITE + "are now " + ChatColor.GREEN + "enabled.");
            }
        } else {
            //Let the player know they don't have permission
            AdminPermissions.noPermsMessage(player);
        }
    }    
}
