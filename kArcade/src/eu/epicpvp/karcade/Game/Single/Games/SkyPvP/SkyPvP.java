package eu.epicpvp.karcade.Game.Single.Games.SkyPvP;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
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
import eu.epicpvp.karcade.Game.Single.SingleWorldData;
import eu.epicpvp.karcade.Game.Single.Addons.AddonEntityKing;
import eu.epicpvp.karcade.Game.Single.Addons.AddonTargetNextPlayer;
import eu.epicpvp.karcade.Game.Single.Events.AddonEntityKingDeathEvent;
import eu.epicpvp.karcade.Game.Single.Games.SoloGame;
import eu.epicpvp.kcore.Addons.AddonDay;
import eu.epicpvp.kcore.Enum.PlayerState;
import eu.epicpvp.kcore.Enum.Team;
import eu.epicpvp.kcore.Permission.PermissionType;
import eu.epicpvp.kcore.StatsManager.Event.PlayerStatsLoadedEvent;
import eu.epicpvp.kcore.Translation.TranslationHandler;
import eu.epicpvp.kcore.Update.UpdateType;
import eu.epicpvp.kcore.Update.Event.UpdateEvent;
import eu.epicpvp.kcore.Util.Color;
import eu.epicpvp.kcore.Util.UtilDisplay;
import eu.epicpvp.kcore.Util.UtilEvent;
import eu.epicpvp.kcore.Util.UtilEvent.ActionType;
import eu.epicpvp.kcore.Util.UtilLocation;
import eu.epicpvp.kcore.Util.UtilMap;
import eu.epicpvp.kcore.Util.UtilMath;
import eu.epicpvp.kcore.Util.UtilParticle;
import eu.epicpvp.kcore.Util.UtilPlayer;
import eu.epicpvp.kcore.Util.UtilScoreboard;
import eu.epicpvp.kcore.Util.UtilServer;
import eu.epicpvp.kcore.Util.UtilString;
import eu.epicpvp.kcore.Util.UtilTime;

public class SkyPvP extends SoloGame{

	private HashMap<Player,Location> island = new HashMap<>();
	private HashMap<Player,Integer> life = new HashMap<>();
	private HashMap<Location,Inventory> enderchests = new HashMap<>();
	private ArrayList<ItemStack> enderchest_material = new ArrayList<>();
	private AddonEntityKing entity_king;
	private AddonTargetNextPlayer TargetNextPlayer;
	
