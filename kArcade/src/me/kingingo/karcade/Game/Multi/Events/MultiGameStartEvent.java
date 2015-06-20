package me.kingingo.karcade.Game.Multi.Events;

import lombok.Getter;
import me.kingingo.karcade.Game.Multi.Games.MultiGame;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MultiGameStartEvent extends Event{
	private static HandlerList handlers = new HandlerList();
	@Getter
	private MultiGame game;
	
	public MultiGameStartEvent(MultiGame game){
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
