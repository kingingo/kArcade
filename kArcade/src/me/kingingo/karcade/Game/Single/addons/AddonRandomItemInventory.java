package me.kingingo.karcade.Game.Single.addons;

import java.util.ArrayList;

import me.kingingo.karcade.kArcadeManager;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilMath;

import org.bukkit.Bukkit;
import me.kingingo.kcore.Util.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class AddonRandomItemInventory implements Listener{

	private ArrayList<Material> itemlist;
	private Material m;
	
	public AddonRandomItemInventory(kArcadeManager manager,Material m,ArrayList<Material> itemlist){
		Bukkit.getPluginManager().registerEvents(this, manager.getInstance());
		this.itemlist=itemlist;
		this.m=m;
	}
	
	public ItemStack getRandomItemInv(){
		return UtilItem.RenameItem(new ItemStack(m), Color.PURPLE+"Zufalls Item");
	}
	
	@EventHandler
	public void InteractEvent(PlayerInteractEvent ev){
		if(UtilEvent.isAction(ev, ActionType.R)){
			if(ev.getPlayer().getItemInHand().getType()==this.m){
				ev.getPlayer().setItemInHand(null);
				Inventory inv = Bukkit.createInventory(null, 9,"Random Inventory:");
				int r=0;
				for(int i = 0; i < inv.getSize() ; i++){
					r=UtilMath.r(itemlist.size());
					inv.addItem(new ItemStack(itemlist.get(r)));
				}
				ev.getPlayer().openInventory(inv);
			}
		}
	}
	
	@EventHandler
	public void Inv(InventoryClickEvent ev){
		if (!(ev.getWhoClicked() instanceof Player)|| ev.getInventory() == null || ev.getCursor() == null || ev.getCurrentItem() == null)return;
		if(ev.getInventory().getName().equalsIgnoreCase("Random Inventory:")){
			ev.getWhoClicked().getInventory().addItem(ev.getCurrentItem());
			ev.getWhoClicked().closeInventory();
		}
	}
	
}
