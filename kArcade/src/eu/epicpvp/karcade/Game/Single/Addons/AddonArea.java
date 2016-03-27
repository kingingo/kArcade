package eu.epicpvp.karcade.Game.Single.Addons;

import java.util.ArrayList;
import java.util.HashMap;

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
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import lombok.Setter;
import eu.epicpvp.karcade.Game.Multi.Addons.Evemts.BuildType;
import eu.epicpvp.karcade.Game.Single.Events.AddonAreaRestoreEvent;
import eu.epicpvp.karcade.Game.Single.Events.AddonAreaRestoreExplosionEvent;
import eu.epicpvp.kcore.Listener.kListener;
import eu.epicpvp.kcore.Util.UtilLocation;

public class AddonArea extends kListener{
	
		@Getter
		private HashMap<Location,BlockState> blocks;
		public int X = 0;
		public int Y = 1;
		public int Z = 2;
		public int Min = 0;
		public int Max = 1;
		public int MinMax[][];
		@Getter
		private World world;
		@Getter
		@Setter
		private boolean onlyBuild=false; //TRUE Man kann nur noch das zerst§ren was man selber gesetzt hat!
		@Getter
		private ArrayList<Player> players = new ArrayList<>();
		
		public AddonArea(JavaPlugin instance ,Location ecke1,Location ecke2){
			super(instance,"AddonArea");
			this.blocks=new HashMap<>();
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
				if((!(e instanceof Player))&&(!(e instanceof ItemFrame))&&(!(e instanceof ArmorStand))){
					if(isInArea(e.getLocation())){
						e.remove();
					}
				}
			}
			
			blocks.clear();
		}

		ArrayList<Block> delete;
		@EventHandler(priority=EventPriority.LOW,ignoreCancelled=false)
		public void addonAreaRestoreExplosionEvent(AddonAreaRestoreExplosionEvent ev){
			if(!ev.getBlocks().isEmpty()&&isInArea(ev.getBlocks().get(0).getLocation())){
				ev.setCancelled(true);
				if(delete==null)this.delete=new ArrayList<>();
				delete.clear();
				Block b;
				for(int i = 0; i<ev.getBlocks().size() ; i++){
					b=ev.getBlocks().get(i);
					if(isOnlyBuild()){
						if(!blocks.containsKey(b.getLocation())){
							delete.add(b);
						}
					}else{
						if(!blocks.containsKey(b.getLocation())){
							blocks.put(b.getLocation(),b.getState());
						}
					}
				}
					
				for(Block de : delete)ev.getBlocks().remove(de);
			}
		}
		
		@EventHandler(priority=EventPriority.LOW,ignoreCancelled=false)
		public void addonAreaRestoreEvent(AddonAreaRestoreEvent ev){
			if(isInArea(ev.getLocation())&&players.contains(ev.getPlayer())){
				ev.setCancelled(true);
				if(isOnlyBuild()){
					if(ev.getBuildType()==BuildType.PLACE){
						if(!blocks.containsKey(ev.getLocation())){
							blocks.put(ev.getLocation(),ev.getReplacedState());
						}
					}else{
						if(!blocks.containsKey(ev.getLocation())){
							ev.setBuild(false);
						}
					}
				}else{
					if(!blocks.containsKey(ev.getLocation())){
						blocks.put(ev.getLocation(),ev.getReplacedState());
					}
				}
			}else if(players.contains(ev.getPlayer())){
				ev.setBuild(false);
				ev.setCancelled(true);
			}
		}
		
	}