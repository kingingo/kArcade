package me.kingingo.karcade.Command;

import me.kingingo.kcore.Command.CommandHandler.Sender;
import me.kingingo.kcore.Minecraft.NPC;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandLeiche implements CommandExecutor{

	JavaPlugin plugin;
	
	public CommandLeiche(JavaPlugin plugin){
		this.plugin=plugin;
	}
	
	@me.kingingo.kcore.Command.CommandHandler.Command(command = "leiche", sender = Sender.PLAYER)
	public boolean onCommand(CommandSender cs, Command cmd, String arg2,String[] args) {
		final Player p = (Player)cs;
		final NPC n = new NPC("kingingo",p.getLocation());
		n.verdeckt("Unidentificiert");
		
		Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable(){

			@Override
			public void run() {
				n.aufdecken();
				p.sendMessage("AUFGEDECKT");
			}
			
		}, 20*5);
		
		return false;
	}

}
