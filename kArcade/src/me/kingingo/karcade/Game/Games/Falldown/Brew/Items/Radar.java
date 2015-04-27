package me.kingingo.karcade.Game.Games.Falldown.Brew.Items;

import me.kingingo.karcade.Game.Games.Falldown.Falldown;
import me.kingingo.karcade.Game.Games.Falldown.Brew.BrewItem;
import me.kingingo.kcore.Util.UtilItem;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Radar extends BrewItem{

	public Radar(Integer[] brewing_items,Falldown falldown) {
		super(100, UtilItem.Item(new ItemStack(Material.COMPASS,1), new String[]{""}, "§7Radar"), brewing_items, falldown);
	}

}
