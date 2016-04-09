package eu.epicpvp.karcade;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

import dev.wolveringer.client.Callback;
import dev.wolveringer.client.ClientWrapper;
import dev.wolveringer.client.ProgressFuture;
import dev.wolveringer.dataserver.gamestats.GameState;
import dev.wolveringer.dataserver.gamestats.GameType;
import dev.wolveringer.dataserver.gamestats.StatsKey;
import dev.wolveringer.dataserver.protocoll.packets.PacketOutTopTen;
import dev.wolveringer.dataserver.protocoll.packets.PacketOutTopTen.RankInformation;
import eu.epicpvp.karcade.Events.RankingEvent;
import eu.epicpvp.karcade.Game.Game;
import eu.epicpvp.karcade.Game.Events.GameStartEvent;
import eu.epicpvp.karcade.Game.Events.GameStateChangeEvent;
import eu.epicpvp.karcade.Game.Multi.MultiGames;
import eu.epicpvp.karcade.Game.Single.SingleGame;
import eu.epicpvp.karcade.Game.Single.Games.CustomWars.CustomWars;
import eu.epicpvp.karcade.Game.Single.Games.CustomWars.CustomWarsType;
import eu.epicpvp.karcade.Game.Single.Games.CustomWars.SheepWars.SheepWars;
import eu.epicpvp.karcade.Game.Single.Games.DeathGames.DeathGames;
import eu.epicpvp.karcade.Game.Single.Games.Falldown.Falldown;
import eu.epicpvp.karcade.Game.Single.Games.Masterbuilders.Masterbuilders;
import eu.epicpvp.karcade.Game.Single.Games.Masterbuilders.MasterbuildersType;
import eu.epicpvp.karcade.Game.Single.Games.OneInTheChamber.OneInTheChamber;
import eu.epicpvp.karcade.Game.Single.Games.QuickSurvivalGames.QuickSurvivalGames;
import eu.epicpvp.karcade.Game.Single.Games.SkyPvP.SkyPvP;
import eu.epicpvp.karcade.Game.Single.Games.SkyWars.SkyWars;
import eu.epicpvp.karcade.Game.Single.Games.SkyWars.SkyWarsType;
import eu.epicpvp.karcade.Game.Single.Games.SurvivalGames.SurvivalGames;
import eu.epicpvp.karcade.Game.Single.Games.TroubleInMinecraft.TroubleInMinecraft;
import eu.epicpvp.karcade.Service.CommandService;
import eu.epicpvp.kcore.Addons.AddonDay;
import eu.epicpvp.kcore.Addons.AddonNight;
import eu.epicpvp.kcore.Calendar.Calendar;
import eu.epicpvp.kcore.Calendar.Calendar.CalendarType;
import eu.epicpvp.kcore.Command.CommandHandler;
import eu.epicpvp.kcore.Disguise.DisguiseManager;
import eu.epicpvp.kcore.Enum.GameStateChangeReason;
import eu.epicpvp.kcore.Events.ServerChangeGameTypeEvent;
import eu.epicpvp.kcore.Hologram.Hologram;
import eu.epicpvp.kcore.Listener.kListener;
import eu.epicpvp.kcore.LogHandler.Event.LogEvent;
import eu.epicpvp.kcore.MySQL.MySQL;
import eu.epicpvp.kcore.Nick.NickManager;
import eu.epicpvp.kcore.Permission.PermissionManager;
import eu.epicpvp.kcore.Pet.PetManager;
import eu.epicpvp.kcore.Util.Color;
import eu.epicpvp.kcore.Util.UtilBG;
import eu.epicpvp.kcore.Util.UtilInv;
import eu.epicpvp.kcore.Util.UtilServer;
import lombok.Getter;
import lombok.Setter;

public class kArcadeManager extends kListener{

	@Getter
	@Setter
	private Game game = null;
	@Getter
	@Setter
	private PermissionManager permManager;
	@Getter
	@Setter
	private JavaPlugin Instance;
	@Getter
	@Setter
	private Location lobby=new Location(Bukkit.getWorld("world"),-465.352,68,249.518);
	@Getter
	private HashMap<Integer,Sign> ranking = new HashMap<>();
	@Getter
	@Setter
	private Location loc_stats = new Location(Bukkit.getWorld("world"), -473.441,66,258.683);
	@Getter
	@Setter
	private Location loc_raking = new Location(Bukkit.getWorld("world"),-476.537,66,253.671);
	@Getter
	@Setter
	private String[] string_ranking;
	@Getter
	private MySQL mysql;
	@Getter
	private ClientWrapper client;
	@Getter
	private CommandHandler cmd;
	private PetManager pet;
	private DisguiseManager disguiseManager;
	@Getter
	private CommandService service;
	private Hologram hologram;
	@Getter
	private NickManager nickManager;
	
