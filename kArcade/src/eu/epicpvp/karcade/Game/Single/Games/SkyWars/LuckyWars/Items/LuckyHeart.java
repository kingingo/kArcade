package eu.epicpvp.karcade.Game.Single.Games.SkyWars.LuckyWars.Items;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import eu.epicpvp.datenclient.client.Callback;
import eu.epicpvp.kcore.Util.UtilItem;

public class LuckyHeart extends LuckyClickItem{

	public LuckyHeart(double chance) {
		super(UtilItem.RenameItem(new ItemStack(Material.NETHER_WARTS), "§c§l+ §6Lucky Heart"), chance, new Callback<Player>() {

			@Override
			public void call(Player player, Throwable exception) {
				player.setMaxHealth(player.getMaxHealth()+2);
				player.setHealth(player.getMaxHealth());
			}
		});
	}

}
