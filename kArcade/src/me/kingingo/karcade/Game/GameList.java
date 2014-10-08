package me.kingingo.karcade.Game;

import java.util.ArrayList;
import java.util.HashMap;

import lombok.Getter;
import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Enum.PlayerState;
import me.kingingo.karcade.Events.PlayerStateChangeEvent;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class GameList implements Listener{

	private Game game;
	@Getter
	private HashMap<Player, PlayerState> players = new HashMap<>();
    private ArrayList<Location> spawns;
    private kArcadeManager manager;
    
    public GameList(Game game,kArcadeManager manager){
    	Bukkit.getPluginManager().registerEvents(this, manager.getInstance());
    	this.manager=manager;
    	this.game=game;
    }
    
    public PlayerState isPlayerState(Player p){
    	if(players.containsKey(p))return players.get(p);
    	return null;
    }
    
    @EventHandler
    public void Quit(PlayerQuitEvent ev){
    	if(players.containsKey(ev.getPlayer())){
    		players.put(ev.getPlayer(), PlayerState.OUT);
    	}
    }
    
    public void addPlayer(Player player,PlayerState ps){
    	if(players.containsKey(player))players.remove(player);
		players.put(player, ps);
		Bukkit.getPluginManager().callEvent(new PlayerStateChangeEvent(player,ps));
//		for (Player other : getPlayers(ps))
//	    {
//	      if (!other.equals(player))
//	      {
//	        other.hidePlayer(player);
//	        other.showPlayer(player);
//	      }
//	    }
		
	}
    
    public Scoreboard createScoreboard(DisplaySlot typ,Player p,String DisplayName){
    	Scoreboard board;
    	board = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective obj= board.registerNewObjective(typ.name(), "dummy");
		obj.setDisplayName( DisplayName);
		obj.setDisplaySlot(typ);
		return board;
    }
    
    public void setPlayerScoreboard(Player p,Scoreboard board){
    	p.setScoreboard(board);
    }
	
	public void SetSpawns(ArrayList<Location> spawns){
		this.spawns=spawns;
	}
	
	public void SetPlayerState(Player player, PlayerState state){
		if(player==null)return;
		players.put(player, state);
		Bukkit.getPluginManager().callEvent(new PlayerStateChangeEvent(player,state));
	}
	
	public int GetSize(){
		return players.size();
	}
	
	public boolean HasPlayer(Player player){
	return this.players.containsKey(player);
	}
	
	public void RemovePlayer(Player player) {
		players.remove(player);
		Bukkit.getPluginManager().callEvent(new PlayerStateChangeEvent(player,PlayerState.OUT));
    }
	
	public ArrayList<Location> getSpawns(){
		return this.spawns;
	}
	
	public void teleport(Player player,Location loc){
	    player.leaveVehicle();
	    player.eject();
	    player.teleport(loc);
	  }
	
	public ArrayList<Player> getPlayers(PlayerState ps){
		ArrayList<Player> alive = new ArrayList();
		for(Player p : players.keySet()){
			if(players.get(p) == ps|| ps==PlayerState.BOTH)alive.add(p);
		}
		return alive;
	}
	
}
