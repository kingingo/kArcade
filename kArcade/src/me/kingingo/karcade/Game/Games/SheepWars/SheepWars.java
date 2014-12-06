package me.kingingo.karcade.Game.Games.SheepWars;

import java.util.ArrayList;
import java.util.HashMap;

import lombok.Getter;
import me.kingingo.karcade.kArcade;
import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Enum.PlayerState;
import me.kingingo.karcade.Enum.Team;
import me.kingingo.karcade.Events.RankingEvent;
import me.kingingo.karcade.Game.Events.GameStateChangeEvent;
import me.kingingo.karcade.Game.Games.TeamGame;
import me.kingingo.karcade.Game.Games.SheepWars.Addon.AddonDropItems;
import me.kingingo.karcade.Game.World.WorldData;
import me.kingingo.karcade.Game.addons.AddonEnterhacken;
import me.kingingo.karcade.Game.addons.AddonEntityKing;
import me.kingingo.karcade.Game.addons.AddonPlaceBlockCanBreak;
import me.kingingo.karcade.Game.addons.AddonVoteTeam;
import me.kingingo.karcade.Game.addons.Events.AddonEntityKingDeathEvent;
import me.kingingo.kcore.Addons.AddonDay;
import me.kingingo.kcore.Addons.AddonNight;
import me.kingingo.kcore.Calendar.Calendar.CalendarType;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.Game.Events.GameStartEvent;
import me.kingingo.kcore.Hologram.Hologram;
import me.kingingo.kcore.Kit.Kit;
import me.kingingo.kcore.Kit.KitType;
import me.kingingo.kcore.Kit.Perk;
import me.kingingo.kcore.Kit.Perks.PerkArrowFire;
import me.kingingo.kcore.Kit.Perks.PerkDeathDropOnly;
import me.kingingo.kcore.Kit.Perks.PerkHeal;
import me.kingingo.kcore.Kit.Perks.PerkNoDropsByDeath;
import me.kingingo.kcore.Kit.Perks.PerkNoFalldamage;
import me.kingingo.kcore.Kit.Perks.PerkNoFiredamage;
import me.kingingo.kcore.Kit.Perks.PerkNoHunger;
import me.kingingo.kcore.Kit.Perks.PerkNoKnockback;
import me.kingingo.kcore.Kit.Perks.PerkPoisen;
import me.kingingo.kcore.Kit.Perks.PerkPotionByDeath;
import me.kingingo.kcore.Kit.Perks.PerkRespawnBuff;
import me.kingingo.kcore.Kit.Perks.PerkSneakDamage;
import me.kingingo.kcore.Kit.Perks.PerkSpawnByDeath;
import me.kingingo.kcore.Kit.Perks.PerkStopPerk;
import me.kingingo.kcore.Kit.Shop.KitShop;
import me.kingingo.kcore.Merchant.Merchant;
import me.kingingo.kcore.Merchant.MerchantOffer;
import me.kingingo.kcore.Permission.Permission;
import me.kingingo.kcore.PlayerStats.Stats;
import me.kingingo.kcore.Scheduler.kScheduler;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.C;
import me.kingingo.kcore.Util.InventorySize;
import me.kingingo.kcore.Util.UtilDisplay;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilMath;
import me.kingingo.kcore.Util.UtilParticle;
import me.kingingo.kcore.Util.UtilPlayer;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.kcore.Util.UtilTime;
import me.kingingo.kcore.Villager.VillagerShop;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SheepWars extends TeamGame{

	WorldData wd;
	Hologram hm;
	AddonEnterhacken aeh;
	AddonEntityKing aek;
	AddonDropItems adi;
	AddonPlaceBlockCanBreak apbcb;
	@Getter
	HashMap<Team,Boolean> teams = new HashMap<>();
	KitShop kitshop;
	@Getter
	SheepWarsType typ;
	HashMap<Player,String> kits = new HashMap<>();
	
	public SheepWars(kArcadeManager manager,SheepWarsType typ){
		super(manager);
		registerListener();
		long t = System.currentTimeMillis();
		this.typ=typ;
		manager.setState(GameState.Laden);
		setTyp(typ.getType());
		setItemDrop(true);
		setItemPickup(true);
		setReplace_Water(true);
		setCreatureSpawn(false);
		setBlockBurn(false);
		setPlayerShearEntity(true);
		setBlockSpread(false);
		setDeathDropItems(true);
		getInventoryTypDisallow().add(InventoryType.WORKBENCH);
		getInventoryTypDisallow().add(InventoryType.DISPENSER);
		getInventoryTypDisallow().add(InventoryType.BEACON);
		getItemPickupDeny().add(Material.CACTUS.getId());
		getInteractDeny().add(Material.getMaterial(43));
		getInteractDeny().add(Material.GLOWSTONE);
		getInteractDeny().add(Material.ENDER_STONE);
		setReplace_Fire(true);
		setReplace_Lava(true);
		setReplace_Water(true);
		setCompassAddon(true);
		getManager().getPermManager().setSetAllowTab(false);
		setDamage(true);
		setDamageEvP(false);
		setDamagePvE(true);
		setDamagePvP(true);
		setDamageTeamOther(true);
		setDamageTeamSelf(false);
		setProjectileDamage(true);
		setRespawn(true);
		setBlackFade(false);
		setFoodChange(true);
		setExplosion(true);
		setBlockBreak(true);
		setBlockPlace(true);
		setMin_Players(getTyp().getMin());
		setMax_Players(getTyp().getMax());
		setVoteTeam(new AddonVoteTeam(manager,getTyp().getTeam(),InventorySize._9,4));
		
		kitshop=new KitShop(getManager().getInstance(), getCoins(),getTokens(), getManager().getPermManager(), "Kit-Shop", InventorySize._27, new Kit[]{
			new Kit( "§aStarter",new String[]{"Der Starter bekommt kein Hunger."}, new ItemStack(Material.WOOD_SWORD),Permission.SHEEPWARS_KIT_STARTER,KitType.STARTER,2000,new Perk[]{
				new PerkNoHunger()
			}),
			new Kit( "§eArrowMan",new String[]{"Der ArrowMan besitzt die 30% Chance","das seine Pfeile brennen."}, new ItemStack(Material.ARROW),Permission.SHEEPWARS_KIT_ARROWMAN,KitType.KAUFEN,2000,new Perk[]{
				new PerkArrowFire(30)
			}),
			new Kit( "§eItemStealer",new String[]{"Der ItemStealer hat nach seinem"," Tod 10 sekunden um seine","Sachen aufzuheben solange","kann er sie nur aufheben."}, new ItemStack(Material.SHEARS),Permission.SHEEPWARS_KIT_ITEMSTEALER,KitType.KAUFEN,2000,new Perk[]{
				new PerkDeathDropOnly(10)
			}),
			new Kit( "§eHealer",new String[]{"Der Healer heilt schneller."}, new ItemStack(Material.APPLE),Permission.SHEEPWARS_KIT_HEALER,KitType.KAUFEN,2000,new Perk[]{
				new PerkHeal(10)
			}),
			new Kit( "§eDropper",new String[]{"Der Dropper lässt seine Sachen","beim Tod nicht fallen."}, new ItemStack(Material.DROPPER),Permission.SHEEPWARS_KIT_DROPPER,KitType.KAUFEN,2000,new Perk[]{
				new PerkNoDropsByDeath()
			}),
			new Kit( "§eAnker",new String[]{"Der Anker bekommt kein Rückstoß."}, new ItemStack(Material.ANVIL),Permission.SHEEPWARS_KIT_ANKER,KitType.KAUFEN,2000,new Perk[]{
				new PerkNoKnockback(getManager().getInstance())
			}),
			new Kit( "§ePerker",new String[]{"Der Perker stoppt beim angreiffen","vom Gegner die Perk's"}, new ItemStack(Material.TORCH),Permission.SHEEPWARS_KIT_PERKER,KitType.KAUFEN,2000,new Perk[]{
				new PerkStopPerk(10)
			}),
			new Kit( "§eTNTer",new String[]{"Der TNT hat die 10% Chance","das an seiner Todes stelle","ein TNT spawnt."}, new ItemStack(Material.TNT),Permission.SHEEPWARS_KIT_TNTER,KitType.KAUFEN,2000,new Perk[]{
				new PerkSpawnByDeath(EntityType.PRIMED_TNT,10)
			}),
			new Kit( "§eBuffer",new String[]{"Der Buffer bekommt wenn er Respawn","Feuerresistance und Schadenresistance."}, new ItemStack(Material.POTION),Permission.SHEEPWARS_KIT_BUFFER,KitType.KAUFEN,2000,new Perk[]{
				new PerkRespawnBuff(new PotionEffect[]{new PotionEffect(PotionEffectType.FIRE_RESISTANCE,20*20,2),new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,20*20,2)})
			}),
			new Kit( "§eKnight",new String[]{"Der Knight bekommt beim Sneaken","höchstens 1 Herz schaden","wenn er angegriffen wird."}, new ItemStack(Material.DIAMOND_CHESTPLATE),Permission.SHEEPWARS_KIT_KNIGHT,KitType.KAUFEN,2000,new Perk[]{
				new PerkSneakDamage(3.0)
			}),
			new Kit( "§eTheDeath",new String[]{"Der TheDeath drop beim Tod","ein Blindheits Trank."}, new ItemStack(Material.IRON_SWORD),Permission.SHEEPWARS_KIT_THEDEATH,KitType.KAUFEN,2000,new Perk[]{
				new PerkPotionByDeath(new PotionEffect(PotionEffectType.BLINDNESS,20*5,1),"Blindheit")
			}),
			new Kit( "§eSpringer",new String[]{"Der Springer bekommt kein Fallschaden."}, new ItemStack(Material.FEATHER),Permission.SHEEPWARS_KIT_SPRINGER,KitType.KAUFEN,2000,new Perk[]{
				new PerkNoFalldamage()
			}),
			new Kit("§6PigZombie",new String[]{"Der PigZombie bekommt beim Respawnen","Speed und Regeneration"}, new ItemStack(Material.RAW_BEEF), Permission.SHEEPWARS_KIT_PIGZOMBIE,KitType.PREMIUM,0,new Perk[]{
				new PerkRespawnBuff(new PotionEffect[]{new PotionEffect(PotionEffectType.SPEED,15*20,1), new PotionEffect(PotionEffectType.REGENERATION,10*20,1)})
			}),
			new Kit("§6Creeper",new String[]{"Der Creeper hat die 10% Chance","das an seiner Todes stelle","ein TNT spawnt."}, new ItemStack(Material.SKULL_ITEM,1,(byte)4), Permission.SHEEPWARS_KIT_CREEPER,KitType.PREMIUM,0,new Perk[]{
				new PerkSpawnByDeath(EntityType.PRIMED_TNT,30)
			}),
			new Kit("§6Zombie",new String[]{"Der Zombie vergiftet seinen Gegner","bei einer Chance von 30%","für 3 sekunden."}, new ItemStack(Material.SKULL_ITEM,1,(byte)2), Permission.SHEEPWARS_KIT_ZOMBIE, KitType.PREMIUM,0,new Perk[]{
				new PerkPoisen(3,30)
			}),
			new Kit( "§cOldRush",new String[]{"Der OldRush kriegt kein Fallschaden","15% Chance das seine Pfeile brennen","5% Chance das beim Tod ein TNT Spawn."}, new ItemStack(Material.BED), Permission.SHEEPWARS_KIT_OLD_RUSH, KitType.SPEZIAL_KIT, 0,new Perk[]{
				new PerkNoFiredamage(),
				new PerkArrowFire(15),
				new PerkSpawnByDeath(EntityType.PRIMED_TNT,5)
			})
		});

		this.wd=new WorldData(manager,getType());
		wd.Initialize();
		setWorldData(wd);
		
		if(manager.getHoliday()==CalendarType.WEIHNACHTEN){
			getWorldData().setBiome(getWorldData().getLocs(Team.BLACK.toString()).get(0), Biome.TAIGA);
		}
		
		manager.DebugLog(t, this.getClass().getName());
		
		manager.setState(GameState.LobbyPhase);
	}
	
	//GOLD ORANGE
	//IRON PURPLE
	//BRICK WHITE
	
	//EMERALD_BLOCK&MELON_BLOCK||SPONGE = SHEEP PLACE
	//EMERALD_BLOCK&EMERALD_BLOCK VILLAGER
	
	//TEAM RED
	//TEAM YELLOW
	//TEAM BLUE
	//TEAM GREEN
	//SPEZIAL VILLAGER BLACK
	
	@EventHandler
	public void ShopOpen(PlayerInteractEvent ev){
		if(UtilEvent.isAction(ev, ActionType.R)){
			if(getManager().getState()!=GameState.LobbyPhase)return;
			if(ev.getPlayer().getItemInHand()!=null&&UtilItem.ItemNameEquals(ev.getPlayer().getItemInHand(), UtilItem.RenameItem(new ItemStack(Material.CHEST), "§bKitShop"))){
				ev.getPlayer().openInventory(kitshop.getInventory());
			}
		}
	}
	
	@EventHandler
	public void Ranking(RankingEvent ev){
		getManager().setRanking(Stats.WIN);
	}
	
	ArrayList<Location> l;
	@EventHandler
	public void RespawnLocation(PlayerRespawnEvent ev){
		 if(getGameList().isPlayerState(ev.getPlayer())==PlayerState.IN){
			l= getWorldData().getLocs(getTeam(ev.getPlayer()).Name());
			 ev.setRespawnLocation( l.get(UtilMath.r(l.size())) );
		 }
	}
	
