package me.kingingo.karcade.Game;

import lombok.Getter;
import lombok.Setter;
import me.kingingo.karcade.kArcade;
import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Game.Events.GameStartEvent;
import me.kingingo.karcade.Game.Events.GameStateChangeEvent;
import me.kingingo.karcade.Game.Events.GameUpdateInfoEvent;
import me.kingingo.karcade.Game.Multi.MultiGames;
import me.kingingo.karcade.Game.Single.SingleGame;
import me.kingingo.kcore.Client.Events.ClientConnectEvent;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.GameStateChangeReason;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.Language.Language;
import me.kingingo.kcore.Packet.Packets.SERVER_STATUS;
import me.kingingo.kcore.Scoreboard.Events.PlayerSetScoreboardEvent;
import me.kingingo.kcore.StatsManager.StatsManager;
import me.kingingo.kcore.Util.Coins;
import me.kingingo.kcore.Util.Gems;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.UtilPlayer;
import me.kingingo.kcore.Util.UtilServer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

public class Game implements Listener{

	@Getter
	private kArcadeManager manager;
	@Getter
	@Setter
	private String packetServer="hub";
	private Coins coins;
	private Gems gems;
	@Getter
	@Setter
	private Scoreboard board;
	@Getter
	private StatsManager stats;
	private GameType typ = GameType.NONE;
	@Getter
	private GameState state=GameState.NONE;
	@Getter
	@Setter
	private int Min_Players=1;
	@Getter
	@Setter
	private int Max_Players=50;
	@Setter
	@Getter
	private boolean apublic=true;
	@Getter
	@Setter
	private boolean set_default_scoreboard=true;
	
	public Game(kArcadeManager manager) {
		this.manager=manager;
	}
	
	public void unregisterListener(){
		HandlerList.unregisterAll(this);
	}
	
	public void registerListener(){
		Bukkit.getPluginManager().registerEvents(this, manager.getInstance());
	}
	
	public boolean isGemsAktiv(){
		return gems!=null;
	}
	
	public Gems getGems(){
		if(this.gems==null){
			this.gems=new Gems(getManager().getMysql());
			this.gems.setAsync(true);
		}
		return gems;
	}
	
	public boolean isCoinsAktiv(){
		return coins!=null;
	}
	
	public Coins getCoins(){
		if(this.coins==null){
			this.coins=new Coins(getManager().getInstance(),getManager().getMysql());
			this.coins.setAsync(true);
		}
		return coins;
	}

	public void setStats(GameType type){
		if(stats==null){
			this.stats=new StatsManager(getManager().getInstance(),getManager().getMysql(),type);
			this.stats.setAsync(true);
		}
	}
	
	public void setStats(){
		if(stats==null){
			this.stats=new StatsManager(
				getManager().getInstance(),
				getManager().getMysql(),
				getType());
			this.stats.setAsync(true);
		}
	}
	
	public void setTyp(GameType typ){
		this.typ=typ;
		setStats();
	}
	
	public boolean isType(GameType gt){
		return typ==gt;
	}
	
	public GameType getType(){
		return this.typ;
	}
	
	public boolean isState(GameState gs){
		return gs==getState();
	}
	
	public boolean setState(GameState gs){
		return setState(gs, GameStateChangeReason.CUSTOM);
	}
	
	public boolean setState(GameState gs,GameStateChangeReason reason){
		if(gs==getState())return false;
		GameStateChangeEvent stateEvent = new GameStateChangeEvent(state,gs,reason);
		Bukkit.getPluginManager().callEvent(stateEvent);
		if(stateEvent.isCancelled())return false;
		state=gs;
		System.out.println("["+getType().getTyp()+"] GameState wurde zu "+state.string()+" geändert.");
		return true;
	}
	
	public void updateInfo(GameState state,int player_size,int max,String Map,GameType type ){
		SERVER_STATUS ss = new SERVER_STATUS(state
				,player_size
				,max
				, Map
				, type
				,"a"+kArcade.id,apublic);
		GameUpdateInfoEvent ev = new GameUpdateInfoEvent(ss);
		Bukkit.getPluginManager().callEvent(ev);
		if(ev.isCancelled())return;
		getManager().getPacketManager().SendPacket(getPacketServer(), ss);
	}
	
	public void updateInfo(GameState state,int player_size){
		updateInfo(state, player_size, getMax_Players(), (this instanceof SingleGame?(((SingleGame)this).getWorldData()==null?"Loading ...":((SingleGame)this).getWorldData().getMapName()):"Loading ..."), getType());
	}
	
