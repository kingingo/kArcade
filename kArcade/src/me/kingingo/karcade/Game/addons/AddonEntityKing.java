package me.kingingo.karcade.Game.addons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import lombok.Getter;
import lombok.Setter;
import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Enum.PlayerState;
import me.kingingo.karcade.Game.addons.Events.AddonEntityKingDeathEvent;
import me.kingingo.kcore.Hologram.nametags.NameTagMessage;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.UtilMath;
import me.kingingo.kcore.Util.UtilPlayer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Wolf;
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
	private kArcadeManager manager;;
	@Getter
	private ArrayList<Creature> creature = new ArrayList<>();
	@Getter
	@Setter
	private boolean move=false;
	@Getter
	@Setter
	private boolean Damage=false;
	@Getter
	@Setter
	private boolean attack=false;
	@Getter
	@Setter
	private double attack_damage=1.0;
	@Getter
	@Setter
	private boolean ProjectileDamage=false;
	@Getter
	private HashMap<Creature,Double> Heal = new HashMap<>();
	@Getter
	private HashMap<Creature, NameTagMessage> NameTagMessage = new HashMap<>();
	
	public AddonEntityKing(kArcadeManager manager){
		this.manager=manager;
		getManager().getPetManager().setDistance(24);
		Bukkit.getPluginManager().registerEvents(this, manager.getInstance());
	}
	
	public void spawnMob(Location loc,EntityType type,String Name){
			loc.getWorld().loadChunk(loc.getWorld().getChunkAt(loc));
			Creature e=manager.getPetManager().AddPetWithOutOwner(Name,false, type, loc);
			this.creature.add(e);
			this.Heal.put(e, 100D);
	}
	
	public void spawnMobs(Location[] locs,EntityType type,String Name){
		Creature e;
		for(Location loc : locs){
			loc.getWorld().loadChunk(loc.getWorld().getChunkAt(loc));
			e=manager.getPetManager().AddPetWithOutOwner(Name,false, type, loc);
			this.creature.add(e);
			this.Heal.put(e, 100D);
		}
	}
	
	public void spawnMobs(ArrayList<Location> locs,EntityType type,String Name){
		Creature e;
		for(Location loc : locs){
			loc.getWorld().loadChunk(loc.getWorld().getChunkAt(loc));
			e=manager.getPetManager().AddPetWithOutOwner(Name,false, type, loc);
			this.creature.add(e);
			this.Heal.put(e, 100D);
		}
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
	
	LinkedList<Player> l;
	Player random;
	@EventHandler
	public void Attack(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SLOW)return;
		if(!attack)return;
		for(Creature c : getCreature()){
			l=UtilPlayer.getNearby(c.getLocation(), 5);
			if(!l.isEmpty()){
				random=(Player)l.get(UtilMath.r(l.size()));
				if(c.getTarget()!=null&&c.getTarget()==random)continue;
				c.setTarget(random);
			}
		}
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
		if(ev.getEntity() instanceof Creature&&is(ev.getEntity())){
			ev.setCancelled(false);
			if(ev.getDamager() instanceof Player){
				if(manager.getGame().getGameList().isPlayerState( ((Player)ev.getDamager()) )!=PlayerState.IN){
					ev.setCancelled(true);
					return;
				}
				if(!(ev.getDamage()<=0)){
					h = getHealt(ev.getEntity());
					h=h-getD( ((Player)ev.getDamager()).getItemInHand() );
					if(h<=0){
						ev.setDamage(50);
					}else{
						ev.setDamage(0);
					}
					setHealt(((Creature)ev.getEntity()), h);
				}
			}else if(ev.getDamager() instanceof Projectile){
				ev.setCancelled(true);
				if(!(ev.getDamage()<=0)){
					h = getHealt(ev.getEntity());
					h=h-7;
					if(h<=0){
						ev.setDamage(50);
					}else{
						ev.setDamage(0);
					}
					setHealt(((Creature)ev.getEntity()), h);
				}
			}
				
			if(ev.getEntity() instanceof Wolf)((Wolf)ev.getEntity()).setAngry(true);
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(getManager().getInstance(), new Runnable() {
                  public void run() {
                	  ev.getEntity().setVelocity(new Vector());
                   }
               }, 1L);
		}else if(ev.getDamager() instanceof Creature && is(ev.getDamager())){
			ev.setDamage(getAttack_damage());
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
