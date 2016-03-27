package eu.epicpvp.karcade.Game.Single.Games.CustomWars;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import lombok.Setter;
import eu.epicpvp.kcore.Listener.kListener;
import eu.epicpvp.kcore.Util.UtilItem;

public class CustomWarsItem extends kListener{

	@Setter
	private ItemStack item;
	@Getter
	private CustomWars instance;
	@Getter
	private ItemStack buyItem;
	
	public CustomWarsItem(CustomWars instance,ItemStack item, ItemStack buyItem){
		super(instance.getManager().getInstance(),"CWItem:"+item.getItemMeta().getDisplayName());
		this.item=item;
		this.instance=instance;
		this.buyItem=buyItem;
	}
	
	public CustomWarsItem(CustomWars instance,int id,byte idnach,String name, ItemStack buyItem){
		super(instance.getManager().getInstance(),"CWItem:"+name);
		this.item=UtilItem.RenameItem(new ItemStack(id,1,idnach), name);
		this.instance=instance;
		this.buyItem=buyItem;
	}
	
	public CustomWarsItem(CustomWars instance,int id,String name, ItemStack buyItem){
		super(instance.getManager().getInstance(),"CWItem:"+name);
		this.item=UtilItem.RenameItem(new ItemStack(id,1), name);
		this.instance=instance;
		this.buyItem=buyItem;
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
	
	public ItemStack getRealItem(){
		return item;
	}
	
	public ItemStack getItem(){
		return item.clone();
	}
	
	public void add(Player player){
		player.getInventory().addItem(item.clone());
	}
	
}
