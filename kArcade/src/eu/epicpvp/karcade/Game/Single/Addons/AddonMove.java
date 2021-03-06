package eu.epicpvp.karcade.Game.Single.Addons;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffectType;

import eu.epicpvp.karcade.ArcadeManager;
import eu.epicpvp.kcore.Util.UtilPlayer;
import eu.epicpvp.kcore.Util.UtilServer;
import lombok.Getter;

public class AddonMove implements Listener{

	@Getter
	private ArcadeManager manager;
	private boolean move = true;
	
	public AddonMove(ArcadeManager manager){
		this.manager=manager;
		Bukkit.getPluginManager().registerEvents(this, manager.getInstance());
	}
	
	public void setMove(boolean move){
		this.move=move;
		
		if(this.move){
			for(Player player : UtilServer.getPlayers()){
				player.setWalkSpeed(0.2F);
				player.removePotionEffect(PotionEffectType.JUMP);
				player.setFoodLevel(20);
			}
		}else{
			for(Player player : UtilServer.getPlayers()){
				player.setWalkSpeed(0);
				UtilPlayer.addPotionEffect(player, PotionEffectType.JUMP, 60*60, 200);
				player.setFoodLevel(6);
			}
		}
	}
	
//	Location from;
//	Location to;
//	double x;
//	double z;
//	@EventHandler
//	public void Update(PlayerMoveEvent ev){
//		if(!notMove)return;
//		if(!movelist.contains(ev.getPlayer()))return;
//		from = ev.getFrom();
//		to = ev.getTo();
//		x = Math.floor(from.getX());
//		z = Math.floor(from.getZ());
//		if(Math.floor(to.getX())!=x||Math.floor(to.getZ())!=z){
//		    x+=.5;
//		    z+=.5;
//		    ev.getPlayer().teleport(new Location(from.getWorld(),x,from.getY(),z,from.getYaw(),from.getPitch()));
//		}
//	}
//	
}
