package me.kingingo.karcade.Game.addons;

import lombok.Getter;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class AddonNight implements Listener {

	@Getter
	JavaPlugin instance;
	@Getter
	World world;
	@Getter
	boolean drehen=false;
	
	public AddonNight(JavaPlugin plugin,World w){
		this.world=w;
		this.instance=plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	long time;
	@EventHandler
	public void Night(UpdateEvent ev){
		if(ev.getType()!=UpdateType.FAST)return;
		if(getWorld().getTime()<0&&getWorld().getTime()>12000){
			time=getWorld().getTime();
			time+=80;
			getWorld().setTime(time);
		}
	}
	
}
