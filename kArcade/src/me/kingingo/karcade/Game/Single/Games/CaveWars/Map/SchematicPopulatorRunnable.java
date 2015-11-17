package me.kingingo.karcade.Game.Single.Games.CaveWars.Map;

import me.kingingo.karcade.Game.Single.SingleWorldData;
import me.kingingo.karcade.Game.World.WorldData;

import org.bukkit.Location;

public interface SchematicPopulatorRunnable {
	public void onSchematicPopulatorRunnable(Location location,WorldData wd);
	public boolean distance(SingleWorldData wd,Location loc);
}