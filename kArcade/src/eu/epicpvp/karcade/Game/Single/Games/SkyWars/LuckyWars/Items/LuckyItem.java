package eu.epicpvp.karcade.Game.Single.Games.SkyWars.LuckyWars.Items;

import org.bukkit.inventory.ItemStack;

import eu.epicpvp.karcade.Game.Single.Games.SkyWars.LuckyWars.LuckyAddon;
import eu.epicpvp.kcore.Util.UtilMath;
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
	@Setter
	private int amount_min=0;
	@Setter
	private int amount_max=0;
	
	public LuckyItem(ItemStack item,double chance) {
		this(new ItemStack[]{item},0,0,chance);
	}
	
	public LuckyItem(ItemStack[] items,double chance) {
		this(items,0,0,chance);
	}
	
	public LuckyItem(ItemStack item,int amount_min,int amount_max,double chance) {
		this(new ItemStack[]{item},amount_min,amount_max,chance);
	}

	public LuckyItem(ItemStack[] items,int amount_min,int amount_max,double chance) {
		this.items=items;
		this.amount_min=amount_min;
		this.amount_max=amount_max;
		this.chance=chance;
	}
	
	public LuckyItem(double chance) {
		this(new ItemStack[]{},0,0,chance);
	}
	
	public void setItem(ItemStack item){
		this.items=new ItemStack[]{item};
	}
	
	public ItemStack getItem(){
		if(amount_min!=0&&amount_max!=0){
			ItemStack item = items[0].clone();
			item.setAmount(UtilMath.RandomInt(amount_max, amount_min));
			return item;
		}
		
		return items[0];
	}
	
	public ItemStack[] getItems(){
		if(amount_max!=0&&amount_min!=0){
			ItemStack[] items_clone = items.clone();
			for(ItemStack item : items_clone)item.setAmount(UtilMath.RandomInt(amount_max, amount_min));
			return items_clone;
		}
		
		return items.clone();
	}
}
