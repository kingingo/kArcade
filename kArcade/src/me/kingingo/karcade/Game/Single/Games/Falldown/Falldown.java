package me.kingingo.karcade.Game.Single.Games.Falldown;

import java.util.ArrayList;
import java.util.HashMap;

import lombok.Getter;
import me.kingingo.karcade.kArcade;
import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Enum.PlayerState;
import me.kingingo.karcade.Events.WorldLoadEvent;
import me.kingingo.karcade.Game.Single.Games.SoloGame;
import me.kingingo.karcade.Game.Single.Games.Falldown.Brew.BrewItem;
import me.kingingo.karcade.Game.Single.Games.Falldown.Brew.Items.Blocked;
import me.kingingo.karcade.Game.Single.Games.Falldown.Brew.Items.Creeper;
import me.kingingo.karcade.Game.Single.Games.Falldown.Brew.Items.Firestorm;
import me.kingingo.karcade.Game.Single.Games.Falldown.Brew.Items.Magnet;
import me.kingingo.karcade.Game.Single.Games.Falldown.Brew.Items.Poison;
import me.kingingo.karcade.Game.Single.Games.Falldown.Brew.Items.Radar;
import me.kingingo.karcade.Game.Single.Games.Falldown.Brew.Items.Sethbling;
import me.kingingo.karcade.Game.Single.Games.Falldown.Brew.Items.Snowball;
import me.kingingo.karcade.Game.Single.Games.Falldown.Brew.Items.Sugar;
import me.kingingo.karcade.Game.Single.Games.Falldown.Brew.Items.Wolf;
import me.kingingo.karcade.Game.World.WorldData;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.Enum.Team;
import me.kingingo.kcore.Game.Events.GameStartEvent;
import me.kingingo.kcore.Language.Language;
import me.kingingo.kcore.LaunchItem.LaunchItemManager;
import me.kingingo.kcore.StatsManager.Stats;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.Color;
import me.kingingo.kcore.Util.UtilDisplay;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilMap;
import me.kingingo.kcore.Util.UtilMath;
import me.kingingo.kcore.Util.UtilParticle;
import me.kingingo.kcore.Util.UtilServer;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

public class Falldown extends SoloGame{

	@Getter
	private LaunchItemManager ilManager;
	private HashMap<Player,Integer[]> playerbrauen = new HashMap<Player,Integer[]>();
	private HashMap<Player,Integer> power = new HashMap<>();
	private ArrayList<BrewItem> brewItems = new ArrayList<>();
	private HashMap<Integer, ArrayList<Location>> list = new HashMap<>();
	
	public Falldown(kArcadeManager manager) {
		super(manager);
		registerListener();
		long t = System.currentTimeMillis();
		setTyp(GameType.Falldown);
		setState(GameState.Laden);
		setWorldData(new WorldData(manager,getType()));
		setMin_Players(2);
		setCreatureSpawn(false);
		setMax_Players(16);
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
		int f = 353;//Sugar
		
		brewItems.add(new Blocked(new Integer[]{d,e,f}, this));
		brewItems.add(new Creeper(new Integer[]{a,b,c}, this));
		brewItems.add(new Firestorm(new Integer[]{a, b, d}, this));
		brewItems.add(new Magnet(new Integer[]{a, b, e}, this));
		brewItems.add(new Poison(new Integer[]{a, c, d}, this));
		brewItems.add(new Radar(new Integer[]{a, c, e}, this));
		brewItems.add(new Sethbling(new Integer[]{a,d,e}, this));
		brewItems.add(new Snowball(new Integer[]{b,c,d}, this));
		brewItems.add(new Sugar(new Integer[]{b,c,e}, this));
		brewItems.add(new me.kingingo.karcade.Game.Single.Games.Falldown.Brew.Items.TNT(new Integer[]{b,d,e}, this));
		brewItems.add(new Wolf(new Integer[]{c,d,e}, this));
		
		ilManager=new LaunchItemManager(getManager().getInstance());
		getWorldData().Initialize();
		setState(GameState.LobbyPhase);
		manager.DebugLog(t, this.getClass().getName());
	}
	
