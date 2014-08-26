package me.kingingo.karcade.Game.Games.TroubleInMinecraft;

import java.util.ArrayList;
import java.util.HashMap;

import lombok.Getter;
import me.kingingo.karcade.kArcade;
import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Enum.PlayerState;
import me.kingingo.karcade.Enum.Team;
import me.kingingo.karcade.Events.RankingEvent;
import me.kingingo.karcade.Events.WorldLoadEvent;
import me.kingingo.karcade.Game.Events.GameStartEvent;
import me.kingingo.karcade.Game.Games.TeamGame;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.Command.CommandDetective;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.Command.CommandTraitor;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.Shop.IShop;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.Shop.Shop;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.Shop.Item.Defibrillator;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.Shop.Item.Fake_Chest;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.Shop.Item.Golden_Weapon;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.Shop.Item.Healing_Station;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.Shop.Item.Knife;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.Shop.Item.Medipack;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.Shop.Item.Radar;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.Shop.Item.Tester_Spoofer;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.Weapon.Minigun;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.Weapon.Shotgun;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.Weapon.Sniper;
import me.kingingo.karcade.Game.World.WorldData;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.Hologram.Hologram;
import me.kingingo.kcore.ItemFake.ItemFake;
import me.kingingo.kcore.ItemFake.ItemFakeManager;
import me.kingingo.kcore.ItemFake.Events.ItemFakePickupEvent;
import me.kingingo.kcore.NPC.NPC;
import me.kingingo.kcore.NPC.NPCManager;
import me.kingingo.kcore.NPC.Event.PlayerInteractNPCEvent;
import me.kingingo.kcore.PlayerStats.Stats;
import me.kingingo.kcore.PlayerStats.Event.PlayerStatsChangeEvent;
import me.kingingo.kcore.ScoreboardManager.PlayerScoreboard;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.C;
import me.kingingo.kcore.Util.UtilDisplay;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilMath;
import me.kingingo.kcore.Util.UtilPlayer;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.kcore.Util.UtilTime;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityEquipment;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;

public class TroubleInMinecraft extends TeamGame{
	
	WorldData wd;
	@Getter
	private kArcadeManager manager;
	HashMap<Integer,String> npclist = new HashMap<>();
	Tester tester;
	Shop dshop;
	NPCManager npcManager;
	Hologram hm;
	HashMap<Player,PlayerScoreboard> boards = new HashMap<>();
	@Getter
	ItemFakeManager ifm;
	@Getter
	public static Shotgun shotgun;
	@Getter
	public static Sniper sniper;
	@Getter
	public static Minigun minigun;
	
	Defibrillator defi;
	Shop traitor_shop;
	Shop detective_shop;
	
