package me.kingingo.karcade.Game.Games.TroubleInMinecraft;

import lombok.Getter;
import me.kingingo.kcore.Util.UtilItem;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.inventory.ItemStack;

public enum TTT_Item {
SCHWERT_HOLZ("VareidePlays",UtilItem.RenameItem(new ItemStack(Material.WOOD_SWORD), "Holzschwert")),
SCHWERT_STONE("Nottrex",UtilItem.RenameItem(new ItemStack(Material.STONE_SWORD), "Steinschwert")),
SCHWERT_IRON("BillTheBuild3r",UtilItem.RenameItem(new ItemStack(Material.IRON_SWORD), "Eisenschwert")),

BOW_MINIGUN("KlausurThaler144",UtilItem.RenameItem(new ItemStack(Material.BOW), "MiniGun")),
BOW_SHOTGUN("IntelliJ",UtilItem.RenameItem(new ItemStack(Material.BOW), "Shotgun")),
BOW_BOGEN("Abmahnung",UtilItem.RenameItem(new ItemStack(Material.BOW), "Bogen")),
BOW_SNIPER("FallingDiamond",UtilItem.RenameItem(new ItemStack(Material.BOW), "Sniper"));

	@Getter
	String nick;
	ItemStack item;
	
	private TTT_Item(String nick,ItemStack item){
		this.nick=nick;
		this.item=item;
	}
	
	public ItemStack getItem(){
		return item.clone();
	}
	
	public void setBlock(Location l){
		Block b = l.getBlock();
		b.setType(Material.SKULL);
		b.setData((byte)3);
		if(l.getBlock().getState() instanceof Skull){
			Skull s = (Skull)l.getBlock().getState();
			s.setOwner(nick);
			s.update(true);
		}
	 }
	
	public void setBlock(Block b){
		b.setType(Material.SKULL);
		b.setData((byte)3);
		if(b.getState() instanceof Skull){
			Skull s = (Skull)b.getState();
			s.setOwner(nick);
			s.update(true);
		}
	 }
	
}
