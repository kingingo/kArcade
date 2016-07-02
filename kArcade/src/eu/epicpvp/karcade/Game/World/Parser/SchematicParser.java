package eu.epicpvp.karcade.Game.World.Parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

import eu.epicpvp.kcore.Enum.Team;
import eu.epicpvp.kcore.Util.UtilVector;

public class SchematicParser {

	public static void Scan(Location location,String MapName){
		System.out.println("[WorldSetUp] Scannen der Bloecke...");
		Block block;
		int processed = 0;
		HashMap<String,ArrayList<Vector>> list = new HashMap<>();
		for (int x = -600; x < 600; x++){
			for (int z = -600; z < 600; z++){
				for (int y = 0; y < 256; y++){
		        	processed++;
		        	if (processed % 20000000 == 0) {
		        		System.out.println("[WorldSetUp] Processed: " + processed);
		            }
		        	block = location.getWorld().getBlockAt(location.getBlockX() + x, y, location.getBlockZ() + z);
		        	if(block.getType()==Material.MELON_BLOCK&&block.getRelative(BlockFace.UP).getType()==Material.REDSTONE_BLOCK){
		        		if(!list.containsKey(Team.SOLO.getDisplayName())){
		        			list.put(Team.SOLO.getDisplayName(), new ArrayList<Vector>());
		        		}
		        		((ArrayList)list.get(Team.SOLO.getDisplayName())).add( UtilVector.subtract(location.toVector(), block.getLocation().toVector()) );
		        		block.setTypeId(0);
		        		block.getRelative(BlockFace.UP).setTypeId(0);
		        	}else if(block.getType()==Material.WOOL&&block.getRelative(BlockFace.UP).getType()==Material.REDSTONE_BLOCK){
		        		if(block.getData()==14){
		        			if(!list.containsKey(Team.RED.getDisplayName())){
			        			list.put(Team.RED.getDisplayName(), new ArrayList<Vector>());
			        		}
			        		((ArrayList)list.get(Team.RED.getDisplayName())).add( UtilVector.subtract(location.toVector(), block.getLocation().toVector())  );
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==8){
		        			if(!list.containsKey(Team.LIGHT_GRAY.getDisplayName())){
			        			list.put(Team.LIGHT_GRAY.getDisplayName(), new ArrayList<Vector>());
			        		}
			        		((ArrayList)list.get(Team.LIGHT_GRAY.getDisplayName())).add( UtilVector.subtract(location.toVector(), block.getLocation().toVector()) );
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==2){
		        			if(!list.containsKey(Team.MAGENTA.getDisplayName())){
			        			list.put(Team.MAGENTA.getDisplayName(), new ArrayList<Vector>());
			        		}
			        		((ArrayList)list.get(Team.MAGENTA.getDisplayName())).add( UtilVector.subtract(location.toVector(), block.getLocation().toVector()) );
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==5){
		        			if(!list.containsKey(Team.LIME.getDisplayName())){
			        			list.put(Team.LIME.getDisplayName(), new ArrayList<Vector>());
			        		}
			        		((ArrayList)list.get(Team.LIME.getDisplayName())).add( UtilVector.subtract(location.toVector(), block.getLocation().toVector()) );
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==12){
		        			if(!list.containsKey(Team.BROWN.getDisplayName())){
			        			list.put(Team.BROWN.getDisplayName(), new ArrayList<Vector>());
			        		}
			        		((ArrayList)list.get(Team.BROWN.getDisplayName())).add( UtilVector.subtract(location.toVector(), block.getLocation().toVector()) );
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==11){
		        			if(!list.containsKey(Team.BLUE.getDisplayName())){
			        			list.put(Team.BLUE.getDisplayName(), new ArrayList<Vector>());
			        		}
			        		((ArrayList)list.get(Team.BLUE.getDisplayName())).add( UtilVector.subtract(location.toVector(), block.getLocation().toVector()) );
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==2){
		        			if(!list.containsKey(Team.PINK.getDisplayName())){
			        			list.put(Team.PINK.getDisplayName(), new ArrayList<Vector>());
			        		}
			        		((ArrayList)list.get(Team.PINK.getDisplayName())).add( UtilVector.subtract(location.toVector(), block.getLocation().toVector()) );
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==7){
		        			if(!list.containsKey(Team.GRAY.getDisplayName())){
			        			list.put(Team.GRAY.getDisplayName(), new ArrayList<Vector>());
			        		}
			        		((ArrayList)list.get(Team.GRAY.getDisplayName())).add( UtilVector.subtract(location.toVector(), block.getLocation().toVector()) );
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==13){
		        			if(!list.containsKey(Team.GREEN.getDisplayName())){
			        			list.put(Team.GREEN.getDisplayName(), new ArrayList<Vector>());
			        		}
			        		((ArrayList)list.get(Team.GREEN.getDisplayName())).add( UtilVector.subtract(location.toVector(), block.getLocation().toVector()) );
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==4){
		        			if(!list.containsKey(Team.YELLOW.getDisplayName())){
			        			list.put(Team.YELLOW.getDisplayName(), new ArrayList<Vector>());
			        		}
			        		((ArrayList)list.get(Team.YELLOW.getDisplayName())).add( UtilVector.subtract(location.toVector(), block.getLocation().toVector()) );
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==10){
		        			if(!list.containsKey(Team.PURPLE.getDisplayName())){
			        			list.put(Team.PURPLE.getDisplayName(), new ArrayList<Vector>());
			        		}
			        		((ArrayList)list.get(Team.PURPLE.getDisplayName())).add( UtilVector.subtract(location.toVector(), block.getLocation().toVector()) );
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==1){
		        			if(!list.containsKey(Team.ORANGE.getDisplayName())){
			        			list.put(Team.ORANGE.getDisplayName(), new ArrayList<Vector>());
			        		}
			        		((ArrayList)list.get(Team.ORANGE.getDisplayName())).add( UtilVector.subtract(location.toVector(), block.getLocation().toVector()) );
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==0){
		        			if(!list.containsKey(Team.WHITE.getDisplayName())){
			        			list.put(Team.WHITE.getDisplayName(), new ArrayList<Vector>());
			        		}
			        		((ArrayList)list.get(Team.WHITE.getDisplayName())).add( UtilVector.subtract(location.toVector(), block.getLocation().toVector()) );
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==9){
		        			if(!list.containsKey(Team.CYAN.getDisplayName())){
			        			list.put(Team.CYAN.getDisplayName(), new ArrayList<Vector>());
			        		}
			        		((ArrayList)list.get(Team.CYAN.getDisplayName())).add( UtilVector.subtract(location.toVector(), block.getLocation().toVector()) );
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==3){
		        			if(!list.containsKey(Team.AQUA.getDisplayName())){
			        			list.put(Team.AQUA.getDisplayName(), new ArrayList<Vector>());
			        		}
			        		((ArrayList)list.get(Team.AQUA.getDisplayName())).add( UtilVector.subtract(location.toVector(), block.getLocation().toVector()) );
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==15){
		        			if(!list.containsKey(Team.BLACK.getDisplayName())){
			        			list.put(Team.BLACK.getDisplayName(), new ArrayList<Vector>());
			        		}
			        		((ArrayList)list.get(Team.BLACK.getDisplayName())).add( UtilVector.subtract(location.toVector(), block.getLocation().toVector()) );
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}
		        	}else if(block.getType()==Material.WOOL&&block.getRelative(BlockFace.UP).getType()==Material.EMERALD_BLOCK){
		        		if(block.getData()==14){
		        			if(!list.containsKey(Team.VILLAGE_RED.getDisplayName())){
			        			list.put(Team.VILLAGE_RED.getDisplayName(), new ArrayList<Vector>());
			        		}
			        		((ArrayList)list.get(Team.VILLAGE_RED.getDisplayName())).add( UtilVector.subtract(location.toVector(), block.getLocation().toVector()) );
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==8){
		        			if(!list.containsKey(Team.VILLAGE_LIGHT_GRAY.getDisplayName())){
			        			list.put(Team.VILLAGE_LIGHT_GRAY.getDisplayName(), new ArrayList<Vector>());
			        		}
			        		((ArrayList)list.get(Team.VILLAGE_LIGHT_GRAY.getDisplayName())).add( UtilVector.subtract(location.toVector(), block.getLocation().toVector()) );
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==2){
		        			if(!list.containsKey(Team.VILLAGE_MAGENTA.getDisplayName())){
			        			list.put(Team.VILLAGE_MAGENTA.getDisplayName(), new ArrayList<Vector>());
			        		}
			        		((ArrayList)list.get(Team.VILLAGE_MAGENTA.getDisplayName())).add( UtilVector.subtract(location.toVector(), block.getLocation().toVector()) );
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==5){
		        			if(!list.containsKey(Team.VILLAGE_LIME.getDisplayName())){
			        			list.put(Team.VILLAGE_LIME.getDisplayName(), new ArrayList<Vector>());
			        		}
			        		((ArrayList)list.get(Team.VILLAGE_LIME.getDisplayName())).add( UtilVector.subtract(location.toVector(), block.getLocation().toVector()) );
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==12){
		        			if(!list.containsKey(Team.VILLAGE_BROWN.getDisplayName())){
			        			list.put(Team.VILLAGE_BROWN.getDisplayName(), new ArrayList<Vector>());
			        		}
			        		((ArrayList)list.get(Team.VILLAGE_BROWN.getDisplayName())).add( UtilVector.subtract(location.toVector(), block.getLocation().toVector()) );
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==11){
		        			if(!list.containsKey(Team.VILLAGE_BLUE.getDisplayName())){
			        			list.put(Team.VILLAGE_BLUE.getDisplayName(), new ArrayList<Vector>());
			        		}
			        		((ArrayList)list.get(Team.VILLAGE_BLUE.getDisplayName())).add( UtilVector.subtract(location.toVector(), block.getLocation().toVector()) );
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==13){
		        			if(!list.containsKey(Team.VILLAGE_GREEN.getDisplayName())){
			        			list.put(Team.VILLAGE_GREEN.getDisplayName(), new ArrayList<Vector>());
			        		}
			        		((ArrayList)list.get(Team.VILLAGE_GREEN.getDisplayName())).add( UtilVector.subtract(location.toVector(), block.getLocation().toVector()) );
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==4){
		        			if(!list.containsKey(Team.VILLAGE_YELLOW.getDisplayName())){
			        			list.put(Team.VILLAGE_YELLOW.getDisplayName(), new ArrayList<Vector>());
			        		}
			        		((ArrayList)list.get(Team.VILLAGE_YELLOW.getDisplayName())).add( UtilVector.subtract(location.toVector(), block.getLocation().toVector()) );
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==10){
		        			if(!list.containsKey(Team.VILLAGE_PURPLE.getDisplayName())){
			        			list.put(Team.VILLAGE_PURPLE.getDisplayName(), new ArrayList<Vector>());
			        		}
			        		((ArrayList)list.get(Team.VILLAGE_PURPLE.getDisplayName())).add( UtilVector.subtract(location.toVector(), block.getLocation().toVector()) );
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==2){
		        			if(!list.containsKey(Team.VILLAGE_PINK.getDisplayName())){
			        			list.put(Team.VILLAGE_PINK.getDisplayName(), new ArrayList<Vector>());
			        		}
			        		((ArrayList)list.get(Team.VILLAGE_PINK.getDisplayName())).add( UtilVector.subtract(location.toVector(), block.getLocation().toVector()) );
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==7){
		        			if(!list.containsKey(Team.VILLAGE_GRAY.getDisplayName())){
			        			list.put(Team.VILLAGE_GRAY.getDisplayName(), new ArrayList<Vector>());
			        		}
			        		((ArrayList)list.get(Team.VILLAGE_GRAY.getDisplayName())).add( UtilVector.subtract(location.toVector(), block.getLocation().toVector()) );
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==1){
		        			if(!list.containsKey(Team.VILLAGE_ORANGE.getDisplayName())){
			        			list.put(Team.VILLAGE_ORANGE.getDisplayName(), new ArrayList<Vector>());
			        		}
			        		((ArrayList)list.get(Team.VILLAGE_ORANGE.getDisplayName())).add( UtilVector.subtract(location.toVector(), block.getLocation().toVector()) );
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==0){
		        			if(!list.containsKey(Team.VILLAGE_WHITE.getDisplayName())){
			        			list.put(Team.VILLAGE_WHITE.getDisplayName(), new ArrayList<Vector>());
			        		}
			        		((ArrayList)list.get(Team.VILLAGE_WHITE.getDisplayName())).add( UtilVector.subtract(location.toVector(), block.getLocation().toVector()) );
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==9){
		        			if(!list.containsKey(Team.VILLAGE_CYAN.getDisplayName())){
			        			list.put(Team.VILLAGE_CYAN.getDisplayName(), new ArrayList<Vector>());
			        		}
			        		((ArrayList)list.get(Team.VILLAGE_CYAN.getDisplayName())).add( UtilVector.subtract(location.toVector(), block.getLocation().toVector()) );
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==3){
		        			if(!list.containsKey(Team.VILLAGE_AQUA.getDisplayName())){
			        			list.put(Team.VILLAGE_AQUA.getDisplayName(), new ArrayList<Vector>());
			        		}
			        		((ArrayList)list.get(Team.VILLAGE_AQUA.getDisplayName())).add( UtilVector.subtract(location.toVector(), block.getLocation().toVector()) );
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==15){
		        			if(!list.containsKey(Team.VILLAGE_BLACK.getDisplayName())){
			        			list.put(Team.VILLAGE_BLACK.getDisplayName(), new ArrayList<Vector>());
			        		}
			        		((ArrayList)list.get(Team.VILLAGE_BLACK.getDisplayName())).add( UtilVector.subtract(location.toVector(), block.getLocation().toVector()) );
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}
		        	}else if(block.getType()==Material.WOOL&&block.getRelative(BlockFace.UP).getType()==Material.BEDROCK){
		        		if(block.getData()==14){
		        			if(!list.containsKey(Team.SHEEP_RED.getDisplayName())){
			        			list.put(Team.SHEEP_RED.getDisplayName(), new ArrayList<Vector>());
			        		}
			        		((ArrayList)list.get(Team.SHEEP_RED.getDisplayName())).add( UtilVector.subtract(location.toVector(), block.getLocation().toVector()) );
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==8){
		        			if(!list.containsKey(Team.SHEEP_LIGHT_GRAY.getDisplayName())){
			        			list.put(Team.SHEEP_LIGHT_GRAY.getDisplayName(), new ArrayList<Vector>());
			        		}
			        		((ArrayList)list.get(Team.SHEEP_LIGHT_GRAY.getDisplayName())).add( UtilVector.subtract(location.toVector(), block.getLocation().toVector()) );
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==2){
		        			if(!list.containsKey(Team.SHEEP_MAGENTA.getDisplayName())){
			        			list.put(Team.SHEEP_MAGENTA.getDisplayName(), new ArrayList<Vector>());
			        		}
			        		((ArrayList)list.get(Team.SHEEP_MAGENTA.getDisplayName())).add( UtilVector.subtract(location.toVector(), block.getLocation().toVector()) );
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==5){
		        			if(!list.containsKey(Team.SHEEP_LIME.getDisplayName())){
			        			list.put(Team.SHEEP_LIME.getDisplayName(), new ArrayList<Vector>());
			        		}
			        		((ArrayList)list.get(Team.SHEEP_LIME.getDisplayName())).add( UtilVector.subtract(location.toVector(), block.getLocation().toVector()) );
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==12){
		        			if(!list.containsKey(Team.SHEEP_BROWN.getDisplayName())){
			        			list.put(Team.SHEEP_BROWN.getDisplayName(), new ArrayList<Vector>());
			        		}
			        		((ArrayList)list.get(Team.SHEEP_BROWN.getDisplayName())).add( UtilVector.subtract(location.toVector(), block.getLocation().toVector()) );
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==11){
		        			if(!list.containsKey(Team.SHEEP_BLUE.getDisplayName())){
			        			list.put(Team.SHEEP_BLUE.getDisplayName(), new ArrayList<Vector>());
			        		}
			        		((ArrayList)list.get(Team.SHEEP_BLUE.getDisplayName())).add( UtilVector.subtract(location.toVector(), block.getLocation().toVector()) );
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==13){
		        			if(!list.containsKey(Team.SHEEP_GREEN.getDisplayName())){
			        			list.put(Team.SHEEP_GREEN.getDisplayName(), new ArrayList<Vector>());
			        		}
			        		((ArrayList)list.get(Team.SHEEP_GREEN.getDisplayName())).add( UtilVector.subtract(location.toVector(), block.getLocation().toVector()) );
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==4){
		        			if(!list.containsKey(Team.SHEEP_YELLOW.getDisplayName())){
			        			list.put(Team.SHEEP_YELLOW.getDisplayName(), new ArrayList<Vector>());
			        		}
			        		((ArrayList)list.get(Team.SHEEP_YELLOW.getDisplayName())).add( UtilVector.subtract(location.toVector(), block.getLocation().toVector()) );
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==1){
		        			if(!list.containsKey(Team.SHEEP_ORANGE.getDisplayName())){
			        			list.put(Team.SHEEP_ORANGE.getDisplayName(), new ArrayList<Vector>());
			        		}
			        		((ArrayList)list.get(Team.SHEEP_ORANGE.getDisplayName())).add( UtilVector.subtract(location.toVector(), block.getLocation().toVector()) );
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==10){
		        			if(!list.containsKey(Team.SHEEP_PURPLE.getDisplayName())){
			        			list.put(Team.SHEEP_PURPLE.getDisplayName(), new ArrayList<Vector>());
			        		}
			        		((ArrayList)list.get(Team.SHEEP_PURPLE.getDisplayName())).add( UtilVector.subtract(location.toVector(), block.getLocation().toVector()) );
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==2){
		        			if(!list.containsKey(Team.SHEEP_PINK.getDisplayName())){
			        			list.put(Team.SHEEP_PINK.getDisplayName(), new ArrayList<Vector>());
			        		}
			        		((ArrayList)list.get(Team.SHEEP_PINK.getDisplayName())).add( UtilVector.subtract(location.toVector(), block.getLocation().toVector()) );
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==7){
		        			if(!list.containsKey(Team.SHEEP_GRAY.getDisplayName())){
			        			list.put(Team.SHEEP_GRAY.getDisplayName(), new ArrayList<Vector>());
			        		}
			        		((ArrayList)list.get(Team.SHEEP_GRAY.getDisplayName())).add( UtilVector.subtract(location.toVector(), block.getLocation().toVector()) );
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==0){
		        			if(!list.containsKey(Team.SHEEP_WHITE.getDisplayName())){
			        			list.put(Team.SHEEP_WHITE.getDisplayName(), new ArrayList<Vector>());
			        		}
			        		((ArrayList)list.get(Team.SHEEP_WHITE.getDisplayName())).add( UtilVector.subtract(location.toVector(), block.getLocation().toVector()) );
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==9){
		        			if(!list.containsKey(Team.SHEEP_CYAN.getDisplayName())){
			        			list.put(Team.SHEEP_CYAN.getDisplayName(), new ArrayList<Vector>());
			        		}
			        		((ArrayList)list.get(Team.SHEEP_CYAN.getDisplayName())).add( UtilVector.subtract(location.toVector(), block.getLocation().toVector()) );
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==3){
		        			if(!list.containsKey(Team.SHEEP_AQUA.getDisplayName())){
			        			list.put(Team.SHEEP_AQUA.getDisplayName(), new ArrayList<Vector>());
			        		}
			        		((ArrayList)list.get(Team.SHEEP_AQUA.getDisplayName())).add( UtilVector.subtract(location.toVector(), block.getLocation().toVector()) );
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==15){
		        			if(!list.containsKey(Team.SHEEP_BLACK.getDisplayName())){
			        			list.put(Team.SHEEP_BLACK.getDisplayName(), new ArrayList<Vector>());
			        		}
			        		((ArrayList)list.get(Team.SHEEP_BLACK.getDisplayName())).add( UtilVector.subtract(location.toVector(), block.getLocation().toVector()) );
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}
		        	}else if(block.getRelative(BlockFace.UP).getType()==Material.DIAMOND_BLOCK){
		        			if(block.getType()==Material.GOLD_BLOCK){
			        			if(!list.containsKey(Team.GOLD.getDisplayName())){
				        			list.put(Team.GOLD.getDisplayName(), new ArrayList<Vector>());
				        		}
				        		((ArrayList)list.get(Team.GOLD.getDisplayName())).add( UtilVector.subtract(location.toVector(), block.getLocation().toVector()) );
				        		block.setTypeId(0);
				        		block.getRelative(BlockFace.UP).setTypeId(0);	
		        			}else if(block.getType()==Material.DIAMOND_BLOCK){
			        			if(!list.containsKey(Team.DIAMOND.getDisplayName())){
				        			list.put(Team.DIAMOND.getDisplayName(), new ArrayList<Vector>());
				        		}
				        		((ArrayList)list.get(Team.DIAMOND.getDisplayName())).add( UtilVector.subtract(location.toVector(), block.getLocation().toVector()) );
				        		block.setTypeId(0);
				        		block.getRelative(BlockFace.UP).setTypeId(0);	
		        			}else if(block.getType()==Material.IRON_BLOCK){
			        			if(!list.containsKey(Team.SILBER.getDisplayName())){
				        			list.put(Team.SILBER.getDisplayName(), new ArrayList<Vector>());
				        		}
				        		((ArrayList)list.get(Team.SILBER.getDisplayName())).add( UtilVector.subtract(location.toVector(), block.getLocation().toVector()) );
				        		block.setTypeId(0);
				        		block.getRelative(BlockFace.UP).setTypeId(0);	
		        			}else if(block.getType()==Material.COAL_BLOCK){
			        			if(!list.containsKey(Team.BRONZE.getDisplayName())){
				        			list.put(Team.BRONZE.getDisplayName(), new ArrayList<Vector>());
				        		}
				        		((ArrayList)list.get(Team.BRONZE.getDisplayName())).add( UtilVector.subtract(location.toVector(), block.getLocation().toVector()) );
				        		block.setTypeId(0);
				        		block.getRelative(BlockFace.UP).setTypeId(0);	
		        			}
		        	}
				}  
			}
		}
		
		if(list.isEmpty()){
			System.out.println("[WorldSetUp] Es wurde nichts gefunden!");
			return;
		}
		      
		try {
			FileWriter fstream= new FileWriter(location.getWorld().getName() + File.separator + "SchematicConfig.dat");
			BufferedWriter out = new BufferedWriter(fstream);  
			
			out.write("MAP_NAME:"+MapName);
			out.write("\n");
			if(list.containsKey(Team.SOLO.getDisplayName())){
				out.write("SOLO:"+VectorListTOStringList(list.get(Team.SOLO.getDisplayName())));
				out.write("\n");
			}
			if(list.containsKey(Team.LIGHT_GRAY.getDisplayName())){
				out.write("LIGHT_GRAY:"+VectorListTOStringList(list.get(Team.LIGHT_GRAY.getDisplayName())));
				out.write("\n");
			}
			if(list.containsKey(Team.MAGENTA.getDisplayName())){
				out.write("MAGENTA:"+VectorListTOStringList(list.get(Team.MAGENTA.getDisplayName())));
				out.write("\n");
			}
			if(list.containsKey(Team.LIME.getDisplayName())){
				out.write("LIME:"+VectorListTOStringList(list.get(Team.LIME.getDisplayName())));
				out.write("\n");
			}
			if(list.containsKey(Team.BROWN.getDisplayName())){
				out.write("BROWN:"+VectorListTOStringList(list.get(Team.BROWN.getDisplayName())));
				out.write("\n");
			}
			if(list.containsKey(Team.RED.getDisplayName())){
				out.write("RED:"+VectorListTOStringList(list.get(Team.RED.getDisplayName())));
				out.write("\n");
			}
			if(list.containsKey(Team.PINK.getDisplayName())){
				out.write("PINK:"+VectorListTOStringList(list.get(Team.PINK.getDisplayName())));
				out.write("\n");
			}
			if(list.containsKey(Team.PURPLE.getDisplayName())){
				out.write("PURPLE:"+VectorListTOStringList(list.get(Team.PURPLE.getDisplayName())));
				out.write("\n");
			}
			if(list.containsKey(Team.ORANGE.getDisplayName())){
				out.write("ORANGE:"+VectorListTOStringList(list.get(Team.ORANGE.getDisplayName())));
				out.write("\n");
			}
			if(list.containsKey(Team.GREEN.getDisplayName())){
				out.write("GREEN:"+VectorListTOStringList(list.get(Team.GREEN.getDisplayName())));
				out.write("\n");
			}
			if(list.containsKey(Team.GRAY.getDisplayName())){
				out.write("GRAY:"+VectorListTOStringList(list.get(Team.GRAY.getDisplayName())));
				out.write("\n");
			}
			if(list.containsKey(Team.YELLOW.getDisplayName())){
				out.write("YELLOW:"+VectorListTOStringList(list.get(Team.YELLOW.getDisplayName())));
				out.write("\n");
			}
			if(list.containsKey(Team.BLUE.getDisplayName())){
				out.write("BLUE:"+VectorListTOStringList(list.get(Team.BLUE.getDisplayName())));
				out.write("\n");
			}
			if(list.containsKey(Team.CYAN.getDisplayName())){
				out.write("CYAN:"+VectorListTOStringList(list.get(Team.CYAN.getDisplayName())));
				out.write("\n");
			}
			if(list.containsKey(Team.AQUA.getDisplayName())){
				out.write("AQUA:"+VectorListTOStringList(list.get(Team.AQUA.getDisplayName())));
				out.write("\n");
			}
			if(list.containsKey(Team.WHITE.getDisplayName())){
				out.write("WHITE:"+VectorListTOStringList(list.get(Team.WHITE.getDisplayName())));
				out.write("\n");
			}
			if(list.containsKey(Team.BLACK.getDisplayName())){
				out.write("BLACK:"+VectorListTOStringList(list.get(Team.BLACK.getDisplayName())));
				out.write("\n");
			}
			if(list.containsKey(Team.BRONZE.getDisplayName())){
				out.write("BRONZE:"+VectorListTOStringList(list.get(Team.BRONZE.getDisplayName())));
				out.write("\n");
			}
			if(list.containsKey(Team.SILBER.getDisplayName())){
				out.write("SILBER:"+VectorListTOStringList(list.get(Team.SILBER.getDisplayName())));
				out.write("\n");
			}
			if(list.containsKey(Team.DIAMOND.getDisplayName())){
				out.write("DIAMOND:"+VectorListTOStringList(list.get(Team.GOLD.getDisplayName())));
				out.write("\n");
			}
			if(list.containsKey(Team.GOLD.getDisplayName())){
				out.write("GOLD:"+VectorListTOStringList(list.get(Team.GOLD.getDisplayName())));
				out.write("\n");
			}
			if(list.containsKey(Team.VILLAGE_LIGHT_GRAY.getDisplayName())){
				out.write("VILLAGE_LIGHT_GRAY:"+VectorListTOStringList(list.get(Team.VILLAGE_LIGHT_GRAY.getDisplayName())));
				out.write("\n");
			}
			if(list.containsKey(Team.VILLAGE_MAGENTA.getDisplayName())){
				out.write("VILLAGE_MAGENTA:"+VectorListTOStringList(list.get(Team.VILLAGE_MAGENTA.getDisplayName())));
				out.write("\n");
			}
			if(list.containsKey(Team.VILLAGE_LIME.getDisplayName())){
				out.write("VILLAGE_LIME:"+VectorListTOStringList(list.get(Team.VILLAGE_LIME.getDisplayName())));
				out.write("\n");
			}
			if(list.containsKey(Team.VILLAGE_BROWN.getDisplayName())){
				out.write("VILLAGE_BROWN:"+VectorListTOStringList(list.get(Team.VILLAGE_BROWN.getDisplayName())));
				out.write("\n");
			}
			if(list.containsKey(Team.VILLAGE_BLUE.getDisplayName())){
				out.write("VILLAGE_BLUE:"+VectorListTOStringList(list.get(Team.VILLAGE_BLUE.getDisplayName())));
				out.write("\n");
			}
			if(list.containsKey(Team.VILLAGE_GREEN.getDisplayName())){
				out.write("VILLAGE_GREEN:"+VectorListTOStringList(list.get(Team.VILLAGE_GREEN.getDisplayName())));
				out.write("\n");
			}
			if(list.containsKey(Team.VILLAGE_RED.getDisplayName())){
				out.write("VILLAGE_RED:"+VectorListTOStringList(list.get(Team.VILLAGE_RED.getDisplayName())));
				out.write("\n");
			}
			if(list.containsKey(Team.VILLAGE_YELLOW.getDisplayName())){
				out.write("VILLAGE_YELLOW:"+VectorListTOStringList(list.get(Team.VILLAGE_YELLOW.getDisplayName())));
				out.write("\n");
			}
			if(list.containsKey(Team.VILLAGE_ORANGE.getDisplayName())){
				out.write("VILLAGE_ORANGE:"+VectorListTOStringList(list.get(Team.VILLAGE_ORANGE.getDisplayName())));
				out.write("\n");
			}
			if(list.containsKey(Team.VILLAGE_PINK.getDisplayName())){
				out.write("VILLAGE_PINK:"+VectorListTOStringList(list.get(Team.VILLAGE_PINK.getDisplayName())));
				out.write("\n");
			}
			if(list.containsKey(Team.VILLAGE_PURPLE.getDisplayName())){
				out.write("VILLAGE_PURPLE:"+VectorListTOStringList(list.get(Team.VILLAGE_PURPLE.getDisplayName())));
				out.write("\n");
			}
			if(list.containsKey(Team.VILLAGE_GRAY.getDisplayName())){
				out.write("VILLAGE_GRAY:"+VectorListTOStringList(list.get(Team.VILLAGE_GRAY.getDisplayName())));
				out.write("\n");
			}
			if(list.containsKey(Team.SHEEP_LIGHT_GRAY.getDisplayName())){
				out.write("SHEEP_LIGHT_GRAY:"+VectorListTOStringList(list.get(Team.SHEEP_LIGHT_GRAY.getDisplayName())));
				out.write("\n");
			}
			if(list.containsKey(Team.SHEEP_MAGENTA.getDisplayName())){
				out.write("SHEEP_MAGENTA:"+VectorListTOStringList(list.get(Team.SHEEP_MAGENTA.getDisplayName())));
				out.write("\n");
			}
			if(list.containsKey(Team.SHEEP_LIME.getDisplayName())){
				out.write("SHEEP_LIME:"+VectorListTOStringList(list.get(Team.SHEEP_LIME.getDisplayName())));
				out.write("\n");
			}
			if(list.containsKey(Team.SHEEP_BROWN.getDisplayName())){
				out.write("SHEEP_BROWN:"+VectorListTOStringList(list.get(Team.SHEEP_BROWN.getDisplayName())));
				out.write("\n");
			}
			if(list.containsKey(Team.SHEEP_BLUE.getDisplayName())){
				out.write("SHEEP_BLUE:"+VectorListTOStringList(list.get(Team.SHEEP_BLUE.getDisplayName())));
				out.write("\n");
			}
			if(list.containsKey(Team.SHEEP_GREEN.getDisplayName())){
				out.write("SHEEP_GREEN:"+VectorListTOStringList(list.get(Team.SHEEP_GREEN.getDisplayName())));
				out.write("\n");
			}
			if(list.containsKey(Team.SHEEP_RED.getDisplayName())){
				out.write("SHEEP_RED:"+VectorListTOStringList(list.get(Team.SHEEP_RED.getDisplayName())));
				out.write("\n");
			}
			if(list.containsKey(Team.SHEEP_YELLOW.getDisplayName())){
				out.write("SHEEP_YELLOW:"+VectorListTOStringList(list.get(Team.SHEEP_YELLOW.getDisplayName())));
				out.write("\n");
			}
			if(list.containsKey(Team.SHEEP_ORANGE.getDisplayName())){
				out.write("SHEEP_ORANGE:"+VectorListTOStringList(list.get(Team.SHEEP_ORANGE.getDisplayName())));
				out.write("\n");
			}
			if(list.containsKey(Team.SHEEP_PINK.getDisplayName())){
				out.write("SHEEP_PINK:"+VectorListTOStringList(list.get(Team.SHEEP_PINK.getDisplayName())));
				out.write("\n");
			}
			if(list.containsKey(Team.SHEEP_PURPLE.getDisplayName())){
				out.write("SHEEP_PURPLE:"+VectorListTOStringList(list.get(Team.SHEEP_PURPLE.getDisplayName())));
				out.write("\n");
			}
			if(list.containsKey(Team.SHEEP_GRAY.getDisplayName())){
				out.write("SHEEP_GRAY:"+VectorListTOStringList(list.get(Team.SHEEP_GRAY.getDisplayName())));
				out.write("\n");
			}
			if(list.containsKey(Team.SHEEP_CYAN.getDisplayName())){
				out.write("SHEEP_CYAN:"+VectorListTOStringList(list.get(Team.SHEEP_CYAN.getDisplayName())));
				out.write("\n");
			}
			if(list.containsKey(Team.SHEEP_AQUA.getDisplayName())){
				out.write("SHEEP_AQUA:"+VectorListTOStringList(list.get(Team.SHEEP_AQUA.getDisplayName())));
				out.write("\n");
			}
			if(list.containsKey(Team.SHEEP_WHITE.getDisplayName())){
				out.write("SHEEP_WHITE:"+VectorListTOStringList(list.get(Team.SHEEP_WHITE.getDisplayName())));
				out.write("\n");
			}
			if(list.containsKey(Team.SHEEP_BLACK.getDisplayName())){
				out.write("SHEEP_BLACK:"+VectorListTOStringList(list.get(Team.SHEEP_BLACK.getDisplayName())));
				out.write("\n");
			}
			if(list.containsKey(Team.VILLAGE_CYAN.getDisplayName())){
				out.write("VILLAGE_CYAN:"+VectorListTOStringList(list.get(Team.VILLAGE_CYAN.getDisplayName())));
				out.write("\n");
			}
			if(list.containsKey(Team.VILLAGE_AQUA.getDisplayName())){
				out.write("VILLAGE_AQUA:"+VectorListTOStringList(list.get(Team.VILLAGE_AQUA.getDisplayName())));
				out.write("\n");
			}
			if(list.containsKey(Team.VILLAGE_WHITE.getDisplayName())){
				out.write("VILLAGE_WHITE:"+VectorListTOStringList(list.get(Team.VILLAGE_WHITE.getDisplayName())));
				out.write("\n");
			}
			if(list.containsKey(Team.VILLAGE_BLACK.getDisplayName())){
				out.write("VILLAGE_BLACK:"+VectorListTOStringList(list.get(Team.VILLAGE_BLACK.getDisplayName())));
				out.write("\n");
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	      
		System.out.println("[WorldSetUp] Schematic Data Saved.");
	}
	
	public static String VectorListTOStringList(ArrayList<Vector> list){
		if(list==null||list.isEmpty()){
			System.err.println("[WorldData]: LIST EMPTY!");
			return "";
		}
		String l="";
		for(Vector v : list){
			l=l+v.getX()+";"+v.getY()+";"+v.getZ()+",";
		}
		return l.substring(0, l.length()-1);
	}
	
	public static ArrayList<Location> StringListTOVectorList(Location paste, String s){
		ArrayList<Location> list = new ArrayList<Location>();
		//X;Y;Z;,X;Y;Z;,X;Y;Z;,X;Y;Z;
		String[] cordi;
		for(String l : s.split(",")){
			cordi = l.split(";");
			list.add( UtilVector.add(new Vector(Double.valueOf(cordi[0]),Double.valueOf(cordi[1]),Double.valueOf(cordi[2])), paste.toVector()).toLocation(paste.getWorld()) );
		}
		return list;
	}
	
}