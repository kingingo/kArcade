package eu.epicpvp.karcade.Game.Multi.Games;

import java.util.ArrayList;

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
import org.bukkit.event.block.BlockBurnEvent;
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

import dev.wolveringer.dataserver.gamestats.GameState;
import dev.wolveringer.dataserver.gamestats.GameType;
import dev.wolveringer.dataserver.gamestats.StatsKey;
import eu.epicpvp.karcade.kArcade;
import eu.epicpvp.karcade.Game.GameList;
import eu.epicpvp.karcade.Game.Multi.MultiGames;
import eu.epicpvp.karcade.Game.Multi.MultiWorldData;
import eu.epicpvp.karcade.Game.Multi.Events.MultiGameStartEvent;
import eu.epicpvp.karcade.Game.Multi.Events.MultiGameStateChangeEvent;
import eu.epicpvp.karcade.Game.Multi.Events.MultiGameUpdateInfoEvent;
import eu.epicpvp.kcore.Arena.ArenaType;
import eu.epicpvp.kcore.Enum.GameStateChangeReason;
import eu.epicpvp.kcore.Enum.PlayerState;
import eu.epicpvp.kcore.Enum.Team;
import eu.epicpvp.kcore.Enum.Zeichen;
import eu.epicpvp.kcore.Inventory.Item.Buttons.ButtonBase;
import eu.epicpvp.kcore.Listener.kListener;
import eu.epicpvp.kcore.PacketAPI.Packets.WrapperPacketPlayOutWorldBorder;
import eu.epicpvp.kcore.Packets.PacketArenaSettings;
import eu.epicpvp.kcore.Packets.PacketArenaStatus;
import eu.epicpvp.kcore.Packets.PacketArenaWinner;
import eu.epicpvp.kcore.Translation.TranslationHandler;
import eu.epicpvp.kcore.Update.UpdateType;
import eu.epicpvp.kcore.Update.Event.UpdateEvent;
import eu.epicpvp.kcore.Util.Color;
import eu.epicpvp.kcore.Util.Title;
import eu.epicpvp.kcore.Util.UtilBG;
import eu.epicpvp.kcore.Util.UtilDisplay;
import eu.epicpvp.kcore.Util.UtilEvent;
import eu.epicpvp.kcore.Util.UtilEvent.ActionType;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.kcore.Util.UtilScoreboard;
import eu.epicpvp.kcore.Util.UtilServer;
import eu.epicpvp.kcore.Versus.PlayerKit;
import lombok.Getter;
import lombok.Setter;

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
	private int timer=-1; // F§R DIE SCHEDULER EIN TIMER
	@Getter
	@Setter
	private String arena="arena";
	@Getter
	@Setter
	private String Map="loading...";
	@Getter
	@Setter
	private String updateTo = "versushub1";
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
	private int max_team=0; // wird von spiel zu spiel geaendert!
	@Getter
	@Setter 
	private int teams=0; // Maximale Team groesse
	@Getter
	@Setter 
	private int team=0;
	@Getter
	private ArenaType type;
	@Getter
	@Setter 
	private ButtonBase button;
	@Getter
	@Setter 
	private WrapperPacketPlayOutWorldBorder worldBorderPacket;
	
	public MultiGame(MultiGames games,String Map,Location pasteLocation) {
		super(games.getManager().getInstance(), "MultiGame:Arena"+games.getGames().size());
		this.games=games;
		this.Map=Map;
		this.pasteLocation=pasteLocation.clone();
		this.gameList=new GameList(games.getManager());
		this.arena="arena"+games.getGames().size();
		this.min_team=0;
		this.max_team=0;
	}
	
	public void setSettings(PacketArenaSettings settings){
		if(settings.getTeam()==Team.SOLO && this instanceof MultiTeamGame){
			settings.setTeam(((MultiTeamGame)this).littleTeam());
		}
		
//		if(UtilPlayer.isOnline(settings.getPlayer())){
//			((MultiTeamGame)this).getTeamList().put(Bukkit.getPlayer(settings.getPlayer()), settings.getTeam());
//			getGameList().addPlayer(Bukkit.getPlayer(settings.getPlayer()), PlayerState.IN);
//			MultiGamePlayerJoinEvent event=new MultiGamePlayerJoinEvent(Bukkit.getPlayer(settings.getPlayer()),this);
//			Bukkit.getPluginManager().callEvent(event);
//		}else{
			getGames().getWarte_liste().put(settings.getPlayer(), settings);
//		}
		
		setMax_team(settings.getMax_team());
		setMin_team(settings.getMin_team());
		setType(settings.getType());
		setState(GameState.Laden);
	}
	
	public void updateInfoButton(){
		if(button==null)return;
		String players = "";
		for(Player player : getGameList().getPlayers().keySet()){
			if(getGameList().getPlayers().get(player)==PlayerState.INGAME){
				players+="§a"+player.getName()+"§7,";
			}else if(getGameList().getPlayers().get(player)==PlayerState.SPECTATOR){
				players+="§c"+player.getName()+"§7,";
			}else if(getGameList().getPlayers().get(player)==PlayerState.BOTH){
				players+="§d"+player.getName()+"§7,";
			}
		}
		button.setItemStack(UtilItem.setLore(button.getItemStack(), new String[]{"§aMap§7 "+Zeichen.DOUBLE_ARROWS_R.getIcon()+" §e"+getMap(),"§aStatus§7 "+Zeichen.DOUBLE_ARROWS_R.getIcon()+" §e "+getState().name(),"§aSpieler§7 "+Zeichen.DOUBLE_ARROWS_R.getIcon()+"§e "+(players.length()>3?players.substring(0, players.length()-3):"")}));
		button.refreshItemStack();
	}
	
	public void loadMaxTeam(){
		setTeams(0);
		for(Team team : games.getSpielerTeams()){
			if(getWorldData().existLoc(this, team)&&!getWorldData().getLocs(this, team).isEmpty()){
				setTeams(getTeams()+1);
			}
		}
		logMessage("Max Team: "+getTeams());
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
	public void updateInfo(dev.wolveringer.dataserver.gamestats.GameState state,GameType type,String arena,boolean apublic){	
		MultiGameUpdateInfoEvent ev = new MultiGameUpdateInfoEvent(this, new PacketArenaStatus( 
				(state!=null ? state : getState()) 
				, getGameList().getPlayers(PlayerState.INGAME).size()
				,teams
				,team 
				,(type!=null ? type : getGames().getType())
				,"a"+kArcade.id
				,(arena!=null ? arena : getArena())
				,apublic
				,getMap()
				,min_team
				,max_team
				,(kit==null?"null":kit.kit) )  );
		Bukkit.getPluginManager().callEvent(ev);
		if(ev.isCancelled())return;
		updateInfoButton();

		getGames().getManager().getClient().sendPacket(updateTo, ev.getPacket());
	}
	
	public void broadcastWithPrefix1(String name){
		for(Player player : getGameList().getPlayers().keySet())player.sendMessage(TranslationHandler.getText(player,"PREFIX_GAME",getGames().getType().getTyp())+TranslationHandler.getText(player,name));
	}
	
	public void broadcastWithPrefix(String name,Object input){
		for(Player player : getGameList().getPlayers().keySet())player.sendMessage(TranslationHandler.getText(player,"PREFIX_GAME",getGames().getType().getTyp())+TranslationHandler.getText(player,name,input));
	}
	
	public void broadcastWithPrefix(String name,Object[] input){
		for(Player player : getGameList().getPlayers().keySet())player.sendMessage(TranslationHandler.getText(player,"PREFIX_GAME",getGames().getType().getTyp())+TranslationHandler.getText(player,name,input));
	}
	
	public void broadcastWithPrefix(String msg){
		for(Player player : getGameList().getPlayers().keySet())player.sendMessage(TranslationHandler.getText(player,"PREFIX_GAME",getGames().getType().getTyp())+msg);
	}
	
	public void broadcast(String msg){
		for(Player player : getGameList().getPlayers().keySet())player.sendMessage(msg);
	}
	
	public void broadcast(String name,Object[] input){
		for(Player player : getGameList().getPlayers().keySet())player.sendMessage(TranslationHandler.getText(player,name,input));
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
			if(!dropItem||getGameList().getPlayers(PlayerState.SPECTATOR).contains(ev.getPlayer())){
				if(getGames().getManager().getService().isDebug())System.err.println("[MultiGame] PlayerDropItem Cancelled TRUE!");
				ev.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void pickup(PlayerPickupItemEvent ev){
		if(getGameList().getPlayers().containsKey(ev.getPlayer())){
			if(!pickItem||getGameList().getPlayers(PlayerState.SPECTATOR).contains(ev.getPlayer())){
				if(getGames().getManager().getService().isDebug())System.err.println("[MultiGame] PlayerPickUP Cancelled TRUE!");
				ev.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void bplace(BlockPlaceEvent ev){
		if(getGameList().getPlayers().containsKey(ev.getPlayer())){
			if(!blockPlace||getGameList().getPlayers(PlayerState.SPECTATOR).contains(ev.getPlayer())){
				if(getGames().getManager().getService().isDebug())System.err.println("[MultiGame] BlockPlace Cancelled TRUE!");
				ev.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void bbreak(BlockBreakEvent ev){
		if(getGameList().getPlayers().containsKey(ev.getPlayer())){
			if(!blockBreak||getGameList().getPlayers(PlayerState.SPECTATOR).contains(ev.getPlayer())){
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
		for(Player player : getGameList().getPlayers().keySet())player.sendMessage(TranslationHandler.getText(player,name,input));
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
	public void BlockBurn(BlockBurnEvent ev){
		
	}
	
	@EventHandler(ignoreCancelled=false)
	public void Interact(PlayerInteractEvent ev){
		if(getGameList().getPlayers(PlayerState.SPECTATOR).contains(ev.getPlayer())){
			if(ev.getPlayer().getItemInHand()==null)return;
			if(UtilEvent.isAction(ev, ActionType.RIGHT)&&ev.getPlayer().getItemInHand().getType()==Material.FIREBALL){
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
		logMessage("GameState wurde zu "+state.string()+"("+reason.name()+") geändert.");
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
			if(this instanceof MultiTeamGame && (((MultiTeamGame)this).islastTeam()||ev.getReason()==GameStateChangeReason.LAST_TEAM)){
				for(Player player : getGameList().getPlayers(PlayerState.INGAME)){
					
					getGames().getManager().getClient().sendPacket(updateTo, new PacketArenaWinner(player.getUniqueId(), "a"+kArcade.id, getGames().getType(), getArena()));
					getGames().getStats().addInt(player, 1, StatsKey.WIN);
				}
				
				last_team = ((MultiTeamGame)this).getlastTeam();
				if(last_team!=null){
					broadcastWithPrefix("TEAM_WIN",last_team.getColor()+last_team.getDisplayName());
					t= new Title(last_team.getColor()+last_team.getDisplayName(),((MultiTeamGame)this).getTeamMember(last_team));
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
				for(Player player : getGameList().getPlayers(PlayerState.INGAME)){
					getGames().getStats().addInt(player, 1, StatsKey.WIN);
				}
				
				last_player = getGameList().getPlayers(PlayerState.INGAME).get(0);

				getGames().getManager().getClient().sendPacket(updateTo, new PacketArenaWinner(last_player.getUniqueId(), "a"+kArcade.id, getGames().getType(), getArena()));
				
				if(last_player!=null){
					broadcastWithPrefix("GAME_WIN",last_player.getName());
					t= new Title("",TranslationHandler.getText("GAME_WIN",last_player.getName()));
					for(Player player : getGameList().getPlayers().keySet()){
						t.send(player);
					}
				}
				for(Player player : getGameList().getPlayers().keySet())UtilBG.sendToServer(player, getGames().getManager().getInstance());
				
				getGameList().getPlayers().clear();
				setTimer(-1);
				getGames().updatePlayedGames();
			}
			
			UtilServer.getLagMeter().unloadChunks(null,null);
			UtilServer.getLagMeter().entitiesClearAll();
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
			if(isPlayerState(ev.getPlayer(), PlayerState.SPECTATOR))ev.setCancelled(true);
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
					UtilDisplay.displayTextBar(p, TranslationHandler.getText(p, "GAME_START_IN",getTimer()));
				}
				
				if(getTimer()!=0){
					switch(getTimer()){
					case 30:broadcastWithPrefix("GAME_START_IN", getTimer());break;
					case 15:broadcastWithPrefix("GAME_START_IN", getTimer());break;
					case 10:broadcastWithPrefix("GAME_START_IN", getTimer());break;
					case 5:broadcastWithPrefix("GAME_START_IN", getTimer()); sendTitle(Color.RED+getTimer()," ");break;
					case 4:broadcastWithPrefix("GAME_START_IN", getTimer()); sendTitle(Color.RED+getTimer()," ");break;
					case 3:broadcastWithPrefix("GAME_START_IN", getTimer()); sendTitle(Color.RED+getTimer()," ");break;
					case 2:broadcastWithPrefix("GAME_START_IN", getTimer()); sendTitle(Color.RED+getTimer()," ");break;
					case 1:broadcastWithPrefix("GAME_START_IN", getTimer()); sendTitle(Color.RED+getTimer()," ");break;
					}
				}else{
					if(startBereit()){
						sendTitle(Color.GREEN+"LOS!"," ");
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
		
		if((ev.getDamager() instanceof Player && isPlayerState((Player)ev.getDamager(), PlayerState.SPECTATOR)) || !Damage || isState(GameState.LobbyPhase)|| isState(GameState.Laden)){
			if(getGames().getManager().getService().isDebug())System.err.println("[MultiGame] Cancelled TRUE bei Damage  "+getState());
			ev.setCancelled(true);
		}else if((ev.getEntity() instanceof Player && ev.getDamager() instanceof Player)&&!DamagePvP){
			if(!isPlayerState((Player)ev.getDamager(), PlayerState.INGAME))return;
			if(!isPlayerState((Player)ev.getEntity(), PlayerState.INGAME))return;
			//P vs P
			if(getGames().getManager().getService().isDebug())System.err.println("[MultiGame] Cancelled TRUE bei DamagePvP");
			ev.setCancelled(true);
		}else if(((ev.getEntity() instanceof Player && ev.getDamager() instanceof Creature))&&!DamageEvP){
			if(!isPlayerState((Player)ev.getEntity(), PlayerState.INGAME))return;
			//E vs P
			if(getGames().getManager().getService().isDebug())System.err.println("[MultiGame] Cancelled TRUE bei DamageEvP");
			ev.setCancelled(true);
		}else if ( ((ev.getDamager() instanceof Player && ev.getEntity() instanceof Creature))&&!DamagePvE){
			if(!isPlayerState((Player)ev.getDamager(), PlayerState.INGAME))return;
			if(getGames().getManager().getService().isDebug())System.err.println("[MultiGame] Cancelled TRUE bei DamagePvE");
			//P vs E
			ev.setCancelled(true);
		}else if((ev.getDamager() instanceof Arrow||ev.getDamager() instanceof Snowball||ev.getDamager() instanceof Egg)&&!ProjectileDamage){
			if(ev.getDamager() instanceof Projectile && ((Projectile)ev.getDamager()).getShooter() instanceof Player && !isPlayerState((Player)((Projectile)ev.getDamager()).getShooter(), PlayerState.INGAME))return;
			if(getGames().getManager().getService().isDebug())System.err.println("[MultiGame] Cancelled TRUE bei ProjectileDamage");
			ev.setCancelled(true);
		}
	}
	
}
