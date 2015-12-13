package me.kingingo.karcade.Game.Single.Addons;

import me.kingingo.karcade.Game.Single.SingleGame;
import me.kingingo.kcore.Listener.kListener;

import org.bukkit.Location;
import org.bukkit.WorldBorder;

public class AddonWorldBorder extends kListener{
	private WorldBorder border;
	
	public AddonWorldBorder(SingleGame game,Location center,double radius){
		super(game.getManager().getInstance(), "AddonWorldBorder");
		this.border=center.getWorld().getWorldBorder();
		
		this.border.setCenter(center);
		this.border.setSize(radius*2);
		this.border.setDamageBuffer(0.0);
		this.border.setDamageAmount(1.0);
		this.border.setWarningDistance(20);
		
		Log("Radius: "+this.border.getSize());
	}
	
	public double getSize(){
		return this.border.getSize();
	}
	
	public double getRadius(){
		return this.border.getSize()/2;
	}
	
	public void setSize(double size){
		this.border.setSize(size);
	}
	
	public void setRadius(double radius){
		this.border.setSize(radius*2);
	}
	
	public boolean isInGrenze(Location loc){
		if(loc.getX() < getMaxX() && loc.getX() > getMinX() && loc.getZ() > getMinZ() && loc.getZ() < getMaxZ())return true;
		return false;
	}
	
	public double getMaxY(){
		return 255.0;
	}
	
	public double getMinY(){
		return this.border.getCenter().getY();
	}
	
	public double getMinZ() {
		return this.border.getCenter().getZ() - getRadius();
	}

	public double getMinX() {
		return this.border.getCenter().getX() - getRadius();
	}

	public double getMaxZ() {
		return this.border.getCenter().getZ() + getRadius();
	}

	public double getMaxX() {
		return this.border.getCenter().getX() + getRadius();
	}

}
