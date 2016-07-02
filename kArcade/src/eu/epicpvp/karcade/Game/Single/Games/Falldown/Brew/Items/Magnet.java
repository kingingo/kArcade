package eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.Items;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import eu.epicpvp.karcade.Game.Single.Games.Falldown.Falldown;
import eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.BrewItem;
import eu.epicpvp.kcore.Util.UtilEvent;
import eu.epicpvp.kcore.Util.UtilEvent.ActionType;
import eu.epicpvp.kcore.Util.UtilInv;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.kcore.Util.UtilParticle;

public class Magnet extends BrewItem{

	public Magnet(Integer[] brewing_items,Falldown falldown) {
		super(100, UtilItem.Item(new ItemStack(Material.GOLD_NUGGET), new String[]{"§6§7 Alle Spieler in deiner Umgebung werden ","§7   ruckartig zu dir gezogen!"}, "§e§lMagnet"), brewing_items, falldown);
	}

	@EventHandler
	public void Launch(final PlayerInteractEvent event){
		if(UtilEvent.isAction(event, ActionType.RIGHT)&&event.getPlayer().getItemInHand().hasItemMeta()&&event.getPlayer().getItemInHand().getItemMeta().hasDisplayName()){
			if(event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(getRealItem().getItemMeta().getDisplayName())){
				event.setCancelled(true);
				if(!fireEvent(event.getPlayer())){
					UtilInv.remove(event.getPlayer(),event.getPlayer().getItemInHand().getType(),event.getPlayer().getItemInHand().getData().getData(), 1);
					for(Entity player : getFalldown().getNearPlayers(20, event.getPlayer().getLocation(), true)){
						Vector unitVector = event.getPlayer().getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
						player.setVelocity(unitVector.multiply(2.0));
						unitVector.setY(unitVector.getY() + 0.5);
						player.setVelocity(unitVector);
						UtilParticle.CLOUD.display(4, 2, 4, 1, 20, player.getLocation(), 30);
					}
				}
			}
		}
	}
	
}
