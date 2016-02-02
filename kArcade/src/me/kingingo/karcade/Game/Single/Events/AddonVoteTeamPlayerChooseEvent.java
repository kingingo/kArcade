package me.kingingo.karcade.Game.Single.Events;

import lombok.Getter;
import me.kingingo.kcore.Enum.PlayerState;
import me.kingingo.kcore.Enum.Team;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AddonVoteTeamPlayerChooseEvent extends Event{
	private static HandlerList handlers = new HandlerList();
	@Getter
	private Player player;
	@Getter
	private Team team;
	@Getter
	private PlayerState state;

	public AddonVoteTeamPlayerChooseEvent(Player player,Team team,PlayerState state){
		this.player=player;
		this.team=team;
		this.state=state;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
        return handlers;
    }

}