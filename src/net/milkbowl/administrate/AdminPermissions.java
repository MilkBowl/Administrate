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
    public static final String stealthPerm = "administrate.stealth";
    public static final String adminModePerm = "administrate.adminmode";
    public static final String godPerm = "administrate.god";
    public static final String vanishPerm = "administrate.invisible";
    public static final String allMessages = "administrate.allmessages";
    public static final String noAggro = "administrate.noaggro";
    public static final String returnPerm = "administrate.return";
    public static final String seeInvis = "administrate.seeinvis";
    public static final String status = "administrate.status";
    
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

    public static boolean permission(Player player, String permission, boolean defaultPerm) {
        switch (handler) {
        case PERMISSIONSEX:
            return PermissionsEx.getPermissionManager().has(player, permission);
        case PERMISSIONS3:
            return ((Permissions) permissionPlugin).getHandler().has(player, permission);
        case PERMISSIONS:
            return ((Permissions) permissionPlugin).getHandler().has(player, permission);
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
    		if (pm.has(player, stealthPerm) || pm.has(player, godPerm) || pm.has(player, returnPerm) || pm.has(player, stealthPerm) || pm.has(player, vanishPerm))
    			return true;
    		else
    			return false;
    	case PERMISSIONS3:
    		PermissionHandler ph = ((Permissions) permissionPlugin).getHandler();
    		if (ph.has(player, stealthPerm) || ph.has(player, godPerm) || ph.has(player, returnPerm) || ph.has(player, stealthPerm) || ph.has(player, vanishPerm))
    			return true;
    		else
    			return false;
    	case PERMISSIONS:
    		PermissionHandler pha = ((Permissions) permissionPlugin).getHandler();
    		if (pha.has(player, stealthPerm) || pha.has(player, godPerm) || pha.has(player, returnPerm) || pha.has(player, stealthPerm) || pha.has(player, vanishPerm))
    			return true;
    		else
    			return false;
    	case NONE:
    		return false;
    	default:
    		return false;
    	}
    }

    public static boolean has(Player player, String perm) {
        switch (handler) {
        case PERMISSIONSEX:
            return PermissionsEx.getPermissionManager().has(player, perm);
        case PERMISSIONS3:
            return ((Permissions) permissionPlugin).getHandler().has(player, perm);
        case PERMISSIONS:
            return ((Permissions) permissionPlugin).getHandler().has(player, perm);
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
