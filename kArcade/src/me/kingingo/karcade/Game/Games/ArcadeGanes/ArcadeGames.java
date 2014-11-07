package me.kingingo.karcade.Game.Games.ArcadeGanes;

import java.util.ArrayList;

import lombok.Getter;
import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Game.Game;
import me.kingingo.karcade.Game.Events.GameStateChangeEvent;
import me.kingingo.karcade.Game.Events.GameUpdateInfoEvent;
import me.kingingo.karcade.Game.Games.DeathGames.DeathGames;
import me.kingingo.karcade.Game.Games.OneInTheChamber.OneInTheChamber;
import me.kingingo.karcade.Game.Games.SkyPvP.SkyPvP;
import me.kingingo.karcade.Game.Games.SurvivalGames.SurvivalGames;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.TroubleInMinecraft;
import me.kingingo.kcore.kListener;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.Packet.Packets.SERVER_STATUS;
import me.kingingo.kcore.Util.UtilMath;
import me.kingingo.kcore.Util.UtilServer;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class ArcadeGames extends kListener{

	@Getter
	private kArcadeManager manager;
	@Getter
	private ArrayList<Game> games = new ArrayList<>();
	private Game last_game;
	
	public ArcadeGames(kArcadeManager manager) {
		super(manager.getInstance(),"[ArcadeGames]");
		long time = System.currentTimeMillis();
		this.manager=manager;
		loadGames();
		NextGame();
		manager.setState(GameState.LobbyPhase);
		manager.DebugLog(time, this.getClass().getName());
	}
	
	public Game NextGame(){
		if(last_game!=null){
			games.remove(last_game);
			last_game=null;
		}
		last_game=games.get(UtilMath.r(games.size()));
		return last_game;
	}
	
	@EventHandler
	public void GameEnd(GameStateChangeEvent ev){
		if(ev.getTo()==GameState.Restart){
			for(Player p : UtilServer.getPlayers())p.teleport(manager.getLobby());
			NextGame();
			
		}
	}
	
	SERVER_STATUS packet;
	@EventHandler
	public void UpdateInfo(GameUpdateInfoEvent ev){
		packet=ev.getPacket();
		packet.setMax_online(12);
		packet.setOnline(UtilServer.getPlayers().length);
		packet.setMap( last_game.getType().name() );
	}

	public void loadGames(){
		games.add(new OneInTheChamber(manager));
		games.add(new DeathGames(manager));
		games.add(new DeathGames(manager));
		games.add(new TroubleInMinecraft(manager));
		games.add(new SurvivalGames(manager));
		games.add(new SkyPvP(manager));
	}
	
	
}
