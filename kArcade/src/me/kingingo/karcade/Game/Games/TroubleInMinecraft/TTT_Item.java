package me.kingingo.karcade.Game.Games.TroubleInMinecraft;

import lombok.Getter;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilReflection;
import net.minecraft.util.com.mojang.authlib.GameProfile;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.inventory.ItemStack;

public enum TTT_Item {
SCHWERT_HOLZ("VareidePlays",UtilItem.RenameItem(new ItemStack(Material.WOOD_SWORD), "Holzschwert"),"SCHWERT"),
SCHWERT_STONE("Nottrex",UtilItem.RenameItem(new ItemStack(Material.STONE_SWORD), "Steinschwert"),"SCHWERT"),
SCHWERT_IRON("BillTheBuild3r",UtilItem.RenameItem(new ItemStack(Material.IRON_SWORD), "Eisenschwert"),"SCHWERT"),

BOW_MINIGUN("KlausurThaler144",UtilItem.RenameItem(new ItemStack(Material.BOW), "MiniGun"),"BOW"),
BOW_SHOTGUN("IntelliJ",UtilItem.RenameItem(new ItemStack(Material.BOW), "Shotgun"),"BOW"),
BOW_BOGEN("Abmahnung",UtilItem.RenameItem(new ItemStack(Material.BOW), "Bogen"),"BOW"),
BOW_SNIPER("FallingDiamond",UtilItem.RenameItem(new ItemStack(Material.BOW), "Sniper"),"BOW");

	@Getter
	String nick;
	ItemStack item;
	@Getter
	String n;
	@Getter
	GameProfile profil;
	
	private TTT_Item(String nick,ItemStack item,String n){
		this.nick=nick;
		this.profil=profil;
		this.item=item;
		this.n=n;
	}
	
	public ItemStack getItem(){		
			return item.clone();
	}
	
	public void setBlock(Location l){
		Block b = l.getBlock();
		l.getWorld().loadChunk(l.getWorld().getChunkAt(l));
		b.setType(Material.SKULL);
		b.setData((byte)3);
		if(l.getBlock().getState() instanceof Skull){
			Skull s = (Skull)l.getBlock().getState();
			s.setSkullType(SkullType.PLAYER);
			s.setOwner(nick);
			s.update();
		}
		
	 }
	
	public void setBlock(Block b){
		b.getLocation().getWorld().loadChunk(b.getLocation().getWorld().getChunkAt(b.getLocation()));
		b.setType(Material.SKULL);
		b.setData((byte)3);
		if(b.getState() instanceof Skull){
			Skull s = (Skull)b.getState();
			s.setSkullType(SkullType.PLAYER);
			s.setOwner(nick);
			s.update();
		}
	 }
	
}
