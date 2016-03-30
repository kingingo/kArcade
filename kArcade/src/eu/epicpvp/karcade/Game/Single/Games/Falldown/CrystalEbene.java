package eu.epicpvp.karcade.Game.Single.Games.Falldown;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.entity.EnderCrystal;

import eu.epicpvp.kcore.Util.UtilMap;
import lombok.Getter;

public class CrystalEbene {

	@Getter
	private ArrayList<EnderCrystal> crystals;
	@Getter
	private int id;
	
	public CrystalEbene(Location location, int radius){
		this.crystals = UtilMap.setCrystalCricle(location, radius, location.getBlockY());
		this.id=location.getBlockY();
	}
	
}
