package eu.epicpvp.karcade.Game.Single.Addons;

import java.util.ArrayList;

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

import dev.wolveringer.dataclient.protocoll.packets.PacketOutServerStatus.GameState;
import lombok.Getter;
import lombok.Setter;
import eu.epicpvp.karcade.Game.Single.SingleGame;
import eu.epicpvp.kcore.Enum.PlayerState;
import eu.epicpvp.kcore.Language.Language;
import eu.epicpvp.kcore.Update.UpdateType;
import eu.epicpvp.kcore.Update.Event.UpdateEvent;
import eu.epicpvp.kcore.Util.UtilLocation;
import eu.epicpvp.kcore.Util.UtilMath;
import eu.epicpvp.kcore.Util.UtilPlayer;

public class AddonQuadratGrenze implements Listener{

	private ArrayList<Location> list;
	@Getter
	private Location center;
	private SingleGame game;
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
	
	public AddonQuadratGrenze(SingleGame game,Location loc,int radius){
		long time = System.currentTimeMillis();
		Bukkit.getPluginManager().registerEvents(this, game.getManager().getInstance());
		this.game=game;
		this.radius=radius;
		this.center=loc;
		game.getManager().DebugLog(time, this.getClass().getName());
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
			if(game.getState()==GameState.InGame||game.getState()==GameState.SchutzModus){
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
			if(game.getState()==GameState.InGame||game.getState()==GameState.SchutzModus){
				for(Player p : game.getGameList().getPlayers(PlayerState.IN)){
					if(!isInGrenze(p.getLocation())){
						p.damage(-1);
						UtilPlayer.health(p, -1);
						p.sendMessage(Language.getText(p, "PREFIX_GAME",game.getType().name())+"§c"+Language.getText(p, "AU§ERHALB_DER_MAP"));
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
