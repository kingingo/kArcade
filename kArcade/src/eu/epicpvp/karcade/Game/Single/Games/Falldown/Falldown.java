package eu.epicpvp.karcade.Game.Single.Games.Falldown;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import dev.wolveringer.dataserver.gamestats.GameState;
import dev.wolveringer.dataserver.gamestats.GameType;
import dev.wolveringer.dataserver.gamestats.StatsKey;
import eu.epicpvp.karcade.kArcade;
import eu.epicpvp.karcade.ArcadeManager;
import eu.epicpvp.karcade.Events.RankingEvent;
import eu.epicpvp.karcade.Game.Events.GameStartEvent;
import eu.epicpvp.karcade.Game.Events.GameStateChangeEvent;
import eu.epicpvp.karcade.Game.Single.SingleWorldData;
import eu.epicpvp.karcade.Game.Single.Addons.AddonTargetNextPlayer;
import eu.epicpvp.karcade.Game.Single.Games.SoloGame;
import eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.BrewItem;
import eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.Events.PlayerUseBrewItemEvent;
import eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.Items.Budder;
import eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.Items.Chainer;
import eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.Items.CreeperArmy;
import eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.Items.EnergyRod;
import eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.Items.Firestorm;
import eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.Items.Flashbang;
import eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.Items.Heal;
import eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.Items.HerobrinesMinions;
import eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.Items.Invisibility;
import eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.Items.Lightning;
import eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.Items.Magnet;
import eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.Items.Rocket;
import eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.Items.Sethbling;
import eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.Items.Snowbomb;
import eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.Items.Speed;
import eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.Items.Supershock;
import eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.Items.TntBomb;
import eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.Items.Tornado;
import eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.Items.ToxinBottle;
import eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.Items.WolfArmy;
import eu.epicpvp.karcade.Service.Games.ServiceFalldown;
import eu.epicpvp.kcore.Enum.PlayerState;
import eu.epicpvp.kcore.Enum.Team;
import eu.epicpvp.kcore.LaunchItem.LaunchItemManager;
import eu.epicpvp.kcore.Permission.PermissionType;
import eu.epicpvp.kcore.StatsManager.Event.PlayerStatsLoadedEvent;
import eu.epicpvp.kcore.Translation.TranslationHandler;
import eu.epicpvp.kcore.Update.UpdateType;
import eu.epicpvp.kcore.Update.Event.UpdateEvent;
import eu.epicpvp.kcore.Util.Color;
import eu.epicpvp.kcore.Util.Title;
import eu.epicpvp.kcore.Util.UtilDisplay;
import eu.epicpvp.kcore.Util.UtilEvent;
import eu.epicpvp.kcore.Util.UtilEvent.ActionType;
import eu.epicpvp.kcore.Util.UtilInv;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.kcore.Util.UtilMath;
import eu.epicpvp.kcore.Util.UtilPlayer;
import eu.epicpvp.kcore.Util.UtilServer;
import eu.epicpvp.kcore.Util.UtilString;
import eu.epicpvp.kcore.Util.UtilTime;
import lombok.Getter;
import me.konsolas.aac.api.PlayerViolationCommandEvent;
import me.konsolas.aac.api.PlayerViolationEvent;

public class Falldown extends SoloGame{

	@Getter
	private LaunchItemManager ilManager;
	private HashMap<Player,Integer[]> playerbrauen = new HashMap<Player,Integer[]>();
	private HashMap<Player,Integer> power = new HashMap<>();
	@Getter
	private ArrayList<BrewItem> brewItems = new ArrayList<>();
	private HashMap<Integer, ArrayList<Location>> list = new HashMap<>();
	
	private World cleanWorld;
	private Location spawn;
	private AddonTargetNextPlayer targetNextPlayer;
	
