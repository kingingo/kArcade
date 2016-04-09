package eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.Items;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import eu.epicpvp.karcade.Game.Single.Games.Falldown.Falldown;
import eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.BrewItem;
import eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.Events.PlayerUseBrewItemEvent;
import eu.epicpvp.kcore.Translation.TranslationManager;
import eu.epicpvp.kcore.Util.TimeSpan;
import eu.epicpvp.kcore.Util.UtilEvent;
import eu.epicpvp.kcore.Util.UtilEvent.ActionType;
import eu.epicpvp.kcore.Util.UtilInv;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.kcore.Util.UtilTime;

public class Blocked extends BrewItem{

	private long time = -1;
	
	public Blocked(Integer[] brewing_items,Falldown falldown) {
		super(100, UtilItem.Item(new ItemStack(Material.IRON_BARDING,1), new String[]{"§6§§7 Blockiert alle Braust§nde!"}, "§e§lBlocked"), brewing_items, falldown);
	}
	
	@EventHandler
	public void Block(PlayerUseBrewItemEvent ev){
		if(time!=-1 && time > System.currentTimeMillis()){
			ev.getPlayer().sendMessage(TranslationManager.getText(ev.getPlayer(), "PREFIX_GAME", getFalldown().getType().getTyp())+TranslationManager.getText(ev.getPlayer(), "FALLDOWN_BREWITEM_BLOCKED", UtilTime.formatMili(time)));
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
