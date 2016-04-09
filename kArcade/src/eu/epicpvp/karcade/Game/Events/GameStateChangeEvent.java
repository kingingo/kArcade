package eu.epicpvp.karcade.Game.Events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import dev.wolveringer.dataserver.gamestats.GameState;
import eu.epicpvp.kcore.Enum.GameStateChangeReason;
import lombok.Getter;

public class GameStateChangeEvent extends Event implements Cancellable{
	private static HandlerList handlers = new HandlerList();
	private GameState from;
	private GameState to;
	private boolean cancel=false;
	@Getter
	private GameStateChangeReason reason;
	
	public GameStateChangeEvent(GameState from,GameState to,GameStateChangeReason reason){
		this.from=from;
		this.to=to;
		this.reason=reason;
	}
	
	public GameState getTo(){
		return to;
	}
	
	public GameState getFrom(){
		return from;
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
