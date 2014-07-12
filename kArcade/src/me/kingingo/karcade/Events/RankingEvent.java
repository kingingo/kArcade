package me.kingingo.karcade.Events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RankingEvent extends Event{
	private static HandlerList handlers = new HandlerList();
	
	public RankingEvent(){
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
        return handlers;
    }

}
