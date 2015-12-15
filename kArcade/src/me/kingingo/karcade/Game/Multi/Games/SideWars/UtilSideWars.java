package me.kingingo.karcade.Game.Multi.Games.SideWars;

import me.kingingo.kcore.Enum.Team;

import org.bukkit.Location;
import org.bukkit.Material;

public class UtilSideWars {
	
	public static int Blau = 0;
	public static int Rot = 1;
	public static int X = 0;
	public static int Y = 1;
	public static int Z = 2;
	public static int Min = 0;
	public static int Max = 1;
	
	public static void changeBuildTime(SideWars game, boolean buildTime){
		int x = game.getMinMax()[Blau][ (game.getAchse() == 'Z' ? Z : X) ][ (game.isBlaukleiner() ? Max : Min) ];
		Location l = game.getWorldData().getLocs(game, Team.BLUE).get(0).clone();
		l.setX(x);
		for (int y = game.getMinMax()[Blau][Y][Min]; y < game.getMinMax()[Blau][Y][Max] + 4; y++) {
			l.setY(y);
			for (int z = game.getMinMax()[Blau][(game.getAchse() != 'Z' ? Z : X)][Min]; z < game.getMinMax()[Blau][(game.getAchse() != 'Z' ? Z : X)][Max] + 1; z++) {
				l.setZ(z);
//				PlayerIsInLocation(l);
				if(buildTime){
					//SEZTEN
					if(l.getBlock().getType() == Material.AIR){
						l.getBlock().setType(Material.GLASS);
					}else if(!(l.getBlock().getType() == Material.STAINED_CLAY)){
						game.getBlock().put(l, l.getBlock().getData());
						l.getBlock().setType(Material.GLASS);
					}
				}else{
					//LÖSCHEN
					if(l.getBlock().getType() == Material.GLASS){
						if(game.getBlock().containsKey(l)){
							byte i = game.getBlock().get(l);
							game.getBlock().remove(l);
							l.getBlock().setType(Material.WOOL);
							l.getBlock().setData(i);
							continue;
						}
						l.getBlock().setType(Material.AIR);
					}
					
				}
				
			}
			
		}
		
		
		x = game.getMinMax()[Rot][ (game.getAchse() == 'Z' ? Z : X) ][ (game.isBlaukleiner() ? Min : Max) ];
		l = game.getWorldData().getLocs(game, Team.BLUE).get(0).clone();
		l.setX(x);
		for (int y = game.getMinMax()[Rot][Y][Min]; y < game.getMinMax()[Rot][Y][Max] + 4; y++) {
			l.setY(y);
			for (int z = game.getMinMax()[Rot][(game.getAchse() != 'Z' ? Z : X)][Min]; z < game.getMinMax()[Rot][(game.getAchse() != 'Z' ? Z : X)][Max] + 1; z++) {
				l.setZ(z);
//				PlayerIsInLocation(l);
				if(buildTime){
					//SEZTEN
					if(l.getBlock().getType() == Material.AIR){
						l.getBlock().setType(Material.GLASS);
					}else if(!(l.getBlock().getType() == Material.STAINED_CLAY)){
						game.getBlock().put(l, l.getBlock().getData());
						l.getBlock().setType(Material.GLASS);
					}
				}else{
					//LÖSCHEN
					if(l.getBlock().getType() == Material.GLASS){
						if(game.getBlock().containsKey(l)){
							byte i = game.getBlock().get(l);
							game.getBlock().remove(l);
							l.getBlock().setType(Material.WOOL);
							l.getBlock().setData(i);
							continue;
						}
						l.getBlock().setType(Material.AIR);
					}
					
				}
				
			}
			
		}
	}
	
