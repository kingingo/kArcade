package me.kingingo.karcade.Game.Single.Games.Masterbuilders;

import java.awt.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import me.kingingo.karcade.kArcade;
import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Events.RankingEvent;
import me.kingingo.karcade.Game.Events.GameStartEvent;
import me.kingingo.karcade.Game.Single.SingleWorldData;
import me.kingingo.karcade.Game.Single.Addons.AddonArea;
import me.kingingo.karcade.Game.Single.Addons.AddonMainArea;
import me.kingingo.karcade.Game.Single.Events.AddonAreaRestoreEvent;
import me.kingingo.karcade.Game.Single.Games.SoloGame;
import me.kingingo.kcore.Addons.AddonDay;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.Enum.PlayerState;
import me.kingingo.kcore.Enum.Team;
import me.kingingo.kcore.Language.Language;
import me.kingingo.kcore.Language.LanguageType;
import me.kingingo.kcore.Permission.kPermission;
import me.kingingo.kcore.StatsManager.Stats;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.Color;
import me.kingingo.kcore.Util.Title;
import me.kingingo.kcore.Util.UtilFirework;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilLocation;
import me.kingingo.kcore.Util.UtilMath;
import me.kingingo.kcore.Util.UtilParticle;
import me.kingingo.kcore.Util.UtilPlayer;
import me.kingingo.kcore.Util.UtilScoreboard;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.kcore.Util.UtilString;
import me.kingingo.kcore.Util.UtilTime;
import me.kingingo.kcore.kListen.kRank;

import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

public class Masterbuilders extends SoloGame{
	
	private HashMap<Player, Team> area;
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
	
	private Scoreboard scoreENG;
	private Scoreboard scoreGER;
	
