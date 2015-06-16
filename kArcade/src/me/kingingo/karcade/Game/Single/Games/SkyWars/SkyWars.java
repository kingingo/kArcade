package me.kingingo.karcade.Game.Single.Games.SkyWars;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import me.kingingo.karcade.kArcade;
import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Enum.PlayerState;
import me.kingingo.karcade.Events.RankingEvent;
import me.kingingo.karcade.Game.Events.GameStateChangeEvent;
import me.kingingo.karcade.Game.Single.Events.AddonEntityKingDeathEvent;
import me.kingingo.karcade.Game.Single.Games.SoloGame;
import me.kingingo.karcade.Game.Single.Games.TeamGame;
import me.kingingo.karcade.Game.Single.addons.AddonEntityKing;
import me.kingingo.karcade.Game.Single.addons.AddonTargetNextPlayer;
import me.kingingo.karcade.Game.Single.addons.AddonVoteTeam;
import me.kingingo.karcade.Game.World.WorldData;
import me.kingingo.kcore.Addons.AddonDay;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.Enum.Team;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.Game.Events.GameStartEvent;
import me.kingingo.kcore.Kit.Kit;
import me.kingingo.kcore.Kit.KitType;
import me.kingingo.kcore.Kit.Perk;
import me.kingingo.kcore.Kit.Perks.PerkArrowFire;
import me.kingingo.kcore.Kit.Perks.PerkArrowInfinity;
import me.kingingo.kcore.Kit.Perks.PerkDeathDropOnly;
import me.kingingo.kcore.Kit.Perks.PerkEquipment;
import me.kingingo.kcore.Kit.Perks.PerkHeal;
import me.kingingo.kcore.Kit.Perks.PerkHealByHit;
import me.kingingo.kcore.Kit.Perks.PerkHolzfäller;
import me.kingingo.kcore.Kit.Perks.PerkMoreHearth;
import me.kingingo.kcore.Kit.Perks.PerkNoDropsByDeath;
import me.kingingo.kcore.Kit.Perks.PerkNoExplosionDamage;
import me.kingingo.kcore.Kit.Perks.PerkNoFalldamage;
import me.kingingo.kcore.Kit.Perks.PerkNoFiredamage;
import me.kingingo.kcore.Kit.Perks.PerkNoHunger;
import me.kingingo.kcore.Kit.Perks.PerkNoKnockback;
import me.kingingo.kcore.Kit.Perks.PerkPoisen;
import me.kingingo.kcore.Kit.Perks.PerkPotionByDeath;
import me.kingingo.kcore.Kit.Perks.PerkRespawnBuff;
import me.kingingo.kcore.Kit.Perks.PerkSneakDamage;
import me.kingingo.kcore.Kit.Perks.PerkSpawnByDeath;
import me.kingingo.kcore.Kit.Perks.PerkStopPerk;
import me.kingingo.kcore.Kit.Perks.PerkTNT;
import me.kingingo.kcore.Kit.Perks.PerkWalkEffect;
import me.kingingo.kcore.Kit.Shop.KitShop;
import me.kingingo.kcore.Permission.kPermission;
import me.kingingo.kcore.StatsManager.Stats;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.Color;
import me.kingingo.kcore.Util.UtilDisplay;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.InventorySize;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilLocation;
import me.kingingo.kcore.Util.UtilMath;
import me.kingingo.kcore.Util.UtilParticle;
import me.kingingo.kcore.Util.UtilPlayer;
import me.kingingo.kcore.Util.UtilScoreboard;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.kcore.Util.UtilString;
import me.kingingo.kcore.Util.UtilTime;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

public class SkyWars extends TeamGame{

	private HashMap<Location,Inventory> chests = new HashMap<>();
	private ArrayList<ItemStack> chest_material = new ArrayList<>();
	private AddonTargetNextPlayer TargetNextPlayer;
	private SkyWarsType type;
	private KitShop kitshop;
	private HashMap<Player,String> kits = new HashMap<>();
	
