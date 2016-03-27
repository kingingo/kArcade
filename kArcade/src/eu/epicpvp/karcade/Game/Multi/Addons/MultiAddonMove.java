package eu.epicpvp.karcade.Game.Multi.Addons;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffectType;

import lombok.Getter;
import eu.epicpvp.karcade.Game.Multi.Events.MultiGamePlayerJoinEvent;
import eu.epicpvp.karcade.Game.Multi.Games.MultiGame;
import eu.epicpvp.kcore.Listener.kListener;
import eu.epicpvp.kcore.Util.UtilPlayer;

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
			for(Player player : game.getGameList().getPlayers().keySet()){
				player.setWalkSpeed(0.2F);
				player.removePotionEffect(PotionEffectType.JUMP);
				player.setFoodLevel(20);
			}
		}else{
			for(Player player : game.getGameList().getPlayers().keySet()){
				player.setWalkSpeed(0);
				UtilPlayer.addPotionEffect(player, PotionEffectType.JUMP, 60*60, 200);
				player.setFoodLevel(6);
			}
		}
	}
	
	@EventHandler
	public void Move(MultiGamePlayerJoinEvent ev){
		if(this.game!=ev.getGame())return;
		//Pr§ft ob dieser Spieler f§r die Arena angemeldet ist.
		if(this.game.getGameList().getPlayers().containsKey(ev.getPlayer())){
			//F§gt Spieler zu AddonMove hinzu
			if(this.move){
				ev.getPlayer().setWalkSpeed(0.2F);
				ev.getPlayer().removePotionEffect(PotionEffectType.JUMP);
				ev.getPlayer().setFoodLevel(20);
			}else{
				ev.getPlayer().setWalkSpeed(0);
				UtilPlayer.addPotionEffect(ev.getPlayer(), PotionEffectType.JUMP, 60*60, 200);
				ev.getPlayer().setFoodLevel(6);
			}
		}
	}
}
