package eu.epicpvp.karcade.Game.Single;

import java.util.ArrayList;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;

import dev.wolveringer.dataserver.gamestats.GameState;
import eu.epicpvp.karcade.kArcade;
import eu.epicpvp.karcade.kArcadeManager;
import eu.epicpvp.karcade.Game.Game;
import eu.epicpvp.karcade.Game.GameList;
import eu.epicpvp.karcade.Game.Events.GamePreStartEvent;
import eu.epicpvp.karcade.Game.Events.GameStartEvent;
import eu.epicpvp.karcade.Game.Events.GameStateChangeEvent;
import eu.epicpvp.karcade.Game.Single.Addons.AddonSpecCompass;
import eu.epicpvp.kcore.Enum.PlayerState;
import eu.epicpvp.kcore.Events.ServerStatusUpdateEvent;
import eu.epicpvp.kcore.Permission.PermissionType;
import eu.epicpvp.kcore.Permission.Events.PlayerLoadPermissionEvent;
import eu.epicpvp.kcore.Translation.TranslationHandler;
import eu.epicpvp.kcore.Update.UpdateType;
import eu.epicpvp.kcore.Update.Event.UpdateEvent;
import eu.epicpvp.kcore.Util.Color;
import eu.epicpvp.kcore.Util.TimeSpan;
import eu.epicpvp.kcore.Util.UtilBG;
import eu.epicpvp.kcore.Util.UtilDisplay;
import eu.epicpvp.kcore.Util.UtilEvent;
import eu.epicpvp.kcore.Util.UtilEvent.ActionType;
import eu.epicpvp.kcore.Util.UtilPlayer;
import eu.epicpvp.kcore.Util.UtilServer;
import lombok.Getter;
import lombok.Setter;

public class SingleGame extends Game {
	@Getter
	@Setter
	public boolean Damage = true;
	@Setter
	@Getter
	public boolean ProjectileDamage = true;
	@Getter
	@Setter
	public boolean DamagePvP = true;
	@Getter
	@Setter
	public boolean DamagePvE = true;
	@Getter
	@Setter
	public boolean DamageEvP = true;
	@Getter
	@Setter
	public boolean DamageSelf = true;
	@Getter
	@Setter
	public boolean DamageTeamSelf = false;
	@Getter
	@Setter
	public boolean DamageTeamOther = true;
	@Getter
	@Setter
	public boolean HangingBreak = false;
	@Getter
	@Setter
	public boolean CompassAddon = false;
	@Getter
	@Setter
	int start = -1;
	@Getter
	@Setter
	private boolean Explosion = true;
	@Getter
	@Setter
	private boolean Respawn = false;
	@Getter
	@Setter
	private double respawnTime = 0.0D;
	@Getter
	@Setter
	public boolean BlockBreak = false;
	@Getter
	public HashSet<Material> BlockBreakAllow = new HashSet<>();
	@Getter
	public HashSet<Material> BlockBreakDeny = new HashSet<>();
	@Getter
	@Setter
	public boolean BlockPlace = false;
	@Getter
	public HashSet<Material> BlockPlaceAllow = new HashSet<>();
	@Getter
	public HashSet<Material> BlockPlaceDeny = new HashSet<>();
	@Getter
	@Setter
	public boolean ItemPickup = false;
	@Getter
	public HashSet<Integer> ItemPickupAllow = new HashSet<>();
	@Getter
	public HashSet<Integer> ItemPickupDeny = new HashSet<>();
	@Getter
	@Setter
	public boolean ItemDrop = false;
	@Getter
	public HashSet<Integer> ItemDropAllow = new HashSet<>();
	@Getter
	public HashSet<Integer> ItemDropDeny = new HashSet<>();
	@Getter
	@Setter
	public boolean BlockBurn = false;
	@Getter
	@Setter
	public boolean BlockSpread = false;
	@Getter
	@Setter
	public boolean BlackFade = false;
	@Getter
	@Setter
	public boolean LeavesDecay = false;
	@Getter
	@Setter
	public boolean CreatureSpawn = false;
	@Getter
	public ArrayList<CreatureType> AllowSpawnCreature = new ArrayList<>();
	@Getter
	@Setter
	public String Winner = "nobody";
	@Getter
	@Setter
	public boolean DeathDropItems = false;
	@Getter
	@Setter
	private AddonSpecCompass compass;
	@Getter
	@Setter
	private boolean Replace_Water = false;
	@Getter
	@Setter
	private boolean Replace_Lava = false;
	@Getter
	@Setter
	private boolean Replace_Fire = false;
	@Getter
	private ArrayList<Material> InteractDeny = new ArrayList<>();
	@Setter
	@Getter
	private boolean FoodChange = false;
	@Setter
	@Getter
	private boolean solid = false;
	@Getter
	@Setter
	private boolean PlayerShearEntity = false;
	@Getter
	@Setter
	private boolean InventoryTyp = true;
	@Getter
	private ArrayList<InventoryType> InventoryTypDisallow = new ArrayList<>();
	@Getter
	private ArrayList<DamageCause> EntityDamage = new ArrayList<>();
	@Getter
	@Setter
	private boolean soilChange = false;
	@Getter
	private GameList gameList;
	@Setter
	@Getter
	private SingleWorldData worldData;