	public void updateInfo(int player_size){
		updateInfo(getState(),player_size);
	}
	
	public void updateInfo(GameState state){
		updateInfo(state,UtilServer.getPlayers().size());
	}
	
	public void updateInfo(){
		updateInfo(getState(), UtilServer.getPlayers().size());
	}
	
	public void broadcastWithPrefix(String name,Object input){
		for(Player player : UtilServer.getPlayers())player.sendMessage(Language.getText(player,"PREFIX_GAME",getType().getTyp())+Language.getText(player,name,input));
	}
	
	public void broadcastWithPrefix(String name,Object[] input){
		for(Player player : UtilServer.getPlayers())player.sendMessage(Language.getText(player,"PREFIX_GAME",getType().getTyp())+Language.getText(player,name,input));
	}
	
	public void broadcastWithPrefixName(String name){
		for(Player player : UtilServer.getPlayers())player.sendMessage(Language.getText(player,"PREFIX_GAME",getType().getTyp())+Language.getText(player, name));
	}
	
	public void broadcastWithPrefix(String msg){
		for(Player player : UtilServer.getPlayers())player.sendMessage(Language.getText(player,"PREFIX_GAME",getType().getTyp())+msg);
	}
	
	public void broadcastName(String name){
		for(Player player : UtilServer.getPlayers())player.sendMessage(Language.getText(player, name));
	}
	
	public void broadcast(String msg){
		for(Player player : UtilServer.getPlayers())player.sendMessage(msg);
	}
	
	public void broadcast(String name,Object[] input){
		for(Player player : UtilServer.getPlayers())player.sendMessage(Language.getText(player,name,input));
	}
	
	public void broadcast(String name,Object input){
		for(Player player : UtilServer.getPlayers())player.sendMessage(Language.getText(player,name,input));
	}
	
	@EventHandler
	public void OpenChest(PlayerInteractEvent ev){
		if(getState() == GameState.LobbyPhase&&ev.getPlayer().getWorld().getUID()==getManager().getLobby().getWorld().getUID()){
			if(UtilEvent.isAction(ev, ActionType.R_BLOCK)){
				if(ev.getClickedBlock().getType()==Material.BREWING_STAND||ev.getClickedBlock().getType()==Material.DROPPER||ev.getClickedBlock().getType()==Material.DISPENSER||ev.getClickedBlock().getType()==Material.ANVIL||ev.getClickedBlock().getType()==Material.ENCHANTMENT_TABLE||ev.getClickedBlock().getType()==Material.TRAP_DOOR||ev.getClickedBlock().getType()==Material.WORKBENCH||ev.getClickedBlock().getType()==Material.FURNACE||ev.getClickedBlock().getType()==Material.ENDER_CHEST||ev.getClickedBlock().getType()==Material.CHEST)ev.setCancelled(true);
			}
		}
	}
	
	@EventHandler(ignoreCancelled=false,priority=EventPriority.LOWEST)
	public void Board(PlayerSetScoreboardEvent ev){
		if(isSet_default_scoreboard())UtilPlayer.setScoreboard(ev.getPlayer(),getGems(), getCoins());
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void Start(GameStartEvent ev){
		for(Player player : UtilServer.getPlayers()){
			if(player.getScoreboard()!=null){
				if(player.getScoreboard().getObjective(DisplaySlot.SIDEBAR)!=null){
					player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).unregister();
				}
			}
		}
	}
	
	@EventHandler
	public void Start(GameStateChangeEvent ev){
		updateInfo();
	}
	
	@EventHandler
	public void ClientC(ClientConnectEvent ev){
		if(getState()!=GameState.Laden){
			updateInfo();
		}
	}
	
	@EventHandler
	public void Q(PlayerQuitEvent ev){
		ev.setQuitMessage(null);
		updateInfo(UtilServer.getPlayers().size()-1);
		if(this instanceof SingleGame&&isState(GameState.LobbyPhase))broadcastWithPrefix("GAME_LEAVE", ev.getPlayer().getName());
	}
	
	@EventHandler
	public void J(PlayerJoinEvent ev){
		ev.setJoinMessage(null);
		Bukkit.getWorld("world").setWeatherDuration(0);
		Bukkit.getWorld("world").setStorm(false);
		if(isState(GameState.LobbyPhase)||this instanceof MultiGames)updateInfo();
		if(this instanceof SingleGame&&getState()==GameState.LobbyPhase)broadcastWithPrefix("GAME_ENTER", new String[]{ev.getPlayer().getName(),String.valueOf(UtilServer.getPlayers().size()),String.valueOf(getMax_Players())});
	}

}
