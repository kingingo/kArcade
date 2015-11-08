package me.kingingo.karcade.Game.Multi;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;
import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Enum.PlayerState;
import me.kingingo.karcade.Game.Game;
import me.kingingo.karcade.Game.Multi.Addons.AddonArenaRestore;
import me.kingingo.karcade.Game.Multi.Events.MultiGamePlayerJoinEvent;
import me.kingingo.karcade.Game.Multi.Events.MultiGameStartEvent;
import me.kingingo.karcade.Game.Multi.Games.MultiGame;
import me.kingingo.karcade.Game.Multi.Games.Versus.Versus;
import me.kingingo.karcade.Game.World.WorldData;
import me.kingingo.karcade.Service.Games.ServiceMultiGames;
import me.kingingo.kcore.Client.Events.ClientReceiveMessageEvent;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.Enum.Team;
import me.kingingo.kcore.Game.Events.GameStateChangeEvent;
import me.kingingo.kcore.Packet.Events.PacketReceiveEvent;
import me.kingingo.kcore.Packet.Packets.ARENA_SETTINGS;
import me.kingingo.kcore.StatsManager.Stats;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.TabTitle;
import me.kingingo.kcore.Util.UtilDebug;
import me.kingingo.kcore.Util.UtilException;
import me.kingingo.kcore.Util.UtilMath;
import me.kingingo.kcore.Util.UtilPlayer;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.kcore.Versus.PlayerKitManager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
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
	private HashMap<String,ARENA_SETTINGS> warte_liste = new HashMap<>(); //Warte Liste falls Spieler zu fr�h auf dem Server kommen...
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
		UtilServer.createLagListener(manager.getCmd());
		setState(GameState.Laden);
		setSet_default_scoreboard(false);
		registerListener();
		ServiceMultiGames.setGames(this);
		setTyp(GameType.get(type));
		setWorldData(new WorldData(getManager(), getType()));
		createGames(getType());
		setApublic(false);
	}
	
	public boolean haveToRestart(){
		return (getPlayed_games() > 50);
	}
	
	public void updatePlayedGames(){
		setPlayed_games(getPlayed_games()+1);
	}
	
	public void createGames(GameType type){
		if(GameType.Versus==type){
			setPacketServer("versushub");
			this.kitManager=new PlayerKitManager(getManager().getMysql(), GameType.Versus);
			new AddonArenaRestore(getManager().getInstance());
			setCreatureSpawn(false);
			getWorldData().createCustomWorld("90,quartz_block");
			for(org.bukkit.entity.Entity e : getWorldData().getWorld().getEntities())e.remove();
			ArrayList<File> schematics = getWorldData().loadSchematics();
			Location loc = new Location(getWorldData().getWorld(),0,120,0);
			
			long time;
			Versus v;
			File file;
			for(int i = 0; i<=(schematics.size()<=6?schematics.size():6); i++){
				if(!schematics.isEmpty()){
					if(schematics.size()==1){
						file = schematics.get(0);
					}else{
						file = schematics.get(UtilMath.r(schematics.size()));
					}
					time=System.currentTimeMillis();
					getWorldData().pastePlate(loc, file);
					v=new Versus(this, file.getName().replaceAll(".schematic", "") ,loc);
					games.put(v.getArena(), v);
					loc=loc.add(0, 0, 600);
					getManager().DebugLog(time,"PASTE - "+v.getArena() ,MultiGames.class.getName());
					schematics.remove(file);
				}else{
					break;
				}
			}
			
			loc=null;
			time=0;
			v=null;
			schematics.clear();
			schematics=null;
			
			for(MultiGame game : games.values()){
				game.setState(GameState.LobbyPhase,false);
			}
			setState(GameState.LobbyPhase);
		}
	}
	
	@EventHandler
	public void chat(AsyncPlayerChatEvent ev){
		if(!ev.getPlayer().isOp())ev.setCancelled(true);
	}
	
	@EventHandler
	public void antiLagg(UpdateEvent ev){
		if(ev.getType()==UpdateType.MIN_08&&!UtilServer.getPlayers().isEmpty()){
			UtilServer.getLagMeter().unloadChunks(null, null);
		}
	}
	
	/**
	 * Sendet vom Arcade Server Infos!
	 * @param ev
	 */
	@EventHandler
	public void updateInfo(UpdateEvent ev){
		if(ev.getType()==UpdateType.SLOW&&!isState(GameState.Laden)){
			if(haveToRestart()&&isState(GameState.Restart)){
				if(UtilServer.getPlayers().isEmpty()){
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
				}
			}
			updateInfo();
		}
	}
	
	/**
	 * Restartet wenn 50 Spiele vorbei sind und keine Spieler mehr online sind!
	 * @param ev
	 */
	@EventHandler
	public void restartQuit(PlayerQuitEvent ev){
		if(haveToRestart()&&isState(GameState.Restart)){
			if(UtilServer.getPlayers().isEmpty()){
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
			}
		}
	}
	
	/**
	 * Z�hlt die laufenden Spiele und sendet dies weiter zu den Daten Server
	 * @param ev
	 */
	@EventHandler
	public void arenaCounter(MultiGameStartEvent ev){
		updatePlayedGames();
		
		if(haveToRestart()){
			setState(GameState.Restart);
			
		}else if(setState( (getGames(GameState.InGame)>=getGames().size() ? GameState.InGame:GameState.LobbyPhase) )){
			updateInfo();
		}
	}
	
	@EventHandler
	public void restartState(GameStateChangeEvent ev){
		if(haveToRestart()&&ev.getFrom()==GameState.Restart)ev.setCancelled(true);
	}
	
	public int getGames(GameState state){
		if(state == GameState.NONE){
			return getGames().size();
		}else{
			int i = 0;
			for(MultiGame g : getGames().values())if(g.getState()==state)i++;
			return i;
		}
	}
	
	@EventHandler
	  public void MobSpawn(CreatureSpawnEvent ev){
//		if(ev.getSpawnReason()==SpawnReason.CUSTOM)return;
	    if (!isCreatureSpawn()){
			if(getManager().getService().isDebug())System.err.println("[MultiGame] CreatureSpawnEvent GLOBAL Cancelled TRUE!");
	    	ev.setCancelled(true);
	    }
	  }
	
	@EventHandler
	public void Re(ClientReceiveMessageEvent ev){
		if(ev.getMessage().contains("ARENA_SETTINGS"))System.out.println("PACKET: "+ev.getMessage());
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
		
		for(Player p : UtilServer.getPlayers()){
			p.hidePlayer(ev.getPlayer());
			ev.getPlayer().hidePlayer(p);
		}

		try{

			TabTitle.setHeaderAndFooter(ev.getPlayer(), "�eEPICPVP �7-�e "+getType().getTyp(), "�eShop.EpicPvP.de");
			if(warte_liste.containsKey(ev.getPlayer().getName())){
				ARENA_SETTINGS settings = (ARENA_SETTINGS)warte_liste.get(ev.getPlayer().getName());
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
		}catch(Exception e){
			UtilDebug.debug("Joina", e.getMessage());
			UtilException.catchException(e, "versus"+getManager().getInstance().getConfig().getString("Config.Server.ID"), Bukkit.getIp(), getManager().getMysql());
			
		}
	}
	
	@EventHandler
	public void PacketReceive(PacketReceiveEvent ev){
		if(getType()==GameType.Versus&&ev.getPacket() instanceof ARENA_SETTINGS){
			ARENA_SETTINGS settings = (ARENA_SETTINGS)ev.getPacket();
			MultiGame g = games.get(settings.getArena());
			if(g instanceof Versus){
				
				try{
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
				}catch(Exception e){
					UtilDebug.debug("PacketReceive", e.getMessage());
					UtilException.catchException(e, "versus"+getManager().getInstance().getConfig().getString("Config.Server.ID"), Bukkit.getIp(), getManager().getMysql());
					
					if(g!=null){
						if(((Versus)g).getMax_type()==null){
							UtilDebug.debug("PacketReceive", "PACKET: 1");
							System.out.println("PACKET: 1");
						}else if(((Versus)g).getMax_type().getTeam()==null){
							UtilDebug.debug("PacketReceive", "PACKET: 2");
							System.out.println("PACKET: 2");
						}
						
						if(settings.getType().getTeam()==null){
							UtilDebug.debug("PacketReceive", "PACKET: 3");
							System.out.println("PACKET: 3");
						}else if(settings.getType()==null){
							UtilDebug.debug("PacketReceive", "PACKET: 4");
							System.out.println("PACKET: 4");
						}
					}else{
						UtilDebug.debug("PacketReceive", "PACKET: 0");
					}
				}
			}
		}
	}
	
}