	public SkyWars(kArcadeManager manager,SkyWarsType type){
		super(manager);
		registerListener();
		long l = System.currentTimeMillis();
		this.type=type;
		setTyp(GameType.SkyWars);
		setState(GameState.Laden);
		setMax_Players(type.getMax());
		setMin_Players(type.getMin());
		setDamage(true);
		setDamagePvP(true);
		setDamageSelf(true);
		setBlockBreak(true);
		setBlockPlace(true);
		setCreatureSpawn(false);
		setCompassAddon(true);
		setDeathDropItems(true);
		setFoodChange(true);
		setItemPickup(true);
		setItemDrop(true);
		setRespawn(true);
		setWorldData(new WorldData(manager,getType()));
		getWorldData().LoadWorldConfig();
		if(type.getTeam_size()!=1)setVoteTeam(new AddonVoteTeam(this,type.getTeam(),InventorySize._27,type.getTeam_size()));
		
		kitshop=new KitShop(getManager().getInstance(), getCoins(), getManager().getPermManager(), "Kit-Shop", InventorySize._27, new Kit[]{
			new Kit( "§aStarter",new String[]{"Der Starter bekommt kein Hunger."}, new ItemStack(Material.WOOD_SWORD),kPermission.SHEEPWARS_KIT_STARTER,KitType.STARTER,2000,new Perk[]{
				new PerkNoHunger()
			}),
			new Kit( "§aSuperman",new String[]{"Der Superman ist das Beste kit in SheepWars!"}, new ItemStack(Material.DIAMOND_SWORD),kPermission.SHEEPWARS_KIT_SUPERMAN,KitType.ADMIN,2000,new Perk[]{
				new PerkNoHunger(),
				new PerkEquipment(new ItemStack[]{new ItemStack(Material.IRON_CHESTPLATE,1)}),
				new PerkSneakDamage(1),
				new PerkPoisen(10,50),
				new PerkHolzfäller(),
				new PerkNoFiredamage(),
				new PerkNoFalldamage(),
				new PerkArrowFire(80),
				new PerkNoExplosionDamage(),
				new PerkTNT(),
				new PerkHealByHit(60, 6),
				new PerkHeal(6),
				new PerkMoreHearth(6, 60),
				new PerkArrowInfinity(),
				new PerkWalkEffect(Effect.HEART,10),
				new PerkSpawnByDeath(EntityType.PRIMED_TNT,80),
				new PerkNoKnockback(getManager().getInstance())
			})
		});
		
		getManager().DebugLog(l, this.getClass().getName());
	}
	
	public void loadMaterialList(){
		chest_material.add(new ItemStack(Material.POTION,2,(byte) 8257));
		chest_material.add(new ItemStack(Material.POTION,2,(byte) 8259));
		chest_material.add(new ItemStack(Material.POTION,2,(byte) 8226));
		chest_material.add(new ItemStack(Material.POTION,2,(byte) 8233));
		chest_material.add(new ItemStack(Material.POTION,2,(byte) 8257));
		chest_material.add(new ItemStack(Material.POTION,2,(byte) 8259));
		chest_material.add(new ItemStack(Material.POTION,2,(byte) 8226));
		chest_material.add(new ItemStack(Material.POTION,2,(byte) 8233));
		chest_material.add(new ItemStack(Material.EXP_BOTTLE,16));
		chest_material.add(new ItemStack(Material.ENCHANTMENT_TABLE,1));
		chest_material.add(new ItemStack(Material.BOOKSHELF,8));
		chest_material.add(new ItemStack(Material.DIAMOND,3));
		chest_material.add(new ItemStack(Material.DIAMOND_HELMET,1));
		chest_material.add(new ItemStack(Material.DIAMOND_CHESTPLATE,1));
		chest_material.add(new ItemStack(Material.DIAMOND_LEGGINGS,1));
		chest_material.add(new ItemStack(Material.DIAMOND_BOOTS,1));
		chest_material.add(new ItemStack(Material.IRON_HELMET,1));
		chest_material.add(new ItemStack(Material.IRON_CHESTPLATE,1));
		chest_material.add(new ItemStack(Material.IRON_LEGGINGS,1));
		chest_material.add(new ItemStack(Material.IRON_BOOTS,1));
		chest_material.add(new ItemStack(Material.LEATHER_HELMET,1));
		chest_material.add(new ItemStack(Material.LEATHER_CHESTPLATE,1));
		chest_material.add(new ItemStack(Material.LEATHER_LEGGINGS,1));
		chest_material.add(new ItemStack(Material.LEATHER_BOOTS,1));
		chest_material.add(new ItemStack(Material.DIAMOND_AXE,1));
		chest_material.add(new ItemStack(Material.DIAMOND_SWORD,1));
		chest_material.add(new ItemStack(Material.BOW,1));
		chest_material.add(new ItemStack(Material.ARROW,16));
		chest_material.add(new ItemStack(Material.ARROW,32));
		chest_material.add(new ItemStack(Material.ARROW,8));
		chest_material.add(new ItemStack(Material.ARROW,12));
		chest_material.add(new ItemStack(Material.GOLDEN_APPLE,2));
	}
	
