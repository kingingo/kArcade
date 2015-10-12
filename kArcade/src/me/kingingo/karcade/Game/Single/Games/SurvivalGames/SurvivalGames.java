package me.kingingo.karcade.Game.Single.Games.SurvivalGames;

import java.util.ArrayList;
import java.util.HashMap;

import me.kingingo.karcade.kArcade;
import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Enum.PlayerState;
import me.kingingo.karcade.Events.RankingEvent;
import me.kingingo.karcade.Game.Single.Games.TeamGame;
import me.kingingo.karcade.Game.Single.addons.AddonBagPack;
import me.kingingo.karcade.Game.Single.addons.AddonMove;
import me.kingingo.karcade.Game.Single.addons.AddonSphereGrenze;
import me.kingingo.karcade.Game.Single.addons.AddonVoteTeam;
import me.kingingo.karcade.Game.World.WorldData;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.Enum.Team;
import me.kingingo.kcore.Game.Events.GameStartEvent;
import me.kingingo.kcore.Game.Events.GameStateChangeEvent;
import me.kingingo.kcore.Language.Language;
import me.kingingo.kcore.Permission.kPermission;
import me.kingingo.kcore.StatsManager.Stats;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.Color;
import me.kingingo.kcore.Util.InventorySize;
import me.kingingo.kcore.Util.UtilDisplay;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.UtilMath;
import me.kingingo.kcore.Util.UtilScoreboard;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.kcore.Util.UtilString;
import me.kingingo.kcore.Util.UtilTime;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

public class SurvivalGames extends TeamGame{

	WorldData wd;
	AddonMove move;
	AddonBagPack bp;
	HashMap<Location,Inventory> chest = new HashMap<Location,Inventory>();
	boolean jump=true;
	
	public SurvivalGames(kArcadeManager manager) {
		super(manager);
		registerListener();
	long t = System.currentTimeMillis();
	setTyp(GameType.SurvivalGames); 
	wd=new WorldData(manager,getType());
	wd.Initialize();
	setWorldData(wd);
	setMin_Players(6);
	setMax_Players(24);
	setCompassAddon(true);
	setDamageTeamSelf(false);
	setDamageTeamOther(true);
	setExplosion(false);
	setDeathDropItems(true);
	setFoodChange(true);
	setDamageSelf(false);
	setDamage(true);
	setDamagePvE(true);
	setItemDrop(true);
	setItemPickup(true);
	setBlockBreak(false);
	setBlockPlace(false);
	getBlockBreakAllow().add(Material.LEAVES);
	getBlockPlaceAllow().add(Material.CAKE);
	getBlockPlaceAllow().add(Material.CAKE_BLOCK);
	getBlockPlaceAllow().add(Material.FIRE);
	getBlockPlaceAllow().add(Material.WEB);
	bp=new AddonBagPack(manager);
	//tnp = new AddonTargetNextPlayer(getTeamList(),getManager());
	//tnp.setRadius(40);
	ArrayList<Material> itemlist = new ArrayList<>();
	itemlist.add(Material.STONE_SWORD);
	itemlist.add(Material.LEATHER_CHESTPLATE);
	itemlist.add(Material.LEATHER_BOOTS);
	itemlist.add(Material.LEATHER_HELMET);
	itemlist.add(Material.LEATHER_LEGGINGS);
	itemlist.add(Material.CHAINMAIL_BOOTS);
	itemlist.add(Material.CHAINMAIL_CHESTPLATE);
	itemlist.add(Material.CHAINMAIL_HELMET);
	itemlist.add(Material.CHAINMAIL_LEGGINGS);
	itemlist.add(Material.WOOD_SWORD);
	itemlist.add(Material.WOOD_AXE);
	itemlist.add(Material.IRON_HELMET);
	getItemPickupDeny().add(95);
	//rii=new AddonRandomItemInventory(manager,Material.BLAZE_POWDER,itemlist);
	setVoteTeam(new AddonVoteTeam(this,new Team[]{Team.DISTRICT_1,Team.DISTRICT_2,Team.DISTRICT_3,Team.DISTRICT_4,Team.DISTRICT_5
			,Team.DISTRICT_6,Team.DISTRICT_7,Team.DISTRICT_8,Team.DISTRICT_9,Team.DISTRICT_10,Team.DISTRICT_11,Team.DISTRICT_12},InventorySize._18,2));
	manager.DebugLog(t, this.getClass().getName());
	}
	//RED WOOL = SPAWN PLAYER!
	//BLUE BLOCK = CHEST
	//YELLOW BLOCK= CENTER
	
