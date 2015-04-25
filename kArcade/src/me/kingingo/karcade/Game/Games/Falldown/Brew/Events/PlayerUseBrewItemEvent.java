package me.kingingo.karcade.Game.Games.Falldown.Brew.Events;

import lombok.Getter;
import lombok.Setter;
import me.kingingo.karcade.Game.Games.Falldown.Brew.BrewItem;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerUseBrewItemEvent extends Event implements Cancellable{
	private static HandlerList handlers = new HandlerList();
	@Getter
	private BrewItem brewItem;
	@Getter
	private Player player;
	@Getter
	@Setter
	private boolean cancelled;

	public PlayerUseBrewItemEvent(Player player,BrewItem brewItem){
		this.player=player;
		this.brewItem=brewItem;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
        return handlers;
    }

}