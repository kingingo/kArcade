package eu.epicpvp.karcade.Game.Multi;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;

import dev.wolveringer.dataclient.gamestats.GameType;
import eu.epicpvp.karcade.kArcadeManager;
import eu.epicpvp.karcade.Game.Multi.Games.MultiGame;
import eu.epicpvp.karcade.Game.World.WorldData;
import eu.epicpvp.karcade.Game.World.Parser.SchematicParser;
import eu.epicpvp.kcore.Enum.Team;
import lombok.Setter;

public class MultiWorldData extends WorldData{

	@Setter
	private HashMap<MultiGame,HashMap<Team,ArrayList<Location>>> locs; //Alle Team Locations der einzelnen MultiGame Arenas
	
	public MultiWorldData(kArcadeManager manager, GameType type) {
		super(manager, type);
		this.locs= new HashMap<>();
	}
	
	public void loadSchematic(MultiGame game,Location paste, File folder){
		folder = UnzipSchematic(folder);
		pasteSchematic(paste, new File(folder.getAbsolutePath() + File.separator + "file.schematic"));
		loadSchematicConfig(game, paste, folder);
	}
	
	public void loadSchematicConfig(MultiGame game,Location paste, File folder){
		addMultiGame(game, null);
		String line=null;
		try {
			FileInputStream fstream = new FileInputStream(folder + File.separator + "SchematicConfig.dat");
			DataInputStream in = new DataInputStream(fstream);
		    BufferedReader br = new BufferedReader(new InputStreamReader(in));
		    
		    while((line=br.readLine()) != null){
		    	String[] tokens = line.split(":");
		    	if (tokens.length >= 2){
		    		if(tokens[0].equalsIgnoreCase("MAP_NAME")){
		    			game.setMap(tokens[1]);
		    		}else if(tokens[0].equalsIgnoreCase(Team.SOLO.Name())){
		    			locs.get(game).put(Team.SOLO, SchematicParser.StringListTOVectorList(paste,tokens[1]));
		    		}else if(tokens[0].equalsIgnoreCase(Team.SILBER.Name())){
		    			locs.get(game).put(Team.SILBER, SchematicParser.StringListTOVectorList(paste,tokens[1]));
		    		}else if(tokens[0].equalsIgnoreCase(Team.GOLD.Name())){
		    			locs.get(game).put(Team.GOLD, SchematicParser.StringListTOVectorList(paste,tokens[1]));
		    		}else if(tokens[0].equalsIgnoreCase(Team.DIAMOND.Name())){
		    			locs.get(game).put(Team.DIAMOND, SchematicParser.StringListTOVectorList(paste,tokens[1]));
		    		}else if(tokens[0].equalsIgnoreCase(Team.BRONZE.Name())){
		    			locs.get(game).put(Team.BRONZE, SchematicParser.StringListTOVectorList(paste,tokens[1]));
		    		}else if(tokens[0].equalsIgnoreCase(Team.ORANGE.Name())){
		    			locs.get(game).put(Team.ORANGE, SchematicParser.StringListTOVectorList(paste,tokens[1]));
		    		}else if(tokens[0].equalsIgnoreCase(Team.PURPLE.Name())){
		    			locs.get(game).put(Team.PURPLE, SchematicParser.StringListTOVectorList(paste,tokens[1]));
		    		}else if(tokens[0].equalsIgnoreCase(Team.GRAY.Name())){
		    			locs.get(game).put(Team.GRAY, SchematicParser.StringListTOVectorList(paste,tokens[1]));
		    		}else if(tokens[0].equalsIgnoreCase(Team.BLUE.Name())){
		    			locs.get(game).put(Team.BLUE, SchematicParser.StringListTOVectorList(paste,tokens[1]));
		    		}else if(tokens[0].equalsIgnoreCase(Team.RED.Name())){
		    			locs.get(game).put(Team.RED, SchematicParser.StringListTOVectorList(paste,tokens[1]));
		    		}else if(tokens[0].equalsIgnoreCase(Team.YELLOW.Name())){
		    			locs.get(game).put(Team.YELLOW, SchematicParser.StringListTOVectorList(paste,tokens[1]));
		    		}else if(tokens[0].equalsIgnoreCase(Team.GREEN.Name())){
		    			locs.get(game).put(Team.GREEN, SchematicParser.StringListTOVectorList(paste,tokens[1]));
		    		}else if(tokens[0].equalsIgnoreCase(Team.CYAN.Name())){
		    			locs.get(game).put(Team.CYAN, SchematicParser.StringListTOVectorList(paste,tokens[1]));
		    		}else if(tokens[0].equalsIgnoreCase(Team.AQUA.Name())){
		    			locs.get(game).put(Team.AQUA, SchematicParser.StringListTOVectorList(paste,tokens[1]));
		    		}else if(tokens[0].equalsIgnoreCase(Team.BLACK.Name())){
		    			locs.get(game).put(Team.BLACK, SchematicParser.StringListTOVectorList(paste,tokens[1]));
		    		}else if(tokens[0].equalsIgnoreCase(Team.WHITE.Name())){
		    			locs.get(game).put(Team.WHITE, SchematicParser.StringListTOVectorList(paste,tokens[1]));
		    		}else if(tokens[0].equalsIgnoreCase(Team.VILLAGE_BLUE.Name())){
		    			locs.get(game).put(Team.VILLAGE_BLUE, SchematicParser.StringListTOVectorList(paste,tokens[1]));
		    		}else if(tokens[0].equalsIgnoreCase(Team.VILLAGE_RED.Name())){
		    			locs.get(game).put(Team.VILLAGE_RED, SchematicParser.StringListTOVectorList(paste,tokens[1]));
		    		}else if(tokens[0].equalsIgnoreCase(Team.VILLAGE_YELLOW.Name())){
		    			locs.get(game).put(Team.VILLAGE_YELLOW, SchematicParser.StringListTOVectorList(paste,tokens[1]));
		    		}else if(tokens[0].equalsIgnoreCase(Team.VILLAGE_BLACK.Name())){
		    			locs.get(game).put(Team.VILLAGE_BLACK, SchematicParser.StringListTOVectorList(paste,tokens[1]));
		    		}else if(tokens[0].equalsIgnoreCase(Team.VILLAGE_GREEN.Name())){
		    			locs.get(game).put(Team.VILLAGE_GREEN, SchematicParser.StringListTOVectorList(paste,tokens[1]));
		    		}else if(tokens[0].equalsIgnoreCase(Team.SHEEP_BLUE.Name())){
		    			locs.get(game).put(Team.SHEEP_BLUE, SchematicParser.StringListTOVectorList(paste,tokens[1]));
		    		}else if(tokens[0].equalsIgnoreCase(Team.SHEEP_RED.Name())){
		    			locs.get(game).put(Team.SHEEP_RED, SchematicParser.StringListTOVectorList(paste,tokens[1]));
		    		}else if(tokens[0].equalsIgnoreCase(Team.SHEEP_YELLOW.Name())){
		    			locs.get(game).put(Team.SHEEP_YELLOW, SchematicParser.StringListTOVectorList(paste,tokens[1]));
		    		}else if(tokens[0].equalsIgnoreCase(Team.SHEEP_GREEN.Name())){
		    			locs.get(game).put(Team.SHEEP_GREEN, SchematicParser.StringListTOVectorList(paste,tokens[1]));
		    		}else if(tokens[0].equalsIgnoreCase(Team.PINK.Name())){
		    			locs.get(game).put(Team.PINK, SchematicParser.StringListTOVectorList(paste,tokens[1]));
		    		}else if(tokens[0].equalsIgnoreCase(Team.VILLAGE_GRAY.Name())){
		    			locs.get(game).put(Team.VILLAGE_GRAY, SchematicParser.StringListTOVectorList(paste,tokens[1]));
		    		}else if(tokens[0].equalsIgnoreCase(Team.VILLAGE_PINK.Name())){
		    			locs.get(game).put(Team.VILLAGE_PINK, SchematicParser.StringListTOVectorList(paste,tokens[1]));
		    		}else if(tokens[0].equalsIgnoreCase(Team.VILLAGE_PURPLE.Name())){
		    			locs.get(game).put(Team.VILLAGE_PURPLE, SchematicParser.StringListTOVectorList(paste,tokens[1]));
		    		}else if(tokens[0].equalsIgnoreCase(Team.VILLAGE_ORANGE.Name())){
		    			locs.get(game).put(Team.VILLAGE_ORANGE, SchematicParser.StringListTOVectorList(paste,tokens[1]));
		    		}else if(tokens[0].equalsIgnoreCase(Team.SHEEP_GRAY.Name())){
		    			locs.get(game).put(Team.SHEEP_GRAY, SchematicParser.StringListTOVectorList(paste,tokens[1]));
		    		}else if(tokens[0].equalsIgnoreCase(Team.SHEEP_ORANGE.Name())){
		    			locs.get(game).put(Team.SHEEP_ORANGE, SchematicParser.StringListTOVectorList(paste,tokens[1]));
		    		}else if(tokens[0].equalsIgnoreCase(Team.SHEEP_PURPLE.Name())){
		    			locs.get(game).put(Team.SHEEP_PURPLE, SchematicParser.StringListTOVectorList(paste,tokens[1]));
		    		}else if(tokens[0].equalsIgnoreCase(Team.SHEEP_PINK.Name())){
		    			locs.get(game).put(Team.SHEEP_PINK, SchematicParser.StringListTOVectorList(paste,tokens[1]));
		    		}else if(tokens[0].equalsIgnoreCase(Team.SHEEP_CYAN.Name())){
		    			locs.get(game).put(Team.SHEEP_CYAN, SchematicParser.StringListTOVectorList(paste,tokens[1]));
		    		}else if(tokens[0].equalsIgnoreCase(Team.SHEEP_AQUA.Name())){
		    			locs.get(game).put(Team.SHEEP_AQUA, SchematicParser.StringListTOVectorList(paste,tokens[1]));
		    		}else if(tokens[0].equalsIgnoreCase(Team.SHEEP_BLACK.Name())){
		    			locs.get(game).put(Team.SHEEP_BLACK, SchematicParser.StringListTOVectorList(paste,tokens[1]));
		    		}else if(tokens[0].equalsIgnoreCase(Team.SHEEP_WHITE.Name())){
		    			locs.get(game).put(Team.SHEEP_WHITE, SchematicParser.StringListTOVectorList(paste,tokens[1]));
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
		log("MultiGame Arena: "+game.getArena());
		log("MultiGame Map: "+game.getMap());
		for(Team team : locs.get(game).keySet())log("TEAM:"+team.Name()+" LOC:"+locs.get(game).get(team).size());
	}
	
	public void addMultiGame(MultiGame game,Team team){
		if(!locs.containsKey(game))locs.put(game,new HashMap<>());
		if(team!=null&&!locs.get(game).containsKey(team))locs.get(game).put(team, new ArrayList<Location>());
	}
	
	public void addLoc(MultiGame game,Team team,Location loc){
		addMultiGame(game,team);
		locs.get(game).get(team).add(loc);
	}
	
	public boolean existLoc(MultiGame game,Team team){
		addMultiGame(game,team);
		return locs.get(game).containsKey(team);
	}
	
	public HashMap<Team,ArrayList<Location>> getTeams(MultiGame game){
		addMultiGame(game,null);
		return locs.get(game);
	}
	
	public ArrayList<Location> getLocs(MultiGame game,Team team){
		addMultiGame(game,team);
		if(!locs.get(game).containsKey(team)){
			logErr("Team NOT Found! "+team.Name());
			return null;
		}else{
			return locs.get(game).get(team);
		}
	}

}
