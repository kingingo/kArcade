package me.kingingo.karcade.Game.Games.TroubleInMinecraft.Shop.Item;

import me.kingingo.karcade.Game.Games.TroubleInMinecraft.TroubleInMinecraft;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.Shop.IShop;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.UtilInv;
import me.kingingo.kcore.Util.UtilItem;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class Medipack implements Listener,IShop{

	ItemStack item=UtilItem.RenameItem(new ItemStack(351,1,(byte)1), "§cMedipack");
	
	public Medipack(TroubleInMinecraft TTT){
		Bukkit.getPluginManager().registerEvents(this, TTT.getManager().getInstance());
	}
	
	public void add(Player p){
		p.getInventory().addItem(item.clone());
	}
	
	public int getPunkte(){
		return 1;
	}
	
	public ItemStack getShopItem(){
		ItemStack i = UtilItem.RenameItem(new ItemStack(351,1,(byte)1), "§cMedipack §7("+getPunkte()+" Punkt)");
		UtilItem.SetDescriptions(i, new String[]{
				"§7Heilt 2,5 Herzen."
		});
		return i;
	}
	
	@EventHandler
	public void Use(PlayerInteractEvent ev){
		if(UtilEvent.isAction(ev, ActionType.R)){
			if(ev.getPlayer().getItemInHand()!=null){
				if(UtilItem.ItemNameEquals(ev.getPlayer().getItemInHand(), item)){
					UtilInv.remove(ev.getPlayer(),ev.getPlayer().getItemInHand().getType(),ev.getPlayer().getItemInHand().getData().getData(), 1);
					if(((CraftPlayer)ev.getPlayer()).getHealth()+5>((CraftPlayer)ev.getPlayer()).getMaxHealth()){
						ev.getPlayer().setHealth( ((CraftPlayer)ev.getPlayer()).getMaxHealth() );
					}else{
						ev.getPlayer().setHealth( ((CraftPlayer)ev.getPlayer()).getHealth()+5 );
					}
					ev.setCancelled(true);
				}
			}
		}
	}
	
}
