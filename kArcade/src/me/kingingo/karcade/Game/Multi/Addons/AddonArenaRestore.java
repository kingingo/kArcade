package me.kingingo.karcade.Game.Multi.Addons;

import java.util.ArrayList;
import java.util.HashMap;

import me.kingingo.karcade.Game.Multi.Games.MultiGame;
import me.kingingo.kcore.Listener.kListener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketEvent;

public class AddonArenaRestore extends kListener{

	private MultiGame game;
	private HashMap<Location,BlockState> blocks;
	private int X = 0;
	private int Y = 1;
	private int Z = 2;
	private int Min = 0;
	private int Max = 1;
	public int MinMax[][];
	private World world;
	
	public AddonArenaRestore(MultiGame game,Location ecke1,Location ecke2){
		super(game.getGames().getManager().getInstance(),"AddonArenaRestore: "+game.getArena());
		this.blocks=new HashMap<>();
		this.game=game;
		this.world=ecke1.getWorld();
		this.MinMax=new int[3][2];
		
		if (ecke1.getBlockX() > ecke2.getBlockX()) {
			MinMax[X][Min] = ecke2.getBlockX();
			MinMax[X][Max] = ecke1.getBlockX();
		} else {
			MinMax[X][Max] = ecke2.getBlockX();
			MinMax[X][Min] = ecke1.getBlockX();
		}
		if (ecke1.getBlockY() > ecke2.getBlockY()) {
			MinMax[Y][Min] = ecke2.getBlockY();
			MinMax[Y][Max] = ecke1.getBlockY();
		} else {
			MinMax[Y][Max] = ecke2.getBlockY();
			MinMax[Y][Min] = ecke1.getBlockY();
		}
		if (ecke1.getBlockZ() > ecke2.getBlockZ()) {
			MinMax[Z][Min] = ecke2.getBlockZ();
			MinMax[Z][Max] = ecke1.getBlockZ();
		} else {
			MinMax[Z][Max] = ecke2.getBlockZ();
			MinMax[Z][Min] = ecke1.getBlockZ();
		}
		
		ecke1=new Location(ecke1.getWorld(),MinMax[X][Max],MinMax[Y][Max],MinMax[Z][Max]);
		System.out.println("TP: "+ecke1.toString());
		ecke1.getBlock().setType(Material.ENCHANTMENT_TABLE);

		ecke2=new Location(ecke2.getWorld(),MinMax[X][Min],MinMax[Y][Min],MinMax[Z][Min]);
		System.out.println("TP: "+ecke2.toString());
		ecke2.getBlock().setType(Material.EMERALD_BLOCK);
	}
	
	public boolean isInArea(Player player){
		return isInArea(player.getLocation());
	}
	
	public boolean isInArea(Location loc){
		if(loc.getY()<=MinMax[Y][Max]){
			if(loc.getY()>=MinMax[Y][Min]){
				if(loc.getZ()<=MinMax[Z][Max]){
					if(loc.getZ()>=MinMax[Z][Min]){
						if(loc.getX()<=MinMax[X][Max]){
							if(loc.getX()>=MinMax[X][Min]){
								return true;
							}
						}
					}
				}
			}
		}
		
		return false;
	}
	
	public void restore(){
		for(BlockState b : blocks.values()){
			b.update(true);
		}
		
		for(Entity e : world.getEntities()){
			if(!(e instanceof Player)&&!(e instanceof ItemFrame)&&!(e instanceof ArmorStand)){
				if(isInArea(e.getLocation())){
					e.remove();
				}
			}
		}
		blocks.clear();
	}
	
	@EventHandler
	public void bucket(PlayerBucketEmptyEvent ev){
		if(game.getTeamList().containsKey(ev.getPlayer())){
			if(isInArea(ev.getBlockClicked().getLocation())){
				if(!blocks.containsKey(ev.getBlockClicked().getLocation())){
					System.out.println("PlayerBucket "+ev.getBlockClicked().getRelative(ev.getBlockFace()).getType().name());
					blocks.put(ev.getBlockClicked().getLocation(),ev.getBlockClicked().getRelative(ev.getBlockFace()).getState());
				}
			}else{
				ev.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void Explose(EntityExplodeEvent ev){
		for(int i = 0; i<ev.blockList().size(); i++){
			if(isInArea(ev.blockList().get(i).getLocation())){
				if(!blocks.containsKey(ev.blockList().get(i).getLocation())){
					System.out.println("BlockEx "+ev.blockList().get(i).getType().name());
					blocks.put(ev.blockList().get(i).getLocation(),ev.blockList().get(i).getState());
				}
			}else{
				ev.blockList().remove(i);
			}
		}
	}

	@EventHandler
	public void BlockIgnite(BlockIgniteEvent ev){
		if(isInArea(ev.getBlock().getLocation())){
			if(!blocks.containsKey(ev.getBlock().getLocation())){
				System.out.println("BlockIgnite "+ev.getBlock().getType().name());
				blocks.put(ev.getBlock().getLocation(),ev.getBlock().getState());
			}
		}else{
			ev.setCancelled(true);
		}
	}
	
	
	@EventHandler
	public void BlockForm(BlockFormEvent ev){
		if(isInArea(ev.getBlock().getLocation())){
			if(!blocks.containsKey(ev.getBlock().getLocation())){
				System.out.println("BlockFrom "+ev.getBlock().getType().name());
				blocks.put(ev.getBlock().getLocation(),ev.getBlock().getState());
			}
		}else{
			ev.setCancelled(true);
		}
	}
	
	@EventHandler
	public void BlockFromTo(BlockFromToEvent ev){
		if(isInArea(ev.getToBlock().getLocation())){
			if(!blocks.containsKey(ev.getToBlock().getLocation())){
				System.out.println("getToBlock "+ev.getToBlock().getType().name());
				blocks.put(ev.getToBlock().getLocation(),ev.getToBlock().getState());
			}
		}else{
			ev.setCancelled(true);
		}
	}
	
	@EventHandler
	public void breakb(BlockBreakEvent ev){
		if(game.getTeamList().containsKey(ev.getPlayer())){
			if(isInArea(ev.getBlock().getLocation())){
				if(!blocks.containsKey(ev.getBlock().getLocation())){
					System.out.println("BlockBreak "+ev.getBlock().getType().name());
					blocks.put(ev.getBlock().getLocation(),ev.getBlock().getState());
				}
			}else{
				ev.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void place(BlockPlaceEvent ev){
		if(game.getTeamList().containsKey(ev.getPlayer())){
			if(isInArea(ev.getBlock().getLocation())){
				if(!blocks.containsKey(ev.getBlock().getLocation())){
					System.out.println("BlockPlace "+ev.getBlockReplacedState().getType().name());
					blocks.put(ev.getBlock().getLocation(),ev.getBlockReplacedState());
				}
			}else{
				ev.setCancelled(true);
			}
		}
	}
	
}
