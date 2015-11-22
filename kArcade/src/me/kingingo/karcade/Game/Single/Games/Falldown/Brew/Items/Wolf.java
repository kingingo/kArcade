package me.kingingo.karcade.Game.Single.Games.Falldown.Brew.Items;

import java.util.TreeMap;

import me.kingingo.karcade.Game.Single.Games.Falldown.Falldown;
import me.kingingo.karcade.Game.Single.Games.Falldown.Brew.BrewItem;
import me.kingingo.kcore.LaunchItem.LaunchItem;
import me.kingingo.kcore.LaunchItem.LaunchItemEvent;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilMath;
import me.kingingo.kcore.Util.UtilPlayer;

import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class Wolf extends BrewItem{

	public Wolf(Integer[] brewing_items,Falldown falldown) {
		super(100, UtilItem.Item(new ItemStack(Material.BONE,UtilMath.RandomInt(6, 3)), new String[]{""}, "§bWolf"), brewing_items, falldown);
	}

	@EventHandler
	public void Launch(final PlayerInteractEvent event){
		if(UtilEvent.isAction(event, ActionType.R)&&event.getPlayer().getItemInHand().hasItemMeta()&&event.getPlayer().getItemInHand().getItemMeta().hasDisplayName()){
			if(event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(getRealItem().getItemMeta().getDisplayName())){
				event.setCancelled(true);
				if(!fireEvent(event.getPlayer())){
					LaunchItem item = new LaunchItem(event.getPlayer(),4,new LaunchItem.LaunchItemEventHandler(){
						@Override
						public void onLaunchItem(LaunchItemEvent ev) {
							Creature c = (Creature)ev.getItem().getDroppedItem()[0].getLocation().getWorld().spawnCreature(ev.getItem().getDroppedItem()[0].getLocation(), CreatureType.WOLF);
							Player p = null;
							TreeMap<Double, Player> l = UtilPlayer.getNearby(ev.getItem().getDroppedItem()[0].getLocation(), 9);
							if(!l.isEmpty())p=l.get(UtilMath.r(l.size()));
							c.setTarget(p);
						}
					});
					getFalldown().getIlManager().LaunchItem(item);
				}
			}
		}
	}

}
