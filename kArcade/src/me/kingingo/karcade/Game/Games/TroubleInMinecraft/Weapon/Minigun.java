package me.kingingo.karcade.Game.Games.TroubleInMinecraft.Weapon;

import lombok.Getter;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.TroubleInMinecraft;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.UtilItem;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class Minigun implements Listener {

	TroubleInMinecraft TTT;
	@Getter
	ItemStack item = UtilItem.RenameItem(new ItemStack(Material.BOW), "§cMinigun");
	
	public Minigun(TroubleInMinecraft TTT){
		this.TTT=TTT;
		Bukkit.getPluginManager().registerEvents(this, TTT.getManager().getInstance());
	}
	
	Location loc;
	Player p;
	Arrow a;
	Vector v;
	@EventHandler
	public void Bow(PlayerInteractEvent ev){
		if(UtilEvent.isAction(ev, ActionType.R)){
			if(UtilItem.ItemNameEquals(ev.getPlayer().getItemInHand(),item)){
				ev.getPlayer().shootArrow();
			}
		}
	}
	
}
