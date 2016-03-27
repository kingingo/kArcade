package eu.epicpvp.karcade.Game.Multi.Addons.Evemts;

import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import lombok.Setter;
import eu.epicpvp.karcade.Game.Multi.Games.MultiGame;

public class MultiGameAddonAreaRestoreExplosionEvent extends Event implements Cancellable{
	private static HandlerList handlers = new HandlerList();
	@Getter
	private List<Block> blocks;
	@Getter
	@Setter
	private boolean cancelled=false;
	@Getter
	@Setter
	private boolean build=true;
	@Getter
	@Setter
	private MultiGame game;
	
	public MultiGameAddonAreaRestoreExplosionEvent(List<Block> blocks){
		this.blocks=blocks;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
        return handlers;
    }
}