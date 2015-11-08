package me.kingingo.karcade.Game.Multi.Games.Versus;

import java.util.ArrayList;
import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;
import me.kingingo.karcade.Enum.PlayerState;
import me.kingingo.karcade.Game.Multi.MultiGames;
import me.kingingo.karcade.Game.Multi.Addons.GameArenaRestore;
import me.kingingo.karcade.Game.Multi.Events.MultiGamePlayerJoinEvent;
import me.kingingo.karcade.Game.Multi.Events.MultiGameStartEvent;
import me.kingingo.karcade.Game.Multi.Events.MultiGameStateChangeEvent;
import me.kingingo.karcade.Game.Multi.Games.MultiGame;
import me.kingingo.karcade.Game.Single.addons.AddonMove;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.GameStateChangeReason;
import me.kingingo.kcore.Enum.Team;
import me.kingingo.kcore.Language.Language;
import me.kingingo.kcore.StatsManager.Stats;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.UtilBG;
import me.kingingo.kcore.Util.UtilDebug;
import me.kingingo.kcore.Util.UtilDisplay;
import me.kingingo.kcore.Util.UtilException;
import me.kingingo.kcore.Util.UtilLocation;
import me.kingingo.kcore.Util.UtilPlayer;
import me.kingingo.kcore.Util.UtilScoreboard;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.kcore.Util.UtilTime;
import me.kingingo.kcore.Versus.VersusType;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

public class Versus extends MultiGame{
	
	@Getter
	@Setter
	private VersusType max_type;
	@Getter
	private VersusType type;
	@Getter
	private AddonMove addonMove;
	private GameArenaRestore area;
	private Scoreboard scoreboard;
	
