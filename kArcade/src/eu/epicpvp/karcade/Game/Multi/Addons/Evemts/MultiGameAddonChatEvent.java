package eu.epicpvp.karcade.Game.Multi.Addons.Evemts;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import lombok.Setter;

public class MultiGameAddonChatEvent extends Event implements Cancellable{
	private static HandlerList handlers = new HandlerList();
	@Getter
	private String message;
	@Getter
	private Player player;
	@Getter
	@Setter
	private boolean cancelled=false;
	
	public MultiGameAddonChatEvent(Player player,String message){
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