package me.kingingo.karcade.Game.Multi.Games;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;
import me.kingingo.karcade.kArcade;
import me.kingingo.karcade.Game.GameList;
import me.kingingo.karcade.Game.Multi.MultiGames;
import me.kingingo.karcade.Game.Multi.MultiWorldData;
import me.kingingo.karcade.Game.Multi.Events.MultiGameStartEvent;
import me.kingingo.karcade.Game.Multi.Events.MultiGameStateChangeEvent;
import me.kingingo.karcade.Game.Multi.Events.MultiGameUpdateInfo;
import me.kingingo.kcore.Arena.ArenaType;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.GameStateChangeReason;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.Enum.PlayerState;
import me.kingingo.kcore.Enum.Team;
import me.kingingo.kcore.Language.Language;
import me.kingingo.kcore.Listener.kListener;
import me.kingingo.kcore.Packet.Packets.ARENA_STATUS;
import me.kingingo.kcore.StatsManager.Stats;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.Color;
import me.kingingo.kcore.Util.Title;
import me.kingingo.kcore.Util.UtilBG;
import me.kingingo.kcore.Util.UtilDisplay;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.UtilScoreboard;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.kcore.Versus.PlayerKit;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.scoreboard.Scoreboard;

public class MultiGame extends kListener{
	@Getter
	private MultiGames games;
	@Getter
	@Setter
	private int startCountdown=11;
	@Getter
	private GameState state=GameState.Laden;
	@Getter
	@Setter
	private boolean Damage = true;
	@Setter
	@Getter
	private boolean blockPlace=true;
	@Setter
	@Getter
	private boolean blockBreak=true;
	@Setter
	@Getter
	private boolean ProjectileDamage=true;
	@Getter
	@Setter
	private boolean DamagePvP = true;
	@Getter
	@Setter
	private boolean DamagePvE = true;
	@Getter
	@Setter
	private boolean DamageEvP = true;
	@Getter
	@Setter
	private boolean DamageSelf = true;
	@Getter
	@Setter
	private boolean foodlevelchange = true;
	@Getter
	@Setter
	private boolean pickItem = true;
	@Getter
	@Setter
	private boolean dropItem = true;
	@Getter
	@Setter
	private boolean dropItembydeath = true;
	@Getter
	private ArrayList<DamageCause> EntityDamage = new ArrayList<>();
	@Getter
	private GameList gameList;
	@Getter
	@Setter
	private int timer=-1; // FÜR DIE SCHEDULER EIN TIMER
	@Getter
	@Setter
	private String arena="arena";
	@Getter
	@Setter
	private String Map="loading...";
	@Getter
	@Setter
	private String updateTo = "hub";
	@Getter
	@Setter
	private PlayerKit kit;
	@Getter
	private Location pasteLocation;
	@Getter
	@Setter 
	private int min_team=0;
	@Getter
	@Setter 
	private int max_team=0;
	@Getter
	private int teams=0;
	@Getter
	@Setter 
	private int team=0;
	@Getter
	private ArenaType type;
	
	public MultiGame(MultiGames games,String Map,Location pasteLocation) {
		super(games.getManager().getInstance(), "MultiGame:Arena"+games.getGames().size());
		this.games=games;
		this.Map=Map;
		this.pasteLocation=pasteLocation;
		this.gameList=new GameList(games.getManager());
		this.arena="arena"+games.getGames().size();
		this.min_team=2;
		this.max_team=0;
	}
	
	public void loadMaxTeam(){
		setMax_team(0);
		for(Team team : games.getSpielerTeams()){
			if(getWorldData().existLoc(this, team)&&!getWorldData().getLocs(this, team).isEmpty()){
				setMax_team(getMax_team()+1);
			}
		}
		Log("Max Team: "+getMax_team());
	}
	
	public void setType(ArenaType type){
		this.type=type;
		setTeam(type.getTeam().length);
	}
	
	public MultiWorldData getWorldData(){
		return getGames().getWorldData();
	}
	
