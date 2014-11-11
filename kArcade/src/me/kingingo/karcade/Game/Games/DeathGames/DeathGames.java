package me.kingingo.karcade.Game.Games.DeathGames;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;

import lombok.Getter;
import me.kingingo.karcade.kArcade;
import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Enum.PlayerState;
import me.kingingo.karcade.Events.RankingEvent;
import me.kingingo.karcade.Game.Events.GameStateChangeEvent;
import me.kingingo.karcade.Game.Games.SoloGame;
import me.kingingo.karcade.Game.Games.DeathGames.Addon.AddonPlayerTeleport;
import me.kingingo.karcade.Game.Games.DeathGames.Perk.PerkTeleporter;
import me.kingingo.karcade.Game.World.WorldData;
import me.kingingo.karcade.Game.addons.AddonQuadratGrenze;
import me.kingingo.karcade.Game.addons.AddonTargetNextPlayer;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.Game.Events.GameStartEvent;
import me.kingingo.kcore.Hologram.Hologram;
import me.kingingo.kcore.Kit.Kit;
import me.kingingo.kcore.Kit.KitType;
import me.kingingo.kcore.Kit.Perk;
import me.kingingo.kcore.Kit.Perks.PerkAngle;
import me.kingingo.kcore.Kit.Perks.PerkArrowFire;
import me.kingingo.kcore.Kit.Perks.PerkArrowInfinity;
import me.kingingo.kcore.Kit.Perks.PerkEnterhacken;
import me.kingingo.kcore.Kit.Perks.PerkEquipment;
import me.kingingo.kcore.Kit.Perks.PerkHeal;
import me.kingingo.kcore.Kit.Perks.PerkHealByHit;
import me.kingingo.kcore.Kit.Perks.PerkHealByKill;
import me.kingingo.kcore.Kit.Perks.PerkHitEffect;
import me.kingingo.kcore.Kit.Perks.PerkHolzfäller;
import me.kingingo.kcore.Kit.Perks.PerkLessDamage;
import me.kingingo.kcore.Kit.Perks.PerkMoreHeart;
import me.kingingo.kcore.Kit.Perks.PerkMoreHearth;
import me.kingingo.kcore.Kit.Perks.PerkNoExplosionDamage;
import me.kingingo.kcore.Kit.Perks.PerkNoFalldamage;
import me.kingingo.kcore.Kit.Perks.PerkNoFiredamage;
import me.kingingo.kcore.Kit.Perks.PerkNoHunger;
import me.kingingo.kcore.Kit.Perks.PerkNoKnockback;
import me.kingingo.kcore.Kit.Perks.PerkPoisen;
import me.kingingo.kcore.Kit.Perks.PerkPotionEffect;
import me.kingingo.kcore.Kit.Perks.PerkPotionEffectByHearth;
import me.kingingo.kcore.Kit.Perks.PerkPotionInWater;
import me.kingingo.kcore.Kit.Perks.PerkRunner;
import me.kingingo.kcore.Kit.Perks.PerkSneakDamage;
import me.kingingo.kcore.Kit.Perks.PerkSnowballSwitcher;
import me.kingingo.kcore.Kit.Perks.PerkTNT;
import me.kingingo.kcore.Kit.Perks.PerkWalkEffect;
import me.kingingo.kcore.Kit.Shop.KitShop;
import me.kingingo.kcore.Permission.Permission;
import me.kingingo.kcore.PlayerStats.Stats;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.C;
import me.kingingo.kcore.Util.InventorySize;
import me.kingingo.kcore.Util.UtilDisplay;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.UtilInv;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilLocation;
import me.kingingo.kcore.Util.UtilMath;
import me.kingingo.kcore.Util.UtilParticle;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.kcore.Util.UtilTime;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;

public class DeathGames extends SoloGame{

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
	private HashMap<Inventory,Location> chest1 = new HashMap<>();
	private int chest_anzahl=120;
	private int extra_chest_anzahl=0;
	
	private ArrayList<ItemStack> selten = new ArrayList<>();
	private ArrayList<ItemStack> medium = new ArrayList<>();
	private ArrayList<ItemStack> normal = new ArrayList<>();
	
	private KitShop kitShop;
	
	private Hologram hm;
	
	private HashMap<Integer,ArrayList<Location>> g = new HashMap<>();
	
