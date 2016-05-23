package eu.epicpvp.karcade.Game.Single.Games.CustomWars;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;

import dev.wolveringer.dataserver.gamestats.GameState;
import dev.wolveringer.dataserver.gamestats.GameType;
import dev.wolveringer.dataserver.gamestats.StatsKey;
import eu.epicpvp.karcade.kArcade;
import eu.epicpvp.karcade.kArcadeManager;
import eu.epicpvp.karcade.Events.RankingEvent;
import eu.epicpvp.karcade.Game.Events.GameStartEvent;
import eu.epicpvp.karcade.Game.Events.GameStateChangeEvent;
import eu.epicpvp.karcade.Game.Events.TeamDelEvent;
import eu.epicpvp.karcade.Game.Multi.Games.CustomWars1vs1.UtilCustomWars1vs1;
import eu.epicpvp.karcade.Game.Single.GameMapVote;
import eu.epicpvp.karcade.Game.Single.SingleWorldData;
import eu.epicpvp.karcade.Game.Single.Addons.AddonBedTeamKing;
import eu.epicpvp.karcade.Game.Single.Addons.AddonDropItems;
import eu.epicpvp.karcade.Game.Single.Addons.AddonEntityTeamKing;
import eu.epicpvp.karcade.Game.Single.Addons.AddonPlaceBlockCanBreak;
import eu.epicpvp.karcade.Game.Single.Addons.AddonVoteTeam;
import eu.epicpvp.karcade.Game.Single.Events.AddonBedKingDeathEvent;
import eu.epicpvp.karcade.Game.Single.Games.TeamGame;
import eu.epicpvp.karcade.Game.Single.Games.CustomWars.Items.AlarmSystem;
import eu.epicpvp.karcade.Game.Single.Games.CustomWars.Items.C4;
import eu.epicpvp.karcade.Game.Single.Games.CustomWars.Items.MobileSpezialVillager;
import eu.epicpvp.karcade.Game.Single.Games.CustomWars.Items.MobileVillager;
import eu.epicpvp.karcade.Game.Single.Games.CustomWars.Items.Parachute;
import eu.epicpvp.karcade.Game.Single.Games.CustomWars.Items.RescuePlattform;
import eu.epicpvp.karcade.Game.Single.Games.CustomWars.SheepWars.SheepWars;
import eu.epicpvp.karcade.Game.World.Event.WorldDataInitializeEvent;
import eu.epicpvp.kcore.Addons.AddonDay;
import eu.epicpvp.kcore.Addons.AddonHalloween;
import eu.epicpvp.kcore.Addons.AddonNight;
import eu.epicpvp.kcore.Calendar.Calendar;
import eu.epicpvp.kcore.Calendar.Calendar.CalendarType;
import eu.epicpvp.kcore.Enum.GameStateChangeReason;
import eu.epicpvp.kcore.Enum.PlayerState;
import eu.epicpvp.kcore.Enum.Team;
import eu.epicpvp.kcore.Enum.Zeichen;
import eu.epicpvp.kcore.Events.ServerStatusUpdateEvent;
import eu.epicpvp.kcore.Permission.PermissionType;
import eu.epicpvp.kcore.StatsManager.Event.PlayerStatsLoadedEvent;
import eu.epicpvp.kcore.Translation.TranslationHandler;
import eu.epicpvp.kcore.Update.UpdateType;
import eu.epicpvp.kcore.Update.Event.UpdateEvent;
import eu.epicpvp.kcore.Util.Color;
import eu.epicpvp.kcore.Util.InventorySize;
import eu.epicpvp.kcore.Util.Title;
import eu.epicpvp.kcore.Util.UtilBlock;
import eu.epicpvp.kcore.Util.UtilDisplay;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.kcore.Util.UtilMath;
import eu.epicpvp.kcore.Util.UtilParticle;
import eu.epicpvp.kcore.Util.UtilPlayer;
import eu.epicpvp.kcore.Util.UtilScoreboard;
import eu.epicpvp.kcore.Util.UtilServer;
import eu.epicpvp.kcore.Util.UtilString;
import eu.epicpvp.kcore.Util.UtilTime;
import eu.epicpvp.kcore.Util.UtilWorldEdit;
import eu.epicpvp.kcore.Villager.Event.VillagerShopEvent;
import eu.epicpvp.kcore.kListen.kSort;
import lombok.Getter;

