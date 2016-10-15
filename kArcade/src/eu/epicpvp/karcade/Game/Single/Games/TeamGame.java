package eu.epicpvp.karcade.Game.Single.Games;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import eu.epicpvp.datenserver.definitions.dataserver.gamestats.GameState;
import eu.epicpvp.karcade.ArcadeManager;
import eu.epicpvp.karcade.Game.Events.TeamAddEvent;
import eu.epicpvp.karcade.Game.Events.TeamDelEvent;
import eu.epicpvp.karcade.Game.Single.SingleGame;
import eu.epicpvp.karcade.Game.Single.Addons.AddonSpecCompass;
import eu.epicpvp.karcade.Game.Single.Addons.AddonSpectator;
import eu.epicpvp.karcade.Game.Single.Addons.AddonVoteTeam;
import eu.epicpvp.kcore.Enum.GameStateChangeReason;
import eu.epicpvp.kcore.Enum.PlayerState;
import eu.epicpvp.kcore.Enum.Team;
import eu.epicpvp.kcore.Kit.Shop.Events.KitShopPlayerDeleteEvent;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.kcore.Util.UtilMath;
import eu.epicpvp.kcore.Util.UtilScoreboard;
import eu.epicpvp.kcore.Util.UtilServer;
import lombok.Getter;
import lombok.Setter;

public class TeamGame extends SingleGame {

	@Getter
	private HashMap<Player, Team> teamList = new HashMap<>();
	@Getter
	@Setter
	private AddonVoteTeam VoteTeam;
	private AddonSpectator spec = null;

	public TeamGame(ArcadeManager manager) {
		super(manager);
	}

	public void addTeam(Player p, Team t) {
		teamList.put(p, t);
		Bukkit.getPluginManager().callEvent(new TeamAddEvent(p, t));
	}

	public String getERR(Team[] t, int size) {
		String s = "";
		HashMap<Team, Integer> l = createTeamDistributionLimits(t, size);
		for (Team te : l.keySet()) {
			s = s + "TEAM:" + te.getDisplayName() + "/" + l.get(te);
		}
		s = s + "  -  ";
		for (Player p : teamList.keySet()) {
			s = s + "PLAYER:" + p.getName() + "-" + teamList.get(p).getDisplayName();
		}

		return s;
	}

	public HashMap<Team, Integer> createTeamDistributionLimits(Team[] teams, int size) {
		if (size == 1) {
			HashMap<Team, Integer> list = new HashMap<>();
			for (Team team : teams)
				list.put(team, 1);
			return list;
		} else if (size == 2) {
			HashMap<Team, Integer> list = new HashMap<>();
			for (Team team : teams)
				list.put(team, 2);
			return list;
		} else {
			HashMap<Team, Integer> list = new HashMap<>();
			Collection<? extends Player> l = UtilServer.getPlayers();

			for (Team team : teams) {
				list.put(team, l.size() / teams.length);
			}

			if (l.size() % teams.length != 0) {
				list.remove(teams[0]);
				list.put(teams[0], (l.size() / teams.length) + 1);
			}

			return list;
		}
	}

	public int getTeamCount(Team team) {
		int i = 0;
		for (Team t : teamList.values()) {
			if (t == team)
				i++;
		}
		return i;
	}

	public Team calculateLowestTeam() {
		return calculateLowestTeam(true);
	}

	public Team calculateLowestTeam(boolean returnNullBySame) {
		return calculateLowestTeam(teamList.values().toArray(new Team[0]), returnNullBySame);
	}

	public Team calculateLowestTeam(Team[] teams, boolean returnNullBySame) {
		List<Entry<Team, Integer>> playerCountEntries = calculateLowestTeams(teams, returnNullBySame);
		if (playerCountEntries.isEmpty())
			if (returnNullBySame)
				return null;
			else
				return Team.RED;
		return playerCountEntries.get(0).getKey();
	}

	public List<Entry<Team, Integer>> calculateLowestTeams(Team[] teams, boolean returnNullBySame) {
		HashMap<Team, Integer> playerCount = new HashMap<>();
		for (Team t : teams)
			playerCount.put(t, 0);
		for (Entry<Player, Team> t : teamList.entrySet()) {
			if (playerCount.containsKey(t.getValue()))
				playerCount.put(t.getValue(), playerCount.get(t.getValue()) + 1);
		}
		List<Entry<Team, Integer>> playerCountEntries = new ArrayList<>(playerCount.entrySet());
		Collections.sort(playerCountEntries, new Comparator<Entry<Team, Integer>>() {
			@Override
			public int compare(Entry<Team, Integer> o1, Entry<Team, Integer> o2) {
				return Integer.compare(o1.getValue(), o2.getValue());
			}
		});
		if (playerCountEntries.isEmpty())
			if (returnNullBySame)
				return null;
			else
				return new ArrayList<>();
		return playerCountEntries;
	}

