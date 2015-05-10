package me.kingingo.karcade.Game.Single.addons;

import lombok.Getter;
import me.kingingo.kcore.Util.UtilItem;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fish;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class AddonEnterhacken implements Listener {

	@Getter
	ItemStack item = UtilItem.RenameItem(new ItemStack(Material.FISHING_ROD), "Enterhacken");
	int use=3;
	
	public AddonEnterhacken(JavaPlugin instance,int use){
		this.use=use;
		Bukkit.getPluginManager().registerEvents(this, instance);
	}
	
	public AddonEnterhacken(JavaPlugin instance){
		Bukkit.getPluginManager().registerEvents(this, instance);
	}
	
    @EventHandler
	  public void grapple(ProjectileHitEvent event) {
	    Entity e = event.getEntity();
	    if ((event.getEntity() instanceof Fish)) {
	      Player p = (Player)event.getEntity().getShooter();
	      if(UtilItem.ItemNameEquals(item, p.getItemInHand())){
	    	  item.setDurability(((short) (item.getDurability()-(item.getType().getMaxDurability()/use))));
	      }else{
	    	  for(ItemStack i : p.getInventory()){
	    		  if(UtilItem.ItemNameEquals(i, p.getItemInHand())){
	    	    	  i.setDurability(((short) (i.getDurability()-(i.getType().getMaxDurability()/use))));
	    	    	  break;
	    	      }
	    	  }
	      }
	      Location locA = p.getLocation();
	      Location locB = e.getLocation();
	      Vector dir = locB.toVector().subtract(locA.toVector()).normalize();
	      p.setVelocity(dir.multiply(2));
	      e.remove();
	    }
	  }
	
}
