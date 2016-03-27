package eu.epicpvp.karcade.Game.Single.Games.Falldown;

import java.util.Comparator;

import org.bukkit.entity.Entity;

import lombok.Getter;
import lombok.Setter;

public class kDistance implements Comparable<kDistance>{

	@Getter
	@Setter
	private Entity entity;
	@Getter
	@Setter
	private double obj;
	
	public kDistance(Entity entity,double obj){
		this.entity=entity;
		this.obj=obj;
	}
	
	public int compareTo(kDistance compareFruit) {
		double compareQuantity = ((kDistance) compareFruit).getObj(); 
		return (int)(compareQuantity - this.getObj());
	}
	
	public static Comparator<kDistance> DESCENDING
	    = new Comparator<kDistance>() {
	
		public int compare(kDistance r1, kDistance r2) {
			return (int)(r2.getObj() - r1.getObj());
		}
	
	};
	
	public static Comparator<kDistance> ASCENDING 
                          = new Comparator<kDistance>() {

	    public int compare(kDistance r1, kDistance r2) {
			return (int)(r1.getObj() - r2.getObj());
	    }

	};
}
