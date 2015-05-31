package me.kingingo.karcade.Game.Single.Games.TroubleInMinecraft.Command;

import me.kingingo.karcade.Enum.Team;
import me.kingingo.karcade.Game.Single.Games.TroubleInMinecraft.TroubleInMinecraft;
import me.kingingo.kcore.Command.CommandHandler.Sender;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.StatsManager.Stats;
import me.kingingo.kcore.Util.UtilPlayer;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class CommandDetective implements CommandExecutor, Listener{
	
	TroubleInMinecraft TTT;
	
	public CommandDetective(TroubleInMinecraft TTT){
		this.TTT=TTT;
		Bukkit.getPluginManager().registerEvents(this, TTT.getManager().getInstance());
	}
	
	@me.kingingo.kcore.Command.CommandHandler.Command(command = "detective", sender = Sender.PLAYER)
	public boolean onCommand(CommandSender cs, Command cmd, String arg2,String[] args) {
		
		if(TTT.getState()!=GameState.LobbyPhase){
			UtilPlayer.sendMessage(((Player)cs),Text.PREFIX_GAME.getText(TTT.getType().getTyp())+ Text.TTT_PASSE_LOBBYPHASE.getText());
			return false;
		}
		int t_p = TTT.getStats().getInt(Stats.TTT_PÄSSE, ((Player)cs));
		if(!(t_p>0)){
			UtilPlayer.sendMessage(((Player)cs),Text.PREFIX_GAME.getText(TTT.getType().getTyp())+ Text.TTT_PASSE_KEINE.getText("Detective"));
			return false;
		}
		
		int t = TTT.getDetective();
		int tt = TTT.isInTeam(Team.DETECTIVE);
		if(tt>=t){
			UtilPlayer.sendMessage(((Player)cs),Text.PREFIX_GAME.getText(TTT.getType().getTyp())+ Text.TTT_PASSE_MAX_USED.getText("Detective"));
			return false;
		}
		t_p=t_p-1;
		TTT.getStats().setInt( ((Player)cs) , t_p, Stats.TTT_PÄSSE);
		TTT.addTeam(((Player)cs) , Team.DETECTIVE);
		UtilPlayer.sendMessage(((Player)cs),Text.PREFIX_GAME.getText(TTT.getType().getTyp())+ Text.TTT_PASSE_USE.getText(new String[]{"Detective",String.valueOf(t_p)}));
		return false;
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void Quit(PlayerQuitEvent ev){
		if(TTT.getState()!=GameState.LobbyPhase)return;
		if(TTT.getTeamList().containsKey(ev.getPlayer())){
			Team t = TTT.getTeamList().get(ev.getPlayer());
			if(t==Team.DETECTIVE){
				TTT.getTeamList().remove(ev.getPlayer());
				TTT.getStats().setInt(ev.getPlayer() ,TTT.getStats().getInt(Stats.TTT_PÄSSE, ev.getPlayer())+1, Stats.TTT_PÄSSE);
			}
		}
	}

}
