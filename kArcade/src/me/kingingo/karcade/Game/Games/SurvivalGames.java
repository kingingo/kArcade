package me.kingingo.karcade.Game.Games;

import java.util.ArrayList;
import java.util.HashMap;

import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Enum.PlayerState;
import me.kingingo.karcade.Enum.Team;
import me.kingingo.karcade.Game.Events.GameStartEvent;
import me.kingingo.karcade.Game.Events.GameStateChangeEvent;
import me.kingingo.karcade.Game.SoloOrTeam.TeamGame;
import me.kingingo.karcade.Game.World.WorldData;
import me.kingingo.karcade.Game.addons.BagPack;
import me.kingingo.karcade.Game.addons.Move;
import me.kingingo.karcade.Game.addons.RandomItemInventory;
import me.kingingo.karcade.Game.addons.SphereGrenze;
import me.kingingo.karcade.Game.addons.VoteTeam;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.UtilDisplay;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.C;
import me.kingingo.kcore.Util.UtilMath;
import me.kingingo.kcore.Util.UtilServer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class SurvivalGames extends TeamGame{

	WorldData wd;
	Move move;
	BagPack bp;
	RandomItemInventory rii;
	int start=16;
	HashMap<Location,Inventory> chest = new HashMap<Location,Inventory>();
	HashMap<Team,Scoreboard> boards = new HashMap<>();
	
	public SurvivalGames(kArcadeManager manager) {
		super(manager);
	long t = System.currentTimeMillis();
	manager.setState(GameState.Laden);
	manager.setTyp(GameType.SurvivalGames); 
	setMin_Players(1);
	setMax_Players(24);
	setCompassAddon(true);
	setDamageTeamSelf(false);
	setDamagePvE(true);
	setItemDrop(true);
	setItemPickup(true);
	getBlockBreakAllow().add(Material.LEAVES);
	wd = new WorldData(manager,GameType.SurvivalGames.name());
	wd.Initialize();
	bp=new BagPack(manager);
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
	rii=new RandomItemInventory(manager,Material.BLAZE_POWDER,itemlist);
	setVoteTeam(new VoteTeam(manager,new Team[]{Team.DISTRICT_1,Team.DISTRICT_2,Team.DISTRICT_3,Team.DISTRICT_4,Team.DISTRICT_5
			,Team.DISTRICT_6,Team.DISTRICT_7,Team.DISTRICT_8,Team.DISTRICT_9,Team.DISTRICT_10,Team.DISTRICT_11,Team.DISTRICT_12},18,2));
	manager.setWorldData(wd);
	manager.setState(GameState.LobbyPhase);
	manager.DebugLog(t, 34, this.getClass().getName());
	}
	
	Inventory inv;
	int r;
	int rdmplace;
	int rdm;
	ItemStack item;
	int a;
	public void CreateChest(Location loc){
		inv = Bukkit.createInventory(null, 27, "§lChest");
		r = UtilMath.RandomInt(4, 2);
		for (int nur = 0; nur < r; nur++) {
	          rdmplace = (int)(Math.random() * 27.0D);
	          item = rdmItem();
	          a = 1;
	          
	          inv.setItem(rdmplace, item); 
	        }
		chest.put(loc, inv);
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
		case 2: i=new ItemStack(Material.BEACON,UtilMath.RandomInt(2, 1));break;
		case 3: i=new ItemStack(Material.CAKE,UtilMath.RandomInt(2, 1));break;
		case 4: i=new ItemStack(Material.COOKED_BEEF,UtilMath.RandomInt(2, 1));break;
		case 5: i=new ItemStack(Material.COOKED_CHICKEN,UtilMath.RandomInt(2, 1));break;
		case 6: i=new ItemStack(Material.COOKED_FISH,UtilMath.RandomInt(2, 1));break;
		case 7: i=new ItemStack(Material.COOKIE,UtilMath.RandomInt(2, 1));break;
		case 8: i=new ItemStack(Material.APPLE,UtilMath.RandomInt(2, 1));break;
		case 9: i=new ItemStack(360,UtilMath.RandomInt(2, 1));break;
		case 10: i=new ItemStack(Material.CARROT,UtilMath.RandomInt(2, 1));break;
		case 11: i=new ItemStack(349,UtilMath.RandomInt(2, 1),(byte) 2);break;
		case 12: i=new ItemStack(349,UtilMath.RandomInt(2, 1),(byte) 3);break;
		case 13: i=new ItemStack(349,UtilMath.RandomInt(2, 1),(byte) 1);break;
		case 14: i=new ItemStack(Material.GOLD_SWORD);break;
		case 15: i=new ItemStack(Material.BOW);break;
		}
		
		return i;
	}
	
	public ItemStack LOWER(){
		ItemStack i = null;
		switch(UtilMath.RandomInt(7, 0)){
		case 1: i=new ItemStack(Material.BREAD,UtilMath.RandomInt(2, 1));break;
		case 2: i=new ItemStack(Material.BEACON,UtilMath.RandomInt(2, 1));break;
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
		case 4: i=rii.getRandomItemInv();break;
		case 5: i=bp.getNewBagPack();break;
		case 6: i=new ItemStack(Material.EXP_BOTTLE,UtilMath.RandomInt(5, 2));break;
		}
		
		return i;
	}
	
