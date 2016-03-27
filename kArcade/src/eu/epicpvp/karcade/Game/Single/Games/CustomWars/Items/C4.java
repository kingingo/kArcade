package eu.epicpvp.karcade.Game.Single.Games.CustomWars.Items;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import eu.epicpvp.karcade.Game.Single.Games.CustomWars.CustomWars;
import eu.epicpvp.karcade.Game.Single.Games.CustomWars.CustomWarsItem;
import eu.epicpvp.kcore.Util.UtilInv;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.kcore.Util.UtilNumber;

public class C4 extends CustomWarsItem{

	public HashMap<Integer, ArrayList<Location>> tnt;
	private int amount=0;
	
	public C4(CustomWars instance) {
		super(instance, UtilItem.RenameItem(new ItemStack(Material.TNT), "§cC4"), CustomWars.Gold(3));
		this.tnt=new HashMap<>();
	}
	
	@EventHandler
	public void inter(PlayerInteractEvent ev){
		if(ev.getPlayer().getItemInHand()!=null && ev.getPlayer().getItemInHand().hasItemMeta() && ev.getPlayer().getItemInHand().getItemMeta().hasLore()){
			if(ev.getPlayer().getItemInHand().getType()==Material.TRIPWIRE_HOOK){
				for(Location loc : this.tnt.get( Integer.valueOf(ev.getPlayer().getItemInHand().getItemMeta().getLore().get(0)) )){
					if(loc.getBlock().getType() == Material.TNT){
						loc.getWorld().spawnEntity(loc, EntityType.PRIMED_TNT);
					}
					loc.getBlock().setType(Material.AIR);
				}
				this.tnt.get( Integer.valueOf(ev.getPlayer().getItemInHand().getItemMeta().getLore().get(0)) ).clear();
				this.tnt.get( Integer.valueOf(ev.getPlayer().getItemInHand().getItemMeta().getLore().get(0)) ).remove(ev.getPlayer());
				ev.setCancelled(true);
				UtilInv.remove(ev.getPlayer(), ev.getPlayer().getItemInHand().getType(), ev.getPlayer().getItemInHand().getData().getData(), 1);
			}
		}
	}
	
	@EventHandler
	public void place(BlockPlaceEvent ev){
		if(ev.getItemInHand().hasItemMeta() && ev.getItemInHand().getItemMeta().hasDisplayName() &&
				ev.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(getRealItem().getItemMeta().getDisplayName())){
			
			ItemStack item = UtilInv.getFirstItem(ev.getPlayer().getInventory(), Material.TRIPWIRE_HOOK.getId(), UtilNumber.toByte(0));
			
			if(item == null){
				amount++;
				item=UtilItem.Item(new ItemStack(Material.TRIPWIRE_HOOK), new String[]{amount+""},"§cZ§nder");
				ev.getPlayer().getInventory().addItem(item);
				this.tnt.put(amount, new ArrayList<>());
			}
			
			this.tnt.get( Integer.valueOf(item.getItemMeta().getLore().get(0)) ).add(ev.getBlock().getLocation());
		}
	}
}
