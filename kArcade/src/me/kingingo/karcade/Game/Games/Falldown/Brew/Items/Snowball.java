package me.kingingo.karcade.Game.Games.Falldown.Brew.Items;

import me.kingingo.karcade.Game.Games.Falldown.Falldown;
import me.kingingo.karcade.Game.Games.Falldown.Brew.BrewItem;
import me.kingingo.kcore.Util.UtilFirework;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilPlayer;

import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;

public class Snowball extends BrewItem{

	public Snowball(ItemStack[] brewing_items,Falldown falldown) {
		super(100, UtilItem.Item(new ItemStack(Material.SNOW_BALL,1), new String[]{""}, "§aSnowball"), brewing_items, falldown);
	}
	
	Player player;
	org.bukkit.entity.Snowball snowball;
	@EventHandler
	public void onProjectile(ProjectileHitEvent ev) {
		if (ev.getEntity() instanceof Snowball) {
			snowball = (org.bukkit.entity.Snowball) ev.getEntity();
			if(snowball.getShooter() instanceof Player){
				player=(Player)snowball.getShooter();
				if(!fireEvent(player)){
					for(Player p : UtilPlayer.getNearby(snowball.getLocation(), 6))UtilPlayer.Knockback(snowball.getLocation(), p, -2.0,1.5);
					UtilFirework.start(snowball.getLocation(), Color.WHITE, Type.BALL_LARGE);
				}else{
					player.getInventory().addItem(getItem());
				}
			}
		}
		
	}
}