	public DeathGames(kArcadeManager manager) {
		super(manager);
		registerListener();
		long t = System.currentTimeMillis();
		setTyp(GameType.DeathGames);
		manager.setState(GameState.Laden);
		this.manager=manager;
		this.worldData=new WorldData(manager,getType());
		setWorldData(getWorldData());
		setupItems();
		setCreatureSpawn(true);
		setMin_Players(4);
		setMax_Players(12);
		setDamage(true);
		setDamagePvP(true);
		setDamageEvP(false);
		setDamagePvE(true);
		setDamageSelf(true);
		setBlockSpread(false);
		setCreatureSpawn(false);
		setDeathDropItems(true);
		setBlockBreak(true);
		setBlockPlace(true);
		setItemDrop(true);
		getBlockBreakDeny().add(Material.ENDER_CHEST);
		getBlockBreakDeny().add(Material.CHEST);
		setFoodChange(true);
		setCompassAddon(true);
		setItemPickup(true);
		getWorldData().createWorld();
		ArrayList<Biome> nobiome = new ArrayList<>();
		nobiome.add(Biome.DEEP_OCEAN);
		nobiome.add(Biome.FROZEN_OCEAN);
		nobiome.add(Biome.STONE_BEACH);
		nobiome.add(Biome.BEACH);
		nobiome.add(Biome.OCEAN);
		nobiome.add(Biome.EXTREME_HILLS);
		nobiome.add(Biome.EXTREME_HILLS_PLUS);
		nobiome.add(Biome.TAIGA_MOUNTAINS);
		nobiome.add(Biome.BIRCH_FOREST_HILLS_MOUNTAINS);
		nobiome.add(Biome.BIRCH_FOREST_MOUNTAINS);
		nobiome.add(Biome.COLD_TAIGA_MOUNTAINS);
		nobiome.add(Biome.DESERT_MOUNTAINS);
		nobiome.add(Biome.EXTREME_HILLS_MOUNTAINS);
		nobiome.add(Biome.EXTREME_HILLS_PLUS_MOUNTAINS);
		nobiome.add(Biome.ICE_MOUNTAINS);
		nobiome.add(Biome.JUNGLE_EDGE_MOUNTAINS);
		nobiome.add(Biome.JUNGLE_MOUNTAINS);
		nobiome.add(Biome.MESA_PLATEAU_FOREST_MOUNTAINS);
		nobiome.add(Biome.MESA_PLATEAU_MOUNTAINS);
		nobiome.add(Biome.ROOFED_FOREST_MOUNTAINS);
		nobiome.add(Biome.MESA_PLATEAU_MOUNTAINS);
		nobiome.add(Biome.SAVANNA_MOUNTAINS);
		nobiome.add(Biome.SAVANNA_PLATEAU_MOUNTAINS);
		nobiome.add(Biome.TAIGA_MOUNTAINS);
		nobiome.add(Biome.SWAMPLAND_MOUNTAINS);
		nobiome.add(Biome.SMALL_MOUNTAINS);
		getWorldData().loadBiomes(nobiome);
		getWorldData().setMapName( ((String)getWorldData().getBiomes().keySet().toArray()[UtilMath.r(getWorldData().getBiomes().size())]) );
		this.center=getWorldData().getBiomes().get(getWorldData().getMapName());
		grenze=new AddonQuadratGrenze(manager,getCenter(),0);
		
		this.kitShop=new KitShop(getManager().getInstance(), getCoins(),getTokens(), getManager().getPermManager(), "Kit-Shop", InventorySize._27, new Kit[]{
			
			new Kit( "§aBogenschütze",new String[]{"Der Bogenschütze startet mit ","einem Bogen und 4 Pfeilen.","30% Chance das der Pfeil brennt!"}, new ItemStack(Material.BOW),Permission.SHEEPWARS_KIT_STARTER,KitType.STARTER,2000,new Perk[]{
				new PerkEquipment(new ItemStack[]{new ItemStack(Material.BOW),new ItemStack(Material.ARROW,4)}),
				new PerkArrowFire(30)
			}),
			new Kit( "§aBogenmeister",new String[]{"Der Bogenschütze startet mit ","einem Bogen und 8 Pfeilen.","60% Chance das der Pfeil brennt!"}, new ItemStack(Material.BOW),Permission.SHEEPWARS_KIT_STARTER,KitType.STARTER,2000,new Perk[]{
				new PerkEquipment(new ItemStack[]{new ItemStack(Material.BOW),new ItemStack(Material.ARROW,8)}),
				new PerkArrowFire(60)
			}),
			new Kit( "§aAnker",new String[]{"Der Anker bekommt kein Rückstoß."}, new ItemStack(Material.ANVIL),Permission.DEATHGAMES_KIT_ANKER,KitType.KAUFEN,2000,new Perk[]{
				new PerkNoKnockback(manager.getInstance())
			}),
			new Kit( "§aBomber",new String[]{"Der Bomber bekommt kein Explosion Schaden und","TNT zündet direkt beim setzten."}, new ItemStack(Material.TNT),Permission.DEATHGAMES_KIT_BOMBER,KitType.KAUFEN,2000,new Perk[]{
				new PerkEquipment(new ItemStack[]{new ItemStack(Material.TNT,2)}),
				new PerkNoExplosionDamage(),
				new PerkTNT()
			}),
			new Kit( "§aFireman",new String[]{"Der Fireman bekommt kein Feuerschaden","und bekommt einen","Lava Eimer und "," ein Feuerzeug."}, new ItemStack(Material.LAVA_BUCKET),Permission.DEATHGAMES_KIT_FIREMAN,KitType.KAUFEN,2000,new Perk[]{
				new PerkNoFiredamage(),
				new PerkEquipment(new ItemStack[]{new ItemStack(Material.LAVA,2),new ItemStack(Material.FLINT_AND_STEEL)})
			}),
			new Kit( "§aSwitcher",new String[]{"Der Switcher hat ein unendlichen Schneeball"," im Inventar welcher ihn mit seinem"," Gegner Tauscht sobald"," er ihn abwirft"}, new ItemStack(Material.SNOW_BALL),Permission.DEATHGAMES_KIT_SWITCHER,KitType.KAUFEN,2000,new Perk[]{
				new PerkSnowballSwitcher()
			}),
			new Kit( "§aHolzfäller",new String[]{"Der Holzfäller kann schnell","Baeume abbauen."}, new ItemStack(Material.WOOD_AXE),Permission.DEATHGAMES_KIT_HOLZ,KitType.KAUFEN,2000,new Perk[]{
				new PerkEquipment(new ItemStack[]{new ItemStack(Material.WOOD_AXE)}),
				new PerkHolzfäller()
			}),
			new Kit( "§aPanzer",new String[]{"Der Panzer bekommt beim Sneaken","höchstens 1 Herz schaden","wenn er angegriffen wird."}, new ItemStack(Material.DIAMOND_CHESTPLATE),Permission.DEATHGAMES_KIT_PANZER,KitType.KAUFEN,2000,new Perk[]{
				new PerkSneakDamage(3)
			}),
			new Kit( "§aSkorpion",new String[]{"Der Skorpion hat die Chance von 80%"," einen Spieler mit einem Schlag"," 5 Sek lang zu vergiften"}, new ItemStack(351,1,(byte)10),Permission.DEATHGAMES_KIT_SKORPION,KitType.KAUFEN,2000,new Perk[]{
				new PerkPoisen(2, 80)
			}),
			new Kit( "§aVersorger",new String[]{"Der Versorger bekommt kein Hunger."}, new ItemStack(Material.BAKED_POTATO),Permission.DEATHGAMES_KIT_VERSORGER,KitType.KAUFEN,2000,new Perk[]{
				new PerkNoHunger()
			}),
			new Kit( "§aVampire",new String[]{"Der Vampire wird für jedem","getöteten Mob 3 Herzen geheilt"}, new ItemStack(Material.GHAST_TEAR),Permission.DEATHGAMES_KIT_VAMPIRE,KitType.KAUFEN,2000,new Perk[]{
				new PerkHealByKill(6)
			}),
			new Kit( "§aAngler",new String[]{"Der Angler kann seine","Gegner zu sich ziehen"}, new ItemStack(Material.RAW_FISH),Permission.DEATHGAMES_KIT_ANGLE,KitType.KAUFEN,2000,new Perk[]{
				new PerkEquipment(new ItemStack[]{new ItemStack(Material.FISHING_ROD)}),
				new PerkAngle()
			}),
			new Kit( "§aEnterhaken",new String[]{"Mit dem Enterhaken kannst du","dich schnell zu Gegner und ","Blöcken ziehen um"," dich schneller fortzubewegen"}, new ItemStack(Material.FISHING_ROD),Permission.DEATHGAMES_KIT_ENTERHARKEN,KitType.KAUFEN,2000,new Perk[]{
				new PerkEquipment(new ItemStack[]{new ItemStack(Material.FISHING_ROD)}),
				new PerkEnterhacken()
			}),
			new Kit( "§aJumper",new String[]{"Der Jumper kann höher","als normal springen"}, new ItemStack(Material.FEATHER),Permission.DEATHGAMES_KIT_JUMPER,KitType.KAUFEN,2000,new Perk[]{
				new PerkPotionEffect(PotionEffectType.JUMP, 16*60, 3)
			}),
			new Kit( "§aRunner",new String[]{"Der Runner kann schneller","rennen und das ","durchgehend"}, new ItemStack(Material.LEATHER_BOOTS),Permission.DEATHGAMES_KIT_RUNNER,KitType.KAUFEN,2000,new Perk[]{
				new PerkRunner(0.35F)
			}),
			new Kit( "§aHai",new String[]{"Der Hai erhält","im Wasser Regeneration"}, new ItemStack(Material.CLAY_BALL),Permission.DEATHGAMES_KIT_HAI,KitType.KAUFEN,2000,new Perk[]{
				new PerkPotionInWater(PotionEffectType.REGENERATION, 5, 1)
			}),
			new Kit( "§aSchildkroete",new String[]{"Die Schildkroete hat 5 Herzen mehr!","macht aber dennoch ","weniger Schaden!"}, new ItemStack(Material.IRON_CHESTPLATE),Permission.DEATHGAMES_KIT_SCHILDKROETE,KitType.KAUFEN,2000,new Perk[]{
				new PerkMoreHeart(30),
				new PerkLessDamage(75)
			}),
			new Kit( "§aRitter",new String[]{"Der Ritter bekommt bei unter 4 Herzen Stärke 1."}, new ItemStack(Material.LEATHER_CHESTPLATE),Permission.DEATHGAMES_KIT_RITTER,KitType.KAUFEN,2000,new Perk[]{
				new PerkPotionEffectByHearth(PotionEffectType.INCREASE_DAMAGE, 1, 7)
			}),
			new Kit( "§aToxin",new String[]{"Wenn er einen Gegner schlägt"," wird dieser für ","5 Sekunden lang blind."}, new ItemStack(Material.FLINT),Permission.DEATHGAMES_KIT_TELEPORTER,KitType.KAUFEN,2000,new Perk[]{
				new PerkHitEffect(5, 90, PotionEffectType.BLINDNESS)
			}),
			new Kit( "§aYeti",new String[]{"Sobald er einen Gegner"," schlägt erhält dieser","Langsamkeit für"," 5 Sekunden."}, new ItemStack(Material.SOUL_SAND),Permission.DEATHGAMES_KIT_TELEPORTER,KitType.KAUFEN,2000,new Perk[]{
				new PerkHitEffect(5, 90, PotionEffectType.SLOW)
			}),
			new Kit( "§aTeleporter",new String[]{"Der Teleporter kann","sich 1x mit","einem Spieler ","tauschen lassen!"}, new ItemStack(Material.WATCH),Permission.DEATHGAMES_KIT_TELEPORTER,KitType.KAUFEN,2000,new Perk[]{
				new PerkTeleporter(getManager())
			}),
			new Kit( "§aSuperman",new String[]{"Der Superman ist das Beste kit in DeathGames!"}, new ItemStack(Material.DIAMOND_SWORD),Permission.DEATHGAMES_KIT_SUPERMAN,KitType.ADMIN,2000,new Perk[]{
				new PerkNoHunger(),
				new PerkEquipment(new ItemStack[]{new ItemStack(Material.IRON_SWORD,1), new ItemStack(Material.LEATHER_HELMET,1), new ItemStack(Material.IRON_CHESTPLATE,1), new ItemStack(Material.LEATHER_LEGGINGS,1), new ItemStack(Material.LEATHER_BOOTS)}),
				new PerkSneakDamage(1),
				new PerkPoisen(10,50),
				new PerkHolzfäller(),
				new PerkNoFiredamage(),
				new PerkNoFalldamage(),
				new PerkArrowFire(80),
				new PerkNoExplosionDamage(),
				new PerkTNT(),
				new PerkHealByHit(60, 6),
				new PerkHeal(6),
				new PerkMoreHearth(6, 60),
				new PerkArrowInfinity(),
				new PerkWalkEffect(Effect.HEART,10)
			}),
		});
		
		for(int i=10; i < (getMax_Players()*10)+1; i++){
			g.put(i, getGrenze().scanWithLowestBlock(i, 35, 10));
			System.out.println("[Grenze] Radius: "+i+" GELADEN("+g.get(i).size()+")!");
		}
		
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
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerChatd(AsyncPlayerChatEvent event) {
		if (!event.isCancelled()) {
			Player p = event.getPlayer();
			String msg = event.getMessage();
			msg=msg.replaceAll("%","");
			if(manager.getPermManager().hasPermission(p, Permission.ALL_PERMISSION))msg=msg.replaceAll("&", "§");
			event.setFormat(manager.getPermManager().getPrefix(p) + p.getName() + "§7: "+ msg);
		}
	}
	
	private void setupItems(){
		selten.add(new ItemStack(Material.DIAMOND, 1));
		selten.add(new ItemStack(Material.DIAMOND_CHESTPLATE,1));
		selten.add(new ItemStack(Material.DIAMOND, 2));
		selten.add(new ItemStack(Material.DIAMOND, 3));
		selten.add(new ItemStack(Material.GOLDEN_APPLE, 1));
		selten.add(new ItemStack(Material.GOLDEN_APPLE, 2));
		selten.add(new ItemStack(Material.ENDER_PEARL, 3));
		selten.add(new ItemStack(Material.ENDER_PEARL, 2));
		selten.add(new ItemStack(Material.CAKE, 2));
		selten.add(new ItemStack(Material.TNT, 2));
		selten.add(new ItemStack(Material.IRON_CHESTPLATE,1));
		selten.add(new ItemStack(Material.GOLD_SWORD, 1));
		selten.add(new ItemStack(Material.FLINT_AND_STEEL, 1));
		selten.add(new ItemStack(Material.EXP_BOTTLE, 3));
		selten.add(new ItemStack(Material.EXP_BOTTLE, 5));
		selten.add(new ItemStack(Material.EXP_BOTTLE, 8));
		selten.add(new ItemStack(Material.ENCHANTMENT_TABLE, 1));
		selten.add(new ItemStack(Material.ENCHANTMENT_TABLE, 1));
		selten.add(new ItemStack(Material.ENCHANTMENT_TABLE, 1));
		selten.add(new ItemStack(Material.EXP_BOTTLE, 3));
		selten.add(new ItemStack(Material.EXP_BOTTLE, 4));
		selten.add(new ItemStack(Material.POTION, 5,(byte)16389));
		selten.add(new ItemStack(Material.POTION, 2,(byte)16389));
		selten.add(new ItemStack(Material.POTION, 2,(byte)16451));
		selten.add(new ItemStack(Material.POTION, 2,(byte)16393));
		selten.add(new ItemStack(Material.POTION, 2,(byte)16386));
		
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
		medium.add(new ItemStack(Material.IRON_HELMET, 1));
		medium.add(new ItemStack(Material.IRON_LEGGINGS, 1));
		medium.add(new ItemStack(Material.IRON_BOOTS, 1));
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
		normal.add(new ItemStack(Material.STONE_SWORD, 1));
		normal.add(new ItemStack(Material.STONE_AXE, 1));
		normal.add(new ItemStack(Material.IRON_AXE, 1));
		medium.add(new ItemStack(Material.STICK, 2));
		
		normal.add(new ItemStack(Material.APPLE, 1));
		normal.add(new ItemStack(Material.WHEAT, 7));
		normal.add(new ItemStack(Material.BAKED_POTATO, 3));
		normal.add(new ItemStack(Material.WOOD_SWORD, 1));
		normal.add(new ItemStack(Material.LEATHER_HELMET, 1));
		normal.add(new ItemStack(Material.LEATHER_CHESTPLATE, 1));
		normal.add(new ItemStack(Material.LEATHER_LEGGINGS, 1));
		normal.add(new ItemStack(Material.LEATHER_BOOTS, 1));
		normal.add(new ItemStack(Material.BOW, 1));
		normal.add(new ItemStack(Material.WOOD_AXE, 1));
		normal.add(new ItemStack(Material.BREAD, 1));
		medium.add(new ItemStack(Material.COOKED_BEEF, 3));
		medium.add(new ItemStack(Material.COOKED_CHICKEN, 2));
		medium.add(new ItemStack(Material.CAKE, 2));
		normal.add(new ItemStack(Material.CARROT_ITEM, 3));
		normal.add(new ItemStack(Material.WORKBENCH, 1));
		normal.add(new ItemStack(Material.ROTTEN_FLESH, 4));
	}
	
	public Inventory setupInv() {
		Inventory inv = Bukkit.createInventory(null, 9 * 3, "DeathGames");
		ItemStack[] is = new ItemStack[9 * 3];
		for (int ii = 0; ii < UtilMath.r(3) + 2; ii++) {
			if (UtilMath.r(15) == 1) {
				ItemStack added = selten.get(UtilMath.r(selten.size()));
				inv.setItem(UtilMath.r(26), added);
				is[ii] = added;
			} else if (UtilMath.r(5) == 3 || UtilMath.r(5) == 1) {
				ItemStack added = medium.get(UtilMath.r(medium.size()));
				inv.setItem(UtilMath.r(26), added);
				is[ii] = added;
			} else {
				ItemStack added = normal.get(UtilMath.r(normal.size()));
				inv.setItem(UtilMath.r(26), added);
				is[ii] = added;
			}
		}
		return inv;
	}
	
	@EventHandler
	public void Death(PlayerDeathEvent ev){
		if(ev.getEntity() instanceof Player){
			Player v = (Player)ev.getEntity();
			UtilParticle.DRIP_LAVA.display(0.5F, 0.9F, 0.5F, 3, 65, v.getLocation(), 30);
			UtilParticle.DRIP_WATER.display(0.5F, 0.9F, 0.5F, 3, 65, v.getLocation(), 30);
			getStats().setInt(v, getStats().getInt(Stats.LOSE, v)+1, Stats.LOSE);
			getStats().setInt(v, getStats().getInt(Stats.DEATHS, v)+1, Stats.DEATHS);
			getGameList().addPlayer(v, PlayerState.OUT);
			if(ev.getEntity().getKiller() instanceof Player){
				Player a = (Player)ev.getEntity().getKiller();
				getStats().setInt(a, getStats().getInt(Stats.KILLS, a)+1, Stats.KILLS);
				getCoins().addCoins(a, false, 5,getType());
				getManager().broadcast( Text.PREFIX_GAME.getText(getType().name())+Text.KILL_BY.getText(new String[]{v.getName(),a.getName()}) );
				return;
			}else{
				getManager().broadcast( Text.PREFIX_GAME.getText(getType().name())+Text.DEATH.getText(v.getName()) );
			}
		}
	}
	
	long time;
	Location loc;
	List<Player> gl;
	@EventHandler
	public void Schild(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SLOW)return;
		if(getManager().getState()!=GameState.InGame)return;
		if(getGrenze().getRadius() != (getGameList().getPlayers(PlayerState.IN).size()*10)){
			//getGrenze().setRadius( getGrenze().getRadius()-1 );
			//getGrenze().scan();
			getGrenze().setList(getGrenze().getRadius()-1, g.get(getGrenze().getRadius()-1));
			
			g.remove(getGrenze().getRadius());
			chest_anzahl=getGameList().getPlayers(PlayerState.IN).size()*10;
			if(getManager().getState()==GameState.InGame||getManager().getState()==GameState.SchutzModus){
				for(int i = 0; i < chest.size(); i++){
					loc=(Location)chest.keySet().toArray()[i];
					if(!getGrenze().isInGrenze(loc)){
						if(chest.get(loc)!=null){
							try{
								for (HumanEntity en : chest.get(loc).getViewers()){
									en.closeInventory();
								}
							}catch(ConcurrentModificationException e){
								System.out.println("[DeathGames] ConcurrentModificationException <-");
							}
						}
						if(loc.getBlock().getType()==Material.CHEST)UtilParticle.LAVA.display(0.0F, 1.0F, 0.0F, 1, 40, loc, 7);
						if(loc.getBlock().getType()==Material.ENDER_CHEST)loc.getWorld().playEffect(loc,Effect.ENDER_SIGNAL, 3);
						loc.getBlock().setType(Material.AIR);
						chest1.remove(chest.get(loc));
						chest.remove(loc);
						spawnChest();
					}
				}
			}
			
		}
	}
	
//	ArrayList<Entity> fall = new ArrayList<>();
//	@EventHandler
//	public void FallDamage(EntityDamageEvent ev){
//		if(manager.getState()!=GameState.SchutzModus)return;
//		if(ev.getEntity() instanceof Player){
//			if(ev.getCause()==DamageCause.FALL){
//				if(fall.contains((ev.getEntity()) ))ev.setDamage(0);
//			}
//		}
//	}
	
//	@EventHandler
//	public void onEntityChangeBlock(EntityChangeBlockEvent ev){
//		if(ev.getEntity() instanceof FallingBlock){
//			if(fall.contains(ev.getEntity())){
//				if(ev.getEntity().getPassenger()!=null){
//		    		gp = ev.getEntity().getPassenger();
//		    		gp.leaveVehicle();
//		    		gp.remove();
//		    	}
//				fall.remove(ev.getEntity());
//			}
//		}
//	}
	
//	private Entity gp;
//	@EventHandler
//	public void Fall(UpdateEvent ev){
//		if(ev.getType()!=UpdateType.FAST)return;
//		if(getManager().getState()==GameState.InGame||getManager().getState()==GameState.SchutzModus){
//			for(int i = 0; i < fall.size(); i++){
//				if(fall.get(i).getPassenger()!=null&&fall.get(i).getPassenger().getType()==EntityType.CHICKEN){
//					if(fall.get(i).isOnGround()){
//						fall.get(i).getPassenger().leaveVehicle();
//						fall.remove(fall.get(i));
//					}else if(fall.get(i).isOnGround()&&fall.get(i).getLocation().getBlock().getType().toString().contains("WATER")){
//						if(fall.get(i).getPassenger()!=null){
//				    		gp = fall.get(i).getPassenger();
//				    		gp.leaveVehicle();
//				    		gp.remove();
//				    	}
//						fall.remove(i);
//					}else{
//						fall.get(i).setVelocity(new Vector(fall.get(i).getVelocity().getX(), fall.get(i).getVelocity().getY()/4, fall.get(i).getVelocity().getZ()));
//					}
//				}else{
//					fall.get(i).setPassenger(fall.get(i).getWorld().spawnEntity(fall.get(i).getLocation(), EntityType.CHICKEN));
//				}
//			}
//		}
//	}
	
