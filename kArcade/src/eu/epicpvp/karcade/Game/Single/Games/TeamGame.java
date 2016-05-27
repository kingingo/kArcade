package eu.epicpvp.karcade.Game.Single.Games;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

import dev.wolveringer.dataserver.gamestats.GameState;
import eu.epicpvp.karcade.kArcadeManager;
import eu.epicpvp.karcade.Game.Events.TeamAddEvent;
import eu.epicpvp.karcade.Game.Events.TeamDelEvent;
import eu.epicpvp.karcade.Game.Single.SingleGame;
import eu.epicpvp.karcade.Game.Single.Addons.AddonSpecCompass;
import eu.epicpvp.karcade.Game.Single.Addons.AddonSpectator;
import eu.epicpvp.karcade.Game.Single.Addons.AddonVoteTeam;
import eu.epicpvp.kcore.Enum.GameStateChangeReason;
import eu.epicpvp.kcore.Enum.PlayerState;
import eu.epicpvp.kcore.Enum.Team;
import eu.epicpvp.kcore.Kit.Shop.Events.KitShopPlayerDeleteEvent;
import eu.epicpvp.kcore.Permission.PermissionType;
import eu.epicpvp.kcore.Translation.TranslationHandler;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.kcore.Util.UtilMath;
import eu.epicpvp.kcore.Util.UtilScoreboard;
import eu.epicpvp.kcore.Util.UtilServer;
import lombok.Getter;
import lombok.Setter;

public class TeamGame extends SingleGame{
	
	@Getter
	private HashMap<Player,Team> TeamList = new HashMap<>();
	@Getter
	@Setter
	private AddonVoteTeam VoteTeam;
	private AddonSpectator spec=null;
	
	public TeamGame(kArcadeManager manager) {
		super(manager);
	}
	
	public void addTeam(Player p, Team t){
		TeamList.put(p, t);
		Bukkit.getPluginManager().callEvent(new TeamAddEvent(p,t));
	}
	
	public String getERR(Team[] t,int size){
		String s = "";
		HashMap<Team,Integer> l = verteilung(t, size);
		for(Team te : l.keySet()){
			s=s+"TEAM:"+te.Name()+"/"+l.get(te);
		}
		s=s+"  -  ";
		for(Player p : TeamList.keySet()){
			s=s+"PLAYER:"+p.getName()+"-"+TeamList.get(p).Name();
		}
		
		return s;
	}
	
	public HashMap<Team,Integer> verteilung(Team[] t,int size){
		if(size==1){
			HashMap<Team,Integer> list = new HashMap<>();
			for(Team team : t)list.put(team, 1);
			return list;
		}else if(size==2){
			HashMap<Team,Integer> list = new HashMap<>();
			for(Team team : t)list.put(team, 2);
			return list;
		}else{
			HashMap<Team,Integer> list = new HashMap<>();
			Collection<? extends Player> l = UtilServer.getPlayers();
		
			for(Team team : t){
				list.put(team, l.size()/t.length);
			}
			
			if(l.size()%t.length!=0){
				list.remove(t[0]);
				list.put(t[0], (l.size()/t.length)+1);
			}

			return list;
		}
	}
	
	public int getTeamCount(Team team){
		int i = 0;
		for(Team t : TeamList.values()){
			if(t==team)i++;
		}
		return i;
	}

	public Team littleTeam(){
		return littleTeam(true);
	}
	
