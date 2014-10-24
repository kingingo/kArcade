package me.kingingo.karcade.Game.addons;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;
import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Enum.PlayerState;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.UtilLocation;
import me.kingingo.kcore.Util.UtilMath;
import me.kingingo.kcore.Util.UtilPlayer;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
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
		manager.DebugLog(time, this.getClass().getName());
	}
	
	public Vector calculateVector(Location from, Location to) {
		Location a = from, b = to;
		
		double dX = a.getX() - b.getX();
		double dY = a.getY() - b.getY();
		double dZ = a.getZ() - b.getZ();
		double yaw = Math.atan2(dZ, dX);

		double pitch = Math.atan2(Math.sqrt(dZ * dZ + dX * dX), dY) + Math.PI;

		double x = Math.sin(pitch) * Math.cos(yaw);
		double y = Math.sin(pitch) * Math.sin(yaw);
		double z = Math.cos(pitch);
		
		Vector vector = new Vector(x, z, y);
		return vector;
	}
	
	@EventHandler
	public void Update(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC){
			if(manager.getState()==GameState.InGame||manager.getState()==GameState.SchutzModus){
				for (Location loc : list) {
					for (Player p : Bukkit.getOnlinePlayers()) {
						if (p.getWorld() == loc.getWorld()) {
							if (p.getLocation().distance(loc) <= 6) {
								if(p.getLocation().distance(loc) <= 1){
									p.setVelocity(calculateVector(p.getLocation(), center).multiply(1.2).setY(0.2));
								}
								if (UtilMath.RandomInt(30, 1)==1) {
									loc.getWorld().playEffect(loc, Effect.MOBSPAWNER_FLAMES, -5);
								}
							}
						}
					}
				}
				
			}
		}else if(ev.getType()!=UpdateType.SEC_2){
			if(manager.getState()==GameState.InGame||manager.getState()==GameState.SchutzModus){
				for(Player p : manager.getGame().getGameList().getPlayers(PlayerState.IN)){
					if(!isInGrenze(p.getLocation())){
						p.damage(-1);
						UtilPlayer.health(p, -1);
						p.sendMessage(Text.PREFIX_GAME.getText(manager.getTyp().name())+"�c"+Text.AU�ERHALB_DER_MAP.getText());
					}
				}	
			}
		}
		
	}
	
	public boolean isInGrenze(Location loc){
		if(loc.getX() < MaxX && loc.getX() > MinX && loc.getZ() > MinZ && loc.getZ() < MaxZ)return true;
		return false;
	}
	
	@EventHandler
	public void Teleport(PlayerTeleportEvent ev){
		if(ev.getCause()==TeleportCause.ENDER_PEARL){
			if(!ev.getTo().getWorld().getName().equalsIgnoreCase(center.getWorld().getName()))return;
			if(!isInGrenze(ev.getTo()))ev.setCancelled(true);
		}
	}
	
	public void setList(int radius,ArrayList<Location> list1){
		setRadius(radius);
		MaxX = MaxX();
		MaxZ = MaxZ();
		MinX = MinX();
		MinZ = MinZ();
		if(list!=null)list.clear();
		list=(ArrayList<Location>)list1.clone();
		list1.clear();
		list1=null;
	}
	
	public ArrayList<Location> scanWithLowestBlock(int radius,int wallUp,int wallDown) {
		setRadius(radius);
		ArrayList<Location> list1 = new ArrayList<Location>();
		MaxX = MaxX();
		MaxZ = MaxZ();
		MinX = MinX();
		MinZ = MinZ();

		Location l=null;
		
		int y=0;
		
		for (int x = MaxX; x > MinX; x--) {
			l = new Location(center.getWorld(), x, MaxY(), MaxZ);
			l = UtilLocation.getLowestBlock(l);
			y=l.getBlockY();
			for(int up = y; up < y+wallUp; up++){
				l.setY(up);
				list1.add(l.clone());
			}
			
			for(int down = y; down > y-wallDown; down--){
				l.setY(down);
				list1.add(l.clone());
			}
		}
		
		for (int x = MinX; x < MaxX; x++) {
			l = new Location(center.getWorld(), x, MaxY(), MinZ);
			l = UtilLocation.getLowestBlock(l);
			y=l.getBlockY();
			
			for(int up = y; up < y+wallUp; up++){
				l.setY(up);
				list1.add(l.clone());
			}
			
			for(int down = y; down > y-wallDown; down--){
				l.setY(down);
				list1.add(l.clone());
			}
		}
		
		for (int z = MaxZ; z > MinZ; z--) {
			l = new Location(center.getWorld(), MinX, MaxY(), z);
			l = UtilLocation.getLowestBlock(l);
			y=l.getBlockY();
			
			for(int up = y; up < y+wallUp; up++){
				l.setY(up);
				list1.add(l.clone());
			}
			
			for(int down = y; down > y-wallDown; down--){
				l.setY(down);
				list1.add(l.clone());
			}
		}
		
		for (int z = MinZ; z < MaxZ; z++) {
			l = new Location(center.getWorld(), MaxX, MaxY(), z);
			l = UtilLocation.getLowestBlock(l);
			y=l.getBlockY();
			
			for(int up = y; up < y+wallUp; up++){
				l.setY(up);
				list1.add(l.clone());
			}
			
			for(int down = y; down > y-wallDown; down--){
				l.setY(down);
				list1.add(l.clone());
			}
		}
		return list1;
	}
	
	public ArrayList<Location> scan(int radius) {
		setRadius(radius);
		ArrayList<Location> list1 = new ArrayList<Location>();
		MaxX = MaxX();
		MaxZ = MaxZ();
		MinX = MinX();
		MinZ = MinZ();

		Location l=null;
		for (int y = MinY(); y < MaxY(); y++) {
			for (int z = MinZ; z < MaxZ; z++) {
				l = new Location(center.getWorld(), MaxX, y, z);
				if (l.getBlock().getType() == Material.AIR||l.getBlock().getType().toString().contains("WATER"))
					list1.add(l);
			}

			for (int x = MaxX; x > MinX; x--) {
				l = new Location(center.getWorld(), x, y, MaxZ);
				if (l.getBlock().getType() == Material.AIR)
					list1.add(l);
			}

			for (int z = MaxZ; z > MinZ; z--) {
				 l = new Location(center.getWorld(), MinX, y, z);
				if (l.getBlock().getType() == Material.AIR||l.getBlock().getType().toString().contains("WATER"))
					list1.add(l);
			}

			for (int x = MinX; x < MaxX; x++) {
				l = new Location(center.getWorld(), x, y, MinZ);
				if (l.getBlock().getType() == Material.AIR||l.getBlock().getType().toString().contains("WATER"))
					list1.add(l);
			}
		}
		return list1;
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
		if(list!=null)list.clear();
		list=(ArrayList<Location>)list1.clone();
		list1.clear();
		list1=null;
	}
	
	public int MaxY(){
		return 255;
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