	@EventHandler
	public void SpawnChest(UpdateEvent ev){
		if(ev.getType()!=UpdateType.FASTER)return;
		if(getManager().getState()==GameState.InGame||getManager().getState()==GameState.SchutzModus){
			if(chest.size()<chest_anzahl){
				spawnChest();
			}
			if(0==extra_chest_anzahl){
				spawnExtraChest();
				extra_chest_anzahl=1;
			}
		}
	}
	
	public void spawnExtraChest(){
		Location loc = new Location(getWorldData().getWorld(), UtilMath.RandomInt(getGrenze().getMaxX(), getGrenze().getMinX()), 200, UtilMath.RandomInt(getGrenze().getMaxZ(), getGrenze().getMinZ()));
		loc.getWorld().spawnFallingBlock(loc, Material.ENDER_CHEST, (byte)1);
		//fb.setPassenger(loc.getWorld().spawnEntity(loc, EntityType.CHICKEN));
		Location l = UtilLocation.getLowestBlock(loc);

		Inventory inv = Bukkit.createInventory(null, 9 * 3, "DeathGames");
		ItemStack[] is = new ItemStack[9 * 3];
		for (int ii = 0; ii < UtilMath.r(3) + 2; ii++) {
			if (UtilMath.r(5) == 1) {
				ItemStack added = selten.get(UtilMath.r(selten.size()));
				inv.setItem(UtilMath.r(26), added);
				is[ii] = added;
			} else {
				ItemStack added = medium.get(UtilMath.r(medium.size()));
				inv.setItem(UtilMath.r(26), added);
				is[ii] = added;
			}
		}
		
		chest.put(l,inv);
		chest1.put(inv,l);
		//fall.add(fb);
	}
	
