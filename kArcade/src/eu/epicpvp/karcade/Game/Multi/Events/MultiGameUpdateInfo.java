package eu.epicpvp.karcade.Game.Multi.Events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import eu.epicpvp.karcade.Game.Multi.Games.MultiGame;
import eu.epicpvp.kcore.Packets.PacketArenaStatus;
import lombok.Getter;
import lombok.Setter;

public class MultiGameUpdateInfo extends Event implements Cancellable{
	private static HandlerList handlers = new HandlerList();
	private boolean cancel=false;
	@Getter
	@Setter
	private PacketArenaStatus packet;
	@Getter
	private MultiGame game;
	
	public MultiGameUpdateInfo(MultiGame game,PacketArenaStatus packet){
		this.packet=packet;
		this.game=game;
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
