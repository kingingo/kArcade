package eu.epicpvp.karcade.Game.World;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import eu.epicpvp.kcore.Enum.Team;
import lombok.Getter;
import lombok.Setter;

public class GameMap{
	
	@Getter
	@Setter
	private String mapName;
	@Getter
	@Setter
	private ItemStack item;
	@Getter
	private HashMap<Team,ArrayList<Location>> locations;
	@Getter
	@Setter
	private World world;
	@Getter
	@Setter
	private File file;
	
	public GameMap(World world,File file, WorldData worldData){
		this.mapName="Loading ...";
		this.locations = new HashMap<>();
		this.world=world;
		this.file=file;
	}
	
	public void clear(){
		this.mapName="";
		this.item=null;
		this.locations.clear();
		this.locations=null;
		this.world=null;
		this.file=null;
	}
}
