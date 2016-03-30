package eu.epicpvp.karcade.Service.Games;

import org.bukkit.entity.Player;

import eu.epicpvp.karcade.Game.Single.Games.Falldown.Falldown;
import eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.BrewItem;
import lombok.Getter;
import lombok.Setter;

public class ServiceFalldown {

	@Getter
	@Setter
	private static Falldown fd;
	
	public static void Service(Player p,String[] args){
		if(args.length==1){
			p.sendMessage("§e/Service FD all");
		}else{
			if(args[1].equalsIgnoreCase("all")){
				for(BrewItem b : fd.getBrewItems())p.getInventory().addItem(b.getItem());
				p.sendMessage("§aDu hast alle Items erhalten.");
			}
		}
	}
	
}
