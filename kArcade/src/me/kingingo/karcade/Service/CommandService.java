package me.kingingo.karcade.Service;

import lombok.Getter;
import lombok.Setter;
import me.kingingo.karcade.Service.Games.ServiceMultiGames;
import me.kingingo.karcade.Service.Games.ServiceSheepWars;
import me.kingingo.karcade.Service.Games.ServiceTroubleInMinecraft;
import me.kingingo.kcore.Command.CommandHandler.Sender;
import me.kingingo.kcore.Permission.PermissionManager;
import me.kingingo.kcore.Permission.kPermission;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandService implements CommandExecutor{
	
	@Getter
	@Setter
	private boolean Damage=false;
	@Getter
	@Setter
	private boolean lagg=false;
	private PermissionManager manager;
	
	public CommandService(PermissionManager manager){
		this.manager=manager;
	}
	
	@me.kingingo.kcore.Command.CommandHandler.Command(command = "service", sender = Sender.PLAYER)
	public boolean onCommand(CommandSender cs, Command cmd, String arg2,String[] args) {
		Player p = (Player)cs;
		if(!manager.hasPermission(p, kPermission.ADMIN_SERVICE)||!p.isOp())return false;
		if(args.length==0){
			p.sendMessage("§e=---§7 Service§e---=");
			p.sendMessage("§e/Service Damage §7|§e Damage Nachrichten wo Cancelled wird");
			return true;
		}
		
		if(args[0].equalsIgnoreCase("damage")){
			if(Damage){
				setDamage(false);
				Bukkit.broadcastMessage("§e[Service]:§7 Die Damage Nachrichten wurden §cDeaktiviert!");
			}else{
				setDamage(true);
				Bukkit.broadcastMessage("§e[Service]:§7 Die Damage Nachrichten wurden §aAktiviert!");
			}
		}else if(args[0].equalsIgnoreCase("lagg")){
			if(lagg){
				setLagg(false);
				Bukkit.broadcastMessage("§e[Service]:§7 Die Lagg Nachrichten wurden §cDeaktiviert!");
			}else{
				setLagg(true);
				Bukkit.broadcastMessage("§e[Service]:§7 Die Lagg Nachrichten wurden §aAktiviert!");
			}
		}else if(args[0].equalsIgnoreCase("SW")){
			ServiceSheepWars.Service(p, args);
		}else if(args[0].equalsIgnoreCase("TTT")){
			ServiceTroubleInMinecraft.Service(p,args);
		}else if(args[0].equalsIgnoreCase("mg")){
			ServiceMultiGames.Service(p,args);
		}
		
		return false;
	}

}