	public SingleGame(kArcadeManager manager) {
		super(manager);
		this.gameList = new GameList(getManager());
	}

	@EventHandler
	public void soilChangeEntity(EntityInteractEvent event) {
		if (!isSoilChange() && (event.getEntityType() != EntityType.PLAYER)
				&& (event.getBlock().getType() == Material.SOIL))
			event.setCancelled(true);
	}

	@EventHandler
	public void PlayerShearEntity(PlayerShearEntityEvent ev) {
		if (PlayerShearEntity)
			ev.setCancelled(true);
	}

	@EventHandler
	public void OpenInventory(InventoryOpenEvent ev) {
		if (!InventoryTyp)
			ev.setCancelled(true);
		if (!InventoryTypDisallow.isEmpty() && InventoryTypDisallow.contains(ev.getInventory().getType())) {
			ev.setCancelled(true);
		}
	}

	@EventHandler
	public void singlestatusUpdate(ServerStatusUpdateEvent ev) {
		if (getWorldData() != null)
			ev.getPacket().setMots((getWorldData().getMap() == null ? "Loading..." : getWorldData().getMapName()));
	}

	@EventHandler
	public void InterBack(PlayerInteractEvent ev) {
		if (UtilEvent.isAction(ev, ActionType.R) && getGameList().getPlayers().containsKey(ev.getPlayer())
				&& getGameList().getPlayers().get(ev.getPlayer()) == PlayerState.OUT) {
			if (ev.getPlayer().getItemInHand() == null)
				return;
			if (ev.getPlayer().getItemInHand().getTypeId() != 385)
				return;
			UtilBG.sendToServer(ev.getPlayer(), getManager().getInstance());
			ev.setCancelled(true);
		}
	}

	@EventHandler
	public void QuitPlayerListener(PlayerQuitEvent ev) {
		ev.setQuitMessage(null);
		if (getState() == GameState.Restart)
			return;
		getStats().SaveAllPlayerData(ev.getPlayer());
	}

	@EventHandler
	public void Damage(EntityDamageEvent ev) {
		if (ev.getEntity() instanceof Player
				&& getGameList().getPlayers(PlayerState.OUT).contains((Player) ev.getEntity()))
			ev.setCancelled(true);
		if (isState(GameState.LobbyPhase))
			ev.setCancelled(true);
		if (!Damage)
			ev.setCancelled(true);
		if (EntityDamage.contains(ev.getCause())) {
			ev.setCancelled(true);
		}
	}

	@EventHandler
	public void Food(FoodLevelChangeEvent ev) {
		if (ev.getEntity() instanceof Player
				&& getGameList().getPlayers(PlayerState.OUT).contains((Player) ev.getEntity()))
			ev.setCancelled(true);
		if ((isState(GameState.LobbyPhase)) || !FoodChange) {
			ev.setCancelled(true);
		}
	}

	@EventHandler
	public void ItemFrame(HangingBreakByEntityEvent ev) {
		if (!HangingBreak) {
			ev.setCancelled(true);
		} else if (ev.getEntity().getType() == EntityType.ITEM_FRAME) {
			ev.getEntity().remove();
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction() == Action.PHYSICAL && (isState(GameState.LobbyPhase) || solid)) {
			Block block = event.getClickedBlock();
			if (block == null)
				return;
			int blockType = block.getTypeId();
			if (blockType == Material.SOIL.getId()) {
				event.setUseInteractedBlock(org.bukkit.event.Event.Result.DENY);
				event.setCancelled(true);
				block.setTypeId(blockType);
				block.setData(block.getData());
			}
		}
	}

