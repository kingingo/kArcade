package me.kingingo.karcade.Game.Games;

import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Enum.GameStateChangeReason;
import me.kingingo.karcade.Enum.PlayerState;
import me.kingingo.karcade.Game.Game;
import me.kingingo.karcade.Game.addons.AddonSpecCompass;
import me.kingingo.karcade.Game.addons.AddonSpectator;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Util.UtilMath;
import me.kingingo.kcore.Util.UtilServer;

import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class SoloGame extends Game{
	
	AddonSpectator spec=null;
	
	public SoloGame(kArcadeManager manager) {
		super(manager);
	}
	
	@EventHandler
	public void Quit(PlayerQuitEvent ev){
		if(getManager().isState(GameState.Restart)||getManager().isState(GameState.LobbyPhase))return;
		getGameList().addPlayer(ev.getPlayer(), PlayerState.OUT);
		if(getGameList().getPlayers(PlayerState.IN).size()<1){
			getManager().setState(GameState.Restart,GameStateChangeReason.LAST_PLAYER);
		}
	}
	
	@EventHandler
	public void Funk(PlayerRespawnEvent ev){
		if(getGameList().isPlayerState(ev.getPlayer())==PlayerState.OUT){
			SetSpectator(ev.getPlayer());
		}
	}
	
	public void SetSpectator(Player player){
		if(spec==null)spec=new AddonSpectator(getManager());
	    getManager().Clear(player);
	    player.teleport(UtilServer.getPlayers()[UtilMath.RandomInt(UtilServer.getPlayers().length, 0)].getLocation().add(0.0D,3.5D,0.0D));
	    player.setGameMode(GameMode.CREATIVE);
	    player.setFlying(true);
	    player.setFlySpeed(0.1F);
	    ((CraftPlayer)player).getHandle().k = false;
	    if(getCompass()==null)setCompass(new AddonSpecCompass(getManager()));
	    player.getInventory().addItem(getCompass().getCompassItem());
	    if(getGameList().getPlayers(PlayerState.IN).size()<1){
			getManager().setState(GameState.Restart,GameStateChangeReason.LAST_PLAYER);
		}
	  }
	
}
