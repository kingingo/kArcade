package me.kingingo.karcade.Game.Single.Games.SkyWars;

import java.util.ArrayList;
import java.util.HashMap;

import lombok.Getter;
import me.kingingo.karcade.kArcade;
import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Events.RankingEvent;
import me.kingingo.karcade.Events.WorldLoadEvent;
import me.kingingo.karcade.Game.Events.GameStartEvent;
import me.kingingo.karcade.Game.Events.GameStateChangeEvent;
import me.kingingo.karcade.Game.Single.SingleWorldData;
import me.kingingo.karcade.Game.Single.Games.TeamGame;
import me.kingingo.karcade.Game.Single.Games.SkyWars.Item.CreeperSpawner;
import me.kingingo.karcade.Game.Single.addons.AddonVoteTeam;
import me.kingingo.kcore.Addons.AddonDay;
import me.kingingo.kcore.Addons.AddonNight;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.Enum.PlayerState;
import me.kingingo.kcore.Enum.Team;
import me.kingingo.kcore.Kit.Kit;
import me.kingingo.kcore.Kit.KitType;
import me.kingingo.kcore.Kit.Perk;
import me.kingingo.kcore.Kit.Perks.PerkArrowFire;
import me.kingingo.kcore.Kit.Perks.PerkArrowInfinity;
import me.kingingo.kcore.Kit.Perks.PerkDoubleJump;
import me.kingingo.kcore.Kit.Perks.PerkEquipment;
import me.kingingo.kcore.Kit.Perks.PerkHeal;
import me.kingingo.kcore.Kit.Perks.PerkHealByHit;
import me.kingingo.kcore.Kit.Perks.PerkHolzfäller;
import me.kingingo.kcore.Kit.Perks.PerkMoreHearth;
import me.kingingo.kcore.Kit.Perks.PerkNoExplosionDamage;
import me.kingingo.kcore.Kit.Perks.PerkNoFalldamage;
import me.kingingo.kcore.Kit.Perks.PerkNoFiredamage;
import me.kingingo.kcore.Kit.Perks.PerkNoHunger;
import me.kingingo.kcore.Kit.Perks.PerkNoKnockback;
import me.kingingo.kcore.Kit.Perks.PerkPoisen;
import me.kingingo.kcore.Kit.Perks.PerkSneakDamage;
import me.kingingo.kcore.Kit.Perks.PerkTNT;
import me.kingingo.kcore.Kit.Perks.PerkWalkEffect;
import me.kingingo.kcore.Kit.Shop.SingleKitShop;
import me.kingingo.kcore.Language.Language;
import me.kingingo.kcore.LaunchItem.LaunchItemManager;
import me.kingingo.kcore.Permission.kPermission;
import me.kingingo.kcore.StatsManager.Stats;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.Color;
import me.kingingo.kcore.Util.InventorySize;
import me.kingingo.kcore.Util.Title;
import me.kingingo.kcore.Util.UtilBG;
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
import org.bukkit.DyeColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.Potion.Tier;
import org.bukkit.potion.PotionType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

public class SkyWars extends TeamGame{

	private SkyWarsType type;
	private SingleKitShop kitshop;
	private HashMap<String,Integer> kills =  new HashMap<>();
	@Getter
	private LaunchItemManager ilManager;
	private CreeperSpawner creeper;
	