	public TroubleInMinecraft(kArcadeManager manager) {
		super(manager);
		this.manager=manager;
		long t = System.currentTimeMillis();
		manager.setState(GameState.Laden);
		manager.setTyp(GameType.TroubleInMinecraft);
		setMin_Players(1);
		setMax_Players(12);
		setDamageTeamSelf(true);
		setDamageSelf(true);
		setItemDrop(true);
		setItemPickup(true);
		setBlockPlace(false);
		setBlockBreak(false);
		setCreatureSpawn(false);
		setHangingBreak(false);
		setBlockSpread(false);
		setDamagePvP(true);
		setExplosion(false);
		setDeathDropItems(true);
		setDamageEvP(false);
		setDamagePvE(true);
		setFoodChange(false);
		setProjectileDamage(true);
		setRespawn(true);
		shotgun=new Shotgun(this);
		sniper=new Sniper(this);
		minigun=new Minigun(this);
		getManager().getCmd().register(CommandDetective.class, new CommandDetective(this));
		getManager().getCmd().register(CommandTraitor.class, new CommandTraitor(this));
		npcManager= new NPCManager(getManager().getInstance());
		Radar r = new Radar(this);
		defi = new Defibrillator(this);
		traitor_shop= new Shop(this,"Traitor-Shop:",Stats.TTT_TRAITOR_PUNKTE,Team.TRAITOR,new IShop[]{
				new Fake_Chest(this),
				new Medipack(this),
				new Knife(this),
				r,
				new Tester_Spoofer(this)
		});
		
		detective_shop= new Shop(this,"Detective-Shop:",Stats.TTT_DETECTIVE_PUNKTE,Team.DETECTIVE,new IShop[]{
				defi,
				new Golden_Weapon(this),
				r,
				new Healing_Station(this)
		});
		wd = new WorldData(manager,GameType.TroubleInMinecraft.name());
		wd.Initialize();
		manager.setWorldData(wd);
		manager.setState(GameState.LobbyPhase);
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
	
	@EventHandler
	public void Chat(PlayerChatEvent ev){
		ev.setCancelled(true);
		if(getManager().getState()!=GameState.LobbyPhase&&getGameList().getPlayers(PlayerState.OUT).contains(ev.getPlayer())){
			ev.setCancelled(true);
			UtilPlayer.sendMessage(ev.getPlayer(),Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.SPECTATOR_CHAT_CANCEL.getText());
			return;
		}
		UtilServer.broadcast(C.cGray+ev.getPlayer().getDisplayName()+": "+ev.getMessage());
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
	
	 @EventHandler
		public void Aufdecken(PlayerInteractNPCEvent ev){
			NPC npc = ev.getNpc();
			
			if(npc.getName().equalsIgnoreCase("Unidentifiziert")){
				String name = npclist.get(npc.getP().getId());
				Location loc = npc.getLoc();
				npclist.remove(npc.getP().getId());
				npc.remove();
				
				npc = npcManager.createNPC( name );
				npc.spawn(loc);
				npc.sleep();
				
				if(getTeam(ev.getPlayer())==Team.DETECTIVE){
					getManager().getStats().setInt(ev.getPlayer(),getManager().getStats().getInt(Stats.TTT_DETECTIVE_PUNKTE, ev.getPlayer())+1, Stats.TTT_DETECTIVE_PUNKTE);	
				}
			}else{
				if(defi.getTeams().containsKey(npc.getName().toLowerCase())){
					UtilPlayer.sendMessage(ev.getPlayer(),Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.TTT_NPC_CLICKED.getText(new String[]{npc.getName(),defi.getTeams().get(npc.getName().toLowerCase()).getColor()+defi.getTeams().get(npc.getName().toLowerCase()).name()}));
				}
			}
		}
	
	@EventHandler
	public void DeathTTT(PlayerDeathEvent ev){
		if(ev.getEntity() instanceof Player&&getGameList().getPlayers(PlayerState.IN).contains( ((Player)ev.getEntity()) )){
			getManager().getStats().setInt(((Player)ev.getEntity()),getManager().getStats().getInt(Stats.DEATHS, ((Player)ev.getEntity()))+1, Stats.DEATHS);
			getGameList().addPlayer( ((Player)ev.getEntity()) , PlayerState.OUT);
			boolean b = false;
			for(ItemStack item : ev.getDrops()){
				for(TTT_Item ii : TTT_Item.values()){
					if(UtilItem.ItemNameEquals(ii.getItem(), item)){
						b=true;
						break;
					}
				}
				if(b)new ItemFake(ev.getEntity().getLocation().add(UtilMath.RandomDouble(-0.4, 0.4),0.2,UtilMath.RandomDouble(-0.4, 0.4)),item,getManager().getInstance());
			}
			ev.getDrops().clear();
			NPC npc = npcManager.createNPC( "Unidentifiziert" );
			npc.spawn( ((Player)ev.getEntity()).getLocation() );
			npc.sleep();
			npclist.put(npc.getP().getId(), ((Player)ev.getEntity()).getName());
			if(ev.getEntity().getKiller() instanceof Player){
				getManager().getStats().setInt(((Player)ev.getEntity().getKiller()),getManager().getStats().getInt(Stats.KILLS, ((Player)ev.getEntity().getKiller()))+1, Stats.KILLS);
				Team t = getTeam(((Player)ev.getEntity()));
				Team t1 = getTeam( ((Player)ev.getEntity().getKiller()) );
				int k = getManager().getStats().getInt(Stats.TTT_KARMA, (Player)ev.getEntity().getKiller());
				int d=-1;
				int tr=-1;
				if(t1==Team.TRAITOR){
					if(t==Team.TRAITOR){
						getManager().getStats().setInt( ((Player)ev.getEntity().getKiller()) , k-50, Stats.TTT_KARMA);
						k=-50;
					}else if(t==Team.INOCCENT){
						getManager().getStats().setInt( ((Player)ev.getEntity().getKiller()) , k+10, Stats.TTT_KARMA);
						getManager().getStats().setInt( ((Player)ev.getEntity().getKiller()) ,getManager().getStats().getInt(Stats.TTT_TRAITOR_PUNKTE, (Player)ev.getEntity().getKiller())+2, Stats.TTT_TRAITOR_PUNKTE);
						k=10;
						tr=2;
					}else if(t==Team.DETECTIVE){
						getManager().getStats().setInt( ((Player)ev.getEntity().getKiller()) , k+20, Stats.TTT_KARMA);
						getManager().getStats().setInt( ((Player)ev.getEntity().getKiller()) ,getManager().getStats().getInt(Stats.TTT_TRAITOR_PUNKTE, (Player)ev.getEntity().getKiller())+4, Stats.TTT_TRAITOR_PUNKTE);
						k=20;
						tr=4;
					}
				}else if(t1==Team.INOCCENT){
					if(t==Team.TRAITOR){
						getManager().getStats().setInt( ((Player)ev.getEntity().getKiller()) , k+20, Stats.TTT_KARMA);
						k=20;
					}else if(t==Team.INOCCENT){
						getManager().getStats().setInt( ((Player)ev.getEntity().getKiller()) , k-20, Stats.TTT_KARMA);
						k=-20;
					}else if(t==Team.DETECTIVE){
						getManager().getStats().setInt( ((Player)ev.getEntity().getKiller()) , k-50, Stats.TTT_KARMA);
						k=-50;
					}
				}else if(t1==Team.DETECTIVE){
					if(t==Team.TRAITOR){
						getManager().getStats().setInt( ((Player)ev.getEntity().getKiller()) , k+30, Stats.TTT_KARMA);
						getManager().getStats().setInt( ((Player)ev.getEntity().getKiller()) ,getManager().getStats().getInt(Stats.TTT_DETECTIVE_PUNKTE, (Player)ev.getEntity().getKiller())+2, Stats.TTT_DETECTIVE_PUNKTE);
						k=30;
						d=2;
					}else if(t==Team.INOCCENT){
						getManager().getStats().setInt( ((Player)ev.getEntity().getKiller()) , k-20, Stats.TTT_KARMA);
						k=-20;
					}else if(t==Team.DETECTIVE){
						getManager().getStats().setInt( ((Player)ev.getEntity().getKiller()) , k-50, Stats.TTT_KARMA);
						k=-50;
					}
				}
			
				if(d!=-1){
					if(k>0){
						hm.sendText(ev.getEntity().getKiller(), ev.getEntity().getLocation().add(0,1,0), 3, new String[]{
							"§aDu hast §e"+k+" Karma§a erhalten.",
							"§bDu hast §e"+d+" Detective-Punke§b erhalten."
						});
					}else{
						hm.sendText(ev.getEntity().getKiller(), ev.getEntity().getLocation().add(0,1,0), 3, new String[]{
							 "§cDu hast §e"+k+" Karma§c verloren.",
							 "§bDu hast §e"+d+" Detective-Punke§b erhalten."
						});
					}
				}else if(tr!=-1){
					if(k>0){
						hm.sendText(ev.getEntity().getKiller(), ev.getEntity().getLocation().add(0,1,0), 3, new String[]{
							"§aDu hast §e"+k+" Karma§a erhalten.",
							"§bDu hast §e"+tr+" Traitor-Punke§b erhalten."
						});
					}else{
						hm.sendText(ev.getEntity().getKiller(), ev.getEntity().getLocation().add(0,1,0), 3, new String[]{
							 "§cDu hast §e"+k+" Karma§c verloren.",
							 "§bDu hast §e"+tr+" Traitor-Punke§b erhalten."
						});
					}
				}else{
					if(k>0){
						hm.sendText(ev.getEntity().getKiller(), ev.getEntity().getLocation().add(0,1,0), 3, "§aDu hast §e"+k+" Karma§a erhalten.");
					}else{
						hm.sendText(ev.getEntity().getKiller(), ev.getEntity().getLocation().add(0,1,0), 3, "§cDu hast §e"+k+" Karma§c verloren.");
					}
				}
				
			}
			
			delTeam(((Player)ev.getEntity()));
		}
	}
	
	@EventHandler
	public void DropItemFake(PlayerDropItemEvent ev){
		if(ev.getItemDrop().getItemStack().getType()==Material.WOOD_SWORD||ev.getItemDrop().getItemStack().getType()==Material.STONE_SWORD||ev.getItemDrop().getItemStack().getType()==Material.IRON_SWORD||ev.getItemDrop().getItemStack().getType()==Material.BOW){
			new ItemFake(ev.getItemDrop(),getManager().getInstance());
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
			}
		}else if(t.getTyp().equalsIgnoreCase("BOW")){
			for(ItemStack i : ev.getPlayer().getInventory()){
				if(i==null||i.getType()==Material.AIR)continue;
				if(i.getType()==Material.BOW){
					b=true;
					break;
				}
			}
			
			if(!b){
				ev.getItemfake().remove();
				ev.getPlayer().getInventory().addItem(ev.getItem().getItemStack());
			}
		}else if(t.getTyp().equalsIgnoreCase("ARROW")){
			ev.getItemfake().remove();
			ev.getPlayer().getInventory().addItem(ev.getItem().getItemStack());
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
		case "Bogen":return TTT_Item.BOW_BOGEN;
		case "§cMinigun":return TTT_Item.BOW_MINIGUN;
		case "§aShotgun":return TTT_Item.BOW_SHOTGUN;
		case "§eSniper":return TTT_Item.BOW_SNIPER;
		default:
			return null;
		}
	}
	
	public void setItemFake(ArrayList<Location> list){
		int s_h=(int)(list.size()*0.2);// 20 %
		int s_s=(int)(list.size()*0.15);// 15 %
		int s_i=(int)(list.size()*0.05);// 5 %
		
		int arrow=(int)(list.size()*0.1);// 10%
		int b_mg=(int)(list.size()*0.1);// 10%
		int b_s=(int)(list.size()*0.15);// 15%
		int b_b=(int)(list.size()*0.25);// 15%
		int b_sn=(int)(list.size()*0.10);// 10%
		
		System.out.println("S_H "+s_h);
		System.out.println("S_s "+s_s);
		System.out.println("S_i "+s_i);

		System.out.println("ARROW "+arrow);
		System.out.println("b_mg "+b_mg);
		System.out.println("b_s "+b_s);
		System.out.println("b_b "+b_b);
		System.out.println("b_sn "+b_sn);	
		
		int r;
		for(int i=0; i < 20000; i++){
			if(list.isEmpty())break;
			r=UtilMath.r(list.size());
			if(s_h!=0){
				TTT_Item.SCHWERT_HOLZ.setItemFake(list.get(r),getManager().getInstance());
				list.remove(r);
				s_h--;
			}else if(arrow!=0){
				TTT_Item.ARROW.setItemFake(list.get(r),getManager().getInstance());
				list.remove(r);
				arrow--;
			}else if(s_s!=0){
				TTT_Item.SCHWERT_STONE.setItemFake(list.get(r),getManager().getInstance());
				list.remove(r);
				s_s--;
			}else if(s_i!=0){
				TTT_Item.SCHWERT_IRON.setItemFake(list.get(r),getManager().getInstance());
				list.remove(r);
				s_i--;
			}else if(b_mg!=0){
				TTT_Item.BOW_MINIGUN.setItemFake(list.get(r),getManager().getInstance());
				list.remove(r);
				b_mg--;
			}else if(b_s!=0){
				TTT_Item.BOW_SHOTGUN.setItemFake(list.get(r),getManager().getInstance());
				list.remove(r);
				b_s--;
			}else if(b_b!=0){
				TTT_Item.BOW_BOGEN.setItemFake(list.get(r),getManager().getInstance());
				list.remove(r);
				b_b--;
			}else if(b_sn!=0){
				TTT_Item.BOW_SNIPER.setItemFake(list.get(r),getManager().getInstance());
				list.remove(r);
				b_sn--;
			}else{
				if(UtilMath.r(1)==0){
					TTT_Item.SCHWERT_HOLZ.setItemFake(list.get(r),getManager().getInstance());
				}else{
					TTT_Item.BOW_BOGEN.setItemFake(list.get(r),getManager().getInstance());
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
		if(getManager().getState()!=GameState.InGame)return;
		getManager().setStart(getManager().getStart()-1);
//		if(isInTeam(Team.INOCCENT)==0&&isInTeam(Team.DETECTIVE)==0){
//			getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.TTT_WIN.getText(Team.INOCCENT.Name()));
//			
//			for(Player p : getTeamList().keySet()){
//				if(getTeamList().get(p)==Team.INOCCENT||getTeamList().get(p)==Team.DETECTIVE){
//					getManager().getStats().setInt(p,getManager().getStats().getInt(Stats.WIN, p)+1, Stats.WIN);
//				}else{
//					getManager().getStats().setInt(p,getManager().getStats().getInt(Stats.LOSE, p)+1, Stats.LOSE);
//				}
//			}
//			
//			getManager().setState(GameState.Restart);	
//		}else if(isInTeam(Team.TRAITOR)==0){
//			getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.TTT_WIN.getText(Team.TRAITOR.Name()));
//			
//			for(Player p : getTeamList().keySet()){
//				if(getTeamList().get(p)==Team.INOCCENT||getTeamList().get(p)==Team.DETECTIVE){
//					getManager().getStats().setInt(p,getManager().getStats().getInt(Stats.WIN, p)+1, Stats.WIN);
//				}else{
//					getManager().getStats().setInt(p,getManager().getStats().getInt(Stats.LOSE, p)+1, Stats.LOSE);
//				}
//			}
//			
//			getManager().setState(GameState.Restart);
//		}
		
		for(Player p : UtilServer.getPlayers())UtilDisplay.displayTextBar(p, Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())));
		switch(getManager().getStart()){
		case 30: getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())));break;
		case 20: getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())));break;
		case 15: getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())));break;
		case 10: getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())));break;
		case 5: getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())));break;
		case 4: getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())));break;
		case 3: getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())));break;
		case 2: getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())));break;
		case 1: getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())));break;
		case 0: 
			Team t = getHaveWinTeam();
			getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.TTT_WIN.getText(t.Name()));
		
			for(Player p : getTeamList().keySet()){
				if(t==Team.TRAITOR){
					if(getTeamList().get(p)==Team.TRAITOR){
						getManager().getStats().setInt(p,getManager().getStats().getInt(Stats.WIN, p)+1, Stats.WIN);
					}else{
						getManager().getStats().setInt(p,getManager().getStats().getInt(Stats.LOSE, p)+1, Stats.LOSE);
					}
				}else{
					if(getTeamList().get(p)==Team.INOCCENT||getTeamList().get(p)==Team.DETECTIVE){
						getManager().getStats().setInt(p,getManager().getStats().getInt(Stats.WIN, p)+1, Stats.WIN);
					}else{
						getManager().getStats().setInt(p,getManager().getStats().getInt(Stats.LOSE, p)+1, Stats.LOSE);
					}
				}
			}
			
			getManager().setState(GameState.Restart);
			getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.GAME_END.getText());
			break;
		}
	}
	
	@EventHandler
	public void StatsChange(PlayerStatsChangeEvent ev){
		if(!boards.containsKey(ev.getPlayer()))return;
		if(ev.getStats()==Stats.TTT_KARMA){
			PlayerScoreboard ps = boards.get(ev.getPlayer());
			ps.setScore(C.cGreen+"Karma:", DisplaySlot.SIDEBAR, getManager().getStats().getInt(Stats.TTT_KARMA, ev.getPlayer()));
		}else if(ev.getStats()==Stats.TTT_DETECTIVE_PUNKTE){
			PlayerScoreboard ps = boards.get(ev.getPlayer());
			ps.setScore(C.cAqua+"D-Punkte:", DisplaySlot.SIDEBAR, getManager().getStats().getInt(Stats.TTT_DETECTIVE_PUNKTE, ev.getPlayer()));
		}else if(ev.getStats()==Stats.TTT_DETECTIVE_PUNKTE){
			PlayerScoreboard ps = boards.get(ev.getPlayer());
			ps.setScore(C.cRed+"T-Punkte:", DisplaySlot.SIDEBAR, getManager().getStats().getInt(Stats.TTT_TRAITOR_PUNKTE, ev.getPlayer()));
		}
	}
	
	@EventHandler
	public void JoinHologram(PlayerJoinEvent ev){
		if(getManager().getState()!=GameState.LobbyPhase)return;
		if(hm==null)hm=new Hologram(getManager().getInstance());

		int win = getManager().getStats().getInt(Stats.WIN, ev.getPlayer());
		int lose = getManager().getStats().getInt(Stats.LOSE, ev.getPlayer());
		getManager().getLoc_stats().getWorld().loadChunk(getManager().getLoc_stats().getWorld().getChunkAt(getManager().getLoc_stats()));
		hm.sendText(ev.getPlayer(),getManager().getLoc_stats().add(0, 0.59, 0),new String[]{
		C.cGreen+getManager().getTyp().string()+C.mOrange+C.Bold+" Info",
		"Server: TroubleInMinecraft §a"+kArcade.id,
		"Map: "+wd.getMapName(),
		" ",
		C.cGreen+getManager().getTyp().string()+C.mOrange+C.Bold+" Stats",
		"Rang: "+getManager().getStats().getRank(Stats.WIN, ev.getPlayer()),	
		"Kills: "+getManager().getStats().getInt(Stats.KILLS, ev.getPlayer()),
		"Tode: "+getManager().getStats().getInt(Stats.DEATHS, ev.getPlayer()),
		"Karma: "+getManager().getStats().getInt(Stats.TTT_KARMA, ev.getPlayer()),
		"Tests: "+getManager().getStats().getInt(Stats.TTT_TESTS, ev.getPlayer()),
		"Traitor-Punkte: "+getManager().getStats().getInt(Stats.TTT_TRAITOR_PUNKTE, ev.getPlayer()),
		"Detective-Punkte: "+getManager().getStats().getInt(Stats.TTT_DETECTIVE_PUNKTE, ev.getPlayer()),
		" ",
		"Gespielte Spiele: "+(win+lose),
		"Gewonnene Spiele: "+win,
		"Verlorene Spiele: "+lose
		});
	}
	
	@EventHandler
	public void Schutzzeit(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		if(getManager().getState()!=GameState.StartGame)return;
		getManager().setStart(getManager().getStart()-1);
		for(Player p : UtilServer.getPlayers())UtilDisplay.displayTextBar(p, Text.SCHUTZZEIT_END_IN.getText(getManager().getStart()));
		switch(getManager().getStart()){
		case 30: getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.SCHUTZZEIT_END_IN.getText(getManager().getStart()));break;
		case 20: getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.SCHUTZZEIT_END_IN.getText(getManager().getStart()));break;
		case 15: getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.SCHUTZZEIT_END_IN.getText(getManager().getStart()));break;
		case 10: getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.SCHUTZZEIT_END_IN.getText(getManager().getStart()));break;
		case 5: getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.SCHUTZZEIT_END_IN.getText(getManager().getStart()));break;
		case 4: getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.SCHUTZZEIT_END_IN.getText(getManager().getStart()));break;
		case 3: getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.SCHUTZZEIT_END_IN.getText(getManager().getStart()));break;
		case 2: getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.SCHUTZZEIT_END_IN.getText(getManager().getStart()));break;
		case 1: getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.SCHUTZZEIT_END_IN.getText(getManager().getStart()));break;
		case 0: 
			getManager().setStart(60*20);
			ArrayList<Player> plist = new ArrayList<>();
			for(Player p : UtilServer.getPlayers()){
				plist.add(p);
			}
			PlayerVerteilung(verteilung(),plist);
			ArrayList<Player> d = (ArrayList<Player>)getPlayerFrom(Team.DETECTIVE);
			for(Player p : d){
				PlayerScoreboard ps = new PlayerScoreboard(p);
				ps.addBoard(DisplaySlot.SIDEBAR, C.cBlue+"DetectiveBoard");
				ps.setScore(C.cGreen+"Karma", DisplaySlot.SIDEBAR, getManager().getStats().getInt(Stats.TTT_KARMA, p));
				ps.setScore(C.cAqua+"D-Punkte:", DisplaySlot.SIDEBAR, getManager().getStats().getInt(Stats.TTT_DETECTIVE_PUNKTE, p));
				if(d.size()!=1){
					ps.setScore("§7", DisplaySlot.SIDEBAR, -1);
					ps.setScore(C.cBlue+"Detective:", DisplaySlot.SIDEBAR, -2);
					int t = -3;
					for(Player p1 : d){
						if(p1==p)continue;
						ps.setScore(p1.getName(), DisplaySlot.SIDEBAR, t);
						t--;
					}
				}
				ps.setBoard();
				boards.put(p, ps);
				p.getInventory().setChestplate(UtilItem.LSetColor(new ItemStack(Material.LEATHER_CHESTPLATE), Color.BLUE));
				p.getInventory().addItem(UtilItem.RenameItem(new ItemStack(Material.STICK), "Detective-Shop"));
				setDamage(true);
				setProjectileDamage(true);
			}
			ArrayList<Player> t = (ArrayList<Player>)getPlayerFrom(Team.TRAITOR);
			for(Player p : t){
				p.getInventory().addItem(UtilItem.RenameItem(new ItemStack(Material.STICK), "Traitor-Shop"));
				PlayerScoreboard ps = new PlayerScoreboard(p);
				ps.addBoard(DisplaySlot.SIDEBAR, C.cDRed+"TraitorBoard");
				ps.setScore(C.cGreen+"Karma:", DisplaySlot.SIDEBAR, getManager().getStats().getInt(Stats.TTT_KARMA, p));
				ps.setScore(C.cRed+"T-Punkte:", DisplaySlot.SIDEBAR, getManager().getStats().getInt(Stats.TTT_TRAITOR_PUNKTE, p));
				if(d.size()!=1){
					ps.setScore("§7", DisplaySlot.SIDEBAR, -1);
					ps.setScore(C.cRed+"Traitor:", DisplaySlot.SIDEBAR, -2);
					int t1 = -3;
					for(Player p1 : d){
						if(p1==p)continue;
						ps.setScore(p1.getName(), DisplaySlot.SIDEBAR, t1);
						t1--;
					}
				}
				
				ps.setBoard();
				boards.put(p, ps);
			}
			
			for(Player p : t){
				PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment(p.getEntityId(),2, CraftItemStack.asNMSCopy( UtilItem.LSetColor(new ItemStack(Material.LEATHER_CHESTPLATE), Color.RED) ));
				for(Player p1 : t){
					((CraftPlayer)p1).getHandle().playerConnection.sendPacket(packet);
				}
			}
			
			ArrayList<Player> i = (ArrayList<Player>) getPlayerFrom(Team.INOCCENT);
			
			for(Player p:i){
				PlayerScoreboard ps = new PlayerScoreboard(p);
				ps.addBoard(DisplaySlot.SIDEBAR, C.cRed+"InnocentBoard");
				ps.setScore(C.cGreen+"Karma:", DisplaySlot.SIDEBAR, getManager().getStats().getInt(Stats.TTT_KARMA, p));
				ps.setBoard();
				boards.put(p, ps);
			}
			
			getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.SCHUTZZEIT_END.getText());
			getManager().setState(GameState.InGame);
			break;
		}
	}
	
	@EventHandler
	public void World(WorldLoadEvent ev){
		HashMap<String,ArrayList<Location>> list = getManager().getWorldData().getLocs();
		tester = new Tester(this, list.get(Team.BLUE.Name()).get(0),list.get(Team.GREEN.Name()).get(0), list.get(Team.SOLO.Name()), list.get(Team.GRAY.Name()));
	}
	
	@EventHandler
	public void Regen(EntityRegainHealthEvent ev){
		if(manager.getState()==GameState.LobbyPhase)return;
		if(ev.getRegainReason()==RegainReason.SATIATED)ev.setCancelled(true);
	}
	
	@EventHandler
	public void Start(GameStartEvent ev){
		ArrayList<Location> list = getManager().getWorldData().getLocs(Team.RED.Name());
		int r=0;
		for(Player p : UtilServer.getPlayers()){
			getManager().Clear(p);
			p.setMaxHealth(40);
			p.setHealth(40);
			r=UtilMath.r(list.size());
			p.teleport(list.get(r));
			list.remove(r);
			getGameList().addPlayer(p,PlayerState.IN);
		}
		ifm=new ItemFakeManager(getManager().getInstance(),hm);
		setItemFake(getManager().getWorldData().getLocs(Team.YELLOW.Name()));
		setDamage(false);
		setProjectileDamage(false);
		getManager().setStart(31);
		getManager().setState(GameState.StartGame);
	}

	public HashMap<Team,Integer> verteilung(){
		HashMap<Team,Integer> list = new HashMap<>();
		list.put(Team.DETECTIVE, getDetective());
		list.put(Team.TRAITOR, getTraitor());
		list.put(Team.INOCCENT,UtilServer.getPlayers().length-(getTraitor()+getDetective()));
		
		return list;
	}
	
	public int getTraitor(){
		switch(UtilServer.getPlayers().length){
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
		switch(UtilServer.getPlayers().length){
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
