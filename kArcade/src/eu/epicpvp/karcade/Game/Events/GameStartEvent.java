package eu.epicpvp.karcade.Game.Events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import dev.wolveringer.dataserver.gamestats.GameType;
import lombok.Getter;
import lombok.Setter;

public class GameStartEvent  extends Event implements Cancellable{
	private static HandlerList handlers = new HandlerList();
	private GameType type;
	@Getter
	@Setter
	private boolean cancelled=false;
	
	public GameStartEvent(GameType type){
		this.type=type;
	}
	
	public GameType getGameType(){
		return this.type;
	}
	
	public String getGameTypeName(){
		return this.type.getTyp();
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
        return handlers;
    }

}
