package eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.Items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import eu.epicpvp.karcade.Game.Single.Games.Falldown.Falldown;
import eu.epicpvp.karcade.Game.Single.Games.Falldown.kDistance;
import eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.BrewItem;
import eu.epicpvp.kcore.LaunchItem.LaunchItem;
import eu.epicpvp.kcore.LaunchItem.LaunchItemEvent;
import eu.epicpvp.kcore.Scheduler.kScheduler;
import eu.epicpvp.kcore.Update.UpdateType;
import eu.epicpvp.kcore.Util.UtilEvent;
import eu.epicpvp.kcore.Util.UtilEvent.ActionType;
import eu.epicpvp.kcore.Util.UtilItem;

public class WolfArmy extends BrewItem{

	public WolfArmy(Integer[] brewing_items,Falldown falldown) {
		super(100, UtilItem.Item(new ItemStack(Material.BONE,1), new String[]{"§6§§7 Deine eigene Wolf-Armee verteidgt dich,","§7   bis auf denn letzten Wolf."}, "§e§lWolfArmy"), brewing_items, falldown);
	}

	@EventHandler
	public void Launch(final PlayerInteractEvent event){
		if(UtilEvent.isAction(event, ActionType.R)&&event.getPlayer().getItemInHand().hasItemMeta()&&event.getPlayer().getItemInHand().getItemMeta().hasDisplayName()){
			if(event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(getRealItem().getItemMeta().getDisplayName())){
				event.setCancelled(true);
				if(!fireEvent(event.getPlayer())){
					event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.FUSE, 1.0F, 1.0F);
					LaunchItem item = new LaunchItem(event.getPlayer(),4,new LaunchItem.LaunchItemEventHandler(){
						@SuppressWarnings("deprecation")
						@Override
						public void onLaunchItem(LaunchItemEvent ev) {
							Player player = event.getPlayer();
							Location location = player.getLocation();
					        
					        ArrayList<kDistance> list = getFalldown().getNearDistance(40, location, true, player);
					        
					        if(!list.isEmpty()){
								Collections.sort(list,kDistance.ASCENDING);
						        final Player target = (Player) list.get(0).getEntity();
						        if (target != null) {
							          location.getWorld().playSound(player.getLocation(), Sound.WOLF_GROWL, 1.0F, 1.0F);

							          ArrayList<Wolf> wolves = new ArrayList<Wolf>();
							          for (int counter = 0; counter < 3; counter++) {
							            Wolf wolf = (Wolf)location.getWorld().spawnEntity(ev.getItem().getDroppedItem()[0].getLocation(), EntityType.WOLF);
							            wolf.setHealth(6);
							            wolf.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 2147483647, 2), true);
							            wolf.damage(1, target);
							            wolf.setTarget(target);
							            wolves.add(wolf);
							          }

							          final AtomicInteger counter = new AtomicInteger(30);
							          new kScheduler(getFalldown().getManager().getInstance(), new kScheduler.kSchedulerHandler() {
										
										@Override
										public void onRun(kScheduler arg0) {
											if (((target != null) && (target.isDead())) || target.getWorld().getUID() != wolves.get(0).getWorld().getUID() || (counter.get() <= 0)) {
								                for (Wolf wolf : wolves) {
								                  if (!wolf.isDead()) {
								                	  player.getWorld().createExplosion(wolf.getLocation(), 1.0F,false);
								                  }

								                  wolf.remove();
								                }

								                wolves.clear();
								                arg0.close();
								              }
										}
									}, UpdateType.SEC_3);
						        }
					        }

					        
						}
					});
					getFalldown().getIlManager().LaunchItem(item);
				}
			}
		}
	}

}
