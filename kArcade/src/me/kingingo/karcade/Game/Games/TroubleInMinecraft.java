package me.kingingo.karcade.Game.Games;

import java.util.ArrayList;
import java.util.HashMap;

import lombok.Getter;
import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Enum.PlayerState;
import me.kingingo.karcade.Enum.Team;
import me.kingingo.karcade.Game.Events.GameStartEvent;
import me.kingingo.karcade.Game.SoloOrTeam.TeamGame;
import me.kingingo.karcade.Game.World.WorldData;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.C;
import me.kingingo.kcore.Util.UtilDisplay;
import me.kingingo.kcore.Util.UtilMath;
import me.kingingo.kcore.Util.UtilServer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChatEvent;

public class TroubleInMinecraft extends TeamGame{

	//MINI_CHEST MHF_Chest
	
	WorldData wd;
	@Getter
	private kArcadeManager manager;
	int start=0;
	
	public TroubleInMinecraft(kArcadeManager manager) {
		super(manager);
		this.manager=manager;
		long t = System.currentTimeMillis();
		manager.setState(GameState.Laden);
		manager.setTyp(GameType.TroubleInMinecraft);
		setMin_Players(4);
		setMax_Players(12);
		setDamageTeamSelf(true);
		setDamageSelf(true);
		setItemDrop(false);
		setItemPickup(false);
		setBlockPlace(false);
		setBlockBreak(false);
		
		wd = new WorldData(manager,GameType.TroubleInMinecraft.name());
		wd.Initialize();
		manager.setWorldData(wd);
		manager.setState(GameState.LobbyPhase);
		manager.DebugLog(t, 31, this.getClass().getName());
	}

	@EventHandler
	public void Chat(PlayerChatEvent ev){
		ev.setCancelled(true);
		Bukkit.broadcastMessage(C.cGray+ev.getPlayer().getDisplayName()+": "+ev.getMessage());
	}
	
	@EventHandler
	public void inGame(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		if(getManager().getState()!=GameState.InGame)return;
		for(Player p : UtilServer.getPlayers())UtilDisplay.displayTextBar(p, Text.GAME_END_IN.getText(start));
		switch(start){
		case 30: getManager().broadcast(Text.PREFIX.getText()+Text.GAME_END_IN.getText(start));break;
		case 20: getManager().broadcast(Text.PREFIX.getText()+Text.GAME_END_IN.getText(start));break;
		case 15: getManager().broadcast(Text.PREFIX.getText()+Text.GAME_END_IN.getText(start));break;
		case 10: getManager().broadcast(Text.PREFIX.getText()+Text.GAME_END_IN.getText(start));break;
		case 5: getManager().broadcast(Text.PREFIX.getText()+Text.GAME_END_IN.getText(start));break;
		case 4: getManager().broadcast(Text.PREFIX.getText()+Text.GAME_END_IN.getText(start));break;
		case 3: getManager().broadcast(Text.PREFIX.getText()+Text.GAME_END_IN.getText(start));break;
		case 2: getManager().broadcast(Text.PREFIX.getText()+Text.GAME_END_IN.getText(start));break;
		case 1: getManager().broadcast(Text.PREFIX.getText()+Text.GAME_END_IN.getText(start));break;
		case 0: 
			getManager().setState(GameState.Restart);
			getManager().broadcast(Text.PREFIX.getText()+Text.GAME_END.getText());
			break;
		}
	}
	
	@EventHandler
	public void Schutzzeit(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		if(getManager().getState()!=GameState.StartGame)return;
		for(Player p : UtilServer.getPlayers())UtilDisplay.displayTextBar(p, Text.SCHUTZZEIT_END_IN.getText(start));
		switch(start){
		case 30: getManager().broadcast(Text.PREFIX.getText()+Text.SCHUTZZEIT_END_IN.getText(start));break;
		case 20: getManager().broadcast(Text.PREFIX.getText()+Text.SCHUTZZEIT_END_IN.getText(start));break;
		case 15: getManager().broadcast(Text.PREFIX.getText()+Text.SCHUTZZEIT_END_IN.getText(start));break;
		case 10: getManager().broadcast(Text.PREFIX.getText()+Text.SCHUTZZEIT_END_IN.getText(start));break;
		case 5: getManager().broadcast(Text.PREFIX.getText()+Text.SCHUTZZEIT_END_IN.getText(start));break;
		case 4: getManager().broadcast(Text.PREFIX.getText()+Text.SCHUTZZEIT_END_IN.getText(start));break;
		case 3: getManager().broadcast(Text.PREFIX.getText()+Text.SCHUTZZEIT_END_IN.getText(start));break;
		case 2: getManager().broadcast(Text.PREFIX.getText()+Text.SCHUTZZEIT_END_IN.getText(start));break;
		case 1: getManager().broadcast(Text.PREFIX.getText()+Text.SCHUTZZEIT_END_IN.getText(start));break;
		case 0: 
			ArrayList<Player> plist = new ArrayList<>();
			for(Player p : UtilServer.getPlayers()){
				plist.add(p);
			}
			PlayerVerteilung(verteilung(),plist);
			getManager().setState(GameState.InGame);
			getManager().broadcast(Text.PREFIX.getText()+Text.SCHUTZZEIT_END.getText());
			break;
		}
	}
	
	@EventHandler
	public void Start(GameStartEvent ev){
		start=31;
		getManager().setState(GameState.StartGame);
		HashMap<String,ArrayList<Location>> list = getManager().getWorldData().getLocs();
		
		int r=0;
		for(Player p : UtilServer.getPlayers()){
			getManager().Clear(p);
			getGameList().addPlayer(p,PlayerState.IN);
		}
		
		for(Player p : getTeamList().keySet()){
			r=UtilMath.r(list.get(Team.RED.Name()).size());
			p.teleport(list.get(Team.RED.Name()).get(r));
			list.get(Team.RED.Name()).remove(r);
		}
	}

	public Team[] verteilung(){
		Team[] t = new Team[]{Team.INOCCENT,Team.DETECTIVE,Team.TRAITOR};
		 t[2].setPlayer(getTraitor());
		 t[1].setPlayer(getDetective());
		 t[0].setPlayer(UtilServer.getPlayers().length-(t[2].getPlayer()+t[1].getPlayer()));
		return t;
	}
	
	public int getTraitor(){
		switch(UtilServer.getPlayers().length){
		case 4: return 1;
		case 5: return 1;
		case 6: return 2;
		case 7: return 2;
		case 8: return 2;
		case 9: return 2;
		case 10: return 3;
		case 11: return 3;
		case 12: return 4;
		}
		return 3;
	}
	
	public int getDetective(){
		switch(UtilServer.getPlayers().length){
		case 4: return 1;
		case 5: return 1;
		case 6: return 1;
		case 7: return 1;
		case 8: return 2;
		case 9: return 2;
		case 10: return 3;
		case 11: return 4;
		case 12: return 4;
		}
		return 3;
	}
	
}
