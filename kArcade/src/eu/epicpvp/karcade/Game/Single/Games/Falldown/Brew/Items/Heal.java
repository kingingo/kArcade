package eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.Items;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import eu.epicpvp.karcade.Game.Single.Games.Falldown.Falldown;
import eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.BrewItem;
import eu.epicpvp.kcore.Util.UtilEffect;
import eu.epicpvp.kcore.Util.UtilEvent;
import eu.epicpvp.kcore.Util.UtilEvent.ActionType;
import eu.epicpvp.kcore.Util.UtilInv;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.kcore.Util.UtilParticle;

public class Heal extends BrewItem{

	public Heal(Integer[] brewing_items, Falldown falldown) {
		super(100,UtilItem.Item(new ItemStack(Material.CAKE), new String[]{"§6§7 Deine Leben werden wieder aufgef§llt"}, "§e§lHeal"), brewing_items, falldown);
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void Launch(final PlayerInteractEvent event){
		if(UtilEvent.isAction(event, ActionType.R)&&event.getPlayer().getItemInHand().hasItemMeta()&&event.getPlayer().getItemInHand().getItemMeta().hasDisplayName()){
			if(event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(getRealItem().getItemMeta().getDisplayName())){
				event.setCancelled(true);
				if(!fireEvent(event.getPlayer())){
					UtilInv.remove(event.getPlayer(),event.getPlayer().getItemInHand().getType(),event.getPlayer().getItemInHand().getData().getData(), 1);
					event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.EAT, 1.0F, 1.0F);
					event.getPlayer().setHealth(event.getPlayer().getHealth() + 10 <= 20 ? event.getPlayer().getHealth() + 10 : 20);
					UtilEffect.playHelix(event.getPlayer().getLocation(), UtilParticle.HEART);
				}
			}
		}
	}
	
}
