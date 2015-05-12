package me.kingingo.karcade.Game.Single.Games.QuickSurvivalGames;

import java.util.ArrayList;
import java.util.HashMap;

import me.kingingo.karcade.kArcade;
import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Enum.PlayerState;
import me.kingingo.karcade.Enum.Team;
import me.kingingo.karcade.Events.RankingEvent;
import me.kingingo.karcade.Game.Events.GameStateChangeEvent;
import me.kingingo.karcade.Game.Single.Games.SoloGame;
import me.kingingo.karcade.Game.Single.addons.AddonBagPack;
import me.kingingo.karcade.Game.Single.addons.AddonMove;
import me.kingingo.karcade.Game.Single.addons.AddonTargetNextPlayer;
import me.kingingo.karcade.Game.World.WorldData;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.Game.Events.GameStartEvent;
import me.kingingo.kcore.Permission.kPermission;
import me.kingingo.kcore.PlayerStats.Stats;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.C;
import me.kingingo.kcore.Util.Title;
import me.kingingo.kcore.Util.UtilDisplay;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilInv;
import me.kingingo.kcore.Util.UtilParticle;
import me.kingingo.kcore.Util.UtilScoreboard;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.UtilMath;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.kcore.Util.UtilTime;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;

public class QuickSurvivalGames extends SoloGame{

	WorldData wd;
	AddonMove move;
	private HashMap<Location,Inventory> chest = new HashMap<Location,Inventory>();
	private HashMap<Inventory,Location> chest1 = new HashMap<>();
	boolean jump=true;
	private HashMap<Player,Integer> kills = new HashMap<>();
	
	public QuickSurvivalGames(kArcadeManager manager) {
		super(manager);
		registerListener();
	long t = System.currentTimeMillis();
	setTyp(GameType.QuickSurvivalGames); 
	wd=new WorldData(manager,getType());
	wd.Initialize();
	setWorldData(wd);
	setMin_Players(6);
	setMax_Players(16);
	setCompassAddon(true);
	setExplosion(false);
	setDeathDropItems(true);
	setFoodChange(false);
	setDamageSelf(false);
	setDamage(true);
	setDamagePvE(true);
	setItemDrop(true);
	setItemPickup(true);
	setCreatureSpawn(false);
	setBlockBreak(false);
	setBlockPlace(false);
	getBlockBreakAllow().add(Material.LEAVES);
	getBlockPlaceAllow().add(Material.CAKE);
	getBlockPlaceAllow().add(Material.CAKE_BLOCK);
	getBlockPlaceAllow().add(Material.FIRE);
	getBlockPlaceAllow().add(Material.WEB);
	getItemPickupDeny().add(95);
	manager.DebugLog(t, this.getClass().getName());
	}
	//RED WOOL = SPAWN PLAYER!
	
	Inventory inv;
	int r;
	int rdmplace;
	int rdm;
	ItemStack item;
	int a;
	public void CreateChest(Location loc){
		inv = Bukkit.createInventory(null, 27, "§lChest");
		r = UtilMath.RandomInt(4, 2);
		for (int nur = 0; nur < r; nur++) {
	          rdmplace = (int)(Math.random() * 27.0D);
	          item = rdmItem();
	          a = 1;
	          
	          inv.setItem(rdmplace, item); 
	        }
		chest.put(loc, inv);
		chest1.put(inv, loc);
	}
	
	@EventHandler
	public void Ranking(RankingEvent ev){
		getManager().setRanking(Stats.WIN);
	}
	
	@EventHandler
	public void tnt(BlockPlaceEvent ev){
		if(ev.getBlock().getType()==Material.TNT){
			ev.getBlock().setType(Material.AIR);
			ev.getBlock().getLocation().getWorld().spawnEntity(ev.getBlock().getLocation(),EntityType.PRIMED_TNT);
		}
	}
	
	public ItemStack Sonstiges(){
		switch(UtilMath.RandomInt(7, 0)){
		case 0: return new ItemStack(Material.FLINT_AND_STEEL);
		case 1: return new ItemStack(Material.BOW);
		case 2: return new ItemStack(Material.GOLDEN_APPLE);
		case 3: return new ItemStack(Material.ARROW,UtilMath.RandomInt(8, 3));
		case 4: return new ItemStack(Material.FISHING_ROD);
		case 5: return new ItemStack(Material.TNT);
		case 6: return new ItemStack(Material.POTION,1,(byte)16421);
		case 7: return new ItemStack(Material.POTION,1,(byte)16389);
		}
		return null;
	}
	
