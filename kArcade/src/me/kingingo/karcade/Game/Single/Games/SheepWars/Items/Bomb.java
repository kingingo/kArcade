package me.kingingo.karcade.Game.Single.Games.SheepWars.Items;

import lombok.Getter;
import me.kingingo.kcore.LaunchItem.LaunchItem;
import me.kingingo.kcore.LaunchItem.LaunchItemEvent;
import me.kingingo.kcore.LaunchItem.LaunchItemManager;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.UtilInv;
import me.kingingo.kcore.Util.UtilItem;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Bomb extends SheepWarsItem{

	@Getter
	private LaunchItemManager manager;
	
	public Bomb(JavaPlugin instance,LaunchItemManager manager) {
		super(instance, UtilItem.addEnchantmentGlow(UtilItem.RenameItem(new ItemStack(Material.BOWL), "§0Bomb")));
		this.manager=manager;
	}
	
	@EventHandler
	public void Launch(final PlayerInteractEvent event){
		if(UtilEvent.isAction(event, ActionType.R)&&event.getPlayer().getItemInHand().hasItemMeta()&&event.getPlayer().getItemInHand().getItemMeta().hasDisplayName()){
			if(equal(event.getPlayer().getItemInHand())){
				event.setCancelled(true);
				LaunchItem item = new LaunchItem(event.getPlayer(),4,new LaunchItem.LaunchItemEventHandler(){
					@Override
					public void onLaunchItem(LaunchItemEvent ev) {
						
						LaunchItem item = new LaunchItem(event.getPlayer(),ev.getItem().getDroppedItem()[0].getLocation(),Material.TNT.getId(),3,3,new LaunchItem.LaunchItemEventHandler(){
							@Override
							public void onLaunchItem(LaunchItemEvent ev) {
								for(Entity e : ev.getItem().getDroppedItem()){
									e.getWorld().strikeLightningEffect(e.getLocation());
									e.getWorld().createExplosion(e.getLocation(), 4.0F, false);
								}
							}
						});
						getManager().LaunchItem(item);
						
					}
				});
				getManager().LaunchItem(item);
				UtilInv.remove(event.getPlayer(), event.getPlayer().getItemInHand().getType(), event.getPlayer().getItemInHand().getData().getData(), 1);
			}
		}
	}

}
