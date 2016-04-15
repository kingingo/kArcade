package eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.Items;

import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import eu.epicpvp.karcade.Game.Single.Games.Falldown.Falldown;
import eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.BrewItem;
import eu.epicpvp.kcore.Scheduler.kScheduler;
import eu.epicpvp.kcore.Update.UpdateType;
import eu.epicpvp.kcore.Util.UtilEvent;
import eu.epicpvp.kcore.Util.UtilEvent.ActionType;
import eu.epicpvp.kcore.Util.UtilInv;
import eu.epicpvp.kcore.Util.UtilItem;

public class Supershock extends BrewItem{

	public Supershock(Integer[] brewing_items, Falldown falldown) {
		super(100,UtilItem.Item(new ItemStack(Material.NETHER_STAR), new String[]{"§6§7 Du beschw§rst ein Erdbeben."}, "§e§lSupershock"), brewing_items, falldown);
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void Launch(final PlayerInteractEvent event){
		if(UtilEvent.isAction(event, ActionType.R)&&event.getPlayer().getItemInHand().hasItemMeta()&&event.getPlayer().getItemInHand().getItemMeta().hasDisplayName()){
			if(event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(getRealItem().getItemMeta().getDisplayName())){
				event.setCancelled(true);
				if(!fireEvent(event.getPlayer())){
					UtilInv.remove(event.getPlayer(),event.getPlayer().getItemInHand().getType(),event.getPlayer().getItemInHand().getData().getData(), 1);
					
					final Player player = event.getPlayer();
				    final World world = player.getWorld();

				    final AtomicInteger counter = new AtomicInteger(10);
				    
				    new kScheduler(getFalldown().getManager().getInstance(), new kScheduler.kSchedulerHandler() {
						
						@Override
						public void onRun(kScheduler s) {
							world.playEffect(player.getLocation(), Effect.ENDER_SIGNAL, 0);
					        counter.decrementAndGet();
					        if (counter.get() <= 0) {

					          player.setVelocity(new Vector(0, 1, 0));
					          Bukkit.getScheduler().scheduleSyncDelayedTask(getFalldown().getManager().getInstance(), new BukkitRunnable() {
								
								@Override
								public void run() {
									player.setVelocity(new Vector(0, -2, 0));
									new kScheduler(getFalldown().getManager().getInstance(), new kScheduler.kSchedulerHandler() {
										
										@Override
										public void onRun(kScheduler arg0) {
											if (player.isOnGround()) {
												world.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 0.5F);
												playImpact(player, 20);
										        arg0.close();
											}
										}
									}, UpdateType.SEC);
								}
							}, 20L);

					          s.close();
					        }
						}
					}, UpdateType.FASTEST);
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	private void playImpact(final Player player, final int radius)
	  {
	    final Location location = player.getLocation();

	    final AtomicInteger expansion = new AtomicInteger(1);
	    new kScheduler(getFalldown().getManager().getInstance(), new kScheduler.kSchedulerHandler() {
			
			@Override
			public void onRun(kScheduler arg0) {
				for (int x = -radius; x <= radius; x++)
		        {
		          for (int z = -radius; z <= radius; z++) {
		            Location impactPixel = location.clone().add(x, 1.0D, z);
		            if (Math.round(impactPixel.distance(location)) == expansion.get()) {
		              impactPixel = impactPixel.add(0.0D, 1.0D, 0.0D);
		              while (impactPixel.getBlock().getType() == Material.AIR) {
		                impactPixel.subtract(0.0D, 1.0D, 0.0D);
		                if (impactPixel.getBlockY() <= 0)
		                {
		                  break;
		                }
		              }
		              impactPixel.getWorld().playEffect(impactPixel, Effect.STEP_SOUND, impactPixel.getBlock().getTypeId());

		              for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
		                if (((entity instanceof LivingEntity)) && (!entity.equals(player)) && (entity.getWorld().equals(impactPixel.getWorld()))) {
		                  long distance1 = Math.round(entity.getLocation().distance(impactPixel));
		                  long distance2 = Math.round(entity.getLocation().distance(impactPixel.clone().add(0.0D, 1.0D, 0.0D)));
		                  if (((distance1 >= 0L) && (distance1 <= 1L)) || ((distance2 >= 0L) && (distance2 <= 1L))) {
		                    ((LivingEntity)entity).damage(10, player);
		                    entity.setVelocity(new Vector(x / 7.0D, 1.5D, z / 7.0D));
		                  }
		                }
		              }
		            }
		          }
		        }

		        expansion.incrementAndGet();
		        if (expansion.get() >= radius)
		        	arg0.close();
			}
		}, UpdateType.FASTEST);
	  }
}
