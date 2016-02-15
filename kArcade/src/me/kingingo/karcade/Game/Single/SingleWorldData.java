package me.kingingo.karcade.Game.Single;

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
import me.kingingo.karcade.Events.WorldLoadEvent;
import me.kingingo.karcade.Game.World.WorldData;
import me.kingingo.karcade.Game.World.WorldParser;
import me.kingingo.karcade.Game.World.Event.WorldDataInitializeEvent;
import me.kingingo.kcore.ChunkGenerator.CleanroomChunkGenerator;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.Enum.Team;
import me.kingingo.kcore.Util.UtilFile;
import me.kingingo.kcore.Util.UtilLocation;
import me.kingingo.kcore.Util.UtilMath;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.kcore.Util.UtilWorld;
import me.kingingo.kcore.Util.UtilWorldEdit;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;

public class SingleWorldData extends WorldData{
	@Getter
	@Setter
	private String MapName="Loading ...";
	@Setter
	private HashMap<Team,ArrayList<Location>> locs = new HashMap<>();
	
	public SingleWorldData(kArcadeManager manager,String gameName,String kürzel){
		super(manager,gameName,kürzel);
	}
	
	public SingleWorldData(kArcadeManager manager,GameType type){
		this(manager,type.name(),type.getKürzel());
	}
	
	public void addLoc(Team team,Location loc){
		if(!locs.containsKey(team))locs.put(team, new ArrayList<Location>());
		locs.get(team).add(loc);
	}
	
	public boolean existLoc(Team team){
		return locs.containsKey(team);
	}
	
	public ArrayList<Location> getLocs(Team team){
		if(team==null){
			logErr("Team == NULL!");
			return null;
		}else if(!locs.containsKey(team)){
			logErr("Team NOT Found! "+team.Name());
			return null;
		}else{
			return locs.get(team);
		}
	}
	
	public File randomMap(ArrayList<File> files){
		if(files.isEmpty())return null;
		if(files.size()==1)return files.get(0);
		return files.get(UtilMath.RandomInt(files.size(), 0));
	}
	
