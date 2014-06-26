package me.kingingo.karcade;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;
import me.kingingo.karcade.Game.Game;
import me.kingingo.karcade.Game.Events.GameStartEvent;
import me.kingingo.karcade.Game.Events.GameStateChangeEvent;
import me.kingingo.karcade.Game.Games.OneInTheChamber;
import me.kingingo.karcade.Game.Games.Rush;
import me.kingingo.karcade.Game.Games.SurvivalGames;
import me.kingingo.karcade.Game.World.WorldData;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.Permission.PermissionManager;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.C;
import me.kingingo.kcore.Util.UtilBG;
import me.kingingo.kcore.Util.UtilDisplay;
import me.kingingo.kcore.Util.UtilInv;
import me.kingingo.kcore.Util.UtilPlayer;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.kcore.Util.UtilTime;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class kArcadeManager implements Listener{

	@Getter
	@Setter
	private Game game;
	@Getter
	@Setter
	private WorldData worldData;
	@Getter
	@Setter
	private PermissionManager pManager;
	@Getter
	private GameState state=GameState.NONE;
	@Getter
	@Setter
	private int LobbyCount=-1;
	@Getter
	@Setter
	private int RestartCount=-1;
	@Getter
	@Setter
	private JavaPlugin Instance;
	@Getter
	@Setter
	private String BungeeCord_Fallback_Server = "falldown";
	@Getter
	@Setter
	private GameType typ = GameType.NONE;
	@Getter
	@Setter
	private Location lobby=new Location(Bukkit.getWorld("world"), -180,87,254);
	
	public kArcadeManager(JavaPlugin plugin, String modulName,String g,PermissionManager pManager) {
		this.Instance=plugin;
		Bukkit.getPluginManager().registerEvents(this, getInstance());
		this.game=Game(g);
		this.pManager=pManager;
	}
	
	public Game Game(String game){
		if(GameType.OneInTheChamber.string().equalsIgnoreCase(game)){
			return new OneInTheChamber(this);
		}else if(GameType.Rush.string().equalsIgnoreCase(game)){
			return new Rush(this);
		}else if(GameType.Rush.string().equalsIgnoreCase(game)){
			return new Rush(this);
		}else if(GameType.SurvivalGames.string().equalsIgnoreCase(game)){
			return new SurvivalGames(this);
		}else{
			return new OneInTheChamber(this);
		}
	}
	
	public ArrayList<String> LoadFiles(String gameName){
	    File folder = new File(File.separator+"root"+File.separator+"Maps"+File.separator+gameName);
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
	            maps.add(File.separator+"root"+File.separator+"Maps"+File.separator+gameName+File.separator+file.getName().substring(0, file.getName().length() - 4)+".zip");
	          }
	        }
	      }
	    }
	    for (String map : maps) {
	      System.out.println("Maps: " + map);
	    }
	    return maps;
	  }
	
	public void DebugLog(long time,int zeile,String c){
		System.err.println("[DebugMode]: Class: "+c);
		System.err.println("[DebugMode]: Zeile: "+zeile);
		System.err.println("[DebugMode]: Zeit: "+UtilTime.convertString(System.currentTimeMillis() - time, 1, UtilTime.TimeUnit.FIT));
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

	    player.setLevel(0);
	    player.setExp(0.0F);
	    
	    ((CraftPlayer)player).getHandle().k = true;

	    ((CraftPlayer)player).getHandle().p(0);
	}
	
	public void setState(GameState gs){
//		if(gs==GameState.Restart){
//			System.out.println("RESTART!!   ");
//			return;
//		}
		GameStateChangeEvent stateEvent = new GameStateChangeEvent(state);
		Bukkit.getPluginManager().callEvent(stateEvent);
		if(stateEvent.isCancelled())return;
		state=gs;
		System.out.println("["+this.typ.string()+"] GameState wurde zu "+state.string()+" geändert.");
	}
	
	public boolean isType(GameType gt){
		return typ==gt;
	}
	
	public boolean isState(GameState gs){
		return gs==getState();
	}
	
	public void setType(GameType gt){
		this.typ=gt;
	}
	
	public GameType getType(){
		return this.typ;
	}
	
	public void broadcast(String message){
	    for (Player player : UtilServer.getPlayers())
	    {
	      player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);

	      UtilPlayer.message(player, message);
	    }

	    System.out.println(Text.PREFIX.getText() + message);
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
	     } else if (cmd.equalsIgnoreCase("/pl")) {
	         Bukkit.broadcastMessage("Der Spieler: " + p.getName() + " möchte gerne unsere Plugins sich ansehen...");

	       event.setCancelled(true);
	      p.sendMessage(ChatColor.RED + "Nope :3");
	     } else if (cmd.equalsIgnoreCase("/about")) {
	         Bukkit.broadcastMessage("Der Spieler: " + p.getName() + " möchte gerne unsere Plugins sich ansehen...");

	       event.setCancelled(true);
	       p.sendMessage(ChatColor.RED + "Nope :3");
	     } else if (cmd.equalsIgnoreCase("/version")) {
	         Bukkit.broadcastMessage("Der Spieler: " + p.getName() + " möchte gerne unsere Plugins sich ansehen...");

	     event.setCancelled(true);
	       p.sendMessage(ChatColor.RED + "Nope :3");
	     } else if (cmd.equalsIgnoreCase("/me")) {
	      event.setCancelled(true);
	       p.sendMessage(ChatColor.RED + "Nope :3");
	     } else if (cmd.equalsIgnoreCase("/?")) {
	         Bukkit.broadcastMessage("Der Spieler: " + p.getName() + " möchte gerne unsere Plugins sich ansehen...");

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
	
	@EventHandler
	public void Restart(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		if(getState()!=GameState.Restart)return;
		if(RestartCount<0)RestartCount=30;
		RestartCount--;
		for(Player p : Bukkit.getOnlinePlayers())UtilDisplay.displayTextBar(p, Text.RESTART_IN.getText(RestartCount));
		
		switch(RestartCount){
		case 30:broadcast(Text.PREFIX.getText()+Text.RESTART_IN.getText(RestartCount));break;
		case 25:
			broadcast(Text.PREFIX.getText()+Text.RESTART_IN.getText(RestartCount));
			for(Player p : Bukkit.getOnlinePlayers())UtilBG.sendToServer(p, BungeeCord_Fallback_Server, getInstance());
			break;
		case 15:broadcast(Text.PREFIX.getText()+Text.RESTART_IN.getText(RestartCount));break;
		case 10:broadcast(Text.PREFIX.getText()+Text.RESTART_IN.getText(RestartCount));break;
		case 5:broadcast(Text.PREFIX.getText()+Text.RESTART_IN.getText(RestartCount));
			for(Player p : Bukkit.getOnlinePlayers())UtilBG.sendToServer(p, BungeeCord_Fallback_Server, getInstance());
			break;
		case 4:broadcast(Text.PREFIX.getText()+Text.RESTART_IN.getText(RestartCount));break;
		case 3:broadcast(Text.PREFIX.getText()+Text.RESTART_IN.getText(RestartCount));break;
		case 2:broadcast(Text.PREFIX.getText()+Text.RESTART_IN.getText(RestartCount));break;
		case 1:broadcast(Text.PREFIX.getText()+Text.RESTART_IN.getText(RestartCount));break;
		case 0: 
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
			try {
				Runtime.getRuntime().exec("./start.sh");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			;break;
		}
	}
	
	@EventHandler
	public void Lobby(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		if(getState()!=GameState.LobbyPhase)return;
		if(LobbyCount<0)LobbyCount=20;
		LobbyCount--;
		for(Player p : UtilServer.getPlayers())UtilDisplay.displayTextBar(p, C.cGray+"Das Spiel startet in "+C.cDAqua+LobbyCount+C.cGray+" sekunden.");
		if(LobbyCount!=0){
			switch(LobbyCount){
			case 120:
				broadcast(Text.PREFIX.getText()+"Das Spiel startet in "+C.cDAqua+LobbyCount+C.cGray+" sekunden.");
				Bukkit.getWorld("world").setWeatherDuration(0);
				Bukkit.getWorld("world").setStorm(false);
				
				break;
			case 90:broadcast(Text.PREFIX.getText()+"Das Spiel startet in "+C.cDAqua+LobbyCount+C.cGray+" sekunden.");break;
			case 60:broadcast(Text.PREFIX.getText()+"Das Spiel startet in "+C.cDAqua+LobbyCount+C.cGray+" sekunden.");break;
			case 30:broadcast(Text.PREFIX.getText()+"Das Spiel startet in "+C.cDAqua+LobbyCount+C.cGray+" sekunden.");break;
			case 15:broadcast(Text.PREFIX.getText()+"Das Spiel startet in "+C.cDAqua+LobbyCount+C.cGray+" sekunden.");break;
			case 10:broadcast(Text.PREFIX.getText()+"Das Spiel startet in "+C.cDAqua+LobbyCount+C.cGray+" sekunden.");break;
			case 3:broadcast(Text.PREFIX.getText()+"Das Spiel startet in "+C.cDAqua+LobbyCount+C.cGray+" sekunden.");break;
			case 2:broadcast(Text.PREFIX.getText()+"Das Spiel startet in "+C.cDAqua+LobbyCount+C.cGray+" sekunden.");break;
			case 1:broadcast(Text.PREFIX.getText()+"Das Spiel startet in "+C.cDAqua+LobbyCount+C.cGray+" sekunden.");break;
			}
		}else{
			if(Bukkit.getOnlinePlayers().length>=game.getMin_Players()){
				Bukkit.getPluginManager().callEvent(new GameStartEvent(getType()));
			}else{
				LobbyCount=-1;
				broadcast(Text.PREFIX.getText()+C.cRed+"Es sind zu wenig Spieler online! Wartemodus wird neugestartet!");
			}
		}
	}

}
