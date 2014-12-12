package me.kingingo.karcade.Game.World.Event;

import lombok.Getter;
import me.kingingo.karcade.Enum.Team;
import me.kingingo.karcade.Game.World.WorldData;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class WorldDataInitializeEvent extends Event{
	private static HandlerList handlers = new HandlerList();
	@Getter
	private WorldData worldData;
	
	public WorldDataInitializeEvent(WorldData worldData){
		this.worldData=worldData;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
        return handlers;
    }

}