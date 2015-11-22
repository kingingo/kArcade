package me.kingingo.karcade.Game.Single.Games.TroubleInMinecraft;

import java.util.ArrayList;
import java.util.HashMap;

import lombok.Getter;
import me.kingingo.karcade.kArcade;
import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Events.RankingEvent;
import me.kingingo.karcade.Events.WorldLoadEvent;
import me.kingingo.karcade.Game.Single.SingleWorldData;
import me.kingingo.karcade.Game.Single.Games.TeamGame;
import me.kingingo.karcade.Game.Single.Games.TroubleInMinecraft.Command.CommandDetective;
import me.kingingo.karcade.Game.Single.Games.TroubleInMinecraft.Command.CommandTraitor;
import me.kingingo.karcade.Game.Single.Games.TroubleInMinecraft.Command.CommandTraitorChat;
import me.kingingo.karcade.Game.Single.Games.TroubleInMinecraft.Shop.IShop;
import me.kingingo.karcade.Game.Single.Games.TroubleInMinecraft.Shop.Shop;
import me.kingingo.karcade.Game.Single.Games.TroubleInMinecraft.Shop.Item.CreeperSpawner;
import me.kingingo.karcade.Game.Single.Games.TroubleInMinecraft.Shop.Item.DNA_TEST;
import me.kingingo.karcade.Game.Single.Games.TroubleInMinecraft.Shop.Item.Defibrillator;
import me.kingingo.karcade.Game.Single.Games.TroubleInMinecraft.Shop.Item.Fake_Chest;
import me.kingingo.karcade.Game.Single.Games.TroubleInMinecraft.Shop.Item.Golden_Weapon;
import me.kingingo.karcade.Game.Single.Games.TroubleInMinecraft.Shop.Item.Healing_Station;
import me.kingingo.karcade.Game.Single.Games.TroubleInMinecraft.Shop.Item.Knife;
import me.kingingo.karcade.Game.Single.Games.TroubleInMinecraft.Shop.Item.Medipack;
import me.kingingo.karcade.Game.Single.Games.TroubleInMinecraft.Shop.Item.Radar;
import me.kingingo.karcade.Game.Single.Games.TroubleInMinecraft.Shop.Item.Tester_Spoofer;
import me.kingingo.karcade.Game.Single.Games.TroubleInMinecraft.Weapon.Minigun;
import me.kingingo.karcade.Game.Single.Games.TroubleInMinecraft.Weapon.Shotgun;
import me.kingingo.karcade.Game.Single.Games.TroubleInMinecraft.Weapon.Sniper;
import me.kingingo.karcade.Service.Games.ServiceTroubleInMinecraft;
import me.kingingo.kcore.Addons.AddonDay;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.GameStateChangeReason;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.Enum.PlayerState;
import me.kingingo.kcore.Enum.Team;
import me.kingingo.kcore.Game.Events.GameStartEvent;
import me.kingingo.kcore.Game.Events.GameStateChangeEvent;
import me.kingingo.kcore.ItemFake.ItemFake;
import me.kingingo.kcore.ItemFake.ItemFakeManager;
import me.kingingo.kcore.ItemFake.Events.ItemFakePickupEvent;
import me.kingingo.kcore.Language.Language;
import me.kingingo.kcore.LaunchItem.LaunchItemManager;
import me.kingingo.kcore.NPC.NPC;
import me.kingingo.kcore.NPC.NPCManager;
import me.kingingo.kcore.NPC.Event.PlayerInteractNPCEvent;
import me.kingingo.kcore.Permission.kPermission;
import me.kingingo.kcore.StatsManager.Stats;
import me.kingingo.kcore.StatsManager.Event.PlayerStatsChangeEvent;
import me.kingingo.kcore.StatsManager.Event.PlayerStatsCreateEvent;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.Color;
import me.kingingo.kcore.Util.UtilDisplay;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilMath;
import me.kingingo.kcore.Util.UtilPlayer;
import me.kingingo.kcore.Util.UtilScoreboard;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.kcore.Util.UtilString;
import me.kingingo.kcore.Util.UtilTime;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

public class TroubleInMinecraft extends TeamGame{
	
	@Getter
	private HashMap<Integer,String> npclist = new HashMap<>();
	private ArrayList<Player> arrow =new ArrayList<>();
	private Tester tester;
	private Shop dshop;
	@Getter
	private NPCManager npcManager;
	@Getter
	private ItemFakeManager ifm;
	@Getter
	public static Shotgun shotgun;
	@Getter
	public static Sniper sniper;
	@Getter
	public static Minigun minigun;
	ArrayList<Player> traitor = new ArrayList<>();
	@Getter
	private MagnetStab magnet;
	private Defibrillator defi;
	private Shop traitor_shop;
	private Shop detective_shop;
	@Getter
	private LaunchItemManager ilManager;
	
