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
import me.kingingo.karcade.kArcade;
import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Enum.Team;
import me.kingingo.karcade.Events.WorldLoadEvent;
import me.kingingo.karcade.Game.World.Event.WorldDataInitializeEvent;
import me.kingingo.kcore.ChunkGenerator.CleanroomChunkGenerator;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.Util.UtilFile;
import me.kingingo.kcore.Util.UtilLocation;
import me.kingingo.kcore.Util.UtilMap;
import me.kingingo.kcore.Util.UtilMath;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.kcore.Util.UtilWorldEdit;
import me.kingingo.kcore.Util.WorldUtil;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

public class WorldData {
	@Getter
	kArcadeManager manager;
	@Getter
	String gameName;
	@Getter
	World world;
	String folder=null;
	@Getter
	@Setter
	String MapName="Loading ...";
	@Setter
	HashMap<String,ArrayList<Location>> locs = new HashMap<>();
	@Getter
	@Setter
	HashMap<String,Location> biomes = null;
	@Getter
	@Setter
	boolean cleanroomChunkGenerator = false;
	
	public WorldData(kArcadeManager manager,String gameName,String kürzel){
		this.manager=manager;
		this.gameName=gameName;
		this.folder=kürzel;
	}
	
	public WorldData(kArcadeManager manager,GameType type){
		this.manager=manager;
		this.gameName=type.name();
		this.folder=type.getKürzel();
	}
	
