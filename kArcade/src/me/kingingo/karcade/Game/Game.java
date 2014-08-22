package me.kingingo.karcade.Game;

import java.util.ArrayList;
import java.util.HashSet;

import lombok.Getter;
import lombok.Setter;
import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Enum.PlayerState;
import me.kingingo.karcade.Game.addons.AddonSpecCompass;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.Permission.Permission;
import me.kingingo.kcore.Util.Coins;
import me.kingingo.kcore.Util.Tokens;
import me.kingingo.kcore.Util.UtilBG;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.UtilPlayer;
import me.kingingo.kcore.Util.UtilServer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
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
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;

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
	public boolean HangingBreak = false;
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
	private boolean Explosion=true;
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
	@Getter
	public HashSet<Integer> ItemPickupAllow = new HashSet<>();
	@Getter
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
	private AddonSpecCompass compass;
	@Getter
	@Setter
	private boolean Replace_Water=false;
	@Getter
	@Setter
	private boolean Replace_Lava=false;
	@Getter
	@Setter
	private boolean Replace_Fire=false;
	@Getter
	private ArrayList<Material> InteractDeny = new ArrayList<>();
	@Setter
	@Getter
	private boolean FoodChange=false;
	@Setter
	@Getter
	private boolean solid=false;
	@Setter
	private GameList gamelist;
	@Getter
	@Setter
	private boolean PlayerShearEntity=false;
	
	@Getter
	private ArrayList<InventoryType> InventoryTypDisallow = new ArrayList<>(); 
	@Getter
	private ArrayList<DamageCause> EntityDamage = new ArrayList<>();
	private Tokens tokens;
	private Coins coins;
	
	public Game(kArcadeManager manager) {
		this.manager=manager;
		this.gamelist=new GameList(this,manager);
		Bukkit.getPluginManager().registerEvents(this, manager.getInstance());
	}
	
	public GameList getGameList(){
		return this.gamelist;
	}
	
	@EventHandler
	public void PlayerShearEntity(PlayerShearEntityEvent ev){
		if(PlayerShearEntity)ev.setCancelled(true);
	}
	
	public boolean isTokensAktiv(){
		return tokens!=null;
	}
	
	public boolean isCoinsAktiv(){
		return coins!=null;
	}
	
	public Coins getCoins(){
		if(coins==null)coins=new Coins(getManager().getInstance(),getManager().getMysql());
		return coins;
	}
	
	public Tokens getTokens(){
		if(tokens==null)tokens=new Tokens(getManager().getInstance(),getManager().getMysql());
		return tokens;
	}
	
	@EventHandler
	public void OpenInventory(InventoryOpenEvent ev){
		if(!InventoryTypDisallow.isEmpty()&&InventoryTypDisallow.contains(ev.getInventory().getType())){
			ev.setCancelled(true);
		}
	}
	
	@EventHandler
	public void InterBack(PlayerInteractEvent ev){
		if(UtilEvent.isAction(ev, ActionType.R)){
			if(ev.getPlayer().getItemInHand()==null)return;
			if(ev.getPlayer().getItemInHand().getTypeId()!=385)return;
				UtilBG.sendToServer(ev.getPlayer(), getManager().getBungeeCord_Fallback_Server(), getManager().getInstance());
				ev.setCancelled(true);
			}
	}
	
	@EventHandler
	public void Quit(PlayerQuitEvent ev){
		ev.setQuitMessage(null);
		manager.getStats().SaveAllPlayerData(ev.getPlayer());
	}
	
	@EventHandler
	public void Damage(EntityDamageEvent ev){
		if(ev.getEntity() instanceof Player && getGameList().getPlayers(PlayerState.OUT).contains((Player)ev.getEntity()))ev.setCancelled(true);
		if(manager.isState(GameState.LobbyPhase))ev.setCancelled(true);
		if(EntityDamage.contains(ev.getCause())){
			ev.setCancelled(true);
		}
	}
	
	@EventHandler
	public void Food(FoodLevelChangeEvent ev){
		if(ev.getEntity() instanceof Player && getGameList().getPlayers(PlayerState.OUT).contains((Player)ev.getEntity()))ev.setCancelled(true);
		if((getManager().isState(GameState.LobbyPhase))||!FoodChange){
			ev.setCancelled(true);
		}
	}
	
	@EventHandler
	public void ItemFrame(HangingBreakByEntityEvent ev){
		if(!HangingBreak){
			ev.setCancelled(true);
		}else if(ev.getEntity().getType()==EntityType.ITEM_FRAME) {
			ev.getEntity().remove();
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event)
    {
        if(event.getAction() == Action.PHYSICAL&& (manager.isState(GameState.LobbyPhase)||solid)){
        	Block block = event.getClickedBlock();
        	if(block == null)
        		return;
        	int blockType = block.getTypeId();
        	if(blockType == Material.SOIL.getId()){
            	event.setUseInteractedBlock(org.bukkit.event.Event.Result.DENY);
            	event.setCancelled(true);
        		block.setTypeId(blockType);
        		block.setData(block.getData());
        	}
        }
    }
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void EntityDamageByEntity(EntityDamageByEntityEvent ev){
		if((ev.getDamager() instanceof Player &&getGameList().getPlayers(PlayerState.OUT).contains((Player)ev.getDamager()))||!Damage||getManager().isState(GameState.LobbyPhase)){
			ev.setCancelled(true);
		}else if((ev.getEntity() instanceof Player && ev.getDamager() instanceof Player)&&!DamagePvP){
			//P vs P
			//if(((Player)ev.getEntity()).getName().equalsIgnoreCase(((Player)ev.getDamager()).getName()))ev.setCancelled(true);
			ev.setCancelled(true);
		}else if(((ev.getEntity() instanceof Player && !(ev.getDamager() instanceof Player)))&&!DamageEvP){
			//E vs P
			ev.setCancelled(true);
		}else if ( ((!(ev.getEntity() instanceof Player) && (ev.getDamager() instanceof Player)))&&!DamagePvE){
			//P vs E
			ev.setCancelled(true);
		}else if((ev.getDamager() instanceof Arrow||ev.getDamager() instanceof Snowball||ev.getDamager() instanceof Egg)&&!ProjectileDamage){
			ev.setCancelled(true);
		}
	}
	
//	@EventHandler(priority=EventPriority.HIGHEST)
//	public void InteractBlockWater(PlayerInteractEvent ev){
//		if(UtilEvent.isAction(ev, ActionType.R_BLOCK)&&ev.getPlayer().getItemInHand()!=null){
//			if(InteractDeny.contains(ev.getPlayer().getItemInHand().getType())){
//				if(Replace_Water&&ev.getClickedBlock().getTypeId()==8||ev.getClickedBlock().getTypeId()==9)ev.setCancelled(true);
//				if(Replace_Lava&&ev.getClickedBlock().getTypeId()==10||ev.getClickedBlock().getTypeId()==11)ev.setCancelled(true);
//				if(Replace_Fire&&ev.getClickedBlock().getTypeId()==51)ev.setCancelled(true);
//			}
//		}
//	}
	
	@EventHandler
	public void PlaceBlockInMap(BlockPlaceEvent ev){
		if(getManager().getPermManager().hasPermission(ev.getPlayer(), Permission.ALL_PERMISSION)||ev.getPlayer().isOp())return;
		if(getGameList().getPlayers(PlayerState.OUT).contains(ev.getPlayer()))ev.setCancelled(true);
		if(getManager().isState(GameState.DeathMatch)){
			ev.setCancelled(false);
			return;
		}
		if((!getManager().isState(GameState.InGame))||BlockPlaceDeny.contains(ev.getBlock().getType()) || (!BlockPlace && !BlockPlaceAllow.contains(ev.getBlock().getType()))){
			ev.setCancelled(true);
		}
	}
	
	@EventHandler
	public void TNT (ExplosionPrimeEvent ev){
			if(!Explosion){
				ev.setCancelled(true);
			}
	}
	
	@EventHandler
	public void BreakBlockInMap(BlockBreakEvent ev){
		if(getManager().getPermManager().hasPermission(ev.getPlayer(), Permission.ALL_PERMISSION)||ev.getPlayer().isOp())return;
		if(getGameList().getPlayers(PlayerState.OUT).contains(ev.getPlayer()) || !ev.getBlock().getWorld().getName().equalsIgnoreCase(getManager().getWorldData().getWorld().getName()))ev.setCancelled(true);
		if((getManager().isState(GameState.LobbyPhase))||BlockBreakDeny.contains(ev.getBlock().getType()) || (!BlockBreak && !BlockBreakAllow.contains(ev.getBlock().getType()))){
			ev.setCancelled(true);
		}
	}
	
	@EventHandler
	public void PickUpItemsFromGround(PlayerPickupItemEvent ev){
		if((getManager().isState(GameState.LobbyPhase))||!ItemPickup&&!ItemPickupAllow.contains(ev.getItem().getItemStack().getTypeId()) || (ItemPickupDeny.contains(ev.getItem().getItemStack().getTypeId()))){
			ev.setCancelled(true);
		}else if(getManager().getState()!=GameState.LobbyPhase&&getGameList().getPlayers(PlayerState.OUT).contains(ev.getPlayer())){
			ev.setCancelled(true);
		}
	}
	
	@EventHandler
	public void Drop(PlayerDropItemEvent ev){
		if(getGameList().getPlayers(PlayerState.OUT).contains(ev.getPlayer())||(getManager().isState(GameState.LobbyPhase)) || ItemDropDeny.contains(ev.getItemDrop().getItemStack().getTypeId()) || (!ItemDrop && !ItemDropAllow.contains(ev.getItemDrop().getItemStack().getTypeId()))){
			ev.setCancelled(true);
		}
	}
	
	@EventHandler
	public void RespawnNow(PlayerDeathEvent ev){
		if(ev.getEntity()instanceof Player){
			UtilPlayer.RespawnNow(((Player)ev.getEntity()), getManager().getInstance());
		}
	}
	
	@EventHandler
	public void In(PlayerInteractEntityEvent ev){
		if(ev.getRightClicked().getType()==EntityType.ITEM_FRAME)ev.setCancelled(true);
	}
	
	@EventHandler
	public void In(PlayerInteractEvent ev){
		if(getGameList().getPlayers(PlayerState.OUT).contains(ev.getPlayer()))ev.setCancelled(true);
	}
	
	@EventHandler
	public void DropNNN(PlayerDeathEvent ev){
		if(!DeathDropItems){
			ev.getDrops().clear();
			ev.setDroppedExp(0);
		}
		ev.setDeathMessage(null);
	}
	
	  @EventHandler
	  public void Joinnow(PlayerJoinEvent ev){
		  ev.setJoinMessage(null);
		  getManager().Clear(ev.getPlayer());
		  if(getManager().isState(GameState.LobbyPhase)){
			  getManager().getLobby().getWorld().setStorm(false);
			  getManager().getLobby().getWorld().setTime(4000);
			  ev.getPlayer().teleport(getManager().getLobby());
		  }
	  }
	  
	  @EventHandler
	  public void Login(PlayerLoginEvent ev){
		 if(UtilServer.getPlayers().length>=getMax_Players()&&getManager().isState(GameState.LobbyPhase)){
			  if(getManager().getPermManager().hasPermission(ev.getPlayer(), Permission.JOIN_FULL_SERVER)){
				  boolean b = false;
				  for(Player p : UtilServer.getPlayers()){
					  if(!getManager().getPermManager().hasPermission(p, Permission.JOIN_FULL_SERVER)){
						  p.sendMessage(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.KICKED_BY_PREMIUM.getText());
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
		  }else  if(!getManager().isState(GameState.LobbyPhase)&&!getManager().getPermManager().hasPermission(ev.getPlayer(), Permission.SERVER_JOIN_SPECTATE)){
			  ev.disallow(Result.KICK_OTHER, Text.SERVER_NOT_LOBBYPHASE.getText());
			  return;
		  }
	  }
	  
	  @EventHandler(priority=EventPriority.HIGHEST)
	  public void BreakBlockLobby(BlockBreakEvent ev){
		  if(getManager().getState()==GameState.LobbyPhase)ev.setCancelled(true);
		  if(!ev.getBlock().getWorld().getName().equalsIgnoreCase(getManager().getWorldData().getWorld().getName()))ev.setCancelled(true);
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
