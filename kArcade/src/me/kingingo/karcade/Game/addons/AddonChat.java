package me.kingingo.karcade.Game.addons;

import lombok.Getter;
import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Enum.PlayerState;
import me.kingingo.karcade.Game.Games.TeamGame;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.Listener.kListener;
import me.kingingo.kcore.Util.C;
import me.kingingo.kcore.Util.UtilPlayer;
import me.kingingo.kcore.Util.UtilServer;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class AddonChat extends kListener{

	@Getter
	private kArcadeManager manager;
	@Getter
	private boolean spectator=true;
	@Getter
	private TeamGame game = null;
	
	public AddonChat(kArcadeManager manager,boolean spec_chat){
		super(manager.getInstance(),"[SpectatorChat]");
		this.manager=manager;
		this.spectator=spec_chat;
	}
	
	public AddonChat(TeamGame game,boolean spec_chat){
		super(game.getManager().getInstance(),"[SpectatorChat]");
		this.game=game;
		this.manager=game.getManager();
		this.spectator=spec_chat;
	}
	
	@EventHandler
	public void Chat(AsyncPlayerChatEvent ev){
		ev.setCancelled(true);
		if(manager.getState()==GameState.LobbyPhase){
			UtilServer.broadcast(getManager().getPermManager().getPrefix(ev.getPlayer())+ev.getPlayer().getDisplayName()+":§7 "+ev.getMessage());
		}else{
			if(game!=null){
				if(manager.getGame().getGameList().isPlayerState(ev.getPlayer())==PlayerState.IN){
					if(spectator){
						for(Player p : manager.getGame().getGameList().getPlayers(PlayerState.IN)){
							p.sendMessage(C.cGray+ev.getPlayer().getDisplayName()+": "+ev.getMessage());
						}
					}else{
						UtilServer.broadcast(game.getTeam(ev.getPlayer()).getColor()+game.getTeam(ev.getPlayer()).Name()+" "+ev.getPlayer().getDisplayName()+":§7 "+ev.getMessage());
					}
				}else{
					if(spectator){
						for(Player p : manager.getGame().getGameList().getPlayers(PlayerState.OUT)){
							p.sendMessage(C.mOrange+ev.getPlayer().getDisplayName()+":§7 "+ev.getMessage());
						}
					}else{
						UtilPlayer.sendMessage(ev.getPlayer(),Text.PREFIX_GAME.getText(getManager().getGame().getType().getTyp())+Text.SPECTATOR_CHAT_CANCEL.getText());
					}
				}
			}else{
				if(manager.getGame().getGameList().isPlayerState(ev.getPlayer())==PlayerState.IN){
					if(spectator){
						for(Player p : manager.getGame().getGameList().getPlayers(PlayerState.IN)){
							p.sendMessage(C.cGray+ev.getPlayer().getDisplayName()+": "+ev.getMessage());
						}
					}else{
						UtilServer.broadcast(getManager().getPermManager().getPrefix(ev.getPlayer())+ev.getPlayer().getDisplayName()+":§7 "+ev.getMessage());
					}
				}else{
					if(spectator){
						for(Player p : manager.getGame().getGameList().getPlayers(PlayerState.OUT)){
							p.sendMessage(C.mOrange+ev.getPlayer().getDisplayName()+":§7 "+ev.getMessage());
						}
					}else{
						UtilPlayer.sendMessage(ev.getPlayer(),Text.PREFIX_GAME.getText(getManager().getGame().getType().getTyp())+Text.SPECTATOR_CHAT_CANCEL.getText());
					}
				}
			}
		}
	}
	
}
