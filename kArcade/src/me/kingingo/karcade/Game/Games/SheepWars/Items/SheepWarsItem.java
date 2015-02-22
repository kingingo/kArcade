package me.kingingo.karcade.Game.Games.SheepWars.Items;

import lombok.Setter;
import me.kingingo.kcore.Listener.kListener;
import me.kingingo.kcore.Util.UtilItem;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class SheepWarsItem extends kListener{

	@Setter
	private ItemStack item;
	
	public SheepWarsItem(JavaPlugin instance,ItemStack item){
		super(instance,"SWItem:"+item.getItemMeta().getDisplayName());
		this.item=item;
	}
	
	public SheepWarsItem(JavaPlugin instance,int id,byte idnach,String name){
		super(instance,"SWItem:"+name);
		this.item=UtilItem.RenameItem(new ItemStack(id,1,idnach), name);
	}
	
	public SheepWarsItem(JavaPlugin instance,int id,String name){
		super(instance,"SWItem:"+name);
		this.item=UtilItem.RenameItem(new ItemStack(id,1), name);
	}
	
	public String getName(){
		if(item.hasItemMeta()&&item.getItemMeta().hasDisplayName()){
			return item.getItemMeta().getDisplayName();
		}
		return "null";
	}
	
	public boolean equal(ItemStack item){
		return UtilItem.ItemNameEquals(item, this.item);
	}
	
	public ItemStack getItem(){
		return item.clone();
	}
	
	public void add(Player player){
		player.getInventory().addItem(item.clone());
	}
	
}