	public static int[][][] getMinMax(Location Ecken[][]) {
		int MinMax[][][] = new int[2][3][2]; // [Blau/Rot][X/Y/Z][Min/Max]
		if (Ecken[Blau][0].getBlockX() > Ecken[Blau][1].getBlockX()) {
			MinMax[Blau][X][Min] = Ecken[Blau][1].getBlockX();
			MinMax[Blau][X][Max] = Ecken[Blau][0].getBlockX();
		} else {
			MinMax[Blau][X][Max] = Ecken[Blau][1].getBlockX();
			MinMax[Blau][X][Min] = Ecken[Blau][0].getBlockX();
		}
		if (Ecken[Blau][0].getBlockY() > Ecken[Blau][1].getBlockY()) {
			MinMax[Blau][Y][Min] = Ecken[Blau][1].getBlockY();
			MinMax[Blau][Y][Max] = Ecken[Blau][0].getBlockY();
		} else {
			MinMax[Blau][Y][Max] = Ecken[Blau][1].getBlockY();
			MinMax[Blau][Y][Min] = Ecken[Blau][0].getBlockY();
		}
		if (Ecken[Blau][0].getBlockZ() > Ecken[Blau][1].getBlockZ()) {
			MinMax[Blau][Z][Min] = Ecken[Blau][1].getBlockZ();
			MinMax[Blau][Z][Max] = Ecken[Blau][0].getBlockZ();
		} else {
			MinMax[Blau][Z][Max] = Ecken[Blau][1].getBlockZ();
			MinMax[Blau][Z][Min] = Ecken[Blau][0].getBlockZ();
		}

		if (Ecken[Rot][0].getBlockX() > Ecken[Rot][1].getBlockX()) {
			MinMax[Rot][X][Min] = Ecken[Rot][1].getBlockX();
			MinMax[Rot][X][Max] = Ecken[Rot][0].getBlockX();
		} else {
			MinMax[Rot][X][Max] = Ecken[Rot][1].getBlockX();
			MinMax[Rot][X][Min] = Ecken[Rot][0].getBlockX();
		}
		if (Ecken[Rot][0].getBlockY() > Ecken[Rot][1].getBlockY()) {
			MinMax[Rot][Y][Min] = Ecken[Rot][1].getBlockY();
			MinMax[Rot][Y][Max] = Ecken[Rot][0].getBlockY();
		} else {
			MinMax[Rot][Y][Max] = Ecken[Rot][1].getBlockY();
			MinMax[Rot][Y][Min] = Ecken[Rot][0].getBlockY();
		}
		if (Ecken[Rot][0].getBlockZ() > Ecken[Rot][1].getBlockZ()) {
			MinMax[Rot][Z][Min] = Ecken[Rot][1].getBlockZ();
			MinMax[Rot][Z][Max] = Ecken[Rot][0].getBlockZ();
		} else {
			MinMax[Rot][Z][Max] = Ecken[Rot][1].getBlockZ();
			MinMax[Rot][Z][Min] = Ecken[Rot][0].getBlockZ();
		}
		return MinMax;
	}
	
	public static void XorZ(SideWars game) {
		if (game.getWorldData().getLocs(game, Team.BLUE).get(0).getBlockX() == game.getWorldData().getLocs(game, Team.RED).get(0).getBlockX()) {
			game.setAchse('Z');
			game.setBlaukleiner(game.getWorldData().getLocs(game, Team.BLUE).get(0).getBlockZ() < game.getWorldData().getLocs(game, Team.RED).get(0).getBlockZ());
			game.setRotkleiner(game.getWorldData().getLocs(game, Team.RED).get(0).getBlockZ() < game.getWorldData().getLocs(game, Team.BLUE).get(0).getBlockZ());
		} else if (game.getWorldData().getLocs(game, Team.BLUE).get(0).getBlockZ() == game.getWorldData().getLocs(game, Team.RED).get(0).getBlockZ()) {
			game.setAchse('X');
			game.setBlaukleiner(game.getWorldData().getLocs(game, Team.BLUE).get(0).getBlockX() < game.getWorldData().getLocs(game, Team.RED).get(0).getBlockX());
			game.setRotkleiner(game.getWorldData().getLocs(game, Team.RED).get(0).getBlockX() < game.getWorldData().getLocs(game, Team.BLUE).get(0).getBlockX());
		} else {
			System.err.println("[UtilSideWars] Die beiden Spawns sind nicht parallel zueinander!");
			System.err.println("[UtilSideWars] Arena: "+game.getArena());
			return;
		}

	}
}
