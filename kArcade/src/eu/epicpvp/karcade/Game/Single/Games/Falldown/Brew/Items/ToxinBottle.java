package eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.Items;

import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import eu.epicpvp.karcade.Game.Single.Games.Falldown.Falldown;
import eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.BrewItem;
import eu.epicpvp.kcore.Util.UtilEvent;
import eu.epicpvp.kcore.Util.UtilEvent.ActionType;
import eu.epicpvp.kcore.Util.UtilFirework;
import eu.epicpvp.kcore.Util.UtilInv;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.kcore.Util.UtilPlayer;

public class ToxinBottle extends BrewItem{

	public ToxinBottle(Integer[] brewing_items,Falldown falldown) {
		super(100, UtilItem.Item(new ItemStack(Material.POTION,1,(byte)8228), new String[]{"§6§§7 In deinem Umkreis werden alle Gegner vergiftet"}, "§e§lToxin Bottle"), brewing_items, falldown);
	}

	@EventHandler
	public void Launch(final PlayerInteractEvent event){
		if(UtilEvent.isAction(event, ActionType.R)&&event.getPlayer().getItemInHand().hasItemMeta()&&event.getPlayer().getItemInHand().getItemMeta().hasDisplayName()){
			if(event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(getRealItem().getItemMeta().getDisplayName())){
				event.setCancelled(true);
				if(!fireEvent(event.getPlayer())){
					UtilInv.remove(event.getPlayer(),event.getPlayer().getItemInHand().getType(),event.getPlayer().getItemInHand().getData().getData(), 1);
					UtilFirework.start(event.getPlayer().getLocation(), Color.GREEN, Type.BALL_LARGE);
					for(Entity player : getFalldown().getNearPlayers(12, event.getPlayer().getLocation(), true)){
						if(!player.getName().equalsIgnoreCase(event.getPlayer().getName())){
							UtilFirework.start(player.getLocation(), Color.GREEN, Type.BURST);
				            event.getPlayer().getWorld().playEffect(player.getLocation(), Effect.ENDER_SIGNAL, 0);
							UtilPlayer.addPotionEffect(((Player)player), PotionEffectType.POISON, 5, 1);
						}
					}
				}
			}
		}
	}
	
}
