package eu.epicpvp.karcade.Game.Single.Games.SkyWars;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.Potion.Tier;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

import dev.wolveringer.dataserver.gamestats.GameState;
import dev.wolveringer.dataserver.gamestats.GameType;
import dev.wolveringer.dataserver.gamestats.StatsKey;
import eu.epicpvp.karcade.kArcade;
import eu.epicpvp.karcade.kArcadeManager;
import eu.epicpvp.karcade.Events.RankingEvent;
import eu.epicpvp.karcade.Game.Events.GameStartEvent;
import eu.epicpvp.karcade.Game.Events.GameStateChangeEvent;
import eu.epicpvp.karcade.Game.Multi.Games.SkyWars1vs1.UtilSkyWars1vs1;
import eu.epicpvp.karcade.Game.Single.GameMapVote;
import eu.epicpvp.karcade.Game.Single.SingleWorldData;
import eu.epicpvp.karcade.Game.Single.Addons.AddonTargetNextPlayer;
import eu.epicpvp.karcade.Game.Single.Addons.AddonVoteTeam;
import eu.epicpvp.karcade.Game.Single.Events.AddonVoteTeamPlayerChooseEvent;
import eu.epicpvp.karcade.Game.Single.Games.TeamGame;
import eu.epicpvp.karcade.Game.Single.Games.SkyWars.LuckyWars.Items.LuckyCow;
import eu.epicpvp.karcade.Game.Single.Games.SkyWars.LuckyWars.Items.LuckyCreeper;
import eu.epicpvp.karcade.Game.Single.Games.SkyWars.LuckyWars.Items.LuckyHeart;
import eu.epicpvp.karcade.Game.Single.Games.SkyWars.LuckyWars.Items.LuckyItem;
import eu.epicpvp.karcade.Game.Single.Games.SkyWars.LuckyWars.Items.LuckyPotion;
import eu.epicpvp.karcade.Game.Single.Games.SkyWars.LuckyWars.Items.LuckyWolf;
import eu.epicpvp.karcade.Game.World.Event.WorldDataInitializeEvent;
import eu.epicpvp.kcore.Addons.AddonDay;
import eu.epicpvp.kcore.Addons.AddonNight;
import eu.epicpvp.kcore.Calendar.Calendar;
import eu.epicpvp.kcore.Calendar.Calendar.CalendarType;
import eu.epicpvp.kcore.Enum.GameCage;
import eu.epicpvp.kcore.Enum.PlayerState;
import eu.epicpvp.kcore.Enum.Team;
import eu.epicpvp.kcore.Events.ServerStatusUpdateEvent;
import eu.epicpvp.kcore.Inventory.InventoryPageBase;
import eu.epicpvp.kcore.Kit.Kit;
import eu.epicpvp.kcore.Kit.Perk;
import eu.epicpvp.kcore.Kit.Shop.SingleKitShop;
import eu.epicpvp.kcore.LaunchItem.LaunchItemManager;
import eu.epicpvp.kcore.Lists.kSort;
import eu.epicpvp.kcore.Permission.PermissionType;
import eu.epicpvp.kcore.StatsManager.Event.PlayerStatsLoadedEvent;
import eu.epicpvp.kcore.Translation.TranslationHandler;
import eu.epicpvp.kcore.Update.UpdateType;
import eu.epicpvp.kcore.Update.Event.UpdateEvent;
import eu.epicpvp.kcore.Util.Color;
import eu.epicpvp.kcore.Util.InventorySize;
import eu.epicpvp.kcore.Util.Title;
import eu.epicpvp.kcore.Util.UtilBG;
import eu.epicpvp.kcore.Util.UtilDisplay;
import eu.epicpvp.kcore.Util.UtilEvent;
import eu.epicpvp.kcore.Util.UtilEvent.ActionType;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.kcore.Util.UtilMap;
import eu.epicpvp.kcore.Util.UtilMath;
import eu.epicpvp.kcore.Util.UtilPlayer;
import eu.epicpvp.kcore.Util.UtilScoreboard;
import eu.epicpvp.kcore.Util.UtilServer;
import eu.epicpvp.kcore.Util.UtilString;
import eu.epicpvp.kcore.Util.UtilTime;
import eu.epicpvp.kcore.Util.UtilWorldEdit;
import lombok.Getter;

public class SkyWars extends TeamGame{

	@Getter
	private SkyWarsType skyWarsType;
	private SingleKitShop kitshop;
	private HashMap<String,Integer> kills =  new HashMap<>();
	private ArrayList<kSort<String>> ranking;
	private HashMap<Player,Player> hit =  new HashMap<>();
	@Getter
	private LaunchItemManager ilManager;
	private AddonTargetNextPlayer targetNextPlayer;
	private InventoryPageBase choose_type;
	
