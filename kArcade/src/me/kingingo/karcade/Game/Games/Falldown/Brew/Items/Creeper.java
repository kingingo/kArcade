package me.kingingo.karcade.Game.Games.Falldown.Brew.Items;

import java.util.HashMap;
import java.util.LinkedList;

import lombok.Getter;
import me.kingingo.karcade.Game.Games.Falldown.Falldown;
import me.kingingo.karcade.Game.Games.Falldown.Brew.BrewItem;
import me.kingingo.kcore.LaunchItem.LaunchItem;
import me.kingingo.kcore.LaunchItem.LaunchItemEvent;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilMath;
import me.kingingo.kcore.Util.UtilPlayer;

import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class Creeper extends BrewItem{
	
	@Getter
	private HashMap<Creature,Player> list = new HashMap<>();

	public Creeper(Integer[] items,Falldown instance) {
		super(100, UtilItem.Item(new ItemStack(Material.MONSTER_EGG,UtilMath.RandomInt(6, 3),(byte)91), new String[]{""}, "§aCreeper Spawner"), items, instance);
	}
	
	HashMap<Player,Double> l;
	double d;
	@EventHandler
	public void Explode(EntityExplodeEvent ev){
		if(!(ev.getEntity() instanceof Creature)||!(ev.getEntity() instanceof Creeper))return;
		ev.blockList().clear();
		Player a = list.get( ((Creature)ev.getEntity()) );
		l = UtilPlayer.getInRadius(ev.getLocation(), 5);
		for(Player player : l.keySet()){
			d=l.get(player);
			player.damage(12/d, a);
		}
	}
	
	@EventHandler
	public void Launch(final PlayerInteractEvent event){
		if(UtilEvent.isAction(event, ActionType.R)&&event.getPlayer().getItemInHand().hasItemMeta()&&event.getPlayer().getItemInHand().getItemMeta().hasDisplayName()){
			if(event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(getRealItem().getItemMeta().getDisplayName())){
				event.setCancelled(true);
				
				if(!fireEvent(event.getPlayer())){
					LaunchItem item = new LaunchItem(event.getPlayer(),4,new LaunchItem.LaunchItemEventHandler(){
						@Override
						public void onLaunchItem(LaunchItemEvent ev) {
							Creature c = (Creature)ev.getItem().getDroppedItem()[0].getLocation().getWorld().spawnCreature(ev.getItem().getDroppedItem()[0].getLocation(), CreatureType.CREEPER);
							Player p = null;
							LinkedList<Player> l = UtilPlayer.getNearby(ev.getItem().getDroppedItem()[0].getLocation(), 9);
							if(!l.isEmpty())p=l.get(UtilMath.r(l.size()));
							c.setTarget(p);
							list.put(c, event.getPlayer());
						}
					});
					getFalldown().getIlManager().LaunchItem(item);
				}
			}
		}
	}

}
