package me.kingingo.karcade.Service;

import lombok.Getter;
import lombok.Setter;
import me.kingingo.kcore.Command.CommandHandler.Sender;
import me.kingingo.kcore.Permission.Permission;
import me.kingingo.kcore.Permission.PermissionManager;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandService implements CommandExecutor{
	
	@Getter
	@Setter
	private boolean Damage=false;
	private PermissionManager manager;
	
	public CommandService(PermissionManager manager){
		this.manager=manager;
	}
	
	@me.kingingo.kcore.Command.CommandHandler.Command(command = "service", sender = Sender.PLAYER)
	public boolean onCommand(CommandSender cs, Command cmd, String arg2,String[] args) {
		Player p = (Player)cs;
		if(!manager.hasPermission(p, Permission.ADMIN_SERVICE)||!p.isOp())return false;
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
		}
		
		return false;
	}

}
