package eu.epicpvp.karcade.Game.Single.Games.CustomWars;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;

import eu.epicpvp.datenserver.definitions.dataserver.gamestats.GameState;
import eu.epicpvp.datenserver.definitions.dataserver.gamestats.GameType;
import eu.epicpvp.datenserver.definitions.dataserver.gamestats.StatsKey;
import eu.epicpvp.iterator.InfinityIterator;
import eu.epicpvp.karcade.ArcadeManager;
import eu.epicpvp.karcade.kArcade;
import eu.epicpvp.karcade.Events.RankingEvent;
import eu.epicpvp.karcade.Game.Events.GameStartEvent;
import eu.epicpvp.karcade.Game.Events.GameStateChangeEvent;
import eu.epicpvp.karcade.Game.Events.TeamDelEvent;
import eu.epicpvp.karcade.Game.Multi.Games.CustomWars1vs1.UtilCustomWars1vs1;
import eu.epicpvp.karcade.Game.Single.GameMapVote;
import eu.epicpvp.karcade.Game.Single.SingleWorldData;
import eu.epicpvp.karcade.Game.Single.Addons.AddonBedTeamKing;
import eu.epicpvp.karcade.Game.Single.Addons.AddonDropItems;
import eu.epicpvp.karcade.Game.Single.Addons.AddonEntityTeamKing;
import eu.epicpvp.karcade.Game.Single.Addons.AddonPlaceBlockCanBreak;
import eu.epicpvp.karcade.Game.Single.Addons.AddonVoteTeam;
import eu.epicpvp.karcade.Game.Single.Events.AddonBedKingDeathEvent;
import eu.epicpvp.karcade.Game.Single.Games.TeamGame;
import eu.epicpvp.karcade.Game.Single.Games.CustomWars.Items.AlarmSystem;
import eu.epicpvp.karcade.Game.Single.Games.CustomWars.Items.C4;
import eu.epicpvp.karcade.Game.Single.Games.CustomWars.Items.MobileSpezialVillager;
import eu.epicpvp.karcade.Game.Single.Games.CustomWars.Items.MobileVillager;
import eu.epicpvp.karcade.Game.Single.Games.CustomWars.Items.Parachute;
import eu.epicpvp.karcade.Game.Single.Games.CustomWars.Items.RescuePlattform;
import eu.epicpvp.karcade.Game.Single.Games.CustomWars.SheepWars.SheepWars;
import eu.epicpvp.karcade.Game.World.Event.WorldDataInitializeEvent;
import eu.epicpvp.kcore.Addons.AddonDay;
import eu.epicpvp.kcore.Addons.AddonHalloween;
import eu.epicpvp.kcore.Addons.AddonNight;
import eu.epicpvp.kcore.Calendar.Calendar;
import eu.epicpvp.kcore.Calendar.Calendar.CalendarType;
import eu.epicpvp.kcore.Enum.GameStateChangeReason;
import eu.epicpvp.kcore.Enum.PlayerState;
import eu.epicpvp.kcore.Enum.Team;
import eu.epicpvp.kcore.Enum.Zeichen;
import eu.epicpvp.kcore.Events.ServerStatusUpdateEvent;
import eu.epicpvp.kcore.Lists.kSort;
import eu.epicpvp.kcore.Permission.PermissionType;
import eu.epicpvp.kcore.StatsManager.Event.PlayerStatsLoadedEvent;
import eu.epicpvp.kcore.Translation.TranslationHandler;
import eu.epicpvp.kcore.Update.UpdateType;
import eu.epicpvp.kcore.Update.Event.UpdateEvent;
import eu.epicpvp.kcore.Util.Color;
import eu.epicpvp.kcore.Util.InventorySize;
import eu.epicpvp.kcore.Util.Title;
import eu.epicpvp.kcore.Util.UtilBlock;
import eu.epicpvp.kcore.Util.UtilDisplay;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.kcore.Util.UtilMath;
import eu.epicpvp.kcore.Util.UtilParticle;
import eu.epicpvp.kcore.Util.UtilPlayer;
import eu.epicpvp.kcore.Util.UtilScoreboard;
import eu.epicpvp.kcore.Util.UtilServer;
import eu.epicpvp.kcore.Util.UtilString;
import eu.epicpvp.kcore.Util.UtilTime;
import eu.epicpvp.kcore.Util.UtilWorldEdit;
import eu.epicpvp.kcore.Villager.Event.VillagerShopEvent;
import lombok.Getter;

