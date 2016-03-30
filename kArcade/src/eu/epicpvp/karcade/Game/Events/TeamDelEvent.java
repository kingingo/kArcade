package eu.epicpvp.karcade.Game.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import eu.epicpvp.kcore.Enum.Team;
import lombok.Getter;

public class TeamDelEvent  extends Event{
	private static HandlerList handlers = new HandlerList();
	@Getter
	private Player player;
	@Getter
	private Team team;
	
	public TeamDelEvent(Player player,Team team){
		this.player=player;
		this.team=team;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
        return handlers;
    }

}
