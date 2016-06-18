package eu.epicpvp.karcade.Game.Single.Addons.VoteHandler;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.BrewItem;
import lombok.Getter;
import lombok.Setter;

public class PlayerVoteEvent extends Event {
	private static HandlerList handlers = new HandlerList();
	@Getter
	private Vote vote;
	@Getter
	private Player player;

	public PlayerVoteEvent(Player player,Vote vote){
		this.player=player;
		this.vote=vote;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
        return handlers;
    }

}