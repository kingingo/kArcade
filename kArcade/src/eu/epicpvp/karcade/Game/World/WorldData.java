package eu.epicpvp.karcade.Game.World;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
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

import eu.epicpvp.datenserver.definitions.dataserver.gamestats.GameType;
import eu.epicpvp.karcade.ArcadeManager;
import eu.epicpvp.karcade.kArcade;
import eu.epicpvp.kcore.ChunkGenerator.CleanroomChunkGenerator;
import eu.epicpvp.kcore.Util.UtilFile;
import eu.epicpvp.kcore.Util.UtilMap;
import eu.epicpvp.kcore.Util.UtilWorld;
import eu.epicpvp.kcore.Util.UtilWorldEdit;
import lombok.Getter;
import lombok.Setter;

public abstract class WorldData {

	@Getter
	@Setter
	private GameMap map;
	@Getter
	private String gameName;
	@Getter
	private ArcadeManager manager;
	@Getter
	private String shortName;
	@Getter
	@Setter
	private EditSession editSession;
	@Getter
	@Setter
	private boolean cleanroomChunkGenerator;
	@Getter
	@Setter
	private HashMap<String,Location> biomes;
	private ArrayList<File> loadFiles;

	public WorldData(ArcadeManager manager,GameType type){
		this(manager,type.name(),type.getShortName());
	}

	public WorldData(ArcadeManager manager,String gameName,String shortName){
		this.manager=manager;
		this.gameName=gameName;
		this.shortName=shortName;
		this.cleanroomChunkGenerator=false;
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
		clearWorld(getMap());
	}

	public void clearWorld(GameMap map){
		for(Entity e : map.getWorld().getEntities())if(!(e instanceof Player))e.remove();
	}

	public void Uninitialize(){
		Uninitialize(getMap());
	}

	public void Uninitialize(GameMap map){
		if(map!=null){
			if(getMap()!=null&&map.getWorld()!=null&&getMap().getWorld().getUID() == map.getWorld().getUID())setMap(null);

			if(map.getWorld()!=null)UtilMap.UnloadWorld(getManager().getInstance(), map.getWorld());
			UtilFile.DeleteFolder(map.getFile());
			map.clear();
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
		 if(loadFiles!=null)return loadFiles;
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

		 this.loadFiles=maps;
		 return maps;
	 }

	 public World getWorld(){
//		if(getMap() == null)throw new NullPointerException("WorldData GameMap ist NULL");
		return getMap().getWorld();
	 }

	 public String getMapName(){
		if(getMap() == null)throw new NullPointerException("WorldData GameMap ist NULL");
		return getMap().getMapName();
	 }

	 public ArrayList<File> loadSchematics(){
		 return loadFile(".schematic");
	 }

	 public void loadBiomes(ArrayList<Biome> nobiome){
		 loadBiomes(getMap(), nobiome);
	 }

	 public void loadBiomes(GameMap map, ArrayList<Biome> nobiome){
		 if(getBiomes()==null)setBiomes(new HashMap<String, Location>());
		 Location start = map.getWorld().getSpawnLocation().clone();

		 for(int i=0;i<150.000;i++){
			 start.add(0,0,i);
			 map.getWorld().loadChunk(start.getChunk());
		 }

		 start=map.getWorld().getSpawnLocation().clone();

		 for(int i=0;i<150.000;i++){
			 start.add(i,0,0);
			 map.getWorld().loadChunk(start.getChunk());
		 }

		 int bx;
		 int bz;
		 Block block;
		 Biome biome;
		 for(Chunk c : map.getWorld().getLoadedChunks()){
			bx = c.getX()<<4;
			bz = c.getZ()<<4;
			for(int xx = bx; xx < bx+16; xx++) {
			    for(int zz = bz; zz < bz+16; zz++) {
			        block = map.getWorld().getBlockAt(xx, 90, zz);
			        biome = map.getWorld().getBiome(block.getLocation().getBlockX(), block.getLocation().getBlockZ());
					if(!getBiomes().containsKey(biome.name())&&!nobiome.contains(biome)){
						getBiomes().put(biome.name(),block.getLocation());
					}
			    }
			}
		}
	 }

	 public void createCustomWorld(String s,String fileName){
		 Uninitialize();
		 WorldCreator wc = new WorldCreator(fileName);
		 wc.generator(new CleanroomChunkGenerator(s));
		 setMap(new GameMap(UtilWorld.LoadWorld(wc),new File(fileName),this));
	 }

	 public void createFlatWorld(){
		 createCustomWorld("64,grass","void");
	 }

	 public void createCleanWorld(){
		 createCustomWorld(".0,AIR","void");
	 }

	 public void createWorld(){
		 Uninitialize();
		 setMap(new GameMap(Bukkit.createWorld(new WorldCreator(getShortName())),new File(getShortName()),this));
	 }

	 public void setBiome(Location l,Biome biome){
		 setBiome(l, 200, biome);
	 }

	public void setBiome(Location l,int add,Biome biome){
		int min_x = l.getBlockX()-add;
		int max_x = l.getBlockX()+add;

		int min_z = l.getBlockZ()-add;
		int max_z = l.getBlockZ()+add;

		UtilWorldEdit.setBiome(new Location(l.getWorld(),min_x,90,min_z), new Location(l.getWorld(),max_x,90,max_z),biome);
	}

	public void pasteSchematic(Location l,File file){
		if(getEditSession()==null)setEditSession(new EditSession(new BukkitWorld(l.getWorld()), 999999999));
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
