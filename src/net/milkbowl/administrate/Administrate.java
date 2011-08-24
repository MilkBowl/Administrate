/**
 * 
 */
package net.milkbowl.administrate;

import java.io.File;
import java.util.Collection;
import java.util.logging.Logger;

import net.milkbowl.administrate.listeners.AdminEntityListener;
import net.milkbowl.administrate.listeners.AdminPlayerListener;
import net.milkbowl.administrate.listeners.AdminPluginListener;
import net.milkbowl.administrate.runnable.ResetVisiblesForPlayer;
import net.milkbowl.administrate.runnable.UpdateInvisibilityTask;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

/**
 * @author sleaker
 *
 */
public class Administrate extends JavaPlugin {
	protected static final Logger log = Logger.getLogger("Minecraft");
	private AdminPlayerListener playerListener = new AdminPlayerListener(this);
	private AdminEntityListener entityListener = new AdminEntityListener(this);
	private AdminPluginListener pluginListener = new AdminPluginListener(this);
	private final AdminPacketManager packetManager = new AdminPacketManager();
	public AdminHandler adminHandler;
	public CommandExecutor cmdExec;
	public static boolean useSpout = false;

	public static Permission perms;
	public static final String plugName = "[Administrate]";
	public static final String playerDataPath = "plugins/Administrate/players/";
	Configuration globalConfig;


	public void onDisable() {
		log.info(plugName + " - Disabled!");
	}

	public void onEnable() {
		//Setup Permissions 
		if (!setupDependencies()) {
			log.warning(plugName + " - Could not detect a permissions plugin, disabling.");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		
		setupOptionals();
		
		//Make our directories.
		File dir = new File(playerDataPath);
		dir.mkdirs();

		//Register our events
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Type.PLAYER_JOIN, playerListener, Priority.Highest, this);
		pm.registerEvent(Type.PLAYER_QUIT, playerListener, Priority.Highest, this);
		pm.registerEvent(Type.PLAYER_TELEPORT, playerListener, Priority.Highest, this);
		pm.registerEvent(Type.PLAYER_PICKUP_ITEM, playerListener, Priority.Highest, this);
		pm.registerEvent(Type.ENTITY_DAMAGE, entityListener, Priority.Highest, this);
		pm.registerEvent(Type.ENTITY_COMBUST, entityListener, Priority.Highest, this);
		pm.registerEvent(Type.ENTITY_TARGET, entityListener, Priority.Highest, this);
		pm.registerEvent(Type.PLUGIN_ENABLE, pluginListener, Priority.Monitor, this);
		pm.registerEvent(Type.PLUGIN_DISABLE, pluginListener, Priority.Monitor, this);

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
		
		//If this was a reload lets make sure to reload all of our players data
		for (Player player : this.getServer().getOnlinePlayers()) {
			loadPlayer(player);
		}
	}

	private void setupOptionals() {
		Plugin spout = getServer().getPluginManager().getPlugin("Spout");
		if (spout != null) {
			if (spout.isEnabled()) {
				useSpout = true;
				packetManager.enable();
			}
		}
	}
	
	private boolean setupDependencies() {
        Collection<RegisteredServiceProvider<Permission>> perms = this.getServer().getServicesManager().getRegistrations(net.milkbowl.vault.permission.Permission.class);
        for(RegisteredServiceProvider<Permission> perm : perms) {
            Permission p = perm.getProvider();
            log.info(String.format("[%s] Found Service (Permission) %s", getDescription().getName(), p.getName()));
        }
        
        Administrate.perms = this.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class).getProvider();
        log.info(String.format("[%s] Using Permission Provider %s", getDescription().getName(), Administrate.perms.getName()));
        return (Administrate.perms != null);
	}
	
	public AdminHandler getAdminHandler() {
		return adminHandler;
	}    
	
	/**
	 * @return the packetManager
	 */
	public AdminPacketManager getPacketManager() {
		return packetManager;
	}

	public void loadPlayer(Player player) {
		//Try to load the player object from file
		if (AdminPermissions.hasAny(player)) {
			if (AdminHandler.loadPlayer(player.getName())) {

				//Make the player go invisible if they have the toggle
				if (AdminHandler.isInvisible(player.getName()))
					this.getServer().getScheduler().scheduleSyncDelayedTask(this, new UpdateInvisibilityTask(player, adminHandler));
			} 
		}

		//Makes it so players can't rejoin the server to see invisible players
		this.getServer().getScheduler().scheduleSyncDelayedTask(this, new ResetVisiblesForPlayer(player, adminHandler));
	}
}
