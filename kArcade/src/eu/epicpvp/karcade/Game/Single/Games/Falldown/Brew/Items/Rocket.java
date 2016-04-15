package eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.Items;

import java.util.List;
import java.util.Set;

import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import eu.epicpvp.karcade.Game.Single.Games.Falldown.Falldown;
import eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.BrewItem;
import eu.epicpvp.kcore.Util.UtilEvent;
import eu.epicpvp.kcore.Util.UtilEvent.ActionType;
import eu.epicpvp.kcore.Util.UtilFirework;
import eu.epicpvp.kcore.Util.UtilInv;
import eu.epicpvp.kcore.Util.UtilItem;

public class Rocket extends BrewItem{

	public Rocket(Integer[] brewing_items, Falldown falldown) {
		super(100,UtilItem.Item(new ItemStack(Material.GOLD_HOE), new String[]{"§6§7 Schie§e eine verauberte Rakete","§7   auf Gegner."}, "§6Rocket"),brewing_items, falldown);
	}
	
	@EventHandler
	public void Launch(final PlayerInteractEvent event){
		if(UtilEvent.isAction(event, ActionType.R)&&event.getPlayer().getItemInHand().hasItemMeta()&&event.getPlayer().getItemInHand().getItemMeta().hasDisplayName()){
			if(event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(getRealItem().getItemMeta().getDisplayName())){
				event.setCancelled(true);
				if(!fireEvent(event.getPlayer())){
					UtilInv.remove(event.getPlayer(),event.getPlayer().getItemInHand().getType(),event.getPlayer().getItemInHand().getData().getData(), 1);
					useRocket(event.getPlayer());
				}
			}
		}
	}
	
	public void useRocket(Player player){
		UtilFirework.start(player.getLocation(), Color.RED, Type.BURST);
		List<Block> blocks = player.getLineOfSight((Set<Material>)null, 80);
		
		 for (int i=0; i<blocks.size(); i++) {
			 Block b = blocks.get(i);
			 Location loc = b.getLocation();
			 loc.add(0, 4, 0);;
			 UtilFirework.start(loc, Color.RED, Type.BURST);
			 if(i == blocks.size() -1){
				 
				 for(Entity e : getFalldown().getNearPlayers(10, loc, true)){
					 if(e instanceof Player){
						 Player p1 = (Player)e;
						 
						 p1.damage(8, player);
						 p1.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 1), true);
						 p1.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 150, 1), true);
						 p1.getWorld().playSound(p1.getLocation(), Sound.BLAZE_HIT, 1.0F, 1.0F);
						 
					 }else if(e instanceof Wolf){
						((Wolf) e).damage(8, player);
					 }else if(e instanceof Zombie){
						((Zombie) e).damage(8, player);
					 }
				 }

				UtilFirework.start(loc, Color.YELLOW, Type.BALL_LARGE);
				UtilFirework.start(loc, Color.BLACK, Type.BALL_LARGE);
				UtilFirework.start(loc, Color.WHITE, Type.STAR);
				UtilFirework.start(loc, Color.LIME, Type.STAR);
				UtilFirework.start(loc, Color.PURPLE, Type.BURST);
				UtilFirework.start(loc, Color.PURPLE, Type.BURST);
			 }
		 }
	}

}
