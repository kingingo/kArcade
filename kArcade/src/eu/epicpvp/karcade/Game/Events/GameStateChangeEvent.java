package eu.epicpvp.karcade.Game.Events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import dev.wolveringer.dataserver.gamestats.GameState;
import eu.epicpvp.karcade.Game.Game;
import eu.epicpvp.kcore.Enum.GameStateChangeReason;
import lombok.Getter;
import lombok.Setter;

public class GameStateChangeEvent extends Event implements Cancellable{
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
	private Game game;
	
	public GameStateChangeEvent(GameState from,GameState to,Game game,GameStateChangeReason reason){
		this.from=from;
		this.game=game;
		this.to=to;
		this.reason=reason;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
        return handlers;
    }
}
