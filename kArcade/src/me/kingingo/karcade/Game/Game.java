package me.kingingo.karcade.Game;

import lombok.Getter;
import lombok.Setter;
import me.kingingo.karcade.kArcade;
import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Game.Events.GameUpdateInfoEvent;
import me.kingingo.karcade.Game.Single.SingleGame;
import me.kingingo.karcade.Game.World.WorldData;
import me.kingingo.kcore.Client.Events.ClientConnectEvent;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.GameStateChangeReason;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.Game.Events.GameStartEvent;
import me.kingingo.kcore.Game.Events.GameStateChangeEvent;
import me.kingingo.kcore.Language.Language;
import me.kingingo.kcore.Packet.Packets.SERVER_STATUS;
import me.kingingo.kcore.Scoreboard.Events.PlayerSetScoreboardEvent;
import me.kingingo.kcore.StatsManager.StatsManager;
import me.kingingo.kcore.Util.Coins;
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
	@Setter
	@Getter
	private WorldData worldData;
	private Coins coins;
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
	
	public Game(kArcadeManager manager) {
		this.manager=manager;
	}
	
	public void unregisterListener(){
		HandlerList.unregisterAll(this);
	}
	
	public void registerListener(){
		Bukkit.getPluginManager().registerEvents(this, manager.getInstance());
	}
	
	public boolean isCoinsAktiv(){
		return coins!=null;
	}
	
	public Coins getCoins(){
		if(coins==null)coins=new Coins(getManager().getInstance(),getManager().getMysql());
		return coins;
	}

	public void setStats(GameType type){
		if(stats==null)this.stats=new StatsManager(getManager().getInstance(),getManager().getMysql(),type);
	}
	
	public void setStats(){
		if(stats==null)this.stats=new StatsManager(
				getManager().getInstance(),
				getManager().getMysql(),
				getType());
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
	
	public void setState(GameState gs){
		setState(gs, GameStateChangeReason.CUSTOM);
	}
	
	public void setState(GameState gs,GameStateChangeReason reason){
		if(gs==getState())return;
		GameStateChangeEvent stateEvent = new GameStateChangeEvent(state,gs,reason);
		Bukkit.getPluginManager().callEvent(stateEvent);
		if(stateEvent.isCancelled())return;
		state=gs;
		System.out.println("["+getType().getTyp()+"] GameState wurde zu "+state.string()+" geändert.");
	}
	
	public void updateInfo(int o){
		SERVER_STATUS ss = new SERVER_STATUS(state,o, getMax_Players(),getWorldData().getMapName(), getType(),"a"+kArcade.id,apublic);
		GameUpdateInfoEvent ev = new GameUpdateInfoEvent(ss);
		Bukkit.getPluginManager().callEvent(ev);
		if(ev.isCancelled())return;
		getManager().getPacketManager().SendPacket("hub", ss);
	}
	
	public void updateInfo(GameState s){
		SERVER_STATUS ss = new SERVER_STATUS(s,UtilServer.getPlayers().size(), getMax_Players(),getWorldData().getMapName(), getType(),"a"+kArcade.id,apublic);
		GameUpdateInfoEvent ev = new GameUpdateInfoEvent(ss);
		Bukkit.getPluginManager().callEvent(ev);
		if(ev.isCancelled())return;
		getManager().getPacketManager().SendPacket("hub", ss);
	}
	
	public void updateInfo(){
		SERVER_STATUS ss = new SERVER_STATUS(state
				,UtilServer.getPlayers().size()
				, getMax_Players()
				, (getWorldData()==null ? "Loading ..." : getWorldData().getMapName())
				, getType()
				,"a"+kArcade.id,apublic);
		GameUpdateInfoEvent ev = new GameUpdateInfoEvent(ss);
		Bukkit.getPluginManager().callEvent(ev);
		if(ev.isCancelled())return;
		getManager().getPacketManager().SendPacket("hub", ss);
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
		if(getState() == GameState.LobbyPhase){
			if(UtilEvent.isAction(ev, ActionType.R_BLOCK)){
				if(ev.getClickedBlock().getType()==Material.BREWING_STAND||ev.getClickedBlock().getType()==Material.DISPENSER||ev.getClickedBlock().getType()==Material.ANVIL||ev.getClickedBlock().getType()==Material.ENCHANTMENT_TABLE||ev.getClickedBlock().getType()==Material.TRAP_DOOR||ev.getClickedBlock().getType()==Material.WORKBENCH||ev.getClickedBlock().getType()==Material.FURNACE||ev.getClickedBlock().getType()==Material.ENDER_CHEST||ev.getClickedBlock().getType()==Material.CHEST)ev.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void Board(PlayerSetScoreboardEvent ev){
		UtilPlayer.setScoreboard(ev.getPlayer(), getCoins(), getManager().getPermManager());
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
		updateInfo(UtilServer.getPlayers().size()-1);
		if(getState()==GameState.LobbyPhase)broadcastWithPrefix("GAME_LEAVE", ev.getPlayer().getName());
	}
	
	@EventHandler
	public void J(PlayerJoinEvent ev){
		ev.setJoinMessage(null);
		if(getState()==GameState.LobbyPhase)broadcastWithPrefix("GAME_ENTER", new String[]{ev.getPlayer().getName(),String.valueOf(UtilServer.getPlayers().size()),String.valueOf(getMax_Players())});
		if(this instanceof SingleGame&&state==GameState.LobbyPhase)updateInfo();
	}

}
