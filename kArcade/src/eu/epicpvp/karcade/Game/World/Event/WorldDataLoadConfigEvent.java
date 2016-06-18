package eu.epicpvp.karcade.Game.World.Event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import eu.epicpvp.karcade.Game.World.GameMap;
import eu.epicpvp.karcade.Game.World.WorldData;
import lombok.Getter;
import lombok.Setter;

public class WorldDataLoadConfigEvent extends Event implements Cancellable{
	private static HandlerList handlers = new HandlerList();
	@Getter
	private WorldData worldData;
	@Getter
	private String[] line;
	@Getter
	private GameMap map;
	@Getter
	@Setter
	private boolean cancelled=false;
	
	public WorldDataLoadConfigEvent(WorldData worldData,GameMap map,String[] line){
		this.worldData=worldData;
		this.line=line;	
		this.map=map;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
        return handlers;
    }
}