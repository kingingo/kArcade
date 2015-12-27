package me.kingingo.karcade.Game.Multi.Games.SurvivalGames1vs1;

import java.io.File;

import org.bukkit.Location;

import me.kingingo.karcade.Game.Multi.MultiGames;
import me.kingingo.karcade.Game.Multi.Addons.MultiGameArenaRestore;
import me.kingingo.karcade.Game.Multi.Games.MultiTeamGame;
import me.kingingo.kcore.PacketAPI.Packets.kPacketPlayOutWorldBorder;

public class SurvivalGames1vs1 extends MultiTeamGame{

	private MultiGameArenaRestore area;
	private kPacketPlayOutWorldBorder packet;
	
	public SurvivalGames1vs1(MultiGames games,String Map,Location location,File file) {
		super(games,Map, location);
		
	}

}
