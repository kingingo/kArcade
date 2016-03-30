package eu.epicpvp.karcade.Game.Events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import dev.wolveringer.dataserver.gamestats.GameType;
import lombok.Getter;

public class GameTypeEvent  extends Event{
	private static HandlerList handlers = new HandlerList();
	@Getter
	private GameType type;
	
	public GameTypeEvent(GameType type){
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
