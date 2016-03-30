package eu.epicpvp.karcade.Game.Multi.Games.SurvivalGames1vs1;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

import dev.wolveringer.dataserver.gamestats.GameState;
import dev.wolveringer.dataserver.gamestats.StatsKey;
import eu.epicpvp.karcade.Game.Multi.MultiGames;
import eu.epicpvp.karcade.Game.Multi.Addons.MultiAddonMove;
import eu.epicpvp.karcade.Game.Multi.Addons.MultiGameArenaRestore;
import eu.epicpvp.karcade.Game.Multi.Addons.Evemts.MultiGameAddonChatEvent;
import eu.epicpvp.karcade.Game.Multi.Events.MultiGamePlayerJoinEvent;
import eu.epicpvp.karcade.Game.Multi.Events.MultiGameStartEvent;
import eu.epicpvp.karcade.Game.Multi.Events.MultiGameStateChangeEvent;
import eu.epicpvp.karcade.Game.Multi.Games.MultiTeamGame;
import eu.epicpvp.kcore.Enum.GameStateChangeReason;
import eu.epicpvp.kcore.Enum.PlayerState;
import eu.epicpvp.kcore.Enum.Team;
import eu.epicpvp.kcore.Language.Language;
import eu.epicpvp.kcore.PacketAPI.Packets.kPacketPlayOutWorldBorder;
import eu.epicpvp.kcore.Update.UpdateType;
import eu.epicpvp.kcore.Update.Event.UpdateEvent;
import eu.epicpvp.kcore.Util.TimeSpan;
import eu.epicpvp.kcore.Util.Title;
import eu.epicpvp.kcore.Util.UtilBG;
import eu.epicpvp.kcore.Util.UtilDisplay;
import eu.epicpvp.kcore.Util.UtilInv;
import eu.epicpvp.kcore.Util.UtilPlayer;
import eu.epicpvp.kcore.Util.UtilScoreboard;
import eu.epicpvp.kcore.Util.UtilTime;
import eu.epicpvp.kcore.Util.UtilWorld;
import lombok.Getter;
import lombok.Setter;

public class SurvivalGames1vs1 extends MultiTeamGame{

	private MultiGameArenaRestore area;
	private kPacketPlayOutWorldBorder packet;
	private MultiAddonMove addonMove;
	@Getter
	private HashMap<Chest,ArrayList<String>> template;
	@Getter
	private HashMap<String,Integer> template_type;
	
	//Enderchest Variabeln
	@Getter
	@Setter
	private long enderchest_time=0; //Change Time
	@Getter
	@Setter
	private Location enderchest_loc=null; //Last Location
	@Getter
	@Setter
	private Inventory enderchest_inv=null; //Inventory Enderchest
	private Scoreboard scoreboard;
	
	//VILLAGER GREEN NORMAL CHEST
	//VILLAGER BLACK ENDERCHEST LOCS
	//VILLAGER RED ECKE1
	//VILLAGER BLUE ECKE2
	
	//SHEEP RED User Chest for RED
	//SHEEP BLUE User Chest for BLUE
	
	//RED User Location
	//BLUE User Location
	
	public SurvivalGames1vs1(MultiGames games,String Map,Location location,File file) {
		super(games,Map, location);
		setStartCountdown(10);
		UtilBG.setHub("versus");
		setUpdateTo("versus");
		this.scoreboard=Bukkit.getScoreboardManager().getNewScoreboard();
		UtilScoreboard.addBoard(scoreboard, DisplaySlot.SIDEBAR, "§6§lEpicPvP.eu - Board");
		UtilScoreboard.setScore(scoreboard, " ", DisplaySlot.SIDEBAR, 3);
		UtilScoreboard.setScore(scoreboard, "§7Time: ", DisplaySlot.SIDEBAR, 2);
		UtilScoreboard.setScore(scoreboard, "  ", DisplaySlot.SIDEBAR, 0);
		
		this.template_type=new HashMap<>();
		this.template=new HashMap<>();
		this.addonMove=new MultiAddonMove(this);
		this.addonMove.setMove(false);
		getWorldData().loadSchematic(this, location, file);
		
		if(!getWorldData().existLoc(this, Team.VILLAGE_RED)||
				getWorldData().existLoc(this, Team.VILLAGE_RED)&&getWorldData().getLocs(this, Team.VILLAGE_RED).isEmpty()){
			Log("Fehler VILLAGE_RED NICHT GEFUNDEN");
		}else if(!getWorldData().existLoc(this, Team.VILLAGE_BLUE)||
				getWorldData().existLoc(this, Team.VILLAGE_BLUE)&&getWorldData().getLocs(this, Team.VILLAGE_BLUE).isEmpty()){
			Log("Fehler VILLAGE_BLUE NICHT GEFUNDEN");
		}else{
			area=new MultiGameArenaRestore(this, getWorldData().getLocs(this, Team.VILLAGE_RED).get(0).add(0, 1, 0), getWorldData().getLocs(this, Team.VILLAGE_BLUE).get(0));
			this.packet=UtilWorld.createWorldBorder(getPasteLocation(), 125*2, 25, 10);
		}
		this.area.setTntDestroy(false);
		this.area.setOnlyBuild(true);
		UtilSurvivalGames1vs1.loadWorld(this, template, template_type);
		
		setBlockBreak(true);
		setBlockPlace(true);
		setDropItem(true);
		setPickItem(true);
		setDropItembydeath(true);
		setFoodlevelchange(true);
		setDamagePvP(false);
		setDamage(false);
		loadMaxTeam();
	}
	
