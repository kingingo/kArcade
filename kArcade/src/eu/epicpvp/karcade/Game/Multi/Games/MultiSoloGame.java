package eu.epicpvp.karcade.Game.Multi.Games;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import dev.wolveringer.dataserver.gamestats.GameState;
import eu.epicpvp.karcade.Game.Multi.MultiGames;
import eu.epicpvp.kcore.Enum.GameStateChangeReason;
import eu.epicpvp.kcore.Enum.PlayerState;
import eu.epicpvp.kcore.Kit.Shop.Events.KitShopPlayerDeleteEvent;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.kcore.Util.UtilMath;
import eu.epicpvp.kcore.Util.UtilServer;

public class MultiSoloGame extends MultiGame{

	public MultiSoloGame(MultiGames games, String Map, Location pasteLocation) {
		super(games, Map, pasteLocation);
	}
	
	@EventHandler
	public void SpectaterAndRespawn(PlayerRespawnEvent ev){
		if(getGameList().isPlayerState(ev.getPlayer())==PlayerState.SPECTATOR){
			SetSpectator(ev,ev.getPlayer());
		}
	}
	
	@EventHandler
	public void Quit(PlayerQuitEvent ev){
		if(isState(GameState.Restart)||isState(GameState.LobbyPhase))return;
		getGameList().addPlayer(ev.getPlayer(), PlayerState.SPECTATOR);
		if(getGameList().getPlayers(PlayerState.INGAME).size()<=1){
			setState(GameState.Restart,GameStateChangeReason.LAST_PLAYER);
		}
	}
	
	public void SetSpectator(PlayerRespawnEvent ev,Player player){
	    getGames().getManager().clear(player);
	    List<Player> l = getGameList().getPlayers(PlayerState.INGAME);
	    if(l.size()>1){
	    	if(ev==null){
		    	player.teleport(l.get(UtilMath.r(l.size())).getLocation().add(0.0D,3.5D,0.0D));
	    	}else{
	    		ev.setRespawnLocation(l.get(UtilMath.r(l.size())).getLocation().add(0.0D,3.5D,0.0D));
	    	}
	    }else{
	    	if(ev==null){
	    		player.teleport(getGames().getManager().getLobby());
	    	}else{
	    		ev.setRespawnLocation(getGames().getManager().getLobby());
	    	}
	    	setState(GameState.Restart,GameStateChangeReason.LAST_PLAYER);
	    }
	    for(Player p : UtilServer.getPlayers()){
	    	p.hidePlayer(player);
	    }
	    player.getInventory().setItem(8,UtilItem.RenameItem(new ItemStack(385), "§aZurück zur Lobby"));
	    player.setGameMode(GameMode.CREATIVE);
	    player.setFlying(true);
	    player.setFlySpeed(0.1F);
	    ((CraftPlayer)player).getHandle().k = false;
	    Bukkit.getPluginManager().callEvent(new KitShopPlayerDeleteEvent(player));
	  }

}
