package me.kingingo.karcade.Game.Games.Falldown.Brew;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class BrewingItem implements Listener{

	@Getter
	private ItemStack item;
	@Getter
	private ItemStack[] brewing_items;
	@Getter
	private int power;
	
	public BrewingItem(int power,ItemStack item,ItemStack[] brewing_items,JavaPlugin instance){
		this.item=item;
		this.brewing_items=brewing_items;
		this.power=power;
		Bukkit.getPluginManager().registerEvents(this, instance);
	}
	
	public boolean check(Integer[] id,Player player) {
		boolean ok = false;
		Integer[] ids = new Integer[3];
		ids[0] = getBrewing_items()[0].getTypeId();
		ids[1] = getBrewing_items()[1].getTypeId();
		ids[2] = getBrewing_items()[2].getTypeId();

		boolean unsortiert = true;
		int temp;
		while (unsortiert) {
			unsortiert = false;
			for (int i = 0; i < ids.length - 1; i++)
				if (ids[i] > ids[i + 1]) {
					temp = ids[i];
					ids[i] = ids[i + 1];
					ids[i + 1] = temp;
					unsortiert = true;
				}
		}
		boolean unsortiert2 = true;
		int temp2;
		while (unsortiert2) {
			unsortiert2 = false;
			for (int i = 0; i < id.length - 1; i++)
				if (id[i] > id[i + 1]) {
					temp2 = id[i];
					id[i] = id[i + 1];
					id[i + 1] = temp2;
					unsortiert2 = true;
				}
		}

		if ((id[0].equals(ids[0])) && (id[1].equals(ids[1]))
				&& (id[2].equals(ids[2]))) {
			ok = true;
			player.getInventory().addItem(item.clone());
		}
		
		return ok;
	}
	
}