	public void delTeam(Player p) {
		if (teamList.containsKey(p)) {
			Bukkit.getPluginManager().callEvent(new TeamDelEvent(p, getTeam(p)));
			teamList.remove(p);
		}
	}

	public Team getLastTeam() {
		Team t = null;
		for (Player p : teamList.keySet()) {
			t = teamList.get(p);
			for (Player p1 : teamList.keySet()) {
				if (teamList.get(p1) != t) {
					t = null;
					break;
				}
			}
		}
		return t;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void SpectJoin(PlayerJoinEvent ev) {
		if (getState() != GameState.LobbyPhase) {
			setSpectator(null, ev.getPlayer());
		}
	}

	public boolean hasLastTeam() {
		return getLastTeam() != null;
	}

	public Team getTeam(Player p) {
		if (teamList.containsKey(p)) {
			return teamList.get(p);
		}
		return null;
	}

	public ArrayList<Player> getAllPlayersFromTeam(Team t) {
		ArrayList<Player> list = new ArrayList<>();
		for (Entry<Player, Team> entry : teamList.entrySet()) {
			if (entry.getValue() == t) {
				list.add(entry.getKey());
			}
		}
		return list;
	}

	public int getPlayerCountFromTeam(Team t) {
		return getAllPlayersFromTeam(t).size();
	}

	public void updateTab(Team[] teams) {
		if (getScoreboard() == null)
			setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		for (Team team : teams) {
			ArrayList<Player> list = getAllPlayersFromTeam(team);

			UtilScoreboard.addTeam(getScoreboard(), team.getDisplayName(), team.getColor());

			for (Player player : list) {
				getScoreboard().getTeam(team.getDisplayName()).addPlayer(player);
				player.setPlayerListName(team.getColor() + player.getName());
			}

			for (Player p : list) {
				if (!getScoreboard().getPlayers().contains(p)) {
					p.setScoreboard(getScoreboard());
				}
			}
		}

	}

	public int random(int i) {
		if (i == 1)
			return 0;
		return UtilMath.RandomInt((i - 1), 0);
	}

	public void distributePlayers(Team[] teams, ArrayList<Player> list) {
		Collections.shuffle(list);
		Iterator<Player> players = list.iterator();

		if(getVoteTeam() != null)
			for(Entry<Player, Team> voted : getVoteTeam().getAllVotes())
				getTeamList().put(voted.getKey(), voted.getValue());

		while (players.hasNext()) {
			Player p = players.next();
			if (getTeamList().containsKey(p)) //Voted
				continue;
			getTeamList().put(p, calculateLowestTeam(teams, false));
		}
	}

	public boolean isSetTeam(Team[] teams) {
		for (Team t : teams) {
			if (!isSetTeam(t))
				return false;
		}
		return true;
	}

	public boolean isSetTeam(Team team) {
		return getTeamList().values().contains(team);
	}

	public boolean TeamAmountSame() {
		Set<Team> teams = new HashSet();
		teams.addAll(getTeamList().values());

		for (Team team1 : teams) {
			for (Team team2 : teams) {
				if (getPlayerCountFromTeam(team1) != getPlayerCountFromTeam(team2)) {
					return false;
				}
			}
		}

		return true;
	}

	public void distributePlayersWithLimits(HashMap<Team, Integer> teamVerteilung, ArrayList<Player> list) {
		if(getVoteTeam() != null)
			for(Entry<Player, Team> voted : getVoteTeam().getAllVotes())
				getTeamList().put(voted.getKey(), voted.getValue());

		Collections.shuffle(list);
		Iterator<Player> players = list.iterator();
		while(players.hasNext()) {
			Player player = players.next();
			if (getTeamList().containsKey(player))
				continue;
			List<Entry<Team, Integer>> playerPerTeam = calculateLowestTeams(teamVerteilung.keySet().toArray(new Team[0]), false);
			for (Entry<Team, Integer> team : playerPerTeam) {
				if (team == null || team.getValue() >= teamVerteilung.get(team.getKey()))
					continue;
				teamList.put(player, team.getKey());
				break;
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void RestartQuit(PlayerQuitEvent ev) {
		if (teamList.containsKey(ev.getPlayer())) {
			delTeam(ev.getPlayer());
		}
		if (isState(GameState.Restart) || isState(GameState.LobbyPhase))
			return;
		getGameList().addPlayer(ev.getPlayer(), PlayerState.SPECTATOR);
		if (hasLastTeam() && (getState() == GameState.InGame || getState() == GameState.DeathMatch)) {
			setState(GameState.Restart, GameStateChangeReason.LAST_TEAM);
		} else if (getGameList().getPlayers(PlayerState.INGAME).size() <= 1) {
			setState(GameState.Restart, GameStateChangeReason.LAST_PLAYER);
		}
	}

	@EventHandler
	public void SpectaterAndRespawn(PlayerRespawnEvent ev) {
		if (getGameList().isPlayerState(ev.getPlayer()) == PlayerState.SPECTATOR) {
			if (!getGameList().getPlayers(PlayerState.INGAME).isEmpty()) {
				ev.setRespawnLocation(getGameList().getPlayers(PlayerState.INGAME).get(0).getLocation());
			}
			setSpectator(ev, ev.getPlayer());
		}
	}

	public void setSpectator(PlayerRespawnEvent ev, Player player) {
		if (spec == null)
			spec = new AddonSpectator(this);
		delTeam(player);
		getGameList().addPlayer(player, PlayerState.SPECTATOR);
		getManager().clear(player);
		List<Player> l = getGameList().getPlayers(PlayerState.INGAME);
		if (l.size() > 1) {
			if (ev == null) {
				player.teleport(l.get(UtilMath.randomInteger(l.size())).getLocation().add(0.0D, 3.5D, 0.0D));
			} else {
				ev.setRespawnLocation(l.get(UtilMath.randomInteger(l.size())).getLocation().add(0.0D, 3.5D, 0.0D));
			}
		} else {
			if (ev == null) {
				player.teleport(getManager().getLobby());
			} else {
				ev.setRespawnLocation(getManager().getLobby());
			}
			setState(GameState.Restart, GameStateChangeReason.LAST_PLAYER);
		}
		player.setGameMode(GameMode.SPECTATOR);
		player.setAllowFlight(true);
		player.setFlying(true);
		player.setFlySpeed(0.1F);
		for (Player p : UtilServer.getPlayers()) {
			p.hidePlayer(player);
		}
		if (getCompass() == null)
			setCompass(new AddonSpecCompass(this));
		player.getInventory().addItem(getCompass().getCompassItem());
		player.getInventory().setItem(8, UtilItem.RenameItem(new ItemStack(385), "§aZurück zur Lobby"));

		if (hasLastTeam() && (getState() == GameState.InGame || getState() == GameState.DeathMatch)) {
			setState(GameState.Restart, GameStateChangeReason.LAST_TEAM);
		} else if (getGameList().getPlayers(PlayerState.INGAME).size() <= 1) {
			setState(GameState.Restart, GameStateChangeReason.LAST_PLAYER);
		}

		Bukkit.getPluginManager().callEvent(new KitShopPlayerDeleteEvent(player));
		getMoney().save(player);
	}

	/*//Not needed See below
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void handleArrowDamage(EntityDamageByEntityEvent ev) {
		if (ev.getEntity() instanceof Player && ev.getDamager() instanceof Arrow) {
			Arrow a = (Arrow) ev.getDamager();
			if (!(a.getShooter() instanceof Player))
				return;
			Player d = (Player) a.getShooter();
			Player v = (Player) ev.getEntity();
			if (!teamDamageSelfEnabled && getTeam(d) == getTeam(v)) {
				if (getManager().getService().isDebug())
					System.err.println("[TeamGame] Cancelled TRUE bei DamageTeamSelf Projectile");
				ev.setCancelled(true);
			} else if (!teamDamageOtherEnabled && getTeam(d) != getTeam(v)) {
				if (getManager().getService().isDebug())
					System.err.println("[TeamGame] Cancelled TRUE bei DamageTeamOther Projectile");
				ev.setCancelled(true);
			}
		}
	}
	*/

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void handleTeamDamage(EntityDamageByEntityEvent ev) {
		Player entity = getPlayerFromDamager(ev.getEntity());
		Player damager = getPlayerFromDamager(ev.getDamager());

		if (entity != null && damager != null) {
			if (!teamDamageSelfEnabled && getTeam(damager) == getTeam(entity)) {
				if (getManager().getService().isDebug())
					System.err.println("[TeamGame] Cancelled TRUE bei DamageTeamSelf");
				ev.setCancelled(true);
			} else if (!teamDamageOtherEnabled && getTeam(damager) != getTeam(entity)) {
				if (getManager().getService().isDebug())
					System.err.println("[TeamGame] Cancelled TRUE bei DamageTeamOther");
				ev.setCancelled(true);
			}
		}
	}

	private Player getPlayerFromDamager(Entity e){
		if(e instanceof Player)
			return (Player) e;
		if(e instanceof Projectile){
			Projectile projectile = (Projectile) e;
			if(projectile.getShooter() instanceof Player)
				return (Player) projectile.getShooter();
		}
		return null;
	}
}
