package eu.epicpvp.karcade.Game.Single.Games.SideWar;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import eu.epicpvp.kcore.Enum.Team;

public class SideHandler {
	private int X = 0;
	private int Y = 1;
	private int Z = 2;
	private int Min = 0;
	private int Max = 1;
	private int Blau = 0;
	private int Rot = 1;
	private int MinMax[][][];
	private char achse;
	private SideWar instance;
	private boolean BLAUkleiner;
	private boolean ROTkleiner;
	private Location Spawns[];
	private int lines;
	private World world;
	private int rotLines;
	private int blauLines;
	private boolean EoderZ=false; //Zerstörung oder Eroberung
	
	public SideHandler(Location Ecken[][],int lines){
		this.MinMax = new int[2][3][2];
		this.Spawns = new Location[2];
		this.lines=lines;
		this.world=Ecken[Blau][0].getWorld();
		
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
		
		XorZ();
		
		if(achse == 'X'){
			rotLines=MinMax[Rot][Z][Max]-MinMax[Rot][Z][Min];
			blauLines=MinMax[Blau][Z][Max]-MinMax[Blau][Z][Min];
		}else{
			rotLines=MinMax[Rot][X][Max]-MinMax[Rot][X][Min];
			blauLines=MinMax[Blau][X][Max]-MinMax[Blau][X][Min];
		}
	}
	
