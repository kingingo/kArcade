package me.kingingo.karcade.Game.Multi.Games.OneInTheChamber;

import java.io.File;
import java.util.HashMap;

import me.kingingo.karcade.Game.Multi.MultiGames;
import me.kingingo.karcade.Game.Multi.Addons.MultiGameArenaRestore;
import me.kingingo.karcade.Game.Multi.Games.MultiSoloGame;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.GameStateChangeReason;
import me.kingingo.kcore.Enum.PlayerState;
import me.kingingo.kcore.Language.Language;
import me.kingingo.kcore.PacketAPI.Packets.kPacketPlayOutWorldBorder;
import me.kingingo.kcore.Permission.kPermission;
import me.kingingo.kcore.StatsManager.Stats;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.UtilBG;
import me.kingingo.kcore.Util.UtilDisplay;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilMath;
import me.kingingo.kcore.Util.UtilPlayer;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.kcore.Util.UtilTime;
import me.kingingo.kcore.Util.UtilWorld;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

public class OneInTheChamber extends MultiSoloGame{

	private HashMap<Player,Integer> Life = new HashMap<>();
	private HashMap<Player,Integer> kills = new HashMap<>();
	private MultiGameArenaRestore area;
	private kPacketPlayOutWorldBorder packet;

	public OneInTheChamber(MultiGames games, String Map,Location pasteLocation,File file) {
		super(games, Map, pasteLocation);
		this.packet=UtilWorld.createWorldBorder(getPasteLocation(), 125*2, 25, 10);
		UtilBG.setHub("versus");
		setUpdateTo("versus");
		Location ecke1 = getPasteLocation().clone();
		ecke1.setY(255);
		ecke1.add(125, 0, 125);
		Location ecke2 = getPasteLocation().clone();
		ecke2.setY(0);
		ecke2.add(-125, 0, -125);
		this.area=new MultiGameArenaRestore(this, ecke1,ecke2);
		getWorldData().loadSchematic(this, pasteLocation, file);
		
		setBlockBreak(true);
		setBlockPlace(true);
		setDropItem(false);
		setPickItem(false);
		setDropItembydeath(false);
		setFoodlevelchange(true);
		setDamagePvP(false);
		setDamage(false);
		getEntityDamage().add(DamageCause.FALL);
	}

	Player v;
	Player a;
	@EventHandler
	public void Death(PlayerDeathEvent ev){
		if(ev.getEntity() instanceof Player && getGameList().getPlayers().containsKey( ((Player)ev.getEntity()) )){
			v = ev.getEntity();
			Life.put(v,(Life.get(v)-1));
			if(Life.get(v)<=0){
				getGameList().addPlayer(v, PlayerState.OUT);
				getGames().getStats().setInt(v, getGames().getStats().getInt(Stats.LOSE, v)+1, Stats.LOSE);
				broadcastWithPrefix("GAME_EXCLUSION", v.getName());
			}
			
			if(ev.getEntity().getKiller() instanceof Player){
				a = (Player)ev.getEntity().getKiller();
				int i =kills.get(a)+1;
				kills.put(a, i);
				a.getInventory().addItem(new ItemStack(Material.ARROW));
				getGames().getStats().setInt(a, getGames().getStats().getInt(Stats.KILLS, a)+1, Stats.KILLS);
				broadcastWithPrefix("KILL_BY", new String[]{v.getName(),a.getName()});
				return;
			}
			broadcastWithPrefix("DEATH", v.getName());
			getGames().getStats().setInt(v, getGames().getStats().getInt(Stats.DEATHS, v)+1, Stats.DEATHS);
			UtilPlayer.RespawnNow(v, getGames().getManager().getInstance());
		}
	}
	Player defend;
	Player attack;
	Projectile ar;
	@EventHandler
	  public void onEntityDamageEventByEntity(EntityDamageByEntityEvent ev)
	  {
	    if (((ev.getEntity() instanceof Player)) && ((ev.getDamager() instanceof Player))) {
	    	defend = (Player)ev.getEntity();
	    	attack = (Player)ev.getDamager();
	    	
	    	if(getGameList().getPlayers().containsKey( defend )&&getGameList().getPlayers().containsKey( attack )){
	    		if(getGameList().isPlayerState(defend)==PlayerState.IN&&getGameList().isPlayerState(attack)==PlayerState.IN){
	    			return;
	    		}
	    	}
	    	ev.setCancelled(true);
	     }else if (ev.getDamager() instanceof Arrow && (ev.getEntity() instanceof Player)){
	      ar = (Projectile)ev.getDamager();
	      if ((ar.getShooter() instanceof Player)) {
	    	  if(getGameList().getPlayers().containsKey(((Player)ar.getShooter()))
	    			  &&getGameList().getPlayers().containsKey(((Player)ev.getEntity()))){
	  	    	
	    		  if(getGameList().isPlayerState(defend)==PlayerState.IN&&getGameList().isPlayerState(attack)==PlayerState.IN){

		    		  ev.setDamage(((CraftPlayer)ev.getEntity()).getMaxHealth());
	    		  }
	    	  }
	    	  ev.setCancelled(true);
	      }
	    }
	  }
	
	public void getSpawnInventory(Player p){
			getGames().getManager().Clear(p);
			if(getGames().getManager().getPermManager().hasPermission(p, kPermission.OneInTheChamber_KIT)){
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
	public void inGame(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		if(getState()!=GameState.InGame)return;
		setTimer(getTimer()-1);
		if(getTimer()<0)setTimer((60*10)+1);
		
		for(Player p : UtilServer.getPlayers())UtilDisplay.displayTextBar(Language.getText(p, "GAME_END_IN", UtilTime.formatSeconds(getTimer())), p);
		
		switch(getTimer()){
		case 30: broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getTimer()));break;
		case 15: broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getTimer()));break;
		case 10: broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getTimer()));break;
		case 5: broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getTimer()));break;
		case 4: broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getTimer()));break;
		case 3: broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getTimer()));break;
		case 2: broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getTimer()));break;
		case 1: broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getTimer()));break;
		case 0:
			broadcastWithPrefix(Language.getText("GAME_END"));
			setState(GameState.Restart,GameStateChangeReason.GAME_END);
			break;
		}
	}

}