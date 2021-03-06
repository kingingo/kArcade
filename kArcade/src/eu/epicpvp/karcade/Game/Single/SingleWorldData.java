package eu.epicpvp.karcade.Game.Single;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import eu.epicpvp.datenserver.definitions.dataserver.gamestats.GameType;
import eu.epicpvp.karcade.ArcadeManager;
import eu.epicpvp.karcade.Events.WorldLoadEvent;
import eu.epicpvp.karcade.Game.World.GameMap;
import eu.epicpvp.karcade.Game.World.WorldData;
import eu.epicpvp.karcade.Game.World.Event.WorldDataInitializeEvent;
import eu.epicpvp.karcade.Game.World.Event.WorldDataLoadConfigEvent;
import eu.epicpvp.karcade.Game.World.Parser.WorldParser;
import eu.epicpvp.kcore.ChunkGenerator.CleanroomChunkGenerator;
import eu.epicpvp.kcore.Enum.Team;
import eu.epicpvp.kcore.Util.UtilFile;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.kcore.Util.UtilLocation;
import eu.epicpvp.kcore.Util.UtilMath;
import eu.epicpvp.kcore.Util.UtilServer;
import eu.epicpvp.kcore.Util.UtilWorld;
import eu.epicpvp.kcore.Util.UtilWorldEdit;

public class SingleWorldData extends WorldData{

	public SingleWorldData(ArcadeManager manager,String gameName,String kuerzel){
		super(manager,gameName,kuerzel);
	}

	public SingleWorldData(ArcadeManager manager,GameType type){
		this(manager,type.name(),type.getShortName());
	}

	public void addLoc(Team team,Location loc){
		if(getMap() == null)throw new NullPointerException("WorldData GameMap ist NULL");
		addLoc(getMap(), team,loc);
	}

	public void addLoc(GameMap map,Team team,Location loc){
		if(!map.getLocations().containsKey(team))map.getLocations().put(team, new ArrayList<Location>());
		map.getLocations().get(team).add(loc);
	}

	public boolean existLoc(Team team){
		if(getMap() == null)throw new NullPointerException("WorldData GameMap ist NULL");
		return existLoc(getMap(), team);
	}

	public boolean existLoc(GameMap map,Team team){
		return map.getLocations().containsKey(team);
	}

	public ArrayList<Location> getSpawnLocations(Team team){
		if(getMap() == null)throw new NullPointerException("WorldData GameMap ist NULL");
		return getLocs(getMap(), team);
	}

	public ArrayList<Location> getLocs(GameMap map,Team team){
		if(team==null){
			logErr("Team == NULL!");
			return null;
		}else if(!map.getLocations().containsKey(team)){
			logErr("Team NOT Found! "+team.getDisplayName());
			return null;
		}else{
			return map.getLocations().get(team);
		}
	}

	public File[] randomMaps(int map_amount) throws Exception{
		return randomMaps(loadZips(),map_amount);
	}

	public File[] toFiles(){
		return toFiles(loadZips());
	}

	public File[] toFiles(ArrayList<File> files){
		File[] maps = new File[files.size()];
		int size = files.size();
		for(int i = 0; i < size; i++){
			maps[i]=files.get(UtilMath.randomInteger(files.size()));
			files.remove(maps[i]);
		}
		return maps;
	}

	public File[] randomMaps(ArrayList<File> files,int map_amount) throws Exception{
		if(files.isEmpty())return null;
		if(files.size() < map_amount) throw new Exception("Zu wenig einträge um "+map_amount+">"+files.size()+" zufüllige Maps auszuwählen!");
		File[] maps = new File[map_amount];

		for(int i = 0; i < map_amount; i++){
			maps[i]=files.get(UtilMath.randomInteger(files.size()));
			files.remove(maps[i]);
		}

		return maps;
	}

	public File randomMap(){
		return randomMap(loadZips());
	}

	public File randomMap(ArrayList<File> files){
		if(files.isEmpty())return null;
		if(files.size()==1)return files.get(0);
		return files.get(UtilMath.RandomInt(files.size(), 0));
	}

	public File UnzipWorld(){
		return UnzipWorld(randomMap());
	}

	public File UnzipWorld(File file){
	    if(file==null)throw new NullPointerException("Die File ist null");
	    String folder = file.getName();
	    if(folder.endsWith(".zip"))folder=folder.replaceAll(".zip", "");
	    folder="worldData_"+folder;

	    new File(folder).mkdir();
	    new File(folder + File.separator + "region"+File.separator).mkdir();
	    new File(folder + File.separator + "data"+File.separator).mkdir();
	    try {
	    	UtilFile.unzip(file, new File(folder));
		} catch (IOException e) {
			e.printStackTrace();
		}
	    new File(folder + File.separator + "data" + File.separator + "villages.dat").delete();
	    new File(folder + File.separator + "data" + File.separator + "villages_end.dat").delete();
	    new File(folder + File.separator + "data" + File.separator + "villages_nether.dat").delete();
	    new File(folder + File.separator + "uid.dat").delete();
	    return new File(folder);
	}

