package me.kingingo.karcade.Game.Events;

import me.kingingo.kcore.Enum.GameState;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameStateChangeEvent extends Event implements Cancellable{
	private static HandlerList handlers = new HandlerList();
	private GameState state;
	private boolean cancel=false;
	
	public GameStateChangeEvent(GameState state){
		this.state=state;
	}
	
	public GameState getState(){
		return state;
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