public class CustomWars extends TeamGame {

	@Getter
	private CustomWarsType customType;
	@Getter
	private HashMap<Team, Boolean> teams = new HashMap<>();
	private AddonBedTeamKing addonBedTeamKing;
	private AddonEntityTeamKing addonEntityTeamKing;
	private AddonDropItems addonDropItems;
	@Getter
	private AddonPlaceBlockCanBreak addonPlaceBlockCanBreak;
	@Getter
	private ArrayList<CustomWarsItem> items;
	private HashMap<String, Integer> kills = new HashMap<>();
	private ArrayList<kSort<String>> ranking;

	public CustomWars(ArcadeManager manager, GameType type, CustomWarsType customType) {
		super(manager);
		long t = System.currentTimeMillis();
		this.customType = customType;
		this.ranking = new ArrayList<>();
		setTyp(type);

		items = new ArrayList<>();
		items.add(new AlarmSystem(this));
		items.add(new C4(this));
		items.add(new Parachute(this));
		items.add(new MobileVillager(this));
		items.add(new RescuePlattform(this));
		items.add(new MobileSpezialVillager(this));

		setDamage(false);
		setItemDrop(true);
		setItemPickup(true);
		setReplace_Water(true);
		setCreatureSpawn(false);
		setBlockBurn(false);
		setPlayerShearEntity(true);
		setBlockSpread(false);
		setDeathDropItems(true);
		setReplace_Fire(true);
		setReplace_Lava(true);
		setReplace_Water(true);
		setCompassAddon(true);

		setDamage(false);
		setDamageEvP(false);
		setDamagePvE(false);
		setDamagePvP(false);
		setTeamDamageOtherEnabled(true);
		setTeamDamageSelfEnabled(false);
		setProjectileDamage(true);
		setRespawn(true);
		setBlackFade(false);
		setFoodChange(true);
		setExplosion(true);
		setBlockBreak(true);
		setBlockPlace(true);
		setMinPlayers(customType.getMin());
		setMaxPlayers(customType.getMax());

		getInventoryTypDisallow().add(InventoryType.WORKBENCH);
		getInventoryTypDisallow().add(InventoryType.DISPENSER);
		getInventoryTypDisallow().add(InventoryType.BEACON);

		getItemPickupDeny().add(Material.CACTUS.getId());

		getInteractDeny().add(Material.getMaterial(43));
		getInteractDeny().add(Material.ENDER_STONE);

		if (getCustomType().getTeam_size() != 1)
			setVoteTeam(new AddonVoteTeam(this, getCustomType().getTeam(), InventorySize._9, getCustomType().getTeam_size()));

		setWorldData(new SingleWorldData(manager, getType().getTyp() + getCustomType().getTeam().length, getType().getShortName()));
		getWorldData().setCleanroomChunkGenerator(true);

		if (kArcade.id == -1) {
			Bukkit.getConsoleSender().sendMessage("§cEvent-Server!");
			getVoteHandler().add(new GameMapVote(getWorldData(), -1));
		} else {
			if (getWorldData().loadZips().size() < 3) {
				getWorldData().Initialize();
			} else {
				getVoteHandler().add(new GameMapVote(getWorldData(), 3));
			}
		}

		manager.DebugLog(t, this.getClass().getName());
		setState(GameState.LobbyPhase);
	}

	@EventHandler
	public void ServerStatusUpdateCustom(ServerStatusUpdateEvent ev) {
		ev.getPacket().setSubstate(getCustomType().name());
	}

	@EventHandler
	public void Ranking(RankingEvent ev) {
		getManager().setRanking(StatsKey.WIN);
	}

	@EventHandler
	public void RespawnLocation(PlayerRespawnEvent ev) {
		if (getGameList().isPlayerState(ev.getPlayer()) == PlayerState.INGAME) {
			ev.setRespawnLocation(getWorldData().getSpawnLocations(getTeam(ev.getPlayer())).get(UtilMath.randomInteger(getWorldData().getSpawnLocations(getTeam(ev.getPlayer())).size())));
		}
	}

	String format;

