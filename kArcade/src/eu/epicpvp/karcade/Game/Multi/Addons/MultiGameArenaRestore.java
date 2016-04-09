package eu.epicpvp.karcade.Game.Multi.Addons;

import java.util.ArrayList;
import java.util.HashMap;

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
import org.bukkit.event.EventPriority;

import dev.wolveringer.dataserver.gamestats.GameState;
import eu.epicpvp.karcade.Game.Multi.Addons.Evemts.BuildType;
import eu.epicpvp.karcade.Game.Multi.Addons.Evemts.MultiGameAddonAreaRestoreEvent;
import eu.epicpvp.karcade.Game.Multi.Addons.Evemts.MultiGameAddonAreaRestoreExplosionEvent;
import eu.epicpvp.karcade.Game.Multi.Games.MultiGame;
import eu.epicpvp.kcore.Listener.kListener;
import eu.epicpvp.kcore.Util.UtilLocation;
import lombok.Getter;
import lombok.Setter;

public class MultiGameArenaRestore extends kListener{
	
		private MultiGame game;
		@Getter
		private HashMap<Location,BlockState> blocks;
		public int X = 0;
		public int Y = 1;
		public int Z = 2;
		public int Min = 0;
		public int Max = 1;
		public int MinMax[][];
		private World world;
		@Getter
		@Setter
		private boolean tntDestroy=true;
		@Getter
		@Setter
		private boolean blockBurn=false;
		@Getter
		@Setter
		private boolean onlyBuild=false; //TRUE Man kann nur noch das zerst§ren was man selber gesetzt hat!
		@Getter
		@Setter
		private ArrayList<Material> bypass;
		@Getter
		@Setter
		private ArrayList<Material> blacklist;
		
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
			logMessage(UtilLocation.getLocString(ecke1));
			
			ecke2=new Location(ecke2.getWorld(),MinMax[X][Min],MinMax[Y][Min],MinMax[Z][Min]);
			logMessage(UtilLocation.getLocString(ecke2));
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
		public void MultiGameAddonAreaRestoreExplosion(MultiGameAddonAreaRestoreExplosionEvent ev){
			if(!ev.getBlocks().isEmpty()&&isInArea(ev.getBlocks().get(0).getLocation())){
				ev.setGame(game);
				ev.setCancelled(true);
				if(game.getState() == GameState.InGame){
					if(isTntDestroy()){
						if(delete==null)this.delete=new ArrayList<>();
						delete.clear();
						Block b;
						for(int i = 0; i<ev.getBlocks().size() ; i++){
							b=ev.getBlocks().get(i);
							if(isOnlyBuild()){
								if(blacklist.contains(b.getType())||!blocks.containsKey(b.getLocation())){
									delete.add(b);
								}
							}else{
								if(!blocks.containsKey(b.getLocation())){
									blocks.put(b.getLocation(),b.getState());
								}
							}
						}
						
						for(Block de : delete)ev.getBlocks().remove(de);
					}else{
						ev.getBlocks().clear();
					}
					return;
				}
				
				ev.setBuild(false);
			}
		}
		
		@EventHandler(priority=EventPriority.LOW,ignoreCancelled=false)
		public void MultiGameAddonAreaRestore(MultiGameAddonAreaRestoreEvent ev){
			if(isInArea(ev.getLocation())&&ev.isBuild()){
				ev.setGame(game);
				ev.setCancelled(true);
				if(game.getState() == GameState.InGame){
					if(ev.getBuildType() == BuildType.BURN && isBlockBurn()){
						ev.setBuild(false);
					}else{
						if(isOnlyBuild()){
							if(ev.getBuildType()!=BuildType.BREAK){
								if(!blocks.containsKey(ev.getLocation())){
									blocks.put(ev.getLocation(),ev.getReplacedState());
								}
							}else{
								if(bypass!=null&&bypass.contains(ev.getReplacedState().getType())){
									if(!blocks.containsKey(ev.getLocation())){
										blocks.put(ev.getLocation(),ev.getReplacedState());
									}
								}else{
									if(!blocks.containsKey(ev.getLocation())){
										ev.setBuild(false);
									}
								}
							}
						}else{
							if(!blocks.containsKey(ev.getLocation())){
								blocks.put(ev.getLocation(),ev.getReplacedState());
							}
						}
					}
					return;
				}
				
				ev.setBuild(false);
			}
		}
		
	}