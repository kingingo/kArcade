package eu.epicpvp.karcade.Game.Multi.Games.Versus;

import java.io.File;

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

import eu.epicpvp.datenserver.definitions.dataserver.gamestats.GameState;
import eu.epicpvp.datenserver.definitions.dataserver.gamestats.GameType;
import eu.epicpvp.datenserver.definitions.dataserver.gamestats.StatsKey;
import eu.epicpvp.karcade.Game.Multi.MultiGames;
import eu.epicpvp.karcade.Game.Multi.Addons.MultiAddonMove;
import eu.epicpvp.karcade.Game.Multi.Addons.MultiGameArenaRestore;
import eu.epicpvp.karcade.Game.Multi.Addons.Evemts.MultiGameAddonChatEvent;
import eu.epicpvp.karcade.Game.Multi.Events.MultiGamePlayerJoinEvent;
import eu.epicpvp.karcade.Game.Multi.Events.MultiGameStartEvent;
import eu.epicpvp.karcade.Game.Multi.Events.MultiGameStateChangeEvent;
import eu.epicpvp.karcade.Game.Multi.Games.MultiTeamGame;
import eu.epicpvp.kcore.Arena.ArenaType;
import eu.epicpvp.kcore.Enum.GameStateChangeReason;
import eu.epicpvp.kcore.Enum.PlayerState;
import eu.epicpvp.kcore.Enum.Team;
import eu.epicpvp.kcore.Packets.PacketArenaSettings;
import eu.epicpvp.kcore.StatsManager.Event.PlayerStatsLoadedEvent;
import eu.epicpvp.kcore.Translation.TranslationHandler;
import eu.epicpvp.kcore.Update.UpdateType;
import eu.epicpvp.kcore.Update.Event.UpdateEvent;
import eu.epicpvp.kcore.Util.UtilBG;
import eu.epicpvp.kcore.Util.UtilDebug;
import eu.epicpvp.kcore.Util.UtilDisplay;
import eu.epicpvp.kcore.Util.UtilException;
import eu.epicpvp.kcore.Util.UtilLocation;
import eu.epicpvp.kcore.Util.UtilPlayer;
import eu.epicpvp.kcore.Util.UtilScoreboard;
import eu.epicpvp.kcore.Util.UtilTime;
import eu.epicpvp.kcore.Versus.PlayerKit;
import lombok.Getter;
import lombok.Setter;

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

		if(!getWorldData().existLoc(this, Team.SHEEP_RED)||
				getWorldData().existLoc(this, Team.SHEEP_RED)&&getWorldData().getLocs(this, Team.SHEEP_RED).isEmpty()){
			logMessage("Fehler SHEEP_RED NICHT GEFUNDEN");
		}else if(!getWorldData().existLoc(this, Team.SHEEP_BLUE)||
				getWorldData().existLoc(this, Team.SHEEP_BLUE)&&getWorldData().getLocs(this, Team.SHEEP_BLUE).isEmpty()){
			logMessage("Fehler SHEEP_BLUE NICHT GEFUNDEN");
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
			for(Team t : getWorldData().getTeams(this).keySet())s+=","+t.getDisplayName();
			UtilException.catchException("a"+getGames().getManager().getInstance().getConfig().getString("Config.Server.ID"), Bukkit.getServer().getIp(), getGames().getManager().getMysql(),"LOC:"+UtilLocation.getLocString(location)+"  MAP:"+getMap()+"SIZE:"+getWorldData().getTeams(this).size()+" "+s);
			UtilDebug.debug("MaxType", new String[]{"SIZE:"+getWorldData().getTeams(this).size(),s});
		}else{
			String s="";
			for(Team t : getWorldData().getTeams(this).keySet())s+=","+t.getDisplayName();
			for(Team t : getMax_type().getTeam()){
				if(!getWorldData().getTeams(this).containsKey(t)){
					UtilException.catchException("a"+getGames().getManager().getInstance().getConfig().getString("Config.Server.ID"), Bukkit.getServer().getIp(), getGames().getManager().getMysql(),"FEHLT        L:"+getMax_type().getTeam().length+"   "+"TEAM"+t.getDisplayName()+"   LOC:"+UtilLocation.getLocString(location)+"  MAP:"+getMap()+"SIZE:"+getWorldData().getTeams(this).size()+" "+s);
				}
			}
		}

		setKit(new PlayerKit());
	}

	public void setSettings(PacketArenaSettings settings){
		if(settings.getTeam()==Team.SOLO){
			settings.setTeam(littleTeam());
		}

//		if(UtilPlayer.isOnline(settings.getPlayer())){
//			getGameList().addPlayer(Bukkit.getPlayer(settings.getPlayer()), PlayerState.IN);
//			getTeamList().put(Bukkit.getPlayer(settings.getPlayer()), settings.getTeam());
//			MultiGamePlayerJoinEvent event=new MultiGamePlayerJoinEvent(Bukkit.getPlayer(settings.getPlayer()),this);
//			Bukkit.getPluginManager().callEvent(event);
//		}else{
			getGames().getWarte_liste().put(settings.getPlayer(), settings);
//		}

		setMax_team(settings.getMax_team());
		setMin_team(settings.getMin_team());

		if(getKit().kit == null || getKit().kit.equalsIgnoreCase(settings.getKit())){
			getKit().kit = settings.getKit();
			getKit().id = -1;
		}

		setType(settings.getType());
		setState(GameState.Laden);
	}

	@EventHandler
	public void Join(MultiGamePlayerJoinEvent ev){
		if(ev.getGame()!=this)return;
		//Prüft ob dieser Spieler für die Arena angemeldet ist.
		if(getTeamList().containsKey(ev.getPlayer())){
			//Spieler wird zu der Location des Teams teleportiert
			logMessage("TELEPORT: "+ev.getPlayer().getName());
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
				player.sendMessage(getTeam(ev.getPlayer()).getColor()+ev.getPlayer().getName()+"§8 § §7"+ev.getMessage());
		}
	}

	@EventHandler
	public void Death(PlayerDeathEvent ev){
		if(getTeamList().containsKey(ev.getEntity())){
			UtilPlayer.RespawnNow(ev.getEntity(), getGames().getManager().getInstance());
			getGameList().getPlayers().remove(ev.getEntity());
			getGameList().getPlayers().put(ev.getEntity(), PlayerState.SPECTATOR);
			getGames().getStats().addInt(ev.getEntity(), 1, StatsKey.DEATHS);
			getGames().getStats().addInt(ev.getEntity(), 1, StatsKey.LOSE);

			if(ev.getEntity().getKiller()!=null){

				getGames().getStats().addInt(ev.getEntity().getKiller(), 1, StatsKey.KILLS);
				getGames().getMoney().addInt(ev.getEntity().getKiller(),4,StatsKey.COINS);
				broadcastWithPrefix("KILL_BY", new String[]{ ev.getEntity().getName() , ev.getEntity().getKiller().getName() });
				ev.getEntity().sendMessage(TranslationHandler.getText(ev.getEntity(),"PREFIX_GAME",getGames().getType().getTyp())+TranslationHandler.getText(ev.getEntity(), "HEART",new String[]{ev.getEntity().getKiller().getName(),UtilPlayer.getHealthBar(ev.getEntity().getKiller())}));
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
					UtilDisplay.displayTextBar(p,TranslationHandler.getText(p, "GAME_END_IN",UtilTime.formatSeconds(getTimer())));
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
					broadcastWithPrefix(TranslationHandler.getText("GAME_END"));
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
			UtilScoreboard.resetScore(scoreboard, 2, DisplaySlot.SIDEBAR);

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
	public void loadStats(PlayerStatsLoadedEvent ev){
		if(ev.getManager().getType()==GameType.Money)return;
		if(getKit()==null || getKit().kit==null)return;

		if(UtilPlayer.isOnline(ev.getPlayerId())){
			Player player = UtilPlayer.searchExact(ev.getPlayerId());

			if(player.getName().equalsIgnoreCase(getKit().kit)){
				setKit( getGames().getKitManager().getKit(ev.getPlayerId(), getGames().getStats().getInt(StatsKey.KIT_ID, player)) );
				if(getKit()!=null)getKit().kit=player.getName();
			}
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
			setKit(new PlayerKit());
			setState(GameState.InGame);
	}
}
