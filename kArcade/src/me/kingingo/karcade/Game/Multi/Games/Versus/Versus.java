package me.kingingo.karcade.Game.Multi.Games.Versus;

import java.util.ArrayList;
import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;
import me.kingingo.karcade.Enum.PlayerState;
import me.kingingo.karcade.Game.Multi.MultiGames;
import me.kingingo.karcade.Game.Multi.Events.MultiGamePlayerJoinEvent;
import me.kingingo.karcade.Game.Multi.Events.MultiGameStartEvent;
import me.kingingo.karcade.Game.Multi.Events.MultiGameStateChangeEvent;
import me.kingingo.karcade.Game.Multi.Games.MultiGame;
import me.kingingo.karcade.Game.Single.addons.AddonMove;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.GameStateChangeReason;
import me.kingingo.kcore.Enum.Team;
import me.kingingo.kcore.Kit.Kit;
import me.kingingo.kcore.Language.Language;
import me.kingingo.kcore.StatsManager.Stats;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.UtilBG;
import me.kingingo.kcore.Util.UtilDisplay;
import me.kingingo.kcore.Util.UtilLocation;
import me.kingingo.kcore.Util.UtilPlayer;
import me.kingingo.kcore.Util.UtilTime;
import me.kingingo.kcore.Versus.VersusKit;
import me.kingingo.kcore.Versus.VersusType;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class Versus extends MultiGame{
	
	@Getter
	@Setter
	private VersusType type;
	@Getter
	private AddonMove addonMove;
	
	public Versus(MultiGames games,String Map,Location location) {
		super(games);
		setMap(Map);
		UtilBG.setHub("versus");
		setUpdateTo("versus");
		getGames().getLocs().put(this, new HashMap<Team,ArrayList<Location>>());
		for(Block block : UtilLocation.searchBlocks(Material.SPONGE, 40, location)){
			if(block.getRelative(BlockFace.UP).getType()==Material.WOOL){
				if(block.getRelative(BlockFace.UP).getData()==14){
					if(!getGames().getLocs().get(this).containsKey(Team.RED))getGames().getLocs().get(((MultiGame)this)).put(Team.RED, new ArrayList<Location>());
					getGames().getLocs().get( ((MultiGame)this) ).get(Team.RED).add(block.getLocation());
				}else if(block.getRelative(BlockFace.UP).getData()==11){
					if(!getGames().getLocs().get(((MultiGame)this)).containsKey(Team.BLUE))getGames().getLocs().get(((MultiGame)this)).put(Team.BLUE, new ArrayList<Location>());
					getGames().getLocs().get(((MultiGame)this)).get(Team.BLUE).add(block.getLocation());
				}else if(block.getRelative(BlockFace.UP).getData()==5){
					if(!getGames().getLocs().get(((MultiGame)this)).containsKey(Team.GREEN))getGames().getLocs().get(((MultiGame)this)).put(Team.GREEN, new ArrayList<Location>());
					getGames().getLocs().get(((MultiGame)this)).get(Team.GREEN).add(block.getLocation());
				}else if(block.getRelative(BlockFace.UP).getData()==4){
					if(!getGames().getLocs().get(((MultiGame)this)).containsKey(Team.YELLOW))getGames().getLocs().get(((MultiGame)this)).put(Team.YELLOW, new ArrayList<Location>());
					getGames().getLocs().get(((MultiGame)this)).get(Team.YELLOW).add(block.getLocation());
				}else if(block.getRelative(BlockFace.UP).getData()==1){
					if(!getGames().getLocs().get(((MultiGame)this)).containsKey(Team.ORANGE))getGames().getLocs().get(((MultiGame)this)).put(Team.ORANGE, new ArrayList<Location>());
					getGames().getLocs().get(((MultiGame)this)).get(Team.ORANGE).add(block.getLocation());
				}else if(block.getRelative(BlockFace.UP).getData()==7){
					if(!getGames().getLocs().get(((MultiGame)this)).containsKey(Team.GRAY))getGames().getLocs().get(((MultiGame)this)).put(Team.GRAY, new ArrayList<Location>());
					getGames().getLocs().get(((MultiGame)this)).get(Team.GRAY).add(block.getLocation());
				}
				block.getRelative(BlockFace.UP).setType(Material.AIR);
				block.setType(Material.AIR);
			}
		}
		setBlockBreak(false);
		setBlockPlace(false);
		setDropItem(false);
		setPickItem(false);
		setDropItembydeath(false);
		setFoodlevelchange(false);
		addonMove=new AddonMove(games.getManager());
		setType( VersusType.withTeamAnzahl( getGames().getLocs().get(this).size() ) );
		setState(GameState.LobbyPhase);
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
			addonMove.setnotMove(true);
			setDamagePvP(false);
			setDamage(false);
		}
	}
	
	@EventHandler
	public void Start(MultiGameStartEvent ev){
		if(ev.getGame()!=this)return;
			addonMove.setnotMove(false);
			
			if(getKit()==null){
				Log("kit == NULL");
			}else{
				for(Player player : getTeamList().keySet()){
					if(getKit()!=null){
						if(getKit().inventory!=null){
							player.getInventory().setArmorContents(getKit().inventory.getArmorContents());
							player.getInventory().setContents(getKit().inventory.getContents());
						}
					}
				}
			}
			setDamagePvP(true);
			setDamage(true);
			setState(GameState.InGame);
	}
	
	@EventHandler
	public void Move(MultiGamePlayerJoinEvent ev){
		if(ev.getGame()!=this)return;
		//Prüft ob dieser Spieler für die Arena angemeldet ist.
		if(getTeamList().containsKey(ev.getPlayer())){
			//Fügt Spieler zu AddonMove hinzu
			addonMove.getMovelist().add(ev.getPlayer());
		}
	}

}
