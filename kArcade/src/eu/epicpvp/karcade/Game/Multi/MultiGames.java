package eu.epicpvp.karcade.Game.Multi;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
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
import org.bukkit.inventory.ItemStack;

import dev.wolveringer.client.connection.PacketListener;
import dev.wolveringer.dataserver.gamestats.GameState;
import dev.wolveringer.dataserver.gamestats.GameType;
import dev.wolveringer.dataserver.protocoll.packets.Packet;
import eu.epicpvp.karcade.kArcadeManager;
import eu.epicpvp.karcade.Command.CommandSpectate;
import eu.epicpvp.karcade.Game.Game;
import eu.epicpvp.karcade.Game.Events.GameStateChangeEvent;
import eu.epicpvp.karcade.Game.Multi.Addons.MultiAddonArenaRestore;
import eu.epicpvp.karcade.Game.Multi.Addons.MultiAddonChat;
import eu.epicpvp.karcade.Game.Multi.Events.MultiGamePlayerJoinEvent;
import eu.epicpvp.karcade.Game.Multi.Events.MultiGameStartEvent;
import eu.epicpvp.karcade.Game.Multi.Events.MultiGameUpdateInfoEvent;
import eu.epicpvp.karcade.Game.Multi.Games.MultiGame;
import eu.epicpvp.karcade.Game.Multi.Games.MultiTeamGame;
import eu.epicpvp.karcade.Game.Multi.Games.CustomWars1vs1.CustomWars1vs1;
import eu.epicpvp.karcade.Game.Multi.Games.SkyWars1vs1.SkyWars1vs1;
import eu.epicpvp.karcade.Game.Multi.Games.SurvivalGames1vs1.SurvivalGames1vs1;
import eu.epicpvp.karcade.Game.Multi.Games.Versus.Versus;
import eu.epicpvp.karcade.Service.Games.ServiceBedWars1vs1;
import eu.epicpvp.kcore.Addons.AddonDay;
import eu.epicpvp.kcore.Addons.AddonSun;
import eu.epicpvp.kcore.Arena.ArenaType;
import eu.epicpvp.kcore.Enum.PlayerState;
import eu.epicpvp.kcore.Enum.Team;
import eu.epicpvp.kcore.Events.ServerStatusUpdateEvent;
import eu.epicpvp.kcore.Inventory.InventoryPageBase;
import eu.epicpvp.kcore.Inventory.Item.Click;
import eu.epicpvp.kcore.Inventory.Item.Buttons.ButtonBase;
import eu.epicpvp.kcore.Kit.Shop.MultiKitShop;
import eu.epicpvp.kcore.Packets.PacketArenaSettings;
import eu.epicpvp.kcore.Packets.PacketArenaStatus;
import eu.epicpvp.kcore.Packets.PacketArenaWinner;
import eu.epicpvp.kcore.StatsManager.Event.PlayerStatsLoadedEvent;
import eu.epicpvp.kcore.Update.UpdateType;
import eu.epicpvp.kcore.Update.Event.UpdateEvent;
import eu.epicpvp.kcore.Util.InventorySize;
import eu.epicpvp.kcore.Util.TabTitle;
import eu.epicpvp.kcore.Util.UtilDebug;
import eu.epicpvp.kcore.Util.UtilEvent.ActionType;
import eu.epicpvp.kcore.Util.UtilException;
import eu.epicpvp.kcore.Util.UtilInv;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.kcore.Util.UtilMath;
import eu.epicpvp.kcore.Util.UtilPlayer;
import eu.epicpvp.kcore.Util.UtilServer;
import eu.epicpvp.kcore.Versus.PlayerKitManager;
import lombok.Getter;
import lombok.Setter;

public class MultiGames extends Game{
	
	@Getter
	private HashMap<String,MultiGame> games = new HashMap<>(); // Auflistung aller MutltiGame Arenas
	@Getter
	@Setter
	private MultiWorldData worldData;
	@Getter
	private HashMap<String,PacketArenaSettings> warte_liste = new HashMap<>(); //Warte Liste falls Spieler zu fr§h auf dem Server kommen...
	@Getter
	@Setter
	private boolean CreatureSpawn = true; //Creature Spawn GLOBAL
	@Getter
	private PlayerKitManager kitManager;
	@Getter
	@Setter
	private int played_games=0;
	@Getter
	@Setter
	private MultiKitShop kitshop;
	@Getter
	private Team[] spielerTeams;
	@Getter
	private InventoryPageBase page;
	
