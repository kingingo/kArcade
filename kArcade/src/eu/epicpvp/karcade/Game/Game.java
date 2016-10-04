package eu.epicpvp.karcade.Game;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

import dev.wolveringer.booster.BoosterType;
import dev.wolveringer.booster.NetworkBooster;
import eu.epicpvp.datenclient.client.Callback;
import eu.epicpvp.datenserver.definitions.dataserver.gamestats.GameState;
import eu.epicpvp.datenserver.definitions.dataserver.gamestats.GameType;
import eu.epicpvp.datenserver.definitions.dataserver.gamestats.StatsKey;
import eu.epicpvp.datenclient.event.EventListener;
import dev.wolveringer.events.Event;
import dev.wolveringer.events.EventConditions;
import dev.wolveringer.events.EventType;
import dev.wolveringer.events.booster.BoosterStatusChangeEvent;
import eu.epicpvp.karcade.ArcadeManager;
import eu.epicpvp.karcade.Game.Events.GameStartEvent;
import eu.epicpvp.karcade.Game.Events.GameStateChangeEvent;
import eu.epicpvp.karcade.Game.Multi.MultiGames;
import eu.epicpvp.karcade.Game.Single.SingleGame;
import eu.epicpvp.karcade.Game.World.Event.WorldDataInitializeEvent;
import eu.epicpvp.kcore.Addons.AddonDay;
import eu.epicpvp.kcore.Enum.GameStateChangeReason;
import eu.epicpvp.kcore.Events.ServerStatusUpdateEvent;
import eu.epicpvp.kcore.Listener.kListener;
import eu.epicpvp.kcore.Scoreboard.Events.PlayerSetScoreboardEvent;
import eu.epicpvp.kcore.StatsManager.StatsManager;
import eu.epicpvp.kcore.StatsManager.StatsManagerRepository;
import eu.epicpvp.kcore.StatsManager.Event.PlayerStatsAddEvent;
import eu.epicpvp.kcore.Translation.TranslationHandler;
import eu.epicpvp.kcore.Util.UtilEvent;
import eu.epicpvp.kcore.Util.UtilEvent.ActionType;
import eu.epicpvp.kcore.Util.UtilNumber;
import eu.epicpvp.kcore.Util.UtilPlayer;
import eu.epicpvp.kcore.Util.UtilServer;
import lombok.Getter;
import lombok.Setter;

public class Game extends kListener {

	@Getter
	private ArcadeManager manager;
	@Getter
	@Setter
	private String packetServer = "hub";
	@Getter
	private StatsManager money;
	@Getter
	@Setter
	private Scoreboard scoreboard;
	@Getter
	private StatsManager stats;
	private GameType typ = GameType.NONE;
	@Getter
	private GameState state = GameState.NONE;
	@Getter
	@Setter
	private int minPlayers = 1;
	@Getter
	@Setter
	private int maxPlayers = 50;
	@Setter
	@Getter
	private boolean apublic = true;
	@Getter
	@Setter
	private boolean set_default_scoreboard = true;
	@Getter
	@Setter
	private NetworkBooster booster;

	public Game(ArcadeManager manager) {
		super(manager.getInstance(), "Game");
		this.manager = manager;
		this.money = StatsManagerRepository.createStatsManager(GameType.Money);
		UtilServer.getClient().getHandle().getEventManager().getEventManager(EventType.BOOSTER_SWITCH).setConditionEnables(EventConditions.BOOSTER_TYPE, true);
		UtilServer.getClient().getHandle().getEventManager().registerListener(new EventListener() {

			@Override
			public void fireEvent(Event e) {
				if (e instanceof BoosterStatusChangeEvent) {
					BoosterStatusChangeEvent ev = (BoosterStatusChangeEvent) e;

					if (ev.getBoosterType() == BoosterType.ARCADE) {
						checkNetworkBooster();
					}
				}
			}
		});
	}

