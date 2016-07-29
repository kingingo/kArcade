package eu.epicpvp.karcade.Game.Single.Games.SkyWars.LuckyWars.Items;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.MushroomCow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import eu.epicpvp.kcore.LaunchItem.LaunchItem;
import eu.epicpvp.kcore.LaunchItem.LaunchItemEvent;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.kcore.Util.UtilMath;

public class LuckyCow extends LuckyThrowItem{

	public LuckyCow(double chance) {
		super( UtilItem.RenameItem(new ItemStack(Material.RED_MUSHROOM), "§cLuckyCow Spawn Mushroom"), 2, new LaunchItem.LaunchItemEventHandler() {
			
			@Override
			public void onLaunchItem(LaunchItemEvent event) {
				Location location = event.getItem().getDroppedItem()[0].getLocation();
				MushroomCow cow = (MushroomCow) location.getWorld().spawnEntity(location, EntityType.MUSHROOM_COW);
				
				cow.setBaby();
				cow.setAgeLock(true);
				cow.setRemoveWhenFarAway(false);
				cow.setTarget(event.getItem().getPlayer());
				cow.setCustomName("§6§lLucky Cow");
				cow.setCustomNameVisible(true);
			}
		},chance);
	}
	
	@EventHandler
	public void death(EntityDeathEvent ev){
		if(ev.getEntity() instanceof MushroomCow){
			ev.getDrops().clear();
			ev.setDroppedExp(UtilMath.RandomInt(500, 100));
			
			ev.getDrops().add(new ItemStack(Material.COOKED_BEEF,UtilMath.RandomInt(15, 10)));
			
			if(UtilMath.randomInteger(250)<25){
				ev.getDrops().add(new ItemStack(Material.GOLDEN_CARROT,2));
				ev.getDrops().add(new ItemStack(Material.POTION,UtilMath.RandomInt(3, 1),(byte)16421));
			}else
				ev.getDrops().add(new ItemStack(Material.GOLDEN_APPLE,1));
			
		}
	}

}