	private ArrayList<CrystalEbene> ebenen;
	private ArrayList<Player> player_up;
	private HashMap<Player,Integer> player_ebene;
	private HashMap<Player,Integer> player_amount;
	
	
	public Falldown(ArcadeManager manager) {
		super(manager);
		long t = System.currentTimeMillis();
		ServiceFalldown.setFd(this);
		setTyp(GameType.Falldown);
		targetNextPlayer=new AddonTargetNextPlayer(140,this);
		ebenen=new ArrayList<>();
		player_up=new ArrayList<>();
		player_ebene=new HashMap<>();
		player_amount=new HashMap<>();
		
		WorldCreator wc = new WorldCreator("void");
		wc.generator(new eu.epicpvp.kcore.ChunkGenerator.CleanroomChunkGenerator(".0,AIR"));
		cleanWorld=Bukkit.createWorld(wc);
		spawn=new Location(cleanWorld,0,250,0);
		spawn.getChunk().load();
		for(int i = 0; i<256; i+=75){
			ebenen.add(new CrystalEbene(new Location(cleanWorld,0,i,0), 40));
		}
		
		setWorldData(new SingleWorldData(manager,getType()));
		setMinPlayers(8);
		setMaxPlayers(16);
		setCreatureSpawn(false);
		setDamage(false);
		setDamagePvP(false);
		setDamageEvP(false);
		setDamagePvE(false);
		setDamageSelf(false);
		setBlockSpread(false);
		setDeathDropItems(true);
		setBlockBreak(false);
		setBlockPlace(false);
		setItemDrop(true);
		setFoodChange(false);
		setCompassAddon(true);
		setItemPickup(true);
		
		int a = 377; // BLAZE POWDER
		int b = 288;// FEATHER
		int c = 331;// Redstone
		int d = 348;// GLOWSTONE DUST
		int e = 378;// MAGMA CREAM
		int f = 360; //MELONE

		brewItems.add(new Tornado(new Integer[]{a,b,c}, this));
		brewItems.add(new CreeperArmy(new Integer[]{a,b,d}, this));
		brewItems.add(new WolfArmy(new Integer[]{a,b,e}, this));
		brewItems.add(new Rocket(new Integer[]{a,b,f}, this));
		brewItems.add(new ToxinBottle(new Integer[]{a,c,d}, this));
		brewItems.add(new TntBomb(new Integer[]{a,c,e}, this));
		brewItems.add(new Sethbling(new Integer[]{a,c,f}, this));
		brewItems.add(new Snowbomb(new Integer[]{a,d,e}, this));
		brewItems.add(new Speed(new Integer[]{a,d,f}, this));
		brewItems.add(new Firestorm(new Integer[]{a,e,f}, this));
		brewItems.add(new Supershock(new Integer[]{b,c,d}, this));
		brewItems.add(new Lightning(new Integer[]{b,c,e}, this));
		brewItems.add(new Invisibility(new Integer[]{b,c,f}, this));
		brewItems.add(new Magnet(new Integer[]{b,d,e}, this));
		brewItems.add(new Heal(new Integer[]{b,d,f}, this));
		brewItems.add(new Flashbang(new Integer[]{b,e,f}, this));
		brewItems.add(new EnergyRod(new Integer[]{c,d,e}, this));
		brewItems.add(new Chainer(new Integer[]{c,d,f}, this));
		brewItems.add(new Budder(new Integer[]{c,e,f}, this));
		brewItems.add(new HerobrinesMinions(new Integer[]{d,e,f}, this));
//		brewItems.add(new Blocked(new Integer[]{b,d,e}, this));
		
		ilManager=new LaunchItemManager(getManager().getInstance());
		getWorldData().Initialize();
		setState(GameState.LobbyPhase);
		manager.DebugLog(t, this.getClass().getName());
	}
	
	@EventHandler
	public void onBreak(EntityDamageEvent e){
		if(getState() == GameState.SchutzModus){
			e.setCancelled(true);
		}else{
			if(e.getEntity() instanceof Player && e.getCause()==DamageCause.FALL){
				if(e.getDamage() >= UtilPlayer.getHealth((Player)e.getEntity())){
					e.setDamage( -((60/100)*UtilPlayer.getCraftPlayer((Player)e.getEntity()).getHealth()) );
				}
			}
		}
	}
	
	@EventHandler
	public void brew(PlayerUseBrewItemEvent ev){
		if(getState() != GameState.InGame){
			ev.setCancelled(true);
		}
	}
	
	public ArrayList<Entity> getNearPlayers(int r, Location loc, boolean onlyPlayer) {
		ArrayList<Entity> ps = new ArrayList<Entity>();

		for (Player p : getGameList().getPlayers(PlayerState.INGAME)) {
			if (p.getWorld().getUID() == loc.getWorld().getUID() && loc.distance(p.getLocation()) <= r) {
				if(!ps.contains(p)){
					ps.add(p);
				}
			}

		}
		
		if(!onlyPlayer){
			for (Entity p : getWorldData().getWorld().getEntities()) {
				if (p.getWorld().getUID() == loc.getWorld().getUID() && loc.distance(p.getLocation()) <= r) {
					if(!ps.contains(p)){
						ps.add(p);
					}
				}
			}
		}

		return ps;
	}
	
	public ArrayList<kDistance> getNearDistance(int r, Location loc, boolean onlyPlayer, Player player) {
		ArrayList<kDistance> ps = new ArrayList<kDistance>();

		for (Player p : getGameList().getPlayers(PlayerState.INGAME)) {
			if(p.getUniqueId() == player.getUniqueId())continue;
			if (p.getWorld().getUID() == loc.getWorld().getUID()) {
				if(loc.distance(p.getLocation()) <= r){
					if(!ps.contains(p)){
						ps.add(new kDistance(p, loc.distance(p.getLocation())));
					}
				}
			}

		}
		
		if(!onlyPlayer){
			for (Entity p : getWorldData().getWorld().getEntities()) {
				if (p.getWorld().getUID() == loc.getWorld().getUID() && loc.distance(p.getLocation()) <= r) {
					if(!ps.contains(p)){
						ps.add(new kDistance(p, loc.distance(p.getLocation())));
					}
				}
			}
		}

		return ps;
	}
	