public class CustomWars extends TeamGame{

	@Getter
	private CustomWarsType customType;
	@Getter
	private HashMap<Team,Boolean> teams = new HashMap<>();
	private GameMapVote gameMapVote;
	private AddonBedTeamKing addonBedTeamKing;
	private AddonEntityTeamKing addonEntityTeamKing;
	private AddonDropItems addonDropItems;
	@Getter
	private AddonPlaceBlockCanBreak addonPlaceBlockCanBreak;
	@Getter
	private ArrayList<CustomWarsItem> items;
	private HashMap<String,Integer> kills =  new HashMap<>();
	private ArrayList<kSort> ranking;
	
	public CustomWars(kArcadeManager manager,GameType type,CustomWarsType customType) {
		super(manager);
		long t = System.currentTimeMillis();
		this.customType=customType;
		this.ranking=new ArrayList<>();
		setTyp(type);
		
		items=new ArrayList<>();
		items.add(new AlarmSystem(this));
		items.add(new C4(this));
		items.add(new Parachute(this));
		items.add(new MobileVillager(this));
		items.add(new RescuePlattform(this));
		items.add(new MobileSpezialVillager(this));
		
		setDamage(false);
		setItemDrop(true);
		setItemPickup(true);
		setReplace_Water(true);
		setCreatureSpawn(false);
		setBlockBurn(false);
		setPlayerShearEntity(true);
		setBlockSpread(false);
		setDeathDropItems(true);
		setReplace_Fire(true);
		setReplace_Lava(true);
		setReplace_Water(true);
		setCompassAddon(true);

		setDamage(false);
		setDamageEvP(false);
		setDamagePvE(false);
		setDamagePvP(false);
		setDamageTeamOther(true);
		setDamageTeamSelf(false);
		setProjectileDamage(true);
		setRespawn(true);
		setBlackFade(false);
		setFoodChange(true);
		setExplosion(true);
		setBlockBreak(true);
		setBlockPlace(true);
		setMin_Players(customType.getMin());
		setMax_Players(customType.getMax());
		
		getInventoryTypDisallow().add(InventoryType.WORKBENCH);
		getInventoryTypDisallow().add(InventoryType.DISPENSER);
		getInventoryTypDisallow().add(InventoryType.BEACON);
		
		getItemPickupDeny().add(Material.CACTUS.getId());
		
		getInteractDeny().add(Material.getMaterial(43));
		getInteractDeny().add(Material.ENDER_STONE);
		
		if(getCustomType().getTeam_size()!=1)setVoteTeam(new AddonVoteTeam(this,getCustomType().getTeam(),InventorySize._9,getCustomType().getTeam_size()));
		
		setWorldData(new SingleWorldData(manager,getType().getTyp()+getCustomType().getTeam().length,getType().getShortName()));
		getWorldData().setCleanroomChunkGenerator(true);
		
		if(kArcade.id==-1){
			this.gameMapVote=new GameMapVote(getWorldData());
			this.gameMapVote.Initialize(-1);
		}else{
			if(getWorldData().loadZips().size()<3){
				getWorldData().Initialize();
			}else{
				this.gameMapVote=new GameMapVote(getWorldData());
				this.gameMapVote.Initialize(3);
			}
		}
		
		manager.DebugLog(t, this.getClass().getName());
		setState(GameState.LobbyPhase);
	}
	
	@EventHandler
	public void ServerStatusUpdateCustom(ServerStatusUpdateEvent ev){
		ev.getPacket().setSubstate(getCustomType().name());
	}
	
	@EventHandler
	public void Ranking(RankingEvent ev){
		getManager().setRanking(StatsKey.WIN);
	}
	
	@EventHandler
	public void RespawnLocation(PlayerRespawnEvent ev){
		 if(getGameList().isPlayerState(ev.getPlayer())==PlayerState.IN){
			 ev.setRespawnLocation( getWorldData().getLocs(getTeam(ev.getPlayer())).get(UtilMath.r(getWorldData().getLocs(getTeam(ev.getPlayer())).size())) );
		 }
	}
	
