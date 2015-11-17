package me.kingingo.karcade.Game.Single.Games.CaveWars;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import lombok.Getter;
import me.kingingo.karcade.kArcade;
import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Enum.PlayerState;
import me.kingingo.karcade.Events.RankingEvent;
import me.kingingo.karcade.Game.Single.SingleWorldData;
import me.kingingo.karcade.Game.Single.Events.AddonEntityTeamKingDeathEvent;
import me.kingingo.karcade.Game.Single.Games.TeamGame;
import me.kingingo.karcade.Game.Single.Games.CaveWars.Addon.AddonDropItems;
import me.kingingo.karcade.Game.Single.addons.AddonEntityTeamKing;
import me.kingingo.karcade.Game.Single.addons.AddonPlaceBlockCanBreak;
import me.kingingo.karcade.Game.Single.addons.AddonTargetNextPlayer;
import me.kingingo.karcade.Game.Single.addons.AddonVoteTeam;
import me.kingingo.kcore.Addons.AddonDay;
import me.kingingo.kcore.Addons.AddonNight;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.Enum.Team;
import me.kingingo.kcore.Game.Events.GameStartEvent;
import me.kingingo.kcore.Game.Events.GameStateChangeEvent;
import me.kingingo.kcore.Kit.Kit;
import me.kingingo.kcore.Kit.KitType;
import me.kingingo.kcore.Kit.Perk;
import me.kingingo.kcore.Kit.Perks.PerkArrowFire;
import me.kingingo.kcore.Kit.Perks.PerkArrowInfinity;
import me.kingingo.kcore.Kit.Perks.PerkDeathDropOnly;
import me.kingingo.kcore.Kit.Perks.PerkEquipment;
import me.kingingo.kcore.Kit.Perks.PerkHeal;
import me.kingingo.kcore.Kit.Perks.PerkHealByHit;
import me.kingingo.kcore.Kit.Perks.PerkHolzfäller;
import me.kingingo.kcore.Kit.Perks.PerkMoreHearth;
import me.kingingo.kcore.Kit.Perks.PerkNoDropsByDeath;
import me.kingingo.kcore.Kit.Perks.PerkNoExplosionDamage;
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
import me.kingingo.kcore.Kit.Perks.PerkTNT;
import me.kingingo.kcore.Kit.Perks.PerkWalkEffect;
import me.kingingo.kcore.Kit.Shop.KitShop;
import me.kingingo.kcore.Language.Language;
import me.kingingo.kcore.Merchant.Merchant;
import me.kingingo.kcore.Merchant.MerchantOffer;
import me.kingingo.kcore.Permission.kPermission;
import me.kingingo.kcore.Scheduler.kScheduler;
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
import me.kingingo.kcore.Util.UtilParticle;
import me.kingingo.kcore.Util.UtilPlayer;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.kcore.Util.UtilString;
import me.kingingo.kcore.Util.UtilTime;
import me.kingingo.kcore.Villager.VillagerShop;
import me.kingingo.kcore.Villager.Event.VillagerShopEvent;

import org.bukkit.DyeColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
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

public class CaveWars extends TeamGame{

	private AddonTargetNextPlayer targetnextplayer;
	private AddonEntityTeamKing aek;
	private AddonDropItems adi;
	private AddonPlaceBlockCanBreak apbcb;
	@Getter
	private HashMap<Team,Boolean> teams = new HashMap<>();
	private KitShop kitshop;
	@Getter
	private CaveWarsType typ;
	private HashMap<Player,String> kits = new HashMap<>();
	private ArrayList<Location> buildings=new ArrayList<>();
	
