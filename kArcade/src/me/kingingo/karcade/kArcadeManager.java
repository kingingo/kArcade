package me.kingingo.karcade;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import me.kingingo.karcade.Enum.GameStateChangeReason;
import me.kingingo.karcade.Events.RankingEvent;
import me.kingingo.karcade.Game.Game;
import me.kingingo.karcade.Game.Events.GameStateChangeEvent;
import me.kingingo.karcade.Game.Events.GameUpdateInfoEvent;
import me.kingingo.karcade.Game.Games.ArcadeGames.ArcadeGames;
import me.kingingo.karcade.Game.Games.BedWars.BedWars;
import me.kingingo.karcade.Game.Games.BedWars.BedWarsType;
import me.kingingo.karcade.Game.Games.CaveWars.CaveWars;
import me.kingingo.karcade.Game.Games.CaveWars.CaveWarsType;
import me.kingingo.karcade.Game.Games.DeathGames.DeathGames;
import me.kingingo.karcade.Game.Games.Falldown.Falldown;
import me.kingingo.karcade.Game.Games.OneInTheChamber.OneInTheChamber;
import me.kingingo.karcade.Game.Games.SheepWars.SheepWars;
import me.kingingo.karcade.Game.Games.SheepWars.SheepWarsType;
import me.kingingo.karcade.Game.Games.SkyPvP.SkyPvP;
import me.kingingo.karcade.Game.Games.SurvivalGames.SurvivalGames;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.TroubleInMinecraft;
import me.kingingo.karcade.Service.CommandService;
import me.kingingo.kcore.Calendar.Calendar;
import me.kingingo.kcore.Calendar.Calendar.CalendarType;
import me.kingingo.kcore.Client.Client;
import me.kingingo.kcore.Client.Events.ClientConnectEvent;
import me.kingingo.kcore.Command.CommandHandler;
import me.kingingo.kcore.Disguise.DisguiseManager;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.Game.Events.GameStartEvent;
import me.kingingo.kcore.Hologram.Hologram;
import me.kingingo.kcore.LogHandler.Event.LogEvent;
import me.kingingo.kcore.MySQL.MySQL;
import me.kingingo.kcore.Packet.PacketManager;
import me.kingingo.kcore.Packet.Events.PacketReceiveEvent;
import me.kingingo.kcore.Packet.Packets.SERVER_STATUS;
import me.kingingo.kcore.Packet.Packets.SERVER_TYPE_CHANGE;
import me.kingingo.kcore.Permission.PermissionManager;
import me.kingingo.kcore.Pet.PetManager;
import me.kingingo.kcore.PlayerStats.Stats;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.C;
import me.kingingo.kcore.Util.TimeSpan;
import me.kingingo.kcore.Util.UtilBG;
import me.kingingo.kcore.Util.UtilDisplay;
import me.kingingo.kcore.Util.UtilInv;
import me.kingingo.kcore.Util.UtilMath;
import me.kingingo.kcore.Util.UtilServer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

public class kArcadeManager implements Listener{

	@Getter
	@Setter
	private Game game = null;
	@Getter
	@Setter
	int start=-1;
	@Getter
	@Setter
	private PermissionManager permManager;
	@Getter
	private GameState state=GameState.NONE;
	@Getter
	@Setter
	private JavaPlugin Instance;
	@Getter
	@Setter
	private Location lobby=new Location(Bukkit.getWorld("world"),-148.28747,19,384.90493);
	@Getter
	private HashMap<Integer,Sign> ranking = new HashMap<>();
	@Getter
	@Setter
	private Location loc_stats = new Location(Bukkit.getWorld("world"), -128.57864,22.5,386.48775);
	@Getter
	@Setter
	private Location loc_raking = new Location(Bukkit.getWorld("world"),-128.35064,22.5,380.57131);
	@Getter
	@Setter
	private String[] string_ranking;
	@Getter
	private MySQL mysql;
	@Getter
	private Client c;
	@Getter
	private PacketManager pManager;
	@Getter
	private CommandHandler cmd;
	private PetManager pet;
	private DisguiseManager disguiseManager;
	@Getter
	private CommandService service;
	@Getter
	private CalendarType holiday;
	private Hologram hologram;
	
