package eu.epicpvp.karcade.Game.Single.Games.Masterbuilders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

import dev.wolveringer.dataserver.gamestats.GameState;
import dev.wolveringer.dataserver.gamestats.GameType;
import dev.wolveringer.dataserver.gamestats.StatsKey;
import eu.epicpvp.karcade.kArcade;
import eu.epicpvp.karcade.kArcadeManager;
import eu.epicpvp.karcade.Events.RankingEvent;
import eu.epicpvp.karcade.Game.Events.GameStartEvent;
import eu.epicpvp.karcade.Game.Single.SingleWorldData;
import eu.epicpvp.karcade.Game.Single.Addons.AddonArea;
import eu.epicpvp.karcade.Game.Single.Addons.AddonMainArea;
import eu.epicpvp.karcade.Game.Single.Events.AddonAreaRestoreEvent;
import eu.epicpvp.karcade.Game.Single.Games.SoloGame;
import eu.epicpvp.kcore.Addons.AddonDay;
import eu.epicpvp.kcore.Enum.ParticleItem;
import eu.epicpvp.kcore.Enum.PlayerState;
import eu.epicpvp.kcore.Enum.Team;
import eu.epicpvp.kcore.Inventory.InventoryBase;
import eu.epicpvp.kcore.Inventory.InventoryPageBase;
import eu.epicpvp.kcore.Inventory.Item.Click;
import eu.epicpvp.kcore.Inventory.Item.Buttons.ButtonBack;
import eu.epicpvp.kcore.Inventory.Item.Buttons.ButtonBase;
import eu.epicpvp.kcore.Inventory.Item.Buttons.ButtonOpenInventory;
import eu.epicpvp.kcore.Language.Language;
import eu.epicpvp.kcore.Language.LanguageType;
import eu.epicpvp.kcore.Permission.PermissionType;
import eu.epicpvp.kcore.StatsManager.Event.PlayerStatsLoadedEvent;
import eu.epicpvp.kcore.Update.UpdateType;
import eu.epicpvp.kcore.Update.Event.UpdateEvent;
import eu.epicpvp.kcore.Util.Color;
import eu.epicpvp.kcore.Util.InventorySize;
import eu.epicpvp.kcore.Util.Title;
import eu.epicpvp.kcore.Util.UtilEvent.ActionType;
import eu.epicpvp.kcore.Util.UtilFirework;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.kcore.Util.UtilMath;
import eu.epicpvp.kcore.Util.UtilParticle;
import eu.epicpvp.kcore.Util.UtilPlayer;
import eu.epicpvp.kcore.Util.UtilScoreboard;
import eu.epicpvp.kcore.Util.UtilServer;
import eu.epicpvp.kcore.Util.UtilString;
import eu.epicpvp.kcore.Util.UtilTime;
import eu.epicpvp.kcore.kListen.kSort;

public class Masterbuilders extends SoloGame{
	
	private HashMap<String, Team> area;
	private HashMap<Team, AddonArea> team_areas;
	private MasterbuildersType mtype;
	private AddonMainArea mainArea;
	private Buildings building;
	private ItemStack[] items_bewertungGER=new ItemStack[]{UtilItem.RenameItem(new ItemStack(Material.STAINED_GLASS_PANE,1,(byte)14), "§4Einfach nur SCHLECHT!"),
			UtilItem.RenameItem(new ItemStack(Material.STAINED_GLASS_PANE,1,(byte)1), "§6MEH..."),
			UtilItem.RenameItem(new ItemStack(Material.STAINED_GLASS_PANE,1,(byte)4), "§eEs ist Oke..."),
			UtilItem.RenameItem(new ItemStack(Material.STAINED_GLASS_PANE,1,(byte)5), "§aGut"),
			UtilItem.RenameItem(new ItemStack(Material.STAINED_GLASS_PANE,1,(byte)3), "§bErstaunlich"),
			UtilItem.RenameItem(new ItemStack(Material.STAINED_GLASS_PANE,1,(byte)2), "§dALLES IST FANTASTISCH!")};
	private ItemStack[] items_bewertungENG=new ItemStack[]{UtilItem.RenameItem(new ItemStack(Material.STAINED_GLASS_PANE,1,(byte)14), "§4MY EYES ARE BLEEDING!"),
			UtilItem.RenameItem(new ItemStack(Material.STAINED_GLASS_PANE,1,(byte)1), "§6MEH..."),
			UtilItem.RenameItem(new ItemStack(Material.STAINED_GLASS_PANE,1,(byte)4), "§eIt's okey..."),
			UtilItem.RenameItem(new ItemStack(Material.STAINED_GLASS_PANE,1,(byte)5), "§aGood"),
			UtilItem.RenameItem(new ItemStack(Material.STAINED_GLASS_PANE,1,(byte)3), "§bAmazing"),
			UtilItem.RenameItem(new ItemStack(Material.STAINED_GLASS_PANE,1,(byte)2), "§dWOW! EVERYTHING IS AWESOME!")};
	