	public kArcadeManager(JavaPlugin plugin, String modulName,String g,PermissionManager permManager,MySQL mysql,ClientWrapper client,CommandHandler cmd) {
		super(plugin,"kArcadeManager");
		this.Instance=plugin;
		this.lobby.setPitch((float) 27.6);
		this.lobby.setYaw( (float)52.4 );
		this.permManager=permManager;
		this.mysql=mysql;
		this.cmd=cmd;
		this.client=client;
		Calendar.getHoliday();
		this.game=Game(g);
		this.service=new CommandService(permManager);
		this.hologram=new Hologram(getInstance());
		this.hologram.RemoveText();
		this.nickManager=new NickManager(permManager);
		new ArrowBugListener(plugin);
		getLoc_stats().getWorld().loadChunk(getLoc_stats().getWorld().getChunkAt(getLoc_stats()));
		getLoc_raking().getWorld().loadChunk(getLoc_raking().getWorld().getChunkAt(getLoc_raking()));
		
		cmd.register(CommandService.class, this.service);
		for(Entity e : getLobby().getWorld().getEntities()){
			if(!(e instanceof Player)){
				e.remove();
			}
		}
		Bukkit.getPluginManager().callEvent(new RankingEvent());
		
		if(Calendar.holiday!=null){
			switch(Calendar.holiday){
			case HALLOWEEN:
				new AddonNight(getInstance(),getLobby().getWorld());
				getLobby().getWorld().setStorm(false);
				break;
			case WEIHNACHTEN:
				new AddonDay(getInstance(),getLobby().getWorld());
				getLobby().getWorld().setStorm(true);
				break;
			default:
				new AddonDay(getInstance(),getLobby().getWorld());
				getLobby().getWorld().setStorm(false);
				break;
			}
		}
		
		if(getGame() instanceof SingleGame)getGame().setState(GameState.LobbyPhase);
		
		new AddonDay(getInstance(), getLobby().getWorld());
	}
	
