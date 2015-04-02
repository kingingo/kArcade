package me.kingingo.karcade.Game.Games.CaveWars.Map;

import me.kingingo.karcade.Game.World.WorldData;

import org.bukkit.Chunk;
import org.bukkit.Location;

public interface SchematicPopulatorRunnable {
	public void onSchematicPopulatorRunnable(Location location,WorldData wd);
	public boolean distance(WorldData wd,Location loc);
}