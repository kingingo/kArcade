package me.kingingo.karcade;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;
import me.kingingo.karcade.Enum.GameStateChangeReason;
import me.kingingo.karcade.Events.RankingEvent;
import me.kingingo.karcade.Game.Game;
import me.kingingo.karcade.Game.Events.GameStateChangeEvent;
import me.kingingo.karcade.Game.Games.DeathGames.DeathGames;
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
import me.kingingo.kcore.MySQL.MySQL;
import me.kingingo.kcore.Nick.NickManager;
import me.kingingo.kcore.Packet.PacketManager;
import me.kingingo.kcore.Packet.Packets.SERVER_STATUS;
import me.kingingo.kcore.Permission.PermissionManager;
import me.kingingo.kcore.Pet.PetManager;
import me.kingingo.kcore.PlayerStats.Stats;
import me.kingingo.kcore.PlayerStats.StatsManager;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.C;
import me.kingingo.kcore.Util.Title;
import me.kingingo.kcore.Util.UtilBG;
import me.kingingo.kcore.Util.UtilDisplay;
import me.kingingo.kcore.Util.UtilInv;
import me.kingingo.kcore.Util.UtilServer;
import net.minecraft.server.v1_7_R4.WorldServer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
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
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

public class kArcadeManager implements Listener{

	@Getter
	private Game game;
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
	private String BungeeCord_Fallback_Server = "falldown";
	private GameType typ = GameType.NONE;
	@Getter
	@Setter
	private Location lobby=new Location(Bukkit.getWorld("world"),789.415,29,680.2699);
	@Getter
	private HashMap<Integer,Sign> ranking = new HashMap<>();
	@Getter
	@Setter
	private Location loc_stats = new Location(Bukkit.getWorld("world"), 785.54191,24.5,615.49871);
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
	private NickManager nManager;
	@Getter
	private CommandService service;
	@Getter
	private CalendarType holiday;
	
	public kArcadeManager(JavaPlugin plugin, String modulName,String g,PermissionManager permManager,MySQL mysql,Client c,PacketManager pManager,CommandHandler cmd) {
		this.lobby.setPitch(2);
		this.lobby.setYaw( (float)179.60071 );
		this.permManager=permManager;
		this.Instance=plugin;
		this.mysql=mysql;
		this.cmd=cmd;
		this.pManager=pManager;
		this.nManager=new NickManager(cmd,permManager);
		this.c=c;
		Bukkit.getPluginManager().registerEvents(this, getInstance());
		this.game=Game(g);
		this.service=new CommandService(permManager);
		this.holiday=Calendar.getHoliday(5);
		
		cmd.register(CommandService.class, this.service);
		for(Entity e : getLobby().getWorld().getEntities()){
			if(!(e instanceof Player)){
				e.remove();
			}
		}
		
		new Location(Bukkit.getWorld("world"),756,23,610).getWorld().loadChunk(new Location(Bukkit.getWorld("world"),756,23,610).getWorld().getChunkAt(new Location(Bukkit.getWorld("world"),756,23,610)));
		ranking.put(1, ((Sign)new Location(Bukkit.getWorld("world"),756,23,610).getBlock().getState()));
		ranking.put(2, ((Sign)new Location(Bukkit.getWorld("world"),756,23,609).getBlock().getState()));
		ranking.put(3, ((Sign)new Location(Bukkit.getWorld("world"),756,23,608).getBlock().getState()));
		ranking.put(4, ((Sign)new Location(Bukkit.getWorld("world"),756,23,607).getBlock().getState()));
		ranking.put(5, ((Sign)new Location(Bukkit.getWorld("world"),756,23,606).getBlock().getState()));
		ranking.put(6, ((Sign)new Location(Bukkit.getWorld("world"),756,21,610).getBlock().getState()));
		ranking.put(7, ((Sign)new Location(Bukkit.getWorld("world"),756,21,609).getBlock().getState()));
		ranking.put(8, ((Sign)new Location(Bukkit.getWorld("world"),756,21,608).getBlock().getState()));
		ranking.put(9, ((Sign)new Location(Bukkit.getWorld("world"),756,21,607).getBlock().getState()));
		ranking.put(10, ((Sign)new Location(Bukkit.getWorld("world"),756,21,606).getBlock().getState()));
		Bukkit.getPluginManager().callEvent(new RankingEvent());
		setState(GameState.LobbyPhase);
	}

