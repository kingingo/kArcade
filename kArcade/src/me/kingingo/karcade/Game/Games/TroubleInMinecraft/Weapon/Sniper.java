package me.kingingo.karcade.Game.Games.TroubleInMinecraft.Weapon;

import java.util.ArrayList;

import lombok.Getter;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.TroubleInMinecraft;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.UtilItem;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Sniper implements Listener {

	TroubleInMinecraft TTT;
	ArrayList<Player> l = new ArrayList<>();
	@Getter
	ItemStack item = UtilItem.RenameItem(new ItemStack(Material.BOW), "§eSniper");
	ArrayList<Arrow> shot = new ArrayList<>();
	
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
			shot.add(((Arrow)ev.getProjectile()));
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)
	public void Interact(PlayerInteractEvent ev){
		if(UtilEvent.isAction(ev, ActionType.R)&&UtilItem.ItemNameEquals(ev.getPlayer().getItemInHand(),item)){
			if(!isZoom(ev.getPlayer())){
				Zoom(ev.getPlayer());
				l.add(ev.getPlayer());
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void Damage(EntityDamageByEntityEvent ev){
		if(ev.getEntity() instanceof Player && ev.getDamager() instanceof Arrow){
			Arrow a = (Arrow)ev.getDamager();
			if(shot.contains(a)){
				ev.setDamage(20.0);
				shot.remove(a);
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
