package eu.epicpvp.karcade.Game.Single.Games.SkyWars;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import dev.wolveringer.client.Callback;
import eu.epicpvp.karcade.Game.Single.Addons.VoteHandler.Vote;
import eu.epicpvp.karcade.Game.Single.Addons.VoteHandler.Votes;
import eu.epicpvp.karcade.Game.Single.Games.SkyWars.LuckyWars.LuckyAddon;
import eu.epicpvp.karcade.Game.Single.Games.SkyWars.LuckyWars.Items.LuckyItem;
import eu.epicpvp.karcade.Game.World.GameMap;
import eu.epicpvp.kcore.Lists.kSort;
import eu.epicpvp.kcore.Util.UtilItem;

public class SkyWarsTypeVote extends Votes {

	public SkyWarsTypeVote(SkyWars instance, LuckyItem[] items) {
		super(new Vote[] {
				new Vote<Mode>(Mode.NORMAL,
						UtilItem.RenameItem(new ItemStack(Material.EYE_OF_ENDER), "§aSkyWars §lNORMAL")),
				new Vote<Mode>(Mode.LUCKYWARS,
						UtilItem.RenameItem(new ItemStack(Material.GLOWSTONE_DUST), "§6§lLuckyWars")) },
				UtilItem.RenameItem(new ItemStack(Material.ANVIL), "§eMode Vote"),
				new Callback<ArrayList<kSort<Vote>>>() {

					@Override
					public void call(ArrayList<kSort<Vote>> votes, Throwable exception) {
						Mode mode = ((Vote<Mode>) votes.get(0).getObject()).getValue();

						if (mode == Mode.LUCKYWARS)
							new LuckyAddon(instance, items);
					}
				}, 100);
	}

	private enum Mode {
		NORMAL, LUCKYWARS;
	}
}
