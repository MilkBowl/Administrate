/**
 * 
 */
package net.milkbowl.administrate;

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
    private static Administrate plugin;
    private AdminHandler admins;
        
    @SuppressWarnings("static-access")
    AdminEntityListener(Administrate plugin) {
        this.plugin = plugin;
        admins = new AdminHandler(this.plugin);
    }
    
    public void onEntityTarget (EntityTargetEvent event) {
        if (event.isCancelled())
            return;
        
        if (event.getEntity() instanceof Player) {
            String playerName = ((Player) event.getEntity()).getName();
            if( (admins.isGod(playerName) || admins.isInvisible(playerName)) && AdminPermissions.has((Player) event.getEntity(), AdminPermissions.noAggro))
                event.setCancelled(true);
        }
    }
    
    public void onEntityDamage (EntityDamageEvent event) {
        if (event.isCancelled())
            return;
        
        if (event.getEntity() instanceof Player) {
            String playerName = ((Player) event.getEntity()).getName();
            if (admins.isGod(playerName))
                event.setCancelled(true);
        }
    }
    
    public void onEntityCombust (EntityCombustEvent event) {
        if (event.isCancelled())
            return;
        
        if (event.getEntity() instanceof Player) {
            String playerName = ((Player) event.getEntity()).getName();
            if (admins.isGod(playerName)) {
                event.setCancelled(true);
                ((Player) event.getEntity()).setFireTicks(0);
            }
        }
    }
}