	String format;
	@EventHandler
	public void InGame(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		if(getState()!=GameState.InGame)return;
		setStart(getStart()-1);
		
		if(game_end()){
			setState(GameState.Restart);
			broadcastWithPrefixName("GAME_END");
			return;
		}
		
		format = UtilTime.formatSeconds(getStart());
		
		for(Player p : UtilServer.getPlayers())UtilDisplay.displayTextBar(p, TranslationHandler.getText(p, "GAME_END_IN", format));
		getBoard().getObjective(DisplaySlot.SIDEBAR).setDisplayName("§e"+getType().getTyp()+" §7- §e"+format);
		
		if( (getCustomType().getInGameTime()-2) == getStart()){
			for(Player p : UtilServer.getPlayers()){
				if(getTeamList().containsKey(p))continue;
				Team t = littleTeam();
				addTeam(p, t);
				p.teleport(getWorldData().getLocs(t).get(0));
			}
			
			HashMap<Player,String> l= new HashMap<>();
			for(Player p : getTeamList().keySet()){
				l.put(p, getTeamList().get(p).getColor());
			}
			
			TeamTab(getCustomType().getTeam());
		}
		
		switch(getStart()){
			case 15:broadcastWithPrefix("DEATHMATCH_START_IN", format);break;
			case 10:broadcastWithPrefix("DEATHMATCH_START_IN", format);break;
			case 5:broadcastWithPrefix("DEATHMATCH_START_IN", format);break;
			case 4:broadcastWithPrefix("DEATHMATCH_START_IN", format);break;
			case 3:broadcastWithPrefix("DEATHMATCH_START_IN", format);break;
			case 2:broadcastWithPrefix("DEATHMATCH_START_IN", format);break;
			case 1:broadcastWithPrefix("DEATHMATCH_START_IN", format);break;
			case 0:
				broadcastWithPrefixName("DEATHMATCH_START");
				
				if(addonBedTeamKing!=null){
					for(Team team : addonBedTeamKing.getTeams().keySet()){
						Block block = addonBedTeamKing.getTeams().get(team);
						Block twin = UtilBlock.getTwinLocation( block ).getBlock();
						block.setType(Material.AIR);
						twin.setType(Material.AIR);

						getTeams().remove(team);
						getTeams().put(team, false);
					}
					addonBedTeamKing.getTeams().clear();
					
				}else if(addonEntityTeamKing!=null){
					for(Team team : addonEntityTeamKing.getTeams().keySet()){
						Entity e = addonEntityTeamKing.getTeams().get(team);
						e.remove();

						getTeams().remove(team);
						getTeams().put(team, false);
					}
					addonEntityTeamKing.getTeams().clear();
				}
				
				setState(GameState.DeathMatch);
			break;
		}
	}
	
	@EventHandler
	public void DeathMatch(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		if(getState()!=GameState.DeathMatch)return;
		setStart(getStart()-1);
		
		if(game_end()){
			setState(GameState.Restart);
			broadcastWithPrefixName("GAME_END");
			return;
		}
		
		format = UtilTime.formatSeconds(getStart());
		
		for(Player p : UtilServer.getPlayers())UtilDisplay.displayTextBar(p, TranslationHandler.getText(p, "GAME_END_IN", format));
		getBoard().getObjective(DisplaySlot.SIDEBAR).setDisplayName("§e"+getType().getTyp()+" §7- §e"+format);
		
		switch(getStart()){
			case 15:broadcastWithPrefix("GAME_END_IN", format);break;
			case 10:broadcastWithPrefix("GAME_END_IN", format);break;
			case 5:broadcastWithPrefix("GAME_END_IN", format);break;
			case 4:broadcastWithPrefix("GAME_END_IN", format);break;
			case 3:broadcastWithPrefix("GAME_END_IN", format);break;
			case 2:broadcastWithPrefix("GAME_END_IN", format);break;
			case 1:broadcastWithPrefix("GAME_END_IN", format);break;
			case 0:
				broadcastWithPrefixName("GAME_END");
				setState(GameState.Restart);
			break;
		}
	}
	
	//ER PRüFT OB NUR NOCH EIN TEAM ÜBRIG IST!
	public boolean game_end(){
		for(Player p : getTeamList().keySet()){
			for(Player p1 : getTeamList().keySet()){
				if(getTeamList().get(p1)!=getTeamList().get(p)){
					return false;
				}
			}
		}
		return true;
	}

