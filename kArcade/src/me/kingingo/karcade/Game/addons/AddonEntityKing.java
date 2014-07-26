package me.kingingo.karcade.Game.addons;

import java.util.HashMap;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import me.kingingo.karcade.Enum.Team;
import me.kingingo.karcade.Game.addons.Events.AddonEntityKingDeathEvent;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
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
	JavaPlugin instance;
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
	
	public AddonEntityKing(JavaPlugin instance,Team[] teams,EntityType type,List<Location> locs){
		this.instance=instance;
		for(Team t : teams){
			this.teams.put(t, locs.get(0).getWorld().spawnEntity(locs.get(0), type));
		}
		Bukkit.getPluginManager().registerEvents(this, getInstance());
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
	
	@EventHandler
	public void EntityDamageByEntity(EntityDamageByEntityEvent ev){
		if(((ev.getEntity() instanceof Player && !(ev.getDamager() instanceof Player))&& is(ev.getDamager()))&&!DamageEvP){
			//E vs P
			ev.setCancelled(true);
		}else if ( ((!(ev.getEntity() instanceof Player) && (ev.getDamager() instanceof Player))&&is(ev.getEntity()))&&!DamagePvE){
			//P vs E
			ev.setCancelled(true);
			return;
		}else{
			ev.getEntity().setVelocity(new Vector(0,0,0));
		}
		
		if((ev.getDamager() instanceof Arrow||ev.getDamager() instanceof Snowball||ev.getDamager() instanceof Egg)&&is(ev.getEntity())&&!ProjectileDamage){
			ev.setCancelled(true);
		}
	}
	
}
