package eu.epicpvp.karcade.Game.Events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import eu.epicpvp.karcade.Game.Single.SingleGame;

public class GamePreStartEvent  extends Event{
	private static HandlerList handlers = new HandlerList();
	@Getter
	private SingleGame game;
	
	public GamePreStartEvent(SingleGame game){
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
