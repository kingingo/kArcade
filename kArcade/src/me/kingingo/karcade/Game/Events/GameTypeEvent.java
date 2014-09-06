package me.kingingo.karcade.Game.Events;

import me.kingingo.kcore.Enum.GameType;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameTypeEvent  extends Event{
	private static HandlerList handlers = new HandlerList();
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
