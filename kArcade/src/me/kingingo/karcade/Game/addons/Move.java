package me.kingingo.karcade.Game.addons;

import java.util.ArrayList;

import lombok.Getter;
import me.kingingo.karcade.kArcadeManager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class Move implements Listener{

	@Getter
	private kArcadeManager manager;
	private boolean notMove = false;
	private ArrayList<Player> movelist = new ArrayList<>();
	
	public Move(kArcadeManager manager){
		this.manager=manager;
		Bukkit.getPluginManager().registerEvents(this, manager.getInstance());
	}
	
	public void setnotMove(boolean move){
		notMove=move;
		
		if(!move){
			this.movelist.clear();
		}
	}
	
	public void setnotMove(boolean move,ArrayList<Player> list){
		notMove=move;
		
		if(move){
			this.movelist=list;
		}else{
			this.movelist.clear();
		}
	}
	
	Location from;
	Location to;
	double x;
	double z;
	@EventHandler
	public void Update(PlayerMoveEvent ev){
		if(!notMove)return;
		if(!movelist.contains(ev.getPlayer()))return;
		from = ev.getFrom();
		to = ev.getTo();
		x = Math.floor(from.getX());
		z = Math.floor(from.getZ());
		if(Math.floor(to.getX())!=x||Math.floor(to.getZ())!=z){
		    x+=.5;
		    z+=.5;
		    ev.getPlayer().teleport(new Location(from.getWorld(),x,from.getY(),z,from.getYaw(),from.getPitch()));
		}
	}
	
}
