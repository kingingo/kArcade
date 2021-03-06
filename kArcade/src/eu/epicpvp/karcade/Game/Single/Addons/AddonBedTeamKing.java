package eu.epicpvp.karcade.Game.Single.Addons;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.material.Bed;

import eu.epicpvp.karcade.ArcadeManager;
import eu.epicpvp.karcade.Game.Single.SingleGame;
import eu.epicpvp.karcade.Game.Single.Events.AddonBedKingDeathEvent;
import eu.epicpvp.karcade.Game.Single.Games.TeamGame;
import eu.epicpvp.kcore.Enum.PlayerState;
import eu.epicpvp.kcore.Enum.Team;
import eu.epicpvp.kcore.Util.UtilBlock;
import lombok.Getter;

public class AddonBedTeamKing implements Listener {
	
	@Getter
	private ArcadeManager manager;;
	@Getter
	private HashMap<Team,Block> teams = new HashMap<>();
	@Getter
	private TeamGame team;
	
	public AddonBedTeamKing(ArcadeManager manager,Team[] teams,TeamGame team){
		this.manager=manager;
		this.team=team;
		BlockFace face = null;
		Location loc = null;
		for(Team t : teams){
			loc=((SingleGame)manager.getGame()).getWorldData().getSpawnLocations(getBlockTeam(t)).get(0);
			loc.getWorld().loadChunk(loc.getWorld().getChunkAt(loc));
		
			for(BlockFace f : BlockFace.values()){
				if(loc.getBlock().getRelative(f).getType()==Material.WOOL){
					face=f;
					break;
				}
			}
			
			UtilBlock.placeBed(loc, face);
			this.teams.put(t, loc.getBlock());
		}
		Bukkit.getPluginManager().registerEvents(this, manager.getInstance());
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
	
	public Block getBlock(Team t){
		return teams.get(t);
	}
	
	public Team get(Block e){
		for(Team team : getTeams().keySet()){
			if(getTeams().get(team).getLocation()==e.getLocation())return team;
		}
		return null;
	}
	
	public boolean haveBed(Team team){
		return teams.containsKey(team);
	}
	
	@EventHandler
	public void drop(ItemSpawnEvent ev){
		if(ev.getEntity().getItemStack().getType() == Material.BED){
			ev.getEntity().remove();
		}
	}
	
	Team t;
	Location twin;
	@EventHandler
	public void Death(BlockBreakEvent ev){
		if(ev.getBlock().getType()==Material.BED_BLOCK){
			t = null;
			twin=UtilBlock.getTwinLocation( ev.getBlock() );
			for(Team tt : getTeams().keySet()){
				if(getTeams().get(tt).getLocation().equals(ev.getBlock().getLocation()) || getTeams().get(tt).getLocation().equals(twin)){
					t=tt;
					break;
				}
			}
			
			if(t==null||getTeam().getTeam( ev.getPlayer() )==t ||  team.getGameList().isPlayerState( ev.getPlayer() )!=PlayerState.INGAME){
				ev.setCancelled(true);
				return;
			}
			AddonBedKingDeathEvent e = new AddonBedKingDeathEvent(t,((Bed)ev.getBlock().getState().getData()),ev.getPlayer());
			Bukkit.getPluginManager().callEvent(e);
			
			ev.getBlock().setType(Material.AIR);
			twin.getBlock().setType(Material.AIR);
			teams.remove(t);
		}
	}
}
