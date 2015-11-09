package me.kingingo.karcade.Game.Multi.Events;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MutliGameAddonChatEvent extends Event implements Cancellable{
	private static HandlerList handlers = new HandlerList();
	@Getter
	private String message;
	@Getter
	private Player player;
	@Getter
	@Setter
	private boolean cancelled=false;
	
	public MutliGameAddonChatEvent(Player player,String message){
		this.player=player;
		this.message=message;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
        return handlers;
    }
}