	@EventHandler
	public void tnt(BlockPlaceEvent ev){
		if(getState()==GameState.InGame){
			if(ev.getBlock().getType()==Material.TNT&&area.isInArea(ev.getBlock().getLocation())){
				UtilInv.remove(ev.getPlayer(), new ItemStack(Material.TNT), 1);
				ev.getBlock().setType(Material.AIR);
				ev.getBlock().getLocation().getWorld().spawnEntity(ev.getBlock().getLocation(),EntityType.PRIMED_TNT);
			}
		}
	}
	
	@EventHandler
	public void BlockIgnite(BlockIgniteEvent ev){
		if(getState()==GameState.InGame){
			if(getGameList().getPlayers().containsKey(ev.getPlayer())){
				if(ev.getCause() == IgniteCause.FLINT_AND_STEEL&&ev.getPlayer().getItemInHand().getType()==Material.FLINT_AND_STEEL){
					ev.getPlayer().getItemInHand().setDurability((short) (ev.getPlayer().getItemInHand().getDurability() + ((short) (ev.getPlayer().getItemInHand().getType().getMaxDurability()/5))) );
				}
			}
		}
	}

	@EventHandler(priority=EventPriority.LOWEST)
	public void Join(MultiGamePlayerJoinEvent ev){
		if(ev.getGame()!=this)return;
		//Pr§ft ob dieser Spieler f§r die Arena angemeldet ist.
		if(getTeamList().containsKey(ev.getPlayer())){
			//Spieler wird zu der Location des Teams teleportiert
			setTimer(-1);
			ev.getPlayer().teleport( getGames().getWorldData().getLocs(this, getTeamList().get(ev.getPlayer())).get(0));
			ev.setCancelled(true);
			updateInfo();
		}
	}
	

