package me.kingingo.karcade.Game.Games.TroubleInMinecraft.Traitor.Item;

import java.util.HashMap;

import me.kingingo.karcade.Game.Games.TroubleInMinecraft.TroubleInMinecraft;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.Traitor.Shop;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.UtilItem;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Radar implements Listener,Shop{

	ItemStack item=UtilItem.RenameItem(new ItemStack(Material.COMPASS), "§7Radar");
	HashMap<Player,Player> list = new HashMap<>();
	TroubleInMinecraft TTT;
	HashMap<Player,BlockFace> face = new HashMap<>();
	
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
	
	Player r;
	Location l;
	BlockFace f;
	@EventHandler
	public void Updater(UpdateEvent ev){
		if(ev.getType()!=UpdateType.FASTER)return;
		for(Player p : list.keySet()){
			if(!p.isOnline())continue;
			r=list.get(p);
			if(list.get(p)==null){
				l = p.getCompassTarget();
				if(face.containsKey(p)){
					f=face.get(p);
					
					if(f==BlockFace.NORTH){
						f=BlockFace.EAST;
					}else if(f==BlockFace.EAST){
						f=BlockFace.SOUTH;
					}else if(f==BlockFace.SOUTH){
						f=BlockFace.WEST;
					}else if(f==BlockFace.WEST){
						f=BlockFace.NORTH;
					}
					l=l.getBlock().getRelative(f).getLocation();
					
				}else{
					f=BlockFace.NORTH;
					l=l.getBlock().getRelative(f).getLocation();
				}
				face.put(p, f);
				p.setCompassTarget(l);
				continue;
			}
			
			if(r.isOnline()){
				p.setCompassTarget(r.getLocation());
			}else{
				l = p.getCompassTarget();
				if(face.containsKey(p)){
					f=face.get(p);
					
					if(f==BlockFace.NORTH){
						f=BlockFace.EAST;
					}else if(f==BlockFace.EAST){
						f=BlockFace.SOUTH;
					}else if(f==BlockFace.SOUTH){
						f=BlockFace.WEST;
					}else if(f==BlockFace.WEST){
						f=BlockFace.NORTH;
					}
					l=l.getBlock().getRelative(f).getLocation();
					
				}else{
					f=BlockFace.NORTH;
					l=l.getBlock().getRelative(f).getLocation();
				}
				face.put(p, f);
				p.setCompassTarget(l);
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
