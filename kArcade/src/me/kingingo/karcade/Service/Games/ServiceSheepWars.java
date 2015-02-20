package me.kingingo.karcade.Service.Games;

import lombok.Getter;
import lombok.Setter;
import me.kingingo.karcade.Game.Games.SheepWars.SheepWars;

import org.bukkit.entity.Player;

public class ServiceSheepWars {

	@Getter
	@Setter
	private static SheepWars sheepWars;
	
	public static void Service(Player p,String[] args){
		if(args.length==1){
			p.sendMessage("§e/Service SW Bombe §7|§e Gibt eine Bombe.");
			p.sendMessage("§e/Service SW Bright §7|§e Gibt eine Bright.");
		}else{
			if(args[1].equalsIgnoreCase("bombe")){
				getSheepWars().getBomb().add(p);
				p.sendMessage("§aDu hast eine Bombe erhalten.");
			}else if(args[1].equalsIgnoreCase("bright")){
				getSheepWars().getBright().add(p);
				p.sendMessage("§aDu hast eine Bright erhalten.");
			}else if(args[1].equalsIgnoreCase("wall")){
				getSheepWars().getWall().add(p);
				p.sendMessage("§aDu hast eine ProtectWall erhalten.");
			}
		}
	}
	
}
