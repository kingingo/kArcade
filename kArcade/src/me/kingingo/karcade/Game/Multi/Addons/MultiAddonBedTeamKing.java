package me.kingingo.karcade.Game.Multi.Addons;

import java.util.HashMap;

import lombok.Getter;
import me.kingingo.karcade.Game.Multi.MultiGames;
import me.kingingo.karcade.Game.Multi.Addons.Evemts.MultiAddonBedKingDeathEvent;
import me.kingingo.karcade.Game.Multi.Addons.Evemts.MultiGameAddonAreaRestoreEvent;
import me.kingingo.karcade.Game.Multi.Games.MultiGame;
import me.kingingo.karcade.Game.Multi.Games.MultiTeamGame;
import me.kingingo.karcade.Game.Multi.Games.BedWars1vs1.BedWars1vs1;
import me.kingingo.kcore.Enum.PlayerState;
import me.kingingo.kcore.Enum.Team;
import me.kingingo.kcore.Listener.kListener;
import me.kingingo.kcore.Util.UtilBlock;
import me.kingingo.kcore.Util.UtilLocation;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.material.Bed;

public class MultiAddonBedTeamKing extends kListener {
	
	@Getter
	private MultiGames multiGames;
	@Getter
	private HashMap<MultiGame,HashMap<Team,Block>> games = new HashMap<>();
	
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
		Location loc=null;
		BlockFace face=null;
		for(Team team : teams){
			loc=game.getWorldData().getLocs(game, getBlockTeam(team)).get(0);
			loc.getChunk().load();
			
			for(BlockFace f : BlockFace.values()){
				if(loc.getBlock().getRelative(f).getType()==Material.BED_BLOCK){
					face=f;
					break;
				}
			}
			
			this.games.get(game).put(team, loc.getBlock());
			if(game instanceof BedWars1vs1){
				((BedWars1vs1)game).getArea().getBlocks().put(loc,loc.getBlock().getState());
				((BedWars1vs1)game).getArea().getBlocks().put(loc.getBlock().getRelative(face).getLocation(),loc.getBlock().getRelative(face).getState());
			}
		}
	}
	
	public void addMultiGame(MultiGame game,Team[] teams){
		this.games.put(game, new HashMap<>());
		
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
			if(game instanceof BedWars1vs1){
				((BedWars1vs1)game).getArea().getBlocks().put(loc,loc.getBlock().getState());
				((BedWars1vs1)game).getArea().getBlocks().put(loc.getBlock().getRelative(face).getLocation(),loc.getBlock().getRelative(face).getState());
			}
			
			this.games.get(game).put(team, loc.getBlock());
		}
	}
	
	Team t;
	Location twin;
	@EventHandler(priority=EventPriority.NORMAL)
	public void Death(MultiGameAddonAreaRestoreEvent ev){
		if(ev.getGame()!=null&&this.games.containsKey(ev.getGame())&&ev.getLocation().getBlock().getType()==Material.BED_BLOCK){
			
			t = null;
			twin=UtilBlock.getTwinLocation( ev.getLocation().getBlock() );
			
			for(Team tt : this.games.get(ev.getGame()).keySet()){
				if(this.games.get(ev.getGame()).get(tt).getLocation().equals(ev.getLocation()) || this.games.get(ev.getGame()).get(tt).getLocation().equals(twin)){
					t=tt;
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
			this.games.get(ev.getGame()).remove(t);
		}
	}
}