	public SkyWars(kArcadeManager manager,SkyWarsType skyWarsType){
		super(manager);
		long l = System.currentTimeMillis();
		this.skyWarsType=skyWarsType;
		this.ilManager=new LaunchItemManager(getManager().getInstance());
		this.ranking=new ArrayList<>();
		setTyp(GameType.SkyWars);
		setMax_Players(skyWarsType.getMax());
		setMin_Players(skyWarsType.getMin());
		setDamage(false);
		setDamagePvP(false);
		setDamageSelf(false);
		setBlockBreak(true);
		setBlockPlace(true);
		setCreatureSpawn(false);
		setCompassAddon(true);
		setDeathDropItems(true);
		setFoodChange(true);
		setItemPickup(true);
		setItemDrop(true);
		setRespawn(true);
		setWorldData(new SingleWorldData(manager,getType().getTyp()+skyWarsType.getTeam().length,getType().getShortName()));
		getWorldData().setCleanroomChunkGenerator(true);
		
		if(getSkyWarsType().isLuckywars())getVoteHandler().add(new SkyWarsTypeVote(this, new LuckyItem[]{
				new LuckyCow(45),
				new LuckyCreeper(35),
				new LuckyWolf(40),
				new LuckyHeart(35),
				new LuckyItem( UtilItem.EnchantItem(UtilItem.RenameItem(new ItemStack(Material.DIAMOND_SWORD), "§6Lucky Sword")
						, new String[]{Enchantment.FIRE_ASPECT.getName()+":1",Enchantment.DAMAGE_ALL.getName()+":2"}) , 16),
				new LuckyItem( UtilItem.EnchantItem(UtilItem.RenameItem(new ItemStack(Material.DIAMOND_SWORD), "§6Lucky Sword")
						, new String[]{Enchantment.DAMAGE_ALL.getName()+":3"}) , 12),
				new LuckyItem( UtilItem.EnchantItem(UtilItem.RenameItem(new ItemStack(Material.DIAMOND_SWORD), "§6Lucky Sword")
						, new String[]{Enchantment.KNOCKBACK.getName()+":2",Enchantment.DAMAGE_ALL.getName()+":2"}) , 10),
				new LuckyItem( UtilItem.EnchantItem(UtilItem.RenameItem(new ItemStack(Material.DIAMOND_SWORD), "§6Lucky Sword")
						, new String[]{Enchantment.KNOCKBACK.getName()+":1",Enchantment.DAMAGE_ALL.getName()+":3"}) , 10),
				new LuckyItem( UtilItem.EnchantItem(UtilItem.RenameItem(new ItemStack(Material.DIAMOND_SWORD), "§6Lucky Sword")
						, new String[]{Enchantment.FIRE_ASPECT.getName()+":1"}) , 16),
				new LuckyItem( UtilItem.EnchantItem(UtilItem.RenameItem(new ItemStack(Material.BOW), "§6Lucky Bow")
						, new String[]{Enchantment.ARROW_FIRE.getName()+":1",Enchantment.ARROW_DAMAGE.getName()+":3"}) , 16),
				new LuckyItem( UtilItem.EnchantItem(UtilItem.RenameItem(new ItemStack(Material.BOW), "§6Lucky Bow")
						, new String[]{Enchantment.ARROW_KNOCKBACK.getName()+":1",Enchantment.ARROW_DAMAGE.getName()+":3"}) , 16),
				new LuckyItem( UtilItem.EnchantItem(UtilItem.RenameItem(new ItemStack(Material.BOW), "§6Lucky Bow")
						, new String[]{Enchantment.ARROW_INFINITE.getName()+":1",Enchantment.ARROW_DAMAGE.getName()+":2"}) , 16),
				
				new LuckyItem( UtilItem.EnchantItem(UtilItem.RenameItem(new ItemStack(Material.DIAMOND_HELMET), "§6Lucky Helm")
						, new String[]{Enchantment.PROTECTION_ENVIRONMENTAL.getName()+":2",Enchantment.PROTECTION_FIRE.getName()+":1",Enchantment.PROTECTION_EXPLOSIONS.getName()+":1"}) , 25),
				new LuckyItem( UtilItem.EnchantItem(UtilItem.RenameItem(new ItemStack(Material.DIAMOND_CHESTPLATE), "§6Lucky Chestplate")
						, new String[]{Enchantment.PROTECTION_ENVIRONMENTAL.getName()+":3",Enchantment.THORNS.getName()+":1"}) , 12),
				new LuckyItem( UtilItem.EnchantItem(UtilItem.RenameItem(new ItemStack(Material.DIAMOND_LEGGINGS), "§6Lucky Leggings")
						, new String[]{Enchantment.PROTECTION_ENVIRONMENTAL.getName()+":2",Enchantment.PROTECTION_FIRE.getName()+":1",Enchantment.PROTECTION_EXPLOSIONS.getName()+":1"}) , 20),
				new LuckyItem( UtilItem.EnchantItem(UtilItem.RenameItem(new ItemStack(Material.DIAMOND_BOOTS), "§6Lucky Boots")
						, new String[]{Enchantment.PROTECTION_ENVIRONMENTAL.getName()+":2",Enchantment.PROTECTION_FALL.getName()+":4"}) , 15),
				
				new LuckyItem( UtilItem.EnchantItem(UtilItem.RenameItem(new ItemStack(Material.DIAMOND_HELMET), "§6Lucky Helm")
						, new String[]{Enchantment.PROTECTION_ENVIRONMENTAL.getName()+":1",Enchantment.PROTECTION_FIRE.getName()+":1",Enchantment.PROTECTION_EXPLOSIONS.getName()+":1"}) , 35),
				new LuckyItem( UtilItem.EnchantItem(UtilItem.RenameItem(new ItemStack(Material.DIAMOND_CHESTPLATE), "§6Lucky Chestplate")
						, new String[]{Enchantment.PROTECTION_ENVIRONMENTAL.getName()+":2",Enchantment.THORNS.getName()+":1"}) , 15),
				new LuckyItem( UtilItem.EnchantItem(UtilItem.RenameItem(new ItemStack(Material.DIAMOND_LEGGINGS), "§6Lucky Leggings")
						, new String[]{Enchantment.PROTECTION_ENVIRONMENTAL.getName()+":1",Enchantment.PROTECTION_FIRE.getName()+":1",Enchantment.PROTECTION_EXPLOSIONS.getName()+":1"}) , 25),
				new LuckyItem( UtilItem.EnchantItem(UtilItem.RenameItem(new ItemStack(Material.DIAMOND_BOOTS), "§6Lucky Boots")
						, new String[]{Enchantment.PROTECTION_FALL.getName()+":4"}) , 20),
				
				new LuckyItem( UtilItem.EnchantItem(UtilItem.RenameItem(new ItemStack(Material.GOLD_HELMET), "§6Lucky Helm")
						, new String[]{Enchantment.PROTECTION_ENVIRONMENTAL.getName()+":2",Enchantment.PROTECTION_FIRE.getName()+":1",Enchantment.PROTECTION_EXPLOSIONS.getName()+":1"}) , 35),
				new LuckyItem( UtilItem.EnchantItem(UtilItem.RenameItem(new ItemStack(Material.GOLD_CHESTPLATE), "§6Lucky Chestplate")
						, new String[]{Enchantment.PROTECTION_ENVIRONMENTAL.getName()+":3",Enchantment.THORNS.getName()+":1"}) , 15),
				new LuckyItem( UtilItem.EnchantItem(UtilItem.RenameItem(new ItemStack(Material.GOLD_LEGGINGS), "§6Lucky Leggings")
						, new String[]{Enchantment.PROTECTION_ENVIRONMENTAL.getName()+":2",Enchantment.PROTECTION_FIRE.getName()+":1",Enchantment.PROTECTION_EXPLOSIONS.getName()+":1"}) , 30),
				new LuckyItem( UtilItem.EnchantItem(UtilItem.RenameItem(new ItemStack(Material.GOLD_BOOTS), "§6Lucky Boots")
						, new String[]{Enchantment.PROTECTION_FALL.getName()+":4"}) , 25),
				
				new LuckyItem( UtilItem.EnchantItem(UtilItem.RenameItem(new ItemStack(Material.GOLD_HELMET), "§6Lucky Helm")
						, new String[]{Enchantment.PROTECTION_ENVIRONMENTAL.getName()+":1",Enchantment.PROTECTION_FIRE.getName()+":1",Enchantment.PROTECTION_EXPLOSIONS.getName()+":1"}) , 40),
				new LuckyItem( UtilItem.EnchantItem(UtilItem.RenameItem(new ItemStack(Material.GOLD_CHESTPLATE), "§6Lucky Chestplate")
						, new String[]{Enchantment.PROTECTION_ENVIRONMENTAL.getName()+":2",Enchantment.THORNS.getName()+":1"}) , 16),
				new LuckyItem( UtilItem.EnchantItem(UtilItem.RenameItem(new ItemStack(Material.GOLD_LEGGINGS), "§6Lucky Leggings")
						, new String[]{Enchantment.PROTECTION_ENVIRONMENTAL.getName()+":1",Enchantment.PROTECTION_FIRE.getName()+":1",Enchantment.PROTECTION_EXPLOSIONS.getName()+":1"}) , 30),
				new LuckyItem( UtilItem.EnchantItem(UtilItem.RenameItem(new ItemStack(Material.GOLD_BOOTS), "§6Lucky Boots")
						, new String[]{Enchantment.PROTECTION_FALL.getName()+":4"}) , 25),
				
				new LuckyItem( UtilItem.EnchantItem(UtilItem.RenameItem(new ItemStack(Material.GOLD_SPADE), "§6Lucky Shovel")
						, new String[]{Enchantment.DIG_SPEED.getName()+":2"}) , 35),
				new LuckyItem( UtilItem.EnchantItem(UtilItem.RenameItem(new ItemStack(Material.GOLD_SPADE), "§6Lucky Shovel")
						, new String[]{Enchantment.DIG_SPEED.getName()+":1"}) , 30),
				new LuckyItem( UtilItem.RenameItem(new ItemStack(Material.MILK_BUCKET), "§6Milk Bucket") , 45),
				new LuckyItem( UtilItem.RenameItem(new ItemStack(Material.ARROW), "§6Arrow"),10,25 , 65),
				new LuckyItem( new ItemStack[]{UtilItem.RenameItem(new ItemStack(Material.CAKE,2), "§6Lucky Food"),UtilItem.RenameItem(new ItemStack(Material.COOKIE,8), "§6Lucky Food")}, 60),
				new LuckyItem( new ItemStack[]{UtilItem.RenameItem(new ItemStack(Material.LAVA_BUCKET,1), "§6Lucky Lava"),UtilItem.RenameItem(new ItemStack(Material.WATER_BUCKET,1), "§6Lucky Water")}, 40),
				new LuckyItem( UtilItem.RenameItem(new ItemStack(Material.FISHING_ROD), "§6Lucky Fishing Rod"), 40),
				new LuckyItem( UtilItem.RenameItem(new ItemStack(Material.EXP_BOTTLE,16), "§6Lucky XP"), 30),
				new LuckyItem( UtilItem.RenameItem(new ItemStack(Material.ENDER_PEARL,1), "§6Lucky Pearl"), 35),
				new LuckyItem( UtilItem.RenameItem(new ItemStack(Material.ENDER_PEARL,3), "§6Lucky Pearl"), 5),
				new LuckyItem( UtilItem.RenameItem(new ItemStack(Material.BRICK,64), "§6Lucky Brick"), 40),
				new LuckyItem( new ItemStack[]{UtilItem.RenameItem(new ItemStack(Material.TNT,3), "§6Lucky TNT"),UtilItem.RenameItem(new ItemStack(Material.FIREBALL,2), "§6Lucky Fireball")}, 40),
				new LuckyItem( UtilItem.RenameItem(new ItemStack(Material.SNOW_BALL), "§6Lucky Snowball"),5,15, 50),
				new LuckyItem( UtilItem.RenameItem(new ItemStack(Material.EGG), "§6Lucky Egg"),5,15, 50),
				new LuckyPotion(PotionType.POISON, true, 8, 1, 1,2, 50),
				new LuckyPotion(PotionType.SPEED, true, 45, 1, 1,2, 40),
				new LuckyPotion(PotionType.STRENGTH, true, 20, 1, 1,2, 40),
				new LuckyPotion(PotionType.REGEN, true, 30, 1, 1,2, 40),
				new LuckyItem( new ItemStack(373,1,(short)16421),2,4, 55),
			}));
		
		if(getWorldData().loadZips().size()<3){
			getWorldData().Initialize();
		}else{
			getVoteHandler().add(new GameMapVote(getWorldData(), (kArcade.id==-1 ? -1 : 3)));
		}
		
		if(skyWarsType.getTeam_size()!=1){
			if(skyWarsType==SkyWarsType._32x4){
				setVoteTeam(new AddonVoteTeam(this,skyWarsType.getTeam(),InventorySize._36,skyWarsType.getTeam_size()));
			}else{
				setVoteTeam(new AddonVoteTeam(this,skyWarsType.getTeam(),InventorySize._18,skyWarsType.getTeam_size()));
			}
		}
		this.kitshop=new SingleKitShop(getManager().getInstance(), getMoney(), getManager().getPermManager(), "Kit-Shop", InventorySize._9, UtilSkyWars1vs1.getKits(this));
		getManager().DebugLog(l, this.getClass().getName());
	}
	
