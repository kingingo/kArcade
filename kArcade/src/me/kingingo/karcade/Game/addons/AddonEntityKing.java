package me.kingingo.karcade.Game.addons;

import java.util.ArrayList;
import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;
import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Enum.PlayerState;
import me.kingingo.karcade.Game.addons.Events.AddonEntityKingDeathEvent;
import me.kingingo.kcore.Hologram.nametags.NameTagMessage;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class AddonEntityKing implements Listener {
	
	@Getter
	kArcadeManager manager;;
	@Getter
	ArrayList<Creature> creature = new ArrayList<>();
	@Getter
	@Setter
	boolean move=false;
	@Getter
	@Setter
	boolean Damage=false;
	@Getter
	@Setter
	boolean ProjectileDamage=false;
	@Getter
	HashMap<Creature,Double> Heal = new HashMap<>();
	@Getter
	HashMap<Creature, NameTagMessage> NameTagMessage = new HashMap<>();
	
	public AddonEntityKing(kArcadeManager manager,Location[] locs,EntityType type,String Name){
		this.manager=manager;
		Creature e;
		for(Location loc : locs){
			loc.getWorld().loadChunk(loc.getWorld().getChunkAt(loc));
			e=manager.getPetManager().AddPetWithOutOwner(Name,false, type, loc);
			this.creature.add(e);
			this.Heal.put(e, 100D);
		}
		Bukkit.getPluginManager().registerEvents(this, manager.getInstance());
	}
	
	public AddonEntityKing(kArcadeManager manager,ArrayList<Location> locs,EntityType type,String Name){
		this.manager=manager;
		Creature e;
		for(Location loc : locs){
			loc.getWorld().loadChunk(loc.getWorld().getChunkAt(loc));
			e=manager.getPetManager().AddPetWithOutOwner(Name,false, type, loc);
			this.creature.add(e);
			this.Heal.put(e, 100D);
		}
		Bukkit.getPluginManager().registerEvents(this, manager.getInstance());
	}
	
	public void setHealt(Creature e,double h){
		getHeal().put(e, h);
		if(!(e instanceof LivingEntity))return;
		((LivingEntity)e).setCustomName(((LivingEntity)e).getCustomName().split(" ")[0]+" §c"+h+"❤");
	}
	
	public double getHealt(Entity e){
		return getHeal().get(e);
	}
	
	public boolean is(Entity e){
		for(Creature c : getCreature()){
			if(c.getEntityId()==e.getEntityId()){
				return true;
			}
		}
		return false;
	}
	
	ArrayList<Creature> list = new ArrayList<>();
	@EventHandler
	public void Testeer(UpdateEvent ev){
		if(ev.getType()!=UpdateType.MIN_005)return;

		for(Creature c : creature){
			try{
				if(c.isDead()){
					AddonEntityKingDeathEvent e = new AddonEntityKingDeathEvent(c,null);
					Bukkit.getPluginManager().callEvent(e);
					list.add(c);
				}
			}catch(NullPointerException e){
				System.err.println("[AddonEntityKing] Fehler: Testeer NullPointerException!");
			}
		}
		
		for(Creature c : list)creature.remove(c);
		list.clear();
	}
	
	@EventHandler
	public void Death(EntityDeathEvent ev){
		if(is(ev.getEntity())){
			AddonEntityKingDeathEvent e = new AddonEntityKingDeathEvent(ev.getEntity(),ev.getEntity().getKiller());
			Bukkit.getPluginManager().callEvent(e);
		}
	}
	
	@EventHandler
	public void Target(EntityTargetEvent ev){
		if(is(ev.getEntity())){
			if(!move)ev.setCancelled(true);
		}
	}
	
	@EventHandler
	public void TargetLivingEntity(EntityTargetLivingEntityEvent ev){
		if(is(ev.getEntity())){
			if(!move)ev.setCancelled(true);
		}
	}
	
	@EventHandler
	public void Damage(EntityDamageEvent ev){
		if(!Damage&&is(ev.getEntity())){
			ev.setCancelled(true);
		}
	}
	
	double h;
	@EventHandler(priority=EventPriority.HIGHEST)
	public void EntityDamageByEntity(final EntityDamageByEntityEvent ev){
		if(!(ev.getDamager() instanceof Player))return;
		if(ev.getEntity() instanceof Creature&&is(ev.getEntity())){
			if(manager.getGame().getGameList().isPlayerState( ((Player)ev.getDamager()) )!=PlayerState.IN){
				ev.setCancelled(true);
				return;
			}
				ev.setCancelled(false);
				h = getHealt(ev.getEntity());
				h=h-getD( ((Player)ev.getDamager()).getItemInHand() );
				if(h<=0){
					ev.setDamage(50);
				}else{
					ev.setDamage(0);
				}
				((Creature)ev.getEntity()).setTarget(((Player)ev.getDamager()));
				setHealt(((Creature)ev.getEntity()), h);
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(getManager().getInstance(), new Runnable() {
                    public void run() {
                        ev.getEntity().setVelocity(new Vector());
                    }
                }, 1L);
		}
	}
	
	public int getD(ItemStack i){
		switch(i.getType()){
		case WOOD_SWORD:return 5;
		case IRON_SWORD:return 7;
		case STONE_SWORD:return 6;
		case DIAMOND_SWORD:return 8;
		case GOLD_SWORD:return 5;
		default:
			return 4;
		}
	}
	
}