	public SkyWars(kArcadeManager manager,SkyWarsType type){
		super(manager);
		registerListener();
		long l = System.currentTimeMillis();
		this.type=type;
		this.ilManager=new LaunchItemManager(getManager().getInstance());
		this.creeper=new CreeperSpawner(this);
		setTyp(GameType.SkyWars);
		setState(GameState.Laden);
		setMax_Players(type.getMax());
		setMin_Players(type.getMin());
		setDamage(true);
		setDamagePvP(true);
		setDamageSelf(true);
		setBlockBreak(true);
		setBlockPlace(true);
		setCreatureSpawn(false);
		setCompassAddon(true);
		setDeathDropItems(true);
		setFoodChange(true);
		setItemPickup(true);
		setItemDrop(true);
		setRespawn(true);
		setWorldData(new SingleWorldData(manager,getType()));
		getWorldData().setCleanroomChunkGenerator(true);
		getWorldData().Initialize();
		if(type.getTeam_size()!=1)setVoteTeam(new AddonVoteTeam(this,type.getTeam(),InventorySize._18,type.getTeam_size()));
		
		kitshop=new SingleKitShop(getManager().getInstance(), getGems(),getCoins(), getManager().getPermManager(), "Kit-Shop", InventorySize._9, new Kit[]{
			new Kit("§aStarter-Kit",new String[]{"§8x1§7 Leder Rüstung","§8x1§7 Holzschwert mit Schärfe 1"},new ItemStack(Material.LEATHER_HELMET),kPermission.SKYWARS_KIT_STARTERKIT,KitType.STARTER,0,0,new Perk[]{
				new PerkEquipment(new ItemStack[]{UtilItem.EnchantItem(new ItemStack(Material.WOOD_SWORD), Enchantment.DAMAGE_ALL, 1),new ItemStack(Material.LEATHER_BOOTS),new ItemStack(Material.LEATHER_LEGGINGS),new ItemStack(Material.LEATHER_CHESTPLATE),new ItemStack(Material.LEATHER_HELMET)})
			}),
			new Kit("§ePanzer",new String[]{"§8x1§7 Diamanthelm mit Dornen 1,Unbreaking 1","§8x1§7 Eisenbrustpanzer mit Unbreaking 1","§8x1§7 Eisenhose mit Unbreaking 1","§8x1§7 Eisenschuhe mit Unbreaking 1"},new ItemStack(Material.SLIME_BALL),kPermission.SKYWARS_KIT_PANZER,KitType.KAUFEN,2000,500,new Perk[]{
				new PerkEquipment(new ItemStack[]{UtilItem.EnchantItem(UtilItem.EnchantItem(new ItemStack(Material.DIAMOND_CHESTPLATE), Enchantment.DURABILITY, 1), Enchantment.THORNS, 1),
						UtilItem.EnchantItem(new ItemStack(Material.IRON_HELMET), Enchantment.DURABILITY, 1),
						UtilItem.EnchantItem(new ItemStack(Material.IRON_LEGGINGS), Enchantment.DURABILITY, 1),
						UtilItem.EnchantItem(new ItemStack(Material.IRON_BOOTS), Enchantment.DURABILITY, 1)})
			}),
			new Kit("§eGlueckshase",new String[]{"§8x1§7 Eisenspitzhacke mit Glueck 2"},new ItemStack(Material.RABBIT_FOOT),kPermission.SKYWARS_KIT_HASE,KitType.KAUFEN,2000,500,new Perk[]{
				new PerkEquipment(new ItemStack[]{UtilItem.EnchantItem(new ItemStack(Material.IRON_PICKAXE), Enchantment.LUCK, 2)})
			}),
			new Kit("§eHulk",new String[]{"§8x1§7 Leder Rüstung mit Schutz 1","§8x1§7 Stärke 2 Trank"},UtilItem.LSetColor(new ItemStack(Material.LEATHER_HELMET), DyeColor.GREEN),kPermission.SKYWARS_KIT_HULK,KitType.KAUFEN,2000,500,new Perk[]{
				new PerkEquipment(new ItemStack[]{UtilItem.LSetColor(new ItemStack(Material.LEATHER_BOOTS), DyeColor.GREEN),
						UtilItem.LSetColor(new ItemStack(Material.LEATHER_LEGGINGS), DyeColor.GREEN),
						UtilItem.LSetColor(new ItemStack(Material.LEATHER_CHESTPLATE), DyeColor.GREEN),
						UtilItem.LSetColor(new ItemStack(Material.LEATHER_HELMET), DyeColor.GREEN),
						new ItemStack(Material.POTION,1,(byte)8233)})
			}),
			new Kit("§eSuperMario",new String[]{"§8x1§7 Roter Lederbrustpanzer","§8x1§7 Blaue Lederhose","§8x1§7 Sprungkraft 1 Trank"},UtilItem.LSetColor(new ItemStack(Material.LEATHER_HELMET), DyeColor.RED),kPermission.SKYWARS_KIT_MARIO,KitType.KAUFEN,2000,500,new Perk[]{
				new PerkEquipment(new ItemStack[]{UtilItem.LSetColor(new ItemStack(Material.LEATHER_LEGGINGS), DyeColor.RED),
						UtilItem.LSetColor(new ItemStack(Material.LEATHER_CHESTPLATE), DyeColor.BLUE),
						new ItemStack(Material.POTION,1,(byte)16459)})
			}),
			new Kit("§eStuntman",new String[]{"§8x1§7 Diamantschuhe mit Federfall IV"},new ItemStack(Material.FEATHER),kPermission.SKYWARS_KIT_STUNTMAN,KitType.KAUFEN,2000,500,new Perk[]{
				new PerkEquipment(new ItemStack[]{UtilItem.EnchantItem(new ItemStack(Material.DIAMOND_BOOTS), Enchantment.PROTECTION_FALL, 5)})
			}),
			new Kit("§eSlime",new String[]{"§8x16§7 Slime Bleocke","§8x5§7 Jump Wurftrank"},new ItemStack(Material.SLIME_BALL),kPermission.SKYWARS_KIT_SLIME,KitType.KAUFEN,2000,500,new Perk[]{
				new PerkEquipment(new ItemStack[]{new ItemStack(Material.SLIME_BLOCK,16),new ItemStack(Material.POTION,5,(byte)16427)})
			}),
			new Kit( "§eKoch",new String[]{"§8x1§7 Holzschwert Schärfe 2","§8x8§7 Steaks","§8x1§7 Kuchen","§8x3§7 Goldenäpfel"}, new ItemStack(Material.CAKE),kPermission.SKYWARS_KIT_KOCH,KitType.KAUFEN,2000,500,new Perk[]{
				new PerkEquipment(new ItemStack[]{UtilItem.RenameItem(UtilItem.EnchantItem(new ItemStack(Material.WOOD_SWORD),Enchantment.DAMAGE_ALL,2), "§7Gabel"),new ItemStack(Material.COOKED_BEEF,8),new ItemStack(Material.CAKE,1),new ItemStack(Material.GOLDEN_APPLE,3)})
			}),
			new Kit( "§eSprengmeister",new String[]{"§8x4 Creeper Spawner","§8x10 TnT","§8x1 Feuerzeug"}, new ItemStack(Material.TNT),kPermission.SKYWARS_KIT_SPRENGMEISTER,KitType.KAUFEN,2000,500,new Perk[]{
				new PerkEquipment(new ItemStack[]{new ItemStack(Material.TNT,10),new ItemStack(Material.FLINT_AND_STEEL),this.creeper.getItem(4)})
			}),
			new Kit( "§eJäger",new String[]{"§8x1§7 Bogen","§8x10 §7Pfeile"}, new ItemStack(Material.ARROW),kPermission.SKYWARS_KIT_JÄGER,KitType.KAUFEN,2000,500,new Perk[]{
				new PerkEquipment(new ItemStack[]{new ItemStack(Material.BOW),new ItemStack(Material.ARROW,10)})
			}),
			new Kit( "§eEnchanter",new String[]{"§8x1§7 Enchantment Table","§8x64 §7Enchantment Bottles"}, new ItemStack(Material.ENCHANTMENT_TABLE),kPermission.SKYWARS_KIT_ENCHANTER,KitType.KAUFEN,2000,500,new Perk[]{
				new PerkEquipment(new ItemStack[]{new ItemStack(Material.ENCHANTMENT_TABLE),new ItemStack(Material.EXP_BOTTLE,64)})
			}),
			new Kit( "§eHeiler",new String[]{"§8x3§7 Tränke der Heilung","§8x3§7 Regenerations Tränke"}, new ItemStack(Material.POTION,1,(short)16389),kPermission.SKYWARS_KIT_HEILER,KitType.KAUFEN,2000,500,new Perk[]{
				new PerkEquipment(new ItemStack[]{new ItemStack(Material.POTION,3,(short)16389),new ItemStack(Material.POTION,3,(short)16385)})
			}),
			new Kit( "§eSpäher",new String[]{"§8x1§7 Steinschwert","§8x3 §7Schnelligkeits Tränke"}, new ItemStack(Material.STONE_SWORD),kPermission.SKYWARS_KIT_SPÄHER,KitType.KAUFEN,2000,500,new Perk[]{
				new PerkEquipment(new ItemStack[]{new ItemStack(Material.STONE_SWORD),new ItemStack(Material.POTION,3,(short)16386)})
			}),
			new Kit( "§eKampfmeister",new String[]{"§8x1§7 Anvil","§864x§7 Enchantment Bottles","§8x1§7 Diamant Helm","§8x1§7 Enchantment Buch (Protection III)"}, new ItemStack(Material.ANVIL),kPermission.SKYWARS_KIT_KAMPFMEISTER,KitType.KAUFEN,2000,500,new Perk[]{
				new PerkEquipment(new ItemStack[]{UtilItem.getEnchantmentBook(Enchantment.PROTECTION_ENVIRONMENTAL, 3),new ItemStack(Material.DIAMOND_HELMET),new ItemStack(Material.EXP_BOTTLE,64),new ItemStack(Material.ANVIL)})
			}),
			new Kit( "§eRitter",new String[]{"§8x1§7 Eisenschwert (Schärfe II)"}, new ItemStack(Material.IRON_SWORD),kPermission.SHEEPWARS_KIT_ARROWMAN,KitType.KAUFEN,2000,500,new Perk[]{
				new PerkEquipment(new ItemStack[]{UtilItem.EnchantItem(new ItemStack(Material.IRON_SWORD), Enchantment.DAMAGE_ALL, 2)})
			}),
			new Kit( "§eFeuermeister",new String[]{"§8x1§7Feuerzeug","§82x§7 Lava-Eimer","§82x§7 Feuer Tränke"}, new ItemStack(Material.LAVA_BUCKET),kPermission.SKYWARS_KIT_FEUERMEISTER,KitType.KAUFEN,2000,500,new Perk[]{
				new PerkEquipment(new ItemStack[]{new ItemStack(Material.POTION,2,(short)16387),new ItemStack(Material.FLINT_AND_STEEL,1),new ItemStack(Material.LAVA_BUCKET,2)})
			}),
			new Kit( "§eDroide",new String[]{"§8x1§7 Regenerations Trank","§82x§7 Tränke der Heilung","§82x§7 Vergiftungs Tränke","§82x§7 Schadens Tränke"}, new ItemStack(Material.POTION),kPermission.SKYWARS_KIT_DROIDE,KitType.KAUFEN,2000,500,new Perk[]{
				new PerkEquipment(new ItemStack[]{new ItemStack(Material.POTION,1,(short)16385),new ItemStack(Material.POTION,2,(short)16388),new ItemStack(Material.POTION,2,(short)16389),new ItemStack(Material.POTION,2,(short)16396)})
			}),
			new Kit( "§eStoßer",new String[]{"§8x1§7 Holzschwert mit Rückstoß 2"}, new ItemStack(Material.WOOD_SWORD),kPermission.SKYWARS_KIT_STOßER,KitType.KAUFEN,2000,500,new Perk[]{
				new PerkEquipment(new ItemStack[]{UtilItem.EnchantItem(new ItemStack(Material.WOOD_SWORD), Enchantment.KNOCKBACK,1)})
			}),
			new Kit( "§eHase",new String[]{"§8x2§7 Schnelligkeits Treanke","§8x2§7 Sprungkraft Treanke","§8x8§7 Kartotten"}, new ItemStack(Material.CARROT),kPermission.SKYWARS_KIT_HASE,KitType.KAUFEN,2000,500,new Perk[]{
				new PerkEquipment(new ItemStack[]{new ItemStack(Material.POTION,2,(byte)0),new ItemStack(Material.POTION,2,(byte)0),new ItemStack(Material.CARROT,8)})
			}),
			new Kit( "§eRusher",new String[]{"§8x25§7 Granit Bloecke","§8x1§7 Steinschwert Schaerfe 1","§8x1§7 Goldbrustpanzer"}, new ItemStack(Material.GOLD_CHESTPLATE),kPermission.SKYWARS_KIT_RUSHER,KitType.KAUFEN,2000,500,new Perk[]{
				new PerkEquipment(new ItemStack[]{new ItemStack(Material.COBBLESTONE,25),new ItemStack(Material.GOLD_CHESTPLATE),UtilItem.EnchantItem(new ItemStack(Material.STONE_SWORD), Enchantment.DAMAGE_ALL, 1)})
			}),
			new Kit( "§ePolizist",new String[]{"§8x1§7 Lederbrustpanzer","§8x1§7 Lederhose","§8x1§7 Stock mit Schaerfe 2"}, UtilItem.LSetColor(new ItemStack(Material.LEATHER_CHESTPLATE), DyeColor.BLUE),kPermission.SKYWARS_KIT_POLIZIST,KitType.KAUFEN,2000,500,new Perk[]{
				new PerkEquipment(new ItemStack[]{UtilItem.LSetColor(new ItemStack(Material.LEATHER_CHESTPLATE), DyeColor.BLUE),UtilItem.LSetColor(new ItemStack(Material.LEATHER_LEGGINGS), DyeColor.BLUE),UtilItem.RenameItem(UtilItem.EnchantItem(new ItemStack(Material.STICK), Enchantment.DAMAGE_ALL,2), "§7Knueppel")})
			}),
			new Kit( "§eFarmer",new String[]{"§8x1§7 Eisenspitzhacke mit Schaerfe 2 u. Efficiens 2","§8x16§7 Eier"}, new ItemStack(Material.IRON_PICKAXE),kPermission.SKYWARS_KIT_FARMER,KitType.KAUFEN,2000,500,new Perk[]{
				new PerkEquipment(new ItemStack[]{new ItemStack(Material.EGG,16),UtilItem.EnchantItem(UtilItem.EnchantItem(new ItemStack(Material.IRON_PICKAXE), Enchantment.DIG_SPEED, 2), Enchantment.DAMAGE_ALL,2)})
			}),
			new Kit( "§eFischer",new String[]{"§8x1§7 Angel mit unbreaking 2","§8x1§7 Kettenrüstung mit Schutz 2"}, new ItemStack(Material.CHAINMAIL_CHESTPLATE),kPermission.SKYWARS_KIT_FISCHER,KitType.KAUFEN,2000,500,new Perk[]{
				new PerkEquipment(new ItemStack[]{UtilItem.EnchantItem(new ItemStack(Material.FISHING_ROD), Enchantment.DURABILITY,2),UtilItem.EnchantItem(new ItemStack(Material.CHAINMAIL_CHESTPLATE), Enchantment.PROTECTION_ENVIRONMENTAL,2)})
			}),
			new Kit( "§eVip",new String[]{"§8x1§7 Steinschwert","§8x1§7 Bogen","§8x8§7 Pfeile","§8x2§7 Heal Potions"}, new ItemStack(Material.GOLD_NUGGET),kPermission.SKYWARS_KIT_VIP,KitType.VIP,0,0,new Perk[]{
				new PerkEquipment(new ItemStack[]{new ItemStack(Material.STONE_SWORD),new ItemStack(Material.BOW),new ItemStack(Material.ARROW,8),new Potion(PotionType.INSTANT_HEAL, Tier.TWO, true).toItemStack(2)})
			}),
			new Kit( "§eUltra",new String[]{"§8x1§7 Steinschwert mit Schaerfe 1","§8x1§7 Eisenpickaxe","§8x16§7 Steine","§8x4§7 Heal Potions"}, new ItemStack(Material.GOLD_INGOT),kPermission.SKYWARS_KIT_ULTRA,KitType.ULTRA,0,0,new Perk[]{
				new PerkEquipment(new ItemStack[]{UtilItem.EnchantItem(new ItemStack(Material.STONE_SWORD), Enchantment.DAMAGE_ALL, 1),new ItemStack(Material.IRON_PICKAXE),new ItemStack(Material.STONE,16),new Potion(PotionType.INSTANT_HEAL, Tier.TWO, true).toItemStack(4)})
			}),
			new Kit( "§eLegend",new String[]{"§8x1§7 Eisenpanzer","§8x1§7 Eisenschuhe","§8x1§7 Steinschwert","§8x8§7 Steaks","§8x4§7 Heal Potions"}, new ItemStack(Material.IRON_INGOT),kPermission.SKYWARS_KIT_LEGEND,KitType.LEGEND,0,0,new Perk[]{
				new PerkEquipment(new ItemStack[]{new ItemStack(Material.STONE_SWORD),new ItemStack(Material.IRON_CHESTPLATE),new ItemStack(Material.IRON_BOOTS),new ItemStack(Material.COOKED_BEEF,8),new Potion(PotionType.INSTANT_HEAL, Tier.TWO, true).toItemStack(4)})
			}),
			new Kit( "§eMVP",new String[]{"§8x1§7 Steinschwert mit Rueckstoß","§8x1§7 Diamanthelm","§8x1§7 Diamnantschuhe","§8x4§7 Heal Potion"}, new ItemStack(Material.EMERALD),kPermission.SKYWARS_KIT_MVP,KitType.MVP,0,0,new Perk[]{
				new PerkEquipment(new ItemStack[]{UtilItem.EnchantItem(new ItemStack(Material.STONE_SWORD), Enchantment.KNOCKBACK, 1),new ItemStack(Material.DIAMOND_HELMET),new ItemStack(Material.DIAMOND_BOOTS),new Potion(PotionType.INSTANT_HEAL, Tier.TWO, true).toItemStack(4)})
			}),
			new Kit( "§eMVP+",new String[]{"§8x1§7 Diamanthelm","§8x1§7 Eisenbrustpanzer","§8x16§7 Steine","§8x8§7 Eisenschwert","§8x4§7 Heal Potion"}, new ItemStack(Material.DIAMOND),kPermission.SKYWARS_KIT_MVPPLUS,KitType.MVP_PLUS,0,0,new Perk[]{
				new PerkEquipment(new ItemStack[]{new ItemStack(Material.IRON_SWORD),new ItemStack(Material.DIAMOND_HELMET),new ItemStack(Material.STONE,16),new Potion(PotionType.INSTANT_HEAL, Tier.TWO, true).toItemStack(4)})
			}),
			new Kit( "§aSuperman",new String[]{"Der Superman ist das Beste kit in SkyWars!"}, new ItemStack(Material.DIAMOND_SWORD),kPermission.SKYWARS_KIT_SUPERMAN,KitType.ADMIN,2000,new Perk[]{
				new PerkNoHunger(),
				new PerkEquipment(new ItemStack[]{UtilItem.EnchantItem(new ItemStack(Material.IRON_SWORD,1), Enchantment.DAMAGE_ALL,4),new ItemStack(Material.BOW), new ItemStack(Material.LEATHER_HELMET,1), UtilItem.EnchantItem(new ItemStack(Material.IRON_CHESTPLATE,1), Enchantment.PROTECTION_ENVIRONMENTAL,3), UtilItem.EnchantItem(new ItemStack(Material.LEATHER_LEGGINGS,1), Enchantment.PROTECTION_ENVIRONMENTAL,4), new ItemStack(Material.LEATHER_BOOTS)}),
				new PerkSneakDamage(1),
				new PerkPoisen(10,50),
				new PerkHolzfäller(),
				new PerkNoFiredamage(),
				new PerkNoFalldamage(),
				new PerkArrowFire(80),
				new PerkDoubleJump(),
				new PerkNoKnockback(manager.getInstance()),
				new PerkNoExplosionDamage(),
				new PerkTNT(),
				new PerkHealByHit(60, 6),
				new PerkHeal(6),
				new PerkMoreHearth(6, 60),
				new PerkArrowInfinity(),
				new PerkWalkEffect(Effect.CRIT,10)
			})
		});
		
		getManager().DebugLog(l, this.getClass().getName());
	}
	