	//VILLAGER_RED RED_WOOL/EMERALD NORMAL_CHEST -> CHANGE TO GOLD
	//SHEEP WOOL/BEDROCK ISLAND CHEST
	//SPAWN WOOL/REDSTONE
	
	@EventHandler
	public void ServerStatusUpdateSW(ServerStatusUpdateEvent ev){
		ev.getPacket().setSubstate(skyWarsType.name());
	}
	
	@EventHandler
	public void WorldData(WorldDataInitializeEvent ev){
		if(Calendar.holiday==CalendarType.WEIHNACHTEN){
			if(getWorldData().existLoc(Team.BLACK)&&!getWorldData().getSpawnLocations(Team.BLACK).isEmpty()){
				UtilWorldEdit.simulateSnow(getWorldData().getSpawnLocations(Team.BLACK).get(0), 150);
			}else{
				UtilWorldEdit.simulateSnow(getWorldData().getSpawnLocations(Team.RED).get(0), 150);
			}
		}
	}

	@EventHandler
	public void load(WorldDataInitializeEvent ev){
		int i=0;
		Chest[] chests;
		for(Team t : skyWarsType.getTeam()){
			chests=new Chest[getWorldData().getSpawnLocations(getChestSpawn(t)).size()];
			
			for(Location loc : getWorldData().getSpawnLocations(getChestSpawn(t))){
				loc.getBlock().setType(Material.CHEST);
				chests[i]=((Chest)loc.getBlock().getState());
				i++;
			}
			i=0;
			
			fillIslandChests(t,chests);
		}
		
		if(getWorldData().existLoc(Team.GOLD)&&!getWorldData().getSpawnLocations(Team.GOLD).isEmpty()){
			Chest chest;
			for(Location loc : getWorldData().getSpawnLocations(Team.GOLD)){
				loc.getBlock().setType(Material.CHEST);
				if(loc.getBlock().getState() instanceof Chest){
					chest=(Chest)loc.getBlock().getState();
					for (int nur = 0; nur < UtilMath.RandomInt(6,3); nur++) {
						chest.getInventory().setItem(emptySlot(chest.getInventory()), rdmItem()); 
					}
				}
			}
		}

		if(skyWarsType==SkyWarsType._32x4){
			GameCage gcase = GameCage.GLASS;
			int color;
			for(Team t : skyWarsType.getTeam()){
				if(getWorldData().existLoc(t)){
					color=UtilMath.r(15);
					UtilMap.makeQuadrat(null,getWorldData().getSpawnLocations(t).get(0).clone().add(0, 10, 0), 2, 5,gcase.getGround((byte)color),gcase.getWall((byte)color));
				}
			}
		}
	}
	