	@EventHandler
	public void InGame(UpdateEvent ev) {
		if (ev.getType() != UpdateType.SEC)
			return;
		if (getState() != GameState.InGame)
			return;
		setStart(getStart() - 1);

		if (game_end()) {
			setState(GameState.Restart);
			broadcastWithPrefixName("GAME_END");
			return;
		}

		format = UtilTime.formatSeconds(getStart());

		for (Player p : UtilServer.getPlayers())
			UtilDisplay.displayTextBar(p, TranslationHandler.getText(p, "GAME_END_IN", format));
		getScoreboard().getObjective(DisplaySlot.SIDEBAR).setDisplayName("§e" + getType().getTyp() + " §7- §e" + format);

		if ((getCustomType().getInGameTime() - 2) == getStart()) {
			for (Player p : UtilServer.getPlayers()) {
				if (getTeamList().containsKey(p))
					continue;
				Team t = calculateLowestTeam();
				addTeam(p, t);
				p.teleport(getWorldData().getSpawnLocations(t).get(0));
			}

			HashMap<Player, String> l = new HashMap<>();
			for (Player p : getTeamList().keySet()) {
				l.put(p, getTeamList().get(p).getColor());
			}

			updateTab(getCustomType().getTeam());
		}

		switch (getStart()) {
		case 15:
			broadcastWithPrefix("DEATHMATCH_START_IN", format);
			break;
		case 10:
			broadcastWithPrefix("DEATHMATCH_START_IN", format);
			break;
		case 5:
			broadcastWithPrefix("DEATHMATCH_START_IN", format);
			break;
		case 4:
			broadcastWithPrefix("DEATHMATCH_START_IN", format);
			break;
		case 3:
			broadcastWithPrefix("DEATHMATCH_START_IN", format);
			break;
		case 2:
			broadcastWithPrefix("DEATHMATCH_START_IN", format);
			break;
		case 1:
			broadcastWithPrefix("DEATHMATCH_START_IN", format);
			break;
		case 0:
			broadcastWithPrefixName("DEATHMATCH_START");

			if (addonBedTeamKing != null) {
				for (Team team : addonBedTeamKing.getTeams().keySet()) {
					Block block = addonBedTeamKing.getTeams().get(team);
					Block twin = UtilBlock.getTwinLocation(block).getBlock();
					block.setType(Material.AIR);
					twin.setType(Material.AIR);

					getTeams().remove(team);
					getTeams().put(team, false);
				}
				addonBedTeamKing.getTeams().clear();

			} else if (addonEntityTeamKing != null) {
				for (Team team : addonEntityTeamKing.getTeams().keySet()) {
					Entity e = addonEntityTeamKing.getTeams().get(team);
					e.remove();

					getTeams().remove(team);
					getTeams().put(team, false);
				}
				addonEntityTeamKing.getTeams().clear();
			}

			setState(GameState.DeathMatch);
			break;
		}
	}

	@EventHandler
	public void DeathMatch(UpdateEvent ev) {
		if (ev.getType() != UpdateType.SEC)
			return;
		if (getState() != GameState.DeathMatch)
			return;
		setStart(getStart() - 1);

		if (game_end()) {
			setState(GameState.Restart);
			broadcastWithPrefixName("GAME_END");
			return;
		}

		format = UtilTime.formatSeconds(getStart());

		for (Player p : UtilServer.getPlayers())
			UtilDisplay.displayTextBar(p, TranslationHandler.getText(p, "GAME_END_IN", format));
		getScoreboard().getObjective(DisplaySlot.SIDEBAR).setDisplayName("§e" + getType().getTyp() + " §7- §e" + format);

		switch (getStart()) {
		case 15:
			broadcastWithPrefix("GAME_END_IN", format);
			break;
		case 10:
			broadcastWithPrefix("GAME_END_IN", format);
			break;
		case 5:
			broadcastWithPrefix("GAME_END_IN", format);
			break;
		case 4:
			broadcastWithPrefix("GAME_END_IN", format);
			break;
		case 3:
			broadcastWithPrefix("GAME_END_IN", format);
			break;
		case 2:
			broadcastWithPrefix("GAME_END_IN", format);
			break;
		case 1:
			broadcastWithPrefix("GAME_END_IN", format);
			break;
		case 0:
			broadcastWithPrefixName("GAME_END");
			setState(GameState.Restart);
			break;
		}
	}

	//ER PRüFT OB NUR NOCH EIN TEAM ÜBRIG IST!
	public boolean game_end() {
		for (Player p : getTeamList().keySet()) {
			for (Player p1 : getTeamList().keySet()) {
				if (getTeamList().get(p1) != getTeamList().get(p)) {
					return false;
				}
			}
		}
		return true;
	}