	public Versus(MultiGames games,String Map,Location location) {
		super(games,location);
		games.getManager().DebugLog(new String[]{"Versus "+getArena(),"Map "+Map});
		games.getManager().DebugLog(UtilServer.getLagMeter().getUpdate());
		setMap(Map);
		UtilBG.setHub("versus");
		setUpdateTo("versus");
		Location ecke1 = null;
		Location ecke2 = null;
		getGames().getLocs().put(this, new HashMap<Team,ArrayList<Location>>());
		for(Block block : UtilLocation.searchBlocks(Material.SPONGE, 100, location)){
			if(block.getRelative(BlockFace.UP).getType()==Material.WOOL){
				if(block.getRelative(BlockFace.UP).getData()==14){
					if(!getGames().getLocs().get(this).containsKey(Team.RED))getGames().getLocs().get(((MultiGame)this)).put(Team.RED, new ArrayList<Location>());
					getGames().getLocs().get( ((MultiGame)this) ).get(Team.RED).add(block.getRelative(BlockFace.UP).getLocation());
				}else if(block.getRelative(BlockFace.UP).getData()==11){
					if(!getGames().getLocs().get(((MultiGame)this)).containsKey(Team.BLUE))getGames().getLocs().get(((MultiGame)this)).put(Team.BLUE, new ArrayList<Location>());
					getGames().getLocs().get(((MultiGame)this)).get(Team.BLUE).add(block.getRelative(BlockFace.UP).getLocation());
				}else if(block.getRelative(BlockFace.UP).getData()==5){
					if(!getGames().getLocs().get(((MultiGame)this)).containsKey(Team.GREEN))getGames().getLocs().get(((MultiGame)this)).put(Team.GREEN, new ArrayList<Location>());
					getGames().getLocs().get(((MultiGame)this)).get(Team.GREEN).add(block.getRelative(BlockFace.UP).getLocation());
				}else if(block.getRelative(BlockFace.UP).getData()==4){
					if(!getGames().getLocs().get(((MultiGame)this)).containsKey(Team.YELLOW))getGames().getLocs().get(((MultiGame)this)).put(Team.YELLOW, new ArrayList<Location>());
					getGames().getLocs().get(((MultiGame)this)).get(Team.YELLOW).add(block.getRelative(BlockFace.UP).getLocation());
				}else if(block.getRelative(BlockFace.UP).getData()==1){
					if(!getGames().getLocs().get(((MultiGame)this)).containsKey(Team.ORANGE))getGames().getLocs().get(((MultiGame)this)).put(Team.ORANGE, new ArrayList<Location>());
					getGames().getLocs().get(((MultiGame)this)).get(Team.ORANGE).add(block.getRelative(BlockFace.UP).getLocation());
				}else if(block.getRelative(BlockFace.UP).getData()==7){
					if(!getGames().getLocs().get(((MultiGame)this)).containsKey(Team.GRAY))getGames().getLocs().get(((MultiGame)this)).put(Team.GRAY, new ArrayList<Location>());
					getGames().getLocs().get(((MultiGame)this)).get(Team.GRAY).add(block.getRelative(BlockFace.UP).getLocation());
				}
				block.getRelative(BlockFace.UP).setType(Material.AIR);
				block.setType(Material.AIR);
			}
		}
		setMax_type( VersusType.withTeamAnzahl( getGames().getLocs().get(this).size() ) );
		if(getMax_type()==null){
			String s="";
			for(Team t : getGames().getLocs().get(this).keySet())s+=","+t.Name();
			UtilException.catchException("a"+getGames().getManager().getInstance().getConfig().getString("Config.Server.ID"), Bukkit.getServer().getIp(), getGames().getManager().getMysql(),"LOC:"+UtilLocation.getLocString(location)+"  MAP:"+getMap()+"SIZE:"+getGames().getLocs().get(this).size()+" "+s);
			UtilDebug.debug("MaxType", new String[]{"SIZE:"+getGames().getLocs().get(this).size(),s});
		}else{
			String s="";
			for(Team t : getGames().getLocs().get(this).keySet())s+=","+t.Name();
			for(Team t : getMax_type().getTeam()){
				if(!getGames().getLocs().get(this).containsKey(t)){
					UtilException.catchException("a"+getGames().getManager().getInstance().getConfig().getString("Config.Server.ID"), Bukkit.getServer().getIp(), getGames().getManager().getMysql(),"FEHLT        L:"+getMax_type().getTeam().length+"   "+"TEAM"+t.Name()+"   LOC:"+UtilLocation.getLocString(location)+"  MAP:"+getMap()+"SIZE:"+getGames().getLocs().get(this).size()+" "+s);
				}
			}
		}
		ecke1 = UtilLocation.getLowestLocInCase(location, Material.BARRIER);
		ecke2 = UtilLocation.getHighestLocInCase(location, Material.BARRIER);
		
		if(ecke1==null||ecke2==null){
			Log("ECKE1: "+(ecke1==null)+" ECKE2:"+(ecke2==null));
			Log("LOC: "+location.toString());
		}else{
			area=new GameArenaRestore(this, ecke1, ecke2);
		}
		
		setDropItem(false);
		setPickItem(true);
		setDropItembydeath(false);
		setFoodlevelchange(false);
		addonMove=new AddonMove(games.getManager());
		
		this.scoreboard=Bukkit.getScoreboardManager().getNewScoreboard();
		UtilScoreboard.addBoard(scoreboard, DisplaySlot.SIDEBAR, "�6�lVERSUS Board:");
		UtilScoreboard.setScore(scoreboard, "   ", DisplaySlot.SIDEBAR, 7);
		UtilScoreboard.setScore(scoreboard, "�cMap: ", DisplaySlot.SIDEBAR, 6);
		UtilScoreboard.setScore(scoreboard, "�7"+getMap(), DisplaySlot.SIDEBAR, 5);
		UtilScoreboard.setScore(scoreboard, " ", DisplaySlot.SIDEBAR, 4);
		UtilScoreboard.setScore(scoreboard, "�eKit: ", DisplaySlot.SIDEBAR, 3);
		UtilScoreboard.setScore(scoreboard, "  ", DisplaySlot.SIDEBAR, 1);
		UtilScoreboard.setScore(scoreboard, "�7----------------", DisplaySlot.SIDEBAR, 0);
	}
	
	public void setType(VersusType type){
		this.type=type;
		setTeam(type.getTeam().length);
	}
	
	@EventHandler
	public void Death(PlayerDeathEvent ev){
		if(getTeamList().containsKey(ev.getEntity())){
			UtilPlayer.RespawnNow(ev.getEntity(), getGames().getManager().getInstance());
			getGameList().getPlayers().remove(ev.getEntity());
			getGameList().getPlayers().put(ev.getEntity(), PlayerState.OUT);
			getGames().getStats().setInt(ev.getEntity(), getGames().getStats().getInt(Stats.LOSE, ev.getEntity())+1, Stats.LOSE);
			getGames().getStats().setInt(ev.getEntity(), getGames().getStats().getInt(Stats.DEATHS, ev.getEntity())+1, Stats.DEATHS);
			
			if(ev.getEntity().getKiller()!=null){
				getGames().getCoins().addCoins(ev.getEntity().getKiller(), false, 4);
				getGames().getStats().setInt(ev.getEntity().getKiller(), getGames().getStats().getInt(Stats.KILLS, ev.getEntity().getKiller())+1, Stats.KILLS);
				broadcastWithPrefix("KILL_BY", new String[]{ ev.getEntity().getName() , ev.getEntity().getKiller().getName() });
				ev.getEntity().sendMessage(Language.getText(ev.getEntity(),"PREFIX_GAME",getGames().getType().getTyp())+Language.getText(ev.getEntity(), "HEART",new String[]{ev.getEntity().getKiller().getName(),((int)UtilPlayer.getHealth(ev.getEntity().getKiller()))+""}));
			}else{
				broadcastWithPrefix("DEATH", new String[]{ ev.getEntity().getName() });
			}
		}
	}
	
