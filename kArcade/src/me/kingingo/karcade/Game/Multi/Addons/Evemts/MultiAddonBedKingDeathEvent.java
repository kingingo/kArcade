package me.kingingo.karcade.Game.Multi.Addons.Evemts;

import lombok.Getter;
import me.kingingo.karcade.Game.Multi.Games.MultiGame;
import me.kingingo.kcore.Enum.Team;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.material.Bed;

public class MultiAddonBedKingDeathEvent extends Event{
	private static HandlerList handlers = new HandlerList();
	@Getter
	private Bed bed;
	@Getter
	private Player killer;
	@Getter
	private Team team;
	@Getter
	private MultiGame game;

	public MultiAddonBedKingDeathEvent(MultiGame game, Team team,Bed bed,Player killer){
		this.killer=killer;
		this.team=team;
		this.bed=bed;
		this.game=game;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
        return handlers;
    }

}