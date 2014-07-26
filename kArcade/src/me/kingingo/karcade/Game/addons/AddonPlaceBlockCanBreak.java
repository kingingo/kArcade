package me.kingingo.karcade.Game.addons;

import java.util.ArrayList;

import lombok.Getter;

import org.bukkit.Bukkit;
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
	ArrayList<Block> blocks = new ArrayList<>();
	
	public AddonPlaceBlockCanBreak(JavaPlugin instance){
		this.instance=instance;
		Bukkit.getPluginManager().registerEvents(this, getInstance());
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void Explose(EntityExplodeEvent ev){
		for(int i=0;i<ev.blockList().size();i++){
			if(!getBlocks().contains(ev.blockList().get(i))){
				ev.blockList().remove(i);
			}
		}
	}
	
	@EventHandler
	public void Break(BlockBreakEvent ev){
		if(getBlocks().contains(ev.getBlock())){
			getBlocks().remove(ev.getBlock());
		}else{
			ev.setCancelled(true);
		}
	}
	
	@EventHandler
	public void Place(BlockPlaceEvent ev){
		getBlocks().add(ev.getBlock());
	}
	
}
