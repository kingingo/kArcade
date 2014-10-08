package me.kingingo.karcade.Game.Games.EnderGames;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.Getter;
import me.kingingo.karcade.kArcade;
import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Enum.PlayerState;
import me.kingingo.karcade.Events.RankingEvent;
import me.kingingo.karcade.Game.Games.SoloGame;
import me.kingingo.karcade.Game.Games.EnderGames.Addon.AddonPlayerTeleport;
import me.kingingo.karcade.Game.World.WorldData;
import me.kingingo.karcade.Game.addons.AddonQuadratGrenze;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.Game.Events.GameStartEvent;
import me.kingingo.kcore.Hologram.Hologram;
import me.kingingo.kcore.Kit.Kit;
import me.kingingo.kcore.Kit.KitType;
import me.kingingo.kcore.Kit.Perk;
import me.kingingo.kcore.Kit.Perks.PerkArrowFire;
import me.kingingo.kcore.Kit.Perks.PerkEquipment;
import me.kingingo.kcore.Kit.Perks.PerkHolzfäller;
import me.kingingo.kcore.Kit.Perks.PerkNoExplosionDamage;
import me.kingingo.kcore.Kit.Perks.PerkNoFiredamage;
import me.kingingo.kcore.Kit.Perks.PerkNoHunger;
import me.kingingo.kcore.Kit.Perks.PerkNoKnockback;
import me.kingingo.kcore.Kit.Perks.PerkPoisen;
import me.kingingo.kcore.Kit.Perks.PerkSneakDamage;
import me.kingingo.kcore.Kit.Perks.PerkTNT;
import me.kingingo.kcore.Kit.Shop.KitShop;
import me.kingingo.kcore.Permission.Permission;
import me.kingingo.kcore.PlayerStats.Stats;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.C;
import me.kingingo.kcore.Util.InventorySize;
import me.kingingo.kcore.Util.TimeSpan;
import me.kingingo.kcore.Util.UtilDisplay;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilLocation;
import me.kingingo.kcore.Util.UtilMath;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.kcore.Util.UtilTime;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.google.common.collect.Lists;

public class EnderGames extends SoloGame{

	@Getter
	private WorldData worldData;
	@Getter
	private kArcadeManager manager;
	@Getter
	private Location center;
	@Getter
	private AddonQuadratGrenze grenze;
	@Getter
	private HashMap<Location,Inventory> chest = new HashMap<>();
	private HashMap<Location,Long> chest_time = new HashMap<>();
	private int chest_anzahl=120;
	
	private ArrayList<ItemStack> selten = new ArrayList<>();
	private ArrayList<ItemStack> medium = new ArrayList<>();
	private ArrayList<ItemStack> normal = new ArrayList<>();
	
	private KitShop kitShop;
	
	private Hologram hm;
	
	private int minZ;
	private int maxZ;
	private int minX;
	private int maxX;
	
