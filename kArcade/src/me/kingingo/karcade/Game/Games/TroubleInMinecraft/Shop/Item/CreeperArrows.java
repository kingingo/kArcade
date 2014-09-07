package me.kingingo.karcade.Game.Games.TroubleInMinecraft.Shop.Item;

import java.util.HashMap;

import lombok.Getter;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.TroubleInMinecraft;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.Shop.IShop;
import me.kingingo.kcore.kListener;
import me.kingingo.kcore.Util.UtilItem;

import org.bukkit.entity.Egg;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class CreeperArrows extends kListener implements IShop{

	@Getter
	TroubleInMinecraft TTT;
	@Getter
	private HashMap<Player,Integer> list = new HashMap<>();
	
	public CreeperArrows(TroubleInMinecraft TTT) {
		super(TTT.getManager().getInstance(), "[CreeperArrows]");
		this.TTT=TTT;
	}

	@Override
	public int getPunkte() {
		return 2;
	}

	@Override
	public ItemStack getShopItem() {
		ItemStack i = UtilItem.RenameItem(new ItemStack(Material.SKULL_ITEM,1,(byte)4), "§aCreeper Egg's §7("+getPunkte()+" Punkte)");
		UtilItem.SetDescriptions(i, new String[]{
				"§7Man bekommt 3 Eier womit man §aCreeper§7 Spawnen kann."
		});
		return i;
	}

	@EventHandler
	public void EggLaunch(ProjectileLaunchEvent ev){
		
	}
	
	@EventHandler
	public void Egg(ProjectileHitEvent ev){
		if(ev.getEntity() instanceof Egg){
			Egg egg = (Egg)ev.getEntity();
			
		}
	}
	
	@Override
	public void add(Player p) {
		p.getInventory().addItem(UtilItem.RenameItem(new ItemStack(Material.EGG,3), "§aCreeper Egg's"));
	}

}
