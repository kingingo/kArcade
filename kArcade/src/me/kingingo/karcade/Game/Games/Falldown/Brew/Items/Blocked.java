package me.kingingo.karcade.Game.Games.Falldown.Brew.Items;

import me.kingingo.karcade.Game.Games.Falldown.Falldown;
import me.kingingo.karcade.Game.Games.Falldown.Brew.BrewItem;
import me.kingingo.karcade.Game.Games.Falldown.Brew.Events.PlayerUseBrewItemEvent;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.Util.TimeSpan;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.UtilInv;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilTime;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class Blocked extends BrewItem{

	private long time = -1;
	
	public Blocked(ItemStack[] brewing_items,Falldown falldown) {
		super(100, UtilItem.Item(new ItemStack(Material.IRON_BARDING,1), new String[]{""}, "§cBlocked"), brewing_items, falldown);
	}
	
	@EventHandler
	public void Block(PlayerUseBrewItemEvent ev){
		if(time!=-1 && time > System.currentTimeMillis()){
			ev.getPlayer().sendMessage(Text.PREFIX_GAME.getText(getFalldown().getType().getTyp())+Text.FALLDOWN_BREWITEM_BLOCKED.getText(UtilTime.formatMili(time)));
			ev.setCancelled(true);
		}
	}

	@EventHandler
	public void Launch(final PlayerInteractEvent event){
		if(UtilEvent.isAction(event, ActionType.R)&&event.getPlayer().getItemInHand().hasItemMeta()&&event.getPlayer().getItemInHand().getItemMeta().hasDisplayName()){
			if(event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(getRealItem().getItemMeta().getDisplayName())){
				event.setCancelled(true);
				if(!fireEvent(event.getPlayer())){
					UtilInv.remove(event.getPlayer(),event.getPlayer().getItemInHand().getType(),event.getPlayer().getItemInHand().getData().getData(), 1);
					time=System.currentTimeMillis()+TimeSpan.SECOND*10;
				}
			}
		}
	}

}
