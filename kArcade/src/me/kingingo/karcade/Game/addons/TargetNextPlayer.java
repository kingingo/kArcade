package me.kingingo.karcade.Game.addons;

import java.util.ArrayList;
import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;
import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Enum.PlayerState;
import me.kingingo.karcade.Enum.Team;
import me.kingingo.karcade.Game.GameList;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TargetNextPlayer implements Listener {

	private HashMap<Player,Team> TeamList;
	@Getter
	@Setter
	private int radius=30;
	@Getter
	@Setter
	private boolean aktiv=false;
	@Getter
	GameList GL;
	
	public TargetNextPlayer(kArcadeManager Manager){
		Bukkit.getPluginManager().registerEvents(this, Manager.getInstance());
	}
	
	public TargetNextPlayer(int radius,kArcadeManager Manager){
		this.radius=radius;
		Bukkit.getPluginManager().registerEvents(this, Manager.getInstance());
	}
	
	public TargetNextPlayer(HashMap<Player,Team> TeamList,GameList GL,kArcadeManager Manager){
		this.TeamList=TeamList;
		this.GL=GL;
		Bukkit.getPluginManager().registerEvents(this, Manager.getInstance());
	}
	
	public TargetNextPlayer(HashMap<Player,Team> TeamList,kArcadeManager Manager){
		this.TeamList=TeamList;
		Bukkit.getPluginManager().registerEvents(this, Manager.getInstance());
	}
	
	ArrayList<Player> list;
	Player target=null;
	Location l;
	double dis=-1;
	@EventHandler
	public void Update(UpdateEvent ev){
		if(UpdateType.FAST!=ev.getType())return;
		if(!isAktiv())return;
		list=GL.getPlayers(PlayerState.IN);
		for(Player p : list){
			target=null;
			dis=-1;
			for(Player p1 : list){
				if(p.getWorld()!=p1.getWorld())continue;
				if(p==p1)continue;
				if(p.getLocation().distance(p1.getLocation())<=getRadius()&&dis!=-1&&target!=null&&p.getLocation().distance(p1.getLocation())<dis){
					dis=p.getLocation().distance(p1.getLocation());
					target=p1;
				}
			}
			if(target!=null){
				p.setCompassTarget(target.getLocation());
			}else{
				l = p.getCompassTarget();
				if(l.getYaw()==360){
					l.setYaw(0);
				}else{
					l.setYaw(l.getYaw()+10);
				}
				p.setCompassTarget(l);
			}
		}
	}
	
}