	public void spawnChest(){
		Location loc = new Location(getWorldData().getWorld(), UtilMath.RandomInt(getGrenze().getMaxX(), getGrenze().getMinX()), 200, UtilMath.RandomInt(getGrenze().getMaxZ(), getGrenze().getMinZ()));
		loc.getWorld().spawnFallingBlock(loc, Material.CHEST,(byte)1);
		//fb.setPassenger(loc.getWorld().spawnEntity(loc, EntityType.CHICKEN));
		Location l = UtilLocation.getLowestBlock(loc);
		Inventory inv = setupInv();
		chest.put(l,inv);
		chest1.put(inv,l);
		//fall.add(fb);
	}
	
//	long time;
//	Location loc;
//	@EventHandler
//	public void ChangeChest(UpdateEvent ev){
//		if(ev.getType()!=UpdateType.FAST)return;
//		if(getManager().getState()==GameState.InGame||getManager().getState()==GameState.SchutzModus){
//			for(int i = 0; i < chest.size(); i++){
//				loc=(Location)chest.keySet().toArray()[i];
//				if(!getGrenze().isInGrenze(loc)){
//					if(chest.get(loc)!=null){
//						try{
//							for (HumanEntity en : chest.get(loc).getViewers()){
//								en.closeInventory();
//							}
//						}catch(ConcurrentModificationException e){
//							System.out.println("[DeathGames] ConcurrentModificationException <-");
//						}
//					}
//					if(loc.getBlock().getType()==Material.CHEST)UtilParticle.LAVA.display(0.0F, 1.0F, 0.0F, 1, 40, loc, 7);
//					if(loc.getBlock().getType()==Material.ENDER_CHEST)loc.getWorld().playEffect(loc,Effect.ENDER_SIGNAL, 3);
//					loc.getBlock().setType(Material.AIR);
//					chest1.remove(chest.get(loc));
//					chest.remove(loc);
//					spawnChest();
//				}
//			}
//		}
//	}
	