	//TEAM RED
	
	@EventHandler
	public void World(WorldLoadEvent ev){
		UtilMap.setCrystals(getWorldData().getLocs(Team.RED.Name()).get(0), 40);
	}
	
	Player player;
	int lvl;
	@EventHandler
	public void PowerCounter(UpdateEvent ev){
		if(ev.getType()!=UpdateType.FAST){
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
				if(lvl==0){
					power.put(player, lvl);
				}
			}
		}
	}
	
	public void setlevel(Player player, int lvl){
		getStats().setInt(player, player.getLevel()+lvl, Stats.POWER);
		if(power.containsKey(player)){
			int l = power.get(player);
			power.remove(player);
			power.put(player, (lvl+l) );
		}else{
			power.put(player, lvl);
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void JoinHologram(PlayerJoinEvent ev){
		if(getState()!=GameState.LobbyPhase)return;
		int win = getStats().getInt(Stats.WIN, ev.getPlayer());
		int lose = getStats().getInt(Stats.LOSE, ev.getPlayer());

		getManager().getHologram().sendText(ev.getPlayer(),getManager().getLoc_stats(),new String[]{
			Color.GREEN+getType().getTyp()+Color.ORANGE+"§l Info",
			Language.getText(ev.getPlayer(), "",getType().getTyp()+" §a"+kArcade.id),
			Language.getText(ev.getPlayer(), "GAME_HOLOGRAM_MAP", getWorldData().getMapName()),
			" ",
			Language.getText(ev.getPlayer(), "GAME_HOLOGRAM_STATS", getType().getTyp()),
			Language.getText(ev.getPlayer(), "GAME_HOLOGRAM_KILLS", getStats().getInt(Stats.KILLS, ev.getPlayer())),
			Language.getText(ev.getPlayer(), "GAME_HOLOGRAM_DEATHS", getStats().getInt(Stats.DEATHS, ev.getPlayer())),
			Language.getText(ev.getPlayer(), "GAME_HOLOGRAM_POWER", getStats().getInt(Stats.POWER, ev.getPlayer())),
			" ",
			Language.getText(ev.getPlayer(), "GAME_HOLOGRAM_GAMES", (win+lose)),
			Language.getText(ev.getPlayer(), "GAME_HOLOGRAM_WINS", win),
			Language.getText(ev.getPlayer(), "GAME_HOLOGRAM_LOSE", lose),
			});
	}
	
	@EventHandler
	public void Schutzzeit(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		if(getState()!=GameState.SchutzModus)return;
		setStart( getStart()-1 );
		
		for(Player p : UtilServer.getPlayers())UtilDisplay.displayTextBar(p,Language.getText(p, "SCHUTZZEIT_END_IN", getStart()));
		switch(getStart()){
		case 30: broadcastWithPrefix("SCHUTZZEIT_END_IN", getStart());break;
		case 20: broadcastWithPrefix("SCHUTZZEIT_END_IN", getStart());break;
		case 15: broadcastWithPrefix("SCHUTZZEIT_END_IN", getStart());break;
		case 10: broadcastWithPrefix("SCHUTZZEIT_END_IN", getStart());break;
		case 5: broadcastWithPrefix("SCHUTZZEIT_END_IN", getStart());break;
		case 4: broadcastWithPrefix("SCHUTZZEIT_END_IN", getStart());break;
		case 3: broadcastWithPrefix("SCHUTZZEIT_END_IN", getStart());break;
		case 2: broadcastWithPrefix("SCHUTZZEIT_END_IN", getStart());break;
		case 1: broadcastWithPrefix("SCHUTZZEIT_END_IN", getStart());break;
		case 0: 
			setDamage(true);
			setDamagePvP(true);
			setStart( 60*10 );
			setDamagePvP(true);
			setDamageEvP(true);
			setDamagePvE(true);
			setDamageSelf(true);
			setState(GameState.InGame);
			broadcastWithPrefixName("SCHUTZZEIT_END");
		break;
		}
	}
	
	public static String RandomItem() {
		String i = "";
		int high = 11;
		int low = 1;
		int z = UtilMath.RandomInt(high, low);

		if (z == 1 || z == 8) {
			i = "ARMOR";
		} else if (z ==2) {
			i = "SWORD";
		} else if (z == 3) {
			i = "BOW";
		} else if (z == 4 || z == 5) {
			i = "BRAUITEMS";
		} else if (z == 6 || z == 7 || z == 9) {
			i = "POWER";
		} else if (z == 10 || z == 11){
			i = "null";
		}

		return i;
	}
	
	public void EnchantArmor(ItemStack i, Player p) {
		int lvl = p.getLevel();

		if(lvl < 40){
			broadcastWithPrefixName("FALLDOWN_NICHT_GENUG_POWER");
			return;
		}else{
			Bukkit.getWorld(p.getWorld().getName()).playEffect(p.getLocation(),
					Effect.POTION_BREAK, 16421);
			setlevel(p, -40);
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
		int lvl = p.getLevel();
		if(lvl < 40){
			broadcastWithPrefixName("FALLDOWN_NICHT_GENUG_POWER");
			return;
		}else{
			Bukkit.getWorld(p.getWorld().getName()).playEffect(p.getLocation(),
					Effect.POTION_BREAK, 16421);
			setlevel(p, -40);
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
		int lvl = p.getLevel();

		if(lvl < 40){
			broadcastWithPrefixName("FALLDOWN_NICHT_GENUG_POWER");
			return;
		}else{
			Bukkit.getWorld(p.getWorld().getName()).playEffect(p.getLocation(),
					Effect.POTION_BREAK, 16421);
			setlevel(p, -40);
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
	
	private ItemStack RandomBow() {
		ItemStack i = null;
		int high = 3;
		int low = 1;
		int z = UtilMath.RandomInt(high, low);

		if (z == 1) {
			i = new ItemStack(261, 1);
		} else if (z == 2 || z == 3) {
			i = new ItemStack(262, 5);
		}

		return i;
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
		int e = 378;// MAGMA CREAM  11 / 3
		
		ItemStack i = null;
		int high = 10;
		int low = 1;
		int z = UtilMath.RandomInt(high, low);
		if (z == 1 || z == 6) {
			i = new ItemStack(a, 1);
		} else if (z == 2 || z == 7) {
			i = new ItemStack(b, 1);
		} else if (z == 3 || z == 8) {
			i = new ItemStack(c, 1);
		} else if (z == 4 || z == 9) {
			i = new ItemStack(d, 1);
		} else if (z == 5 || z == 10) {
			i = new ItemStack(e, 1);
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
	
	public static void RemoveItem(Player p) {

		int a = p.getItemInHand().getAmount();
		ItemStack hand = p.getItemInHand();

		if (a == 1) {

			p.getInventory().remove(p.getItemInHand());

		}

		if (a > 1) {

			hand.setAmount(hand.getAmount() - 1);
		}

	}
	
	@EventHandler
	public void Brew(PlayerInteractEvent ev){
		if (UtilEvent.isAction(ev, ActionType.R_BLOCK) && ev.getClickedBlock().getType() == Material.BREWING_STAND) {
			final Player p = ev.getPlayer();
			ev.setCancelled(true);

			if (ev.getPlayer().getItemInHand().getType() == Material.BLAZE_POWDER
					|| ev.getPlayer().getItemInHand().getType() == Material.REDSTONE
					|| ev.getPlayer().getItemInHand().getType() == Material.GLOWSTONE_DUST
					|| ev.getPlayer().getItemInHand().getType() == Material.FEATHER
					|| ev.getPlayer().getItemInHand().getType() == Material.MAGMA_CREAM) {
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

						RemoveItem(p);
						p.sendMessage(Language.getText(p, "PREFIX_GAME", getType().getTyp())+ "§bDas Item wurde den Braustand hinzugefügt.");
					} else if (id[2] == null) {
						id[2] = p.getItemInHand().getTypeId();
						final Location loc = ev.getClickedBlock().getLocation();
						Bukkit.getScheduler().scheduleAsyncDelayedTask(getManager().getInstance(), new Runnable(){

							@Override
							public void run() {
								p.sendBlockChange(loc, Material.BREWING_STAND, (byte)7);
								
							}
							
						},2);

						RemoveItem(p);
						p.sendMessage(Language.getText(p, "PREFIX_GAME", getType().getTyp())+ "§bDas Item wurde den Braustand hinzugefügt Du kannst Jetzt mit den Magic Stick dir was Brauen.");
					} else {
						final Location loc = ev.getClickedBlock().getLocation();
						p.sendMessage(Language.getText(p, "PREFIX_GAME", getType().getTyp())
								+ "§bBraustand voll!");
						Bukkit.getScheduler().scheduleAsyncDelayedTask(getManager().getInstance(), new Runnable(){

							@Override
							public void run() {
								p.sendBlockChange(loc, Material.BREWING_STAND, (byte)2);
								
							}
							
						},2);
					}
					playerbrauen.put(p, id);
					RemoveItem(p);
				} else {
					Integer[] id = new Integer[3];
					id[0] = p.getItemInHand().getTypeId();
					playerbrauen.put(p, id);
					RemoveItem(p);
					final Location loc = ev.getClickedBlock().getLocation();
					Bukkit.getScheduler().scheduleAsyncDelayedTask(getManager().getInstance(), new Runnable(){

						@Override
						public void run() {
							p.sendBlockChange(loc, Material.BREWING_STAND, (byte)2);
							
						}
						
					},10);
					RemoveItem(p);
					p.sendMessage(Language.getText(p, "PREFIX_GAME", getType().getTyp())+ "§bDas Item wurde den Braustand hinzugefügt.");		
				}
				return;
			} else if (p.getItemInHand().getType() == Material.STICK) {
				if (playerbrauen.containsKey(p)) {
					Integer[] id = playerbrauen.get(p);
					if (id[0] == null) {
						p.sendMessage(Language.getText(p, "PREFIX_GAME", getType().getTyp())
								+ "§cDer Braustand ist leer!");
					} else if (id[1] == null) {
						p.getInventory().addItem(
								new ItemStack(id[0], 1));
						p.sendMessage(Language.getText(p, "PREFIX_GAME", getType().getTyp())+ "§cEs Fehlen 2 Items min 3");
					} else if (id[2] == null) {
						p.getInventory().addItem(
								new ItemStack(id[0], 1));
						p.getInventory().addItem(
								new ItemStack(id[1], 1));
						p.sendMessage(Language.getText(p, "PREFIX_GAME", getType().getTyp())+ "§cEs Fehlt 1 Item min 3");
					} else {

						boolean have = Brauen(id, p);

						if (!have) {
							p.getInventory().addItem(
									new ItemStack(id[0], 1));
							p.getInventory().addItem(
									new ItemStack(id[1], 1));
							p.getInventory().addItem(
									new ItemStack(id[2], 1));
							p.sendMessage(Language.getText(p, "PREFIX_GAME", getType().getTyp())+"§cDie Kombination stimmt nicht!");
						}
					}
					p.updateInventory();
					playerbrauen.remove(p);
				} else {
					p.sendMessage(Language.getText(p, "PREFIX_GAME", getType().getTyp())
							+ "§cDer Braustand ist leer!");
				}
			}

			return;
		}
	}
	
	@EventHandler
	public void Update(UpdateEvent ev){
		if(getState() == GameState.SchutzModus&&ev.getType()!=UpdateType.SEC){
			for(Integer i : list.keySet()){
				for(Player p : UtilServer.getPlayers()){
					if(p.getLocation().getBlockY() <= i+50 && p.getLocation().getBlockY() >= i-250){
						for(Location loc : list.get(i))UtilParticle.BARRIER.display(0.0F, 2, loc, 100);
						//for(EntityEnderCrystal e : UtilMap.list_entitys.get(i))UtilPlayer.sendPacket(p, new PacketPlayOutSpawnEntity(e,51));
					}
				}
			}
		}
	}
	
	String i;
	Player p;
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent ev){
		if(getState() == GameState.SchutzModus){
			p = ev.getPlayer();
			if(!p.isOnGround()&&getGameList().isPlayerState(p)==PlayerState.IN){
				
//				for(Integer i : UtilMap.list_entitys.keySet()){
//					if(list.containsKey(p)&&list.get(p).contains(i))continue;
//					if(p.getLocation().getBlockY() <= i+9 && p.getLocation().getBlockY() >= i){
//						System.out.println("P: "+p.getName()+" Y:"+p.getLocation().getBlockY()+" I:"+i);
//						if(!list.containsKey(p))list.put(p, new ArrayList<Integer>());
//						list.get(p).add(i);
//						for(EntityEnderCrystal e : UtilMap.list_entitys.get(i))UtilPlayer.sendPacket(p, new PacketPlayOutSpawnEntity(e, 51));
//					}
//				}
				
				for (Entity e : p.getLocation().getChunk().getEntities()) {
					if (e instanceof EnderCrystal) {
						if (p.getLocation().distanceSquared(e.getLocation()) <= 3) {
							i = RandomItem();
							if(i.equalsIgnoreCase("POWER")){
								setlevel(p, 10);
							} else if (i.equalsIgnoreCase("ARMOR")) {
								p.getInventory().addItem(RandomArmor());
							} else if (i.equalsIgnoreCase("SWORD")) {
								p.getInventory().addItem(RandomSword());
							} else if (i.equalsIgnoreCase("BOW")) {
								p.getInventory().addItem(RandomBow());
							} else if (i.equalsIgnoreCase("BRAUITEMS")) {
								p.getInventory().addItem(RandomBrau());
							}
							p.updateInventory();
							break;
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
				p.getInventory().addItem(item.getItem());
				setlevel(p, -50);
				have=true;
				break;
			}
		}
		return have;
	}
	
	String t;
	@EventHandler
	public void Enchant(PlayerInteractEvent ev){
		if (UtilEvent.isAction(ev, ActionType.R_BLOCK )&& ev.getClickedBlock().getType() == Material.ENCHANTMENT_TABLE) {
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
		
		for(Player p : UtilServer.getPlayers())UtilDisplay.displayTextBar(p,Language.getText(p, "GAME_END_IN", getStart()));
		switch(getStart()){
		case 30: broadcastWithPrefix("GAME_END_IN", getStart());break;
		case 20: broadcastWithPrefix("GAME_END_IN", getStart());break;
		case 15: broadcastWithPrefix("GAME_END_IN", getStart());break;
		case 10: broadcastWithPrefix("GAME_END_IN", getStart());break;
		case 5: broadcastWithPrefix("GAME_END_IN", getStart());break;
		case 4: broadcastWithPrefix("GAME_END_IN", getStart());break;
		case 3: broadcastWithPrefix("GAME_END_IN", getStart());break;
		case 2: broadcastWithPrefix("GAME_END_IN", getStart());break;
		case 1: broadcastWithPrefix("GAME_END_IN", getStart());break;
		case 0: 
			setDamage(false);
			setDamagePvP(false);
			setState(GameState.Restart);
			broadcastWithPrefixName("GAME_END");
		break;
		}
	}
	
	@EventHandler
	public void Start(GameStartEvent ev){
		Location spawn = getWorldData().getLocs(Team.RED.Name()).get(0).clone();
		spawn.setY(6000);
		for(Player player : UtilServer.getPlayers()){
			player.teleport(spawn);
			getManager().Clear(player);
			player.setLevel( getStats().getInt(Stats.POWER, player) );
			getGameList().addPlayer(player,PlayerState.IN);
			player.getInventory().addItem(UtilItem.RenameItem(new ItemStack(Material.STICK), "§cMagic Stick"));
		}
		setStart(60*2);
		setState(GameState.SchutzModus);
	}
	
}
