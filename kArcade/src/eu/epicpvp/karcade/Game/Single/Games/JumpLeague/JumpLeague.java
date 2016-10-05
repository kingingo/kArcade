package eu.epicpvp.karcade.Game.Single.Games.JumpLeague;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.DisplaySlot;

import eu.epicpvp.datenserver.definitions.dataserver.gamestats.GameState;
import eu.epicpvp.datenserver.definitions.dataserver.gamestats.GameType;
import eu.epicpvp.datenserver.definitions.dataserver.gamestats.StatsKey;
import eu.epicpvp.karcade.ArcadeManager;
import eu.epicpvp.karcade.kArcade;
import eu.epicpvp.karcade.Events.RankingEvent;
import eu.epicpvp.karcade.Game.Events.GameStartEvent;
import eu.epicpvp.karcade.Game.Events.GameStateChangeEvent;
import eu.epicpvp.karcade.Game.Single.SingleWorldData;
import eu.epicpvp.karcade.Game.Single.Addons.AddonMove;
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
import eu.epicpvp.kcore.Util.Title;
import eu.epicpvp.kcore.Util.UtilDisplay;
import eu.epicpvp.kcore.Util.UtilMath;
import eu.epicpvp.kcore.Util.UtilPlayer;
import eu.epicpvp.kcore.Util.UtilScoreboard;
import eu.epicpvp.kcore.Util.UtilServer;
import eu.epicpvp.kcore.Util.UtilString;

public class JumpLeague extends SoloGame {

	private AddonMove move;

	public JumpLeague(ArcadeManager manager) {
		super(manager);
		long t = System.currentTimeMillis();
		setTyp(GameType.QuickSurvivalGames); //später mal ändern wenn die enum geupdated wurde
		setWorldData(new SingleWorldData(manager, getType()));
		getWorldData().Initialize();
		setMinPlayers(6);
		setMaxPlayers(16);
		setExplosion(false);
		setDeathDropItems(false);
		setFoodChange(false);
		setDamageSelf(false);
		setDamage(true);
		setDamagePvE(false);
		setDamagePvP(false);
		setItemDrop(false);
		setItemPickup(false);
		setCreatureSpawn(false);
		setBlockBreak(false);
		setBlockPlace(false);
	}
	// RED WOOL = SPAWN PLAYER!

	@EventHandler
	public void Ranking(RankingEvent ev) {
		getManager().setRanking(StatsKey.WIN);
	}

	@EventHandler
	public void Interact(PlayerInteractEvent ev) {
		if (getState() != GameState.InGame) {
			return;
		}
		if (getGameList().getPlayers(PlayerState.SPECTATOR).contains(ev.getPlayer())) {
			return;
		}
		//hier item interacts einfügen

	}

	@EventHandler
	public void Start(GameStartEvent ev) { //initial game start
		long time = System.currentTimeMillis();
		ArrayList<Location> list = new ArrayList<>(getWorldData().getSpawnLocations(Team.RED));
		int r;

		setScoreboard(getManager().getPermManager().getScoreboard());
		UtilScoreboard.addBoard(getScoreboard(), DisplaySlot.SIDEBAR, "§6§l" + getType().getShortName() + " Players:");
		Title title = new Title("", "");
		for (Player p : UtilServer.getPlayers()) {
			if (list.isEmpty()) {
				r = 0;
				list.add(getWorldData().getSpawnLocations(Team.RED).get(0)); //team rot ist wohl das team was genutzt wird, wenn es sonst keine teams gibt
			} else {
				r = UtilMath.randomInteger(list.size());
			}
			getManager().clear(p);
			getGameList().addPlayer(p, PlayerState.INGAME);
			p.teleport(list.get(r));
			list.remove(r);
			UtilScoreboard.setScore(getScoreboard(), p.getName(), DisplaySlot.SIDEBAR, 0); //joa spielername
			p.setScoreboard(getScoreboard());
			title.setSubtitle(TranslationHandler.getText(p, "NO_TEAMS_ALLOWED"));
			title.send(p);
		}
		move = new AddonMove(getManager());
		new AddonDay(getManager().getInstance(), getWorldData().getWorld());
		move.setMove(false);
		setState(GameState.StartGame);

		for (Entity e : getWorldData().getWorld().getEntities()) {
			if (!(e instanceof Player)) {
				e.remove();
			}
		}
		getManager().DebugLog(time, this.getClass().getName());
	}

	@EventHandler
	public void Start_Game(UpdateEvent ev) { //game start countdown nach initial gamestart
		if (ev.getType() != UpdateType.SEC) {
			return;
		}
		if (getState() != GameState.StartGame) {
			return;
		}
		if (getStart() < 0) {
			setStart(11);
		}
		setStart(getStart() - 1);
		for (Player p : UtilServer.getPlayers()) {
			UtilDisplay.displayTextBar(p, TranslationHandler.getText("GAME_START_IN", getStart()));
		}
		switch (getStart()) {
			case 10:
				setDamage(false);
				broadcastWithPrefix("GAME_START_IN", getStart());
				new Title("", TranslationHandler.getText("GAME_START_IN", getStart())).broadcast();
				break;
			case 9:
				for (Entity e : getWorldData().getWorld().getEntities()) {
					if (!(e instanceof Player)) {
						e.remove();
					}
				}
				break;
			case 5:
				broadcastWithPrefix("GAME_START_IN", getStart());
				break;
			case 4:
				broadcastWithPrefix("GAME_START_IN", getStart());
				break;
			case 3:
				broadcastWithPrefix("GAME_START_IN", getStart());
				new Title("", TranslationHandler.getText("GAME_START_IN", getStart())).broadcast();
				break;
			case 2:
				broadcastWithPrefix("GAME_START_IN", getStart());
				new Title("", TranslationHandler.getText("GAME_START_IN", getStart())).broadcast();
				break;
			case 1:
				broadcastWithPrefix("GAME_START_IN", getStart());
				new Title("", TranslationHandler.getText("GAME_START_IN", getStart())).broadcast();
				break;
			case 0:
				new Title("", TranslationHandler.getText("GAME_START_IN", getStart())).broadcast();
				setStart(1831);//zeit bis zum ende
				setState(GameState.InGame);
				broadcastWithPrefixName("GAME_START");
				move.setMove(true);
				//hier die items geben zb
				break;
		}
	}

