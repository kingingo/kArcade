package me.kingingo.karcade.Game.Multi;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;
import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Enum.Team;
import me.kingingo.karcade.Game.Game;
import me.kingingo.karcade.Game.Multi.Games.MultiGame;
import me.kingingo.karcade.Game.Multi.Games.One_VS_One.One_VS_One;
import me.kingingo.karcade.Game.World.WorldData;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.Packet.Events.PacketReceiveEvent;
import me.kingingo.kcore.Util.UtilWorldEdit;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

public class MultiGames extends Game{
	
	@Getter
	private ArrayList<MultiGame> games = new ArrayList<>();
	@Getter
	private HashMap<MultiGame,HashMap<Team,ArrayList<Location>>> locs = new HashMap<>();
	@Getter
	@Setter
	private WorldData worldData;
	@Getter
	private ArrayList<String[]> list = new ArrayList<>();
	
	public MultiGames(kArcadeManager manager,String type){
		super(manager);
		setWorldData(new WorldData(getManager(), GameType.valueOf(type)));
	}
	
	public void createGames(GameType type){
		if(GameType.ONE_VS_ONE==type){
			getWorldData().createCleanWorld();
			File[] schematics = getWorldData().loadSchematicFiles();
			Location loc = new Location(getWorldData().getWorld(),0,90,0);
			
			for(File file : schematics){
				UtilWorldEdit.pastePlate(loc, file);
				games.add(new One_VS_One(this,loc));
			}
		}
	}
	
	@EventHandler
	public void Join(PlayerJoinEvent ev){
		
	}
	
	@EventHandler
	public void PacketReceive(PacketReceiveEvent ev){
		
	}
	
}
