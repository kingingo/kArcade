package me.kingingo.karcade.Game.Single.Games.SkyWars;

import java.util.ArrayList;
import java.util.HashMap;

import me.kingingo.karcade.kArcade;
import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Enum.PlayerState;
import me.kingingo.karcade.Events.RankingEvent;
import me.kingingo.karcade.Events.WorldLoadEvent;
import me.kingingo.karcade.Game.Events.GameStateChangeEvent;
import me.kingingo.karcade.Game.Single.Games.TeamGame;
import me.kingingo.karcade.Game.Single.addons.AddonVoteTeam;
import me.kingingo.karcade.Game.World.WorldData;
import me.kingingo.kcore.Addons.AddonDay;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.Enum.Team;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.Game.Events.GameStartEvent;
import me.kingingo.kcore.Kit.Kit;
import me.kingingo.kcore.Kit.KitType;
import me.kingingo.kcore.Kit.Perk;
import me.kingingo.kcore.Kit.Perks.PerkNoHunger;
import me.kingingo.kcore.Kit.Shop.KitShop;
import me.kingingo.kcore.Permission.kPermission;
import me.kingingo.kcore.StatsManager.Stats;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.Color;
import me.kingingo.kcore.Util.InventorySize;
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
import org.bukkit.block.BlockFace;
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
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

public class SkyWars extends TeamGame{

	private ArrayList<ItemStack> chest_material = new ArrayList<>();
	private SkyWarsType type;
	private KitShop kitshop;
	
	public SkyWars(kArcadeManager manager,SkyWarsType type){
		super(manager);
		registerListener();
		long l = System.currentTimeMillis();
		this.type=type;
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
		setWorldData(new WorldData(manager,getType()));
		getWorldData().Initialize();
		if(type.getTeam_size()!=1)setVoteTeam(new AddonVoteTeam(this,type.getTeam(),InventorySize._18,type.getTeam_size()));
		
		kitshop=new KitShop(getManager().getInstance(), getCoins(), getManager().getPermManager(), "Kit-Shop", InventorySize._27, new Kit[]{
			new Kit( "§aStarter",new String[]{"Der Starter bekommt kein Hunger."}, new ItemStack(Material.WOOD_SWORD),kPermission.SHEEPWARS_KIT_STARTER,KitType.STARTER,2000,new Perk[]{
				new PerkNoHunger()
			})
		});
		
		getManager().DebugLog(l, this.getClass().getName());
	}
	
	@EventHandler
	public void load(WorldLoadEvent ev){
		int i=0;
		Chest[] chests;
		for(Team t : type.getTeam()){
			chests=new Chest[getWorldData().getLocs(getChestSpawn(t).Name()).size()];
			
			for(Location loc : getWorldData().getLocs(getChestSpawn(t).Name())){
				loc.getBlock().setType(Material.CHEST);
				chests[i]=((Chest)loc.getBlock().getState());
				i++;
			}
			i=0;
			
			fillIslandChests(t,chests);
		}
		
		Chest chest;
		for(Location loc : getWorldData().getLocs(Team.VILLAGE_RED.Name())){
			loc.getBlock().setType(Material.CHEST);
			if(loc.getBlock().getState() instanceof Chest){
				chest=(Chest)loc.getBlock().getState();
				
				
				
			}
		}
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
		}

