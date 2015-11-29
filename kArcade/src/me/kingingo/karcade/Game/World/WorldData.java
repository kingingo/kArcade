package me.kingingo.karcade.Game.World;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;
import me.kingingo.karcade.kArcade;
import me.kingingo.karcade.kArcadeManager;
import me.kingingo.kcore.ChunkGenerator.CleanroomChunkGenerator;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.Util.UtilFile;
import me.kingingo.kcore.Util.UtilMap;
import me.kingingo.kcore.Util.UtilWorld;
import me.kingingo.kcore.Util.UtilWorldEdit;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.data.DataException;

public abstract class WorldData {

	@Getter
	@Setter
	private World world;
	@Getter
	private String gameName;
	@Getter
	private kArcadeManager manager;
	@Getter
	private String folder;
	@Getter
	@Setter
	private EditSession editSession;
	@Getter
	@Setter
	private boolean cleanroomChunkGenerator;
	@Getter
	@Setter
	private HashMap<String,Location> biomes;
	
	public WorldData(kArcadeManager manager,String gameName,String kürzel){
		this.manager=manager;
		this.gameName=gameName;
		this.folder=kürzel;
		this.cleanroomChunkGenerator=false;
	}
	
	public WorldData(kArcadeManager manager,GameType type){
		this(manager,type.name(),type.getKürzel());
	}
	
	public void logErr(String msg){
		System.err.println("[WorldData]: "+msg);
	}
	
	public void logErr(String... msgs){
		for(String msg : msgs)System.err.println("[WorldData]: "+msg);
	}
	
	public void log(String msg){
		System.out.println("[WorldData]: "+msg);
	}
	
	public void log(String... msgs){
		for(String msg : msgs)System.out.println("[WorldData]: "+msg);
	}

	public void clearWorld(){
		for(Entity e : getWorld().getEntities())if(!(e instanceof Player))e.remove();
	}
	 
	 public void Uninitialize(){
		if(getWorld()!=null){
			UtilMap.UnloadWorld(getManager().getInstance(), getWorld());
			UtilFile.DeleteFolder(new File(getWorld().getName()));
			setWorld(null);
		}
	 }
	 
	 public File UnzipSchematic(File file){
		 new File("schematics").mkdir();
		 File folder = new File("schematics" + File.separator + file.getName().replaceAll(".zip", ""));
		 folder.mkdir();
		 
		 try {
			 UtilFile.unzip(file, folder);
		 } catch (IOException e) {
			 e.printStackTrace();
		 }
		 
		 return folder;
	}
	 
	 public File[] loadSchematicFiles(){
		 ArrayList<File> maps = loadSchematics();
		 File[] files = new File[maps.size()];
		 int i = 0;
		 
		 for(File f : maps){
			 files[i]=f;
			 i++;
		 }
		 
		 maps.clear();
		 maps=null;
		 return files;
	 }
	 
	 public ArrayList<File> loadZips(){
		 return loadFile(".zip");
	 }
	 
	 public String getMapFolder(){
		 return kArcade.FilePath+File.separator+getGameName();
	 }
	 
	 public ArrayList<File> loadFile(String fileType){
		 File folder = new File(getMapFolder());
		 ArrayList<File> maps = new ArrayList<>();
		 if (!folder.exists()) folder.mkdirs();
		 log("Look in the Folder: " + folder);
		 if(fileType!=null)log("Look only for these file types " + fileType.toUpperCase());
		 
		 for (File file : folder.listFiles()){
			 if (file.isFile()){
				 if(fileType==null||file.getName().endsWith(fileType))maps.add(file);
		     }
		 }
		 
		 for (File map : maps)
			 log("Files: "+map.getPath());
		 
		 return maps;
	 }
	 
	 public ArrayList<File> loadSchematics(){
		 return loadFile(".schematic");
	 }
	 
	 public void loadBiomes(ArrayList<Biome> nobiome){
		 if(getBiomes()==null)setBiomes(new HashMap<String, Location>());
		 Location start = getWorld().getSpawnLocation().clone();
		 
		 for(int i=0;i<150.000;i++){
			 start.add(0,0,i);
			 getWorld().loadChunk(start.getChunk());
		 }
		 
		 start=getWorld().getSpawnLocation().clone();
		 
		 for(int i=0;i<150.000;i++){
			 start.add(i,0,0);
			getWorld().loadChunk(start.getChunk());
		 }
		
		 int bx;
		 int bz;
		 Block block;
		 Biome biome;
		 for(Chunk c : getWorld().getLoadedChunks()){
			bx = c.getX()<<4;
			bz = c.getZ()<<4;
			for(int xx = bx; xx < bx+16; xx++) {
			    for(int zz = bz; zz < bz+16; zz++) {
			        block = getWorld().getBlockAt(xx, 90, zz);
			        biome = getWorld().getBiome(block.getLocation().getBlockX(), block.getLocation().getBlockZ());
					if(!getBiomes().containsKey(biome.name())&&!nobiome.contains(biome)){
						getBiomes().put(biome.name(),block.getLocation());
					}
			    }
			}
		}
	 }
	 
	 public void createCustomWorld(String s){
		 Uninitialize();
		 WorldCreator wc = new WorldCreator(getFolder());
		 wc.generator(new CleanroomChunkGenerator(s));
		 setWorld(UtilWorld.LoadWorld(wc));
	 }
	 
	 public void createFlatWorld(){
		 createCustomWorld("64,grass");
	 }
	 
	 public void createCleanWorld(){
		 createCustomWorld(".0,AIR");
	 }
	 
	 public void createWorld(){
		 Uninitialize();
		 setWorld(Bukkit.createWorld(new WorldCreator(getFolder())));
	 }
	 
	 public void setBiome(Location l,Biome biome){
		 setBiome(l, 60, biome);
	 }
	 
	public void setBiome(Location l,int add,Biome biome){	
		int min_x = l.getBlockX()-add;
		int max_x = l.getBlockX()+add;
		
		int min_z = l.getBlockZ()-add;
		int max_z = l.getBlockZ()+add;
		
		for(int x = min_x; x < max_x; x++){
			for(int z = min_z; z < max_z; z++){
				if(l.getWorld().getBiome(x, z)!=biome){
					getWorld().loadChunk(x,z);
					getWorld().setBiome(x, z, biome);
					getWorld().refreshChunk(x, z);
//					getWorld().unloadChunk(x,z);
				}
			}
		}
	}
	
	public void pasteSchematic(Location l,File file){
		if(getEditSession()==null)setEditSession(new EditSession(new BukkitWorld(getWorld()), 999999999));
		l.getChunk().load();
		try {
		    CuboidClipboard cc = CuboidClipboard.loadSchematic(file);
		    cc.paste(getEditSession(), new Vector(l.getX(), l.getY(), l.getZ()), true);
		    cc=null;
		} catch (MaxChangedBlocksException e) {
			e.printStackTrace();
		} catch (DataException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (com.sk89q.worldedit.world.DataException e) {
			e.printStackTrace();
		}
	}
	
}
