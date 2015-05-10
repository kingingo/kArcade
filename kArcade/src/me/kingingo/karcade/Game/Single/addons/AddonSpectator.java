package me.kingingo.karcade.Game.Single.addons;

import me.kingingo.karcade.Enum.PlayerState;
import me.kingingo.karcade.Game.Single.SingleGame;
import me.kingingo.kcore.Permission.kPermission;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class AddonSpectator implements Listener {

	SingleGame game;
	
	public AddonSpectator(SingleGame game){
		this.game=game;
		Bukkit.getPluginManager().registerEvents(this, game.getManager().getInstance());
	}

	Location ploc;
	@EventHandler
	public void Move(PlayerMoveEvent ev){
		Player p = ev.getPlayer();
			if(game.getGameList().isPlayerState(p)==PlayerState.OUT){
				if(game.getManager().getPermManager().hasPermission(p, kPermission.SERVER_JOIN_SPECTATE))return;
				
				for (Player s : game.getGameList().getPlayers(PlayerState.IN)) {
					if(!s.getWorld().getName().equalsIgnoreCase(p.getWorld().getName()))continue;
					ploc = s.getLocation();
					if (p.getLocation().distance(ploc) <= 5) {
						Knockback(ploc, p, -2.0, 1.5);
						break;
					}
				}
			}else if(game.getGameList().isPlayerState(p)==PlayerState.IN){
				for (Player s : game.getGameList().getPlayers(PlayerState.OUT)) {
					if(game.getManager().getPermManager().hasPermission(s, kPermission.SERVER_JOIN_SPECTATE))continue;
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
