package me.kingingo.karcade.Game.Single.Games.CaveWars.Map;

import java.io.File;
import java.util.Random;

import me.kingingo.karcade.Game.World.WorldData;
import me.kingingo.kcore.Util.UtilWorldEdit;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

public class SchematicPopulator extends BlockPopulator {

	private int limit=-1;
	private int set=0;
	private int CHANCE=0;
	private File file;
	private SchematicPopulatorRunnable runnable;
	private WorldData worldData;
	
	public SchematicPopulator(File file,WorldData worldData,SchematicPopulatorRunnable runnable,int CHANCE,int limit){
		this.CHANCE=CHANCE;
		this.file=file;
		this.runnable=runnable;
		this.limit=limit;
		this.worldData=worldData;
	}
	
	@Override
	public void populate(World world, Random random, Chunk source) {
		if(limit!=-1&&limit==set)return;
		if(random.nextInt(200) <= CHANCE){
			int centerX = (source.getX() << 4) + random.nextInt(16);
            int centerZ = (source.getZ() << 4) + random.nextInt(16);
            int centerY = world.getHighestBlockYAt(centerX, centerZ);
            Vector center = new BlockVector(centerX, centerY, centerZ);
			if(!runnable.distance(worldData,center.toLocation(world)))return;
            UtilWorldEdit.pastePlate(center.toLocation(world), file);
            runnable.onSchematicPopulatorRunnable(center.toLocation(world), worldData);
			set++;
		}
	}

}
