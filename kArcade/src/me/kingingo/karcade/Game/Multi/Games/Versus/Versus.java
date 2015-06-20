package me.kingingo.karcade.Game.Multi.Games.Versus;

import java.util.ArrayList;
import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;
import me.kingingo.karcade.Enum.GameStateChangeReason;
import me.kingingo.karcade.Enum.PlayerState;
import me.kingingo.karcade.Game.Multi.MultiGames;
import me.kingingo.karcade.Game.Multi.Events.MultiGamePlayerJoinEvent;
import me.kingingo.karcade.Game.Multi.Events.MultiGameStartEvent;
import me.kingingo.karcade.Game.Multi.Games.MultiGame;
import me.kingingo.karcade.Game.Single.addons.AddonMove;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.Team;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.StatsManager.Stats;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
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
	@Getter
	@Setter
	private VersusKit kit;
	
	public Versus(MultiGames games,String Map,Location location) {
		super(games);
		setMap(Map);
		
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
		
		addonMove=new AddonMove(games.getManager());
		addonMove.setnotMove(true);
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
				broadcast(Text.PREFIX_GAME.getText(getGames().getType().getTyp())+Text.KILL_BY.getText(new String[]{ ev.getEntity().getName() , ev.getEntity().getKiller().getName() }));
			}else{
				broadcast(Text.PREFIX_GAME.getText(getGames().getType().getTyp())+Text.DEATH.getText(new String[]{ ev.getEntity().getName() }));
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
					UtilDisplay.displayTextBar(p,Text.GAME_END_IN.getText(UtilTime.formatSeconds(getTimer())) );
				}
				
				if(getTimer()!=0){
					switch(getTimer()){
					case 300:broadcast(Text.PREFIX_GAME.getText(getGames().getType().getTyp())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getTimer())));break;
					case 120:broadcast(Text.PREFIX_GAME.getText(getGames().getType().getTyp())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getTimer())));break;
					case 90:broadcast(Text.PREFIX_GAME.getText(getGames().getType().getTyp())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getTimer())));break;
					case 60:broadcast(Text.PREFIX_GAME.getText(getGames().getType().getTyp())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getTimer())));break;
					case 30:broadcast(Text.PREFIX_GAME.getText(getGames().getType().getTyp())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getTimer())));break;
					case 15:broadcast(Text.PREFIX_GAME.getText(getGames().getType().getTyp())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getTimer())));break;
					case 10:broadcast(Text.PREFIX_GAME.getText(getGames().getType().getTyp())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getTimer())));break;
					case 3:broadcast(Text.PREFIX_GAME.getText(getGames().getType().getTyp())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getTimer())));break;
					case 2:broadcast(Text.PREFIX_GAME.getText(getGames().getType().getTyp())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getTimer())));break;
					case 1:broadcast(Text.PREFIX_GAME.getText(getGames().getType().getTyp())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getTimer())));break;
					}
				}else{
					broadcast(Text.PREFIX_GAME.getText(getGames().getType().getTyp())+Text.GAME_END.getText());
					setState(GameState.Restart,GameStateChangeReason.GAME_END);
				}
			}
		}
	}
	
	@EventHandler
	public void Start(MultiGameStartEvent ev){
		if(ev.getGame() == this){
			addonMove.setnotMove(false);
			
			for(Player player : getTeamList().keySet()){
				player.getInventory().setHelmet(kit.helm);
				player.getInventory().setChestplate(kit.chestplate);
				player.getInventory().setLeggings(kit.leggings);
				player.getInventory().setBoots(kit.boots);
				for(ItemStack item : kit.inv)player.getInventory().addItem(item);
			}
			
			setState(GameState.InGame);
		}
	}
	
	@EventHandler
	public void Move(MultiGamePlayerJoinEvent ev){
		//Prüft ob dieser Spieler für die Arena angemeldet ist.
		if(getTeamList().containsKey(ev.getPlayer())){
			//Fügt Spieler zu AddonMove hinzu
			addonMove.getMovelist().add(ev.getPlayer());
		}
	}

}
