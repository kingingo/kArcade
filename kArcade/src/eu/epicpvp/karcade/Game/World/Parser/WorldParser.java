package eu.epicpvp.karcade.Game.World.Parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;

import eu.epicpvp.kcore.Enum.Team;

public class WorldParser {
	
	public static void Scan(Location caller,String MapName){
		System.out.println("[WorldSetUp] Scannen der Bloecke...");
		Block block;
		int processed = 0;
		HashMap<String,ArrayList<Location>> list = new HashMap<>();
		for (int x = -600; x < 600; x++){
			for (int z = -600; z < 600; z++){
				for (int y = 0; y < 256; y++){
		        	processed++;
		        	if (processed % 20000000 == 0) {
		        		System.out.println("[WorldSetUp] Prozess:" + processed);
		            }
		        	block = caller.getWorld().getBlockAt(caller.getBlockX() + x, y, caller.getBlockZ() + z);
		        	
		        	if(block.getType()==Material.MELON_BLOCK&&block.getRelative(BlockFace.UP).getType()==Material.REDSTONE_BLOCK){
		        		if(!list.containsKey(Team.SOLO.Name())){
		        			list.put(Team.SOLO.Name(), new ArrayList<Location>());
		        		}
		        		((ArrayList)list.get(Team.SOLO.Name())).add(block.getLocation());
		        		block.setTypeId(0);
		        		block.getRelative(BlockFace.UP).setTypeId(0);
		        	}else if(block.getType()==Material.WOOL&&block.getRelative(BlockFace.UP).getType()==Material.REDSTONE_BLOCK
		        			&&block.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getState() instanceof Sign){
	        					Sign sign = (Sign)block.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getState();
				        		Team team=null;
		        				if(block.getData()==0){
			        				
			        				switch(Integer.valueOf(sign.getLine(0).replaceAll(" ", ""))){
				        				case 1: team=Team.TEAM_1; break;
				        				case 2: team=Team.TEAM_2; break;
				        				case 3: team=Team.TEAM_3; break;
				        				case 4: team=Team.TEAM_4; break;
				        				case 5: team=Team.TEAM_5; break;
				        				case 6: team=Team.TEAM_6; break;
				        				case 7: team=Team.TEAM_7; break;
				        				case 8: team=Team.TEAM_8; break;
				        				case 9: team=Team.TEAM_9; break;
				        				case 10: team=Team.TEAM_10; break;
				        				case 11: team=Team.TEAM_11; break;
				        				case 12: team=Team.TEAM_12; break;
				        				case 13: team=Team.TEAM_13; break;
				        				case 14: team=Team.TEAM_14; break;
				        				case 15: team=Team.TEAM_15; break;
				        				case 16: team=Team.TEAM_16; break;
				        				case 17: team=Team.TEAM_17; break;
				        				case 18: team=Team.TEAM_18; break;
				        				case 19: team=Team.TEAM_19; break;
				        				case 20: team=Team.TEAM_20; break;
				        				case 21: team=Team.TEAM_21; break;
				        				case 22: team=Team.TEAM_22; break;
				        				case 23: team=Team.TEAM_23; break;
				        				case 24: team=Team.TEAM_24; break;
				        				case 25: team=Team.TEAM_25; break;
				        				case 26: team=Team.TEAM_26; break;
				        				case 27: team=Team.TEAM_27; break;
				        				case 28: team=Team.TEAM_28; break;
				        				case 29: team=Team.TEAM_29; break;
				        				case 30: team=Team.TEAM_30; break;
				        				case 31: team=Team.TEAM_31; break;
				        				case 32: team=Team.TEAM_32; break;
			        				}
			        				
			        				if(team!=null){
			        					if(!list.containsKey(team.Name())){
						        			list.put(team.Name(), new ArrayList<Location>());
						        		}
						        		((ArrayList)list.get(team.Name())).add(block.getLocation());
			        				}
		        				}else if(block.getData()==5){
			        				
			        				switch(Integer.valueOf(sign.getLine(0).replaceAll(" ", ""))){
				        				case 1: team=Team.TEAM_POINT_1; break;
				        				case 2: team=Team.TEAM_POINT_2; break;
				        				case 3: team=Team.TEAM_POINT_3; break;
				        				case 4: team=Team.TEAM_POINT_4; break;
				        				case 5: team=Team.TEAM_POINT_5; break;
				        				case 6: team=Team.TEAM_POINT_6; break;
				        				case 7: team=Team.TEAM_POINT_7; break;
				        				case 8: team=Team.TEAM_POINT_8; break;
				        				case 9: team=Team.TEAM_POINT_9; break;
				        				case 10: team=Team.TEAM_POINT_10; break;
				        				case 11: team=Team.TEAM_POINT_11; break;
				        				case 12: team=Team.TEAM_POINT_12; break;
				        				case 13: team=Team.TEAM_POINT_13; break;
				        				case 14: team=Team.TEAM_POINT_14; break;
				        				case 15: team=Team.TEAM_POINT_15; break;
				        				case 16: team=Team.TEAM_POINT_16; break;
				        				case 17: team=Team.TEAM_POINT_17; break;
				        				case 18: team=Team.TEAM_POINT_18; break;
				        				case 19: team=Team.TEAM_POINT_19; break;
				        				case 20: team=Team.TEAM_POINT_20; break;
				        				case 21: team=Team.TEAM_POINT_21; break;
				        				case 22: team=Team.TEAM_POINT_22; break;
				        				case 23: team=Team.TEAM_POINT_23; break;
				        				case 24: team=Team.TEAM_POINT_24; break;
				        				case 25: team=Team.TEAM_POINT_25; break;
				        				case 26: team=Team.TEAM_POINT_26; break;
				        				case 27: team=Team.TEAM_POINT_27; break;
				        				case 28: team=Team.TEAM_POINT_28; break;
				        				case 29: team=Team.TEAM_POINT_29; break;
				        				case 30: team=Team.TEAM_POINT_30; break;
				        				case 31: team=Team.TEAM_POINT_31; break;
				        				case 32: team=Team.TEAM_POINT_32; break;
			        				}
			        				
			        				if(team!=null){
			        					if(!list.containsKey(team.Name())){
						        			list.put(team.Name(), new ArrayList<Location>());
						        		}
						        		((ArrayList)list.get(team.Name())).add(block.getLocation());
			        				}
		        				}
		        				

				        		block.getRelative(BlockFace.UP).getRelative(BlockFace.UP).setTypeId(0);
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
		        		}else if(block.getData()==2||block.getData()==6){
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
		        		}else if(block.getData()==15){
		        			if(!list.containsKey(Team.BLACK.Name())){
			        			list.put(Team.BLACK.Name(), new ArrayList<Location>());
			        		}
			        		((ArrayList)list.get(Team.BLACK.Name())).add(block.getLocation());
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==8){
		        			if(!list.containsKey(Team.LIGHT_GRAY.Name())){
			        			list.put(Team.LIGHT_GRAY.Name(), new ArrayList<Location>());
			        		}
			        		((ArrayList)list.get(Team.LIGHT_GRAY.Name())).add(block.getLocation());
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==12){
		        			if(!list.containsKey(Team.BROWN.Name())){
			        			list.put(Team.BROWN.Name(), new ArrayList<Location>());
			        		}
			        		((ArrayList)list.get(Team.BROWN.Name())).add(block.getLocation());
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==2){
		        			if(!list.containsKey(Team.MAGENTA.Name())){
			        			list.put(Team.MAGENTA.Name(), new ArrayList<Location>());
			        		}
			        		((ArrayList)list.get(Team.MAGENTA.Name())).add(block.getLocation());
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==5){
		        			if(!list.containsKey(Team.LIME.Name())){
			        			list.put(Team.LIME.Name(), new ArrayList<Location>());
			        		}
			        		((ArrayList)list.get(Team.LIME.Name())).add(block.getLocation());
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==13){
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
		        		}
		        	}else if(block.getType()==Material.WOOL&&block.getRelative(BlockFace.UP).getType()==Material.EMERALD_BLOCK){ 
		        		if(block.getData()==14){
		        			if(!list.containsKey(Team.VILLAGE_RED.Name())){
			        			list.put(Team.VILLAGE_RED.Name(), new ArrayList<Location>());
			        		}
			        		((ArrayList)list.get(Team.VILLAGE_RED.Name())).add(block.getLocation());
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==8){
		        			if(!list.containsKey(Team.VILLAGE_LIGHT_GRAY.Name())){
			        			list.put(Team.VILLAGE_LIGHT_GRAY.Name(), new ArrayList<Location>());
			        		}
			        		((ArrayList)list.get(Team.VILLAGE_LIGHT_GRAY.Name())).add(block.getLocation());
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==12){
		        			if(!list.containsKey(Team.VILLAGE_BROWN.Name())){
			        			list.put(Team.VILLAGE_BROWN.Name(), new ArrayList<Location>());
			        		}
			        		((ArrayList)list.get(Team.VILLAGE_BROWN.Name())).add(block.getLocation());
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==2){
		        			if(!list.containsKey(Team.VILLAGE_MAGENTA.Name())){
			        			list.put(Team.VILLAGE_MAGENTA.Name(), new ArrayList<Location>());
			        		}
			        		((ArrayList)list.get(Team.VILLAGE_MAGENTA.Name())).add(block.getLocation());
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==5){
		        			if(!list.containsKey(Team.VILLAGE_LIME.Name())){
			        			list.put(Team.VILLAGE_LIME.Name(), new ArrayList<Location>());
			        		}
			        		((ArrayList)list.get(Team.VILLAGE_LIME.Name())).add(block.getLocation());
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==11){
		        			if(!list.containsKey(Team.VILLAGE_BLUE.Name())){
			        			list.put(Team.VILLAGE_BLUE.Name(), new ArrayList<Location>());
			        		}
			        		((ArrayList)list.get(Team.VILLAGE_BLUE.Name())).add(block.getLocation());
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==13){
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
		        		}else if(block.getData()==2||block.getData()==6){
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
		        		}else if(block.getData()==9){
		        			if(!list.containsKey(Team.VILLAGE_CYAN.Name())){
			        			list.put(Team.VILLAGE_CYAN.Name(), new ArrayList<Location>());
			        		}
			        		((ArrayList)list.get(Team.VILLAGE_CYAN.Name())).add(block.getLocation());
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==3){
		        			if(!list.containsKey(Team.VILLAGE_AQUA.Name())){
			        			list.put(Team.VILLAGE_AQUA.Name(), new ArrayList<Location>());
			        		}
			        		((ArrayList)list.get(Team.VILLAGE_AQUA.Name())).add(block.getLocation());
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==15){
		        			if(!list.containsKey(Team.VILLAGE_BLACK.Name())){
			        			list.put(Team.VILLAGE_BLACK.Name(), new ArrayList<Location>());
			        		}
			        		((ArrayList)list.get(Team.VILLAGE_BLACK.Name())).add(block.getLocation());
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==0){
		        			if(!list.containsKey(Team.VILLAGE_WHITE.Name())){
			        			list.put(Team.VILLAGE_WHITE.Name(), new ArrayList<Location>());
			        		}
			        		((ArrayList)list.get(Team.VILLAGE_WHITE.Name())).add(block.getLocation());
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
		        		}else if(block.getData()==8){
		        			if(!list.containsKey(Team.SHEEP_LIGHT_GRAY.Name())){
			        			list.put(Team.SHEEP_LIGHT_GRAY.Name(), new ArrayList<Location>());
			        		}
			        		((ArrayList)list.get(Team.SHEEP_LIGHT_GRAY.Name())).add(block.getLocation());
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==12){
		        			if(!list.containsKey(Team.SHEEP_BROWN.Name())){
			        			list.put(Team.SHEEP_BROWN.Name(), new ArrayList<Location>());
			        		}
			        		((ArrayList)list.get(Team.SHEEP_BROWN.Name())).add(block.getLocation());
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==2){
		        			if(!list.containsKey(Team.SHEEP_MAGENTA.Name())){
			        			list.put(Team.SHEEP_MAGENTA.Name(), new ArrayList<Location>());
			        		}
			        		((ArrayList)list.get(Team.SHEEP_MAGENTA.Name())).add(block.getLocation());
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==5){
		        			if(!list.containsKey(Team.SHEEP_LIME.Name())){
			        			list.put(Team.SHEEP_LIME.Name(), new ArrayList<Location>());
			        		}
			        		((ArrayList)list.get(Team.SHEEP_LIME.Name())).add(block.getLocation());
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==11){
		        			if(!list.containsKey(Team.SHEEP_BLUE.Name())){
			        			list.put(Team.SHEEP_BLUE.Name(), new ArrayList<Location>());
			        		}
			        		((ArrayList)list.get(Team.SHEEP_BLUE.Name())).add(block.getLocation());
			        		block.setTypeId(0);
			        		block.getRelative(BlockFace.UP).setTypeId(0);
		        		}else if(block.getData()==13){
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
		        		}else if(block.getData()==2||block.getData()==6){
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
			System.out.println("[WorldSetUp] Leider konnte nichts gefunden werden!");
			return;
		}
		      
		save(caller.getWorld(),MapName,list);
		System.out.println("[WorldSetUp] Die Locations wurden in der Welt erfolgreich gespeichert.");
	}
	
	public static void save(World world,String MapName,HashMap<String,ArrayList<Location>> list){
		try {
			FileWriter fstream= new FileWriter(world.getName() + File.separator + "WorldConfig.dat");
			BufferedWriter out = new BufferedWriter(fstream);  
			
			out.write("MAP_NAME:"+MapName);
			out.write("\n");
			if(list.containsKey(Team.SOLO.Name())){
				out.write("SOLO:"+LocListTOStringList(list.get(Team.SOLO.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.MAGENTA.Name())){
				out.write("MAGENTA:"+LocListTOStringList(list.get(Team.MAGENTA.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.LIME.Name())){
				out.write("LIME:"+LocListTOStringList(list.get(Team.LIME.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.LIGHT_GRAY.Name())){
				out.write("LIGHT_GRAY:"+LocListTOStringList(list.get(Team.LIGHT_GRAY.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.BROWN.Name())){
				out.write("BROWN:"+LocListTOStringList(list.get(Team.BROWN.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.RED.Name())){
				out.write("RED:"+LocListTOStringList(list.get(Team.RED.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.WHITE.Name())){
				out.write("WHITE:"+LocListTOStringList(list.get(Team.WHITE.Name())));
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
			if(list.containsKey(Team.BLACK.Name())){
				out.write("BLACK:"+LocListTOStringList(list.get(Team.BLACK.Name())));
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
			if(list.containsKey(Team.VILLAGE_MAGENTA.Name())){
				out.write("VILLAGE_MAGENTA:"+LocListTOStringList(list.get(Team.VILLAGE_MAGENTA.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.VILLAGE_LIME.Name())){
				out.write("VILLAGE_LIME:"+LocListTOStringList(list.get(Team.VILLAGE_LIME.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.VILLAGE_LIGHT_GRAY.Name())){
				out.write("VILLAGE_LIGHT_GRAY:"+LocListTOStringList(list.get(Team.VILLAGE_LIGHT_GRAY.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.VILLAGE_BROWN.Name())){
				out.write("VILLAGE_BROWN:"+LocListTOStringList(list.get(Team.VILLAGE_BROWN.Name())));
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
			if(list.containsKey(Team.VILLAGE_BLACK.Name())){
				out.write("VILLAGE_BLACK:"+LocListTOStringList(list.get(Team.VILLAGE_BLACK.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.VILLAGE_AQUA.Name())){
				out.write("VILLAGE_AQUA:"+LocListTOStringList(list.get(Team.VILLAGE_AQUA.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.VILLAGE_CYAN.Name())){
				out.write("VILLAGE_CYAN:"+LocListTOStringList(list.get(Team.VILLAGE_CYAN.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.VILLAGE_WHITE.Name())){
				out.write("VILLAGE_WHITE:"+LocListTOStringList(list.get(Team.VILLAGE_WHITE.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.SHEEP_MAGENTA.Name())){
				out.write("SHEEP_MAGENTA:"+LocListTOStringList(list.get(Team.SHEEP_MAGENTA.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.SHEEP_LIME.Name())){
				out.write("SHEEP_LIME:"+LocListTOStringList(list.get(Team.SHEEP_LIME.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.SHEEP_LIGHT_GRAY.Name())){
				out.write("SHEEP_LIGHT_GRAY:"+LocListTOStringList(list.get(Team.SHEEP_LIGHT_GRAY.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.SHEEP_BROWN.Name())){
				out.write("SHEEP_BROWN:"+LocListTOStringList(list.get(Team.SHEEP_BROWN.Name())));
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
			if(list.containsKey(Team.TEAM_1.Name())){
				out.write("TEAM_1:"+LocListTOStringList(list.get(Team.TEAM_1.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_2.Name())){
				out.write("TEAM_2:"+LocListTOStringList(list.get(Team.TEAM_2.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_3.Name())){
				out.write("TEAM_3:"+LocListTOStringList(list.get(Team.TEAM_3.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_4.Name())){
				out.write("TEAM_4:"+LocListTOStringList(list.get(Team.TEAM_4.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_5.Name())){
				out.write("TEAM_5:"+LocListTOStringList(list.get(Team.TEAM_5.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_6.Name())){
				out.write("TEAM_6:"+LocListTOStringList(list.get(Team.TEAM_6.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_7.Name())){
				out.write("TEAM_7:"+LocListTOStringList(list.get(Team.TEAM_7.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_8.Name())){
				out.write("TEAM_8:"+LocListTOStringList(list.get(Team.TEAM_8.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_9.Name())){
				out.write("TEAM_9:"+LocListTOStringList(list.get(Team.TEAM_9.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_10.Name())){
				out.write("TEAM_10:"+LocListTOStringList(list.get(Team.TEAM_10.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_11.Name())){
				out.write("TEAM_11:"+LocListTOStringList(list.get(Team.TEAM_11.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_12.Name())){
				out.write("TEAM_12:"+LocListTOStringList(list.get(Team.TEAM_12.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_13.Name())){
				out.write("TEAM_13:"+LocListTOStringList(list.get(Team.TEAM_13.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_14.Name())){
				out.write("TEAM_14:"+LocListTOStringList(list.get(Team.TEAM_14.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_15.Name())){
				out.write("TEAM_15:"+LocListTOStringList(list.get(Team.TEAM_15.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_16.Name())){
				out.write("TEAM_16:"+LocListTOStringList(list.get(Team.TEAM_16.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_17.Name())){
				out.write("TEAM_17:"+LocListTOStringList(list.get(Team.TEAM_17.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_18.Name())){
				out.write("TEAM_18:"+LocListTOStringList(list.get(Team.TEAM_18.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_19.Name())){
				out.write("TEAM_19:"+LocListTOStringList(list.get(Team.TEAM_19.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_20.Name())){
				out.write("TEAM_20:"+LocListTOStringList(list.get(Team.TEAM_20.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_21.Name())){
				out.write("TEAM_21:"+LocListTOStringList(list.get(Team.TEAM_21.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_22.Name())){
				out.write("TEAM_22:"+LocListTOStringList(list.get(Team.TEAM_22.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_23.Name())){
				out.write("TEAM_23:"+LocListTOStringList(list.get(Team.TEAM_23.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_24.Name())){
				out.write("TEAM_24:"+LocListTOStringList(list.get(Team.TEAM_24.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_25.Name())){
				out.write("TEAM_25:"+LocListTOStringList(list.get(Team.TEAM_25.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_26.Name())){
				out.write("TEAM_26:"+LocListTOStringList(list.get(Team.TEAM_26.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_27.Name())){
				out.write("TEAM_27:"+LocListTOStringList(list.get(Team.TEAM_27.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_28.Name())){
				out.write("TEAM_28:"+LocListTOStringList(list.get(Team.TEAM_28.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_29.Name())){
				out.write("TEAM_29:"+LocListTOStringList(list.get(Team.TEAM_29.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_30.Name())){
				out.write("TEAM_30:"+LocListTOStringList(list.get(Team.TEAM_30.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_31.Name())){
				out.write("TEAM_31:"+LocListTOStringList(list.get(Team.TEAM_31.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_32.Name())){
				out.write("TEAM_32:"+LocListTOStringList(list.get(Team.TEAM_32.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_POINT_1.Name())){
				out.write("TEAM_POINT_1:"+LocListTOStringList(list.get(Team.TEAM_POINT_1.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_POINT_2.Name())){
				out.write("TEAM_POINT_2:"+LocListTOStringList(list.get(Team.TEAM_POINT_2.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_POINT_3.Name())){
				out.write("TEAM_POINT_3:"+LocListTOStringList(list.get(Team.TEAM_POINT_3.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_POINT_4.Name())){
				out.write("TEAM_POINT_4:"+LocListTOStringList(list.get(Team.TEAM_POINT_4.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_POINT_5.Name())){
				out.write("TEAM_POINT_5:"+LocListTOStringList(list.get(Team.TEAM_POINT_5.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_POINT_6.Name())){
				out.write("TEAM_POINT_6:"+LocListTOStringList(list.get(Team.TEAM_POINT_6.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_POINT_7.Name())){
				out.write("TEAM_POINT_7:"+LocListTOStringList(list.get(Team.TEAM_POINT_7.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_POINT_8.Name())){
				out.write("TEAM_POINT_8:"+LocListTOStringList(list.get(Team.TEAM_POINT_8.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_POINT_9.Name())){
				out.write("TEAM_POINT_9:"+LocListTOStringList(list.get(Team.TEAM_POINT_9.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_POINT_10.Name())){
				out.write("TEAM_POINT_10:"+LocListTOStringList(list.get(Team.TEAM_POINT_10.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_POINT_11.Name())){
				out.write("TEAM_POINT_11:"+LocListTOStringList(list.get(Team.TEAM_POINT_11.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_POINT_12.Name())){
				out.write("TEAM_POINT_12:"+LocListTOStringList(list.get(Team.TEAM_POINT_12.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_POINT_13.Name())){
				out.write("TEAM_POINT_13:"+LocListTOStringList(list.get(Team.TEAM_POINT_13.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_POINT_14.Name())){
				out.write("TEAM_POINT_14:"+LocListTOStringList(list.get(Team.TEAM_POINT_14.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_POINT_15.Name())){
				out.write("TEAM_POINT_15:"+LocListTOStringList(list.get(Team.TEAM_POINT_15.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_POINT_16.Name())){
				out.write("TEAM_POINT_16:"+LocListTOStringList(list.get(Team.TEAM_POINT_16.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_POINT_17.Name())){
				out.write("TEAM_POINT_17:"+LocListTOStringList(list.get(Team.TEAM_POINT_17.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_POINT_18.Name())){
				out.write("TEAM_POINT_18:"+LocListTOStringList(list.get(Team.TEAM_POINT_18.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_POINT_19.Name())){
				out.write("TEAM_POINT_19"+LocListTOStringList(list.get(Team.TEAM_POINT_19.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_POINT_20.Name())){
				out.write("TEAM_POINT_20:"+LocListTOStringList(list.get(Team.TEAM_POINT_20.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_POINT_21.Name())){
				out.write("TEAM_POINT_21:"+LocListTOStringList(list.get(Team.TEAM_POINT_21.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_POINT_22.Name())){
				out.write("TEAM_POINT_22:"+LocListTOStringList(list.get(Team.TEAM_POINT_22.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_POINT_23.Name())){
				out.write("TEAM_POINT_23:"+LocListTOStringList(list.get(Team.TEAM_POINT_23.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_POINT_24.Name())){
				out.write("TEAM_POINT_24:"+LocListTOStringList(list.get(Team.TEAM_POINT_24.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_POINT_25.Name())){
				out.write("TEAM_POINT_25:"+LocListTOStringList(list.get(Team.TEAM_POINT_25.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_POINT_26.Name())){
				out.write("TEAM_POINT_26:"+LocListTOStringList(list.get(Team.TEAM_POINT_26.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_POINT_27.Name())){
				out.write("TEAM_POINT_27:"+LocListTOStringList(list.get(Team.TEAM_POINT_27.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_POINT_28.Name())){
				out.write("TEAM_POINT_28:"+LocListTOStringList(list.get(Team.TEAM_POINT_28.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_POINT_29.Name())){
				out.write("TEAM_POINT_29:"+LocListTOStringList(list.get(Team.TEAM_POINT_29.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_POINT_30.Name())){
				out.write("TEAM_POINT_30:"+LocListTOStringList(list.get(Team.TEAM_POINT_30.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_POINT_31.Name())){
				out.write("TEAM_POINT_31:"+LocListTOStringList(list.get(Team.TEAM_POINT_31.Name())));
				out.write("\n");
			}
			if(list.containsKey(Team.TEAM_POINT_32.Name())){
				out.write("TEAM_POINT_32:"+LocListTOStringList(list.get(Team.TEAM_POINT_32.Name())));
				out.write("\n");
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String LocListTOStringList(ArrayList<Location> list){
		if(list==null||list.isEmpty()){
			System.err.println("[WorldSetUp] Die Liste ist leer!");
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