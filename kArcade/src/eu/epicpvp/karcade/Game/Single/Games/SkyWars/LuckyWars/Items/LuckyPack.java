package eu.epicpvp.karcade.Game.Single.Games.SkyWars.LuckyWars.Items;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import eu.epicpvp.datenclient.client.Callback;
import eu.epicpvp.kcore.Util.UtilItem;

public class LuckyPack extends LuckyClickItem{

	public LuckyPack(double chance) {
		super(UtilItem.RenameItem(new ItemStack(Material.GHAST_TEAR), "§6LuckyPack"), chance,new Callback<Player>(){

			@Override
			public void call(Player player, Throwable exception) {
				player.getLocation().getBlock().getRelative(BlockFace.NORTH).setType(Material.ENCHANTMENT_TABLE);
				player.getLocation().getBlock().getRelative(BlockFace.NORTH).getRelative(BlockFace.EAST).setType(Material.ANVIL);
			}

		});
	}

}
