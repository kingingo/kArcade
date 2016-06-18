package eu.epicpvp.karcade.Game.Single.Games.SkyWars.LuckyWars.Items;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import eu.epicpvp.kcore.LaunchItem.LaunchItem;
import eu.epicpvp.kcore.LaunchItem.LaunchItem.LaunchItemEventHandler;
import eu.epicpvp.kcore.Util.UtilEvent;
import eu.epicpvp.kcore.Util.UtilEvent.ActionType;
import eu.epicpvp.kcore.Util.UtilItem;
import lombok.Setter;

public class LuckyThrowItem extends LuckyItemListener{
	
	@Setter
	private LaunchItemEventHandler launch;
	private int seconds;

	public LuckyThrowItem(ItemStack item, int seconds,double chance) {
		this(item,seconds,null,chance);
	}
	
	public LuckyThrowItem(ItemStack item, int seconds,LaunchItemEventHandler launch,double chance) {
		super(item,chance);
		this.launch=launch;
		this.seconds=seconds;
	}
	
	@EventHandler
	public void Launch(final PlayerInteractEvent event){
		if(UtilEvent.isAction(event, ActionType.R)&&event.getPlayer().getItemInHand().hasItemMeta()&&event.getPlayer().getItemInHand().getItemMeta().hasDisplayName()){
			if(UtilItem.ItemNameEquals(event.getPlayer().getItemInHand(), getItem())){
				event.setCancelled(true);
				LaunchItem item = new LaunchItem(event.getPlayer(),seconds, launch);
				getAddon().getInstance().getIlManager().LaunchItem(item);
			}
		}
	}
}
