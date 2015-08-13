package me.kingingo.karcade.Game.Multi.Events;

import lombok.Getter;
import me.kingingo.karcade.Game.Multi.Games.MultiGame;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.GameStateChangeReason;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MultiGameStateChangeEvent extends Event implements Cancellable{
	private static HandlerList handlers = new HandlerList();
	private GameState from;
	private GameState to;
	private boolean cancel=false;
	@Getter
	private GameStateChangeReason reason;
	@Getter
	private MultiGame game;
	
	public MultiGameStateChangeEvent(MultiGame game,GameState from,GameState to,GameStateChangeReason reason){
		this.from=from;
		this.to=to;
		this.reason=reason;
		this.game=game;
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