	public CaveWars(kArcadeManager manager,CaveWarsType typ){
		super(manager);
		registerListener();
		long t = System.currentTimeMillis();
		setTyp(GameType.CaveWars);
		setState(GameState.Laden);
		this.typ=typ;
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
		setMin_Players(2);
		setMax_Players(getTyp().getMax());
		setVoteTeam(new AddonVoteTeam(this,getTyp().getTeam(),InventorySize._9,getTyp().getTeam_size()));
		
		kitshop=new KitShop(getManager().getInstance(),getGems(), getCoins(),getManager().getPermManager(), "Kit-Shop", InventorySize._27, new Kit[]{
			new Kit( "§aStarter",new String[]{"Der Starter bekommt kein Hunger."}, new ItemStack(Material.WOOD_SWORD),kPermission.SHEEPWARS_KIT_STARTER,KitType.STARTER,2000,500,new Perk[]{
				new PerkNoHunger()
			}),
			new Kit( "§eArrowMan",new String[]{"Der ArrowMan besitzt die 30% Chance","das seine Pfeile brennen."}, new ItemStack(Material.ARROW),kPermission.SHEEPWARS_KIT_ARROWMAN,KitType.KAUFEN,2000,500,new Perk[]{
				new PerkArrowFire(30)
			}),
			new Kit( "§eItemStealer",new String[]{"Der ItemStealer hat nach seinem"," Tod 10 sekunden um seine","Sachen aufzuheben solange","kann er sie nur aufheben."}, new ItemStack(Material.SHEARS),kPermission.SHEEPWARS_KIT_ITEMSTEALER,KitType.KAUFEN,2000,new Perk[]{
				new PerkDeathDropOnly(10)
			}),
			new Kit( "§eHealer",new String[]{"Der Healer heilt schneller."}, new ItemStack(Material.APPLE),kPermission.SHEEPWARS_KIT_HEALER,KitType.KAUFEN,2000,500,new Perk[]{
				new PerkHeal(10)
			}),
			new Kit( "§eDropper",new String[]{"Der Dropper lässt seine Sachen","beim Tod nicht fallen."}, new ItemStack(Material.DROPPER),kPermission.SHEEPWARS_KIT_DROPPER,KitType.KAUFEN,2000,500,new Perk[]{
				new PerkNoDropsByDeath()
			}),
			new Kit( "§eAnker",new String[]{"Der Anker bekommt kein Rückstoß."}, new ItemStack(Material.ANVIL),kPermission.SHEEPWARS_KIT_ANKER,KitType.KAUFEN,2000,500,new Perk[]{
				new PerkNoKnockback(getManager().getInstance())
			}),
			new Kit( "§ePerker",new String[]{"Der Perker stoppt beim angreiffen","vom Gegner die Perk's"}, new ItemStack(Material.TORCH),kPermission.SHEEPWARS_KIT_PERKER,KitType.KAUFEN,2000,500,new Perk[]{
				new PerkStopPerk(25)
			}),
			new Kit( "§eTNTer",new String[]{"Der TNT hat die 10% Chance","das an seiner Todes stelle","ein TNT spawnt."}, new ItemStack(Material.TNT),kPermission.SHEEPWARS_KIT_TNTER,KitType.KAUFEN,2000,500,new Perk[]{
				new PerkSpawnByDeath(EntityType.PRIMED_TNT,10)
			}),
			new Kit( "§eBuffer",new String[]{"Der Buffer bekommt wenn er Respawn","Feuerresistance und Schadenresistance."}, new ItemStack(Material.POTION),kPermission.SHEEPWARS_KIT_BUFFER,KitType.KAUFEN,2000,500,new Perk[]{
				new PerkRespawnBuff(new PotionEffect[]{new PotionEffect(PotionEffectType.FIRE_RESISTANCE,20*20,2),new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,20*20,2)})
			}),
			new Kit( "§eKnight",new String[]{"Der Knight bekommt beim Sneaken","höchstens 1 Herz schaden","wenn er angegriffen wird."}, new ItemStack(Material.DIAMOND_CHESTPLATE),kPermission.SHEEPWARS_KIT_KNIGHT,KitType.KAUFEN,2000,500,new Perk[]{
				new PerkSneakDamage(3.0)
			}),
			new Kit( "§eTheDeath",new String[]{"Der TheDeath drop beim Tod","ein Blindheits Trank."}, new ItemStack(Material.IRON_SWORD),kPermission.SHEEPWARS_KIT_THEDEATH,KitType.KAUFEN,2000,500,new Perk[]{
				new PerkPotionByDeath(new PotionEffect(PotionEffectType.BLINDNESS,20*5,1))
			}),
			new Kit( "§eSpringer",new String[]{"Der Springer bekommt kein Fallschaden."}, new ItemStack(Material.FEATHER),kPermission.SHEEPWARS_KIT_SPRINGER,KitType.KAUFEN,2000,500,new Perk[]{
				new PerkNoFalldamage()
			}),
			new Kit("§6PigZombie",new String[]{"Der PigZombie bekommt beim Respawnen","Speed und Regeneration"}, new ItemStack(Material.RAW_BEEF), kPermission.SHEEPWARS_KIT_PIGZOMBIE,KitType.PREMIUM,0,new Perk[]{
				new PerkRespawnBuff(new PotionEffect[]{new PotionEffect(PotionEffectType.SPEED,15*20,1), new PotionEffect(PotionEffectType.REGENERATION,10*20,1)})
			}),
			new Kit("§6Creeper",new String[]{"Der Creeper hat die 10% Chance","das an seiner Todes stelle","ein TNT spawnt."}, new ItemStack(Material.SKULL_ITEM,1,(byte)4), kPermission.SHEEPWARS_KIT_CREEPER,KitType.PREMIUM,0,new Perk[]{
				new PerkSpawnByDeath(EntityType.PRIMED_TNT,30)
			}),
			new Kit("§6Zombie",new String[]{"Der Zombie vergiftet seinen Gegner","bei einer Chance von 30%","für 3 sekunden."}, new ItemStack(Material.SKULL_ITEM,1,(byte)2), kPermission.SHEEPWARS_KIT_ZOMBIE, KitType.PREMIUM,0,new Perk[]{
				new PerkPoisen(3,30)
			}),
			new Kit( "§cOldRush",new String[]{"Der OldRush kriegt kein Fallschaden","15% Chance das seine Pfeile brennen","10% Chance das beim Tod ein TNT Spawn."}, new ItemStack(Material.BED), kPermission.SHEEPWARS_KIT_OLD_RUSH, KitType.SPEZIAL_KIT, 0,new Perk[]{
				new PerkNoFiredamage(),
				new PerkArrowFire(15),
				new PerkSpawnByDeath(EntityType.PRIMED_TNT,10)
			}),
			new Kit( "§aSuperman",new String[]{"Der Superman ist das Beste kit in SheepWars!"}, new ItemStack(Material.DIAMOND_SWORD),kPermission.SHEEPWARS_KIT_SUPERMAN,KitType.ADMIN,0,new Perk[]{
				new PerkNoHunger(),
				new PerkEquipment(new ItemStack[]{new ItemStack(Material.IRON_CHESTPLATE,1)}),
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
				new PerkWalkEffect(Effect.HEART,10),
				new PerkSpawnByDeath(EntityType.PRIMED_TNT,80),
				new PerkNoKnockback(getManager().getInstance())
			})
		});