	//VILLAGER_RED RED_WOOL/EMERALD NORMAL_CHEST
	//SHEEP WOOL/BEDROCK ISLAND CHEST
	//SPAWN WOOL/REDSTONE

	@EventHandler
	public void load(WorldLoadEvent ev){
		int i=0;
		Chest[] chests;
		for(Team t : type.getTeam()){
			chests=new Chest[getWorldData().getLocs(getChestSpawn(t)).size()];
			
			for(Location loc : getWorldData().getLocs(getChestSpawn(t))){
				loc.getBlock().setType(Material.CHEST);
				chests[i]=((Chest)loc.getBlock().getState());
				i++;
			}
			i=0;
			
			fillIslandChests(t,chests);
		}
		
		Chest chest;
		for(Location loc : getWorldData().getLocs(Team.VILLAGE_RED)){
			loc.getBlock().setType(Material.CHEST);
			if(loc.getBlock().getState() instanceof Chest){
				chest=(Chest)loc.getBlock().getState();
				for (int nur = 0; nur < UtilMath.RandomInt(6,3); nur++) {
					chest.getInventory().setItem(emptySlot(chest.getInventory()), rdmItem()); 
				}
			}
		}
	}
	
	public ItemStack Sonstiges(){
		try{
			switch(UtilMath.r(39)){
			case 0: return new ItemStack(Material.ENDER_PEARL,UtilMath.RandomInt(4, 2));
			case 1: return new ItemStack(Material.GOLDEN_APPLE,1);
			case 2: return new ItemStack(Material.ARROW,UtilMath.RandomInt(64,32));
			case 3: return new ItemStack(Material.ARROW,UtilMath.RandomInt(64,32));
			case 4: return new ItemStack(Material.ARROW,UtilMath.RandomInt(64,32));
			case 5: return new ItemStack(Material.ARROW,UtilMath.RandomInt(64,32));
			case 6: return new ItemStack(Material.FISHING_ROD);
			case 7: return new ItemStack(Material.FISHING_ROD);
			case 8: return new ItemStack(Material.FISHING_ROD);
			case 9: return new ItemStack(Material.TNT);
			case 10: return new ItemStack(Material.TNT);
			case 11: return new ItemStack(Material.TNT);
			case 12:return new ItemStack(Material.POTION,UtilMath.RandomInt(3, 1),(short)16421);
			case 13:return new ItemStack(Material.POTION,UtilMath.RandomInt(3, 1),(short)16417);
			case 14:return new ItemStack(Material.POTION,UtilMath.RandomInt(3, 1),(short)16389);
			case 15:return new ItemStack(Material.POTION,UtilMath.RandomInt(3, 1),(short)16385);
			case 16:return new ItemStack(Material.POTION,UtilMath.RandomInt(3, 1),(short)16387);
			case 17:return new ItemStack(Material.POTION,UtilMath.RandomInt(3, 1),(short)16421);
			case 18:return new ItemStack(Material.POTION,UtilMath.RandomInt(3, 1),(short)16417);
			case 19:return new ItemStack(Material.POTION,UtilMath.RandomInt(3, 1),(short)16389);
			case 20:return new ItemStack(Material.POTION,UtilMath.RandomInt(3, 1),(short)16385);
			case 21:return new ItemStack(Material.POTION,UtilMath.RandomInt(3, 1),(short)16387);
			case 22:return new ItemStack(Material.SNOW_BALL,UtilMath.RandomInt(32,10));
			case 23:return new ItemStack(Material.SNOW_BALL,UtilMath.RandomInt(32,10));
			case 24:return new ItemStack(Material.SNOW_BALL,UtilMath.RandomInt(32,10));
			case 25:return new ItemStack(Material.COOKED_BEEF,UtilMath.RandomInt(12,8));
			case 26:return new ItemStack(Material.COOKED_CHICKEN,UtilMath.RandomInt(12,8));
			case 27:return new ItemStack(Material.COOKED_FISH,UtilMath.RandomInt(12,8));
			case 28:return new ItemStack(Material.COOKED_RABBIT,UtilMath.RandomInt(12,8));
			case 29:return new ItemStack(Material.BREAD,UtilMath.RandomInt(12,8));
			case 30:return new ItemStack(Material.COOKED_BEEF,UtilMath.RandomInt(12,8));
			case 31:return new ItemStack(Material.COOKED_CHICKEN,UtilMath.RandomInt(12,8));
			case 32:return new ItemStack(Material.COOKED_FISH,UtilMath.RandomInt(12,8));
			case 33:return new ItemStack(Material.COOKED_RABBIT,UtilMath.RandomInt(12,8));
			case 34:return new ItemStack(Material.BREAD,UtilMath.RandomInt(12,8));
			case 35: return new ItemStack(Material.ENDER_PEARL,UtilMath.RandomInt(4, 2));
			case 36: return new ItemStack(Material.ENDER_PEARL,UtilMath.RandomInt(4, 2));
			default: return new ItemStack(Material.STICK);
			}
		}catch(NullPointerException e){
			System.err.println("Error: ");
			e.printStackTrace();
			return new ItemStack(Material.BREAD,UtilMath.RandomInt(12,8));
		}
	}
	