	public ItemStack Axt(){
		if(UtilMath.RandomInt(100, 1) != 35){
			switch(UtilMath.RandomInt(3, 0)){
			case 0: return new ItemStack(271);
			case 1: return new ItemStack(275);
			case 2: return new ItemStack(286);
			case 3: return new ItemStack(258);
			}
		}else{
			return new ItemStack(279);
		}
		return null;
	}
	
	public ItemStack Schwert(){
		if(UtilMath.RandomInt(100, 1) != 35){
			switch(UtilMath.RandomInt(3, 0)){
			case 0: return new ItemStack(267);
			case 1: return new ItemStack(268);
			case 2: return new ItemStack(272);
			case 3: return new ItemStack(283);
			}
		}else{
			return new ItemStack(276);
		}
		return null;
	}
	
	public ItemStack Rüstung(){
		if(UtilMath.RandomInt(100, 1) != 35){
			switch(UtilMath.RandomInt(3, 0)){
			case 0: return new ItemStack(UtilMath.RandomInt(301, 298));
			case 1: return new ItemStack(UtilMath.RandomInt(305, 302));
			case 2: return new ItemStack(UtilMath.RandomInt(309, 306));
			case 3: return new ItemStack(UtilMath.RandomInt(317, 314));
			}
		}else{
			return new ItemStack(UtilMath.RandomInt(313, 310));
		}
		return null;
	}
	
