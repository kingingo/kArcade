package me.kingingo.karcade.Game.Games.Falldown.Brew.Items;

import me.kingingo.karcade.Game.Games.Falldown.Falldown;
import me.kingingo.karcade.Game.Games.Falldown.Brew.BrewItem;
import me.kingingo.kcore.LaunchItem.LaunchItem;
import me.kingingo.kcore.LaunchItem.LaunchItemEvent;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.UtilInv;
import me.kingingo.kcore.Util.UtilItem;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class TNT extends BrewItem{

	public TNT(ItemStack[] brewing_items,Falldown falldown) {
		super(100, UtilItem.Item(new ItemStack(Material.TNT), new String[]{""}, "§cTNT"), brewing_items, falldown);
	}

	@EventHandler
	public void Launch(final PlayerInteractEvent event){
		if(UtilEvent.isAction(event, ActionType.R)&&event.getPlayer().getItemInHand().hasItemMeta()&&event.getPlayer().getItemInHand().getItemMeta().hasDisplayName()){
			if(UtilItem.ItemNameEquals(getRealItem(), event.getPlayer().getItemInHand())){
				event.setCancelled(true);
				if(!fireEvent(event.getPlayer())){
					LaunchItem item = new LaunchItem(event.getPlayer(),4,new LaunchItem.LaunchItemEventHandler(){
						@Override
						public void onLaunchItem(LaunchItemEvent ev) {
							
							LaunchItem item = new LaunchItem(event.getPlayer(),ev.getItem().getDroppedItem()[0].getLocation(),Material.TNT.getId(),3,3,new LaunchItem.LaunchItemEventHandler(){
								@Override
								public void onLaunchItem(LaunchItemEvent ev) {
									for(Entity e : ev.getItem().getDroppedItem()){
										e.getWorld().createExplosion(e.getLocation(), 0.5f);
										e.getWorld().strikeLightningEffect(e.getLocation());
										e.getWorld().createExplosion(e.getLocation(), 4.0F, false);
									}
								}
							});
							getFalldown().getIlManager().LaunchItem(item);
							
						}
					});
					getFalldown().getIlManager().LaunchItem(item);
					UtilInv.remove(event.getPlayer(), event.getPlayer().getItemInHand().getType(), event.getPlayer().getItemInHand().getData().getData(), 1);
				
				}
			}
		}
	}
	
}
