package me.kingingo.karcade.Game.Games.SkyPvP;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import me.kingingo.karcade.kArcade;
import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Enum.PlayerState;
import me.kingingo.karcade.Enum.Team;
import me.kingingo.karcade.Events.RankingEvent;
import me.kingingo.karcade.Game.Events.GameStateChangeEvent;
import me.kingingo.karcade.Game.Games.SoloGame;
import me.kingingo.karcade.Game.World.WorldData;
import me.kingingo.karcade.Game.addons.AddonEntityKing;
import me.kingingo.kcore.Addons.AddonDay;
import me.kingingo.kcore.Addons.AddonNight;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.Game.Events.GameStartEvent;
import me.kingingo.kcore.Hologram.Hologram;
import me.kingingo.kcore.Permission.Permission;
import me.kingingo.kcore.PlayerStats.Stats;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.C;
import me.kingingo.kcore.Util.UtilDisplay;
import me.kingingo.kcore.Util.UtilLocation;
import me.kingingo.kcore.Util.UtilPlayer;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.kcore.Util.UtilTime;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class SkyPvP extends SoloGame{

	HashMap<Player,Location> island = new HashMap<>();
	HashMap<Player,Integer> life = new HashMap<>();
	Hologram hm;
	AddonEntityKing entity_king;
	
	public SkyPvP(kArcadeManager manager){
		super(manager);
		registerListener();
		long l = System.currentTimeMillis();
		getManager().setState(GameState.Laden);
		setTyp(GameType.SkyPvP);
		setMax_Players(12);
		setMin_Players(3);
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
		wd.setIsland(new File[]{new File(kArcade.FilePath+"/SkyPvP/elite.schematic"),new File(kArcade.FilePath+"/SkyPvP/premium.schematic"),new File(kArcade.FilePath+"/SkyPvP/vip.schematic")},12,30, new Location(wd.getWorld(),0,80,0), 50);
		getManager().DebugLog(l, this.getClass().getName());
		
		Chest chest;
		Block b;
		for(Location loc : wd.getLocs(Team.RED.Name())){
			b=UtilLocation.searchBlock(Material.CHEST, 10, loc);
			if(b!=null&&b.getState() instanceof Chest){
				chest=(Chest)b.getState();
				chest.getInventory().addItem(new ItemStack(Material.LAVA_BUCKET));
				chest.getInventory().addItem(new ItemStack(Material.BOW));
				chest.getInventory().addItem(new ItemStack(Material.ARROW,2));
				chest.getInventory().addItem(new ItemStack(Material.EGG,2));
				chest.getInventory().addItem(new ItemStack(Material.SNOW_BALL,2));
				chest.getInventory().addItem(new ItemStack(Material.COAL,5));
				chest.getInventory().addItem(new ItemStack(Material.CARROT_ITEM,2));
				chest.getInventory().addItem(new ItemStack(Material.BONE,2));
			}
			chest=null;
			b=null;
		}
	}
	
	@EventHandler
	public void Chat(AsyncPlayerChatEvent ev){
		if(ev.isCancelled())return;
		ev.setCancelled(true);
		if(getManager().getState()!=GameState.LobbyPhase&&getGameList().getPlayers(PlayerState.OUT).contains(ev.getPlayer())){
			ev.setCancelled(true);
			UtilPlayer.sendMessage(ev.getPlayer(),Text.PREFIX_GAME.getText(getType().getTyp())+Text.SPECTATOR_CHAT_CANCEL.getText());
		}else{
			UtilServer.broadcast(getManager().getPermManager().getPrefix(ev.getPlayer())+ev.getPlayer().getDisplayName()+":§7 "+ev.getMessage());
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void RespawnLocation(PlayerRespawnEvent ev){
		if(island.containsKey(ev.getPlayer()))ev.setRespawnLocation(island.get(ev.getPlayer()));
	}
	
	@EventHandler
	public void inGame(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		if(getManager().getState()!=GameState.InGame)return;
		getManager().setStart(getManager().getStart()-1);
		for(Player p : UtilServer.getPlayers())UtilDisplay.displayTextBar(Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())), p);
		switch(getManager().getStart()){
		case 30: getManager().broadcast( Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())) );break;
		case 15: getManager().broadcast( Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())) );break;
		case 10: getManager().broadcast( Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())) );break;
		case 5: getManager().broadcast( Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())) );break;
		case 4: getManager().broadcast( Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())) );break;
		case 3: getManager().broadcast( Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())) );break;
		case 2: getManager().broadcast( Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())) );break;
		case 1: getManager().broadcast( Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())) );break;
		case 0:
			getManager().broadcast( Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_END.getText() );
			getManager().setState(GameState.Restart);
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
			
			if(ev.getEntity().getKiller() instanceof Player){
				Player a = (Player)ev.getEntity().getKiller();
				getStats().setInt(a, getStats().getInt(Stats.KILLS, a)+1, Stats.KILLS);
				getManager().broadcast( Text.PREFIX_GAME.getText(getType().getTyp())+Text.KILL_BY.getText(new String[]{v.getName(),a.getName()}) );
				return;
			}
			getManager().broadcast( Text.PREFIX_GAME.getText(getType().getTyp())+Text.DEATH.getText(v.getName()) );
			if(b)getManager().broadcast( Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_AUSGESCHIEDEN.getText(v.getName()) );
			getStats().setInt(v, getStats().getInt(Stats.DEATHS, v)+1, Stats.DEATHS);
		}
	}
	
	@EventHandler
	public void GameStateChangeSkyPvP(GameStateChangeEvent ev){
		if(ev.getFrom()==GameState.InGame&&ev.getTo()==GameState.Restart){
			if(getGameList().getPlayers(PlayerState.IN).size()==1){
				Player win = getGameList().getPlayers(PlayerState.IN).get(0);
				getStats().setInt(win, getStats().getInt(Stats.WIN, win)+1, Stats.WIN);
				getManager().broadcast( Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_WIN.getText(win.getName()));
			}
		}
	}
	
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void JoinHologram(PlayerJoinEvent ev){
		if(getManager().getState()!=GameState.LobbyPhase)return;
		if(hm==null)hm=new Hologram(getManager().getInstance());
		int win = getStats().getInt(Stats.WIN, ev.getPlayer());
		int lose = getStats().getInt(Stats.LOSE, ev.getPlayer());
		getManager().getLoc_stats().getWorld().loadChunk(getManager().getLoc_stats().getWorld().getChunkAt(getManager().getLoc_stats()));
		hm.sendText(ev.getPlayer(),getManager().getLoc_stats().add(0, 0.3, 0),new String[]{
		C.cGreen+getType().getTyp()+C.mOrange+C.Bold+" Info",
		"Server: SkyPvP §a"+kArcade.id,
		"Map: "+getWorldData().getMapName(),
		" ",
		C.cGreen+getType().getTyp()+C.mOrange+C.Bold+" Stats",
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
		ArrayList<Location> locs = getWorldData().getLocs(Team.RED.Name());
		for(Location loc : locs){
			loc.getWorld().loadChunk(loc.getWorld().getChunkAt(loc));
		}
		if(locs.size()<UtilServer.getPlayers().length)System.err.println("[SkyPvP] Es sind zu wenig Location's angegeben!");
		for(Player p : UtilServer.getPlayers()){
			getGameList().addPlayer(p, PlayerState.IN);
			getManager().Clear(p);
			if(getManager().getPermManager().hasPermission(p, Permission.SkyPvP_Mehr_Leben)){
				life.put(p, 5);
			}else{
				life.put(p, 3);
			}
			p.teleport(locs.get(0));
			
			p.getInventory().addItem(new ItemStack(Material.STONE_PICKAXE));
			p.getInventory().addItem(new ItemStack(Material.STONE_SWORD));
			p.getInventory().addItem(new ItemStack(Material.STONE_AXE));
			p.getInventory().addItem(new ItemStack(Material.STONE_SPADE));
			p.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
			
			island.put(p, locs.get(0));
			locs.remove(0);
		}
		
		if(!locs.isEmpty()){
			entity_king=new AddonEntityKing(getManager(), locs, EntityType.IRON_GOLEM, "§c§lIronGolem");
			entity_king.setDamage(true);
			entity_king.setMove(true);
		}
		
		new AddonDay(getManager().getInstance(),getWorldData().getWorld());
		getManager().setStart((60*30)+1);
		getManager().setState(GameState.InGame);
	}
	
	@EventHandler
	public void Ranking(RankingEvent ev){
		getManager().setRanking(Stats.WIN);
	}

}
