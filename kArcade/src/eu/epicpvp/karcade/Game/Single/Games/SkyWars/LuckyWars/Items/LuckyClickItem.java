package eu.epicpvp.karcade.Game.Single.Games.SkyWars.LuckyWars.Items;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import dev.wolveringer.client.Callback;
import eu.epicpvp.kcore.Util.UtilEvent;
import eu.epicpvp.kcore.Util.UtilEvent.ActionType;
import eu.epicpvp.kcore.Util.UtilInv;
import eu.epicpvp.kcore.Util.UtilItem;
import lombok.Setter;

public class LuckyClickItem extends LuckyItemListener{
	
	@Setter
	private Callback<Player> click;
	
	public LuckyClickItem(ItemStack item,double chance) {
		this(item,chance,null);
	}
	
	public LuckyClickItem(ItemStack item,double chance, Callback<Player> click) {
		super(item,chance);
		this.click=click;
	}
	
	@EventHandler
	public void click(final PlayerInteractEvent event){
		if(UtilEvent.isAction(event, ActionType.R)&&event.getPlayer().getItemInHand().hasItemMeta()&&event.getPlayer().getItemInHand().getItemMeta().hasDisplayName()){
			if(UtilItem.ItemNameEquals(event.getPlayer().getItemInHand(), getItem())){
				event.setCancelled(true);
		   		UtilInv.remove(event.getPlayer(), getItem(), 1);
				this.click.call(event.getPlayer(), null);
			}
		}
	}
}