	public void setNewGame(GameType typ){
		getInstance().getConfig().set("Config.Server.Game", typ.getTyp());
		if(UtilServer.getPlayers().length!=0)for(Player p : UtilServer.getPlayers())UtilBG.sendToServer(p, BungeeCord_Fallback_Server, getInstance());
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
	
	public DisguiseManager getDisguiseManager(){
		if(disguiseManager==null)disguiseManager=new DisguiseManager(getInstance());
		return disguiseManager;
	}
	
	public void setTyp(GameType typ){
		getGame().setStats(typ);
		this.typ=typ;
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
	
	public void setWorldUnSave(World w){
		WorldServer ws = ((CraftWorld)w).getHandle();
		try{
			Field field = ws.chunkProvider.getClass().getDeclaredField("CanSave");
			field.setAccessible(true);
			field.set(ws.chunkProvider, "false");
		}catch(Exception e){
			
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
		HashMap<Integer,String> list = getGame().getStats().getRanking(s, 10);
		Sign sign;
		Skull sk;
		for(int i : ranking.keySet()){
			if(list.get(i)==null)break;
			sign=ranking.get(i);
			sign.setLine(0, "---- "+C.Bold+"#"+i+"§r ----");
			sign.setLine(1, list.get(i));
			sign.setLine(2, s.getKÜRZEL()+" "+getGame().getStats().getIntWithString(s, list.get(i)));
			sign.setLine(3, "K/D "+getGame().getStats().getKDR(getGame().getStats().getIntWithString(Stats.KILLS, list.get(i)), getGame().getStats().getIntWithString(Stats.DEATHS,list.get(i))));
			sign.update(true);
			sk = (Skull)sign.getLocation().add(0,1,0).getBlock().getState();
			sk.setOwner(list.get(i));
			sk.update(true);
		}
	}
	
	public Game Game(String game){
		if(GameType.OneInTheChamber.getTyp().equalsIgnoreCase(game)){
			return new OneInTheChamber(this);
		}else if((GameType.SheepWars8.getTyp()).equalsIgnoreCase(game)){
			return new SheepWars(this,SheepWarsType._2);
		}else if((GameType.SheepWars16.getTyp()).equalsIgnoreCase(game)){
			return new SheepWars(this,SheepWarsType._4);
		}else if(GameType.TroubleInMinecraft.getTyp().equalsIgnoreCase(game)){
			return new TroubleInMinecraft(this);
		}else if(GameType.SkyPvP.getTyp().equalsIgnoreCase(game)){
			return new SkyPvP(this);
		}else if(GameType.DeathGames.getTyp().equalsIgnoreCase(game)){
			return new DeathGames(this);
		}else if(GameType.SurvivalGames.getTyp().equalsIgnoreCase(game)){
			return new SurvivalGames(this);
		}else{
			return new OneInTheChamber(this);
		}
	}
	
	public ArrayList<String> LoadFiles(String gameName){
	    File folder = new File(kArcade.FilePath+File.separator+gameName);
	    if (!folder.exists()) folder.mkdirs();
	    ArrayList<String> maps = new ArrayList<>();
	    System.out.println("Suche Maps in: " + folder);

	    for (File file : folder.listFiles())
	    {
	      if (file.isFile())
	      {
	        String name = file.getName();

	        if (name.length() >= 5)
	        {
	          name = name.substring(name.length() - 4, name.length());

	          if (!file.getName().equals(".zip"))
	          {
	            maps.add(kArcade.FilePath+File.separator+gameName+File.separator+file.getName().substring(0, file.getName().length() - 4)+".zip");
	          }
	        }
	      }
	    }
	    for (String map : maps) {
	      System.out.println("Maps: " + map);
	    }
	    return maps;
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

	    ((CraftPlayer)player).getHandle().p(0);
	}
	
	public void updateInfo(int o){
		SERVER_STATUS ss = new SERVER_STATUS(state,o, getGame().getMax_Players(),getGame().getWorldData().getMapName(), getTyp(),"a"+kArcade.id);
		pManager.SendPacket("hub", ss);
	}
	
	public void updateInfo(GameState s){
		SERVER_STATUS ss = new SERVER_STATUS(s,UtilServer.getPlayers().length, getGame().getMax_Players(),getGame().getWorldData().getMapName(), getTyp(),"a"+kArcade.id);
		pManager.SendPacket("hub", ss);
	}
	
	public void updateInfo(){
//		System.out.println("S:"+state.string());
//		System.out.println("O: "+UtilServer.getPlayers().length);
//		System.out.println("O: "+getGame().getMax_Players());
//		System.out.println("O: "+getWorldData().getMapName());
//		System.out.println("O: "+getTyp());
//		System.out.println("O: "+"a"+kArcade.id);
		SERVER_STATUS ss = new SERVER_STATUS(state,UtilServer.getPlayers().length, getGame().getMax_Players(),getGame().getWorldData().getMapName(), getTyp(),"a"+kArcade.id);
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
		/*if(state==GameState.LobbyPhase)*/updateInfo(UtilServer.getPlayers().length-1);
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
		System.out.println("["+this.typ.getTyp()+"] GameState wurde zu "+state.string()+" geändert.");
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void r(GameStateChangeEvent ev){
		if(!ev.isCancelled()&&ev.getTo()==GameState.Restart){
			setStart(-1);
		}
	}
	
	public boolean isType(GameType gt){
		return typ==gt;
	}
	
	public boolean isState(GameState gs){
		return gs==getState();
	}
	
	public GameType getTyp(){
		return this.typ;
	}
	
	public void broadcast(String message){
		if(UtilServer.getPlayers().length==0)return;
	    for (Player player : UtilServer.getPlayers())player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
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
	    	 for(Player p1 : Bukkit.getOnlinePlayers())UtilBG.sendToServer(p1, BungeeCord_Fallback_Server, getInstance());
	    	 
	    	 Bukkit.getScheduler().scheduleSyncDelayedTask(getInstance(), new Runnable(){

	  			@Override
	  			public void run() {
	  				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
	  			}
	      		 
	      	 }, 4*20);
	    	 
	     }else if (cmd.equalsIgnoreCase("/stop") && p.isOp()) {
	    	 event.setCancelled(true);
	    	 for(Player p1 : Bukkit.getOnlinePlayers())UtilBG.sendToServer(p1, BungeeCord_Fallback_Server, getInstance());
	    	 
	    	 Bukkit.getScheduler().scheduleSyncDelayedTask(getInstance(), new Runnable(){

	  			@Override
	  			public void run() {
	  				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
	  			}
	      		 
	      	 }, 4*20);
	    	 
	     }
	   }
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void GameStateForCoins(GameStateChangeEvent ev){
		if(ev.getTo()==GameState.Restart){
			if(getGame().isCoinsAktiv())getGame().getCoins().SaveAll();
			if(getGame().isTokensAktiv())getGame().getTokens().SaveAll();
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
		case 30:broadcast(Text.PREFIX_GAME.getText(getTyp().getTyp())+Text.RESTART_IN.getText(start));break;
		case 25:
			broadcast(Text.PREFIX_GAME.getText(getTyp().getTyp())+Text.RESTART_IN.getText(start));
			for(Player p : UtilServer.getPlayers())UtilBG.sendToServer(p, BungeeCord_Fallback_Server, getInstance());
			break;
		case 23:getGame().getStats().SaveAllData();break;
		case 20:broadcast(Text.PREFIX_GAME.getText(getTyp().getTyp())+Text.RESTART_IN.getText(start));
		for(Player p : UtilServer.getPlayers())UtilBG.sendToServer(p, BungeeCord_Fallback_Server, getInstance());
			break;
		case 10:broadcast(Text.PREFIX_GAME.getText(getTyp().getTyp())+Text.RESTART_IN.getText(start));break;
		case 5:broadcast(Text.PREFIX_GAME.getText(getTyp().getTyp())+Text.RESTART_IN.getText(start));
		case 4:broadcast(Text.PREFIX_GAME.getText(getTyp().getTyp())+Text.RESTART_IN.getText(start));break;
		case 3:broadcast(Text.PREFIX_GAME.getText(getTyp().getTyp())+Text.RESTART_IN.getText(start));break;
		case 2:broadcast(Text.PREFIX_GAME.getText(getTyp().getTyp())+Text.RESTART_IN.getText(start));break;
		case 1:broadcast(Text.PREFIX_GAME.getText(getTyp().getTyp())+Text.RESTART_IN.getText(start));break;
		case 0: 
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
			try {
				Runtime.getRuntime().exec("./start.sh");
			} catch (IOException e) {
				e.printStackTrace();
			}
			;break;
		}
	}
	
	@EventHandler
	public void Lobby(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
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
				broadcast(Text.PREFIX_GAME.getText(getTyp().getTyp())+"Das Spiel startet in "+C.cDAqua+start+C.cGray+" sekunden.");
				Bukkit.getWorld("world").setWeatherDuration(0);
				Bukkit.getWorld("world").setStorm(false);
				break;
			case 90:broadcast(Text.PREFIX_GAME.getText(getTyp().getTyp())+"Das Spiel startet in "+C.cDAqua+start+C.cGray+" sekunden.");break;
			case 60:broadcast(Text.PREFIX_GAME.getText(getTyp().getTyp())+"Das Spiel startet in "+C.cDAqua+start+C.cGray+" sekunden.");break;
			case 30:broadcast(Text.PREFIX_GAME.getText(getTyp().getTyp())+"Das Spiel startet in "+C.cDAqua+start+C.cGray+" sekunden.");break;
			case 15:
				broadcast(Text.PREFIX_GAME.getText(getTyp().getTyp())+"Das Spiel startet in "+C.cDAqua+start+C.cGray+" sekunden.");
				for(Player p : UtilServer.getPlayers())Title.sendTitle(p, "§aMap:§6 "+getGame().getWorldData().getMapName());
				break;
			case 10:broadcast(Text.PREFIX_GAME.getText(getTyp().getTyp())+"Das Spiel startet in "+C.cDAqua+start+C.cGray+" sekunden.");break;
			case 3:broadcast(Text.PREFIX_GAME.getText(getTyp().getTyp())+"Das Spiel startet in "+C.cDAqua+start+C.cGray+" sekunden.");break;
			case 2:broadcast(Text.PREFIX_GAME.getText(getTyp().getTyp())+"Das Spiel startet in "+C.cDAqua+start+C.cGray+" sekunden.");break;
			case 1:broadcast(Text.PREFIX_GAME.getText(getTyp().getTyp())+"Das Spiel startet in "+C.cDAqua+start+C.cGray+" sekunden.");break;
			}
		}else{
			if(UtilServer.getPlayers().length>=game.getMin_Players()){
				Bukkit.getPluginManager().callEvent(new GameStartEvent(getTyp()));
				updateInfo(GameState.InGame);
			}else{
				start=-1;
				broadcast(Text.PREFIX_GAME.getText(getTyp().getTyp())+C.cRed+"Es sind zu wenig Spieler(min. "+getGame().getMin_Players()+") online! Wartemodus wird neugestartet!");
			}
		}
	}

}
