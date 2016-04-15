package eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.Items;


import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityExplodeEvent;
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

public class Budder extends BrewItem{

	public Budder(Integer[] brewing_items,Falldown falldown) {
		super(100, UtilItem.Item(new ItemStack(Material.GOLD_INGOT,2), new String[]{"§6§7 Du bekommst 20 Sekunden lang","§7   Regeneration."}, "§e§lBudder"), brewing_items, falldown);
	}

	@EventHandler
	public void explosion(EntityExplodeEvent ev){
		for(org.bukkit.block.Block b : ev.blockList()){
			b.setType(Material.AIR);
		}
		ev.blockList().clear();
	}
	
	@EventHandler
	public void Launch(final PlayerInteractEvent event){
		if(UtilEvent.isAction(event, ActionType.R)&&event.getPlayer().getItemInHand().hasItemMeta()&&event.getPlayer().getItemInHand().getItemMeta().hasDisplayName()){
			if(UtilItem.ItemNameEquals(getRealItem(), event.getPlayer().getItemInHand())){
				event.setCancelled(true);
				if(!fireEvent(event.getPlayer())){
					UtilInv.remove(event.getPlayer(), event.getPlayer().getItemInHand().getType(), event.getPlayer().getItemInHand().getData().getData(), 1);
					event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.EAT, 1.0F, 1.0F);
					UtilPlayer.addPotionEffect(event.getPlayer(), PotionEffectType.REGENERATION, 20, 1);
					UtilPlayer.addPotionEffect(event.getPlayer(), PotionEffectType.SPEED, 2, 10);
					UtilFirework.start(event.getPlayer().getLocation(), Color.YELLOW, Type.BALL_LARGE);
				}
			}
		}
	}
	
}
