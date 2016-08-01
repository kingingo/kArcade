package eu.epicpvp.karcade.Game.Single.Addons;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import dev.wolveringer.dataserver.gamestats.GameState;
import eu.epicpvp.karcade.Game.Single.SingleGame;
import eu.epicpvp.karcade.Game.Single.Games.TeamGame;
import eu.epicpvp.kcore.Enum.PlayerState;
import eu.epicpvp.kcore.Listener.kListener;
import eu.epicpvp.kcore.Permission.PermissionType;
import eu.epicpvp.kcore.Translation.TranslationHandler;
import eu.epicpvp.kcore.Util.Color;
import eu.epicpvp.kcore.Util.UtilPlayer;
import eu.epicpvp.kcore.Util.UtilServer;
import eu.epicpvp.kcore.Util.UtilString;
import lombok.Getter;

public class AddonChat extends kListener {

	@Getter
	private boolean spectator = true;
	@Getter
	private SingleGame game = null;
	@Getter
	private TeamGame team_game = null;

	public AddonChat(SingleGame game, boolean spec_chat) {
		super(game.getManager().getInstance(), "[SpectatorChat]");
		this.spectator = spec_chat;
	}

	public AddonChat(TeamGame team_game, boolean spec_chat) {
		super(team_game.getManager().getInstance(), "[SpectatorChat]");
		this.team_game = team_game;
		this.spectator = spec_chat;
	}

	@EventHandler
	public void Chat(AsyncPlayerChatEvent ev) {
		ev.setCancelled(true);

		if ((!ev.getPlayer().hasPermission(PermissionType.CHAT_LINK.getPermissionToString())) && UtilString.isBadWord(ev.getMessage()) || UtilString.checkForIP(ev.getMessage())) {
			ev.setMessage("Ich heul rum!");
			ev.getPlayer().sendMessage(TranslationHandler.getText(ev.getPlayer(), "PREFIX") + TranslationHandler.getText(ev.getPlayer(), "CHAT_MESSAGE_BLOCK"));
		}

		if (game.getState() == GameState.LobbyPhase) {
			UtilServer.broadcast(game.getManager().getPermManager().getPrefix(ev.getPlayer()) + ev.getPlayer().getDisplayName() + "§7:§f " + ev.getMessage());
		} else {
			if (team_game != null) {
				if (team_game.getGameList().isPlayerState(ev.getPlayer()) == PlayerState.INGAME) {
					if (spectator) {
						for (Player p : team_game.getGameList().getPlayers(PlayerState.INGAME)) {
							p.sendMessage(Color.GRAY + ev.getPlayer().getName()+"§7:§f " + ev.getMessage());
						}
					} else {
						UtilServer.broadcast(team_game.getTeam(ev.getPlayer()).getColor() + team_game.getTeam(ev.getPlayer()).getDisplayName() + " " + ev.getPlayer().getName()+"§7:§f " + ev.getMessage());
					}
				} else {
					if (spectator) {
						for (Player p : team_game.getGameList().getPlayers(PlayerState.SPECTATOR)) {
							p.sendMessage(Color.ORANGE + ev.getPlayer().getName()+"§7:§f " + ev.getMessage());
						}
					} else {
						UtilPlayer.sendMessage(ev.getPlayer(), TranslationHandler.getText(ev.getPlayer(), "PREFIX_GAME", team_game.getType().getTyp()) + TranslationHandler.getText(ev.getPlayer(), "SPECTATOR_CHAT_CANCEL"));
					}
				}
			} else {
				if (game.getGameList().isPlayerState(ev.getPlayer()) == PlayerState.INGAME) {
					if (spectator) {
						for (Player p : game.getGameList().getPlayers(PlayerState.INGAME)) {
							p.sendMessage(Color.GRAY + ev.getPlayer().getName()+"§7:§f " + ev.getMessage());
						}
					} else {
						UtilServer.broadcast(game.getManager().getPermManager().getPrefix(ev.getPlayer()) + ev.getPlayer().getName()+"§7:§f " + ev.getMessage());
					}
				} else {
					if (spectator) {
						for (Player p : game.getGameList().getPlayers(PlayerState.SPECTATOR)) {
							p.sendMessage(Color.ORANGE + ev.getPlayer().getName()+":§7:§f " + ev.getMessage());
						}
					} else {
						UtilPlayer.sendMessage(ev.getPlayer(), TranslationHandler.getText(ev.getPlayer(), "PREFIX_GAME", team_game.getType().getTyp()) + TranslationHandler.getText(ev.getPlayer(), "SPECTATOR_CHAT_CANCEL"));
					}
				}
			}
		}
	}

}
