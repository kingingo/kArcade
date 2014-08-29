package me.kingingo.karcade.Game.Games;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Enum.PlayerState;
import me.kingingo.karcade.Enum.Team;
import me.kingingo.karcade.Game.Game;
import me.kingingo.karcade.Game.Events.TeamAddEvent;
import me.kingingo.karcade.Game.Events.TeamDelEvent;
import me.kingingo.karcade.Game.addons.AddonSpecCompass;
import me.kingingo.karcade.Game.addons.AddonSpectator;
import me.kingingo.karcade.Game.addons.AddonVoteTeam;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilMath;
import me.kingingo.kcore.Util.UtilServer;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;

public class TeamGame extends Game{
	
	@Getter
	HashMap<Player,Team> TeamList = new HashMap<>();
	@Getter
	@Setter
	private AddonVoteTeam VoteTeam;
	@Getter
	@Setter
	private Scoreboard board;
	AddonSpectator spec=null;
	
	public TeamGame(kArcadeManager manager) {
		super(manager);
	}
	
	public void addTeam(Player p, Team t){
		TeamList.put(p, t);
		Bukkit.getPluginManager().callEvent(new TeamAddEvent(p,t));
	}
	
	public Team littleTeam(){
		Team t = null;
		ArrayList<Team> all = new ArrayList<>();
		
		for(Player p : TeamList.keySet()){
			if(!all.contains(TeamList.get(p)))all.add(TeamList.get(p));
		}
		
		for(Team team : all){
			t=team;
			int i = isInTeam(t);
			for(Team team1 : all){
				if(i>isInTeam(t)){
					t=null;
					break;
				}
			}
			if(t!=null){
				break;
			}
		}
		
		if(t==null){
			t=Team.RED;
		}
		
		return t;
	}

	public void delTeam(Player p){
		if(TeamList.containsKey(p)){
			Bukkit.getPluginManager().callEvent(new TeamDelEvent(p,getTeam(p)));
			TeamList.remove(p);
		}
	}
	
