package eu.epicpvp.karcade.Game.Single.Addons;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.util.Vector;

import eu.epicpvp.karcade.ArcadeManager;
import eu.epicpvp.kcore.Util.UtilMath;
import lombok.Getter;
import lombok.Setter;

public class AddonFallingBlockJump implements Listener{

	ArcadeManager manager;
	//ArrayList<Material> list;
	int spawn;
	int radius;
	int MaxX = 0;
	int MaxZ = 0;
	int MinX = 0;
	int MinZ = 0;
	int MinY = 0;
	int MaxY = 255;
	Location center;
	FallingBlock fb;
	@Getter
	@Setter
	boolean enable=true;
	@Getter
	@Setter
	boolean remove=true;
	
	public AddonFallingBlockJump(ArcadeManager manager,Location center,int radius,int spawn){
		this.manager=manager;
		this.center=center;
		this.radius=radius;
		this.spawn=spawn;
		Bukkit.getPluginManager().registerEvents(this, manager.getInstance());
	}
	
	public void spawn() {
		enable=true;
		center.getWorld().loadChunk(center.getWorld().getChunkAt(center));
		if (MaxX == 0 || MaxZ == 0 || MinX == 0 || MinZ == 0) {
			MaxX = MaxX(center.clone(), radius);
			MaxZ = MaxZ(center.clone(), radius);
			MinX = MinX(center.clone(), radius);
			MinZ = MinZ(center.clone(), radius);
		}
		for(int i = 0; i<spawn;i++){
			Random rdm = new Random();
			int z = rdm.nextInt(MaxZ - MinZ) + MinZ;
			int x = rdm.nextInt(MaxX - MinX) + MinX;
			Location loc = new Location(center.getWorld(), x, center.getBlockY()+17, z);
			loc.getWorld().spawnFallingBlock(loc, 159,(byte) UtilMath.randomInteger(15));
		}
	}

	public int MinZ(Location start, int r) {
		return start.getBlockZ() - (r);
	}

	public int MinX(Location start, int r) {
		return start.getBlockX() - (r);
	}

	public int MaxZ(Location start, int r) {
		return start.getBlockZ() + (r);
	}

	public int MaxX(Location start, int r) {
		return start.getBlockX() + (r);
	}
	
	@EventHandler
	public void EntityChange (EntityChangeBlockEvent ev) {
		if (ev.getEntityType() == EntityType.FALLING_BLOCK) {
				if(enable){
					fb=ev.getEntity().getLocation().getWorld().spawnFallingBlock(ev.getEntity().getLocation().add(0,0.5,0),159,(byte) UtilMath.randomInteger(15));
					//UtilFirework.launchFirework(ev.getEntity().getLocation().add(0,0.5,0), FireworkEffect.builder().with(Type.BURST).withColor(Color.RED).build(), new Vector(0,1.8,0),1);
					fb.setVelocity( new Vector(0,1.8,0) );
				}
				if(remove){
					ev.setCancelled(true);
				}
		}
	 }
	
}
