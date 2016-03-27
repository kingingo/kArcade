package eu.epicpvp.karcade.Game.Single.Games.TroubleInMinecraft;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import eu.epicpvp.kcore.ItemFake.ItemFake;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.kcore.Util.UtilMath;

public enum TTT_Item {
SCHWERT_HOLZ(UtilItem.RenameItem(new ItemStack(Material.WOOD_SWORD), "§7Holzschwert"),"SCHWERT"),
SCHWERT_STONE(UtilItem.RenameItem(new ItemStack(Material.STONE_SWORD), "§8Steinschwert"),"SCHWERT"),
SCHWERT_IRON(UtilItem.RenameItem(new ItemStack(Material.IRON_SWORD), "§bEisenschwert"),"SCHWERT"),

ARROW(UtilItem.RenameItem(new ItemStack(Material.ARROW), "§7Pfeile"),"ARROW"),
BOW_MINIGUN(TroubleInMinecraft.getMinigun().getItem(),"BOW"),
BOW_SHOTGUN(TroubleInMinecraft.getShotgun().getItem(),"BOW"),
//BOW_BOGEN(UtilItem.RenameItem(new ItemStack(Material.BOW), "§7Bogen"),"BOW"),
BOW_SNIPER(TroubleInMinecraft.getSniper().getItem(),"BOW");

	ItemStack item;
	@Getter
	String typ;
	
	private TTT_Item(ItemStack item,String typ){
		this.item=item;
		this.typ=typ;
	}
	
	public ItemStack getItem(){		
			return item.clone();
	}
	
	public ItemFake setItemFake(Location l){
		if(getTyp().equalsIgnoreCase("ARROW")){
			ItemStack ite = getItem().clone();
			ite.setAmount(UtilMath.RandomInt(20, 5));
			return new ItemFake(l,ite);
		}else{
			return new ItemFake(l,getItem());
		}
	 }
	
}