	public EnderGames(kArcadeManager manager) {
		super(manager);
		long t = System.currentTimeMillis();
		manager.setTyp(GameType.EnderGames);
		manager.setState(GameState.Laden);
		this.manager=manager;
		this.worldData=new WorldData(manager,GameType.EnderGames.name());
		manager.setWorldData(getWorldData());
		setupItems();
		setCreatureSpawn(true);
		setMin_Players(1);
		setMax_Players(8);
		setDamage(false);
		setDamagePvP(true);
		setDamageEvP(false);
		setDamagePvE(true);
		setDamageSelf(true);
		setDeathDropItems(true);
		setBlockBreak(true);
		setBlockPlace(true);
		setItemDrop(true);
		setFoodChange(true);
		setCompassAddon(true);
		getWorldData().createWorld();
		getWorldData().loadBiomes();
		getWorldData().setMapName( ((String)getWorldData().getBiomes().keySet().toArray()[UtilMath.r(getWorldData().getBiomes().size())]) );
		this.center=getWorldData().getBiomes().get(getWorldData().getMapName());
		grenze=new AddonQuadratGrenze(manager,getCenter(),0);
		this.kitShop=new KitShop(getManager().getInstance(), getCoins(),getTokens(), getManager().getPermManager(), "Kit-Shop", InventorySize._27, new Kit[]{
			new Kit( "§aBogenschütze",new String[]{"Der Bogenschütze startet mit ","einem Bogen und 4 Pfeilen.","30% Chance das der Pfeil brennt!"}, new ItemStack(Material.BOW),Permission.SHEEPWARS_KIT_STARTER,KitType.STARTER,2000,new Perk[]{
				new PerkArrowFire(30),
				new PerkEquipment(new ItemStack[]{new ItemStack(Material.BOW,1),new ItemStack(Material.ARROW,4)})
			}),
			new Kit( "§aAnker",new String[]{"Der Anker bekommt kein Rückstoß."}, new ItemStack(Material.ANVIL),Permission.SHEEPWARS_KIT_STARTER,KitType.STARTER,2000,new Perk[]{
				new PerkNoKnockback(manager.getInstance())
			}),
			new Kit( "§aBerserker",new String[]{"Der Berserker startet mit","einem Gold Schwert und","Gold Chestplate."}, new ItemStack(Material.IRON_SWORD),Permission.SHEEPWARS_KIT_STARTER,KitType.STARTER,2000,new Perk[]{
				new PerkEquipment(new ItemStack[]{new ItemStack(Material.GOLD_SWORD,1), new ItemStack(Material.GOLD_CHESTPLATE)})
			}),
			new Kit( "§aBomber",new String[]{"Der Bomber bekommt kein Explosion Schaden und","TNT zündet direkt beim setzten."}, new ItemStack(Material.TNT),Permission.SHEEPWARS_KIT_STARTER,KitType.STARTER,2000,new Perk[]{
				new PerkNoExplosionDamage(),
				new PerkTNT()
			}),
			new Kit( "§aRitter",new String[]{"Der Ritter startet mit,","einen Holzschwert und ","einer Workbench"}, new ItemStack(Material.WOOD_SWORD),Permission.SHEEPWARS_KIT_STARTER,KitType.STARTER,2000,new Perk[]{
				new PerkEquipment(new ItemStack[]{new ItemStack(Material.WOOD_SWORD,1),new ItemStack(Material.WORKBENCH,1)}),
			}),
			new Kit( "§aFireman",new String[]{"Der Fireman bekommt kein Feuerschaden."}, new ItemStack(Material.FIRE),Permission.SHEEPWARS_KIT_STARTER,KitType.STARTER,2000,new Perk[]{
				new PerkNoFiredamage()
			}),
			new Kit( "§aHolzfäller",new String[]{"Der Holzfäller kann schnell","Baeume abbauen."}, new ItemStack(Material.WOOD_AXE),Permission.SHEEPWARS_KIT_STARTER,KitType.STARTER,2000,new Perk[]{
				new PerkHolzfäller()
			}),
			new Kit( "§aPanzer",new String[]{"Der Panzer bekommt beim Sneaken","höchstens 1 Herz schaden","wenn er angegriffen wird."}, new ItemStack(Material.DIAMOND_CHESTPLATE),Permission.SHEEPWARS_KIT_STARTER,KitType.STARTER,2000,new Perk[]{
				new PerkSneakDamage(2)
			}),
			new Kit( "§aSkorpion",new String[]{"Der Skorpion hat die 30% Chance","wenn er einen Spieler","schlägt das dieser Vergift ist","5 sekunden lang."}, new ItemStack(Material.POTION),Permission.SHEEPWARS_KIT_STARTER,KitType.STARTER,2000,new Perk[]{
				new PerkPoisen(5, 30)
			}),
			new Kit( "§aSoldat",new String[]{"Der Soldat startet mit","einem Holzschwert und einer Lederrüstung"}, new ItemStack(Material.LEATHER_HELMET),Permission.SHEEPWARS_KIT_STARTER,KitType.STARTER,2000,new Perk[]{
				new PerkEquipment(new ItemStack[]{new ItemStack(Material.WOOD_SWORD,1), new ItemStack(Material.LEATHER_HELMET,1), new ItemStack(Material.LEATHER_CHESTPLATE,1), new ItemStack(Material.LEATHER_LEGGINGS,1), new ItemStack(Material.LEATHER_BOOTS)})
			}),
			new Kit( "§aVersorger",new String[]{"Der Versorger bekommt kein Hunger."}, new ItemStack(Material.BAKED_POTATO),Permission.SHEEPWARS_KIT_STARTER,KitType.STARTER,2000,new Perk[]{
				new PerkNoHunger()
			}),
		});
		new AddonPlayerTeleport(this);
		manager.setState(GameState.LobbyPhase);
		manager.DebugLog(t, this.getClass().getName());
	}
	