	public static ItemStack Silber(int i){
		return UtilItem.RenameItem(new ItemStack(Material.IRON_INGOT,i), "§bSilver");
	}
	
	public static ItemStack Gold(int i){
		return UtilItem.RenameItem(new ItemStack(Material.GOLD_INGOT,i), "§bGold");
	}
	
	public static ItemStack Bronze(int i){
		return UtilItem.RenameItem(new ItemStack(Material.CLAY_BRICK,i), "§bBronze");
	}
	
	String v;
	String k;
	@EventHandler
	public void Death(PlayerDeathEvent ev){
		if(ev.getEntity() instanceof Player&&ev.getEntity().getKiller() instanceof Player){
			Player killer = ev.getEntity().getKiller();
			Player victim = ev.getEntity();
			Team t = getTeam(victim);
			getMoney().add(killer, StatsKey.COINS, 4);
			getStats().addInt(killer,1, StatsKey.KILLS);
			getStats().addInt(victim,1, StatsKey.DEATHS);
			v=t.getColor()+victim.getName();
			k=getTeam(killer).getColor()+killer.getName();

			if(this instanceof SheepWars){
				if(((SheepWars)this).getKits().containsKey(victim)){
					v=v+"§a["+((SheepWars)this).getKits().get(victim)+"§a]";
				}
				if(((SheepWars)this).getKits().containsKey(killer)){
					k=k+"§a["+((SheepWars)this).getKits().get(killer)+"§a]";
				}
			}
			
			int ki = kills.get(killer.getName());
			ki++;
			kills.remove(killer.getName());
			kills.put(killer.getName(), ki);
			
			broadcastWithPrefix("KILL_BY",new String[]{v,k});
			
			if(getTeams().get(t)==false){
				getGameList().addPlayer(victim, PlayerState.OUT);
				getStats().addInt(victim,1, StatsKey.LOSE);
			}
		}else if(ev.getEntity() instanceof Player){
			Player victim = ev.getEntity();
			Team t = getTeam(victim);
			getStats().addInt(victim,1, StatsKey.DEATHS);
			v=t.getColor()+victim.getName();
			
			if(this instanceof SheepWars){
				if(((SheepWars)this).getKits().containsKey(victim)){
					v=v+"§a["+((SheepWars)this).getKits().get(victim)+"§a]";
				}
			}
			
			broadcastWithPrefix("DEATH", v);

			if(getTeams().get(t)==false){
				getGameList().addPlayer(victim, PlayerState.OUT);
				getStats().addInt(victim,1, StatsKey.LOSE);
			}
		}
	}
	
