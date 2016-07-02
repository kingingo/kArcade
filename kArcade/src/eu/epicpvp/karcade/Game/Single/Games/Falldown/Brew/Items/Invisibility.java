package eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.Items;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import eu.epicpvp.karcade.Game.Single.Games.Falldown.Falldown;
import eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.BrewItem;
import eu.epicpvp.kcore.Enum.PlayerState;
import eu.epicpvp.kcore.Util.UtilEvent;
import eu.epicpvp.kcore.Util.UtilEvent.ActionType;
import eu.epicpvp.kcore.Util.UtilInv;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.kcore.Util.UtilServer;

public class Invisibility extends BrewItem{

	public Invisibility(Integer[] brewing_items, Falldown falldown) {
		super(100,UtilItem.Item(new ItemStack(Material.DIAMOND), new String[]{"§6§7 Du wirst kurzzeitig unsichtbar."}, "§e§lInvisibility"), brewing_items, falldown);
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void Launch(final PlayerInteractEvent event){
		if(UtilEvent.isAction(event, ActionType.RIGHT)&&event.getPlayer().getItemInHand().hasItemMeta()&&event.getPlayer().getItemInHand().getItemMeta().hasDisplayName()){
			if(event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(getRealItem().getItemMeta().getDisplayName())){
				event.setCancelled(true);
				if(!fireEvent(event.getPlayer())){
					UtilInv.remove(event.getPlayer(),event.getPlayer().getItemInHand().getType(),event.getPlayer().getItemInHand().getData().getData(), 1);
					
					for(Player player : getFalldown().getGameList().getPlayers(PlayerState.INGAME))player.hidePlayer(event.getPlayer());
					
					Bukkit.getScheduler().scheduleSyncDelayedTask(getFalldown().getManager().getInstance(), new BukkitRunnable() {
						
						@Override
						public void run() {
							if(getFalldown().getGameList().isPlayerState(event.getPlayer()) == PlayerState.INGAME){
								for(Player player : UtilServer.getPlayers())player.showPlayer(event.getPlayer());
								event.getPlayer().getWorld().strikeLightningEffect(event.getPlayer().getLocation());
							}
						}
					}, 20L * 15);
				}
			}
		}
	}
	
}