	@EventHandler
	public void ShopOpen(PlayerInteractEvent ev){
		if(getManager().getState()!=GameState.LobbyPhase)return;
		if(UtilEvent.isAction(ev, ActionType.R)){
			if(ev.getPlayer().getItemInHand()!=null&&UtilItem.ItemNameEquals(ev.getPlayer().getItemInHand(), UtilItem.RenameItem(new ItemStack(Material.CHEST), "§bKitShop"))){
				ev.getPlayer().openInventory(kitShop.getInventory());
			}
		}
	}
	
	private void setupItems(){
		selten.add(new ItemStack(Material.DIAMOND, 1));
		selten.add(new ItemStack(Material.DIAMOND, 2));
		selten.add(new ItemStack(Material.DIAMOND, 3));
		selten.add(new ItemStack(Material.GOLDEN_APPLE, 1));
		selten.add(new ItemStack(Material.GOLDEN_APPLE, 2));
		selten.add(new ItemStack(Material.ENDER_PEARL, 3));
		selten.add(new ItemStack(Material.STONE_SWORD, 1));
		selten.add(new ItemStack(Material.CAKE, 2));
		selten.add(new ItemStack(Material.EXP_BOTTLE, 4));
		selten.add(new ItemStack(Material.EXP_BOTTLE, 2));
		selten.add(new ItemStack(Material.TNT, 2));
		selten.add(new ItemStack(Material.FIRE, 4));
		selten.add(new ItemStack(Material.FLINT_AND_STEEL, 1));
		medium.add(new ItemStack(Material.IRON_INGOT, 3));
		medium.add(new ItemStack(Material.IRON_INGOT, 5));
		medium.add(new ItemStack(Material.IRON_INGOT, 3));
		medium.add(new ItemStack(Material.IRON_INGOT, 4));
		medium.add(new ItemStack(Material.IRON_INGOT, 5));
		medium.add(new ItemStack(Material.IRON_INGOT, 3));
		medium.add(new ItemStack(Material.IRON_INGOT, 3));
		medium.add(new ItemStack(Material.IRON_INGOT, 3));
		medium.add(new ItemStack(Material.GOLD_INGOT, 3));
		medium.add(new ItemStack(Material.GOLD_INGOT, 5));
		medium.add(new ItemStack(Material.GOLD_INGOT, 3));
		medium.add(new ItemStack(Material.GOLD_INGOT, 3));
		medium.add(new ItemStack(Material.GOLD_INGOT, 4));
		medium.add(new ItemStack(Material.GOLD_INGOT, 3));
		medium.add(new ItemStack(Material.GOLD_INGOT, 5));
		medium.add(new ItemStack(Material.GOLD_INGOT, 3));
		medium.add(new ItemStack(Material.GOLD_INGOT, 4));
		medium.add(new ItemStack(Material.ARROW, 4));
		medium.add(new ItemStack(Material.ARROW, 7));
		medium.add(new ItemStack(Material.ARROW, 2));
		medium.add(new ItemStack(Material.ARROW, 1));
		medium.add(new ItemStack(Material.ARROW, 3));
		medium.add(new ItemStack(Material.COOKED_BEEF, 3));
		medium.add(new ItemStack(Material.COOKED_CHICKEN, 2));
		medium.add(new ItemStack(Material.FISHING_ROD, 1));
		medium.add(new ItemStack(Material.COOKIE, 7));
		medium.add(new ItemStack(Material.RAW_CHICKEN, 3));
		medium.add(new ItemStack(Material.RAW_FISH, 5));
		medium.add(new ItemStack(Material.WATER_BUCKET, 1));
		medium.add(new ItemStack(Material.LAVA_BUCKET, 1));
		medium.add(new ItemStack(Material.WEB, 3));
		medium.add(new ItemStack(Material.BOW, 1));
		medium.add(new ItemStack(Material.STICK, 2));
		medium.add(new ItemStack(Material.MUSHROOM_SOUP, 2));
		medium.add(new ItemStack(Material.SNOW_BALL, 7));
		normal.add(new ItemStack(Material.LEATHER, 3));
		normal.add(new ItemStack(Material.EGG, 2));
		normal.add(new ItemStack(Material.STRING, 2));
		normal.add(new ItemStack(Material.APPLE, 1));
		normal.add(new ItemStack(Material.WHEAT, 7));
		normal.add(new ItemStack(Material.BAKED_POTATO, 3));
		normal.add(new ItemStack(Material.BOWL, 3));
		normal.add(new ItemStack(Material.BREAD, 1));
		normal.add(new ItemStack(Material.BROWN_MUSHROOM, 2));
		normal.add(new ItemStack(Material.RED_MUSHROOM, 2));
		normal.add(new ItemStack(Material.CARROT_ITEM, 3));
		normal.add(new ItemStack(Material.GOLD_NUGGET, 4));
		normal.add(new ItemStack(Material.WORKBENCH, 1));
		normal.add(new ItemStack(Material.LADDER, 7));
		normal.add(new ItemStack(Material.ROTTEN_FLESH, 4));
		normal.add(new ItemStack(Material.APPLE, 2));
	}
	
