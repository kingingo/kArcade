package eu.epicpvp.karcade.Listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;

import dev.wolveringer.dataserver.gamestats.GameState;
import eu.epicpvp.karcade.Game.Events.GameStateChangeEvent;
import eu.epicpvp.kcore.Listener.kListener;
import eu.epicpvp.kcore.Util.TimeSpan;

public class DeathFixListener extends kListener{
	
	private long change;
	
	public DeathFixListener(JavaPlugin instance) {
		super(instance, "DeathFixListener");
		this.change=System.currentTimeMillis();
	}
	
	@EventHandler
	public void damgeByB(EntityDamageByBlockEvent ev){
		if((change+TimeSpan.SECOND*5) > System.currentTimeMillis()){
			ev.setCancelled(true);
		}
	}
	
	@EventHandler
	public void damgeBy(EntityDamageByEntityEvent ev){
		if((change+TimeSpan.SECOND*5) > System.currentTimeMillis()){
			ev.setCancelled(true);
		}
	}
	
	@EventHandler
	public void damge(EntityDamageEvent ev){
		if((change+TimeSpan.SECOND*5) > System.currentTimeMillis()){
			ev.setCancelled(true);
		}
	}

	@EventHandler
	public void GameStateChange(GameStateChangeEvent ev){
		if(ev.getTo() == GameState.InGame){
			this.change=System.currentTimeMillis();
		}
	}
}