	public void updateInfo(){
		updateInfo(null, null, null, true);
	}
	
	//SENDET DEN AKTUELLEN STATUS DER ARENA DEN HUB SERVER!
	public void updateInfo(GameState state,GameType type,String arena,boolean apublic){
		MultiGameUpdateInfo ev = new MultiGameUpdateInfo(this, new ARENA_STATUS( (state!=null ? state : getState()) , getGameList().getPlayers(PlayerState.IN).size(),team , (type!=null ? type : getGames().getType()),"a"+kArcade.id , (arena!=null ? arena : getArena()) , apublic, getMap(),min_team,max_team,(kit==null?"null":kit.kit) ));
		Bukkit.getPluginManager().callEvent(ev);
		if(ev.isCancelled())return;
		getGames().getManager().getPacketManager().SendPacket(updateTo, ev.getPacket());
	}
	
	public void broadcastWithPrefix1(String name){
		for(Player player : getGameList().getPlayers().keySet())player.sendMessage(Language.getText(player,"PREFIX_GAME",getGames().getType().getTyp())+Language.getText(player,name));
	}
	
	public void broadcastWithPrefix(String name,Object input){
		for(Player player : getGameList().getPlayers().keySet())player.sendMessage(Language.getText(player,"PREFIX_GAME",getGames().getType().getTyp())+Language.getText(player,name,input));
	}
	
	public void broadcastWithPrefix(String name,Object[] input){
		for(Player player : getGameList().getPlayers().keySet())player.sendMessage(Language.getText(player,"PREFIX_GAME",getGames().getType().getTyp())+Language.getText(player,name,input));
	}
	
	public void broadcastWithPrefix(String msg){
		for(Player player : getGameList().getPlayers().keySet())player.sendMessage(Language.getText(player,"PREFIX_GAME",getGames().getType().getTyp())+msg);
	}
	
	public void broadcast(String msg){
		for(Player player : getGameList().getPlayers().keySet())player.sendMessage(msg);
	}
	
	public void broadcast(String name,Object[] input){
		for(Player player : getGameList().getPlayers().keySet())player.sendMessage(Language.getText(player,name,input));
	}
	
	public void setTab(Player p,Color enemy, Color friend){
		Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
		UtilScoreboard.addTeam(board, "friend", friend.toString());
		UtilScoreboard.addTeam(board, "enemy", enemy.toString());
		
		for(Player player : UtilServer.getPlayers()){
			if(player.getUniqueId()==p.getUniqueId()){
				UtilScoreboard.addPlayerToTeam(board, "enemy", player);
			}else{
				UtilScoreboard.addPlayerToTeam(board, "friend", player);
			}
		}
		
		p.setScoreboard(board);
	}
	
	@EventHandler
	public void death(PlayerDeathEvent ev){
		ev.setDeathMessage(null);
		if(getGameList().getPlayers().containsKey(ev.getEntity().getKiller())){
			if(getGames().getManager().getService().isDebug())System.err.println("[MultiGame] Player Death Drops Clear");
			if(!dropItembydeath)ev.getDrops().clear();
		}
	}
	
