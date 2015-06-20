package me.kingingo.karcade.Game.Multi.Events;

import lombok.Getter;
import lombok.Setter;
import me.kingingo.karcade.Game.Multi.Games.MultiGame;
import me.kingingo.kcore.Packet.Packets.ARENA_STATUS;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MultiGameUpdateInfo extends Event implements Cancellable{
	private static HandlerList handlers = new HandlerList();
	private boolean cancel=false;
	@Getter
	@Setter
	private ARENA_STATUS packet;
	@Getter
	private MultiGame game;
	
	public MultiGameUpdateInfo(MultiGame game,ARENA_STATUS packet){
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
