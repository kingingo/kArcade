package me.kingingo.karcade.Game.Single.Games.Falldown.Brew.Items;

import me.kingingo.karcade.Game.Single.Games.Falldown.Falldown;
import me.kingingo.karcade.Game.Single.Games.Falldown.Brew.BrewItem;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.UtilInv;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilPlayer;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class Magnet extends BrewItem{

	public Magnet(Integer[] brewing_items,Falldown falldown) {
		super(100, UtilItem.Item(new ItemStack(Material.GOLD_NUGGET), new String[]{""}, "§bMagnet"), brewing_items, falldown);
	}

	@EventHandler
	public void Launch(final PlayerInteractEvent event){
		if(UtilEvent.isAction(event, ActionType.R)&&event.getPlayer().getItemInHand().hasItemMeta()&&event.getPlayer().getItemInHand().getItemMeta().hasDisplayName()){
			if(event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(getRealItem().getItemMeta().getDisplayName())){
				event.setCancelled(true);
				if(!fireEvent(event.getPlayer())){
					UtilInv.remove(event.getPlayer(),event.getPlayer().getItemInHand().getType(),event.getPlayer().getItemInHand().getData().getData(), 1);
					for(Player player : UtilPlayer.getNearby(event.getPlayer().getLocation(), 20)){
						Vector unitVector = event.getPlayer().getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
						player.setVelocity(unitVector.multiply(2.0));
						unitVector.setY(unitVector.getY() + 0.2);
						player.setVelocity(unitVector);
					}
				}
			}
		}
	}
	
}