	public Inventory setupInv() {
		Inventory inv = Bukkit.createInventory(null, 9 * 3, "EnderGames");
		ItemStack[] is = new ItemStack[9 * 3];
		for (int ii = 0; ii < UtilMath.r(3) + 2; ii++) {
			if (UtilMath.r(15) == 1) {
				ItemStack added = selten.get(UtilMath.r(selten.size()));
				inv.addItem(added);
				is[ii] = added;
			} else if (UtilMath.r(5) == 3 || UtilMath.r(5) == 1) {
				ItemStack added = medium.get(UtilMath.r(medium.size()));
				inv.addItem(added);
				is[ii] = added;
			} else {
				ItemStack added = normal.get(UtilMath.r(normal.size()));
				inv.addItem(added);
				is[ii] = added;
			}
		}
		return inv;
	}
	
	@EventHandler
	public void SpawnChest(UpdateEvent ev){
		if(ev.getType()!=UpdateType.FASTER)return;
		if(getManager().getState()!=GameState.InGame)return;
		if(chest.size()<chest_anzahl){
			spawnChest();
		}
	}
	
	public void spawnChest(){
		Location loc = UtilLocation.getLowestBlock(new Location(getWorldData().getWorld(), UtilMath.RandomInt(maxX, minX), 200, UtilMath.RandomInt(maxZ, minZ)));
		loc.getBlock().setType(Material.ENDER_CHEST);
		chest.put(loc, null);
		chest_time.put(loc, System.currentTimeMillis());
	}
	
