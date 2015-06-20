package me.kingingo.karcade.Game.Single.addons;

import java.util.HashMap;

import lombok.Getter;
import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Enum.PlayerState;
import me.kingingo.karcade.Game.Single.Events.AddonBedKingDeathEvent;
import me.kingingo.karcade.Game.Single.Games.TeamGame;
import me.kingingo.kcore.Enum.Team;
import me.kingingo.kcore.Util.UtilBlock;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.material.Bed;

public class AddonBedTeamKing implements Listener {
	
	@Getter
	kArcadeManager manager;;
	@Getter
	HashMap<Team,Block> teams = new HashMap<>();
	@Getter
	TeamGame team;
	
	public AddonBedTeamKing(kArcadeManager manager,Team[] teams,TeamGame team){
		this.manager=manager;
		this.team=team;
		BlockFace face = null;
		Location loc = null;
		for(Team t : teams){
			loc=manager.getGame().getWorldData().getLocs(getBlockTeam(t).Name()).get(0);
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
			
			if(t==null||getTeam().getTeam( ev.getPlayer() )==t ||  team.getGameList().isPlayerState( ev.getPlayer() )!=PlayerState.IN){
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