	Inventory inv;
	int r;
	int rdmplace;
	int rdm;
	ItemStack item;
	int a;
	public void CreateChest(Location loc){
		inv = Bukkit.createInventory(null, 27, "§lChest");
		r = UtilMath.RandomInt(6, 2);
		for (int nur = 0; nur < r; nur++) {
	          rdmplace = (int)(Math.random() * 27.0D);
	          item = rdmItem();
	          a = 1;
	          
	          inv.setItem(rdmplace, item); 
	        }
		chest.put(loc, inv);
	}
	
	@EventHandler
	public void Ranking(RankingEvent ev){
		getManager().setRanking(Stats.WIN);
	}
	
	public ItemStack rdmItem(){
		switch(UtilMath.RandomInt(16, 0)){
		case 0: return LOWER();
		case 1: return LOWER();
		case 2: return LOWER();
		case 3: return LOWER();
		case 4: return LOWER();
		case 5: return LOWER();
		case 6: return LOW();
		case 7: return LOW();
		case 8: return LOW();
		case 9: return LOW();
		case 10: return MEDIUM();
		case 11: return MEDIUM();
		case 12: return MEDIUM();
		case 13: return MEDIUM2();
		case 14: return MEDIUM2();
		case 15: return HARD();
		}
		return new ItemStack(Material.GOLDEN_APPLE,1,(byte)1);
	}
	
	public ItemStack LOW(){
		ItemStack i = null;
		
		switch(UtilMath.RandomInt(15, 0)){
		case 0: i=new ItemStack(Material.FISHING_ROD,1);break;
		case 1: i=new ItemStack(Material.BREAD,UtilMath.RandomInt(2, 1));break;
		case 3: i=new ItemStack(Material.CAKE,UtilMath.RandomInt(2, 1));break;
		case 4: i=new ItemStack(Material.COOKED_BEEF,UtilMath.RandomInt(2, 1));break;
		case 5: i=new ItemStack(Material.COOKED_CHICKEN,UtilMath.RandomInt(2, 1));break;
		case 6: i=new ItemStack(Material.COOKED_FISH,UtilMath.RandomInt(2, 1));break;
		case 7: i=new ItemStack(Material.COOKIE,UtilMath.RandomInt(2, 1));break;
		case 8: i=new ItemStack(Material.APPLE,UtilMath.RandomInt(2, 1));break;
		case 9: i=new ItemStack(360,UtilMath.RandomInt(2, 1));break;
		case 10: i=new ItemStack(391,UtilMath.RandomInt(2, 1));break;
		case 11: i=new ItemStack(349,UtilMath.RandomInt(2, 1),(byte) 2);break;
		case 12: i=new ItemStack(349,UtilMath.RandomInt(2, 1),(byte) 3);break;
		case 13: i=new ItemStack(349,UtilMath.RandomInt(2, 1),(byte) 1);break;
		case 14: i=new ItemStack(Material.GOLD_SWORD);break;
		case 2: i=new ItemStack(Material.BOW);break;
		}
		
		return i;
	}
	
