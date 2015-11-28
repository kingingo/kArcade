package me.kingingo.karcade.Game.Single.addons;

import lombok.Getter;
import me.kingingo.karcade.kArcadeManager;
import me.kingingo.kcore.Util.UtilServer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class AddonMove implements Listener{

	@Getter
	private kArcadeManager manager;
	private boolean move = true;
	
	public AddonMove(kArcadeManager manager){
		this.manager=manager;
		Bukkit.getPluginManager().registerEvents(this, manager.getInstance());
	}
	
	public void setMove(boolean move){
		this.move=move;
		
		if(this.move){
			for(Player player : UtilServer.getPlayers())player.setWalkSpeed(0.2F);
		}else{
			for(Player player : UtilServer.getPlayers())player.setWalkSpeed(0);
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
