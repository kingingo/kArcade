package me.kingingo.karcade.Game.Single.Games.SkyPvP;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import me.kingingo.karcade.kArcade;
import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Enum.PlayerState;
import me.kingingo.karcade.Enum.Team;
import me.kingingo.karcade.Events.RankingEvent;
import me.kingingo.karcade.Game.Events.GameStateChangeEvent;
import me.kingingo.karcade.Game.Single.Events.AddonEntityKingDeathEvent;
import me.kingingo.karcade.Game.Single.Games.SoloGame;
import me.kingingo.karcade.Game.Single.addons.AddonEntityKing;
import me.kingingo.karcade.Game.Single.addons.AddonTargetNextPlayer;
import me.kingingo.karcade.Game.World.WorldData;
import me.kingingo.kcore.Addons.AddonDay;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.Game.Events.GameStartEvent;
import me.kingingo.kcore.Permission.kPermission;
import me.kingingo.kcore.PlayerStats.Stats;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.UtilDisplay;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.UtilLocation;
import me.kingingo.kcore.Util.UtilMap;
import me.kingingo.kcore.Util.UtilMath;
import me.kingingo.kcore.Util.UtilParticle;
import me.kingingo.kcore.Util.UtilPlayer;
import me.kingingo.kcore.Util.UtilScoreboard;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.kcore.Util.UtilTime;

import org.bukkit.Bukkit;
import me.kingingo.kcore.Util.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

public class SkyPvP extends SoloGame{

	HashMap<Player,Location> island = new HashMap<>();
	HashMap<Player,Integer> life = new HashMap<>();
	HashMap<Location,Inventory> enderchests = new HashMap<>();
	ArrayList<ItemStack> enderchest_material = new ArrayList<>();
	AddonEntityKing entity_king;
	AddonTargetNextPlayer TargetNextPlayer;
	
	public SkyPvP(kArcadeManager manager){
		super(manager);
		registerListener();
		long l = System.currentTimeMillis();
		setTyp(GameType.SkyPvP);
		setState(GameState.Laden);
		setMax_Players(12);
		setMin_Players(5);
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
		WorldData wd=new WorldData(manager,getType());
		setWorldData(wd);
		wd.createCleanWorld();
		HashMap<File[],Integer> list = new HashMap<>();
		File[] files = wd.loadSchematicFiles();
		int p=0;
		int c=0;
		int o=0;
		for(File file : files){
			if(file.getName().contains("PLAYER_ISLAND")){
				p++;
			}else if(file.getName().contains("CHEST_ISLAND")){
				c++;
			}else if(file.getName().contains("OBSIDIAN_ISLAND")){
				o++;
			}
		}
		
		File[] p_files = new File[p];
		File[] c_files = new File[c];
		File[] o_files = new File[o];
		p=0;
		c=0;
		o=0;
		for(File file : files){
			if(file.getName().contains("PLAYER_ISLAND")){
				p_files[p]=file;
				p++;
			}else if(file.getName().contains("CHEST_ISLAND")){
				c_files[c]=file;
				c++;
			}else if(file.getName().contains("OBSIDIAN_ISLAND")){
				o_files[o]=file;
				o++;
			}
		}
		
		list.put(c_files, 1);
		list.put(p_files, getMax_Players());
		list.put(o_files, UtilMath.RandomInt(4, 2));
		
		wd.setIsland(list,20, new Location(wd.getWorld(),0,80,0));
	
		Chest chest;
		Block b;
		for(Location loc : wd.getLocs(Team.RED.Name())){
			b=UtilLocation.searchBlock(Material.CHEST, 15, loc);
			if(b!=null&&b.getState() instanceof Chest){
				chest=(Chest)b.getState();
				chest.getInventory().addItem(new ItemStack(Material.LAVA_BUCKET));
				chest.getInventory().addItem(new ItemStack(Material.BOW));
				chest.getInventory().addItem(new ItemStack(Material.ARROW,1));
				chest.getInventory().addItem(new ItemStack(Material.EGG,2));
				chest.getInventory().addItem(new ItemStack(Material.SNOW_BALL,2));
				chest.getInventory().addItem(new ItemStack(Material.COAL,5));
				chest.getInventory().addItem(new ItemStack(Material.CARROT_ITEM,2));
				chest.getInventory().addItem(new ItemStack(Material.BONE,2));
			}
			chest=null;
			b=null;
		}
		
		UtilMap.loadChunks(new Location(wd.getWorld(),0,80,0), 700);
		getManager().DebugLog(l, this.getClass().getName());
	}
	
