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
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.Detective.DetectiveShop;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.Traitor.TraitorShop;
import me.kingingo.karcade.Game.World.WorldData;
import me.kingingo.karcade.Game.addons.SkullNameTag;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.Hologram.Hologram;
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
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilMath;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.kcore.Util.UtilTime;
import net.minecraft.server.v1_7_R4.MinecraftServer;
import net.minecraft.util.com.mojang.authlib.GameProfile;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;

public class TroubleInMinecraft extends TeamGame{
	
	WorldData wd;
	@Getter
	private kArcadeManager manager;
	HashMap<Integer,String> npclist = new HashMap<>();
	Tester tester;
	TraitorShop tshop;
	DetectiveShop dshop;
	NPCManager npcManager;
	Hologram hm;
	SkullNameTag snt;
	HashMap<Player,PlayerScoreboard> boards = new HashMap<>();
	//HashMap<Integer,NPC> NPCList = new HashMap<>();
	
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
		getManager().getCmd().register(CommandDetective.class, new CommandDetective(this));
		getManager().getCmd().register(CommandTraitor.class, new CommandTraitor(this));
		npcManager= new NPCManager(getManager().getInstance());
		tshop = new TraitorShop(this);
		dshop = new DetectiveShop(this);
		wd = new WorldData(manager,GameType.TroubleInMinecraft.name());
		wd.Initialize();
		manager.setWorldData(wd);
		
//		minigun= new Weapon(manager.getInstance(), WeaponTyp.MACHINE_GUNS,new ItemStack(Material.ARROW),TTT_Item.BOW_MINIGUN.getItem(),TimeSpan.SECOND*5, 500,32,0.1,1,1,"�7MiniGun");
//		
//		sniper = new Weapon(manager.getInstance(), WeaponTyp.SNIPER, new ItemStack(Material.ARROW), TTT_Item.BOW_SNIPER.getItem(),TimeSpan.SECOND*5, TimeSpan.SECOND*3,5,0.3,1,4,"�7Sniper");
//		
//		shotgun = new Weapon(manager.getInstance(), WeaponTyp.SHOTGUN, new ItemStack(Material.ARROW), TTT_Item.BOW_SHOTGUN.getItem(),TimeSpan.SECOND*5,TimeSpan.SECOND*2,7,0.15,1,1,"�7Shotgun");

		manager.setState(GameState.LobbyPhase);
		manager.DebugLog(t, 84, this.getClass().getName());
	}

//	Ein Innocent t�tet einen Traitor: +20 Karma
//	Ein Innocent t�tet einen Innocent: -20 Karma
//	Ein Innocent t�tet einen Detective: -50 Karma
//	Ein Detective t�tet einen Traitor: +30 Karma und 2 Detective-Punkte
//	Ein Detective t�tet einen Innocent: -20 Karma
//	Ein Detective t�tet einen Detective: -50 Karma
//	Ein Traitor t�tet einen Innocent: +10 Karma und 2 Traitor-Punkte
//	Ein Traitor t�tet einen Detective: +20 Karma und 4 Traitor-Punkte
//	Ein Traitor t�tet einen Traitor: -50 Karma
	
//	Shops:
	
//	Traitor-Shop:
//	Medipack (1 Punkt): Heil 2,5 Herzen
//	Radar (1 Punkt): Zeigt auf einen ausgew�hlten Spieler.
//	Creeper Arrows (2 Punkte): Aus geschossenen Pfeilen spawnen Creeper
//	Disguiser (3 Punkte): Versteckt den eigenen Name
//	Tester-Spoofer (4 Punkte): Traitoren werden zu 75% im Tester also Innocent angezeigt.
//	Fake-Chest (4 Punkte): Wenn man dieses Fake-Item aufnimmt stirbt man sofort.
//	Teleporter (4 Punkte): Erm�glicht die einmalige teleportation zu einem vorher festgelegten Punkt.
//	C4 (5 Punkte): Ein TNT-Block der mit einer Redstone-Fackel aktiviert werden kann.
//	Knife (6 Punkte): Erm�glicht einmalig das sofortige t�ten eines Spielers von hinten.
//	JihadBomb (7 Punkte): L�sst alle Spieler um einen selbst explodieren.
//	FireGrenade (7 Punkte): Erzeugt ein gro�es Feuer, nachdem sie explodiert ist.
//	SmokeGrenade (7 Punkte): Erzeugt eine gro�e Rauchwolke nach der Explosion.
	
