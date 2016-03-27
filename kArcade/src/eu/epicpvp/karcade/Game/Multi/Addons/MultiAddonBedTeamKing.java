package eu.epicpvp.karcade.Game.Multi.Addons;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.material.Bed;

import lombok.Getter;
import eu.epicpvp.karcade.Game.Multi.MultiGames;
import eu.epicpvp.karcade.Game.Multi.Addons.Evemts.MultiAddonBedKingDeathEvent;
import eu.epicpvp.karcade.Game.Multi.Addons.Evemts.MultiGameAddonAreaRestoreEvent;
import eu.epicpvp.karcade.Game.Multi.Games.MultiGame;
import eu.epicpvp.karcade.Game.Multi.Games.MultiTeamGame;
import eu.epicpvp.kcore.Enum.PlayerState;
import eu.epicpvp.kcore.Enum.Team;
import eu.epicpvp.kcore.Listener.kListener;
import eu.epicpvp.kcore.Util.UtilBlock;
import eu.epicpvp.kcore.Util.UtilDirection;

public class MultiAddonBedTeamKing extends kListener {
	
	@Getter
	private MultiGames multiGames;
	@Getter
	private HashMap<MultiGame,ArrayList<Team>> games_boolean = new HashMap<>();
	@Getter
	private HashMap<MultiGame,HashMap<Team,Location[]>> games = new HashMap<>();
	
	public MultiAddonBedTeamKing(MultiGames multiGames){
		super(multiGames.getManager().getInstance(),"MultiAddonBedTeamKing");
		this.multiGames=multiGames;
		this.games=new HashMap<>();
	}
	
	public Team getBlockTeam(Team team){
		switch(team){
		case RED:return Team.SHEEP_RED;
		case BLUE:return Team.SHEEP_BLUE;
		case YELLOW:return Team.SHEEP_YELLOW;
		case GREEN:return Team.SHEEP_GREEN;
		case PINK:return Team.SHEEP_PINK;
		case GRAY:return Team.SHEEP_GRAY;
		case ORANGE:return Team.SHEEP_ORANGE;
		case PURPLE:return Team.SHEEP_PURPLE;
		default:
		return Team.SHEEP_RED;
		}
	}
	
	public void refreshMultiGame(MultiGame game,Team[] teams){
		this.games_boolean.get(game).clear();
		for(Team team : teams){
			this.games.get(game).get(team)[0].getBlock().setType(Material.AIR);
			this.games.get(game).get(team)[1].getBlock().setType(Material.AIR);
			
			UtilBlock.placeBed(this.games.get(game).get(team)[0], UtilDirection.getDirection(this.games.get(game).get(team)[0], this.games.get(game).get(team)[1]).getBlockFace());
			
			this.games_boolean.get(game).add(team);
		}
	}
	
	public void addMultiGame(MultiGame game,Team[] teams){
		this.games.put(game, new HashMap<>());
		this.games_boolean.put(game, new ArrayList<>());
		
		Location loc=null;
		BlockFace face=null;
		for(Team team : teams){
			loc=game.getWorldData().getLocs(game, getBlockTeam(team)).get(0);
			loc.getChunk().load();
			
			for(BlockFace f : BlockFace.values()){
				if(loc.getBlock().getRelative(f).getType()==Material.WOOL){
					face=f;
					break;
				}
			}
			UtilBlock.placeBed(loc, face);
			this.games.get(game).put(team, new Location[]{loc,loc.getBlock().getRelative(face).getLocation()});
			this.games_boolean.get(game).add(team);
		}
	}
	
	Team t;
	Location twin;
	@EventHandler(priority=EventPriority.NORMAL)
	public void Death(MultiGameAddonAreaRestoreEvent ev){
		if(ev.getGame()!=null&&this.games.containsKey(ev.getGame())&&ev.getLocation().getBlock().getType()==Material.BED_BLOCK){
			
			t = null;
			for(Team tt : this.games.get(ev.getGame()).keySet()){
				if(this.games.get(ev.getGame()).get(tt)[0].equals(ev.getLocation())){
					t=tt;
					twin=this.games.get(ev.getGame()).get(tt)[1];
					break;
				}else if(this.games.get(ev.getGame()).get(tt)[1].equals(ev.getLocation())){
					t=tt;
					twin=this.games.get(ev.getGame()).get(tt)[0];
					break;
				}
			}

			if(t==null ||  ev.getGame().getGameList().isPlayerState( ev.getPlayer() )!=PlayerState.IN 
					|| (ev.getGame() instanceof MultiTeamGame && ((MultiTeamGame)ev.getGame()).getTeamList().get(ev.getPlayer())==t)){
				ev.setBuild(false);
				ev.setCancelled(true);
				return;
			}
			MultiAddonBedKingDeathEvent e = new MultiAddonBedKingDeathEvent(ev.getGame(),t,((Bed)ev.getLocation().getBlock().getState().getData()),ev.getPlayer());
			Bukkit.getPluginManager().callEvent(e);
			
			ev.getLocation().getBlock().setType(Material.AIR);
			twin.getBlock().setType(Material.AIR);
			this.games_boolean.get(ev.getGame()).remove(t);
		}
	}
}