//	@EventHandler
//	public void Explosion(EntityExplodeEvent ev){
//		for(int i = 0; i<ev.blockList().size(); i++){
//			if(ev.blockList().get(i).getType()==Material.GLOWSTONE){
//				ev.blockList().remove(i);
//			}
//		}
//	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void Explode (EntityExplodeEvent ev){
		for(Block b : ev.blockList()){
			if(b.getType()==Material.GLOWSTONE){
				b.setTypeId(0);
			}
		}
	}
	
	HashMap<Team,ArrayList<Block>> block = new HashMap<>();
	@EventHandler(priority=EventPriority.HIGHEST)
	public void Break(BlockBreakEvent ev){
		if(getManager().getState()==GameState.LobbyPhase){
			ev.setCancelled(true);
			return;
		}
		if(ev.getBlock().getType()==Material.GLOWSTONE){
			Team t = getTeam(ev.getPlayer());
			if(block.containsKey(t)){
				if(block.get(t).contains(ev.getBlock())){
					block.get(t).remove(ev.getBlock());
					ev.getBlock().getWorld().dropItem(ev.getBlock().getLocation().add(0,0.1,0),new ItemStack(Material.GLOWSTONE,1));
					ev.getBlock().setTypeId(0);
					return;
				}
			}
			ev.setCancelled(true);
		}else if(ev.getBlock().getTypeId()==43&&ev.getBlock().getData()==9&&!ev.isCancelled()){
			ev.getBlock().setTypeId(0);
			ev.getBlock().getWorld().dropItem(ev.getBlock().getLocation().add(0,0.1,0),new ItemStack(Material.getMaterial(43),1,(byte)9));
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void Place(BlockPlaceEvent ev){
		if(getManager().getState()==GameState.LobbyPhase){
			ev.setCancelled(true);
			return;
		}
		if(ev.getBlock().getType()==Material.GLOWSTONE){
			if(!block.containsKey(getTeam(ev.getPlayer())))block.put(getTeam(ev.getPlayer()), new ArrayList<Block>());
			block.get(getTeam(ev.getPlayer())).add(ev.getBlock());
		}
	}
	
	public boolean game_end(){
		Team t=null;
		for(Player p : getTeamList().keySet()){
			t=getTeamList().get(p);
			for(Player p1 : getTeamList().keySet()){
				if(getTeamList().get(p1)!=t){
					return false;
				}
			}
		}
		return true;
	}
	
	@EventHandler
	public void InGame(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		if(getManager().getState()!=GameState.InGame)return;
		getManager().setStart(getManager().getStart()-1);
		
		if(game_end()){
			getManager().setState(GameState.Restart);
			getManager().broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_END.getText());
			return;
		}
		
		for(Player p : UtilServer.getPlayers())UtilDisplay.displayTextBar(p, Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())));
		switch(getManager().getStart()){
			case 3597: 
				for(Player p : UtilServer.getPlayers()){
					if(getTeamList().containsKey(p))continue;
					Team t = littleTeam();
					addTeam(p, t);
					p.teleport(getWorldData().getLocs(t.Name()).get(0));
				}
				
				HashMap<Player,String> l= new HashMap<>();
				for(Player p : getTeamList().keySet()){
					l.put(p, getTeamList().get(p).getColor());
				}
				for(Kit kit : kitshop.getKits()){
					kit.StartGame(l);
					for(Player p :kit.getPlayers())kits.put(p, kit.getName());
				}
				
				TeamTab(typ.getTeam());
				break; 
			case 15:getManager().broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())));break;
			case 10:getManager().broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())));break;
			case 5:getManager().broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())));break;
			case 4:getManager().broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())));break;
			case 3:getManager().broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())));break;
			case 2:getManager().broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())));break;
			case 1:getManager().broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())));break;
			case 0:
				getManager().broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_END.getText());
				getManager().setState(GameState.Restart);
			break;
		}
	}
	
	public HashMap<Team,Integer> verteilung(Team[] t){
		HashMap<Team,Integer> list = new HashMap<>();
		Player[] l = UtilServer.getPlayers();
	
		for(Team team : t){
			list.put(team, l.length/t.length);
		}
		
		if(l.length%t.length!=0){
			list.remove(t[0]);
			list.put(t[0], (l.length/t.length)+1);
		}

		return list;
	}

	public ItemStack Silber(int i){
		return UtilItem.RenameItem(new ItemStack(Material.IRON_INGOT,i), "§bSilver");
	}
	
	public ItemStack Gold(int i){
		return UtilItem.RenameItem(new ItemStack(Material.GOLD_INGOT,i), "§bGold");
	}
	
	public ItemStack Bronze(int i){
		return UtilItem.RenameItem(new ItemStack(Material.CLAY_BRICK,i), "§bBronze");
	}
	
	public void setSpezialVillager(Location l,EntityType e){
		l=l.add(0.5,0.5,0.5);
		VillagerShop v = new VillagerShop(getManager().getInstance(),e,"Spezial-Shop",l,InventorySize._27);
		
		Merchant rustung = new Merchant();
		ItemStack r1 = UtilItem.RenameItem(new ItemStack(Material.IRON_CHESTPLATE), "Spezial Eisenhemd Lvl 1");
		r1.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
		r1.addEnchantment(Enchantment.PROTECTION_FIRE, 2);
		r1.addEnchantment(Enchantment.DURABILITY, 1);
		rustung.addOffer(new MerchantOffer(Gold(15),r1));
		ItemStack r2 = UtilItem.RenameItem(new ItemStack(Material.IRON_CHESTPLATE), "Spezial Eisenhemd Lvl 2");
		r2.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
		r2.addEnchantment(Enchantment.PROTECTION_FIRE, 4);
		r2.addEnchantment(Enchantment.DURABILITY, 1);
		rustung.addOffer(new MerchantOffer(Gold(30),r2));
		ItemStack r3 = UtilItem.RenameItem(new ItemStack(Material.DIAMOND_CHESTPLATE), "Diamanthemd");
		r3.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
		rustung.addOffer(new MerchantOffer(Gold(50),r3));
		v.addShop(UtilItem.Item(new ItemStack(Material.DIAMOND_CHESTPLATE), new String[]{"§aSpezial Schutz!"}, "§cRüstung"), rustung, 10);
		
		Merchant schwert = new Merchant();
		ItemStack s1 = UtilItem.RenameItem(new ItemStack(Material.IRON_SWORD), "Spezial Schwert Lvl 1");
		s1.addEnchantment(Enchantment.DAMAGE_ALL, 2);
		s1.addEnchantment(Enchantment.FIRE_ASPECT, 1);
		schwert.addOffer(new MerchantOffer(Gold(20),s1));
		ItemStack s2 = UtilItem.RenameItem(new ItemStack(Material.IRON_SWORD), "Spezial Schwert Lvl 2");
		s2.addEnchantment(Enchantment.DAMAGE_ALL, 3);
		s2.addEnchantment(Enchantment.FIRE_ASPECT, 1);
		schwert.addOffer(new MerchantOffer(Gold(40),s2));
		v.addShop(UtilItem.Item(new ItemStack(Material.IRON_SWORD), new String[]{"§aLehre deinen Gegner Schmerz!"}, "§cSchwerter"), schwert, 12);
		
		Merchant trank = new Merchant();
		trank.addOffer(new MerchantOffer(Gold(10),UtilItem.RenameItem(new ItemStack(Material.POTION,3,(byte)8229), "Heilung II")));
		trank.addOffer(new MerchantOffer(Gold(10),UtilItem.RenameItem(new ItemStack(Material.POTION,3,(byte)8225), "Regeneration II")));
		trank.addOffer(new MerchantOffer(Gold(10),UtilItem.RenameItem(new ItemStack(Material.POTION,3,(byte)8233), "Stärke II")));
		v.addShop(UtilItem.Item(new ItemStack(Material.POTION), new String[]{"§aWillst du mir mir Drogen nehmen?"}, "§cTränke"), trank, 14);
		
		Merchant gold = new Merchant();
		gold.addOffer(new MerchantOffer(Gold(30), aek.getItem().clone()));
		gold.addOffer(new MerchantOffer(Silber(3), UtilItem.RenameItem(new ItemStack(Material.GOLDEN_APPLE), "Goldener Apfel")));
		gold.addOffer(new MerchantOffer(Gold(25), UtilItem.RenameItem(new ItemStack(Material.GOLDEN_APPLE,1,(byte)1), "Op Apfel")));
		gold.addOffer(new MerchantOffer(Gold(10), UtilItem.RenameItem(new ItemStack(Material.ENDER_PEARL), "Enderpearl")));
		v.addShop(UtilItem.Item(new ItemStack(Material.GOLDEN_APPLE), new String[]{"§aRette dich in größter Not!"}, "§cSpezial"), gold, 16);
		
		v.finish();
	}
	
	public void setVillager(Team t,EntityType e){
		Location l=getWorldData().getLocs(getVillagerSpawn(t).Name()).get(0).add(0.5,0.3,0.5);
		VillagerShop v = new VillagerShop(getManager().getInstance(),e,t.getColor()+"Villager-Shop",l,InventorySize._27);
		v.setDamage(false);
		v.setMove(false);
		
		Merchant bloecke = new Merchant();
		bloecke.addOffer(new MerchantOffer(Bronze(1), new ItemStack(Material.getMaterial(43),4,(byte)9)));
		bloecke.addOffer(new MerchantOffer(Bronze(3), new ItemStack(Material.GLOWSTONE)));
		bloecke.addOffer(new MerchantOffer(Bronze(7),new ItemStack(Material.ENDER_STONE)));
		v.addShop(UtilItem.Item(new ItemStack(Material.getMaterial(43),1,(byte)9), new String[]{"§aHier findest du alles was du zum bauen brauchst"}, "§cBlöcke"), bloecke, 9);
	
		Merchant spitzhacken = new Merchant();
		ItemStack spitzhack1 = UtilItem.RenameItem(new ItemStack(Material.WOOD_PICKAXE), "Holzhacke");
		spitzhack1.addEnchantment(Enchantment.DURABILITY, 1);
		spitzhack1.addEnchantment(Enchantment.DIG_SPEED, 1);
		spitzhacken.addOffer(new MerchantOffer(Bronze(3), spitzhack1));
		ItemStack spitzhack2 = UtilItem.RenameItem(new ItemStack(Material.GOLD_PICKAXE), "Goldhacke Lvl 1");
		spitzhack2.addEnchantment(Enchantment.DURABILITY, 1);
		spitzhack2.addEnchantment(Enchantment.DIG_SPEED, 1);
		spitzhacken.addOffer(new MerchantOffer(Silber(1), spitzhack2));
		ItemStack spitzhack3 = UtilItem.RenameItem(new ItemStack(Material.GOLD_PICKAXE), "Goldhacke Lvl 2");
		spitzhack3.addEnchantment(Enchantment.DURABILITY, 1);
		spitzhack3.addEnchantment(Enchantment.DIG_SPEED, 2);
		spitzhacken.addOffer(new MerchantOffer(Silber(3), spitzhack3));
		ItemStack spitzhack4 = UtilItem.RenameItem(new ItemStack(Material.IRON_PICKAXE), "Eisenhacke");
		spitzhack4.addEnchantment(Enchantment.DURABILITY, 1);
		spitzhack4.addEnchantment(Enchantment.DIG_SPEED, 2);
		spitzhacken.addOffer(new MerchantOffer(Gold(3), spitzhack4));
		v.addShop(UtilItem.Item(new ItemStack(274), new String[]{"§aBaue Blöcke deines Gegners ab!"}, "§cSpitzhacken"), spitzhacken, 10);
		
		Merchant rustung = new Merchant();
		ItemStack r1 = UtilItem.RenameItem(UtilItem.LSetColor(new ItemStack(Material.LEATHER_HELMET), c(t.getColor())), t.getColor()+"Lederhelm");
		r1.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL,1);
		r1.addEnchantment(Enchantment.DURABILITY, 1);
		rustung.addOffer(new MerchantOffer(Bronze(1), r1));
		ItemStack r2 = UtilItem.RenameItem(UtilItem.LSetColor(new ItemStack(Material.LEATHER_CHESTPLATE), c(t.getColor())), t.getColor()+"Lederhemd");
		r2.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL,1);
		r2.addEnchantment(Enchantment.DURABILITY, 1);
		rustung.addOffer(new MerchantOffer(Bronze(1), r2));
		ItemStack r3 = UtilItem.RenameItem(UtilItem.LSetColor(new ItemStack(Material.LEATHER_LEGGINGS), c(t.getColor())), t.getColor()+"Lederhose");
		r3.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL,1);
		r3.addEnchantment(Enchantment.DURABILITY, 1);
		rustung.addOffer(new MerchantOffer(Bronze(1), r3));
		ItemStack r4 = UtilItem.RenameItem(UtilItem.LSetColor(new ItemStack(Material.LEATHER_BOOTS), c(t.getColor())), t.getColor()+"Lederschue");
		r4.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL,1);
		r4.addEnchantment(Enchantment.DURABILITY, 1);
		rustung.addOffer(new MerchantOffer(Bronze(1), r4));
		ItemStack r5 = UtilItem.RenameItem(new ItemStack(Material.CHAINMAIL_CHESTPLATE), "Kettenhemd Lvl 1");
		r5.addEnchantment(Enchantment.DURABILITY, 1);
		r5.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
		rustung.addOffer(new MerchantOffer(Silber(1),r5));
		ItemStack r6 = UtilItem.RenameItem(new ItemStack(Material.CHAINMAIL_CHESTPLATE), "Kettenhemd Lvl 2");
		r6.addEnchantment(Enchantment.DURABILITY, 1);
		r6.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
		rustung.addOffer(new MerchantOffer(Silber(3),r6));
		ItemStack r7 = UtilItem.RenameItem(new ItemStack(Material.CHAINMAIL_CHESTPLATE), "Kettenhemd Lvl 3");
		r7.addEnchantment(Enchantment.DURABILITY, 1);
		r7.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
		r7.addEnchantment(Enchantment.PROTECTION_FIRE, 2);
		rustung.addOffer(new MerchantOffer(Silber(7),r7));
		ItemStack r8 = UtilItem.RenameItem(new ItemStack(Material.IRON_CHESTPLATE), "Eisenhemd");
		r8.addEnchantment(Enchantment.DURABILITY, 1);
		r8.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
		r8.addEnchantment(Enchantment.PROTECTION_FIRE, 2);
		rustung.addOffer(new MerchantOffer(Gold(3),r8));
		v.addShop(UtilItem.Item(new ItemStack(Material.IRON_CHESTPLATE), new String[]{"§aSchütze dich vor deinen Gegnern!"}, "§cRüstung"), rustung, 11);
		
		Merchant schwerter = new Merchant();
		ItemStack s1 = UtilItem.RenameItem(new ItemStack(Material.WOOD_SWORD), "Knüppel");
		s1.addEnchantment(Enchantment.KNOCKBACK, 1);
		schwerter.addOffer(new MerchantOffer(Bronze(8), s1));
		ItemStack s2 = UtilItem.RenameItem(new ItemStack(Material.GOLD_SWORD), "Goldschwert Lvl 1");
		s2.addEnchantment(Enchantment.DURABILITY, 1);
		s2.addEnchantment(Enchantment.DAMAGE_ALL, 1);
		schwerter.addOffer(new MerchantOffer(Silber(1), s2));
		ItemStack s3 = UtilItem.RenameItem(new ItemStack(Material.GOLD_SWORD), "Goldschwert Lvl 2");
		s3.addEnchantment(Enchantment.DURABILITY, 1);
		s3.addEnchantment(Enchantment.DAMAGE_ALL, 2);
		schwerter.addOffer(new MerchantOffer(Silber(3), s3));
		ItemStack s4 = UtilItem.RenameItem(new ItemStack(Material.GOLD_SWORD), "Goldschwert Lvl 3");
		s4.addEnchantment(Enchantment.DURABILITY, 1);
		s4.addEnchantment(Enchantment.DAMAGE_ALL, 3);
		schwerter.addOffer(new MerchantOffer(Silber(7), s4));
		ItemStack s5 = UtilItem.RenameItem(new ItemStack(Material.IRON_SWORD), "Eisenschwert");
		s5.addEnchantment(Enchantment.DURABILITY, 1);
		s5.addEnchantment(Enchantment.DAMAGE_ALL, 1);
		s5.addEnchantment(Enchantment.KNOCKBACK, 1);
		schwerter.addOffer(new MerchantOffer(Gold(3), s5));
		v.addShop(UtilItem.Item(new ItemStack(Material.IRON_SWORD), new String[]{"§aGreife deine Gegner an und töte sie!"}, "§cSchwerter"), schwerter, 12);
		
		Merchant bogen = new Merchant();
		ItemStack b0 = UtilItem.RenameItem(new ItemStack(Material.BOW), "§bBogen Lvl 1");
		bogen.addOffer(new MerchantOffer(Silber(10),b0));
		ItemStack b1 = UtilItem.RenameItem(new ItemStack(Material.BOW), "Bogen Lvl 2");
		b1.addEnchantment(Enchantment.ARROW_INFINITE, 1);
		bogen.addOffer(new MerchantOffer(Gold(3),b1));
		ItemStack b2 = UtilItem.RenameItem(new ItemStack(Material.BOW), "Bogen Lvl 3");
		b2.addEnchantment(Enchantment.ARROW_INFINITE, 1);
		b2.addEnchantment(Enchantment.ARROW_DAMAGE, 1);
		bogen.addOffer(new MerchantOffer(Gold(7),b2));
		ItemStack b3 = UtilItem.RenameItem(new ItemStack(Material.BOW), "Bogen Lvl 4");
		b3.addEnchantment(Enchantment.ARROW_INFINITE, 1);
		b3.addEnchantment(Enchantment.ARROW_DAMAGE, 2);
		b3.addEnchantment(Enchantment.ARROW_KNOCKBACK, 1);
		bogen.addOffer(new MerchantOffer(Gold(13),b3));
		bogen.addOffer(new MerchantOffer(Bronze(5),UtilItem.RenameItem(new ItemStack(Material.ARROW), "Pfeil")));
		v.addShop(UtilItem.Item(new ItemStack(Material.BOW), new String[]{"§aDer fernkampf hat schon einige Kriege entschieden!"}, "§cBögen"), bogen, 13);
		
		Merchant nahrung = new Merchant();
		nahrung.addOffer(new MerchantOffer(Bronze(2), UtilItem.RenameItem(new ItemStack(Material.APPLE), "Apfel")));
		nahrung.addOffer(new MerchantOffer(Bronze(3), UtilItem.RenameItem(new ItemStack(Material.BREAD), "Brot")));
		nahrung.addOffer(new MerchantOffer(Bronze(3), UtilItem.RenameItem(new ItemStack(Material.getMaterial(364)), "Steak")));
		nahrung.addOffer(new MerchantOffer(Bronze(5), UtilItem.RenameItem(new ItemStack(Material.CAKE), "Kuchen")));
		nahrung.addOffer(new MerchantOffer(Silber(2), UtilItem.RenameItem(new ItemStack(Material.GOLDEN_APPLE), "Goldapfel")));
		v.addShop(UtilItem.Item(new ItemStack(260), new String[]{"§aAuch ein Soldat muss irgendwann was essen!"}, "§cNahrung"),nahrung, 14);
		
		Merchant kisten = new Merchant();
		kisten.addOffer(new MerchantOffer(Silber(2), UtilItem.RenameItem(new ItemStack(Material.CHEST), "Kiste")));
		v.addShop(UtilItem.Item(new ItemStack(54), new String[]{"§aDein Inventar ist nicht Unendlich, die Anzahl der Kisten schon!"}, "§cKisten"), kisten, 15);
		
		Merchant trank = new Merchant();
		trank.addOffer(new MerchantOffer(Silber(5), UtilItem.RenameItem(new ItemStack(Material.POTION,1,(byte)8261), "Heilung")));
		trank.addOffer(new MerchantOffer(Silber(5), UtilItem.RenameItem(new ItemStack(Material.POTION,1,(byte)8194), "Schnelligkeit")));
		trank.addOffer(new MerchantOffer(Gold(5), UtilItem.RenameItem(new ItemStack(Material.POTION,1,(byte)8227), "Feuerresitänz")));
		trank.addOffer(new MerchantOffer(Gold(5), UtilItem.RenameItem(new ItemStack(Material.POTION,1,(byte)8193), "Regeneration")));
		trank.addOffer(new MerchantOffer(Gold(5), UtilItem.RenameItem(new ItemStack(Material.POTION,1,(byte)8201), "Stärke")));
		v.addShop(UtilItem.Item(new ItemStack(Material.POTION), new String[]{"§aAls Soldat kann man schonmal die eine oder andere Droge gebrauchen!"}, "§cTränke"), trank, 16);
		
		Merchant spezial = new Merchant();
		spezial.addOffer(new MerchantOffer(Silber(3), UtilItem.RenameItem(new ItemStack(Material.TNT), "TNT")));
		spezial.addOffer(new MerchantOffer(Gold(1), UtilItem.RenameItem(new ItemStack(Material.FLINT_AND_STEEL), "Feuerzeug")));
		spezial.addOffer(new MerchantOffer(Bronze(5), UtilItem.RenameItem(new ItemStack(Material.LADDER), "Leiter")));
		//spezial.addOffer(new MerchantOffer(Gold(3), UtilItem.RenameItem(new ItemStack(346), "Angel")));
		spezial.addOffer(new MerchantOffer(Silber(3), UtilItem.RenameItem(new ItemStack(Material.getMaterial(30)), "Spinnennetz")));
		v.addShop(UtilItem.Item(new ItemStack(46), new String[]{"§aZeige deinen Gegnern wer der Chef auf dem Schlachtfeld ist!"}, "§cSpezial"), spezial, 17);
		v.finish();
	}
	
	public DyeColor cd(String s){
		if(s.equalsIgnoreCase(C.cRed))return DyeColor.RED;
		if(s.equalsIgnoreCase(C.cYellow))return DyeColor.YELLOW;
		if(s.equalsIgnoreCase(C.cBlue))return DyeColor.BLUE;
		if(s.equalsIgnoreCase(C.cGreen))return DyeColor.GREEN;
		if(s.equalsIgnoreCase(C.cGray))return DyeColor.GRAY;
		if(s.equalsIgnoreCase(C.cWhite))return DyeColor.WHITE;
		if(s.equalsIgnoreCase(C.mOrange))return DyeColor.ORANGE;
		if(s.equalsIgnoreCase(C.cPurple))return DyeColor.PURPLE;
		return DyeColor.BLACK;
	}
	
	public Color c(String s){
		if(s.equalsIgnoreCase(C.cRed))return Color.RED;
		if(s.equalsIgnoreCase(C.cYellow))return Color.YELLOW;
		if(s.equalsIgnoreCase(C.cBlue))return Color.BLUE;
		if(s.equalsIgnoreCase(C.cGreen))return Color.GREEN;
		if(s.equalsIgnoreCase(C.cGray))return Color.GRAY;
		if(s.equalsIgnoreCase(C.cWhite))return Color.WHITE;
		if(s.equalsIgnoreCase(C.mOrange))return Color.ORANGE;
		if(s.equalsIgnoreCase(C.cPurple))return Color.PURPLE;
		return Color.BLACK;
	}
	
	@EventHandler
	public void Start(GameStartEvent ev){
		long time = System.currentTimeMillis();
		getManager().setStart(60*60);
		getManager().setState(GameState.InGame);
		ArrayList<Player> plist = new ArrayList<>();
		for(Player p : UtilServer.getPlayers()){
			getManager().Clear(p);
			getGameList().addPlayer(p,PlayerState.IN);
			plist.add(p);
		}
		PlayerVerteilung(verteilung(typ.getTeam()), plist);
		
		Team[] teams = getTyp().getTeam();
		ArrayList<Location> list;
		
		for(Entity e : getWorldData().getWorld().getEntities()){
			if(!(e instanceof Player)&&!(e instanceof Villager)){
				e.remove();
			}
		}
		
		EntityType et = EntityType.VILLAGER;
		EntityType sh_et=EntityType.SHEEP;
		
		if(getManager().getHoliday()!=null){
			switch(getManager().getHoliday()){
			case HELLOWEEN:
				et=EntityType.WITCH;
				new AddonNight(getManager().getInstance(),getWorldData().getWorld());
				for(Player p : UtilServer.getPlayers())p.getInventory().setHelmet(new ItemStack(Material.PUMPKIN));
				break;
			case WEIHNACHTEN:
				et=EntityType.SNOWMAN;
				new AddonNight(getManager().getInstance(),getWorldData().getWorld());
				//getWorldData().getWorld().setStorm(true);
				new kScheduler(getManager().getInstance(),new kScheduler.kSchedulerHandler(){

					@Override
					public void onRun() {
						//for(Team team : getTyp().getTeam())UtilParticle.FIREWORKS_SPARK.display(10F, 4F, 10F, 0, 60, getWorldData().getLocs(team.Name()).get(0), 10);
						getWorldData().getWorld().setStorm(true);
					}
					
				},UpdateType.MIN_005);
				break;
			default:
				new AddonDay(getManager().getInstance(),getWorldData().getWorld());
				break;
			}
		}
		
		int i = 0;
		for(Team t : teams){
			getTeams().put(t, true);
			setVillager(t,et);
			list = getWorldData().getLocs(t.Name());
			for(Player p : getPlayerFrom(t)){
				p.teleport(list.get(i));
				i++;
				if(i==list.size())i=0;
			}
		}
		
		adi= new AddonDropItems(this);
		aek=new AddonEntityKing(getManager(), teams,this, sh_et);
		apbcb= new AddonPlaceBlockCanBreak(getManager().getInstance(),new Material[]{Material.getMaterial(31),Material.getMaterial(38),Material.getMaterial(37),Material.BROWN_MUSHROOM,Material.RED_MUSHROOM});
		aeh=new AddonEnterhacken(getManager().getInstance());
		getWorldData().getWorld().setStorm(false);
		LivingEntity s;
		for(Team t: aek.getTeams().keySet()){
			s = (LivingEntity)aek.getTeams().get(t);
			s.setCustomName(t.getColor()+getName(sh_et)+" ");
			if(s instanceof Sheep){
				((Sheep)s).setColor(cd(t.getColor()));
			}
		}
		
		if(getWorldData().getLocs().containsKey(Team.BLACK.Name())&&!getWorldData().getLocs().get(Team.BLACK.Name()).isEmpty()){
			for(Location loc : getWorldData().getLocs(Team.BLACK.Name())){
				setSpezialVillager(loc,et);
			}
		}
		hm.RemoveAllText();
		getManager().DebugLog(time, this.getClass().getName());
	}
	
	public String getName(EntityType e){
		switch(e){
		case SHEEP: return "Schaf";
		case SPIDER: return "Spinne";
		case ZOMBIE: return "Zombie";
		case CAVE_SPIDER: return "Spinne";
		default: return "Tier";
		}
	}
	
	public Team getVillagerSpawn(Team team){
		switch(team){
		case RED:return Team.VILLAGE_RED;
		case BLUE:return Team.VILLAGE_BLUE;
		case YELLOW:return Team.VILLAGE_YELLOW;
		case GREEN:return Team.VILLAGE_GREEN;
		default:
		return Team.VILLAGE_RED;
		}
	}
	
	String v;
	String k;
	@EventHandler
	public void Death(PlayerDeathEvent ev){
		if(ev.getEntity() instanceof Player&&ev.getEntity().getKiller() instanceof Player){
			Player killer = ev.getEntity().getKiller();
			Player victim = ev.getEntity();
			Team t = getTeam(victim);
			getCoins().addCoins(killer, false, 4,getType());
			getStats().setInt(killer, getStats().getInt(Stats.KILLS, killer)+1, Stats.KILLS);
			getStats().setInt(victim, getStats().getInt(Stats.DEATHS, victim)+1, Stats.DEATHS);
			v=t.getColor()+victim.getName();
			k=getTeam(killer).getColor()+killer.getName();
			
			if(kits.containsKey(victim)){
				v=v+"§a["+kits.get(victim)+"§a]";
			}
			if(kits.containsKey(killer)){
				k=k+"§a["+kits.get(killer)+"§a]";
			}
			
			getManager().broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.KILL_BY.getText(new String[]{v,k}));
			
			if(getTeams().get(t)==false){
				getGameList().addPlayer(victim, PlayerState.OUT);
				getStats().setInt(victim, getStats().getInt(Stats.LOSE, victim)+1, Stats.LOSE);
			}
		}else if(ev.getEntity() instanceof Player){
			Player victim = ev.getEntity();
			Team t = getTeam(victim);
			getStats().setInt(victim, getStats().getInt(Stats.DEATHS, victim)+1, Stats.DEATHS);
			v=t.getColor()+victim.getName();
			
			if(kits.containsKey(victim)){
				v=v+"§a["+kits.get(victim)+"§a]";
			}
			getManager().broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.DEATH.getText(new String[]{v}));

			if(getTeams().get(t)==false){
				getGameList().addPlayer(victim, PlayerState.OUT);
				getStats().setInt(victim, getStats().getInt(Stats.LOSE, victim)+1, Stats.LOSE);
			}
		}
	}
	
	@EventHandler
	public void GameStateChange(GameStateChangeEvent ev){
		if(ev.getTo()==GameState.Restart){
			if(game_end()){
				Team t = lastTeam();
				for(Player p : getPlayerFrom(t)){
					if(getGameList().isPlayerState(p)==PlayerState.IN){
						getStats().setInt(p, getStats().getInt(Stats.WIN, p)+1, Stats.WIN);
						getCoins().addCoins(p, false, 10,getType());
					}
				}
				getManager().broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.TEAM_WIN.getText(t.getColor()+t.Name()));
		
			}
		}
	}
	
	@EventHandler
	public void SheepDeath(AddonEntityKingDeathEvent ev){
		Team t = getTeam(ev.getKiller());
		if(getManager().isDisguiseManagerEnable())getManager().getDisguiseManager().undisguiseAll();
		getTeams().remove(ev.getTeam());
		getTeams().put(ev.getTeam(), false);
		if(ev.getKiller()!=null){
			getStats().setInt(ev.getKiller(), getStats().getInt(Stats.SHEEPWARS_KILLED_SHEEPS, ev.getKiller())+1, Stats.SHEEPWARS_KILLED_SHEEPS);
			getManager().broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.SHEEPWARS_SHEEP_DEATH.getText(new String[]{ev.getTeam().getColor()+ev.getTeam().Name(),ev.getKiller().getName()}));
		}else{
			getManager().broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.SHEEPWARS_SHEEP_DEATH.getText(new String[]{ev.getTeam().getColor()+ev.getTeam().Name(),"FAIL"}));
		}
	}
	
	@EventHandler
	public void Chat(AsyncPlayerChatEvent ev){
		if(ev.isCancelled())return;
		ev.setCancelled(true);
		if(!getManager().isState(GameState.LobbyPhase)&&getTeamList().containsKey(ev.getPlayer())){
			if(ev.getMessage().toCharArray()[0]=='#'){
				Team t = getTeam(ev.getPlayer());
				getManager().broadcast("§7["+t.getColor()+t.Name()+"§7] "+ev.getPlayer().getDisplayName()+": §7"+ev.getMessage().subSequence(1, ev.getMessage().length()));
			}else{
				Team t = getTeam(ev.getPlayer());
				for(Player p : getPlayerFrom(getTeam(ev.getPlayer()))){
					UtilPlayer.sendMessage(p,t.getColor()+"Team-Chat "+ev.getPlayer().getDisplayName()+":§7 "+ev.getMessage());
				}
			}
		}else if(getManager().getState()!=GameState.LobbyPhase&&getGameList().getPlayers(PlayerState.OUT).contains(ev.getPlayer())){
			ev.setCancelled(true);
			UtilPlayer.sendMessage(ev.getPlayer(),Text.PREFIX_GAME.getText(getType().getTyp())+Text.SPECTATOR_CHAT_CANCEL.getText());
		}else{
			UtilServer.broadcast(getManager().getPermManager().getPrefix(ev.getPlayer())+ev.getPlayer().getDisplayName()+":§7 "+ev.getMessage());
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void JoinHologram(PlayerJoinEvent ev){
		if(getManager().getState()!=GameState.LobbyPhase)return;
		if(hm==null)hm=new Hologram(getManager().getInstance());

		int win = getStats().getInt(Stats.WIN, ev.getPlayer());
		int lose = getStats().getInt(Stats.LOSE, ev.getPlayer());
		getManager().getLoc_stats().getWorld().loadChunk(getManager().getLoc_stats().getWorld().getChunkAt(getManager().getLoc_stats()));
		hm.sendText(ev.getPlayer(),getManager().getLoc_stats().clone().add(0, 0.4, 0),new String[]{
		C.cGreen+getType().getTyp()+C.mOrange+C.Bold+" Info",
		"Server: SheepWars §a"+kArcade.id,
		"Map: "+wd.getMapName(),
		" ",
		C.cGreen+getType().getTyp()+C.mOrange+C.Bold+" Stats",
		//"Rang: "+getStats().getRank(Stats.WIN, ev.getPlayer()),	
		"Kills: "+getStats().getInt(Stats.KILLS, ev.getPlayer()),
		"Tode: "+getStats().getInt(Stats.DEATHS, ev.getPlayer()),
		"Schaf-Kills: "+getStats().getInt(Stats.SHEEPWARS_KILLED_SHEEPS, ev.getPlayer()),
		" ",
		"Gespielte Spiele: "+(win+lose),
		"Gewonnene Spiele: "+win,
		"Verlorene Spiele: "+lose
		});
		ev.getPlayer().getInventory().addItem(UtilItem.RenameItem(new ItemStack(Material.CHEST), "§bKitShop"));
	}
	
}
