package me.kingingo.karcade.Game.World;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import me.kingingo.karcade.Enum.Team;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class WorldParser {

	public static void Scan(Player caller,String MapName){
		caller.sendMessage("Scannen der Blöcke...");
		Block block;
		int processed = 0;
		HashMap<String,ArrayList<Location>> list = new HashMap<>();
		for (int x = -600; x < 600; x++){
			for (int z = -600; z < 600; z++){
				for (int y = 0; y < 256; y++){
		        	processed++;
		        	if (processed % 20000000 == 0) {
		                caller.sendMessage("Processed: " + processed);
		            }
		        	block = caller.getWorld().getBlockAt(caller.getLocation().getBlockX() + x, caller.getLocation().getBlockY() + y, caller.getLocation().getBlockZ() + z);
		        	if(block.getType()==Material.MELON_BLOCK&&block.getRelative(BlockFace.UP).getType()==Material.REDSTONE_BLOCK){
		        		if(!list.containsKey(Team.SOLO.Name())){
		        			list.put(Team.SOLO.Name(), new ArrayList<Location>());
		        		}
		        		((ArrayList)list.get(Team.SOLO.Name())).add(block.getLocation());
		        		block.setTypeId(0);
		        		block.getRelative(BlockFace.UP).setTypeId(0);
		        	}else if(block.getType()==Material.WOOL&&block.getRelative(BlockFace.UP).getType()==Material.REDSTONE_BLOCK){
		        		if(block.getData()==14){
		        			if(!list.containsKey(Team.RED.Name())){
			        			list.put(Team.RED.Name(), new ArrayList<Location>());
			        		}
			        		((ArrayList)list.get(Team.RED.Name())).add(block.getLocation());
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==11){
		        			if(!list.containsKey(Team.BLUE.Name())){
			        			list.put(Team.BLUE.Name(), new ArrayList<Location>());
			        		}
			        		((ArrayList)list.get(Team.BLUE.Name())).add(block.getLocation());
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==5){
		        			if(!list.containsKey(Team.GREEN.Name())){
			        			list.put(Team.GREEN.Name(), new ArrayList<Location>());
			        		}
			        		((ArrayList)list.get(Team.GREEN.Name())).add(block.getLocation());
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==4){
		        			if(!list.containsKey(Team.YELLOW.Name())){
			        			list.put(Team.YELLOW.Name(), new ArrayList<Location>());
			        		}
			        		((ArrayList)list.get(Team.YELLOW.Name())).add(block.getLocation());
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}
		        	}
				}  
			}
		}
		
		if(list.isEmpty()){
			caller.sendMessage("Es wurde nichts gefunden!");
			return;
		}
		      
		try {
			FileWriter fstream= new FileWriter(caller.getWorld().getName() + File.separator + "WorldConfig.dat");
			BufferedWriter out = new BufferedWriter(fstream);  
			
			out.write("MAP_NAME:"+MapName);
			out.write("\n");
			if(list.containsKey(Team.SOLO.Name())){
				out.write("SOLO:"+LocListTOStringList(list.get(Team.SOLO.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.RED.Name())){
				out.write("RED:"+LocListTOStringList(list.get(Team.RED.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.GREEN.Name())){
				out.write("GREEN:"+LocListTOStringList(list.get(Team.GREEN.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.YELLOW.Name())){
				out.write("YELLOW:"+LocListTOStringList(list.get(Team.YELLOW.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.BLUE.Name())){
				out.write("BLUE:"+LocListTOStringList(list.get(Team.BLUE.Name())));
				out.write("\n");
			}
			
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	      
		caller.sendMessage("World Data Saved.");
	}
	
	public static String LocListTOStringList(ArrayList<Location> list){
		String l="";
		for(Location loc : list){
			l=l+loc.getBlockX()+";"+loc.getBlockY()+";"+loc.getBlockZ()+",";
		}
		return l.substring(0, l.length()-1);
	}
	
	public static ArrayList<Location> StringListTOLocList(String s,World world){
		ArrayList<Location> list = new ArrayList<Location>();
		//X;Y;Z;,X;Y;Z;,X;Y;Z;,X;Y;Z;
		for(String l : s.split(",")){
			String[] cordi = l.split(";");
			list.add(new Location(world,Integer.valueOf(cordi[0]),Integer.valueOf(cordi[1]),Integer.valueOf(cordi[2])));
		}
		
		return list;
	}
	
}
