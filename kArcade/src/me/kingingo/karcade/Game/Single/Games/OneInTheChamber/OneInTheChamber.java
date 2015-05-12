package me.kingingo.karcade.Game.Single.Games.OneInTheChamber;

import java.util.ArrayList;
import java.util.HashMap;

import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Enum.PlayerState;
import me.kingingo.karcade.Enum.Team;
import me.kingingo.karcade.Game.Single.Games.SoloGame;
import me.kingingo.karcade.Game.World.WorldData;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.Game.Events.GameStartEvent;
import me.kingingo.kcore.Permission.kPermission;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.UtilDisplay;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilMath;
import me.kingingo.kcore.Util.UtilServer;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class OneInTheChamber extends SoloGame implements Listener{

	private HashMap<Player,Integer> Life = new HashMap<>();
	private HashMap<Player,Integer> kills = new HashMap<>();
	//Scoreboard board;
	ArrayList<Location> list;
	
	public OneInTheChamber(kArcadeManager manager) {
		super(manager);
		registerListener();
		setTyp(GameType.OneInTheChamber);
		setStart(186);
		setMax_Players(16);
		setMin_Players(2);
		setCompassAddon(true);
		setDamagePvE(true);
		setDamagePvP(true);
		setDamageSelf(false);
		setProjectileDamage(true);
		getEntityDamage().add(DamageCause.FALL);
		WorldData wd=new WorldData(manager,getType());
		wd.Initialize();
		setWorldData(wd);
	}
	
	@EventHandler
	public void Respawn(PlayerRespawnEvent ev){
		if((!isState(GameState.InGame)) || getGameList().isPlayerState(ev.getPlayer())==PlayerState.OUT)return;
		ev.setRespawnLocation(list.get(UtilMath.RandomInt(list.size(), 0)));
		getSpawnInventory(ev.getPlayer());
	}
	
	@EventHandler
	public void Start(GameStartEvent ev){
		setStart(187);
		setState(GameState.InGame);
		list = getWorldData().getLocs(Team.SOLO.Name());
		long time = System.currentTimeMillis();
		int r=0;
		//board = getGameList().createScoreboard(DisplaySlot.SIDEBAR,"GameInfo");
		for(Player p : UtilServer.getPlayers()){
			getManager().Clear(p);
			Life.put(p, 6);
			kills.put(p, 0);
			getGameList().addPlayer(p,PlayerState.IN);
			
			//getGameList().setPlayerScoreboard(p,board);
			
			getSpawnInventory(p);
			if(list.size()==1){
				r=0;
			}else{
				r=UtilMath.RandomInt(list.size(), 0);
			}
			p.teleport(list.get(r));
			list.remove(r);
		}
		list = getWorldData().getLocs(Team.SOLO.Name());
		getManager().DebugLog(time, this.getClass().getName());
	}
	
	public void onDisable(){
		setState(GameState.Restart);
		getWorldData().Uninitialize();
	}
	
	@EventHandler
	public void Death(PlayerDeathEvent ev){
		if(ev.getEntity() instanceof Player && ev.getEntity().getKiller() instanceof Player){
			Player killer = ev.getEntity().getKiller();
			Player victim = ev.getEntity();
			int i =kills.get(killer)+1;
			kills.put(killer, i);
		//	board.getObjective(DisplaySlot.SIDEBAR).getScore(Bukkit.getOfflinePlayer(killer.getName())).setScore(i);
			Life.put(victim,(Life.get(victim)-1));
			broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.KILL_BY.getText(new String[]{victim.getName(),killer.getName()}));
			killer.getInventory().addItem(new ItemStack(Material.ARROW));
			if(Life.get(victim)<=0){
				getGameList().addPlayer(victim, PlayerState.OUT);
				broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_EXCLUSION.getText(victim.getName()));
			}
		}
	}
	
	@EventHandler
	  public void onEntityDamageEventByEntity(EntityDamageByEntityEvent ev)
	  {
	    Entity attacker = ev.getDamager();
	    Entity defender = ev.getEntity();
	    if (((defender instanceof Player)) && ((attacker instanceof Player))) {
	      Player defend = (Player)defender;
	      Player attack = (Player)attacker;
	      if(getGameList().getPlayers(PlayerState.OUT).contains(defend)||getGameList().getPlayers(PlayerState.OUT).contains(attack))ev.setCancelled(true);
	    }else if (ev.getDamager() instanceof Arrow){
	      Projectile ar = (Projectile)ev.getDamager();
	      if ((ar.getShooter() instanceof Player)) {
	    	if (ev.getEntity() instanceof Player){
	    		ev.setDamage(((CraftPlayer)ev.getEntity()).getMaxHealth());
	    	}
	      }
	    }
	  }
	
	public void getSpawnInventory(Player p){
			getManager().Clear(p);
			if(getManager().getPermManager().hasPermission(p, kPermission.OneInTheChamber_KIT)){
				p.getInventory().setChestplate(UtilItem.LSetColor(new ItemStack(Material.LEATHER_CHESTPLATE), Color.YELLOW));
				int r = UtilMath.RandomInt(5, 1);
				switch(r){
				case 1:p.getInventory().setItem(8, new ItemStack(Material.ARROW,1));break;
				case 2:p.getInventory().setItem(8, new ItemStack(Material.ARROW,1));break;
				case 3:p.getInventory().setItem(8, new ItemStack(Material.ARROW,1));break;
				case 4:p.getInventory().setItem(8, new ItemStack(Material.ARROW,2));break;
				case 5:p.getInventory().setItem(8, new ItemStack(Material.ARROW,2));break;
				}
				
			}else{
				p.getInventory().setItem(8, new ItemStack(Material.ARROW,1));
			}
			
			p.getInventory().setItem(0, new ItemStack(Material.WOOD_SWORD,1));
			p.getInventory().setItem(1, new ItemStack(Material.BOW,1));
			p.getInventory().setItem(7, new ItemStack(Material.REDSTONE,Life.get(p)));
	}
	
	@EventHandler
	public void Countdown(UpdateEvent ev){
	if(ev.getType()!=UpdateType.SEC)return;
	if(getState()!=GameState.InGame)return;
	setStart(getStart()-1);
	if(getStart() > 179 && getStart() < 186){
		for(Player p : getGameList().getPlayers(PlayerState.BOTH))UtilDisplay.displayTextBar(p, Text.FIGHT_START_IN.getText(String.valueOf((getStart() - 180))));
		
		switch(this.getStart()){
		case 185:broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.FIGHT_START_IN.getText(String.valueOf((getStart() - 180))));break;
		case 184:broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.FIGHT_START_IN.getText(String.valueOf((getStart() - 180))));break;
		case 183:broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.FIGHT_START_IN.getText(String.valueOf((getStart() - 180))));break;
		case 182:broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.FIGHT_START_IN.getText(String.valueOf((getStart() - 180))));break;
		case 181:broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.FIGHT_START_IN.getText(String.valueOf((getStart() - 180))));break;
		case 180:broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.FIGHT_START.getText()); setProjectileDamage(true);break;
		}
	}else{
		for(Player p : getGameList().getPlayers(PlayerState.BOTH))UtilDisplay.displayTextBar(p, Text.GAME_END_IN.getText(String.valueOf(getStart())));
		
		switch(getStart()){
		case 5: broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_END_IN.getText(String.valueOf(getStart())));break;
		case 4: broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_END_IN.getText(String.valueOf(getStart())));break;
		case 3: broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_END_IN.getText(String.valueOf(getStart())));break;
		case 2: broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_END_IN.getText(String.valueOf(getStart())));break;
		case 1: broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_END_IN.getText(String.valueOf(getStart())));break;
		case 0: 
			broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_END.getText());
			onDisable();
		break;
		}
	}
	}
	
}