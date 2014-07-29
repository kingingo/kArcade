package me.kingingo.karcade.Game.addons;

import java.util.HashMap;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Enum.Team;
import me.kingingo.karcade.Game.Games.TeamGame;
import me.kingingo.karcade.Game.addons.Events.AddonEntityKingDeathEvent;
import me.kingingo.kcore.Hologram.Hologram;
import me.kingingo.kcore.Hologram.nametags.NameTagMessage;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.UtilServer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class AddonEntityKing implements Listener {
	
	@Getter
	kArcadeManager manager;;
	@Getter
	HashMap<Team,Entity> teams = new HashMap<>();
	@Getter
	@Setter
	boolean move=false;
	@Getter
	@Setter
	boolean DamageEvP=false;
	@Getter
	@Setter
	boolean DamagePvE=false;
	@Getter
	@Setter
	boolean DamageEvE=false;
	@Getter
	@Setter
	boolean Damage=false;
	@Getter
	@Setter
	boolean ProjectileDamage=false;
	@Getter
	HashMap<Entity,Double> Heal = new HashMap<>();
	@Getter
	HashMap<Entity, NameTagMessage> NameTagMessage = new HashMap<>();
	@Getter
	TeamGame team;
	
	public AddonEntityKing(kArcadeManager manager,Team[] teams,TeamGame team,EntityType type,HashMap<Team, Location> sheeps){
		this.manager=manager;
		this.team=team;
		Entity e;
		for(Team t : teams){
			if(sheeps.containsKey(t)){
				sheeps.get(t).getWorld().loadChunk(sheeps.get(t).getWorld().getChunkAt(sheeps.get(t)));
				e=manager.getPetManager().AddPetWithOutOwner(t.getColor()+"Schaf", type, sheeps.get(t));
				this.teams.put(t, e/*sheeps.get(t).getWorld().spawnEntity(sheeps.get(t), type)*/);
				this.Heal.put(e, 100D);
			}else{
				System.out.println("[AddonEntityKing] TEAM('"+t.Name()+"') NOT FOUND!");
			}
		}
		Bukkit.getPluginManager().registerEvents(this, manager.getInstance());
	}
	
	public void setHealt(Entity e,double h){
		getHeal().put(e, h);
		((Sheep)e).setCustomName(((Sheep)e).getCustomName().split(" ")[0]+" §c"+h+"❤");
	}
	
	public double getHealt(Entity e){
		return getHeal().get(e);
	}
	
	public Entity getEntity(Team t){
		return teams.get(t);
	}
	
	public boolean is(Entity e){
		for(Entity entity : getTeams().values()){
			if(entity.getEntityId()==e.getEntityId()){
				return true;
			}
		}
		return false;
	}
	
	@EventHandler
	public void Death(EntityDeathEvent ev){
		if(is(ev.getEntity())){
			Team t = Team.BLACK;
			for(Team tt : getTeams().keySet()){
				if(getTeams().get(tt).getEntityId()==ev.getEntity().getEntityId()){
					t=tt;
					break;
				}
			}
			AddonEntityKingDeathEvent e = new AddonEntityKingDeathEvent(t,ev.getEntity(),ev.getEntity().getKiller());
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
		if(((ev.getEntity() instanceof Player && !(ev.getDamager() instanceof Player))&& is(ev.getDamager()))&&!DamageEvP){
			//E vs P
			ev.setCancelled(true);
			return;
		}else if ( ((!(ev.getEntity() instanceof Player) && (ev.getDamager() instanceof Player))&&is(ev.getEntity()))&&!DamagePvE){
			//P vs E
			ev.setCancelled(true);
			return;
		}
		
		if((ev.getDamager() instanceof Arrow||ev.getDamager() instanceof Snowball||ev.getDamager() instanceof Egg)&&is(ev.getEntity())&&!ProjectileDamage){
			ev.setCancelled(true);
			return;
		}
		
		
		if(is(ev.getEntity())){
			if(getEntity(getTeam().getTeam( ((Player)ev.getDamager()) )).getEntityId() != ev.getEntity().getEntityId()){
				ev.setCancelled(false);
				h = getHealt(ev.getEntity());
				h=h-4;
				if(h<=0.0){
					ev.setDamage(50);
				}else{
					ev.setDamage(0);
				}
				setHealt(ev.getEntity(), h);
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(getManager().getInstance(), new Runnable() {
                    public void run() {
                        ev.getEntity().setVelocity(new Vector()); //Sets the velocity a tick after getting damaged, else it won't work
                    }
                }, 1L);
			}
		}
	}
	
}
