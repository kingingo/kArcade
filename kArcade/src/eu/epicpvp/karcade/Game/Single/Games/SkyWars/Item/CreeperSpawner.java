package eu.epicpvp.karcade.Game.Single.Games.SkyWars.Item;

import java.util.HashMap;
import java.util.TreeMap;

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

import eu.epicpvp.karcade.Game.Single.Games.SkyWars.SkyWars;
import eu.epicpvp.kcore.Enum.Team;
import eu.epicpvp.kcore.LaunchItem.LaunchItem;
import eu.epicpvp.kcore.LaunchItem.LaunchItemEvent;
import eu.epicpvp.kcore.Listener.kListener;
import eu.epicpvp.kcore.Util.UtilEvent;
import eu.epicpvp.kcore.Util.UtilEvent.ActionType;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.kcore.Util.UtilMath;
import eu.epicpvp.kcore.Util.UtilPlayer;
import lombok.Getter;

public class CreeperSpawner extends kListener{

	@Getter
	SkyWars skyWars;
	@Getter
	private HashMap<Creature,Player> list = new HashMap<>();
	
	public CreeperSpawner(SkyWars skyWars) {
		super(skyWars.getManager().getInstance(), "[CreeperArrows]");
		this.skyWars=skyWars;
	}

	@EventHandler
	public void Target(EntityTargetEvent ev){
		if(ev.getEntity() instanceof Creature){
			if(ev.getEntity() instanceof Creeper){
				if(ev.getTarget() instanceof Player){
					Creature c = (Creature)ev.getEntity();
					if(list.containsKey(c)){
						Team t = getSkyWars().getTeam(list.get(c));
						Player target = (Player)ev.getTarget();
						if(getSkyWars().getTeam(target)==t){
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
			if(getSkyWars().getTeam(player)==Team.TRAITOR)continue;
			d=l.get(player);
			player.damage(12/d, a);
		}
	}
	
	@EventHandler
	public void Launch(final PlayerInteractEvent event){
		if(UtilEvent.isAction(event, ActionType.R)&&event.getPlayer().getItemInHand().hasItemMeta()&&event.getPlayer().getItemInHand().getItemMeta().hasDisplayName()){
			if(event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase("§aCreeper Spawner")){
				event.setCancelled(true);
				LaunchItem item = new LaunchItem(event.getPlayer(),4,new LaunchItem.LaunchItemEventHandler(){
					@Override
					public void onLaunchItem(LaunchItemEvent ev) {
						Creature c = (Creature)ev.getItem().getDroppedItem()[0].getLocation().getWorld().spawnCreature(ev.getItem().getDroppedItem()[0].getLocation(), CreatureType.CREEPER);
						Player p = null;
						TreeMap<Double, Player> l = UtilPlayer.getNearby(ev.getItem().getDroppedItem()[0].getLocation(), 9);
						if(!l.isEmpty())p=l.get(UtilMath.r(l.size()));
						c.setTarget(p);
						list.put(c, event.getPlayer());
					}
				});
				getSkyWars().getIlManager().LaunchItem(item);
			}
		}
	}
	
	public ItemStack getItem(int amount) {
		return UtilItem.RenameItem(new ItemStack(Material.SKULL_ITEM,amount,(byte)4), "§aCreeper Spawner");
	}

}