	public Team littleTeam(boolean returnNullBySame){
		Team t = null;
		Set<Team> all = new HashSet();
		all.addAll(TeamList.values());
		
		for(Team team : all){
			t=team;
			for(Team team1 : all){
				if(isInTeam(t)>isInTeam(team1)){
					t=null;
					break;
				}
			}
			if(t!=null){
				break;
			}
		}
		if(t==null && !returnNullBySame)t=Team.RED;
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
		if(getState()!=GameState.LobbyPhase){
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
	
	public ArrayList<Player> getPlayerFrom(Team t){
		ArrayList<Player> list = new ArrayList<>();
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
	
	public void TeamTab(Team[] teams){
	    if(getBoard()==null)setBoard(Bukkit.getScoreboardManager().getNewScoreboard());
	    for (Team team : teams) {
	    	ArrayList<Player> list = getPlayerFrom(team);
	    	
	    	UtilScoreboard.addTeam(getBoard(), team.Name(), team.getColor());
	    	
	    	for(Player player : list){
	    		if(getManager().getNickManager()!=null&&getManager().getNickManager().getNicks().containsKey(player.getEntityId())){
	    			getBoard().getTeam(team.Name()).addEntry( getManager().getNickManager().getNicks().get(player.getEntityId()).getName() );
	    			continue;
	    		}
	    		getBoard().getTeam(team.Name()).addPlayer(player);
	    	}
	    	
	    	for(Player p : list){
	    		if(!getBoard().getPlayers().contains(p)){
	    			p.setScoreboard(getBoard());
	    		}
	    	}
	    }
	    
	}
	
	public int r(int i){
		if(i==1)return 0;
		return UtilMath.RandomInt((i-1), 0);
	}
	
	public void PlayerVerteilung(Team[] teams, ArrayList<Player> list){
		if(getVoteTeam()!=null){
			for(Player player : getVoteTeam().getVote().keySet()){
				if(list.contains(player)){
					getTeamList().put(player, getVoteTeam().getVote().get(player));
					list.remove(player);
				}
			}
		}
		
		Collections.shuffle(list);
		Player player;
		for(int i = 0; i < list.size(); i++){
			if(list.isEmpty())break;
			player = list.get(i);
			logMessage("List-Size: "+list.size()+" Player:"+player.getName()+" (Index:"+i+")");
			if(getTeamList().containsKey(player))continue;
			
			Team team = littleTeam(false);
			if(team!=null){
				getTeamList().put(player, team);
			}else{
				getTeamList().put(player, teams[0]);
			}
		}
	}
	
	public boolean TeamAmountSame(){
		Set<Team> teams = new HashSet();
		teams.addAll(getTeamList().values());
		
		for(Team team1 : teams){
			for(Team team2 : teams){
				if(isInTeam(team1) != isInTeam(team2)){
					return false;
				}
			}
		}
		
		return true;
	}
	
	public void PlayerVerteilung(HashMap<Team,Integer> teamVerteilung,ArrayList<Player> list){
		if(getVoteTeam()!=null){
			for(Player player : getVoteTeam().getVote().keySet()){
				if(list.contains(player)){
					getTeamList().put(player, getVoteTeam().getVote().get(player));
					list.remove(player);
				}
			}
		}
		
		Collections.shuffle(list);
		Player player;
		for(int i = 0; i < list.size(); i++){
			if(list.isEmpty())break;
			player = list.get(i);
			logMessage("List-Size: "+list.size()+" Player:"+player.getName()+" (Index:"+i+")");
			
			if(getTeamList().containsKey(player))continue;
			for(Team team : teamVerteilung.keySet()){
				if(isInTeam(team)>=teamVerteilung.get(team))continue;
				addTeam(player, team);
				list.remove(player);
				break;
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void RestartQuit(PlayerQuitEvent ev){
		if(TeamList.containsKey(ev.getPlayer())){
			delTeam(ev.getPlayer());
		}
		if(isState(GameState.Restart)||isState(GameState.LobbyPhase))return;
			getGameList().addPlayer(ev.getPlayer(), PlayerState.OUT);
		if(islastTeam()&&(getState()==GameState.InGame||getState()==GameState.DeathMatch)){
			setState(GameState.Restart,GameStateChangeReason.LAST_TEAM);
		}else if(getGameList().getPlayers(PlayerState.IN).size()<=1){
			setState(GameState.Restart,GameStateChangeReason.LAST_PLAYER);
		}
	}
	
	@EventHandler
	public void SpectaterAndRespawn(PlayerRespawnEvent ev){
		if(getGameList().isPlayerState(ev.getPlayer())==PlayerState.OUT){
			ev.setRespawnLocation(getGameList().getPlayers(PlayerState.IN).get(0).getLocation());
			SetSpectator(ev,ev.getPlayer());
		}
	}
	
	public void SetSpectator(PlayerRespawnEvent ev,Player player)
	  {
		if(spec==null)spec=new AddonSpectator(this);
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
	    	setState(GameState.Restart,GameStateChangeReason.LAST_PLAYER);
	    }
	    player.setGameMode(GameMode.SPECTATOR);
	    player.setAllowFlight(true);
	    player.setFlying(true);
	    player.setFlySpeed(0.1F);
	    for(Player p : UtilServer.getPlayers()){
	    	p.hidePlayer(player);
	    }
	    if(getCompass()==null)setCompass(new AddonSpecCompass(this));
	    player.getInventory().addItem(getCompass().getCompassItem());
	    player.getInventory().setItem(8,UtilItem.RenameItem(new ItemStack(385), "§aZurück zur Lobby"));
	    
	    if(islastTeam()&& (getState()==GameState.InGame||getState()==GameState.DeathMatch)){
			setState(GameState.Restart,GameStateChangeReason.LAST_TEAM);
		}else if(getGameList().getPlayers(PlayerState.IN).size()<=1){
			setState(GameState.Restart,GameStateChangeReason.LAST_PLAYER);
		}

	    Bukkit.getPluginManager().callEvent(new KitShopPlayerDeleteEvent(player));
	    getMoney().save(player);
	}
	
	@EventHandler(priority=EventPriority.NORMAL,ignoreCancelled=true)
	public void arrow_damage(EntityDamageByEntityEvent ev){
		if(ev.getEntity() instanceof Player && ev.getDamager() instanceof Arrow){
			Arrow a = (Arrow)ev.getDamager();
			if(!(a.getShooter() instanceof Player))return;
			Player d = (Player)a.getShooter();
			Player v = (Player)ev.getEntity();
			if(!DamageTeamSelf&&getTeam(d)==getTeam(v)){
				if(getManager().getService().isDebug())System.err.println("[TeamGame] Cancelled TRUE bei DamageTeamSelf Projectile");
				ev.setCancelled(true);
			}else if(!DamageTeamOther&&getTeam(d)!=getTeam(v)){
				if(getManager().getService().isDebug())System.err.println("[TeamGame] Cancelled TRUE bei DamageTeamOther Projectile");
				ev.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority=EventPriority.NORMAL,ignoreCancelled=true)
	public void TeamDamage(EntityDamageByEntityEvent ev){
		if((ev.getEntity() instanceof Player && ev.getDamager() instanceof Player)){
			if(!DamageTeamSelf&&getTeam((Player)ev.getDamager())==getTeam((Player)ev.getEntity())){
				if(getManager().getService().isDebug())System.err.println("[TeamGame] Cancelled TRUE bei DamageTeamSelf");
				ev.setCancelled(true);
			}else if(!DamageTeamOther&&getTeam((Player)ev.getDamager())!=getTeam((Player)ev.getEntity())){
				if(getManager().getService().isDebug())System.err.println("[TeamGame] Cancelled TRUE bei DamageTeamOther");
				ev.setCancelled(true);
			}
		}
	}
	
}
