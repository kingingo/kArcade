package eu.epicpvp.karcade.Service.Games;

import org.bukkit.entity.Player;

import eu.epicpvp.karcade.Game.Multi.MultiGames;
import lombok.Getter;
import lombok.Setter;

public class ServiceBedWars1vs1 {

	@Getter
	@Setter
	private static MultiGames multiGames;
	
	public static void Service(Player p,String[] args){
		if(args.length==1){
			p.sendMessage("§e/Service BW1vs1 villager §7|§e Replace Villager");
		}else{
			
		}
	}
	
}
