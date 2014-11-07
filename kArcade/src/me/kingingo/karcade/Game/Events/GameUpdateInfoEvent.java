package me.kingingo.karcade.Game.Events;

import lombok.Getter;
import lombok.Setter;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Packet.Packets.SERVER_STATUS;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameUpdateInfoEvent extends Event implements Cancellable{
	private static HandlerList handlers = new HandlerList();
	private boolean cancel=false;
	@Getter
	@Setter
	private SERVER_STATUS packet;
	
	public GameUpdateInfoEvent(SERVER_STATUS packet){
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