	public MultiGames(kArcadeManager manager,String type){
		super(manager);
		setTyp(GameType.get(type));
		this.spielerTeams=ArenaType._TEAMx6.getTeam();
		UtilServer.createLagListener(manager.getCmd());
		setState(GameState.LobbyPhase);
		setSet_default_scoreboard(false);
		setWorldData(new MultiWorldData(getManager(), getType()));
		createGames(getType());
		setApublic(false);
		new MultiAddonChat(manager.getInstance());
		manager.getCmd().register(CommandSpectate.class, new CommandSpectate(manager));
		
		PacketArenaSettings.register();
		PacketArenaStatus.register();
		PacketArenaWinner.register();
		getManager().getClient().getHandle().getHandlerBoss().addListener(new PacketListener() {
			
			@Override
			public void handle(Packet packet) {
				if(packet instanceof PacketArenaSettings){
					Bukkit.getScheduler().runTask(getManager().getInstance(), new Runnable(){

						@Override
						public void run() {
							final PacketArenaSettings settings = (PacketArenaSettings)packet;
							final MultiGame g = games.get(settings.getArena());

							System.out.println("PACKET: "+settings.getPlayer()+"("+UtilPlayer.isOnline(settings.getPlayer())+") to "+settings.getArena());
							g.setSettings(settings);
						}
						
					});
				}
			}
		});
	}
	
	@EventHandler
	public void MulistatusUpdate(ServerStatusUpdateEvent ev){
		ev.getPacket().setListed(false);
	}
	
	public void createAdminPage(){
		page=new InventoryPageBase(InventorySize._18, "§cTeam Interface");
		
		for(MultiGame game : getGames().values()){
			game.setButton(new ButtonBase(new Click(){

				@Override
				public void onClick(Player p, ActionType a, Object o) {
					p.setGameMode(GameMode.SPECTATOR);
					p.teleport(game.getPasteLocation().clone().add(0, 10, 0));
					for(Player player : game.getGameList().getPlayers().keySet())p.showPlayer(player);
				}
				
			}, UtilItem.Item(new ItemStack(Material.COMMAND,1), new String[]{""}, "§a"+getType().name()+" §7[§e"+(game.getArena().replaceAll("arena", ""))+"§7]")));
			page.addButton(game.getButton());
		}
		
		UtilInv.getBase().addPage(page);
	}
	
	public boolean haveToRestart(){
		return (getPlayed_games() > 50);
	}
	
	public void updatePlayedGames(){
		setPlayed_games(getPlayed_games()+1);
	}
	