	long time;
	Location loc;
	@EventHandler
	public void ChangeChest(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SLOWER)return;
		if(getManager().getState()!=GameState.InGame)return;
		for(int i = 0; i < chest_time.size(); i++){
			loc=(Location)chest_time.keySet().toArray()[i];
			time=chest_time.get(loc);
			if( (time+(TimeSpan.SECOND*45)<System.currentTimeMillis()) ){
				if(chest.get(loc)!=null)for (HumanEntity en : chest.get(loc).getViewers())en.closeInventory();
				loc.getBlock().setType(Material.AIR);
				loc.getWorld().playEffect(loc, Effect.ENDER_SIGNAL, -2);
				chest.remove(loc);
				chest_time.remove(loc);
				spawnChest();
			}
		}
	}
	
	@EventHandler
	public void Open(PlayerInteractEvent ev){
		if(getManager().getState()!=GameState.InGame)return;
		if(getGameList().getPlayers(PlayerState.OUT).contains(ev.getPlayer()))return;
		System.err.println("1");
		if(UtilEvent.isAction(ev, ActionType.R_BLOCK)){
			System.err.println("2");
			if(ev.getClickedBlock().getType()==Material.ENDER_CHEST){
				System.err.println("3");
				ev.getPlayer().closeInventory();
				if(chest.containsKey(ev.getClickedBlock().getLocation())){
					System.err.println("4");
					if(chest.get(ev.getClickedBlock().getLocation())==null){
						System.err.println("5");
						chest.remove(ev.getClickedBlock().getLocation());
						chest.put(ev.getClickedBlock().getLocation(), setupInv());
					}
					System.err.println("6");
					ev.getPlayer().openInventory(chest.get(ev.getClickedBlock().getLocation()));
				}
			}
		}
	}
	
	@EventHandler
	public void Ranking(RankingEvent ev){
		getManager().setRanking(Stats.WIN);
	}
	
	@EventHandler
	public void Fallschirm(UpdateEvent ev){
		if(ev.getType()!=UpdateType.FASTER)return;
		for(Player p : getGameList().getPlayers().keySet()){
			if(p.getPassenger()!=null&&p.getPassenger().getType()==EntityType.CHICKEN){
				if(p.isOnGround()){
					p.getPassenger().leaveVehicle();
				}else{
					p.setVelocity(new Vector(p.getVelocity().getX(), p.getVelocity().getY()/4, p.getVelocity().getZ()));
				}
			}
		}
	}
	
	@EventHandler
	public void inGame(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		if(getManager().getState()!=GameState.InGame)return;
		getManager().setStart(getManager().getStart()-1);
		for(Player p : UtilServer.getPlayers())UtilDisplay.displayTextBar(Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())), p);
		switch(getManager().getStart()){
		case 895: for(Player p : getGameList().getPlayers(PlayerState.IN))p.setPassenger(p.getWorld().spawnEntity(p.getLocation(),EntityType.CHICKEN));break;
		case 850: setDamage(true);
		case 30: getManager().broadcast( Text.PREFIX_GAME.getText(getManager().getTyp().name())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())) );break;
		case 15: getManager().broadcast( Text.PREFIX_GAME.getText(getManager().getTyp().name())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())) );break;
		case 10: getManager().broadcast( Text.PREFIX_GAME.getText(getManager().getTyp().name())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())) );break;
		case 5: getManager().broadcast( Text.PREFIX_GAME.getText(getManager().getTyp().name())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())) );break;
		case 4: getManager().broadcast( Text.PREFIX_GAME.getText(getManager().getTyp().name())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())) );break;
		case 3: getManager().broadcast( Text.PREFIX_GAME.getText(getManager().getTyp().name())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())) );break;
		case 2: getManager().broadcast( Text.PREFIX_GAME.getText(getManager().getTyp().name())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())) );break;
		case 1: getManager().broadcast( Text.PREFIX_GAME.getText(getManager().getTyp().name())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())) );break;
		case 0:
			getManager().broadcast( Text.PREFIX_GAME.getText(getManager().getTyp().name())+Text.GAME_END.getText() );
			getManager().setState(GameState.Restart);
			break;
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void JoinHologram(PlayerJoinEvent ev){
		if(getManager().getState()!=GameState.LobbyPhase)return;
		if(hm==null)hm=new Hologram(getManager().getInstance());

		int win = getManager().getStats().getInt(Stats.WIN, ev.getPlayer());
		int lose = getManager().getStats().getInt(Stats.LOSE, ev.getPlayer());
		getManager().getLoc_stats().getWorld().loadChunk(getManager().getLoc_stats().getWorld().getChunkAt(getManager().getLoc_stats()));
		hm.sendText(ev.getPlayer(),getManager().getLoc_stats().clone().add(0, 0.1, 0),new String[]{
		C.cGreen+getManager().getTyp().getTyp()+C.mOrange+C.Bold+" Info",
		"Server: EnderGames §a"+kArcade.id,
		"Biom: "+getWorldData().getMapName(),
		" ",
		C.cGreen+getManager().getTyp().getTyp()+C.mOrange+C.Bold+" Stats",
		"Kills: "+getManager().getStats().getInt(Stats.KILLS, ev.getPlayer()),
		"Tode: "+getManager().getStats().getInt(Stats.DEATHS, ev.getPlayer()),
		" ",
		"Gespielte Spiele: "+(win+lose),
		"Gewonnene Spiele: "+win,
		"Verlorene Spiele: "+lose
		});
		ev.getPlayer().getInventory().addItem(UtilItem.RenameItem(new ItemStack(Material.CHEST), "§bKitShop"));
	}
	
	@EventHandler
	public void Start(GameStartEvent ev){
		long time = System.currentTimeMillis();
		getWorldData().getWorld().setStorm(false);
		chest_anzahl=UtilServer.getPlayers().length*10;
		grenze.setRadius( (UtilServer.getPlayers().length*20) );
		grenze.scan();
		
		minZ=grenze.MinZ()+5;
		maxZ=grenze.MaxZ()-5;
		minX=grenze.MinX()+5;
		maxX=grenze.MaxX()-5;
		System.err.println("X: MAX:"+maxX+" MIN:"+minX);
		System.err.println("Z: MAX:"+maxZ+" MIN:"+minZ);
		
		for(Player p : UtilServer.getPlayers()){
			getManager().Clear(p);
			getGameList().addPlayer(p,PlayerState.IN);
			p.teleport( new Location(getWorldData().getWorld(), UtilMath.RandomInt(maxX, minX), 255, UtilMath.RandomInt(maxZ, minZ)) );
		}
		
		getManager().setStart(60*15);
		getManager().setState(GameState.InGame);
		getManager().DebugLog(time, this.getClass().getName());
	}

}