	public void addLoc(String s,Location loc){
		if(!locs.containsKey(s))locs.put(s, new ArrayList<Location>());
		locs.get(s).add(loc);
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
	
	public void clearWorld(){
		for(Entity e : getWorld().getEntities())e.remove();
	}
	
	public boolean ExistLoc(String s){
		return locs.containsKey(s);
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<Location> getLocs(String s){
		ArrayList<Location> list = new ArrayList<>();
		if(!locs.containsKey(s)){
			System.out.println("[WorldData] Team NOT Found!");
		}else{
			for(Location l : locs.get(s)){
				list.add(l);
			}
		}
		return list;
	}
	
	public String RandomMap(ArrayList<String> files){
		if(files.isEmpty())return null;
		if(files.size()==1)return files.get(0);
		return files.get(UtilMath.RandomInt(files.size(), 0));
	}
	
	//new File(f).lastModified();
	
	 protected void UnzipWorld(){
	    String folder = getFolder();
	    String f = RandomMap(LoadFiles());
	    if(f==null){
	    	System.out.println("ES WURDEN KEINE MAPS GEFUNDEN ...");
	    	return;
	    }
	    
	    new File(folder).mkdir();
	    new File(folder + File.separator + "region"+File.separator).mkdir();
	    new File(folder + File.separator + "data"+File.separator).mkdir();
	    try {
	    	UtilFile.unzip(new File(f), new File(getFolder()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	  }
	
	 public String getFolder(){
		 return folder;
	 }
	 
	 public ArrayList<String> LoadFiles(){
		    File folder = new File(kArcade.FilePath+File.separator+gameName);
		    if (!folder.exists()) folder.mkdirs();
		    ArrayList<String> maps = new ArrayList<>();
		    System.out.println("Suche Maps in: " + folder);

		    for (File file : folder.listFiles())
		    {
		      if (file.isFile())
		      {
		        String name = file.getName();

		        if (name.length() >= 5)
		        {
		          name = name.substring(name.length() - 4, name.length());

		          if (!file.getName().equals(".zip"))
		          {
		            maps.add(kArcade.FilePath+File.separator+gameName+File.separator+file.getName().substring(0, file.getName().length() - 4)+".zip");
		          }
		        }
		      }
		    }
		    for (String map : maps) {
		      System.out.println("Maps: " + map);
		    }
		    return maps;
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
	 
	 public ArrayList<File> loadSchematics(){
		 	File folder = new File(kArcade.FilePath+File.separator+gameName);
		 	ArrayList<File> maps = new ArrayList<>();
		    if (!folder.exists()) folder.mkdirs();
		    System.out.println("Suche Maps in: " + folder);

		    String name;
		    for (File file : folder.listFiles())
		    {
		      if (file.isFile())
		      {
		        name = file.getName();

		        if (name.length() >= 11)
		        {
		          name = name.substring(name.length() - 10, name.length());
		          if (!file.getName().equals(".schematic")){
		        	  maps.add(file);
		          }
		        }
		      }
		    }
		    for (File map : maps) {
		      System.out.println("Schematics: " + map.getPath());
		    }
		    return maps;
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
		UtilFile.DeleteFolder(new File(world.getName()));
		world=null;
	 }
	 
	 public void createCleanWorld(){
		 removeWorld();
		 WorldCreator wc = new WorldCreator(getFolder());
		 wc.generator(new CleanroomChunkGenerator(".0,AIR"));
		 world= WorldUtil.LoadWorld(wc);
	 }
	 
	 public void createWorld(){
		 removeWorld();
		 world=Bukkit.createWorld(new WorldCreator(getFolder()));
	 }
	 
	 public void removeWorld(){
		 if(Bukkit.getWorld(getFolder())!=null||world!=null){
			 Bukkit.unloadWorld(getFolder(), false);
			 UtilFile.DeleteFolder(new File(getFolder()));
			 world=null;
		 }
	 }
	 
	 public void setBiome(Location l,Biome biome){
		 setBiome(l, 300, biome);
	 }
	 
	public void setBiome(Location l,int add,Biome biome){	
		int min_x = l.getBlockX()-add;
		int max_x = l.getBlockX()+add;
		
		int min_z = l.getBlockZ()-add;
		int max_z = l.getBlockZ()+add;
		
		for(int x = min_x; x < max_x; x++){
			for(int z = min_z; z < max_z; z++){
				world.setBiome(x, z, biome);
			}
		}
	}
	 
	public void Initialize(){
		final WorldData wd = this;
		UtilServer.getServer().getScheduler().runTaskAsynchronously(manager.getInstance(), new Runnable()
	    {
	      public void run()
	      {
	        UnzipWorld();
	        UtilServer.getServer().getScheduler().runTask(manager.getInstance(), new Runnable()
	        {
	          public void run()
	          {
	            if(isCleanroomChunkGenerator()){
	            	world=WorldUtil.LoadWorld(new WorldCreator(getFolder()), new CleanroomChunkGenerator(".0,AIR"));
	            }else{
	            	world=WorldUtil.LoadWorld(new WorldCreator(getFolder()));
	            }
	            world.setDifficulty(Difficulty.HARD);
	            LoadWorldConfig();
	            Bukkit.getPluginManager().callEvent(new WorldDataInitializeEvent( wd ));
	          }
	        });
	      }
	    });
	}
	
	public void setIsland(HashMap<File[],Integer> l,int border,Location location){
		int amount=0;
		for(Integer i : l.values())amount=amount+i;
		
		ArrayList<Location> list = UtilLocation.RandomLocs(getWorld(), amount, border, location);
		File file;
		File[] files;
		boolean f=false;
		int a = 0;
		if(!locs.containsKey(Team.RED.Name()))locs.put(Team.RED.Name(), new ArrayList<Location>());
		if(!locs.containsKey(Team.BLUE.Name()))locs.put(Team.BLUE.Name(), new ArrayList<Location>());
		if(!locs.containsKey(Team.GREEN.Name()))locs.put(Team.GREEN.Name(), new ArrayList<Location>());
		Block block;
		Block[] blocks;
		Location loc;
			for(int o = 0; o < (amount*amount) ; o++){
				if(list.isEmpty())break;
				if(l.isEmpty())break;
				loc=list.get(UtilMath.r(list.size()));
				files=(File[])l.keySet().toArray()[UtilMath.r(l.size())];
				f=true;
				for(File[] fi : l.keySet()){
					if(fi==files){
						a=l.get(fi);
						if(a==0){
							f=false;
						}
						break;
					}
				}
				if(!f){
					l.remove(files);
					continue;
				}
				
				l.remove(files);
				a--;
				l.put(files, a);
				
				file=files[UtilMath.r(files.length)];
				System.out.println("[WorldData] A: "+a+" FILE:"+file.getName());		
						
				if(file.getName().contains("PLAYER_ISLAND")){
					loc.setY(UtilMath.RandomInt(80, 65));
					UtilWorldEdit.pastePlate(loc, file);
					
					block=UtilLocation.searchBlock(Material.ENDER_PORTAL_FRAME, 20, loc);
					if(block==null){
						System.out.println("WorlData: RED_LOCATION NICHT GEFUNDEN!!!!");
						continue;
					}
					locs.get(Team.RED.Name()).add(block.getLocation());
					block.setType(Material.AIR);
					block=null;
				}else if(file.getName().contains("CHEST_ISLAND")){
					loc.setY(UtilMath.RandomInt(120, 90));
					UtilWorldEdit.pastePlate(loc, file);
					blocks=UtilLocation.searchBlocks(Material.ENDER_PORTAL_FRAME, 20, loc);
					if(blocks.length==0){
						System.out.println("WorlData: BLUE_LOCATION NICHT GEFUNDEN!!!!");
						continue;
					}
					
					for(Block b : blocks){
						locs.get(Team.BLUE.Name()).add(b.getLocation());
						b.setType(Material.AIR);
					}
				}else if(file.getName().contains("OBSIDIAN_ISLAND")){
					loc.setY(UtilMath.RandomInt(90,75));
					UtilWorldEdit.pastePlate(loc, file);
					blocks=UtilLocation.searchBlocks(Material.ENDER_CHEST, 20, loc);
					if(blocks.length==0){
						System.out.println("WorlData: GREEN_LOCATION NICHT GEFUNDEN!!!!");
						continue;
					}
					
					for(Block b : blocks){
						locs.get(Team.GREEN.Name()).add(b.getLocation());
					}
				}
			list.remove(loc);
			}
		
		System.out.println("["+gameName+"] Load Islands:");
		for(String t : locs.keySet()){
			System.out.println("["+gameName+"] TEAM:"+t+" LOC_ANZAHL:"+locs.get(t).size());
		}
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
		    		}else if(tokens[0].equalsIgnoreCase(Team.SILBER.Name())){
		    			locs.put(Team.SILBER.Name(), WorldParser.StringListTOLocList(tokens[1],world));
		    		}else if(tokens[0].equalsIgnoreCase(Team.GOLD.Name())){
		    			locs.put(Team.GOLD.Name(), WorldParser.StringListTOLocList(tokens[1],world));
		    		}else if(tokens[0].equalsIgnoreCase(Team.DIAMOND.Name())){
		    			locs.put(Team.DIAMOND.Name(), WorldParser.StringListTOLocList(tokens[1],world));
		    		}else if(tokens[0].equalsIgnoreCase(Team.BRONZE.Name())){
		    			locs.put(Team.BRONZE.Name(), WorldParser.StringListTOLocList(tokens[1],world));
		    		}else if(tokens[0].equalsIgnoreCase(Team.ORANGE.Name())){
		    			locs.put(Team.ORANGE.Name(), WorldParser.StringListTOLocList(tokens[1],world));
		    		}else if(tokens[0].equalsIgnoreCase(Team.PURPLE.Name())){
		    			locs.put(Team.PURPLE.Name(), WorldParser.StringListTOLocList(tokens[1],world));
		    		}else if(tokens[0].equalsIgnoreCase(Team.WHITE.Name())){
		    			locs.put(Team.WHITE.Name(), WorldParser.StringListTOLocList(tokens[1],world));
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
		    		}else if(tokens[0].equalsIgnoreCase(Team.VILLAGE_BLUE.Name())){
		    			locs.put(Team.VILLAGE_BLUE.Name(), WorldParser.StringListTOLocList(tokens[1],world));
		    		}else if(tokens[0].equalsIgnoreCase(Team.VILLAGE_RED.Name())){
		    			locs.put(Team.VILLAGE_RED.Name(), WorldParser.StringListTOLocList(tokens[1],world));
		    		}else if(tokens[0].equalsIgnoreCase(Team.VILLAGE_YELLOW.Name())){
		    			locs.put(Team.VILLAGE_YELLOW.Name(), WorldParser.StringListTOLocList(tokens[1],world));
		    		}else if(tokens[0].equalsIgnoreCase(Team.VILLAGE_GREEN.Name())){
		    			locs.put(Team.VILLAGE_GREEN.Name(), WorldParser.StringListTOLocList(tokens[1],world));
		    		}else if(tokens[0].equalsIgnoreCase(Team.SHEEP_BLUE.Name())){
		    			locs.put(Team.SHEEP_BLUE.Name(), WorldParser.StringListTOLocList(tokens[1],world));
		    		}else if(tokens[0].equalsIgnoreCase(Team.SHEEP_RED.Name())){
		    			locs.put(Team.SHEEP_RED.Name(), WorldParser.StringListTOLocList(tokens[1],world));
		    		}else if(tokens[0].equalsIgnoreCase(Team.SHEEP_YELLOW.Name())){
		    			locs.put(Team.SHEEP_YELLOW.Name(), WorldParser.StringListTOLocList(tokens[1],world));
		    		}else if(tokens[0].equalsIgnoreCase(Team.SHEEP_GREEN.Name())){
		    			locs.put(Team.SHEEP_GREEN.Name(), WorldParser.StringListTOLocList(tokens[1],world));
		    		}
		        }
		    }
		    in.close();
		    
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("["+gameName+"] Load Config:");
		System.out.println("["+gameName+"] Map Name: "+MapName);
		for(String t : locs.keySet()){
				System.out.println("["+gameName+"] TEAM:"+t+" LOC:"+locs.get(t).size());
		}
		Bukkit.getPluginManager().callEvent(new WorldLoadEvent(Bukkit.getWorld(gameName)));
	}
	
}
