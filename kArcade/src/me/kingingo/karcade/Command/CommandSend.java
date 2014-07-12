package me.kingingo.karcade.Command;

import me.kingingo.kcore.Client.Client;
import me.kingingo.kcore.Command.CommandHandler.Sender;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandSend implements CommandExecutor{

	Client c;
	
	public CommandSend(Client c){
		this.c=c;
	}
	
	@me.kingingo.kcore.Command.CommandHandler.Command(command = "send", sender = Sender.CONSOLE)
	public boolean onCommand(CommandSender cs, Command cmd, String arg2,String[] args) {
		c.sendMessageToServer(args[0]);
		return false;
	}

}
