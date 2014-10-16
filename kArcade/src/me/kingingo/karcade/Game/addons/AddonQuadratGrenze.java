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
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.util.Vector;

public class AddonQuadratGrenze implements Listener{

	private ArrayList<Location> list;
	@Getter
	private Location center;
	private kArcadeManager manager;
	@Getter
	@Setter
	private int radius=0;
	@Getter
	private int MaxX;
	@Getter
	private int MinX;
	@Getter
	private int MaxZ;
	@Getter
	private int MinZ;
	
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
		if(ev.getType()!=UpdateType.FASTER)return;
		if(manager.getState()==GameState.InGame||manager.getState()==GameState.SchutzModus){
			for (Location loc : list) {
				for (Player p : Bukkit.getOnlinePlayers()) {
					if (p.getWorld() == loc.getWorld()) {
						if (p.getLocation().distance(loc) <= 6) {
							if(p.getLocation().distance(loc) <= 3){
								p.setVelocity(calculateVector(p.getLocation(), center).multiply(3).setY(0.4));
							}
							
							if(!isInGrenze(p.getLocation())){
								p.setVelocity(calculateVector(p.getLocation(), center).multiply(3).setY(0.4));
							}
							
							if (UtilMath.r(3) == 0) {
								loc.getWorld().playEffect(loc, Effect.MOBSPAWNER_FLAMES, -30);
							}
						}
					}
				}
			}
			
		}
	}
	
	public boolean isInGrenze(Location loc){
		if(loc.getX() < MaxX() && loc.getX() > MinX() && loc.getZ() > MinZ() && loc.getZ() < MaxZ())return true;
		return false;
	}
	
	@EventHandler
	public void Teleport(PlayerTeleportEvent ev){
		if(ev.getCause()==TeleportCause.ENDER_PEARL){
			if(!ev.getTo().getWorld().getName().equalsIgnoreCase(center.getWorld().getName()))return;
			if(!isInGrenze(ev.getTo()))ev.setCancelled(true);
		}
	}
	
	public void scan() {
		ArrayList<Location> list1 = new ArrayList<Location>();
		MaxX = MaxX();
		MaxZ = MaxZ();
		MinX = MinX();
		MinZ = MinZ();

		for (int y = MinY(); y < MaxY(); y++) {
			for (int z = MinZ; z < MaxZ; z++) {
				Location l = new Location(center.getWorld(), MaxX, y, z);
				if (l.getBlock().getType() == Material.AIR||l.getBlock().getType().toString().contains("WATER"))
					list1.add(l);
			}

			for (int x = MaxX; x > MinX; x--) {
				Location l = new Location(center.getWorld(), x, y, MaxZ);
				if (l.getBlock().getType() == Material.AIR)
					list1.add(l);
			}

			for (int z = MaxZ; z > MinZ; z--) {
				Location l = new Location(center.getWorld(), MinX, y, z);
				if (l.getBlock().getType() == Material.AIR||l.getBlock().getType().toString().contains("WATER"))
					list1.add(l);
			}

			for (int x = MinX; x < MaxX; x++) {
				Location l = new Location(center.getWorld(), x, y, MinZ);
				if (l.getBlock().getType() == Material.AIR||l.getBlock().getType().toString().contains("WATER"))
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
