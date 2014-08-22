package me.kingingo.karcade.Game.Games.TroubleInMinecraft;

import lombok.Getter;
import me.kingingo.kcore.ItemFake.ItemFake;
import me.kingingo.kcore.Util.UtilItem;
import net.minecraft.util.com.mojang.authlib.GameProfile;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public enum TTT_Item {
SCHWERT_HOLZ(UtilItem.RenameItem(new ItemStack(Material.WOOD_SWORD), "Holzschwert"),"SCHWERT"),
SCHWERT_STONE(UtilItem.RenameItem(new ItemStack(Material.STONE_SWORD), "Steinschwert"),"SCHWERT"),
SCHWERT_IRON(UtilItem.RenameItem(new ItemStack(Material.IRON_SWORD), "Eisenschwert"),"SCHWERT"),

BOW_MINIGUN(TroubleInMinecraft.getMinigun().getItem(),"BOW"),
BOW_SHOTGUN(TroubleInMinecraft.getShotgun().getItem(),"BOW"),
BOW_BOGEN(UtilItem.RenameItem(new ItemStack(Material.BOW), "Bogen"),"BOW"),
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
	
	public ItemFake setItemFake(Location l,JavaPlugin plugin){
		return new ItemFake(l,getItem(),plugin);
	 }
	
}
