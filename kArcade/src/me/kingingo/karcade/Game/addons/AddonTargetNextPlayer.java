package me.kingingo.karcade.Game.addons;

import java.util.ArrayList;
import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;
import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Enum.PlayerState;
import me.kingingo.karcade.Enum.Team;
import me.kingingo.karcade.Game.GameList;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.UtilDirection;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AddonTargetNextPlayer implements Listener {

	HashMap<Player,UtilDirection> pl = new HashMap<>();
	private HashMap<Player,Team> TeamList;
	@Getter
	@Setter
	private int radius=30;
	@Getter
	@Setter
	private boolean aktiv=false;
	@Getter
	private kArcadeManager manager;
	
	public AddonTargetNextPlayer(kArcadeManager Manager){
		this.manager=Manager;
		Bukkit.getPluginManager().registerEvents(this, Manager.getInstance());
	}
	
	public AddonTargetNextPlayer(int radius,kArcadeManager Manager){
		this.radius=radius;
		this.manager=Manager;
		Bukkit.getPluginManager().registerEvents(this, Manager.getInstance());
	}
	
	public AddonTargetNextPlayer(HashMap<Player,Team> TeamList,kArcadeManager Manager){
		this.TeamList=TeamList;
		this.manager=Manager;
		Bukkit.getPluginManager().registerEvents(this, Manager.getInstance());
	}
	
	ArrayList<Player> list;
	Player target=null;
	Location l;
	UtilDirection face;
	double dis=-1;
	@EventHandler
	public void Update(UpdateEvent ev){
		if(UpdateType.FAST!=ev.getType())return;
		if(isAktiv()==false)return;
		list=getManager().getGame().getGameList().getPlayers(PlayerState.IN);
		for(Player p : list){
			target=null;
			dis=-1;
			for(Player p1 : list){
				if(p.getWorld()!=p1.getWorld())continue;
				if(p==p1)continue;
				if(p.getLocation().distance(p1.getLocation())<=getRadius()){
					if(dis!=-1&&p.getLocation().distance(p1.getLocation())>dis)continue;
					dis=p.getLocation().distance(p1.getLocation());
					target=p1;
				}
			}
			if(target!=null){
				if(p.getItemInHand().getType()==Material.COMPASS)p.sendMessage(Text.PREFIX_GAME.getText(getManager().getGame().getType().name())+Text.SPIELER_ENTFERNT_COMPASS.getText(new String[]{target.getName(),String.valueOf( Math.round(target.getLocation().distance(p.getLocation())) )}));
				p.setCompassTarget(target.getLocation());
			}else{
				if(!pl.containsKey(p)){
				pl.put(p, UtilDirection.NORTH);
				}
			
			face=pl.get(p);
			face=face.nextDirection();
			pl.remove(p);
			pl.put(p, face);
			p.setCompassTarget(face.get(p.getLocation()));
			}
		}
	}
	
}
