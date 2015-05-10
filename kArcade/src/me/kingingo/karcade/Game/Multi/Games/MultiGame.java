package me.kingingo.karcade.Game.Multi.Games;

import java.util.ArrayList;
import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;
import me.kingingo.karcade.Enum.GameStateChangeReason;
import me.kingingo.karcade.Enum.PlayerState;
import me.kingingo.karcade.Enum.Team;
import me.kingingo.karcade.Game.GameList;
import me.kingingo.karcade.Game.Events.GameStateChangeEvent;
import me.kingingo.karcade.Game.Events.TeamAddEvent;
import me.kingingo.karcade.Game.Events.TeamDelEvent;
import me.kingingo.karcade.Game.Multi.MultiGames;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Listener.kListener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class MultiGame extends kListener{
	@Getter
	private MultiGames games;
	@Getter
	private GameState state;
	@Getter
	@Setter
	public boolean Damage = true;
	@Setter
	@Getter
	public boolean ProjectileDamage=true;
	@Getter
	@Setter
	public boolean DamagePvP = true;
	@Getter
	@Setter
	public boolean DamagePvE = true;
	@Getter
	@Setter
	public boolean DamageEvP = true;
	@Getter
	@Setter
	public boolean DamageSelf = true;
	@Getter
	@Setter
	public boolean DamageTeamSelf = false;
	@Getter
	@Setter
	public boolean DamageTeamOther = true;
	@Getter
	private ArrayList<DamageCause> EntityDamage = new ArrayList<>();
	@Getter
	private GameList gameList;
	@Getter
	HashMap<Player,Team> TeamList = new HashMap<>();
	
	
	public MultiGame(MultiGames games) {
		super(games.getManager().getInstance(), "MultiGame");
		this.games=games;
		this.gameList=new GameList(games.getManager());
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
	
	public boolean isState(GameState gs){
		return gs==getState();
	}
	
	public void setState(GameState gs){
		setState(gs, GameStateChangeReason.CUSTOM);
	}
	
	public void setState(GameState gs,GameStateChangeReason reason){
		GameStateChangeEvent stateEvent = new GameStateChangeEvent(state,gs,reason);
		Bukkit.getPluginManager().callEvent(stateEvent);
		if(stateEvent.isCancelled())return;
		state=gs;
		Log("GameState wurde zu "+state.string()+" geändert.");
	}
	
	public boolean isPlayerState(Player player,PlayerState state){
		if(!getGameList().getPlayers().containsKey(player))return false;
		return (getGameList().isPlayerState(player)==state);
	}
	
	@EventHandler
	public void Damage(EntityDamageEvent ev){
		if(ev.getEntity() instanceof Player){
			if(isPlayerState((Player)ev.getEntity(), PlayerState.IN)){
				if(!Damage)ev.setCancelled(true);
				if(isState(GameState.LobbyPhase))ev.setCancelled(true);
				if(EntityDamage.contains(ev.getCause()))ev.setCancelled(true);
				
			}
		}
	}
	
	@EventHandler(priority=EventPriority.NORMAL,ignoreCancelled=true)
	public void arrow_damage(EntityDamageByEntityEvent ev){
		if(ev.getEntity() instanceof Player && ev.getDamager() instanceof Arrow){
			Arrow a = (Arrow)ev.getDamager();
			if(!(a.getShooter() instanceof Player))return;
			Player d = (Player)a.getShooter();
			Player v = (Player)ev.getEntity();
			if( !(isPlayerState(v, PlayerState.IN)&&isPlayerState(d, PlayerState.OUT)) )return;
			if(!DamageTeamSelf&&getTeam(d)==getTeam(v)){
				if(getGames().getManager().getService().isDamage())System.err.println("[Game] Cancelled TRUE bei DamageTeamSelf Projectile");
				ev.setCancelled(true);
			}else if(!DamageTeamOther&&getTeam(d)!=getTeam(v)){
				if(getGames().getManager().getService().isDamage())System.err.println("[Game] Cancelled TRUE bei DamageTeamOther Projectile");
				ev.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority=EventPriority.NORMAL,ignoreCancelled=true)
	public void TeamDamage(EntityDamageByEntityEvent ev){
		if((ev.getEntity() instanceof Player && ev.getDamager() instanceof Player)){
			if( !(isPlayerState((Player)ev.getEntity(), PlayerState.IN)&&isPlayerState((Player)ev.getDamager(), PlayerState.OUT)) )return;
			if(!DamageTeamSelf&&getTeam((Player)ev.getDamager())==getTeam((Player)ev.getEntity())){
				if(getGames().getManager().getService().isDamage())System.err.println("[Game] Cancelled TRUE bei DamageTeamSelf");
				ev.setCancelled(true);
			}else if(!DamageTeamOther&&getTeam((Player)ev.getDamager())!=getTeam((Player)ev.getEntity())){
				if(getGames().getManager().getService().isDamage())System.err.println("[Game] Cancelled TRUE bei DamageTeamOther");
				ev.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void EntityDamageByEntity(EntityDamageByEntityEvent ev){
		if((ev.getDamager() instanceof Player && isPlayerState((Player)ev.getDamager(), PlayerState.OUT)) || !Damage || isState(GameState.LobbyPhase)){
			if(getGames().getManager().getService().isDamage())System.err.println("[Game] Cancelled TRUE bei Damage");
			ev.setCancelled(true);
		}else if((ev.getEntity() instanceof Player && ev.getDamager() instanceof Player)&&!DamagePvP){
			if(!isPlayerState((Player)ev.getDamager(), PlayerState.IN))return;
			if(!isPlayerState((Player)ev.getEntity(), PlayerState.IN))return;
			//P vs P
			if(getGames().getManager().getService().isDamage())System.err.println("[Game] Cancelled TRUE bei DamagePvP");
			ev.setCancelled(true);
		}else if(((ev.getEntity() instanceof Player && ev.getDamager() instanceof Creature))&&!DamageEvP){
			if(!isPlayerState((Player)ev.getEntity(), PlayerState.IN))return;
			//E vs P
			if(getGames().getManager().getService().isDamage())System.err.println("[Game] Cancelled TRUE bei DamageEvP");
			ev.setCancelled(true);
		}else if ( ((ev.getDamager() instanceof Player && ev.getEntity() instanceof Creature))&&!DamagePvE){
			if(!isPlayerState((Player)ev.getDamager(), PlayerState.IN))return;
			if(getGames().getManager().getService().isDamage())System.err.println("[Game] Cancelled TRUE bei DamagePvE");
			//P vs E
			ev.setCancelled(true);
		}else if((ev.getDamager() instanceof Arrow||ev.getDamager() instanceof Snowball||ev.getDamager() instanceof Egg)&&!ProjectileDamage){
			if(ev.getDamager() instanceof Projectile && ((Projectile)ev.getDamager()).getShooter() instanceof Player && !isPlayerState((Player)((Projectile)ev.getDamager()).getShooter(), PlayerState.IN))return;
			if(getGames().getManager().getService().isDamage())System.err.println("[Game] Cancelled TRUE bei ProjectileDamage");
			ev.setCancelled(true);
		}
	}
	
}
