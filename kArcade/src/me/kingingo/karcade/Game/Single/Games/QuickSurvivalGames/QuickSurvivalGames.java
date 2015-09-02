package me.kingingo.karcade.Game.Single.Games.QuickSurvivalGames;

import java.util.ArrayList;
import java.util.HashMap;

import me.kingingo.karcade.kArcade;
import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Enum.PlayerState;
import me.kingingo.karcade.Events.RankingEvent;
import me.kingingo.karcade.Game.Single.Games.SoloGame;
import me.kingingo.karcade.Game.Single.addons.AddonMove;
import me.kingingo.karcade.Game.World.WorldData;
import me.kingingo.kcore.Addons.AddonDay;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.Enum.Team;
import me.kingingo.kcore.Enum.Zeichen;
import me.kingingo.kcore.Game.Events.GameStartEvent;
import me.kingingo.kcore.Game.Events.GameStateChangeEvent;
import me.kingingo.kcore.Language.Language;
import me.kingingo.kcore.Permission.kPermission;
import me.kingingo.kcore.StatsManager.Stats;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.Color;
import me.kingingo.kcore.Util.InventorySize;
import me.kingingo.kcore.Util.Title;
import me.kingingo.kcore.Util.UtilDisplay;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.UtilInv;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilMath;
import me.kingingo.kcore.Util.UtilParticle;
import me.kingingo.kcore.Util.UtilScoreboard;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.kcore.Util.UtilString;
import me.kingingo.kcore.Util.UtilTime;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.entity.EntityExplodeEvent;
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
		setExplosion(true);
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
		getBlockPlaceAllow().add(Material.TNT);
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
	public void explosion(EntityExplodeEvent ev){
		ev.blockList().clear();
	}
	
	@EventHandler
	public void tnt(BlockPlaceEvent ev){
		if(ev.getBlock().getType()==Material.TNT){
			UtilInv.remove(ev.getPlayer(), new ItemStack(Material.TNT), 1);
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
			switch(UtilMath.r(4)){
			case 0: return new ItemStack(UtilMath.RandomInt(301, 298));
			case 1: return new ItemStack(UtilMath.RandomInt(305, 302));
			case 2: return new ItemStack(UtilMath.RandomInt(309, 306));
			case 3: return new ItemStack(UtilMath.RandomInt(317, 314));
			}
		}else{
			return new ItemStack(UtilMath.RandomInt(317, 314));
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
				ev.setCancelled(true);
				if(ev.getPlayer().getItemInHand()!=null&&UtilItem.isWeapon(ev.getPlayer().getItemInHand())){
					ev.getPlayer().sendMessage(Language.getText(ev.getPlayer(), "PREFIX")+Language.getText(ev.getPlayer(), "OPEN_CHEST_WITH_WEAPON"));
					return;
				}
				if(!chest.containsKey(ev.getClickedBlock().getLocation()))CreateChest(ev.getClickedBlock().getLocation());
				ev.getPlayer().openInventory(chest.get(ev.getClickedBlock().getLocation()));
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
		for(Player p : UtilServer.getPlayers())UtilDisplay.displayTextBar(p, Language.getText("GAME_START_IN", getStart()));
		switch(getStart()){
			case 10:
			setDamage(false);
			broadcastWithPrefix("GAME_START_IN", getStart());
			new Title("",Language.getText("GAME_START_IN", getStart())).broadcast();
			break;
			case 9 : for(Entity e : getWorldData().getWorld().getEntities())if(!(e instanceof Player))e.remove(); 
			break;
			case 5:broadcastWithPrefix("GAME_START_IN", getStart());break;
			case 4:broadcastWithPrefix("GAME_START_IN", getStart());break;
			case 3:
				broadcastWithPrefix("GAME_START_IN", getStart());
				new Title("",Language.getText("GAME_START_IN", getStart())).broadcast();
			break;
			case 2:
				broadcastWithPrefix("GAME_START_IN", getStart());
				new Title("",Language.getText("GAME_START_IN", getStart())).broadcast();
			break;
			case 1:
				broadcastWithPrefix("GAME_START_IN", getStart());
				new Title("",Language.getText("GAME_START_IN", getStart())).broadcast();
			break;
			case 0:
				new Title("",Language.getText("GAME_START_IN", getStart())).broadcast();
				setStart(1831);
				setState(GameState.InGame);
				broadcastWithPrefixName("GAME_START");
				setDamage(true);
				move.setnotMove(false);
			break;
		}
	}
	
	@EventHandler
	public void BlockIgnite(BlockIgniteEvent ev){
		if(ev.getCause() == IgniteCause.FLINT_AND_STEEL&&ev.getPlayer().getItemInHand().getType()==Material.FLINT_AND_STEEL){
			if(0 == item.getDurability()){
				ev.getPlayer().getItemInHand().setDurability( (short) (ev.getPlayer().getItemInHand().getType().getMaxDurability()/2) );
			}else{
				UtilInv.remove(ev.getPlayer(), ev.getPlayer().getItemInHand(), 1);
			}
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
		
		for(Player p : UtilServer.getPlayers())UtilDisplay.displayTextBar(p,Language.getText(p, "TELEPORT_TO_DEATHMATCH_ARENA",UtilTime.formatSeconds(getStart())));
		switch(getStart()){
			case 15:broadcastWithPrefix("TELEPORT_TO_DEATHMATCH_ARENA", UtilTime.formatSeconds(getStart()));break;
			case 10:broadcastWithPrefix("TELEPORT_TO_DEATHMATCH_ARENA", UtilTime.formatSeconds(getStart()));break;
			case 5:broadcastWithPrefix("TELEPORT_TO_DEATHMATCH_ARENA", UtilTime.formatSeconds(getStart()));break;
			case 4:broadcastWithPrefix("TELEPORT_TO_DEATHMATCH_ARENA", UtilTime.formatSeconds(getStart()));break;
			case 3:broadcastWithPrefix("TELEPORT_TO_DEATHMATCH_ARENA", UtilTime.formatSeconds(getStart()));break;
			case 2:broadcastWithPrefix("TELEPORT_TO_DEATHMATCH_ARENA", UtilTime.formatSeconds(getStart()));break;
			case 1:broadcastWithPrefix("TELEPORT_TO_DEATHMATCH_ARENA", UtilTime.formatSeconds(getStart()));break;
			case 0:
				setDamage(false);
				move.setnotMove(true);
				Title title = new Title("","");
				broadcastWithPrefixName("TELEPORT_TO_DEATHMATCH_ARENA");
				setStart(6);
				for(Player p : UtilServer.getPlayers()){
					title.setSubtitle(Language.getText(p,"TELEPORT_TO_DEATHMATCH_ARENA", getStart()));
					title.send(p);
					p.teleport( getWorldData().getLocs().get(Team.YELLOW.Name()).get(0) );
				}
				
				for(Entity e : getWorldData().getWorld().getEntities())if(!(e instanceof Player))e.remove();
				setState(GameState.StartDeathMatch);
			break;
		}
	}
	
	@EventHandler
	public void StartDeathMatch(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		if(getState()!=GameState.StartDeathMatch)return;
		setStart(getStart()-1);
		for(Player p : UtilServer.getPlayers())UtilDisplay.displayTextBar(p,Language.getText("DEATHMATCH_START_IN", getStart()));
		
		if(getStart()>1||getStart()<=5){
			Title title = new Title("","§c§l"+getStart());
			title.setStayTime(2);
			for(Player p : UtilServer.getPlayers()){
				title.send(p);
			}
		}
		
		switch(getStart()){
		case 5:broadcastWithPrefix("DEATHMATCH_START_IN", UtilTime.formatSeconds(getStart()));break;
		case 4:broadcastWithPrefix("DEATHMATCH_START_IN", UtilTime.formatSeconds(getStart()));break;
		case 3:broadcastWithPrefix("DEATHMATCH_START_IN", UtilTime.formatSeconds(getStart()));break;
		case 2:broadcastWithPrefix("DEATHMATCH_START_IN", UtilTime.formatSeconds(getStart()));break;
		case 1:broadcastWithPrefix("DEATHMATCH_START_IN", UtilTime.formatSeconds(getStart()));break;
		case 0:
			Title title = new Title("","");
			title.setStayTime(2);
			for(Player p : UtilServer.getPlayers()){
				title.setSubtitle(Language.getText(p,"GO", getStart()));
				title.send(p);
			}
			broadcastWithPrefixName("DEATHMATCH_START");
			setStart(176);
			setDamage(true);
			move.setnotMove(false);
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
		for(Player p : UtilServer.getPlayers())UtilDisplay.displayTextBar(p,Language.getText("DEATHMATCH_END_IN", getStart()));
		switch(getStart()){
			case 5:broadcastWithPrefix("DEATHMATCH_END_IN", getStart());break;
			case 4:broadcastWithPrefix("DEATHMATCH_END_IN", getStart());break;
			case 3:broadcastWithPrefix("DEATHMATCH_END_IN", getStart());break;
			case 2:broadcastWithPrefix("DEATHMATCH_END_IN", getStart());break;
			case 1:broadcastWithPrefix("DEATHMATCH_END_IN", getStart());break;
			case 0:
				broadcastWithPrefixName("DEATHMATCH_END");
				Title title = new Title("","");
				for(Player p : UtilServer.getPlayers()){
					title.setSubtitle(Language.getText("DEATHMATCH_END"));
					title.send(p);
				}
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
			broadcastWithPrefix("KILL_BY", new String[]{victim.getName(),killer.getName()});
			getGameList().addPlayer(victim, PlayerState.OUT);
			
			int k = kills.get(killer);
			k++;
			kills.remove(killer);
			kills.put(killer, k);

			UtilScoreboard.resetScore(getBoard(), killer.getName()+" §a"+Zeichen.BIG_HERZ.getIcon(), DisplaySlot.SIDEBAR);
			UtilScoreboard.resetScore(getBoard(), victim.getName()+" §a"+Zeichen.BIG_HERZ.getIcon(), DisplaySlot.SIDEBAR);
			UtilScoreboard.setScore(getBoard(), killer.getName()+" §a"+Zeichen.BIG_HERZ.getIcon(), DisplaySlot.SIDEBAR, k);
			UtilScoreboard.setScore(getBoard(), victim.getName()+" §4"+Zeichen.MAHLZEICHEN_FETT.getIcon(), DisplaySlot.SIDEBAR, -1);

			UtilParticle.FLAME.display(1, 60, victim.getLocation(), 15);
		}else if(ev.getEntity() instanceof Player){
			Player victim = ev.getEntity();
			getStats().setInt(victim, getStats().getInt(Stats.DEATHS, victim)+1, Stats.DEATHS);
			getStats().setInt(victim, getStats().getInt(Stats.LOSE, victim)+1, Stats.LOSE);
			broadcastWithPrefix("DEATH", new String[]{victim.getName()});
			getGameList().addPlayer(victim, PlayerState.OUT);

			UtilScoreboard.resetScore(getBoard(), victim.getName()+" §a"+Zeichen.BIG_HERZ.getIcon(), DisplaySlot.SIDEBAR);
			UtilScoreboard.setScore(getBoard(), victim.getName()+" §4"+Zeichen.MAHLZEICHEN_FETT.getIcon(), DisplaySlot.SIDEBAR, -1);
			UtilParticle.FLAME.display(1, 60,  victim.getLocation(), 15);
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerChatd(AsyncPlayerChatEvent event) {
		if (!event.isCancelled()) {
			
			if((!event.getPlayer().hasPermission(kPermission.CHAT_LINK.getPermissionToString()))&&UtilString.isBadWord(event.getMessage())||UtilString.checkForIP(event.getMessage())){
				event.setMessage("Ich heul rum!");
				event.getPlayer().sendMessage(Language.getText(event.getPlayer(), "PREFIX")+Language.getText(event.getPlayer(), "CHAT_MESSAGE_BLOCK"));
			}
			
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
		UtilScoreboard.addBoard(getBoard(), DisplaySlot.SIDEBAR, "§6§l"+getType().getKürzel()+" Players:");
		Title title = new Title("", "");
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
			UtilScoreboard.setScore(getBoard(), p.getName()+" §a"+Zeichen.BIG_HERZ.getIcon(), DisplaySlot.SIDEBAR, 0);
			p.setScoreboard(getBoard());
			kills.put(p, 0);
			title.setSubtitle(Language.getText(p, "NO_TEAMS_ALLOWED"));
			title.send(p);
		}
		
		getManager().DebugLog(time, this.getClass().getName());
		move=new AddonMove(getManager());
		new AddonDay(getManager().getInstance(),getWorldData().getWorld());
		move.setnotMove(true, getGameList().getPlayers(PlayerState.IN));
		getManager().getHologram().RemoveAllText();
		setState(GameState.StartGame);
		
		for(Entity e : getWorldData().getWorld().getEntities())if(!(e instanceof Player))e.remove();
		getManager().DebugLog(time, this.getClass().getName());
	}
	
	@EventHandler
	public void QuitSc(PlayerQuitEvent ev){
		if(getBoard()==null)return;
		UtilScoreboard.resetScore(getBoard(), ev.getPlayer().getName()+" §a"+Zeichen.BIG_HERZ.getIcon(), DisplaySlot.SIDEBAR);
		UtilScoreboard.setScore(getBoard(), ev.getPlayer().getName()+" §4"+Zeichen.MAHLZEICHEN_FETT.getIcon(), DisplaySlot.SIDEBAR, -1);
	}
	
	@EventHandler
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
				broadcastWithPrefix("GAME_WIN", p.getName());
			}
		}
	}
	
}