//	Detective-Shop:
//	Radar (1 Punkt): Zeigt auf einen ausgew�hlten Spieler.
//	DNA-Test (2 Punkte): Untersucht Leichen auf genauere Informationen.
//	Healing-Station (2 Punkte): Heilt andere Spieler in der n�he der Heal-Station.
//	Fluffy (3 Punkte): Ein Hund, der in der N�he von Traitoren knurrt.
//	Golden Weapon (5 Punkte): Ein Goldschwert, welches einmalig einen Traitor mit einem Schlag t�tet. Bei Innocents zerbricht es sofort.
	
	//RED BLOCK = PLAYER SPAWN
	//BLUE = BUTTON
	//GREEN = JOIN
	//MELON = LAMPEN
	//GRAY = GLASS
	
	@EventHandler
	public void Chat(PlayerChatEvent ev){
		ev.setCancelled(true);
		Bukkit.broadcastMessage(C.cGray+ev.getPlayer().getDisplayName()+": "+ev.getMessage());
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
	public void Shot(EntityShootBowEvent ev){
		ev.setCancelled(true);
	}
	
	 @EventHandler
	  public void onPlayerInteract(PlayerInteractEvent e) {
	    if (e.getAction() != Action.RIGHT_CLICK_AIR) return;

	    if (e.getItem().getType() != Material.BOW) return;
	    e.setCancelled(true);
	    Arrow a = (Arrow)e.getPlayer().launchProjectile(Arrow.class, e.getPlayer().getEyeLocation().getDirection().multiply(5));
	  }
	
	 @EventHandler
		public void Aufdecken(PlayerInteractNPCEvent ev){
			NPC npc = ev.getNpc();
			if(!npc.getName().equalsIgnoreCase("Unidentifiziert"))return;
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
		}
	
	@EventHandler
	public void DeathTTT(PlayerDeathEvent ev){
		if(ev.getEntity() instanceof Player&&getGameList().getPlayers(PlayerState.IN).contains( ((Player)ev.getEntity()) )){
			getManager().getStats().setInt(((Player)ev.getEntity()),getManager().getStats().getInt(Stats.DEATHS, ((Player)ev.getEntity()))+1, Stats.DEATHS);
			getGameList().addPlayer( ((Player)ev.getEntity()) , PlayerState.OUT);
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
				if(t==Team.TRAITOR){
					if(t1==Team.TRAITOR){
						getManager().getStats().setInt( ((Player)ev.getEntity().getKiller()) , k-50, Stats.TTT_KARMA);
						k=-50;
					}else if(t1==Team.INOCCENT){
						getManager().getStats().setInt( ((Player)ev.getEntity().getKiller()) , k+10, Stats.TTT_KARMA);
						getManager().getStats().setInt( ((Player)ev.getEntity().getKiller()) ,getManager().getStats().getInt(Stats.TTT_TRAITOR_PUNKTE, (Player)ev.getEntity().getKiller())+2, Stats.TTT_TRAITOR_PUNKTE);
						k=10;
						tr=2;
					}else if(t1==Team.DETECTIVE){
						getManager().getStats().setInt( ((Player)ev.getEntity().getKiller()) , k+20, Stats.TTT_KARMA);
						getManager().getStats().setInt( ((Player)ev.getEntity().getKiller()) ,getManager().getStats().getInt(Stats.TTT_TRAITOR_PUNKTE, (Player)ev.getEntity().getKiller())+4, Stats.TTT_TRAITOR_PUNKTE);
						k=20;
						tr=4;
					}
				}else if(t==Team.INOCCENT){
					if(t1==Team.TRAITOR){
						getManager().getStats().setInt( ((Player)ev.getEntity().getKiller()) , k+20, Stats.TTT_KARMA);
						k=20;
					}else if(t1==Team.INOCCENT){
						getManager().getStats().setInt( ((Player)ev.getEntity().getKiller()) , k-20, Stats.TTT_KARMA);
						k=-20;
					}else if(t1==Team.DETECTIVE){
						getManager().getStats().setInt( ((Player)ev.getEntity().getKiller()) , k-50, Stats.TTT_KARMA);
						k=-50;
					}
				}else if(t==Team.DETECTIVE){
					if(t1==Team.TRAITOR){
						getManager().getStats().setInt( ((Player)ev.getEntity().getKiller()) , k+30, Stats.TTT_KARMA);
						getManager().getStats().setInt( ((Player)ev.getEntity().getKiller()) ,getManager().getStats().getInt(Stats.TTT_DETECTIVE_PUNKTE, (Player)ev.getEntity().getKiller())+2, Stats.TTT_DETECTIVE_PUNKTE);
						k=30;
						d=2;
					}else if(t1==Team.INOCCENT){
						getManager().getStats().setInt( ((Player)ev.getEntity().getKiller()) , k-20, Stats.TTT_KARMA);
						k=-20;
					}else if(t1==Team.DETECTIVE){
						getManager().getStats().setInt( ((Player)ev.getEntity().getKiller()) , k-50, Stats.TTT_KARMA);
						k=-50;
					}
				}
			
				if(d!=-1){
					if(k>0){
						hm.sendText(ev.getEntity().getKiller(), ev.getEntity().getLocation().add(0,2,0), 3, new String[]{
							"�aDu hast �e"+k+" Karma�a erhalten.",
							"�bDu hast �e"+tr+" Detective-Punke�b erhalten."
						});
					}else{
						hm.sendText(ev.getEntity().getKiller(), ev.getEntity().getLocation().add(0,2,0), 3, new String[]{
							 "�cDu hast �e"+k+" Karma�c verloren.",
							 "�bDu hast �e"+tr+" Detective-Punke�b erhalten."
						});
					}
				}else if(tr!=-1){
					if(k>0){
						hm.sendText(ev.getEntity().getKiller(), ev.getEntity().getLocation().add(0,2,0), 3, new String[]{
							"�aDu hast �e"+k+" Karma�a erhalten.",
							"�bDu hast �e"+tr+" Traitor-Punke�b erhalten."
						});
					}else{
						hm.sendText(ev.getEntity().getKiller(), ev.getEntity().getLocation().add(0,2,0), 3, new String[]{
							 "�cDu hast �e"+k+" Karma�c verloren.",
							 "�bDu hast �e"+tr+" Traitor-Punke�b erhalten."
						});
					}
				}else{
					if(k>0){
						hm.sendText(ev.getEntity().getKiller(), ev.getEntity().getLocation().add(0,2,0), 3, "�aDu hast �e"+k+" Karma�a erhalten.");
					}else{
						hm.sendText(ev.getEntity().getKiller(), ev.getEntity().getLocation().add(0,2,0), 3, "�cDu hast �e"+k+" Karma�c verloren.");
					}
				}
				
			}
			
			delTeam(((Player)ev.getEntity()));
		}
	}
	
	
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void Items(PlayerInteractEvent ev){
		if(UtilEvent.isAction(ev, ActionType.R_BLOCK)&& !ev.isCancelled() ){
			if(ev.getClickedBlock().getState() instanceof Skull){
				TTT_Item t = getSkull(ev.getClickedBlock());
				if(t==null)return;
				boolean b = false;
				Inventory inv = ev.getPlayer().getInventory();
				ev.getClickedBlock().setType(Material.AIR);
				if(t.getN().equalsIgnoreCase(TTT_Item.SCHWERT_HOLZ.getN())){
					if(inv.contains(TTT_Item.SCHWERT_HOLZ.getItem())){
						for(int i = 0 ; i < inv.getSize(); i++){
							if(inv.getItem(i)==null||inv.getItem(i).getType()==Material.AIR)continue;
							if(inv.getItem(i).getType()==TTT_Item.SCHWERT_HOLZ.getItem().getType()){
								inv.setItem(i, t.getItem());
								b=true;
								TTT_Item.SCHWERT_HOLZ.setBlock(ev.getClickedBlock());
							}
						}
					}else if(inv.contains(TTT_Item.SCHWERT_IRON.getItem())){
						for(int i = 0 ; i < inv.getSize(); i++){
							if(inv.getItem(i)==null||inv.getItem(i).getType()==Material.AIR)continue;
							if(inv.getItem(i).getType()==TTT_Item.SCHWERT_IRON.getItem().getType()){
								inv.setItem(i, t.getItem());
								b=true;
								TTT_Item.SCHWERT_IRON.setBlock(ev.getClickedBlock());
							}
						}
					}else if(inv.contains(TTT_Item.SCHWERT_STONE.getItem())){
						for(int i = 0 ; i < inv.getSize(); i++){
							if(inv.getItem(i)==null||inv.getItem(i).getType()==Material.AIR)continue;
							if(inv.getItem(i).getType()==TTT_Item.SCHWERT_STONE.getItem().getType()){
								inv.setItem(i, t.getItem());
								b=true;
								TTT_Item.SCHWERT_STONE.setBlock(ev.getClickedBlock());
							}
						}
					}
				}else if(t.getN().equalsIgnoreCase(TTT_Item.BOW_BOGEN.getN())){
					if(inv.contains(TTT_Item.BOW_BOGEN.getItem())){
						for(int i = 0 ; i < inv.getSize(); i++){
							if(inv.getItem(i)==null||inv.getItem(i).getType()==Material.AIR)continue;
							if(inv.getItem(i).getType()==TTT_Item.BOW_BOGEN.getItem().getType()){
								inv.setItem(i, t.getItem());
								b=true;
								TTT_Item.BOW_BOGEN.setBlock(ev.getClickedBlock());
							}
						}
					}else if(inv.contains(TTT_Item.BOW_MINIGUN.getItem())){
						for(int i = 0 ; i < inv.getSize(); i++){
							if(inv.getItem(i)==null||inv.getItem(i).getType()==Material.AIR)continue;
							if(inv.getItem(i).getType()==TTT_Item.BOW_MINIGUN.getItem().getType()){
								ev.getPlayer().getInventory().setItem(i,t.getItem());
								b=true;
								TTT_Item.BOW_MINIGUN.setBlock(ev.getClickedBlock());
							}
						}
					}if(inv.contains(TTT_Item.BOW_SHOTGUN.getItem())){
						for(int i = 0 ; i < inv.getSize(); i++){
							if(inv.getItem(i)==null||inv.getItem(i).getType()==Material.AIR)continue;
							if(inv.getItem(i).getType()==TTT_Item.BOW_SHOTGUN.getItem().getType()){
								ev.getPlayer().getInventory().setItem(i,t.getItem());
								b=true;
								TTT_Item.BOW_SHOTGUN.setBlock(ev.getClickedBlock());
							}
						}
					}if(inv.contains(TTT_Item.BOW_SNIPER.getItem())){
						for(int i = 0 ; i < inv.getSize(); i++){
							if(inv.getItem(i)==null||inv.getItem(i).getType()==Material.AIR)continue;
							if(inv.getItem(i).getType()==TTT_Item.BOW_SNIPER.getItem().getType()){
								ev.getPlayer().getInventory().setItem(i,t.getItem());
								b=true;
								TTT_Item.BOW_SNIPER.setBlock(ev.getClickedBlock());
							}
						}
					}
				}
				
				
				if(!b)ev.getPlayer().getInventory().addItem(t.getItem());
				ev.getPlayer().updateInventory();
			}
		}
	}
	
	public TTT_Item getSkull(Block b){
		if(b.getState() instanceof Skull){
			Skull s = (Skull)b.getState();
			if(!s.hasOwner())return null;
			switch(s.getOwner()){
			case "VareidePlays": return TTT_Item.SCHWERT_HOLZ;
			case "Nottrex": return TTT_Item.SCHWERT_STONE;
			case "BillTheBuild3r": return TTT_Item.SCHWERT_IRON;
			
			case "KlausurThaler144":return TTT_Item.BOW_MINIGUN;
			case "IntelliJ":return TTT_Item.BOW_SHOTGUN;
			case "Abmahnung":return TTT_Item.BOW_BOGEN;
			case "FallingDiamond":return TTT_Item.BOW_SNIPER;
			}
		}
		return null;
	}
	
	public void setSkull(ArrayList<Location> list){
		int s_h=(int)(list.size()*0.2);// 20 %
		int s_s=(int)(list.size()*0.15);// 15 %
		int s_i=(int)(list.size()*0.05);// 5 %
		
		int b_mg=(int)(list.size()*0.1);// 10%
		int b_s=(int)(list.size()*0.15);// 15%
		int b_b=(int)(list.size()*0.25);// 25%
		int b_sn=(int)(list.size()*0.10);// 10%
		
		System.out.println("S_H "+s_h);
		System.out.println("S_s "+s_s);
		System.out.println("S_i "+s_i);
		
		System.out.println("b_mg "+b_mg);
		System.out.println("b_s "+b_s);
		System.out.println("b_b "+b_b);
		System.out.println("b_sn "+b_sn);	
		
		int r;
		for(int i=0; i < 20000; i++){
			if(list.isEmpty())break;
			r=UtilMath.r(list.size());
			if(s_h!=0){
				TTT_Item.SCHWERT_HOLZ.setBlock(list.get(r));
				list.remove(r);
				s_h--;
			}else if(s_s!=0){
				TTT_Item.SCHWERT_STONE.setBlock(list.get(r));
				list.remove(r);
				s_s--;
			}else if(s_i!=0){
				TTT_Item.SCHWERT_IRON.setBlock(list.get(r));
				list.remove(r);
				s_i--;
			}else if(b_mg!=0){
				TTT_Item.BOW_MINIGUN.setBlock(list.get(r));
				list.remove(r);
				b_mg--;
			}else if(b_s!=0){
				TTT_Item.BOW_SHOTGUN.setBlock(list.get(r));
				list.remove(r);
				b_s--;
			}else if(b_b!=0){
				TTT_Item.BOW_BOGEN.setBlock(list.get(r));
				list.remove(r);
				b_b--;
			}else if(b_sn!=0){
				TTT_Item.BOW_SNIPER.setBlock(list.get(r));
				list.remove(r);
				b_sn--;
			}else{
				if(UtilMath.r(1)==0){
					TTT_Item.SCHWERT_HOLZ.setBlock(list.get(r));
				}else{
					TTT_Item.BOW_BOGEN.setBlock(list.get(r));
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
		hm.sendText(ev.getPlayer(),getManager().getLoc_stats().add(0, 0.78, 0),new String[]{
		C.cGreen+getManager().getTyp().string()+C.mOrange+C.Bold+" Info",
		"Server: TroubleInMinecraft �a"+kArcade.id,
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
					ps.setScore("�7", DisplaySlot.SIDEBAR, -1);
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
			}
			ArrayList<Player> t = (ArrayList<Player>)getPlayerFrom(Team.TRAITOR);
			for(Player p : t){
				p.getInventory().addItem(UtilItem.RenameItem(new ItemStack(Material.STICK), "Traitor-Shop"));
				PlayerScoreboard ps = new PlayerScoreboard(p);
				ps.addBoard(DisplaySlot.SIDEBAR, C.cDRed+"TraitorBoard");
				ps.setScore(C.cGreen+"Karma:", DisplaySlot.SIDEBAR, getManager().getStats().getInt(Stats.TTT_KARMA, p));
				ps.setScore(C.cRed+"T-Punkte:", DisplaySlot.SIDEBAR, getManager().getStats().getInt(Stats.TTT_TRAITOR_PUNKTE, p));
				if(d.size()!=1){
					ps.setScore("�7", DisplaySlot.SIDEBAR, -1);
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
		tester = new Tester(this, tshop.getTs(),list.get(Team.BLUE.Name()).get(0),list.get(Team.GREEN.Name()).get(0), list.get(Team.SOLO.Name()), list.get(Team.GRAY.Name()));
		setSkull(list.get(Team.YELLOW.Name()));
	}
	
	@EventHandler
	public void Start(GameStartEvent ev){
		getManager().setStart(31);
		getManager().setState(GameState.StartGame);
		ArrayList<Location> list = getManager().getWorldData().getLocs(Team.RED.Name());
		snt=new SkullNameTag(getManager(),hm);
		int r=0;
		for(Player p : UtilServer.getPlayers()){
			getManager().Clear(p);
			r=UtilMath.r(list.size());
			p.teleport(list.get(r));
			list.remove(r);
			getGameList().addPlayer(p,PlayerState.IN);
		}
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