	public void Initialize(){
		Initialize(randomMap(loadZips()));
	}

	public void Initialize(File file){
		final WorldData wd = this;
		UtilServer.getServer().getScheduler().runTaskAsynchronously(getManager().getInstance(), new Runnable()
	    {
	      public void run()
	      {
	        File nfile=UnzipWorld(file);
	        UtilServer.getServer().getScheduler().runTask(getManager().getInstance(), new Runnable()
	        {
	          public void run(){
	        	GameMap map;
	            if(isCleanroomChunkGenerator()){
	            	map=new GameMap(UtilWorld.LoadWorld(new WorldCreator(nfile.getName()), new CleanroomChunkGenerator(".0,AIR")),nfile, wd);
	            }else{
	            	map=new GameMap(UtilWorld.LoadWorld(new WorldCreator(nfile.getName())),nfile, wd);
	            }

	            map.getWorld().setDifficulty(Difficulty.HARD);
	            LoadWorldConfig(map);
	            if(getMap()==null)setMap(map);
	            Bukkit.getPluginManager().callEvent(new WorldDataInitializeEvent( wd, map ));
	    		Bukkit.getPluginManager().callEvent(new WorldLoadEvent( map.getWorld() ));
	          }
	        });
	      }
	    });
	}

	public void Initialize(GameMap map){
		if(isCleanroomChunkGenerator()){
			map.setWorld(UtilWorld.LoadWorld(new WorldCreator(map.getFile().getName()), new CleanroomChunkGenerator(".0,AIR")));
	    }else{
	    	map.setWorld(UtilWorld.LoadWorld(new WorldCreator(map.getFile().getName())));
	    }

		map.getWorld().setDifficulty(Difficulty.HARD);

		LoadWorldConfig(map);
		if(getMap()==null)setMap(map);
		Bukkit.getPluginManager().callEvent(new WorldDataInitializeEvent( this, map ));
	}

	public void setIsland(HashMap<File[],Integer> l,int border,Location location){
		setIsland(l, border, location, getMap());
	}

