package eu.epicpvp.karcade.Game.Single.Games.OneInTheChamber;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
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

import dev.wolveringer.dataserver.gamestats.GameState;
import dev.wolveringer.dataserver.gamestats.GameType;
import eu.epicpvp.karcade.ArcadeManager;
import eu.epicpvp.karcade.Game.Events.GameStartEvent;
import eu.epicpvp.karcade.Game.Single.SingleWorldData;
import eu.epicpvp.karcade.Game.Single.Games.SoloGame;
import eu.epicpvp.kcore.Enum.PlayerState;
import eu.epicpvp.kcore.Enum.Team;
import eu.epicpvp.kcore.Permission.PermissionType;
import eu.epicpvp.kcore.Translation.TranslationHandler;
import eu.epicpvp.kcore.Update.UpdateType;
import eu.epicpvp.kcore.Update.Event.UpdateEvent;
import eu.epicpvp.kcore.Util.UtilDisplay;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.kcore.Util.UtilMath;
import eu.epicpvp.kcore.Util.UtilServer;

public class OneInTheChamber extends SoloGame implements Listener{

	private HashMap<Player,Integer> Life = new HashMap<>();
	private HashMap<Player,Integer> kills = new HashMap<>();
	//Scoreboard board;
	private ArrayList<Location> list;
	
	public OneInTheChamber(ArcadeManager manager) {
		super(manager);
		setTyp(GameType.OneInTheChamber);
		setStart(186);
		setMaxPlayers(16);
		setMinPlayers(2);
		setCompassAddon(true);
		setDamagePvE(true);
		setDamagePvP(true);
		setDamageSelf(false);
		setProjectileDamage(true);
		getEntityDamage().add(DamageCause.FALL);
		setWorldData(new SingleWorldData(manager,getType()));
		getWorldData().Initialize();
	}
	
	@EventHandler
	public void Respawn(PlayerRespawnEvent ev){
		if((!isState(GameState.InGame)) || getGameList().isPlayerState(ev.getPlayer())==PlayerState.SPECTATOR)return;
		ev.setRespawnLocation(list.get(UtilMath.RandomInt(list.size(), 0)));
		getSpawnInventory(ev.getPlayer());
	}
	
	@EventHandler
	public void Start(GameStartEvent ev){
		setStart(187);
		setState(GameState.InGame);
		list = getWorldData().getSpawnLocations(Team.SOLO);
		long time = System.currentTimeMillis();
		int r=0;
		
		for(Player p : UtilServer.getPlayers()){
			getManager().clear(p);
			Life.put(p, 6);
			kills.put(p, 0);
			getGameList().addPlayer(p,PlayerState.INGAME);
			
			getSpawnInventory(p);
			if(list.size()==1){
				r=0;
			}else{
				r=UtilMath.RandomInt(list.size(), 0);
			}
			p.teleport(list.get(r));
			list.remove(r);
		}
		list = getWorldData().getSpawnLocations(Team.SOLO);
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
			Life.put(victim,(Life.get(victim)-1));
			broadcastWithPrefix("KILL_BY", new String[]{victim.getName(),killer.getName()});
			killer.getInventory().addItem(new ItemStack(Material.ARROW));
			if(Life.get(victim)<=0){
				getGameList().addPlayer(victim, PlayerState.SPECTATOR);
				broadcastWithPrefix("GAME_EXCLUSION", victim.getName());
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
	      if(getGameList().getPlayers(PlayerState.SPECTATOR).contains(defend)||getGameList().getPlayers(PlayerState.SPECTATOR).contains(attack))ev.setCancelled(true);
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
			getManager().clear(p);
			if(getManager().getPermManager().hasPermission(p, PermissionType.OneInTheChamber_KIT)){
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
		for(Player p : getGameList().getPlayers(PlayerState.BOTH))UtilDisplay.displayTextBar(p, TranslationHandler.getText(p, "FIGHT_START_IN", getStart()-180) );
		
		switch(this.getStart()){
		case 185:broadcastWithPrefix("FIGHT_START_IN", String.valueOf(getStart() - 180));break;
		case 184:broadcastWithPrefix("FIGHT_START_IN", String.valueOf(getStart() - 180));break;
		case 183:broadcastWithPrefix("FIGHT_START_IN", String.valueOf(getStart() - 180));break;
		case 182:broadcastWithPrefix("FIGHT_START_IN", String.valueOf(getStart() - 180));break;
		case 181:broadcastWithPrefix("FIGHT_START_IN", String.valueOf(getStart() - 180));break;
		case 180:broadcastWithPrefixName("FIGHT_START"); setProjectileDamage(true);break;
		}
	}else{
		for(Player p : getGameList().getPlayers(PlayerState.BOTH))UtilDisplay.displayTextBar(p, TranslationHandler.getText(p, "GAME_END_IN", getStart()) );
		
		switch(getStart()){
		case 5: broadcastWithPrefix("GAME_END_IN", String.valueOf(getStart()));break;
		case 4: broadcastWithPrefix("GAME_END_IN", String.valueOf(getStart()));break;
		case 3: broadcastWithPrefix("GAME_END_IN", String.valueOf(getStart()));break;
		case 2: broadcastWithPrefix("GAME_END_IN", String.valueOf(getStart()));break;
		case 1: broadcastWithPrefix("GAME_END_IN", String.valueOf(getStart()));break;
		case 0: 
			broadcastWithPrefixName("GAME_END");
			onDisable();
		break;
		}
	}
	}
	
}
