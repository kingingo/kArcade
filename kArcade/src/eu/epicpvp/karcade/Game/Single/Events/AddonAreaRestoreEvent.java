package eu.epicpvp.karcade.Game.Single.Events;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import eu.epicpvp.karcade.Game.Multi.Addons.Evemts.BuildType;
import lombok.Getter;
import lombok.Setter;

public class AddonAreaRestoreEvent extends Event implements Cancellable{
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
	@Getter
	private Player player;
	@Getter
	private BuildType buildType;
	
	public AddonAreaRestoreEvent(Player player,BuildType buildType,Location location,BlockState replBlockState,String event){
		this.location=location;
		this.buildType=buildType;
		this.player=player;
		this.replacedState=replBlockState;
		this.event=event;
	}
	
	public AddonAreaRestoreEvent(Player player,BuildType buildType,Block block,String event){
		this(player,buildType,block.getLocation(),block.getState(),event);
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
        return handlers;
    }
}