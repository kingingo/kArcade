package me.kingingo.karcade.Game.Games.TroubleInMinecraft.Traitor.Item;

import java.util.ArrayList;

import lombok.Getter;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.Traitor.Shop;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilMath;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Tester_Spoofer implements Shop{

	@Getter
	ArrayList<Player> test = new ArrayList<>();
	
	public Tester_Spoofer(){}
	
	public boolean Is(Player p){
		if(!test.contains(p))return false;
		int r = UtilMath.RandomInt(100, 0);
		if(r<=75){
			return true;
		}else{
			return false;
		}
	}
	
	public void add(Player p){
		test.add(p);
	}
	
	public int getPunkte(){
		return 4;
	}
	
	public ItemStack getShopItem(){
		ItemStack i = UtilItem.RenameItem(new ItemStack(Material.IRON_BLOCK), "§cTester-Spoofer §7("+getPunkte()+" Punkte)");
		UtilItem.SetDescriptions(i, new String[]{
				"§7Traitoren werden zu 75%",
				"§7im Tester also Innocent angezeigt."
		});
		return i;
	}
	
}