		setWorldData(new SingleWorldData(manager,getType().getTyp()+getTyp().getTeam().length,getType().getKürzel()));
		getWorldData().Initialize();
		
		
		manager.DebugLog(t, this.getClass().getName());
		setState(GameState.LobbyPhase);
	}
	
	//GOLD ORANGE / REDSTONE
	//IRON PURPLE / REDSTONE
	//BRICK WHITE / REDSTONE
	
	//BLACK / REDSTONE VILLAGER
	//DARK BLUE / REDSTONE -> PLAYER SPAWNS
	//SPONGE -> AUSTAUSCHEN MIT TEAM FARBE
	//PURPLE /REDSTONE -> SPINNE
	
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
	public void Ranking(RankingEvent ev){
		getManager().setRanking(Stats.WIN);
	}
	
	@EventHandler
	public void RespawnLocation(PlayerRespawnEvent ev){
		 if(getGameList().isPlayerState(ev.getPlayer())==PlayerState.IN){
			 ev.setRespawnLocation( getWorldData().getLocs(getTeam(ev.getPlayer())).get(UtilMath.r(getWorldData().getLocs(getTeam(ev.getPlayer())).size())) );
			 ev.getPlayer().getInventory().addItem(new ItemStack(Material.COMPASS));
		 }
	}
	
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
		if(getState()==GameState.LobbyPhase){
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
		if(getState()==GameState.LobbyPhase){
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
		if(getState()!=GameState.InGame)return;
		setStart(getStart()-1);
		
		if(game_end()){
			setState(GameState.Restart);
			broadcastWithPrefixName("GAME_END");
			return;
		}

		for(Player p : UtilServer.getPlayers())UtilDisplay.displayTextBar(p, Language.getText(p, "GAME_END_IN", UtilTime.formatSeconds(getStart())));
		switch(getStart()){
			case 3597: 
				for(Player p : UtilServer.getPlayers()){
					if(getTeamList().containsKey(p))continue;
					Team t = littleTeam();
					addTeam(p, t);
					p.teleport(getWorldData().getLocs(t).get(0));
				}
				
				HashMap<Player,String> l= new HashMap<>();
				for(Player p : getTeamList().keySet()){
					l.put(p, getTeamList().get(p).getColor());
				}
				for(Kit kit : kitshop.getKits()){
					kit.StartGame(l);
					for(Perk perk : kit.getPlayers().keySet()){
						for(Player p : kit.getPlayers().get(perk))kits.put(p, kit.getName());
						break;
					}
				}
				
				TeamTab(typ.getTeam());
				break; 
			case 15:broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getStart()));break;
			case 10:broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getStart()));break;
			case 5:broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getStart()));break;
			case 4:broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getStart()));break;
			case 3:broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getStart()));break;
			case 2:broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getStart()));break;
			case 1:broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getStart()));break;
			case 0:
				broadcastWithPrefixName("GAME_END");
				setState(GameState.Restart);
			break;
		}
	}
	
	public HashMap<Team,Integer> verteilung(Team[] t){
		HashMap<Team,Integer> list = new HashMap<>();
		Collection<? extends Player> l = UtilServer.getPlayers();
	
		for(Team team : t){
			list.put(team, l.size()/t.length);
		}
		
		if(l.size()%t.length!=0){
			list.remove(t[0]);
			list.put(t[0], (l.size()/t.length)+1);
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
		v.addShop(UtilItem.Item(new ItemStack(Material.POTION), new String[]{"§aWillst du mit mir Drogen nehmen?"}, "§cTränke"), trank, 14);
		
		Merchant gold = new Merchant();
		gold.addOffer(new MerchantOffer(Gold(30), aek.getItem().clone()));
		gold.addOffer(new MerchantOffer(Silber(3), UtilItem.RenameItem(new ItemStack(Material.GOLDEN_APPLE), "Goldener Apfel")));
		gold.addOffer(new MerchantOffer(Gold(25), UtilItem.RenameItem(new ItemStack(Material.GOLDEN_APPLE,1,(byte)1), "Op Apfel")));
		gold.addOffer(new MerchantOffer(Gold(10), UtilItem.RenameItem(new ItemStack(Material.ENDER_PEARL), "Enderpearl")));
		v.addShop(UtilItem.Item(new ItemStack(Material.GOLDEN_APPLE), new String[]{"§aRette dich in größter Not!"}, "§cSpezial"), gold, 16);
		
		v.finish();
	}
	
	public void setVillager(Location l,EntityType e){
		VillagerShop v = new VillagerShop(getManager().getInstance(),e,"Villager-Shop",l,InventorySize._27);
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
		ItemStack r1 = UtilItem.RenameItem(new ItemStack(Material.LEATHER_HELMET), "Lederhelm");
		r1.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL,1);
		r1.addEnchantment(Enchantment.DURABILITY, 1);
		rustung.addOffer(new MerchantOffer(Bronze(1), r1));
		ItemStack r2 = UtilItem.RenameItem(new ItemStack(Material.LEATHER_CHESTPLATE), "Lederhemd");
		r2.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL,1);
		r2.addEnchantment(Enchantment.DURABILITY, 1);
		rustung.addOffer(new MerchantOffer(Bronze(1), r2));
		ItemStack r3 = UtilItem.RenameItem(new ItemStack(Material.LEATHER_LEGGINGS),"Lederhose");
		r3.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL,1);
		r3.addEnchantment(Enchantment.DURABILITY, 1);
		rustung.addOffer(new MerchantOffer(Bronze(1), r3));
		ItemStack r4 = UtilItem.RenameItem(new ItemStack(Material.LEATHER_BOOTS), "Lederschue");
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
		spezial.addOffer(new MerchantOffer(Silber(3), UtilItem.RenameItem(new ItemStack(Material.getMaterial(30)), "Spinnennetz")));
		v.addShop(UtilItem.Item(new ItemStack(46), new String[]{"§aZeige deinen Gegnern wer der Chef auf dem Schlachtfeld ist!"}, "§cSpezial"), spezial, 17);
		v.finish();
	}
	
	public DyeColor cd(String s){
		if(s.equalsIgnoreCase(Color.RED.toString()))return DyeColor.RED;
		if(s.equalsIgnoreCase(Color.YELLOW.toString()))return DyeColor.YELLOW;
		if(s.equalsIgnoreCase(Color.BLUE.toString()))return DyeColor.BLUE;
		if(s.equalsIgnoreCase(Color.GREEN.toString()))return DyeColor.GREEN;
		if(s.equalsIgnoreCase(Color.GRAY.toString()))return DyeColor.GRAY;
		if(s.equalsIgnoreCase(Color.WHITE.toString()))return DyeColor.WHITE;
		if(s.equalsIgnoreCase(Color.ORANGE.toString()))return DyeColor.ORANGE;
		if(s.equalsIgnoreCase(Color.PURPLE.toString()))return DyeColor.PURPLE;
		return DyeColor.BLACK;
	}
	
	public org.bukkit.Color c(String s){
		if(s.equalsIgnoreCase(Color.RED.toString()))return org.bukkit.Color.RED;
		if(s.equalsIgnoreCase(Color.YELLOW.toString()))return org.bukkit.Color.YELLOW;
		if(s.equalsIgnoreCase(Color.BLUE.toString()))return org.bukkit.Color.BLUE;
		if(s.equalsIgnoreCase(Color.GREEN.toString()))return org.bukkit.Color.GREEN;
		if(s.equalsIgnoreCase(Color.GRAY.toString()))return org.bukkit.Color.GRAY;
		if(s.equalsIgnoreCase(Color.WHITE.toString()))return org.bukkit.Color.WHITE;
		if(s.equalsIgnoreCase(Color.ORANGE.toString()))return org.bukkit.Color.ORANGE;
		if(s.equalsIgnoreCase(Color.PURPLE.toString()))return org.bukkit.Color.PURPLE;
		return org.bukkit.Color.BLACK;
	}
	
	@EventHandler
	public void Start(GameStartEvent ev){
		long time = System.currentTimeMillis();
		setStart(60*60);
		setState(GameState.InGame);
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
		EntityType sh_et=EntityType.SPIDER;
		
		if(getManager().getHoliday()!=null){
			switch(getManager().getHoliday()){
			case HALLOWEEN:
				et=EntityType.WITCH;
				new AddonNight(getManager().getInstance(),getWorldData().getWorld());
				for(Player p : UtilServer.getPlayers())p.getInventory().setHelmet(new ItemStack(Material.PUMPKIN));
				getWorldData().getWorld().setStorm(false);
				break;
			case WEIHNACHTEN:
				et=EntityType.SNOWMAN;
				new AddonNight(getManager().getInstance(),getWorldData().getWorld());
				//getWorldData().setBiome(l, add, biome);
				getWorldData().getWorld().setStorm(true);
				new kScheduler(getManager().getInstance(),new kScheduler.kSchedulerHandler(){

					@Override
					public void onRun() {
						for(Team team : getTyp().getTeam())UtilParticle.FIREWORKS_SPARK.display(10F, 4F, 10F, 0, 60, getWorldData().getLocs(team).get(0), 10);
					}
					
				},UpdateType.MIN_005);
				break;
			default:
				new AddonDay(getManager().getInstance(),getWorldData().getWorld());
				getWorldData().getWorld().setStorm(false);
				break;
			}
		}
		
		targetnextplayer=new AddonTargetNextPlayer(this);
		targetnextplayer.setMsg(false);
		targetnextplayer.setAktiv(true);
		targetnextplayer.setRadius(120);
		
		int i = 0;
		for(Team t : teams){
			getTeams().put(t, true);
			
			list = getWorldData().getLocs(t);
			for(Player p : getPlayerFrom(t)){
				p.teleport(list.get(i));
				p.getInventory().addItem(new ItemStack(Material.COMPASS));
				i++;
				if(i==list.size())i=0;
			}
		}
		
		adi= new AddonDropItems(this);
		aek=new AddonEntityTeamKing(teams,this, sh_et);
		apbcb= new AddonPlaceBlockCanBreak(getManager().getInstance(),new Material[]{Material.SANDSTONE,Material.SANDSTONE_STAIRS,Material.SAND,Material.getMaterial(31),Material.getMaterial(38),Material.getMaterial(37),Material.BROWN_MUSHROOM,Material.RED_MUSHROOM});
		
		LivingEntity s;
		for(Team t: aek.getTeams().keySet()){
			s = (LivingEntity)aek.getTeams().get(t);
			s.setCustomName(t.getColor()+getName(sh_et)+" ");
			if(s instanceof Sheep){
				((Sheep)s).setColor(cd(t.getColor()));
			}
		}
		
		if(getWorldData().existLoc(Team.BLACK)&&!getWorldData().getLocs(Team.BLACK).isEmpty()){
			for(Location loc : getWorldData().getLocs(Team.BLACK)){
				setVillager(loc, et);
			}
		}
		
		getManager().DebugLog(time, this.getClass().getName());
	}
	
	@EventHandler
	public void VillagerShop(VillagerShopEvent ev){
		if(getGameList().isPlayerState(ev.getPlayer())==PlayerState.OUT){
			ev.setCancelled(true);
		}
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

			broadcastWithPrefix("KILL_BY", new String[]{v,k});
			
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
			broadcastWithPrefix("DEATH",v);

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
				if(t!=null)broadcastWithPrefix("TEAM_WIN", t.getColor()+t.Name());
		
			}
		}
	}
	
	@EventHandler
	public void SheepDeath(AddonEntityTeamKingDeathEvent ev){
		if(getManager().isDisguiseManagerEnable())getManager().getDisguiseManager().undisguiseAll();
		getTeams().remove(ev.getTeam());
		getTeams().put(ev.getTeam(), false);
		if(ev.getKiller()!=null){
			getStats().setInt(ev.getKiller(), getStats().getInt(Stats.SHEEPWARS_KILLED_SHEEPS, ev.getKiller())+1, Stats.SHEEPWARS_KILLED_SHEEPS);
			broadcastWithPrefix("CAVEWARS_SPIDER_DEATH", new String[]{ev.getTeam().getColor()+ev.getTeam().Name(),ev.getKiller().getName()});
		}else{
			broadcastWithPrefix("CAVEWARS_SPIDER_DEATH", new String[]{ev.getTeam().getColor()+ev.getTeam().Name(),"FAIL"});
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
		
		if(!isState(GameState.LobbyPhase)&&getTeamList().containsKey(ev.getPlayer())){
			if(ev.getMessage().toCharArray()[0]=='#'){
				Team t = getTeam(ev.getPlayer());
				broadcast("§7["+t.getColor()+t.Name()+"§7] "+ev.getPlayer().getDisplayName()+": §7"+ev.getMessage().subSequence(1, ev.getMessage().length()));
			}else{
				Team t = getTeam(ev.getPlayer());
				for(Player p : getPlayerFrom(getTeam(ev.getPlayer()))){
					UtilPlayer.sendMessage(p,t.getColor()+"Team-Chat "+ev.getPlayer().getDisplayName()+":§7 "+ev.getMessage());
				}
			}
		}else if(getState()!=GameState.LobbyPhase&&getGameList().getPlayers(PlayerState.OUT).contains(ev.getPlayer())){
			ev.setCancelled(true);
			UtilPlayer.sendMessage(ev.getPlayer(),Language.getText(ev.getPlayer(), "PREFIX_GAME",getType().getTyp())+Language.getText(ev.getPlayer(), "SPECTATOR_CHAT_CANCEL"));
		}else{
			UtilServer.broadcast(getManager().getPermManager().getPrefix(ev.getPlayer())+ev.getPlayer().getDisplayName()+":§7 "+ev.getMessage());
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
			Language.getText(ev.getPlayer(), "GAME_HOLOGRAM_CAVEWARS", getStats().getInt(Stats.SHEEPWARS_KILLED_SHEEPS, ev.getPlayer())),
			" ",
			Language.getText(ev.getPlayer(), "GAME_HOLOGRAM_GAMES", (win+lose)),
			Language.getText(ev.getPlayer(), "GAME_HOLOGRAM_WINS", win),
			Language.getText(ev.getPlayer(), "GAME_HOLOGRAM_LOSE", lose),
			});
		ev.getPlayer().getInventory().addItem(UtilItem.RenameItem(new ItemStack(Material.CHEST), "§bKitShop"));
	}
	
}