	@EventHandler
	public void InGame(UpdateEvent ev){
		if(ev.getType()==UpdateType.SEC){
			if(getState()==GameState.InGame){
				if(getTimer()==-1)setTimer(60*5);
				
				if(getTimer()<0){
					setTimer(60*5);
				}
				setTimer(getTimer()-1);
				for(Player p : getGameList().getPlayers().keySet()){
					UtilDisplay.displayTextBar(p,Language.getText(p, "GAME_END_IN",UtilTime.formatSeconds(getTimer())));
				}
				
				if(getTimer()!=0){
					switch(getTimer()){
					case 300:broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getTimer()));break;
					case 120:broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getTimer()));break;
					case 90:broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getTimer()));break;
					case 60:broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getTimer()));break;
					case 30:broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getTimer()));break;
					case 15:broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getTimer()));break;
					case 10:broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getTimer()));break;
					case 3:broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getTimer()));break;
					case 2:broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getTimer()));break;
					case 1:broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getTimer()));break;
					}
				}else{
					broadcastWithPrefix(Language.getText("GAME_END"));
					setState(GameState.Restart,GameStateChangeReason.GAME_END);
				}
			}
		}
	}
	
	@EventHandler
	public void lobby(MultiGameStateChangeEvent ev){
		if(ev.getGame()!=this)return;
		if(ev.getTo()==GameState.LobbyPhase){
			if(area!=null)area.restore();
			this.addonMove.setnotMove(true);

			if(getKit()!=null){
				UtilScoreboard.resetScore(scoreboard, "�7"+getKit().kit, DisplaySlot.SIDEBAR);
			}else{
				UtilScoreboard.resetScore(scoreboard, "�7Default", DisplaySlot.SIDEBAR);
			}
			
			org.bukkit.scoreboard.Team t;
			for(int i = 0; i<this.scoreboard.getTeams().size(); i++){
				t=(org.bukkit.scoreboard.Team)this.scoreboard.getTeams().toArray()[i];
				for(int a = 0; a<t.getPlayers().size(); a++)t.removePlayer((OfflinePlayer)t.getPlayers().toArray()[a]);
			}
			
			setDamagePvP(false);
			setDamage(false);
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void Start(MultiGameStartEvent ev){
		if(ev.getGame()!=this)return;
			this.addonMove.setnotMove(false);
			
			for(Player player : getTeamList().keySet()){
				if(getKit()!=null){
					if(getKit().content!=null&&getKit().armor_content!=null){
						player.getInventory().setArmorContents(getKit().armor_content);
						player.getInventory().setContents(getKit().content);
					}
				}else{
					player.getInventory().addItem(new ItemStack(Material.DIAMOND_SWORD));
					player.getInventory().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
					player.getInventory().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
					player.getInventory().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
					player.getInventory().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
				}
			}
			
			if(getKit()!=null){
				UtilScoreboard.setScore(scoreboard, "�7"+getKit().kit, DisplaySlot.SIDEBAR, 2);
			}else{
				UtilScoreboard.setScore(scoreboard, "�7Default", DisplaySlot.SIDEBAR, 2);
			}
			
			TeamTab(scoreboard);
			setDamagePvP(true);
			setDamage(true);
			setState(GameState.InGame);
	}
	
	@EventHandler
	public void Move(MultiGamePlayerJoinEvent ev){
		if(ev.getGame()!=this)return;
		//Pr�ft ob dieser Spieler f�r die Arena angemeldet ist.
		if(getTeamList().containsKey(ev.getPlayer())){
			//F�gt Spieler zu AddonMove hinzu
			addonMove.getMovelist().add(ev.getPlayer());
		}
	}

}
