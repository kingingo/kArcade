package me.kingingo.karcade.Game.Multi.Games.SkyWars1vs1;

import java.io.File;
import java.util.ArrayList;

import me.kingingo.karcade.Game.Multi.MultiGames;
import me.kingingo.karcade.Game.Multi.Addons.MultiGameArenaRestore;
import me.kingingo.karcade.Game.Multi.Events.MultiGameStartEvent;
import me.kingingo.karcade.Game.Multi.Events.MultiGameStateChangeEvent;
import me.kingingo.karcade.Game.Multi.Games.MultiGame;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.Team;
import me.kingingo.kcore.Util.UtilBG;
import me.kingingo.kcore.Util.UtilLocation;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class SkyWars1vs1 extends MultiGame{

	private ArrayList<BlockState> states;
	private MultiGameArenaRestore area;
	
	public SkyWars1vs1(MultiGames games,String Map,Location location,File file) {
		super(games,Map, location);
		this.states=new ArrayList<BlockState>();
//		this.area=new MultiGameArenaRestore(this, new Location(location.getWorld(),0,0,0), new Location(location.getWorld(),0,0,0));
		UtilBG.setHub("versus");
		setUpdateTo("versus");
		getWorldData().loadSchematic(this, location, file);
		
		for(Team team : getWorldData().getTeams(this).keySet()){
			for(Location loc : getWorldData().getTeams(this).get(team)){
				loc.getBlock().setType(Material.EMERALD_BLOCK);
				Log(UtilLocation.getLocString(loc));
			}
		}
		
//		UtilMap.makeQuadrat(states,getWorldData().getLocs(this, Team.RED).get(0).clone().add(0,20, 0), 2, 6, Material.STAINED_GLASS, Material.STAINED_GLASS_PANE);
		
		setDropItem(true);
		setPickItem(true);
		setDropItembydeath(true);
		setFoodlevelchange(true);
		setDamagePvP(false);
		setDamage(false);
		getEntityDamage().add(DamageCause.FALL);
		
	}

	@EventHandler
	public void lobby(MultiGameStateChangeEvent ev){
		if(ev.getGame()!=this)return;
		if(ev.getTo()==GameState.LobbyPhase){
			if(area!=null)area.restore();
			for(BlockState state : states)state.update(false);
			
			setDamagePvP(false);
			setDamage(false);
		}
	}
	
	@EventHandler
	public void start(MultiGameStartEvent ev){
		if(ev.getGame() == this){
			
			
			for(BlockState state : states)state.update(true);
		}
	}
}
