package me.kingingo.karcade.Game.Games.TroubleInMinecraft.Shop.Item;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import me.kingingo.karcade.Game.Games.TroubleInMinecraft.TroubleInMinecraft;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.Shop.IShop;
import me.kingingo.kcore.kListener;
import me.kingingo.kcore.Util.UtilItem;

public class DNA_TEST extends kListener implements IShop{

	private TroubleInMinecraft TTT;
	
	public DNA_TEST(TroubleInMinecraft TTT) {
		super(TTT.getManager().getInstance(), "[DNA-TEST]");
		this.TTT=TTT;
	}

	@Override
	public int getPunkte() {
		return 2;
	}

	@Override
	public ItemStack getShopItem() {
		ItemStack i = UtilItem.RenameItem(new ItemStack(Material.BLAZE_ROD,1), "§aDNA-TESTER §7("+getPunkte()+" Punkte)");
		UtilItem.SetDescriptions(i, new String[]{
				"§7Untersucht Leichen auf genauere Informationen."
		});
		return i;
	}

	@Override
	public void add(Player p) {
		
	}

}
