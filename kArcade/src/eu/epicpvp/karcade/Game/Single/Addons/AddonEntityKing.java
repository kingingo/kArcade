package eu.epicpvp.karcade.Game.Single.Addons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

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

import eu.epicpvp.karcade.Game.Single.SingleGame;
import eu.epicpvp.karcade.Game.Single.Events.AddonEntityKingDeathEvent;
import eu.epicpvp.kcore.Enum.PlayerState;
import eu.epicpvp.kcore.Enum.Zeichen;
import eu.epicpvp.kcore.Hologram.nametags.NameTagMessage;
import eu.epicpvp.kcore.Update.UpdateType;
import eu.epicpvp.kcore.Update.Event.UpdateEvent;
import eu.epicpvp.kcore.Util.UtilMath;
import eu.epicpvp.kcore.Util.UtilPlayer;
import lombok.Getter;
import lombok.Setter;

public class AddonEntityKing implements Listener {
	
	@Getter
	private SingleGame game;
	@Getter
	private ArrayList<Entity> creature = new ArrayList<>();
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
	private HashMap<Entity,Double> Heal = new HashMap<>();
	@Getter
	private HashMap<Entity, NameTagMessage> NameTagMessage = new HashMap<>();
	
	public AddonEntityKing(SingleGame game){
		this.game=game;
		game.getManager().getPetManager().setDistance(24);
		Bukkit.getPluginManager().registerEvents(this, game.getManager().getInstance());
	}
	
	public void spawnMob(Location loc,EntityType type,String Name){
			loc.getWorld().loadChunk(loc.getWorld().getChunkAt(loc));
			Entity e=game.getManager().getPetManager().AddPetWithOutOwner(Name,false, type, loc);
			this.creature.add(e);
			this.Heal.put(e, 100D);
	}
	
	public void spawnMobs(Location[] locs,EntityType type,String Name){
		Entity e;
		for(Location loc : locs){
			loc.getWorld().loadChunk(loc.getWorld().getChunkAt(loc));
			e=game.getManager().getPetManager().AddPetWithOutOwner(Name,false, type, loc);
			this.creature.add(e);
			this.Heal.put(e, 100D);
		}
	}
	
	public void spawnMobs(ArrayList<Location> locs,EntityType type,String Name){
		Entity e;
		for(Location loc : locs){
			loc.getWorld().loadChunk(loc.getWorld().getChunkAt(loc));
			e=game.getManager().getPetManager().AddPetWithOutOwner(Name,false, type, loc);
			this.creature.add(e);
			this.Heal.put(e, 100D);
		}
	}
	
	public void setHealt(Creature e,double h){
		getHeal().put(e, h);
		if(!(e instanceof LivingEntity))return;
		((LivingEntity)e).setCustomName(((LivingEntity)e).getCustomName().split(" ")[0]+" §c"+h+" "+Zeichen.BIG_HERZ.getIcon());
	}
	
	public double getHealt(Entity e){
		return getHeal().get(e);
	}
	
	public boolean is(Entity e){
		for(Entity c : getCreature()){
			if(c.getEntityId()==e.getEntityId()){
				return true;
			}
		}
		return false;
	}
	
	TreeMap<Double, Player> l;
	Player random;
	@EventHandler
	public void Attack(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SLOW)return;
		if(!attack)return;
		for(Entity c : getCreature()){
			l=UtilPlayer.getNearby(c.getLocation(), 5);
			if(!l.isEmpty()){
				random=(Player)l.get(UtilMath.randomInteger(l.size()));
				if(((Creature)c).getTarget()!=null&&((Creature)c).getTarget()==random)continue;
				((Creature)c).setTarget(random);
			}
		}
	}
	
	ArrayList<Entity> list = new ArrayList<>();
	@EventHandler
	public void Testeer(UpdateEvent ev){
		if(ev.getType()!=UpdateType.MIN_005)return;

		for(Entity c : creature){
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
		
		for(Entity c : list)creature.remove(c);
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
				if(game.getGameList().isPlayerState( ((Player)ev.getDamager()) )!=PlayerState.INGAME){
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
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(game.getManager().getInstance(), new Runnable() {
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
