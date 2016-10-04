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

import eu.epicpvp.datenserver.definitions.dataserver.gamestats.GameState;
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
			this.MinMax=UtilLocation.getMinMax(ecke1, ecke2);

			ecke1=new Location(ecke1.getWorld(),MinMax[UtilLocation.X][UtilLocation.Max],MinMax[UtilLocation.Y][UtilLocation.Max],MinMax[UtilLocation.Z][UtilLocation.Max]);
			logMessage(UtilLocation.getLocString(ecke1));

			ecke2=new Location(ecke2.getWorld(),MinMax[UtilLocation.X][UtilLocation.Min],MinMax[UtilLocation.Y][UtilLocation.Min],MinMax[UtilLocation.Z][UtilLocation.Min]);
			logMessage(UtilLocation.getLocString(ecke2));
		}

		public boolean isInArea(Player player){
			return isInArea(player.getLocation());
		}

		public boolean isInArea(Location loc){
			return UtilLocation.isInMinMax(MinMax, loc);
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
