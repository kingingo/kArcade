package eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.Items;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import eu.epicpvp.karcade.Game.Single.Games.Falldown.Falldown;
import eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.BrewItem;
import eu.epicpvp.kcore.LaunchItem.LaunchItem;
import eu.epicpvp.kcore.LaunchItem.LaunchItemEvent;
import eu.epicpvp.kcore.Util.UtilEvent;
import eu.epicpvp.kcore.Util.UtilEvent.ActionType;
import eu.epicpvp.kcore.Util.UtilInv;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.kcore.Util.UtilMath;

public class TntBomb extends BrewItem{

	public TntBomb(Integer[] brewing_items,Falldown falldown) {
		super(100, UtilItem.Item(new ItemStack(Material.TNT), new String[]{"§6§§7 Eine tragbare Tnt-Kanone, welche","§7   verherenden Schaden bewirkt."}, "§e§lTNTBomb"), brewing_items, falldown);
	}

	@EventHandler
	public void explosion(EntityExplodeEvent ev){
		FallingBlock fb;
		for(org.bukkit.block.Block b : ev.blockList()){
			b.setType(Material.AIR);
			
			fb = b.getWorld().spawnFallingBlock(b.getLocation(), b.getType(), b.getData());
            fb.setVelocity( new Vector(UtilMath.RandomDouble(-0.8, 0.8), UtilMath.RandomDouble(0.1, 0.5), UtilMath.RandomDouble(-0.8, 0.8)) );
			
			b.getDrops().clear();
		}
		ev.blockList().clear();
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
