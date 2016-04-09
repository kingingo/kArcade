package eu.epicpvp.karcade.Game.Single.Games.CustomWars.Items;

import org.bukkit.Material;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import eu.epicpvp.karcade.Game.Single.Games.CustomWars.CustomWars;
import eu.epicpvp.karcade.Game.Single.Games.CustomWars.CustomWarsItem;
import eu.epicpvp.kcore.Util.UtilInv;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.kcore.Util.UtilParticle;
import eu.epicpvp.kcore.Util.UtilServer;

public class Parachute extends CustomWarsItem{
	
	public Parachute(CustomWars instance) {
		super(instance, UtilItem.RenameItem(new ItemStack(Material.FEATHER), "§cFallschirm"), CustomWars.Gold(10));
	}

	@EventHandler
	public void inter(PlayerInteractEvent ev){
		if(ev.getPlayer().getItemInHand().getType()==Material.FEATHER){
			UtilInv.remove(ev.getPlayer(), ev.getPlayer().getItemInHand().getType(), ev.getPlayer().getItemInHand().getData().getData(), 1);
			
			Chicken e = (Chicken)ev.getPlayer().getWorld().spawnEntity(ev.getPlayer().getLocation(), EntityType.CHICKEN);
			e.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,600*20,1),true);
			e.setPassenger(ev.getPlayer().getWorld().spawnEntity(ev.getPlayer().getLocation(), EntityType.CHICKEN));
			ev.getPlayer().setPassenger(e);
			logMessage("ADD "+ev.getPlayer());
		}
	}
	
	@EventHandler
	public void damage(EntityDamageByEntityEvent ev){
		if(ev.getEntity() instanceof Chicken){
			for(Player player : UtilServer.getPlayers())
				try {
					UtilParticle.EXPLOSION_LARGE.sendToPlayer(player, ev.getEntity().getLocation(), 1, 1, 1, 1, 2);
				} catch (Exception e) {
					e.printStackTrace();
				}
			ev.getEntity().remove();
		}
	}

	Vector vel;
	Vector dir;
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent ev){
		if(ev.getPlayer().getPassenger()!=null){
			if(ev.getPlayer().getPassenger().getType()==EntityType.CHICKEN){
				if(ev.getPlayer().isSneaking()||ev.getPlayer().isOnGround()||ev.getPlayer().getLocation().getY()<-10){
					Entity e = ev.getPlayer().getPassenger();
					Entity e1 = e.getPassenger();
					e.eject();
					e1.remove();
					ev.getPlayer().eject();
					e.remove();
					
					if(ev.getPlayer().isSneaking()){
						logMessage("REMOVE isSneaking "+ev.getPlayer());
					}
					
					if(ev.getPlayer().isOnGround()){
						logMessage("REMOVE isOnGround "+ev.getPlayer());
					}
					
					if(ev.getPlayer().getLocation().getY()<-10){
						logMessage("REMOVE ev.getPlayer().getLocation().getY()<-10 "+ev.getPlayer());
					}
					
					return;
				}
				
				vel = ev.getPlayer().getVelocity();
			    dir = ev.getPlayer().getLocation().getDirection();
			    ev.getPlayer().setVelocity(new Vector(dir.getX() * 0.4, vel.getY() * 0.4, dir.getZ() * 0.4));
			    ev.getPlayer().setFallDistance(1);
			}
		}
	}
}
