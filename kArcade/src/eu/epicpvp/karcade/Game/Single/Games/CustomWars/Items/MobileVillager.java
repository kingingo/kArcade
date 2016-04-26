package eu.epicpvp.karcade.Game.Single.Games.CustomWars.Items;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import eu.epicpvp.karcade.Game.Multi.Games.CustomWars1vs1.UtilCustomWars1vs1;
import eu.epicpvp.karcade.Game.Single.Games.CustomWars.CustomWars;
import eu.epicpvp.karcade.Game.Single.Games.CustomWars.CustomWarsItem;
import eu.epicpvp.kcore.Util.UtilEnt;
import eu.epicpvp.kcore.Util.UtilInv;
import eu.epicpvp.kcore.Util.UtilItem;

public class MobileVillager extends CustomWarsItem{

	public MobileVillager(CustomWars instance) {
		super(instance, UtilItem.RenameItem(new ItemStack(Material.ARMOR_STAND), "§cMobile-Villager"), CustomWars.Gold(8));
	}

	@EventHandler
	public void inter(PlayerInteractEvent ev){
		if(ev.getPlayer().getItemInHand()!=null){
			if(equal(ev.getPlayer().getItemInHand())){
				UtilInv.remove(ev.getPlayer(), ev.getPlayer().getItemInHand().getType(), ev.getPlayer().getItemInHand().getData().getData(), 1);
			
				UtilEnt.setNoAI(UtilCustomWars1vs1.setVillager(getInstance().getManager().getInstance(),getInstance().getItems(),getInstance().getTeam(ev.getPlayer()),ev.getPlayer().getLocation(), EntityType.VILLAGER).getVillager(), true);;
			}
		}
	}
}
