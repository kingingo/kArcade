package me.kingingo.karcade.Game.addons;

import java.util.ArrayList;
import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;
import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Enum.PlayerState;
import me.kingingo.karcade.Enum.Team;
import me.kingingo.karcade.Game.Games.TeamGame;
import me.kingingo.karcade.Game.addons.Events.AddonEntityKingDeathEvent;
import me.kingingo.kcore.Hologram.nametags.NameTagMessage;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.UtilItem;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class AddonEntityKing implements Listener {
	
	@Getter
	kArcadeManager manager;;
	@Getter
	HashMap<Team,Creature> teams = new HashMap<>();
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
	@Getter
	TeamGame team;
	@Getter
	ItemStack item = UtilItem.RenameItem(new ItemStack(Material.SUGAR), "§bSchaf-Heiler");
	
	public AddonEntityKing(kArcadeManager manager,Team[] teams,TeamGame team,EntityType type){
		this.manager=manager;
		this.team=team;
		Creature e;
		Location loc = null;
		for(Team t : teams){
			loc=manager.getWorldData().getLocs(getSheep(t).Name()).get(0);
			loc.getWorld().loadChunk(loc.getWorld().getChunkAt(loc));
			e=manager.getPetManager().AddPetWithOutOwner(t.getColor()+"Schaf", type, loc);
			this.teams.put(t, e);
			this.Heal.put(e, 100D);
		}
		Bukkit.getPluginManager().registerEvents(this, manager.getInstance());
	}
	
	public Team getSheep(Team team){
		switch(team){
		case RED:return Team.SHEEP_RED;
		case BLUE:return Team.SHEEP_BLUE;
		case YELLOW:return Team.SHEEP_YELLOW;
		case GREEN:return Team.SHEEP_GREEN;
		default:
		return Team.SHEEP_RED;
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
	
	public Entity getEntity(Team t){
		return teams.get(t);
	}
	
	public Team get(Entity e){
		for(Team team : getTeams().keySet()){
			if(getTeams().get(team).getEntityId()==e.getEntityId())return team;
		}
		return null;
	}
	
	public boolean is(Entity e){
		for(Entity entity : getTeams().values()){
			if(entity.getEntityId()==e.getEntityId()){
				return true;
			}
		}
		return false;
	}
	
	ArrayList<Team> list = new ArrayList<>();
	@EventHandler
	public void Testeer(UpdateEvent ev){
		if(ev.getType()!=UpdateType.MIN_005)return;
		
//		System.out.println("Eintrag: "+teams.size());
//		for(Team t : teams.keySet()){
//			try{
//			System.out.println("TEAM: "+t.Name());
//			System.out.println("eeeeENTITY: "+teams.get(t)==null);
//			System.out.println("ENTITYddd: "+teams.get(t).isDead());
//			}catch(NullPointerException e){
//				System.err.println("[AddonEntityKing] Fehler: dddddd NullPointer!");
//			}
//		}
//		
		for(Team t : teams.keySet()){
			try{
				if(teams.get(t).isDead()){
					AddonEntityKingDeathEvent e = new AddonEntityKingDeathEvent(t,teams.get(t),null);
					Bukkit.getPluginManager().callEvent(e);
					list.add(t);
				}
			}catch(NullPointerException e){
				System.err.println("[AddonEntityKing] Fehler: Testeer NullPointerException!");
			}
		}
		
		for(Team t : list)teams.remove(t);
		list.clear();
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
			teams.remove(t);
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
	public void Click(PlayerInteractEntityEvent ev){
		if(ev.getRightClicked() instanceof Creature&&is(ev.getRightClicked())){
			if(UtilItem.ItemNameEquals(item, ev.getPlayer().getItemInHand())){
				ev.getPlayer().getInventory().remove(ev.getPlayer().getItemInHand());
				setHealt(((Creature)ev.getRightClicked()),20);
			}
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
			Team t = get(ev.getEntity());
			if(t==null||getTeam().getTeam( ((Player)ev.getDamager()) )==t || manager.getGame().getGameList().isPlayerState( ((Player)ev.getDamager()) )!=PlayerState.IN){
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
		case IRON_SWORD:return 6;
		case DIAMOND_SWORD:return 7;
		case GOLD_SWORD:return 5;
		default:
			return 4;
		}
	}
	
}