	@EventHandler
	public void inGame(UpdateEvent ev) {
		if (ev.getType() != UpdateType.SEC) {
			return;
		}
		if (getState() != GameState.InGame) {
			return;
		}
		setStart(getStart() - 1);
		switch (getStart()) {
			case 5:
				broadcastWithPrefix("GAME_END_IN", getStart());
				break;
			case 4:
				broadcastWithPrefix("GAME_END_IN", getStart());
				break;
			case 3:
				broadcastWithPrefix("GAME_END_IN", getStart());
				break;
			case 2:
				broadcastWithPrefix("GAME_END_IN", getStart());
				break;
			case 1:
				broadcastWithPrefix("GAME_END_IN", getStart());
				break;
			case 0:
				//hier ne nachricht senden das es vorbei ist
				setState(GameState.Restart);
				break;
		}
	}

	@EventHandler
	public void Death(PlayerDeathEvent ev) {
		//naja denke mal zurückporten gehört hier rein + nen fail counter erhöhen wie wenn man das item klickt - da kann man auch ne extramethode für machen
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerChatd(AsyncPlayerChatEvent event) {//joa einfach so lassen
		if (!event.isCancelled()) {

			if ((!event.getPlayer().hasPermission(PermissionType.CHAT_LINK.getPermissionToString())) && UtilString.isBadWord(event.getMessage()) || UtilString.checkForIP(event.getMessage())) {
				event.setMessage("Ich heul rum!");
				event.getPlayer().sendMessage(TranslationHandler.getText(event.getPlayer(), "PREFIX") + TranslationHandler.getText(event.getPlayer(), "CHAT_MESSAGE_BLOCK"));
			}

			Player p = event.getPlayer();
			String msg = event.getMessage();
			msg = msg.replaceAll("%", "");
			if (getManager().getPermManager().hasPermission(p, PermissionType.ALL_PERMISSION)) {
				msg = msg.replaceAll("&", "§");
			}
			event.setFormat(getManager().getPermManager().getPrefix(p) + p.getName() + "§7:§7 " + msg);
		}
	}

	@EventHandler
	public void QuitSc(PlayerQuitEvent ev) {
		if (getScoreboard() == null) {
			return;
		}
		//aus dem scoreboard entfernen
	}

	@EventHandler
	public void StatsLoaded(PlayerStatsLoadedEvent ev) { //man müsste die stats anpassen etwas evtl später
		if (ev.getManager().getType() != getType()) {
			return;
		}
		if (getState() != GameState.LobbyPhase) {
			return;
		}
		if (UtilPlayer.isOnline(ev.getPlayerId())) {
			Player player = UtilPlayer.searchExact(ev.getPlayerId());
			int win = getStats().getInt(StatsKey.WIN, player);
			int lose = getStats().getInt(StatsKey.LOSE, player);

			Bukkit.getScheduler().runTask(getManager().getInstance(), new Runnable() {

				@Override
				public void run() {

					getManager().getHologram().sendText(player, getManager().getLoc_stats(), new String[]{Color.GREEN + getType().getTyp() + Color.ORANGE + "§l Info", TranslationHandler.getText(player, "GAME_HOLOGRAM_SERVER", getType().getTyp() + " §a" + kArcade.id), TranslationHandler.getText(player, "GAME_HOLOGRAM_MAP", getWorldData().getMapName()), " ", TranslationHandler.getText(player, "GAME_HOLOGRAM_STATS", getType().getTyp()), TranslationHandler.getText(player, "GAME_HOLOGRAM_KILLS", getStats().getInt(StatsKey.KILLS, player)), TranslationHandler.getText(player, "GAME_HOLOGRAM_DEATHS", getStats().getInt(StatsKey.DEATHS, player)), " ", TranslationHandler.getText(player, "GAME_HOLOGRAM_GAMES", (win + lose)), TranslationHandler.getText(player, "GAME_HOLOGRAM_WINS", win), TranslationHandler.getText(player, "GAME_HOLOGRAM_LOSE", lose),});
				}
			});
		}
	}

	@EventHandler
	public void onBreak(BlockBreakEvent ev) {
		if (!ev.getPlayer().isOp()) {
			ev.setCancelled(true);
		}
	}

	@EventHandler
	public void ChangeState(GameStateChangeEvent ev) { //joa musst du auch mal schauen wegen stats und so
		if (ev.getTo() == GameState.Restart) {
			ArrayList<Player> list = getGameList().getPlayers(PlayerState.INGAME);
			if (list.size() == 1) {
				Player p = list.get(0);
				getStats().addInt(p, 1, StatsKey.WIN);
				getMoney().add(p, StatsKey.COINS, 20);
				broadcastWithPrefix("GAME_WIN", p.getName());
			}
		}
	}
}
