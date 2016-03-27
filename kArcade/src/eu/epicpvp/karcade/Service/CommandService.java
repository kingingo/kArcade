package eu.epicpvp.karcade.Service;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.epicpvp.karcade.Service.Games.ServiceFalldown;
import eu.epicpvp.karcade.Service.Games.ServiceTroubleInMinecraft;
import eu.epicpvp.kcore.Command.CommandHandler.Sender;
import eu.epicpvp.kcore.Permission.PermissionManager;
import eu.epicpvp.kcore.Permission.PermissionType;
import lombok.Getter;
import lombok.Setter;

public class CommandService implements CommandExecutor{
	
	@Getter
	@Setter
	private boolean debug=false;
	private PermissionManager manager;
	
	public CommandService(PermissionManager manager){
		this.manager=manager;
	}
	
	@eu.epicpvp.kcore.Command.CommandHandler.Command(command = "service", sender = Sender.PLAYER)
	public boolean onCommand(CommandSender cs, Command cmd, String arg2,String[] args) {
		Player p = (Player)cs;
		if(!manager.hasPermission(p, PermissionType.ADMIN_SERVICE)||!p.isOp())return false;
		if(args.length==0){
			p.sendMessage("§e=---§7 Service§e---=");
			p.sendMessage("§e/Service Damage §7|§e Damage Nachrichten wo Cancelled wird");
			p.sendMessage("§e/Service BlockPlace §7|§e BlockPlace Nachrichten wo Cancelled wird");
			return true;
		}
		
		if(args[0].equalsIgnoreCase("debug")){
			if(debug){
				setDebug(false);
				Bukkit.broadcastMessage("§e[Service]:§7 Der Debug-Mode wurde §cDeaktiviert!");
			}else{
				setDebug(true);
				Bukkit.broadcastMessage("§e[Service]:§7 Der Debug Mode wurde §aAktiviert!");
			}
		}else if(args[0].equalsIgnoreCase("TTT")){
			ServiceTroubleInMinecraft.Service(p,args);
		}else if(args[0].equalsIgnoreCase("fd")){
			ServiceFalldown.Service(p,args);
		}
		
		return false;
	}

}
