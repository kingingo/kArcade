package me.kingingo.karcade.Game.Single;

import java.util.ArrayList;
import java.util.HashSet;

import lombok.Getter;
import lombok.Setter;
import me.kingingo.karcade.kArcade;
import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Enum.PlayerState;
import me.kingingo.karcade.Game.Game;
import me.kingingo.karcade.Game.GameList;
import me.kingingo.karcade.Game.Events.GameStateChangeEvent;
import me.kingingo.karcade.Game.Single.addons.AddonSpecCompass;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.Game.Events.GameStartEvent;
import me.kingingo.kcore.Permission.kPermission;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.C;
import me.kingingo.kcore.Util.TabTitle;
import me.kingingo.kcore.Util.TimeSpan;
import me.kingingo.kcore.Util.UtilBG;
import me.kingingo.kcore.Util.UtilDisplay;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.UtilPlayer;
import me.kingingo.kcore.Util.UtilServer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityInteractEvent;
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

public class SingleGame extends Game{
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
	int start=-1;
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
	@Getter
	@Setter
	private boolean PlayerShearEntity=false;
	@Getter
	@Setter
	private boolean InventoryTyp=true;
	@Getter
	private ArrayList<InventoryType> InventoryTypDisallow = new ArrayList<>(); 
	@Getter
	private ArrayList<DamageCause> EntityDamage = new ArrayList<>();
	@Getter
	@Setter
	private boolean soilChange=false;
	@Getter
	private GameList gameList;
	
	public SingleGame(kArcadeManager manager) {
		super(manager);
		this.gameList=new GameList(getManager());
	}
	
	@EventHandler
	public void soilChangeEntity(EntityInteractEvent event){
	    if (!isSoilChange() && (event.getEntityType() != EntityType.PLAYER) && (event.getBlock().getType() == Material.SOIL)) event.setCancelled(true);
	}
	
	@EventHandler
	public void PlayerShearEntity(PlayerShearEntityEvent ev){
		if(PlayerShearEntity)ev.setCancelled(true);
	}
	
	@EventHandler
	public void OpenInventory(InventoryOpenEvent ev){
		if(!InventoryTyp)ev.setCancelled(true);
		if(!InventoryTypDisallow.isEmpty()&&InventoryTypDisallow.contains(ev.getInventory().getType())){
			ev.setCancelled(true);
		}
	}
	
	@EventHandler
	public void InterBack(PlayerInteractEvent ev){
		if(UtilEvent.isAction(ev, ActionType.R)){
			if(ev.getPlayer().getItemInHand()==null)return;
			if(ev.getPlayer().getItemInHand().getTypeId()!=385)return;
				UtilBG.sendToServer(ev.getPlayer(), getManager().getInstance());
				ev.setCancelled(true);
			}
	}
	
	@EventHandler
	public void QuitPlayerListener(PlayerQuitEvent ev){
		ev.setQuitMessage(null);
		if(getState()==GameState.Restart)return;
		getStats().SaveAllPlayerData(ev.getPlayer());
	}
	
	@EventHandler
	public void Damage(EntityDamageEvent ev){
		if(ev.getEntity() instanceof Player && getGameList().getPlayers(PlayerState.OUT).contains((Player)ev.getEntity()))ev.setCancelled(true);
		if(isState(GameState.LobbyPhase))ev.setCancelled(true);
		if(!Damage)ev.setCancelled(true);
		if(EntityDamage.contains(ev.getCause())){
			ev.setCancelled(true);
		}
	}
	