	public ItemStack Sonstiges(){
		try{
			switch(UtilMath.r(43)){
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
			case 22:return new ItemStack(Material.SNOW_BALL,UtilMath.RandomInt(16,10));
			case 24:return new ItemStack(Material.SNOW_BALL,UtilMath.RandomInt(16,10));
			case 25:return new ItemStack(Material.SNOW_BALL,UtilMath.RandomInt(16,10));
			case 26:return new ItemStack(Material.SNOW_BALL,UtilMath.RandomInt(16,10));
			case 27:return new ItemStack(Material.SNOW_BALL,UtilMath.RandomInt(16,10));
			case 28:return new ItemStack(Material.EGG,UtilMath.RandomInt(16,10));
			case 29:return new ItemStack(Material.EGG,UtilMath.RandomInt(16,10));
			case 30:return new ItemStack(Material.EGG,UtilMath.RandomInt(16,10));
			case 31:return new ItemStack(Material.EGG,UtilMath.RandomInt(16,10));
			case 32:return new ItemStack(Material.COOKED_BEEF,UtilMath.RandomInt(12,8));
			case 33:return new ItemStack(Material.COOKED_CHICKEN,UtilMath.RandomInt(12,8));
			case 34:return new ItemStack(Material.COOKED_FISH,UtilMath.RandomInt(12,8));
			case 35:return new ItemStack(Material.COOKED_RABBIT,UtilMath.RandomInt(12,8));
			case 36:return new ItemStack(Material.BREAD,UtilMath.RandomInt(12,8));
			case 37:return new ItemStack(Material.COOKED_BEEF,UtilMath.RandomInt(12,8));
			case 38:return new ItemStack(Material.COOKED_CHICKEN,UtilMath.RandomInt(12,8));
			case 39:return new ItemStack(Material.COOKED_FISH,UtilMath.RandomInt(12,8));
			case 40:return new ItemStack(Material.COOKED_RABBIT,UtilMath.RandomInt(12,8));
			case 41:return new ItemStack(Material.BREAD,UtilMath.RandomInt(12,8));
			case 42: return new ItemStack(Material.ENDER_PEARL,UtilMath.RandomInt(4, 2));
			case 43: return new ItemStack(Material.ENDER_PEARL,UtilMath.RandomInt(4, 2));
			default: return new ItemStack(Material.STICK);
			}
		}catch(NullPointerException e){
			System.err.println("Error: ");
			e.printStackTrace();
			return new ItemStack(Material.BREAD,UtilMath.RandomInt(12,8));
		}
	}
	
	public ItemStack Tools(){
		switch(UtilMath.r(6)){
		case 0: return new ItemStack(Material.DIAMOND_SWORD);
		case 1: return new ItemStack(Material.IRON_SWORD);
		case 2: return new ItemStack(Material.DIAMOND_AXE);
		case 3: return new ItemStack(Material.DIAMOND_PICKAXE);
		case 4: return new ItemStack(Material.SHEARS);
		case 5: return new ItemStack(Material.BOW);
		default: return new ItemStack(Material.WOOD_SWORD);
		}
	}
	
