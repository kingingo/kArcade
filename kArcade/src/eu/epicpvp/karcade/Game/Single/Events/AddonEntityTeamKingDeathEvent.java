package eu.epicpvp.karcade.Game.Single.Events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import eu.epicpvp.kcore.Enum.Team;
import lombok.Getter;

public class AddonEntityTeamKingDeathEvent extends Event{
	private static HandlerList handlers = new HandlerList();
	@Getter
	private Entity entity;
	@Getter
	private Team team;
	@Getter
	private Player killer;

	public AddonEntityTeamKingDeathEvent(Team team,Entity entity,Player killer){
		this.team=team;
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