	public void setMaxMemory(int gb){
 		 try {
 			 File file = new File("start.sh");
 	         FileWriter fstream= new FileWriter(file);
 	 		 BufferedWriter out = new BufferedWriter(fstream);
 	 		 out.write("rm usercache.json");
 	 		 out.write("\n");
 	 		 out.write("rm -R world/playerdata/*");
 	 		 out.write("\n");
 	 		 out.write("screen -AdmS a"+kArcade.id+" java -server -XX:+UseConcMarkSweepGC -XX:MaxGCPauseMillis=50 -XX:+UseParNewGC -XX:+CMSIncrementalPacing -XX:ParallelGCThreads=7 -XX:+AggressiveOpts -Xms100M -Xmx"+gb+"G -d64 -jar paperspigot.jar nogui");
	 		 out.close();
	 		 
	 		 DebugLog("Der maximale Ram wurde auf "+gb+"GB gesetzt!");
 		 } catch (IOException e) {
			e.printStackTrace();
		}       		      
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void Start(GameStartEvent ev){
		Calendar.getHoliday();
	}
	
	public CalendarType getHoliday(){
		return Calendar.holiday;
	}
	
	public void setNewGame(GameType typ,String cTyp){
		getInstance().getConfig().set("Config.Server.Game", typ.getTyp()+cTyp);
		getInstance().saveConfig();
		if(UtilServer.getPlayers().size()!=0)for(Player p : UtilServer.getPlayers())UtilBG.sendToServer(p, getInstance());
		Bukkit.getScheduler().scheduleSyncDelayedTask(getInstance(), new Runnable(){

  			@Override
  			public void run() {
  				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
  			}
      		 
      	 }, 4*20);
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
	
	public void setRanking(StatsKey win){
		ProgressFuture<PacketOutTopTen> response = getClient().getTopTen(getGame().getType(), win);
		
		response.getAsync(new Callback<PacketOutTopTen>() {
			@Override
			public void call(PacketOutTopTen packet) {
				setString_ranking(new String[11]);
				getString_ranking()[0]=Color.GREEN+getGame().getType().getTyp()+Color.ORANGE+"§l Ranking";
				int i = 1;
				
				for(RankInformation rInfo : packet.getRanks()){
					getString_ranking()[i]="§l#"+i+"§r "+rInfo.getPlayer()+" "+win.getMySQLName()+": "+rInfo.getTopValue();
					i++;
				}
			}
		});
	}
	
	public void restart(){
		Bukkit.getScheduler().runTask(getInstance(), new Runnable() {
			
			@Override
			public void run() {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
			}
		});
	}
	
	public Game Game(String game){
		if(GameType.OneInTheChamber.getTyp().equalsIgnoreCase(game)){
			return new OneInTheChamber(this);
		}else if(GameType.SurvivalGames1vs1.getTyp().equalsIgnoreCase(game)){
			return new MultiGames(this,game);
		}else if(GameType.BedWars1vs1.getTyp().equalsIgnoreCase(game)){
			return new MultiGames(this,game);
		}else if(GameType.SkyWars1vs1.getTyp().equalsIgnoreCase(game)){
			return new MultiGames(this,game);
		}else if(game.toLowerCase().startsWith("bedwars")){
			for(CustomWarsType type : CustomWarsType.values()){
				if(game.equalsIgnoreCase("bedwars"+type.name())){
					return new CustomWars(this, GameType.BedWars, type);
				}
			}
			CustomWarsType r = CustomWarsType.random(GameType.BedWars);
			getInstance().getConfig().set("Config.Server.Game", GameType.BedWars.getTyp()+r.name());
			getInstance().saveConfig();
			
			return new CustomWars(this, GameType.BedWars, CustomWarsType.random(GameType.BedWars));
		}else if(game.toLowerCase().startsWith("sheepwars")){
			for(CustomWarsType type : CustomWarsType.values()){
				if(game.equalsIgnoreCase("sheepwars"+type.name())){
					return new SheepWars(this, type);
				}
			}
			CustomWarsType r = CustomWarsType.random(GameType.SheepWars);
			getInstance().getConfig().set("Config.Server.Game", GameType.SheepWars.getTyp()+r.name());
			getInstance().saveConfig();
			
			return new SheepWars(this, CustomWarsType.random(GameType.SheepWars));
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
		}else if(GameType.Versus.getTyp().equalsIgnoreCase(game)){
			return new MultiGames(this,game);
		}else if(GameType.QuickSurvivalGames.getTyp().equalsIgnoreCase(game)){
			return new QuickSurvivalGames(this);
		}else if("SkyWars12".equalsIgnoreCase(game)){
			return new SkyWars(this,SkyWarsType._12x1);
		}else if("SkyWars128".equalsIgnoreCase(game)){
			return new SkyWars(this,SkyWarsType._32x4);
		}else if(game.toLowerCase().startsWith("skywars")){
			for(SkyWarsType type : SkyWarsType.values()){
				if(game.equalsIgnoreCase("skywars"+type.name())){
					return new SkyWars(this,type);
				}
			}
			SkyWarsType r = SkyWarsType.random();
			getInstance().getConfig().set("Config.Server.Game", GameType.SkyWars.getTyp()+r.name());
			getInstance().saveConfig();
			
			return new SkyWars(this,r);
		}else if(GameType.Masterbuilders.getTyp().equalsIgnoreCase(game)){
			return new Masterbuilders(this,MasterbuildersType.random());
		}else{
			return new OneInTheChamber(this);
		}
	}
	
	public void DebugLog(String m){
		DebugLog(null,0,m);
	}
	
	public void DebugLog(long time,String c){
		DebugLog(c,time,null);
	}
	
	public void DebugLog(long time,String reason,String c){
		DebugLog(c, 0, reason);
	}
	
	public void DebugLog(String... reason){
		DebugLog(null,0,reason);
	}
	
	public void DebugLog(String clas,long time,String... reason){
		if(clas!=null)System.err.println("[Debug]: Class: "+clas);
		if(reason!=null)for(String m : reason)System.err.println("[Debuge]: Reason: "+m);
		if(time!=0)System.err.println("[Debug]: Zeit: "+ ((System.currentTimeMillis()-time) / 1000.0D) + " Seconds");
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
	    ((CraftEntity)player).getHandle().getDataWatcher().watch(9, Byte.valueOf((byte)0));
	}
	
	@EventHandler
	public void Mobs(CreatureSpawnEvent ev){
		if(ev.getLocation().getWorld()==getLobby().getWorld())ev.setCancelled(true);
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
	     }else if (cmd.equalsIgnoreCase("/minecraft")) {
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
	  				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
	  			}
	      		 
	      	 }, 4*20);
	    	 
	     }
	   }
	
	@EventHandler 
	public void Packet(ServerChangeGameTypeEvent ev){		
		logMessage("Game Change to "+ev.getType() + " " + ev.getSubType());
		getInstance().getConfig().set("Config.Server.Game", ev.getType()+(ev.getSubType().equalsIgnoreCase("none") ? "":ev.getSubType()));
		
		getInstance().saveConfig();
		if(getGame()!=null)getGame().setState(GameState.Restart, GameStateChangeReason.CHANGE_TYPE);
		restart();
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void GameStateForCoins(GameStateChangeEvent ev){
		if(ev.getTo()==GameState.Restart){
			getGame().getMoney().saveAll();
		}
	}
}