	public Masterbuilders(kArcadeManager manager,MasterbuildersType mtype) {
		super(manager);
		registerListener();
		long l = System.currentTimeMillis();
		setTyp(GameType.Masterbuilders);
		setState(GameState.Laden);
		
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
	public void Chat(AsyncPlayerChatEvent ev){
		if(ev.isCancelled())return;
		ev.setCancelled(true);
		
		if((!ev.getPlayer().hasPermission(kPermission.CHAT_LINK.getPermissionToString()))&&UtilString.isBadWord(ev.getMessage())||UtilString.checkForIP(ev.getMessage())){
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
	ArrayList<kRank> ranking;
	@EventHandler
	public void win(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		if(getState()==GameState.DeathMatch){
			if(getStart()==15){
				Collections.sort(ranking,kRank.DESCENDING);
				
				if(!ranking.isEmpty()){
					getStats().setInt(ranking.get(0).getPlayer(), getStats().getInt(Stats.WIN, ranking.get(0).getPlayer()), Stats.WIN);

					for(Player player : getGameList().getPlayers().keySet()){
						if(ranking.get(0).getPlayer().getUniqueId()==player.getUniqueId()){
							
						}
					}
					
					UtilScoreboard.resetScore(scoreGER, 6, DisplaySlot.SIDEBAR);
					UtilScoreboard.setScore(scoreGER, "§a§lGewinner:", DisplaySlot.SIDEBAR, 6);
					UtilScoreboard.resetScore(scoreGER, 5, DisplaySlot.SIDEBAR);
					UtilScoreboard.setScore(scoreGER, "§7"+ranking.get(0).getPlayer().getName(), DisplaySlot.SIDEBAR, 5);

					UtilScoreboard.resetScore(scoreENG, 6, DisplaySlot.SIDEBAR);
					UtilScoreboard.setScore(scoreENG, "§a§lWinner:", DisplaySlot.SIDEBAR, 6);
					UtilScoreboard.resetScore(scoreENG, 5, DisplaySlot.SIDEBAR);
					UtilScoreboard.setScore(scoreENG, "§7"+ranking.get(0).getPlayer().getName(), DisplaySlot.SIDEBAR, 5);
					
					broadcastWithPrefix("MASTERBUILDER_WIN", new String[]{"§e§l1",ranking.get(0).getPlayer().getName()});
					setWinner( ranking.get(0).getPlayer().getName() );
					
					if(area.containsKey(ranking.get(0).getPlayer())){
						for(Player player : UtilServer.getPlayers())player.teleport( getWorldData().getLocs( area.get(ranking.get(0).getPlayer()) ).get(0) );
					}
				}
				if(ranking.size()>=2)broadcastWithPrefix("MASTERBUILDER_WIN", new String[]{"§6§l2",ranking.get(1).getPlayer().getName()});
				if(ranking.size()>=3)broadcastWithPrefix("MASTERBUILDER_WIN", new String[]{"§c§l3",ranking.get(2).getPlayer().getName()});
			}else if(getStart()==0){
				setState(GameState.Restart);	
			}
			
			if(ranking!=null){
				for(int i = 0 ; i<10; i++){
					UtilFirework.start(getWorldData().getLocs( area.get(ranking.get(0).getPlayer()) ).get(0).clone().add(UtilMath.RandomInt(15, -15), UtilMath.RandomInt(19, 13), UtilMath.RandomInt(15, -15)), Color.rdmColor(), Type.BALL);
				}
			}
			
			setStart(getStart()-1);
			
		}
	}
	
	@EventHandler
	public void EntityChangeBlock(EntityChangeBlockEvent ev){
		if(ev.getEntityType()==EntityType.FALLING_BLOCK){
			ev.getEntity().remove();
			ev.getBlock().setType(Material.AIR);
		}
	}
	
	int team=-1;
	ArrayList<Player> list;
	boolean explosion=false;
	@EventHandler
	public void lookTime(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		if(getState()!=GameState.SchutzModus)return;
		if(list==null)list=getGameList().getPlayers(PlayerState.IN);
		if(getStart()<0){
			setStart(21);
			if(ranking==null)ranking = new ArrayList<>();
			
			if(!explosion&&team!=-1){
				Player p = list.get(team);
				int i=0;
					
				if(p!=null){
					for(Player p1 : vote.get(area.get(list.get(team))).keySet()){
						i+=vote.get(area.get(list.get(team))).get(p1);
					}
					ranking.add(new kRank(p, i));
					
					if(i<=(vote.get(area.get(list.get(team))).size())){
						if(this.team_areas.containsKey(area.get(p))&&!this.team_areas.get(area.get(p)).getBlocks().isEmpty()){
							UtilParticle.EXPLOSION_HUGE.display(4.0F, 4, ((Location)this.team_areas.get(area.get(p)).getBlocks().keySet().toArray()[0]), 40);
							for(Location loc : this.team_areas.get(area.get(list.get(team))).getBlocks().keySet()){
								loc.getWorld().spawnFallingBlock(loc, loc.getBlock().getType(), loc.getBlock().getData()).setVelocity(new Vector(UtilMath.RandomInt(10, -10),UtilMath.RandomInt(15, 13),UtilMath.RandomInt(10, -10)));
								loc.getBlock().setType(Material.AIR);
							}
							explosion=true;
							setStart(3);
							return;
						}
					}
				}else{
					getManager().DebugLog("PLAYER == NULL");
				}
				p=null;
				i=0;
			}
			
			this.team++;
			explosion=false;
			
			if(area.size()==this.team){
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
			}else{
				

				UtilScoreboard.resetScore(scoreGER, 5, DisplaySlot.SIDEBAR);
				UtilScoreboard.setScore(scoreGER, "§7"+list.get(team).getName(), DisplaySlot.SIDEBAR, 5);
				
				UtilScoreboard.resetScore(scoreENG, 5, DisplaySlot.SIDEBAR);
				UtilScoreboard.setScore(scoreENG, "§7"+list.get(team).getName(), DisplaySlot.SIDEBAR, 5);
				

				for(Player player : UtilServer.getPlayers()){
					if(Language.getLanguage(player)==LanguageType.GERMAN){
						player.getInventory().setContents(this.items_bewertungGER);
					}else{
						player.getInventory().setContents(this.items_bewertungENG);
					}
					player.teleport(getWorldData().getLocs(area.get(list.get(team))).get(0).clone().add(0, 5, 0));	
				}
			}
		}
		setStart(getStart()-1);

		UtilScoreboard.resetScore(scoreENG, 2, DisplaySlot.SIDEBAR);
		UtilScoreboard.setScore(scoreENG, "§7"+UtilTime.formatSeconds(getStart()), DisplaySlot.SIDEBAR, 2);
		UtilScoreboard.resetScore(scoreGER, 2, DisplaySlot.SIDEBAR);
		UtilScoreboard.setScore(scoreGER, "§7"+UtilTime.formatSeconds(getStart()), DisplaySlot.SIDEBAR, 2);
	}

	@EventHandler
	public void mvoe(InventoryClickEvent ev){
		if(getState()==GameState.SchutzModus)ev.setCancelled(true);
	}
	
	@EventHandler
	public void interact(PlayerInteractEvent ev){
		if(getState() == GameState.SchutzModus&&team!=-1){
			if(ev.getPlayer().getItemInHand()!=null
					&&(ev.getPlayer().getInventory().getHeldItemSlot()>=0
					&& ev.getPlayer().getInventory().getHeldItemSlot()<=this.items_bewertungGER.length-1)){
				if(!vote.containsKey(area.get(list.get(team)))){
					vote.put(area.get(list.get(team)), new HashMap<>());
				}
				
				if(ev.getPlayer().getUniqueId()==list.get(team).getUniqueId())return;
				if(vote.get(area.get(list.get(team))).containsKey(ev.getPlayer())&&vote.get(area.get(list.get(team))).get(ev.getPlayer())==ev.getPlayer().getInventory().getHeldItemSlot())return;
				
				if(Language.getLanguage(ev.getPlayer())==LanguageType.GERMAN){
					ev.getPlayer().getInventory().setContents(this.items_bewertungGER);
				}else{
					ev.getPlayer().getInventory().setContents(this.items_bewertungENG);
				}
				
				ev.getPlayer().setItemInHand(UtilItem.addEnchantmentGlow(ev.getPlayer().getItemInHand()));
				vote.get(area.get(list.get(team))).remove(ev.getPlayer());
				vote.get(area.get(list.get(team))).put(ev.getPlayer(), ev.getPlayer().getInventory().getHeldItemSlot());
				new Title("",ev.getPlayer().getItemInHand().getItemMeta().getDisplayName()).send(ev.getPlayer());
				ev.getPlayer().updateInventory();
				ev.getPlayer().playSound(ev.getPlayer().getLocation(), Sound.BLAZE_HIT, 1F, 1F);
			}
		}
	}
	
	@EventHandler
	public void inGame(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		if(getState()!=GameState.InGame)return;
		if(getStart()<0){
			setStart((5*60)+1);
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
			UtilScoreboard.resetScore(scoreENG, 6, DisplaySlot.SIDEBAR);
			UtilScoreboard.setScore(scoreENG, "§f§lBuilder:", DisplaySlot.SIDEBAR, 6);
			
			UtilScoreboard.resetScore(scoreGER, 3, DisplaySlot.SIDEBAR);
			UtilScoreboard.setScore(scoreGER, "§cVote Zeit:", DisplaySlot.SIDEBAR, 3);
			UtilScoreboard.resetScore(scoreGER, 6, DisplaySlot.SIDEBAR);
			UtilScoreboard.setScore(scoreGER, "§f§lBauer:", DisplaySlot.SIDEBAR, 6);
			
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
		getManager().setRanking(Stats.WIN);
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void JoinHologram(PlayerJoinEvent ev){
		if(getState()!=GameState.LobbyPhase)return;
		int win = getStats().getInt(Stats.WIN, ev.getPlayer());
		int lose = getStats().getInt(Stats.LOSE, ev.getPlayer());
		getManager().getHologram().sendText(ev.getPlayer(),getManager().getLoc_stats(),new String[]{
		Color.GREEN+getType().getTyp()+Color.ORANGE+"§l Info",
		Language.getText(ev.getPlayer(), "GAME_HOLOGRAM_SERVER",getType().getTyp()+" §a"+kArcade.id),
		Language.getText(ev.getPlayer(), "GAME_HOLOGRAM_MAP", getWorldData().getMapName()),
		" ",
		Language.getText(ev.getPlayer(), "GAME_HOLOGRAM_GAMES", (win+lose)),
		Language.getText(ev.getPlayer(), "GAME_HOLOGRAM_WINS", win),
		Language.getText(ev.getPlayer(), "GAME_HOLOGRAM_LOSE", lose),
		});
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
		
		int i=0;
		for(Player player : UtilServer.getPlayers()){
			getManager().Clear(player);
			getGameList().addPlayer(player, PlayerState.IN);
			area.put(player, mtype.getTeam()[i]);
			player.teleport(getWorldData().getLocs(area.get(player)).get(0).clone().add(0, 5, 0));

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
	}
	
}
