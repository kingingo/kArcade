package me.kingingo.karcade.Game.Single.Games.Masterbuilders;

import me.kingingo.kcore.Listener.kListener;
import me.konsolas.aac.api.HackType;
import me.konsolas.aac.api.PlayerViolationEvent;
import me.konsolas.aac.api.PlayerViolationKickEvent;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.java.JavaPlugin;

public class AACListener extends kListener{

	public AACListener(JavaPlugin instance){
		super(instance,"AACListener");
		Log("NOW ENABLE");
	}
	
	@EventHandler(priority=EventPriority.LOW)
	public void onPlayerViolationKick(PlayerViolationKickEvent ev){
		if(ev.getHackType()==HackType.GLIDE
				|| ev.getHackType()==HackType.FLY
				|| ev.getHackType()==HackType.BLINK
				|| ev.getHackType()==HackType.NOFALL
				|| ev.getHackType()==HackType.SPEED){
			ev.setCancelled(true);
		}
	}
	

	@EventHandler(priority=EventPriority.LOW)
	public void onPlayerViolation(PlayerViolationEvent ev){
		ev.setCancelled(true);
	}
}
