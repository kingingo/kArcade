package eu.epicpvp.karcade.Game.Single.Games;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import dev.wolveringer.dataserver.gamestats.GameState;
import eu.epicpvp.karcade.kArcadeManager;
import eu.epicpvp.karcade.Game.Single.SingleGame;
import eu.epicpvp.karcade.Game.Single.Addons.AddonSpecCompass;
import eu.epicpvp.karcade.Game.Single.Addons.AddonSpectator;
import eu.epicpvp.kcore.Enum.GameStateChangeReason;
import eu.epicpvp.kcore.Enum.PlayerState;
import eu.epicpvp.kcore.Kit.Shop.Events.KitShopPlayerDeleteEvent;
import eu.epicpvp.kcore.Util.UtilMath;
import eu.epicpvp.kcore.Util.UtilServer;

public class SoloGame extends SingleGame{
	
	AddonSpectator spec=null;
	
	public SoloGame(kArcadeManager manager) {
		super(manager);
	}
	
	@EventHandler
	public void Quit(PlayerQuitEvent ev){
		if(getGameList().getPlayers().containsKey(ev.getPlayer())){
			if(isState(GameState.Restart)||isState(GameState.LobbyPhase))return;
			getGameList().addPlayer(ev.getPlayer(), PlayerState.SPECTATOR);
			if(getGameList().getPlayers(PlayerState.INGAME).size()<=1){
				setState(GameState.Restart,GameStateChangeReason.LAST_PLAYER);
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void SpectJoin(PlayerJoinEvent ev){
		if(getState()!=GameState.LobbyPhase){
			getGameList().addPlayer(ev.getPlayer(), PlayerState.SPECTATOR);
			SetSpectator(null,ev.getPlayer());
		}
	}
	
	@EventHandler
	public void Funk(PlayerRespawnEvent ev){
		if(getGameList().isPlayerState(ev.getPlayer())==PlayerState.SPECTATOR){
			SetSpectator(ev,ev.getPlayer());
		}
	}
	
	public void SetSpectator(PlayerRespawnEvent ev,Player player){
		if(spec==null)spec=new AddonSpectator(this);
	    getManager().clear(player);
	    List<Player> l = getGameList().getPlayers(PlayerState.INGAME);
	    if(l.size()>1){
	    	if(ev==null){
		    	player.teleport(l.get(UtilMath.r(l.size())).getLocation().add(0.0D,3.5D,0.0D));
	    	}else{
	    		ev.setRespawnLocation(l.get(UtilMath.r(l.size())).getLocation().add(0.0D,3.5D,0.0D));
	    	}
	    }else{
	    	if(ev==null){
	    		player.teleport(getManager().getLobby());
	    	}else{
	    		ev.setRespawnLocation(getManager().getLobby());
	    	}
	    	setState(GameState.Restart,GameStateChangeReason.LAST_PLAYER);
	    }
	    for(Player p : UtilServer.getPlayers()){
	    	p.hidePlayer(player);
	    }
	    player.setGameMode(GameMode.SPECTATOR);
	    player.setFlying(true);
	    player.setFlySpeed(0.1F);
	    ((CraftPlayer)player).getHandle().k = false;
	    if(getCompass()==null)setCompass(new AddonSpecCompass(this));
	    player.getInventory().addItem(getCompass().getCompassItem());
	    Bukkit.getPluginManager().callEvent(new KitShopPlayerDeleteEvent(player));
	    getMoney().save(player);
	  }
	
}