	public static ItemStack Silber(int i) {
		return UtilItem.RenameItem(new ItemStack(Material.IRON_INGOT, i), "§bSilver");
	}

	public static ItemStack Gold(int i) {
		return UtilItem.RenameItem(new ItemStack(Material.GOLD_INGOT, i), "§bGold");
	}

	public static ItemStack Bronze(int i) {
		return UtilItem.RenameItem(new ItemStack(Material.CLAY_BRICK, i), "§bBronze");
	}

	@EventHandler
	public void Death(PlayerDeathEvent ev) {
		String v;
		String k = null;
		if (ev.getEntity() instanceof Player) {
			Player killer = null;
			Player victim = ev.getEntity();
			if (ev.getEntity().getKiller() != null && !ev.getEntity().getKiller().equals(ev.getEntity()))
				killer = ev.getEntity().getKiller();
			else if (ev.getEntity().getLastDamageCause() != null)
				if (ev.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent)
					if (((EntityDamageByEntityEvent) ev.getEntity().getLastDamageCause()).getDamager() instanceof Player)
						killer = (Player) ((EntityDamageByEntityEvent) ev.getEntity().getLastDamageCause()).getDamager();
					else if (((EntityDamageByEntityEvent) ev.getEntity().getLastDamageCause()).getDamager() instanceof Projectile && ((Projectile) ((EntityDamageByEntityEvent) ev.getEntity().getLastDamageCause()).getDamager()).getShooter() instanceof Player)
						killer = (Player) ((Projectile) ((EntityDamageByEntityEvent) ev.getEntity().getLastDamageCause()).getDamager()).getShooter();

			Team t = getTeam(victim);
			getStats().addInt(victim, 1, StatsKey.DEATHS);
			v = t.getColor() + "{player_"+victim.getName()+"}";

			if (killer != null) {
				getMoney().add(killer, StatsKey.COINS, 4);
				getStats().addInt(killer, 1, StatsKey.KILLS);
				k = getTeam(killer).getColor() + "{player_"+killer.getName()+"}";
				int ki = kills.get(killer.getName());
				ki++;
				kills.remove(killer.getName());
				kills.put(killer.getName(), ki);
			}

			if (this instanceof SheepWars) {
				if (((SheepWars) this).getKits().containsKey(victim)) {
					v = v + "§a[" + ((SheepWars) this).getKits().get(victim) + "§a]";
				}
				if (killer != null)
					if (((SheepWars) this).getKits().containsKey(killer)) {
						k = k + "§a[" + ((SheepWars) this).getKits().get(killer) + "§a]";
					}
			}

			if (k != null)
				broadcastWithPrefix("KILL_BY", new String[]{ v, k });
			else
				broadcastWithPrefix("DEATH", v);

			if (getTeams().get(t) == false) {
				getGameList().addPlayer(victim, PlayerState.SPECTATOR);
				getStats().addInt(victim, 1, StatsKey.LOSE);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void replace(BlockPlaceEvent ev) {
		if (getState() != GameState.LobbyPhase) {
			if (ev.getBlock().getType() == Material.STAINED_GLASS || ev.getBlock().getType() == Material.STAINED_CLAY) {
				if (ev.getBlock().getData() != getTeam(ev.getPlayer()).getItem().getData().getData()) {
					if (getGameList().getPlayers().containsKey(ev.getPlayer()) && getGameList().getPlayers().get(ev.getPlayer()) == PlayerState.INGAME) {
						ev.getBlock().setData(getTeam(ev.getPlayer()).getItem().getData().getData());
						//						UtilInv.remove(ev.getPlayer(), ev.getBlock().getType(),ev.getBlock().getData(), 1);
					}
				}
			}
		}
	}

	@EventHandler
	public void setBlock(PlayerInteractEvent ev) {
		if (ev.getPlayer().getItemInHand() != null) {
			if (ev.getPlayer().getItemInHand().getType() == Material.STAINED_GLASS || ev.getPlayer().getItemInHand().getType() == Material.STAINED_CLAY) {
				if (ev.getPlayer().getItemInHand().getData().getData() != getTeam(ev.getPlayer()).getItem().getData().getData()) {
					if (getGameList().getPlayers().containsKey(ev.getPlayer()) && getGameList().getPlayers().get(ev.getPlayer()) == PlayerState.INGAME) {
						ev.getPlayer().setItemInHand(new ItemStack(ev.getPlayer().getItemInHand().getType(), ev.getPlayer().getItemInHand().getAmount(), getTeam(ev.getPlayer()).getItem().getData().getData()));
						ev.getPlayer().updateInventory();
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void Place(BlockPlaceEvent ev) {
		if (getState() == GameState.LobbyPhase) {
			ev.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void Break(BlockBreakEvent ev) {
		if (getState() == GameState.LobbyPhase) {
			ev.setCancelled(true);
			return;
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void pickUp(PlayerPickupItemEvent ev) {
		if (getState() != GameState.LobbyPhase) {
			if (ev.getItem().getItemStack().getType() == Material.STAINED_GLASS || ev.getItem().getItemStack().getType() == Material.STAINED_CLAY) {
				ev.getItem().getItemStack().getData().setData(getTeam(ev.getPlayer()).getItem().getData().getData());
			}
		}
	}

	@EventHandler
	public void move(UpdateEvent ev) {
		if (ev.getType() == UpdateType.FASTEST) {
			if (getState() == GameState.InGame || getState() == GameState.DeathMatch) {
				Block block;
				for (Player player : getGameList().getPlayers().keySet()) {
					if (getGameList().getPlayers().get(player) == PlayerState.INGAME) {
						if (player.isOnGround()) {
							block = player.getLocation().add(0, -1, 0).getBlock();
							if (addonPlaceBlockCanBreak.getBlocks().contains(block.getLocation()) && block.getType() == Material.STAINED_GLASS) {
								if (getTeam(player).getItem().getData().getData() != block.getData()) {
									try {
										UtilParticle.SMOKE_NORMAL.display(1, 2, 2, 0.5f, 40, block.getLocation(), 15);
										block.setType(Material.AIR);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void WorldLoad(WorldDataInitializeEvent ev) {
		ev.getMap().setMapName(shortMap(getWorldData().getMapName(), " " + getCustomType().getTeam().length + "x" + getCustomType().getTeam_size()));
	}

	@EventHandler
	public void VillagerShop(VillagerShopEvent ev) {
		if (getGameList().isPlayerState(ev.getPlayer()) == PlayerState.SPECTATOR) {
			ev.setCancelled(true);
		}
	}

	@EventHandler
	public void a(ChunkLoadEvent e){
		for(Entity et : e.getChunk().getEntities())
			if (!(et instanceof Player) && !(et instanceof Villager))
				et.remove();
	}

	@EventHandler
	public void a(ChunkUnloadEvent e){
		for(Entity et : e.getChunk().getEntities()){
			if(et instanceof Player || et instanceof Villager)
				return;
		}
		e.setCancelled(true);
	}

	@EventHandler
	public void Start(GameStartEvent ev) {
		long time = System.currentTimeMillis();

		/**
		 * Settup world
		 */
		for (Entity e : getWorldData().getWorld().getEntities()) {
			if (!(e instanceof Player) && !(e instanceof Villager)) {
				e.remove();
			}
		}

		EntityType villagerEntityType = EntityType.VILLAGER;
		if (getManager().getHoliday() != null) {
			switch (getManager().getHoliday()) {
			case HALLOWEEN:
				new AddonNight(getManager().getInstance(), getWorldData().getWorld());
				new AddonHalloween(getManager().getInstance());
				getWorldData().getWorld().setStorm(false);
				break;
			case WEIHNACHTEN:
				villagerEntityType = EntityType.SNOWMAN;
				new AddonDay(getManager().getInstance(), getWorldData().getWorld());
				getWorldData().getWorld().setStorm(false);
				break;
			default:
				new AddonDay(getManager().getInstance(), getWorldData().getWorld());
				getWorldData().getWorld().setStorm(false);
				break;
			}
		}

		/**
		 * Setup scoreboard
		 */
		setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		UtilScoreboard.addBoard(getScoreboard(), DisplaySlot.SIDEBAR, "§e" + getType().getTyp());

		/**
		 * Setup players
		 */
		ArrayList<Player> plist = new ArrayList<>();
		for (Player p : UtilServer.getPlayers()) {
			getManager().clear(p);
			kills.put(p.getName(), 0);
			getGameList().addPlayer(p, PlayerState.INGAME);
			plist.add(p);
		}

		Team[] teams = getCustomType().getTeam();
		distributePlayers(teams, plist); //Verteile schonmal alle spieler

		//Teleport Players
		for (Team t : teams) {
			List<Player> players = getAllPlayersFromTeam(t);
			UtilScoreboard.setScore(getScoreboard(), "§a§l" + Zeichen.BIG_HERZ.getIcon() + " " + t.getColor() + t.getDisplayName(), DisplaySlot.SIDEBAR, players.size());
			getTeams().put(t, true);
			setVillager(t, villagerEntityType);
			InfinityIterator<Location> spawns = new InfinityIterator<>(getWorldData().getSpawnLocations(t));
			if (!spawns.hasNext())
				throw new RuntimeException("Cant find worlspawns for team: " + t.getDisplayName());
			for (Player p : players) {
				p.setScoreboard(getScoreboard());
				p.teleport(spawns.next().clone().add(0, 1, 0));
				/* Not force teleport? WolverinDEV prefare force :D
				if (p.getWorld().getUID() != getWorldData().getWorld().getUID()) {
					p.teleport(list.get(i).clone().add(0, 1, 0));
				}
				*/
			}
		}

		addonDropItems = new AddonDropItems(this, getCustomType().getDrop_rate());
		addonPlaceBlockCanBreak = new AddonPlaceBlockCanBreak(getManager().getInstance(), new Material[] { Material.getMaterial(31), Material.getMaterial(38), Material.getMaterial(37), Material.BROWN_MUSHROOM, Material.RED_MUSHROOM });

		if (getType() == GameType.SheepWars) {
			addonEntityTeamKing = new AddonEntityTeamKing(teams, this, EntityType.SHEEP);

			LivingEntity s;
			for (Team t : addonEntityTeamKing.getTeams().keySet()) {
				s = (LivingEntity) addonEntityTeamKing.getTeams().get(t);
				s.setCustomName(t.getColor() + getName(EntityType.SHEEP) + " ");
				if (s instanceof Sheep) {
					((Sheep) s).setColor(UtilCustomWars1vs1.getDyeColorFromTeamName(t.getColor()));
				}
			}
		} else {
			addonBedTeamKing = new AddonBedTeamKing(getManager(), teams, this);
		}

		if (getWorldData().existLoc(Team.BLACK) && !getWorldData().getSpawnLocations(Team.BLACK).isEmpty()) {
			for (Location loc : getWorldData().getSpawnLocations(Team.BLACK)) {
				UtilCustomWars1vs1.setSpezialVillager(getManager().getInstance(), loc, villagerEntityType);
			}
		}

		setStart(getCustomType().getInGameTime());
		setState(GameState.InGame);
		setDamage(true);
		setDamageEvP(false);
		setDamagePvE(true);
		setDamagePvP(true);
		getManager().DebugLog(time, this.getClass().getName());
	}

	public String getName(EntityType e) {
		switch (e) {
		case SHEEP:
			return "Schaf";
		case SPIDER:
			return "Spinne";
		case ZOMBIE:
			return "Zombie";
		case CAVE_SPIDER:
			return "Spinne";
		default:
			return "Tier";
		}
	}

	@EventHandler
	public void GameStateChange(GameStateChangeEvent ev) {
		if (ev.getTo() == GameState.Restart && ev.getReason() != GameStateChangeReason.CHANGE_TYPE) {
			if (game_end()) {
				Team t = getLastTeam();
				//				int size = getPlayerFrom(t).size();

				for (Player p : getAllPlayersFromTeam(t)) {
					if (getGameList().isPlayerState(p) == PlayerState.INGAME) {
						getStats().addInt(p, 1, StatsKey.WIN);

						if (getCustomType() == CustomWarsType._4x32 || getCustomType() == CustomWarsType._8x16) {
							getMoney().add(p, StatsKey.GEMS, 200);
						} else {
							getMoney().add(p, StatsKey.COINS, 10);
						}
					}
				}

				for (String name : this.kills.keySet()) {
					this.ranking.add(new kSort<String>(name, this.kills.get(name)));
				}
				Collections.sort(ranking, kSort.DESCENDING);

				Bukkit.broadcastMessage("§a§l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
				if (t != null) {
					Bukkit.broadcastMessage(UtilString.center("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬".length(), ("Winner - " + t.getDisplayName()).length()) + "§eWinner §7- " + t.getColor() + t.getDisplayName());
					Bukkit.broadcastMessage(" ");
				}

				if (!this.ranking.isEmpty() && this.ranking.size() >= 1) {
					Bukkit.broadcastMessage(UtilString.center("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬".length(), ("1st Killer - " + this.ranking.get(0).getObject() + " - " + this.ranking.get(0).getValue()).length()) + "§e1st Killer - §7" + "{player_"+this.ranking.get(0).getObject()+"}" + " - " + this.ranking.get(0).getValue());
				}
				if (!this.ranking.isEmpty() && this.ranking.size() >= 2) {
					Bukkit.broadcastMessage(UtilString.center("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬".length(), ("2st Killer - " + this.ranking.get(1).getObject() + " - " + this.ranking.get(1).getValue()).length()) + "§62st Killer - §7" + "{player_"+this.ranking.get(1).getObject()+"}" + " - " + this.ranking.get(1).getValue());
				}
				if (!this.ranking.isEmpty() && this.ranking.size() >= 3) {
					Bukkit.broadcastMessage(UtilString.center("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬".length(), ("3st Killer - " + this.ranking.get(2).getObject() + " - " + this.ranking.get(2).getValue()).length()) + "§c3st Killer - §7" + "{player_"+this.ranking.get(2).getObject()+"}" + " - " + this.ranking.get(2).getValue());
				}
				Bukkit.broadcastMessage("§a§l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
			}
		}
	}

	@EventHandler
	public void TeamDelBoard(TeamDelEvent ev) {
		Team team = getTeam(ev.getPlayer());
		if (getTeams().containsKey(team)) {
			if (getTeams().get(team)) {
				UtilScoreboard.resetScore(getScoreboard(), "§a§l" + Zeichen.BIG_HERZ.getIcon() + " " + team.getColor() + team.getDisplayName(), DisplaySlot.SIDEBAR);
				UtilScoreboard.setScore(getScoreboard(), "§a§l" + Zeichen.BIG_HERZ.getIcon() + " " + team.getColor() + team.getDisplayName(), DisplaySlot.SIDEBAR, getAllPlayersFromTeam(team).size() - 1);
			} else {
				UtilScoreboard.resetScore(getScoreboard(), "§4§l" + Zeichen.MAHLZEICHEN_FETT.getIcon() + " " + team.getColor() + team.getDisplayName(), DisplaySlot.SIDEBAR);
				UtilScoreboard.setScore(getScoreboard(), "§4§l" + Zeichen.MAHLZEICHEN_FETT.getIcon() + " " + team.getColor() + team.getDisplayName(), DisplaySlot.SIDEBAR, getAllPlayersFromTeam(team).size() - 1);
			}
		}
	}

	@EventHandler
	public void bedDeath(AddonBedKingDeathEvent ev) {
		UtilScoreboard.resetScore(getScoreboard(), "§a§l" + Zeichen.BIG_HERZ.getIcon() + " " + ev.getTeam().getColor() + ev.getTeam().getDisplayName(), DisplaySlot.SIDEBAR);
		UtilScoreboard.setScore(getScoreboard(), "§4§l" + Zeichen.MAHLZEICHEN_FETT.getIcon() + " " + ev.getTeam().getColor() + ev.getTeam().getDisplayName(), DisplaySlot.SIDEBAR, getAllPlayersFromTeam(ev.getTeam()).size());
		getTeams().remove(ev.getTeam());
		getTeams().put(ev.getTeam(), false);
		Title t = new Title("", "");
		if (ev.getKiller() != null) {
			getStats().addInt(ev.getKiller(), 1, StatsKey.BEDWARS_ZERSTOERTE_BEDs);
		}

		for (Player player : UtilServer.getPlayers()) {
			t.setSubtitle(TranslationHandler.getText(player, "BEDWARS_BED_BROKE", ev.getTeam().getColor() + "§l" + ev.getTeam().getDisplayName()));
			t.send(player);
		}
	}

	@EventHandler
	public void Chat(AsyncPlayerChatEvent ev) {
		if (ev.isCancelled())
			return;
		ev.setCancelled(true);

		if ((!ev.getPlayer().hasPermission(PermissionType.CHAT_LINK.getPermissionToString())) && UtilString.isBadWord(ev.getMessage()) || UtilString.checkForIP(ev.getMessage())) {
			ev.setMessage("Ich heul rum!");
			ev.getPlayer().sendMessage(TranslationHandler.getText(ev.getPlayer(), "PREFIX") + TranslationHandler.getText(ev.getPlayer(), "CHAT_MESSAGE_BLOCK"));
		}

		if (!isState(GameState.LobbyPhase) && getTeamList().containsKey(ev.getPlayer())) {
			if (ev.getMessage().toCharArray()[0] == '#') {
				Team t = getTeam(ev.getPlayer());
				broadcast("§7[" + t.getColor() + t.getDisplayName() + "§7] " + "{player_"+ev.getPlayer().getName()+"}" + ": §7" + ev.getMessage().subSequence(1, ev.getMessage().length()));
			} else {
				Team t = getTeam(ev.getPlayer());
				for (Player p : getAllPlayersFromTeam(getTeam(ev.getPlayer()))) {
					UtilPlayer.sendMessage(p, t.getColor() + "Team-Chat " + "{player_"+ev.getPlayer().getName()+"}" + ":§7 " + ev.getMessage());
				}
			}
		} else if (getState() != GameState.LobbyPhase && getGameList().getPlayers(PlayerState.SPECTATOR).contains(ev.getPlayer())) {
			ev.setCancelled(true);
			UtilPlayer.sendMessage(ev.getPlayer(), TranslationHandler.getText(ev.getPlayer(), "PREFIX_GAME", getType().getTyp()) + TranslationHandler.getText(ev.getPlayer(), "SPECTATOR_CHAT_CANCEL"));
		} else {
			UtilServer.broadcast(getManager().getPermManager().getPrefix(ev.getPlayer()) + "{player_"+ev.getPlayer().getName()+"}" + ":§7 " + ev.getMessage());
		}
	}

	@EventHandler
	public void WorldData(WorldDataInitializeEvent ev) {
		if (Calendar.holiday == CalendarType.WEIHNACHTEN) {
			if (getWorldData().existLoc(Team.BLACK) && !getWorldData().getSpawnLocations(Team.BLACK).isEmpty()) {
				UtilWorldEdit.simulateSnow(getWorldData().getSpawnLocations(Team.BLACK).get(0), 150);
			} else {
				UtilWorldEdit.simulateSnow(getWorldData().getSpawnLocations(Team.RED).get(0), 150);
			}
		}
	}

	@EventHandler
	public void StatsLoaded(PlayerStatsLoadedEvent ev) {
		if (ev.getManager().getType() != getType())
			return;
		if (getState() != GameState.LobbyPhase)
			return;

		if (UtilPlayer.isOnline(ev.getPlayerId())) {
			Player player = UtilPlayer.searchExact(ev.getPlayerId());
			int win = getStats().getInt(StatsKey.WIN, player);
			int lose = getStats().getInt(StatsKey.LOSE, player);

			Bukkit.getScheduler().runTask(getManager().getInstance(), new Runnable() {

				@Override
				public void run() {
					getManager().getHologram().sendText(player, getManager().getLoc_stats(), new String[]
					{ Color.GREEN + getType().getTyp() + " " + getCustomType().name().replaceAll("_", "") + Color.ORANGE + "§l Info", TranslationHandler.getText(player, "GAME_HOLOGRAM_SERVER", getType().getTyp() + " §a" + kArcade.id), TranslationHandler.getText(player, "GAME_HOLOGRAM_MAP", (getWorldData().getMap() != null ? getWorldData().getMapName() : "Loading...")), " ", TranslationHandler.getText(player, "GAME_HOLOGRAM_STATS", getType().getTyp()),
							TranslationHandler.getText(player, "GAME_HOLOGRAM_KILLS", getStats().getInt(StatsKey.KILLS, player)), TranslationHandler.getText(player, "GAME_HOLOGRAM_DEATHS", getStats().getInt(StatsKey.DEATHS, player)),
							(getType() == GameType.BedWars ? TranslationHandler.getText(player, "GAME_HOLOGRAM_BEDWARS", getStats().getInt(StatsKey.BEDWARS_ZERSTOERTE_BEDs, player)) : (getType() == GameType.SheepWars ? TranslationHandler.getText(player, "GAME_HOLOGRAM_SHEEP", getStats().getInt(StatsKey.SHEEPWARS_KILLED_SHEEPS, player)) : " ")), " ", TranslationHandler.getText(player, "GAME_HOLOGRAM_GAMES", (win + lose)), TranslationHandler.getText(player, "GAME_HOLOGRAM_WINS", win),
							TranslationHandler.getText(player, "GAME_HOLOGRAM_LOSE", lose), });
				}
			});
		}
	}

	public void setVillager(Team t, EntityType e) {
		for (Location l : getWorldData().getSpawnLocations(UtilCustomWars1vs1.getVillagerSpawn(t))) {
			UtilCustomWars1vs1.setVillager(getManager().getInstance(), items, t, l, e);
		}
	}
}
