package me.kingingo.karcade.Command;

import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Game.Multi.MultiGames;
import me.kingingo.karcade.Game.Single.SingleGame;
import me.kingingo.kcore.Command.CommandHandler.Sender;
import me.kingingo.kcore.Language.Language;
import me.kingingo.kcore.Permission.kPermission;
import me.kingingo.kcore.Util.AnvilGUI;
import me.kingingo.kcore.Util.AnvilGUI.AnvilClickEvent;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilPlayer;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandSpectate implements CommandExecutor{
	
	kArcadeManager Manager;
	
	public CommandSpectate(kArcadeManager Manager){
		this.Manager=Manager;
	}
	
	@me.kingingo.kcore.Command.CommandHandler.Command(command = "spectate",alias={"spec"}, sender = Sender.PLAYER)
	public boolean onCommand(CommandSender cs, Command cmd, String arg2,String[] args) {
			final Player p = (Player)cs;
			if(!p.hasPermission(kPermission.TEAM_MESSAGE.getPermissionToString()))return false;
			if(!(Manager.getGame() instanceof MultiGames))return false;
			
			p.openInventory(((MultiGames)Manager.getGame()).getPage());
		return false;
	}

}
