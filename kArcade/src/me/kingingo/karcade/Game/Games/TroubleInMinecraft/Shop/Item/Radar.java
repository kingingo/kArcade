package me.kingingo.karcade.Game.Games.TroubleInMinecraft.Shop.Item;

import java.util.HashMap;

import me.kingingo.karcade.Game.Games.TroubleInMinecraft.TroubleInMinecraft;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.Shop.IShop;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.UtilDirection;
import me.kingingo.kcore.Util.UtilItem;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

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
				"§7Zeigt auf einen ausgewählten Spieler."
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
	
	@EventHandler
	public void Use(PlayerInteractEntityEvent ev){
		if(UtilItem.ItemNameEquals(ev.getPlayer().getItemInHand(), item)&&ev.getRightClicked() instanceof Player){
			Player r = (Player)ev.getRightClicked();
			list.put(ev.getPlayer(), r);
			ev.getPlayer().sendMessage(Text.PREFIX_GAME.getText(TTT.getManager().getTyp().string())+Text.TTT_TRAITOR_SHOP_RADAR_CHANGE.getText(r.getName()));
		}
	}
	
}
