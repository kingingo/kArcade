package me.kingingo.karcade.Game;

import java.util.ArrayList;
import java.util.HashSet;

import lombok.Getter;
import lombok.Setter;
import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Game.addons.SpecCompass;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.Permission.Permission;
import me.kingingo.kcore.Util.UtilBG;
import me.kingingo.kcore.Util.UtilPlayer;
import me.kingingo.kcore.Util.UtilServer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Game implements Listener{
	@Getter
	private kArcadeManager manager=null;
	@Getter
	@Setter
	public boolean Damage = true;
	@Setter
	@Getter
	public boolean ProjectileDamage=true;
	@Getter
	@Setter
	public boolean DamagePvP = true;
	@Getter
	@Setter
	public boolean DamagePvE = true;
	@Getter
	@Setter
	public boolean DamageEvP = true;
	@Getter
	@Setter
	public boolean DamageSelf = true;
	@Getter
	@Setter
	public boolean DamageTeamSelf = false;
	@Getter
	@Setter
	public boolean DamageTeamOther = true;
	@Getter
	@Setter
	public boolean CompassAddon = false;
	@Getter
	@Setter
	private int Min_Players=1;
	@Getter
	@Setter
	private int Max_Players=50;
	@Getter
	@Setter
	private boolean Respawn=false;
	@Getter
	@Setter
	private double respawnTime = 0.0D;
	@Getter
	@Setter
	public boolean BlockBreak = false;
	@Getter
	public HashSet<Material> BlockBreakAllow = new HashSet<>();
	@Getter
	public HashSet<Material> BlockBreakDeny = new HashSet<>();
	@Getter
	@Setter
	public boolean BlockPlace = false;
	@Getter
	public HashSet<Material> BlockPlaceAllow = new HashSet<>();
	@Getter
	public HashSet<Material> BlockPlaceDeny = new HashSet<>();
	@Getter
	@Setter
	public boolean ItemPickup = false;
	public HashSet<Integer> ItemPickupAllow = new HashSet<>();
	public HashSet<Integer> ItemPickupDeny = new HashSet<>();
	@Getter
	@Setter
	public boolean ItemDrop = false;
	@Getter
	public HashSet<Integer> ItemDropAllow = new HashSet<>();
	@Getter
	public HashSet<Integer> ItemDropDeny = new HashSet<>();
	@Getter
	@Setter
	public boolean BlockBurn = false;
	@Getter
	@Setter
	public boolean BlockSpread = false;
	@Getter
	@Setter
	public boolean BlackFade = false;
	@Getter
	@Setter
	public boolean LeavesDecay=false;
	@Getter
	@Setter
	public boolean CreatureSpawn=false;
	@Getter
	public ArrayList<CreatureType> AllowSpawnCreature= new ArrayList<>();
	@Getter
	@Setter
	public String Winner = "nobody";
	@Getter
	@Setter
	public boolean DeathDropItems = false;
	@Getter
	@Setter
	private SpecCompass compass;
	
	@Setter
	@Getter
	private boolean FoodChange=false;

	@Setter
	private GameList gamelist;
	
	@Getter
	private ArrayList<DamageCause> EntityDamage = new ArrayList<>();
	
	public Game(kArcadeManager manager) {
		this.manager=manager;
		this.gamelist=new GameList(this,manager);
		Bukkit.getPluginManager().registerEvents(this, manager.getInstance());
	}
	
	public GameList getGameList(){
		return this.gamelist;
	}
	
	@EventHandler
	public void Quit(PlayerQuitEvent ev){
		ev.setQuitMessage(null);
	}
	
	@EventHandler
	public void Damage(EntityDamageEvent ev){
		if(EntityDamage.contains(ev.getCause())){
			ev.setCancelled(true);
		}
	}
	
	@EventHandler
	public void Food(FoodLevelChangeEvent ev){
		if((!getManager().isState(GameState.InGame))||!FoodChange){
			ev.setCancelled(true);
		}
	}
	
	@EventHandler
	public void EntityDamageByEntity(EntityDamageByEntityEvent ev){
		if(!Damage||!getManager().isState(GameState.InGame)){
			ev.setCancelled(true);
		}else if((ev.getEntity() instanceof Player && ev.getDamager() instanceof Player)&&!DamagePvP){
			//P vs P
			if(((Player)ev.getEntity()).getName().equalsIgnoreCase(((Player)ev.getDamager()).getName()))ev.setCancelled(true);
			ev.setCancelled(true);
		}else if(((ev.getEntity() instanceof Player && !(ev.getDamager() instanceof Player)))&&!DamageEvP){
			//E vs P
			ev.setCancelled(true);
		}else if ( ((!(ev.getEntity() instanceof Player) && (ev.getDamager() instanceof Player)))&&!DamagePvE){
			//P vs E
			System.out.println("5");
			ev.setCancelled(true);
		}else if((ev.getDamager() instanceof Arrow||ev.getDamager() instanceof Snowball||ev.getDamager() instanceof Egg)&&!ProjectileDamage){
			ev.setCancelled(true);
		}
	}
	
	@EventHandler
	public void Place(BlockPlaceEvent ev){
		if(getManager().getPManager().hasPermission(ev.getPlayer(), Permission.ALL_PERMISSION)||ev.getPlayer().isOp())return;
		if((!getManager().isState(GameState.InGame))||BlockPlaceDeny.contains(ev.getBlock().getType()) || (!BlockPlace && !BlockPlaceAllow.contains(ev.getBlock().getType()))){
			ev.setCancelled(true);
		}
	}
	
	@EventHandler
	public void Break(BlockBreakEvent ev){
		if(getManager().getPManager().hasPermission(ev.getPlayer(), Permission.ALL_PERMISSION)||ev.getPlayer().isOp())return;
		if((!getManager().isState(GameState.InGame))||BlockBreakDeny.contains(ev.getBlock().getType()) || (!BlockBreak && !BlockBreakAllow.contains(ev.getBlock().getType()))){
			ev.setCancelled(true);
		}
	}
	
	@EventHandler
	public void PickUp(PlayerPickupItemEvent ev){
		if((!getManager().isState(GameState.InGame))||!ItemPickup&&!ItemPickupAllow.contains(ev.getItem()) || (ItemPickupDeny.contains(ev.getItem()))){
			ev.setCancelled(true);
		}
	}
	
	@EventHandler
	public void Drop(PlayerDropItemEvent ev){
		if((!getManager().isState(GameState.InGame)) || ItemDropDeny.contains(ev.getItemDrop()) || (!ItemDrop && !ItemDropAllow.contains(ev.getItemDrop()))){
			ev.setCancelled(true);
		}
	}
	
	@EventHandler
	public void Respawn(PlayerDeathEvent ev){
		if(ev.getEntity()instanceof Player){
			UtilPlayer.RespawnNow(((Player)ev.getEntity()), getManager().getInstance());
		}
	}
	
	@EventHandler
	public void Drop(PlayerDeathEvent ev){
		if(!DeathDropItems){
			ev.getDrops().clear();
			ev.setDroppedExp(0);
		}
		ev.setDeathMessage(null);
	}
	
	  @EventHandler
	  public void Join(PlayerJoinEvent ev){
		  ev.setJoinMessage(null);
		  getManager().Clear(ev.getPlayer());
		  ev.getPlayer().teleport(getManager().getLobby());
	  }
	  
	  @EventHandler
	  public void Login(PlayerLoginEvent ev){
		 if(UtilServer.getPlayers().length>=getMax_Players()&&getManager().isState(GameState.LobbyPhase)){
			  if(getManager().getPManager().hasPermission(ev.getPlayer(), Permission.JOIN_FULL_SERVER)){
				  boolean b = false;
				  for(Player p : UtilServer.getPlayers()){
					  if(!getManager().getPManager().hasPermission(p, Permission.JOIN_FULL_SERVER)){
						  p.sendMessage(Text.PREFIX.getText()+Text.KICKED_BY_PREMIUM.getText());
						  UtilBG.sendToServer(p, getManager().getBungeeCord_Fallback_Server(), getManager().getInstance());
						  b=true;
						  break;
					  }
				  }
				  if(!b){
					  ev.disallow(Result.KICK_FULL, Text.SERVER_FULL_WITH_PREMIUM.getText());
					  return;
				  }
			  }else{
				  ev.disallow(Result.KICK_FULL, Text.SERVER_FULL.getText());
				  return;
			  }
		  }else  if(!getManager().isState(GameState.LobbyPhase)){
			  ev.disallow(Result.KICK_OTHER, Text.SERVER_NOT_LOBBYPHASE.getText());
			  return;
		  }
	  }
	
	  @EventHandler
	  public void BlockBurn(BlockBurnEvent ev)
	  {
	    if (!BlockBurn||!getManager().isState(GameState.InGame)){
	    	ev.setCancelled(true);
	    }
	  }

	  @EventHandler
	  public void BlockSpread(BlockSpreadEvent ev)
	  {
	    if (!BlockSpread||!getManager().isState(GameState.InGame)){
	    	ev.setCancelled(true);
	    }
	  }

	  @EventHandler
	  public void BlockFade(BlockFadeEvent ev)
	  {
	    if (!BlackFade||!getManager().isState(GameState.InGame)){
	    	ev.setCancelled(true);
	    }
	  }

	  @EventHandler
	  public void BlockDecay(LeavesDecayEvent ev)
	  {
	    if (!LeavesDecay||!getManager().isState(GameState.InGame)){
	    	ev.setCancelled(true);
	    }
	  }

	  @EventHandler
	  public void MobSpawn(CreatureSpawnEvent ev)
	  {
	    if ((!CreatureSpawn||!getManager().isState(GameState.InGame)) && !AllowSpawnCreature.contains(ev.getCreatureType())){
		      ev.setCancelled(true);
	    }
	  }
	
}
