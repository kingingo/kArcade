package me.kingingo.karcade.Game.Games.TroubleInMinecraft.Weapon;

import lombok.Getter;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.TroubleInMinecraft;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilMath;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class Shotgun implements Listener {

	TroubleInMinecraft TTT;
	@Getter
	ItemStack item = UtilItem.RenameItem(new ItemStack(Material.BOW), "§aShotgun");
	
	public Shotgun(TroubleInMinecraft TTT){
		this.TTT=TTT;
		Bukkit.getPluginManager().registerEvents(this, TTT.getManager().getInstance());
	}
	
	Location loc;
	Player p;
	Arrow a;
	Vector v;
	@EventHandler
	public void Bow(EntityShootBowEvent ev){
		if(ev.getEntity() instanceof Player&&UtilItem.ItemNameEquals(item, ev.getBow())){
			p=(Player)ev.getEntity();
			v = Location(p.getEyeLocation());
			//v = v.multiply(3);
			for(int i=0; i<7; i++){
				a = (Arrow)p.getWorld().spawn(p.getEyeLocation(),Arrow.class);
				a.setShooter(p);
				switch(UtilMath.RandomInt(6, 1)){
				case 1:
					v.setX(v.getX()+0.1);
					break;
				case 2:
					v.setX(v.getX()-0.1);
					break;
				case 3:
					v.setZ(v.getZ()+0.1);
					break;
				case 4:
					v.setZ(v.getZ()-0.1);
					break;
				case 5:
					v.setY(v.getY()+0.1);
					break;
				case 6:
					v.setY(v.getY()-0.1);
					break;
				}
				a.setVelocity(v);
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