	@EventHandler(ignoreCancelled=false)
	public void enderchest(PlayerInteractEvent ev){
		if(getGameList().getPlayers().containsKey(ev.getPlayer())){
			if(getState()==GameState.InGame){
				if(ev.getClickedBlock()!=null){
					if(ev.getClickedBlock().getType()==Material.ENDER_CHEST){
						if(getGameList().getPlayers().get(ev.getPlayer())==PlayerState.IN){
							ev.setCancelled(true);
							ev.getPlayer().openInventory(enderchest_inv);
							return;
						}
					}
				}
			}else{
				ev.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void inGame(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		if(getState()!=GameState.InGame)return;
		
		if((System.currentTimeMillis() - this.enderchest_time) >= (TimeSpan.SECOND*30)){
			UtilSurvivalGames1vs1.loadEnderChest(this);
		}
		
		setTimer(getTimer()-1);
		UtilScoreboard.resetScore(scoreboard, 1, DisplaySlot.SIDEBAR);
		UtilScoreboard.setScore(scoreboard, "§c"+UtilTime.formatSeconds(getTimer()), DisplaySlot.SIDEBAR, 1);
		if(getTimer()<0)setTimer((60*12)+1);
		
		for(Player player : getGameList().getPlayers().keySet()){
			UtilDisplay.displayTextBar(Language.getText(player, "GAME_END_IN", UtilTime.formatSeconds(getTimer())), player);
			player.setLevel( (int)(((TimeSpan.SECOND*30)-(System.currentTimeMillis() - this.enderchest_time))/1000) );
		}
		
		switch(getTimer()){
		case 30: broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getTimer()));break;
		case 15: broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getTimer()));break;
		case 10: broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getTimer()));break;
		case 5: broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getTimer()));break;
		case 4: broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getTimer()));break;
		case 3: broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getTimer()));break;
		case 2: broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getTimer()));break;
		case 1: broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getTimer()));break;
		case 0:
			broadcastWithPrefix(Language.getText("GAME_END"));
			setState(GameState.Restart,GameStateChangeReason.GAME_END);
			break;
		}
	}
	
	Player v;
	Player a;
	@EventHandler
	public void DeathSG1vs1(PlayerDeathEvent ev){
		if(ev.getEntity() instanceof Player && getGameList().getPlayers().containsKey( ((Player)ev.getEntity()) )){
			v = (Player)ev.getEntity();
			UtilPlayer.RespawnNow(v, getGames().getManager().getInstance());
			
			getGames().getStats().setInt(v, 1, StatsKey.LOSE);
			getGameList().addPlayer(v, PlayerState.OUT);
			
			if(ev.getEntity().getKiller() instanceof Player){
				a = (Player)ev.getEntity().getKiller();
				getGames().getStats().setInt(a, 1, StatsKey.KILLS);
				broadcastWithPrefix("KILL_BY", new String[]{v.getName(),a.getName()});
				return;
			}
			broadcastWithPrefix("DEATH", v.getName());
			getGames().getStats().setInt(v, 1, StatsKey.DEATHS);
		}
	}
	
	@EventHandler
	public void chat(MultiGameAddonChatEvent ev){
		if(getGameList().getPlayers().containsKey(ev.getPlayer())){
			ev.setCancelled(true);
			
			for(Player player : getGameList().getPlayers().keySet()){
				System.out.println("[AddonChat:"+getArena()+"] "+ev.getPlayer().getName()+": "+ev.getMessage());
				player.sendMessage(getTeam(ev.getPlayer()).getColor()+ev.getPlayer().getName()+"§8 § §7"+ev.getMessage());
			}
		}
	}
	
	@EventHandler
	public void MultiGameStateChangeSG1vs1(MultiGameStateChangeEvent ev){
		if(ev.getGame()==this&&ev.getTo()==GameState.Restart){
			ArrayList<Player> list = getGameList().getPlayers(PlayerState.IN);
			if(list.size()==1){
				Player p = list.get(0);
				getGames().getStats().addInt(p, 1, StatsKey.WIN);
				getGames().getMoney().addInt(p,12,StatsKey.COINS);
				broadcastWithPrefix("GAME_WIN", p.getName());
				new Title("§6§lGEWONNEN").send(p);
			}else if(list.size()==2){
				Player p = list.get(0);
				Player p1 = list.get(1);
				getGames().getStats().addInt(p, 1, StatsKey.WIN);
				getGames().getMoney().addInt(p,12,StatsKey.COINS);
				getGames().getStats().addInt(p1, 1, StatsKey.WIN);
				getGames().getMoney().addInt(p1,12,StatsKey.COINS);
				new Title("§6§lGEWONNEN").send(p);
				new Title("§6§lGEWONNEN").send(p1);
			}
		}
		
	}
	
	@EventHandler
	public void lobby(MultiGameStateChangeEvent ev){
		if(ev.getGame()!=this)return;
		if(ev.getTo()==GameState.Restart){
			if(area!=null)area.restore();
		
			addonMove.setMove(false);
			setDamagePvP(false);
			setDamage(false);
			setDropItem(false);
			UtilSurvivalGames1vs1.loadWorld(this, template, template_type);
		}
	}
	
	@EventHandler(priority=EventPriority.NORMAL)
	public void start(MultiGameStartEvent ev){
		if(ev.getGame() == this){
			if(enderchest_inv!=null)enderchest_inv.clear();
			enderchest_inv=null;
			UtilSurvivalGames1vs1.loadEnderChest(this);
			for(Player player : getGameList().getPlayers().keySet()){
				player.closeInventory();
				getGames().getManager().Clear(player);
				player.getInventory().setItem(8, UtilSurvivalGames1vs1.getEnderchest_compass());
				player.setScoreboard(scoreboard);
				UtilPlayer.sendPacket(player, this.packet);
			}
			
			setTeamTab(scoreboard);
			setDropItem(true);
			setDamagePvP(true);
			setDamage(true);
			addonMove.setMove(true);
			setState(GameState.InGame);
		}
	}
}
