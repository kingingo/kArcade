package me.kingingo.karcade.Game.Multi.Games.Versus;

import java.io.File;

import lombok.Getter;
import lombok.Setter;
import me.kingingo.karcade.Game.Multi.MultiGames;
import me.kingingo.karcade.Game.Multi.Addons.MultiAddonMove;
import me.kingingo.karcade.Game.Multi.Addons.MultiGameArenaRestore;
import me.kingingo.karcade.Game.Multi.Addons.Evemts.MultiGameAddonChatEvent;
import me.kingingo.karcade.Game.Multi.Events.MultiGamePlayerJoinEvent;
import me.kingingo.karcade.Game.Multi.Events.MultiGameStartEvent;
import me.kingingo.karcade.Game.Multi.Events.MultiGameStateChangeEvent;
import me.kingingo.karcade.Game.Multi.Games.MultiTeamGame;
import me.kingingo.kcore.Arena.ArenaType;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.GameStateChangeReason;
import me.kingingo.kcore.Enum.PlayerState;
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
import me.kingingo.kcore.Util.UtilTime;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

public class Versus extends MultiTeamGame{
	
	@Getter
	@Setter
	private ArenaType max_type;
	@Getter
	private MultiAddonMove addonMove;
	private MultiGameArenaRestore area;
	private Scoreboard scoreboard;
	
	public Versus(MultiGames games,File file,Location location) {
		super(games,"",location);
		getWorldData().loadSchematic(this, location, file);
		UtilBG.setHub("versus");
		setUpdateTo("versus");
		
		if(!getWorldData().existLoc(this, Team.SHEEP_RED)||
				getWorldData().existLoc(this, Team.SHEEP_RED)&&getWorldData().getLocs(this, Team.SHEEP_RED).isEmpty()){
			Log("Fehler SHEEP_RED NICHT GEFUNDEN");
		}else if(!getWorldData().existLoc(this, Team.SHEEP_BLUE)||
				getWorldData().existLoc(this, Team.SHEEP_BLUE)&&getWorldData().getLocs(this, Team.SHEEP_BLUE).isEmpty()){
			Log("Fehler SHEEP_BLUE NICHT GEFUNDEN");
		}else{
			area=new MultiGameArenaRestore(this, getWorldData().getLocs(this, Team.SHEEP_RED).get(0).add(0, 1, 0), getWorldData().getLocs(this, Team.SHEEP_BLUE).get(0));
		}
		
		setDropItem(false);
		setPickItem(true);
		setDropItembydeath(false);
		setFoodlevelchange(false);
		addonMove=new MultiAddonMove(this);
		
		this.scoreboard=Bukkit.getScoreboardManager().getNewScoreboard();
		UtilScoreboard.addBoard(scoreboard, DisplaySlot.SIDEBAR, "§6§lVERSUS Board:");
		UtilScoreboard.setScore(scoreboard, "   ", DisplaySlot.SIDEBAR, 7);
		UtilScoreboard.setScore(scoreboard, "§cMap: ", DisplaySlot.SIDEBAR, 6);
		UtilScoreboard.setScore(scoreboard, "§7"+getMap(), DisplaySlot.SIDEBAR, 5);
		UtilScoreboard.setScore(scoreboard, " ", DisplaySlot.SIDEBAR, 4);
		UtilScoreboard.setScore(scoreboard, "§eKit: ", DisplaySlot.SIDEBAR, 3);
		UtilScoreboard.setScore(scoreboard, "  ", DisplaySlot.SIDEBAR, 1);
		UtilScoreboard.setScore(scoreboard, "§7----------------", DisplaySlot.SIDEBAR, 0);
		
		loadMaxTeam();
		setMax_type(ArenaType.withTeamAnzahl(getTeams()));
		
		if(getMax_type()==null){
			String s="";
			for(Team t : getWorldData().getTeams(this).keySet())s+=","+t.Name();
			UtilException.catchException("a"+getGames().getManager().getInstance().getConfig().getString("Config.Server.ID"), Bukkit.getServer().getIp(), getGames().getManager().getMysql(),"LOC:"+UtilLocation.getLocString(location)+"  MAP:"+getMap()+"SIZE:"+getWorldData().getTeams(this).size()+" "+s);
			UtilDebug.debug("MaxType", new String[]{"SIZE:"+getWorldData().getTeams(this).size(),s});
		}else{
			String s="";
			for(Team t : getWorldData().getTeams(this).keySet())s+=","+t.Name();
			for(Team t : getMax_type().getTeam()){
				if(!getWorldData().getTeams(this).containsKey(t)){
					UtilException.catchException("a"+getGames().getManager().getInstance().getConfig().getString("Config.Server.ID"), Bukkit.getServer().getIp(), getGames().getManager().getMysql(),"FEHLT        L:"+getMax_type().getTeam().length+"   "+"TEAM"+t.Name()+"   LOC:"+UtilLocation.getLocString(location)+"  MAP:"+getMap()+"SIZE:"+getWorldData().getTeams(this).size()+" "+s);
				}
			}
		}
	}
	
	@EventHandler
	public void Join(MultiGamePlayerJoinEvent ev){
		if(ev.getGame()!=this)return;
		//Prüft ob dieser Spieler für die Arena angemeldet ist.
		if(getTeamList().containsKey(ev.getPlayer())){
			//Spieler wird zu der Location des Teams teleportiert
			
			ev.getPlayer().teleport( getGames().getWorldData().getLocs(this, getTeamList().get(ev.getPlayer())).get(0).clone().add(0, 1, 0) );
			setTimer(-1);
			ev.setCancelled(true);
			updateInfo();
		}
	}
	
	@EventHandler
	public void chat(MultiGameAddonChatEvent ev){
		if(getGameList().getPlayers().containsKey(ev.getPlayer())){
			ev.setCancelled(true);
			
			for(Player player : getGameList().getPlayers().keySet())
				player.sendMessage(getTeam(ev.getPlayer()).getColor()+ev.getPlayer().getName()+"§8 » §7"+ev.getMessage());
		}
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
			this.addonMove.setMove(false);

			if(getKit()!=null){
				UtilScoreboard.resetScore(scoreboard, "§7"+getKit().kit, DisplaySlot.SIDEBAR);
			}else{
				UtilScoreboard.resetScore(scoreboard, "§7Default", DisplaySlot.SIDEBAR);
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
			this.addonMove.setMove(true);
			
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
				UtilScoreboard.setScore(scoreboard, "§7"+getKit().kit, DisplaySlot.SIDEBAR, 2);
			}else{
				UtilScoreboard.setScore(scoreboard, "§7Default", DisplaySlot.SIDEBAR, 2);
			}
			
			setTeamTab(scoreboard);
			setDamagePvP(true);
			setDamage(true);
			setState(GameState.InGame);
	}
}
