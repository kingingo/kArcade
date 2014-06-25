package me.kingingo.karcade.Game.addons;

import java.util.ArrayList;
import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;
import me.kingingo.karcade.kArcadeManager;
import me.kingingo.kcore.Util.UtilMath;
import me.kingingo.kcore.Util.UtilServer;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.patterns.Pattern;

public class SphereGrenze {
	
	@Getter
	private ArrayList<Location> grenzen = new ArrayList<>();
	private kArcadeManager manager;
	private World world;
	@Getter
	@Setter
	private boolean Grenze=false;
	private Thread shuffle;
	Location mitte;
	int radius=30;
	
	public SphereGrenze(kArcadeManager manager,World world){
		this.manager=manager;
		this.world=world;
	}
	
	public boolean isAlive(){
		return shuffle.isAlive();
	}
	
	public void stop(){
		if(shuffle!=null)shuffle.stop();
	}
	
	public void start(){
		shuffle = new Thread() {
			public void run() {
				while (true) {
					try {
						for (Location loc : grenzen) {
							for (Player p : UtilServer.getPlayers()) {
								if (p.getWorld() == loc.getWorld()) {
									if (p.getLocation().distance(loc) <= 10) {
										if (UtilMath.r(1) == 0) {
											loc.getWorld().playEffect(loc,Effect.SPELL, -30);
										}
										
										if(p.getLocation().distance(loc)<=2){
											p.getPlayer().setVelocity(p.getPlayer().getLocation().getDirection().multiply(-3).setY(0.0D));
										}
									}
								}
							}
						}
					} catch (Exception error) {

					}
					try {
						sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		shuffle.start();
	}
	
	  public int makeSphere(final Vector pos, double radiusX,  double radiusY,  double radiusZ, final boolean filled) throws MaxChangedBlocksException {
	        int affected2 = 0;
	        radiusX += 0.5;
  	        radiusY += 0.5;
  	        radiusZ += 0.5;
  	        final HashMap<Integer, Double> hmap = new HashMap<>();
  	        hmap.put(1, radiusX);
  	        hmap.put(2, radiusY);
  	        hmap.put(3, radiusZ);

	      Thread t = new Thread(){
	    	  public void run(){
	  	       try{
	  	    	 int affected = 0;
		  	        double radiusX2 = hmap.get(1);
		  	        double radiusY2 = hmap.get(2);
		  	        double radiusZ2 = hmap.get(3);

		  	        final double invRadiusX = 1 / radiusX2;
		  	        final double invRadiusY = 1 / radiusY2;
		  	        final double invRadiusZ = 1 / radiusZ2;

		  	        final int ceilRadiusX = (int) Math.ceil(radiusX2);
		  	        final int ceilRadiusY = (int) Math.ceil(radiusY2);
		  	        final int ceilRadiusZ = (int) Math.ceil(radiusZ2);

		  	        double nextXn = 0;
		  	        forX: for (int x = 0; x <= ceilRadiusX; ++x) {
		  	            final double xn = nextXn;
		  	            nextXn = (x + 1) * invRadiusX;
		  	            double nextYn = 0;
		  	            forY: for (int y = 0; y <= ceilRadiusY; ++y) {
		  	                final double yn = nextYn;
		  	                nextYn = (y + 1) * invRadiusY;
		  	                double nextZn = 0;
		  	                forZ: for (int z = 0; z <= ceilRadiusZ; ++z) {
		  	                    final double zn = nextZn;
		  	                    nextZn = (z + 1) * invRadiusZ;

		  	                    double distanceSq = lengthSq(xn, yn, zn);
		  	                    if (distanceSq > 1) {
		  	                        if (z == 0) {
		  	                            if (y == 0) {
		  	                                break forX;
		  	                            }
		  	                            break forY;
		  	                        }
		  	                        break forZ;
		  	                    }

		  	                    if (!filled) {
		  	                        if (lengthSq(nextXn, yn, zn) <= 1 && lengthSq(xn, nextYn, zn) <= 1 && lengthSq(xn, yn, nextZn) <= 1) {
		  	                            continue;
		  	                        }
		  	                    }

		  	                    if (setBlock(pos.add(x, y, z))) {
		  	                        ++affected;
		  	                    }
		  	                    if (setBlock(pos.add(-x, y, z))) {
		  	                        ++affected;
		  	                    }
		  	                    if (setBlock(pos.add(x, -y, z))) {
		  	                        ++affected;
		  	                    }
		  	                    if (setBlock(pos.add(x, y, -z))) {
		  	                        ++affected;
		  	                    }
		  	                    if (setBlock(pos.add(-x, -y, z))) {
		  	                        ++affected;
		  	                    }
		  	                    if (setBlock(pos.add(x, -y, -z))) {
		  	                        ++affected;
		  	                    }
		  	                    if (setBlock(pos.add(-x, y, -z))) {
		  	                        ++affected;
		  	                    }
		  	                    if (setBlock(pos.add(-x, -y, -z))) {
		  	                        ++affected;
		  	                    }
		  	                }
		  	            }
		  	        }
		    	     
	  	       } catch (Exception error){
	  	    	   
	  	       }
	    	  }
	      };
	      t.start();

	        return affected2;
	    }
	  
	  
	  private final double lengthSq(double x, double y, double z) {
	        return (x * x) + (y * y) + (z * z);
	    }

	    private final double lengthSq(double x, double z) {
	        return (x * x) + (z * z);
	    }
	    public boolean setBlock(Vector pt, BaseBlock block)
	            throws MaxChangedBlocksException {
	    
	    	grenzen.add(new Location(world, pt.getX(), pt.getY(), pt.getZ()));
	         return true;
	    }

	   
	    public boolean setBlock(Vector pt, Pattern pat)
	            throws MaxChangedBlocksException {
	    	grenzen.add(new Location(world, pt.getX(), pt.getY(), pt.getZ()));
	         return true;
	    }

	    public boolean setBlock(Vector pt)
	            throws MaxChangedBlocksException {
	    	grenzen.add(new Location(world, pt.getX(), pt.getY(), pt.getZ()));
	         return true;
	    }
	   
	    public void loadGrenzen(Location mitte,int radius){
	    	this.radius=radius;
	    	this.mitte=mitte;
	    	Double radiusX,radiusY,radiusZ;
            radiusX = radiusY = radiusZ = Math.max(1, (double)radius);
	    	try {
				makeSphere(new Vector(mitte.getBlockX(),mitte.getBlockY(),mitte.getBlockZ()), radiusX, radiusY, radiusZ, false);
			} catch (MaxChangedBlocksException e) {
				e.printStackTrace();
			}
	    }
	  
}