	public Team lastTeam(){
		Team t=null;
		for(Player p : TeamList.keySet()){
			t=TeamList.get(p);
			for(Player p1 : TeamList.keySet()){
				if(TeamList.get(p1)!=t){
					t=null;
					break;
				}
			}
		}
		return t;
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void SpectJoin(PlayerJoinEvent ev){
		if(getManager().getState()!=GameState.LobbyPhase){
			SetSpectator(null,ev.getPlayer());
		}
	}
	
	public boolean islastTeam(){
		Team t;
		for(Player p : TeamList.keySet()){
			t=TeamList.get(p);
			for(Player p1 : TeamList.keySet()){
				if(TeamList.get(p1)!=t){
					return false;
				}
			}
		}
		return true;
	}
	
	public Team getTeam(Player p){
		if(TeamList.containsKey(p)){
			return TeamList.get(p);
		}
		return null;
	}
	
	public List<Player> getPlayerFrom(Team t){
		List<Player> list = new ArrayList<>();
		for(Player p : TeamList.keySet()){
			if(TeamList.get(p)==t){
				list.add(p);
			}
		}
		return list;
	}
	
	public int isInTeam(Team t){
		int i=0;
		
		for(Player p : TeamList.keySet()){
			if(TeamList.get(p)==t){
				i++;
			}
		}
		
		return i;
	}
	
	public int r(int i){
		if(i==1)return 0;
		return UtilMath.RandomInt(i, 0);
	}
	
	public void TeamTab(Team[] teams){
		setBoard(Bukkit.getScoreboardManager().getNewScoreboard());
		for(Team team : teams){
			org.bukkit.scoreboard.Team s = getBoard().registerNewTeam(team.Name());
			s.setPrefix(team.getColor());
			for(Player p : getTeamList().keySet()){
				if(getTeamList().get(p)==team){
					s.addPlayer(p);
				}
			}
		}
		for(Player p : UtilServer.getPlayers()){
			p.setScoreboard(getBoard());
		}
	}
	
	public void PlayerVerteilung(HashMap<Team,Integer> t,ArrayList<Player> list){
		int r;
		Player p;
		
		if(getVoteTeam()!=null){
			for(Player p1 : getVoteTeam().getVote().keySet()){
				TeamList.put(p1, getVoteTeam().getVote().get(p1));
				list.remove(p1);
			}
		}
		
		for(int c = 1; c <= 2000; c++){
			if(list.isEmpty())break;
			r=r(list.size());
			p=list.get(r);
			if(TeamList.containsKey(p))continue;
			for(Team team : t.keySet()){
				if(isInTeam(team)>=t.get(team))continue;
				TeamList.put(p, team);
				list.remove(r);
				break;
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void RestartQuit(PlayerQuitEvent ev){
		if(TeamList.containsKey(ev.getPlayer())){
			TeamList.remove(ev.getPlayer());
		}
		if(getManager().isState(GameState.Restart)||getManager().isState(GameState.LobbyPhase))return;
		
		getGameList().addPlayer(ev.getPlayer(), PlayerState.OUT);
		if(islastTeam()||getGameList().getPlayers(PlayerState.IN).size()<=1){
			getManager().setState(GameState.Restart);
		}
	}
	
	@EventHandler
	public void SpectaterAndRespawn(PlayerRespawnEvent ev){
		if(getGameList().getPlayers(PlayerState.OUT).contains(ev.getPlayer())){
			SetSpectator(ev,ev.getPlayer());
			ev.setRespawnLocation(getGameList().getPlayers(PlayerState.IN).get(0).getLocation());
		}
	}
	
	public void SetSpectator(PlayerRespawnEvent ev,Player player)
	  {
		if(spec==null)spec=new AddonSpectator(getManager());
		TeamList.remove(player);
		getGameList().addPlayer(player, PlayerState.OUT);
	    getManager().Clear(player);
	    if(ev==null){
	    	player.teleport(getGameList().getPlayers(PlayerState.IN).get(0).getLocation());
	    }else{
	    	ev.setRespawnLocation(getGameList().getPlayers(PlayerState.IN).get(0).getLocation());
	    }
	    player.setGameMode(GameMode.CREATIVE);
	    player.setFlying(true);
	    player.setFlySpeed(0.1F);
	    ((CraftPlayer)player).getHandle().k = false;
	    for(Player p : UtilServer.getPlayers()){
	    	p.hidePlayer(player);
	    }
	    if(getCompass()==null)setCompass(new AddonSpecCompass(getManager()));
	    player.getInventory().addItem(getCompass().getCompassItem());
	    player.getInventory().setItem(8,UtilItem.RenameItem(new ItemStack(385), "§aZurück zur Lobby"));
	    if(islastTeam()||getGameList().getPlayers(PlayerState.IN).size()<=1){
			getManager().setState(GameState.Restart);
		}
	    
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void arrow_damage(EntityDamageByEntityEvent ev){
		if(ev.getEntity() instanceof Player && ev.getDamager() instanceof Arrow){
			Arrow a = (Arrow)ev.getDamager();
			if(!(a.getShooter() instanceof Player))return;
			Player d = (Player)a.getShooter();
			Player v = (Player)ev.getEntity();
			if(!DamageTeamSelf&&getTeam(d)==getTeam(v)){
				ev.setCancelled(true);
			}else if(!DamageTeamOther&&getTeam(d)!=getTeam(v)){
				ev.setCancelled(true);
			}else{
				ev.setCancelled(false);
			}
		}
	}
	
	@EventHandler(priority=EventPriority.NORMAL)
	public void TeamDamage(EntityDamageByEntityEvent ev){
		if((ev.getEntity() instanceof Player && ev.getDamager() instanceof Player)){
			if(!DamageTeamSelf&&getTeam((Player)ev.getDamager())==getTeam((Player)ev.getEntity())){
				ev.setCancelled(true);
			}else if(!DamageTeamOther&&getTeam((Player)ev.getDamager())!=getTeam((Player)ev.getEntity())){
				ev.setCancelled(true);
			}
		}
	}
	
}
