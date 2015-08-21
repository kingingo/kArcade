package me.kingingo.karcade.Game.Multi;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;
import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Enum.PlayerState;
import me.kingingo.karcade.Game.Game;
import me.kingingo.karcade.Game.Events.GameUpdateInfoEvent;
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
import me.kingingo.kcore.Util.TabTitle;
import me.kingingo.kcore.Util.UtilInv;
import me.kingingo.kcore.Util.UtilPlayer;
import me.kingingo.kcore.Util.UtilWorldEdit;
import me.kingingo.kcore.Versus.VersusKit;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
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
	private ArrayList<MultiGame> games = new ArrayList<>(); // Auflistung aller MutltiGame Arenas
	@Getter
	private HashMap<MultiGame,HashMap<Team,ArrayList<Location>>> locs = new HashMap<>(); //Alle Team Locations der einzelnen MultiGame Arenas
	@Getter
	@Setter
	private WorldData worldData;
	private HashMap<String,VERSUS_SETTINGS> warte_liste = new HashMap<>(); //Warte Liste falls Spieler zu früh auf dem Server kommen...
	@Getter
	@Setter
	private boolean CreatureSpawn = true; //Creature Spawn GLOBAL
	
	public MultiGames(kArcadeManager manager,String type){
		super(manager);
		registerListener();
		ServiceMultiGames.setGames(this);
		setTyp(GameType.valueOf(type));
		setWorldData(new WorldData(getManager(), getType()));
		createGames(getType());
	}
	
	public void createGames(GameType type){
		if(GameType.Versus==type){
			setCreatureSpawn(false);
			getWorldData().createCleanWorld();
			for(org.bukkit.entity.Entity e : getWorldData().getWorld().getEntities())e.remove();
			File[] schematics = getWorldData().loadSchematicFiles();
			Location loc = new Location(getWorldData().getWorld(),0,90,0);
			
			for(File file : schematics){
				UtilWorldEdit.pastePlate(loc, file);
				games.add(new Versus(this, file.getName().replaceAll(".schematic", "") ,loc));
				loc=loc.add(0, 0, 100);
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
		TabTitle.setHeaderAndFooter(ev.getPlayer(), "§eEPICPVP §7-§e "+getType().getTyp(), "§eShop.EpicPvP.de");
		if(warte_liste.containsKey(ev.getPlayer().getName())){
			VERSUS_SETTINGS settings = (VERSUS_SETTINGS)warte_liste.get(ev.getPlayer().getName());
			for(MultiGame g : games){
				if(g instanceof Versus){
					if(((Versus)g).getType()==settings.getType()&&settings.getArena().equalsIgnoreCase(g.getArena())&&g.getState() == GameState.LobbyPhase){
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
									try {
										g.setKit( new VersusKit().fromItemArray( UtilInv.itemStackArrayFromBase64( getStats().getString(Stats.KIT, Bukkit.getPlayer(settings.getPlayer())) ) ) );
									} catch (IOException e) {
										e.printStackTrace();
									}
									g.getKit().name=settings.getKit();
								}
								
								g.setMin_team(settings.getMin_team());
								g.setMax_team(settings.getMax_team());
								
								event=new MultiGamePlayerJoinEvent(Bukkit.getPlayer(settings.getPlayer()),g);
								Bukkit.getPluginManager().callEvent(event);
							}
						break;
					}
				}
			}
		}
		
		//Spieler ist noch keiner Arena zugewiesen deswegen auf die Warte Liste
		if(event==null||!event.isCancelled()){
			//Spieler wird in die Lobby zum warten Teleportiert!
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

			for(MultiGame g : games){
				if(g instanceof Versus){
					if(((Versus)g).getType()==settings.getType()&&settings.getArena().equalsIgnoreCase(g.getArena())&&g.getState() == GameState.LobbyPhase){
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
									try {
										g.setKit( new VersusKit().fromItemArray( UtilInv.itemStackArrayFromBase64( getStats().getString(Stats.KIT, Bukkit.getPlayer(settings.getPlayer())) ) ) );
									} catch (IOException e) {
										e.printStackTrace();
									}
									g.getKit().name=settings.getKit();
								}
								
								g.setMin_team(settings.getMin_team());
								g.setMax_team(settings.getMax_team());
//								g.setState(GameState.Laden);
								
								event=new MultiGamePlayerJoinEvent(Bukkit.getPlayer(settings.getPlayer()),g);
								Bukkit.getPluginManager().callEvent(event);
							}else{
								warte_liste.put(settings.getPlayer(),settings);
							}
						break;
					}
				}
			}
		}
	}
	
}
