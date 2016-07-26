package eu.epicpvp.karcade.Game.Single.Addons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import dev.wolveringer.client.LoadedPlayer;
import dev.wolveringer.dataserver.gamestats.GameState;
import eu.epicpvp.karcade.Game.Single.SingleGame;
import eu.epicpvp.karcade.Game.Single.Events.AddonVoteTeamPlayerChooseEvent;
import eu.epicpvp.kcore.Enum.PlayerState;
import eu.epicpvp.kcore.Enum.Team;
import eu.epicpvp.kcore.Permission.PermissionManager;
import eu.epicpvp.kcore.Translation.TranslationHandler;
import eu.epicpvp.kcore.Util.InventorySize;
import eu.epicpvp.kcore.Util.UtilEvent;
import eu.epicpvp.kcore.Util.UtilEvent.ActionType;
import eu.epicpvp.kcore.Util.UtilInv;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.kcore.Util.UtilPlayer;
import eu.epicpvp.kcore.Util.UtilServer;
import lombok.Getter;

public class AddonVoteTeam implements Listener {

	@Getter
	private SingleGame game;
	private Team[] list;
	private int invSize = 0;
	private int maxPlayerPerTeam = 3;
	@Getter
	private HashMap<Team, Integer> invslot = new HashMap<>();
	@Getter
	private HashMap<Player, Team> vote = new HashMap<>();

	private HashMap<Player, Inventory> openViews = new HashMap<>();

	public AddonVoteTeam(SingleGame game, Team[] list, InventorySize size, int MaxInTeam) {
		this.game = game;
		this.list = list;
		this.maxPlayerPerTeam = MaxInTeam;
		this.invSize = size.getSize();
		Bukkit.getPluginManager().registerEvents(this, this.game.getManager().getInstance());

		int slot = 0;
		if (list.length == 2) {
			slot = 2;
			for (Team t : list) {
				invslot.put(t, slot);
				slot = 6;
			}

		} else if (list.length == 4) {
			for (Team t : list) {
				slot++;
				invslot.put(t, slot);
				slot++;
			}

		} else {
			slot = 0;
			for (Team t : list) {
				invslot.put(t, slot);
				slot++;
			}
		}
	}

	@EventHandler
	public void Join(PlayerJoinEvent ev) {
		if (game.getState() != GameState.LobbyPhase)
			return;
		ev.getPlayer().getInventory().addItem(UtilItem.RenameItem(new ItemStack(Material.PAPER, 1), TranslationHandler.getText(ev.getPlayer(), "GAME_TEAM_ITEM")));
	}

	@EventHandler
	public void Interact(PlayerInteractEvent ev) {
		if (game.getState() != GameState.LobbyPhase)
			return;
		if (this.game == null)
			return;
		if (ev.getPlayer().getItemInHand() == null)
			return;
		if (UtilEvent.isAction(ev, ActionType.RIGHT) && ev.getPlayer().getItemInHand().getType() == Material.PAPER) {
			Inventory inv = buildVoteInventory(ev.getPlayer());
			openViews.put(ev.getPlayer(), inv);
			ev.getPlayer().openInventory(inv);
			ev.setCancelled(true);
		}
	}

	@EventHandler
	public void a(InventoryCloseEvent e) {
		openViews.remove(e.getPlayer());
	}

	@EventHandler
	public void a(PlayerQuitEvent e) {
		openViews.remove(e.getPlayer());
	}

