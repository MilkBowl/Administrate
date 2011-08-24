package net.milkbowl.administrate;

import net.milkbowl.administrate.listeners.AdminPacketListener;

import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.packet.PacketManager;

public class AdminPacketManager {
	
	private AdminPacketListener packetListener = new AdminPacketListener();

	public AdminPacketManager() {
		
	}
	
	public void enable() {
		PacketManager pm = SpoutManager.getPacketManager();
		pm.addListener(5, packetListener);
		pm.addListener(17, packetListener);
		pm.addListener(18, packetListener);
		pm.addListener(19, packetListener);
		pm.addListener(20, packetListener);
		pm.addListener(28, packetListener);
		pm.addListener(30, packetListener);
		pm.addListener(31, packetListener);
		pm.addListener(32, packetListener);
		pm.addListener(33, packetListener);
		pm.addListener(34, packetListener);
		pm.addListener(38, packetListener);
		pm.addListener(39, packetListener);
	}
	
	public void disable() {
		PacketManager pm = SpoutManager.getPacketManager();
		pm.removeListener(5, packetListener);
		pm.removeListener(17, packetListener);
		pm.removeListener(18, packetListener);
		pm.removeListener(19, packetListener);
		pm.removeListener(20, packetListener);
		pm.removeListener(28, packetListener);
		pm.removeListener(30, packetListener);
		pm.removeListener(31, packetListener);
		pm.removeListener(32, packetListener);
		pm.removeListener(33, packetListener);
		pm.removeListener(34, packetListener);
		pm.removeListener(38, packetListener);
		pm.removeListener(39, packetListener);
	}
}