	public ItemStack rdmItem(){
		switch(UtilMath.RandomInt(4, 0)){
		case 0: return Rüstung();
		case 1: return Axt();
		case 2: return Schwert();
		case 3: return Sonstiges();
		case 4: return Sonstiges();
		}
		return null;
	}
	
//	public ItemStack rdmItem(){
//		switch(UtilMath.RandomInt(81, 0)){
//		case 0: return new ItemStack(Material.FLINT_AND_STEEL,1);
//		case 1: return new ItemStack(Material.FLINT_AND_STEEL,1);
//		case 2: return new ItemStack(Material.FLINT_AND_STEEL,1);
//		case 3: return new ItemStack(Material.FLINT_AND_STEEL,1);
//		case 4: return new ItemStack(Material.ARROW,UtilMath.RandomInt(6, 1));
//		case 5: return new ItemStack(Material.WOOD_SWORD,1);
//		case 6: return new ItemStack(Material.WOOD_SWORD,1);
//		case 7: return new ItemStack(Material.IRON_HELMET,1);
//		case 8: return new ItemStack(Material.IRON_CHESTPLATE,1);
//		case 9: return new ItemStack(Material.IRON_LEGGINGS,1);
//		case 10: return new ItemStack(Material.IRON_BOOTS,1);
//		case 11: return new ItemStack(Material.IRON_HELMET,1);
//		case 12: return new ItemStack(Material.IRON_CHESTPLATE,1);
//		case 13: return new ItemStack(Material.IRON_LEGGINGS,1);
//		case 14: return new ItemStack(Material.IRON_BOOTS,1);
//		case 15: return new ItemStack(Material.STONE_AXE,1);
//		case 16: return new ItemStack(Material.STONE_AXE,1);
//		case 17: return new ItemStack(Material.STONE_AXE,1);
//		case 18: return new ItemStack(Material.BOW);
//		case 19: return new ItemStack(Material.BOW);
//		case 20: return new ItemStack(Material.BOW);
//		case 21: return new ItemStack(Material.BOW);
//		case 22: return new ItemStack(Material.CHAINMAIL_HELMET);
//		case 23: return new ItemStack(Material.CHAINMAIL_CHESTPLATE);
//		case 24: return new ItemStack(Material.CHAINMAIL_LEGGINGS);
//		case 25: return new ItemStack(Material.CHAINMAIL_BOOTS);
//		case 26: return new ItemStack(Material.CHAINMAIL_HELMET);
//		case 27: return new ItemStack(Material.CHAINMAIL_CHESTPLATE);
//		case 28: return new ItemStack(Material.CHAINMAIL_LEGGINGS);
//		case 29: return new ItemStack(Material.CHAINMAIL_BOOTS);
//		case 30: return new ItemStack(Material.CHAINMAIL_HELMET);
//		case 31: return new ItemStack(Material.CHAINMAIL_CHESTPLATE);
//		case 32: return new ItemStack(Material.CHAINMAIL_LEGGINGS);
//		case 33: return new ItemStack(Material.CHAINMAIL_BOOTS);
//		case 34: return new ItemStack(Material.STONE_SWORD);
//		case 35: return new ItemStack(Material.IRON_SWORD);
//		case 36: return new ItemStack(Material.STONE_SWORD);
//		case 37: return new ItemStack(Material.IRON_SWORD);
//		case 38: return new ItemStack(Material.STONE_SWORD);
//		case 39: return new ItemStack(Material.IRON_SWORD);
//		case 40: return new ItemStack(Material.FLINT_AND_STEEL,1);
//		case 41: return new ItemStack(Material.FLINT_AND_STEEL,1);
//		case 42: return new ItemStack(Material.ARROW,UtilMath.RandomInt(6, 1));
//		case 43: return new ItemStack(Material.WOOD_SWORD,1);
//		case 44: return new ItemStack(Material.WOOD_SWORD,1);
//		case 45: return new ItemStack(Material.IRON_HELMET,1);
//		case 46: return new ItemStack(Material.IRON_CHESTPLATE,1);
//		case 47: return new ItemStack(Material.IRON_LEGGINGS,1);
//		case 48: return new ItemStack(Material.IRON_BOOTS,1);
//		case 49: return new ItemStack(Material.IRON_HELMET,1);
//		case 50: return new ItemStack(Material.IRON_CHESTPLATE,1);
//		case 51: return new ItemStack(Material.IRON_LEGGINGS,1);
//		case 52: return new ItemStack(Material.IRON_BOOTS,1);
//		case 53: return new ItemStack(Material.STONE_AXE,1);
//		case 54: return new ItemStack(Material.STONE_AXE,1);
//		case 55: return new ItemStack(Material.STONE_AXE,1);
//		case 56: return new ItemStack(Material.BOW);
//		case 57: return new ItemStack(Material.BOW);
//		case 58: return new ItemStack(Material.BOW);
//		case 59: return new ItemStack(Material.BOW);
//		case 60: return new ItemStack(Material.CHAINMAIL_HELMET);
//		case 61: return new ItemStack(Material.CHAINMAIL_CHESTPLATE);
//		case 62: return new ItemStack(Material.CHAINMAIL_LEGGINGS);
//		case 63: return new ItemStack(Material.CHAINMAIL_BOOTS);
//		case 64: return new ItemStack(Material.CHAINMAIL_HELMET);
//		case 65: return new ItemStack(Material.CHAINMAIL_CHESTPLATE);
//		case 66: return new ItemStack(Material.CHAINMAIL_LEGGINGS);
//		case 67: return new ItemStack(Material.CHAINMAIL_BOOTS);
//		case 68: return new ItemStack(Material.CHAINMAIL_HELMET);
//		case 69: return new ItemStack(Material.CHAINMAIL_CHESTPLATE);
//		case 70: return new ItemStack(Material.CHAINMAIL_LEGGINGS);
//		case 71: return new ItemStack(Material.CHAINMAIL_BOOTS);
//		case 72: return new ItemStack(Material.STONE_SWORD);
//		case 73: return new ItemStack(Material.IRON_SWORD);
//		case 74: return new ItemStack(Material.STONE_SWORD);
//		case 75: return new ItemStack(Material.IRON_SWORD);
//		case 76: return new ItemStack(Material.STONE_SWORD);
//		case 77: return new ItemStack(Material.IRON_SWORD);
//		case 78: return new ItemStack(Material.DIAMOND_AXE);
//		case 79: return new ItemStack(Material.DIAMOND_SWORD);
//		case 80: return new ItemStack(Material.DIAMOND_CHESTPLATE);
//		case 81: return new ItemStack(Material.GOLDEN_APPLE);
//		}
//		return new ItemStack(Material.GOLDEN_APPLE,1,(byte)1);
//	}
	
	@EventHandler
	public void InventoryCloseCCC(InventoryCloseEvent ev){
		if(ev.getInventory().getTitle().equalsIgnoreCase("§lChest")){
			if(UtilInv.isInventoryEmpty(ev.getInventory())){
				Location l = chest1.get(ev.getInventory());
				if(ev.getInventory().getViewers().isEmpty()){
					chest.remove(l);
					chest1.remove(ev.getInventory());
					l.getBlock().setType(Material.AIR);
				}
			}
		}
	}
	