	public ItemStack DiaRuestung(){
		switch(UtilMath.r(4)){
		case 0: return new ItemStack(Material.DIAMOND_HELMET);
		case 1: return new ItemStack(Material.DIAMOND_CHESTPLATE);
		case 2: return new ItemStack(Material.DIAMOND_LEGGINGS);
		case 3: return new ItemStack(Material.DIAMOND_BOOTS);
		default:
			return new ItemStack(Material.LEATHER_HELMET);
		}
	}
	
	@EventHandler
	public void damage(EntityDamageByEntityEvent ev){
		if(ev.getEntity() instanceof Player){
			if(ev.getDamager() instanceof Player){
				if(this.hit.containsKey( ((Player)ev.getEntity()) )){
					if(this.hit.get( ((Player)ev.getEntity()) ).getUniqueId() == ((Player)ev.getDamager()).getUniqueId())return;
					this.hit.remove(((Player)ev.getEntity()));
				}
				
				this.hit.put(((Player)ev.getEntity()), ((Player)ev.getDamager()));
			}else if(ev.getDamager() instanceof Projectile && ((Projectile)ev.getDamager()).getShooter() instanceof Player){
				if(this.hit.containsKey( ((Player)ev.getEntity()) )){
					if(this.hit.get( ((Player)ev.getEntity()) ).getUniqueId() == ((Player)((Projectile)ev.getDamager()).getShooter()).getUniqueId())return;
					this.hit.remove(((Player)ev.getEntity()));
				}
				
				this.hit.put(((Player)ev.getEntity()), ((Player)((Projectile)ev.getDamager()).getShooter()));
			}
		}
	}
	
