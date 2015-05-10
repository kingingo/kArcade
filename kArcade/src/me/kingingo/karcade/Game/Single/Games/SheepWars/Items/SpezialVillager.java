package me.kingingo.karcade.Game.Single.Games.SheepWars.Items;

import lombok.Getter;
import me.kingingo.karcade.Game.Single.Games.SheepWars.SheepWars;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.UtilInv;
import me.kingingo.kcore.Util.UtilItem;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class SpezialVillager extends SheepWarsItem{

	@Getter
	private SheepWars manager;
	
	public SpezialVillager(SheepWars manager) {
		super(manager.getManager().getInstance(), UtilItem.addEnchantmentGlow(UtilItem.RenameItem(new ItemStack(Material.MOB_SPAWNER), "§bVillagerSpawner")));
		this.manager=manager;
	}

	@EventHandler(priority=EventPriority.LOW)
	public void BlockSet(PlayerInteractEvent ev){
		if(ev.hasBlock()){
			if(UtilEvent.isAction(ev, ActionType.R_BLOCK)){
				if(equal(ev.getPlayer().getItemInHand())){
					ev.setCancelled(true);
					UtilInv.remove(ev.getPlayer(), ev.getPlayer().getItemInHand().getType(), ev.getPlayer().getItemInHand().getData().getData(), 1);
					manager.setSpezialVillager(ev.getClickedBlock().getLocation().add(0, 2, 0), EntityType.VILLAGER);
				}
			}
		}
	}
}
