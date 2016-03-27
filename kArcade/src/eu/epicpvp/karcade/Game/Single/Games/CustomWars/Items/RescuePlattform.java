package eu.epicpvp.karcade.Game.Single.Games.CustomWars.Items;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import eu.epicpvp.karcade.Game.Single.Games.CustomWars.CustomWars;
import eu.epicpvp.karcade.Game.Single.Games.CustomWars.CustomWarsItem;
import eu.epicpvp.kcore.Util.UtilInv;
import eu.epicpvp.kcore.Util.UtilItem;

public class RescuePlattform extends CustomWarsItem{

	public RescuePlattform(CustomWars instance) {
		super(instance, UtilItem.RenameItem(new ItemStack(Material.SLIME_BALL), "§cRettungsplattform"), CustomWars.Gold(10));
	}

	@EventHandler
	public void inter(PlayerInteractEvent ev){
		if(ev.getPlayer().getItemInHand().getType()==Material.SLIME_BALL){
			UtilInv.remove(ev.getPlayer(), ev.getPlayer().getItemInHand().getType(), ev.getPlayer().getItemInHand().getData().getData(), 1);
			
			Location loc = ev.getPlayer().getLocation().add(-1, -3, -1);
			byte data = getInstance().getTeam(ev.getPlayer()).getItem().getData().getData();
			BlockState[] states = new BlockState[9];
			int i = 0;
			for(int x = 0; x<3; x++){
				for(int z = 0; z<3; z++){
					states[i]=loc.clone().add(x, 0, z).getBlock().getState();
					loc.clone().add(x, 0, z).getBlock().setType(Material.SLIME_BLOCK);
					i++;
				}
			}
			
			Bukkit.getScheduler().scheduleSyncDelayedTask(getInstance().getManager().getInstance(), new BukkitRunnable() {
				
				@Override
				public void run() {
					for(BlockState s : states){
						s.update(true);
					}
				}
			}, 20*30);
		}
	}
}
