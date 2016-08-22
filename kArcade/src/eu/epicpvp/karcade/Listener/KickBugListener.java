package eu.epicpvp.karcade.Listener;

import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

import eu.epicpvp.kcore.Listener.kListener;
import eu.epicpvp.kcore.PacketAPI.Packets.WrapperPacketPlayOutSpawnEntity;
import eu.epicpvp.kcore.PacketAPI.packetlistener.event.PacketListenerSendEvent;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntity;

public class KickBugListener extends kListener{
	
	public KickBugListener(JavaPlugin instance) {
		super(instance, "KickBugListener");
	}

	@EventHandler
	public void PacketListener(PacketListenerSendEvent ev){
		if(ev.getPacket() instanceof PacketPlayOutSpawnEntity){
			WrapperPacketPlayOutSpawnEntity entity = new WrapperPacketPlayOutSpawnEntity(((PacketPlayOutSpawnEntity)ev.getPacket()));
					
			if(entity.getEntityType() == 60){
				if(entity.getVelX() == 0&&entity.getVelY() == 0&&entity.getVelZ() == 0&&entity.getObjectData() == 0){
					ev.setCancelled(true);
				}
			}
		}
		
	}
}
