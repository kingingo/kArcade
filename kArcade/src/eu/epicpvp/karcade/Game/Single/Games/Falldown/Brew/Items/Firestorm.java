package eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.Items;

import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import eu.epicpvp.karcade.Game.Single.Games.Falldown.Falldown;
import eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.BrewItem;
import eu.epicpvp.kcore.Enum.PlayerState;
import eu.epicpvp.kcore.Scheduler.kScheduler;
import eu.epicpvp.kcore.Update.UpdateType;
import eu.epicpvp.kcore.Util.TimeSpan;
import eu.epicpvp.kcore.Util.UtilEvent;
import eu.epicpvp.kcore.Util.UtilEvent.ActionType;
import eu.epicpvp.kcore.Util.UtilInv;
import eu.epicpvp.kcore.Util.UtilItem;

public class Firestorm extends BrewItem{

	public Firestorm(Integer[] brewing_items,Falldown falldown) {
		super(100, UtilItem.Item(new ItemStack(351,1,(byte)14), new String[]{"§6§7 Alle Spieler um dich herum fangen an zu brennen"}, "§e§lFirestorm"), brewing_items, falldown);
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void Launch(final PlayerInteractEvent event){
		if(UtilEvent.isAction(event, ActionType.R)&&event.getPlayer().getItemInHand().hasItemMeta()&&event.getPlayer().getItemInHand().getItemMeta().hasDisplayName()){
			if(event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(getRealItem().getItemMeta().getDisplayName())){
				event.setCancelled(true);
				if(!fireEvent(event.getPlayer())){
					UtilInv.remove(event.getPlayer(),event.getPlayer().getItemInHand().getType(),event.getPlayer().getItemInHand().getData().getData(), 1);
					Player player = event.getPlayer();
					
					player.getWorld().playSound(player.getLocation(), Sound.FIRE_IGNITE, 1.0F, 1.0F);
				    player.getWorld().playSound(player.getLocation(), Sound.FIRE, 1.0F, 1.0F);
				    player.getWorld().strikeLightningEffect(player.getLocation());

				    final AtomicInteger expansion = new AtomicInteger(1);
				    new kScheduler(getFalldown().getManager().getInstance(), new kScheduler.kSchedulerHandler() {
						
						@Override
						public void onRun(kScheduler arg0) {
							for (int x = -8; x <= 8; x++) {
						          for (int y = -8; y <= 8; y++) {
						            for (int z = -8; z <= 8; z++) {
						              Location firePixel = player.getLocation().clone().add(x, y, z);
						              if ((Math.round(firePixel.distance(player.getLocation())) == expansion.get()) && (firePixel.getBlock().getType() == Material.AIR)) {
						                firePixel.getWorld().playEffect(firePixel, Effect.MOBSPAWNER_FLAMES, firePixel.getBlock().getTypeId());

						                for (Entity entity : player.getNearbyEntities(8.0D, 8.0D, 8.0D)) {
						                  if ((entity != null) && ((entity instanceof LivingEntity)) && (!entity.equals(player)) && (
						                    (!(entity instanceof Player)) || ( getFalldown().getGameList().isPlayerState(((Player)entity))==PlayerState.IN )))
						                  {
						                    long distance1 = Math.round(entity.getLocation().distance(firePixel));
						                    long distance2 = Math.round(entity.getLocation().distance(firePixel.clone().add(0.0D, 1.0D, 0.0D)));
						                    if (((distance1 >= 0L) && (distance1 <= 1L)) || ((distance2 >= 0L) && (distance2 <= 1L))) {
						                      ((LivingEntity)entity).damage(1, player);
						                      entity.setFireTicks((int) (15000L * TimeSpan.SECOND) );
						                    }
						                  }
						                }
						              }
						            }
						          }
						        }

						        expansion.incrementAndGet();
						        if (expansion.get() >= 8){
						        	arg0.close();
						        }
						}
					}, UpdateType.SEC_2);
				}
			}
		}
	}
	
}
