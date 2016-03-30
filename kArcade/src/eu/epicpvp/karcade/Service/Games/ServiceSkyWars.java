package eu.epicpvp.karcade.Service.Games;

import org.bukkit.entity.Player;

import eu.epicpvp.karcade.Game.Single.Games.SkyWars.SkyWars;
import lombok.Getter;
import lombok.Setter;

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
