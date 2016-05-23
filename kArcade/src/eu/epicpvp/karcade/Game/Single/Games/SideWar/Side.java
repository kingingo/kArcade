package eu.epicpvp.karcade.Game.Single.Games.SideWar;

import org.bukkit.Location;
import org.bukkit.Material;

import eu.epicpvp.kcore.Enum.Team;
import eu.epicpvp.kcore.Util.UtilDirection;
import lombok.Getter;

public class Side {

	private int X = 0;
	private int Y = 1;
	private int Z = 2;
	private int Min = 0;
	private int Max = 1;
	@Getter
	private int MinMax[][];
	@Getter
	private Team team;
	@Getter
	private Location spawn;
	
	public Side(Team team, Location spawn,Location ecke1,Location ecke2){
		this.spawn=spawn;
		this.team=team;
		this.MinMax=new int[3][2];
		
		if (ecke1.getBlockX() > ecke2.getBlockX()) {
			MinMax[X][Min] = ecke2.getBlockX();
			MinMax[X][Max] = ecke1.getBlockX();
		} else {
			MinMax[X][Max] = ecke2.getBlockX();
			MinMax[X][Min] = ecke1.getBlockX();
		}
		if (ecke1.getBlockY() > ecke2.getBlockY()) {
			MinMax[Y][Min] = ecke2.getBlockY();
			MinMax[Y][Max] = ecke1.getBlockY();
		} else {
			MinMax[Y][Max] = ecke2.getBlockY();
			MinMax[Y][Min] = ecke1.getBlockY();
		}
		if (ecke1.getBlockZ() > ecke2.getBlockZ()) {
			MinMax[Z][Min] = ecke2.getBlockZ();
			MinMax[Z][Max] = ecke1.getBlockZ();
		} else {
			MinMax[Z][Max] = ecke2.getBlockZ();
			MinMax[Z][Min] = ecke1.getBlockZ();
		}
	}
	
	
	public void setLine(Material material, byte data){
		
	}
}
