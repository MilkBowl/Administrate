package net.milkbowl.administrate.listeners;

import java.lang.reflect.Field;

import net.milkbowl.administrate.AdminHandler;
import net.milkbowl.administrate.AdminPermissions;
import net.milkbowl.administrate.AdminPermissions.Perms;

import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.getspout.spout.packet.standard.MCCraftPacket;
import org.getspout.spoutapi.packet.listener.PacketListener;
import org.getspout.spoutapi.packet.standard.MCPacket;

public class AdminPacketListener implements PacketListener {

	public boolean checkPacket(Player player, MCPacket mcPacket) {
		if (AdminPermissions.has(player, Perms.ALL_MESSAGES))
			return true;
		
		Integer id = null;
		try {
			Field f = ((MCCraftPacket) mcPacket).getPacket().getClass().getField("a");
			if (f == null)
				return true;
			
			id = (Integer) f.get(((MCCraftPacket) mcPacket).getPacket());
			Entity entity = ((CraftWorld) null).getHandle().getEntity(id).getBukkitEntity();
			if (entity instanceof Player) {
				return !AdminHandler.isInvisible(((Player) entity).getName());
			}
		} catch (Exception e) {
			return true;
		}
		
		return true;
	}

}