	public ItemStack LOWER(){
		ItemStack i = null;
		switch(UtilMath.RandomInt(7, 0)){
		case 1: i=new ItemStack(Material.BREAD,UtilMath.RandomInt(2, 1));break;
		case 2: i=new ItemStack(Material.BAKED_POTATO,UtilMath.RandomInt(2, 1));break;
		case 3: i=new ItemStack(Material.CAKE,UtilMath.RandomInt(2, 1));break;
		case 4: i=new ItemStack(Material.COOKED_BEEF,UtilMath.RandomInt(2, 1));break;
		case 5: i=new ItemStack(Material.COOKED_CHICKEN,UtilMath.RandomInt(2, 1));break;
		case 6: i=new ItemStack(Material.COOKED_FISH,UtilMath.RandomInt(2, 1));break;
		case 7: i=new ItemStack(Material.COOKIE,UtilMath.RandomInt(2, 1));break;
		case 0: i=new ItemStack(Material.APPLE,UtilMath.RandomInt(2, 1));break;
		}
		return i;
	}
	
	public ItemStack MEDIUM(){
		ItemStack i = null;
		
		switch(UtilMath.RandomInt(13, 0)){
		case 0: i=new ItemStack(Material.FLINT_AND_STEEL,1);break;
		case 1: i=new ItemStack(Material.STICK,UtilMath.RandomInt(2, 1));break;
		case 2: i=new ItemStack(Material.ARROW,UtilMath.RandomInt(6, 1));break;
		case 3: i=new ItemStack(Material.WOOD_SWORD,1);break;
		case 4: i=new ItemStack(Material.LEATHER_HELMET,1);break;
		case 5: i=new ItemStack(Material.LEATHER_CHESTPLATE,1);break;
		case 6: i=new ItemStack(Material.LEATHER_LEGGINGS,1);break;
		case 7: i=new ItemStack(Material.LEATHER_BOOTS,1);break;
		case 8: i=new ItemStack(Material.IRON_HELMET,1);break;
		case 9: i=new ItemStack(Material.IRON_BOOTS,1);break;
		case 10: i=new ItemStack(Material.IRON_INGOT,1);break;
		case 11: i=new ItemStack(Material.GOLD_INGOT,1);break;
		case 12: i=new ItemStack(Material.STONE_AXE,1);break;
		case 13: i=new ItemStack(Material.IRON_PICKAXE,1);break;
		}
		
		return i;
	}
	
	public ItemStack MEDIUM2(){
		ItemStack i = null;
		switch(UtilMath.RandomInt(11, 0)){
		case 0: i=new ItemStack(Material.WORKBENCH);break;
		case 1: i=new ItemStack(Material.CHAINMAIL_HELMET);break;
		case 2: i=new ItemStack(Material.CHAINMAIL_CHESTPLATE);break;
		case 3: i=new ItemStack(Material.CHAINMAIL_LEGGINGS);break;
		case 4: i=new ItemStack(Material.CHAINMAIL_BOOTS);break;
		case 5: i=new ItemStack(Material.STONE_SWORD);break;
		case 6: i=new ItemStack(Material.ENCHANTMENT_TABLE);break;
		case 7: i=new ItemStack(30);break;
		case 8: i=new ItemStack(Material.IRON_AXE);break;
		case 9: i=new ItemStack(Material.IRON_CHESTPLATE);break;
		case 10: i=new ItemStack(Material.IRON_LEGGINGS);break;
		case 11: i=new ItemStack(Material.IRON_SWORD);break;
		}
		
		return i;
	}
	
	public ItemStack HARD(){
		ItemStack i = null;
		
		switch(UtilMath.RandomInt(6,0)){
		case 0: i=new ItemStack(Material.DIAMOND_AXE);break;
		case 1: i=new ItemStack(Material.DIAMOND);break;
		case 2: i=new ItemStack(Material.ENDER_PEARL);break;
		case 3: i=new ItemStack(Material.GOLDEN_APPLE);break;
		case 4: i=new ItemStack(Material.EXP_BOTTLE,UtilMath.RandomInt(5, 2));break;
		case 5: i=bp.getNewBagPack();break;
		//case 7: i=rii.getRandomItemInv();break;
		case 6: i=new ItemStack(Material.COMPASS);break;
		}
		
		return i;
	}
	
