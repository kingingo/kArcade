package me.kingingo.karcade.Service.Games;

import lombok.Getter;
import lombok.Setter;
import me.kingingo.karcade.Game.Single.Games.SkyWars.SkyWars;

import org.bukkit.entity.Player;

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
