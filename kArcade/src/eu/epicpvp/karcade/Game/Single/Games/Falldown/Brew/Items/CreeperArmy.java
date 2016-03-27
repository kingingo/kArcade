package eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.Items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import lombok.Getter;
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
import eu.epicpvp.kcore.Util.UtilPlayer;

public class CreeperArmy extends BrewItem{
	
	@Getter
	private HashMap<Creature,Player> list = new HashMap<>();

	public CreeperArmy(Integer[] items,Falldown instance) {
		super(100, UtilItem.Item(new ItemStack(Material.MONSTER_EGG,1,(byte)50), new String[]{"§6§§7 Gef§hrliche Creeper greifen deine Gegner an!"}, "§e§lCreeperArmy"), items, instance);
	}
	
	HashMap<Player,Double> l;
	double d;
	@EventHandler
	public void Explode(EntityExplodeEvent ev){
		if(!(ev.getEntity() instanceof Creature)||!(ev.getEntity() instanceof Creeper))return;
		ev.blockList().clear();
		Player a = list.get( ((Creature)ev.getEntity()) );
		l = UtilPlayer.getInRadius(ev.getLocation(), 5);
		for(Player player : l.keySet()){
			d=l.get(player);
			player.damage(12/d, a);
		}
	}
	
	@EventHandler
	public void target(EntityTargetEvent ev){
		if(ev.getEntity().getType() == EntityType.CREEPER){
			ev.setCancelled(true);
		}
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
							          location.getWorld().playSound(player.getLocation(), Sound.CREEPER_HISS, 1.0F, 1.0F);

							          ArrayList<Creeper> creepers = new ArrayList<Creeper>();
							          for (int counter = 0; counter < 3; counter++) {
							        	Creeper c = (Creeper)location.getWorld().spawnEntity(ev.getItem().getDroppedItem()[0].getLocation(), EntityType.CREEPER);
							            c.setHealth(8);
							            c.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 2147483647, 2), true);
							            c.damage(1, target);
							            c.setTarget(target);
							            c.getWorld().strikeLightningEffect(c.getLocation());
							            c.setPowered(true);
							            creepers.add(c);
							          }

							          final AtomicInteger counter = new AtomicInteger(30);
							          new kScheduler(getFalldown().getManager().getInstance(), new kScheduler.kSchedulerHandler() {
										
										@Override
										public void onRun(kScheduler arg0) {
											if (((target != null) && (target.isDead())) || target.getWorld().getUID() != creepers.get(0).getWorld().getUID() || (counter.get() <= 0)) {
								                for (Creeper wolf : creepers) {
								                  if (!wolf.isDead()) {
								                	  player.getWorld().createExplosion(wolf.getLocation(), 1.0F,false);
								                  }

								                  wolf.remove();
								                }

								                creepers.clear();
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
//	
//	@EventHandler
//	public void Launch(final PlayerInteractEvent event){
//		if(UtilEvent.isAction(event, ActionType.R)&&event.getPlayer().getItemInHand().hasItemMeta()&&event.getPlayer().getItemInHand().getItemMeta().hasDisplayName()){
//			if(event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(getRealItem().getItemMeta().getDisplayName())){
//				event.setCancelled(true);
//				
//				if(!fireEvent(event.getPlayer())){
//					LaunchItem item = new LaunchItem(event.getPlayer(),4,new LaunchItem.LaunchItemEventHandler(){
//						@Override
//						public void onLaunchItem(LaunchItemEvent ev) {
//							Creature c = (Creature)ev.getItem().getDroppedItem()[0].getLocation().getWorld().spawnCreature(ev.getItem().getDroppedItem()[0].getLocation(), CreatureType.CREEPER);
//							Player p = null;
//							
//							ArrayList<Entity> ps = new ArrayList<Entity>();
//
//							for (Player p1 : getFalldown().getGameList().getPlayers(PlayerState.IN)) {
//								if(p1.getUniqueId() == event.getPlayer().getUniqueId())continue;
//								if (c.getLocation().distance(p1.getLocation()) <= 13) {
//									ps.add(p1);
//								}
//							}
//							
//							if(!ps.isEmpty())p=(Player)ps.get(UtilMath.r(ps.size()));
//							c.setTarget(p);
//							list.put(c, event.getPlayer());
//						}
//					});
//					getFalldown().getIlManager().LaunchItem(item);
//				}
//			}
//		}
//	}

}
