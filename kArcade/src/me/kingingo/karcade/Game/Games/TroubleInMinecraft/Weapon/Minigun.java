package me.kingingo.karcade.Game.Games.TroubleInMinecraft.Weapon;

import lombok.Getter;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.TroubleInMinecraft;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.UtilInv;
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
	
	Player p;
	Arrow a;
	@EventHandler
	public void Bow(PlayerInteractEvent ev){
		if(UtilEvent.isAction(ev, ActionType.R)){
			if(UtilItem.ItemNameEquals(ev.getPlayer().getItemInHand(),item)&&UtilInv.contains(ev.getPlayer(), Material.ARROW, (byte) 0, 1)){
				UtilInv.remove(ev.getPlayer(), Material.ARROW, (byte) 0, 1);
				p=ev.getPlayer();
				a=p.launchProjectile(Arrow.class);
				a.setShooter(p);
			}
		}
	}
	
	public Vector Location(Location l){
		double pitch = ((l.getPitch() + 90) * Math.PI) / 180;
		double yaw  = ((l.getYaw() + 90)  * Math.PI) / 180;
		
		double x = Math.sin(pitch) * Math.cos(yaw);
		double y = Math.sin(pitch) * Math.sin(yaw);
		double z = Math.cos(pitch);
		
		Vector vector = new Vector(x, z, y);
		return vector;
	}
	
}
