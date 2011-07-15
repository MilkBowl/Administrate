/**
 * 
 */
package net.milkbowl.administrate;

import net.milkbowl.administrate.AdminPermissions.Perms;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTargetEvent;

/**
 * @author sleaker
 *
 */
public class AdminEntityListener extends EntityListener {
        
	private Administrate plugin;
    AdminEntityListener(Administrate plugin) {
    	this.plugin = plugin;
    }
    
    public void onEntityTarget (EntityTargetEvent event) {
        if (event.isCancelled())
            return;
        
        if (event.getTarget() instanceof Player ) {
            String playerName = ((Player) event.getTarget()).getName();
            if( (AdminHandler.isGod(playerName) || AdminHandler.isInvisible(playerName)) && AdminPermissions.has((Player) event.getTarget(), Perms.NO_AGGRO))
                event.setCancelled(true);
        }
    }
    
    public void onEntityDamage (EntityDamageEvent event) {
        if (event.isCancelled())
            return;
        
        if (event.getEntity() instanceof Player) {
            String playerName = ((Player) event.getEntity()).getName();
            if (AdminHandler.isGod(playerName))
                event.setCancelled(true);
        }
    }
    
    public void onEntityCombust (EntityCombustEvent event) {
        if (event.isCancelled())
            return;
        
        if (event.getEntity() instanceof Player) {
            String playerName = ((Player) event.getEntity()).getName();
            if (AdminHandler.isGod(playerName)) {
                event.setCancelled(true);
                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new douse((Player) event.getEntity()), 2);
            }
        }
    }
    
    public class douse implements Runnable {
    	
    	Player player;
    	douse(Player player) {
    		this.player = player;
    	}
    	
		public void run() {
			player.setFireTicks(0);
		}
    }
}
