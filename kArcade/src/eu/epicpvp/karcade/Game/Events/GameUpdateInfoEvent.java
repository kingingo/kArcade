package eu.epicpvp.karcade.Game.Events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import dev.wolveringer.dataclient.protocoll.packets.PacketOutServerStatus;
import lombok.Getter;
import lombok.Setter;

public class GameUpdateInfoEvent extends Event implements Cancellable{
	private static HandlerList handlers = new HandlerList();
	private boolean cancel=false;
	@Getter
	@Setter
	private PacketOutServerStatus packet;
	
	public GameUpdateInfoEvent(PacketOutServerStatus packet){
		this.packet=packet;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
        return handlers;
    }

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean arg0) {
		cancel=arg0;
	}

}
