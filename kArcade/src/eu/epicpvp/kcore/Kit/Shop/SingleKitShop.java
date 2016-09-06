package eu.epicpvp.kcore.Kit.Shop;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.java.JavaPlugin;

import eu.epicpvp.karcade.Game.Events.GameStartEvent;
import eu.epicpvp.kcore.Kit.Kit;
import eu.epicpvp.kcore.Kit.Perk;
import eu.epicpvp.kcore.Kit.Perks.Event.PerkStartEvent;
import eu.epicpvp.kcore.Permission.PermissionManager;
import eu.epicpvp.kcore.StatsManager.StatsManager;
import eu.epicpvp.kcore.Util.InventorySize;
import eu.epicpvp.kcore.Util.UtilServer;

public class SingleKitShop extends KitShop{

	public SingleKitShop(JavaPlugin instance, StatsManager money,PermissionManager manager, String name, InventorySize size,Kit[] kits) {
		super(instance, money, manager, name, size, kits);
	}
	
	@EventHandler(priority=EventPriority.NORMAL)
	public void Start(GameStartEvent ev){
		for(Kit k : getKits()){
			for(Perk perk : k.getPerks()){
				Bukkit.getPluginManager().registerEvents(perk, getPermManager().getInstance());
			}
		}
		ArrayList<Player> list = new ArrayList<>();
		for(Player player : UtilServer.getPlayers())list.add(player);
		Bukkit.getPluginManager().callEvent(new PerkStartEvent( list ));
		list.clear();
		list=null;
	}
}
