package eu.epicpvp.karcade.Game.Single.Games.CustomWars.Items;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import eu.epicpvp.karcade.Game.Single.Games.CustomWars.CustomWars;
import eu.epicpvp.karcade.Game.Single.Games.CustomWars.CustomWarsItem;
import eu.epicpvp.kcore.Util.Title;
import eu.epicpvp.kcore.Util.UtilEvent;
import eu.epicpvp.kcore.Util.UtilEvent.ActionType;
import eu.epicpvp.kcore.Util.UtilInv;
import eu.epicpvp.kcore.Util.UtilItem;

public class AlarmSystem extends CustomWarsItem{

	public HashMap<Location,Player> list;
	
	public AlarmSystem(CustomWars instance) {
		super(instance, UtilItem.RenameItem(new ItemStack(Material.STRING), "§cAlarmanlage"), CustomWars.Gold(5));
		this.list=new HashMap<>();
	}

	@EventHandler
	public void inter(PlayerInteractEvent ev){
		if(UtilEvent.isAction(ev, ActionType.BLOCK)){
			if(ev.getPlayer().getItemInHand()!=null&&ev.getPlayer().getItemInHand().getType()==Material.STRING){
				UtilInv.remove(ev.getPlayer(), ev.getPlayer().getItemInHand().getType(), ev.getPlayer().getItemInHand().getData().getData(), 1);
				
				ev.getClickedBlock().getLocation().add(0, 1, 0).getBlock().setType(Material.TRIPWIRE);
				this.list.put(ev.getClickedBlock().getLocation().add(0, 1, 0), ev.getPlayer());
			}
		}
	}
	

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent ev){
		if(ev.getPlayer().getLocation().getBlock().getType() == Material.TRIPWIRE){
			if(list.containsKey(ev.getPlayer().getLocation().getBlock().getLocation())){
				Player p = list.get(ev.getPlayer().getLocation().getBlock().getLocation());
				if(p.getUniqueId() != ev.getPlayer().getUniqueId()){
					if( getInstance().getTeam(ev.getPlayer()) != getInstance().getTeam(p) ){
						Title t = new Title("", "§c§lALARM");
						t.send(p);
						p.playSound(p.getLocation(), Sound.ENDERDRAGON_GROWL, 1f, 1f);
						ev.getPlayer().getLocation().getBlock().setType(Material.AIR);
						list.remove(ev.getPlayer().getLocation().getBlock().getLocation());
					}
				}
			}
		}
	}
}
