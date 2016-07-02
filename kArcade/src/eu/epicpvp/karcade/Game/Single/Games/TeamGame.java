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
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import de.schlichtherle.util.Arrays;
import dev.wolveringer.dataserver.gamestats.GameState;
import eu.epicpvp.karcade.kArcadeManager;
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

	public TeamGame(kArcadeManager manager) {
		super(manager);
	}

	public void addTeam(Player p, Team t) {
		teamList.put(p, t);
		Bukkit.getPluginManager().callEvent(new TeamAddEvent(p, t));
	}

	public String getERR(Team[] t, int size) {
		String s = "";
		HashMap<Team, Integer> l = verteilung(t, size);
		for (Team te : l.keySet()) {
			s = s + "TEAM:" + te.getDisplayName() + "/" + l.get(te);
		}
		s = s + "  -  ";
		for (Player p : teamList.keySet()) {
			s = s + "PLAYER:" + p.getName() + "-" + teamList.get(p).getDisplayName();
		}

		return s;
	}

	public HashMap<Team, Integer> verteilung(Team[] t, int size) {
		if (size == 1) {
			HashMap<Team, Integer> list = new HashMap<>();
			for (Team team : t)
				list.put(team, 1);
			return list;
		} else if (size == 2) {
			HashMap<Team, Integer> list = new HashMap<>();
			for (Team team : t)
				list.put(team, 2);
			return list;
		} else {
			HashMap<Team, Integer> list = new HashMap<>();
			Collection<? extends Player> l = UtilServer.getPlayers();

			for (Team team : t) {
				list.put(team, l.size() / t.length);
			}

			if (l.size() % t.length != 0) {
				list.remove(t[0]);
				list.put(t[0], (l.size() / t.length) + 1);
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

	public Team littleTeam() {
		return calculateLowestTeam(true);
	}

	public Team calculateLowestTeam(boolean returnNullBySame) {
		return calculateLowestTeam(teamList.values().toArray(new Team[0]),returnNullBySame);
	}
	
	public Team calculateLowestTeam(Team[] teams,boolean returnNullBySame) {
		HashMap<Team, Integer> playerCount = new HashMap<>();
		for(Team t : teams)
			playerCount.put(t, 0);
		for(Entry<Player, Team> t : teamList.entrySet()){
			if(playerCount.containsKey(t.getValue()))
				playerCount.put(t.getValue(), playerCount.get(t.getValue())+1);
		}
		List<Entry<Team, Integer>> playerCountEntries = new ArrayList<>(playerCount.entrySet());
		Collections.sort(playerCountEntries,new Comparator<Entry<Team, Integer>>() {
			@Override
			public int compare(Entry<Team, Integer> o1, Entry<Team, Integer> o2) {
				return Integer.compare(o1.getValue(), o2.getValue());
			}
		});
		if(playerCountEntries.isEmpty())
			if(returnNullBySame)
				return null;
			else
				return Team.RED;
		return playerCountEntries.get(0).getKey();
		/*
		Team t = null;

		for (Team team : teams) {
			t = team;
			for (Team team1 : teams) {
				if (isInTeam(t) > isInTeam(team1)) {
					t = null;
					break;
				}
			}
			if (t != null) {
				break;
			}
		}
		if (t == null && !returnNullBySame)
			t = Team.RED;
		return t;
		
		*/
	}

	public void delTeam(Player p) {
		if (teamList.containsKey(p)) {
			Bukkit.getPluginManager().callEvent(new TeamDelEvent(p, getTeam(p)));
			teamList.remove(p);
		}
	}

	public Team lastTeam() {
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
			SetSpectator(null, ev.getPlayer());
		}
	}

	public boolean islastTeam() {
		Team t;
		for (Player p : teamList.keySet()) {
			t = teamList.get(p);
			for (Player p1 : teamList.keySet()) {
				if (teamList.get(p1) != t) {
					return false;
				}
			}
		}
		return true;
	}

	public Team getTeam(Player p) {
		if (teamList.containsKey(p)) {
			return teamList.get(p);
		}
		return null;
	}

	public ArrayList<Player> getPlayersFromTeam(Team t) {
		ArrayList<Player> list = new ArrayList<>();
		for (Player p : teamList.keySet()) {
			if (teamList.get(p) == t) {
				list.add(p);
			}
		}
		return list;
	}

	public int isInTeam(Team t) {
		int i = 0;

		for (Player p : teamList.keySet()) {
			if (teamList.get(p) == t) {
				i++;
			}
		}

		return i;
	}

	public void updateTab(Team[] teams) {
		if (getScoreboard() == null)
			setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		for (Team team : teams) {
			ArrayList<Player> list = getPlayersFromTeam(team);

			UtilScoreboard.addTeam(getScoreboard(), team.getDisplayName(), team.getColor());

			for (Player player : list) {
				if (getManager().getNickManager() != null && getManager().getNickManager().getNicks().containsKey(player.getEntityId())) {
					getScoreboard().getTeam(team.getDisplayName()).addEntry(getManager().getNickManager().getNicks().get(player.getEntityId()).getName());
					player.setPlayerListName(team.getColor()+getManager().getNickManager().getNicks().get(player.getEntityId()).getName());
					continue;
				}
				getScoreboard().getTeam(team.getDisplayName()).addPlayer(player);
				player.setPlayerListName(team.getColor()+player.getName());
			}

			for (Player p : list) {
				if (!getScoreboard().getPlayers().contains(p)) {
					p.setScoreboard(getScoreboard());
				}
			}
		}

	}

	public int r(int i) {
		if (i == 1)
			return 0;
		return UtilMath.RandomInt((i - 1), 0);
	}

	public void distributePlayers(Team[] teams, ArrayList<Player> list) {
		/*
		if (getVoteTeam() != null) {
			for (Player player : getVoteTeam().getVote().keySet()) {
				if (list.contains(player)) {
					getTeamList().put(player, getVoteTeam().getVote().get(player));
					list.remove(player);
				}
			}
		}
		*/
		Collections.shuffle(list);
		Iterator<Player> players = list.iterator();

		while (players.hasNext()) {
			Player p = players.next();
			if(getVoteTeam().getVote().containsKey(p)){
				getTeamList().put(p, getVoteTeam().getVote().get(p));
				continue;
			}
			if(getTeamList().containsKey(p)){ //Whjyever (Not possiable)
				continue;
			}
			Team lowest = calculateLowestTeam(teams, false);
			getTeamList().put(p, lowest);
		}
		
		/*
		Player player;
		for (int i = 0; i < list.size(); i++) {
			if (list.isEmpty())
				break;
			player = list.get(i);
			if (getTeamList().containsKey(player))
				continue;

			if (!isSetTeam(teams)) {
				for (Team t : teams) {
					if (!isSetTeam(t)) {
						getTeamList().put(player, t);
						break;
					}
				}
				continue;
			}

			Team team = calculateLowestTeam(false);
			if (team != null) {
				getTeamList().put(player, team);
			} else {
				getTeamList().put(player, teams[0]);
			}
		}
		*/
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
				if (isInTeam(team1) != isInTeam(team2)) {
					return false;
				}
			}
		}

		return true;
	}

	public void PlayerVerteilung(HashMap<Team, Integer> teamVerteilung, ArrayList<Player> list) {
		if (getVoteTeam() != null) {
			for (Player player : getVoteTeam().getVote().keySet()) {
				if (list.contains(player)) {
					getTeamList().put(player, getVoteTeam().getVote().get(player));
					list.remove(player);
				}
			}
		}

		Collections.shuffle(list);
		Player player;
		for (int i = 0; i < list.size(); i++) {
			if (list.isEmpty())
				break;
			player = list.get(i);

			if (getTeamList().containsKey(player))
				continue;
			for (Team team : teamVerteilung.keySet()) {
				if (isInTeam(team) >= teamVerteilung.get(team))
					continue;
				addTeam(player, team);
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
		if (islastTeam() && (getState() == GameState.InGame || getState() == GameState.DeathMatch)) {
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
			SetSpectator(ev, ev.getPlayer());
		}
	}

	public void SetSpectator(PlayerRespawnEvent ev, Player player) {
		if (spec == null)
			spec = new AddonSpectator(this);
		delTeam(player);
		getGameList().addPlayer(player, PlayerState.SPECTATOR);
		getManager().Clear(player);
		List<Player> l = getGameList().getPlayers(PlayerState.INGAME);
		if (l.size() > 1) {
			if (ev == null) {
				player.teleport(l.get(UtilMath.r(l.size())).getLocation().add(0.0D, 3.5D, 0.0D));
			} else {
				ev.setRespawnLocation(l.get(UtilMath.r(l.size())).getLocation().add(0.0D, 3.5D, 0.0D));
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

		if (islastTeam() && (getState() == GameState.InGame || getState() == GameState.DeathMatch)) {
			setState(GameState.Restart, GameStateChangeReason.LAST_TEAM);
		} else if (getGameList().getPlayers(PlayerState.INGAME).size() <= 1) {
			setState(GameState.Restart, GameStateChangeReason.LAST_PLAYER);
		}

		Bukkit.getPluginManager().callEvent(new KitShopPlayerDeleteEvent(player));
		getMoney().save(player);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void arrow_damage(EntityDamageByEntityEvent ev) {
		if (ev.getEntity() instanceof Player && ev.getDamager() instanceof Arrow) {
			Arrow a = (Arrow) ev.getDamager();
			if (!(a.getShooter() instanceof Player))
				return;
			Player d = (Player) a.getShooter();
			Player v = (Player) ev.getEntity();
			if (!DamageTeamSelf && getTeam(d) == getTeam(v)) {
				if (getManager().getService().isDebug())
					System.err.println("[TeamGame] Cancelled TRUE bei DamageTeamSelf Projectile");
				ev.setCancelled(true);
			} else if (!DamageTeamOther && getTeam(d) != getTeam(v)) {
				if (getManager().getService().isDebug())
					System.err.println("[TeamGame] Cancelled TRUE bei DamageTeamOther Projectile");
				ev.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void TeamDamage(EntityDamageByEntityEvent ev) {
		if ((ev.getEntity() instanceof Player && ev.getDamager() instanceof Player)) {
			if (!DamageTeamSelf && getTeam((Player) ev.getDamager()) == getTeam((Player) ev.getEntity())) {
				if (getManager().getService().isDebug())
					System.err.println("[TeamGame] Cancelled TRUE bei DamageTeamSelf");
				ev.setCancelled(true);
			} else if (!DamageTeamOther && getTeam((Player) ev.getDamager()) != getTeam((Player) ev.getEntity())) {
				if (getManager().getService().isDebug())
					System.err.println("[TeamGame] Cancelled TRUE bei DamageTeamOther");
				ev.setCancelled(true);
			}
		}
	}

}