	@EventHandler
	public void drop(PlayerDropItemEvent ev){
		if(getGameList().getPlayers().containsKey(ev.getPlayer())){
			if(!dropItem||getGameList().getPlayers(PlayerState.OUT).contains(ev.getPlayer())){
				if(getGames().getManager().getService().isDebug())System.err.println("[MultiGame] PlayerDropItem Cancelled TRUE!");
				ev.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void pickup(PlayerPickupItemEvent ev){
		if(getGameList().getPlayers().containsKey(ev.getPlayer())){
			if(!pickItem||getGameList().getPlayers(PlayerState.OUT).contains(ev.getPlayer())){
				if(getGames().getManager().getService().isDebug())System.err.println("[MultiGame] PlayerPickUP Cancelled TRUE!");
				ev.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void bplace(BlockPlaceEvent ev){
		if(getGameList().getPlayers().containsKey(ev.getPlayer())){
			if(!blockPlace||getGameList().getPlayers(PlayerState.OUT).contains(ev.getPlayer())){
				if(getGames().getManager().getService().isDebug())System.err.println("[MultiGame] BlockPlace Cancelled TRUE!");
				ev.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void bbreak(BlockBreakEvent ev){
		if(getGameList().getPlayers().containsKey(ev.getPlayer())){
			if(!blockBreak||getGameList().getPlayers(PlayerState.OUT).contains(ev.getPlayer())){
				if(getGames().getManager().getService().isDebug())System.err.println("[MultiGame] BlockBreak Cancelled TRUE!");
				ev.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void hunger(FoodLevelChangeEvent ev){
		if(ev.getEntity() instanceof Player&&getGameList().getPlayers().containsKey(ev.getEntity())){
			if(!foodlevelchange){
				if(getGames().getManager().getService().isDebug())System.err.println("[MultiGame] FoodLevelChange Cancelled TRUE!");
				ev.setCancelled(true);
			}
		}
	}
	
	public void broadcast(String name,Object input){
		for(Player player : getGameList().getPlayers().keySet())player.sendMessage(Language.getText(player,name,input));
	}
	
	public boolean isState(GameState gs){
		return gs==getState();
	}
	
	public void setState(GameState gs){
		setState(gs, GameStateChangeReason.CUSTOM,true);
	}
	
	public void setState(GameState gs,boolean update){
		setState(gs, GameStateChangeReason.CUSTOM,update);
	}

	public void setState(GameState gs,GameStateChangeReason reason){
		setState(gs, reason, true);
	}
	
	@EventHandler(ignoreCancelled=false)
	public void Interact(PlayerInteractEvent ev){
		if(getGameList().getPlayers(PlayerState.OUT).contains(ev.getPlayer())){
			if(ev.getPlayer().getItemInHand()==null)return;
			if(UtilEvent.isAction(ev, ActionType.R)&&ev.getPlayer().getItemInHand().getType()==Material.FIREBALL){
				ev.setCancelled(true);
				UtilBG.sendToServer(ev.getPlayer(), getGames().getManager().getInstance());
			}
		}
	}
	
	public void setState(GameState gs,GameStateChangeReason reason,boolean update){
		MultiGameStateChangeEvent stateEvent = new MultiGameStateChangeEvent(this,state,gs,reason);
		Bukkit.getPluginManager().callEvent(stateEvent);
		if(stateEvent.isCancelled())return;
		this.state=gs;
		if(update)updateInfo();
		Log("GameState wurde zu "+state.string()+"("+reason.name()+") geändert.");
	}
	
	public boolean isPlayerState(Player player,PlayerState state){
		if(!getGameList().getPlayers().containsKey(player))return false;
		return (getGameList().isPlayerState(player)==state);
	}
	
	Title t;
	Player last_player;
	Team last_team;
	@EventHandler
	public void StateChange(MultiGameStateChangeEvent ev){
		if(ev.getGame()!=this)return;
		if(ev.getTo()==GameState.Restart&&ev.getFrom()!=ev.getTo()){
			if(getGames().getStats()!=null){
				for(Player player : getGameList().getPlayers().keySet())getGames().getStats().SaveAllPlayerData(player);
			}
			
			if(this instanceof MultiTeamGame && (((MultiTeamGame)this).islastTeam()||ev.getReason()==GameStateChangeReason.LAST_TEAM)){
				for(Player player : getGameList().getPlayers(PlayerState.IN)){
					getGames().getStats().setInt(player, getGames().getStats().getInt(Stats.WIN, player)+1, Stats.WIN);
				}
				
				last_team = ((MultiTeamGame)this).getlastTeam();
				if(last_team!=null){
					broadcastWithPrefix("TEAM_WIN",last_team.getColor()+last_team.Name());
					t= new Title(last_team.getColor()+last_team.Name(),((MultiTeamGame)this).getTeamMember(last_team));
					for(Player player : getGameList().getPlayers().keySet()){
						t.send(player);
					}
				}
				for(Player player : getGameList().getPlayers().keySet())UtilBG.sendToServer(player, getGames().getManager().getInstance());
				
				((MultiTeamGame)this).getTeamList().clear();
				getGameList().getPlayers().clear();
				setTimer(-1);
				getGames().updatePlayedGames();
			}else if(ev.getReason()==GameStateChangeReason.LAST_PLAYER||ev.getReason()==GameStateChangeReason.GAME_END){
				for(Player player : getGameList().getPlayers(PlayerState.IN)){
					getGames().getStats().setInt(player, getGames().getStats().getInt(Stats.WIN, player)+1, Stats.WIN);
				}
				
				last_player = getGameList().getPlayers(PlayerState.IN).get(0);
				if(last_player!=null){
					broadcastWithPrefix("GAME_WIN",last_player.getName());
					t= new Title("",Language.getText("GAME_WIN",last_player.getName()));
					for(Player player : getGameList().getPlayers().keySet()){
						t.send(player);
					}
				}
				for(Player player : getGameList().getPlayers().keySet())UtilBG.sendToServer(player, getGames().getManager().getInstance());
				
				getGameList().getPlayers().clear();
				setTimer(-1);
				getGames().updatePlayedGames();
			}
		}
	}
	
	public void sendTitle(String first,String second){
		try{
			t= new Title(first,second);
			for(Player player : getGameList().getPlayers().keySet())t.send(player);
		}catch(NullPointerException e){
			e.printStackTrace();
		}
	}
	
	@EventHandler
	public void interact(PlayerInteractEvent ev){
		if(getGameList().getPlayers().containsKey(ev.getPlayer())){
			if(isPlayerState(ev.getPlayer(), PlayerState.OUT))ev.setCancelled(true);
		}
	}
	

	@EventHandler
	public void Restart(UpdateEvent ev){
		if(ev.getType()==UpdateType.SEC){
			if(getState() == GameState.Restart){
				if(getTimer()<0){
					setTimer(7);
				}
				setTimer(getTimer()-1);
				
				if(getTimer()==0){
					setState(GameState.LobbyPhase);
				}
			}
		}
	}
	
	@EventHandler
	public void Start(UpdateEvent ev){
		if(ev.getType()==UpdateType.SEC){
			if(getState() == GameState.LobbyPhase||getState() == GameState.Laden){
				if(getTimer()<0){
					setTimer(getStartCountdown());
				}
				setTimer(getTimer()-1);
				
				for(Player p : getGameList().getPlayers().keySet()){
					UtilDisplay.displayTextBar(p, Language.getText(p, "GAME_START_IN",getTimer()));
				}
				
				if(getTimer()!=0){
					switch(getTimer()){
					case 30:broadcastWithPrefix("GAME_START_IN", getTimer());break;
					case 15:broadcastWithPrefix("GAME_START_IN", getTimer());break;
					case 10:broadcastWithPrefix("GAME_START_IN", getTimer());break;
					case 5:broadcastWithPrefix("GAME_START_IN", getTimer()); sendTitle(Color.RED+getTimer(),"");break;
					case 4:broadcastWithPrefix("GAME_START_IN", getTimer()); sendTitle(Color.RED+getTimer(),"");break;
					case 3:broadcastWithPrefix("GAME_START_IN", getTimer()); sendTitle(Color.RED+getTimer(),"");break;
					case 2:broadcastWithPrefix("GAME_START_IN", getTimer()); sendTitle(Color.RED+getTimer(),"");break;
					case 1:broadcastWithPrefix("GAME_START_IN", getTimer()); sendTitle(Color.RED+getTimer(),"");break;
					}
				}else{
					if(startBereit()){
						sendTitle(Color.GREEN+"LOS!","");
						Bukkit.getPluginManager().callEvent(new MultiGameStartEvent(this));
						setTimer(-1);
					}else{
						setTimer(11);
						updateInfo();
						broadcastWithPrefix1("GAME_START_MIN_PLAYER2");
					}
				}
			}
		}
	}
	
	public boolean startBereit(){
		if(getGameList().getPlayers().isEmpty())return false;
		
		for(Player player : getGameList().getPlayers().keySet()){
			if(!player.isOnline()){
				return false;
			}
		}
		return true;
	}
	
	@EventHandler
	public void Damage(EntityDamageEvent ev){
		//ENTITY IST PLAYER?
		if(ev.getEntity() instanceof Player){
			//IS PLAYER IN ARENA?
			if(ev.getEntity() instanceof Player && !getGameList().getPlayers().containsKey( ((Player)ev.getEntity()) ))return;
			if(!Damage)ev.setCancelled(true);
			if(isState(GameState.LobbyPhase)||isState(GameState.Laden))ev.setCancelled(true);
			if(EntityDamage.contains(ev.getCause()))ev.setCancelled(true);
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void MultiGamestart(MultiGameStartEvent ev){
		if(ev.getGame()==this){
			for(Player p : getGameList().getPlayers().keySet()){
				for(Player p1 : getGameList().getPlayers().keySet()){
					p.hidePlayer(p1);
					p1.hidePlayer(p);
				}
			}
			
			for(Player p : getGameList().getPlayers().keySet()){
				for(Player p1 : getGameList().getPlayers().keySet()){
					p.hidePlayer(p1);
					p.showPlayer(p1);
					p1.showPlayer(p);
				}
			}
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void EntityDamageByEntity(EntityDamageByEntityEvent ev){
		if(ev.getDamager() instanceof Player && !getGameList().getPlayers().containsKey( ((Player)ev.getDamager()) ))return;
		if(ev.getEntity() instanceof Player && !getGameList().getPlayers().containsKey( ((Player)ev.getEntity()) ))return;
		
		if((ev.getDamager() instanceof Player && isPlayerState((Player)ev.getDamager(), PlayerState.OUT)) || !Damage || isState(GameState.LobbyPhase)|| isState(GameState.Laden)){
			if(getGames().getManager().getService().isDebug())System.err.println("[MultiGame] Cancelled TRUE bei Damage  "+getState());
			ev.setCancelled(true);
		}else if((ev.getEntity() instanceof Player && ev.getDamager() instanceof Player)&&!DamagePvP){
			if(!isPlayerState((Player)ev.getDamager(), PlayerState.IN))return;
			if(!isPlayerState((Player)ev.getEntity(), PlayerState.IN))return;
			//P vs P
			if(getGames().getManager().getService().isDebug())System.err.println("[MultiGame] Cancelled TRUE bei DamagePvP");
			ev.setCancelled(true);
		}else if(((ev.getEntity() instanceof Player && ev.getDamager() instanceof Creature))&&!DamageEvP){
			if(!isPlayerState((Player)ev.getEntity(), PlayerState.IN))return;
			//E vs P
			if(getGames().getManager().getService().isDebug())System.err.println("[MultiGame] Cancelled TRUE bei DamageEvP");
			ev.setCancelled(true);
		}else if ( ((ev.getDamager() instanceof Player && ev.getEntity() instanceof Creature))&&!DamagePvE){
			if(!isPlayerState((Player)ev.getDamager(), PlayerState.IN))return;
			if(getGames().getManager().getService().isDebug())System.err.println("[MultiGame] Cancelled TRUE bei DamagePvE");
			//P vs E
			ev.setCancelled(true);
		}else if((ev.getDamager() instanceof Arrow||ev.getDamager() instanceof Snowball||ev.getDamager() instanceof Egg)&&!ProjectileDamage){
			if(ev.getDamager() instanceof Projectile && ((Projectile)ev.getDamager()).getShooter() instanceof Player && !isPlayerState((Player)((Projectile)ev.getDamager()).getShooter(), PlayerState.IN))return;
			if(getGames().getManager().getService().isDebug())System.err.println("[MultiGame] Cancelled TRUE bei ProjectileDamage");
			ev.setCancelled(true);
		}
	}
	
}
