package eu.epicpvp.karcade.Game.Single.Games.SkyWars.LuckyWars.Items;

import org.bukkit.inventory.ItemStack;

import eu.epicpvp.karcade.Game.Single.Games.SkyWars.LuckyWars.LuckyAddon;
import lombok.Getter;
import lombok.Setter;

public class LuckyItem{
	@Getter
	@Setter
	private LuckyAddon addon;
	@Setter
	private ItemStack[] items;
	@Getter
	private double chance;
	
	public LuckyItem(ItemStack item,double chance) {
		this(new ItemStack[]{item},chance);
	}

	public LuckyItem(ItemStack[] items,double chance) {
		this.items=items;
		this.chance=chance;
	}
	
	public LuckyItem(double chance) {
		this(new ItemStack[]{},chance);
	}
	
	public void setItem(ItemStack item){
		this.items=new ItemStack[]{item};
	}
	
	public ItemStack getItem(){
		return items[0];
	}
	
	public ItemStack[] getItems(){
		return items.clone();
	}
}
