package me.kingingo.karcade.Game.World;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;
import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Enum.Team;
import me.kingingo.karcade.Events.WorldLoadEvent;
import me.kingingo.kcore.Util.FileUtil;
import me.kingingo.kcore.Util.UtilMap;
import me.kingingo.kcore.Util.UtilMath;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.kcore.Util.WorldUtil;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

public class WorldData {
	@Getter
	kArcadeManager manager;
	@Getter
	String gameName;
	@Getter
	World world;
	String folder="map";
	@Getter
	@Setter
	String MapName="Loading ...";
	@Setter
	HashMap<String,ArrayList<Location>> locs = new HashMap<>();
	@Getter
	@Setter
	HashMap<String,Location> biomes = null;
	
	public WorldData(kArcadeManager manager,String gameName){
		this.manager=manager;
		this.gameName=gameName;
	}
	
	public HashMap<String,ArrayList<Location>> getLocs(){
		HashMap<String,ArrayList<Location>> list = new HashMap<>();
		for(String s : locs.keySet()){
			list.put(s, new ArrayList<Location>());
			for(Location l : locs.get(s)){
				list.get(s).add(l);
			}
		}
		return list;
	}
	
	public boolean ExistLoc(String s){
		return locs.containsKey(s);
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<Location> getLocs(String s){
		ArrayList<Location> list = new ArrayList<>();
		for(Location l : locs.get(s)){
			list.add(l);
		}
		return list;
	}
	
	public String RandomMap(ArrayList<String> files){
		if(files.isEmpty())return null;
		if(files.size()==1)return files.get(0);
		return files.get(UtilMath.RandomInt(files.size(), 0));
	}
	
	 protected void UnzipWorld(){
	    String folder = getFolder();
	    String f = RandomMap(manager.LoadFiles(gameName));
	    if(f==null){
	    	System.out.println("ES WURDEN KEINE MAPS GEFUNDEN ...");
	    	return;
	    }
	    new File(folder).mkdir();
	    new File(folder + File.separator + "region"+File.separator).mkdir();
	    new File(folder + File.separator + "data"+File.separator).mkdir();
	    try {
	    	FileUtil.unzip(new File(f), new File(getFolder()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	  }
	
	 public String getFolder(){
		 return folder;
	 }
	 
	 public void loadBiomes(ArrayList<Biome> nobiome){
		 biomes=new HashMap<String, Location>();
		 Location start = world.getSpawnLocation().clone();
		 for(int i=0;i<150.000;i++){
				start.add(0,0,i);
		        world.loadChunk(start.getChunk());
			}
		start=world.getSpawnLocation().clone();
		for(int i=0;i<150.000;i++){
				start.add(i,0,0);
				world.loadChunk(start.getChunk());
			}
		for(Chunk c : world.getLoadedChunks()){
			int bx = c.getX()<<4;
			int bz = c.getZ()<<4;
			for(int xx = bx; xx < bx+16; xx++) {
			    for(int zz = bz; zz < bz+16; zz++) {
			        for(int yy = 0; yy < 128; yy++) {
			        	Block b = world.getBlockAt(xx, yy, zz);
			        	Biome bio = world.getBiome(b.getLocation().getBlockX(), b.getLocation().getBlockZ());
						if(!biomes.containsKey(bio.name())&&!nobiome.contains(bio)){
							biomes.put(bio.name(),b.getLocation());
						}
						break;
			        }
			    }
			}
		}
	 }
	 
	 public void Uninitialize(){
		UtilMap.UnloadWorld(manager.getInstance(), world);
		FileUtil.DeleteFolder(new File(world.getName()));
		world=null;
	 }
	 
	 public void createWorld(){
		 removeWorld();
		 world=Bukkit.createWorld(new WorldCreator("map"));
	 }
	 
	 public void removeWorld(){
		 if(Bukkit.getWorld("map")!=null||world!=null){
			 Bukkit.unloadWorld("map", false);
			 FileUtil.DeleteFolder(new File("map"));
			 world=null;
		 }
	 }
	 
	public void Initialize(){
		UtilServer.getServer().getScheduler().runTaskAsynchronously(manager.getInstance(), new Runnable()
	    {
	      public void run()
	      {
	        UnzipWorld();
	        UtilServer.getServer().getScheduler().runTask(manager.getInstance(), new Runnable()
	        {
	          public void run()
	          {
	            world=WorldUtil.LoadWorld(new WorldCreator(getFolder()));
	            world.setDifficulty(Difficulty.HARD);
	            LoadWorldConfig();
	          }
	        });
	      }
	    });
	}
	
	public void LoadWorldConfig(){
		String line=null;
		try {
			FileInputStream fstream = new FileInputStream(getFolder() + File.separator + "WorldConfig.dat");
			DataInputStream in = new DataInputStream(fstream);
		    BufferedReader br = new BufferedReader(new InputStreamReader(in));
		    
		    while((line=br.readLine()) != null){
		    	String[] tokens = line.split(":");
		    	if (tokens.length >= 2){
		    		if(tokens[0].equalsIgnoreCase("MAP_NAME")){
		    			this.MapName=tokens[1];
		    		}else if(tokens[0].equalsIgnoreCase(Team.SOLO.Name())){
		    			locs.put(Team.SOLO.Name(), WorldParser.StringListTOLocList(tokens[1],world));
		    		}else if(tokens[0].equalsIgnoreCase(Team.BLACK.Name())){
		    			locs.put(Team.BLACK.Name(), WorldParser.StringListTOLocList(tokens[1],world));
		    		}else if(tokens[0].equalsIgnoreCase(Team.GRAY.Name())){
		    			locs.put(Team.GRAY.Name(), WorldParser.StringListTOLocList(tokens[1],world));
		    		}else if(tokens[0].equalsIgnoreCase(Team.BLUE.Name())){
		    			locs.put(Team.BLUE.Name(), WorldParser.StringListTOLocList(tokens[1],world));
		    		}else if(tokens[0].equalsIgnoreCase(Team.RED.Name())){
		    			locs.put(Team.RED.Name(), WorldParser.StringListTOLocList(tokens[1],world));
		    		}else if(tokens[0].equalsIgnoreCase(Team.YELLOW.Name())){
		    			locs.put(Team.YELLOW.Name(), WorldParser.StringListTOLocList(tokens[1],world));
		    		}else if(tokens[0].equalsIgnoreCase(Team.GREEN.Name())){
		    			locs.put(Team.GREEN.Name(), WorldParser.StringListTOLocList(tokens[1],world));
		    		}
		        }
		    }
		    in.close();
		    
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("[kArcade] Load Config:");
		System.out.println("[kArcade] Map Name: "+MapName);
		for(String t : locs.keySet()){
				System.out.println("TEAM:"+t+" LOC:"+locs.get(t).size());
		}
		Bukkit.getPluginManager().callEvent(new WorldLoadEvent(Bukkit.getWorld("map")));
	}
	
}