		add( (Chest)template.keySet().toArray()[UtilMath.r(template.size())] ,"SWORD");
		add( (Chest)template.keySet().toArray()[UtilMath.r(template.size())] ,"BLOCK");
		add( (Chest)template.keySet().toArray()[UtilMath.r(template.size())] ,"TOOL");
		add( (Chest)template.keySet().toArray()[UtilMath.r(template.size())] ,"POTION");
		add( (Chest)template.keySet().toArray()[UtilMath.r(template.size())] ,"CHESTPLATE");
		
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
					chest.getBlock().getRelative(BlockFace.UP).setType(Material.ANVIL);
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(Material.BOW));
					break;
				case "ARROW":
					chest.getBlock().getRelative(BlockFace.UP).setType(Material.BRICK);
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
		System.out.println("1");
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
	
	public void loadMaterialList(){
		chest_material.add(new ItemStack(Material.POTION,2,(byte) 8257));
		chest_material.add(new ItemStack(Material.POTION,2,(byte) 8259));
		chest_material.add(new ItemStack(Material.POTION,2,(byte) 8226));
		chest_material.add(new ItemStack(Material.POTION,2,(byte) 8233));
		chest_material.add(new ItemStack(Material.POTION,2,(byte) 8257));
		chest_material.add(new ItemStack(Material.POTION,2,(byte) 8259));
		chest_material.add(new ItemStack(Material.POTION,2,(byte) 8226));
		chest_material.add(new ItemStack(Material.POTION,2,(byte) 8233));
		chest_material.add(new ItemStack(Material.EXP_BOTTLE,16));
		chest_material.add(new ItemStack(Material.ENCHANTMENT_TABLE,1));
		chest_material.add(new ItemStack(Material.BOOKSHELF,8));
		chest_material.add(new ItemStack(Material.DIAMOND,3));
		chest_material.add(new ItemStack(Material.DIAMOND_HELMET,1));
		chest_material.add(new ItemStack(Material.DIAMOND_CHESTPLATE,1));
		chest_material.add(new ItemStack(Material.DIAMOND_LEGGINGS,1));
		chest_material.add(new ItemStack(Material.DIAMOND_BOOTS,1));
		chest_material.add(new ItemStack(Material.IRON_HELMET,1));
		chest_material.add(new ItemStack(Material.IRON_CHESTPLATE,1));
		chest_material.add(new ItemStack(Material.IRON_LEGGINGS,1));
		chest_material.add(new ItemStack(Material.IRON_BOOTS,1));
		chest_material.add(new ItemStack(Material.LEATHER_HELMET,1));
		chest_material.add(new ItemStack(Material.LEATHER_CHESTPLATE,1));
		chest_material.add(new ItemStack(Material.LEATHER_LEGGINGS,1));
		chest_material.add(new ItemStack(Material.LEATHER_BOOTS,1));
		chest_material.add(new ItemStack(Material.DIAMOND_AXE,1));
		chest_material.add(new ItemStack(Material.DIAMOND_SWORD,1));
		chest_material.add(new ItemStack(Material.BOW,1));
		chest_material.add(new ItemStack(Material.ARROW,16));
		chest_material.add(new ItemStack(Material.ARROW,32));
		chest_material.add(new ItemStack(Material.ARROW,8));
		chest_material.add(new ItemStack(Material.ARROW,12));
		chest_material.add(new ItemStack(Material.GOLDEN_APPLE,2));
	}
	
	@EventHandler
	public void Chat(AsyncPlayerChatEvent ev){
		if(ev.isCancelled())return;
		ev.setCancelled(true);
		
		if((!ev.getPlayer().hasPermission(kPermission.CHAT_LINK.getPermissionToString()))&&UtilString.isBadWord(ev.getMessage())||UtilString.checkForIP(ev.getMessage())){
			ev.setMessage("Ich heul rum!");
			ev.getPlayer().sendMessage(Text.PREFIX.getText()+Text.CHAT_MESSAGE_BLOCK.getText());
		}
		
		if(getState()!=GameState.LobbyPhase&&getGameList().getPlayers(PlayerState.OUT).contains(ev.getPlayer())){
			ev.setCancelled(true);
			UtilPlayer.sendMessage(ev.getPlayer(),Text.PREFIX_GAME.getText(getType().getTyp())+Text.SPECTATOR_CHAT_CANCEL.getText());
		}else{
			UtilServer.broadcast(getManager().getPermManager().getPrefix(ev.getPlayer())+ev.getPlayer().getDisplayName()+":§7 "+ev.getMessage());
		}
	}
	
	@EventHandler
	public void inGame(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		if(getState()!=GameState.InGame)return;
		setStart(getStart()-1);
		for(Player p : UtilServer.getPlayers())UtilDisplay.displayTextBar(Text.GAME_END_IN.getText(UtilTime.formatSeconds(getStart())), p);
		switch(getStart()){
		case 300: broadcast( Text.PREFIX_GAME.getText(getType().getTyp())+"§eChest refill" );break;
		case 30: broadcast( Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getStart())) );break;
		case 15: broadcast( Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getStart())) );break;
		case 10: broadcast( Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getStart())) );break;
		case 5: broadcast( Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getStart())) );break;
		case 4: broadcast( Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getStart())) );break;
		case 3: broadcast( Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getStart())) );break;
		case 2: broadcast( Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getStart())) );break;
		case 1: broadcast( Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getStart())) );break;
		case 0:
			broadcast( Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_END.getText() );
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
				getStats().setInt(a, getStats().getInt(Stats.KILLS, a)+1, Stats.KILLS);
				UtilScoreboard.resetScore(a.getScoreboard(), "§e"+ (getStats().getInt(Stats.KILLS, a)-1) , DisplaySlot.SIDEBAR);
				UtilScoreboard.setScore(a.getScoreboard(), "§e"+getStats().getInt(Stats.KILLS, a), DisplaySlot.SIDEBAR, 7);
				getCoins().addCoins(a, false, 5);
				broadcast( Text.PREFIX_GAME.getText(getType().getTyp())+Text.KILL_BY.getText(new String[]{v.getName(),a.getName()}) );
				return;
			}
			broadcast( Text.PREFIX_GAME.getText(getType().getTyp())+Text.DEATH.getText(v.getName()) );
			broadcast( Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_AUSGESCHIEDEN.getText(v.getName()) );
			getStats().setInt(v, getStats().getInt(Stats.DEATHS, v)+1, Stats.DEATHS);
			
		}
	}
	
	@EventHandler
	public void GameStateChangeSkyWars(GameStateChangeEvent ev){
		if(ev.getFrom()==GameState.InGame&&ev.getTo()==GameState.Restart){
			if(getGameList().getPlayers(PlayerState.IN).size()==1){
				Player win = getGameList().getPlayers(PlayerState.IN).get(0);
				getCoins().addCoins(win, false, 25);
				getStats().setInt(win, getStats().getInt(Stats.WIN, win)+1, Stats.WIN);
				broadcast( Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_WIN.getText(win.getName()));
			}
		}
	}
	
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void JoinHologram(PlayerJoinEvent ev){
		if(getState()!=GameState.LobbyPhase)return;
		int win = getStats().getInt(Stats.WIN, ev.getPlayer());
		int lose = getStats().getInt(Stats.LOSE, ev.getPlayer());
		getManager().getHologram().sendText(ev.getPlayer(),getManager().getLoc_stats().clone().add(0,0.3,0),new String[]{
			Color.GREEN+getType().getTyp()+Color.ORANGE+"§l Info",
		"Server: SkyWars §a"+kArcade.id,
		"Map: "+getWorldData().getMapName(),
		" ",
		Color.GREEN+getType().getTyp()+Color.ORANGE+"§l Stats",
		"Coins: "+getCoins().getCoins(ev.getPlayer()),
		"Rang: "+getStats().getRank(Stats.WIN, ev.getPlayer()),	
		"Kills: "+getStats().getInt(Stats.KILLS, ev.getPlayer()),
		"Tode: "+getStats().getInt(Stats.DEATHS, ev.getPlayer()),
		" ",
		"Gespielte Spiele: "+(win+lose),
		"Gewonnene Spiele: "+win,
		"Verlorene Spiele: "+lose
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
			plist.add(p);
		}
		PlayerVerteilung(verteilung(type.getTeam(),type.getTeam_size()), plist);
		
		for(Player player : getTeamList().keySet()){
			player.teleport(getWorldData().getLocs(getTeamList().get(player).Name()).get(0));
		}
		
		new AddonDay(getManager().getInstance(),getWorldData().getWorld());

		Scoreboard ps;
		for(Player p : UtilServer.getPlayers()){
			ps=Bukkit.getScoreboardManager().getNewScoreboard();
			
			UtilScoreboard.addBoard(ps, DisplaySlot.SIDEBAR, "§6§lEpicPvP.eu");
			UtilScoreboard.setScore(ps, "§7Kills: ", DisplaySlot.SIDEBAR, 6);
			UtilScoreboard.setScore(ps, "§e"+getStats().getInt(Stats.KILLS, p), DisplaySlot.SIDEBAR, 5);
			UtilScoreboard.setScore(ps, " ", DisplaySlot.SIDEBAR, 4);
			UtilScoreboard.setScore(ps, "§7Kit: ", DisplaySlot.SIDEBAR, 3);
			UtilScoreboard.setScore(ps, "§e"+ (kits.containsKey(p) ? kits.get(p) : "Kein Kit") , DisplaySlot.SIDEBAR,2);
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
		}
		
		setStart((60*10)+1);
		setState(GameState.InGame);
	}
	
	@EventHandler
	public void Ranking(RankingEvent ev){
		getManager().setRanking(Stats.WIN);
	}

}
