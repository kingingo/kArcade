package me.kingingo.karcade.Game.Games.TroubleInMinecraft.Traitor.Item;

import java.util.HashMap;

import me.kingingo.karcade.Game.Games.TroubleInMinecraft.TroubleInMinecraft;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.Traitor.Shop;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.UtilItem;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Radar implements Listener,Shop{

	ItemStack item=UtilItem.RenameItem(new ItemStack(Material.COMPASS), "§7Radar");
	HashMap<Player,Player> list = new HashMap<>();
	
	public Radar(TroubleInMinecraft TTT){
		Bukkit.getPluginManager().registerEvents(this, TTT.getManager().getInstance());
	}
	
	public void add(Player p){
		list.put(p, null);
		p.getInventory().addItem(item.clone());
	}
	
	public int getPunkte(){
		return 1;
	}
	
	public ItemStack getShopItem(){
		ItemStack i = UtilItem.RenameItem(new ItemStack(Material.COMPASS), "§cRadar §7("+getPunkte()+" Punkt");
		UtilItem.SetDescriptions(i, new String[]{
				"§7Zeigt auf einen ausgewählten Spieler."
		});
		return i;
	}
	
	Player r;
	Location l;
	@EventHandler
	public void Updater(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		for(Player p : list.keySet()){
			if(list.get(p)==null||!p.isOnline())continue;
			r=list.get(p);
			if(r.isOnline()){
				p.setCompassTarget(r.getLocation());
			}else{
				l = p.getCompassTarget();
				if(l.getYaw()==360){
					l.setYaw(0);
				}else{
					l.setYaw(l.getYaw()+10);
				}
				p.setCompassTarget(l);
			}
		}
	}
	
	@EventHandler
	public void Use(PlayerInteractEntityEvent ev){
		if(ev.getPlayer().getItemInHand()==item&&ev.getRightClicked() instanceof Player){
			Player r = (Player)ev.getRightClicked();
			list.put(ev.getPlayer(), r);
		}
	}
	
}
