package eu.epicpvp.karcade.Game.Single.Games.SkyWars.LuckyWars.Items;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import eu.epicpvp.datenclient.client.Callback;
import eu.epicpvp.kcore.Enum.Team;
import eu.epicpvp.kcore.Util.UtilItem;

public class LuckyWolf extends LuckyClickItem {

	private HashMap<Wolf, Player> spawned = new HashMap<>();
	private HashMap<Wolf, Integer> failedAttemptsToLocation = new HashMap<>();

	public LuckyWolf(double chance) {
		super(UtilItem.RenameItem(new ItemStack(Material.BONE), "§6§lLucky Wolf Spawn Bone"), chance);

		setClick(new Callback<Player>() {

			@Override
			public void call(Player player, Throwable exception) {
				Location location = player.getLocation();
				Wolf wolf = (Wolf) location.getWorld().spawnEntity(location, EntityType.WOLF);
				wolf.setCustomName("§6" + player.getName() + "'s§l Lucky Wolf");
				wolf.setCustomNameVisible(true);
				wolf.setAdult();
				wolf.setAgeLock(true);
				Team team = getAddon().getInstance().getTeam(player);
				spawned.put(wolf, player);
				failedAttemptsToLocation.put(wolf, 0);
				if (team != null) {
					wolf.setCollarColor(org.bukkit.DyeColor.getByWoolData(team.getItem().getData().getData()));
					wolf.setOwner(player);
					wolf.setTamed(true);
					wolf.setBreed(true);
		            wolf.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 2147483647, 1), true);
				}
			}
		});
	}

//	@EventHandler
//	public void onUpdate(UpdateEvent event) {
//		if (event.getType() != UpdateType.FASTER)
//			return;
//		if (spawned.isEmpty())
//			return;
//
//		for (Wolf wolf : new ArrayList<>(spawned.keySet())) {
//			Player owner = spawned.get(wolf);
//
//			if (owner.isOnline() && getAddon().getInstance().getGameList().isPlayerState(owner) == PlayerState.IN
//					&& !wolf.isDead()) {
//				if (wolf.isAngry()) {
//					if (wolf.getTarget() != null && wolf.getTarget() instanceof Player) {
//						Player target = (Player) wolf.getTarget();
//
//						if (target.isOnline()
//								&& getAddon().getInstance().getGameList().isPlayerState(target) == PlayerState.IN)
//							continue;
//					}
//					wolf.setAngry(false);
//					wolf.setTarget(null);
//				}
//
//				Location wolfSpot = wolf.getLocation();
//				Location ownerSpot = owner.getLocation();
//
//				int xDiff = Math.abs(wolfSpot.getBlockX() - ownerSpot.getBlockX());
//				int yDiff = Math.abs(wolfSpot.getBlockY() - ownerSpot.getBlockY());
//				int zDiff = Math.abs(wolfSpot.getBlockZ() - ownerSpot.getBlockZ());
//
//				if (xDiff + yDiff + zDiff > 4) {
//					EntityInsentient ec = (EntityInsentient) ((CraftLivingEntity) wolf).getHandle();
//					NavigationAbstract nav = ec.getNavigation();
//					int xIndex = -1;
//					int zIndex = -1;
//					Block targetBlock = ownerSpot.getBlock().getRelative(xIndex, -1, zIndex);
//					while ((targetBlock.isEmpty()) || (targetBlock.isLiquid())) {
//						if (xIndex < 2) {
//							xIndex++;
//						} else if (zIndex < 2) {
//							xIndex = -1;
//							zIndex++;
//						} else {
//							break;
//						}
//						targetBlock = ownerSpot.getBlock().getRelative(xIndex, -1, zIndex);
//					}
//
//					if (((Integer) this.failedAttemptsToLocation.get(wolf)).intValue() > 6) {
//						if (wolf.getPassenger() != null) {
//							Entity passenger = wolf.getPassenger();
//							passenger.leaveVehicle();
//
//							passenger.teleport(ownerSpot);
//							wolf.teleport(ownerSpot);
//							wolf.setPassenger(passenger);
//						} else {
//							wolf.teleport(ownerSpot);
//						}
//
//						this.failedAttemptsToLocation.put(wolf, Integer.valueOf(0));
//					} else if (!nav.a(targetBlock.getX(), targetBlock.getY() + 1, targetBlock.getZ(), 1.6D)) {
//						if (wolf.getFallDistance() == 0.0F || wolf.getLocation().distance(ownerSpot) > 12) {
//							this.failedAttemptsToLocation.put(wolf, Integer
//									.valueOf(((Integer) this.failedAttemptsToLocation.get(wolf)).intValue() + 1));
//						}
//					} else {
//						this.failedAttemptsToLocation.put(wolf, Integer.valueOf(0));
//					}
//				}
//			} else {
//				spawned.remove(wolf);
//				failedAttemptsToLocation.remove(wolf);
//
//				wolf.remove();
//			}
//		}
//	}
//
//	public void attack(Player player, Player attack) {
//		for (Wolf w : spawned.keySet()) {
//			if (spawned.get(w).getUniqueId() == player.getUniqueId()) {
//				w.setAngry(true);
//				w.setTarget(attack);
//			}
//		}
//	}
//
//	@EventHandler
//	public void damage(EntityDamageByEntityEvent ev) {
//		if (spawned.isEmpty())return;
//
//		if (ev.getEntity() instanceof Wolf && ev.getDamager() instanceof Player) {
//			Wolf wolf = (Wolf) ev.getEntity();
//
//			if (spawned.containsKey(wolf)) {
//				Player attacker = null;
//				if (ev.getDamager() instanceof Player) {
//					attacker = (Player) ev.getDamager();
//				} else if (ev.getDamager() instanceof Projectile) {
//					ProjectileSource source = ((Projectile) ev.getDamager()).getShooter();
//					if (source instanceof Player) {
//						attacker = (Player) source;
//					}
//				}
//
//				if (attacker != null
//						&& getAddon().getInstance().getGameList().isPlayerState(attacker) == PlayerState.IN) {
//					Team team = getAddon().getInstance().getTeam(attacker);
//					Team wolfTeam = getAddon().getInstance().getTeam(spawned.get(wolf));
//
//					if (wolfTeam != null && team != null) {
//						if (wolfTeam == team) {
//							ev.setCancelled(true);
//						} else {
//							wolf.setTarget(attacker);
//							if (wolf.isAngry())
//								wolf.setAngry(true);
//						}
//					}
//				}
//			}
//		}else if(ev.getEntity() instanceof Player && ev.getDamager() instanceof Player){
//			attack(((Player)ev.getEntity()), ((Player)ev.getDamager()));
//			attack(((Player)ev.getDamager()), ((Player)ev.getEntity()));
//		}
//	}
}
