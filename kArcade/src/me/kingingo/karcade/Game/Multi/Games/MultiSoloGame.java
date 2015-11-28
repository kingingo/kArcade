package me.kingingo.karcade.Game.Multi.Games;

import java.util.List;

import me.kingingo.karcade.Game.Multi.MultiGames;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.GameStateChangeReason;
import me.kingingo.kcore.Enum.PlayerState;
import me.kingingo.kcore.Kit.Shop.Events.KitShopPlayerDeleteEvent;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilMath;
import me.kingingo.kcore.Util.UtilServer;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class MultiSoloGame extends MultiGame{

	public MultiSoloGame(MultiGames games, String Map, Location pasteLocation) {
		super(games, Map, pasteLocation);
	}
	
	@EventHandler
	public void SpectaterAndRespawn(PlayerRespawnEvent ev){
		if(getGameList().isPlayerState(ev.getPlayer())==PlayerState.OUT){
			SetSpectator(ev,ev.getPlayer());
		}
	}
	
	@EventHandler
	public void Quit(PlayerQuitEvent ev){
		if(isState(GameState.Restart)||isState(GameState.LobbyPhase))return;
		getGameList().addPlayer(ev.getPlayer(), PlayerState.OUT);
		if(getGameList().getPlayers(PlayerState.IN).size()<=1){
			setState(GameState.Restart,GameStateChangeReason.LAST_PLAYER);
		}
	}
	
	public void SetSpectator(PlayerRespawnEvent ev,Player player){
	    getGames().getManager().Clear(player);
	    List<Player> l = getGameList().getPlayers(PlayerState.IN);
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
