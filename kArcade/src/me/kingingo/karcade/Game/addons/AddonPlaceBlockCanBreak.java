package me.kingingo.karcade.Game.addons;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class AddonPlaceBlockCanBreak implements Listener{

	@Getter
	JavaPlugin instance;
	@Getter
	ArrayList<Location> blocks = new ArrayList<>();
	@Getter
	ArrayList<Material> ausnahmen;
	
	public AddonPlaceBlockCanBreak(JavaPlugin instance,Material[] ausnahmen){
		this.instance=instance;
		this.ausnahmen=new ArrayList<>();
		for(Material m : ausnahmen){
			this.ausnahmen.add(m);
		}
		Bukkit.getPluginManager().registerEvents(this, getInstance());
	}
	
	public AddonPlaceBlockCanBreak(JavaPlugin instance){
		this.instance=instance;
		Bukkit.getPluginManager().registerEvents(this, getInstance());
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void Explose(EntityExplodeEvent ev){
		if(getBlocks().isEmpty()){
			ev.blockList().clear();
			return;
		}

		for(Block b : ev.blockList()){
			if(getBlocks().contains(b.getLocation())){
				b.setType(Material.AIR);
				getBlocks().remove(b.getLocation());
			}
		}
		ev.blockList().clear();
	}
	
	@EventHandler
	public void Break(BlockBreakEvent ev){
		if(getBlocks().contains(ev.getBlock().getLocation())){
			getBlocks().remove(ev.getBlock().getLocation());
		}else{
			if(getAusnahmen().contains(ev.getBlock().getType())){
				ev.getBlock().setType(Material.AIR);
				return;
			}
			ev.setCancelled(true);
		}
	}
	
	@EventHandler
	public void Place(BlockPlaceEvent ev){
		if(!getBlocks().contains(ev.getBlock().getLocation())){
			getBlocks().add(ev.getBlock().getLocation());
		}
	}
	
}
