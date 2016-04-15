package eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.Items;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import eu.epicpvp.karcade.Game.Single.Games.Falldown.Falldown;
import eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.BrewItem;
import eu.epicpvp.kcore.Util.UtilEvent;
import eu.epicpvp.kcore.Util.UtilEvent.ActionType;
import eu.epicpvp.kcore.Util.UtilInv;
import eu.epicpvp.kcore.Util.UtilItem;

public class Lightning extends BrewItem{
	
	public Lightning(Integer[] brewing_items,Falldown falldown) {
		super(100, UtilItem.Item(new ItemStack(Material.GHAST_TEAR,1), new String[]{"§6§7 Ein Blitz elektrisiert deinen Gegner."}, "§e§lLightning"), brewing_items, falldown);
	}

	@EventHandler
	public void Launch(final PlayerInteractEvent event){
		if(UtilEvent.isAction(event, ActionType.R)&&event.getPlayer().getItemInHand().hasItemMeta()&&event.getPlayer().getItemInHand().getItemMeta().hasDisplayName()){
			if(event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(getRealItem().getItemMeta().getDisplayName())){
				event.setCancelled(true);
				if(!fireEvent(event.getPlayer())){
					UtilInv.remove(event.getPlayer(),event.getPlayer().getItemInHand().getType(),event.getPlayer().getItemInHand().getData().getData(), 1);
					 event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.AMBIENCE_THUNDER, 1.0F, 1.0F);

					    for (Entity entity : event.getPlayer().getNearbyEntities(10.0D, 10.0D, 10.0D)) {
					      if ((entity instanceof LivingEntity)) {
					    	event.getPlayer().getWorld().strikeLightningEffect(entity.getLocation());
					        ((LivingEntity)entity).damage(10,event.getPlayer());
					      }
					    }

				}
			}
		}
	}

}