	@EventHandler
	public void Food(FoodLevelChangeEvent ev){
		if(ev.getEntity() instanceof Player && getGameList().getPlayers(PlayerState.OUT).contains((Player)ev.getEntity()))ev.setCancelled(true);
		if((isState(GameState.LobbyPhase))||!FoodChange){
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
        if(event.getAction() == Action.PHYSICAL&& (isState(GameState.LobbyPhase)||solid)){
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
	
	@EventHandler
	public void StartGameGame(GameStartEvent ev){
		if(getWorldData()!=null){
			if(getWorldData().getWorld()!=null){
				getWorldData().getWorld().setStorm(false);
			}
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void EntityDamageByEntity(EntityDamageByEntityEvent ev){
		if((ev.getDamager() instanceof Player &&getGameList().getPlayers(PlayerState.OUT).contains((Player)ev.getDamager()))||!Damage||isState(GameState.LobbyPhase)){
			if(getManager().getService().isDamage())System.err.println("[Game] Cancelled TRUE bei Damage");
			ev.setCancelled(true);
		}else if((ev.getEntity() instanceof Player && ev.getDamager() instanceof Player)&&!DamagePvP){
			//P vs P
			if(getManager().getService().isDamage())System.err.println("[Game] Cancelled TRUE bei DamagePvP");
			ev.setCancelled(true);
		}else if(((ev.getEntity() instanceof Player && ev.getDamager() instanceof Creature))&&!DamageEvP){
			//E vs P
			if(getManager().getService().isDamage())System.err.println("[Game] Cancelled TRUE bei DamageEvP");
			ev.setCancelled(true);
		}else if ( ((ev.getDamager() instanceof Player && ev.getEntity() instanceof Creature))&&!DamagePvE){
			if(getManager().getService().isDamage())System.err.println("[Game] Cancelled TRUE bei DamagePvE");
			//P vs E
			ev.setCancelled(true);
		}else if((ev.getDamager() instanceof Arrow||ev.getDamager() instanceof Snowball||ev.getDamager() instanceof Egg)&&!ProjectileDamage){
			if(getManager().getService().isDamage())System.err.println("[Game] Cancelled TRUE bei ProjectileDamage");
			ev.setCancelled(true);
		}
	}
	
	@EventHandler
	public void PlaceBlockInMap(BlockPlaceEvent ev){
		if(getManager().getPermManager().hasPermission(ev.getPlayer(), kPermission.ALL_PERMISSION)||ev.getPlayer().isOp())return;
		if(getGameList().getPlayers(PlayerState.OUT).contains(ev.getPlayer()))ev.setCancelled(true);
		if(isState(GameState.DeathMatch)){
			ev.setCancelled(false);
			return;
		}
		if((!isState(GameState.InGame))||BlockPlaceDeny.contains(ev.getBlock().getType()) || (!BlockPlace && !BlockPlaceAllow.contains(ev.getBlock().getType()))){
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
		if(getManager().getPermManager().hasPermission(ev.getPlayer(), kPermission.ALL_PERMISSION)||ev.getPlayer().isOp())return;
		if(getGameList().getPlayers(PlayerState.OUT).contains(ev.getPlayer()) || !ev.getBlock().getWorld().getName().equalsIgnoreCase(getWorldData().getWorld().getName()))ev.setCancelled(true);
		if((isState(GameState.LobbyPhase))||BlockBreakDeny.contains(ev.getBlock().getType()) || (!BlockBreak && !BlockBreakAllow.contains(ev.getBlock().getType()))){
			ev.setCancelled(true);
		}
	}
	
	@EventHandler
	public void PickUpItemsFromGround(PlayerPickupItemEvent ev){
		if((isState(GameState.LobbyPhase))||!ItemPickup&&!ItemPickupAllow.contains(ev.getItem().getItemStack().getTypeId()) || (ItemPickupDeny.contains(ev.getItem().getItemStack().getTypeId()))){
			ev.setCancelled(true);
		}else if(getState()!=GameState.LobbyPhase&&getGameList().getPlayers(PlayerState.OUT).contains(ev.getPlayer())){
			ev.setCancelled(true);
		}
	}
	
	@EventHandler
	public void Drop(PlayerDropItemEvent ev){
		if(getGameList().getPlayers(PlayerState.OUT).contains(ev.getPlayer())||(isState(GameState.LobbyPhase)) || ItemDropDeny.contains(ev.getItemDrop().getItemStack().getTypeId()) || (!ItemDrop && !ItemDropAllow.contains(ev.getItemDrop().getItemStack().getTypeId()))){
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
	
	 @EventHandler(priority=EventPriority.NORMAL)
	 public void JoinRanking(PlayerJoinEvent ev){
		 TabTitle.setHeaderAndFooter(ev.getPlayer(), "�eEPICPVP �7-�e "+getType().getTyp(), "�eShop.EpicPvP.de");
		 getManager().getHologram().sendText(ev.getPlayer(), getManager().getLoc_raking(), getManager().getString_ranking());
	 }
	
	  @EventHandler(priority=EventPriority.LOWEST)
	  public void Joinnow(PlayerJoinEvent ev){
		  ev.setJoinMessage(null);
		  ev.getPlayer().sendMessage(Text.PREFIX.getText()+"�eDu hast eine Map f�r uns gebaut? Melde sie im Forum und wir nehmen sie!�b http://EpicPvP.me/");
		  getManager().Clear(ev.getPlayer());
		  if(isState(GameState.LobbyPhase)){
			  getManager().getLobby().getWorld().setStorm(false);
			  getManager().getLobby().getWorld().setTime(4000);
			  ev.getPlayer().teleport(getManager().getLobby());
			  if(getMax_Players() <= UtilServer.getPlayers().size() ){
				  if(getStart() > 16){
					  setStart(16);
				  }
			  }
		  }
		  if(getType()!=null){
			  ev.getPlayer().sendMessage(Text.PREFIX_GAME.getText(getType().getTyp())+Text.WHEREIS_TEXT.getText(getType().getTyp()+" "+kArcade.id));
		  }
	  }
	  
	  @EventHandler
	  public void Login(PlayerLoginEvent ev){
		 if(UtilServer.getPlayers().size()>=getMax_Players()&&isState(GameState.LobbyPhase)){
			  if(getManager().getPermManager().hasGroupPermission(ev.getPlayer(), kPermission.JOIN_FULL_SERVER)||getManager().getPermManager().hasPermission(ev.getPlayer(), kPermission.JOIN_FULL_SERVER)){
				  boolean b = false;
				  for(Player p : UtilServer.getPlayers()){
					  if(!getManager().getPermManager().hasPermission(p, kPermission.JOIN_FULL_SERVER)){
						  UtilPlayer.sendMessage(p,Text.PREFIX_GAME.getText(getType().getTyp())+Text.KICKED_BY_PREMIUM.getText());
						  UtilBG.sendToServer(p, getManager().getInstance());
						  b=true;
						  break;
					  }
				  }
				  if(!b){
					  ev.disallow(Result.KICK_FULL, Text.SERVER_FULL_WITH_PREMIUM.getText());
				  }
			  }else{
				  ev.disallow(Result.KICK_FULL, Text.SERVER_FULL.getText());
			  }
		  }else  if(!isState(GameState.LobbyPhase)){
			  if(!getManager().getPermManager().hasGroupPermission(ev.getPlayer(), kPermission.SERVER_JOIN_SPECTATE)){
				  if(!getManager().getPermManager().hasPermission(ev.getPlayer(), kPermission.SERVER_JOIN_SPECTATE)){
					  if(!getManager().getPermManager().hasPermission(ev.getPlayer(), kPermission.ALL_PERMISSION)){
						  if(!getManager().getPermManager().hasGroupPermission(ev.getPlayer(), kPermission.ALL_PERMISSION)){
							  ev.disallow(Result.KICK_OTHER, Text.SERVER_NOT_LOBBYPHASE.getText());
						  }
					  }
				  }
			  }
		  }
	  }
	  
	  @EventHandler(priority=EventPriority.HIGHEST)
	  public void BreakBlockLobby(BlockBreakEvent ev){
		  if(getState()==GameState.LobbyPhase)ev.setCancelled(true);
		  if(!ev.getBlock().getWorld().getName().equalsIgnoreCase(getWorldData().getWorld().getName()))ev.setCancelled(true);
	  }
	
	  @EventHandler
	  public void BlockBurn(BlockBurnEvent ev)
	  {
	    if (!BlockBurn||!isState(GameState.InGame)){
	    	ev.setCancelled(true);
	    }
	  }

	  @EventHandler
	  public void BlockSpread(BlockSpreadEvent ev)
	  {
	    if (!BlockSpread||!isState(GameState.InGame)){
	    	ev.setCancelled(true);
	    }
	  }

	  @EventHandler
	  public void BlockFade(BlockFadeEvent ev){
	    if (!BlackFade||!isState(GameState.InGame)){
	    	ev.setCancelled(true);
	    }
	  }

	  @EventHandler
	  public void BlockDecay(LeavesDecayEvent ev){
	    if (!LeavesDecay||!isState(GameState.InGame)){
	    	ev.setCancelled(true);
	    }
	  }

	  @EventHandler
	  public void MobSpawn(CreatureSpawnEvent ev){
		if(ev.getSpawnReason()==SpawnReason.CUSTOM)return;
	    if ((!CreatureSpawn||!isState(GameState.InGame)) && !AllowSpawnCreature.contains(ev.getCreatureType())){
		      ev.setCancelled(true);
	    }
	  }
		
	  @EventHandler
		public void Restart(UpdateEvent ev){
			if(ev.getType()!=UpdateType.SEC)return;
			if(getState()!=GameState.Restart)return;
			if(start<0){
				setDamage(false);
				start=35;
			}
			start--;
			for(Player p : UtilServer.getPlayers())UtilDisplay.displayTextBar(p, Text.RESTART_IN.getText(start));
			
			switch(start){
			case 30:broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.RESTART_IN.getText(start));break;
			case 25:
				broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.RESTART_IN.getText(start));
				for(Player p : UtilServer.getPlayers())UtilBG.sendToServer(p, getManager().getInstance());
				break;
			case 23:getStats().SaveAllData();break;
			case 20:broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.RESTART_IN.getText(start));
			for(Player p : UtilServer.getPlayers())UtilBG.sendToServer(p, getManager().getInstance());
				break;
			case 10:broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.RESTART_IN.getText(start));break;
			case 5:broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.RESTART_IN.getText(start));
			case 4:broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.RESTART_IN.getText(start));break;
			case 3:broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.RESTART_IN.getText(start));break;
			case 2:broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.RESTART_IN.getText(start));break;
			case 1:broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.RESTART_IN.getText(start));break;
			case 0: 
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
				;break;
			}
		}
	  
	  @EventHandler
		public void Time_Start(UpdateEvent ev){
			if(ev.getType()==UpdateType.MIN_04&&getState()==GameState.LobbyPhase&&UtilServer.getPlayers().size()<=0){
				if(TimeSpan.HOUR*3 < (System.currentTimeMillis() - kArcade.start_time) ){
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
				}
			}
		}
	  
	  @EventHandler(priority=EventPriority.HIGHEST)
		public void r(GameStateChangeEvent ev){
			if(!ev.isCancelled()&&ev.getTo()==GameState.Restart){
				setStart(-1);
			}
		}
		
		@EventHandler
		public void Lobby(UpdateEvent ev){
			if(ev.getType()!=UpdateType.SEC)return;
			if(getState()!=GameState.LobbyPhase)return;
			if(start<0){
				start=120;
				updateInfo();
			}
			start--;
			for(Player p : UtilServer.getPlayers()){
				UtilDisplay.displayTextBar(p, C.cGray+"Das Spiel startet in "+C.cDAqua+start+C.cGray+" sekunden.");
				if(p.getLocation().getY()<5)p.teleport(getManager().getLobby());
			}
			if(start!=0){
				switch(start){
				case 120:
					broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+"Das Spiel startet in "+C.cDAqua+start+C.cGray+" sekunden.");
					Bukkit.getWorld("world").setWeatherDuration(0);
					Bukkit.getWorld("world").setStorm(false);
					break;
				case 90:broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+"Das Spiel startet in "+C.cDAqua+start+C.cGray+" sekunden.");break;
				case 60:broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+"Das Spiel startet in "+C.cDAqua+start+C.cGray+" sekunden.");break;
				case 30:broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+"Das Spiel startet in "+C.cDAqua+start+C.cGray+" sekunden.");break;
				case 15:broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+"Das Spiel startet in "+C.cDAqua+start+C.cGray+" sekunden.");break;
				case 10:broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+"Das Spiel startet in "+C.cDAqua+start+C.cGray+" sekunden.");break;
				case 3:broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+"Das Spiel startet in "+C.cDAqua+start+C.cGray+" sekunden.");break;
				case 2:broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+"Das Spiel startet in "+C.cDAqua+start+C.cGray+" sekunden.");break;
				case 1:broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+"Das Spiel startet in "+C.cDAqua+start+C.cGray+" sekunden.");break;
				}
			}else{
				if(UtilServer.getPlayers().size()>=getMin_Players()){
					Bukkit.getPluginManager().callEvent(new GameStartEvent(getType()));
					updateInfo(GameState.InGame);
				}else{
					start=-1;
					broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+C.cRed+"Es sind zu wenig Spieler(min. "+getMin_Players()+") online! Wartemodus wird neugestartet!");
				}
			}
		}
	
}