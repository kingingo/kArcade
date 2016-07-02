package eu.epicpvp.karcade.Game.Single.Addons;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import eu.epicpvp.karcade.Game.Single.SingleGame;
import eu.epicpvp.kcore.Enum.PlayerState;
import eu.epicpvp.kcore.Permission.PermissionType;

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
			if(game.getGameList().isPlayerState(p)==PlayerState.SPECTATOR){
				if(game.getManager().getPermManager().hasPermission(p, PermissionType.SERVER_JOIN_SPECTATE))return;
				
				for (Player s : game.getGameList().getPlayers(PlayerState.INGAME)) {
					if(!s.getWorld().getName().equalsIgnoreCase(p.getWorld().getName()))continue;
					ploc = s.getLocation();
					if (p.getLocation().distance(ploc) <= 5) {
						Knockback(ploc, p, -2.0, 1.5);
						break;
					}
				}
			}else if(game.getGameList().isPlayerState(p)==PlayerState.INGAME){
				for (Player s : game.getGameList().getPlayers(PlayerState.SPECTATOR)) {
					if(game.getManager().getPermManager().hasPermission(s, PermissionType.SERVER_JOIN_SPECTATE))continue;
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
