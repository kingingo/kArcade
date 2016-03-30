package eu.epicpvp.karcade.Game.Multi.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import eu.epicpvp.karcade.Game.Multi.Games.MultiGame;
import lombok.Getter;
import lombok.Setter;

public class MultiGamePlayerJoinEvent extends Event implements Cancellable{
	private static HandlerList handlers = new HandlerList();
	@Getter
	private Player player;
	@Getter
	@Setter
	private boolean cancelled=false;
	@Getter
	@Setter
	private MultiGame game;
	
	public MultiGamePlayerJoinEvent(Player player,MultiGame game){
		this.player=player;
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
