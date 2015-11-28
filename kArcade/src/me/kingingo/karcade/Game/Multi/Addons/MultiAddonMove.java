package me.kingingo.karcade.Game.Multi.Addons;

import lombok.Getter;
import me.kingingo.karcade.Game.Multi.Events.MultiGamePlayerJoinEvent;
import me.kingingo.karcade.Game.Multi.Games.MultiGame;
import me.kingingo.kcore.Listener.kListener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class MultiAddonMove extends kListener{

	@Getter
	private MultiGame game;
	private boolean move = true;
	
	public MultiAddonMove(MultiGame game){
		super(game.getGames().getManager().getInstance(),"MultiAddonMove:"+game.getArena());
		this.game=game;
	}
	
	public void setMove(boolean move){
		this.move=move;
		
		if(this.move){
			for(Player player : game.getGameList().getPlayers().keySet())player.setWalkSpeed(0.2F);
		}else{
			for(Player player : game.getGameList().getPlayers().keySet())player.setWalkSpeed(0);
		}
	}
	
	@EventHandler
	public void Move(MultiGamePlayerJoinEvent ev){
		if(this.game!=ev.getGame())return;
		//Prüft ob dieser Spieler für die Arena angemeldet ist.
		if(this.game.getGameList().getPlayers().containsKey(ev.getPlayer())){
			//Fügt Spieler zu AddonMove hinzu
			if(this.move){
				ev.getPlayer().setWalkSpeed(0.2F);
			}else{
				ev.getPlayer().setWalkSpeed(0);
			}
		}
	}
}