	@EventHandler
	public void StartGameGame(GameStartEvent ev) {
		if (getWorldData() != null) {
			if (getWorldData().getWorld() != null) {
				getWorldData().getWorld().setStorm(false);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void EntityDamageByEntity(EntityDamageByEntityEvent ev) {
		if ((ev.getDamager() instanceof Player
				&& getGameList().getPlayers(PlayerState.OUT).contains((Player) ev.getDamager())) || !Damage
				|| isState(GameState.LobbyPhase)) {
			if (getManager().getService().isDebug())
				System.err.println("[Game] Cancelled TRUE bei Damage");
			ev.setCancelled(true);
		} else if ((ev.getEntity() instanceof Player && ev.getDamager() instanceof Player) && !DamagePvP) {
			// P vs P
			if (getManager().getService().isDebug())
				System.err.println("[Game] Cancelled TRUE bei DamagePvP");
			ev.setCancelled(true);
		} else if (((ev.getEntity() instanceof Player && ev.getDamager() instanceof Creature)) && !DamageEvP) {
			// E vs P
			if (getManager().getService().isDebug())
				System.err.println("[Game] Cancelled TRUE bei DamageEvP");
			ev.setCancelled(true);
		} else if (((ev.getDamager() instanceof Player && ev.getEntity() instanceof Creature)) && !DamagePvE) {
			if (getManager().getService().isDebug())
				System.err.println("[Game] Cancelled TRUE bei DamagePvE");
			// P vs E
			ev.setCancelled(true);
		} else if ((ev.getDamager() instanceof Arrow || ev.getDamager() instanceof Snowball
				|| ev.getDamager() instanceof Egg) && !ProjectileDamage) {
			if (getManager().getService().isDebug())
				System.err.println("[Game] Cancelled TRUE bei ProjectileDamage");
			ev.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void PlaceBlockInMap(BlockPlaceEvent ev) {
		if (getManager().getPermManager().hasPermission(ev.getPlayer(), PermissionType.ALL_PERMISSION)
				|| ev.getPlayer().isOp())
			return;
		if (getGameList().getPlayers(PlayerState.OUT).contains(ev.getPlayer())) {
			ev.setCancelled(true);
			if (getManager().getService().isDebug())
				System.err.println("[Game] Cancelled TRUE bei BLOCKPLACE");
		}
		if (isState(GameState.DeathMatch)) {
			ev.setCancelled(false);
			return;
		}
		if ((!isState(GameState.InGame)) || BlockPlaceDeny.contains(ev.getBlock().getType())
				|| (!BlockPlace && !BlockPlaceAllow.contains(ev.getBlock().getType()))) {
			if (getManager().getService().isDebug())
				System.err.println("[Game] Cancelled TRUE bei BLOCKPLACE1 " + ev.getBlock().getType().name());
			ev.setCancelled(true);
		}
	}

	@EventHandler
	public void TNT(EntityExplodeEvent ev) {
		if (!Explosion) {
			ev.setCancelled(true);
		}
	}

	@EventHandler
	public void TNT(ExplosionPrimeEvent ev) {
		if (!Explosion) {
			ev.setCancelled(true);
		}
	}

	@EventHandler
	public void BreakBlockInMap(BlockBreakEvent ev) {
		if (getManager().getPermManager().hasPermission(ev.getPlayer(), PermissionType.ALL_PERMISSION)
				|| ev.getPlayer().isOp())
			return;
		if (getGameList().getPlayers(PlayerState.OUT).contains(ev.getPlayer()))
			ev.setCancelled(true);
		if (ev.getBlock().getWorld().getUID() == getManager().getLobby().getWorld().getUID())
			ev.setCancelled(true);
		if ((isState(GameState.LobbyPhase)) || BlockBreakDeny.contains(ev.getBlock().getType())
				|| (!BlockBreak && !BlockBreakAllow.contains(ev.getBlock().getType()))) {
			ev.setCancelled(true);
		}
	}

	@EventHandler
	public void PickUpItemsFromGround(PlayerPickupItemEvent ev) {
		if ((isState(GameState.LobbyPhase))
				|| !ItemPickup && !ItemPickupAllow.contains(ev.getItem().getItemStack().getTypeId())
				|| (ItemPickupDeny.contains(ev.getItem().getItemStack().getTypeId()))) {
			ev.setCancelled(true);
		} else if (getState() != GameState.LobbyPhase
				&& getGameList().getPlayers(PlayerState.OUT).contains(ev.getPlayer())) {
			ev.setCancelled(true);
		}
	}

	@EventHandler
	public void Drop(PlayerDropItemEvent ev) {
		if (getGameList().getPlayers(PlayerState.OUT).contains(ev.getPlayer()) || (isState(GameState.LobbyPhase))
				|| ItemDropDeny.contains(ev.getItemDrop().getItemStack().getTypeId())
				|| (!ItemDrop && !ItemDropAllow.contains(ev.getItemDrop().getItemStack().getTypeId()))) {
			ev.setCancelled(true);
		}
	}

	@EventHandler
	public void RespawnNow(PlayerDeathEvent ev) {
		if (ev.getEntity() instanceof Player) {
			UtilPlayer.RespawnNow(((Player) ev.getEntity()), getManager().getInstance());
		}
	}

	@EventHandler
	public void In(PlayerInteractEntityEvent ev) {
		if (ev.getRightClicked().getType() == EntityType.ITEM_FRAME)
			ev.setCancelled(true);
	}

	@EventHandler
	public void In(PlayerInteractEvent ev) {
		if (getGameList().getPlayers(PlayerState.OUT).contains(ev.getPlayer()))
			ev.setCancelled(true);
	}

	@EventHandler
	public void DropNNN(PlayerDeathEvent ev) {
		if (!DeathDropItems) {
			ev.getDrops().clear();
			ev.setDroppedExp(0);
		}
		ev.setDeathMessage(null);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void JoinRanking(PlayerJoinEvent ev) {
		UtilPlayer.setTab(ev.getPlayer(), getType().getTyp()+kArcade.id);
		if (getState() != GameState.LobbyPhase)
			return;
		getManager().getHologram().sendText(ev.getPlayer(), getManager().getLoc_raking(),
				getManager().getString_ranking());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void joinnow(PlayerJoinEvent ev){
		ev.setJoinMessage(null);
		ev.getPlayer().sendMessage(TranslationHandler.getText(ev.getPlayer(), "PREFIX") + "§eDu hast eine Map für uns gebaut? Melde sie im Forum und wir nehmen sie!§b http://ClashMc.eu/");
		getManager().Clear(ev.getPlayer());
		
		if(isState(GameState.LobbyPhase)){
			ev.getPlayer().teleport(getManager().getLobby());
			if (getStart() <= 2) {
				setStart(3);
			}
			
			if (getMax_Players() <= UtilServer.getPlayers().size()) {
				if (getStart() > 16) {
					setStart(16);
				}
			}
		}
		
		if (getType() != null) {
			ev.getPlayer()
					.sendMessage(TranslationHandler.getText(ev.getPlayer(), "PREFIX_GAME", getType().getTyp())
							+ TranslationHandler.getText(ev.getPlayer(), "WHEREIS_TEXT",
									getType().getTyp() + " " + kArcade.id));
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void loadPerm(PlayerLoadPermissionEvent ev) {
		if (UtilServer.getPlayers().size() > getMax_Players() && isState(GameState.LobbyPhase)) {
			if (getManager().getPermManager().hasPermission(ev.getPlayer(), PermissionType.JOIN_FULL_SERVER)
					|| getManager().getPermManager().hasPermission(ev.getPlayer(), PermissionType.JOIN_FULL_SERVER)) {
				boolean b = false;
				for (Player p : UtilServer.getPlayers()) {
					if (!getManager().getPermManager().hasPermission(p, PermissionType.JOIN_FULL_SERVER)) {
						UtilPlayer.sendMessage(p,
								TranslationHandler.getText(ev.getPlayer(), "PREFIX_GAME", getType().getTyp())
										+ TranslationHandler.getText(ev.getPlayer(), "KICKED_BY_PREMIUM"));
						UtilBG.sendToServer(p, getManager().getInstance());
						b = true;
						break;
					}
				}
				if (!b) {
					ev.getPlayer().kickPlayer(TranslationHandler.getText(ev.getPlayer(), "SERVER_FULL_WITH_PREMIUM"));
				}
			} else {
				ev.getPlayer().kickPlayer(TranslationHandler.getText(ev.getPlayer(), "SERVER_FULL"));
			}
		} else if (!isState(GameState.LobbyPhase)) {
			if (!getManager().getPermManager().hasPermission(ev.getPlayer(), PermissionType.SERVER_JOIN_SPECTATE)) {
				ev.getPlayer().kickPlayer(TranslationHandler.getText(ev.getPlayer(), "SERVER_NOT_LOBBYPHASE"));
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void BreakBlockLobby(BlockBreakEvent ev) {
		if (ev.getPlayer().isOp())
			return;
		if (getState() == GameState.LobbyPhase) {
			ev.setCancelled(true);
			return;
		}
		if (getWorldData().getWorld() == null)
			ev.setCancelled(true);
	}

	@EventHandler
	public void BlockBurn(BlockBurnEvent ev) {
		if (!BlockBurn || !isState(GameState.InGame)) {
			ev.setCancelled(true);
		}
	}

	@EventHandler
	public void BlockSpread(BlockSpreadEvent ev) {
		if (!BlockSpread || !isState(GameState.InGame)) {
			ev.setCancelled(true);
		}
	}

	@EventHandler
	public void BlockFade(BlockFadeEvent ev) {
		if (!BlackFade || !isState(GameState.InGame)) {
			ev.setCancelled(true);
		}
	}

	@EventHandler
	public void BlockDecay(LeavesDecayEvent ev) {
		if (!LeavesDecay || !isState(GameState.InGame)) {
			ev.setCancelled(true);
		}
	}

	@EventHandler
	public void MobSpawn(CreatureSpawnEvent ev) {
		if (ev.getSpawnReason() == SpawnReason.CUSTOM)
			return;
		if ((!CreatureSpawn || !isState(GameState.InGame)) && !AllowSpawnCreature.contains(ev.getCreatureType())) {
			ev.setCancelled(true);
		}
	}

	@EventHandler
	public void Restart(UpdateEvent ev) {
		if (ev.getType() != UpdateType.SEC)
			return;
		if (getState() != GameState.Restart)
			return;
		if (start < 0) {
			setDamage(false);
			start = 35;
		}
		start--;
		for (Player p : UtilServer.getPlayers())
			UtilDisplay.displayTextBar(p, TranslationHandler.getText("RESTART_IN", start));

		switch (start) {
		case 30:
			broadcastWithPrefix("RESTART_IN", start);
			break;
		case 25:
			broadcastWithPrefix("RESTART_IN", start);
			for (Player p : UtilServer.getPlayers())
				UtilBG.sendToServer(p, getManager().getInstance());
			break;
		case 23:
			getStats().saveAll();
			break;
		case 20:
			broadcastWithPrefix("RESTART_IN", start);
			for (Player p : UtilServer.getPlayers())
				UtilBG.sendToServer(p, getManager().getInstance());
			break;
		case 10:
			broadcastWithPrefix("RESTART_IN", start);
			break;
		case 5:
			broadcastWithPrefix("RESTART_IN", start);
		case 4:
			broadcastWithPrefix("RESTART_IN", start);
			break;
		case 3:
			broadcastWithPrefix("RESTART_IN", start);
			break;
		case 2:
			broadcastWithPrefix("RESTART_IN", start);
			break;
		case 1:
			broadcastWithPrefix("RESTART_IN", start);
			break;
		case 0:
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
			break;
		}
	}

	@EventHandler
	public void Time_Start(UpdateEvent ev) {
		if (ev.getType() == UpdateType.MIN_04 && getState() == GameState.LobbyPhase
				&& UtilServer.getPlayers().size() <= 0) {
			if (TimeSpan.HOUR * 3 < (System.currentTimeMillis() - kArcade.start_time)) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void r(GameStateChangeEvent ev) {
		if (!ev.isCancelled() && ev.getTo() == GameState.Restart) {
			setStart(-1);
		}
	}

	@EventHandler
	public void Lobby(UpdateEvent ev) {
		if (ev.getType() != UpdateType.SEC)
			return;
		if (getState() != GameState.LobbyPhase)
			return;
		if (start < 0) {
			start = 120;
			updateInfo();
		}
		start--;

		for (Player p : UtilServer.getPlayers()) {
			UtilDisplay.displayTextBar(p, Color.GRAY + TranslationHandler.getText(p, "GAME_START_IN", start));
			p.setLevel(start);
		}

		if (start != 0) {
			switch (start) {
			case 120:
				broadcastWithPrefix("GAME_START_IN", start);
				Bukkit.getWorld("world").setWeatherDuration(0);
				Bukkit.getWorld("world").setStorm(false);
				break;
			case 90:
				broadcastWithPrefix("GAME_START_IN", start);
				break;
			case 60:
				broadcastWithPrefix("GAME_START_IN", start);
				break;
			case 30:
				broadcastWithPrefix("GAME_START_IN", start);
				break;
			case 15:
				broadcastWithPrefix("GAME_START_IN", start);
				break;
			case 10:
				broadcastWithPrefix("GAME_START_IN", start);
				break;
			case 5:
				Bukkit.getPluginManager().callEvent(new GamePreStartEvent(this));
				break;
			case 3:
				broadcastWithPrefix("GAME_START_IN", start);
				break;
			case 2:
				broadcastWithPrefix("GAME_START_IN", start);
				break;
			case 1:
				broadcastWithPrefix("GAME_START_IN", start);
				break;
			}
		} else {
			if (UtilServer.getPlayers().size() >= getMin_Players()) {
				Bukkit.getPluginManager().callEvent(new GameStartEvent(getType()));
				updateInfo();
			} else {
				start = -1;
				broadcastWithPrefix("GAME_START_MIN_PLAYER", getMin_Players());
			}
		}
	}

}