	public void loadMaterialList(){
		enderchest_material.add(new ItemStack(Material.POTION,2,(byte) 8257));
		enderchest_material.add(new ItemStack(Material.POTION,2,(byte) 8259));
		enderchest_material.add(new ItemStack(Material.POTION,2,(byte) 8226));
		enderchest_material.add(new ItemStack(Material.POTION,2,(byte) 8233));
		

		enderchest_material.add(new ItemStack(Material.EXP_BOTTLE,16));
		enderchest_material.add(new ItemStack(Material.DIAMOND,3));
		enderchest_material.add(new ItemStack(Material.DIAMOND_HELMET,1));
		enderchest_material.add(new ItemStack(Material.GOLDEN_APPLE,2));
		enderchest_material.add(new ItemStack(Material.MONSTER_EGG,2,(byte)50));
	}
	
	@EventHandler
	public void Enderchest(PlayerInteractEvent ev){
		if(UtilEvent.isAction(ev, ActionType.R_BLOCK)&&getGameList().getPlayers(PlayerState.IN).contains(ev.getPlayer())){
			if(ev.getClickedBlock().getType()==Material.ENDER_CHEST){
				if(enderchests.containsKey(ev.getClickedBlock().getLocation())){
					ev.setCancelled(true);
					ev.getPlayer().openInventory(enderchests.get(ev.getClickedBlock().getLocation()));
				}else{
					if(enderchest_material.isEmpty())loadMaterialList();
					Inventory inv = Bukkit.createInventory(null, 9, "EnderChest");
					
					for(int i = 0; i < UtilMath.RandomInt(4, 2); i++){
						inv.addItem( enderchest_material.get(UtilMath.r(enderchest_material.size())).clone() );
					}
					enderchests.put(ev.getClickedBlock().getLocation(), inv);
					
					ev.setCancelled(true);
					ev.getPlayer().openInventory(enderchests.get(ev.getClickedBlock().getLocation()));
				}
			}
		}
	}
	
