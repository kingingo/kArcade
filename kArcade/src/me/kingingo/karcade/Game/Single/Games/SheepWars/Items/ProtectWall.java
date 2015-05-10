package me.kingingo.karcade.Game.Single.Games.SheepWars.Items;

import me.kingingo.kcore.Util.UtilDirection;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.UtilInv;
import me.kingingo.kcore.Util.UtilItem;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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
					UtilInv.remove(ev.getPlayer(), ev.getPlayer().getItemInHand().getType(), ev.getPlayer().getItemInHand().getData().getData(), 1);
					UtilDirection d = UtilDirection.getCardinalDirection(ev.getPlayer()).nextDirection();
					Block b = ev.getClickedBlock();
					Block b1;
					b=b.getRelative(BlockFace.UP);
					for(int i = 0; i<4; i++){
						b.setType(Material.SANDSTONE);
						b1=b;
						for(int a = 0; a<2; a++){
							b1=b1.getRelative(BlockFace.UP);
							b1.setType(Material.SANDSTONE);
						}
						b=b.getRelative(d.getBlockFace());
					}
				}
			}
		}
	}
	
}
