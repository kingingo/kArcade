package me.kingingo.karcade.Events;

import me.kingingo.karcade.Enum.PlayerState;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

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
