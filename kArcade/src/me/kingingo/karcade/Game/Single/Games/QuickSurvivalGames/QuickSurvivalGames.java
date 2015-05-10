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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
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
	setMax_Players(24);
	setCompassAddon(true);
	setExplosion(false);
	setDeathDropItems(true);
	setFoodChange(true);
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
	
	public ItemStack rdmItem(){
		switch(UtilMath.RandomInt(14, 0)){
		case 0: return LOW();
		case 1: return LOW();
		case 2: return LOW();
		case 3: return LOW();
		case 4: return MEDIUM();
		case 5: return MEDIUM();
		case 6: return MEDIUM2();
		case 7: return HARD();
		case 8: return MEDIUM2();
		case 9: return MEDIUM();
		case 10: return MEDIUM();
		case 11: return LOW();
		case 12: return LOW();
		case 13: return LOW();
		case 14: return LOW();
		}
		return new ItemStack(Material.GOLDEN_APPLE,1,(byte)1);
	}
	
	public ItemStack LOW(){
		ItemStack i = null;
		
		switch(UtilMath.RandomInt(15, 0)){
		case 0: i=new ItemStack(Material.FISHING_ROD,1);break;
		case 1: i=new ItemStack(Material.BREAD,UtilMath.RandomInt(2, 1));break;
		case 3: i=new ItemStack(Material.CAKE,UtilMath.RandomInt(2, 1));break;
		case 4: i=new ItemStack(Material.COOKED_BEEF,UtilMath.RandomInt(2, 1));break;
		case 5: i=new ItemStack(Material.COOKED_CHICKEN,UtilMath.RandomInt(2, 1));break;
		case 6: i=new ItemStack(Material.COOKED_FISH,UtilMath.RandomInt(2, 1));break;
		case 7: i=new ItemStack(Material.COOKIE,UtilMath.RandomInt(2, 1));break;
		case 8: i=new ItemStack(Material.APPLE,UtilMath.RandomInt(2, 1));break;
		case 14: i=new ItemStack(Material.GOLD_SWORD);break;
		case 2: i=new ItemStack(Material.BOW);break;
		}
		
		return i;
	}
	
	@EventHandler
	public void InventoryCloseCCC(InventoryCloseEvent ev){
		if(ev.getInventory().getTitle().equalsIgnoreCase("§lChest")){
			if(UtilInv.isInventoryEmpty(ev.getInventory())){
				Location l = chest1.get(ev.getInventory());
				chest.remove(l);
				chest1.remove(ev.getInventory());
				l.getBlock().setType(Material.AIR);
			}
		}
	}
	
	public ItemStack MEDIUM(){
		ItemStack i = null;
		
		switch(UtilMath.RandomInt(8, 1)){
		case 1: i=new ItemStack(Material.FLINT_AND_STEEL,1);break;
		case 2: i=new ItemStack(Material.ARROW,UtilMath.RandomInt(6, 1));break;
		case 3: i=new ItemStack(Material.ARROW,UtilMath.RandomInt(6, 1));break;
		case 4: i=new ItemStack(Material.ARROW,UtilMath.RandomInt(6, 1));break;
		case 5: i=new ItemStack(Material.WOOD_SWORD,1);break;
		case 6: i=new ItemStack(Material.IRON_HELMET,1);break;
		case 7: i=new ItemStack(Material.IRON_BOOTS,1);break;
		case 8: i=new ItemStack(Material.STONE_AXE,1);break;
		}
		
		return i;
	}
	
	public ItemStack MEDIUM2(){
		ItemStack i = null;
		switch(UtilMath.RandomInt(10,1)){
		case 1: i=new ItemStack(Material.CHAINMAIL_HELMET);break;
		case 2: i=new ItemStack(Material.CHAINMAIL_CHESTPLATE);break;
		case 3: i=new ItemStack(Material.CHAINMAIL_LEGGINGS);break;
		case 4: i=new ItemStack(Material.CHAINMAIL_BOOTS);break;
		case 5: i=new ItemStack(Material.STONE_SWORD);break;
		case 7: i=new ItemStack(Material.IRON_AXE);break;
		case 8: i=new ItemStack(Material.IRON_CHESTPLATE);break;
		case 9: i=new ItemStack(Material.IRON_LEGGINGS);break;
		case 10: i=new ItemStack(Material.IRON_SWORD);break;
		}
		
		return i;
	}
	
	public ItemStack HARD(){
		ItemStack i = null;
		
		switch(UtilMath.RandomInt(3,0)){
		case 0: i=new ItemStack(Material.DIAMOND_AXE);break;
		case 1: i=new ItemStack(Material.DIAMOND_SWORD);break;
		case 2: i=new ItemStack(Material.DIAMOND_CHESTPLATE);break;
		case 3: i=new ItemStack(Material.GOLDEN_APPLE);break;
		}
		
		return i;
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
				setStart((3*60)+1);
				for(Player p : UtilServer.getPlayers())p.teleport( getWorldData().getLocs().get(Team.YELLOW.Name()).get(0) );
				setDamage(false);
				setProjectileDamage(false);
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
			case 180:broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.DEATHMATCH_START_IN.getText(getStart()-170));break;
			case 179:broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.DEATHMATCH_START_IN.getText(getStart()-170));break;
			case 178:broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.DEATHMATCH_START_IN.getText(getStart()-170));break;
			case 177:broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.DEATHMATCH_START_IN.getText(getStart()-170));break;
			case 176:broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.DEATHMATCH_START_IN.getText(getStart()-170));break;
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
			
		}else if(ev.getEntity() instanceof Player){
			Player victim = ev.getEntity();
			getStats().setInt(victim, getStats().getInt(Stats.DEATHS, victim)+1, Stats.DEATHS);
			getStats().setInt(victim, getStats().getInt(Stats.LOSE, victim)+1, Stats.LOSE);
			broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.DEATH.getText(new String[]{victim.getName()}));
			getGameList().addPlayer(victim, PlayerState.OUT);

			UtilScoreboard.resetScore(getBoard(), victim.getName()+" §a"+Text.BIG_HERZ.getText(), DisplaySlot.SIDEBAR);
			UtilScoreboard.setScore(getBoard(), victim.getName()+" §4"+Text.MAHLZEICHEN_FETT.getText(), DisplaySlot.SIDEBAR, -1);
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerChatd(AsyncPlayerChatEvent event) {
		if (!event.isCancelled()) {
			Player p = event.getPlayer();
			String msg = event.getMessage();
			msg=msg.replaceAll("%","");
			if(getManager().getPermManager().hasPermission(p, kPermission.ALL_PERMISSION))msg=msg.replaceAll("&", "§");
			event.setFormat(getManager().getPermManager().getPrefix(p) + p.getName() + "§7: "+ msg);
		}
	}
	
	@EventHandler
	public void Start(GameStartEvent ev){
		long time = System.currentTimeMillis();
		ArrayList<Location> list = getWorldData().getLocs(Team.RED.Name());
		int r;
		
		setBoard(getManager().getPermManager().getScoreboard());
		UtilScoreboard.addBoard(getBoard(), DisplaySlot.SIDEBAR, "§6§l"+getType().getKürzel()+" Spieler:");
		for(Player p : UtilServer.getPlayers()){
			r=UtilMath.r(list.size());
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
