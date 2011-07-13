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
        
    AdminEntityListener() {
    }
    
    public void onEntityTarget (EntityTargetEvent event) {
        if (event.isCancelled())
            return;
        
        if (event.getTarget() instanceof Player && !(event.getEntity() instanceof Player)) {
            String playerName = ((Player) event.getEntity()).getName();
            if( (AdminHandler.isGod(playerName) || AdminHandler.isInvisible(playerName)) && AdminPermissions.has((Player) event.getEntity(), Perms.NO_AGGRO))
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
                ((Player) event.getEntity()).setFireTicks(0);
            }
        }
    }
}
