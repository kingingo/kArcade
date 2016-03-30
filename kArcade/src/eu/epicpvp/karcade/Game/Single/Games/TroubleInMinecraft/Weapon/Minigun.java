package eu.epicpvp.karcade.Game.Single.Games.TroubleInMinecraft.Weapon;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import eu.epicpvp.karcade.Game.Single.Games.TroubleInMinecraft.TroubleInMinecraft;
import eu.epicpvp.kcore.Util.UtilEvent;
import eu.epicpvp.kcore.Util.UtilEvent.ActionType;
import eu.epicpvp.kcore.Util.UtilInv;
import eu.epicpvp.kcore.Util.UtilItem;
import lombok.Getter;

public class Minigun implements Listener {

	TroubleInMinecraft TTT;
	@Getter
	ItemStack item = UtilItem.RenameItem(new ItemStack(Material.BOW), "§cMinigun");
	ArrayList<Arrow> shot = new ArrayList<>();
	
	public Minigun(TroubleInMinecraft TTT){
		this.TTT=TTT;
		Bukkit.getPluginManager().registerEvents(this, TTT.getManager().getInstance());
	}
	
	Player p;
	Arrow a;
	@EventHandler(priority=EventPriority.HIGHEST)
	public void Bow(PlayerInteractEvent ev){
		if(UtilEvent.isAction(ev, ActionType.R)){
			if(UtilItem.ItemNameEquals(ev.getPlayer().getItemInHand(),item)&&UtilInv.contains(ev.getPlayer(), Material.ARROW, (byte) 0, 1)){
				UtilInv.remove(ev.getPlayer(), Material.ARROW, (byte) 0, 1);
				p=ev.getPlayer();
				a=p.launchProjectile(Arrow.class);
				//a.setVelocity(a.getVelocity().multiply(2));
				a.setShooter(p);
				shot.add(((Arrow)a));
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void Damage(EntityDamageByEntityEvent ev){
		if(ev.getEntity() instanceof Player && ev.getDamager() instanceof Arrow){
			Arrow a = (Arrow)ev.getDamager();
			if(shot.contains(a)){
				ev.setDamage(4.0);
				shot.remove(a);
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
