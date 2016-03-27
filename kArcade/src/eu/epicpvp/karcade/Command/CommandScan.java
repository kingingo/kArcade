package eu.epicpvp.karcade.Command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.epicpvp.karcade.Game.World.Parser.WorldParser;
import eu.epicpvp.kcore.Command.CommandHandler.Sender;
import eu.epicpvp.kcore.Permission.PermissionManager;

public class CommandScan implements CommandExecutor{
	
	PermissionManager permManager;
	
	public CommandScan(PermissionManager permManager){
		this.permManager=permManager;
	}
	
	@eu.epicpvp.kcore.Command.CommandHandler.Command(command = "scan", sender = Sender.PLAYER)
	public boolean onCommand(CommandSender cs, Command cmd, String arg2,String[] args) {
		if(((Player)cs).isOp()){
			WorldParser.Scan(((Player)cs).getLocation(), args[0]);
		}
		return false;
	}

}
