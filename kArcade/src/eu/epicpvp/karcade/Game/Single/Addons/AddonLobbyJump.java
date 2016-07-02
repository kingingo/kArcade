package eu.epicpvp.karcade.Game.Single.Addons;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Wool;
import org.bukkit.plugin.java.JavaPlugin;

import dev.wolveringer.dataserver.gamestats.GameState;
import eu.epicpvp.karcade.Game.Single.SingleGame;
import eu.epicpvp.kcore.Listener.kListener;

public class AddonLobbyJump  extends kListener {
	private SingleGame game;
	public AddonLobbyJump(SingleGame game) {
		super((JavaPlugin) game.getPlugin(),"Lobby Jump");
		this.game = game;
	}
	
	@EventHandler
	public void a(PlayerMoveEvent e){
		if(game.getState() == GameState.LobbyPhase){
			Location from = e.getFrom().clone();
			Location to = e.getTo().clone();
			from.setY(0);
			to.setY(0);
			if(from.getBlock().getLocation().equals(to.getBlock().getLocation()))
				return;
			Block below = e.getPlayer().getLocation().clone().add(0, -1, 0).getBlock();
			if(below.getType() == Material.WOOL){
				below.setData((byte) ((below.getData()+1)%DyeColor.values().length));
				/*
				Wool wool = (Wool) below.getState().getData();
				wool.setColor(DyeColor.values()[(wool.getColor().ordinal()+1)%DyeColor.values().length]);
				below.getState().setData(wool);
				below.getState().update(true);
				*/
			}
		}
	}

}