	@EventHandler
	public void entityDeath(EntityDeathEvent ev){
		if(!(ev.getEntity() instanceof Player)){
			ev.setDroppedExp(0);
			ev.getDrops().clear();
		}
	}
	
	//TEAM RED
	Player player;
	int lvl;
	@EventHandler
	public void PowerCounter(UpdateEvent ev){
		if(ev.getType()==UpdateType.FASTEST){
			for(int i = 0; i<power.size(); i++){
				player=((Player)power.keySet().toArray()[i]);
				lvl=power.get(player);

				if(lvl<0){
					player.setLevel(player.getLevel() - 1);
					lvl+=1;
				}else{
					player.setLevel(player.getLevel() + 1);
					lvl-=1;
				}
				
				power.remove(player);
				if(lvl!=0){
					power.put(player, lvl);
				}
			}
		}
	}
	
	public int getLevel(Player p){
		return getStats().getInt(StatsKey.POWER, p);
	}
	
	public void setlevel(Player player, int lvl){
		getStats().setInt(player, getLevel(player)+lvl, StatsKey.POWER);
		if(power.containsKey(player)){
			int l = power.get(player);
			power.remove(player);
			power.put(player, (lvl+l) );
		}else{
			power.put(player, lvl);
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
						Color.GREEN+getType().getTyp()+Color.ORANGE+"§l Info",
						TranslationHandler.getText(player, "GAME_HOLOGRAM_SERVER",getType().getTyp()+" §a"+kArcade.id),
						TranslationHandler.getText(player, "GAME_HOLOGRAM_MAP", getWorldData().getMapName()),
						" ",
						TranslationHandler.getText(player, "GAME_HOLOGRAM_STATS", getType().getTyp()),
						TranslationHandler.getText(player, "GAME_HOLOGRAM_KILLS", getStats().getInt(StatsKey.KILLS, player)),
						TranslationHandler.getText(player, "GAME_HOLOGRAM_DEATHS", getStats().getInt(StatsKey.DEATHS, player)),
						TranslationHandler.getText(player, "GAME_HOLOGRAM_POWER", getStats().getInt(StatsKey.POWER, player)),
						" ",
						TranslationHandler.getText(player, "GAME_HOLOGRAM_GAMES", (win+lose)),
						TranslationHandler.getText(player, "GAME_HOLOGRAM_WINS", win),
						TranslationHandler.getText(player, "GAME_HOLOGRAM_LOSE", lose),
						});
				}
			});
		}
	}
	
	@EventHandler
	public void aac(PlayerViolationEvent ev){
		if(getState() == GameState.SchutzModus){
			ev.setCancelled(true);
		}
	}
	
	@EventHandler
	public void aacCMD(PlayerViolationCommandEvent ev){
		if(getState() == GameState.SchutzModus){
			ev.setCancelled(true);
		}
	}
	
	@EventHandler
	public void Schutzzeit(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		if(getState()!=GameState.SchutzModus)return;
		setStart( getStart()-1 );
		
		for(Player p : UtilServer.getPlayers())UtilDisplay.displayTextBar(p,TranslationHandler.getText(p, "SCHUTZZEIT_END_IN", getStart()));
		switch(getStart()){
		case 30: broadcastWithPrefix("SCHUTZZEIT_END_IN", getStart());break;
		case 20: broadcastWithPrefix("SCHUTZZEIT_END_IN", getStart());break;
		case 15:
			broadcastWithPrefix("SCHUTZZEIT_END_IN", getStart());
			Bukkit.unloadWorld(cleanWorld, false);
			Bukkit.getPluginManager().enablePlugin(Bukkit.getPluginManager().getPlugin("AAC"));
			break;
		case 10: broadcastWithPrefix("SCHUTZZEIT_END_IN", getStart());break;
		case 5: broadcastWithPrefix("SCHUTZZEIT_END_IN", getStart());break;
		case 4: broadcastWithPrefix("SCHUTZZEIT_END_IN", getStart());break;
		case 3: broadcastWithPrefix("SCHUTZZEIT_END_IN", getStart());break;
		case 2: broadcastWithPrefix("SCHUTZZEIT_END_IN", getStart());break;
		case 1: broadcastWithPrefix("SCHUTZZEIT_END_IN", getStart());break;
		case 0: 
			long t = System.currentTimeMillis();
			setDamage(true);
			setDamagePvP(true);
			setDamagePvP(true);
			setDamageEvP(true);
			setDamagePvE(true);
			setDamageSelf(true);
			setState(GameState.InGame);
			
			if(getMaxPlayers() == 16){
				setStart( 60*10 );
			}else{
				setStart( 60*20 );
			}
			
			broadcastWithPrefixName("SCHUTZZEIT_END");
			getManager().DebugLog(t, this.getClass().getName());
		break;
		}
	}
	
	public void EnchantArmor(ItemStack i, Player p) {
		int lvl = getLevel(p);

		if(lvl < 30){
			p.sendMessage(TranslationHandler.getText(p, "PREFIX")+TranslationHandler.getText(p, "FALLDOWN_NICHT_GENUG_POWER"));
			return;
		}else{
			Bukkit.getWorld(p.getWorld().getName()).playEffect(p.getLocation(),
					Effect.POTION_BREAK, 16421);
			setlevel(p, -30);
		}
		
		int type = UtilMath.RandomInt(2, 0);
		java.util.Random levelRandom = new java.util.Random();
		p.getWorld().playSound(p.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
		
		if (type == 0) {
	          i.addEnchantment(Enchantment.PROTECTION_PROJECTILE, levelRandom.nextInt(2) + 1);
	          i.addEnchantment(Enchantment.PROTECTION_FIRE, levelRandom.nextInt(2) + 1);
	        } else if (type == 1) {
	          i.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, levelRandom.nextInt(2) + 1);
	          i.addEnchantment(Enchantment.PROTECTION_FIRE, levelRandom.nextInt(2) + 1);
	        } else if (type == 2) {
	          i.addEnchantment(Enchantment.PROTECTION_PROJECTILE, levelRandom.nextInt(2) + 1);
	          i.addEnchantment(Enchantment.THORNS, levelRandom.nextInt(2) + 1);
	        }
	}
	
	public void EnchantBow(ItemStack i, Player p) {
		int lvl = getLevel(p);
		if(lvl < 30){
			p.sendMessage(TranslationHandler.getText(p, "PREFIX")+TranslationHandler.getText(p, "FALLDOWN_NICHT_GENUG_POWER"));
			return;
		}else{
			Bukkit.getWorld(p.getWorld().getName()).playEffect(p.getLocation(),
					Effect.POTION_BREAK, 16421);
			setlevel(p, -30);
		}
		
		int type = UtilMath.RandomInt(2, 0);
		java.util.Random levelRandom = new java.util.Random();
		p.getWorld().playSound(p.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
		if (type == 0) {
	        i.addEnchantment(Enchantment.ARROW_DAMAGE, levelRandom.nextInt(2) + 1);
	        i.addEnchantment(Enchantment.ARROW_INFINITE, 1);
	      } else if (type == 1) {
	        i.addEnchantment(Enchantment.ARROW_DAMAGE, levelRandom.nextInt(2) + 1);
	        i.addEnchantment(Enchantment.ARROW_INFINITE, 1);
	      } else if (type == 2) {
	        i.addEnchantment(Enchantment.ARROW_DAMAGE, levelRandom.nextInt(2) + 1);
	        i.addEnchantment(Enchantment.ARROW_KNOCKBACK, levelRandom.nextInt(2) + 1);
	      }
	}
	
	private String is(ItemStack i) {
		String item = "null";
		int id = i.getTypeId();

		if (id == 268 || id == 272 || id == 276 || id == 283 || id == 267) {
			item = "SWORD";
		} else if (id == 261) {
			item = "BOW";
		} else if (id <= 317 && id >= 298) {
			item = "ARMOR";
		}

		return item;
	}
	
	private void EnchantSword(ItemStack i, Player p) {
		int lvl = getLevel(p);

		if(lvl < 30){
			p.sendMessage(TranslationHandler.getText(p, "PREFIX")+TranslationHandler.getText(p, "FALLDOWN_NICHT_GENUG_POWER"));
			return;
		}else{
			Bukkit.getWorld(p.getWorld().getName()).playEffect(p.getLocation(),
					Effect.POTION_BREAK, 16421);
			setlevel(p, -30);
		}
		
		int type = UtilMath.RandomInt(2, 0);
		java.util.Random levelRandom = new java.util.Random();
		p.getWorld().playSound(p.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
		if (type == 0) {
	          i.addEnchantment(Enchantment.DAMAGE_UNDEAD, levelRandom.nextInt(2) + 1);
	          i.addEnchantment(Enchantment.DAMAGE_ARTHROPODS, levelRandom.nextInt(2) + 1);
	        } else if (type == 1) {
	          i.addEnchantment(Enchantment.DAMAGE_ALL, levelRandom.nextInt(2) + 1);
	          i.addEnchantment(Enchantment.KNOCKBACK, levelRandom.nextInt(2) + 1);
	        } else if (type == 2) {
	          i.addEnchantment(Enchantment.DURABILITY, levelRandom.nextInt(2) + 1);
	          i.addEnchantment(Enchantment.FIRE_ASPECT, levelRandom.nextInt(2) + 1);
	        }
		
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerChatd(AsyncPlayerChatEvent event) {
		if (!event.isCancelled()) {
			
			if((!event.getPlayer().hasPermission(PermissionType.CHAT_LINK.getPermissionToString()))&&UtilString.isBadWord(event.getMessage())||UtilString.checkForIP(event.getMessage())){
				event.setMessage("Ich heul rum!");
				event.getPlayer().sendMessage(TranslationHandler.getText(event.getPlayer(), "PREFIX")+TranslationHandler.getText(event.getPlayer(), "CHAT_MESSAGE_BLOCK"));
			}
			
			Player p = event.getPlayer();
			String msg = event.getMessage();
			if(getManager().getPermManager().hasPermission(p, PermissionType.ALL_PERMISSION))msg=msg.replaceAll("&", "§");
			
			if(getGameList().isPlayerState(p) == PlayerState.INGAME){
				event.setFormat(getManager().getPermManager().getPrefix(p) + "{player_"+p.getName()+"}" + "§7:§7 "+ msg);
			}else{
				event.setCancelled(true);
				for(Player player : getGameList().getPlayers(PlayerState.SPECTATOR)){
					player.sendMessage("§c[Spectator-Chat] " + "{player_"+p.getName()+"}" + ":§7 "+ msg);
				}
			}
		}
	}
	
	public static String RandomItem() {
		String i = "";
		int z = UtilMath.RandomInt(11, 1);

		if (z == 1 || z == 8) {
			i = "ARMOR";
		} else if (z ==2) {
			i = "SWORD";
		} else if (z == 3) {
			i = "BOW";
		} else if (z == 4 || z == 5|| z == 9) {
			i = "BRAUITEMS";
		} else if (z == 10 || z == 6 || z == 7 || z == 11) {
			i = "POWER";
		}
		return i;
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void LobbyMenu(PlayerInteractEvent ev){
		if((UtilEvent.isAction(ev, ActionType.PHYSICAL)&& (ev.getClickedBlock().getType() == Material.SOIL))||(UtilEvent.isAction(ev, ActionType.BLOCK)&&!ev.getPlayer().isOp())){
			ev.setCancelled(true);
		}
	}

	private ItemStack RandomArmor() {
		ItemStack i = null;
		int high = 317;
		int low = 298;
		int z = UtilMath.RandomInt(high, low);
		int anzahl = UtilMath.RandomInt(2, 1);

		i = new ItemStack(z, anzahl);

		return i;
	}
	
	private ItemStack RandomBrau() {
		int a = 377; // BLAZE POWDER
		int b = 288;// FEATHER
		int c = 331;// REDSTONE
		int d = 348;// GLOWSTONE DUST
		int e = 378;// MAGMA CREAM
		int f = 360;// MAGMA CREAM
		
		ItemStack i = null;
		int z = UtilMath.RandomInt(12, 1);
		if (z == 1 || z == 7) {
			i = new ItemStack(a, 1);
		} else if (z == 2 || z == 8) {
			i = new ItemStack(b, 1);
		} else if (z == 3 || z == 9) {
			i = new ItemStack(c, 1);
		} else if (z == 4 || z == 10) {
			i = new ItemStack(d, 1);
		} else if (z == 5 || z == 11) {
			i = new ItemStack(e, 1);
		}else if (z == 6 || z == 12) {
			i = new ItemStack(f, 1);
		}

		return i;
	}
	
	private ItemStack RandomSword() {
		ItemStack i = null;
		int high = 5;
		int low = 1;
		int z = UtilMath.RandomInt(high, low);

		if (z == 1) {
			i = new ItemStack(267, 1);
		} else if (z == 2) {
			i = new ItemStack(268, 1);
		} else if (z == 3) {
			i = new ItemStack(272, 1);
		} else if (z == 4) {
			i = new ItemStack(276, 1);
		} else if (z == 5) {
			i = new ItemStack(283, 1);
		}

		return i;
	}
	
	@EventHandler
	public void Brew(PlayerInteractEvent ev){
		if (UtilEvent.isAction(ev, ActionType.RIGHT_BLOCK) && ev.getClickedBlock().getType() == Material.BREWING_STAND) {
			final Player p = ev.getPlayer();
			ev.setCancelled(true);

			if (ev.getPlayer().getItemInHand().getType() == Material.BLAZE_POWDER
					|| ev.getPlayer().getItemInHand().getType() == Material.REDSTONE
					|| ev.getPlayer().getItemInHand().getType() == Material.GLOWSTONE_DUST
					|| ev.getPlayer().getItemInHand().getType() == Material.FEATHER
					|| ev.getPlayer().getItemInHand().getType() == Material.MAGMA_CREAM
					|| ev.getPlayer().getItemInHand().getType() == Material.MELON) {
				if (playerbrauen.containsKey(p)) {
					Integer[] id = playerbrauen.get(p);
					if (id[1] == null) {
						id[1] = ev.getPlayer().getItemInHand().getTypeId();
						final Location loc = ev.getClickedBlock().getLocation();
						
						Bukkit.getScheduler().scheduleAsyncDelayedTask(getManager().getInstance(), new Runnable(){

							@Override
							public void run() {
								p.sendBlockChange(loc, Material.BREWING_STAND, (byte)3);
							}
							
						},2);

						UtilInv.remove(p, id[1], ev.getPlayer().getItemInHand().getData().getData(), 1);
						p.sendMessage(TranslationHandler.getText(p, "PREFIX_GAME", getType().getTyp())+ "§bDas Item wurde den Braustand hinzugef§gt.");
					} else if (id[2] == null) {
						id[2] = p.getItemInHand().getTypeId();
						final Location loc = ev.getClickedBlock().getLocation();
						Bukkit.getScheduler().scheduleAsyncDelayedTask(getManager().getInstance(), new Runnable(){

							@Override
							public void run() {
								p.sendBlockChange(loc, Material.BREWING_STAND, (byte)7);
								
							}
							
						},2);

						UtilInv.remove(p, id[2], ev.getPlayer().getItemInHand().getData().getData(), 1);
						p.sendMessage(TranslationHandler.getText(p, "PREFIX_GAME", getType().getTyp())+ "§bDas Item wurde den Braustand hinzugef§gt Du kannst Jetzt mit den Magic Stick dir was Brauen.");
					} else {
						final Location loc = ev.getClickedBlock().getLocation();
						p.sendMessage(TranslationHandler.getText(p, "PREFIX_GAME", getType().getTyp())
								+ "§bBraustand voll!");
						Bukkit.getScheduler().scheduleAsyncDelayedTask(getManager().getInstance(), new Runnable(){

							@Override
							public void run() {
								p.sendBlockChange(loc, Material.BREWING_STAND, (byte)2);
								
							}
							
						},2);
					}
					playerbrauen.put(p, id);
				} else {
					Integer[] id = new Integer[3];
					id[0] = p.getItemInHand().getTypeId();
					playerbrauen.put(p, id);
					final Location loc = ev.getClickedBlock().getLocation();
					Bukkit.getScheduler().scheduleAsyncDelayedTask(getManager().getInstance(), new Runnable(){

						@Override
						public void run() {
							p.sendBlockChange(loc, Material.BREWING_STAND, (byte)2);
							
						}
						
					},10);
					UtilInv.remove(p, id[0], ev.getPlayer().getItemInHand().getData().getData(), 1);
					p.sendMessage(TranslationHandler.getText(p, "PREFIX_GAME", getType().getTyp())+ "§bDas Item wurde den Braustand hinzugef§gt.");		
				}
				return;
			} else if (p.getItemInHand().getType() == Material.STICK) {
				if (playerbrauen.containsKey(p)) {
					Integer[] id = playerbrauen.get(p);
					if (id[0] == null) {
						p.sendMessage(TranslationHandler.getText(p, "PREFIX_GAME", getType().getTyp())
								+ "§cDer Braustand ist leer!");
					} else if (id[1] == null) {
						p.getInventory().addItem(
								new ItemStack(id[0], 1));
						p.sendMessage(TranslationHandler.getText(p, "PREFIX_GAME", getType().getTyp())+ "§cEs Fehlen 2 Items min 3");
					} else if (id[2] == null) {
						p.getInventory().addItem(
								new ItemStack(id[0], 1));
						p.getInventory().addItem(
								new ItemStack(id[1], 1));
						p.sendMessage(TranslationHandler.getText(p, "PREFIX_GAME", getType().getTyp())+ "§cEs Fehlt 1 Item min 3");
					} else {
						if(getLevel(p) < 30){
							p.getInventory().addItem(
									new ItemStack(id[0], 1));
							p.getInventory().addItem(
									new ItemStack(id[1], 1));
							p.getInventory().addItem(
									new ItemStack(id[2], 1));
							p.sendMessage(TranslationHandler.getText(p, "PREFIX_GAME", getType().getTyp())+"§cDu hast zu wenig Power!");
							p.updateInventory();
							playerbrauen.remove(p);
							return;
						}
						
						boolean have = Brauen(id, p);

						if (!have) {
							p.getInventory().addItem(
									new ItemStack(id[0], 1));
							p.getInventory().addItem(
									new ItemStack(id[1], 1));
							p.getInventory().addItem(
									new ItemStack(id[2], 1));
							p.sendMessage(TranslationHandler.getText(p, "PREFIX_GAME", getType().getTyp())+"§cDie Kombination stimmt nicht!");
						}
					}
					playerbrauen.remove(p);
					p.updateInventory();
				} else {
					p.sendMessage(TranslationHandler.getText(p, "PREFIX_GAME", getType().getTyp())
							+ "§cDer Braustand ist leer!");
				}
			}
		}
	}
	
	String i;
	Player p;
	Vector vel;
	Vector dir;
	Chicken e;
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent ev){
		if(getState() == GameState.SchutzModus){
			p = ev.getPlayer();
			if(p.getLocation().getY()<=-100){
				p.teleport(spawn);
				player_up.remove(p);
				player_ebene.remove(p);
			}else if(p.getLocation().getY()<=0&&!player_up.contains(p)){
				if(player_amount.containsKey(p)&&player_amount.get(p)>=5){
					p.teleport(getWorldData().getSpawnLocations(Team.RED).get(0).clone().add(UtilMath.randomInteger(20),40,UtilMath.randomInteger(20)));
					e = (Chicken)p.getWorld().spawnEntity(p.getLocation(), EntityType.CHICKEN);
					e.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,600*20,1),true);
					e.setPassenger(p.getWorld().spawnEntity(p.getLocation(), EntityType.CHICKEN));
					p.setPassenger(e);
					return;
				}
				
				p.setVelocity(new Vector(0,100,0));
				player_ebene.remove(p);
				player_up.add(p);
			}else if(p.getLocation().getY()>=240&&player_up.contains(p)){
				player_up.remove(p);
				player_ebene.remove(p);
				
				if(player_amount.containsKey(p)){
					int i = player_amount.get(p);
					i++;
					player_amount.put(p, i);
				}else{
					player_amount.put(p, 1);
				}

			}else if(player_up.contains(p)){
				for(int i = 0; i<256; i+=40){
					if( (p.getLocation().getBlockY()-4)<i && (p.getLocation().getBlockY()+4)>i ){
						p.setVelocity(new Vector(0,100,0));
						vel = p.getVelocity();
					    dir = p.getLocation().getDirection();
					    p.setVelocity(new Vector(dir.getX() * 0.4, (vel.getY() * 0.4)+100, dir.getZ() * 0.4));
					    p.setFallDistance(0);
						break;
					}
				}
			}
			
			if(ev.getPlayer().getPassenger() != null){
				if(ev.getPlayer().getPassenger().getType() == EntityType.CHICKEN){
					if(ev.getPlayer().isOnGround()){
						Entity e = ev.getPlayer().getPassenger();
						Entity e1 = e.getPassenger();
						e.eject();
						e1.remove();
						p.eject();
						e.remove();
						return;
					}
					
					p = ev.getPlayer();
					vel = p.getVelocity();
				    dir = p.getLocation().getDirection();
				    p.setVelocity(new Vector(dir.getX() * 0.4, vel.getY() * 0.4, dir.getZ() * 0.4));
				    p.setFallDistance(1);
				}
			}
			
			if(!p.isOnGround()&&getGameList().isPlayerState(p)==PlayerState.INGAME){
				for (Entity e : p.getLocation().getChunk().getEntities()) {
					if (e instanceof EnderCrystal) {
						if ((p.getLocation().distanceSquared(e.getLocation()) <= 4)) {
							if(((!player_ebene.containsKey(p)) || (player_ebene.containsKey(p) && player_ebene.get(p)!=e.getLocation().getBlockY()))){
								i = RandomItem();
								if(i.equalsIgnoreCase("POWER")){
									setlevel(p, UtilMath.RandomInt(30, 10));
								} else if (i.equalsIgnoreCase("ARMOR")) {
									p.getInventory().addItem(RandomArmor());
								} else if (i.equalsIgnoreCase("SWORD")) {
									p.getInventory().addItem(RandomSword());
								} else if (i.equalsIgnoreCase("BOW")) {
									p.getInventory().addItem(new ItemStack(Material.ARROW,UtilMath.RandomInt(7, 3)));
									p.getInventory().addItem(new ItemStack(Material.BOW));
								} else if (i.equalsIgnoreCase("BRAUITEMS")) {
									p.getInventory().addItem(RandomBrau());
								}
								p.updateInventory();
								player_ebene.remove(p);
								player_ebene.put(p, e.getLocation().getBlockY());
								p.playSound(p.getLocation(), Sound.LEVEL_UP, 1f, 1f);
								break;
							}
						}
					}
				}
			}
		}
	}
	
	public boolean Brauen(Integer[] id, Player p) {
		boolean have = false;
		for(BrewItem item : brewItems){
			if(item.check(id, p)){
				setlevel(p, -30);
				have=true;
				break;
			}
		}
		return have;
	}
	
	String t;
	@EventHandler
	public void Enchant(PlayerInteractEvent ev){
		if (UtilEvent.isAction(ev, ActionType.RIGHT_BLOCK )&& ev.getClickedBlock().getType() == Material.ENCHANTMENT_TABLE) {
			t = is(ev.getPlayer().getItemInHand());
			ev.setCancelled(true);
			if (t.equalsIgnoreCase("SWORD")) {
				EnchantSword(ev.getPlayer().getItemInHand(),ev.getPlayer());
			} else if (t.equalsIgnoreCase("BOW")) {
				EnchantBow(ev.getPlayer().getItemInHand(),ev.getPlayer());
			} else if (t.equalsIgnoreCase("ARMOR")) {
				EnchantArmor(ev.getPlayer().getItemInHand(),ev.getPlayer());
			}
		}
	}
	
	@EventHandler
	public void InGame(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		if(getState()!=GameState.InGame)return;
		setStart( getStart()-1 );
		
		for(Player p : UtilServer.getPlayers())UtilDisplay.displayTextBar(p,TranslationHandler.getText(p, "GAME_END_IN", UtilTime.formatSeconds(getStart())));
		switch(getStart()){
		case 30: broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getStart()));break;
		case 20: broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getStart()));break;
		case 15: broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getStart()));break;
		case 10: broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getStart()));break;
		case 5: broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getStart()));break;
		case 4: broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getStart()));break;
		case 3: broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getStart()));break;
		case 2: broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getStart()));break;
		case 1: broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getStart()));break;
		case 0: 
			setDamage(false);
			setDamagePvP(false);
			setState(GameState.Restart);
			broadcastWithPrefixName("GAME_END");
		break;
		}
	}
	
	@EventHandler
	public void GameStateChangeFD(GameStateChangeEvent ev){
		if(ev.getTo()==GameState.Restart){
			ArrayList<Player> list = getGameList().getPlayers(PlayerState.INGAME);
			if(list.size()==1){
				Player p = list.get(0);
				getStats().addInt(p, 1, StatsKey.WIN);
				getMoney().addInt(p, 25, StatsKey.COINS);
				broadcastWithPrefix("GAME_WIN", p.getName());
				new Title("§6§lGEWONNEN").send(p);
				System.err.println("WINNER: "+p.getName());
			}
		}
		
	}
	
	@EventHandler
	public void join(PlayerJoinEvent ev){
		UtilPlayer.sendHovbarText(ev.getPlayer(), "§4§lZweier Teams erlaubt!");
	}
	
	@EventHandler
	public void Ranking(RankingEvent ev){
		getManager().setRanking(StatsKey.WIN);
	}
	
	@EventHandler
	public void DeathFD(PlayerDeathEvent ev){
		if(ev.getEntity() instanceof Player){
			Player v = (Player)ev.getEntity();
			
			getStats().addInt(v, 1, StatsKey.LOSE);
			getGameList().addPlayer(v, PlayerState.SPECTATOR);
			
			if(ev.getEntity().getKiller() instanceof Player){
				Player a = (Player)ev.getEntity().getKiller();
				
				getStats().addInt(a, 1, StatsKey.KILLS);
				getMoney().addInt(a, 5, StatsKey.COINS);
				setlevel(a, 40);
				broadcastWithPrefix("KILL_BY", new String[]{v.getName(),a.getName()});
				return;
			}
			broadcastWithPrefix("DEATH", v.getName());
			getStats().addInt(v, 1, StatsKey.DEATHS);
		}
	}
	
	@EventHandler
	public void kick(PlayerKickEvent ev){
		if(ev.getReason().equalsIgnoreCase("Flying is not enabled on this server")) ev.setCancelled(true);
	}
	
	@EventHandler
	public void Start(GameStartEvent ev){
		setStart((60*2)+25);
		setState(GameState.SchutzModus);
		targetNextPlayer.setAktiv(true);
		Bukkit.getPluginManager().disablePlugin(Bukkit.getPluginManager().getPlugin("AAC"));
		for(Player player : UtilServer.getPlayers()){
			UtilPlayer.sendHovbarText(player, "§4§lZweier Teams erlaubt!");
			player.teleport(spawn);
			getManager().clear(player);
			player.setLevel( getStats().getInt(StatsKey.POWER, player) );
			getGameList().addPlayer(player,PlayerState.INGAME);
			player.getInventory().addItem(UtilItem.addEnchantmentGlow(UtilItem.RenameItem(new ItemStack(Material.STICK), "§cMagic Stick")));
			player.getInventory().addItem(UtilItem.RenameItem(new ItemStack(Material.COMPASS), "§cRadar"));
		}
	}
	
}
