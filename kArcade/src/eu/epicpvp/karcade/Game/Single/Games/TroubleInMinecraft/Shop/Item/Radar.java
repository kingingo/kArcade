package eu.epicpvp.karcade.Game.Single.Games.TroubleInMinecraft.Shop.Item;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import eu.epicpvp.karcade.Game.Single.Games.TroubleInMinecraft.TroubleInMinecraft;
import eu.epicpvp.karcade.Game.Single.Games.TroubleInMinecraft.Shop.IShop;
import eu.epicpvp.kcore.Translation.TranslationHandler;
import eu.epicpvp.kcore.Update.UpdateType;
import eu.epicpvp.kcore.Update.Event.UpdateEvent;
import eu.epicpvp.kcore.Util.UtilDirection;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.kcore.Util.UtilPlayer;

public class Radar implements Listener,IShop{

	ItemStack item=UtilItem.RenameItem(new ItemStack(Material.COMPASS), "§7Radar");
	HashMap<Player,Player> list = new HashMap<>();
	HashMap<Player,UtilDirection> pl = new HashMap<>();
	TroubleInMinecraft TTT;
	
	public Radar(TroubleInMinecraft TTT){
		this.TTT=TTT;
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
		ItemStack i = UtilItem.RenameItem(new ItemStack(Material.COMPASS), "§cRadar §7("+getPunkte()+" Punkt)");
		UtilItem.SetDescriptions(i, new String[]{
				"§7Zeigt auf einen ausgew§hlten Spieler."
		});
		return i;
	}
	
	Location l;
	UtilDirection face;
	Player r;
	@EventHandler
	public void Updater(UpdateEvent ev){
		if(ev.getType()!=UpdateType.FASTER)return;
		for(Player p : list.keySet()){
			if(!p.isOnline())continue;
			r=list.get(p);
			if(r==null||!r.isOnline()){
				if(!pl.containsKey(p)){
					pl.put(p, UtilDirection.NORTH);
				}
				
				face=pl.get(p);
				face=face.nextDirection();
				pl.remove(p);
				pl.put(p, face);
				p.setCompassTarget(face.get(p.getLocation()));
			}else{
				p.setCompassTarget(r.getLocation());
			}
		}
	}
	
	@EventHandler
	public void Death(PlayerDeathEvent ev){
		if(ev.getEntity() instanceof Player){
			Player p = (Player)ev.getEntity();
			if(list.containsValue(p)){
				Player r =null;
				for(Player l : list.keySet()){
					if(list.get(l)==p){
						r=l;
						break;
					}
				}
				
				list.put(r, null);
			}
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void Use(PlayerInteractEntityEvent ev){
		if(UtilItem.ItemNameEquals(ev.getPlayer().getItemInHand(), item)&&ev.getRightClicked() instanceof Player){
			Player r = (Player)ev.getRightClicked();
			list.put(ev.getPlayer(), r);
			UtilPlayer.sendMessage(ev.getPlayer(),TranslationHandler.getText(ev.getPlayer(), "PREFIX_GAME", TTT.getType().getTyp())+TranslationHandler.getText(ev.getPlayer(), "TTT_TRAITOR_SHOP_RADAR_CHANGE", r.getName()));
		}
	}
	
}
