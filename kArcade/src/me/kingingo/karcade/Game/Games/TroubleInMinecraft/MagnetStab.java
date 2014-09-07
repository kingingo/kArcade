package me.kingingo.karcade.Game.Games.TroubleInMinecraft;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import me.kingingo.kcore.kListener;
import me.kingingo.kcore.NPC.NPC;
import me.kingingo.kcore.NPC.NPCManager;
import me.kingingo.kcore.Util.UtilItem;

public class MagnetStab extends kListener{
	
	@Getter
	private NPCManager manager;
	@Getter
	private ItemStack stab;

	public MagnetStab(NPCManager manager){
		super(manager.getInstance(),"[TTT-MagnetStab]");
		this.manager=manager;
		this.stab=UtilItem.RenameItem(new ItemStack(Material.STICK), "�1Magnet-Stab");
	}
	
	NPC npc;
	Location loc;
	@EventHandler
	public void Interact(PlayerInteractEvent ev){
		if(UtilItem.ItemNameEquals(getStab(), ev.getPlayer().getItemInHand())){
			if(manager.getNPCList()==null||manager.getNPCList().isEmpty())return;
			for(Integer i : manager.getNPCList().keySet()){
				NPC npc = manager.getNPCList().get(i);
				if(!npc.getLoc().getWorld().getName().equalsIgnoreCase(ev.getPlayer().getWorld().getName()))continue;
				if(npc.getLoc().distance(ev.getPlayer().getLocation()) <= 4){
					loc = ev.getPlayer().getLocation().add(0,2,0);
					npc.walk(loc);
					break;
				}
			}
		}
	}
	
}