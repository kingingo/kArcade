package eu.epicpvp.karcade.Game.Single.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.material.Bed;

import lombok.Getter;
import eu.epicpvp.kcore.Enum.Team;

public class AddonBedKingDeathEvent extends Event{
	private static HandlerList handlers = new HandlerList();
	@Getter
	private Bed bed;
	@Getter
	private Player killer;
	@Getter
	private Team team;

	public AddonBedKingDeathEvent(Team team,Bed bed,Player killer){
		this.killer=killer;
		this.team=team;
		this.bed=bed;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
        return handlers;
    }

}