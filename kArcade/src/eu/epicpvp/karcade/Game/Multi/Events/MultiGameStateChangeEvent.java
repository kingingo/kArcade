package eu.epicpvp.karcade.Game.Multi.Events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import dev.wolveringer.dataserver.gamestats.GameState;
import eu.epicpvp.karcade.Game.Multi.Games.MultiGame;
import eu.epicpvp.kcore.Enum.GameStateChangeReason;
import lombok.Getter;
import lombok.Setter;

public class MultiGameStateChangeEvent extends Event implements Cancellable{
	private static HandlerList handlers = new HandlerList();
	@Getter
	private GameState from;
	@Getter
	private GameState to;
	@Getter
	@Setter
	private boolean cancelled=false;
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
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
        return handlers;
    }
}
