package eu.epicpvp.karcade.Game.Single.Addons;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import eu.epicpvp.karcade.Game.Single.SingleGame;
import eu.epicpvp.karcade.Game.Single.Games.TeamGame;
import eu.epicpvp.kcore.Enum.PlayerState;
import eu.epicpvp.kcore.Enum.Team;
import eu.epicpvp.kcore.Translation.TranslationHandler;
import eu.epicpvp.kcore.Update.UpdateType;
import eu.epicpvp.kcore.Update.Event.UpdateEvent;
import eu.epicpvp.kcore.Util.UtilDirection;
import lombok.Getter;
import lombok.Setter;

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
	@Setter
	private boolean msg=true;
	@Getter
	private SingleGame game;
	
	public AddonTargetNextPlayer(SingleGame game){
		this.game=game;
		Bukkit.getPluginManager().registerEvents(this, game.getManager().getInstance());
	}
	
	public AddonTargetNextPlayer(int radius,SingleGame game){
		this.radius=radius;
		this.game=game;
		Bukkit.getPluginManager().registerEvents(this, game.getManager().getInstance());
	}
	
	public AddonTargetNextPlayer(HashMap<Player,Team> TeamList,SingleGame game){
		this.TeamList=TeamList;
		this.game=game;
		Bukkit.getPluginManager().registerEvents(this, game.getManager().getInstance());
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
		list=game.getGameList().getPlayers(PlayerState.IN);
		for(Player p : list){
			target=null;
			dis=-1;
			for(Player p1 : list){
				if(game instanceof TeamGame){
					if( ((TeamGame)game).getTeam(p) == ((TeamGame)game).getTeam(p1))continue;
				}
				if(p.getWorld()!=p1.getWorld())continue;
				if(p==p1)continue;
				if(p.getLocation().distance(p1.getLocation())<=getRadius()){
					if(dis!=-1&&p.getLocation().distance(p1.getLocation())>dis)continue;
					dis=p.getLocation().distance(p1.getLocation());
					target=p1;
				}
			}
			if(target!=null){
				if(msg&&p.getItemInHand().getType()==Material.COMPASS)p.sendMessage(TranslationHandler.getText(p, "PREFIX_GAME",game.getType().name())+TranslationHandler.getText(p, "SPIELER_ENTFERNT_COMPASS",new String[]{target.getName(),String.valueOf( Math.round(target.getLocation().distance(p.getLocation())) )}));
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
