package me.kingingo.karcade.Service.Games;

import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;
import me.kingingo.karcade.Game.Multi.MultiGames;
import me.kingingo.kcore.Enum.Team;
import me.kingingo.kcore.Packet.Events.PacketReceiveEvent;
import me.kingingo.kcore.Packet.Packets.VERSUS_SETTINGS;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.kcore.Versus.VersusKit;
import me.kingingo.kcore.Versus.VersusType;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ServiceMultiGames {

	@Getter
	@Setter
	private static MultiGames games;
	
	public static void Service(Player p,String[] args){
		if(args.length==1){
			p.sendMessage("§e/Service MG Versus §7|§e Sendet ein Versus Setting Packet ab");
		}else{
			if(args[1].equalsIgnoreCase("Versus")){
				HashMap<Player,Team> map = new HashMap<>();
				int i = 0;
				for(Player player : UtilServer.getPlayers()){
					switch(i){
					case 0:map.put(player, Team.RED);break;
					case 1:map.put(player, Team.BLUE);break;
					case 2:map.put(player, Team.GREEN);break;
					case 3:map.put(player, Team.YELLOW);break;
					case 4:map.put(player, Team.ORANGE);break;
					case 5:map.put(player, Team.GRAY);break;
					}
					i++;
				}
				
				VersusKit kit = new VersusKit();
				kit.helm=UtilItem.RenameItem(new ItemStack(Material.IRON_HELMET), "§eHelm");
				kit.chestplate=UtilItem.RenameItem(new ItemStack(Material.IRON_CHESTPLATE), "§eBrustpanzer");
				kit.leggings=UtilItem.RenameItem(new ItemStack(Material.IRON_LEGGINGS), "§eHose");
				kit.boots=UtilItem.RenameItem(new ItemStack(Material.IRON_BOOTS), "§eSchuhe");
				kit.inv=new ItemStack[]{new ItemStack(Material.DIAMOND_SWORD),new ItemStack(Material.DIAMOND_AXE)};
				
				VERSUS_SETTINGS settings = new VERSUS_SETTINGS(VersusType.withTeamAnzahl(map.size()),kit, map);
				p.sendMessage("§eTYP:§a "+settings.getType().name());
				Bukkit.getPluginManager().callEvent(new PacketReceiveEvent( settings ));
				p.sendMessage("§ePacket wurde abgeschickt!");
			}
		}
	}
	
}