	public TroubleInMinecraft(kArcadeManager manager) {
		super(manager);
		registerListener();
		long t = System.currentTimeMillis();
		setTyp(GameType.TroubleInMinecraft);
		setState(GameState.Laden);
		setMin_Players(6);
		setMax_Players(16);
		getInventoryTypDisallow().add(InventoryType.ANVIL);
		getInventoryTypDisallow().add(InventoryType.BREWING);
		getInventoryTypDisallow().add(InventoryType.DISPENSER);
		getInventoryTypDisallow().add(InventoryType.BEACON);
		getInventoryTypDisallow().add(InventoryType.DROPPER);
		getInventoryTypDisallow().add(InventoryType.WORKBENCH);
		setDamageTeamSelf(true);
		setDamageSelf(true);
		setItemDrop(true);
		setItemPickup(false);
		setBlockPlace(false);
		setBlockBreak(false);
		setCreatureSpawn(false);
		setHangingBreak(false);
		setBlockSpread(false);
		setExplosion(true);
		setDeathDropItems(true);
		setFoodChange(false);
		setCompassAddon(true);
		setRespawn(true);
		shotgun=new Shotgun(this);
		sniper=new Sniper(this);
		minigun=new Minigun(this);
		getManager().getCmd().register(CommandTraitorChat.class, new CommandTraitorChat(this));
		getManager().getCmd().register(CommandDetective.class, new CommandDetective(this));
		getManager().getCmd().register(CommandTraitor.class, new CommandTraitor(this));
		npcManager= new NPCManager(getManager().getInstance());
		magnet=new MagnetStab(npcManager);
		Radar r = new Radar(this);
		ilManager=new LaunchItemManager(getManager().getInstance());
		defi = new Defibrillator(this);
		traitor_shop= new Shop(this,UtilItem.RenameItem(new ItemStack(Material.BOW),"§cTraitorShop"),"Traitor-Shop:",Stats.TTT_TRAITOR_PUNKTE,Team.TRAITOR,new IShop[]{
				new Medipack(this),
				new Fake_Chest(this),
				new Knife(this),
				r,
				new Tester_Spoofer(this),
				new CreeperSpawner(this),
		});
		
		detective_shop= new Shop(this,UtilItem.RenameItem(new ItemStack(Material.BOW),"§1DetectiveShop"),"Detective-Shop:",Stats.TTT_DETECTIVE_PUNKTE,Team.DETECTIVE,new IShop[]{
				new Golden_Weapon(this),
				r,
				//defi,
				new Healing_Station(this),
				new Medipack(this),
				new DNA_TEST(this),
		});
		setWorldData(new SingleWorldData(manager,getType()));
		getWorldData().Initialize();
		ServiceTroubleInMinecraft.setTtt(this);
		setState(GameState.LobbyPhase);
		manager.DebugLog(t, this.getClass().getName());
	}

//	Ein Innocent tötet einen Traitor: +20 Karma
//	Ein Innocent tötet einen Innocent: -20 Karma
//	Ein Innocent tötet einen Detective: -50 Karma
//	Ein Detective tötet einen Traitor: +30 Karma und 2 Detective-Punkte
//	Ein Detective tötet einen Innocent: -20 Karma
//	Ein Detective tötet einen Detective: -50 Karma
//	Ein Traitor tötet einen Innocent: +10 Karma und 2 Traitor-Punkte
//	Ein Traitor tötet einen Detective: +20 Karma und 4 Traitor-Punkte
//	Ein Traitor tötet einen Traitor: -50 Karma
	
//	Shops:
	
//	Traitor-Shop:
//	Medipack (1 Punkt): Heil 2,5 Herzen
//	Radar (1 Punkt): Zeigt auf einen ausgewählten Spieler.
//	Creeper Arrows (2 Punkte): Aus geschossenen Pfeilen spawnen Creeper
//	Disguiser (3 Punkte): Versteckt den eigenen Name
//	Tester-Spoofer (4 Punkte): Traitoren werden zu 75% im Tester also Innocent angezeigt.
//	Fake-Chest (4 Punkte): Wenn man dieses Fake-Item aufnimmt stirbt man sofort.
//	Teleporter (4 Punkte): Ermöglicht die einmalige teleportation zu einem vorher festgelegten Punkt.
//	C4 (5 Punkte): Ein TNT-Block der mit einer Redstone-Fackel aktiviert werden kann.
//	Knife (6 Punkte): Ermöglicht einmalig das sofortige töten eines Spielers von hinten.
//	JihadBomb (7 Punkte): Lässt alle Spieler um einen selbst explodieren.
//	FireGrenade (7 Punkte): Erzeugt ein großes Feuer, nachdem sie explodiert ist.
//	SmokeGrenade (7 Punkte): Erzeugt eine große Rauchwolke nach der Explosion.
	
//	Detective-Shop:
//	Radar (1 Punkt): Zeigt auf einen ausgewählten Spieler.
//	DNA-Test (2 Punkte): Untersucht Leichen auf genauere Informationen.
//	Healing-Station (2 Punkte): Heilt andere Spieler in der nähe der Heal-Station.
//	Fluffy (3 Punkte): Ein Hund, der in der Nähe von Traitoren knurrt.
//	Golden Weapon (5 Punkte): Ein Goldschwert, welches einmalig einen Traitor mit einem Schlag tötet. Bei Innocents zerbricht es sofort.
	
	//RED BLOCK = PLAYER SPAWN
	//BLUE = BUTTON
	//GREEN = JOIN
	//MELON = LAMPEN
	//GRAY = GLASS
	//YELLOW = ITEM'S
	
	@EventHandler
	public void Chest(PlayerInteractEvent ev){
		if(UtilEvent.isAction(ev, ActionType.R_BLOCK)&&ev.getClickedBlock().getType()==Material.CHEST)ev.setCancelled(true);
	}
	
//	@EventHandler
//	public void Chat(AsyncPlayerChatEvent ev){
//		ev.setCancelled(true);
//		if(getState()!=GameState.LobbyPhase&&getGameList().getPlayers(PlayerState.OUT).contains(ev.getPlayer())){
//			ev.setCancelled(true);
//			UtilPlayer.sendMessage(ev.getPlayer(),Language.getText(p, "PREFIX_GAME",getType().getTyp())+Text.SPECTATOR_CHAT_CANCEL.getText());
//			return;
//		}
//		UtilServer.broadcast(C.cGray+ev.getPlayer().getDisplayName()+": "+ev.getMessage());
//	}
	
