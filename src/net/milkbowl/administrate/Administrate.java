/**
 * 
 */
package net.milkbowl.administrate;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

/**
 * @author sleaker
 *
 */
public class Administrate extends JavaPlugin {
	protected static final Logger log = Logger.getLogger("Minecraft");
	private AdminPlayerListener playerListener = new AdminPlayerListener(this);
	private AdminEntityListener entityListener = new AdminEntityListener();
	public AdminHandler adminHandler;
	public CommandExecutor cmdExec;

	public static final String plugName = "[Administrate]";
	public static final String playerDataPath = "plugins/Administrate/players/";
	Configuration globalConfig;


	public void onDisable() {
		log.info(plugName + " - Disabled!");
	}

	public void onEnable() {
		//Setup Permissions 
		AdminPermissions.initialize(getServer());
		
		//Make our directories.
		File dir = new File(playerDataPath);
		dir.mkdirs();

		if (AdminPermissions.isInvalidHandler()) {
			log.warning(plugName + " - Could not detect a permissions plugin, disabling.");
			getServer().getPluginManager().disablePlugin(this);
		}

		//Register our events
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Highest, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Highest, this);
		pm.registerEvent(Event.Type.PLAYER_TELEPORT, playerListener, Priority.Highest, this);
		pm.registerEvent(Event.Type.PLAYER_PICKUP_ITEM, playerListener, Priority.Highest, this);
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Priority.Highest, this);
		pm.registerEvent(Event.Type.ENTITY_COMBUST, entityListener, Priority.Highest, this);
		pm.registerEvent(Event.Type.ENTITY_TARGET, entityListener, Priority.Highest, this);

		adminHandler = new AdminHandler(this);
		//Register our commands
		cmdExec = new AdminCommandHandler(this);
		getCommand("stealth").setExecutor(cmdExec);
		getCommand("vanish").setExecutor(cmdExec);
		getCommand("admin").setExecutor(cmdExec);
		getCommand("god").setExecutor(cmdExec);
		getCommand("return").setExecutor(cmdExec);
		getCommand("saveloc").setExecutor(cmdExec);
		getCommand("adminstatus").setExecutor(cmdExec);
		getCommand("fakeout").setExecutor(cmdExec);
		getCommand("fakein").setExecutor(cmdExec);
		getCommand("admin_put").setExecutor(cmdExec);
		getCommand("admin_bring").setExecutor(cmdExec);
		getCommand("admin_tp").setExecutor(cmdExec);
		getCommand("admin_heal").setExecutor(cmdExec);
	}

	public AdminHandler getAdminHandler() {
		return adminHandler;
	}    
}
