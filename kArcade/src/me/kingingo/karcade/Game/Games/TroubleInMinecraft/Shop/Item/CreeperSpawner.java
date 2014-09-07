package me.kingingo.karcade.Game.Games.TroubleInMinecraft.Shop.Item;

import java.util.HashMap;
import java.util.LinkedList;

import lombok.Getter;
import me.kingingo.karcade.Enum.Team;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.TroubleInMinecraft;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.Shop.IShop;
import me.kingingo.kcore.kListener;
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
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class CreeperSpawner extends kListener implements IShop{

	@Getter
	TroubleInMinecraft TTT;
	@Getter
	private HashMap<Creature,Player> list = new HashMap<>();
	
	public CreeperSpawner(TroubleInMinecraft TTT) {
		super(TTT.getManager().getInstance(), "[CreeperArrows]");
		this.TTT=TTT;
	}

	@Override
	public int getPunkte() {
		return 2;
	}

	@Override
	public ItemStack getShopItem() {
		ItemStack i = UtilItem.RenameItem(new ItemStack(Material.SKULL_ITEM,1,(byte)4), "§aCreeper Spawner§7("+getPunkte()+" Punkte)");
		UtilItem.SetDescriptions(i, new String[]{
				"§7Man bekommt 3 Creeper Spawner womit man §aCreeper§7 Spawnen kann."
		});
		return i;
	}

	@EventHandler
	public void Target(EntityTargetEvent ev){
		if(ev.getEntity() instanceof Creature){
			if(ev.getEntity() instanceof Creeper){
				if(ev.getTarget() instanceof Player){
					Creature c = (Creature)ev.getEntity();
					if(list.containsKey(c)){
						Team t = TTT.getTeam(list.get(c));
						Player target = (Player)ev.getTarget();
						if(TTT.getTeam(target)==t){
							ev.setCancelled(true);
						}
					}
				}
			}
		}
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
			if(event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase("§aCreeper Spawner")){
				LaunchItem item = new LaunchItem(event.getPlayer(),4,new LaunchItem.LaunchItemEventHandler(){
					@Override
					public void onLaunchItem(LaunchItemEvent ev) {
						Creature c = (Creature)ev.getItem().getDroppedItem().getLocation().getWorld().spawnCreature(ev.getItem().getDroppedItem().getLocation(), CreatureType.CREEPER);
						Player p = null;
						LinkedList<Player> l = UtilPlayer.getNearby(ev.getItem().getDroppedItem().getLocation(), 9);
						if(!l.isEmpty())p=l.get(UtilMath.r(l.size()));
						c.setTarget(p);
						list.put(c, event.getPlayer());
					}
				});
				TTT.getIlManager().LaunchItem(item);
			}
		}
	}
	
	@Override
	public void add(Player p) {
		p.getInventory().addItem(UtilItem.RenameItem(new ItemStack(Material.SKULL_ITEM,3,(byte)4), "§aCreeper Spawner"));
	}

}
