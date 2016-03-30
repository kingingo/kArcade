package eu.epicpvp.karcade.Service.Games;

import org.bukkit.entity.Player;

import eu.epicpvp.karcade.Game.Single.Games.TroubleInMinecraft.TroubleInMinecraft;
import eu.epicpvp.kcore.Enum.PlayerState;
import eu.epicpvp.kcore.NPC.NPC;
import lombok.Getter;
import lombok.Setter;

public class ServiceTroubleInMinecraft {

	@Getter
	@Setter
	private static TroubleInMinecraft ttt;
	
	public static void Service(Player p,String[] args){
		if(args.length==1){
			p.sendMessage("§e/Service TTT npc §7|§e Spawnt einen NPC");
			p.sendMessage("§e/Service TTT npcsleep §7|§e Spawnt einen schlafenden NPC");
			p.sendMessage("§e/Service TTT invisble §7|§e sichtbar");
		}else{
			if(args[1].equalsIgnoreCase("npc")){
				NPC npc = ttt.getNpcManager().createNPC("test", p.getLocation());
				npc.spawn();
				p.sendMessage("§aNPC gespawnt");
			}else if(args[1].equalsIgnoreCase("npcsleep")){
				NPC npc = ttt.getNpcManager().createNPC("TestSleep", p.getLocation());
				npc.spawn();
				npc.sleep();
				p.sendMessage("§aschlafender NPC gespawnt");
			}else if(args[1].equalsIgnoreCase("invisble")){
				for(Player player : ttt.getGameList().getPlayers(PlayerState.IN)){
					for(Player player1 : ttt.getGameList().getPlayers(PlayerState.IN)){
						player.showPlayer(player1);
						player1.showPlayer(player);
					}
				}
				p.sendMessage("§aSichtbar");
			}
		}
	}
	
}
