package me.kingingo.karcade.Game.Multi.Games.One_VS_One;

import java.util.ArrayList;
import java.util.HashMap;

import me.kingingo.karcade.Enum.Team;
import me.kingingo.karcade.Game.Multi.MultiGames;
import me.kingingo.karcade.Game.Multi.Games.MultiGame;
import me.kingingo.kcore.Util.UtilLocation;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class One_VS_One extends MultiGame{
	
	public One_VS_One(MultiGames games,Location location) {
		super(games);
		getGames().getLocs().put(this, new HashMap<Team,ArrayList<Location>>());
		for(Block block : UtilLocation.searchBlocks(Material.SPONGE, 40, location)){
			if(block.getRelative(BlockFace.UP).getType()==Material.WOOL){
				if(block.getRelative(BlockFace.UP).getData()==14){
					if(!getGames().getLocs().get(this).containsKey(Team.RED))getGames().getLocs().get(this).put(Team.RED, new ArrayList<Location>());
					getGames().getLocs().get(this).get(Team.RED).add(block.getLocation());
				}else if(block.getRelative(BlockFace.UP).getData()==11){
					if(!getGames().getLocs().get(this).containsKey(Team.BLUE))getGames().getLocs().get(this).put(Team.BLUE, new ArrayList<Location>());
					getGames().getLocs().get(this).get(Team.BLUE).add(block.getLocation());
				}else if(block.getRelative(BlockFace.UP).getData()==5){
					if(!getGames().getLocs().get(this).containsKey(Team.GREEN))getGames().getLocs().get(this).put(Team.GREEN, new ArrayList<Location>());
					getGames().getLocs().get(this).get(Team.GREEN).add(block.getLocation());
				}else if(block.getRelative(BlockFace.UP).getData()==4){
					if(!getGames().getLocs().get(this).containsKey(Team.YELLOW))getGames().getLocs().get(this).put(Team.YELLOW, new ArrayList<Location>());
					getGames().getLocs().get(this).get(Team.YELLOW).add(block.getLocation());
				}
				
				block.getRelative(BlockFace.UP).setType(Material.AIR);
				block.setType(Material.AIR);
			}
		}
	}

}
