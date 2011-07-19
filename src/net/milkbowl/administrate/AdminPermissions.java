/**
 * 
 */
package net.milkbowl.administrate;

import org.bukkit.entity.Player;

/**
 * @author sleaker
 *
 */
public class AdminPermissions {
	public static enum Perms {
		STEALTH("administrate.stealth"),
		ADMINMODE("administrate.adminmode"),
		GOD("administrate.god"),
		VANISH("administrate.invisible"),
		ALL_MESSAGES("administrate.allmessages"),
		NO_AGGRO("administrate.noaggro"),
		RETURN("administrate.return"),
		SEE_INVIS("administrate.seeinvis"),
		STATUS("administrate.status"),
		FAKELOG("administrate.fakelog"),
		ADMINTP("administrate.adminmode.tp"),
		ANYTP("administrate.tp"),
		ADMINHEAL("administrate.adminmode.heal"),
		ANYHEAL("administrate.heal");
		
		String perm = null;
		
		Perms(String perm) {
			this.perm = perm;
		}

		public String getPerm() {
			return perm;
		}
	}

    public static boolean has(Player player, Perms perm) {
    	return Administrate.perms.has(player, perm.getPerm());
    }

    
    /*
     * Return an info double
     */
	public static double infoDouble(Player player, String nodeName) {
    	return Administrate.perms.getPlayerInfoDouble(player.getWorld().getName(), player.getName(), nodeName, -1);
    }
    
	
    public static boolean hasAny(Player player) {
    	return has(player, Perms.STEALTH) || has(player, Perms.GOD) || has(player, Perms.RETURN) || has(player, Perms.STEALTH) || has(player, Perms.VANISH);
    }


    /**
     * Warns the player they did not have permission for the command.
     * 
     * @param player
     */
    public static void noPermsMessage(Player player) {
        player.sendMessage("You do not have permission to use this command");
    }

}