	private InventoryPageBase word_vote;
	
	private Scoreboard scoreENG;
	private Scoreboard scoreGER;
	
	private InventoryBase option;
	private InventoryPageBase ground;
	private InventoryPageBase particle;
	
	private HashMap<Team,HashMap<Location,UtilParticle>> particles;
	
	public Masterbuilders(kArcadeManager manager,MasterbuildersType mtype) {
		super(manager);
		long l = System.currentTimeMillis();
		setTyp(GameType.Masterbuilders);
		
		setDamage(false);
		setCreatureSpawn(false);
		setExplosion(false);
		setBlockBurn(false);
		setBlockSpread(false);
		setFoodChange(false);
		setItemPickup(false);
		setItemDrop(false);
		setMax_Players(mtype.getMax());
		setMin_Players(mtype.getMin());
		
		this.mtype=mtype;
		this.area=new HashMap<>();
		this.team_areas=new HashMap<>();
		
		setWorldData(new SingleWorldData(getManager(), getType()));
		getWorldData().Initialize();
		
		this.option=new InventoryBase(getManager().getInstance());
		this.option.setMain(new InventoryPageBase(InventorySize._9, "§bOption"));
		
		this.ground=new InventoryPageBase(InventorySize._45, "§bChange Ground:");
		this.option.addPage(this.ground);
		this.ground.addButton(4,new ButtonBack(this.option.getMain(), UtilItem.RenameItem(new ItemStack(Material.BED), "§cback")));
		ItemStack[] blocks = new ItemStack[]{UtilItem.RenameItem(new ItemStack(Material.STONE), "§7Stone Block"),
				UtilItem.RenameItem(new ItemStack(Material.GRASS), "§7Grass Block"),
				UtilItem.RenameItem(new ItemStack(Material.DIRT), "§7Dirt Block"),
				UtilItem.RenameItem(new ItemStack(Material.SAND), "§7Sand Block"),
				UtilItem.RenameItem(new ItemStack(Material.SANDSTONE), "§7Sandstone Block"),
				UtilItem.RenameItem(new ItemStack(Material.WATER_BUCKET), "§7Water"),
				UtilItem.RenameItem(new ItemStack(Material.LAVA_BUCKET), "§7Lava"),
				UtilItem.RenameItem(new ItemStack(Material.WOOD), "§7Wood Block"),
				UtilItem.RenameItem(new ItemStack(Material.COBBLESTONE), "§7Cobblestone Block"),
				UtilItem.RenameItem(new ItemStack(Material.NETHERRACK), "§7Netherrack Block"),
				UtilItem.RenameItem(new ItemStack(Material.BRICK), "§7Brick Block"),
				UtilItem.RenameItem(new ItemStack(Material.ENDER_STONE), "§7Enderstone Block"),
				UtilItem.RenameItem(new ItemStack(Material.MYCEL), "§7Mycel Block"),
				UtilItem.RenameItem(new ItemStack(Material.DIAMOND_BLOCK), "§7Diamond Block"),
				UtilItem.RenameItem(new ItemStack(Material.GOLD_BLOCK), "§7Gold Block"),
				UtilItem.RenameItem(new ItemStack(Material.IRON_BLOCK), "§7Iron Block"),
				UtilItem.RenameItem(new ItemStack(Material.ICE), "§7Ice Block"),
				UtilItem.RenameItem(new ItemStack(Material.SNOW_BLOCK), "§7Snow Block"),
				UtilItem.RenameItem(new ItemStack(Material.STAINED_CLAY), "§7Clay Block"),
				UtilItem.RenameItem(new ItemStack(Material.STAINED_CLAY,1,(byte)1), "§7Clay Block (Orange)"),
				UtilItem.RenameItem(new ItemStack(Material.STAINED_CLAY,1,(byte)2), "§7Clay Block (Magental)"),
				UtilItem.RenameItem(new ItemStack(Material.STAINED_CLAY,1,(byte)3), "§7Clay Block (Light Blue)"),
				UtilItem.RenameItem(new ItemStack(Material.STAINED_CLAY,1,(byte)4), "§7Clay Block (Yellow)"),
				UtilItem.RenameItem(new ItemStack(Material.STAINED_CLAY,1,(byte)5), "§7Clay Block (Lime)"),
				UtilItem.RenameItem(new ItemStack(Material.STAINED_CLAY,1,(byte)6), "§7Clay Block (Pink)"),
				UtilItem.RenameItem(new ItemStack(Material.STAINED_CLAY,1,(byte)7), "§7Clay Block (Gray)"),
				UtilItem.RenameItem(new ItemStack(Material.STAINED_CLAY,1,(byte)8), "§7Clay Block (Light Gray)"),
				UtilItem.RenameItem(new ItemStack(Material.STAINED_CLAY,1,(byte)9), "§7Clay Block (Cyan)"),
				UtilItem.RenameItem(new ItemStack(Material.STAINED_CLAY,1,(byte)10), "§7Clay Block (Purple)"),
				UtilItem.RenameItem(new ItemStack(Material.STAINED_CLAY,1,(byte)11), "§7Clay Block (Blue)"),
				UtilItem.RenameItem(new ItemStack(Material.STAINED_CLAY,1,(byte)12), "§7Clay Block (Brown)"),
				UtilItem.RenameItem(new ItemStack(Material.STAINED_CLAY,1,(byte)13), "§7Clay Block (Green)"),
				UtilItem.RenameItem(new ItemStack(Material.STAINED_CLAY,1,(byte)14), "§7Clay Block (Red)"),
				UtilItem.RenameItem(new ItemStack(Material.STAINED_CLAY,1,(byte)15), "§7Clay Block (Black)")};
		
		int slot = 9;
		Click click = new Click(){

			@Override
			public void onClick(Player player, ActionType action, Object obj) {
				if(getState()==GameState.InGame&&obj instanceof ItemStack){
					if(area.containsKey(player.getName())){
						AddonArea a = team_areas.get(area.get(player.getName()));
						int y = a.MinMax[a.Y][a.Min]-1;
						ItemStack i = ((ItemStack)obj);
						
						for(int x = a.MinMax[a.X][a.Min]; x<a.MinMax[a.X][a.Max]; x++){
							for(int z = a.MinMax[a.Z][a.Min]; z<a.MinMax[a.Z][a.Max]; z++){
								if(a.getWorld().getBlockAt(x, y, z).getType()!=Material.COAL_BLOCK){
									if(i.getType()==Material.WATER_BUCKET){
										a.getWorld().getBlockAt(x, y, z).setType(Material.WATER);
									}else if(i.getType()==Material.LAVA_BUCKET){
										a.getWorld().getBlockAt(x, y, z).setType(Material.LAVA);
									}else{
										a.getWorld().getBlockAt(x, y, z).setTypeIdAndData(i.getTypeId(), (byte)i.getData().getData(), true);
									}
								}
							}
						}
						
						y=0;
						i=null;
						a=null;
					}
				}
				player.closeInventory();
			}
			
		};
		
		for(ItemStack i : blocks){
			this.ground.addButton(slot, new ButtonBase(click, i));
			slot++;
		}
		this.option.getMain().addButton(2, new ButtonOpenInventory(ground, UtilItem.RenameItem(new ItemStack(82), "§bChange the Ground")));
		this.particles=new HashMap<>();
		this.particle=new InventoryPageBase(InventorySize._45, "§bParticle:");
		this.option.addPage(this.particle);
		this.particle.addButton(4,new ButtonBack(this.option.getMain(), UtilItem.RenameItem(new ItemStack(Material.BED), "§cback")));
		this.particle.addButton(8,new ButtonBase(new Click(){

			@Override
			public void onClick(Player player, ActionType action, Object obj) {
				if(getState()==GameState.InGame&&obj instanceof ItemStack){
					if(area.containsKey(player.getName())){
						if(particles.containsKey(area.get(player.getName()))){
							particles.get(area.get(player.getName())).clear();
						}
					}
				}
				player.closeInventory();
			}
			
		}, UtilItem.RenameItem(new ItemStack(Material.BUCKET), "§bClear All")));
		
		ParticleItem[] items = new ParticleItem[]{ParticleItem.ANGRY_VILLAGER,
				ParticleItem.FOOTSTEP,
				ParticleItem.HEART,
				ParticleItem.EXPLODE,
				ParticleItem.RED_DUST,
				ParticleItem.REDSTONE,
				ParticleItem.DRIP_WATER,
				ParticleItem.DRIP_LAVA,
				ParticleItem.SNOWBALL,
				ParticleItem.CRIT,
				ParticleItem.HAPPY_VILLAGER,
				ParticleItem.SLIME,
				ParticleItem.NOTE,
				ParticleItem.FLAME,
				ParticleItem.SMOKE_NORMAL,
				ParticleItem.CLOUD};
		
		 slot = 9;
		 click = new Click(){

			@Override
			public void onClick(Player player, ActionType action, Object obj) {
				if(getState()==GameState.InGame&&obj instanceof ItemStack){
					if(area.containsKey(player.getName())){
						if(!particles.containsKey(area.get(player.getName()))){
							particles.put(area.get(player.getName()), new HashMap<Location,UtilParticle>());
						}
						
						player.getInventory().setItem(7, ((ItemStack)obj));
					}
				}
				player.closeInventory();
			}
			
		};
		
		for(ParticleItem i : items){
			this.particle.addButton(slot, new ButtonBase(click, i.getItem()));
			slot++;
		}
		
		this.option.getMain().addButton(6, new ButtonOpenInventory(particle, UtilItem.RenameItem(new ItemStack(Material.NETHER_STAR), "§bParticle")));
		this.option.getMain().fill(Material.STAINED_GLASS_PANE, (byte)7);
		
		this.word_vote=new InventoryPageBase(InventorySize._9, "§7Vote");
		
		setState(GameState.LobbyPhase);
		getManager().DebugLog(l, this.getClass().getName());
	}

