package me.kingingo.karcade.Game.Single.Games.TroubleInMinecraft.Shop.Item;

import java.util.ArrayList;

import lombok.Getter;
import me.kingingo.karcade.Game.Single.Games.TroubleInMinecraft.TroubleInMinecraft;
import me.kingingo.karcade.Game.Single.Games.TroubleInMinecraft.Shop.IShop;
import me.kingingo.karcade.Game.Single.Games.TroubleInMinecraft.Shop.Item.Events.TesterSpooferEvent;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilMath;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class Tester_Spoofer implements Listener,IShop{

	@Getter
	ArrayList<Player> test = new ArrayList<>();
	
	public Tester_Spoofer(TroubleInMinecraft TTT){
		Bukkit.getPluginManager().registerEvents(this, TTT.getManager().getInstance());
	}
	
	@EventHandler
	public void Tester(TesterSpooferEvent ev){
		if(!test.contains(ev.getPlayer())){
			ev.setCancelled(false);
			return;
		}
		int r = UtilMath.RandomInt(100, 0);
		if(r<=75){
			ev.setCancelled(true);
		}else{
			ev.setCancelled(false);
		}
	}
	
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
