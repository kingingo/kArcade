package eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.Items;

import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;

import eu.epicpvp.karcade.Game.Single.Games.Falldown.Falldown;
import eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.BrewItem;
import eu.epicpvp.kcore.Util.UtilFirework;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.kcore.Util.UtilPlayer;

public class Snowbomb extends BrewItem{

	public Snowbomb(Integer[] brewing_items,Falldown falldown) {
		super(100, UtilItem.Item(new ItemStack(Material.SNOW_BALL,1), new String[]{"§6§§7 Schmei§e ihn auf deinen Gegner und er ","§7   lernt zu fliegen!"}, "§e§lSnowbomb"), brewing_items, falldown);
	}
	
	Player player;
	org.bukkit.entity.Snowball snowball;
	@EventHandler
	public void onProjectile(ProjectileHitEvent ev) {
		if (ev.getEntity().getType() == EntityType.SNOWBALL) {
			snowball = (org.bukkit.entity.Snowball) ev.getEntity();
			if(snowball.getShooter() instanceof Player){
				player=(Player)snowball.getShooter();
				if(!fireEvent(player)){
					for(Entity p : getFalldown().getNearPlayers(6,snowball.getLocation(), true))UtilPlayer.Knockback(snowball.getLocation(), ((Player)p), -2.0,1.5);
					UtilFirework.start(snowball.getLocation(), Color.WHITE, Type.BALL_LARGE);
				}else{
					player.getInventory().addItem(getItem());
				}
			}
		}
		
	}
}
