package eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.Items;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import eu.epicpvp.karcade.Game.Single.Games.Falldown.Falldown;
import eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.BrewItem;
import eu.epicpvp.kcore.Enum.PlayerState;
import eu.epicpvp.kcore.LaunchItem.LaunchItem;
import eu.epicpvp.kcore.LaunchItem.LaunchItemEvent;
import eu.epicpvp.kcore.Util.UtilEvent;
import eu.epicpvp.kcore.Util.UtilEvent.ActionType;
import eu.epicpvp.kcore.Util.UtilInv;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.kcore.Util.UtilPlayer;

public class Flashbang extends BrewItem{

	public Flashbang(Integer[] brewing_items,Falldown falldown) {
		super(100, UtilItem.Item(new ItemStack(Material.SLIME_BALL), new String[]{"§6§7 Verwirre und blende deine","§7   Gegner im Kampf."}, "§e§lFlashbang"), brewing_items, falldown);
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
							 event.getPlayer().getWorld().createExplosion(ev.getItem().getDroppedItem()[0].getLocation(), 2.0F, false);

						        for (Entity entity : getFalldown().getNearPlayers(8, ev.getItem().getDroppedItem()[0].getLocation(), true))
						          if(((Player)entity).getUniqueId() != event.getPlayer().getUniqueId() && getFalldown().getGameList().isPlayerState( ((Player)entity) ) == PlayerState.IN )
						          {
						        	UtilPlayer.addPotionEffect(((Player)entity), PotionEffectType.BLINDNESS, 20, 1);
						        	UtilPlayer.addPotionEffect(((Player)entity), PotionEffectType.CONFUSION, 20, 1);
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
