package me.kingingo.karcade.Game.Single.Addons;

import java.util.HashMap;

import lombok.Getter;
import me.kingingo.karcade.kArcadeManager;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.UtilItem;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class AddonBagPack implements Listener {
	
	private int i = 0;
	private HashMap<Integer,Inventory> bagpacks = new HashMap<>();
	@Getter
	private boolean Only_One_BagPack_in_Inventory = false;
	
	public AddonBagPack(kArcadeManager manager){
		Bukkit.getPluginManager().registerEvents(this, manager.getInstance());
	}
	
	public ItemStack getNewBagPack(){
		i++;
		ItemStack item = UtilItem.RenameItem(new ItemStack(Material.NAME_TAG,1,(byte)i), "BagPack");
		UtilItem.SetDescriptions(item, new String[]{String.valueOf(i)});
		bagpacks.put(i, Bukkit.createInventory(null, 9,"BagPack"));
		return item;
	}
	
	@EventHandler
	public void Interact(PlayerInteractEvent ev){
		if(UtilEvent.isAction(ev, ActionType.R)){
			if(ev.getPlayer().getItemInHand()!=null){
				if(ev.getPlayer().getItemInHand().getType()==Material.NAME_TAG){
					ev.getPlayer().openInventory(bagpacks.get(Integer.valueOf(ev.getPlayer().getItemInHand().getItemMeta().getLore().get(0))));
				}
			}
		}
	}
	
}