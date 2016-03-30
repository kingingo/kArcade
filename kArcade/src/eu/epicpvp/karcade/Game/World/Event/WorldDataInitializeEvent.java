package eu.epicpvp.karcade.Game.World.Event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import eu.epicpvp.karcade.Game.World.GameMap;
import eu.epicpvp.karcade.Game.World.WorldData;
import lombok.Getter;

public class WorldDataInitializeEvent extends Event{
	private static HandlerList handlers = new HandlerList();
	@Getter
	private WorldData worldData;
	@Getter
	private GameMap map;
	
	public WorldDataInitializeEvent(WorldData worldData,GameMap map){
		this.worldData=worldData;
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