package eu.epicpvp.karcade.Command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.epicpvp.karcade.ArcadeManager;
import eu.epicpvp.karcade.Game.Multi.MultiGames;
import eu.epicpvp.kcore.Command.CommandHandler.Sender;
import eu.epicpvp.kcore.Permission.PermissionType;

public class CommandSpectate implements CommandExecutor{
	
	ArcadeManager Manager;
	
	public CommandSpectate(ArcadeManager Manager){
		this.Manager=Manager;
	}
	
	@eu.epicpvp.kcore.Command.CommandHandler.Command(command = "spectate",alias={"spec"}, sender = Sender.PLAYER)
	public boolean onCommand(CommandSender cs, Command cmd, String arg2,String[] args) {
			final Player p = (Player)cs;
			if(!p.hasPermission(PermissionType.TEAM_MESSAGE.getPermissionToString()))return false;
			if(!(Manager.getGame() instanceof MultiGames))return false;
			
			p.openInventory(((MultiGames)Manager.getGame()).getPage());
		return false;
	}

}
