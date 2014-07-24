package me.kingingo.karcade.Game.Games.TroubleInMinecraft.Weapon;

import lombok.Getter;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.TroubleInMinecraft;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilMath;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;

public class Shotgun {

	TroubleInMinecraft TTT;
	@Getter
	ItemStack item = UtilItem.RenameItem(new ItemStack(Material.BOW), "§eShotgun");
	
	public Shotgun(TroubleInMinecraft TTT){
		this.TTT=TTT;
	}
	
	Location loc;
	Player p;
	@EventHandler
	public void Bow(EntityShootBowEvent ev){
		if(ev.getEntity() instanceof Player&&UtilItem.ItemNameEquals(item, ev.getBow())){
			p=(Player)ev.getEntity();
			loc=ev.getProjectile().getLocation();
			for(int i=0;i<6;i++){
				loc.getWorld().spawnArrow(loc.add(UtilMath.r(2),UtilMath.r(1),UtilMath.r(2)), ev.getProjectile().getVelocity(), 1F, 1F);
			}
		}
	}
	
}
