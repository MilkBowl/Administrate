/**
 * 
 */
package net.milkbowl.administrate;

import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

/**
 * @author sleaker
 *
 */
public class AdminPermissions {
	public static enum Perms {
		STEALTH(0, "administrate.stealth"),
		ADMINMODE(1, "administrate.adminmode"),
		GOD(2, "administrate.god"),
		VANISH(3, "administrate.invisible"),
		ALL_MESSAGES(4, "administrate.allmessages"),
		NO_AGGRO(5, "administrate.noaggro"),
		RETURN(6, "administrate.return"),
		SEE_INVIS(7, "administrate.seeinvis"),
		STATUS(8, "administrate.status"),
		FAKELOG(9, "administrate.fakelog");
		
		int id = -1;
		String perm = null;
		
		Perms(int id, String perm) {
			this.id = id;
			this.perm = perm;
		}
		
		public int getId() {
			return id;
		}

		public String getPerm() {
			return perm;
		}
	}

    public static Logger log = Logger.getLogger("Minecraft");
    private static PermissionsHandler handler;

    private static Plugin permissionPlugin;
    public static Plugin plugin;

    private enum PermissionsHandler {
        PERMISSIONSEX, PERMISSIONS3, PERMISSIONS, NONE
    }


    public static void initialize(Server server) {
        plugin = server.getPluginManager().getPlugin("Administrate");
        Plugin permissionsEx = server.getPluginManager().getPlugin("PermissionsEx");
        Plugin permissions = server.getPluginManager().getPlugin("Permissions");

        if (permissionsEx != null) {
            permissionPlugin = permissionsEx;
            handler = PermissionsHandler.PERMISSIONSEX;
            String version = permissionsEx.getDescription().getVersion();
            log.info(Administrate.plugName + " - Permissions hooked using: PermissionsEx v" + version);
        } else if (permissions != null) {
            permissionPlugin = permissions;
            String version = permissions.getDescription().getVersion();
            if(version.contains("3.")) {
                // This shouldn't make any difference according to the Permissions API
                handler = PermissionsHandler.PERMISSIONS3;
            } else {
                handler = PermissionsHandler.PERMISSIONS;
            }
            log.info(Administrate.plugName + " - Permissions hooked using: Permissions v" + version);
        } else {
            handler = PermissionsHandler.NONE;
            log.warning(Administrate.plugName + " - A permission plugin was not detected.");
        }
    }

    public static boolean permission(Player player, Perms perm, boolean defaultPerm) {
        switch (handler) {
        case PERMISSIONSEX:
            return PermissionsEx.getPermissionManager().has(player, perm.getPerm());
        case PERMISSIONS3:
            return ((Permissions) permissionPlugin).getHandler().has(player, perm.getPerm());
        case PERMISSIONS:
            return ((Permissions) permissionPlugin).getHandler().has(player, perm.getPerm());
        case NONE:
            return defaultPerm;
        default:
            return defaultPerm;
        }
    }

    /*
     * Return an info double
     */
    public static double infoDouble(Player player, String nodeName) {
        switch (handler) {
        case PERMISSIONSEX:
            return PermissionsEx.getPermissionManager().getUser(player.getName()).getOptionDouble(nodeName, player.getWorld().getName(), -1);
        case PERMISSIONS3:
            return ((Permissions) permissionPlugin).getHandler().getPermissionDouble(player.getWorld().getName(), player.getName(), nodeName);
        case PERMISSIONS:
            return ((Permissions) permissionPlugin).getHandler().getPermissionDouble(player.getWorld().getName(), player.getName(), nodeName);
        case NONE:
            return -1;
        default:
            return -1;
        }
    }
    
    public static boolean hasAny(Player player) {
    	switch (handler) {
    	case PERMISSIONSEX:
    		PermissionManager pm = PermissionsEx.getPermissionManager();
    		if (pm.has(player, Perms.STEALTH.getPerm()) || pm.has(player, Perms.GOD.getPerm()) || pm.has(player, Perms.RETURN.getPerm()) || pm.has(player, Perms.STEALTH.getPerm()) || pm.has(player, Perms.VANISH.getPerm()))
    			return true;
    		else
    			return false;
    	case PERMISSIONS3:
    		PermissionHandler ph = ((Permissions) permissionPlugin).getHandler();
    		if (ph.has(player, Perms.STEALTH.getPerm()) || ph.has(player, Perms.GOD.getPerm()) || ph.has(player, Perms.RETURN.getPerm()) || ph.has(player, Perms.STEALTH.getPerm()) || ph.has(player, Perms.VANISH.getPerm()))
    			return true;
    		else
    			return false;
    	case PERMISSIONS:
    		PermissionHandler pha = ((Permissions) permissionPlugin).getHandler();
    		if (pha.has(player, Perms.STEALTH.getPerm()) || pha.has(player, Perms.GOD.getPerm()) || pha.has(player, Perms.RETURN.getPerm()) || pha.has(player, Perms.STEALTH.getPerm()) || pha.has(player, Perms.VANISH.getPerm()))
    			return true;
    		else
    			return false;
    	case NONE:
    		return false;
    	default:
    		return false;
    	}
    }

    public static boolean has(Player player, Perms perm) {
        switch (handler) {
        case PERMISSIONSEX:
            return PermissionsEx.getPermissionManager().has(player, perm.getPerm());
        case PERMISSIONS3:
            return ((Permissions) permissionPlugin).getHandler().has(player, perm.getPerm());
        case PERMISSIONS:
            return ((Permissions) permissionPlugin).getHandler().has(player, perm.getPerm());
        case NONE:
            return false;
        default:
            return false;
        }
    }

    public static boolean isInvalidHandler() {
        if (handler == PermissionsHandler.NONE)
            return true;
        else
            return false;
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