	public void setIsland(HashMap<File[],Integer> l,int border,Location location,GameMap map){
		int amount=0;
		for(Integer i : l.values())amount=amount+i;

		ArrayList<Location> list = UtilLocation.RandomLocs(location.getWorld(), amount, border, location);
		File file;
		File[] files;
		boolean f=false;
		int a = 0;
		if(!getMap().getLocations().containsKey(Team.RED.getDisplayName()))map.getLocations().put(Team.RED, new ArrayList<Location>());
		if(!map.getLocations().containsKey(Team.BLUE.getDisplayName()))map.getLocations().put(Team.BLUE, new ArrayList<Location>());
		if(!map.getLocations().containsKey(Team.GREEN.getDisplayName()))map.getLocations().put(Team.GREEN, new ArrayList<Location>());
		Block block;
		Block[] blocks;
		Location loc;
			for(int o = 0; o < (amount*amount) ; o++){
				if(list.isEmpty())break;
				if(l.isEmpty())break;
				loc=list.get(UtilMath.randomInteger(list.size()));
				files=(File[])l.keySet().toArray()[UtilMath.randomInteger(l.size())];
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

				file=files[UtilMath.randomInteger(files.length)];
				log("A: "+a+" FILE:"+file.getName());

				if(file.getName().contains("PLAYER_ISLAND")){
					loc.setY(UtilMath.RandomInt(80, 65));
					UtilWorldEdit.pastePlate(loc, file);

					block=UtilLocation.searchBlock(Material.ENDER_PORTAL_FRAME, 20, loc);
					if(block==null){
						logErr("RED_LOCATION NOT FOUND!!!!");
						continue;
					}
					map.getLocations().get(Team.RED.getDisplayName()).add(block.getLocation());
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
						map.getLocations().get(Team.BLUE.getDisplayName()).add(b.getLocation());
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
						map.getLocations().get(Team.GREEN.getDisplayName()).add(b.getLocation());
					}
				}
			list.remove(loc);
			}
		log("Load Islands:");
		for(Team team : map.getLocations().keySet())log("	TEAM:"+team.getDisplayName()+" LOC_ANZAHL:"+map.getLocations().get(team).size());
	}

	public void LoadWorldConfig(){
		LoadWorldConfig(getMap());
	}

	public GameMap LoadWorldConfig(GameMap map){
		log("Map: "+map.getFile().getName());
		String line=null;
		try {
			FileInputStream fstream = new FileInputStream(map.getFile().getAbsolutePath() + File.separator + "WorldConfig.dat");
			DataInputStream in = new DataInputStream(fstream);
		    BufferedReader br = new BufferedReader(new InputStreamReader(in));

		    while((line=br.readLine()) != null){
		    	String[] tokens = line.split(":");
		    	if (tokens.length >= 2){
		    		WorldDataLoadConfigEvent ev = new WorldDataLoadConfigEvent(this, map, tokens);
		    		Bukkit.getPluginManager().callEvent(ev);
		    		if(ev.isCancelled())continue;

		    		if(tokens[0].equalsIgnoreCase("MAP_NAME")){
		    			map.setMapName(tokens[1]);
		    		}else if(tokens[0].equalsIgnoreCase("ITEM")){
		    			if(tokens[1].contains("-")){
		    				map.setItem(UtilItem.RenameItem(new ItemStack( Integer.valueOf(tokens[1].split("-")[0]),1,Byte.valueOf(tokens[1].split("-")[1]) ), "§7"+map.getMapName()));
		    			}else{
		    				map.setItem(UtilItem.RenameItem(new ItemStack( Integer.valueOf(tokens[1]),1), "§7"+map.getMapName()));
		    			}
		    		}else if(map.getWorld()==null){
		    			continue;
		    		}else if(tokens[0].equalsIgnoreCase(Team.SOLO.getDisplayName())){
		    			map.getLocations().put(Team.SOLO, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.SILBER.getDisplayName())){
		    			map.getLocations().put(Team.SILBER, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.GOLD.getDisplayName())){
		    			map.getLocations().put(Team.GOLD, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.DIAMOND.getDisplayName())){
		    			map.getLocations().put(Team.DIAMOND, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.BRONZE.getDisplayName())){
		    			map.getLocations().put(Team.BRONZE, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.ORANGE.getDisplayName())){
		    			map.getLocations().put(Team.ORANGE, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.PURPLE.getDisplayName())){
		    			map.getLocations().put(Team.PURPLE, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.GRAY.getDisplayName())){
		    			map.getLocations().put(Team.GRAY, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.BLUE.getDisplayName())){
		    			map.getLocations().put(Team.BLUE, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.RED.getDisplayName())){
		    			map.getLocations().put(Team.RED, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.YELLOW.getDisplayName())){
		    			map.getLocations().put(Team.YELLOW, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.GREEN.getDisplayName())){
		    			map.getLocations().put(Team.GREEN, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.CYAN.getDisplayName())){
		    			map.getLocations().put(Team.CYAN, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.AQUA.getDisplayName())){
		    			map.getLocations().put(Team.AQUA, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.BLACK.getDisplayName())){
		    			map.getLocations().put(Team.BLACK, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.BROWN.getDisplayName())){
		    			map.getLocations().put(Team.BROWN, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.LIME.getDisplayName())){
		    			map.getLocations().put(Team.LIME, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.MAGENTA.getDisplayName())){
		    			map.getLocations().put(Team.MAGENTA, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.LIGHT_GRAY.getDisplayName())){
		    			map.getLocations().put(Team.LIGHT_GRAY, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.WHITE.getDisplayName())){
		    			map.getLocations().put(Team.WHITE, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.VILLAGE_BLUE.getDisplayName())){
		    			map.getLocations().put(Team.VILLAGE_BLUE, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.VILLAGE_BROWN.getDisplayName())){
		    			map.getLocations().put(Team.VILLAGE_BROWN, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.LIME.getDisplayName())){
		    			map.getLocations().put(Team.VILLAGE_LIME, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.VILLAGE_MAGENTA.getDisplayName())){
		    			map.getLocations().put(Team.VILLAGE_MAGENTA, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.VILLAGE_LIGHT_GRAY.getDisplayName())){
		    			map.getLocations().put(Team.VILLAGE_LIGHT_GRAY, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.VILLAGE_RED.getDisplayName())){
		    			map.getLocations().put(Team.VILLAGE_RED, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.VILLAGE_YELLOW.getDisplayName())){
		    			map.getLocations().put(Team.VILLAGE_YELLOW, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.VILLAGE_GREEN.getDisplayName())){
		    			map.getLocations().put(Team.VILLAGE_GREEN, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.SHEEP_BLUE.getDisplayName())){
		    			map.getLocations().put(Team.SHEEP_BLUE, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.SHEEP_RED.getDisplayName())){
		    			map.getLocations().put(Team.SHEEP_RED, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.SHEEP_YELLOW.getDisplayName())){
		    			map.getLocations().put(Team.SHEEP_YELLOW, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.SHEEP_GREEN.getDisplayName())){
		    			map.getLocations().put(Team.SHEEP_GREEN, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.PINK.getDisplayName())){
		    			map.getLocations().put(Team.PINK, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.VILLAGE_GRAY.getDisplayName())){
		    			map.getLocations().put(Team.VILLAGE_GRAY, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.VILLAGE_PINK.getDisplayName())){
		    			map.getLocations().put(Team.VILLAGE_PINK, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.VILLAGE_PURPLE.getDisplayName())){
		    			map.getLocations().put(Team.VILLAGE_PURPLE, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.VILLAGE_ORANGE.getDisplayName())){
		    			map.getLocations().put(Team.VILLAGE_ORANGE, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.SHEEP_BROWN.getDisplayName())){
		    			map.getLocations().put(Team.SHEEP_BROWN, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.SHEEP_LIME.getDisplayName())){
		    			map.getLocations().put(Team.SHEEP_LIME, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.SHEEP_MAGENTA.getDisplayName())){
		    			map.getLocations().put(Team.SHEEP_MAGENTA, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.SHEEP_LIGHT_GRAY.getDisplayName())){
		    			map.getLocations().put(Team.SHEEP_LIGHT_GRAY, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.SHEEP_GRAY.getDisplayName())){
		    			map.getLocations().put(Team.SHEEP_GRAY, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.SHEEP_ORANGE.getDisplayName())){
		    			map.getLocations().put(Team.SHEEP_ORANGE, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.SHEEP_PURPLE.getDisplayName())){
		    			map.getLocations().put(Team.SHEEP_PURPLE, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.SHEEP_PINK.getDisplayName())){
		    			map.getLocations().put(Team.SHEEP_PINK, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.SHEEP_CYAN.getDisplayName())){
		    			map.getLocations().put(Team.SHEEP_CYAN, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.SHEEP_AQUA.getDisplayName())){
		    			map.getLocations().put(Team.SHEEP_AQUA, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.SHEEP_BLACK.getDisplayName())){
		    			map.getLocations().put(Team.SHEEP_BLACK, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.SHEEP_WHITE.getDisplayName())){
		    			map.getLocations().put(Team.SHEEP_WHITE, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.VILLAGE_AQUA.getDisplayName())){
		    			map.getLocations().put(Team.VILLAGE_AQUA, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.VILLAGE_BLACK.getDisplayName())){
		    			map.getLocations().put(Team.VILLAGE_BLACK, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.VILLAGE_CYAN.getDisplayName())){
		    			map.getLocations().put(Team.VILLAGE_CYAN, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.VILLAGE_WHITE.getDisplayName())){
		    			map.getLocations().put(Team.VILLAGE_WHITE, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_1.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_POINT_1, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_2.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_POINT_2, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_3.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_POINT_3, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_4.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_POINT_4, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_5.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_POINT_5, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_6.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_POINT_6, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_7.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_POINT_7, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_8.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_POINT_8, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_9.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_POINT_9, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_10.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_POINT_10, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_11.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_POINT_11, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_12.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_POINT_12, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_13.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_POINT_13, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_14.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_POINT_14, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_15.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_POINT_15, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_16.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_POINT_16, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_17.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_POINT_17, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_18.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_POINT_18, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_19.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_POINT_19, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_20.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_POINT_20, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_21.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_POINT_21, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_22.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_POINT_22, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_23.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_POINT_23, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_24.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_POINT_24, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_25.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_POINT_25, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_26.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_POINT_26, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_27.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_POINT_27, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_28.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_POINT_28, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_29.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_POINT_29, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_30.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_POINT_30, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_31.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_POINT_31, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_POINT_32.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_POINT_32, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_1.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_1, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_2.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_2, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_3.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_3, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_4.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_4, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_5.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_5, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_6.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_6, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_7.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_7, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_8.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_8, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_9.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_9, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_10.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_10, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_11.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_11, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_12.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_12, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_13.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_13, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_14.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_14, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_15.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_15, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_16.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_16, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_17.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_17, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_18.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_18, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_19.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_19, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_20.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_20, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_21.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_21, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_22.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_22, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_23.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_23, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_24.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_24, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_25.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_25, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_26.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_26, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_27.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_27, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_28.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_28, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_29.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_29, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_30.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_30, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_31.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_31, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else if(tokens[0].equalsIgnoreCase(Team.TEAM_32.getDisplayName())){
		    			map.getLocations().put(Team.TEAM_32, WorldParser.StringListTOLocList(tokens[1],map.getWorld()));
		    		}else{
		    			logErr("LOAD TEAM NOT FIND -> "+tokens[0]+" "+tokens[1]);
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
		if(map.getItem()==null){
			map.setItem(UtilItem.RenameItem(new ItemStack(Material.BEDROCK,1), "§7"+map.getMapName()));
		}

		log("Load Config:");
		log("Map Name: "+map.getMapName());
		log("ITEM: "+map.getItem().getType().name());
		for(Team team : map.getLocations().keySet())log("TEAM:"+team.getDisplayName()+" LOC:"+map.getLocations().get(team).size());

		return map;
	}
}
