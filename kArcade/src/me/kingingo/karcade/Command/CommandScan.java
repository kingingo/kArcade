package me.kingingo.karcade.Command;

import me.kingingo.karcade.kArcade;
import me.kingingo.karcade.Game.World.WorldParser;
import me.kingingo.kcore.Client.Client;
import me.kingingo.kcore.Command.CommandHandler.Sender;
import me.kingingo.kcore.Permission.Permission;
import me.kingingo.kcore.Permission.PermissionManager;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class CommandScan implements CommandExecutor{
	
	PermissionManager permManager;
	
	public CommandScan(PermissionManager permManager){
		this.permManager=permManager;
	}
	
	@me.kingingo.kcore.Command.CommandHandler.Command(command = "scan", sender = Sender.PLAYER)
	public boolean onCommand(CommandSender cs, Command cmd, String arg2,String[] args) {
		if((permManager.hasPermission(((Player)cs), Permission.ALL_PERMISSION)||((Player)cs).isOp())){
			WorldParser.Scan(((Player)cs), args[0]);
		}
		return false;
	}

}