	public ItemStack Tools(){
		switch(UtilMath.r(5)){
		case 0: return new ItemStack(Material.DIAMOND_SWORD);
		case 1: return new ItemStack(Material.IRON_SWORD);
		case 2: return new ItemStack(Material.DIAMOND_AXE);
		case 3: return new ItemStack(Material.DIAMOND_PICKAXE);
		case 4: return new ItemStack(Material.BOW);
		default: return new ItemStack(Material.WOOD_SWORD);
		}
	}
	
	public ItemStack DiaRüstung(){
		switch(UtilMath.r(4)){
		case 0: return new ItemStack(Material.DIAMOND_HELMET);
		case 1: return new ItemStack(Material.DIAMOND_CHESTPLATE);
		case 2: return new ItemStack(Material.DIAMOND_LEGGINGS);
		case 3: return new ItemStack(Material.DIAMOND_BOOTS);
		default:
			return new ItemStack(Material.LEATHER_HELMET);
		}
	}
	
	public ItemStack IronRüstung(){
		switch(UtilMath.r(4)){
		case 0: return new ItemStack(Material.IRON_HELMET);
		case 1: return new ItemStack(Material.IRON_CHESTPLATE);
		case 2: return new ItemStack(Material.IRON_LEGGINGS);
		case 3: return new ItemStack(Material.IRON_BOOTS);
		default:
			return new ItemStack(Material.LEATHER_HELMET);
		}
	}
	