	@EventHandler
	public void Interact(PlayerInteractEvent ev){
		if(getState()!=GameState.InGame)return;
		if(getGameList().getPlayers(PlayerState.OUT).contains(ev.getPlayer()))return;
		if(UtilEvent.isAction(ev, ActionType.BLOCK) && ev.getClickedBlock().getTypeId() == 33){
			if(!chest.containsKey(ev.getClickedBlock().getLocation()))CreateChest(ev.getClickedBlock().getLocation());
			ev.getPlayer().openInventory(chest.get(ev.getClickedBlock().getLocation()));
		}
	}
	
	@EventHandler
	public void Start_Game(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		if(getState()!=GameState.StartGame)return;
		if(getStart()<0)setStart(21);
		setStart(getStart()-1);
		for(Player p : UtilServer.getPlayers())UtilDisplay.displayTextBar(p,Language.getText(p, "GAME_START_IN", getStart()));
		switch(getStart()){
			case 20:
				setDamage(false);
//				if(wd.getLocs().containsKey(Team.BLUE.Name())){
//					for(Location loc : wd.getLocs().get(Team.BLUE.Name())){
//						loc.getWorld().loadChunk(loc.getWorld().getChunkAt(loc));
//						loc.getWorld().spawnFallingBlock(loc.add(0,70,0), 33, (byte)6);
//					}
//				}
			break;
			case 15:broadcastWithPrefix("GAME_START_IN", getStart());break;
			case 10:broadcastWithPrefix("GAME_START_IN", getStart());break;
			case 5:broadcastWithPrefix("GAME_START_IN", getStart());break;
			case 4:broadcastWithPrefix("GAME_START_IN", getStart());break;
			case 3:broadcastWithPrefix("GAME_START_IN", getStart());break;
			case 2:broadcastWithPrefix("GAME_START_IN", getStart());break;
			case 1:broadcastWithPrefix("GAME_START_IN", getStart());jump=false;break;
			case 0:
				setStart(1831);
				setState(GameState.InGame);
				broadcastWithPrefix("GAME_START");
				setDamage(true);
				//tnp.setAktiv(true);
				move.setnotMove(false);
			break;
		}
	}
	
//	FallingBlock fb;
//	@EventHandler
//	public void EntityChange (EntityChangeBlockEvent ev) {
//		if (ev.getEntityType() == EntityType.FALLING_BLOCK) {
//			if(jump){
//				fb=ev.getEntity().getLocation().getWorld().spawnFallingBlock(ev.getEntity().getLocation().add(0,0.5,0),33,(byte) 6);
//				fb.setVelocity( new Vector(0,1.8,0) );
//				ev.setCancelled(true);
//			}
//		}
//	 }
	