	public void checkNetworkBooster() {
		if (booster != null && !booster.isActive()) {
			UtilServer.getClient().getNetworkBooster(BoosterType.ARCADE).getAsync(new Callback<NetworkBooster>() {

				@Override
				public void call(NetworkBooster b, Throwable exception) {
					if (b != null && b.isActive()) {
						booster = b;
					}
				}
			});
		}
	}

	public String shortMap(String map, String t) {
		if ((map + t).length() > 16) {
			return map.substring(0, (16 - (t.length() + 1))) + t;
		}
		return map + t;
	}

	public void setStats() {
		if (stats == null) {
			this.stats = StatsManagerRepository.getStatsManager(getType());
		}
	}

	public void setStats(GameType type) {
		if (stats == null) {
			setTyp(type);
			setStats();
		}
	}

	public void setTyp(GameType typ) {
		this.typ = typ;
		setStats();
	}

	public boolean isType(GameType gt) {
		return typ == gt;
	}

	public GameType getType() {
		return this.typ;
	}

	public boolean isState(GameState gs) {
		return gs == getState();
	}

	public boolean setState(GameState gs) {
		return setState(gs, GameStateChangeReason.CUSTOM);
	}

	public boolean setState(GameState gs, GameStateChangeReason reason) {
		if (gs == getState())
			return false;
		GameStateChangeEvent stateEvent = new GameStateChangeEvent(state, gs, this, reason);
		Bukkit.getPluginManager().callEvent(stateEvent);
		if (stateEvent.isCancelled())
			return false;
		state = gs;
		System.out.println("[" + getType().getTyp() + "] GameState wurde zu " + state.string() + " geändert.");
		return true;
	}

	public void updateInfo() {
		if (getManager().getClient().getHandle().isConnected()) {
			getManager().getClient().updateServerStats();
		} else {
			logMessage("Client ist not connected!?");
		}
	}

	public void broadcastWithPrefix(String name, Object input) {
		for (Player player : UtilServer.getPlayers())
			player.sendMessage(TranslationHandler.getText(player, "PREFIX_GAME", getType().getTyp()) + TranslationHandler.getText(player, name, input));
	}

	public void broadcastWithPrefix(String name, Object[] input) {
		for (Player player : UtilServer.getPlayers())
			player.sendMessage(TranslationHandler.getText(player, "PREFIX_GAME", getType().getTyp()) + TranslationHandler.getText(player, name, input));
	}

	public void broadcastWithPrefixName(String name) {
		for (Player player : UtilServer.getPlayers())
			player.sendMessage(TranslationHandler.getText(player, "PREFIX_GAME", getType().getTyp()) + TranslationHandler.getText(player, name));
	}

	public void broadcastWithPrefix(String msg) {
		for (Player player : UtilServer.getPlayers())
			player.sendMessage(TranslationHandler.getText(player, "PREFIX_GAME", getType().getTyp()) + msg);
	}

	public void broadcastName(String name) {
		for (Player player : UtilServer.getPlayers())
			player.sendMessage(TranslationHandler.getText(player, name));
	}

	public void broadcast(String msg) {
		for (Player player : UtilServer.getPlayers())
			player.sendMessage(msg);
	}

	public void broadcast(String name, Object[] input) {
		for (Player player : UtilServer.getPlayers())
			player.sendMessage(TranslationHandler.getText(player, name, input));
	}

	public void broadcast(String name, Object input) {
		for (Player player : UtilServer.getPlayers())
			player.sendMessage(TranslationHandler.getText(player, name, input));
	}

	@EventHandler
	public void booster(PlayerStatsAddEvent ev) {
		if (booster != null && booster.isActive() && ev.getStats() == StatsKey.COINS) {
			if (ev.getValue() instanceof Integer) {
				int coins = UtilNumber.toInt(ev.getValue());

				if (coins > 0) {
					ev.setValue(coins * 2);
				}
			}
		}
	}

