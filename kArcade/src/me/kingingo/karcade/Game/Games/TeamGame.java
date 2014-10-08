package me.kingingo.karcade.Game.Games;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Enum.GameStateChangeReason;
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
				addTeam(p, team);
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
		if(islastTeam()&&getManager().getState()==GameState.InGame){
			getManager().setState(GameState.Restart,GameStateChangeReason.LAST_TEAM);
		}else if(getGameList().getPlayers(PlayerState.IN).size()<=1){
			getManager().setState(GameState.Restart,GameStateChangeReason.LAST_PLAYER);
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
		delTeam(player);
		getGameList().addPlayer(player, PlayerState.OUT);
	    getManager().Clear(player);
	    List<Player> l = getGameList().getPlayers(PlayerState.IN);
	    if(l.size()>1){
	    	if(ev==null){
		    	player.teleport(l.get(UtilMath.r(l.size())).getLocation().add(0.0D,3.5D,0.0D));
	    	}else{
	    		ev.setRespawnLocation(l.get(UtilMath.r(l.size())).getLocation().add(0.0D,3.5D,0.0D));
	    	}
	    }else{
	    	if(ev==null){
	    		player.teleport(getManager().getLobby());
	    	}else{
	    		ev.setRespawnLocation(getManager().getLobby());
	    	}
	    	getManager().setState(GameState.Restart,GameStateChangeReason.LAST_PLAYER);
	    }
	    player.setGameMode(GameMode.CREATIVE);
	    player.setFlying(true);
	    player.setFlySpeed(0.1F);
	    for(Player p : UtilServer.getPlayers()){
	    	p.hidePlayer(player);
	    }
	    if(getCompass()==null)setCompass(new AddonSpecCompass(getManager()));
	    player.getInventory().addItem(getCompass().getCompassItem());
	    player.getInventory().setItem(8,UtilItem.RenameItem(new ItemStack(385), "§aZurück zur Lobby"));
	    if(islastTeam()&&getManager().getState()==GameState.InGame){
			getManager().setState(GameState.Restart,GameStateChangeReason.LAST_TEAM);
		}else if(getGameList().getPlayers(PlayerState.IN).size()<=1){
			getManager().setState(GameState.Restart,GameStateChangeReason.LAST_PLAYER);
		}
	    
	}
	
	@EventHandler(priority=EventPriority.NORMAL,ignoreCancelled=true)
	public void arrow_damage(EntityDamageByEntityEvent ev){
		if(ev.getEntity() instanceof Player && ev.getDamager() instanceof Arrow){
			Arrow a = (Arrow)ev.getDamager();
			if(!(a.getShooter() instanceof Player))return;
			Player d = (Player)a.getShooter();
			Player v = (Player)ev.getEntity();
			if(!DamageTeamSelf&&getTeam(d)==getTeam(v)){
				if(getManager().getService().isDamage())System.err.println("[TeamGame] Cancelled TRUE bei DamageTeamSelf Projectile");
				ev.setCancelled(true);
			}else if(!DamageTeamOther&&getTeam(d)!=getTeam(v)){
				if(getManager().getService().isDamage())System.err.println("[TeamGame] Cancelled TRUE bei DamageTeamOther Projectile");
				ev.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority=EventPriority.NORMAL,ignoreCancelled=true)
	public void TeamDamage(EntityDamageByEntityEvent ev){
		if((ev.getEntity() instanceof Player && ev.getDamager() instanceof Player)){
			if(!DamageTeamSelf&&getTeam((Player)ev.getDamager())==getTeam((Player)ev.getEntity())){
				if(getManager().getService().isDamage())System.err.println("[TeamGame] Cancelled TRUE bei DamageTeamSelf");
				ev.setCancelled(true);
			}else if(!DamageTeamOther&&getTeam((Player)ev.getDamager())!=getTeam((Player)ev.getEntity())){
				if(getManager().getService().isDamage())System.err.println("[TeamGame] Cancelled TRUE bei DamageTeamOther");
				ev.setCancelled(true);
			}
		}
	}
	
}
