package eu.epicpvp.karcade.Game.Single.Games.TroubleInMinecraft.Command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import dev.wolveringer.dataserver.gamestats.GameState;
import dev.wolveringer.dataserver.gamestats.StatsKey;
import eu.epicpvp.karcade.Game.Single.Games.TroubleInMinecraft.TroubleInMinecraft;
import eu.epicpvp.kcore.Command.CommandHandler.Sender;
import eu.epicpvp.kcore.Enum.Team;
import eu.epicpvp.kcore.Translation.TranslationManager;
import eu.epicpvp.kcore.Util.UtilPlayer;

public class CommandDetective implements CommandExecutor, Listener{
	
	TroubleInMinecraft TTT;
	
	public CommandDetective(TroubleInMinecraft TTT){
		this.TTT=TTT;
		Bukkit.getPluginManager().registerEvents(this, TTT.getManager().getInstance());
	}
	
	@eu.epicpvp.kcore.Command.CommandHandler.Command(command = "detective", sender = Sender.PLAYER)
	public boolean onCommand(CommandSender cs, Command cmd, String arg2,String[] args) {
		
		if(TTT.getState()!=GameState.LobbyPhase){
			UtilPlayer.sendMessage(((Player)cs),TranslationManager.getText(((Player)cs), "PREFIX_GAME", TTT.getType().getTyp())+TranslationManager.getText(((Player)cs), "TTT_PASSE_LOBBYPHASE"));
			return false;
		}
		int t_p = TTT.getStats().getInt(StatsKey.TTT_PAESSE, ((Player)cs));
		if(!(t_p>0)){
			UtilPlayer.sendMessage(((Player)cs),TranslationManager.getText(((Player)cs), "PREFIX_GAME", TTT.getType().getTyp())+TranslationManager.getText(((Player)cs), "TTT_PASSE_KEINE","Detective"));
			return false;
		}
		
		int t = TTT.getDetective();
		int tt = TTT.isInTeam(Team.DETECTIVE);
		if(tt>=t){
			UtilPlayer.sendMessage(((Player)cs),TranslationManager.getText(((Player)cs), "PREFIX_GAME", TTT.getType().getTyp())+TranslationManager.getText(((Player)cs), "TTT_PASSE_MAX_USED","Detective"));
			return false;
		}
		t_p=t_p-1;
		TTT.getStats().setInt( ((Player)cs) , t_p, StatsKey.TTT_PAESSE);
		TTT.addTeam(((Player)cs) , Team.DETECTIVE);
		UtilPlayer.sendMessage(((Player)cs),TranslationManager.getText(((Player)cs), "PREFIX_GAME", TTT.getType().getTyp())+TranslationManager.getText(((Player)cs), "TTT_PASSE_USE",new String[]{"Detective",String.valueOf(t_p)}));
		return false;
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void Quit(PlayerQuitEvent ev){
		if(TTT.getState()!=GameState.LobbyPhase)return;
		if(TTT.getTeamList().containsKey(ev.getPlayer())){
			Team t = TTT.getTeamList().get(ev.getPlayer());
			if(t==Team.DETECTIVE){
				TTT.getTeamList().remove(ev.getPlayer());
				TTT.getStats().addInt(ev.getPlayer() ,1, StatsKey.TTT_PAESSE);
			}
		}
	}

}
