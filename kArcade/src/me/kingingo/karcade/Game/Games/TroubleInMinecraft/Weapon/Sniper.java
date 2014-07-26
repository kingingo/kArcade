package me.kingingo.karcade.Game.Games.TroubleInMinecraft.Weapon;

import java.util.ArrayList;

import lombok.Getter;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.TroubleInMinecraft;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilMath;
import me.kingingo.kcore.Util.UtilEvent.ActionType;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Sniper implements Listener {

	TroubleInMinecraft TTT;
	ArrayList<Player> l = new ArrayList<>();
	@Getter
	ItemStack item = UtilItem.RenameItem(new ItemStack(Material.BOW), "§eSniper");
	
	public Sniper(TroubleInMinecraft TTT){
		this.TTT=TTT;
		Bukkit.getPluginManager().registerEvents(this, TTT.getManager().getInstance());
	}
	
	Location loc;
	Player p;
	@EventHandler
	public void Bow(EntityShootBowEvent ev){
		if(ev.getEntity() instanceof Player&&UtilItem.ItemNameEquals(item, ev.getBow())){
			p=(Player)ev.getEntity();
			if(isZoom(p)){
				Zoom(p);
			}
			ev.getProjectile().setVelocity(ev.getProjectile().getVelocity().multiply(4));
		}
	}
	
	@EventHandler
	public void Interact(PlayerInteractEvent ev){
		if(UtilEvent.isAction(ev, ActionType.R)&&UtilItem.ItemNameEquals(ev.getPlayer().getItemInHand(),item)){
			if(!isZoom(ev.getPlayer())){
				Zoom(ev.getPlayer());
				l.add(ev.getPlayer());
			}
		}
	}
	
	Player p1;
	@EventHandler
	public void Change(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		for(int i = 0; i<l.size();i++){
			p1=l.get(i);
			if(!UtilItem.ItemNameEquals(p1.getItemInHand(),item)){
				if(isZoom(p1)){
					Zoom(p1);
				}
			}
		}
	}
	
	public void Zoom(Player p){
		if(!p.hasPotionEffect(PotionEffectType.SLOW)){
			p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,2000,4));
		}else{
			p.removePotionEffect(PotionEffectType.SLOW);
		}
	}
	
	public boolean isZoom(Player p){
		return p.hasPotionEffect(PotionEffectType.SLOW);
	}
	
}