	@EventHandler
	public void DeathMatch(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		if(getState()!=GameState.InGame)return;
		setStart(getStart()-1);
		
		if(getGameList().getPlayers(PlayerState.IN).size()<=4&&getState()==GameState.InGame){
			if(getStart()>15){
				setStart(15);
			}
		}
		
		for(Player p : UtilServer.getPlayers())UtilDisplay.displayTextBar(p, Language.getText(p, "GAME_END_IN", UtilTime.formatSeconds(getStart())));
		switch(getStart()){
			case 15:broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getStart()));break;
			case 10:broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getStart()));break;
			case 5:broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getStart()));break;
			case 4:broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getStart()));break;
			case 3:broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getStart()));break;
			case 2:broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getStart()));break;
			case 1:broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getStart()));break;
			case 0:
				broadcastWithPrefixName("GAME_END");
				setStart((3*60)+1);
				
				ArrayList<Location> list = getWorldData().getLocs(Team.RED.Name());
				Location r=null;
				for(Player p : UtilServer.getPlayers()){
					r=list.get(0);
					p.teleport(r);
					list.remove(r);
				}
				move.setnotMove(true, getGameList().getPlayers(PlayerState.IN));
				setDamage(false);
				setProjectileDamage(false);
				//tnp.setAktiv(false);
				setState(GameState.DeathMatch);
			break;
		}
	}
	
	@EventHandler
	public void Block(PlayerInteractEvent ev){
		if(UtilEvent.isAction(ev, ActionType.R) && ev.getPlayer().getItemInHand()!=null){
			if(ev.getPlayer().getItemInHand().getType()==Material.WORKBENCH){
				ev.getPlayer().getItemInHand().setTypeId(0);
				ev.getPlayer().openWorkbench(ev.getPlayer().getLocation(), true);
			}else if(ev.getPlayer().getItemInHand().getType()==Material.ENCHANTMENT_TABLE){
				ev.getPlayer().getItemInHand().setTypeId(0);
				ev.getPlayer().openEnchanting(ev.getPlayer().getLocation(), true);
			}
		}
	}
	
	@EventHandler
	public void inGame(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		if(getState()!=GameState.DeathMatch)return;
		setStart(getStart()-1);
		for(Player p : UtilServer.getPlayers())UtilDisplay.displayTextBar(p, Language.getText(p, "DEATHMATCH_END_IN", getStart()));
		switch(getStart()){
			case 180:
				broadcastWithPrefix("DEATHMATCH_START_IN", getStart()-170);
				AddonSphereGrenze sg = new AddonSphereGrenze(getManager(),getWorldData().getWorld());
				sg.loadGrenzen(wd.getLocs().get(Team.YELLOW.Name()).get(0), ( (int)wd.getLocs().get(Team.YELLOW.Name()).get(0).distance(wd.getLocs().get(Team.RED.Name()).get(0))+5 ) );
				sg.start();
				break;
			case 179:broadcastWithPrefix("DEATHMATCH_START_IN", getStart());break;
			case 178:broadcastWithPrefix("DEATHMATCH_START_IN", getStart()-170);break;
			case 177:broadcastWithPrefix("DEATHMATCH_START_IN", getStart()-170);break;
			case 176:broadcastWithPrefix("DEATHMATCH_START_IN", getStart()-170);break;
			case 175:broadcastWithPrefix("DEATHMATCH_START_IN", getStart()-170);break;
			case 174:broadcastWithPrefix("DEATHMATCH_START_IN", getStart()-170);break;
			case 173:broadcastWithPrefix("DEATHMATCH_START_IN", getStart()-170);break;
			case 172:broadcastWithPrefix("DEATHMATCH_START_IN", getStart()-170);break;
			case 171:broadcastWithPrefix("DEATHMATCH_START_IN", getStart()-170);break;
			case 170:
				setDamage(true);
				setProjectileDamage(true);
				move.setnotMove(false);
				broadcastWithPrefix("DEATHMATCH_START", getStart());
				break;
			case 5:broadcastWithPrefix("DEATHMATCH_END_IN", getStart());break;
			case 4:broadcastWithPrefix("DEATHMATCH_END_IN", getStart());break;
			case 3:broadcastWithPrefix("DEATHMATCH_END_IN", getStart());break;
			case 2:broadcastWithPrefix("DEATHMATCH_END_IN", getStart());break;
			case 1:broadcastWithPrefix("DEATHMATCH_END_IN", getStart());break;
			case 0:
				broadcastWithPrefixName("DEATHMATCH_END");
				setState(GameState.Restart);
			break;
		}
	}

	@EventHandler
	public void Death(PlayerDeathEvent ev){
		if(ev.getEntity() instanceof Player && ev.getEntity().getKiller() instanceof Player){
			Player killer = ev.getEntity().getKiller();
			Player victim = ev.getEntity();
			getStats().setInt(killer, getStats().getInt(Stats.KILLS, killer)+1, Stats.KILLS);
			getStats().setInt(victim, getStats().getInt(Stats.DEATHS, victim)+1, Stats.DEATHS);
			getStats().setInt(victim, getStats().getInt(Stats.LOSE, victim)+1, Stats.LOSE);
			broadcastWithPrefix("KILL_BY",new String[]{victim.getName(),killer.getName()});
			getGameList().addPlayer(victim, PlayerState.OUT);
			
			Player t = TeamPartner(victim);
			if(t!=null){
				UtilScoreboard.resetScore(t.getScoreboard(), "§a"+victim.getName(), DisplaySlot.SIDEBAR);
				if(victim.getName().length()>12){
					UtilScoreboard.setScore(t.getScoreboard(), "§c"+victim.getName().substring(0, 11), DisplaySlot.SIDEBAR, 8);
				}else{
					UtilScoreboard.setScore(t.getScoreboard(), "§c"+victim.getName(), DisplaySlot.SIDEBAR, 8);
				}
			}
		}else if(ev.getEntity() instanceof Player){
			Player victim = ev.getEntity();
			getStats().setInt(victim, getStats().getInt(Stats.DEATHS, victim)+1, Stats.DEATHS);
			getStats().setInt(victim, getStats().getInt(Stats.LOSE, victim)+1, Stats.LOSE);
			broadcastWithPrefix("DEATH",new String[]{victim.getName()});
			getGameList().addPlayer(victim, PlayerState.OUT);
			
			Player t = TeamPartner(victim);
			if(t!=null){
				UtilScoreboard.resetScore(t.getScoreboard(), "§a"+victim.getName(), DisplaySlot.SIDEBAR);
				UtilScoreboard.setScore(t.getScoreboard(), "§c"+victim.getName(), DisplaySlot.SIDEBAR, 8);
			}
		}
	}
	
	public Player TeamPartner(Player p){
		for(Player p1 : getPlayerFrom(getTeam(p))){
			if(p1==p)continue;
			return p1;
		}
		return null;
	}
	
	@EventHandler
	public void Chat(AsyncPlayerChatEvent ev){
		if(ev.isCancelled())return;
		ev.setCancelled(true);
		
		if((!ev.getPlayer().hasPermission(kPermission.CHAT_LINK.getPermissionToString()))&&UtilString.isBadWord(ev.getMessage())||UtilString.checkForIP(ev.getMessage())){
			ev.setMessage("Ich heul rum!");
			ev.getPlayer().sendMessage(Language.getText(ev.getPlayer(), "PREFIX")+Language.getText(ev.getPlayer(), "CHAT_MESSAGE_BLOCK"));
		}
		
		if(!isState(GameState.LobbyPhase)&&getTeamList().containsKey(ev.getPlayer())){
			for(Player player : getGameList().getPlayers(PlayerState.IN))player.sendMessage("§7[§c"+getTeam(ev.getPlayer()).Name()+"§7] "+ev.getPlayer().getDisplayName()+": "+ev.getMessage());
		}else if(getState()!=GameState.LobbyPhase&&getGameList().getPlayers(PlayerState.OUT).contains(ev.getPlayer())){
			ev.setCancelled(true);
			for(Player player : getGameList().getPlayers(PlayerState.OUT))player.sendMessage("§7[§6Spectator§7] "+ev.getPlayer().getDisplayName()+": "+ev.getMessage());
			//UtilPlayer.sendMessage(ev.getPlayer(),Text.PREFIX_GAME.getText(getType().getTyp())+Text.SPECTATOR_CHAT_CANCEL.getText());
		}else{
			UtilServer.broadcast(getManager().getPermManager().getPrefix(ev.getPlayer())+ev.getPlayer().getDisplayName()+":§7 "+ev.getMessage());
		}
	}
	
	public HashMap<Team,Integer> verteilung(){
		HashMap<Team,Integer> list = new HashMap<>();
		Team[] t = new Team[]{Team.DISTRICT_1,Team.DISTRICT_2,Team.DISTRICT_3,Team.DISTRICT_4,Team.DISTRICT_5
				,Team.DISTRICT_6,Team.DISTRICT_7,Team.DISTRICT_8,Team.DISTRICT_9,Team.DISTRICT_10,Team.DISTRICT_11,Team.DISTRICT_12};
		
		 int a = UtilServer.getPlayers().size()/2;
		 for(int i=0;i<a;i++){
			 list.put(t[i], 2);
		 }
		
		 if (!((UtilServer.getPlayers().size()%2) == 0)){
          list.put(t[a], 1);
        }
		
		return list;
	}
	
	@EventHandler
	public void Start(GameStartEvent ev){
		long time = System.currentTimeMillis();
		ArrayList<Location> list = getWorldData().getLocs(Team.RED.Name());
		ArrayList<Player> plist = new ArrayList<>();
		int r=0;
		for(Player p : UtilServer.getPlayers()){
			getManager().Clear(p);
			getGameList().addPlayer(p,PlayerState.IN);
			plist.add(p);
		}
		PlayerVerteilung(verteilung(),plist);
		Team t;
		Scoreboard ps;
		for(Player p : getTeamList().keySet()){
			time=System.currentTimeMillis();
			r=UtilMath.r(list.size());
			t = getTeamList().get(p);
			ps=Bukkit.getScoreboardManager().getNewScoreboard();
			UtilScoreboard.addBoard(ps, DisplaySlot.BELOW_NAME, Color.GRAY+t.Name().split(" ")[0]);
			for(Player p1 : UtilServer.getPlayers()){
				if(!getTeamList().containsKey(p1))continue;
				UtilScoreboard.setScore(ps, p1.getName(), DisplaySlot.BELOW_NAME, Integer.valueOf( getTeamList().get(p1).Name().split(" ")[1] ));
			}
			
			
			UtilScoreboard.addBoard(ps,DisplaySlot.SIDEBAR, "§aSurvivalGames §6§lInfo");
			UtilScoreboard.setScore(ps, "§3", DisplaySlot.SIDEBAR, 10);
			UtilScoreboard.setScore(ps, "§aPartner:", DisplaySlot.SIDEBAR, 9);
			
			
			for(Player p1 : getPlayerFrom(getTeam(p))){
				if(p==p1)continue;
				if(p1.getName().length()>13){
					UtilScoreboard.setScore(ps, "§a"+p1.getName().substring(13), DisplaySlot.SIDEBAR, 8);
				}else{
					UtilScoreboard.setScore(ps, "§a"+p1.getName(), DisplaySlot.SIDEBAR, 8);
				}
			}
			

			UtilScoreboard.setScore(ps, "§3", DisplaySlot.SIDEBAR, 7);
			p.setScoreboard(ps);
			p.teleport(list.get(r));
			list.remove(r);
		}
		getManager().DebugLog(time, this.getClass().getName());
		move=new AddonMove(getManager());
		move.setnotMove(true, getGameList().getPlayers(PlayerState.IN));
		
		setState(GameState.StartGame);
		getManager().DebugLog(time, this.getClass().getName());
	}
	
	@EventHandler
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
			" ",
			Language.getText(ev.getPlayer(), "GAME_HOLOGRAM_GAMES", (win+lose)),
			Language.getText(ev.getPlayer(), "GAME_HOLOGRAM_WINS", win),
			Language.getText(ev.getPlayer(), "GAME_HOLOGRAM_LOSE", lose),
			});
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent ev){
		if(ev.getBlock().getType() == Material.ITEM_FRAME && !ev.getPlayer().isOp()){
			ev.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
	public void ChangeState(GameStateChangeEvent ev){
		if(ev.getTo()==GameState.Restart){
			ArrayList<Player> list = getGameList().getPlayers(PlayerState.IN);
			if(list.size()==1){
				Player p = list.get(0);
				getStats().setInt(p, getStats().getInt(Stats.WIN, p)+1, Stats.WIN);
				broadcastWithPrefix("GAME_WIN", p.getName());
			}else if(list.size()==2){
				Team t = lastTeam();
				Player p = list.get(0);
				Player p1 = list.get(1);
				getStats().setInt(p, getStats().getInt(Stats.WIN, p)+1, Stats.WIN);
				getStats().setInt(p1, getStats().getInt(Stats.WIN, p1)+1, Stats.WIN);
				broadcastWithPrefix("SURVIVAL_GAMES_DISTRICT_WIN", new String[]{t.Name(),p.getName(),p1.getName()});
			}
		}
	}
	
}
