package me.kingingo.karcade.Game.Single.addons;

import lombok.Getter;
import me.kingingo.karcade.Enum.PlayerState;
import me.kingingo.karcade.Game.Single.SingleGame;
import me.kingingo.karcade.Game.Single.Games.TeamGame;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Language.Language;
import me.kingingo.kcore.Listener.kListener;
import me.kingingo.kcore.Permission.kPermission;
import me.kingingo.kcore.Util.Color;
import me.kingingo.kcore.Util.UtilPlayer;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.kcore.Util.UtilString;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class AddonChat extends kListener{

	@Getter
	private boolean spectator=true;
	@Getter
	private SingleGame game = null;
	@Getter
	private TeamGame team_game=null;
	
	public AddonChat(SingleGame game,boolean spec_chat){
		super(game.getManager().getInstance(),"[SpectatorChat]");
		this.spectator=spec_chat;
	}
	
	public AddonChat(TeamGame team_game,boolean spec_chat){
		super(team_game.getManager().getInstance(),"[SpectatorChat]");
		this.team_game=team_game;
		this.spectator=spec_chat;
	}
	
	@EventHandler
	public void Chat(AsyncPlayerChatEvent ev){
		ev.setCancelled(true);
		
		 if((!ev.getPlayer().hasPermission(kPermission.CHAT_LINK.getPermissionToString()))&&UtilString.isBadWord(ev.getMessage())||UtilString.checkForIP(ev.getMessage())){
				ev.setMessage("Ich heul rum!");
				ev.getPlayer().sendMessage(Language.getText(ev.getPlayer(), "PREFIX")+Language.getText(ev.getPlayer(), "CHAT_MESSAGE_BLOCK"));
		}
		
		if(game.getState()==GameState.LobbyPhase){
			UtilServer.broadcast(game.getManager().getPermManager().getPrefix(ev.getPlayer())+ev.getPlayer().getDisplayName()+"�7:�f "+ev.getMessage());
		}else{
			if(team_game!=null){
				if(team_game.getGameList().isPlayerState(ev.getPlayer())==PlayerState.IN){
					if(spectator){
						for(Player p : team_game.getGameList().getPlayers(PlayerState.IN)){
							p.sendMessage(Color.GRAY+ev.getPlayer().getDisplayName()+"�7:�f "+ev.getMessage());
						}
					}else{
						UtilServer.broadcast(team_game.getTeam(ev.getPlayer()).getColor()+team_game.getTeam(ev.getPlayer()).Name()+" "+ev.getPlayer().getDisplayName()+"�7:�f "+ev.getMessage());
					}
				}else{
					if(spectator){
						for(Player p : team_game.getGameList().getPlayers(PlayerState.OUT)){
							p.sendMessage(Color.ORANGE+ev.getPlayer().getDisplayName()+"�7:�f "+ev.getMessage());
						}
					}else{
						UtilPlayer.sendMessage(ev.getPlayer(),Language.getText(ev.getPlayer(), "PREFIX_GAME",team_game.getType().getTyp())+Language.getText(ev.getPlayer(), "SPECTATOR_CHAT_CANCEL"));
					}
				}
			}else{
				if(game.getGameList().isPlayerState(ev.getPlayer())==PlayerState.IN){
					if(spectator){
						for(Player p : game.getGameList().getPlayers(PlayerState.IN)){
							p.sendMessage(Color.GRAY+ev.getPlayer().getDisplayName()+"�7:�f "+ev.getMessage());
						}
					}else{
						UtilServer.broadcast(game.getManager().getPermManager().getPrefix(ev.getPlayer())+ev.getPlayer().getDisplayName()+"�7:�f "+ev.getMessage());
					}
				}else{
					if(spectator){
						for(Player p : game.getGameList().getPlayers(PlayerState.OUT)){
							p.sendMessage(Color.ORANGE+ev.getPlayer().getDisplayName()+":�7:�f "+ev.getMessage());
						}
					}else{
						UtilPlayer.sendMessage(ev.getPlayer(),Language.getText(ev.getPlayer(), "PREFIX_GAME",team_game.getType().getTyp())+Language.getText(ev.getPlayer(), "SPECTATOR_CHAT_CANCEL"));
					}
				}
			}
		}
	}
	
}