	@EventHandler
	public void InventoryCloseCCC(InventoryCloseEvent ev){
		if(ev.getInventory().getTitle().equalsIgnoreCase("DeathGames")){
			if(UtilInv.isInventoryEmpty(ev.getInventory())){
				Location l = chest1.get(ev.getInventory());
				chest.remove(l);
				chest1.remove(ev.getInventory());
				if(l.getBlock().getType()==Material.CHEST)UtilParticle.LAVA.display(0.0F, 1.0F, 0.0F, 1, 40, l, 7);
				if(l.getBlock().getType()==Material.ENDER_CHEST){
					l.getWorld().playEffect(l,Effect.ENDER_SIGNAL, 3);
					extra_chest_anzahl=0;
				}
				l.getBlock().setType(Material.AIR);
			}
		}
	}
	
	Inventory inv;
	@EventHandler
	public void Open(PlayerInteractEvent ev){
		if(getManager().getState()==GameState.InGame||getManager().getState()==GameState.SchutzModus){
			if(getGameList().getPlayers(PlayerState.OUT).contains(ev.getPlayer()))return;
			if(UtilEvent.isAction(ev, ActionType.R_BLOCK)){
				if(ev.getClickedBlock().getType()==Material.ENDER_CHEST||ev.getClickedBlock().getType()==Material.CHEST){
					if(chest.containsKey(ev.getClickedBlock().getLocation())){
						ev.getPlayer().openInventory(chest.get(ev.getClickedBlock().getLocation()));
						ev.setCancelled(true);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void Ranking(RankingEvent ev){
		getManager().setRanking(Stats.WIN);
	}
	
	@EventHandler
	public void GameStateChange(GameStateChangeEvent ev){
		if(ev.getTo()==GameState.Restart){
			if(getGameList().getPlayers(PlayerState.IN).size()==1){
				Player p = getGameList().getPlayers(PlayerState.IN).get(0);
				getStats().setInt(p, getStats().getInt(Stats.WIN, p)+1, Stats.WIN);
				getCoins().addCoins(p, false, 10,getType());
				getManager().broadcast(Text.PREFIX_GAME.getText(getType())+Text.GAME_WIN.getText(p.getName()));
			}
		}
	}
	
	@EventHandler
	public void Schutzzeit(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		if(getManager().getState()!=GameState.SchutzModus)return;
		getManager().setStart(getManager().getStart()-1);

		for(Player p : UtilServer.getPlayers())UtilDisplay.displayTextBar(Text.SCHUTZZEIT_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())), p);
		switch(getManager().getStart()){
		case 15: getManager().broadcast( Text.PREFIX_GAME.getText(getType().name())+Text.SCHUTZZEIT_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())) );break;
		case 10: getManager().broadcast( Text.PREFIX_GAME.getText(getType().name())+Text.SCHUTZZEIT_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())) );break;
		case 5: getManager().broadcast( Text.PREFIX_GAME.getText(getType().name())+Text.SCHUTZZEIT_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())) );break;
		case 4: getManager().broadcast( Text.PREFIX_GAME.getText(getType().name())+Text.SCHUTZZEIT_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())) );break;
		case 3: getManager().broadcast( Text.PREFIX_GAME.getText(getType().name())+Text.SCHUTZZEIT_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())) );break;
		case 2: getManager().broadcast( Text.PREFIX_GAME.getText(getType().name())+Text.SCHUTZZEIT_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())) );break;
		case 1: getManager().broadcast( Text.PREFIX_GAME.getText(getType().name())+Text.SCHUTZZEIT_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())) );break;
		case 0:
			getManager().broadcast( Text.PREFIX_GAME.getText(getType().name())+Text.SCHUTZZEIT_END.getText() );
			setDamage(true);
			NoCheatToggle();
			getManager().setStart(60*15);
			getManager().setState(GameState.InGame);
			break;
		}
	}
	
	@EventHandler
	public void inGame(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		if(getManager().getState()!=GameState.InGame)return;
		getManager().setStart(getManager().getStart()-1);
		
		for(Player p : UtilServer.getPlayers())UtilDisplay.displayTextBar(Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())), p);
		switch(getManager().getStart()){
		case 30: getManager().broadcast( Text.PREFIX_GAME.getText(getType().name())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())) );break;
		case 15: getManager().broadcast( Text.PREFIX_GAME.getText(getType().name())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())) );break;
		case 10: getManager().broadcast( Text.PREFIX_GAME.getText(getType().name())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())) );break;
		case 5: getManager().broadcast( Text.PREFIX_GAME.getText(getType().name())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())) );break;
		case 4: getManager().broadcast( Text.PREFIX_GAME.getText(getType().name())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())) );break;
		case 3: getManager().broadcast( Text.PREFIX_GAME.getText(getType().name())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())) );break;
		case 2: getManager().broadcast( Text.PREFIX_GAME.getText(getType().name())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())) );break;
		case 1: getManager().broadcast( Text.PREFIX_GAME.getText(getType().name())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())) );break;
		case 0:
			getManager().broadcast( Text.PREFIX_GAME.getText(getType().name())+Text.GAME_END.getText() );
			getManager().setState(GameState.Restart);
			break;
		}
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	public void JoinHologram(PlayerJoinEvent ev){
		if(getManager().getState()!=GameState.LobbyPhase)return;
		if(hm==null)hm=new Hologram(getManager().getInstance());

		int win = getStats().getInt(Stats.WIN, ev.getPlayer());
		int lose = getStats().getInt(Stats.LOSE, ev.getPlayer());
		getManager().getLoc_stats().getWorld().loadChunk(getManager().getLoc_stats().getWorld().getChunkAt(getManager().getLoc_stats()));
		hm.sendText(ev.getPlayer(),getManager().getLoc_stats().clone().add(0, 0.1, 0),new String[]{
		C.cGreen+getType().getTyp()+C.mOrange+C.Bold+" Info",
		"Server: DeathGames §a"+kArcade.id,
		"Biom: "+getWorldData().getMapName(),
		" ",
		C.cGreen+getType().getTyp()+C.mOrange+C.Bold+" Stats",
		"Kills: "+getStats().getInt(Stats.KILLS, ev.getPlayer()),
		"Tode: "+getStats().getInt(Stats.DEATHS, ev.getPlayer()),
		" ",
		"Gespielte Spiele: "+(win+lose),
		"Gewonnene Spiele: "+win,
		"Verlorene Spiele: "+lose
		});
		ev.getPlayer().getInventory().addItem(UtilItem.RenameItem(new ItemStack(Material.CHEST), "§bKitShop"));
	}
	
	public void NoCheatToggle(){
		Plugin pl = Bukkit.getPluginManager().getPlugin("NoCheatPlus");
		if(Bukkit.getPluginManager().isPluginEnabled(pl)){
			Bukkit.getPluginManager().disablePlugin(pl);
		}else{
			Bukkit.getPluginManager().enablePlugin(pl);
		}
		
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void Start(GameStartEvent ev){
		long time = System.currentTimeMillis();
		getWorldData().getWorld().setStorm(false);
		NoCheatToggle();
		chest_anzahl=UtilServer.getPlayers().length*10;
		
		grenze.setList(chest_anzahl, g.get( chest_anzahl ));
		g.remove(chest_anzahl);
		//grenze.setRadius( (UtilServer.getPlayers().length*10) );
		//grenze.scan();
		
		int minZ=grenze.MinZ()+20;
		int maxZ=grenze.MaxZ()-20;
		int minX=grenze.MinX()+20;
		int maxX=grenze.MaxX()-20;
		System.err.println("X: MAX:"+maxX+" MIN:"+minX);
		System.err.println("Z: MAX:"+maxZ+" MIN:"+minZ);
		new AddonPlayerTeleport(this);
		for(Player p : UtilServer.getPlayers()){
			getManager().Clear(p);
			getGameList().addPlayer(p,PlayerState.IN);
			p.teleport( new Location(getWorldData().getWorld(), UtilMath.RandomInt(maxX, minX), 200, UtilMath.RandomInt(maxZ, minZ)) );
			p.getInventory().addItem(new ItemStack(Material.COMPASS));
		}
		AddonTargetNextPlayer a = new AddonTargetNextPlayer(500,getManager());
		a.setAktiv(true);
		setDamage(false);
		getManager().setStart(46);
		getManager().setState(GameState.SchutzModus);
		getManager().DebugLog(time, this.getClass().getName());
	}

}
