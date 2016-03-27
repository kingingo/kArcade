package eu.epicpvp.karcade.Service.Games;

import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;
import eu.epicpvp.karcade.Game.Single.Games.SkyWars.SkyWars;

public class ServiceSkyWars {

	@Getter
	@Setter
	private static SkyWars skyWars;
	
	public static void Service(Player p,String[] args){
		if(args.length==1){
			p.sendMessage("§e/Service skywars Bombe §7|§e ");
		}else{
			
		}
	}
	
}