	public ItemStack rdmItem(){
		ItemStack item;
		int r = UtilMath.r(1000);
		if(r>=150&&r<=155){
			item=UtilItem.RenameItem(new ItemStack(Material.SLIME_BALL), "§bX");
			item.addUnsafeEnchantment(Enchantment.KNOCKBACK, 5);
			return item;
		}
		
		switch(UtilMath.r(6)){
		case 0: item = Sonstiges();break;
		case 1: item = IronRüstung();break;
		case 2: item = DiaRüstung();break;
		case 3: item = Tools();break;
		case 4: item = Sonstiges();break;
		case 5: item = Sonstiges();break;
		default: item = new ItemStack(Material.APPLE);break;
		}
		
		if(item.getType()==Material.BOW){
			r=UtilMath.r(100);
			if(r>=0&&r<=40){
				item.addEnchantment(Enchantment.ARROW_DAMAGE, 3);
			}else if(r>=40&&r<=60){
				item.addEnchantment(Enchantment.ARROW_FIRE, 1);
			}
		}else if(item.getType()==Material.IRON_CHESTPLATE){
			r=UtilMath.r(100);
			if(r>=0&&r<=30){
				item.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
			}else if(r>=30&&r<=50){
				item.addEnchantment(Enchantment.PROTECTION_FIRE, 4);
			}
		}else if(item.getType()==Material.DIAMOND_CHESTPLATE){
			r=UtilMath.r(100);
			if(r>=0&&r<=40){
				item.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
			}else if(r>=30&&r<=50){
				item.addEnchantment(Enchantment.PROTECTION_FIRE, 4);
			}
		}else if(item.getType()==Material.IRON_SWORD){
			item.addEnchantment(Enchantment.DAMAGE_ALL, 3);
		}else if(item.getType()==Material.DIAMOND_SWORD){
			r=UtilMath.r(100);
			if(r>=0&&r<=40){
				item.addEnchantment(Enchantment.DAMAGE_ALL, 2);
			}else if(r>=40&&r<=60){
				item.addEnchantment(Enchantment.DAMAGE_ALL, 1);
				item.addEnchantment(Enchantment.FIRE_ASPECT, 1);
				item.addEnchantment(Enchantment.KNOCKBACK, 1);
			}
		}else if(item.getType()==Material.DIAMOND_AXE){
			item.addEnchantment(Enchantment.DIG_SPEED, 3);
		}else if(item.getType()==Material.DIAMOND_PICKAXE){
			item.addEnchantment(Enchantment.DIG_SPEED, 3);
		}
		return item;
	}
	
	public Team getChestSpawn(Team team){
		switch(team){
		case RED:return Team.SHEEP_RED;
		case BLUE:return Team.SHEEP_BLUE;
		case YELLOW:return Team.SHEEP_YELLOW;
		case GREEN:return Team.SHEEP_GREEN;
		case GRAY:return Team.SHEEP_GRAY;
		case PINK:return Team.SHEEP_PINK;
		case ORANGE:return Team.SHEEP_ORANGE;
		case PURPLE:return Team.SHEEP_PURPLE;
		case WHITE:return Team.SHEEP_WHITE;
		case BLACK:return Team.SHEEP_BLACK;
		case CYAN:return Team.SHEEP_CYAN;
		case AQUA:return Team.SHEEP_AQUA;
		default:
		return Team.SHEEP_RED;
		}
	}
	
	public Material rdmBlock(){
		switch(UtilMath.r(3)){
		case 0:return Material.STONE;
		case 1:return Material.DIRT;
		case 2:return Material.WOOD;
		default: return Material.BEDROCK;
		}
	}
	
	public ItemStack rdmFood(){
		switch(UtilMath.r(4)){
		case 0:return new ItemStack(Material.COOKED_BEEF,UtilMath.RandomInt(16, 8));
		case 1:return new ItemStack(Material.BREAD,UtilMath.RandomInt(16, 8));
		case 2:return new ItemStack(Material.CAKE);
		case 3:return new ItemStack(Material.CARROT,UtilMath.RandomInt(16, 8));
		default: return new ItemStack(Material.GOLDEN_CARROT,UtilMath.RandomInt(16, 8));
		}
	}
	
	public Material rdmHelm(){
		switch(UtilMath.r(4)){
		case 0:return Material.DIAMOND_HELMET;
		case 1:return Material.GOLD_HELMET;
		case 2:return Material.IRON_HELMET;
		case 3:return Material.CHAINMAIL_HELMET;
		default: return Material.LEATHER_HELMET;
		}
	}
	
	public Material rdmChestplate(){
		switch(UtilMath.r(4)){
		case 0:return Material.DIAMOND_CHESTPLATE;
		case 1:return Material.GOLD_CHESTPLATE;
		case 2:return Material.IRON_CHESTPLATE;
		case 3:return Material.CHAINMAIL_CHESTPLATE;
		default: return Material.LEATHER_CHESTPLATE;
		}
	}
	
	public Material rdmLeggings(){
		switch(UtilMath.r(4)){
		case 0:return Material.DIAMOND_LEGGINGS;
		case 1:return Material.GOLD_LEGGINGS;
		case 2:return Material.IRON_LEGGINGS;
		case 3:return Material.CHAINMAIL_LEGGINGS;
		default: return Material.LEATHER_LEGGINGS;
		}
	}
	
	public Material rdmTool(){
		switch(2){
		case 0: return Material.IRON_PICKAXE;
		case 1: return Material.IRON_AXE;
		default: return Material.DIAMOND_PICKAXE;
		}
	}
	
