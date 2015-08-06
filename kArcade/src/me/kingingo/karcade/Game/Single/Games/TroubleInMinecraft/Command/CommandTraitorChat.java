package me.kingingo.karcade.Game.Single.Games.TroubleInMinecraft.Command;

import me.kingingo.karcade.Game.Single.Games.TroubleInMinecraft.TroubleInMinecraft;
import me.kingingo.kcore.Command.CommandHandler.Sender;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.Team;
import me.kingingo.kcore.Language.Language;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class CommandTraitorChat implements CommandExecutor, Listener{
	
	TroubleInMinecraft TTT;
	
	public CommandTraitorChat(TroubleInMinecraft TTT){
		this.TTT=TTT;
	}
	
	@me.kingingo.kcore.Command.CommandHandler.Command(command = "tc", sender = Sender.PLAYER)
	public boolean onCommand(CommandSender cs, Command cmd, String arg2,String[] args) {
		if(TTT.getState()!=GameState.InGame)return false;
		Player p = (Player)cs;
		Team t = TTT.getTeam(p);
		if(t!=Team.TRAITOR)return false;
		if(args.length==0){
			p.sendMessage(Language.getText(p, "PREFIX_GAME", TTT.getType().getTyp())+"§c/tc [Nachricht]");
			return false;
		}else{
			String msg = "";
			for(String arg : args){
				msg=msg+" "+arg;
			}
			for(Player p1 : TTT.getPlayerFrom(t)){
				p1.sendMessage("§cTraitor-Chat§8 |§c "+p.getName()+":§7 "+msg);
			}
		}
		
		return false;
	}

}