	Enchantment[] enchs;
	Enchantment e;
	@EventHandler
	public void chest(PlayerInteractEvent ev){
		if(UtilEvent.isAction(ev, ActionType.R_BLOCK)&&getGameList().getPlayers(PlayerState.IN).contains(ev.getPlayer())){
			if(ev.getClickedBlock().getType()==Material.CHEST){
				if(chests.containsKey(ev.getClickedBlock().getLocation())){
					ev.setCancelled(true);
					ev.getPlayer().openInventory(chests.get(ev.getClickedBlock().getLocation()));
				}else{
					if(chest_material.isEmpty())loadMaterialList();
					Inventory inv = Bukkit.createInventory(null, 9, "Chest: ");
					ItemStack item;
					for(int i = 0; i < UtilMath.RandomInt(4, 2); i++){
						item=chest_material.get(UtilMath.r(chest_material.size())).clone();
						
						if(getStart()>=300){
							if(UtilItem.isArmor(item)){
								if(UtilItem.isHelm(item)&&UtilMath.RandomInt(10, 1)>5){
									enchs = UtilItem.enchantmentsHelm();
									e = enchs[UtilMath.r(enchs.length)];
									item.addEnchantment(e, UtilMath.RandomInt(e.getMaxLevel(), e.getStartLevel()));
								}else if(UtilItem.isChestplate(item)&&UtilMath.RandomInt(10, 1)>5){
									enchs = UtilItem.enchantmentsChestplate();
									e = enchs[UtilMath.r(enchs.length)];
									item.addEnchantment(e, UtilMath.RandomInt(e.getMaxLevel(), e.getStartLevel()));
								}else if(UtilItem.isLeggings(item)&&UtilMath.RandomInt(10, 1)>5){
									enchs = UtilItem.enchantmentsLeggings();
									e = enchs[UtilMath.r(enchs.length)];
									item.addEnchantment(e, UtilMath.RandomInt(e.getMaxLevel(), e.getStartLevel()));
								}else if(UtilItem.isBoots(item)&&UtilMath.RandomInt(10, 1)>5){
									enchs = UtilItem.enchantmentsBoots();
									e = enchs[UtilMath.r(enchs.length)];
									item.addEnchantment(e, UtilMath.RandomInt(e.getMaxLevel(), e.getStartLevel()));
								}
							}else if(UtilItem.isWeapon(item)){
								if(UtilItem.isSword(item)&&UtilMath.RandomInt(10, 1)>5){
									enchs = UtilItem.enchantmentsSword();
									e = enchs[UtilMath.r(enchs.length)];
									item.addEnchantment(e, UtilMath.RandomInt(e.getMaxLevel(), e.getStartLevel()));
								}else if(UtilItem.isAxt(item)&&UtilMath.RandomInt(10, 1)>5){
									enchs = UtilItem.enchantmentsAxt();
									e = enchs[UtilMath.r(enchs.length)];
									item.addEnchantment(e, UtilMath.RandomInt(e.getMaxLevel(), e.getStartLevel()));
								}
							}else if(item.getType()==Material.BOW){
								enchs = UtilItem.enchantmentsBow();
								e = enchs[UtilMath.r(enchs.length)];
								item.addEnchantment(e, UtilMath.RandomInt(e.getMaxLevel(), e.getStartLevel()));
							}
						}
						
						inv.addItem( item );
					}
					chests.put(ev.getClickedBlock().getLocation(), inv);
					
					ev.setCancelled(true);
					ev.getPlayer().openInventory(chests.get(ev.getClickedBlock().getLocation()));
				}
			}
		}
	}
	