	public ItemStack IronRuestung(){
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
		case 1: item = IronRuestung();break;
		case 2: item = DiaRuestung();break;
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
		if(skyWarsType==SkyWarsType._32x4){
			return Team.getPoint(team);
		}else{
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
			case BROWN:return Team.SHEEP_BROWN;
			case MAGENTA:return Team.SHEEP_MAGENTA;
			case LIGHT_GRAY:return Team.SHEEP_LIGHT_GRAY;
			case LIME:return Team.SHEEP_LIME;
			default:
			return Team.SHEEP_RED;
			}
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
		template_type.put("SWORD",(skyWarsType==SkyWarsType._32x4 ? 6 : 3));
		template_type.put("AXT",(skyWarsType==SkyWarsType._32x4 ? 6 : 2));
		template_type.put("BLOCK",(skyWarsType==SkyWarsType._32x4 ? 8 : 5));
		template_type.put("HELM",(skyWarsType==SkyWarsType._32x4 ? 6 : 2));
		template_type.put("CHESTPLATE",(skyWarsType==SkyWarsType._32x4 ? 6 : 2));
		template_type.put("LEGGINGS",(skyWarsType==SkyWarsType._32x4 ? 6 : 2));
		template_type.put("BOOTS",(skyWarsType==SkyWarsType._32x4 ? 6 : 2));
		template_type.put("ARROW",(skyWarsType==SkyWarsType._32x4 ? 2 : 1));
		template_type.put("POTION",(skyWarsType==SkyWarsType._32x4 ? 8 : 4));
		template_type.put("FOOD",(skyWarsType==SkyWarsType._32x4 ? 6 : 3));
		template_type.put("WEB",(skyWarsType==SkyWarsType._32x4 ? 6 : 3));
		template_type.put("LAVA-BUCKET",(skyWarsType==SkyWarsType._32x4 ? 2 : 1));
		template_type.put("WATER-BUCKET",(skyWarsType==SkyWarsType._32x4 ? 2 : 1));
		template_type.put("TNT",(skyWarsType==SkyWarsType._32x4 ? 2 : 1));
		template_type.put("FIRE",(skyWarsType==SkyWarsType._32x4 ? 2 : 1));
		template_type.put("BOW",(skyWarsType==SkyWarsType._32x4 ? 2 : 1));
		template_type.put("TOOL",(skyWarsType==SkyWarsType._32x4 ? 4 : 2));
		
		for(Chest chest : chests)template.put(chest, new ArrayList<String>());
		
		
		if(UtilMath.r(100)>99){
			add( (Chest)template.keySet().toArray()[UtilMath.r(template.size())] ,"BOW");
			add( (Chest)template.keySet().toArray()[UtilMath.r(template.size())] ,"ARROW");
		}else{
			template_type.remove("BOW");
			template_type.remove("ARROW");
		}
		
		if(skyWarsType==SkyWarsType._32x4){
			add( (Chest)template.keySet().toArray()[UtilMath.r(template.size())] ,"FOOD");
			add( (Chest)template.keySet().toArray()[UtilMath.r(template.size())] ,"FOOD");
		}

		add( (Chest)template.keySet().toArray()[UtilMath.r(template.size())] ,"SWORD");
		add( (Chest)template.keySet().toArray()[UtilMath.r(template.size())] ,"BLOCK");
		add( (Chest)template.keySet().toArray()[UtilMath.r(template.size())] ,"BLOCK");
		add( (Chest)template.keySet().toArray()[UtilMath.r(template.size())] ,"BLOCK");
		add( (Chest)template.keySet().toArray()[UtilMath.r(template.size())] ,"TOOL");
		add( (Chest)template.keySet().toArray()[UtilMath.r(template.size())] ,"POTION");
		add( (Chest)template.keySet().toArray()[UtilMath.r(template.size())] ,"CHESTPLATE");
		add( (Chest)template.keySet().toArray()[UtilMath.r(template.size())] ,"BOOTS");
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
				case "AXT":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(UtilItem.rdmAxt()) );
					break;
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
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(rdmBlock(),UtilMath.RandomInt(48,32)));
					break;
				case "POTION":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(rdmPotion()));
					break;
				case "FOOD":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , rdmFood());
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
					}else if(UtilItem.isAxt(item)){
						item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
						if(item.getType()==Material.GOLD_AXE){
							item.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 1);
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
	public void drop(PlayerPickupItemEvent ev){
		if(ev.getItem().getItemStack().getTypeId()==351){
			ev.getItem().remove();
			ev.setCancelled(true);
		}
	}
	
	@EventHandler
	public void Chat(AsyncPlayerChatEvent ev){
		if(ev.isCancelled())return;
		ev.setCancelled(true);
		
		if((!ev.getPlayer().hasPermission(PermissionType.CHAT_LINK.getPermissionToString()))&&UtilString.isBadWord(ev.getMessage())||UtilString.checkForIP(ev.getMessage())){
			ev.setMessage("Ich heul rum!");
			ev.getPlayer().sendMessage(TranslationHandler.getText(ev.getPlayer(), "PREFIX")+TranslationHandler.getText(ev.getPlayer(), "CHAT_MESSAGE_BLOCK"));
		}

		if(getState()!=GameState.LobbyPhase&&getGameList().getPlayers(PlayerState.SPECTATOR).contains(ev.getPlayer())){
			ev.setCancelled(true);
			UtilPlayer.sendMessage(ev.getPlayer(),TranslationHandler.getText(ev.getPlayer(), "PREFIX_GAME", getType().getTyp())+TranslationHandler.getText(ev.getPlayer(), "SPECTATOR_CHAT_CANCEL"));
		}else{
			UtilServer.broadcast(getManager().getPermManager().getPrefix(ev.getPlayer())+ev.getPlayer().getDisplayName()+":§7 "+ev.getMessage());
		}
	}
	
	public void updateTime(String formant){
		for(Player player : UtilServer.getPlayers()){
			if(player.getScoreboard()!=null){
				if(player.getScoreboard().getObjective(DisplaySlot.SIDEBAR)!=null){
					player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).setDisplayName("§e"+getType().getTyp()+" §7- §e"+format);
					
					if(getStart()>10*60){
						UtilScoreboard.resetScore(player.getScoreboard(), 14, DisplaySlot.SIDEBAR);
						UtilScoreboard.setScore(player.getScoreboard(), "§e"+UtilTime.formatSeconds( (getStart()-10*60) ),DisplaySlot.SIDEBAR,14);
					}
				}
			}
			
		}
	}
	
	String format;
	@EventHandler
	public void inGame(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		if(getState()!=GameState.InGame)return;
		setStart(getStart()-1);
		format=UtilTime.formatSeconds(getStart());
		for(Player p : UtilServer.getPlayers())UtilDisplay.displayTextBar(TranslationHandler.getText(p, "GAME_END_IN", format), p);
		updateTime(format);
		switch(getStart()){
		case 10*60:
			Chest chest;
			for(Team t : skyWarsType.getTeam()){
				for(Location loc : getWorldData().getSpawnLocations(getChestSpawn(t))){
					if(loc.getBlock().getState() instanceof Chest){
						chest=(Chest)loc.getBlock().getState();
						for (int nur = 0; nur < UtilMath.RandomInt(6,3); nur++) {
							chest.getInventory().setItem(emptySlot(chest.getInventory()), rdmItem()); 
						}
					}
				}
			}
			
			for(Location loc : getWorldData().getSpawnLocations(Team.GOLD)){
				loc.getBlock().setType(Material.CHEST);
				if(loc.getBlock().getState() instanceof Chest){
					chest=(Chest)loc.getBlock().getState();
					for (int nur = 0; nur < UtilMath.RandomInt(6,3); nur++) {
						chest.getInventory().setItem(emptySlot(chest.getInventory()), rdmItem()); 
					}
				}
			}

			for(Player player : UtilServer.getPlayers()){
				UtilScoreboard.resetScore(player.getScoreboard(), 15, DisplaySlot.SIDEBAR);
				UtilScoreboard.resetScore(player.getScoreboard(), 14, DisplaySlot.SIDEBAR);
				UtilScoreboard.resetScore(player.getScoreboard(), 13, DisplaySlot.SIDEBAR);
			}
			
			broadcastWithPrefix("§eDie Kisten wurden wieder gefüllt");
			break;
		case 30: broadcastWithPrefix("GAME_END_IN", format);break;
		case 15: broadcastWithPrefix("GAME_END_IN", format);break;
		case 10: broadcastWithPrefix("GAME_END_IN", format);break;
		case 5: broadcastWithPrefix("GAME_END_IN", format);break;
		case 4: broadcastWithPrefix("GAME_END_IN", format);break;
		case 3: broadcastWithPrefix("GAME_END_IN", format);break;
		case 2: broadcastWithPrefix("GAME_END_IN", format);break;
		case 1: broadcastWithPrefix("GAME_END_IN", format);break;
		case 0:
			broadcastWithPrefixName("GAME_END");
			setState(GameState.Restart);
			break;
		}
	}
	
	@EventHandler
	public void ShopOpen(PlayerInteractEvent ev){
		if(UtilEvent.isAction(ev, ActionType.RIGHT)){
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
			
			getStats().addInt(v, 1, StatsKey.LOSE);
			getStats().addInt(v, 1, StatsKey.DEATHS);
			getGameList().addPlayer(v, PlayerState.SPECTATOR);
			
			if(ev.getEntity().getKiller() instanceof Player || this.hit.containsKey( ((Player)ev.getEntity()) )){
				Player a;
				if(ev.getEntity().getKiller()==null){
					 a = this.hit.get(((Player)ev.getEntity()));
				}else{
					 a = (Player)ev.getEntity().getKiller();
				}
				
				int k = kills.get(a.getName());
				k++;
				kills.remove(a.getName());
				kills.put(a.getName(), k);
				getStats().addInt(a, 1, StatsKey.KILLS);
				UtilScoreboard.resetScore(a.getScoreboard(), 5 , DisplaySlot.SIDEBAR);
				UtilScoreboard.setScore(a.getScoreboard(), "§e"+kills.get(a.getName()), DisplaySlot.SIDEBAR, 5);
				
				if(skyWarsType==SkyWarsType._32x4){
					getMoney().add(a, StatsKey.COINS, 12);
				}else{
					getMoney().add(a, StatsKey.COINS, 5);
				}
				
				broadcastWithPrefix("KILL_BY", new String[]{v.getName(),a.getName()});
				this.hit.remove(a);
				Title t = new Title("§c§lYOUR ARE DEATH!","§a" + a.getName() + ": " + UtilPlayer.getPlayerLiveString(a));
				t.send(v);
				return;
			}
			
			broadcastWithPrefix("DEATH", v.getName());
			if(skyWarsType.getTeam_size()>1){
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
	
	@EventHandler
	public void teamuse(AddonVoteTeamPlayerChooseEvent ev){
		if(skyWarsType==SkyWarsType._32x4){
			if(ev.getState()==PlayerState.INGAME){
				ev.getPlayer().teleport(getWorldData().getSpawnLocations(ev.getTeam()).get(0).clone().add(0, 12,0));
			}else{
				ev.getPlayer().teleport(getManager().getLobby());
			}
		}
	}
	
	public Player TeamPartner(Player p){
		for(Player p1 : getPlayersFromTeam(getTeam(p))){
			if(p1==p)continue;
			return p1;
		}
		return null;
	}
	
	@EventHandler
	public void GameStateChangeSkyWars(GameStateChangeEvent ev){
		if(ev.getTo()==GameState.Restart){
			String winner="";
			ArrayList<Player> list = getGameList().getPlayers(PlayerState.INGAME);
			if(list.size()==1){
				Player p = list.get(0);
				getStats().addInt(p, 1, StatsKey.WIN);
				if(skyWarsType==SkyWarsType._32x4){
					getMoney().add(p, StatsKey.COINS, 1000);
					getMoney().add(p, StatsKey.GEMS, 1000);
				}else{
					getMoney().add(p, StatsKey.COINS, 25);
				}
//				broadcastWithPrefix("GAME_WIN", p.getName());
				new Title("§6§lGEWONNEN").send(p);
				winner=p.getName();
				System.err.println("WINNER: "+p.getName());
			}else{

				Title t = new Title("§6§lGEWONNEN");
				for(Player player : list){
					if(skyWarsType==SkyWarsType._32x4){
						getMoney().add(player, StatsKey.COINS, 1000);
						getMoney().add(player, StatsKey.GEMS, 1000);
					}else{
						getMoney().add(player, StatsKey.COINS, 25);
					}
					getStats().addInt(player, 1, StatsKey.WIN);
					t.send(player);
					winner=player.getName()+",";
					System.err.println("WINNER: "+player.getName());
				}
			}
			
			for(String name : this.kills.keySet()){
				this.ranking.add(new kSort<String>(name,this.kills.get(name)));
			}
			Collections.sort(ranking,kSort.DESCENDING);

			Bukkit.broadcastMessage("§a§l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
			if(!winner.equals("")){
				Bukkit.broadcastMessage(UtilString.center("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬".length(),("Winner - "+winner).length())+"§eWinner §7- "+winner);
				Bukkit.broadcastMessage(" ");
			}
			
			if(!this.ranking.isEmpty()&&this.ranking.size()>=1){
				Bukkit.broadcastMessage(UtilString.center("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬".length(),("1st - "+this.ranking.get(0).getObject()+" - "+this.ranking.get(0).getValue()).length())+"§e1st Killer - §7"+this.ranking.get(0).getObject()+" - "+this.ranking.get(0).getValue());
			}
			if(!this.ranking.isEmpty()&&this.ranking.size()>=2){
				Bukkit.broadcastMessage(UtilString.center("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬".length(),("2st - "+this.ranking.get(1).getObject()+" - "+this.ranking.get(1).getValue()).length())+"§62st Killer - §7"+this.ranking.get(1).getObject()+" - "+this.ranking.get(1).getValue());
			}
			if(!this.ranking.isEmpty()&&this.ranking.size()>=3){
				Bukkit.broadcastMessage(UtilString.center("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬".length(),("3st - "+this.ranking.get(2).getObject()+" - "+this.ranking.get(2).getValue()).length())+"§c3st Killer - §7"+this.ranking.get(2).getObject()+" - "+this.ranking.get(2).getValue());
			}
			Bukkit.broadcastMessage("§a§l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
		}
	}
	
	@EventHandler
	public void statsLOADED(PlayerStatsLoadedEvent ev){
		if(ev.getManager().getType() != getType())return;
		if(getState()!=GameState.LobbyPhase)return;
		if(UtilPlayer.isOnline(ev.getPlayerId())){
			Player player = UtilPlayer.searchExact(ev.getPlayerId());
			int w = getStats().getInt(StatsKey.WIN, player);
			int l = getStats().getInt(StatsKey.LOSE, player);
			
			Bukkit.getScheduler().runTask(getManager().getInstance(), new Runnable() {
				
				@Override
				public void run() {
					getManager().getHologram().sendText(player,getManager().getLoc_stats(),new String[]{
						Color.GREEN+getType().getTyp()+" "+skyWarsType.name().replaceAll("_", "")+Color.ORANGE+"§l Info",
						TranslationHandler.getText(player, "GAME_HOLOGRAM_SERVER",getType().getTyp()+" §a"+kArcade.id),
						TranslationHandler.getText(player, "GAME_HOLOGRAM_MAP", (getWorldData().getMap()!=null ? getWorldData().getMapName() : "Loading...")),
						" ",
						TranslationHandler.getText(player, "GAME_HOLOGRAM_STATS", getType().getTyp()),
						TranslationHandler.getText(player, "GAME_HOLOGRAM_KILLS", getStats().getInt(StatsKey.KILLS, player)),
						TranslationHandler.getText(player, "GAME_HOLOGRAM_DEATHS", getStats().getInt(StatsKey.DEATHS, player)),
						" ",
						TranslationHandler.getText(player, "GAME_HOLOGRAM_GAMES", (((Integer)w)+((Integer)l))),
						TranslationHandler.getText(player, "GAME_HOLOGRAM_WINS", ((Integer)w)),
						TranslationHandler.getText(player, "GAME_HOLOGRAM_LOSE", ((Integer)l)),
						});
				}
			});
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void JoinHologram(PlayerJoinEvent ev){
		if(getState()!=GameState.LobbyPhase)return;
		ev.getPlayer().getInventory().addItem(UtilItem.RenameItem(new ItemStack(Material.CHEST), "§bKitShop"));
	}
	
	@EventHandler
	public void GameStartSkyWars(GameStartEvent ev){
		getWorldData().clearWorld();

		if(skyWarsType==SkyWarsType._32x4){
			this.targetNextPlayer=new AddonTargetNextPlayer(250, this);
			this.targetNextPlayer.setAktiv(true);
			
			for(Team t : skyWarsType.getTeam()){
				if(getWorldData().existLoc(t)){
					UtilMap.makeQuadrat(null,getWorldData().getSpawnLocations(t).get(0).clone().add(0, 10, 0), 2, 5,new ItemStack(Material.AIR),null);
				}
			}
		}
		
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
			
			getGameList().addPlayer(p,PlayerState.INGAME);
			kills.put(p.getName(), 0);
			plist.add(p);
			for(Player player : UtilServer.getPlayers()){
				p.showPlayer(player);
				player.showPlayer(p);
			}
			
			if(skyWarsType==SkyWarsType._32x4){
				p.getInventory().addItem(new ItemStack(Material.COMPASS));
			}
		}
		distributePlayers(skyWarsType.getTeam(), plist);
		
		for(Player player : getTeamList().keySet()){
			if(player.getWorld().getUID()!=getWorldData().getWorld().getUID()){
				player.teleport(getWorldData().getSpawnLocations(getTeamList().get(player)).get(0));
			}
		}

		Scoreboard ps;
		Title title = new Title("", "");
		for(Player p : UtilServer.getPlayers()){
			ps=Bukkit.getScoreboardManager().getNewScoreboard();
			
			UtilScoreboard.addBoard(ps, DisplaySlot.SIDEBAR, "§e"+getType().getTyp()+" §7- §e");
			UtilScoreboard.setScore(ps, "      ", DisplaySlot.SIDEBAR, 16);
			UtilScoreboard.setScore(ps, "§7Chest-Refill in: ", DisplaySlot.SIDEBAR, 15);
			UtilScoreboard.setScore(ps, "§e",DisplaySlot.SIDEBAR,14);
			if(skyWarsType.getTeam_size()>1){
				Player p1 = TeamPartner(p);
				
				if(p1!=null){
					UtilScoreboard.setScore(ps, "     ", DisplaySlot.SIDEBAR, 13);
					UtilScoreboard.setScore(ps, "§7Team-Partner: ", DisplaySlot.SIDEBAR, 12);
					if(p1.getName().length()>13){
						UtilScoreboard.setScore(ps, "§e"+p1.getName().substring(13), DisplaySlot.SIDEBAR, 11);
					}else{
						UtilScoreboard.setScore(ps, "§e"+p1.getName(), DisplaySlot.SIDEBAR, 11);
					}
				}
			}
			UtilScoreboard.setScore(ps, "    ", DisplaySlot.SIDEBAR, 10);
			UtilScoreboard.setScore(ps, "§7Map: ", DisplaySlot.SIDEBAR, 9);
			UtilScoreboard.setScore(ps, "§e"+getWorldData().getMapName(),DisplaySlot.SIDEBAR,8);
			UtilScoreboard.setScore(ps, "   ", DisplaySlot.SIDEBAR, 7);
			UtilScoreboard.setScore(ps, "§7Kills: ", DisplaySlot.SIDEBAR, 6);
			UtilScoreboard.setScore(ps, "§e"+kills.get(p.getName()),DisplaySlot.SIDEBAR,5);
			UtilScoreboard.setScore(ps, "  ", DisplaySlot.SIDEBAR, 4);
			UtilScoreboard.setScore(ps, "§7Kit: ", DisplaySlot.SIDEBAR, 3);
			UtilScoreboard.setScore(ps, "§e"+ (kits.containsKey(p) ? kits.get(p) : TranslationHandler.getText(p, "NO_KIT")) , DisplaySlot.SIDEBAR,2);
			UtilScoreboard.setScore(ps, " ", DisplaySlot.SIDEBAR, 1);
			UtilScoreboard.setScore(ps, "§ewww.ClashMC.eu", DisplaySlot.SIDEBAR, 0);
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
			title.setSubtitle(TranslationHandler.getText(p, "NO_TEAMS_ALLOWED"));
			title.send(p);
		}
		
		for(Entity e : getWorldData().getWorld().getEntities()){
			if(!(e instanceof Player)){
				e.remove();
			}
		}
		
		for(Player player : UtilServer.getPlayers()){
			if(player.getWorld().getName().equalsIgnoreCase("world")){
				logMessage("Spieler "+player.getName()+" ist zu viel.");
				getManager().getMysql().asyncUpdate("INSERT INTO list_exception (server,ip,exceptiontype,message) VALUES ('a"+kArcade.id+"','null','SkyWars Spieler Verteilung','Spieler: "+player.getName()+" ANZAHL:"+UtilServer.getPlayers().size()+" VER:"+getERR(skyWarsType.getTeam(),skyWarsType.getTeam_size())+"');");
				UtilBG.sendToServer(player, getManager().getInstance());
			}
		}
		
		if(getManager().getHoliday()!=null){
			switch(getManager().getHoliday()){
			case HALLOWEEN:
				new AddonNight(getManager().getInstance(),getWorldData().getWorld());
				break;
			default:
				new AddonDay(getManager().getInstance(),getWorldData().getWorld());
				break;
			}
		}


		getWorldData().getWorld().setStorm(false);
		
		if(skyWarsType==SkyWarsType._32x4){
			setStart((60*25)+1);
		}else{
			setStart((60*15)+1);
		}
		setState(GameState.InGame);
		setDamage(true);
		setDamagePvP(true);
		setDamageSelf(true);
	}
	
	@EventHandler
	public void Ranking(RankingEvent ev){
		getManager().setRanking(StatsKey.WIN);
	}

}
