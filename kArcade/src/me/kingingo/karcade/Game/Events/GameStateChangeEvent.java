package me.kingingo.karcade.Game.Events;

import me.kingingo.kcore.Enum.GameState;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameStateChangeEvent extends Event implements Cancellable{
	private static HandlerList handlers = new HandlerList();
	private GameState from;
	private GameState to;
	private boolean cancel=false;
	
	public GameStateChangeEvent(GameState from,GameState to){
		this.from=from;
		this.to=to;
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