	@EventHandler
	public void Chat(AsyncPlayerChatEvent ev){
		if(ev.isCancelled())return;
		ev.setCancelled(true);
		
		if((!ev.getPlayer().hasPermission(kPermission.CHAT_LINK.getPermissionToString()))&&UtilString.isBadWord(ev.getMessage())||UtilString.checkForIP(ev.getMessage())){
			ev.setMessage("Ich heul rum!");
			ev.getPlayer().sendMessage(Text.PREFIX.getText()+Text.CHAT_MESSAGE_BLOCK.getText());
		}
		
		if(getState()!=GameState.LobbyPhase&&getGameList().getPlayers(PlayerState.OUT).contains(ev.getPlayer())){
			ev.setCancelled(true);
			UtilPlayer.sendMessage(ev.getPlayer(),Text.PREFIX_GAME.getText(getType().getTyp())+Text.SPECTATOR_CHAT_CANCEL.getText());
		}else{
			UtilServer.broadcast(getManager().getPermManager().getPrefix(ev.getPlayer())+ev.getPlayer().getDisplayName()+":§7 "+ev.getMessage());
		}
	}
	
	@EventHandler
	public void inGame(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		if(getState()!=GameState.InGame)return;
		setStart(getStart()-1);
		for(Player p : UtilServer.getPlayers())UtilDisplay.displayTextBar(Text.GAME_END_IN.getText(UtilTime.formatSeconds(getStart())), p);
		switch(getStart()){
		case 300: chests.clear();broadcast( Text.PREFIX_GAME.getText(getType().getTyp())+"§eChest refill" );break;
		case 30: broadcast( Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getStart())) );break;
		case 15: broadcast( Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getStart())) );break;
		case 10: broadcast( Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getStart())) );break;
		case 5: broadcast( Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getStart())) );break;
		case 4: broadcast( Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getStart())) );break;
		case 3: broadcast( Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getStart())) );break;
		case 2: broadcast( Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getStart())) );break;
		case 1: broadcast( Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getStart())) );break;
		case 0:
			broadcast( Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_END.getText() );
			setState(GameState.Restart);
			break;
		}
	}
	
	@EventHandler
	public void DeathSkyWars(PlayerDeathEvent ev){
		if(ev.getEntity() instanceof Player){
			Player v = (Player)ev.getEntity();
			
			getStats().setInt(v, getStats().getInt(Stats.LOSE, v)+1, Stats.LOSE);
			getGameList().addPlayer(v, PlayerState.OUT);
			
			if(ev.getEntity().getKiller() instanceof Player){
				Player a = (Player)ev.getEntity().getKiller();
				getStats().setInt(a, getStats().getInt(Stats.KILLS, a)+1, Stats.KILLS);
				UtilScoreboard.resetScore(a.getScoreboard(), "§e"+ (getStats().getInt(Stats.KILLS, a)-1) , DisplaySlot.SIDEBAR);
				UtilScoreboard.setScore(a.getScoreboard(), "§e"+getStats().getInt(Stats.KILLS, a), DisplaySlot.SIDEBAR, 7);
				getCoins().addCoins(a, false, 5);
				broadcast( Text.PREFIX_GAME.getText(getType().getTyp())+Text.KILL_BY.getText(new String[]{v.getName(),a.getName()}) );
				return;
			}
			broadcast( Text.PREFIX_GAME.getText(getType().getTyp())+Text.DEATH.getText(v.getName()) );
			broadcast( Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_AUSGESCHIEDEN.getText(v.getName()) );
			getStats().setInt(v, getStats().getInt(Stats.DEATHS, v)+1, Stats.DEATHS);
			
		}
	}
	
	@EventHandler
	public void GameStateChangeSkyWars(GameStateChangeEvent ev){
		if(ev.getFrom()==GameState.InGame&&ev.getTo()==GameState.Restart){
			if(getGameList().getPlayers(PlayerState.IN).size()==1){
				Player win = getGameList().getPlayers(PlayerState.IN).get(0);
				getCoins().addCoins(win, false, 25);
				getStats().setInt(win, getStats().getInt(Stats.WIN, win)+1, Stats.WIN);
				broadcast( Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_WIN.getText(win.getName()));
			}
		}
	}
	
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void JoinHologram(PlayerJoinEvent ev){
		if(getState()!=GameState.LobbyPhase)return;
		int win = getStats().getInt(Stats.WIN, ev.getPlayer());
		int lose = getStats().getInt(Stats.LOSE, ev.getPlayer());
		getManager().getHologram().sendText(ev.getPlayer(),getManager().getLoc_stats().clone().add(0,0.3,0),new String[]{
			Color.GREEN+getType().getTyp()+Color.ORANGE+"§l Info",
		"Server: SkyWars §a"+kArcade.id,
		"Map: "+getWorldData().getMapName(),
		" ",
		Color.GREEN+getType().getTyp()+Color.ORANGE+"§l Stats",
		"Coins: "+getCoins().getCoins(ev.getPlayer()),
		"Rang: "+getStats().getRank(Stats.WIN, ev.getPlayer()),	
		"Kills: "+getStats().getInt(Stats.KILLS, ev.getPlayer()),
		"Tode: "+getStats().getInt(Stats.DEATHS, ev.getPlayer()),
		" ",
		"Gespielte Spiele: "+(win+lose),
		"Gewonnene Spiele: "+win,
		"Verlorene Spiele: "+lose
		});
	}
	
	@EventHandler
	public void GameStartSkyWars(GameStartEvent ev){
		getWorldData().clearWorld();
		TargetNextPlayer = new AddonTargetNextPlayer(250,this);
		TargetNextPlayer.setAktiv(true);
		
		HashMap<Player,String> kits = new HashMap<>();
		
		for(Kit kit : kitshop.getKits()){
			kit.StartGame();
			for(Perk perk : kit.getPlayers().keySet()){
				for(Player p : kit.getPlayers().get(perk))kits.put(p, kit.getName());
				break;
			}
		}
		
		Scoreboard board;
		ArrayList<Player> plist = new ArrayList<>();
		for(Player p : UtilServer.getPlayers()){
			getManager().Clear(p);
			getGameList().addPlayer(p,PlayerState.IN);
			board=Bukkit.getScoreboardManager().getNewScoreboard();
			
			UtilScoreboard.addBoard(board, DisplaySlot.SIDEBAR, "§6§lEpicPvP.eu");
			UtilScoreboard.setScore(board, "§7Kills: ", DisplaySlot.SIDEBAR, 8);
			UtilScoreboard.setScore(board, "§e"+getStats().getInt(Stats.KILLS, p), DisplaySlot.SIDEBAR, 7);
			UtilScoreboard.setScore(board, " ", DisplaySlot.SIDEBAR, 6);
			UtilScoreboard.setScore(board, "§7Kit: ", DisplaySlot.SIDEBAR, 5);
			UtilScoreboard.setScore(board, "§e"+kits.get(p), DisplaySlot.SIDEBAR, 4);
			UtilScoreboard.setScore(board, " ", DisplaySlot.SIDEBAR, 3);
			UtilScoreboard.setScore(board, "§ewww.EpicPvP.me", DisplaySlot.SIDEBAR, 2);
			p.setScoreboard(board);
			
			p.getInventory().addItem(new ItemStack(Material.COMPASS));
			plist.add(p);
		}
		PlayerVerteilung(verteilung(type.getTeam(),type.getTeam_size()), plist);
		
		int i = 0;
		ArrayList<Location> list;
		for(Team t : type.getTeam()){
			list = getWorldData().getLocs(t.Name());
			for(Player p : getPlayerFrom(t)){
				p.teleport(list.get(i));
				i++;
				if(i==list.size())i=0;
			}
		}
		
		new AddonDay(getManager().getInstance(),getWorldData().getWorld());

		TeamTab(type.getTeam());
		
		setStart((60*10)+1);
		setState(GameState.InGame);
	}
	
	@EventHandler
	public void Ranking(RankingEvent ev){
		getManager().setRanking(Stats.WIN);
	}

}
