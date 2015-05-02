package me.kingingo.karcade.Game.Games.SheepWars.Items;

import me.kingingo.kcore.Util.UtilDirection;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.UtilInv;
import me.kingingo.kcore.Util.UtilItem;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Bridge extends SheepWarsItem{

	public Bridge(JavaPlugin instance) {
		super(instance, UtilItem.addEnchantmentGlow(UtilItem.RenameItem(new ItemStack(Material.SANDSTONE), "§bBrigde")));
	}
	
	@EventHandler(priority=EventPriority.LOW)
	public void BlockSet(PlayerInteractEvent ev){
		if(ev.hasBlock()){
			if(UtilEvent.isAction(ev, ActionType.R_BLOCK)){
				if(equal(ev.getPlayer().getItemInHand())){
					ev.setCancelled(true);
					UtilInv.remove(ev.getPlayer(), ev.getPlayer().getItemInHand().getType(), ev.getPlayer().getItemInHand().getData().getData(), 1);
					UtilDirection d = UtilDirection.getCardinalDirection(ev.getPlayer());
					Block b = ev.getClickedBlock();		
					for(int i = 100; i>0;i--){
						b=b.getRelative(d.getBlockFace());
						if(i<=20||b.getType()==Material.AIR){
							b.setType(Material.SANDSTONE);
						}else{
							break;
						}
					}
				}
			}
		}
	}

}
