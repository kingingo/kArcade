package me.kingingo.karcade.Game.addons;

import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Enum.PlayerState;
import me.kingingo.kcore.Permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class AddonSpectator implements Listener {

	kArcadeManager manager;
	
	public AddonSpectator(kArcadeManager manager){
		this.manager=manager;
		Bukkit.getPluginManager().registerEvents(this, manager.getInstance());
	}

	Location ploc;
	@EventHandler
	public void Move(PlayerMoveEvent ev){
		Player p = ev.getPlayer();
			if(manager.getGame().getGameList().isPlayerState(p)==PlayerState.OUT){
				if(manager.getPermManager().hasPermission(p, Permission.SERVER_JOIN_SPECTATE))return;
				
				for (Player s : manager.getGame().getGameList().getPlayers(PlayerState.IN)) {
					if(!s.getWorld().getName().equalsIgnoreCase(p.getWorld().getName()))continue;
					ploc = s.getLocation();
					if (p.getLocation().distance(ploc) <= 5) {
						Knockback(ploc, p, -2.0, 1.5);
						break;
					}
				}
			}else if(manager.getGame().getGameList().isPlayerState(p)==PlayerState.IN){
				for (Player s : manager.getGame().getGameList().getPlayers(PlayerState.OUT)) {
					if(manager.getPermManager().hasPermission(s, Permission.SERVER_JOIN_SPECTATE))continue;
					if(!s.getWorld().getName().equalsIgnoreCase(p.getWorld().getName()))continue;
					ploc = s.getLocation();
					if (p.getLocation().distance(ploc) <= 5) {
						Knockback(p.getLocation(), s, -2.0, 1.5);
					}
				}
			}
	}
	
	public static void Knockback(Location center,Player p ,double speed, double high) {
		Vector unitVector = center.toVector().subtract(p.getLocation().toVector()).normalize();
		p.setVelocity(unitVector.multiply(speed));
		unitVector.setY(unitVector.getY() + high);
		p.setVelocity(unitVector);
	}
}