	public HashMap<Team,Integer> verteilung(Team[] t){
		if(getCustomType().getTeam_size()==1){
			HashMap<Team,Integer> list = new HashMap<>();
			for(Team team : t)list.put(team, 1);
			return list;
		}else{
			HashMap<Team,Integer> list = new HashMap<>();
			Collection<? extends Player> l = UtilServer.getPlayers();
		
			for(Team team : t){
				list.put(team, l.size()/t.length);
			}
			
			if(l.size()%t.length!=0){
				list.remove(t[0]);
				list.put(t[0], (l.size()/t.length)+1);
			}

			return list;
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void replace(BlockPlaceEvent ev){
		if(getState()!=GameState.LobbyPhase){
			if(ev.getBlock().getType()==Material.STAINED_GLASS||ev.getBlock().getType()==Material.STAINED_CLAY){
				if(ev.getBlock().getData() != getTeam(ev.getPlayer()).getItem().getData().getData()){
					if(getGameList().getPlayers().containsKey(ev.getPlayer()) && getGameList().getPlayers().get(ev.getPlayer())==PlayerState.IN){
						ev.getBlock().setData(getTeam(ev.getPlayer()).getItem().getData().getData());
//						UtilInv.remove(ev.getPlayer(), ev.getBlock().getType(),ev.getBlock().getData(), 1);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void setBlock(PlayerInteractEvent ev){
		if(ev.getPlayer().getItemInHand()!=null){
			if(ev.getPlayer().getItemInHand().getType()==Material.STAINED_GLASS||ev.getPlayer().getItemInHand().getType()==Material.STAINED_CLAY){
				if(ev.getPlayer().getItemInHand().getData().getData() != getTeam(ev.getPlayer()).getItem().getData().getData()){
					if(getGameList().getPlayers().containsKey(ev.getPlayer()) && getGameList().getPlayers().get(ev.getPlayer())==PlayerState.IN){
						ev.getPlayer().setItemInHand(new ItemStack(ev.getPlayer().getItemInHand().getType(),ev.getPlayer().getItemInHand().getAmount(),getTeam(ev.getPlayer()).getItem().getData().getData()));
						ev.getPlayer().updateInventory();
					}
				}
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void Place(BlockPlaceEvent ev){
		if(getState()==GameState.LobbyPhase){
			ev.setCancelled(true);
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void Break(BlockBreakEvent ev){
		if(getState()==GameState.LobbyPhase){
			ev.setCancelled(true);
			return;
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void pickUp(PlayerPickupItemEvent ev){
		if(getState()!=GameState.LobbyPhase){
			if(ev.getItem().getItemStack().getType() == Material.STAINED_GLASS || ev.getItem().getItemStack().getType() == Material.STAINED_CLAY){
				ev.getItem().getItemStack().getData().setData(getTeam(ev.getPlayer()).getItem().getData().getData());
			}
		}
	}
	
	@EventHandler
	public void move(UpdateEvent ev){
		if(ev.getType()==UpdateType.FASTEST){
			if(getState() == GameState.InGame || getState() == GameState.DeathMatch){
				Block block;
				for(Player player : getGameList().getPlayers().keySet()){
					if(getGameList().getPlayers().get(player)==PlayerState.IN){
						if(player.isOnGround()){
							block=player.getLocation().add(0, -1, 0).getBlock();
							if(addonPlaceBlockCanBreak.getBlocks().contains(block.getLocation()) && block.getType()==Material.STAINED_GLASS){
								if(getTeam(player).getItem().getData().getData() != block.getData()){
									try {
										UtilParticle.SMOKE_NORMAL.display(1, 2, 2, 0.5f, 40, block.getLocation(), 15);
										block.setType(Material.AIR);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void WorldLoad(WorldDataInitializeEvent ev){
		ev.getMap().setMapName(shortMap(getWorldData().getMapName()," "+getCustomType().getTeam().length+"x"+getCustomType().getTeam_size()));
	}
	
	@EventHandler
	public void VillagerShop(VillagerShopEvent ev){
		if(getGameList().isPlayerState(ev.getPlayer())==PlayerState.OUT){
			ev.setCancelled(true);
		}
	}
	
	@EventHandler
	public void Start(GameStartEvent ev){
		long time = System.currentTimeMillis();
		ArrayList<Player> plist = new ArrayList<>();
		for(Player p : UtilServer.getPlayers()){
			getManager().Clear(p);
			kills.put(p.getName(), 0);
			getGameList().addPlayer(p,PlayerState.IN);
			plist.add(p);
		}
		PlayerVerteilung(verteilung(getCustomType().getTeam()), plist);
		
		Team[] teams = getCustomType().getTeam();
		ArrayList<Location> list;
		
		for(Entity e : getWorldData().getWorld().getEntities()){
			if(!(e instanceof Player)&&!(e instanceof Villager)){
				e.remove();
			}
		}
		
		EntityType et = EntityType.VILLAGER;
		
		if(getManager().getHoliday()!=null){
			switch(getManager().getHoliday()){
			case HALLOWEEN:
				new AddonNight(getManager().getInstance(),getWorldData().getWorld());
				new AddonHalloween(getManager().getInstance());
				getWorldData().getWorld().setStorm(false);
				break;
			case WEIHNACHTEN:
				et=EntityType.SNOWMAN;
				new AddonDay(getManager().getInstance(),getWorldData().getWorld());
				getWorldData().getWorld().setStorm(false);
				break;
			default:
				new AddonDay(getManager().getInstance(),getWorldData().getWorld());
				getWorldData().getWorld().setStorm(false);
				break;
			}
		}
		
		setBoard(Bukkit.getScoreboardManager().getNewScoreboard());
		UtilScoreboard.addBoard(getBoard(), DisplaySlot.SIDEBAR, "§e"+getType().getTyp());
		
		int i = 0;
		for(Team t : teams){
			UtilScoreboard.setScore(getBoard(), "§a§l"+Zeichen.BIG_HERZ.getIcon()+" "+t.getColor()+t.Name(), DisplaySlot.SIDEBAR, getPlayerFrom(t).size());
			getTeams().put(t, true);
			setVillager(t,et);
			list = getWorldData().getLocs(t);
			for(Player p : getPlayerFrom(t)){
				p.setScoreboard(getBoard());
				if(p.getWorld().getUID() != getWorldData().getWorld().getUID()){
					p.teleport(list.get(i).clone().add(0, 1, 0));
				}
				i++;
				if(i==list.size())i=0;
			}
		}

		addonDropItems= new AddonDropItems(this,getCustomType().getDrop_rate());
		addonPlaceBlockCanBreak= new AddonPlaceBlockCanBreak(getManager().getInstance(),new Material[]{Material.getMaterial(31),Material.getMaterial(38),Material.getMaterial(37),Material.BROWN_MUSHROOM,Material.RED_MUSHROOM});
		
		if(getType() == GameType.SheepWars){
			addonEntityTeamKing=new AddonEntityTeamKing(teams,this, EntityType.SHEEP);
			
			LivingEntity s;
			for(Team t: addonEntityTeamKing.getTeams().keySet()){
				s = (LivingEntity)addonEntityTeamKing.getTeams().get(t);
				s.setCustomName(t.getColor()+getName(EntityType.SHEEP)+" ");
				if(s instanceof Sheep){
					((Sheep)s).setColor(UtilCustomWars1vs1.cd(t.getColor()));
				}
			}
		}else{
			addonBedTeamKing=new AddonBedTeamKing(getManager(), teams,this);
		}
		
		if(getWorldData().existLoc(Team.BLACK)&&!getWorldData().getLocs(Team.BLACK).isEmpty()){
			for(Location loc : getWorldData().getLocs(Team.BLACK)){
				UtilCustomWars1vs1.setSpezialVillager(getManager().getInstance(),loc,et);
			}
		}
		
		setStart(getCustomType().getInGameTime());
		setState(GameState.InGame);
		setDamage(true);
		setDamageEvP(false);
		setDamagePvE(true);
		setDamagePvP(true);
		getManager().DebugLog(time, this.getClass().getName());
	}
	
	public String getName(EntityType e){
		switch(e){
		case SHEEP: return "Schaf";
		case SPIDER: return "Spinne";
		case ZOMBIE: return "Zombie";
		case CAVE_SPIDER: return "Spinne";
		default: return "Tier";
		}
	}
	
	@EventHandler
	public void GameStateChange(GameStateChangeEvent ev){
		if(ev.getTo()==GameState.Restart && ev.getReason() != GameStateChangeReason.CHANGE_TYPE){
			if(game_end()){
				Team t = lastTeam();
				int size = getPlayerFrom(t).size();
				
				for(Player p : getPlayerFrom(t)){
					if(getGameList().isPlayerState(p)==PlayerState.IN){
						getStats().addInt(p,1, StatsKey.WIN);
						
						if(getCustomType() == CustomWarsType._4x32||getCustomType() == CustomWarsType._8x16){
							getMoney().add(p, StatsKey.GEMS, 200);
						}else{
							getMoney().add(p, StatsKey.COINS, 10);
						}
					}
				}
				
				for(String name : this.kills.keySet()){
					this.ranking.add(new kSort(name,this.kills.get(name)));
				}
				Collections.sort(ranking,kSort.DESCENDING);

				Bukkit.broadcastMessage("§a§l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
				if(t!=null){
					Bukkit.broadcastMessage(UtilString.center("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬".length(),("Winner - "+t.Name()).length())+"§eWinner §7- "+t.getColor()+t.Name());
					Bukkit.broadcastMessage(" ");
				}
				
				if(!this.ranking.isEmpty()&&this.ranking.size()>=1){
					Bukkit.broadcastMessage(UtilString.center("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬".length(),("1st Killer - "+this.ranking.get(0).getName()+" - "+this.ranking.get(0).getObj()).length())+"§e1st Killer - §7"+this.ranking.get(0).getName()+" - "+this.ranking.get(0).getObj());
				}
				if(!this.ranking.isEmpty()&&this.ranking.size()>=2){
					Bukkit.broadcastMessage(UtilString.center("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬".length(),("2st Killer - "+this.ranking.get(1).getName()+" - "+this.ranking.get(1).getObj()).length())+"§62st Killer - §7"+this.ranking.get(1).getName()+" - "+this.ranking.get(1).getObj());
				}
				if(!this.ranking.isEmpty()&&this.ranking.size()>=3){
					Bukkit.broadcastMessage(UtilString.center("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬".length(),("3st Killer - "+this.ranking.get(2).getName()+" - "+this.ranking.get(2).getObj()).length())+"§c3st Killer - §7"+this.ranking.get(2).getName()+" - "+this.ranking.get(2).getObj());
				}
				Bukkit.broadcastMessage("§a§l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
			}
		}
	}
	
	@EventHandler
	public void TeamDelBoard(TeamDelEvent ev){
		Team team = getTeam(ev.getPlayer());
		if(getTeams().containsKey(team)){
			if(getTeams().get(team)){
				UtilScoreboard.resetScore(getBoard(), "§a§l"+Zeichen.BIG_HERZ.getIcon()+" "+team.getColor()+team.Name(), DisplaySlot.SIDEBAR);
				UtilScoreboard.setScore(getBoard(), "§a§l"+Zeichen.BIG_HERZ.getIcon()+" "+team.getColor()+team.Name(), DisplaySlot.SIDEBAR, getPlayerFrom(team).size()-1);
			}else{
				UtilScoreboard.resetScore(getBoard(), "§4§l"+Zeichen.MAHLZEICHEN_FETT.getIcon()+" "+team.getColor()+team.Name(), DisplaySlot.SIDEBAR);
				UtilScoreboard.setScore(getBoard(), "§4§l"+Zeichen.MAHLZEICHEN_FETT.getIcon()+" "+team.getColor()+team.Name(), DisplaySlot.SIDEBAR, getPlayerFrom(team).size()-1);
			}
		}
	}
	
	@EventHandler
	public void bedDeath(AddonBedKingDeathEvent ev){
		UtilScoreboard.resetScore(getBoard(), "§a§l"+Zeichen.BIG_HERZ.getIcon()+" "+ev.getTeam().getColor()+ev.getTeam().Name(), DisplaySlot.SIDEBAR);
		UtilScoreboard.setScore(getBoard(), "§4§l"+Zeichen.MAHLZEICHEN_FETT.getIcon()+" "+ev.getTeam().getColor()+ev.getTeam().Name(), DisplaySlot.SIDEBAR, getPlayerFrom(ev.getTeam()).size());
		getTeams().remove(ev.getTeam());
		getTeams().put(ev.getTeam(), false);
		Title t = new Title("","");
		if(ev.getKiller()!=null){
			getStats().addInt(ev.getKiller(),1, StatsKey.BEDWARS_ZERSTOERTE_BEDs);
		}
		
		for(Player player : UtilServer.getPlayers()){
			t.setSubtitle(TranslationHandler.getText(player,"BEDWARS_BED_BROKE", ev.getTeam().getColor()+"§l"+ev.getTeam().Name()));
			t.send(player);
		}
	}
	
	@EventHandler
	public void Chat(AsyncPlayerChatEvent ev){
		if(ev.isCancelled())return;
		ev.setCancelled(true);
		
		if((!ev.getPlayer().hasPermission(PermissionType.CHAT_LINK.getPermissionToString()))&&UtilString.isBadWord(ev.getMessage())||UtilString.checkForIP(ev.getMessage())){
			ev.setMessage("Ich heul rum!");
			ev.getPlayer().sendMessage(TranslationHandler.getText(ev.getPlayer(), "PREFIX")+TranslationHandler.getText(ev.getPlayer(), "CHAT_MESSAGE_BLOCK"));
		}
		
		if(!isState(GameState.LobbyPhase)&&getTeamList().containsKey(ev.getPlayer())){
			if(ev.getMessage().toCharArray()[0]=='#'){
				Team t = getTeam(ev.getPlayer());
				broadcast("§7["+t.getColor()+t.Name()+"§7] "+ev.getPlayer().getDisplayName()+": §7"+ev.getMessage().subSequence(1, ev.getMessage().length()));
			}else{
				Team t = getTeam(ev.getPlayer());
				for(Player p : getPlayerFrom(getTeam(ev.getPlayer()))){
					UtilPlayer.sendMessage(p,t.getColor()+"Team-Chat "+ev.getPlayer().getDisplayName()+":§7 "+ev.getMessage());
				}
			}
		}else if(getState()!=GameState.LobbyPhase&&getGameList().getPlayers(PlayerState.OUT).contains(ev.getPlayer())){
			ev.setCancelled(true);
			UtilPlayer.sendMessage(ev.getPlayer(),TranslationHandler.getText(ev.getPlayer(), "PREFIX_GAME", getType().getTyp())+TranslationHandler.getText(ev.getPlayer(), "SPECTATOR_CHAT_CANCEL"));
		}else{
			UtilServer.broadcast(getManager().getPermManager().getPrefix(ev.getPlayer())+ev.getPlayer().getDisplayName()+":§7 "+ev.getMessage());
		}
	}
	
	@EventHandler
	public void WorldData(WorldDataInitializeEvent ev){
		if(Calendar.holiday==CalendarType.WEIHNACHTEN){
			if(getWorldData().existLoc(Team.BLACK)&&!getWorldData().getLocs(Team.BLACK).isEmpty()){
				UtilWorldEdit.simulateSnow(getWorldData().getLocs(Team.BLACK).get(0), 150);
			}else{
				UtilWorldEdit.simulateSnow(getWorldData().getLocs(Team.RED).get(0), 150);
			}
		}
	}
	
	@EventHandler
	public void StatsLoaded(PlayerStatsLoadedEvent ev){
		if(ev.getManager().getType() != getType())return;
		if(getState()!=GameState.LobbyPhase)return;

		if(UtilPlayer.isOnline(ev.getPlayerId())){
			Player player = UtilPlayer.searchExact(ev.getPlayerId());
			int win = getStats().getInt(StatsKey.WIN, player);
			int lose = getStats().getInt(StatsKey.LOSE, player);
			
			Bukkit.getScheduler().runTask(getManager().getInstance(), new Runnable() {
				
				@Override
				public void run() {
					getManager().getHologram().sendText(player,getManager().getLoc_stats(),new String[]{
						Color.GREEN+getType().getTyp()+" "+ getCustomType().name().replaceAll("_", "") +Color.ORANGE+"§l Info",
						TranslationHandler.getText(player, "GAME_HOLOGRAM_SERVER",getType().getTyp()+" §a"+kArcade.id),
						TranslationHandler.getText(player, "GAME_HOLOGRAM_MAP", (getWorldData().getMap()!=null ? getWorldData().getMapName() : "Loading...")),
						" ",
						TranslationHandler.getText(player, "GAME_HOLOGRAM_STATS", getType().getTyp()),
						TranslationHandler.getText(player, "GAME_HOLOGRAM_KILLS", getStats().getInt(StatsKey.KILLS, player)),
						TranslationHandler.getText(player, "GAME_HOLOGRAM_DEATHS", getStats().getInt(StatsKey.DEATHS, player)),
						(getType()==GameType.BedWars ? TranslationHandler.getText(player, "GAME_HOLOGRAM_BEDWARS", getStats().getInt(StatsKey.BEDWARS_ZERSTOERTE_BEDs, player)) : (getType()==GameType.SheepWars ? TranslationHandler.getText(player, "GAME_HOLOGRAM_SHEEP", getStats().getInt(StatsKey.SHEEPWARS_KILLED_SHEEPS, player)) : " ")),
						" ",
						TranslationHandler.getText(player, "GAME_HOLOGRAM_GAMES", (win+lose)),
						TranslationHandler.getText(player, "GAME_HOLOGRAM_WINS", win),
						TranslationHandler.getText(player, "GAME_HOLOGRAM_LOSE", lose),
					});
				}
			});
		}
	}

	public void setVillager(Team t,EntityType e){
		for(Location l : getWorldData().getLocs(UtilCustomWars1vs1.getVillagerSpawn(t))){
			UtilCustomWars1vs1.setVillager(getManager().getInstance(),items,t, l,e);
		}
	}
}
