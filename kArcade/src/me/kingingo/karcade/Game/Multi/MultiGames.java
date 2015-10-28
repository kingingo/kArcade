package me.kingingo.karcade.Game.Multi;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;
import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Enum.PlayerState;
import me.kingingo.karcade.Game.Game;
import me.kingingo.karcade.Game.Events.GameUpdateInfoEvent;
import me.kingingo.karcade.Game.Multi.Addons.AddonArenaRestore;
import me.kingingo.karcade.Game.Multi.Events.MultiGamePlayerJoinEvent;
import me.kingingo.karcade.Game.Multi.Games.MultiGame;
import me.kingingo.karcade.Game.Multi.Games.Versus.Versus;
import me.kingingo.karcade.Game.World.WorldData;
import me.kingingo.karcade.Service.Games.ServiceMultiGames;
import me.kingingo.kcore.Client.Events.ClientReceiveMessageEvent;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.Enum.Team;
import me.kingingo.kcore.Packet.Events.PacketReceiveEvent;
import me.kingingo.kcore.Packet.Packets.VERSUS_SETTINGS;
import me.kingingo.kcore.StatsManager.Stats;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.TabTitle;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilPlayer;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.kcore.Util.UtilWorldEdit;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Versus.PlayerKitManager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class MultiGames extends Game{
	
	@Getter
	private HashMap<String,MultiGame> games = new HashMap<>(); // Auflistung aller MutltiGame Arenas
	@Getter
	private HashMap<MultiGame,HashMap<Team,ArrayList<Location>>> locs = new HashMap<>(); //Alle Team Locations der einzelnen MultiGame Arenas
	@Getter
	@Setter
	private WorldData worldData;
	private HashMap<String,VERSUS_SETTINGS> warte_liste = new HashMap<>(); //Warte Liste falls Spieler zu fr�h auf dem Server kommen...
	@Getter
	@Setter
	private boolean CreatureSpawn = true; //Creature Spawn GLOBAL
	@Getter
	private PlayerKitManager kitManager;
	@Getter
	@Setter
	private int played_games=0;
	
	public MultiGames(kArcadeManager manager,String type){
		super(manager);
		setSet_default_scoreboard(false);
		registerListener();
		ServiceMultiGames.setGames(this);
		setTyp(GameType.valueOf(type));
		setWorldData(new WorldData(getManager(), getType()));
		createGames(getType());
	}
	
	public boolean isPlayedGames(){
		return (getPlayed_games()>80);
	}
	
	public void updatePlayedGames(){
		setPlayed_games(getPlayed_games()+1);
	}
	
	public void createGames(GameType type){
		if(GameType.Versus==type){
			this.kitManager=new PlayerKitManager(getManager().getMysql(), GameType.Versus);
			new AddonArenaRestore(getManager().getInstance());
			setCreatureSpawn(false);
			getWorldData().createCustomWorld("90,quartz_block");
			for(org.bukkit.entity.Entity e : getWorldData().getWorld().getEntities())e.remove();
			File[] schematics = getWorldData().loadSchematicFiles();
			Location loc = new Location(getWorldData().getWorld(),0,120,0);
			
			long time;
			Versus v;
			for(File file : schematics){
				time=System.currentTimeMillis();
				getWorldData().pastePlate(loc, file);
				v=new Versus(this, file.getName().replaceAll(".schematic", "") ,loc);
				games.put(v.getArena(), v);
				loc=loc.add(0, 0, 600);
				getManager().DebugLog(time,"PASTE" ,MultiGames.class.getName());
			}
			
			for(MultiGame game : games.values()){
				game.setState(GameState.LobbyPhase,false);
			}
		}
	}
	
	@EventHandler
	  public void MobSpawn(CreatureSpawnEvent ev){
		if(ev.getSpawnReason()==SpawnReason.CUSTOM)return;
	    if (!isCreatureSpawn()){
			if(getManager().getService().isDebug())System.err.println("[MultiGame] CreatureSpawnEvent GLOBAL Cancelled TRUE!");
	    	ev.setCancelled(true);
	    }
	  }
	
	@EventHandler
	public void GameUpdateInfo(GameUpdateInfoEvent ev){
		ev.setCancelled(true);
	}
	
	@EventHandler
	public void Re(ClientReceiveMessageEvent ev){
		if(ev.getMessage().contains("VERSUS_SETTINGS"))System.out.println("PACKET: "+ev.getMessage());
	}
	
	@EventHandler
	public void PLACE(BlockPlaceEvent event) {
		//LOBBY PLACE CHANGE CANCEL
		if(event.getPlayer().getWorld() == getManager().getLobby().getWorld()){
			event.setCancelled(true);
		}
    }
	
	@EventHandler
	public void BREAK(BlockBreakEvent event) {
		//LOBBY BREAK CHANGE CANCEL
		if(event.getPlayer().getWorld() == getManager().getLobby().getWorld()){
			event.setCancelled(true);
		}
    }
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		//LOBBY INTERACT CHANGE CANCEL
		if(event.getPlayer().getWorld() == getManager().getLobby().getWorld()){
			event.setCancelled(true);
		}
    }
	
	@EventHandler
	public void Food(FoodLevelChangeEvent ev){
		//LOBBY FOOD CHANGE CANCEL
		if(ev.getEntity().getWorld() == getManager().getLobby().getWorld()){
			ev.setCancelled(true);
		}
	}
	
	@EventHandler
	public void EntityDamageByEntity(EntityDamageByEntityEvent ev){
		//LOBBY DAMAGE CANCEL
		if(ev.getEntity().getWorld() == getManager().getLobby().getWorld()){
			ev.setCancelled(true);
		}
	}
	
	@EventHandler
	public void Join(PlayerJoinEvent ev){
		ev.setJoinMessage(null);
	}
	
	@EventHandler
	public void Quit(PlayerQuitEvent ev){
		if(getKitManager().getKits().containsKey(UtilPlayer.getRealUUID(ev.getPlayer()))){
			getKitManager().getKits().remove(UtilPlayer.getRealUUID(ev.getPlayer()));
		}
		
		ev.setQuitMessage(null);
	}
	
	@EventHandler
	public void EntityDamage(EntityDamageEvent ev){
		//LOBBY DAMAGE CANCEL
		if(ev.getEntity().getWorld() == getManager().getLobby().getWorld()){
			ev.setCancelled(true);
		}
	}
	
	MultiGamePlayerJoinEvent event;
	@EventHandler
	public void Joina(PlayerJoinEvent ev){
		getManager().Clear(ev.getPlayer());
		TabTitle.setHeaderAndFooter(ev.getPlayer(), "�eEPICPVP �7-�e "+getType().getTyp(), "�eShop.EpicPvP.de");
		if(warte_liste.containsKey(ev.getPlayer().getName())){
			VERSUS_SETTINGS settings = (VERSUS_SETTINGS)warte_liste.get(ev.getPlayer().getName());
			MultiGame g = games.get(settings.getArena());
			
			if(g instanceof Versus){
				if(((Versus)g).getMax_type().getTeam().length>=settings.getType().getTeam().length&&settings.getArena().equalsIgnoreCase(g.getArena())&& (g.getState() == GameState.LobbyPhase||g.getState() == GameState.Laden) ){
						if(UtilPlayer.isOnline(settings.getPlayer())){
							if(warte_liste.containsKey(settings.getPlayer())){
								warte_liste.remove(settings.getPlayer());
							}
								
							if(settings.getTeam()==Team.SOLO){
								settings.setTeam(g.littleTeam());
							}
								
							g.getTeamList().put(Bukkit.getPlayer(settings.getPlayer()), settings.getTeam());
							g.getGameList().addPlayer(Bukkit.getPlayer(settings.getPlayer()), PlayerState.IN);
								
							if(settings.getKit().equalsIgnoreCase(settings.getPlayer())){
								g.setKit( getKitManager().getKit(Bukkit.getPlayer(settings.getPlayer()).getUniqueId(), getStats().getInt(Stats.KIT_ID, Bukkit.getPlayer(settings.getPlayer()))) );
									
								System.out.println("G: "+g.getKit()==null);
								System.out.println("G: "+settings.getKit());
									
								if(g.getKit()!=null){
									System.out.println("G: "+g.getKit().id);
									System.out.println("G: "+g.getKit().kit==null);
								}
									
								if(g.getKit()!=null){
									g.getKit().kit=settings.getKit();
								}
							}
							((Versus)g).setType(settings.getType());
							g.setMin_team(settings.getMin_team());
							g.setMax_team(settings.getMax_team());
							g.setState(GameState.Laden);
								
							event=new MultiGamePlayerJoinEvent(Bukkit.getPlayer(settings.getPlayer()),g);
							Bukkit.getPluginManager().callEvent(event);
						}
				}
			}
		}
		
		//Spieler ist noch keiner Arena zugewiesen deswegen auf die Warte Liste
		if(event==null||!event.isCancelled()){
			//Spieler wird in die Lobby zum warten Teleportiert!
			System.out.println("WARTELISTE: "+ev.getPlayer().getName());
			getManager().getLobby().getWorld().setStorm(false);
			getManager().getLobby().getWorld().setTime(4000);
			ev.getPlayer().teleport(getManager().getLobby());
		}
		event=null;
	}
	
	@EventHandler
	public void PacketReceive(PacketReceiveEvent ev){
		if(getType()==GameType.Versus&&ev.getPacket() instanceof VERSUS_SETTINGS){
			VERSUS_SETTINGS settings = (VERSUS_SETTINGS)ev.getPacket();
			MultiGame g = games.get(settings.getArena());
			if(g instanceof Versus){
				if(((Versus)g).getMax_type().getTeam().length>=settings.getType().getTeam().length&&
						settings.getArena().equalsIgnoreCase(g.getArena())&&
						(g.getState() == GameState.LobbyPhase|| g.getState() == GameState.Laden) ){
						
						if(UtilPlayer.isOnline(settings.getPlayer())){
							if(warte_liste.containsKey(settings.getPlayer())){
								System.out.println("FIND: "+settings.getPlayer());
								warte_liste.remove(settings.getPlayer());
							}
								
							if(settings.getTeam()==Team.SOLO){
								settings.setTeam(g.littleTeam());
							}
								
							g.getTeamList().put(Bukkit.getPlayer(settings.getPlayer()), settings.getTeam());
							g.getGameList().addPlayer(Bukkit.getPlayer(settings.getPlayer()), PlayerState.IN);
							
							if(settings.getKit().equalsIgnoreCase(settings.getPlayer())){
								g.setKit( getKitManager().getKit(Bukkit.getPlayer(settings.getPlayer()).getUniqueId(), getStats().getInt(Stats.KIT_ID, Bukkit.getPlayer(settings.getPlayer()))) );
								g.getKit().kit=settings.getKit();
							}

							((Versus)g).setType(settings.getType());
							g.setMin_team(settings.getMin_team());
							g.setMax_team(settings.getMax_team());
							g.setState(GameState.Laden);
								
							event=new MultiGamePlayerJoinEvent(Bukkit.getPlayer(settings.getPlayer()),g);
							Bukkit.getPluginManager().callEvent(event);
						}else{
							warte_liste.put(settings.getPlayer(),settings);
						}
				}
			}
		}
	}
	
}
