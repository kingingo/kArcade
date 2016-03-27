package eu.epicpvp.karcade.Game.Single.Events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;

public class AddonEntityKingDeathEvent extends Event{
	private static HandlerList handlers = new HandlerList();
	@Getter
	private Entity entity;
	@Getter
	private Player killer;

	public AddonEntityKingDeathEvent(Entity entity,Player killer){
		this.killer=killer;
		this.entity=entity;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
        return handlers;
    }

}