	public kArcadeManager(JavaPlugin plugin, String modulName,String g,PermissionManager permManager,MySQL mysql,Client c,PacketManager pManager,CommandHandler cmd) {
		this.lobby.setPitch(3);
		this.lobby.setYaw( (float)-89.81317 );
		this.permManager=permManager;
		this.Instance=plugin;
		this.mysql=mysql;
		this.cmd=cmd;
		this.pManager=pManager;
		this.c=c;
		Bukkit.getPluginManager().registerEvents(this, getInstance());
		this.holiday=Calendar.getHoliday();
		this.game=Game(g);
		this.service=new CommandService(permManager);
		this.hologram=new Hologram(getInstance());

		getLoc_stats().getWorld().loadChunk(getLoc_stats().getWorld().getChunkAt(getLoc_stats()));
		getLoc_raking().getWorld().loadChunk(getLoc_raking().getWorld().getChunkAt(getLoc_raking()));
		
		cmd.register(CommandService.class, this.service);
		for(Entity e : getLobby().getWorld().getEntities()){
			if(!(e instanceof Player)){
				e.remove();
			}
		}
		Bukkit.getPluginManager().callEvent(new RankingEvent());
		setState(GameState.LobbyPhase);
	}

	public void setNewGame(GameType typ){
		getInstance().getConfig().set("Config.Server.Game", typ.getTyp());
		if(UtilServer.getPlayers().size()!=0)for(Player p : UtilServer.getPlayers())UtilBG.sendToServer(p, getInstance());
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
		try {
			Runtime.getRuntime().exec("./start.sh");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public boolean isDisguiseManagerEnable(){
		if(disguiseManager==null){
			return false;
		}else{
			return true;
		}
	}
	
	public Hologram getHologram(){
		if(hologram==null)hologram=new Hologram(getInstance());
		return hologram;
	}
	
	public DisguiseManager getDisguiseManager(){
		if(disguiseManager==null)disguiseManager=new DisguiseManager(getInstance());
		return disguiseManager;
	}
	
	public Sign getSign(Location loc){
		Block north = loc.getBlock().getRelative(BlockFace.NORTH).getRelative(BlockFace.NORTH);
		Block west = loc.getBlock().getRelative(BlockFace.WEST).getRelative(BlockFace.WEST);
		Block east = loc.getBlock().getRelative(BlockFace.EAST).getRelative(BlockFace.EAST);
		Block south = loc.getBlock().getRelative(BlockFace.SOUTH).getRelative(BlockFace.SOUTH);
		if(north.getState() instanceof Sign){
			return ((Sign)north.getState());
		}else if(west.getState() instanceof Sign){
			return ((Sign)west.getState());
		}else if(east.getState() instanceof Sign){
			return ((Sign)east.getState());
		}else if(south.getState() instanceof Sign){
			return ((Sign)south.getState());
		}
		return null;
	}
	
	@EventHandler
	public void Kick(PlayerKickEvent ev){
		System.out.println("[EpicPvP] PlayerKickEvent: PLAYER:"+ev.getPlayer().getName()+" REASON:"+ev.getReason()+" LEAVE:"+ev.getLeaveMessage());
	}
	
	@EventHandler
	public void Log(LogEvent ev){
		if(ev.getMessage().contains("net.minecraft.util.io.netty.handler.codec.DecoderException: java.lang.LinkageError")){
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
			try {
				Runtime.getRuntime().exec("./start.sh");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@EventHandler
	  public void BlockPlaceArcadeManager(BlockPlaceEvent ev){
	    if(ev.getBlock().getWorld().getName().equalsIgnoreCase("world")&&!ev.getPlayer().isOp())ev.setCancelled(true);
	  }

	  @EventHandler
	  public void BlockBreakArcadeManager(BlockBreakEvent ev){
		  if(ev.getBlock().getWorld().getName().equalsIgnoreCase("world")&&!ev.getPlayer().isOp())ev.setCancelled(true);
	  }
	
	 @EventHandler
	  public void BlockBurnArcadeManager(BlockBurnEvent ev){
	    if(ev.getBlock().getWorld().getName().equalsIgnoreCase("world"))ev.setCancelled(true);
	  }

	  @EventHandler
	  public void BlockSpreadArcadeManager(BlockSpreadEvent ev){
		  if(ev.getBlock().getWorld().getName().equalsIgnoreCase("world"))ev.setCancelled(true);
	  }
	
	public PetManager getPetManager(){
		if(pet==null)pet=new PetManager(getInstance());
		return pet;
	}
	
	public void setRanking(Stats s){
		HashMap<Integer,UUID> list = getGame().getStats().getRanking(s, 10);
		setString_ranking(new String[11]);
		getString_ranking()[0]=C.cGreen+getGame().getType().getTyp()+C.mOrange+C.Bold+" Ranking";
		for(int i : list.keySet()){
			if(list.get(i)==null)break;
			getString_ranking()[i]=C.Bold+"#"+i+"�r "+getGame().getStats().getName(list.get(i))+" "+s.getK�RZEL()+": "+getGame().getStats().getIntWithUUID(s, list.get(i) );
		}
	}
	
	public Game Game(String game){
		if(GameType.OneInTheChamber.getTyp().equalsIgnoreCase(game)){
			return new OneInTheChamber(this);
		}else if(GameType.CaveWars.getTyp().equalsIgnoreCase(game)){
			return new CaveWars(this,CaveWarsType.values()[UtilMath.RandomInt(CaveWarsType.values().length, 0)]);
		}else if(GameType.SheepWars.getTyp().equalsIgnoreCase(game)){
			return new SheepWars(this,SheepWarsType.values()[UtilMath.RandomInt(SheepWarsType.values().length, 0)]);
		}else if(GameType.BedWars.getTyp().equalsIgnoreCase(game)){
			return new BedWars(this,BedWarsType.values()[UtilMath.RandomInt(BedWarsType.values().length, 0)]);
		}else if(GameType.TroubleInMinecraft.getTyp().equalsIgnoreCase(game)){
			return new TroubleInMinecraft(this);
		}else if(GameType.SkyPvP.getTyp().equalsIgnoreCase(game)){
			return new SkyPvP(this);
		}else if(GameType.DeathGames.getTyp().equalsIgnoreCase(game)){
			return new DeathGames(this);
		}else if(GameType.Falldown.getTyp().equalsIgnoreCase(game)){
			return new Falldown(this);
		}else if(GameType.SurvivalGames.getTyp().equalsIgnoreCase(game)){
			return new SurvivalGames(this);
		}else if(GameType.ArcadeGames.getTyp().equalsIgnoreCase(game)){
			new ArcadeGames(this);
			return null;
		}else{
			return new OneInTheChamber(this);
		}
	}
	
	public void DebugLog(long time,String Reason,String c){
		System.err.println("[DebugMode]: Class: "+c);
		System.err.println("[DebugMode]: Reason: "+Reason);
		System.err.println("[DebugMode]: Zeit: "+ ((System.currentTimeMillis()-time) / 1000.0D) + " Seconds");
	}
	
	public void DebugLog(long time,String c){
		System.err.println("[DebugMode]: Class: "+c);
		System.err.println("[DebugMode]: Zeit: "+ ((System.currentTimeMillis()-time) / 1000.0D) + " Seconds");
	}
	
	public void DebugLog(String m){
		System.err.println("[DebugMode]: "+m);
	}
	
	public void Clear(Player player){
	    player.setGameMode(GameMode.SURVIVAL);
	    player.setAllowFlight(false);
	    UtilInv.Clear(player);

	    ((CraftEntity)player).getHandle().getDataWatcher().watch(0, Byte.valueOf((byte)0));

	    player.setSprinting(false);

	    player.setFoodLevel(20);
	    player.setSaturation(3.0F);
	    player.setExhaustion(0.0F);

	    player.setMaxHealth(20.0D);
	    player.setHealth(((CraftPlayer)player).getMaxHealth());

	    player.setFireTicks(0);
	    player.setFallDistance(0.0F);
	    player.setWalkSpeed( 0.2F );
	    player.setLevel(0);
	    player.setExp(0.0F);
	    
	    for(PotionEffect pe : player.getActivePotionEffects())player.removePotionEffect(pe.getType());
	    
	    ((CraftPlayer)player).getHandle().k = true;
//	    EntityPlayer ep = ((CraftPlayer)player).getHandle();
//	    EntityHuman eh = (EntityHuman)ep;
//	    EntityLiving el = (EntityLiving)eh;
//	    Entity e = (Entity)el;
//	    DataWatcher data = (DataWatcher) UtilReflection.getValue("datawatcher",e);
//	    data.watch(9, Byte.valueOf((byte)0));
	    ((CraftEntity)player).getHandle().getDataWatcher().watch(9, Byte.valueOf((byte)0));
	    //((CraftPlayer)player).getHandle().p(0);
	}
	
	public void updateInfo(int o){
		SERVER_STATUS ss = new SERVER_STATUS(state,o, getGame().getMax_Players(),getGame().getWorldData().getMapName(), getGame().getType(),"a"+kArcade.id);
		GameUpdateInfoEvent ev = new GameUpdateInfoEvent(ss);
		Bukkit.getPluginManager().callEvent(ev);
		if(ev.isCancelled())return;
		pManager.SendPacket("hub", ss);
	}
	
	public void updateInfo(GameState s){
		SERVER_STATUS ss = new SERVER_STATUS(s,UtilServer.getPlayers().size(), getGame().getMax_Players(),getGame().getWorldData().getMapName(), getGame().getType(),"a"+kArcade.id);
		GameUpdateInfoEvent ev = new GameUpdateInfoEvent(ss);
		Bukkit.getPluginManager().callEvent(ev);
		if(ev.isCancelled())return;
		pManager.SendPacket("hub", ss);
	}
	
	public void updateInfo(){
		SERVER_STATUS ss = new SERVER_STATUS(state,UtilServer.getPlayers().size(), getGame().getMax_Players(),getGame().getWorldData().getMapName(), getGame().getType(),"a"+kArcade.id);
		GameUpdateInfoEvent ev = new GameUpdateInfoEvent(ss);
		Bukkit.getPluginManager().callEvent(ev);
		if(ev.isCancelled())return;
		pManager.SendPacket("hub", ss);
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
	}
	
	@EventHandler
	public void Mobs(CreatureSpawnEvent ev){
		if(ev.getLocation().getWorld()==getLobby().getWorld())ev.setCancelled(true);
	}
	
	@EventHandler
	public void J(PlayerJoinEvent ev){
		if(state==GameState.LobbyPhase)updateInfo();
	}
	
	public void setState(GameState gs){
		setState(gs, GameStateChangeReason.CUSTOM);
	}
	
	public void setState(GameState gs,GameStateChangeReason reason){
		GameStateChangeEvent stateEvent = new GameStateChangeEvent(state,gs,reason);
		Bukkit.getPluginManager().callEvent(stateEvent);
		if(stateEvent.isCancelled())return;
		state=gs;
		if(getGame()!=null){
			System.out.println("["+getGame().getType().getTyp()+"] GameState wurde zu "+state.string()+" ge�ndert.");
		}else{
			System.out.println("[ArcadeManager] GameState wurde zu "+state.string()+" ge�ndert.");
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void r(GameStateChangeEvent ev){
		if(!ev.isCancelled()&&ev.getTo()==GameState.Restart){
			setStart(-1);
		}
	}
	
	public boolean isState(GameState gs){
		return gs==getState();
	}
	
	public void broadcast(String message){
		if(UtilServer.getPlayers().size()==0)return;
	    UtilServer.broadcast(message);
	}
	
	@EventHandler
	   public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
	   {
	     Player p = event.getPlayer();
	    String cmd = "";
	 
	     if (event.getMessage().contains(" ")) {
	       String[] parts = event.getMessage().split(" ");
	       cmd = parts[0];
	     }
	     else
	     {
	       cmd = event.getMessage();
	     }
	     if (cmd.equalsIgnoreCase("/msg")) {
	      event.setCancelled(true);
	       p.sendMessage(ChatColor.RED + "Nope :3");
	     }else if (cmd.equalsIgnoreCase("/kill")) {
	         event.setCancelled(true);
	         p.sendMessage(ChatColor.RED + "Nope :3");
	     }else if (cmd.contains("/kill")) {
	         event.setCancelled(true);
	         p.sendMessage(ChatColor.RED + "Nope :3");
	     } else if (cmd.equalsIgnoreCase("/tell")) {
	      event.setCancelled(true);
	       p.sendMessage(ChatColor.RED + "Nope :3");
	     } else if (cmd.equalsIgnoreCase("/pl")||cmd.equalsIgnoreCase("/plugins")) {

	       event.setCancelled(true);
	      p.sendMessage(ChatColor.RED + "Nope :3");
	     } else if (cmd.equalsIgnoreCase("/about")) {

	       event.setCancelled(true);
	       p.sendMessage(ChatColor.RED + "Nope :3");
	     } else if (cmd.equalsIgnoreCase("/version")) {

	     event.setCancelled(true);
	       p.sendMessage(ChatColor.RED + "Nope :3");
	     } else if (cmd.equalsIgnoreCase("/me")) {
	      event.setCancelled(true);
	       p.sendMessage(ChatColor.RED + "Nope :3");
	     } else if (cmd.equalsIgnoreCase("/bukkit:kill")) {
		      event.setCancelled(true);
		       p.sendMessage(ChatColor.RED + "Nope :3");
	     } else if (cmd.equalsIgnoreCase("/bukkit:msg")) {
		      event.setCancelled(true);
		       p.sendMessage(ChatColor.RED + "Nope :3");
	     } else if (cmd.equalsIgnoreCase("/bukkit:tell")) {
		      event.setCancelled(true);
		       p.sendMessage(ChatColor.RED + "Nope :3");
	     } else if (cmd.equalsIgnoreCase("/bukkit:me")) {
		      event.setCancelled(true);
		       p.sendMessage(ChatColor.RED + "Nope :3");
	     } else if (cmd.equalsIgnoreCase("/?")) {
	       event.setCancelled(true);
	       p.sendMessage(ChatColor.RED + "Nope :3");
	     } else if (cmd.equalsIgnoreCase("/help")) {
	       event.setCancelled(true);
	       p.sendMessage(ChatColor.RED + "Nope :3");
	     }else if (cmd.equalsIgnoreCase("/reload") && p.isOp()) {
	    	 event.setCancelled(true);
	    	 for(Player p1 : Bukkit.getOnlinePlayers())UtilBG.sendToServer(p1, getInstance());
	    	 
	    	 Bukkit.getScheduler().scheduleSyncDelayedTask(getInstance(), new Runnable(){

	  			@Override
	  			public void run() {
	  				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
	  			}
	      		 
	      	 }, 4*20);
	    	 
	     }else if (cmd.equalsIgnoreCase("/stop") && p.isOp()) {
	    	 event.setCancelled(true);
	    	 for(Player p1 : Bukkit.getOnlinePlayers())UtilBG.sendToServer(p1, getInstance());
	    	 
	    	 Bukkit.getScheduler().scheduleSyncDelayedTask(getInstance(), new Runnable(){

	  			@Override
	  			public void run() {
	  				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
	  			}
	      		 
	      	 }, 4*20);
	    	 
	     }
	   }
	
	@EventHandler
	public void Packet(PacketReceiveEvent ev){
		if(ev.getPacket() instanceof SERVER_TYPE_CHANGE){
			SERVER_TYPE_CHANGE packet = (SERVER_TYPE_CHANGE)ev.getPacket();
			getInstance().getConfig().set("Config.Server.Game", packet.getTyp().getTyp());
			getInstance().saveConfig();
			setState(GameState.Restart, GameStateChangeReason.CHANGE_TYPE);
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void GameStateForCoins(GameStateChangeEvent ev){
		if(ev.getTo()==GameState.Restart){
			if(getGame().isCoinsAktiv())getGame().getCoins().SaveAll();
		}
	}
	
	@EventHandler
	public void Time_Start(UpdateEvent ev){
		if(ev.getType()==UpdateType.MIN_04&&getState()==GameState.LobbyPhase&&UtilServer.getPlayers().size()<=0){
			if(TimeSpan.HOUR*3 < (System.currentTimeMillis() - kArcade.start_time) ){
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
				try {
					Runtime.getRuntime().exec("./start.sh");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@EventHandler
	public void Restart(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		if(getState()!=GameState.Restart)return;
		if(start<0){
			getGame().setDamage(false);
			start=35;
		}
		start--;
		for(Player p : UtilServer.getPlayers())UtilDisplay.displayTextBar(p, Text.RESTART_IN.getText(start));
		
		switch(start){
		case 30:broadcast(Text.PREFIX_GAME.getText(getGame().getType().getTyp())+Text.RESTART_IN.getText(start));break;
		case 25:
			broadcast(Text.PREFIX_GAME.getText(getGame().getType().getTyp())+Text.RESTART_IN.getText(start));
			for(Player p : UtilServer.getPlayers())UtilBG.sendToServer(p, getInstance());
			break;
		case 23:getGame().getStats().SaveAllData();break;
		case 20:broadcast(Text.PREFIX_GAME.getText(getGame().getType().getTyp())+Text.RESTART_IN.getText(start));
		for(Player p : UtilServer.getPlayers())UtilBG.sendToServer(p, getInstance());
			break;
		case 10:broadcast(Text.PREFIX_GAME.getText(getGame().getType().getTyp())+Text.RESTART_IN.getText(start));break;
		case 5:broadcast(Text.PREFIX_GAME.getText(getGame().getType().getTyp())+Text.RESTART_IN.getText(start));
		case 4:broadcast(Text.PREFIX_GAME.getText(getGame().getType().getTyp())+Text.RESTART_IN.getText(start));break;
		case 3:broadcast(Text.PREFIX_GAME.getText(getGame().getType().getTyp())+Text.RESTART_IN.getText(start));break;
		case 2:broadcast(Text.PREFIX_GAME.getText(getGame().getType().getTyp())+Text.RESTART_IN.getText(start));break;
		case 1:broadcast(Text.PREFIX_GAME.getText(getGame().getType().getTyp())+Text.RESTART_IN.getText(start));break;
		case 0: 
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
			;break;
		}
	}
	
	@EventHandler
	public void Lobby(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		if(getGame()==null)return;
		if(getState()!=GameState.LobbyPhase)return;
		if(start<0){
			start=120;
			updateInfo();
		}
		start--;
		for(Player p : UtilServer.getPlayers()){
			UtilDisplay.displayTextBar(p, C.cGray+"Das Spiel startet in "+C.cDAqua+start+C.cGray+" sekunden.");
			if(p.getLocation().getY()<5)p.teleport(lobby);
		}
		if(start!=0){
			switch(start){
			case 120:
				broadcast(Text.PREFIX_GAME.getText(getGame().getType().getTyp())+"Das Spiel startet in "+C.cDAqua+start+C.cGray+" sekunden.");
				Bukkit.getWorld("world").setWeatherDuration(0);
				Bukkit.getWorld("world").setStorm(false);
				break;
			case 90:broadcast(Text.PREFIX_GAME.getText(getGame().getType().getTyp())+"Das Spiel startet in "+C.cDAqua+start+C.cGray+" sekunden.");break;
			case 60:broadcast(Text.PREFIX_GAME.getText(getGame().getType().getTyp())+"Das Spiel startet in "+C.cDAqua+start+C.cGray+" sekunden.");break;
			case 30:broadcast(Text.PREFIX_GAME.getText(getGame().getType().getTyp())+"Das Spiel startet in "+C.cDAqua+start+C.cGray+" sekunden.");break;
			case 15:broadcast(Text.PREFIX_GAME.getText(getGame().getType().getTyp())+"Das Spiel startet in "+C.cDAqua+start+C.cGray+" sekunden.");break;
			case 10:broadcast(Text.PREFIX_GAME.getText(getGame().getType().getTyp())+"Das Spiel startet in "+C.cDAqua+start+C.cGray+" sekunden.");break;
			case 3:broadcast(Text.PREFIX_GAME.getText(getGame().getType().getTyp())+"Das Spiel startet in "+C.cDAqua+start+C.cGray+" sekunden.");break;
			case 2:broadcast(Text.PREFIX_GAME.getText(getGame().getType().getTyp())+"Das Spiel startet in "+C.cDAqua+start+C.cGray+" sekunden.");break;
			case 1:broadcast(Text.PREFIX_GAME.getText(getGame().getType().getTyp())+"Das Spiel startet in "+C.cDAqua+start+C.cGray+" sekunden.");break;
			}
		}else{
			if(UtilServer.getPlayers().size()>=getGame().getMin_Players()){
				Bukkit.getPluginManager().callEvent(new GameStartEvent(getGame().getType()));
				updateInfo(GameState.InGame);
			}else{
				start=-1;
				broadcast(Text.PREFIX_GAME.getText(getGame().getType().getTyp())+C.cRed+"Es sind zu wenig Spieler(min. "+getGame().getMin_Players()+") online! Wartemodus wird neugestartet!");
			}
		}
	}

}
