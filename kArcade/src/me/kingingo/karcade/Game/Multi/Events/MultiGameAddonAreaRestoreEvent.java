package me.kingingo.karcade.Game.Multi.Events;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MultiGameAddonAreaRestoreEvent extends Event implements Cancellable{
	private static HandlerList handlers = new HandlerList();
	@Getter
	private Location location;
	@Getter
	private BlockState replacedState;
	@Getter
	private String event;
	@Getter
	@Setter
	private boolean cancelled=false;
	@Getter
	@Setter
	private boolean build=true;
	
	public MultiGameAddonAreaRestoreEvent(Location location,BlockState replBlockState,String event){
		this.location=location;
		this.replacedState=replBlockState;
		this.event=event;
	}
	
	public MultiGameAddonAreaRestoreEvent(Block block,String event){
		this(block.getLocation(),block.getState(),event);
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
        return handlers;
    }
}