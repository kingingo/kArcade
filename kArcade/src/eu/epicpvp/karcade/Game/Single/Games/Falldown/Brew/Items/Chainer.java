package eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.Items;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import eu.epicpvp.karcade.Game.Single.Games.Falldown.Falldown;
import eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.BrewItem;
import eu.epicpvp.kcore.Enum.PlayerState;
import eu.epicpvp.kcore.LaunchItem.LaunchItem;
import eu.epicpvp.kcore.LaunchItem.LaunchItemEvent;
import eu.epicpvp.kcore.Scheduler.kScheduler;
import eu.epicpvp.kcore.Update.UpdateType;
import eu.epicpvp.kcore.Util.UtilEvent;
import eu.epicpvp.kcore.Util.UtilEvent.ActionType;
import eu.epicpvp.kcore.Util.UtilInv;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.kcore.Util.UtilPlayer;

public class Chainer extends BrewItem{

	public Chainer(Integer[] brewing_items,Falldown falldown) {
		super(100, UtilItem.Item(new ItemStack(Material.IRON_INGOT,2), new String[]{"§6§7 Jede Flucht wird dir gelingen,","§7   denn um dir herum sind alle","§7   aufeinmal viel langsamer!"}, "§e§lChainer"), brewing_items, falldown);
	}

	@EventHandler
	public void explosion(EntityExplodeEvent ev){
		for(org.bukkit.block.Block b : ev.blockList()){
			b.setType(Material.AIR);
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
							Location location = ev.getItem().getDroppedItem()[0].getLocation();
							event.getPlayer().getWorld().playSound(location, Sound.IRONGOLEM_HIT, 1.0F, 1.0F);
					        location.getWorld().playEffect(location, Effect.POTION_BREAK, 16394);

					        for (final Entity entity : getFalldown().getNearPlayers(8, location, true))
					        	 if(((Player)entity).getUniqueId() != event.getPlayer().getUniqueId() && getFalldown().getGameList().isPlayerState( ((Player)entity) ) == PlayerState.IN ){
					            UtilPlayer.addPotionEffect(((Player)entity), PotionEffectType.SLOW, 10, 3);

					            new kScheduler(getFalldown().getManager().getInstance(), new kScheduler.kSchedulerHandler() {
									
									@Override
									public void onRun(kScheduler arg0) {
										if ((entity.isDead()) || (!((LivingEntity)entity).hasPotionEffect(PotionEffectType.SLOW))) {
							                  arg0.close();
							            }else{
							                entity.getWorld().playEffect(entity.getLocation(), Effect.STEP_SOUND, Material.IRON_BLOCK.getId(), 10);
							            }
									}
								}, UpdateType.SEC_2);
					          }
							
						}
					});
					getFalldown().getIlManager().LaunchItem(item);
					UtilInv.remove(event.getPlayer(), event.getPlayer().getItemInHand().getType(), event.getPlayer().getItemInHand().getData().getData(), 1);
				
				}
			}
		}
	}
	
}