	protected void UnzipWorld(){
	    String folder = getFolder();
	    File file = randomMap(loadZips());
	    if(file==null){
	    	logErr("DID NOT FIND ANY MAPS ...");
	    	return;
	    }
	    
	    new File(folder).mkdir();
	    new File(folder + File.separator + "region"+File.separator).mkdir();
	    new File(folder + File.separator + "data"+File.separator).mkdir();
	    try {
	    	UtilFile.unzip(file, new File(getFolder()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	    new File(folder + File.separator + "uid.dat").delete();
	}
	 
	public void Initialize(){
		final WorldData wd = this;
		UtilServer.getServer().getScheduler().runTaskAsynchronously(getManager().getInstance(), new Runnable()
	    {
	      public void run()
	      {
	        UnzipWorld();
	        UtilServer.getServer().getScheduler().runTask(getManager().getInstance(), new Runnable()
	        {
	          public void run(){
	        	  
	            if(isCleanroomChunkGenerator()){
	            	setWorld(UtilWorld.LoadWorld(new WorldCreator(getFolder()), new CleanroomChunkGenerator(".0,AIR")));
	            }else{
	            	setWorld(UtilWorld.LoadWorld(new WorldCreator(getFolder())));
	            }
	            
	            getWorld().setDifficulty(Difficulty.HARD);
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
		if(!locs.containsKey(Team.RED.Name()))locs.put(Team.RED, new ArrayList<Location>());
		if(!locs.containsKey(Team.BLUE.Name()))locs.put(Team.BLUE, new ArrayList<Location>());
		if(!locs.containsKey(Team.GREEN.Name()))locs.put(Team.GREEN, new ArrayList<Location>());
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
				log("A: "+a+" FILE:"+file.getName());
						
				if(file.getName().contains("PLAYER_ISLAND")){
					loc.setY(UtilMath.RandomInt(80, 65));
					UtilWorldEdit.pastePlate(loc, file);
					
					block=UtilLocation.searchBlock(Material.ENDER_PORTAL_FRAME, 20, loc);
					if(block==null){
						logErr("RED_LOCATION NOT FOUND!!!!");
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
						logErr("BLUE_LOCATION NOT FOUND!!!!");
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
						logErr("GREEN_LOCATION NOT FOUND!!!!");
						continue;
					}
					
					for(Block b : blocks){
						locs.get(Team.GREEN.Name()).add(b.getLocation());
					}
				}
			list.remove(loc);
			}
		log("Load Islands:");
		for(Team team : locs.keySet())log("	TEAM:"+team.Name()+" LOC_ANZAHL:"+locs.get(team).size());
	}
	
	public void LoadWorldConfig(){
		log("Map: "+getMapName());
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
		    			locs.put(Team.SOLO, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.SILBER.Name())){
		    			locs.put(Team.SILBER, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.GOLD.Name())){
		    			locs.put(Team.GOLD, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.DIAMOND.Name())){
		    			locs.put(Team.DIAMOND, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.BRONZE.Name())){
		    			locs.put(Team.BRONZE, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.ORANGE.Name())){
		    			locs.put(Team.ORANGE, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.PURPLE.Name())){
		    			locs.put(Team.PURPLE, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.GRAY.Name())){
		    			locs.put(Team.GRAY, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.BLUE.Name())){
		    			locs.put(Team.BLUE, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.RED.Name())){
		    			locs.put(Team.RED, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.YELLOW.Name())){
		    			locs.put(Team.YELLOW, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.GREEN.Name())){
		    			locs.put(Team.GREEN, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.CYAN.Name())){
		    			locs.put(Team.CYAN, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.AQUA.Name())){
		    			locs.put(Team.AQUA, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.BLACK.Name())){
		    			locs.put(Team.BLACK, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.WHITE.Name())){
		    			locs.put(Team.WHITE, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.VILLAGE_BLUE.Name())){
		    			locs.put(Team.VILLAGE_BLUE, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.VILLAGE_RED.Name())){
		    			locs.put(Team.VILLAGE_RED, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.VILLAGE_YELLOW.Name())){
		    			locs.put(Team.VILLAGE_YELLOW, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.VILLAGE_GREEN.Name())){
		    			locs.put(Team.VILLAGE_GREEN, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.SHEEP_BLUE.Name())){
		    			locs.put(Team.SHEEP_BLUE, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.SHEEP_RED.Name())){
		    			locs.put(Team.SHEEP_RED, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.SHEEP_YELLOW.Name())){
		    			locs.put(Team.SHEEP_YELLOW, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.SHEEP_GREEN.Name())){
		    			locs.put(Team.SHEEP_GREEN, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.PINK.Name())){
		    			locs.put(Team.PINK, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.VILLAGE_GRAY.Name())){
		    			locs.put(Team.VILLAGE_GRAY, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.VILLAGE_PINK.Name())){
		    			locs.put(Team.VILLAGE_PINK, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.VILLAGE_PURPLE.Name())){
		    			locs.put(Team.VILLAGE_PURPLE, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.VILLAGE_ORANGE.Name())){
		    			locs.put(Team.VILLAGE_ORANGE, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.SHEEP_GRAY.Name())){
		    			locs.put(Team.SHEEP_GRAY, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.SHEEP_ORANGE.Name())){
		    			locs.put(Team.SHEEP_ORANGE, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.SHEEP_PURPLE.Name())){
		    			locs.put(Team.SHEEP_PURPLE, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.SHEEP_PINK.Name())){
		    			locs.put(Team.SHEEP_PINK, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.SHEEP_CYAN.Name())){
		    			locs.put(Team.SHEEP_CYAN, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.SHEEP_AQUA.Name())){
		    			locs.put(Team.SHEEP_AQUA, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.SHEEP_BLACK.Name())){
		    			locs.put(Team.SHEEP_BLACK, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.SHEEP_WHITE.Name())){
		    			locs.put(Team.SHEEP_WHITE, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.VILLAGE_AQUA.Name())){
		    			locs.put(Team.VILLAGE_AQUA, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.VILLAGE_BLACK.Name())){
		    			locs.put(Team.VILLAGE_BLACK, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.VILLAGE_CYAN.Name())){
		    			locs.put(Team.VILLAGE_CYAN, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.VILLAGE_WHITE.Name())){
		    			locs.put(Team.VILLAGE_WHITE, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_1.Name())){
		    			locs.put(Team.TEAM_POINT_1, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_2.Name())){
		    			locs.put(Team.TEAM_POINT_2, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_3.Name())){
		    			locs.put(Team.TEAM_POINT_3, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_4.Name())){
		    			locs.put(Team.TEAM_POINT_4, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_5.Name())){
		    			locs.put(Team.TEAM_POINT_5, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_6.Name())){
		    			locs.put(Team.TEAM_POINT_6, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_7.Name())){
		    			locs.put(Team.TEAM_POINT_7, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_8.Name())){
		    			locs.put(Team.TEAM_POINT_8, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_9.Name())){
		    			locs.put(Team.TEAM_POINT_9, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_10.Name())){
		    			locs.put(Team.TEAM_POINT_10, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_11.Name())){
		    			locs.put(Team.TEAM_POINT_11, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_12.Name())){
		    			locs.put(Team.TEAM_POINT_12, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_13.Name())){
		    			locs.put(Team.TEAM_POINT_13, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_14.Name())){
		    			locs.put(Team.TEAM_POINT_14, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_15.Name())){
		    			locs.put(Team.TEAM_POINT_15, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_16.Name())){
		    			locs.put(Team.TEAM_POINT_16, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_17.Name())){
		    			locs.put(Team.TEAM_POINT_17, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_18.Name())){
		    			locs.put(Team.TEAM_POINT_18, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_19.Name())){
		    			locs.put(Team.TEAM_POINT_19, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_20.Name())){
		    			locs.put(Team.TEAM_POINT_20, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_21.Name())){
		    			locs.put(Team.TEAM_POINT_21, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_22.Name())){
		    			locs.put(Team.TEAM_POINT_22, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_23.Name())){
		    			locs.put(Team.TEAM_POINT_23, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_24.Name())){
		    			locs.put(Team.TEAM_POINT_24, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_25.Name())){
		    			locs.put(Team.TEAM_POINT_25, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_26.Name())){
		    			locs.put(Team.TEAM_POINT_26, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_27.Name())){
		    			locs.put(Team.TEAM_POINT_27, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_28.Name())){
		    			locs.put(Team.TEAM_POINT_28, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_29.Name())){
		    			locs.put(Team.TEAM_POINT_29, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_30.Name())){
		    			locs.put(Team.TEAM_POINT_30, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_31.Name())){
		    			locs.put(Team.TEAM_POINT_31, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_32.Name())){
		    			locs.put(Team.TEAM_POINT_32, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_1.Name())){
		    			locs.put(Team.TEAM_1, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_2.Name())){
		    			locs.put(Team.TEAM_2, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_3.Name())){
		    			locs.put(Team.TEAM_3, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_4.Name())){
		    			locs.put(Team.TEAM_4, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_5.Name())){
		    			locs.put(Team.TEAM_5, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_6.Name())){
		    			locs.put(Team.TEAM_6, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_7.Name())){
		    			locs.put(Team.TEAM_7, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_8.Name())){
		    			locs.put(Team.TEAM_8, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_9.Name())){
		    			locs.put(Team.TEAM_9, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_10.Name())){
		    			locs.put(Team.TEAM_10, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_11.Name())){
		    			locs.put(Team.TEAM_11, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_12.Name())){
		    			locs.put(Team.TEAM_12, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_13.Name())){
		    			locs.put(Team.TEAM_13, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_14.Name())){
		    			locs.put(Team.TEAM_14, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_15.Name())){
		    			locs.put(Team.TEAM_15, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_16.Name())){
		    			locs.put(Team.TEAM_16, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_17.Name())){
		    			locs.put(Team.TEAM_17, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_18.Name())){
		    			locs.put(Team.TEAM_18, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_19.Name())){
		    			locs.put(Team.TEAM_19, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_20.Name())){
		    			locs.put(Team.TEAM_20, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_21.Name())){
		    			locs.put(Team.TEAM_21, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_22.Name())){
		    			locs.put(Team.TEAM_22, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_23.Name())){
		    			locs.put(Team.TEAM_23, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_24.Name())){
		    			locs.put(Team.TEAM_24, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_25.Name())){
		    			locs.put(Team.TEAM_25, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_26.Name())){
		    			locs.put(Team.TEAM_26, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_27.Name())){
		    			locs.put(Team.TEAM_27, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_28.Name())){
		    			locs.put(Team.TEAM_28, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_29.Name())){
		    			locs.put(Team.TEAM_29, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_30.Name())){
		    			locs.put(Team.TEAM_30, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_31.Name())){
		    			locs.put(Team.TEAM_31, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_32.Name())){
		    			locs.put(Team.TEAM_32, WorldParser.StringListTOLocList(tokens[1],getWorld()));
		    		}else{
		    			logErr("LOAD TEAM NOT FIND -> "+tokens[0]);
		    		}
		    		
		        }
		    }
		    in.close();
		    br.close();
		    fstream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		log("Load Config:");
		log("Map Name: "+MapName);
		for(Team team : locs.keySet())log("TEAM:"+team.Name()+" LOC:"+locs.get(team).size());
		
		Bukkit.getPluginManager().callEvent(new WorldLoadEvent(Bukkit.getWorld(getGameName())));
	}
}
