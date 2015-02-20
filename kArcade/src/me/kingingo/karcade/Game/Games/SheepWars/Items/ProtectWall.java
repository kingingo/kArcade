package me.kingingo.karcade.Game.Games.SheepWars.Items;

import me.kingingo.kcore.Util.UtilDirection;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilEvent.ActionType;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class ProtectWall extends SheepWarsItem{

	public ProtectWall(JavaPlugin instance) {
		super(instance, UtilItem.addEnchantmentGlow(UtilItem.RenameItem(new ItemStack(Material.GLASS), "§bProtectWall")) );
	}

	@EventHandler(priority=EventPriority.LOW)
	public void BlockSet(PlayerInteractEvent ev){
		if(ev.hasBlock()){
			if(UtilEvent.isAction(ev, ActionType.R_BLOCK)){
				if(equal(ev.getPlayer().getItemInHand())){
					UtilDirection d = UtilDirection.getCardinalDirection(ev.getPlayer()).nextDirection2();
					
					Block b = ev.getClickedBlock();		
					for(int i = 4; i>0;i--){
						b.setType(Material.SANDSTONE);
						for(int a = 1; i<4; a++){
							b.getWorld().getBlockAt(b.getX(), b.getY()+a, b.getZ()).setType(Material.SANDSTONE);
						}

						b=b.getRelative(d.getBlockFace());
					}
				}
			}
		}
	}
	
}
