package eu.epicpvp.karcade.Game.Single.Games.SkyWars.LuckyWars.Items;

import org.bukkit.inventory.ItemStack;

import eu.epicpvp.karcade.Game.Single.Games.SkyWars.LuckyWars.LuckyAddon;
import lombok.Getter;
import lombok.Setter;

public class LuckyItem{
	@Getter
	@Setter
	private LuckyAddon addon;
	private ItemStack item;
	@Getter
	private double chance;

	public LuckyItem(ItemStack item,double chance) {
		this.item=item;
		this.chance=chance;
	}
	
	public ItemStack getItem(){
		return item.clone();
	}
}
