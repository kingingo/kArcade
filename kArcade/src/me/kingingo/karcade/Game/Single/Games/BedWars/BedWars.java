package me.kingingo.karcade.Game.Single.Games.BedWars;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import lombok.Getter;
import me.kingingo.karcade.kArcade;
import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Events.RankingEvent;
import me.kingingo.karcade.Events.WorldLoadEvent;
import me.kingingo.karcade.Game.Events.GameStartEvent;
import me.kingingo.karcade.Game.Events.GameStateChangeEvent;
import me.kingingo.karcade.Game.Single.SingleWorldData;
import me.kingingo.karcade.Game.Single.Addons.AddonBedTeamKing;
import me.kingingo.karcade.Game.Single.Addons.AddonDropItems;
import me.kingingo.karcade.Game.Single.Addons.AddonEnterhacken;
import me.kingingo.karcade.Game.Single.Addons.AddonPlaceBlockCanBreak;
import me.kingingo.karcade.Game.Single.Addons.AddonVoteTeam;
import me.kingingo.karcade.Game.Single.Events.AddonBedKingDeathEvent;
import me.kingingo.karcade.Game.Single.Games.TeamGame;
import me.kingingo.karcade.Game.World.Event.WorldDataInitializeEvent;
import me.kingingo.kcore.Addons.AddonDay;
import me.kingingo.kcore.Addons.AddonHalloween;
import me.kingingo.kcore.Addons.AddonNight;
import me.kingingo.kcore.Calendar.Calendar;
import me.kingingo.kcore.Calendar.Calendar.CalendarType;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.Enum.PlayerState;
import me.kingingo.kcore.Enum.Team;
import me.kingingo.kcore.Enum.Zeichen;
import me.kingingo.kcore.Language.Language;
import me.kingingo.kcore.LaunchItem.LaunchItemManager;
import me.kingingo.kcore.Merchant.Merchant;
import me.kingingo.kcore.Merchant.MerchantOffer;
import me.kingingo.kcore.Permission.kPermission;
import me.kingingo.kcore.StatsManager.Stats;
import me.kingingo.kcore.StatsManager.Event.PlayerStatsLoadedEvent;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.Color;
import me.kingingo.kcore.Util.InventorySize;
import me.kingingo.kcore.Util.Title;
import me.kingingo.kcore.Util.UtilDisplay;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilMath;
import me.kingingo.kcore.Util.UtilPlayer;
import me.kingingo.kcore.Util.UtilScoreboard;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.kcore.Util.UtilString;
import me.kingingo.kcore.Util.UtilTime;
import me.kingingo.kcore.Util.UtilWorldEdit;
import me.kingingo.kcore.Villager.VillagerShop;
import me.kingingo.kcore.Villager.Event.VillagerShopEvent;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;

public class BedWars extends TeamGame{

	private AddonEnterhacken aeh;
	private AddonBedTeamKing abtk;
	private AddonDropItems adi;
	private AddonPlaceBlockCanBreak apbcb;
	@Getter
	private HashMap<Team,Boolean> teams = new HashMap<>();
	@Getter
	private BedWarsType typ;
	private LaunchItemManager liManager;
	
	public BedWars(kArcadeManager manager,BedWarsType typ){
		super(manager);
		registerListener();
		long t = System.currentTimeMillis();
		setTyp(GameType.BedWars);
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
		setMin_Players(getTyp().getMin());
		setMax_Players(getTyp().getMax());
		if(getTyp().getTeam_size()!=1)setVoteTeam(new AddonVoteTeam(this,getTyp().getTeam(),InventorySize._9,getTyp().getTeam_size()));
		
		liManager=new LaunchItemManager( manager.getInstance() );
		
		setWorldData(new SingleWorldData(manager,getType().getTyp()+getTyp().getTeam().length,getType().getKürzel()));
		getWorldData().setCleanroomChunkGenerator(true);
		getWorldData().Initialize();
		manager.DebugLog(t, this.getClass().getName());
		
		setState(GameState.LobbyPhase);
	}
	
