package me.kingingo.karcade.Game.Games.SheepWars;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.Getter;
import me.kingingo.karcade.kArcade;
import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Enum.PlayerState;
import me.kingingo.karcade.Enum.Team;
import me.kingingo.karcade.Events.RankingEvent;
import me.kingingo.karcade.Game.Events.GameStartEvent;
import me.kingingo.karcade.Game.Events.GameStateChangeEvent;
import me.kingingo.karcade.Game.Games.TeamGame;
import me.kingingo.karcade.Game.Games.SheepWars.Addon.AddonDropItems;
import me.kingingo.karcade.Game.World.WorldData;
import me.kingingo.karcade.Game.addons.AddonEntityKing;
import me.kingingo.karcade.Game.addons.AddonNight;
import me.kingingo.karcade.Game.addons.AddonPlaceBlockCanBreak;
import me.kingingo.karcade.Game.addons.AddonVoteTeam;
import me.kingingo.karcade.Game.addons.Events.AddonEntityKingDeathEvent;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.Hologram.Hologram;
import me.kingingo.kcore.Kit.Kit;
import me.kingingo.kcore.Kit.KitType;
import me.kingingo.kcore.Kit.Perk;
import me.kingingo.kcore.Kit.Perks.PerkArrowFire;
import me.kingingo.kcore.Kit.Perks.PerkHeal;
import me.kingingo.kcore.Kit.Perks.PerkNoKnockback;
import me.kingingo.kcore.Kit.Perks.PerkPoisen;
import me.kingingo.kcore.Kit.Perks.PerkSneakDamage;
import me.kingingo.kcore.Kit.Shop.KitShop;
import me.kingingo.kcore.Merchant.Merchant;
import me.kingingo.kcore.Merchant.MerchantOffer;
import me.kingingo.kcore.Permission.Permission;
import me.kingingo.kcore.PlayerStats.Stats;
import me.kingingo.kcore.ScoreboardManager.PlayerScoreboard;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.C;
import me.kingingo.kcore.Util.InventorySize;
import me.kingingo.kcore.Util.UtilDisplay;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilLocation;
import me.kingingo.kcore.Util.UtilMath;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.kcore.Util.UtilTime;
import me.kingingo.kcore.Villager.VillagerShop;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class SheepWars extends TeamGame{

	WorldData wd;
	HashMap<Player,PlayerScoreboard> boards = new HashMap<>();
	Hologram hm;
	AddonEntityKing aek;
	AddonNight an;
	AddonDropItems adi;
	AddonPlaceBlockCanBreak apbcb;
	AddonVoteTeam avt;
	@Getter
	HashMap<Team,Boolean> teams = new HashMap<>();
	KitShop kitshop;
	@Getter
	SheepWarsType typ;
	
	public SheepWars(kArcadeManager manager,SheepWarsType typ){
		super(manager);	
		long t = System.currentTimeMillis();
		this.typ=typ;
		manager.setState(GameState.Laden);
		manager.setTyp(GameType.SheepWars);
		setItemDrop(true);
		setItemPickup(true);
		setCreatureSpawn(false);
		getAllowSpawnCreature().add(CreatureType.SHEEP);
		getAllowSpawnCreature().add(CreatureType.VILLAGER);
		setBlockBurn(false);
		setBlockSpread(false);
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
		avt=new AddonVoteTeam(manager,getTyp().getTeam(),InventorySize._9,4);
		
		kitshop=new KitShop(getManager().getInstance(), getCoins(),getTokens(), getManager().getPermManager(), "Kit-Shop", InventorySize._36, new Kit[]{
			new Kit(getManager().getInstance(), "TestKit", UtilItem.RenameItem(new ItemStack(Material.IRON_SWORD), "TestKit"),Permission.SHEEPRUSH_KIT_TESTKIT,KitType.STARTER,10,new Perk[]{
				new PerkSneakDamage(3.0)
			}),
			new Kit(getManager().getInstance(), "TestKit1", UtilItem.RenameItem(new ItemStack(Material.GOLD_SWORD), "TestKit1"),Permission.SHEEPRUSH_KIT_TESTKIT,KitType.KAUFEN,10,new Perk[]{
				new PerkArrowFire(5)
			}),
			new Kit(getManager().getInstance(), "TestKit2", UtilItem.RenameItem(new ItemStack(Material.IRON_SWORD), "TestKit2"),Permission.SHEEPRUSH_KIT_TESTKIT,KitType.KAUFEN_COINS,10,new Perk[]{
				new PerkPoisen(1,1)
			}),
			new Kit(getManager().getInstance(), "TestKit3", UtilItem.RenameItem(new ItemStack(Material.WOOD_SWORD), "TestKit3"),Permission.SHEEPRUSH_KIT_TESTKIT,KitType.KAUFEN_TOKENS,10,new Perk[]{
				new PerkNoKnockback()
			}),
			new Kit(getManager().getInstance(), "TestKit4", UtilItem.RenameItem(new ItemStack(Material.STONE_SWORD), "TestKit4"),Permission.SHEEPRUSH_KIT_TESTKIT,KitType.PREMIUM,10,new Perk[]{
				new PerkHeal(3.0)
			})
			
		});
		
		wd=new WorldData(manager,GameType.SheepWars.name());
		wd.Initialize();
		manager.setWorldData(wd);
		manager.DebugLog(t, this.getClass().getName());
		manager.setState(GameState.LobbyPhase);
	}
	
	//EMERALD_BLOCK&SPONGE = SHEEP PLACE
	//EMERALD_BLOCK&BEDROCK = DROP
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
			l= getManager().getWorldData().getLocs(getTeam(ev.getPlayer()).Name());
			 ev.setRespawnLocation( l.get(UtilMath.r(l.size())) );
		 }
	}
	
	@EventHandler
	public void Explosion(EntityExplodeEvent ev){
		for(int i = 0; i<ev.blockList().size(); i++){
			if(ev.blockList().get(i).getType()==Material.GLOWSTONE){
				ev.blockList().remove(i);
			}
		}
	}
	
	@EventHandler
	public void InGame(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		if(getManager().getState()!=GameState.InGame)return;
		getManager().setStart(getManager().getStart()-1);
		
		for(Player p : UtilServer.getPlayers())UtilDisplay.displayTextBar(p, Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())));
		switch(getManager().getStart()){
			case 15:getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())));break;
			case 10:getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())));break;
			case 5:getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())));break;
			case 4:getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())));break;
			case 3:getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())));break;
			case 2:getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())));break;
			case 1:getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())));break;
			case 0:
				getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.GAME_END.getText());
				getManager().setState(GameState.Restart);
			break;
		}
	}
	
	public HashMap<Team,Integer> verteilung(){
		HashMap<Team,Integer> list = new HashMap<>();
		Player[] l = UtilServer.getPlayers();

        list.put(Team.RED, l.length/2);
        list.put(Team.BLUE, l.length/2);
        list.put(Team.YELLOW, l.length/2);
		
         if (l.length%2!= 0) {
        	 list.put(Team.GREEN, (l.length/2)+1);
         }else{
        	 list.put(Team.GREEN, l.length/2);
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
	
	public void setVillager(Location l,Team t){
		VillagerShop v = new VillagerShop(getManager().getInstance(),"Villager-Shop",l,InventorySize._27);
		v.setDamage(false);
		v.setMove(false);
		
		Merchant bloecke = new Merchant();
		bloecke.addOffer(new MerchantOffer(Bronze(1), new ItemStack(Material.SANDSTONE,2)));
		bloecke.addOffer(new MerchantOffer(Bronze(5), new ItemStack(Material.GLOWSTONE)));
		bloecke.addOffer(new MerchantOffer(Bronze(7),new ItemStack(Material.ENDER_STONE)));
		v.addShop(UtilItem.Item(new ItemStack(Material.SANDSTONE), new String[]{"§aHier findest du alles was du zum bauen brauchst"}, "§cBlöcke"), bloecke, 9);
	
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
		ItemStack s2 = UtilItem.RenameItem(new ItemStack(Material.GOLD_SWORD), "Goldschwerz Lvl 1");
		s2.addEnchantment(Enchantment.DURABILITY, 1);
		s2.addEnchantment(Enchantment.DAMAGE_ALL, 1);
		schwerter.addOffer(new MerchantOffer(Silber(1), s2));
		ItemStack s3 = UtilItem.RenameItem(new ItemStack(Material.GOLD_SWORD), "Goldschwerz Lvl 2");
		s3.addEnchantment(Enchantment.DURABILITY, 1);
		s3.addEnchantment(Enchantment.DAMAGE_ALL, 2);
		schwerter.addOffer(new MerchantOffer(Silber(3), s3));
		ItemStack s4 = UtilItem.RenameItem(new ItemStack(Material.GOLD_SWORD), "Goldschwerz Lvl 3");
		s4.addEnchantment(Enchantment.DURABILITY, 1);
		s4.addEnchantment(Enchantment.DAMAGE_ALL, 3);
		schwerter.addOffer(new MerchantOffer(Silber(7), s4));
		ItemStack s5 = UtilItem.RenameItem(new ItemStack(Material.IRON_SWORD), "Eisenschwert");
		s5.addEnchantment(Enchantment.DURABILITY, 1);
		s5.addEnchantment(Enchantment.DAMAGE_ALL, 2);
		s5.addEnchantment(Enchantment.KNOCKBACK, 1);
		schwerter.addOffer(new MerchantOffer(Gold(3), s5));
		v.addShop(UtilItem.Item(new ItemStack(Material.IRON_SWORD), new String[]{"§aGreife deine Gegner an und töte sie!"}, "§cSchwerter"), schwerter, 12);
		
		Merchant bogen = new Merchant();
		ItemStack b1 = UtilItem.RenameItem(new ItemStack(Material.BOW), "Bogen Lvl 1");
		b1.addEnchantment(Enchantment.ARROW_INFINITE, 1);
		bogen.addOffer(new MerchantOffer(Gold(3),b1));
		ItemStack b2 = UtilItem.RenameItem(new ItemStack(Material.BOW), "Bogen Lvl 2");
		b2.addEnchantment(Enchantment.ARROW_INFINITE, 1);
		b2.addEnchantment(Enchantment.ARROW_DAMAGE, 1);
		bogen.addOffer(new MerchantOffer(Gold(7),b2));
		ItemStack b3 = UtilItem.RenameItem(new ItemStack(Material.BOW), "Bogen Lvl 3");
		b3.addEnchantment(Enchantment.ARROW_INFINITE, 1);
		b3.addEnchantment(Enchantment.ARROW_DAMAGE, 2);
		b3.addEnchantment(Enchantment.ARROW_KNOCKBACK, 1);
		bogen.addOffer(new MerchantOffer(Gold(13),b3));
		bogen.addOffer(new MerchantOffer(Gold(1),UtilItem.RenameItem(new ItemStack(Material.ARROW), "Pfeil")));
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
		spezial.addOffer(new MerchantOffer(Gold(4), UtilItem.RenameItem(new ItemStack(Material.ENDER_PEARL), "Enderpearl")));
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
		getManager().setStart(60*30);
		getManager().setState(GameState.InGame);
		ArrayList<Player> plist = new ArrayList<>();
		for(Player p : UtilServer.getPlayers()){
			getManager().Clear(p);
			getGameList().addPlayer(p,PlayerState.IN);
			plist.add(p);
		}
		PlayerVerteilung(verteilung(), plist);
		
		Team[] teams = getTyp().getTeam();
		ArrayList<Location> list;
		HashMap<Team,Location> sheeps = new HashMap<>();
		HashMap<Team,Location> tt = new HashMap<>();
		for(Team t : teams){
			getTeams().put(t, true);
			list = getManager().getWorldData().getLocs(t.Name());
			for(Player p : getPlayerFrom(t)){
				if(!sheeps.containsKey(t)&&!tt.containsKey(t)){
					Location l = list.get(0);
					List<Block> blist = UtilLocation.getScans(20,l);
					for(Block b : blist){
						if(b.getType()==Material.EMERALD_BLOCK){
							if(b.getRelative(BlockFace.UP).getType()==Material.SPONGE){
								sheeps.put(t, b.getLocation());
								b.setType(Material.AIR);
								b.getRelative(BlockFace.UP).setType(Material.AIR);
							}else if(b.getRelative(BlockFace.UP).getType()==Material.BEDROCK){
								tt.put(t, b.getLocation());
								b.setType(Material.AIR);
								b.getRelative(BlockFace.UP).setType(Material.AIR);
							}else if(b.getRelative(BlockFace.UP).getType()==Material.EMERALD_BLOCK){
								b.setType(Material.AIR);
								b.getRelative(BlockFace.UP).setType(Material.AIR);
								setVillager(b.getLocation().add(0, 0.3, 0), t);
							}
						}
					}
				}
				p.teleport(list.get(0));
				list.remove(0);
			}
		}
		
		adi= new AddonDropItems(getManager().getInstance(),tt);
		aek=new AddonEntityKing(getManager(), teams,this, EntityType.SHEEP,sheeps);
		apbcb= new AddonPlaceBlockCanBreak(getManager().getInstance());
		an= new AddonNight(getManager().getInstance(),getManager().getWorldData().getWorld());
		aek.setDamagePvE(true);
		for(Team t: aek.getTeams().keySet()){
			Sheep s = (Sheep)aek.getTeams().get(t);
			s.setColor(cd(t.getColor()));
			s.setCustomName(t.getColor()+"Schaf ");
		}
		hm.RemoveAllText();
		getManager().DebugLog(time, this.getClass().getName());
	}
	
	@EventHandler
	public void Death(PlayerDeathEvent ev){
		if(ev.getEntity() instanceof Player&&ev.getEntity().getKiller() instanceof Player){
			Player killer = ev.getEntity().getKiller();
			Player victim = ev.getEntity();
			Team t = getTeam(victim);
			getManager().getStats().setInt(killer, getManager().getStats().getInt(Stats.KILLS, killer)+1, Stats.KILLS);
			getManager().getStats().setInt(victim, getManager().getStats().getInt(Stats.DEATHS, victim)+1, Stats.DEATHS);
			getManager().getStats().setInt(victim, getManager().getStats().getInt(Stats.LOSE, victim)+1, Stats.LOSE);
			getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.KILL_BY.getText(new String[]{t.getClass()+victim.getName(),getTeam(killer).getColor()+killer.getName()}));
			
			if(getTeams().get(t)==false){
				getGameList().addPlayer(victim, PlayerState.OUT);
			}
			
			
		}else if(ev.getEntity() instanceof Player){
			Player victim = ev.getEntity();
			Team t = getTeam(victim);
			getManager().getStats().setInt(victim, getManager().getStats().getInt(Stats.DEATHS, victim)+1, Stats.DEATHS);
			getManager().getStats().setInt(victim, getManager().getStats().getInt(Stats.LOSE, victim)+1, Stats.LOSE);
			getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.DEATH.getText(new String[]{t.getColor()+victim.getName()}));

			if(getTeams().get(t)==false){
				getGameList().addPlayer(victim, PlayerState.OUT);
			}
		}
	}
	
	@EventHandler
	public void GameStateChange(GameStateChangeEvent ev){
		if(ev.getTo()==GameState.Restart){
			ArrayList<Player> list = getGameList().getPlayers(PlayerState.IN);
			if(list.size()==1){
				Player p = list.get(0);
				getManager().getStats().setInt(p, getManager().getStats().getInt(Stats.WIN, p)+1, Stats.WIN);
				getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.GAME_WIN.getText(p.getName()));
			}else if(list.size()==2){
				Team t = lastTeam();
				for(Player p : getPlayerFrom(t)){
					if(getGameList().isPlayerState(p)==PlayerState.IN){
						getManager().getStats().setInt(p, getManager().getStats().getInt(Stats.WIN, p)+1, Stats.WIN);
					}
				}
				getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.TEAM_WIN.getText(t.getColor()+t.Name()));
			}
		}
	}
	
	@EventHandler
	public void SheepDeath(AddonEntityKingDeathEvent ev){
		Team t = getTeam(ev.getKiller());
		getTeams().remove(ev.getTeam());
		getTeams().put(ev.getTeam(), false);
		adi.getDrops().remove(ev.getTeam());
		adi.getDrops().put(ev.getTeam(), false);
		int g = adi.getChance().get(t)[0];
		int s = adi.getChance().get(t)[1];
		adi.getChance().remove(t);
		adi.getChance().put(t, new Integer[]{g+1,s+1});
		getManager().getStats().setInt(ev.getKiller(), getManager().getStats().getInt(Stats.SHEEPWARS_KILLED_SHEEPS, ev.getKiller())+1, Stats.SHEEPWARS_KILLED_SHEEPS);
		getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().name())+Text.SHEEPWARS_SHEEP_DEATH.getText(new String[]{ev.getTeam().Name(),ev.getKiller().getName()}));
	}
	
	@EventHandler
	public void Chat(AsyncPlayerChatEvent ev){
		ev.setCancelled(true);
		if(!getManager().isState(GameState.LobbyPhase)&&getTeamList().containsKey(ev.getPlayer())){
			if(ev.getMessage().toCharArray()[0]=='#'){
				Bukkit.broadcastMessage("§7[§c"+getTeam(ev.getPlayer()).Name()+"§7] "+ev.getPlayer().getDisplayName()+": "+ev.getMessage().subSequence(1, ev.getMessage().length()));
			}else{
				for(Player p : getPlayerFrom(getTeam(ev.getPlayer()))){
					p.sendMessage("§cTeam-Chat "+ev.getPlayer().getDisplayName()+": "+ev.getMessage());
				}
			}
		}else if(getManager().getState()!=GameState.LobbyPhase&&getGameList().getPlayers(PlayerState.OUT).contains(ev.getPlayer())){
			ev.setCancelled(true);
			ev.getPlayer().sendMessage(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.SPECTATOR_CHAT_CANCEL.getText());
		}else{
			Bukkit.broadcastMessage(getManager().getPermManager().getPrefix(ev.getPlayer())+ev.getPlayer().getDisplayName()+":§7 "+ev.getMessage());
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void JoinHologram(PlayerJoinEvent ev){
		if(getManager().getState()!=GameState.LobbyPhase)return;
		if(hm==null)hm=new Hologram(getManager().getInstance());

		int win = getManager().getStats().getInt(Stats.WIN, ev.getPlayer());
		int lose = getManager().getStats().getInt(Stats.LOSE, ev.getPlayer());
		getManager().getLoc_stats().getWorld().loadChunk(getManager().getLoc_stats().getWorld().getChunkAt(getManager().getLoc_stats()));
		hm.sendText(ev.getPlayer(),getManager().getLoc_stats().add(0, 0.2, 0),new String[]{
		C.cGreen+getManager().getTyp().string()+C.mOrange+C.Bold+" Info",
		"Server: SheepWars §a"+kArcade.id,
		"Map: "+wd.getMapName(),
		" ",
		C.cGreen+getManager().getTyp().string()+C.mOrange+C.Bold+" Stats",
		"Rang: "+getManager().getStats().getRank(Stats.WIN, ev.getPlayer()),	
		"Kills: "+getManager().getStats().getInt(Stats.KILLS, ev.getPlayer()),
		"Tode: "+getManager().getStats().getInt(Stats.DEATHS, ev.getPlayer()),
		"Schaf-Kills: "+getManager().getStats().getInt(Stats.SHEEPWARS_KILLED_SHEEPS, ev.getPlayer()),
		" ",
		"Gespielte Spiele: "+(win+lose),
		"Gewonnene Spiele: "+win,
		"Verlorene Spiele: "+lose
		});
		ev.getPlayer().getInventory().addItem(UtilItem.RenameItem(new ItemStack(Material.CHEST), "§bKitShop"));
	}
	
}
