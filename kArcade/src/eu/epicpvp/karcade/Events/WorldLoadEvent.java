package eu.epicpvp.karcade.Events;

import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class WorldLoadEvent extends Event{
	private static HandlerList handlers = new HandlerList();
	private World w;
	
	public WorldLoadEvent(World w){
		this.w=w;
	}
	
	public World getWorld(){
		return this.w;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
        return handlers;
    }

}