	public String shortMap(String map,String t){
		if( (map+t).length() > 16 ){
			return map.substring(0, (16-(t.length()+1)) )+t;
		}
		return map+t;
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
	public void WorldData(WorldDataInitializeEvent ev){
		if(Calendar.holiday==CalendarType.WEIHNACHTEN){
			if(getWorldData().existLoc(Team.BLACK)&&!getWorldData().getLocs(Team.BLACK).isEmpty()){
				UtilWorldEdit.simulateSnow(getWorldData().getLocs(Team.BLACK).get(0), 150);
			}else{
				UtilWorldEdit.simulateSnow(getWorldData().getLocs(Team.RED).get(0), 150);
			}
		}
	}
	
	@EventHandler
	public void WorldLoad(WorldLoadEvent ev){
		getWorldData().setMapName( shortMap(getWorldData().getMapName()," "+getTyp().getTeam().length+"x"+getTyp().getTeam_size()) );
	}
	
	@EventHandler
	public void Ranking(RankingEvent ev){
		getManager().setRanking(Stats.WIN);
	}
	
	@EventHandler
	public void RespawnLocation(PlayerRespawnEvent ev){
		 if(getGameList().isPlayerState(ev.getPlayer())==PlayerState.IN){
			 ev.setRespawnLocation( getWorldData().getLocs(getTeam(ev.getPlayer())).get(UtilMath.r(getWorldData().getLocs(getTeam(ev.getPlayer())).size())) );
		 }
	}
	
	HashMap<Team,ArrayList<Block>> block = new HashMap<>();
	@EventHandler(priority=EventPriority.HIGHEST)
	public void Break(BlockBreakEvent ev){
		if(getState()==GameState.LobbyPhase){
			ev.setCancelled(true);
			return;
		}
		if(ev.getBlock().getTypeId()==43&&ev.getBlock().getData()==9&&!ev.isCancelled()){
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
	}
	
	//ER PRÜFT OB NUR NOCH EIN TEAM ÜBRIG IST!
	public boolean game_end(){
		for(Player p : getTeamList().keySet()){
			for(Player p1 : getTeamList().keySet()){
				if(getTeamList().get(p1)!=getTeamList().get(p)){
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
		if(getTyp().getTeam_size()==1){
			HashMap<Team,Integer> list = new HashMap<>();
			for(Team team : t)list.put(team, 1);
			return list;
		}else{
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
		gold.addOffer(new MerchantOffer(Silber(3), UtilItem.RenameItem(new ItemStack(Material.GOLDEN_APPLE), "Goldener Apfel")));
		gold.addOffer(new MerchantOffer(Gold(25), UtilItem.RenameItem(new ItemStack(Material.GOLDEN_APPLE,1,(byte)1), "Op Apfel")));
		gold.addOffer(new MerchantOffer(Gold(10), UtilItem.RenameItem(new ItemStack(Material.ENDER_PEARL), "Enderpearl")));
		v.addShop(UtilItem.Item(new ItemStack(Material.GOLDEN_APPLE), new String[]{"§aRette dich in größter Not!"}, "§cSpezial"), gold, 16);
		
		v.finish();
	}
	
	public void setVillager(Team t,EntityType e){
		Location l=getWorldData().getLocs(getVillagerSpawn(t)).get(0).add(0.5,0.3,0.5);
		VillagerShop v = new VillagerShop(getManager().getInstance(),e,t.getColor()+"Villager-Shop",l,InventorySize._27);
		v.setDamage(false);
		v.setMove(false);
		
		Merchant bloecke = new Merchant();
		bloecke.addOffer(new MerchantOffer(Bronze(1), new ItemStack(24,4)));
		bloecke.addOffer(new MerchantOffer(Bronze(7),new ItemStack(Material.ENDER_STONE)));
		v.addShop(UtilItem.Item(new ItemStack(24), new String[]{"§aHier findest du alles was du zum bauen brauchst"}, "§cBlöcke"), bloecke, 9);
	
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
		
		if(getManager().getHoliday()!=null){
			switch(getManager().getHoliday()){
			case HALLOWEEN:
				new AddonNight(getManager().getInstance(),getWorldData().getWorld());
				new AddonHalloween(getManager().getInstance());
				getWorldData().getWorld().setStorm(false);
				break;
			case WEIHNACHTEN:
				et=EntityType.SNOWMAN;
				new AddonDay(getManager().getInstance(),getWorldData().getWorld());
				getWorldData().getWorld().setStorm(false);
				break;
			default:
				new AddonDay(getManager().getInstance(),getWorldData().getWorld());
				getWorldData().getWorld().setStorm(false);
				break;
			}
		}
		
		setBoard(Bukkit.getScoreboardManager().getNewScoreboard());
		UtilScoreboard.addBoard(getBoard(), DisplaySlot.SIDEBAR, "§eBedWars Teams");
		
		int i = 0;
		for(Team t : teams){
			UtilScoreboard.setScore(getBoard(), t.getColor()+t.Name()+" §a"+Zeichen.HÄKCHEN_FETT.getIcon(), DisplaySlot.SIDEBAR, 1);
			getTeams().put(t, true);
			setVillager(t,et);
			list = getWorldData().getLocs(t);
			for(Player p : getPlayerFrom(t)){
				p.setScoreboard(getBoard());
				p.teleport(list.get(i));
				i++;
				if(i==list.size())i=0;
			}
		}

		abtk=new AddonBedTeamKing(getManager(), teams,this);
		adi= new AddonDropItems(this,getTyp().getDrop_rate());
		if(getTyp() == BedWarsType._8x1)adi.setDrop_name(false);
		apbcb= new AddonPlaceBlockCanBreak(getManager().getInstance(),new Material[]{Material.getMaterial(31),Material.getMaterial(38),Material.getMaterial(37),Material.BROWN_MUSHROOM,Material.RED_MUSHROOM});
		aeh=new AddonEnterhacken(getManager().getInstance());
		
		if(getWorldData().existLoc(Team.BLACK)&&!getWorldData().getLocs(Team.BLACK).isEmpty()){
			for(Location loc : getWorldData().getLocs(Team.BLACK)){
				setSpezialVillager(loc,et);
			}
		}
		
		setStart(60*60);
		setState(GameState.InGame);
		getManager().DebugLog(time, this.getClass().getName());
	}
	
	@EventHandler
	public void VillagerShop(VillagerShopEvent ev){
		if(getGameList().isPlayerState(ev.getPlayer())==PlayerState.OUT){
			ev.setCancelled(true);
		}
	}
	
	public Team getVillagerSpawn(Team team){
		switch(team){
		case RED:return Team.VILLAGE_RED;
		case BLUE:return Team.VILLAGE_BLUE;
		case YELLOW:return Team.VILLAGE_YELLOW;
		case GREEN:return Team.VILLAGE_GREEN;
		case GRAY:return Team.VILLAGE_GRAY;
		case PINK:return Team.VILLAGE_PINK;
		case ORANGE:return Team.VILLAGE_ORANGE;
		case PURPLE:return Team.VILLAGE_PURPLE;
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

			broadcastWithPrefix("KILL_BY",new String[]{v,k});
			
			if(getTeams().get(t)==false){
				getGameList().addPlayer(victim, PlayerState.OUT);
				getStats().setInt(victim, getStats().getInt(Stats.LOSE, victim)+1, Stats.LOSE);
			}
		}else if(ev.getEntity() instanceof Player){
			Player victim = ev.getEntity();
			Team t = getTeam(victim);
			getStats().setInt(victim, getStats().getInt(Stats.DEATHS, victim)+1, Stats.DEATHS);
			v=t.getColor()+victim.getName();
			broadcastWithPrefix("DEATH", v);

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
	public void SheepDeath(AddonBedKingDeathEvent ev){
		if(getManager().isDisguiseManagerEnable())getManager().getDisguiseManager().undisguiseAll();
		getTeams().remove(ev.getTeam());
		getTeams().put(ev.getTeam(), false);
		Title t = new Title("","");
		if(ev.getKiller()!=null){
			getStats().setInt(ev.getKiller(), getStats().getInt(Stats.BEDWARS_ZERSTOERTE_BEDs, ev.getKiller())+1, Stats.BEDWARS_ZERSTOERTE_BEDs);
		}
		UtilScoreboard.resetScore(getBoard(), ev.getTeam().getColor()+ev.getTeam().Name()+" §a"+Zeichen.HÄKCHEN_FETT.getIcon(), DisplaySlot.SIDEBAR);
		UtilScoreboard.setScore(getBoard(), ev.getTeam().getColor()+ev.getTeam().Name()+" §4"+Zeichen.MAHLZEICHEN_FETT.getIcon(), DisplaySlot.SIDEBAR, 1);
		
		for(Player player : UtilServer.getPlayers()){
			t.setSubtitle(Language.getText(player,"BEDWARS_BED_BROKE", ev.getTeam().getColor()+"§l"+ev.getTeam().Name()));
			t.send(player);
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
			UtilPlayer.sendMessage(ev.getPlayer(),Language.getText(ev.getPlayer(), "PREFIX_GAME", getType().getTyp())+Language.getText(ev.getPlayer(), "SPECTATOR_CHAT_CANCEL"));
		}else{
			UtilServer.broadcast(getManager().getPermManager().getPrefix(ev.getPlayer())+ev.getPlayer().getDisplayName()+":§7 "+ev.getMessage());
		}
	}
	
	@EventHandler
	public void StatsLoaded(PlayerStatsLoadedEvent ev){
		if(getState()!=GameState.LobbyPhase)return;
		int win = getStats().getInt(Stats.WIN, ev.getPlayer());
		int lose = getStats().getInt(Stats.LOSE, ev.getPlayer());
		
		Bukkit.getScheduler().runTask(getManager().getInstance(), new Runnable() {
			
			@Override
			public void run() {
				getManager().getHologram().sendText(ev.getPlayer(),getManager().getLoc_stats(),new String[]{
					Color.GREEN+getType().getTyp()+Color.ORANGE+"§l Info",
					Language.getText(ev.getPlayer(), "GAME_HOLOGRAM_SERVER",getType().getTyp()+" §a"+kArcade.id),
					Language.getText(ev.getPlayer(), "GAME_HOLOGRAM_MAP", getWorldData().getMapName()),
					" ",
					Language.getText(ev.getPlayer(), "GAME_HOLOGRAM_STATS", getType().getTyp()),
					Language.getText(ev.getPlayer(), "GAME_HOLOGRAM_KILLS", getStats().getInt(Stats.KILLS, ev.getPlayer())),
					Language.getText(ev.getPlayer(), "GAME_HOLOGRAM_DEATHS", getStats().getInt(Stats.DEATHS, ev.getPlayer())),
					Language.getText(ev.getPlayer(), "GAME_HOLOGRAM_BEDWARS", getStats().getInt(Stats.BEDWARS_ZERSTOERTE_BEDs, ev.getPlayer())),
					" ",
					Language.getText(ev.getPlayer(), "GAME_HOLOGRAM_GAMES", (win+lose)),
					Language.getText(ev.getPlayer(), "GAME_HOLOGRAM_WINS", win),
					Language.getText(ev.getPlayer(), "GAME_HOLOGRAM_LOSE", lose),
				});
			}
		});
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void JoinHologram(PlayerJoinEvent ev){
		if(getState()!=GameState.LobbyPhase)return;
	}
	
}
