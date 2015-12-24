package me.kingingo.karcade.Service.Games;

import lombok.Getter;
import lombok.Setter;
import me.kingingo.karcade.Game.Multi.MultiGames;
import me.kingingo.kcore.Arena.ArenaType;
import me.kingingo.kcore.Enum.Team;
import me.kingingo.kcore.Packet.Events.PacketReceiveEvent;
import me.kingingo.kcore.Packet.Packets.ARENA_SETTINGS;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ServiceMultiGames {

	@Getter
	@Setter
	private static MultiGames games;
	
	public static void Service(Player p,String[] args){
		if(args.length==1){
			p.sendMessage("§e/Service MG bw §7|§e Sendet ein Versus Setting Packet ab");
		}else{
			if(args[1].equalsIgnoreCase("bw")){
				ARENA_SETTINGS s = new ARENA_SETTINGS(ArenaType._TEAMx2, "arena0", "", p, Team.RED, 2, 2);
				ARENA_SETTINGS s1 = new ARENA_SETTINGS(ArenaType._TEAMx2, "arena0", "", Bukkit.getPlayer("t3ker"), Team.BLUE, 2, 2);
				Bukkit.getPluginManager().callEvent(new PacketReceiveEvent(s, games.getManager().getPacketManager()));
				Bukkit.getPluginManager().callEvent(new PacketReceiveEvent(s1, games.getManager().getPacketManager()));
				
			}
		}
	}
	
}
