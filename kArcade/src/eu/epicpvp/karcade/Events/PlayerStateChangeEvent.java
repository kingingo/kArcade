package eu.epicpvp.karcade.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import eu.epicpvp.kcore.Enum.PlayerState;

public class PlayerStateChangeEvent extends Event{
	private static HandlerList handlers = new HandlerList();
	private Player p;
	private PlayerState ps;
	
	public PlayerStateChangeEvent(Player p,PlayerState ps){
		this.p=p;
		this.ps=ps;
	}
	
	public PlayerState getPlayerState(){
		return ps;
	}
	
	public Player getPlayer(){
		return this.p;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
        return handlers;
    }

}
