package me.kingingo.karcade.Game.Multi.Addons;

import java.util.HashMap;

import me.kingingo.karcade.Game.Multi.Events.MultiGameAddonAreaRestoreEvent;
import me.kingingo.karcade.Game.Multi.Events.MultiGameAddonAreaRestoreExplosionEvent;
import me.kingingo.karcade.Game.Multi.Games.MultiGame;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Listener.kListener;
import me.kingingo.kcore.Util.UtilLocation;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class MultiGameArenaRestore extends kListener{
	
		private MultiGame game;
		private HashMap<Location,BlockState> blocks;
		private int X = 0;
		private int Y = 1;
		private int Z = 2;
		private int Min = 0;
		private int Max = 1;
		public int MinMax[][];
		private World world;
		
		public MultiGameArenaRestore(MultiGame game,Location ecke1,Location ecke2){
			super(game.getGames().getManager().getInstance(),"GameArenaRestore: "+game.getArena());
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
			Log(UtilLocation.getLocString(ecke1));
			
			ecke2=new Location(ecke2.getWorld(),MinMax[X][Min],MinMax[Y][Min],MinMax[Z][Min]);
			Log(UtilLocation.getLocString(ecke2));
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
		
		@EventHandler(priority=EventPriority.LOW,ignoreCancelled=false)
		public void MultiGameAddonAreaRestoreExplosion(MultiGameAddonAreaRestoreExplosionEvent ev){
			if(!ev.getBlocks().isEmpty()&&isInArea(ev.getBlocks().get(0).getLocation())){
				ev.setGame(game);
				ev.setCancelled(true);
				if(game.getState() == GameState.InGame){
					for(Block b : ev.getBlocks()){
						if(!blocks.containsKey(b.getLocation())){
							blocks.put(b.getLocation(),b.getState());
						}
					}
					return;
				}
				
				ev.setBuild(false);
			}
		}
		
		@EventHandler(priority=EventPriority.LOW,ignoreCancelled=false)
		public void MultiGameAddonAreaRestore(MultiGameAddonAreaRestoreEvent ev){
			if(isInArea(ev.getLocation())){
				ev.setGame(game);
				ev.setCancelled(true);
				if(game.getState() == GameState.InGame){
					if(!blocks.containsKey(ev.getLocation())){
						blocks.put(ev.getLocation(),ev.getReplacedState());
					}
					return;
				}
				
				ev.setBuild(false);
			}
		}
		
	}