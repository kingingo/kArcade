package me.kingingo.karcade.Game.World;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import me.kingingo.kcore.Enum.Team;

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
		        	block = caller.getWorld().getBlockAt(caller.getLocation().getBlockX() + x, y, caller.getLocation().getBlockZ() + z);
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
		        		}else if(block.getData()==2){
		        			if(!list.containsKey(Team.PINK.Name())){
			        			list.put(Team.PINK.Name(), new ArrayList<Location>());
			        		}
			        		((ArrayList)list.get(Team.PINK.Name())).add(block.getLocation());
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==7){
		        			if(!list.containsKey(Team.GRAY.Name())){
			        			list.put(Team.GRAY.Name(), new ArrayList<Location>());
			        		}
			        		((ArrayList)list.get(Team.GRAY.Name())).add(block.getLocation());
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
		        		}else if(block.getData()==10){
		        			if(!list.containsKey(Team.PURPLE.Name())){
			        			list.put(Team.PURPLE.Name(), new ArrayList<Location>());
			        		}
			        		((ArrayList)list.get(Team.PURPLE.Name())).add(block.getLocation());
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==1){
		        			if(!list.containsKey(Team.ORANGE.Name())){
			        			list.put(Team.ORANGE.Name(), new ArrayList<Location>());
			        		}
			        		((ArrayList)list.get(Team.ORANGE.Name())).add(block.getLocation());
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==0){
		        			if(!list.containsKey(Team.WHITE.Name())){
			        			list.put(Team.WHITE.Name(), new ArrayList<Location>());
			        		}
			        		((ArrayList)list.get(Team.WHITE.Name())).add(block.getLocation());
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==9){
		        			if(!list.containsKey(Team.CYAN.Name())){
			        			list.put(Team.CYAN.Name(), new ArrayList<Location>());
			        		}
			        		((ArrayList)list.get(Team.CYAN.Name())).add(block.getLocation());
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==3){
		        			if(!list.containsKey(Team.AQUA.Name())){
			        			list.put(Team.AQUA.Name(), new ArrayList<Location>());
			        		}
			        		((ArrayList)list.get(Team.AQUA.Name())).add(block.getLocation());
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==15){
		        			if(!list.containsKey(Team.BLACK.Name())){
			        			list.put(Team.BLACK.Name(), new ArrayList<Location>());
			        		}
			        		((ArrayList)list.get(Team.BLACK.Name())).add(block.getLocation());
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}
		        	}else if(block.getType()==Material.WOOL&&block.getRelative(BlockFace.UP).getType()==Material.EMERALD_BLOCK){
		        		if(block.getData()==14){
		        			if(!list.containsKey(Team.VILLAGE_RED.Name())){
			        			list.put(Team.VILLAGE_RED.Name(), new ArrayList<Location>());
			        		}
			        		((ArrayList)list.get(Team.VILLAGE_RED.Name())).add(block.getLocation());
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==11){
		        			if(!list.containsKey(Team.VILLAGE_BLUE.Name())){
			        			list.put(Team.VILLAGE_BLUE.Name(), new ArrayList<Location>());
			        		}
			        		((ArrayList)list.get(Team.VILLAGE_BLUE.Name())).add(block.getLocation());
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==5){
		        			if(!list.containsKey(Team.VILLAGE_GREEN.Name())){
			        			list.put(Team.VILLAGE_GREEN.Name(), new ArrayList<Location>());
			        		}
			        		((ArrayList)list.get(Team.VILLAGE_GREEN.Name())).add(block.getLocation());
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==4){
		        			if(!list.containsKey(Team.VILLAGE_YELLOW.Name())){
			        			list.put(Team.VILLAGE_YELLOW.Name(), new ArrayList<Location>());
			        		}
			        		((ArrayList)list.get(Team.VILLAGE_YELLOW.Name())).add(block.getLocation());
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==10){
		        			if(!list.containsKey(Team.VILLAGE_PURPLE.Name())){
			        			list.put(Team.VILLAGE_PURPLE.Name(), new ArrayList<Location>());
			        		}
			        		((ArrayList)list.get(Team.VILLAGE_PURPLE.Name())).add(block.getLocation());
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==2){
		        			if(!list.containsKey(Team.VILLAGE_PINK.Name())){
			        			list.put(Team.VILLAGE_PINK.Name(), new ArrayList<Location>());
			        		}
			        		((ArrayList)list.get(Team.VILLAGE_PINK.Name())).add(block.getLocation());
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==7){
		        			if(!list.containsKey(Team.VILLAGE_GRAY.Name())){
			        			list.put(Team.VILLAGE_GRAY.Name(), new ArrayList<Location>());
			        		}
			        		((ArrayList)list.get(Team.VILLAGE_GRAY.Name())).add(block.getLocation());
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==1){
		        			if(!list.containsKey(Team.VILLAGE_ORANGE.Name())){
			        			list.put(Team.VILLAGE_ORANGE.Name(), new ArrayList<Location>());
			        		}
			        		((ArrayList)list.get(Team.VILLAGE_ORANGE.Name())).add(block.getLocation());
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}
		        	}else if(block.getType()==Material.WOOL&&block.getRelative(BlockFace.UP).getType()==Material.BEDROCK){
		        		if(block.getData()==14){
		        			if(!list.containsKey(Team.SHEEP_RED.Name())){
			        			list.put(Team.SHEEP_RED.Name(), new ArrayList<Location>());
			        		}
			        		((ArrayList)list.get(Team.SHEEP_RED.Name())).add(block.getLocation());
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==11){
		        			if(!list.containsKey(Team.SHEEP_BLUE.Name())){
			        			list.put(Team.SHEEP_BLUE.Name(), new ArrayList<Location>());
			        		}
			        		((ArrayList)list.get(Team.SHEEP_BLUE.Name())).add(block.getLocation());
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==5){
		        			if(!list.containsKey(Team.SHEEP_GREEN.Name())){
			        			list.put(Team.SHEEP_GREEN.Name(), new ArrayList<Location>());
			        		}
			        		((ArrayList)list.get(Team.SHEEP_GREEN.Name())).add(block.getLocation());
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==4){
		        			if(!list.containsKey(Team.SHEEP_YELLOW.Name())){
			        			list.put(Team.SHEEP_YELLOW.Name(), new ArrayList<Location>());
			        		}
			        		((ArrayList)list.get(Team.SHEEP_YELLOW.Name())).add(block.getLocation());
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==1){
		        			if(!list.containsKey(Team.SHEEP_ORANGE.Name())){
			        			list.put(Team.SHEEP_ORANGE.Name(), new ArrayList<Location>());
			        		}
			        		((ArrayList)list.get(Team.SHEEP_ORANGE.Name())).add(block.getLocation());
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==10){
		        			if(!list.containsKey(Team.SHEEP_PURPLE.Name())){
			        			list.put(Team.SHEEP_PURPLE.Name(), new ArrayList<Location>());
			        		}
			        		((ArrayList)list.get(Team.SHEEP_PURPLE.Name())).add(block.getLocation());
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==2){
		        			if(!list.containsKey(Team.SHEEP_PINK.Name())){
			        			list.put(Team.SHEEP_PINK.Name(), new ArrayList<Location>());
			        		}
			        		((ArrayList)list.get(Team.SHEEP_PINK.Name())).add(block.getLocation());
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==7){
		        			if(!list.containsKey(Team.SHEEP_GRAY.Name())){
			        			list.put(Team.SHEEP_GRAY.Name(), new ArrayList<Location>());
			        		}
			        		((ArrayList)list.get(Team.SHEEP_GRAY.Name())).add(block.getLocation());
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==0){
		        			if(!list.containsKey(Team.SHEEP_WHITE.Name())){
			        			list.put(Team.SHEEP_WHITE.Name(), new ArrayList<Location>());
			        		}
			        		((ArrayList)list.get(Team.SHEEP_WHITE.Name())).add(block.getLocation());
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==9){
		        			if(!list.containsKey(Team.SHEEP_CYAN.Name())){
			        			list.put(Team.SHEEP_CYAN.Name(), new ArrayList<Location>());
			        		}
			        		((ArrayList)list.get(Team.SHEEP_CYAN.Name())).add(block.getLocation());
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==3){
		        			if(!list.containsKey(Team.SHEEP_AQUA.Name())){
			        			list.put(Team.SHEEP_AQUA.Name(), new ArrayList<Location>());
			        		}
			        		((ArrayList)list.get(Team.SHEEP_AQUA.Name())).add(block.getLocation());
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==15){
		        			if(!list.containsKey(Team.SHEEP_BLACK.Name())){
			        			list.put(Team.SHEEP_BLACK.Name(), new ArrayList<Location>());
			        		}
			        		((ArrayList)list.get(Team.SHEEP_BLACK.Name())).add(block.getLocation());
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}
		        	}else if(block.getRelative(BlockFace.UP).getType()==Material.DIAMOND_BLOCK){
		        			if(block.getType()==Material.GOLD_BLOCK){
			        			if(!list.containsKey(Team.GOLD.Name())){
				        			list.put(Team.GOLD.Name(), new ArrayList<Location>());
				        		}
				        		((ArrayList)list.get(Team.GOLD.Name())).add(block.getLocation());
				        		block.setTypeId(0);
				        		block.getRelative(BlockFace.UP).setTypeId(0);	
		        			}else if(block.getType()==Material.DIAMOND_BLOCK){
			        			if(!list.containsKey(Team.DIAMOND.Name())){
				        			list.put(Team.DIAMOND.Name(), new ArrayList<Location>());
				        		}
				        		((ArrayList)list.get(Team.DIAMOND.Name())).add(block.getLocation());
				        		block.setTypeId(0);
				        		block.getRelative(BlockFace.UP).setTypeId(0);	
		        			}else if(block.getType()==Material.IRON_BLOCK){
			        			if(!list.containsKey(Team.SILBER.Name())){
				        			list.put(Team.SILBER.Name(), new ArrayList<Location>());
				        		}
				        		((ArrayList)list.get(Team.SILBER.Name())).add(block.getLocation());
				        		block.setTypeId(0);
				        		block.getRelative(BlockFace.UP).setTypeId(0);	
		        			}else if(block.getType()==Material.COAL_BLOCK){
			        			if(!list.containsKey(Team.BRONZE.Name())){
				        			list.put(Team.BRONZE.Name(), new ArrayList<Location>());
				        		}
				        		((ArrayList)list.get(Team.BRONZE.Name())).add(block.getLocation());
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
			if(list.containsKey(Team.PINK.Name())){
				out.write("PINK:"+LocListTOStringList(list.get(Team.PINK.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.PURPLE.Name())){
				out.write("PURPLE:"+LocListTOStringList(list.get(Team.PURPLE.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.ORANGE.Name())){
				out.write("ORANGE:"+LocListTOStringList(list.get(Team.ORANGE.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.GREEN.Name())){
				out.write("GREEN:"+LocListTOStringList(list.get(Team.GREEN.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.GRAY.Name())){
				out.write("GRAY:"+LocListTOStringList(list.get(Team.GRAY.Name())));
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
			if(list.containsKey(Team.CYAN.Name())){
				out.write("CYAN:"+LocListTOStringList(list.get(Team.CYAN.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.AQUA.Name())){
				out.write("AQUA:"+LocListTOStringList(list.get(Team.AQUA.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.WHITE.Name())){
				out.write("WHITE:"+LocListTOStringList(list.get(Team.WHITE.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.BLACK.Name())){
				out.write("BLACK:"+LocListTOStringList(list.get(Team.BLACK.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.BRONZE.Name())){
				out.write("BRONZE:"+LocListTOStringList(list.get(Team.BRONZE.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.SILBER.Name())){
				out.write("SILBER:"+LocListTOStringList(list.get(Team.SILBER.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.DIAMOND.Name())){
				out.write("DIAMOND:"+LocListTOStringList(list.get(Team.GOLD.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.GOLD.Name())){
				out.write("GOLD:"+LocListTOStringList(list.get(Team.GOLD.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.VILLAGE_BLUE.Name())){
				out.write("VILLAGE_BLUE:"+LocListTOStringList(list.get(Team.VILLAGE_BLUE.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.VILLAGE_GREEN.Name())){
				out.write("VILLAGE_GREEN:"+LocListTOStringList(list.get(Team.VILLAGE_GREEN.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.VILLAGE_RED.Name())){
				out.write("VILLAGE_RED:"+LocListTOStringList(list.get(Team.VILLAGE_RED.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.VILLAGE_YELLOW.Name())){
				out.write("VILLAGE_YELLOW:"+LocListTOStringList(list.get(Team.VILLAGE_YELLOW.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.VILLAGE_ORANGE.Name())){
				out.write("VILLAGE_ORANGE:"+LocListTOStringList(list.get(Team.VILLAGE_ORANGE.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.VILLAGE_PINK.Name())){
				out.write("VILLAGE_PINK:"+LocListTOStringList(list.get(Team.VILLAGE_PINK.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.VILLAGE_PURPLE.Name())){
				out.write("VILLAGE_PURPLE:"+LocListTOStringList(list.get(Team.VILLAGE_PURPLE.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.VILLAGE_GRAY.Name())){
				out.write("VILLAGE_GRAY:"+LocListTOStringList(list.get(Team.VILLAGE_GRAY.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.SHEEP_BLUE.Name())){
				out.write("SHEEP_BLUE:"+LocListTOStringList(list.get(Team.SHEEP_BLUE.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.SHEEP_GREEN.Name())){
				out.write("SHEEP_GREEN:"+LocListTOStringList(list.get(Team.SHEEP_GREEN.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.SHEEP_RED.Name())){
				out.write("SHEEP_RED:"+LocListTOStringList(list.get(Team.SHEEP_RED.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.SHEEP_YELLOW.Name())){
				out.write("SHEEP_YELLOW:"+LocListTOStringList(list.get(Team.SHEEP_YELLOW.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.SHEEP_ORANGE.Name())){
				out.write("SHEEP_ORANGE:"+LocListTOStringList(list.get(Team.SHEEP_ORANGE.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.SHEEP_PINK.Name())){
				out.write("SHEEP_PINK:"+LocListTOStringList(list.get(Team.SHEEP_PINK.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.SHEEP_PURPLE.Name())){
				out.write("SHEEP_PURPLE:"+LocListTOStringList(list.get(Team.SHEEP_PURPLE.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.SHEEP_GRAY.Name())){
				out.write("SHEEP_GRAY:"+LocListTOStringList(list.get(Team.SHEEP_GRAY.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.SHEEP_CYAN.Name())){
				out.write("SHEEP_CYAN:"+LocListTOStringList(list.get(Team.SHEEP_CYAN.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.SHEEP_AQUA.Name())){
				out.write("SHEEP_AQUA:"+LocListTOStringList(list.get(Team.SHEEP_AQUA.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.SHEEP_WHITE.Name())){
				out.write("SHEEP_WHITE:"+LocListTOStringList(list.get(Team.SHEEP_WHITE.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.SHEEP_BLACK.Name())){
				out.write("SHEEP_BLACK:"+LocListTOStringList(list.get(Team.SHEEP_BLACK.Name())));
				out.write("\n");
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	      
		caller.sendMessage("World Data Saved.");
	}
	
	public static String LocListTOStringList(ArrayList<Location> list){
		if(list==null||list.isEmpty()){
			System.err.println("[WorldData]: LIST Leer!");
			return "";
		}
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
