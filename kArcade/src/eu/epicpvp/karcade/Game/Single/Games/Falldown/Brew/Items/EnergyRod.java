package eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.Items;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Fireball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import eu.epicpvp.karcade.Game.Single.Games.Falldown.Falldown;
import eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.BrewItem;
import eu.epicpvp.kcore.Util.UtilEvent;
import eu.epicpvp.kcore.Util.UtilEvent.ActionType;
import eu.epicpvp.kcore.Util.UtilInv;
import eu.epicpvp.kcore.Util.UtilItem;

public class EnergyRod extends BrewItem{

	public EnergyRod(Integer[] brewing_items, Falldown falldown) {
		super(100,UtilItem.Item(new ItemStack(Material.BLAZE_ROD), new String[]{"§6§§7 Schie§e einen brennenden Meteorit","§7   auf deinen Gegner"}, "§e§lEnergy Rod"),brewing_items, falldown);
	}
	
	@EventHandler
	public void Launch(final PlayerInteractEvent event){
		if(UtilEvent.isAction(event, ActionType.R)&&event.getPlayer().getItemInHand().hasItemMeta()&&event.getPlayer().getItemInHand().getItemMeta().hasDisplayName()){
			if(event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(getRealItem().getItemMeta().getDisplayName())){
				event.setCancelled(true);
				if(!fireEvent(event.getPlayer())){
					UtilInv.remove(event.getPlayer(),event.getPlayer().getItemInHand().getType(),event.getPlayer().getItemInHand().getData().getData(), 1);
					event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.CREEPER_HISS, 1.0F, 1.0F);
					Fireball fb = Bukkit.getWorld(event.getPlayer().getWorld().getName()).spawn(event.getPlayer().getEyeLocation(), Fireball.class);
					fb.setShooter(event.getPlayer());
					fb.setIsIncendiary(false);
					fb.setYield(0F);
				}
			}
		}
	}
}