	public Material rdmBoots(){
		switch(UtilMath.r(4)){
		case 0:return Material.DIAMOND_BOOTS;
		case 1:return Material.GOLD_BOOTS;
		case 2:return Material.IRON_BOOTS;
		case 3:return Material.CHAINMAIL_BOOTS;
		default: return Material.LEATHER_BOOTS;
		}
	}
	
	public ItemStack rdmPotion(){
		switch(UtilMath.r(5)){
		case 0:return new ItemStack(Material.POTION,UtilMath.RandomInt(3, 1),(short)16421);
		case 1:return new ItemStack(Material.POTION,UtilMath.RandomInt(3, 1),(short)16417);
		case 2:return new ItemStack(Material.POTION,UtilMath.RandomInt(3, 1),(short)16389);
		case 3:return new ItemStack(Material.POTION,UtilMath.RandomInt(3, 1),(short)16385);
		case 4:return new ItemStack(Material.POTION,UtilMath.RandomInt(3, 1),(short)16387);
		default: return new ItemStack(Material.POTION,1);
		}
	}
	
	private HashMap<Chest,ArrayList<String>> template = new HashMap<>();
	private HashMap<String,Integer> template_type = new HashMap<>();
	public void fillIslandChests(Team t,Chest[] chests){
		//SWORD BLOCK HELM CHESTPLATE LEGGINGS BOOTS BOW ARROW POTION FOOD SNOWBALL EGG WEB LAVA-BUCKET WATER-BUCKET
		template.clear();
		template_type.clear();
		template_type.put("SWORD",3);
		template_type.put("BLOCK",3);
		template_type.put("HELM",2);
		template_type.put("CHESTPLATE",2);
		template_type.put("LEGGINGS",2);
		template_type.put("BOOTS",2);
		template_type.put("ARROW",1);
		template_type.put("POTION",4);
		template_type.put("FOOD",3);
		template_type.put("SNOWBALL",3);
		template_type.put("EGG",3);
		template_type.put("WEB",3);
		template_type.put("LAVA-BUCKET",1);
		template_type.put("WATER-BUCKET",1);
		template_type.put("TNT",1);
		template_type.put("FIRE",1);
		template_type.put("BOW",1);
		template_type.put("TOOL",2);
		
		for(Chest chest : chests)template.put(chest, new ArrayList<String>());
		
		
		if(UtilMath.r(100)>70){
			add( (Chest)template.keySet().toArray()[UtilMath.r(template.size())] ,"BOW");
			add( (Chest)template.keySet().toArray()[UtilMath.r(template.size())] ,"ARROW");
		}else{
			template_type.remove("BOW");
			template_type.remove("ARROW");
		}

		add( (Chest)template.keySet().toArray()[UtilMath.r(template.size())] ,"SWORD");
		add( (Chest)template.keySet().toArray()[UtilMath.r(template.size())] ,"BLOCK");
		add( (Chest)template.keySet().toArray()[UtilMath.r(template.size())] ,"TOOL");
		add( (Chest)template.keySet().toArray()[UtilMath.r(template.size())] ,"POTION");
		add( (Chest)template.keySet().toArray()[UtilMath.r(template.size())] ,"CHESTPLATE");
		add( (Chest)template.keySet().toArray()[UtilMath.r(template.size())] ,"FOOD");
		
		int r;
		ArrayList<String> list;
		for(Chest chest : template.keySet()){
			list=template.get(chest);
			r=UtilMath.RandomInt(8, 5);
			if(r<=list.size())continue;
			for(int i = 0; i < (r-list.size()); i++){
				add(chest, (String)this.template_type.keySet().toArray()[UtilMath.r(this.template_type.size())]);
			}
		}
		
		for(Chest chest : template.keySet()){
			for(String i : template.get(chest)){
				switch(i){
				case "SWORD":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(UtilItem.rdmSchwert()) );
					break;
				case "HELM":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(rdmHelm()));
					break;
				case "CHESTPLATE":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(rdmChestplate()));
					break;
				case "LEGGINGS":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(rdmLeggings()));
					break;
				case "BOOTS":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(rdmBoots()));
					break;
				case "BOW":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(Material.BOW));
					break;
				case "ARROW":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(Material.ARROW,UtilMath.RandomInt(16, 8)));
					break;
				case "BLOCK":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(rdmBlock(),UtilMath.RandomInt(32, 16)));
					break;
				case "POTION":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(rdmPotion()));
					break;
				case "FOOD":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , rdmFood());
					break;
				case "SNOWBALL":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(Material.SNOW_BALL,UtilMath.RandomInt(16, 8)));
					break;
				case "EGG":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(Material.EGG,UtilMath.RandomInt(16, 8)));
					break;
				case "WEB":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(Material.WEB,UtilMath.RandomInt(16, 8)));
					break;
				case "LAVA-BUCKET":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(Material.LAVA_BUCKET));
					break;
				case "WATER-BUCKET":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(Material.WATER_BUCKET));
					break;
				case "TNT":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(Material.TNT,UtilMath.RandomInt(3, 1)));
					break;
				case "FIRE":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(Material.FLINT_AND_STEEL));
					break;
				case "TOOL":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(rdmTool()));
					break;
				default:
					System.out.println("Island Chest FAIL: "+i);
					break;
				}
			}
			template.get(chest).clear();
		}
		
		for(Chest chest : template.keySet()){
			for(ItemStack item : chest.getInventory().getContents()){
				if(item!=null&&item.getType()!=Material.AIR){
					if(item.getType()==Material.BOW){
						switch(UtilMath.r(3)){
						case 0:item.addEnchantment(Enchantment.ARROW_DAMAGE, 1);break;
						case 1:item.addEnchantment(Enchantment.ARROW_DAMAGE, 3);break;
						case 2:item.addEnchantment(Enchantment.ARROW_DAMAGE, 1);item.addEnchantment(Enchantment.ARROW_KNOCKBACK, 1);break;
						default:break;
						}
					}else if(UtilItem.isSword(item)){
						if(item.getType()==Material.WOOD_SWORD){
							item.addEnchantment(Enchantment.DAMAGE_ALL, 2);
						}else if(item.getType()==Material.DIAMOND_SWORD){
							r=UtilMath.r(100);
							if(r>=50&&r<=55){
								item.addEnchantment(Enchantment.FIRE_ASPECT, 1);
							}else if(r>=0&&r<=40){
								item.addEnchantment(Enchantment.DAMAGE_ALL, 1);
							}
						}else if(item.getType()==Material.GOLD_SWORD){
							r=UtilMath.r(100);
							item.addEnchantment(Enchantment.DURABILITY, 1);
							if(r>=0&&r<=60){
								item.addEnchantment(Enchantment.DAMAGE_ALL, 2);
							}else if(r>=60&&r<=100){
								item.addEnchantment(Enchantment.KNOCKBACK, 1);
							}
						}else if(item.getType()==Material.STONE_SWORD){
							item.addEnchantment(Enchantment.DAMAGE_ALL, 1);
						}else if(item.getType()==Material.IRON_SWORD){
							r=UtilMath.r(100);
							if(r>=40&&r<=80){
								item.addEnchantment(Enchantment.DAMAGE_ALL, 2);
							}
						}
					}else if(UtilItem.isArmor(item)){
						if(UtilItem.isGoldArmor(item)){
							r=UtilMath.r(100);
							if(r>=0&&r<=60){
								item.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
							}else{
								item.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
							}
						}else if(UtilItem.isChainmailArmor(item)){
							if(UtilItem.isChestplate(item)){
								r=UtilMath.r(100);
								if(r>=60&&r<=80){
									item.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
								}
							}
							item.addEnchantment(Enchantment.PROTECTION_FIRE, 3);
						}else if(UtilItem.isIronArmor(item)){
							r=UtilMath.r(100);
							if(r>=60&&r<=80){
								item.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
							}
							item.addEnchantment(Enchantment.PROTECTION_PROJECTILE, 3);
						}
					}
				}
			}
		}
	}
	
	public int emptySlot(Inventory inv){
		int slot=0;
		for(int i = 0 ; i<2000; i++){
			slot=UtilMath.r(inv.getSize());
			if(inv.getItem(slot)==null||inv.getItem(slot).getType()==Material.AIR){
				return slot;
			}
		}
		System.out.println("NOT FIND A EMPTY SLOT");
		return 0;
	}
	
	int a=0;
	public void add(Chest g,String type){
		template.get(g).add(type);
		a=template_type.get(type);
		a--;
		template_type.remove(type);
		if(a!=0)template_type.put(type, a);
		a=0;
	}
	
	@EventHandler
	public void Enchant(InventoryOpenEvent ev){
		if(ev.getInventory() instanceof EnchantingInventory){
			EnchantingInventory inv = (EnchantingInventory)ev.getInventory();
			inv.setSecondary(new ItemStack(351,16 ,(short)4));
		}
	}
	
	@EventHandler
	public void Chat(AsyncPlayerChatEvent ev){
		if(ev.isCancelled())return;
		ev.setCancelled(true);
		
		if((!ev.getPlayer().hasPermission(kPermission.CHAT_LINK.getPermissionToString()))&&UtilString.isBadWord(ev.getMessage())||UtilString.checkForIP(ev.getMessage())){
			ev.setMessage("Ich heul rum!");
			ev.getPlayer().sendMessage(Language.getText(ev.getPlayer(), "PREFIX")+Language.getText(ev.getPlayer(), "CHAT_MESSAGE_BLOCK"));
		}
		
		if(getState()!=GameState.LobbyPhase&&getGameList().getPlayers(PlayerState.OUT).contains(ev.getPlayer())){
			ev.setCancelled(true);
			UtilPlayer.sendMessage(ev.getPlayer(),Language.getText(ev.getPlayer(), "PREFIX_GAME", getType().getTyp())+Language.getText(ev.getPlayer(), "SPECTATOR_CHAT_CANCEL"));
		}else{
			UtilServer.broadcast(getManager().getPermManager().getPrefix(ev.getPlayer())+ev.getPlayer().getDisplayName()+":§7 "+ev.getMessage());
		}
	}
	
	@EventHandler
	public void inGame(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		if(getState()!=GameState.InGame)return;
		setStart(getStart()-1);
		for(Player p : UtilServer.getPlayers())UtilDisplay.displayTextBar(Language.getText(p, "GAME_END_IN", UtilTime.formatSeconds(getStart())), p);
		switch(getStart()){
		case 300: 
			
			Chest chest;
			for(Team t : type.getTeam()){
				for(Location loc : getWorldData().getLocs(getChestSpawn(t))){
					if(loc.getBlock().getState() instanceof Chest){
						chest=(Chest)loc.getBlock().getState();
						for (int nur = 0; nur < UtilMath.RandomInt(6,3); nur++) {
							chest.getInventory().setItem(emptySlot(chest.getInventory()), rdmItem()); 
						}
					}
				}
			}
			
			for(Location loc : getWorldData().getLocs(Team.VILLAGE_RED)){
				loc.getBlock().setType(Material.CHEST);
				if(loc.getBlock().getState() instanceof Chest){
					chest=(Chest)loc.getBlock().getState();
					for (int nur = 0; nur < UtilMath.RandomInt(6,3); nur++) {
						chest.getInventory().setItem(emptySlot(chest.getInventory()), rdmItem()); 
					}
				}
			}
			
			broadcastWithPrefix("§eDie Kisten wurden wieder gefüllt");
			break;
		case 30: broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getStart()));break;
		case 15: broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getStart()));break;
		case 10: broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getStart()));break;
		case 5: broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getStart()));break;
		case 4: broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getStart()));break;
		case 3: broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getStart()));break;
		case 2: broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getStart()));break;
		case 1: broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getStart()));break;
		case 0:
			broadcastWithPrefixName("GAME_END");
			setState(GameState.Restart);
			break;
		}
	}
	
	@EventHandler
	public void ShopOpen(PlayerInteractEvent ev){
		if(UtilEvent.isAction(ev, ActionType.R)){
			if(getState()!=GameState.LobbyPhase)return;
			if(ev.getPlayer().getItemInHand()!=null&&UtilItem.ItemNameEquals(ev.getPlayer().getItemInHand(), UtilItem.RenameItem(new ItemStack(Material.CHEST), "§bKitShop"))){
				ev.getPlayer().openInventory(kitshop.getInventory());
			}
		}
	}
	
	@EventHandler
	public void DeathSkyWars(PlayerDeathEvent ev){
		if(ev.getEntity() instanceof Player){
			Player v = (Player)ev.getEntity();
			
			getStats().setInt(v, getStats().getInt(Stats.LOSE, v)+1, Stats.LOSE);
			getGameList().addPlayer(v, PlayerState.OUT);
			
			if(ev.getEntity().getKiller() instanceof Player){
				Player a = (Player)ev.getEntity().getKiller();
				int k = kills.get(a.getName());
				k++;
				kills.remove(a.getName());
				kills.put(a.getName(), k);
				getStats().setInt(a, getStats().getInt(Stats.KILLS, a)+1, Stats.KILLS);
				UtilScoreboard.resetScore(a.getScoreboard(), "§e"+ (kills.get(a.getName())-1) , DisplaySlot.SIDEBAR);
				UtilScoreboard.setScore(a.getScoreboard(), "§e"+kills.get(a.getName()), DisplaySlot.SIDEBAR, 5);
				getCoins().addCoins(a, false, 5);
				broadcastWithPrefix("KILL_BY", new String[]{v.getName(),a.getName()});
				return;
			}
			broadcastWithPrefix("DEATH", v.getName());
			getStats().setInt(v, getStats().getInt(Stats.DEATHS, v)+1, Stats.DEATHS);
			
			if(type.getTeam_size()>1){
				Player p = TeamPartner(v);
				if(p==null)return;
				if(v.getName().length()>13){
					UtilScoreboard.resetScore(p.getScoreboard(), "§e"+v.getName().substring(13), DisplaySlot.SIDEBAR);
					UtilScoreboard.setScore(p.getScoreboard(), "§c"+v.getName().substring(13), DisplaySlot.SIDEBAR, 8);
				}else{
					UtilScoreboard.resetScore(p.getScoreboard(), "§e"+v.getName(), DisplaySlot.SIDEBAR);
					UtilScoreboard.setScore(p.getScoreboard(), "§c"+v.getName(), DisplaySlot.SIDEBAR, 8);
				}
			}
			
		}
	}
	
	public Player TeamPartner(Player p){
		for(Player p1 : getPlayerFrom(getTeam(p))){
			if(p1==p)continue;
			return p1;
		}
		return null;
	}
	
	@EventHandler
	public void GameStateChangeSkyWars(GameStateChangeEvent ev){
		if(ev.getTo()==GameState.Restart){
			ArrayList<Player> list = getGameList().getPlayers(PlayerState.IN);
			if(list.size()==1){
				Player p = list.get(0);
				getStats().setInt(p, getStats().getInt(Stats.WIN, p)+1, Stats.WIN);
				getCoins().addCoins(p, false, 25);
				broadcastWithPrefix("GAME_WIN", p.getName());
				new Title("§6§lGEWONNEN").send(p);
			}else if(list.size()==2){
				Player p = list.get(0);
				Player p1 = list.get(1);
				getStats().setInt(p, getStats().getInt(Stats.WIN, p)+1, Stats.WIN);
				getStats().setInt(p1, getStats().getInt(Stats.WIN, p1)+1, Stats.WIN);
				getCoins().addCoins(p, false, 25);
				getCoins().addCoins(p1, false, 25);
				new Title("§6§lGEWONNEN").send(p);
				new Title("§6§lGEWONNEN").send(p1);
			}
			
//			if(kills.size()>=3){
//				String p;
//				kills=UtilList.getRanked(kills, 3);
//				for(int i = 0; i<kills.size(); i++){
//					p=(String)kills.keySet().toArray()[i];
//					if(i==0){
//						broadcast("§6§l1. Platz§8 "+p+" §7Kills: "+kills.get(p));
//					}else if(i==1){
//						broadcast("§e§l2. Platz§8 "+p+" §7Kills: "+kills.get(p));
//					}else if(i==2){
//						broadcast("§c§l3. Platz§8 "+p+" §7Kills: "+kills.get(p));
//						break;
//					}
//				}
//			}
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
			" ",
			Language.getText(ev.getPlayer(), "GAME_HOLOGRAM_GAMES", (win+lose)),
			Language.getText(ev.getPlayer(), "GAME_HOLOGRAM_WINS", win),
			Language.getText(ev.getPlayer(), "GAME_HOLOGRAM_LOSE", lose),
			});
		ev.getPlayer().getInventory().addItem(UtilItem.RenameItem(new ItemStack(Material.CHEST), "§bKitShop"));
	}
	
	@EventHandler
	public void GameStartSkyWars(GameStartEvent ev){
		getWorldData().clearWorld();
		
		HashMap<Player,String> kits = new HashMap<>();
		
		for(Kit kit : kitshop.getKits()){
			kit.StartGame();
			for(Perk perk : kit.getPlayers().keySet()){
				for(Player p : kit.getPlayers().get(perk))kits.put(p, kit.getName());
				break;
			}
		}
		
		ArrayList<Player> plist = new ArrayList<>();
		for(Player p : UtilServer.getPlayers()){
			getManager().Clear(p);
			getGameList().addPlayer(p,PlayerState.IN);
			kills.put(p.getName(), 0);
			plist.add(p);
			for(Player player : UtilServer.getPlayers()){
				p.showPlayer(player);
				player.showPlayer(p);
			}
		}
		PlayerVerteilung(verteilung(type.getTeam(),type.getTeam_size()), plist);
		
		for(Player player : getTeamList().keySet()){
			player.teleport(getWorldData().getLocs(getTeamList().get(player)).get(0));
		}

		Scoreboard ps;
		Title title = new Title("", "");
		for(Player p : UtilServer.getPlayers()){
			ps=Bukkit.getScoreboardManager().getNewScoreboard();
			
			UtilScoreboard.addBoard(ps, DisplaySlot.SIDEBAR, "§6§lEpicPvP.eu");
			if(type.getTeam_size()>1){
				Player p1 = TeamPartner(p);
				
				if(p1!=null){
					if(p1.getName().length()>13){
						UtilScoreboard.setScore(ps, "§e"+p1.getName().substring(13), DisplaySlot.SIDEBAR, 8);
					}else{
						UtilScoreboard.setScore(ps, "§e"+p1.getName(), DisplaySlot.SIDEBAR, 8);
					}
					

					UtilScoreboard.setScore(ps, "§7Team-Partner: ", DisplaySlot.SIDEBAR, 9);
					UtilScoreboard.setScore(ps, "   ", DisplaySlot.SIDEBAR, 7);
				}
			}
			UtilScoreboard.setScore(ps, "§7Kills: ", DisplaySlot.SIDEBAR, 6);
			UtilScoreboard.setScore(ps, "§e"+kills.get(p.getName()),DisplaySlot.SIDEBAR,5);
			UtilScoreboard.setScore(ps, "  ", DisplaySlot.SIDEBAR, 4);
			UtilScoreboard.setScore(ps, "§7Kit: ", DisplaySlot.SIDEBAR, 3);
			UtilScoreboard.setScore(ps, "§e"+ (kits.containsKey(p) ? kits.get(p) : Language.getText(p, "NO_KIT")) , DisplaySlot.SIDEBAR,2);
			UtilScoreboard.setScore(ps, " ", DisplaySlot.SIDEBAR, 1);
			UtilScoreboard.setScore(ps, "§ewww.EpicPvP.me", DisplaySlot.SIDEBAR, 0);
			UtilScoreboard.addTeam(ps, "friend", Color.GREEN);
			UtilScoreboard.addTeam(ps, "enemy", Color.RED);
			
			for(Player player : UtilServer.getPlayers()){
				if(getTeam(p)!=getTeam(player)){
					UtilScoreboard.addPlayerToTeam(ps, "enemy", player);
				}else{
					UtilScoreboard.addPlayerToTeam(ps, "friend", player);
				}
			}
			
			p.setScoreboard(ps);
			title.setSubtitle(Language.getText(p, "NO_TEAMS_ALLOWED"));
			title.send(p);
		}
		
		for(Player player : UtilServer.getPlayers()){
			if(player.getWorld().getName().equalsIgnoreCase("world")){
				System.out.println("WORLD: "+player.getName());
				getManager().getMysql().Update("INSERT INTO list_exception (server,ip,time,exceptiontype,message) VALUES ('a"+kArcade.id+"','null','"+UtilTime.now()+"','SkyWars Spieler Verteilung','Spieler: "+player.getName()+" ANZAHL:"+UtilServer.getPlayers().size()+" VER:"+getERR(type.getTeam(),type.getTeam_size())+"');");
				UtilBG.sendToServer(player, getManager().getInstance());
			}
		}
		
		if(getManager().getHoliday()!=null){
			switch(getManager().getHoliday()){
			case HALLOWEEN:
				new AddonNight(getManager().getInstance(),getWorldData().getWorld());
				break;
			case WEIHNACHTEN:
				new AddonDay(getManager().getInstance(),getWorldData().getWorld());
				break;
			default:
				new AddonDay(getManager().getInstance(),getWorldData().getWorld());
				break;
			}
		}


		getWorldData().getWorld().setStorm(false);
		setStart((60*15)+1);
		setState(GameState.InGame);
	}
	
	@EventHandler
	public void Ranking(RankingEvent ev){
		getManager().setRanking(Stats.WIN);
	}

}