//	static int id = 1;
//	public static int rdmItem(int rdm){
//	    switch (rdm) {
//	    case 0:
//		      id = 260;
//		      break;
//	    case 1:
//	      id = 264;
//	      break;
//	    case 2:
//	      id = 265;
//	      break;
//	    case 3:
//	      id = 262;
//	      break;
//	    case 4:
//	      id = 261;
//	      break;
//	    case 5:
//	      id = 272;
//	      break;
//	    case 6:
//	      id = 268;
//	      break;
//	    case 7:
//	      id = 271;
//	      break;
//	    case 8:
//	      id = 280;
//	      break;
//	    case 9:
//	      id = 305;
//	      break;
//	    case 10:
//	      id = 299;
//	      break;
//	    case 11:
//	      id = 306;
//	      break;
//	    case 12:
//	      id = 316;
//	      break;
//	    case 13:
//	      id = 364;
//	      break;
//	    case 14:
//	      id = 260;
//	      break;
//	    case 15:
//	      id = 354;
//	      break;
//	    case 16:
//	      id = 298;
//	      break;
//	    case 17:
//	      id = 300;
//	      break;
//	    case 18:
//	      id = 301;
//	      break;
//	    case 19:
//	      id = 302;
//	      break;
//	    case 20:
//	      id = 303;
//	      break;
//	    case 21:
//	      id = 304;
//	      break;
//	    case 22:
//	      id = 307;
//	      break;
//	    case 23:
//	      id = 308;
//	      break;
//	    case 24:
//	      id = 309;
//	      break;
//	    case 25:
//	      id = 314;
//	      break;
//	    case 26:
//	      id = 315;
//	      break;
//	    case 27:
//	      id = 317;
//	      break;
//	    case 28:
//	      id = 283;
//	      break;
//	    case 29:
//	      id = 275;
//	      break;
//	    case 30:
//	      id = 282;
//	      break;
//	    case 31:
//	      id = 349;
//	      break;
//	    case 32:
//	      id = 365;
//	      break;
//	    case 33:
//	      id = 400;
//	      break;
//	    case 34:
//	      id = 384;
//	      break;
//	    case 35:
//	      id = 393;
//	      break;
//	    case 36:
//	      id = 297;
//	      break;
//	    case 37:
//	      id = 287;
//	      break;
//	    case 38:
//	      id = 281;
//	      break;
//	    case 39:
//	      id = 39;
//	      break;
//	    case 40:
//	      id = 40;
//	      break;
//	    case 41:
//	      id = 395;
//	      break;
//	    case 42:
//	      id = 296;
//	      break;
//	    case 43:
//	      id = 270;
//	      break;
//	    case 44:
//	      id = 274;
//	      break;
//	    case 45:
//	      id = 396;
//	      break;
//	    case 46:
//	      id = 268;
//	    case 47:
//		  id = 346;
//	    case 48:
//		      id = 364;
//	    case 49:
//		      id = 357;
//	    case 50:
//		      id = 366;
//	    case 51:
//		      id = 364;
//	    case 52:
//		      id = 357;
//	    case 53:
//		      id = 366;
//	    case 54:
//	    	id = 16584;
//	    case 55:
//	    	id= 4564646;
//	    }
//	    return id;
//	  }
	
	@EventHandler
	public void Interact(PlayerInteractEvent ev){
		if(getManager().getState()!=GameState.InGame)return;
		if(UtilEvent.isAction(ev, ActionType.BLOCK) && ev.getClickedBlock().getTypeId() == 33 && ev.getClickedBlock().getData() == 6){
			if(!chest.containsKey(ev.getClickedBlock().getLocation()))CreateChest(ev.getClickedBlock().getLocation());
			ev.getPlayer().openInventory(chest.get(ev.getClickedBlock().getLocation()));
		}
	}
	
	@EventHandler
	public void Start_Game(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		if(getManager().getState()!=GameState.StartGame)return;
		start--;
		for(Player p : UtilServer.getPlayers())UtilDisplay.displayTextBar(p, Text.GAME_START_IN.getText(start));
		switch(start){
			case 15:
				getManager().broadcast(Text.PREFIX.getText()+Text.GAME_START_IN.getText(start));
				Scoreboard board;
				for(Player p : getTeamList().keySet()){
					Team t = getTeamList().get(p);
					if(!boards.containsKey(t)){
						board=getGameList().createScoreboard(DisplaySlot.BELOW_NAME, "name");
						board.getObjective(DisplaySlot.BELOW_NAME).setDisplayName(C.cGray+t.Name().split(" ")[0]);
						for(Player p1: getTeamList().keySet()){
							board.getObjective(DisplaySlot.BELOW_NAME).getScore(p).setScore( Integer.valueOf( getTeamList().get(p1).Name().split(" ")[1] ) );
						}
						boards.put(t, board);
					}
					board = boards.get(t);
					p.setScoreboard(board);
				}
				break;
			case 10:getManager().broadcast(Text.PREFIX.getText()+Text.GAME_START_IN.getText(start));break;
			case 5:getManager().broadcast(Text.PREFIX.getText()+Text.GAME_START_IN.getText(start));break;
			case 4:getManager().broadcast(Text.PREFIX.getText()+Text.GAME_START_IN.getText(start));break;
			case 3:getManager().broadcast(Text.PREFIX.getText()+Text.GAME_START_IN.getText(start));break;
			case 2:getManager().broadcast(Text.PREFIX.getText()+Text.GAME_START_IN.getText(start));break;
			case 1:getManager().broadcast(Text.PREFIX.getText()+Text.GAME_START_IN.getText(start));break;
			case 0:
				start=1831;
				for(Location loc : wd.getLocs().get(Team.BLUE.Name())){
					loc.getWorld().spawnFallingBlock(loc, 33, (byte)6);
				}
				getManager().setState(GameState.InGame);
				move.setnotMove(false);
				getManager().broadcast(Text.PREFIX.getText()+Text.GAME_START.getText());
			break;
		}
	}
	
	@EventHandler
	public void DeathMatch(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		if(getManager().getState()!=GameState.InGame)return;
		start--;
		for(Player p : UtilServer.getPlayers())UtilDisplay.displayTextBar(p, Text.GAME_END_IN.getText(start));
		switch(start){
			case 15:getManager().broadcast(Text.PREFIX.getText()+Text.GAME_END_IN.getText(start));break;
			case 10:getManager().broadcast(Text.PREFIX.getText()+Text.GAME_END_IN.getText(start));break;
			case 5:getManager().broadcast(Text.PREFIX.getText()+Text.GAME_END_IN.getText(start));break;
			case 4:getManager().broadcast(Text.PREFIX.getText()+Text.GAME_END_IN.getText(start));break;
			case 3:getManager().broadcast(Text.PREFIX.getText()+Text.GAME_END_IN.getText(start));break;
			case 2:getManager().broadcast(Text.PREFIX.getText()+Text.GAME_END_IN.getText(start));break;
			case 1:getManager().broadcast(Text.PREFIX.getText()+Text.GAME_END_IN.getText(start));break;
			case 0:
				getManager().broadcast(Text.PREFIX.getText()+Text.GAME_END.getText());
				start=(3*60)+1;
				
				HashMap<String,ArrayList<Location>> list = getManager().getWorldData().getLocs();
				int r=0;
				for(Player p : getGameList().getPlayers(PlayerState.IN)){
					r=UtilMath.r( list.get( Team.SOLO.Name() ).size() );
					p.teleport(list.get(Team.SOLO.Name()).get(r));
					list.get(Team.SOLO.Name()).remove(r);
				}
				move.setnotMove(true, getGameList().getPlayers(PlayerState.IN));
				setDamage(false);
				getManager().setState(GameState.DeathMatch);
			break;
		}
	}
	
	@EventHandler
	public void Block(PlayerInteractEvent ev){
		if(UtilEvent.isAction(ev, ActionType.BLOCK)){
			if(ev.getPlayer().getItemInHand()!=null&&ev.getPlayer().getItemInHand().getType()==Material.WORKBENCH){
				ev.getItem().setTypeId(0);
				ev.getPlayer().openWorkbench(ev.getPlayer().getLocation(), true);
			}else if(ev.getPlayer().getItemInHand()!=null&&ev.getPlayer().getItemInHand().getType()==Material.ENCHANTMENT_TABLE){
				ev.getItem().setTypeId(0);
				ev.getPlayer().openEnchanting(ev.getPlayer().getLocation(), true);
			}
		}
	}
	
	@EventHandler
	public void inGame(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		if(getManager().getState()!=GameState.DeathMatch)return;
		start--;
		for(Player p : UtilServer.getPlayers())UtilDisplay.displayTextBar(p, Text.DEATHMATCH_END_IN.getText(start));
		switch(start){
			case 180:
				getManager().broadcast(Text.PREFIX.getText()+Text.DEATHMATCH_START_IN.getText(start-170));
				SphereGrenze sg = new SphereGrenze(getManager(),Bukkit.getWorld("map"));
				sg.loadGrenzen(wd.getLocs().get(Team.BLUE.Name()).get(0), 30);
				sg.start();
				break;
			case 179:getManager().broadcast(Text.PREFIX.getText()+Text.DEATHMATCH_START_IN.getText(start-170));break;
			case 178:getManager().broadcast(Text.PREFIX.getText()+Text.DEATHMATCH_START_IN.getText(start-170));break;
			case 177:getManager().broadcast(Text.PREFIX.getText()+Text.DEATHMATCH_START_IN.getText(start-170));break;
			case 176:getManager().broadcast(Text.PREFIX.getText()+Text.DEATHMATCH_START_IN.getText(start-170));break;
			case 175:getManager().broadcast(Text.PREFIX.getText()+Text.DEATHMATCH_START_IN.getText(start-170));break;
			case 174:getManager().broadcast(Text.PREFIX.getText()+Text.DEATHMATCH_START_IN.getText(start-170));break;
			case 173:getManager().broadcast(Text.PREFIX.getText()+Text.DEATHMATCH_START_IN.getText(start-170));break;
			case 172:getManager().broadcast(Text.PREFIX.getText()+Text.DEATHMATCH_START_IN.getText(start-170));break;
			case 171:getManager().broadcast(Text.PREFIX.getText()+Text.DEATHMATCH_START_IN.getText(start-170));break;
			case 170:
				setDamage(true);
				move.setnotMove(false);
				getManager().broadcast(Text.PREFIX.getText()+Text.DEATHMATCH_START.getText(start));
				break;
			case 5:getManager().broadcast(Text.PREFIX.getText()+Text.DEATHMATCH_END_IN.getText(start));break;
			case 4:getManager().broadcast(Text.PREFIX.getText()+Text.DEATHMATCH_END_IN.getText(start));break;
			case 3:getManager().broadcast(Text.PREFIX.getText()+Text.DEATHMATCH_END_IN.getText(start));break;
			case 2:getManager().broadcast(Text.PREFIX.getText()+Text.DEATHMATCH_END_IN.getText(start));break;
			case 1:getManager().broadcast(Text.PREFIX.getText()+Text.DEATHMATCH_END_IN.getText(start));break;
			case 0:
				getManager().broadcast(Text.PREFIX.getText()+Text.DEATHMATCH_END.getText());
				getManager().setState(GameState.Restart);
			break;
		}
	}

	@EventHandler
	public void Death(PlayerDeathEvent ev){
		if(ev.getEntity() instanceof Player && ev.getEntity().getKiller() instanceof Player){
			Player killer = ev.getEntity().getKiller();
			Player victim = ev.getEntity();
			getManager().broadcast(Text.PREFIX.getText()+Text.KILL_BY.getText(new String[]{victim.getName(),killer.getName()}));
			getGameList().addPlayer(victim, PlayerState.OUT);
		}
	}
	
	@EventHandler
	public void Chat(PlayerChatEvent ev){
		ev.setCancelled(true);
		Bukkit.broadcastMessage("§7[§c"+getTeam(ev.getPlayer()).Name()+"§7]"+ev.getPlayer().getDisplayName()+":");
	}
	
	@EventHandler
	public void Start(GameStartEvent ev){
		long time = System.currentTimeMillis();
		getManager().setState(GameState.StartGame);
		HashMap<String,ArrayList<Location>> list = getManager().getWorldData().getLocs();
		ArrayList<Player> plist = new ArrayList<>();
		
		int r=0;
		for(Player p : UtilServer.getPlayers()){
			getManager().Clear(p);
			getGameList().addPlayer(p,PlayerState.IN);
			plist.add(p);
		}
		PlayerVerteilung(new Team[]{Team.DISTRICT_1,Team.DISTRICT_2,Team.DISTRICT_3,Team.DISTRICT_4,Team.DISTRICT_5
				,Team.DISTRICT_6,Team.DISTRICT_7,Team.DISTRICT_8,Team.DISTRICT_9,Team.DISTRICT_10,Team.DISTRICT_11,Team.DISTRICT_12},plist);
		
		for(Player p : getTeamList().keySet()){
			r=UtilMath.r(list.get(Team.RED.Name()).size());
			p.teleport(list.get(Team.RED.Name()).get(r));
			list.get(Team.RED.Name()).remove(r);
		}
		move=new Move(getManager());
		move.setnotMove(true, getGameList().getPlayers(PlayerState.IN));
		getManager().DebugLog(time, 51, this.getClass().getName());
	}
	
	@EventHandler
	public void ChangeState(GameStateChangeEvent ev){
		if(ev.getState()==GameState.Restart){
			ev.setCancelled(true);
			ArrayList<Player> list = getGameList().getPlayers(PlayerState.IN);
			if(list.size()==1){
				Player p = list.get(0);
				getManager().broadcast(Text.PREFIX.getText()+Text.GAME_WIN.getText(p.getName()));
			}else if(list.size()==2){
				Team t = lastTeam();
				Player p = list.get(0);
				Player p1 = list.get(1);
				getManager().broadcast(Text.PREFIX.getText()+Text.SURVIVAL_GAMES_DISTRICT_WIN.getText(new String[]{t.Name(),p.getName(),p1.getName()}));
			}
		}
	}
	
}