	public boolean moveAfterKill(Team team) {
		// -//-//-//-//-//-//-//-//-//-//-//-//-//-//-//-//-//-//-//-//-//-//-//-//-//-//-//-//-//-//-//-//-//-//-//-//-//
		if (achse == 'X') {
			if (team == Team.BLUE) {

				rotLines -= lines;
				if (BLAUkleiner) {

					MinMax[Blau][X][Max] += lines;
					MinMax[Rot][X][Min] += lines;
					Location loc = new Location(world, 0, 0, 0);
					for (int x = MinMax[Blau][X][Max]; x > MinMax[Blau][X][Max]
							- lines; x--) {
						loc.setX(x);

						for (int y = MinMax[Blau][Y][Min]; y < MinMax[Blau][Y][Max] + 1; y++) {
							loc.setY(y);
							for (int z = MinMax[Blau][Z][Min]; z < MinMax[Blau][Z][Max] + 1; z++) {
								loc.setZ(z);
								if (loc.getBlock().getType() == Material.WOOL) {
									// {locZUBLAUERWOLLE}
									if(EoderZ){
										loc.getBlock().setData((byte) 3);
										loc.getBlock().setType(Material.WOOL);
									}else{
										loc.getBlock().setType(Material.AIR);
										Bukkit.getWorld(loc.getWorld().getName()).playEffect(loc, Effect.STEP_SOUND, 152);
									}
								}
								if (loc.getBlock().getType() == Material.STAINED_CLAY) {
									// {locZUROTERWOLLE}
									loc.getBlock().setData((byte) 3);
									loc.getBlock().setType(
											Material.STAINED_CLAY);
								}
							}
						}
					}

					if (MinMax[Rot][X][Min] > MinMax[Rot][X][Max]) {
						// {BLAUGEWONNEN}
						
					}
				} else {
					MinMax[Rot][X][Max] -= lines;
					MinMax[Blau][X][Min] -= lines;
					Location loc = new Location(world, 0, 0, 0);

					for (int x = MinMax[Blau][X][Min]; x < MinMax[Blau][X][Min]
							+ lines; x++) {
						loc.setX(x);
						for (int y = MinMax[Blau][Y][Min]; y < MinMax[Blau][Y][Max] + 1; y++) {
							loc.setY(y);
							for (int z = MinMax[Blau][Z][Min]; z < MinMax[Blau][Z][Max] + 1; z++) {
								loc.setZ(z);
								if (loc.getBlock().getType() == Material.WOOL) {
									// {locZUBLAUERWOLLE}
									if(EoderZ){
										loc.getBlock().setData((byte) 3);
										loc.getBlock().setType(Material.WOOL);
									}else{
										loc.getBlock().setType(Material.AIR);
										Bukkit.getWorld(loc.getWorld().getName()).playEffect(loc, Effect.STEP_SOUND, 22);
									}
								}
								if (loc.getBlock().getType() == Material.STAINED_CLAY) {
									// {locZUROTERWOLLE}
									loc.getBlock().setData((byte) 3);
									loc.getBlock().setType(
											Material.STAINED_CLAY);
								}
							}
						}
					}

					if (MinMax[Rot][X][Max] < MinMax[Rot][X][Min]) {
						// {BLAUGEWONNEN}
						
					}
				}
			} else if (team == Team.RED) {
				rotLines = +lines;
				blauLines = -lines;
				if (ROTkleiner) {
					MinMax[Rot][X][Max] += lines;
					MinMax[Blau][X][Min] += lines;
					Location loc = new Location(world, 0, 0, 0);
					for (int x = MinMax[Rot][X][Max]; x > MinMax[Rot][X][Max]
							- lines; x--) {
						loc.setX(x);
						for (int y = MinMax[Rot][Y][Min]; y < MinMax[Rot][Y][Max] + 1; y++) {
							loc.setY(y);
							for (int z = MinMax[Rot][Z][Min]; z < MinMax[Rot][Z][Max] + 1; z++) {
								loc.setZ(z);
								if (loc.getBlock().getType() == Material.WOOL) {
									// {locZUROTERWOLLE}
									if(EoderZ){
										loc.getBlock().setData((byte) 14);
										loc.getBlock().setType(Material.WOOL);
									}else{
										loc.getBlock().setType(Material.AIR);
										Bukkit.getWorld(loc.getWorld().getName()).playEffect(loc, Effect.STEP_SOUND, 152);
									}
								}
								if (loc.getBlock().getType() == Material.STAINED_CLAY) {
									// {locZUROTERWOLLE}
									loc.getBlock().setData((byte) 14);
									loc.getBlock().setType(
											Material.STAINED_CLAY);
								}
							}
						}
					}
					if (MinMax[Blau][X][Min] > MinMax[Blau][X][Max]) {
						// {ROTGEWONNEN}
						
					}
				} else {
					MinMax[Blau][X][Max] -= lines;
					MinMax[Rot][X][Min] -= lines;
					Location loc = new Location(world, 0, 0, 0);
					for (int x = MinMax[Rot][X][Min]; x < MinMax[Rot][X][Min]
							+ lines; x++) {
						loc.setX(x);
						for (int y = MinMax[Rot][Y][Min]; y < MinMax[Rot][Y][Max] + 1; y++) {
							loc.setY(y);
							for (int z = MinMax[Rot][Z][Min]; z < MinMax[Rot][Z][Max] + 1; z++) {
								loc.setZ(z);
								if (loc.getBlock().getType() == Material.WOOL) {
									// {locZUROTERWOLLE}
									if(EoderZ){
										loc.getBlock().setData((byte) 14);
										loc.getBlock().setType(Material.WOOL);
									}else{
										loc.getBlock().setType(Material.AIR);
										Bukkit.getWorld(loc.getWorld().getName()).playEffect(loc, Effect.STEP_SOUND, 152);
									}
								}
								if (loc.getBlock().getType() == Material.STAINED_CLAY) {
									// {locZUROTERWOLLE}
									loc.getBlock().setData((byte) 14);
									loc.getBlock().setType(
											Material.STAINED_CLAY);
								}
							}
						}
					}
					if (MinMax[Blau][X][Max] < MinMax[Blau][X][Min]) {
						// {ROTGEWONNEN}
						
					}
				}
			}

			blauLines = MinMax[Blau][X][Max] - MinMax[Blau][X][Min] + 1;
			rotLines = MinMax[Rot][X][Max] - MinMax[Rot][X][Min] + 1;
		} else if (achse == 'Z') {

			if (team==Team.BLUE) {
				blauLines += lines;
				rotLines -= lines;
				if (BLAUkleiner) {
					MinMax[Blau][Z][Max] += lines;
					MinMax[Rot][Z][Min] += lines;

					Location loc = new Location(world, 0, 0, 0);
					for (int z = MinMax[Blau][Z][Max]; z > MinMax[Blau][Z][Max]
							- lines; z--) {
						loc.setZ(z);
						for (int y = MinMax[Blau][Y][Min]; y < MinMax[Blau][Y][Max] + 1; y++) {
							loc.setY(y);
							for (int x = MinMax[Blau][X][Min]; x < MinMax[Blau][x][Max] + 1; x++) {
								loc.setX(x);
								if (loc.getBlock().getType() == Material.WOOL) {
									// {locZUBLAUERWOLLE}
									if(EoderZ){
										loc.getBlock().setData((byte) 3);
										loc.getBlock().setType(Material.WOOL);
									}else{
										loc.getBlock().setType(Material.AIR);
										Bukkit.getWorld(loc.getWorld().getName()).playEffect(loc, Effect.STEP_SOUND, 22);
									}
								}
								if (loc.getBlock().getType() == Material.STAINED_CLAY) {
									// {locZUROTERWOLLE}
									loc.getBlock().setData((byte) 3);
									loc.getBlock().setType(
											Material.STAINED_CLAY);
								}
							}
						}
					}
					if (MinMax[Rot][Z][Min] > MinMax[Rot][Z][Max]) {
						// {BLAUGEWONNEN}
						win(Team.BLUE);
					}
				} else {
					MinMax[Rot][Z][Max] -= lines;
					MinMax[Blau][Z][Min] -= lines;
					Location loc = new Location(world, 0, 0, 0);
					for (int z = MinMax[Blau][Z][Min]; z < MinMax[Blau][Z][Min]
							+ lines; z++) {
						loc.setZ(z);
						for (int y = MinMax[Blau][Y][Min]; y < MinMax[Blau][Y][Max] + 1; y++) {
							loc.setY(y);
							for (int x = MinMax[Blau][X][Min]; x < MinMax[Blau][X][Max] + 1; x++) {
								loc.setX(x);
								if (loc.getBlock().getType() == Material.WOOL) {
									// {locZUBLAUERWOLLE}
									if(EoderZ){
										loc.getBlock().setData((byte) 3);
										loc.getBlock().setType(Material.WOOL);
									}else{
										loc.getBlock().setType(Material.AIR);
										Bukkit.getWorld(loc.getWorld().getName()).playEffect(loc, Effect.STEP_SOUND, 22);
									}
								}
								if (loc.getBlock().getType() == Material.STAINED_CLAY) {
									// {locZUROTERWOLLE}
									loc.getBlock().setData((byte) 3);
									loc.getBlock().setType(
											Material.STAINED_CLAY);
								}
							}
						}
					}
					if (MinMax[Rot][Z][Max] < MinMax[Rot][Z][Min]) {
						// {BLAUGEWONNEN}
						win(Team.BLUE);
					}
				}
			} else if (team==Team.RED) {
				rotLines = +lines;
				blauLines = -lines;
				if (ROTkleiner) {
					MinMax[Rot][Z][Max] += lines;
					MinMax[Blau][Z][Min] += lines;
					Location loc = new Location(world, 0, 0, 0);
					for (int z = MinMax[Rot][Z][Max]; z > MinMax[Rot][Z][Max]
							- lines; z--) {
						loc.setZ(z);
						for (int y = MinMax[Rot][Y][Min]; y < MinMax[Rot][Y][Max] + 1; y++) {
							loc.setY(y);
							for (int x = MinMax[Rot][X][Min]; x < MinMax[Rot][X][Max] + 1; x++) {
								loc.setX(x);
								if (loc.getBlock().getType() == Material.WOOL) {
									// {locZUROTERWOLLE}
									if(EoderZ){
										loc.getBlock().setData((byte) 14);
										loc.getBlock().setType(Material.WOOL);
									}else{
										loc.getBlock().setType(Material.AIR);
										Bukkit.getWorld(loc.getWorld().getName()).playEffect(loc, Effect.STEP_SOUND, 152);
									}
								}
								if (loc.getBlock().getType() == Material.STAINED_CLAY) {
									// {locZUROTERWOLLE}
									loc.getBlock().setData((byte) 14);
									loc.getBlock().setType(
											Material.STAINED_CLAY);
								}
							}
						}
					}
					if (MinMax[Blau][Z][Min] > MinMax[Blau][Z][Max]) {
						// {ROTGEWONNEN}
						win(Team.RED);
					}
				} else {
					MinMax[Blau][Z][Max] -= lines;
					MinMax[Rot][Z][Min] -= lines;
					Location loc = new Location(world, 0, 0, 0);
					for (int z = MinMax[Rot][Z][Min]; z < MinMax[Rot][Z][Min]
							+ lines; z++) {
						loc.setZ(z);
						for (int y = MinMax[Rot][Y][Min]; y < MinMax[Rot][Y][Max] + 1; y++) {
							loc.setY(y);
							for (int x = MinMax[Rot][X][Min]; x < MinMax[Rot][X][Max] + 1; x++) {
								loc.setX(x);
								if (loc.getBlock().getType() == Material.WOOL) {
									// {locZUROTERWOLLE}
									if(EoderZ){
										loc.getBlock().setData((byte) 14);
										loc.getBlock().setType(Material.WOOL);
									}else{
										loc.getBlock().setType(Material.AIR);
										Bukkit.getWorld(loc.getWorld().getName()).playEffect(loc, Effect.STEP_SOUND, 152);
									}
								}

								if (loc.getBlock().getType() == Material.STAINED_CLAY) {
									// {locZUROTERWOLLE}
									loc.getBlock().setData((byte) 14);
									loc.getBlock().setType(
											Material.STAINED_CLAY);
								}

							}
						}
					}
					if (MinMax[Blau][Z][Max] < MinMax[Blau][Z][Min]) {
						// {ROTGEWONNEN}
						win(Team.RED);
					}
				}
			}
			blauLines = MinMax[Blau][Z][Max] - MinMax[Blau][Z][Min] + 1;
			rotLines = MinMax[Rot][Z][Max] - MinMax[Rot][Z][Min] + 1;
		}
		return true;
	}
	
	public void win(Team team){
		
	}
	
	public void XorZ() {
		Spawns[Blau] = instance.getWorldData().getSpawnLocations(Team.BLUE).get(0);
		Spawns[Rot] = instance.getWorldData().getSpawnLocations(Team.RED).get(0);
		if (Spawns[Blau].getBlockX() == Spawns[Rot].getBlockX()) {
			achse = 'Z';
			BLAUkleiner = Spawns[Blau].getBlockZ() < Spawns[Rot]
					.getBlockZ();
			ROTkleiner = Spawns[Rot].getBlockZ() < Spawns[Blau]
					.getBlockZ();
		} else if (Spawns[Blau].getBlockZ() == Spawns[Rot].getBlockZ()) {
			achse = 'X';
			BLAUkleiner = Spawns[Blau].getBlockX() < Spawns[Rot]
					.getBlockX();
			ROTkleiner = Spawns[Rot].getBlockX() < Spawns[Blau]
					.getBlockX();
		} else {
			System.out
					.println("Die beiden Spawns sind nicht parallel zueinander!");
			return;
		}

	}
}
