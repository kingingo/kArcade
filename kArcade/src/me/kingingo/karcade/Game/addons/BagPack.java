package me.kingingo.karcade.Game.addons;

import java.util.HashMap;

import lombok.Getter;
import me.kingingo.karcade.kArcadeManager;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.UtilInv;
import me.kingingo.kcore.Util.UtilItem;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class BagPack implements Listener {
	
	private int i = 0;
	private HashMap<Integer,Inventory> bagpacks = new HashMap<>();
	@Getter
	private boolean Only_One_BagPack_in_Inventory = false;
	
	public BagPack(kArcadeManager manager){
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
	public void Pickup(PlayerPickupItemEvent ev){
		if(ev.getItem().getItemStack().getType()==Material.NAME_TAG){
			if(UtilInv.contains(ev.getPlayer(), Material.NAME_TAG, (Byte) null, 1))ev.setCancelled(true);
		}
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
