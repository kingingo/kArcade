package eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.Items;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import eu.epicpvp.karcade.Game.Single.Games.Falldown.Falldown;
import eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.BrewItem;
import eu.epicpvp.kcore.LaunchItem.LaunchItem;
import eu.epicpvp.kcore.LaunchItem.LaunchItemEvent;
import eu.epicpvp.kcore.Util.UtilEvent;
import eu.epicpvp.kcore.Util.UtilEvent.ActionType;
import eu.epicpvp.kcore.Util.UtilInv;
import eu.epicpvp.kcore.Util.UtilItem;

public class HerobrinesMinions extends BrewItem{

	public HerobrinesMinions(Integer[] brewing_items,Falldown falldown) {
		super(100, UtilItem.Item(new ItemStack(Material.COAL), new String[]{"§6§7 Eine fliegende Armee greift","§7   deinen Gegner an."}, "§e§lHerobrine's Minions"), brewing_items, falldown);
	}
	
	@EventHandler
	public void Launch(final PlayerInteractEvent event){
		if(UtilEvent.isAction(event, ActionType.R)&&event.getPlayer().getItemInHand().hasItemMeta()&&event.getPlayer().getItemInHand().getItemMeta().hasDisplayName()){
			if(UtilItem.ItemNameEquals(getRealItem(), event.getPlayer().getItemInHand())){
				event.setCancelled(true);
				if(!fireEvent(event.getPlayer())){
					LaunchItem item = new LaunchItem(event.getPlayer(),4,new LaunchItem.LaunchItemEventHandler(){
						@SuppressWarnings("deprecation")
						@Override
						public void onLaunchItem(LaunchItemEvent ev) {
							Location location = ev.getItem().getDroppedItem()[0].getLocation();
							final ArrayList<LivingEntity> entities = new ArrayList<>();
					        for (int counter = 0; counter < 5; counter++) {
					          Bat entity = (Bat)location.getWorld().spawnEntity(location, EntityType.BAT);
					          
					          entity.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1, 1), true);
					          entities.add(entity);
					        }

					        Bukkit.getScheduler().scheduleAsyncDelayedTask(getFalldown().getManager().getInstance(), new BukkitRunnable() {
								
								@Override
								public void run() {
									location.getWorld().playSound(location, Sound.EXPLODE, 1.0F, 1.0F);

						            for (LivingEntity entity : entities) {
						              if (!entity.isDead()) {
						                entity.remove();

						                for (Entity victimEntity : entity.getNearbyEntities(7.0D, 7.0D, 7.0D)) {
						                  if ((victimEntity instanceof LivingEntity)) {
						                	((LivingEntity)victimEntity).damage(10, event.getPlayer());
						                  }
						                }
						              }
						            }

						            entities.clear();
								}
							},20L * 5);
						}
					});
					getFalldown().getIlManager().LaunchItem(item);
					UtilInv.remove(event.getPlayer(), event.getPlayer().getItemInHand().getType(), event.getPlayer().getItemInHand().getData().getData(), 1);
				
				}
			}
		}
	}
	
}
