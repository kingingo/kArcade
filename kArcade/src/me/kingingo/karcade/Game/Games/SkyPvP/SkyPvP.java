package me.kingingo.karcade.Game.Games.SkyPvP;

import java.util.ArrayList;
import java.util.HashMap;

import me.kingingo.karcade.kArcade;
import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Enum.PlayerState;
import me.kingingo.karcade.Enum.Team;
import me.kingingo.karcade.Events.RankingEvent;
import me.kingingo.karcade.Game.Events.GameStartEvent;
import me.kingingo.karcade.Game.Events.GameStateChangeEvent;
import me.kingingo.karcade.Game.Games.SoloGame;
import me.kingingo.karcade.Game.World.WorldData;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.Hologram.Hologram;
import me.kingingo.kcore.Kit.Kit;
import me.kingingo.kcore.Kit.KitType;
import me.kingingo.kcore.Kit.Perk;
import me.kingingo.kcore.Kit.Shop.KitShop;
import me.kingingo.kcore.Permission.Permission;
import me.kingingo.kcore.PlayerStats.Stats;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.C;
import me.kingingo.kcore.Util.InventorySize;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilServer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class SkyPvP extends SoloGame{

	KitShop kitshop;
	HashMap<Player,Integer> life = new HashMap<>();
	Hologram hm;
	
	public SkyPvP(kArcadeManager manager){
		super(manager);
		long l = System.currentTimeMillis();
		getManager().setState(GameState.Laden);
		getManager().setTyp(GameType.SkyPvP);
		setMax_Players(24);
		setMin_Players(6);
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
		kitshop=new KitShop(manager.getInstance(),getCoins(),getTokens(),getManager().getPermManager(),"Shop",InventorySize._27,new Kit[]{
			new Kit("NAME",new ItemStack(Material.DIAMOND_SWORD),new ItemStack[]{},Permission.NONE,KitType.STARTER,2000,new Perk[]{}),
			new Kit("NAME",new ItemStack(Material.DIAMOND_SWORD),new ItemStack[]{},Permission.NONE,KitType.STARTER,2000,new Perk[]{}),
			new Kit("NAME",new ItemStack(Material.DIAMOND_SWORD),new ItemStack[]{},Permission.NONE,KitType.STARTER,2000,new Perk[]{}),
			new Kit("NAME",new ItemStack(Material.DIAMOND_SWORD),new ItemStack[]{},Permission.NONE,KitType.STARTER,2000,new Perk[]{}),
			new Kit("NAME",new ItemStack(Material.DIAMOND_SWORD),new ItemStack[]{},Permission.NONE,KitType.STARTER,2000,new Perk[]{}),
			new Kit("NAME",new ItemStack(Material.DIAMOND_SWORD),new ItemStack[]{},Permission.NONE,KitType.STARTER,2000,new Perk[]{}),
		});
		setRespawn(true);
		WorldData wd=new WorldData(manager,GameType.SkyPvP.name());
		wd.Initialize();
		manager.setWorldData(wd);
		getManager().DebugLog(l, this.getClass().getName());
	}
	
	@EventHandler
	public void Chat(AsyncPlayerChatEvent ev){
		if(ev.isCancelled())return;
		ev.setCancelled(true);
		if(getManager().getState()!=GameState.LobbyPhase&&getGameList().getPlayers(PlayerState.OUT).contains(ev.getPlayer())){
			ev.setCancelled(true);
			ev.getPlayer().sendMessage(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.SPECTATOR_CHAT_CANCEL.getText());
		}else{
			Bukkit.broadcastMessage(getManager().getPermManager().getPrefix(ev.getPlayer())+ev.getPlayer().getDisplayName()+":§7 "+ev.getMessage());
		}
	}
	
	@EventHandler
	public void inGame(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		if(getManager().getState()!=GameState.InGame)return;
		getManager().setStart(getManager().getStart()-1);
		
		switch(getManager().getStart()){
		case 30: getManager().broadcast( Text.PREFIX_GAME.getText(getManager().getTyp().name())+Text.GAME_END_IN.getText(getManager().getStart()) );break;
		case 15: getManager().broadcast( Text.PREFIX_GAME.getText(getManager().getTyp().name())+Text.GAME_END_IN.getText(getManager().getStart()) );break;
		case 10: getManager().broadcast( Text.PREFIX_GAME.getText(getManager().getTyp().name())+Text.GAME_END_IN.getText(getManager().getStart()) );break;
		case 5: getManager().broadcast( Text.PREFIX_GAME.getText(getManager().getTyp().name())+Text.GAME_END_IN.getText(getManager().getStart()) );break;
		case 4: getManager().broadcast( Text.PREFIX_GAME.getText(getManager().getTyp().name())+Text.GAME_END_IN.getText(getManager().getStart()) );break;
		case 3: getManager().broadcast( Text.PREFIX_GAME.getText(getManager().getTyp().name())+Text.GAME_END_IN.getText(getManager().getStart()) );break;
		case 2: getManager().broadcast( Text.PREFIX_GAME.getText(getManager().getTyp().name())+Text.GAME_END_IN.getText(getManager().getStart()) );break;
		case 1: getManager().broadcast( Text.PREFIX_GAME.getText(getManager().getTyp().name())+Text.GAME_END_IN.getText(getManager().getStart()) );break;
		case 0:
			getManager().broadcast( Text.PREFIX_GAME.getText(getManager().getTyp().name())+Text.GAME_END.getText() );
			getManager().setState(GameState.Restart);
			break;
		}
	}
	
	@EventHandler
	public void DeathSkyPvP(PlayerDeathEvent ev){
		if(ev.getEntity() instanceof Player){
			Player v = (Player)ev.getEntity();
			if(ev.getEntity().getKiller() instanceof Player){
				Player a = (Player)ev.getEntity().getKiller();
				getManager().getStats().setInt(a, getManager().getStats().getInt(Stats.KILLS, a)+1, Stats.KILLS);
				getManager().broadcast( Text.PREFIX_GAME.getText(getManager().getTyp().name())+Text.KILL_BY.getText(new String[]{v.getName(),a.getName()}) );
				return;
			}
			getManager().broadcast( Text.PREFIX_GAME.getText(getManager().getTyp().name())+Text.DEATH.getText(v.getName()) );
			int i = life.get(v);
			i--;
			if(i<=0){
				getManager().getStats().setInt(v, getManager().getStats().getInt(Stats.LOSE, v)+1, Stats.LOSE);
				getGameList().addPlayer(v, PlayerState.OUT);
				getManager().broadcast( Text.PREFIX_GAME.getText(getManager().getTyp().name())+Text.GAME_AUSGESCHIEDEN.getText(v.getName()) );
			}else{
				life.remove(v);
				life.put(v, i);
			}
			getManager().getStats().setInt(v, getManager().getStats().getInt(Stats.DEATHS, v)+1, Stats.DEATHS);
		}
	}
	
	@EventHandler
	public void GameStateChangeSkyPvP(GameStateChangeEvent ev){
		if(ev.getFrom()==GameState.InGame&&ev.getTo()==GameState.Restart){
			if(getGameList().getPlayers(PlayerState.IN).size()==1){
				Player win = getGameList().getPlayers(PlayerState.IN).get(0);
				getManager().getStats().setInt(win, getManager().getStats().getInt(Stats.WIN, win)+1, Stats.WIN);
				getManager().broadcast( Text.PREFIX_GAME.getText(getManager().getTyp().name())+Text.GAME_WIN.getText(win.getName()));
			}
		}
	}
	
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void JoinHologram(PlayerJoinEvent ev){
		if(getManager().getState()!=GameState.LobbyPhase)return;
		if(hm==null)hm=new Hologram(getManager().getInstance());
		int win = getManager().getStats().getInt(Stats.WIN, ev.getPlayer());
		int lose = getManager().getStats().getInt(Stats.LOSE, ev.getPlayer());
		getManager().getLoc_stats().getWorld().loadChunk(getManager().getLoc_stats().getWorld().getChunkAt(getManager().getLoc_stats()));
		hm.sendText(ev.getPlayer(),getManager().getLoc_stats().add(0, 0.3, 0),new String[]{
		C.cGreen+getManager().getTyp().string()+C.mOrange+C.Bold+" Info",
		"Server: SkyPvP §a"+kArcade.id,
		"Map: "+getManager().getWorldData().getMapName(),
		" ",
		C.cGreen+getManager().getTyp().string()+C.mOrange+C.Bold+" Stats",
		"Coins: "+getCoins().getCoins(ev.getPlayer()),
		"Rang: "+getManager().getStats().getRank(Stats.WIN, ev.getPlayer()),	
		"Kills: "+getManager().getStats().getInt(Stats.KILLS, ev.getPlayer()),
		"Tode: "+getManager().getStats().getInt(Stats.DEATHS, ev.getPlayer()),
		" ",
		"Gespielte Spiele: "+(win+lose),
		"Gewonnene Spiele: "+win,
		"Verlorene Spiele: "+lose
		});
		ev.getPlayer().getInventory().addItem(UtilItem.RenameItem(new ItemStack(Material.CHEST), "§bKitShop"));
	}
	
	@EventHandler
	public void GameStartSkyPvP(GameStartEvent ev){
		ArrayList<Location> locs = getManager().getWorldData().getLocs(Team.RED.Name());
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
			locs.remove(0);
		}
		getManager().setStart((60*30)+1);
		getManager().setState(GameState.InGame);
	}
	
	@EventHandler
	public void Ranking(RankingEvent ev){
		getManager().setRanking(Stats.WIN);
	}

}