	@EventHandler
	public void Inv(InventoryClickEvent ev){
		if (!(ev.getWhoClicked() instanceof Player)|| ev.getInventory() == null || ev.getCursor() == null || ev.getCurrentItem() == null)return;
		Player p = (Player)ev.getWhoClicked();
		if(ev.getClickedInventory().getType()==InventoryType.PLAYER){
			if(ev.getCurrentItem().getType()==Material.BOW){
				ev.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void Chat(AsyncPlayerChatEvent ev){
		ev.setCancelled(true);
		
		if((!ev.getPlayer().hasPermission(kPermission.CHAT_LINK.getPermissionToString()))&&UtilString.isBadWord(ev.getMessage())||UtilString.checkForIP(ev.getMessage())){
			ev.setMessage("Ich heul rum!");
			ev.getPlayer().sendMessage(Language.getText(ev.getPlayer(), "PREFIX")+Language.getText(ev.getPlayer(), "CHAT_MESSAGE_BLOCK"));
		}
		
		if(getState()!=GameState.InGame){
			UtilServer.broadcast(getManager().getPermManager().getPrefix(ev.getPlayer())+ev.getPlayer().getDisplayName()+":§7 "+ev.getMessage());
		}else{
				if(getGameList().isPlayerState(ev.getPlayer())==PlayerState.IN){
					Team t = getTeam(ev.getPlayer());
					if(t==null){
						UtilServer.broadcast(getManager().getPermManager().getPrefix(ev.getPlayer())+ev.getPlayer().getDisplayName()+":§7 "+ev.getMessage());
					}else{
						for(Player p : getGameList().getPlayers(PlayerState.IN)){
							if(t==Team.TRAITOR){
								if(getTeam(p)==t){
									p.sendMessage(t.getColor()+t.Name()+" §8| "+t.getColor()+ev.getPlayer().getDisplayName()+":§7 "+ev.getMessage());
								}else{
									p.sendMessage(Team.INOCCENT.getColor()+Team.INOCCENT.Name()+" §8| "+Team.INOCCENT.getColor()+ev.getPlayer().getDisplayName()+":§7 "+ev.getMessage());
								}
							}else{
								p.sendMessage(t.getColor()+t.Name()+" §8| "+t.getColor()+ev.getPlayer().getDisplayName()+":§7 "+ev.getMessage());
							}
						}
					}
				}else{
					for(Player p : getGameList().getPlayers(PlayerState.OUT)){
						p.sendMessage(Color.ORANGE+ev.getPlayer().getDisplayName()+":§7 "+ev.getMessage());
					}
				}
		}
	}
	
	public Team getHaveWinTeam(){
		Team t = Team.INOCCENT;
        for(Player p : getTeamList().keySet()){
        	if(getTeamList().get(p)==Team.TRAITOR){
        		t=Team.TRAITOR;
        		break;
        	}
        }
		return t;
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void Aufdecken(PlayerInteractNPCEvent ev){
		 	if(getGameList().isPlayerState(ev.getPlayer())==PlayerState.IN){
		 		NPC npc = ev.getNpc();
				if(npclist.containsKey(npc.getEntityID())){
					String name = npclist.get(npc.getEntityID());
					Location loc = npc.getLocation();
					npclist.remove(npc.getEntityID());
					npc.despawn();
					Team t = defi.getTeams().get(name.toLowerCase());
					
					if(t!=null)broadcastWithPrefix("TTT_LEICHE_IDENTIFIZIERT", new String[]{t.getColor()+name,t.getColor()+t.Name()});
//					if(t==Team.TRAITOR){
//						for(Player p : getGameList().getPlayers(PlayerState.IN)){
//							if(getTeam(p)==t){
//								UtilPlayer.setTab(Team.TRAITOR.getColor()+"[T] "+name.getName(), p, false);
//							}else{
//								UtilPlayer.setTab(Team.INOCCENT.getColor()+"[I] "+name.getName(), p, false);
//							}
//						}
//					}else if(t==Team.DETECTIVE){
//						for(Player p : getGameList().getPlayers(PlayerState.IN))UtilPlayer.setTab(Team.DETECTIVE.getColor()+"[D] "+name.getName(), p, false);
//					}else if(t==Team.INOCCENT){
//						for(Player p : getGameList().getPlayers(PlayerState.IN))UtilPlayer.setTab(Team.INOCCENT.getColor()+"[I] "+name.getName(), p, false);
//					}
					npc = npcManager.createNPC( name , loc);
					npc.spawn();
					//npc.sleep();
					if(getTeam(ev.getPlayer())==Team.DETECTIVE){
						getStats().setInt(ev.getPlayer(),getStats().getInt(Stats.TTT_DETECTIVE_PUNKTE, ev.getPlayer())+1, Stats.TTT_DETECTIVE_PUNKTE);	
					}
//					else if(getTeam(ev.getPlayer())==Team.TRAITOR){
//						getStats().setInt(ev.getPlayer(),getStats().getInt(Stats.TTT_TRAITOR_PUNKTE, ev.getPlayer())+1, Stats.TTT_TRAITOR_PUNKTE);	
//					}
				}else{
					if(defi.getTeams().containsKey(npc.getName().toLowerCase())){
						UtilPlayer.sendMessage(ev.getPlayer(),Language.getText(ev.getPlayer(), "PREFIX_GAME",getType().getTyp())+Language.getText(ev.getPlayer(), "TTT_NPC_CLICKED", new String[]{npc.getName(),defi.getTeams().get(npc.getName().toLowerCase()).getColor()+defi.getTeams().get(npc.getName().toLowerCase()).name()}));
					}
				}
		 	}
		}
	
	@EventHandler
	public void DeathTTT(PlayerDeathEvent ev){
		if(ev.getEntity() instanceof Player&&getGameList().getPlayers(PlayerState.IN).contains( ((Player)ev.getEntity()) )){
			getStats().setInt(((Player)ev.getEntity()),getStats().getInt(Stats.DEATHS, ((Player)ev.getEntity()))+1, Stats.DEATHS);
			getStats().setInt(((Player)ev.getEntity()),getStats().getInt(Stats.LOSE, ((Player)ev.getEntity()))+1, Stats.LOSE);
			getGameList().addPlayer( ((Player)ev.getEntity()) , PlayerState.OUT);
			boolean b = false;
			for(ItemStack item : ev.getDrops()){
				for(TTT_Item ii : TTT_Item.values()){
					if(UtilItem.ItemNameEquals(ii.getItem(), item)){
						b=true;
						break;
					}
				}
				if(b)new ItemFake(ev.getEntity().getLocation().add(UtilMath.RandomDouble(-0.4, 0.4),0.2,UtilMath.RandomDouble(-0.4, 0.4)),item);
			}
			ev.getDrops().clear();
			Team t = getTeam(((Player)ev.getEntity()));
			NPC npc = npcManager.createNPC( "Unidentifiziert" , ((Player)ev.getEntity()).getLocation());
			npc.spawn();
			//npc.sleep();
//			if(t==Team.TRAITOR){
//				for(Player p : getGameList().getPlayers(PlayerState.IN)){
//					if(getTeam(p)==t){
//						UtilPlayer.setTab(Team.TRAITOR.getColor()+"[T] "+((Player)ev.getEntity()).getName(), p, true);
//					}else{
//						UtilPlayer.setTab(Team.INOCCENT.getColor()+"[I] "+((Player)ev.getEntity()).getName(), p, true);
//					}
//				}
//			}else if(t==Team.DETECTIVE){
//				for(Player p : getGameList().getPlayers(PlayerState.IN))UtilPlayer.setTab(Team.DETECTIVE.getColor()+"[D] "+((Player)ev.getEntity()).getName(), p, true);
//			}else if(t==Team.INOCCENT){
//				for(Player p : getGameList().getPlayers(PlayerState.IN))UtilPlayer.setTab(Team.INOCCENT.getColor()+"[I] "+((Player)ev.getEntity()).getName(), p, true);
//			}
			npclist.put(npc.getEntityID(), ((Player)ev.getEntity()).getName());
			if(ev.getEntity().getKiller() instanceof Player){
				getStats().setInt(((Player)ev.getEntity().getKiller()),getStats().getInt(Stats.KILLS, ((Player)ev.getEntity().getKiller()))+1, Stats.KILLS);
				
				Team t1 = getTeam( ((Player)ev.getEntity().getKiller()) );
				int k = getStats().getInt(Stats.TTT_KARMA, (Player)ev.getEntity().getKiller());
				int d=-1;
				int tr=-1;
				((Player)ev.getEntity()).sendMessage(Language.getText(((Player)ev.getEntity()), "PREFIX_GAME",getType().getTyp())+Language.getText(((Player)ev.getEntity()), "KILL_BY", new String[]{((Player)ev.getEntity()).getName(),((Player)ev.getEntity().getKiller()).getName()}));
				((Player)ev.getEntity().getKiller()).sendMessage(Language.getText(((Player)ev.getEntity().getKiller()), "PREFIX_GAME",getType().getTyp())+Language.getText(((Player)ev.getEntity().getKiller()), "KILL_BY", new String[]{((Player)ev.getEntity()).getName(),((Player)ev.getEntity().getKiller()).getName()}));
				if(t1==Team.TRAITOR){
					if(t==Team.TRAITOR){
						getStats().setInt( ((Player)ev.getEntity().getKiller()) , k-50, Stats.TTT_KARMA);
						k=-50;
					}else if(t==Team.INOCCENT){
						getCoins().addCoins(((Player)ev.getEntity().getKiller()), false, 5);
						getStats().setInt( ((Player)ev.getEntity().getKiller()) , k+10, Stats.TTT_KARMA);
						getStats().setInt( ((Player)ev.getEntity().getKiller()) ,getStats().getInt(Stats.TTT_TRAITOR_PUNKTE, (Player)ev.getEntity().getKiller())+2, Stats.TTT_TRAITOR_PUNKTE);
						k=10;
						tr=2;
					}else if(t==Team.DETECTIVE){
						getCoins().addCoins(((Player)ev.getEntity().getKiller()), false, 5);
						getStats().setInt( ((Player)ev.getEntity().getKiller()) , k+20, Stats.TTT_KARMA);
						getStats().setInt( ((Player)ev.getEntity().getKiller()) ,getStats().getInt(Stats.TTT_TRAITOR_PUNKTE, (Player)ev.getEntity().getKiller())+4, Stats.TTT_TRAITOR_PUNKTE);
						k=20;
						tr=4;
					}
				}else if(t1==Team.INOCCENT){
					if(t==Team.TRAITOR){
						getCoins().addCoins(((Player)ev.getEntity().getKiller()), false, 5);
						getStats().setInt( ((Player)ev.getEntity().getKiller()) , k+20, Stats.TTT_KARMA);
						k=20;
					}else if(t==Team.INOCCENT){
						getStats().setInt( ((Player)ev.getEntity().getKiller()) , k-20, Stats.TTT_KARMA);
						k=-20;
					}else if(t==Team.DETECTIVE){
						getStats().setInt( ((Player)ev.getEntity().getKiller()) , k-50, Stats.TTT_KARMA);
						k=-50;
					}
				}else if(t1==Team.DETECTIVE){
					if(t==Team.TRAITOR){
						getCoins().addCoins(((Player)ev.getEntity().getKiller()), false, 10);
						getStats().setInt( ((Player)ev.getEntity().getKiller()) , k+30, Stats.TTT_KARMA);
						getStats().setInt( ((Player)ev.getEntity().getKiller()) ,getStats().getInt(Stats.TTT_DETECTIVE_PUNKTE, (Player)ev.getEntity().getKiller())+2, Stats.TTT_DETECTIVE_PUNKTE);
						k=30;
						d=2;
					}else if(t==Team.INOCCENT){
						getStats().setInt( ((Player)ev.getEntity().getKiller()) , k-20, Stats.TTT_KARMA);
						k=-20;
					}else if(t==Team.DETECTIVE){
						getStats().setInt( ((Player)ev.getEntity().getKiller()) , k-50, Stats.TTT_KARMA);
						k=-50;
					}
				}
			
				if(d!=-1){
					if(k>0){
						getManager().getHologram().sendText(ev.getEntity().getKiller(), ev.getEntity().getLocation().add(0,1,0), 3, new String[]{
							"§aDu hast §e"+k+" Karma§a erhalten.",
							"§bDu hast §e"+d+" Detective-Punke§b erhalten."
						});
					}else{
						getManager().getHologram().sendText(ev.getEntity().getKiller(), ev.getEntity().getLocation().add(0,1,0), 3, new String[]{
							 "§cDu hast §e"+k+" Karma§c verloren.",
							 "§bDu hast §e"+d+" Detective-Punke§b erhalten."
						});
					}
				}else if(tr!=-1){
					if(k>0){
						getManager().getHologram().sendText(ev.getEntity().getKiller(), ev.getEntity().getLocation().add(0,1,0), 3, new String[]{
							"§aDu hast §e"+k+" Karma§a erhalten.",
							"§bDu hast §e"+tr+" Traitor-Punke§b erhalten."
						});
					}else{
						getManager().getHologram().sendText(ev.getEntity().getKiller(), ev.getEntity().getLocation().add(0,1,0), 3, new String[]{
							 "§cDu hast §e"+k+" Karma§c verloren.",
							 "§bDu hast §e"+tr+" Traitor-Punke§b erhalten."
						});
					}
				}else{
					if(k>0){
						getManager().getHologram().sendText(ev.getEntity().getKiller(), ev.getEntity().getLocation().add(0,1,0), 3, "§aDu hast §e"+k+" Karma§a erhalten.");
					}else{
						getManager().getHologram().sendText(ev.getEntity().getKiller(), ev.getEntity().getLocation().add(0,1,0), 3, "§cDu hast §e"+k+" Karma§c verloren.");
					}
				}
				
			}
			
			delTeam(((Player)ev.getEntity()));
		}
	}
	
	@EventHandler
	public void DropItemFake(PlayerDropItemEvent ev){
		ev.setCancelled(true);
		for(TTT_Item t : TTT_Item.values()){
			if(UtilItem.ItemNameEquals(t.getItem(), ev.getItemDrop().getItemStack())){
				new ItemFake(ev.getItemDrop());
				ev.setCancelled(false);
				break;
			}
		}
	}
	
	@EventHandler
	public void PickupItemFake(ItemFakePickupEvent ev){
		if(ev.isCancelled())return;
		if(getGameList().isPlayerState(ev.getPlayer())==PlayerState.OUT)return;
		TTT_Item t = getItemFake(ev.getItem());
		if(t==null){
			ev.getItemfake().remove();
			return;
		}else if(t==TTT_Item.SCHWERT_IRON&&getState()!=GameState.InGame){
			ev.setCancelled(true);
			return;
		}
		
		boolean b = false;
		
		if(t.getTyp().equalsIgnoreCase("SCHWERT")){
			for(ItemStack i : ev.getPlayer().getInventory()){
				if(i==null||i.getType()==Material.AIR)continue;
				if(i.getType()==Material.WOOD_SWORD){
					b=true;
					break;
				}else if(i.getType()==Material.STONE_SWORD){
					b=true;
					break;
				}else if(i.getType()==Material.IRON_SWORD){
					b=true;
					break;
				}
			}
			
			if(!b){
				ev.getItemfake().remove();
				ev.getPlayer().getInventory().addItem(ev.getItem().getItemStack());
				ev.getPlayer().updateInventory();
			}
		}else if(t.getTyp().equalsIgnoreCase("BOW")){
			for(ItemStack i : ev.getPlayer().getInventory()){
				if(i==null||i.getType()==Material.AIR)continue;
				if(i.getType()==Material.BOW){
					if(i.hasItemMeta()&&i.getItemMeta().hasDisplayName()){
						if(i.getItemMeta().getDisplayName().equalsIgnoreCase(traitor_shop.getShop_item().getItemMeta().getDisplayName()))continue;
						if(i.getItemMeta().getDisplayName().equalsIgnoreCase(detective_shop.getShop_item().getItemMeta().getDisplayName()))continue;
					}
					b=true;
					break;
				}
			}
			
			if(!b){
				ev.getItemfake().remove();
				if(!arrow.contains(ev.getPlayer())){
					ItemStack item = TTT_Item.ARROW.getItem().clone();
					item.setAmount(30);
					ev.getPlayer().getInventory().addItem(item);
					arrow.add(ev.getPlayer());
				}
				ev.getPlayer().getInventory().addItem(ev.getItem().getItemStack());
				ev.getPlayer().updateInventory();
			}
		}else if(t.getTyp().equalsIgnoreCase("ARROW")){
			ev.getItemfake().remove();
			ev.getPlayer().getInventory().addItem(ev.getItem().getItemStack());
			ev.getPlayer().updateInventory();
		}
	}
	
	public TTT_Item getItemFake(Item item){
		if(!item.getItemStack().hasItemMeta())return null;
		if(!item.getItemStack().getItemMeta().hasDisplayName())return null;
		
		switch(item.getItemStack().getItemMeta().getDisplayName()){
		case "§7Holzschwert":return TTT_Item.SCHWERT_HOLZ;
		case "§8Steinschwert":return TTT_Item.SCHWERT_STONE;
		case "§bEisenschwert":return TTT_Item.SCHWERT_IRON;
		case "§7Pfeile":return TTT_Item.ARROW;
		//case "§7Bogen":return TTT_Item.BOW_BOGEN;
		case "§cMinigun":return TTT_Item.BOW_MINIGUN;
		case "§aShotgun":return TTT_Item.BOW_SHOTGUN;
		case "§eSniper":return TTT_Item.BOW_SNIPER;
		default:
			return null;
		}
	}
	
	public void setItemFake(ArrayList<Location> list){
		
		if(!list.isEmpty()){
			Location loc = list.get(UtilMath.r(list.size()));
			TTT_Item.SCHWERT_IRON.setItemFake(loc);
			list.remove(loc);
		}
		
		int s_h=(int)(list.size()*0.15);// 15 %
		int s_s=(int)(list.size()*0.10);// 15 %
		
		int arrow=(int)(list.size()*0.25);// 15%
		int b_mg=(int)(list.size()*0.1);// 15%
		int b_s=(int)(list.size()*0.15);// 15%
		//int b_b=(int)(list.size()*0.15);// 15%
		int b_sn=(int)(list.size()*0.10);// 20%
		
		System.out.println("INSGESAMT: "+list.size());
		System.out.println("Holz Schwert:"+s_h);
		System.out.println("Stein Schwert: "+s_s);
		System.out.println("Arrow:"+arrow);
		System.out.println("Minigun:"+b_mg);
		System.out.println("Shotgun:"+b_s);
		//System.out.println("Bow:"+b_b);
		System.out.println("Sniper:"+b_sn);	
		
		int r;
		for(int i=0; i < 20000; i++){
			if(list.isEmpty())break;
			r=UtilMath.r(list.size());
			if(s_h!=0){
				TTT_Item.SCHWERT_HOLZ.setItemFake(list.get(r));
				list.remove(r);
				s_h--;
			}else if(arrow!=0){
				TTT_Item.ARROW.setItemFake(list.get(r));
				list.remove(r);
				arrow--;
			}else if(s_s!=0){
				TTT_Item.SCHWERT_STONE.setItemFake(list.get(r));
				list.remove(r);
				s_s--;
			}else if(b_mg!=0){
				TTT_Item.BOW_MINIGUN.setItemFake(list.get(r));
				list.remove(r);
				b_mg--;
			}else if(b_s!=0){
				TTT_Item.BOW_SHOTGUN.setItemFake(list.get(r));
				list.remove(r);
				b_s--;
			}
//			else if(b_b!=0){
//				TTT_Item.BOW_BOGEN.setItemFake(list.get(r));
//				list.remove(r);
//				b_b--;
//			}
			else if(b_sn!=0){
				TTT_Item.BOW_SNIPER.setItemFake(list.get(r));
				list.remove(r);
				b_sn--;
			}else{
				if(UtilMath.r(1)==0){
					TTT_Item.SCHWERT_HOLZ.setItemFake(list.get(r));
				}else{
					TTT_Item.ARROW.setItemFake(list.get(r));
				}
				list.remove(r);
			}
		}
		
		
	}
	
	@EventHandler
	public void Ranking(RankingEvent ev){
		getManager().setRanking(Stats.TTT_KARMA);
	}
	
	@EventHandler
	public void inGame(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		if(getState()!=GameState.InGame)return;
		setStart(getStart()-1);
		if(isInTeam(Team.INOCCENT)==0&&isInTeam(Team.DETECTIVE)==0){
			broadcastWithPrefix("TTT_WIN", Team.TRAITOR.Name());
			
			System.out.println("[TTT] TRAITOR HABEN GEWONNEN");
			for(Player p : getTeamList().keySet()){
				if(getTeamList().get(p)==Team.INOCCENT||getTeamList().get(p)==Team.DETECTIVE){
					getStats().setInt(p,getStats().getInt(Stats.LOSE, p)+1, Stats.LOSE);
				}else{
					getStats().setInt(p,getStats().getInt(Stats.WIN, p)+1, Stats.WIN);
				}
			}
			
			setState(GameState.Restart,GameStateChangeReason.LAST_TEAM);	
		}else if(isInTeam(Team.TRAITOR)==0){
			broadcastWithPrefix("TTT_WIN", Team.INOCCENT.Name());
			System.out.println("[TTT] INNOCENT HABEN GEWONNEN");
			for(Player p : getTeamList().keySet()){
				if(getTeamList().get(p)==Team.INOCCENT||getTeamList().get(p)==Team.DETECTIVE){
					getStats().setInt(p,getStats().getInt(Stats.WIN, p)+1, Stats.WIN);
				}else{
					getStats().setInt(p,getStats().getInt(Stats.LOSE, p)+1, Stats.LOSE);
				}
			}
			
			setState(GameState.Restart,GameStateChangeReason.LAST_TEAM);
		}
		
		for(Player p : UtilServer.getPlayers())UtilDisplay.displayTextBar(p, Language.getText(p, "GAME_END_IN", UtilTime.formatSeconds(getStart())));
		switch(getStart()){
		case 30: broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getStart()));break;
		case 20: broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getStart()));break;
		case 15: broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getStart()));break;
		case 10: broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getStart()));break;
		case 5: broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getStart()));break;
		case 4: broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getStart()));break;
		case 3: broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getStart()));break;
		case 2: broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getStart()));break;
		case 1: broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getStart()));break;
		case 0: 
			Team t = getHaveWinTeam();
			broadcastWithPrefix("TTT_WIN", t.Name());
		
			for(Player p : getTeamList().keySet()){
				if(t==Team.TRAITOR){
					if(getTeamList().get(p)==Team.TRAITOR){
						getStats().setInt(p,getStats().getInt(Stats.WIN, p)+1, Stats.WIN);
					}else{
						getStats().setInt(p,getStats().getInt(Stats.LOSE, p)+1, Stats.LOSE);
					}
				}else{
					if(getTeamList().get(p)==Team.INOCCENT||getTeamList().get(p)==Team.DETECTIVE){
						getStats().setInt(p,getStats().getInt(Stats.WIN, p)+1, Stats.WIN);
					}else{
						getStats().setInt(p,getStats().getInt(Stats.LOSE, p)+1, Stats.LOSE);
					}
				}
			}
			
			setState(GameState.Restart);
			broadcastWithPrefixName("GAME_END");
			break;
		}
	}
	
	@EventHandler
	public void StatsChange(PlayerStatsChangeEvent ev){
		if(getState()!=GameState.LobbyPhase&&getState()!=GameState.Laden){
			if(ev.getStats()==Stats.TTT_KARMA){
				UtilScoreboard.setScore(ev.getPlayer().getScoreboard(), Color.GREEN+"Karma:", DisplaySlot.SIDEBAR, getStats().getInt(Stats.TTT_KARMA, ev.getPlayer()));
			}else if(ev.getStats()==Stats.TTT_DETECTIVE_PUNKTE){
				UtilScoreboard.setScore(ev.getPlayer().getScoreboard(), Color.AQUA+"D-Punkte:", DisplaySlot.SIDEBAR, getStats().getInt(Stats.TTT_DETECTIVE_PUNKTE, ev.getPlayer()));
			}else if(ev.getStats()==Stats.TTT_TRAITOR_PUNKTE){
				UtilScoreboard.setScore(ev.getPlayer().getScoreboard(), Color.RED+"T-Punkte:", DisplaySlot.SIDEBAR, getStats().getInt(Stats.TTT_TRAITOR_PUNKTE, ev.getPlayer()));
			}else if(ev.getStats()==Stats.WIN){
				getCoins().addCoins(ev.getPlayer(), false, 25);
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void JoinHologram(PlayerJoinEvent ev){
		if(getState()!=GameState.LobbyPhase)return;
		int win = getStats().getInt(Stats.WIN, ev.getPlayer());
		int lose = getStats().getInt(Stats.LOSE, ev.getPlayer());
		
		getManager().getHologram().sendText(ev.getPlayer(),getManager().getLoc_stats(),new String[]{
			Color.GREEN+getType().getTyp()+Color.ORANGE+"§l Info",
			Language.getText(ev.getPlayer(), "GAME_HOLOGRAM_SERVER",getType().getTyp()+" §a"+kArcade.id),
			Language.getText(ev.getPlayer(), "GAME_HOLOGRAM_MAP", getWorldData().getMapName()),
			" ",
			Language.getText(ev.getPlayer(), "GAME_HOLOGRAM_STATS", getType().getTyp()),
			Language.getText(ev.getPlayer(), "GAME_HOLOGRAM_KILLS", getStats().getInt(Stats.KILLS, ev.getPlayer())),
			Language.getText(ev.getPlayer(), "GAME_HOLOGRAM_DEATHS", getStats().getInt(Stats.DEATHS, ev.getPlayer())),
			Language.getText(ev.getPlayer(), "GAME_HOLOGRAM_KARMA", getStats().getInt(Stats.TTT_KARMA, ev.getPlayer())),
			Language.getText(ev.getPlayer(), "GAME_HOLOGRAM_TESTS", getStats().getInt(Stats.TTT_TESTS, ev.getPlayer())),
			Language.getText(ev.getPlayer(), "GAME_HOLOGRAM_TRAITOR_POINTS", getStats().getInt(Stats.TTT_TRAITOR_PUNKTE, ev.getPlayer())),
			Language.getText(ev.getPlayer(), "GAME_HOLOGRAM_DETECTIVE_POINTS", getStats().getInt(Stats.TTT_DETECTIVE_PUNKTE, ev.getPlayer())),
			" ",
			Language.getText(ev.getPlayer(), "GAME_HOLOGRAM_GAMES", (win+lose)),
			Language.getText(ev.getPlayer(), "GAME_HOLOGRAM_WINS", win),
			Language.getText(ev.getPlayer(), "GAME_HOLOGRAM_LOSE", lose),
			});
	}
	
	@EventHandler
	public void Schutzzeit(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		if(getState()!=GameState.StartGame)return;
		setStart(getStart()-1);
		for(Player p : UtilServer.getPlayers())UtilDisplay.displayTextBar(p, Language.getText(p, "SCHUTZZEIT_END_IN", getStart()));
		switch(getStart()){
		case 30: broadcastWithPrefix("SCHUTZZEIT_END_IN", getStart());break;
		case 20: broadcastWithPrefix("SCHUTZZEIT_END_IN", getStart());break;
		case 15: broadcastWithPrefix("SCHUTZZEIT_END_IN", getStart());break;
		case 10: broadcastWithPrefix("SCHUTZZEIT_END_IN", getStart());break;
		case 5: broadcastWithPrefix("SCHUTZZEIT_END_IN", getStart());break;
		case 4: broadcastWithPrefix("SCHUTZZEIT_END_IN", getStart());break;
		case 3: broadcastWithPrefix("SCHUTZZEIT_END_IN", getStart());break;
		case 2: broadcastWithPrefix("SCHUTZZEIT_END_IN", getStart());break;
		case 1: broadcastWithPrefix("SCHUTZZEIT_END_IN", getStart());break;
		case 0: 
			setStart(60*20);
			ArrayList<Player> plist = new ArrayList<>();
			for(Player p : getGameList().getPlayers(PlayerState.IN)){
				plist.add(p);
			}
			PlayerVerteilung(verteilung(),plist);
			ArrayList<Player> d = (ArrayList<Player>)getPlayerFrom(Team.DETECTIVE);
			Scoreboard ps;
			for(Player p : d){
				ps = Bukkit.getScoreboardManager().getNewScoreboard();
				UtilScoreboard.addBoard(ps,DisplaySlot.SIDEBAR, Color.BLUE+"DetectiveBoard");
				UtilScoreboard.setScore(ps,Color.GREEN+"Karma:", DisplaySlot.SIDEBAR, getStats().getInt(Stats.TTT_KARMA, p));
				UtilScoreboard.setScore(ps,Color.AQUA+"D-Punkte:", DisplaySlot.SIDEBAR, getStats().getInt(Stats.TTT_DETECTIVE_PUNKTE, p));
				if(d.size()!=1){
					UtilScoreboard.setScore(ps,"§7", DisplaySlot.SIDEBAR, -1);
					UtilScoreboard.setScore(ps,Color.BLUE+"Detective:", DisplaySlot.SIDEBAR, -2);
					int t = -3;
					for(Player p1 : d){
						if(p1==p)continue;
						UtilScoreboard.setScore(ps,p1.getName(), DisplaySlot.SIDEBAR, t);
						t--;
					}
				}
				
				org.bukkit.scoreboard.Team s = ps.registerNewTeam(Team.INOCCENT.Name());
				s.setPrefix(Team.INOCCENT.getColor()+"[I] ");
				for(Player p1 : getTeamList().keySet()){
					if(getTeamList().get(p1)==Team.TRAITOR||getTeamList().get(p1)==Team.INOCCENT){
						s.addPlayer(p1);
					}
				}
				
				s = ps.registerNewTeam(Team.DETECTIVE.Name());
				s.setPrefix(Team.DETECTIVE.getColor()+"[D] ");
				for(Player p1 : getTeamList().keySet()){
					if(getTeamList().get(p1)==Team.DETECTIVE){
						s.addPlayer(p1);
					}
				}
				
				p.setScoreboard(ps);
				p.sendMessage(Language.getText(p, "PREFIX_GAME",getType().getTyp())+Language.getText(p, "TTT_IS_NOW",Team.DETECTIVE.Name()));
				p.getInventory().setChestplate(UtilItem.LSetColor(new ItemStack(Material.LEATHER_CHESTPLATE), org.bukkit.Color.BLUE));
				p.getInventory().addItem(detective_shop.getShop_item());
			}
			ArrayList<Player> t = (ArrayList<Player>)getPlayerFrom(Team.TRAITOR);
			for(Player p : t){
				traitor.add(p);
				p.getInventory().addItem(traitor_shop.getShop_item());
				ps=Bukkit.getScoreboardManager().getNewScoreboard();
				UtilScoreboard.addBoard(ps,DisplaySlot.SIDEBAR, Color.RED+"TraitorBoard");
				UtilScoreboard.setScore(ps,Color.GREEN+"Karma:", DisplaySlot.SIDEBAR, getStats().getInt(Stats.TTT_KARMA, p));
				UtilScoreboard.setScore(ps,Color.RED+"T-Punkte:", DisplaySlot.SIDEBAR, getStats().getInt(Stats.TTT_TRAITOR_PUNKTE, p));
				p.sendMessage(Language.getText(p, "PREFIX_GAME",getType().getTyp())+Language.getText(p, "TTT_IS_NOW",Team.TRAITOR.Name()));
				p.sendMessage(Language.getText(p, "PREFIX_GAME",getType().getTyp())+Language.getText(p, "TTT_TRAITOR_CHAT"));
				if(t.size()!=1){
					UtilScoreboard.setScore(ps,"§7", DisplaySlot.SIDEBAR, -1);
					UtilScoreboard.setScore(ps,Color.RED+"Traitor:", DisplaySlot.SIDEBAR, -2);
					int t1 = -3;
					for(Player p1 : t){
						if(p1==p)continue;
						UtilScoreboard.setScore(ps,p1.getName(), DisplaySlot.SIDEBAR, t1);
						t1--;
					}
				}
				
				org.bukkit.scoreboard.Team s = ps.registerNewTeam(Team.INOCCENT.Name());
				s.setPrefix(Team.INOCCENT.getColor()+"[I] ");
				for(Player p1 : getTeamList().keySet()){
					if(getTeamList().get(p1)==Team.INOCCENT){
						s.addPlayer(p1);
					}
				}
				
				s = ps.registerNewTeam(Team.TRAITOR.Name());
				s.setPrefix(Team.TRAITOR.getColor()+"[T] ");
				for(Player p1 : getTeamList().keySet()){
					if(getTeamList().get(p1)==Team.TRAITOR){
						s.addPlayer(p1);
					}
				}
				
				s = ps.registerNewTeam(Team.DETECTIVE.Name());
				s.setPrefix(Team.DETECTIVE.getColor()+"[D] ");
				for(Player p1 : getTeamList().keySet()){
					if(getTeamList().get(p1)==Team.DETECTIVE){
						s.addPlayer(p1);
					}
				}
				p.setScoreboard(ps);
			}
			
			for(Player p : t){
				for(Player p1 : t){
					UtilPlayer.setPlayerFakeEquipment(p, p1, UtilItem.LSetColor(new ItemStack(Material.LEATHER_CHESTPLATE), org.bukkit.Color.RED), (short)2);
				}
			}
			
			ArrayList<Player> i = (ArrayList<Player>) getPlayerFrom(Team.INOCCENT);
			
			for(Player p:i){
				ps=Bukkit.getScoreboardManager().getNewScoreboard();
				UtilScoreboard.addBoard(ps,DisplaySlot.SIDEBAR, Color.RED+"InnocentBoard");
				UtilScoreboard.setScore(ps,Color.GREEN+"Karma:", DisplaySlot.SIDEBAR, getStats().getInt(Stats.TTT_KARMA, p));
				org.bukkit.scoreboard.Team s = ps.registerNewTeam(Team.INOCCENT.Name());
				s.setPrefix(Team.INOCCENT.getColor()+"[I] ");
				for(Player p1 : getTeamList().keySet()){
					if(getTeamList().get(p1)==Team.TRAITOR||getTeamList().get(p1)==Team.INOCCENT){
						s.addPlayer(p1);
					}
				}
				
				s = ps.registerNewTeam(Team.DETECTIVE.Name());
				s.setPrefix(Team.DETECTIVE.getColor()+"[D] ");
				for(Player p1 : getTeamList().keySet()){
					if(getTeamList().get(p1)==Team.DETECTIVE){
						s.addPlayer(p1);
					}
				}
				p.setScoreboard(ps);
				p.sendMessage(Language.getText(p, "PREFIX_GAME",getType().getTyp())+Language.getText(p, "TTT_IS_NOW",Team.INOCCENT.Name()));
			}
			setDamage(true);
			setProjectileDamage(true);
			setDamageSelf(true);
			setDamageTeamOther(true);
			broadcastWithPrefixName("SCHUTZZEIT_END");
			setState(GameState.InGame);
			break;
		}
	}
	
	@EventHandler
	public void GameStateChange(GameStateChangeEvent ev){
		if(ev.getTo()==GameState.Restart&&ev.getFrom()!=GameState.Restart&&ev.getReason()!=GameStateChangeReason.CHANGE_TYPE){
			System.err.println("[TTT] GameStateChangeReason: "+ev.getReason().getName());
			if(traitor.isEmpty()){
				String t = "";
				for(Player p : traitor){
					t=t+p.getName()+",";
				}
				broadcastWithPrefix("§cDiese Spieler waren Traitor: §e"+t.substring(0, t.length()-1));
			}
		}
	}
	
	@EventHandler
	public void World(WorldLoadEvent ev){
		this.tester = new Tester(this, getWorldData().getLocs(Team.BLUE).get(0),getWorldData().getLocs(Team.GREEN).get(0), getWorldData().getLocs(Team.SOLO), getWorldData().getLocs(Team.GRAY));
	}
	
	@EventHandler
	public void Regen(EntityRegainHealthEvent ev){
		if(getState()==GameState.LobbyPhase)return;
		if(ev.getRegainReason()==RegainReason.SATIATED)ev.setCancelled(true);
	}
	
	@EventHandler
	public void StatsCreate(PlayerStatsCreateEvent ev){
		ev.getManager().setInt(ev.getPlayer(), 150, Stats.TTT_KARMA);
	}
	
	@EventHandler
	public void Start(GameStartEvent ev){
		ArrayList<Location> list = getWorldData().getLocs(Team.RED);
		int r=0;
		for(Player p : UtilServer.getPlayers()){
			getManager().Clear(p);
			p.setMaxHealth(40);
			p.setHealth(40);
			r=UtilMath.r(list.size());
			p.teleport(list.get(r));
			p.getInventory().addItem(magnet.getStab());
			getGameList().addPlayer(p,PlayerState.IN);
		}
		ifm=new ItemFakeManager(getManager().getInstance(),getManager().getHologram());
		setItemFake(getWorldData().getLocs(Team.YELLOW));
		new AddonDay(getManager().getInstance(),getWorldData().getWorld());
		setDamage(false);
		setStart(31);
		setState(GameState.StartGame);
	}

	public HashMap<Team,Integer> verteilung(){
		HashMap<Team,Integer> list = new HashMap<>();
		list.put(Team.DETECTIVE, getDetective());
		list.put(Team.TRAITOR, getTraitor());
		list.put(Team.INOCCENT,UtilServer.getPlayers().size()-(getTraitor()+getDetective()));
		
		return list;
	}
	
	public int getTraitor(){
		switch(UtilServer.getPlayers().size()){
		case 3: return 1;
		case 4: return 1;
		case 5: return 1;
		case 6: return 2;
		case 7: return 2;
		case 8: return 2;
		case 9: return 2;
		case 10: return 3;
		case 11: return 3;
		case 12: return 3;
		case 13: return 3;
		case 14: return 4;
		case 15: return 4;
		case 16: return 5;
		case 17: return 5;
		case 18: return 5;
		case 19: return 6;
		case 20: return 6;
		case 21: return 7;
		case 22: return 7;
		case 23: return 8;
		case 24: return 8;
		}
		return -1;
	}
	
	public int getDetective(){
		switch(UtilServer.getPlayers().size()){
		case 3: return 1;
		case 4: return 1;
		case 5: return 1;
		case 6: return 1;
		case 7: return 1;
		case 8: return 1;
		case 9: return 1;
		case 10: return 2;
		case 11: return 2;
		case 12: return 2;
		case 13: return 2;
		case 14: return 2;
		case 15: return 3;
		case 16: return 3;
		case 17: return 3;
		case 18: return 3;
		case 19: return 4;
		case 20: return 4;
		case 21: return 5;
		case 22: return 5;
		case 23: return 6;
		case 24: return 6;
		}
		return -1;
	}
	
}