	@EventHandler
	public void Inv(InventoryClickEvent ev) {
		if (game.getState() != GameState.LobbyPhase)
			return;
		if (!(ev.getWhoClicked() instanceof Player) || ev.getInventory() == null || ev.getCursor() == null || ev.getCurrentItem() == null)
			return;
		if (ev.getInventory().getName().startsWith("§lVote:")) {
			Player player = (Player) ev.getWhoClicked();

			ev.setCancelled(true);
			openViews.remove(player);
			player.closeInventory();

			Team oldTeam = null;
			/*
			if (UtilServer.getPlayers().size() <= game.getMin_Players()) {
				UtilPlayer.sendMessage(player, TranslationHandler.getText(player, "PREFIX_GAME", game.getType().getTyp()) + TranslationHandler.getText(player, "VOTE_TEAM_MIN_PLAYER", game.getMin_Players() + 1));
				return;
			}
			*/

			if (vote.containsKey(player)) {
				UtilPlayer.sendMessage(player, TranslationHandler.getText(player, "PREFIX_GAME", game.getType().getTyp()) + TranslationHandler.getText(player, "VOTE_TEAM_REMOVE", vote.get(player).getDisplayName()));
				oldTeam = vote.get(player);
				vote.remove(player);
				updateTeam(oldTeam);
			}

			Team clickedTeam = null;
			for (Entry<Team, Integer> slots : invslot.entrySet())
				if (slots.getValue() == ev.getSlot()) {
					clickedTeam = slots.getKey();
					break;
				}
			if (clickedTeam != null) {
				HashMap<Team, Integer> votes = new HashMap<>();
				for (Team t : list)
					votes.put(t, 0);
				for (Entry<Player, Team> e : vote.entrySet())
					votes.put(e.getValue(), votes.get(e.getValue()) + 1);
				int minVote = list.length * maxPlayerPerTeam;
				for (Entry<Team, Integer> e : votes.entrySet())
					if (e.getValue() < minVote)
						minVote = e.getValue();

				int notChoosen = 0;
				for (Player p : UtilServer.getPlayers())
					if (!votes.containsKey(p))
						notChoosen++;

				if (votes.get(clickedTeam) > minVote /*+ (notChoosen / list.length)*/) {
					player.sendMessage("§cDu kannst dieses Team nicht betreten da die Teams sonnst unausgeglichen werden würden.");
				} else {
					if (votes.get(clickedTeam) >= maxPlayerPerTeam) {
						UtilPlayer.sendMessage(player, TranslationHandler.getText(player, "PREFIX_GAME", game.getType().getTyp()) + clickedTeam.getColor() + TranslationHandler.getText(player, "VOTE_TEAM_FULL", clickedTeam.getColor() + clickedTeam.getDisplayName()));
					} else {
						vote.put(player, clickedTeam);
						updateTeam(clickedTeam);
						Bukkit.getPluginManager().callEvent(new AddonVoteTeamPlayerChooseEvent(player, clickedTeam, PlayerState.INGAME));
						UtilPlayer.sendMessage(player, TranslationHandler.getText(player, "PREFIX_GAME", game.getType().getTyp()) + clickedTeam.getColor() + TranslationHandler.getText(player, "VOTE_TEAM_ADD", clickedTeam.getColor() + clickedTeam.getDisplayName()));
					}
				}
			}
			/*
			for (Team t : list) {
				if (UtilGear.isMat(ev.getCurrentItem(), t.getItem().getType()) && ev.getCurrentItem().getItemMeta().hasDisplayName() && ev.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(t.getItem().getItemMeta().getDisplayName())) {
					if (isTeam(t)) {
						if (isVotet(t) + 1 != UtilServer.getPlayers().size()) {
							vote.put(player, t);
							updateTeam(t);
							Bukkit.getPluginManager().callEvent(new AddonVoteTeamPlayerChooseEvent(player, t, PlayerState.INGAME));
							UtilPlayer.sendMessage(player, TranslationHandler.getText(player, "PREFIX_GAME", game.getType().getTyp()) + t.getColor() + TranslationHandler.getText(player, "VOTE_TEAM_ADD", t.getColor() + t.getDisplayName()));
						}
					} else {
						UtilPlayer.sendMessage(player, TranslationHandler.getText(player, "PREFIX_GAME", game.getType().getTyp()) + t.getColor() + TranslationHandler.getText(player, "VOTE_TEAM_FULL", t.getColor() + t.getDisplayName()));
					}
					break;
				}
			}
			*/

			if (!vote.containsKey(player)) {
				Bukkit.getPluginManager().callEvent(new AddonVoteTeamPlayerChooseEvent(player, oldTeam, PlayerState.SPECTATOR));
				oldTeam = null;
			}
		}
	}

	public int getVotesFromTeam(Team t) {
		int i = 0;
		for (Player p : vote.keySet()) {
			if (vote.get(p) == t) {
				i++;
			}
		}
		return i;
	}

	public void updateTeam(Team t) {
		int teamSlot = invslot.get(t);
		for (Entry<Player, Inventory> view : openViews.entrySet()) {
			view.getValue().setItem(teamSlot, buildTeamItem(t, view.getKey()));
		}
	}

	public ItemStack buildTeamItem(Team t, Player player) {
		ItemStack is = t.getItem();
		List<String> l = new ArrayList<>();
		l.add("§c" + getVotesFromTeam(t) + "§7/§c" + maxPlayerPerTeam);
		l.add("§7---------------");

		int playerTeamIndex = 1;
		for (Player p : vote.keySet()) {
			if (vote.get(p) == t) {
				LoadedPlayer lp = UtilServer.getClient().getPlayerAndLoad(p.getName());
				if (p.equals(player)) {
					is = UtilItem.setGlowing(is, true);
					l.add("§6" + playerTeamIndex + ".§a " + p.getName());
				} else
					l.add("§6" + playerTeamIndex + ".§7 " + (lp.hasNickname() ? PermissionManager.getManager().hasPermission(player, "nick.showunnicked") ? p.getName() : lp.getNickname() : p.getName()));
				playerTeamIndex++;
			}
		}
		UtilItem.setLore(is, l);
		return is;
	}

	public boolean isTeam(Team t) {
		if (getVotesFromTeam(t) < this.maxPlayerPerTeam) {
			return true;
		}
		return false;
	}

	public Inventory buildVoteInventory(Player player) {
		Inventory inv = Bukkit.createInventory(null, invSize, "§lVote:");
		for (Team t : list) {
			inv.setItem(invslot.get(t), buildTeamItem(t, player));
		}
		for (int i = 0; i < inv.getSize(); i++) {
			if (inv.getItem(i) == null || inv.getItem(i).getType() == Material.AIR) {
				if (inv.getItem(i) == null)
					inv.setItem(i, new ItemStack(Material.IRON_FENCE));
				inv.getItem(i).setType(Material.STAINED_GLASS_PANE);
				inv.getItem(i).setDurability((byte) 7);
				ItemMeta im = inv.getItem(i).getItemMeta();
				im.setDisplayName(" ");
				inv.getItem(i).setItemMeta(im);
			}
		}

		return inv;
	}

	@EventHandler
	public void Quit(PlayerQuitEvent ev) {
		if (game.getState() != GameState.LobbyPhase)
			return;
		if (getVote().containsKey(ev.getPlayer())) {
			Team t = vote.get(ev.getPlayer());
			vote.remove(ev.getPlayer());
			updateTeam(t);
		}
	}

	@EventHandler
	public void DropItem(PlayerDropItemEvent event) {
		if (game.getState() != GameState.LobbyPhase)
			return;
		if ((this.game == null)) {
			return;
		}
		if (!UtilInv.IsItem(event.getItemDrop().getItemStack(), Material.PAPER, (byte) 0)) {
			return;
		}
		event.setCancelled(true);
	}

}
