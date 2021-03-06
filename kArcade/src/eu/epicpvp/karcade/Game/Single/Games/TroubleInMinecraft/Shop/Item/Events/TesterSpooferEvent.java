package eu.epicpvp.karcade.Game.Single.Games.TroubleInMinecraft.Shop.Item.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;

public class TesterSpooferEvent extends Event implements Cancellable{
	private static HandlerList handlers = new HandlerList();
	@Getter
	private Player player;
	private boolean cancel=false;
	
	public TesterSpooferEvent(Player player){
		this.player=player;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
        return handlers;
    }

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean arg0) {
		cancel=arg0;
	}

}

