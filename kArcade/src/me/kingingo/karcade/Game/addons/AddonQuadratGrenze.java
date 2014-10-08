package me.kingingo.karcade.Game.addons;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;
import me.kingingo.karcade.kArcadeManager;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.UtilMath;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

public class AddonQuadratGrenze implements Listener{

	private ArrayList<Location> list;
	private Location center;
	private kArcadeManager manager;
	@Getter
	@Setter
	private int radius=0;
	
	public AddonQuadratGrenze(kArcadeManager manager,Location loc,int radius){
		long time = System.currentTimeMillis();
		Bukkit.getPluginManager().registerEvents(this, manager.getInstance());
		this.manager=manager;
		this.radius=radius;
		this.center=loc;
		this.list=new ArrayList<>();
		scan();
		manager.DebugLog(time, this.getClass().getName());
	}
	
	public Vector calculateVector(Location from, Location to) {
		Location a = from, b = to;
		
		//calculate the distance between the locations (a => from || b => to)
		double dX = a.getX() - b.getX();
		double dY = a.getY() - b.getY();
		double dZ = a.getZ() - b.getZ();
		// -------------------------
		
		//calculate the yaw
		double yaw = Math.atan2(dZ, dX);
		// -------------------------
		
		//calculate the pitch
		double pitch = Math.atan2(Math.sqrt(dZ * dZ + dX * dX), dY) + Math.PI;
		// -------------------------
		
		//calculate and create the new vector
		double x = Math.sin(pitch) * Math.cos(yaw);
		double y = Math.sin(pitch) * Math.sin(yaw);
		double z = Math.cos(pitch);
		
		Vector vector = new Vector(x, z, y);
		// -------------------------
		
		return vector;
	}
	
	@EventHandler
	public void Update(UpdateEvent ev){
		if(manager.getState()!=GameState.InGame||ev.getType()!=UpdateType.FASTER)return;
		for (Location loc : list) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (p.getWorld() == loc.getWorld()) {
					if (p.getLocation().distance(loc) <= 10) {
						if(p.getLocation().distance(loc) <= 2)p.setVelocity(calculateVector(p.getLocation(), center).multiply(8).setY(0.4));
						if (UtilMath.r(1) == 0) {
							loc.getWorld().playEffect(loc,Effect.SPELL, -30);
						}
					}
				}
			}
		}
	}
	
	public void scan() {
		ArrayList<Location> list1 = new ArrayList<Location>();
		int MaxX = MaxX();
		int MaxZ = MaxZ();
		int MinX = MinX();
		int MinZ = MinZ();

		for (int y = MinY(); y < MaxY(); y++) {
			for (int z = MinZ; z < MaxZ; z++) {
				Location l = new Location(center.getWorld(), MaxX, y, z);
				if (l.getBlock().getType() == Material.AIR)
					list1.add(l);
			}

			for (int x = MaxX; x > MinX; x--) {
				Location l = new Location(center.getWorld(), x, y, MaxZ);
				if (l.getBlock().getType() == Material.AIR)
					list1.add(l);
			}

			for (int z = MaxZ; z > MinZ; z--) {
				Location l = new Location(center.getWorld(), MinX, y, z);
				if (l.getBlock().getType() == Material.AIR)
					list1.add(l);
			}

			for (int x = MinX; x < MaxX; x++) {
				Location l = new Location(center.getWorld(), x, y, MinZ);
				if (l.getBlock().getType() == Material.AIR)
					list1.add(l);
			}
		}
		list.clear();
		list=(ArrayList<Location>)list1.clone();
		list1.clear();
		list1=null;
	}
	
	public int MaxY(){
		return 256;
	}
	
	public int MinY(){
		return center.getBlockY();
	}
	
	public int MinZ() {
		return center.getBlockZ() - (radius);
	}

	public int MinX() {
		return center.getBlockX() - (radius);
	}

	public int MaxZ() {
		return center.getBlockZ() + (radius);
	}

	public int MaxX() {
		return center.getBlockX() + (radius);
	}
	
}