	public void createGames(GameType type){
		setPacketServer("versushub");
		new MultiAddonArenaRestore(getManager().getInstance());
		setCreatureSpawn(false);

		long mem=0;
		if(GameType.Versus==type){
			this.kitManager=new PlayerKitManager(getManager().getMysql(), GameType.Versus);
			this.kitManager.setAsync(true);
			getWorldData().createCustomWorld("90,quartz_block","void");
			ArrayList<File> zips = getWorldData().loadZips();
			Location loc = new Location(getWorldData().getWorld(),0,120,0);
			
			Versus v;
			File file;
			int size = zips.size();
			for(int i = 0; i<(size<=8?size:8); i++){
				if(!zips.isEmpty()){
					UtilServer.getLagMeter().unloadChunks(null,null);
					UtilServer.getLagMeter().entitiesClearAll();
					mem = Runtime.getRuntime().freeMemory();
					if(zips.size()==1){
						file = zips.get(0);
					}else{
						file = zips.get(UtilMath.r(zips.size()));
					}
					v=new Versus(this, file ,loc);
					games.put(v.getArena(), v);
					loc=loc.add(0, 0, 5000);
					zips.remove(file);
					getManager().DebugLog("Mem: "+(Runtime.getRuntime().freeMemory()-mem)/1048576L);
				}else{
					break;
				}
			}
			
			loc=null;
			size=0;
			v=null;
			zips.clear();
			zips=null;
		}else if(GameType.BedWars1vs1==type){
			ServiceBedWars1vs1.setMultiGames(this);
			UtilServer.getLagMeter().getEntitiesBlackList().add(EntityType.VILLAGER);
			getWorldData().createCleanWorld();
			ArrayList<File> zips = getWorldData().loadZips();
			Location loc = new Location(getWorldData().getWorld(),0,90,0);
			
			long time;
			CustomWars1vs1 v;
			File file;
			int size = zips.size();
			for(int i = 0; i<(size<=6?size:6); i++){
				if(!zips.isEmpty()){
					UtilServer.getLagMeter().unloadChunks(null,null);
					UtilServer.getLagMeter().entitiesClearAll();
					mem = Runtime.getRuntime().freeMemory();
					time=System.currentTimeMillis();
					if(zips.size()==1){
						file = zips.get(0);
					}else{
						file = zips.get(UtilMath.r(zips.size()));
					}
					loc=loc.add(0, 0, 5000);
					v=new CustomWars1vs1(this, "Loading ...",loc,file);
					games.put(v.getArena(), v);
					zips.remove(file);
					getManager().DebugLog(time,"PASTE - "+v.getArena() ,MultiGames.class.getName());
					getManager().DebugLog("Mem: "+(Runtime.getRuntime().freeMemory()-mem)/1048576L);
				}else{
					break;
				}
			}
			
			loc=null;
			time=0;
			v=null;
			zips.clear();
			zips=null;
		}else if(GameType.SkyWars1vs1==type){
			getWorldData().createCleanWorld();
			ArrayList<File> zips = getWorldData().loadZips();
			Location loc = new Location(getWorldData().getWorld(),0,90,0);
			
			long time;
			SkyWars1vs1 v;
			File file;
			int size = zips.size();
			for(int i = 0; i<(size<=6?size:6); i++){
				if(!zips.isEmpty()){
					UtilServer.getLagMeter().unloadChunks(null,null);
					UtilServer.getLagMeter().entitiesClearAll();
					mem = Runtime.getRuntime().freeMemory();
					time=System.currentTimeMillis();
					if(zips.size()==1){
						file = zips.get(0);
					}else{
						file = zips.get(UtilMath.r(zips.size()));
					}
					loc=loc.add(0, 0, 5000);
					v=new SkyWars1vs1(this, "Loading ...",loc,file);
					games.put(v.getArena(), v);
					zips.remove(file);
					getManager().DebugLog(time,"PASTE - "+v.getArena() ,MultiGames.class.getName());
					getManager().DebugLog("Mem: "+(Runtime.getRuntime().freeMemory()-mem)/1048576L);
				}else{
					break;
				}
			}
			
			loc=null;
			time=0;
			v=null;
			zips.clear();
			zips=null;
		}else if(GameType.SurvivalGames1vs1==type){
			getWorldData().createCleanWorld();
			ArrayList<File> zips = getWorldData().loadZips();
			Location loc = new Location(getWorldData().getWorld(),0,90,0);
			
			long time;
			SurvivalGames1vs1 v;
			File file;
			int size = zips.size();
			for(int i = 0; i<(size<=4?size:4); i++){
				if(!zips.isEmpty()){
					UtilServer.getLagMeter().unloadChunks(null,null);
					UtilServer.getLagMeter().entitiesClearAll();
					mem = Runtime.getRuntime().freeMemory();
					time=System.currentTimeMillis();
					if(zips.size()==1){
						file = zips.get(0);
					}else{
						file = zips.get(UtilMath.r(zips.size()));
					}
					loc=loc.add(0, 0, 5000);
					v=new SurvivalGames1vs1(this, "Loading ...",loc,file);
					games.put(v.getArena(), v);
					zips.remove(file);
					getManager().DebugLog(time,"PASTE - "+v.getArena() ,MultiGames.class.getName());
					getManager().DebugLog("Mem: "+(Runtime.getRuntime().freeMemory()-mem)/1048576L);
				}else{
					break;
				}
			}
			
			loc=null;
			time=0;
			v=null;
			zips.clear();
			zips=null;
		}

		for(org.bukkit.entity.Entity e : getWorldData().getWorld().getEntities())e.remove();
		for(MultiGame game : games.values()){
			game.setState(GameState.LobbyPhase,false);
		}

		new AddonDay(getManager().getInstance(), getWorldData().getWorld());
		new AddonSun(getManager().getInstance(), getWorldData().getWorld());
		setState(GameState.LobbyPhase);
		createAdminPage();
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
	public void updateInfoEv(UpdateEvent ev){
		if(ev.getType()==UpdateType.SLOW){
			updateInfo();
			if(haveToRestart()&&isState(GameState.Restart)){
				if(UtilServer.getPlayers().isEmpty()){
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
				}
			}
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
	 * Z§hlt die laufenden Spiele und sendet dies weiter zu den Daten Server
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
	public void MultiGameUpdateInfo(MultiGameUpdateInfoEvent ev){
		if(!getManager().getClient().getHandle().isConnected()){
			logMessage("Cancel Info Packet from "+ev.getPacket().getArena()+" client is not connected!");
			ev.setCancelled(true);
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
	    if (!isCreatureSpawn()&&ev.getSpawnReason()!=SpawnReason.CUSTOM){
			if(getManager().getService().isDebug())System.err.println("[MultiGame] CreatureSpawnEvent GLOBAL Cancelled TRUE!");
	    	ev.setCancelled(true);
	    }
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
		if(getKitManager() != null && getKitManager().getKits().containsKey(UtilPlayer.getPlayerId(ev.getPlayer()))){
			getKitManager().getKits().remove(UtilPlayer.getPlayerId(ev.getPlayer()));
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
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void joinM(PlayerJoinEvent ev){
		getManager().Clear(ev.getPlayer());
		for(Player p : UtilServer.getPlayers()){
			p.hidePlayer(ev.getPlayer());
			ev.getPlayer().hidePlayer(p);
		}
		TabTitle.setHeaderAndFooter(ev.getPlayer(), "§eCLASHMC §7-§e "+getType().getTyp(), "§eShop.ClashMC.eu");
//		join.add(ev.getPlayer().getName());
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void Joina(PlayerStatsLoadedEvent ev){
		if(ev.getManager().getType() != GameType.Money)return;
		if(UtilPlayer.isOnline(ev.getPlayerId())){
			Player player = UtilPlayer.searchExact(ev.getPlayerId());
			
			Bukkit.getScheduler().runTask(getManager().getInstance(), new Runnable() {
				
				@Override
				public void run() {
					try{
						MultiGamePlayerJoinEvent event = null;
						System.out.println("FIRST: "+player.getName()+" "+ev.getManager().getType().name());
						if(warte_liste.containsKey(player.getName())){
							PacketArenaSettings settings = (PacketArenaSettings)warte_liste.get(player.getName());
							System.out.println("FIRST1: "+player.getName()+" "+ev.getManager().getType().name()+" "+settings.getArena());
							MultiGame g = games.get(settings.getArena());
							
							if(g instanceof Versus){
								System.out.println("FIRST2: "+player.getName()+" "+ev.getManager().getType().name());
								if(((Versus)g).getMax_type().getTeam().length>=settings.getType().getTeam().length&&settings.getArena().equalsIgnoreCase(g.getArena())&& (g.getState() == GameState.LobbyPhase||g.getState() == GameState.Laden) ){

									System.out.println("FIRST3: "+player.getName()+" "+ev.getManager().getType().name());
									if(warte_liste.containsKey(settings.getPlayer())){
										warte_liste.remove(settings.getPlayer());
									}

									((MultiTeamGame)g).getTeamList().put(Bukkit.getPlayer(settings.getPlayer()), settings.getTeam());
									g.getGameList().addPlayer(Bukkit.getPlayer(settings.getPlayer()), PlayerState.IN);
									event=new MultiGamePlayerJoinEvent(Bukkit.getPlayer(settings.getPlayer()),g);
									Bukkit.getPluginManager().callEvent(event);
									System.out.println("CALL: "+player.getName()+" "+(event==null));
								}
							}else{
								if(settings.getArena().equalsIgnoreCase(g.getArena())&& (g.getState() == GameState.LobbyPhase||g.getState() == GameState.Laden) ){
									if(warte_liste.containsKey(settings.getPlayer())){
										warte_liste.remove(settings.getPlayer());
									}
										
									((MultiTeamGame)g).getTeamList().put(Bukkit.getPlayer(settings.getPlayer()), settings.getTeam());
									g.getGameList().addPlayer(Bukkit.getPlayer(settings.getPlayer()), PlayerState.IN);
									event=new MultiGamePlayerJoinEvent(Bukkit.getPlayer(settings.getPlayer()),g);
									Bukkit.getPluginManager().callEvent(event);
									System.out.println("CALL: "+player.getName());
								}
							}
						}

						//Spieler ist noch keiner Arena zugewiesen deswegen auf die Warte Liste
						if(event == null || !event.isCancelled()){
							//Spieler wird in die Lobby zum warten Teleportiert!
							System.out.println("WARTELISTE: "+player.getName());
							getManager().getLobby().getWorld().setStorm(false);
							getManager().getLobby().getWorld().setTime(4000);
							player.teleport(getManager().getLobby());
						}
					}catch(Exception e){
						UtilDebug.debug("Joina", e.getMessage());
						UtilException.catchException(e, "multigame"+getManager().getInstance().getConfig().getString("Config.Server.ID"), Bukkit.getIp(), getManager().getMysql());
					}
				}
			});
		}
	}
}
