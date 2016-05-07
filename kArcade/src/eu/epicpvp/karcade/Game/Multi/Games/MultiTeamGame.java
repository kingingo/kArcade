package eu.epicpvp.karcade.Game.Multi.Games;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;

import dev.wolveringer.dataserver.gamestats.GameState;
import eu.epicpvp.karcade.Game.Events.TeamAddEvent;
import eu.epicpvp.karcade.Game.Events.TeamDelEvent;
import eu.epicpvp.karcade.Game.Multi.MultiGames;
import eu.epicpvp.kcore.Enum.GameStateChangeReason;
import eu.epicpvp.kcore.Enum.PlayerState;
import eu.epicpvp.kcore.Enum.Team;
import eu.epicpvp.kcore.Kit.Shop.Events.KitShopPlayerDeleteEvent;
import eu.epicpvp.kcore.Util.Color;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.kcore.Util.UtilMath;
import eu.epicpvp.kcore.Util.UtilScoreboard;
import eu.epicpvp.kcore.Util.UtilServer;
import lombok.Getter;
import lombok.Setter;

public class MultiTeamGame extends MultiGame{


	@Getter
	@Setter
	private HashMap<Player,Team> TeamList = new HashMap<>();
	@Getter
	@Setter
	private boolean DamageTeamSelf = false;
	@Getter
	@Setter
	private boolean DamageTeamOther = true;
	
	public MultiTeamGame(MultiGames games, String Map, Location pasteLocation) {
		super(games, Map, pasteLocation);
	}
	