	@EventHandler
	public void Interact(PlayerInteractEvent ev){
		if(getState()==GameState.InGame){
			if(getGameList().getPlayers(PlayerState.OUT).contains(ev.getPlayer()))return;
			if(UtilEvent.isAction(ev, ActionType.BLOCK) && ev.getClickedBlock().getType()==Material.CHEST){
				if(!chest.containsKey(ev.getClickedBlock().getLocation()))CreateChest(ev.getClickedBlock().getLocation());
				ev.getPlayer().openInventory(chest.get(ev.getClickedBlock().getLocation()));
				ev.setCancelled(true);
			}
		}else{
			if(UtilEvent.isAction(ev, ActionType.BLOCK) && ev.getClickedBlock().getType()==Material.CHEST){
				ev.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void Start_Game(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		if(getState()!=GameState.StartGame)return;
		if(getStart()<0)setStart(11);
		setStart(getStart()-1);
		for(Player p : UtilServer.getPlayers())UtilDisplay.displayTextBar(p, Text.GAME_START_IN.getText(getStart()));
		switch(getStart()){
			case 10:
			setDamage(false);
			broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_START_IN.getText(getStart()));
			new Title("",Text.GAME_START_IN.getText(getStart())).broadcast();
			break;
			case 5:broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_START_IN.getText(getStart()));break;
			case 4:broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_START_IN.getText(getStart()));break;
			case 3:
				broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_START_IN.getText(getStart()));
				new Title("",Text.GAME_START_IN.getText(getStart())).broadcast();
			break;
			case 2:
				broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_START_IN.getText(getStart()));
			new Title("",Text.GAME_START_IN.getText(getStart())).broadcast();
			break;
			case 1:
				broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_START_IN.getText(getStart()));
			new Title("",Text.GAME_START_IN.getText(getStart())).broadcast();
			break;
			case 0:
				new Title("",Text.GAME_START.getText(getStart())).broadcast();
				setStart(1831);
				setState(GameState.InGame);
				broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_START.getText());
				setDamage(true);
				move.setnotMove(false);
			break;
		}
	}
	
	@EventHandler
	public void DeathMatch(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		if(getState()!=GameState.InGame)return;
		setStart(getStart()-1);
		
		if(getGameList().getPlayers(PlayerState.IN).size()<=2&&getState()==GameState.InGame){
			if(getStart()>15){
				setStart(15);
			}
		}
		
		for(Player p : UtilServer.getPlayers())UtilDisplay.displayTextBar(p, Text.GAME_END_IN.getText(UtilTime.formatSeconds(getStart())));
		switch(getStart()){
			case 15:broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getStart())));break;
			case 10:broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getStart())));break;
			case 5:broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getStart())));break;
			case 4:broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getStart())));break;
			case 3:broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getStart())));break;
			case 2:broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getStart())));break;
			case 1:broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getStart())));break;
			case 0:
				new Title("",Text.GAME_END.getText(getStart())).broadcast();
				broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_END.getText());
				setStart(176);
				for(Player p : UtilServer.getPlayers())p.teleport( getWorldData().getLocs().get(Team.YELLOW.Name()).get(0) );
				setDamage(false);
				setProjectileDamage(false);
				for(Entity e : getWorldData().getWorld().getEntities())if(!(e instanceof Player))e.remove();
				setState(GameState.DeathMatch);
			break;
		}
	}
	
	@EventHandler
	public void Block(PlayerInteractEvent ev){
		if(UtilEvent.isAction(ev, ActionType.R) && ev.getPlayer().getItemInHand()!=null){
			if(ev.getPlayer().getItemInHand().getType()==Material.WORKBENCH){
				ev.getPlayer().getItemInHand().setTypeId(0);
				ev.getPlayer().openWorkbench(ev.getPlayer().getLocation(), true);
			}else if(ev.getPlayer().getItemInHand().getType()==Material.ENCHANTMENT_TABLE){
				ev.getPlayer().getItemInHand().setTypeId(0);
				ev.getPlayer().openEnchanting(ev.getPlayer().getLocation(), true);
			}
		}
	}
	
	@EventHandler
	public void inGame(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		if(getState()!=GameState.DeathMatch)return;
		setStart(getStart()-1);
		for(Player p : UtilServer.getPlayers())UtilDisplay.displayTextBar(p, Text.DEATHMATCH_END_IN.getText(getStart()));
		switch(getStart()){
			case 175:broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.DEATHMATCH_START_IN.getText(getStart()-170));break;
			case 174:broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.DEATHMATCH_START_IN.getText(getStart()-170));break;
			case 173:broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.DEATHMATCH_START_IN.getText(getStart()-170));
			new Title("",Text.DEATHMATCH_START_IN.getText(getStart()-170)).broadcast();break;
			case 172:broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.DEATHMATCH_START_IN.getText(getStart()-170));
			new Title("",Text.DEATHMATCH_START_IN.getText(getStart()-170)).broadcast();break;
			case 171:broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.DEATHMATCH_START_IN.getText(getStart()-170));
			new Title("",Text.DEATHMATCH_START_IN.getText(getStart()-170)).broadcast();break;
			case 170:
				setDamage(true);
				setProjectileDamage(true);
				new Title("",Text.DEATHMATCH_START.getText(getStart())).broadcast();
				broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.DEATHMATCH_START.getText(getStart()));
				break;
			case 5:broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.DEATHMATCH_END_IN.getText(getStart()));break;
			case 4:broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.DEATHMATCH_END_IN.getText(getStart()));break;
			case 3:broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.DEATHMATCH_END_IN.getText(getStart()));break;
			case 2:broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.DEATHMATCH_END_IN.getText(getStart()));break;
			case 1:broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.DEATHMATCH_END_IN.getText(getStart()));break;
			case 0:
				broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.DEATHMATCH_END.getText());
				new Title("",Text.DEATHMATCH_END.getText(getStart())).broadcast();
				setState(GameState.Restart);
			break;
		}
	}

	@EventHandler
	public void Death(PlayerDeathEvent ev){
		if(ev.getEntity() instanceof Player && ev.getEntity().getKiller() instanceof Player){
			Player killer = ev.getEntity().getKiller();
			Player victim = ev.getEntity();
			getStats().setInt(killer, getStats().getInt(Stats.KILLS, killer)+1, Stats.KILLS);
			getStats().setInt(victim, getStats().getInt(Stats.DEATHS, victim)+1, Stats.DEATHS);
			getStats().setInt(victim, getStats().getInt(Stats.LOSE, victim)+1, Stats.LOSE);
			broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.KILL_BY.getText(new String[]{victim.getName(),killer.getName()}));
			getGameList().addPlayer(victim, PlayerState.OUT);
			
			int k = kills.get(killer);
			k++;
			kills.remove(killer);
			kills.put(killer, k);

			UtilScoreboard.resetScore(getBoard(), killer.getName()+" §a"+Text.BIG_HERZ.getText(), DisplaySlot.SIDEBAR);
			UtilScoreboard.resetScore(getBoard(), victim.getName()+" §a"+Text.BIG_HERZ.getText(), DisplaySlot.SIDEBAR);
			UtilScoreboard.setScore(getBoard(), killer.getName()+" §a"+Text.BIG_HERZ.getText(), DisplaySlot.SIDEBAR, k);
			UtilScoreboard.setScore(getBoard(), victim.getName()+" §4"+Text.MAHLZEICHEN_FETT.getText(), DisplaySlot.SIDEBAR, -1);

			UtilParticle.FLAME.display(1, 60, victim.getLocation(), 15);
		}else if(ev.getEntity() instanceof Player){
			Player victim = ev.getEntity();
			getStats().setInt(victim, getStats().getInt(Stats.DEATHS, victim)+1, Stats.DEATHS);
			getStats().setInt(victim, getStats().getInt(Stats.LOSE, victim)+1, Stats.LOSE);
			broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.DEATH.getText(new String[]{victim.getName()}));
			getGameList().addPlayer(victim, PlayerState.OUT);

			UtilScoreboard.resetScore(getBoard(), victim.getName()+" §a"+Text.BIG_HERZ.getText(), DisplaySlot.SIDEBAR);
			UtilScoreboard.setScore(getBoard(), victim.getName()+" §4"+Text.MAHLZEICHEN_FETT.getText(), DisplaySlot.SIDEBAR, -1);
			UtilParticle.FLAME.display(1, 60,  victim.getLocation(), 15);
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerChatd(AsyncPlayerChatEvent event) {
		if (!event.isCancelled()) {
			Player p = event.getPlayer();
			String msg = event.getMessage();
			msg=msg.replaceAll("%","");
			if(getManager().getPermManager().hasPermission(p, kPermission.ALL_PERMISSION))msg=msg.replaceAll("&", "§");
			event.setFormat(getManager().getPermManager().getPrefix(p) + p.getName() + "§7:§f "+ msg);
		}
	}
	
	@EventHandler
	public void Start(GameStartEvent ev){
		long time = System.currentTimeMillis();
		ArrayList<Location> list = (ArrayList<Location>)getWorldData().getLocs(Team.RED.Name()).clone();
		int r;

		setBoard(getManager().getPermManager().getScoreboard());
		UtilScoreboard.addBoard(getBoard(), DisplaySlot.SIDEBAR, "§6§l"+getType().getKürzel()+" Spieler:");
		for(Player p : UtilServer.getPlayers()){
			if(list.isEmpty()){
				r=0;
				list.add(getWorldData().getLocs(Team.RED.Name()).get(0));
			}else{
				r=UtilMath.r(list.size());
			}
			getManager().Clear(p);
			getGameList().addPlayer(p,PlayerState.IN);
			p.teleport(list.get(r));
			list.remove(r);
			UtilScoreboard.setScore(getBoard(), p.getName()+" §a"+Text.BIG_HERZ.getText(), DisplaySlot.SIDEBAR, 0);
			p.setScoreboard(getBoard());
			kills.put(p, 0);
		}
		
		getManager().DebugLog(time, this.getClass().getName());
		move=new AddonMove(getManager());
		move.setnotMove(true, getGameList().getPlayers(PlayerState.IN));
		getManager().getHologram().RemoveAllText();
		setState(GameState.StartGame);
		
		for(Entity e : getWorldData().getWorld().getEntities())if(!(e instanceof Player))e.remove();
		getManager().DebugLog(time, this.getClass().getName());
	}
	
	@EventHandler
	public void QuitSc(PlayerQuitEvent ev){
		if(getBoard()==null)return;
		UtilScoreboard.resetScore(getBoard(), ev.getPlayer().getName()+" §a"+Text.BIG_HERZ.getText(), DisplaySlot.SIDEBAR);
		UtilScoreboard.setScore(getBoard(), ev.getPlayer().getName()+" §4"+Text.MAHLZEICHEN_FETT.getText(), DisplaySlot.SIDEBAR, -1);
	}
	
	@EventHandler
	public void JoinHologram(PlayerJoinEvent ev){
		if(getState()!=GameState.LobbyPhase)return;
		int win = getStats().getInt(Stats.WIN, ev.getPlayer());
		int lose = getStats().getInt(Stats.LOSE, ev.getPlayer());
		getManager().getHologram().sendText(ev.getPlayer(),getManager().getLoc_stats(),new String[]{
		C.cGreen+getType().getTyp()+C.mOrange+C.Bold+" Info",
		"Server: QuickSurvivalGames §a"+kArcade.id,
		"Map: "+wd.getMapName(),
		" ",
		C.cGreen+getType().getTyp()+C.mOrange+C.Bold+" Stats",
		"Kills: "+getStats().getInt(Stats.KILLS, ev.getPlayer()),
		"Tode: "+getStats().getInt(Stats.DEATHS, ev.getPlayer()),
		" ",
		"Gespielte Spiele: "+(win+lose),
		"Gewonnene Spiele: "+win,
		"Verlorene Spiele: "+lose
		});
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent ev){
		if(ev.getBlock().getType() == Material.ITEM_FRAME && !ev.getPlayer().isOp()){
			ev.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
	public void ChangeState(GameStateChangeEvent ev){
		if(ev.getTo()==GameState.Restart){
			ArrayList<Player> list = getGameList().getPlayers(PlayerState.IN);
			if(list.size()==1){
				Player p = list.get(0);
				getStats().setInt(p, getStats().getInt(Stats.WIN, p)+1, Stats.WIN);
				broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_WIN.getText(p.getName()));
			}
		}
	}
	
}
