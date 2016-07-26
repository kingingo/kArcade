package eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import eu.epicpvp.karcade.Game.Single.Games.Falldown.Falldown;
import eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.Events.PlayerUseBrewItemEvent;
import eu.epicpvp.kcore.Listener.kListener;
import lombok.Getter;

public class BrewItem extends kListener {

	private ItemStack item;
	@Getter
	private Integer[] brewing_items;
	@Getter
	private int power;
	@Getter
	private Falldown falldown;

	public BrewItem(int power, ItemStack item, Integer[] brewing_items, Falldown falldown) {
		super(falldown.getManager().getInstance(), item.getItemMeta().getDisplayName());
		this.item = item;
		this.brewing_items = brewing_items;
		this.power = power;
		this.falldown = falldown;
	}

	public ItemStack getRealItem() {
		return item;
	}

	public ItemStack getItem() {
		return item.clone();
	}

	public boolean fireEvent(Player player) {
		PlayerUseBrewItemEvent ev = new PlayerUseBrewItemEvent(player, this);
		Bukkit.getPluginManager().callEvent(ev);
		return ev.isCancelled();
	}

	public boolean check(Integer[] id, Player player) {
		boolean ok = false;
		Integer[] ids = new Integer[3];
		ids = brewing_items.clone();

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

		if ((id[0].equals(ids[0])) && (id[1].equals(ids[1])) && (id[2].equals(ids[2]))) {
			ok = true;
			player.getInventory().addItem(item.clone());
		}

		return ok;
	}

}