	@EventHandler
	public void PlayerBedEnter(PlayerBedEnterEvent ev) {
		ev.setCancelled(true);
	}

	@EventHandler
	public void OpenChest(PlayerInteractEvent ev) {
		if (getState() == GameState.LobbyPhase && ev.getPlayer().getWorld().getUID() == getManager().getLobby().getWorld().getUID()) {
			if (UtilEvent.isAction(ev, ActionType.RIGHT_BLOCK)) {
				if (ev.getClickedBlock().getType() == Material.BREWING_STAND || ev.getClickedBlock().getType() == Material.DROPPER || ev.getClickedBlock().getType() == Material.DISPENSER || ev.getClickedBlock().getType() == Material.ANVIL || ev.getClickedBlock().getType() == Material.ENCHANTMENT_TABLE || ev.getClickedBlock().getType() == Material.TRAP_DOOR || ev.getClickedBlock().getType() == Material.WORKBENCH || ev.getClickedBlock().getType() == Material.FURNACE || ev.getClickedBlock().getType() == Material.ENDER_CHEST || ev.getClickedBlock().getType() == Material.CHEST)
					ev.setCancelled(true);
			}
		}
	}

	@EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
	public void Board(PlayerSetScoreboardEvent ev) {
		if (isSet_default_scoreboard())
			UtilPlayer.setScoreboardGemsAndCoins(ev.getPlayer(), getMoney());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void Start(GameStartEvent ev) {
		for (Player player : UtilServer.getPlayers()) {
			if (player.getScoreboard() != null) {
				if (player.getScoreboard().getObjective(DisplaySlot.SIDEBAR) != null) {
					player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).unregister();
				}
			}
		}
	}

	@EventHandler
	public void statusUpdate(ServerStatusUpdateEvent ev) {
		ev.getPacket().setTyp(getType());
		ev.getPacket().setMaxPlayers(getMaxPlayers());
		ev.getPacket().setPlayers(UtilServer.getPlayers().size());
		ev.getPacket().setState(getState());
	}

	@EventHandler
	public void load(WorldDataInitializeEvent ev) {
		new AddonDay(getManager().getInstance(), ev.getWorldData().getWorld());
	}

	@EventHandler
	public void move(PlayerMoveEvent ev) {
		if (getState() == GameState.LobbyPhase && ev.getPlayer().getWorld().getUID() == getManager().getLobby().getWorld().getUID() && !ev.getPlayer().isOnGround()) {
			if (ev.getPlayer().getLocation().getY() < (getManager().getLobby().getY() - 25)) {
				ev.getPlayer().teleport(getManager().getLobby());
			}
		}
	}

	@EventHandler
	public void Start(GameStateChangeEvent ev) {
		updateInfo();
	}

	@EventHandler
	public void Q(PlayerQuitEvent ev) {
		ev.setQuitMessage(null);
		updateInfo();
		if (this instanceof SingleGame && isState(GameState.LobbyPhase))
			broadcastWithPrefix("GAME_LEAVE", ev.getPlayer().getName());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void loadStats(PlayerJoinEvent ev) {
		getStats().loadPlayer(ev.getPlayer());
		getMoney().loadPlayer(ev.getPlayer());
	}

	@EventHandler
	public void J(PlayerJoinEvent ev) {
		ev.setJoinMessage(null);
		Bukkit.getWorld("world").setWeatherDuration(0);
		Bukkit.getWorld("world").setStorm(false);
		if (isState(GameState.LobbyPhase) || this instanceof MultiGames)
			updateInfo();
		if (this instanceof SingleGame && getState() == GameState.LobbyPhase)
			broadcastWithPrefix("GAME_ENTER", new String[] { ev.getPlayer().getName(), String.valueOf(UtilServer.getPlayers().size()), String.valueOf(getMaxPlayers()) });
	}

}
