package me.kingingo.karcade.Game.Multi.Games;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import me.kingingo.karcade.kArcade;
import me.kingingo.karcade.Enum.PlayerState;
import me.kingingo.karcade.Game.GameList;
import me.kingingo.karcade.Game.Events.TeamAddEvent;
import me.kingingo.karcade.Game.Events.TeamDelEvent;
import me.kingingo.karcade.Game.Multi.MultiGames;
import me.kingingo.karcade.Game.Multi.Events.MultiGamePlayerJoinEvent;
import me.kingingo.karcade.Game.Multi.Events.MultiGameStartEvent;
import me.kingingo.karcade.Game.Multi.Events.MultiGameStateChangeEvent;
import me.kingingo.karcade.Game.Multi.Events.MultiGameUpdateInfo;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.GameStateChangeReason;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.Enum.Team;
import me.kingingo.kcore.Kit.Shop.Events.KitShopPlayerDeleteEvent;
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
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilMath;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.kcore.Versus.PlayerKit;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
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
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class MultiGame extends kListener{
	@Getter
	private MultiGames games;
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
	private boolean DamageTeamSelf = false;
	@Getter
	@Setter
	private boolean DamageTeamOther = true;
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
	private HashMap<Player,Team> TeamList = new HashMap<>();
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
	private int min_team=0;
	@Getter
	@Setter 
	private int max_team=0;
	@Getter
	@Setter 
	private int team=0;
	@Getter
	@Setter
	private PlayerKit kit;
	private Location location;
	
	public MultiGame(MultiGames games,Location location) {
		super(games.getManager().getInstance(), "MultiGame");
		this.games=games;
		this.location=location;
		this.gameList=new GameList(games.getManager());
		this.arena="arena"+games.getGames().size();
	}
	
	public void updateInfo(){
		updateInfo(null, 0, null, null, true);
	}
	
	//SENDET DEN AKTUELLEN STATUS DER ARENA DEN HUB SERVER!
	public void updateInfo(GameState state,int teams,GameType type,String arena,boolean apublic){
		MultiGameUpdateInfo ev = new MultiGameUpdateInfo(this, new ARENA_STATUS( (state!=null ? state : getState()) , getGameList().getPlayers(PlayerState.IN).size(),(teams>0 ? teams : getGames().getLocs().get(this).size()),team , (type!=null ? type : getGames().getType()),"a"+kArcade.id , (arena!=null ? arena : getArena()) , apublic, getMap(),min_team,max_team,(kit==null?"null":kit.kit) ));
		Bukkit.getPluginManager().callEvent(ev);
		if(ev.isCancelled())return;
		getGames().getManager().getPacketManager().SendPacket(updateTo, ev.getPacket());
	}

	public void addTeam(Player p, Team t){
		TeamList.put(p, t);
		Bukkit.getPluginManager().callEvent(new TeamAddEvent(p,t));
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
			if(!dropItem){
				if(getGames().getManager().getService().isDebug())System.err.println("[MultiGame] PlayerDropItem Cancelled TRUE!");
				ev.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void pickup(PlayerPickupItemEvent ev){
		if(getGameList().getPlayers().containsKey(ev.getPlayer())){
			if(!pickItem){
				if(getGames().getManager().getService().isDebug())System.err.println("[MultiGame] PlayerPickUP Cancelled TRUE!");
				ev.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void bplace(BlockPlaceEvent ev){
		if(getGameList().getPlayers().containsKey(ev.getPlayer())){
			if(!blockPlace){
				if(getGames().getManager().getService().isDebug())System.err.println("[MultiGame] BlockPlace Cancelled TRUE!");
				ev.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void bbreak(BlockBreakEvent ev){
		if(getGameList().getPlayers().containsKey(ev.getPlayer())){
			if(!blockBreak){
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
	
	//Gibt wieder ob nur noch ein Team übrig ist!
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
		MultiGameStateChangeEvent stateEvent = new MultiGameStateChangeEvent(this,state,gs,reason);
		Bukkit.getPluginManager().callEvent(stateEvent);
		if(stateEvent.isCancelled())return;
		this.state=gs;
		updateInfo();
		Log("GameState wurde zu "+state.string()+"("+reason.name()+") geändert.");
	}
	
	public boolean isPlayerState(Player player,PlayerState state){
		if(!getGameList().getPlayers().containsKey(player))return false;
		return (getGameList().isPlayerState(player)==state);
	}
	
	Title t;
	Team last;
	@EventHandler
	public void StateChange(MultiGameStateChangeEvent ev){
		if(ev.getGame()!=this)return;
		if(ev.getTo()==GameState.Restart){
			//Wenn nur noch ein Team da ist
			if(islastTeam()||ev.getReason()==GameStateChangeReason.LAST_TEAM||ev.getReason()==GameStateChangeReason.LAST_PLAYER||ev.getReason()==GameStateChangeReason.GAME_END){
				for(Player player : getGameList().getPlayers(PlayerState.IN)){
					getGames().getStats().setInt(player, getGames().getStats().getInt(Stats.WIN, player)+1, Stats.WIN);
				}
				
				last = getlastTeam();
				if(last!=null){
					broadcastWithPrefix("TEAM_WIN",last.getColor()+last.Name());
					t= new Title("","");
					for(Player player : getGameList().getPlayers().keySet()){
						t.setSubtitle(Language.getText(player,"TEAM_WIN", last.getColor()+last.Name()));
						t.send(player);
					}
				}
				setState(GameState.LobbyPhase);
				ev.setCancelled(true);
				for(Player player : getGameList().getPlayers().keySet())UtilBG.sendToServer(player, getGames().getManager().getInstance());
				
				getTeamList().clear();
				getGameList().getPlayers().clear();
				timer=-1;
			}
		}
	}
	
	public void sendTitle(String first,String second){
		try{
			t= new Title(first,second);
			for(Player player : getGameList().getPlayers().keySet())t.send(player);
		}catch(NullPointerException e){
			
		}
	}
	
	@EventHandler
	public void SpectaterAndRespawn(PlayerRespawnEvent ev){
		if(getGameList().getPlayers(PlayerState.OUT).contains(ev.getPlayer())){
			ev.setRespawnLocation(getGameList().getPlayers(PlayerState.IN).get(0).getLocation());
			SetSpectator(ev,ev.getPlayer());
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void RestartQuit(PlayerQuitEvent ev){
		if(getGameList().getPlayers().containsKey(ev.getPlayer())){
			if(TeamList.containsKey(ev.getPlayer())){
				TeamList.remove(ev.getPlayer());
			}
			
			if(getGameList().getPlayers().isEmpty()||isState(GameState.Laden)){
				setState(GameState.LobbyPhase);
			}
			
			if(isState(GameState.Laden)||isState(GameState.LobbyPhase)){
				if(islastTeam()){
					setState(GameState.Restart,GameStateChangeReason.LAST_TEAM);
				}
				return;
			}
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
	    for(Player p : UtilServer.getPlayers()){
	    	p.hidePlayer(player);
	    }
	    player.getInventory().setItem(8,UtilItem.RenameItem(new ItemStack(385), "§aZurück zur Lobby"));
	    
	    if(islastTeam()&& (getState()==GameState.InGame||getState()==GameState.DeathMatch)){
			setState(GameState.Restart,GameStateChangeReason.LAST_TEAM);
		}else if(getGameList().getPlayers(PlayerState.IN).size()<=1){
			setState(GameState.Restart,GameStateChangeReason.LAST_PLAYER);
		}

	    Bukkit.getPluginManager().callEvent(new KitShopPlayerDeleteEvent(player));
	}
	
	@EventHandler
	public void interact(PlayerInteractEvent ev){
		if(getGameList().getPlayers().containsKey(ev.getPlayer())){
			if(isPlayerState(ev.getPlayer(), PlayerState.OUT))ev.setCancelled(true);
		}
	}
	
	@EventHandler
	public void Start(UpdateEvent ev){
		if(ev.getType()==UpdateType.SEC){
			if(getState() == GameState.LobbyPhase||getState() == GameState.Laden){
				if(getTimer()<0){
					setTimer(11);
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
		if(getTeamList().isEmpty())return false;
		
		for(Player player : getTeamList().keySet()){
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
	
	@EventHandler
	public void Join(MultiGamePlayerJoinEvent ev){
		if(ev.getGame()!=this)return;
		//Prüft ob dieser Spieler für die Arena angemeldet ist.
		if(getTeamList().containsKey(ev.getPlayer())){
			//Spieler wird zu der Location des Teams teleportiert
			
			System.out.println("JOIN: "+ev.getPlayer().getName()+" "+getTeamList().containsKey(ev.getPlayer()));
			
			if(getTeamList().containsKey(ev.getPlayer())){
				System.out.println("JOIN1:"+getTeamList().get(ev.getPlayer()).Name()+" "+getGames().getLocs().get(this).containsKey(getTeamList().get(ev.getPlayer())));
				
				for(Team t : getGames().getLocs().get(this).keySet()){
					System.out.println("T: "+t.Name());
				}
				
				if(getGames().getLocs().get(this).containsKey(getTeamList().get(ev.getPlayer()))){
					System.out.println("JOIN2: "+getGames().getLocs().get(this).get(getTeamList().get(ev.getPlayer())).contains(0));
				}
			}
			
			ev.getPlayer().teleport( getGames().getLocs().get(this).get(getTeamList().get(ev.getPlayer())).get(0).add(0, 0.5, 0) );
			setTimer(-1);
			ev.setCancelled(true);
			updateInfo();
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
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void start(MultiGameStartEvent ev){
		if(ev.getGame()!=this)return;
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