	public SkyPvP(kArcadeManager manager){
		super(manager);
		long l = System.currentTimeMillis();
		setTyp(GameType.SkyPvP);
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
		setWorldData(new SingleWorldData(manager,getType()));
		getWorldData().createCleanWorld();
		HashMap<File[],Integer> list = new HashMap<>();
		File[] files = getWorldData().loadSchematicFiles();
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
		
		getWorldData().setIsland(list,20, new Location(getWorldData().getWorld(),0,80,0));
	
		Chest chest;
		Block b;
		for(Location loc : getWorldData().getLocs(Team.RED)){
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
		
		UtilMap.loadChunks(new Location(getWorldData().getWorld(),0,80,0), 700);
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
		
		if((!ev.getPlayer().hasPermission(PermissionType.CHAT_LINK.getPermissionToString()))&&UtilString.isBadWord(ev.getMessage())||UtilString.checkForIP(ev.getMessage())){
			ev.setMessage("Ich heul rum!");
			ev.getPlayer().sendMessage(TranslationHandler.getText(ev.getPlayer(), "PREFIX")+TranslationHandler.getText(ev.getPlayer(), "CHAT_MESSAGE_BLOCK"));
		}
		
		if(getState()!=GameState.LobbyPhase&&getGameList().getPlayers(PlayerState.OUT).contains(ev.getPlayer())){
			ev.setCancelled(true);
			UtilPlayer.sendMessage(ev.getPlayer(),TranslationHandler.getText(ev.getPlayer(), "PREFIX_GAME", getType().getTyp())+TranslationHandler.getText(ev.getPlayer(), "SPECTATOR_CHAT_CANCEL"));
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
		for(Player p : UtilServer.getPlayers())UtilDisplay.displayTextBar(TranslationHandler.getText(p, "GAME_END_IN", UtilTime.formatSeconds(getStart())), p);
		switch(getStart()){
		case 30: broadcastWithPrefix("GAME_END_IN",UtilTime.formatSeconds(getStart()));break;
		case 15: broadcastWithPrefix("GAME_END_IN",UtilTime.formatSeconds(getStart()));break;
		case 10: broadcastWithPrefix("GAME_END_IN",UtilTime.formatSeconds(getStart()));break;
		case 5: broadcastWithPrefix("GAME_END_IN",UtilTime.formatSeconds(getStart()));break;
		case 4: broadcastWithPrefix("GAME_END_IN",UtilTime.formatSeconds(getStart()));break;
		case 3: broadcastWithPrefix("GAME_END_IN",UtilTime.formatSeconds(getStart()));break;
		case 2: broadcastWithPrefix("GAME_END_IN",UtilTime.formatSeconds(getStart()));break;
		case 1: broadcastWithPrefix("GAME_END_IN",UtilTime.formatSeconds(getStart()));break;
		case 0:
			broadcastWithPrefixName("GAME_END");
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
				getStats().addInt(v,1, StatsKey.LOSE);
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
				getStats().addInt(a,1, StatsKey.KILLS);
				getMoney().add(a, StatsKey.COINS, 5);
				broadcastWithPrefix("KILL_BY", new String[]{v.getName(),a.getName()});
				return;
			}
			broadcastWithPrefix("DEATH", v.getName());
			
			if(b)broadcastWithPrefix("GAME_AUSGESCHIEDEN", v.getName());
			getStats().addInt(v,1, StatsKey.DEATHS);
		}
	}
	
	@EventHandler
	public void GameStateChangeSkyPvP(GameStateChangeEvent ev){
		if(ev.getFrom()==GameState.InGame&&ev.getTo()==GameState.Restart){
			if(getGameList().getPlayers(PlayerState.IN).size()==1){
				Player win = getGameList().getPlayers(PlayerState.IN).get(0);
				getStats().addInt(win,1, StatsKey.WIN);
				getMoney().add(win, StatsKey.COINS, 25);
				broadcastWithPrefix("GAME_WIN", win.getName());
			}
		}
	}
	
	@EventHandler
	public void StatsLoaded(PlayerStatsLoadedEvent ev){
		if(ev.getManager().getType() != getType())return;
		if(getState()!=GameState.LobbyPhase)return;
		if(UtilPlayer.isOnline(ev.getPlayerId())){
			Player player = UtilPlayer.searchExact(ev.getPlayerId());
			int win = getStats().getInt(StatsKey.WIN, player);
			int lose = getStats().getInt(StatsKey.LOSE, player);
			
			Bukkit.getScheduler().runTask(getManager().getInstance(), new Runnable() {
				
				@Override
				public void run() {
					getManager().getHologram().sendText(player,getManager().getLoc_stats(),new String[]{
						Color.GREEN+getType().getTyp()+Color.ORANGE+"§l Info",
						TranslationHandler.getText(player, "",getType().getTyp()+" §a"+kArcade.id),
						TranslationHandler.getText(player, "GAME_HOLOGRAM_MAP", getWorldData().getMapName()),
						" ",
						TranslationHandler.getText(player, "GAME_HOLOGRAM_STATS", getType().getTyp()),
						TranslationHandler.getText(player, "GAME_HOLOGRAM_KILLS", getStats().getInt(StatsKey.KILLS, player)),
						TranslationHandler.getText(player, "GAME_HOLOGRAM_DEATHS", getStats().getInt(StatsKey.DEATHS, player)),
						" ",
						TranslationHandler.getText(player, "GAME_HOLOGRAM_GAMES", (win+lose)),
						TranslationHandler.getText(player, "GAME_HOLOGRAM_WINS", win),
						TranslationHandler.getText(player, "GAME_HOLOGRAM_LOSE", lose),
						});
				}
			});
		}
	}
	
	@EventHandler
	public void GameStartSkyPvP(GameStartEvent ev){
		getWorldData().clearWorld();
		ArrayList<Location> locs = getWorldData().getLocs(Team.RED);
		TargetNextPlayer = new AddonTargetNextPlayer(250,this);
		TargetNextPlayer.setAktiv(true);
		
		if(locs.size()<UtilServer.getPlayers().size())System.err.println("[SkyPvP] Es sind zu wenig Location's angegeben!");
		int r;
		Scoreboard board;
		for(Player p : UtilServer.getPlayers()){
			if(locs.isEmpty())break;
			getGameList().addPlayer(p, PlayerState.IN);
			getManager().Clear(p);
			if(getManager().getPermManager().hasPermission(p, PermissionType.SkyPvP_Mehr_Leben)){
				life.put(p, 3);
			}else{
				life.put(p, 2);
			}
			r=UtilMath.r(locs.size());
			p.teleport(locs.get(r));
			board=Bukkit.getScoreboardManager().getNewScoreboard();
			UtilScoreboard.addBoard(board, DisplaySlot.SIDEBAR,UtilScoreboard.getScoreboardDisplayname());
			UtilScoreboard.setScore(board, "§eLeben: ", DisplaySlot.SIDEBAR, life.get(p));
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
			entity_king.spawnMobs(getWorldData().getLocs(Team.BLUE), EntityType.IRON_GOLEM, "§6§lIronGolem");
			entity_king.setDamage(true);
			entity_king.setMove(true);
			entity_king.setAttack(true);
			entity_king.setAttack_damage(5.0);
			
			for(Entity c : entity_king.getCreature()){
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
		getManager().setRanking(StatsKey.WIN);
	}

}
