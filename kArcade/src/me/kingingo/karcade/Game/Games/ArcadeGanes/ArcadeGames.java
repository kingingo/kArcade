package me.kingingo.karcade.Game.Games.ArcadeGanes;

import java.util.HashMap;

import org.bukkit.event.EventHandler;

import lombok.Getter;
import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Game.Game;
import me.kingingo.karcade.Game.Events.GameStateChangeEvent;
import me.kingingo.karcade.Game.Games.DeathGames.DeathGames;
import me.kingingo.karcade.Game.Games.OneInTheChamber.OneInTheChamber;
import me.kingingo.karcade.Game.Games.SkyPvP.SkyPvP;
import me.kingingo.karcade.Game.Games.SurvivalGames.SurvivalGames;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.TroubleInMinecraft;
import me.kingingo.kcore.kListener;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.GameType;

public class ArcadeGames extends kListener{

	@Getter
	private kArcadeManager manager;
	@Getter
	private HashMap<GameType, Game> games = new HashMap<>();
	
	public ArcadeGames(kArcadeManager manager) {
		super(manager.getInstance(),"[ArcadeGames]");
		long time = System.currentTimeMillis();
		this.manager=manager;
		loadGames();
		
		manager.DebugLog(time, this.getClass().getName());
	}
	
	@EventHandler
	public void GameEnd(GameStateChangeEvent ev){
		if(ev.getTo()==GameState.Restart){
			
		}
	}

	public void loadGames(){
		games.put(GameType.OneInTheChamber, new OneInTheChamber(manager));
		games.put(GameType.DeathGames, new DeathGames(manager));
		games.put(GameType.SheepWars16, new DeathGames(manager));
		games.put(GameType.TroubleInMinecraft, new TroubleInMinecraft(manager));
		games.put(GameType.SurvivalGames, new SurvivalGames(manager));
		games.put(GameType.SkyPvP, new SkyPvP(manager));
	}
	
	
}