	public Team littleTeam(){
		Team t = null;
		ArrayList<Team> all = new ArrayList<>();
		
		for(Player p : TeamList.keySet()){
			if(!all.contains(TeamList.get(p)))all.add(TeamList.get(p));
		}
		
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
		
		if(t==null){
			t=Team.RED;
		}
		
		return t;
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
	
	public int getTeamAnzahl(Team t){
		int i = 0;
		for(Player p : getTeamList().keySet()){
			if(getTeamList().get(p)==t)i++;
		}
		return i;
	}
	
	public String getTeamMember(Team t){
		String s = "";
		
		for(Player p : getTeamList().keySet()){
			if(getTeamList().get(p)==t){
				s=s+t.getColor()+p.getName()+"§7,";
			}
		}
		
		return s.substring(0, s.length()-3);
	}
	
	//Gibt das Letze Team zurück
		public Team getlastTeam(){
			Team t=null;
			for(Player p : TeamList.keySet()){
				t=TeamList.get(p);
				for(Player p1 : TeamList.keySet()){
					if(TeamList.get(p1)!=t){
						return null;
					}
				}
			}
			return t;
		}
	
	//Gibt wieder ob nur noch ein Team §brig ist!
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
	
	public void delTeam(Player p){
		if(TeamList.containsKey(p)){
			Bukkit.getPluginManager().callEvent(new TeamDelEvent(p,getTeam(p)));
			TeamList.remove(p);
		}
	}
	
	public void setTeamTab(Team team, Color enemy, Color friend){
		Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
		UtilScoreboard.addTeam(board, "friend", friend.toString());
		UtilScoreboard.addTeam(board, "enemy", enemy.toString());
		
		for(Player player : UtilServer.getPlayers()){
			if(getTeam(player)!=team){
				UtilScoreboard.addPlayerToTeam(board, "enemy", player);
			}else{
				UtilScoreboard.addPlayerToTeam(board, "friend", player);
			}
		}
		
		for(Player player : getTeamList().keySet())if(getTeamList().get(player)==team)player.setScoreboard(board);
	}
	
	public void setTeamTab(Scoreboard board){
	    if(board==null)board=Bukkit.getScoreboardManager().getNewScoreboard();
	    for (Player p : getTeamList().keySet()) {
	    	if(!UtilScoreboard.existTeam(board, getTeamList().get(p).Name())){
	    		UtilScoreboard.addTeam(board, getTeamList().get(p).Name(), getTeamList().get(p).getColor());
	    	}
	    	
	    	UtilScoreboard.addPlayerToTeam(board, getTeamList().get(p).Name(), p);
	    	p.setScoreboard(board);
	    }
	    
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
	
	@EventHandler
	public void SpectaterAndRespawn(PlayerRespawnEvent ev){
		if(getGameList().isPlayerState(ev.getPlayer())==PlayerState.OUT){
			SetSpectator(ev,ev.getPlayer());
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void RestartQuit(PlayerQuitEvent ev){
		if(getGameList().getPlayers().containsKey(ev.getPlayer())){
			if(TeamList.containsKey(ev.getPlayer())){
				TeamList.remove(ev.getPlayer());
			}
			
			if(isState(GameState.Restart)||isState(GameState.LobbyPhase))return;
			getGameList().addPlayer(ev.getPlayer(), PlayerState.OUT);
			if(islastTeam()&&(getState()==GameState.InGame||getState()==GameState.DeathMatch)){
				setState(GameState.Restart,GameStateChangeReason.LAST_TEAM);
			}else if(getGameList().getPlayers(PlayerState.IN).size()<=1){
				setState(GameState.Restart,GameStateChangeReason.LAST_PLAYER);
			}
		}
	}
	
	public void SetSpectator(PlayerRespawnEvent ev,Player player){
		delTeam(player);
		getGameList().addPlayer(player, PlayerState.OUT);
	    getGames().getManager().Clear(player);
	    List<Player> l = getGameList().getPlayers(PlayerState.IN);
	    if(l.size()>1){
	    	if(ev==null){
		    	player.teleport(l.get(UtilMath.r(l.size())).getLocation().add(0.0D,3.5D,0.0D));
	    	}else{
	    		ev.setRespawnLocation(l.get(UtilMath.r(l.size())).getLocation().add(0.0D,3.5D,0.0D));
	    	}
	    }else{
	    	if(ev==null){
	    		player.teleport(getGames().getManager().getLobby());
	    	}else{
	    		ev.setRespawnLocation(getGames().getManager().getLobby());
	    	}
	    	setState(GameState.Restart,GameStateChangeReason.LAST_PLAYER);
	    }
	    player.setGameMode(GameMode.CREATIVE);
	    player.setFlying(true);
	    player.setFlySpeed(0.1F);
	    player.getInventory().setItem(8,UtilItem.RenameItem(new ItemStack(385), "§aZurück zur Lobby"));
	    for(Player p : UtilServer.getPlayers()){
	    	p.hidePlayer(player);
	    }
	    
	    if(islastTeam()&& (getState()==GameState.InGame||getState()==GameState.DeathMatch)){
			setState(GameState.Restart,GameStateChangeReason.LAST_TEAM);
		}else if(getGameList().getPlayers(PlayerState.IN).size()<=1){
			setState(GameState.Restart,GameStateChangeReason.LAST_PLAYER);
		}

	    Bukkit.getPluginManager().callEvent(new KitShopPlayerDeleteEvent(player));
	}

	public void addTeam(Player p, Team t){
		TeamList.put(p, t);
		Bukkit.getPluginManager().callEvent(new TeamAddEvent(p,t));
	}
	
	@EventHandler(priority=EventPriority.NORMAL,ignoreCancelled=true)
	public void arrow_damage(EntityDamageByEntityEvent ev){
		if(ev.getEntity() instanceof Player && ev.getDamager() instanceof Arrow){
			Arrow a = (Arrow)ev.getDamager();
			if(!(a.getShooter() instanceof Player))return;
			Player d = (Player)a.getShooter();
			Player v = (Player)ev.getEntity();

			if(!getGameList().getPlayers().containsKey( d ))return;
			if(!getGameList().getPlayers().containsKey( v ))return;
			
			if(!DamageTeamSelf&&getTeam(d)==getTeam(v)){
				if(getGames().getManager().getService().isDebug())System.err.println("[MultiGame] Cancelled TRUE bei DamageTeamSelf Projectile");
				ev.setCancelled(true);
			}else if(!DamageTeamOther&&getTeam(d)!=getTeam(v)){
				if(getGames().getManager().getService().isDebug())System.err.println("[MultiGame] Cancelled TRUE bei DamageTeamOther Projectile");
				ev.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority=EventPriority.NORMAL,ignoreCancelled=true)
	public void TeamDamage(EntityDamageByEntityEvent ev){
		if((ev.getEntity() instanceof Player && ev.getDamager() instanceof Player)){
			if(ev.getDamager() instanceof Player && !getGameList().getPlayers().containsKey( ((Player)ev.getDamager()) ))return;
			if(ev.getEntity() instanceof Player && !getGameList().getPlayers().containsKey( ((Player)ev.getEntity()) ))return;
			
			if(!DamageTeamSelf&&getTeam((Player)ev.getDamager())==getTeam((Player)ev.getEntity())){
				if(getGames().getManager().getService().isDebug())System.err.println("[MultiGame] Cancelled TRUE bei DamageTeamSelf");
				ev.setCancelled(true);
			}else if(!DamageTeamOther&&getTeam((Player)ev.getDamager())!=getTeam((Player)ev.getEntity())){
				if(getGames().getManager().getService().isDebug())System.err.println("[MultiGame] Cancelled TRUE bei DamageTeamOther");
				ev.setCancelled(true);
			}
		}
	}

}