	public Team getArea(Team team){
		switch(team){
		case RED:return Team.VILLAGE_RED;
		case BLUE:return Team.VILLAGE_BLUE;
		case YELLOW:return Team.VILLAGE_YELLOW;
		case GREEN:return Team.VILLAGE_GREEN;
		case GRAY:return Team.VILLAGE_GRAY;
		case PINK:return Team.VILLAGE_PINK;
		case ORANGE:return Team.VILLAGE_ORANGE;
		case PURPLE:return Team.VILLAGE_PURPLE;
		case WHITE:return Team.VILLAGE_WHITE;
		case BLACK:return Team.VILLAGE_BLACK;
		case CYAN:return Team.VILLAGE_CYAN;
		case AQUA:return Team.VILLAGE_AQUA;
		default:
		return Team.VILLAGE_RED;
		}
	}
	
	@EventHandler
	public void particle(UpdateEvent ev){
		if(ev.getType()==UpdateType.FAST){
			if(getState()==GameState.InGame||getState()==GameState.SchutzModus){
				if(!particles.isEmpty()){
					for(Team t : particles.keySet()){
						if(!particles.get(t).isEmpty()){
							for(Location loc : particles.get(t).keySet()){
								particles.get(t).get(loc).display(0, 3, loc, 20);
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void Chat(AsyncPlayerChatEvent ev){
		if(ev.isCancelled())return;
		ev.setCancelled(true);
		
		if((!ev.getPlayer().hasPermission(PermissionType.CHAT_LINK.getPermissionToString()))&&UtilString.isBadWord(ev.getMessage())||UtilString.checkForIP(ev.getMessage())){
			ev.setMessage("Ich heul rum!");
			ev.getPlayer().sendMessage(Language.getText(ev.getPlayer(), "PREFIX")+Language.getText(ev.getPlayer(), "CHAT_MESSAGE_BLOCK"));
		}
		
		if(getState()!=GameState.LobbyPhase&&getGameList().getPlayers(PlayerState.OUT).contains(ev.getPlayer())){
			ev.setCancelled(true);
			UtilPlayer.sendMessage(ev.getPlayer(),Language.getText(ev.getPlayer(), "PREFIX_GAME", getType().getTyp())+Language.getText(ev.getPlayer(), "SPECTATOR_CHAT_CANCEL"));
		}else{
			UtilServer.broadcast(getManager().getPermManager().getPrefix(ev.getPlayer())+ev.getPlayer().getDisplayName()+":§7 "+ev.getMessage());
		}
	}

	HashMap<Team,HashMap<Player,Integer>> vote = new HashMap<>();
	ArrayList<kSort> ranking;
	@EventHandler
	public void win(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		if(getState()==GameState.DeathMatch){
			if(getStart()==15){
				Collections.sort(ranking,kSort.DESCENDING);
				
				if(!ranking.isEmpty()){
					if(UtilPlayer.isOnline(ranking.get(0).getName())){
						getMoney().addInt(Bukkit.getPlayer(ranking.get(0).getName()), 15, StatsKey.COINS);
						getStats().addInt(Bukkit.getPlayer(ranking.get(0).getName()), 1, StatsKey.WIN);
					}

					for(Player player : getGameList().getPlayers().keySet()){
						if(!ranking.get(0).getName().equalsIgnoreCase(player.getName())){
							if(player.isOnline()){
								getStats().addInt(player, 1, StatsKey.LOSE);
							}
						}
					}
					
					UtilScoreboard.resetScore(scoreGER, 6, DisplaySlot.SIDEBAR);
					UtilScoreboard.setScore(scoreGER, "§a§lGewinner:", DisplaySlot.SIDEBAR, 6);
					UtilScoreboard.resetScore(scoreGER, 5, DisplaySlot.SIDEBAR);
					UtilScoreboard.setScore(scoreGER, "§7"+ranking.get(0).getName(), DisplaySlot.SIDEBAR, 5);

					UtilScoreboard.resetScore(scoreENG, 6, DisplaySlot.SIDEBAR);
					UtilScoreboard.setScore(scoreENG, "§a§lWinner:", DisplaySlot.SIDEBAR, 6);
					UtilScoreboard.resetScore(scoreENG, 5, DisplaySlot.SIDEBAR);
					UtilScoreboard.setScore(scoreENG, "§7"+ranking.get(0).getName(), DisplaySlot.SIDEBAR, 5);
					
					broadcastWithPrefix("MASTERBUILDER_WIN", new String[]{"§e§l1",ranking.get(0).getName()});
					setWinner( ranking.get(0).getName() );
					
					if(area.containsKey(ranking.get(0).getName())){
						for(Player player : UtilServer.getPlayers())player.teleport( getWorldData().getLocs( area.get(ranking.get(0).getName()) ).get(0).clone().add(0, 5, 0) );
					}
				}
				if(ranking.size()>=2){
					if(UtilPlayer.isOnline(ranking.get(1).getName())){
						getMoney().addInt(Bukkit.getPlayer(ranking.get(1).getName()), 10, StatsKey.COINS);
					}
					broadcastWithPrefix("MASTERBUILDER_WIN", new String[]{"§6§l2",ranking.get(1).getName()});
				}
				if(ranking.size()>=3){
					if(UtilPlayer.isOnline(ranking.get(2).getName())){
						getMoney().addInt(Bukkit.getPlayer(ranking.get(2).getName()), 5, StatsKey.COINS);
					}
					broadcastWithPrefix("MASTERBUILDER_WIN", new String[]{"§c§l3",ranking.get(2).getName()});
				}
			}else if(getStart()==0){
				setState(GameState.Restart);	
			}
			
			if(ranking!=null){
				for(int i = 0 ; i<10; i++){
					UtilFirework.start(getWorldData().getLocs( area.get(ranking.get(0).getName()) ).get(0).clone().add(UtilMath.RandomInt(15, -15), UtilMath.RandomInt(19, 13), UtilMath.RandomInt(15, -15)), Color.rdmColor(), Type.BALL);
				}
			}
			
			setStart(getStart()-1);
			
		}
	}
	
	String p;
	ArrayList<String> list;
	boolean explosion=false;
	@EventHandler
	public void lookTime(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		if(getState()!=GameState.SchutzModus)return;
		if(list==null){
			list=new ArrayList<>();
			for(Player player : getGameList().getPlayers(PlayerState.IN))list.add(player.getName());
		}
		if(getStart()<-5){
			setStart(21);
			if(ranking==null)ranking = new ArrayList<>();
			
			if(!explosion&&p!=null){
				int i=0;
					
				for(Player p1 : vote.get(area.get(p)).keySet()){
					i+=vote.get(area.get(p)).get(p1);
				}
				ranking.add(new kSort(p, i));
					
				if(i<(vote.get(area.get(p)).size())){
					if(this.team_areas.containsKey(area.get(p))&&!this.team_areas.get(area.get(p)).getBlocks().isEmpty()){
						UtilParticle.EXPLOSION_HUGE.display(4.0F, 4, ((Location)this.team_areas.get(area.get(p)).getBlocks().keySet().toArray()[0]), 40);
						for(Location loc : this.team_areas.get(area.get(p)).getBlocks().keySet()){
							loc.getWorld().spawnFallingBlock(loc, loc.getBlock().getType(), loc.getBlock().getData()).setVelocity(new Vector(UtilMath.RandomInt(10, -10),UtilMath.RandomInt(15, 13),UtilMath.RandomInt(10, -10)));
							loc.getBlock().setType(Material.AIR);
						}
						explosion=true;
						setStart(-1);
						return;
					}
				}else{
					if(UtilPlayer.isOnline(p)){
						getMoney().addInt(Bukkit.getPlayer(p), 2, StatsKey.COINS);
					}
				}
				
				p=null;
				i=0;
			}
			
			if(list.isEmpty()){
				for(Player player : UtilServer.getPlayers())player.getInventory().clear();
				UtilScoreboard.resetScore(scoreGER, 2, DisplaySlot.SIDEBAR);
				UtilScoreboard.resetScore(scoreGER, 3, DisplaySlot.SIDEBAR);
				UtilScoreboard.resetScore(scoreGER, 4, DisplaySlot.SIDEBAR);
				UtilScoreboard.resetScore(scoreENG, 2, DisplaySlot.SIDEBAR);
				UtilScoreboard.resetScore(scoreENG, 3, DisplaySlot.SIDEBAR);
				UtilScoreboard.resetScore(scoreENG, 4, DisplaySlot.SIDEBAR);
				setState(GameState.DeathMatch);
				setStart(15);
				return;
			}
			
			p=list.get(0);
			list.remove(p);
			explosion=false;
			
			for(Player player : UtilServer.getPlayers()){
				if(Language.getLanguage(player)==LanguageType.GERMAN){
					player.getInventory().setContents(this.items_bewertungGER);
				}else{
					player.getInventory().setContents(this.items_bewertungENG);
				}
				player.teleport(getWorldData().getLocs(area.get(p)).get(0).clone().add(0, 5, 0));	
			}
		}
		
		setStart(getStart()-1);
		
		if(getStart()==0){
			Title en = new Title("§7Built by","§e"+p);
			Title ger = new Title("§7Gebaut von","§e"+p);
			
			for(Player player : UtilServer.getPlayers()){
				player.getInventory().clear();
				if(Language.getLanguage(player)==LanguageType.GERMAN){
					ger.send(player);
				}else{
					en.send(player);
				}
			}
		}

		UtilScoreboard.resetScore(scoreENG, 2, DisplaySlot.SIDEBAR);
		UtilScoreboard.setScore(scoreENG, "§7"+ (getStart()<=0? "0 sec" : UtilTime.formatSeconds(getStart())) , DisplaySlot.SIDEBAR, 2);
		UtilScoreboard.resetScore(scoreGER, 2, DisplaySlot.SIDEBAR);
		UtilScoreboard.setScore(scoreGER, "§7"+(getStart()<=0? "0 sec" : UtilTime.formatSeconds(getStart())), DisplaySlot.SIDEBAR, 2);
	}

	@EventHandler
	public void mvoe(InventoryClickEvent ev){
		if(getState()==GameState.SchutzModus)ev.setCancelled(true);
	}
	
	ParticleItem particleItem;
	@EventHandler
	public void interact(PlayerInteractEvent ev){
		if(getState() == GameState.SchutzModus&&p!=null){
			if(ev.getPlayer().getItemInHand()!=null
					&&(ev.getPlayer().getInventory().getHeldItemSlot()>=0
					&& ev.getPlayer().getInventory().getHeldItemSlot()<=this.items_bewertungGER.length-1)){
				if(!vote.containsKey(area.get(p))){
					vote.put(area.get(p), new HashMap<>());
				}
				
				if(ev.getPlayer().getName().equalsIgnoreCase(p))return;
				if(vote.get(area.get(p)).containsKey(ev.getPlayer())&&vote.get(area.get(p)).get(ev.getPlayer())==ev.getPlayer().getInventory().getHeldItemSlot())return;
				
				if(Language.getLanguage(ev.getPlayer())==LanguageType.GERMAN){
					ev.getPlayer().getInventory().setContents(this.items_bewertungGER);
				}else{
					ev.getPlayer().getInventory().setContents(this.items_bewertungENG);
				}
				
				ev.getPlayer().setItemInHand(UtilItem.addEnchantmentGlow(ev.getPlayer().getItemInHand()));
				vote.get(area.get(p)).remove(ev.getPlayer());
				vote.get(area.get(p)).put(ev.getPlayer(), ev.getPlayer().getInventory().getHeldItemSlot());
				new Title("",ev.getPlayer().getItemInHand().getItemMeta().getDisplayName()).send(ev.getPlayer());
				ev.getPlayer().updateInventory();
				ev.getPlayer().playSound(ev.getPlayer().getLocation(), Sound.BLAZE_HIT, 1F, 1F);
			}
		}else if(getState()==GameState.InGame&&area.containsKey(ev.getPlayer().getName())){
			if(ev.getPlayer().getItemInHand()!=null){
				if(ev.getPlayer().getItemInHand().getType()==Material.BOOK){
					ev.getPlayer().openInventory(this.option.getMain());
				}else if(ev.getPlayer().getItemInHand().hasItemMeta()){
					if(ev.getPlayer().getItemInHand().getItemMeta().hasDisplayName()){
						if(ev.getPlayer().getItemInHand().getItemMeta().getDisplayName().startsWith("§b")){
							particleItem=ParticleItem.valueOf(ev.getPlayer().getItemInHand());
							
							if(particleItem!=null){
								if(!particles.containsKey(area.get(ev.getPlayer().getName()))){
									particles.put(area.get(ev.getPlayer().getName()), new HashMap<Location,UtilParticle>());
								}
								
								if(particles.get(area.get(ev.getPlayer().getName())).size()>=20){
									ev.getPlayer().sendMessage(Language.getText(ev.getPlayer(),"PREFIX_GAME",getType().getTyp())+Language.getText("MASTERBUILDER_PARTICLE_MAX"));
								}else{
									ev.getPlayer().sendMessage(Language.getText(ev.getPlayer(),"PREFIX_GAME",getType().getTyp())+Language.getText("MASTERBUILDER_PARTICLE_PLACE"));
									particles.get(area.get(ev.getPlayer().getName())).put(ev.getPlayer().getLocation(), particleItem.getParticle());
								}
								ev.setCancelled(true);
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void inGame(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		if(getState()!=GameState.InGame)return;
		if(getStart()<0){
			setStart((7*60)+1);
		}
		setStart(getStart()-1);
		
		UtilScoreboard.resetScore(scoreENG, 2, DisplaySlot.SIDEBAR);
		UtilScoreboard.setScore(scoreENG, "§7"+UtilTime.formatSeconds(getStart()), DisplaySlot.SIDEBAR, 2);
		UtilScoreboard.resetScore(scoreGER, 2, DisplaySlot.SIDEBAR);
		UtilScoreboard.setScore(scoreGER, "§7"+UtilTime.formatSeconds(getStart()), DisplaySlot.SIDEBAR, 2);
		
		switch(getStart()){
		case 30: broadcastWithPrefix("BUILD_END_IN",UtilTime.formatSeconds(getStart()));break;
		case 15: broadcastWithPrefix("BUILD_END_IN",UtilTime.formatSeconds(getStart()));break;
		case 10: broadcastWithPrefix("BUILD_END_IN",UtilTime.formatSeconds(getStart()));break;
		case 5: broadcastWithPrefix("BUILD_END_IN",UtilTime.formatSeconds(getStart()));break;
		case 4: broadcastWithPrefix("BUILD_END_IN",UtilTime.formatSeconds(getStart()));break;
		case 3: broadcastWithPrefix("BUILD_END_IN",UtilTime.formatSeconds(getStart()));break;
		case 2: broadcastWithPrefix("BUILD_END_IN",UtilTime.formatSeconds(getStart()));break;
		case 1: broadcastWithPrefix("BUILD_END_IN",UtilTime.formatSeconds(getStart()));break;
		case 0:
			broadcastWithPrefixName("BUILD_END");
			UtilScoreboard.resetScore(scoreENG, 3, DisplaySlot.SIDEBAR);
			UtilScoreboard.setScore(scoreENG, "§cVote Time:", DisplaySlot.SIDEBAR, 3);
			
			UtilScoreboard.resetScore(scoreGER, 3, DisplaySlot.SIDEBAR);
			UtilScoreboard.setScore(scoreGER, "§cVote Zeit:", DisplaySlot.SIDEBAR, 3);
			
			setStart(-1);
			setState(GameState.SchutzModus);
			for(Player player : UtilServer.getPlayers()){
				player.setGameMode(GameMode.ADVENTURE);
				player.setAllowFlight(true);
				player.setFlying(true);
				if(Language.getLanguage(player)==LanguageType.GERMAN){
					player.getInventory().setContents(this.items_bewertungGER);
				}else{
					player.getInventory().setContents(this.items_bewertungENG);
				}
			}
			break;
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void addonAreaRestoreEvent(AddonAreaRestoreEvent ev){
		if(GameState.InGame!=getState()){
			ev.setBuild(false);
			ev.setCancelled(true);
		}
	}
	
	public void createArea(Team team,Player player){
		Location ecke1 = getWorldData().getLocs(getArea(team)).get(0).clone();
		ecke1.getChunk().load();
		for(int y = ecke1.getBlockY(); y > 0 ; y--){
			if(ecke1.getWorld().getBlockAt(ecke1.getBlockX(), y, ecke1.getBlockZ()).getType()==Material.COAL_BLOCK){
				ecke1=ecke1.getWorld().getBlockAt(ecke1.getBlockX(), y+1, ecke1.getBlockZ()).getLocation();
				break;
			}
		}
		
		Location ecke2 = getWorldData().getLocs(getArea(team)).get(1).clone();
		ecke2.getChunk().load();
		for(int y = ecke1.getBlockY(); y < 255 ; y++){
			if(ecke2.getWorld().getBlockAt(ecke2.getBlockX(), y, ecke2.getBlockZ()).getType()==Material.BARRIER){
				ecke2=ecke2.getWorld().getBlockAt(ecke2.getBlockX(), y-1, ecke2.getBlockZ()).getLocation();
				break;
			}
		}
		
		AddonArea a = new AddonArea(getManager().getInstance(), ecke1, ecke2);
		a.getPlayers().add(player);
		this.team_areas.put(team, a);
	}
	
	@EventHandler
	public void Ranking(RankingEvent ev){
		getManager().setRanking(StatsKey.WIN);
	}
	
	@EventHandler
	public void StatsLoaded(PlayerStatsLoadedEvent ev){
		if(ev.getManager().getType() != getType())return;
		if(getState()!=GameState.LobbyPhase)return;
		if(UtilPlayer.isOnline(ev.getPlayername())){
			Player player = Bukkit.getPlayer(ev.getPlayername());
			int win = getStats().getInt(StatsKey.WIN, player);
			int lose = getStats().getInt(StatsKey.LOSE, player);
			
			Bukkit.getScheduler().runTask(getManager().getInstance(), new Runnable() {
				
				@Override
				public void run() {
					getManager().getHologram().sendText(player,getManager().getLoc_stats(),new String[]{
					Color.GREEN+getType().getTyp()+Color.ORANGE+"§l Info",
					Language.getText(player, "GAME_HOLOGRAM_SERVER",getType().getTyp()+" §a"+kArcade.id),
					Language.getText(player, "GAME_HOLOGRAM_MAP", getWorldData().getMapName()),
					" ",
					Language.getText(player, "GAME_HOLOGRAM_GAMES", (win+lose)),
					Language.getText(player, "GAME_HOLOGRAM_WINS", win),
					Language.getText(player, "GAME_HOLOGRAM_LOSE", lose),
					});
				}
			});
		}
	}
	
	@EventHandler
	public void start(GameStartEvent ev){
		setState(GameState.InGame);
		this.mainArea=new AddonMainArea(getManager().getInstance());
		this.building=Buildings.values()[UtilMath.r(Buildings.values().length)];
		
		scoreENG=Bukkit.getScoreboardManager().getNewScoreboard();
		UtilScoreboard.addBoard(scoreENG, DisplaySlot.SIDEBAR, "§6§lEpicPvP.eu - Board");
		UtilScoreboard.setScore(scoreENG, " ", DisplaySlot.SIDEBAR, 7);
		UtilScoreboard.setScore(scoreENG, "§f§lBuild:", DisplaySlot.SIDEBAR, 6);
		UtilScoreboard.setScore(scoreENG, "§7"+this.building.getEnglish(), DisplaySlot.SIDEBAR, 5);
		UtilScoreboard.setScore(scoreENG, "  ", DisplaySlot.SIDEBAR, 4);
		UtilScoreboard.setScore(scoreENG, "§f§lTime: ", DisplaySlot.SIDEBAR, 3);
		UtilScoreboard.setScore(scoreENG, "§7-", DisplaySlot.SIDEBAR, 2);
		UtilScoreboard.setScore(scoreENG, "", DisplaySlot.SIDEBAR, 1);

		scoreGER=Bukkit.getScoreboardManager().getNewScoreboard();
		UtilScoreboard.addBoard(scoreGER, DisplaySlot.SIDEBAR, "§6§lEpicPvP.eu - Board");
		UtilScoreboard.setScore(scoreGER, " ", DisplaySlot.SIDEBAR, 7);
		UtilScoreboard.setScore(scoreGER, "§f§lBaue:", DisplaySlot.SIDEBAR, 6);
		UtilScoreboard.setScore(scoreGER, "§7"+this.building.getGerman(), DisplaySlot.SIDEBAR, 5);
		UtilScoreboard.setScore(scoreGER, "  ", DisplaySlot.SIDEBAR, 4);
		UtilScoreboard.setScore(scoreGER, "§f§lZeit: ", DisplaySlot.SIDEBAR, 3);
		UtilScoreboard.setScore(scoreGER, "§7-", DisplaySlot.SIDEBAR, 2);
		UtilScoreboard.setScore(scoreGER, "", DisplaySlot.SIDEBAR, 1);
		
		UtilScoreboard.setTeams(scoreENG, getManager().getPermManager().getScoreboard().getTeams());
		UtilScoreboard.setTeams(scoreGER, getManager().getPermManager().getScoreboard().getTeams());
		
		int i=0;
		ItemStack option = UtilItem.RenameItem(new ItemStack(Material.BOOK), "§bOption");
		for(Player player : UtilServer.getPlayers()){
			getManager().Clear(player);
			if(i>=12){
				System.err.println("MasterBuilders zu viele Spieler Online ("+UtilServer.getPlayers().size()+")! Der Spieler "+player.getName()+" wird gegickt");
				player.kickPlayer("To many Players!");
				break;
			}
			player.getInventory().setItem(8, option);
			getGameList().addPlayer(player, PlayerState.IN);
			area.put(player.getName(), mtype.getTeam()[i]);
			player.teleport(getWorldData().getLocs(area.get(player.getName())).get(0).clone().add(0, 5, 0));

			createArea(mtype.getTeam()[i],player);
			if(Language.getLanguage(player)==LanguageType.GERMAN){
				player.setScoreboard(scoreGER);
				new Title("","§a§l"+this.building.getGerman()).send(player);
			}else{
				new Title("","§a§l"+this.building.getEnglish()).send(player);
				player.setScoreboard(scoreENG);
			}
			
			player.setGameMode(GameMode.CREATIVE);
			i++;
		}
		new AddonDay(getManager().getInstance(), getWorldData().getWorld());
		
		if(Bukkit.getPluginManager().getPlugin("AAC").isEnabled())new AACListener(getManager().getInstance());
	}
	
}