	@EventHandler
	public void Chat(AsyncPlayerChatEvent ev){
		if(ev.isCancelled())return;
		ev.setCancelled(true);
		if(getState()!=GameState.LobbyPhase&&getGameList().getPlayers(PlayerState.OUT).contains(ev.getPlayer())){
			ev.setCancelled(true);
			UtilPlayer.sendMessage(ev.getPlayer(),Text.PREFIX_GAME.getText(getType().getTyp())+Text.SPECTATOR_CHAT_CANCEL.getText());
		}else{
			UtilServer.broadcast(getManager().getPermManager().getPrefix(ev.getPlayer())+ev.getPlayer().getDisplayName()+":§7 "+ev.getMessage());
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void RespawnLocation(PlayerRespawnEvent ev){
		if(island.containsKey(ev.getPlayer())){
			ev.setRespawnLocation(island.get(ev.getPlayer()));
			if(getGameList().isPlayerState(ev.getPlayer())==PlayerState.IN){
				ev.getPlayer().getInventory().addItem(new ItemStack(Material.COMPASS));
			}
		}
	}
	
	@EventHandler
	public void inGame(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		if(getState()!=GameState.InGame)return;
		setStart(getStart()-1);
		for(Player p : UtilServer.getPlayers())UtilDisplay.displayTextBar(Text.GAME_END_IN.getText(UtilTime.formatSeconds(getStart())), p);
		switch(getStart()){
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
	public void DeathSkyPvP(PlayerDeathEvent ev){
		if(ev.getEntity() instanceof Player){
			Player v = (Player)ev.getEntity();
			boolean b = false;
			int i = life.get(v);
			i--;
			if(i<=0){
				getStats().setInt(v, getStats().getInt(Stats.LOSE, v)+1, Stats.LOSE);
				getGameList().addPlayer(v, PlayerState.OUT);
				b=true;
			}else{
				life.remove(v);
				life.put(v, i);
			}
			
			UtilScoreboard.resetScore(v.getScoreboard(), "§bLeben: ", DisplaySlot.SIDEBAR);
			UtilScoreboard.setScore(v.getScoreboard(), "§bLeben: ", DisplaySlot.SIDEBAR, life.get(v));
			
			if(ev.getEntity().getKiller() instanceof Player){
				Player a = (Player)ev.getEntity().getKiller();
				getStats().setInt(a, getStats().getInt(Stats.KILLS, a)+1, Stats.KILLS);
				getCoins().addCoins(a, false, 5);
				broadcast( Text.PREFIX_GAME.getText(getType().getTyp())+Text.KILL_BY.getText(new String[]{v.getName(),a.getName()}) );
				return;
			}
			broadcast( Text.PREFIX_GAME.getText(getType().getTyp())+Text.DEATH.getText(v.getName()) );
			if(b)broadcast( Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_AUSGESCHIEDEN.getText(v.getName()) );
			getStats().setInt(v, getStats().getInt(Stats.DEATHS, v)+1, Stats.DEATHS);
		}
	}
	
	@EventHandler
	public void GameStateChangeSkyPvP(GameStateChangeEvent ev){
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
		"Server: SkyPvP §a"+kArcade.id,
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
	}
	
	@EventHandler
	public void GameStartSkyPvP(GameStartEvent ev){
		getWorldData().clearWorld();
		ArrayList<Location> locs = getWorldData().getLocs(Team.RED.Name());
		TargetNextPlayer = new AddonTargetNextPlayer(250,this);
		TargetNextPlayer.setAktiv(true);
		
		if(locs.size()<UtilServer.getPlayers().size())System.err.println("[SkyPvP] Es sind zu wenig Location's angegeben!");
		int r;
		Scoreboard board;
		for(Player p : UtilServer.getPlayers()){
			if(locs.isEmpty())break;
			getGameList().addPlayer(p, PlayerState.IN);
			getManager().Clear(p);
			if(getManager().getPermManager().hasPermission(p, kPermission.SkyPvP_Mehr_Leben)){
				life.put(p, 3);
			}else{
				life.put(p, 2);
			}
			r=UtilMath.r(locs.size());
			p.teleport(locs.get(r));
			board=Bukkit.getScoreboardManager().getNewScoreboard();
			UtilScoreboard.addBoard(board, DisplaySlot.SIDEBAR,"§6§lEpicPvP.eu");
			UtilScoreboard.setScore(board, "§bLeben: ", DisplaySlot.SIDEBAR, life.get(p));
			p.setScoreboard(board);
			p.getInventory().addItem(new ItemStack(Material.STONE_PICKAXE));
			p.getInventory().addItem(new ItemStack(Material.STONE_SWORD));
			p.getInventory().addItem(new ItemStack(Material.STONE_AXE));
			p.getInventory().addItem(new ItemStack(Material.STONE_SPADE));
			p.getInventory().addItem(new ItemStack(Material.COMPASS));
			island.put(p, locs.get(r));
			locs.remove(r);
		}
		
		if(!locs.isEmpty()){
			entity_king=new AddonEntityKing(this);
			entity_king.spawnMobs(locs, EntityType.WOLF, "§c§lWolf");
			entity_king.spawnMobs(getWorldData().getLocs(Team.BLUE.Name()), EntityType.IRON_GOLEM, "§6§lIronGolem");
			entity_king.setDamage(true);
			entity_king.setMove(true);
			entity_king.setAttack(true);
			entity_king.setAttack_damage(5.0);
			
			for(Creature c : entity_king.getCreature()){
				if(c instanceof Wolf){
					((Wolf)c).setAngry(true);
				}
			}
		}
		
		new AddonDay(getManager().getInstance(),getWorldData().getWorld());
		setStart((60*30)+1);
		setState(GameState.InGame);
	}
	
	@EventHandler
	public void EntityKingDeath(AddonEntityKingDeathEvent ev){
		if(ev.getEntity().getType()==EntityType.IRON_GOLEM){
			ev.getEntity().getLocation().getBlock().setType(Material.CHEST);
			
			if(enderchest_material.isEmpty())loadMaterialList();
			
			Chest c = (Chest)ev.getEntity().getLocation().getBlock().getState();
			
			for(int i = 0; i < UtilMath.RandomInt(8, 3); i++){
				c.getInventory().addItem( enderchest_material.get(UtilMath.r(enderchest_material.size())).clone() );
			}
			
			ev.getEntity().getLocation().getWorld().strikeLightningEffect(ev.getEntity().getLocation());
			UtilParticle.LARGE_SMOKE.display(1, 100, ev.getEntity().getLocation(), 20);
		}
	}
	
	@EventHandler
	public void Ranking(RankingEvent ev){
		getManager().setRanking(Stats.WIN);
	}

}
