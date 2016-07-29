package eu.epicpvp.karcade.Game.Single.Games.SkyWars.LuckyWars.Items;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.inventory.ItemStack;

import eu.epicpvp.kcore.Enum.Team;
import eu.epicpvp.kcore.LaunchItem.LaunchItem;
import eu.epicpvp.kcore.LaunchItem.LaunchItemEvent;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.kcore.Util.UtilMath;
import eu.epicpvp.kcore.Util.UtilPlayer;

public class LuckyCreeper extends LuckyThrowItem {
	
	private HashMap<Creeper,Player> spawned = new HashMap<>();
	
	public LuckyCreeper(double chance) {
		super(UtilItem.RenameItem(new ItemStack(Material.SKULL_ITEM, 1, (byte) 4), "§6§lLucky Creeper Spawn Egg"), 4,chance);
		
		setLaunch(new LaunchItem.LaunchItemEventHandler() {
					@Override
					public void onLaunchItem(LaunchItemEvent ev) {
						Location location = ev.getItem().getDroppedItem()[0].getLocation();
						Creeper c = (Creeper) location.getWorld().spawnEntity(location, EntityType.CREEPER);
						c.setCustomName("§6§lLucky Creeper");

						Player player = null;

						HashMap<Player, Double> l = UtilPlayer.getInRadius(ev.getItem().getDroppedItem()[0].getLocation(), 9);
						l.remove(ev.getItem().getPlayer());
						if (!l.isEmpty()) {
							player = (Player) l.keySet().toArray()[UtilMath.randomInteger(l.size())];
							c.setTarget(player);
						}
						
						spawned.put(c, ev.getItem().getPlayer());
					}
				});
	}

	@EventHandler
	public void Target(EntityTargetEvent ev) {
		if (ev.getEntity() instanceof Creature) {
			if (ev.getEntity() instanceof Creeper) {
				if (ev.getTarget() instanceof Player) {
					Creeper c = (Creeper) ev.getEntity();
					if (spawned.containsKey(c)) {
						Player owner = spawned.get(c);
						if (owner.isOnline()) {
							Team team = getAddon().getInstance().getTeam(owner);
							Player target = (Player) ev.getTarget();
							if (getAddon().getInstance().getTeam(target) == team) {
								ev.setCancelled(true);
							}
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void death(EntityDeathEvent ev) {
		if (ev.getEntity() instanceof Creature && ev.getEntity() instanceof Creeper) {
			Creeper c = (Creeper) ev.getEntity();
			if (spawned.containsKey(c)) {
				ev.setDroppedExp(0);
				ev.getDrops().clear();
				spawned.remove(c);
			}
		}
	}

	@EventHandler
	public void Explode(EntityExplodeEvent ev) {
		if (ev.getEntity() instanceof Creature) {
			if (ev.getEntity() instanceof Creeper) {
				Creeper c = (Creeper) ev.getEntity();
				if (spawned.containsKey(c)) {
					ev.blockList().clear();
					HashMap<Player, Double> list = UtilPlayer.getInRadius(ev.getLocation(), 5);
					Player owner = spawned.get(c);

					for (Player player : list.keySet())
						player.damage(12 / list.get(player), owner);
				}

			}
		}
	}
}
