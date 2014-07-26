package me.kingingo.karcade.Game.Games.SheepWars;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.DisplaySlot;

import lombok.Getter;
import me.kingingo.karcade.kArcade;
import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Enum.PlayerState;
import me.kingingo.karcade.Enum.Team;
import me.kingingo.karcade.Game.Events.GameStartEvent;
import me.kingingo.karcade.Game.Events.GameStateChangeEvent;
import me.kingingo.karcade.Game.Games.TeamGame;
import me.kingingo.karcade.Game.Games.SheepWars.Addon.AddonDropItems;
import me.kingingo.karcade.Game.World.WorldData;
import me.kingingo.karcade.Game.addons.AddonEntityKing;
import me.kingingo.karcade.Game.addons.AddonMove;
import me.kingingo.karcade.Game.addons.Events.AddonEntityKingDeathEvent;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.Hologram.Hologram;
import me.kingingo.kcore.PlayerStats.Stats;
import me.kingingo.kcore.ScoreboardManager.PlayerScoreboard;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.C;
import me.kingingo.kcore.Util.UtilDisplay;
import me.kingingo.kcore.Util.UtilMath;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.kcore.Util.UtilTime;

public class SheepWars extends TeamGame{

	WorldData wd;
	HashMap<Player,PlayerScoreboard> boards = new HashMap<>();
	Hologram hm;
	AddonEntityKing aek;
	AddonDropItems adi;
	@Getter
	HashMap<Team,Boolean> teams = new HashMap<>();
	
	public SheepWars(kArcadeManager manager){
		super(manager);	
		long t = System.currentTimeMillis();
		manager.setState(GameState.Laden);
		setItemDrop(true);
		setItemPickup(true);
		setCreatureSpawn(false);
		setBlockBurn(false);
		setBlockSpread(false);
		setCompassAddon(true);
		setDamage(true);
		setDamageEvP(false);
		setDamagePvE(true);
		setDamagePvP(true);
		setDamageTeamOther(true);
		setDamageTeamSelf(false);
		setProjectileDamage(true);
		setRespawn(true);
		setBlackFade(false);
		setFoodChange(true);
		setExplosion(true);
		setBlockBreak(true);
		setBlockPlace(true);
		setMin_Players(8);
		setMax_Players(16);
		wd=new WorldData(manager,GameType.SheepWars.name());
		wd.Initialize();
		manager.setWorldData(wd);
		manager.DebugLog(t, this.getClass().getName());
		manager.setState(GameState.LobbyPhase);
	}
	
	@EventHandler
	public void Explosion(EntityExplodeEvent ev){
		for(int i = 0; i<ev.blockList().size(); i++){
			if(ev.blockList().get(i).getType()==Material.GLOWSTONE){
				ev.blockList().remove(i);
			}
		}
	}
	
	@EventHandler
	public void InGame(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		if(getManager().getState()!=GameState.InGame)return;
		getManager().setStart(getManager().getStart()-1);
		
		for(Player p : UtilServer.getPlayers())UtilDisplay.displayTextBar(p, Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())));
		switch(getManager().getStart()){
			case 15:getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())));break;
			case 10:getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())));break;
			case 5:getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())));break;
			case 4:getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())));break;
			case 3:getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())));break;
			case 2:getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())));break;
			case 1:getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())));break;
			case 0:
				getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.GAME_END.getText());
				getManager().setState(GameState.Restart);
			break;
		}
	}
	
	public HashMap<Team,Integer> verteilung(){
		HashMap<Team,Integer> list = new HashMap<>();
		Player[] l = UtilServer.getPlayers();

        list.put(Team.RED, l.length/2);
        list.put(Team.BLUE, l.length/2);
        list.put(Team.YELLOW, l.length/2);
		
         if (l.length%2!= 0) {
        	 list.put(Team.GREEN, (l.length/2)+1);
         }else{
        	 list.put(Team.GREEN, l.length/2);
         }
		return list;
	}
	
	@EventHandler
	public void Start(GameStartEvent ev){
		long time = System.currentTimeMillis();
		getManager().setState(GameState.InGame);
		ArrayList<Player> plist = new ArrayList<>();
		for(Player p : UtilServer.getPlayers()){
			getManager().Clear(p);
			getGameList().addPlayer(p,PlayerState.IN);
			plist.add(p);
		}
		PlayerVerteilung(verteilung(), plist);
		HashMap<Team,Location> tt = new HashMap<>();
		Team[] teams = new Team[]{Team.RED,Team.BLUE,Team.GREEN,Team.YELLOW};
		ArrayList<Location> list;
		for(Team t : teams){
			getTeams().put(t, true);
			list = getManager().getWorldData().getLocs(t.Name());
			for(Player p : getPlayerFrom(t)){
				p.teleport(list.get(0));
				list.remove(0);
			}
		}
		
		List<Location> ll = getManager().getWorldData().getLocs(Team.SOLO.Name());
		for(Team t : teams){
			tt.put(t,ll.get(0));
			ll.remove(0);
		}
		
		adi= new AddonDropItems(getManager().getInstance(),tt);
		aek=new AddonEntityKing(getManager().getInstance(), teams, EntityType.SHEEP,getManager().getWorldData().getLocs(Team.GRAY.Name()));
		
		hm.RemoveAllText();
		getManager().DebugLog(time, this.getClass().getName());
	}
	
	@EventHandler
	public void Death(PlayerDeathEvent ev){
		if(ev.getEntity() instanceof Player&&ev.getEntity().getKiller() instanceof Player){
			Player killer = ev.getEntity().getKiller();
			Player victim = ev.getEntity();
			Team t = getTeam(victim);
			getManager().getStats().setInt(killer, getManager().getStats().getInt(Stats.KILLS, killer)+1, Stats.KILLS);
			getManager().getStats().setInt(victim, getManager().getStats().getInt(Stats.DEATHS, victim)+1, Stats.DEATHS);
			getManager().getStats().setInt(victim, getManager().getStats().getInt(Stats.LOSE, victim)+1, Stats.LOSE);
			getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.KILL_BY.getText(new String[]{t.getClass()+victim.getName(),getTeam(killer).getColor()+killer.getName()}));
			
			if(getTeams().get(t)==false){
				getGameList().addPlayer(victim, PlayerState.OUT);
			}
			
			
		}else if(ev.getEntity() instanceof Player){
			Player victim = ev.getEntity();
			Team t = getTeam(victim);
			getManager().getStats().setInt(victim, getManager().getStats().getInt(Stats.DEATHS, victim)+1, Stats.DEATHS);
			getManager().getStats().setInt(victim, getManager().getStats().getInt(Stats.LOSE, victim)+1, Stats.LOSE);
			getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.DEATH.getText(new String[]{t.getColor()+victim.getName()}));

			if(getTeams().get(t)==false){
				getGameList().addPlayer(victim, PlayerState.OUT);
			}
		}
	}
	
	@EventHandler
	public void GameStateChange(GameStateChangeEvent ev){
		if(ev.getTo()==GameState.Restart){
			ArrayList<Player> list = getGameList().getPlayers(PlayerState.IN);
			if(list.size()==1){
				Player p = list.get(0);
				getManager().getStats().setInt(p, getManager().getStats().getInt(Stats.WIN, p)+1, Stats.WIN);
				getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.GAME_WIN.getText(p.getName()));
			}else if(list.size()==2){
				Team t = lastTeam();
				for(Player p : getPlayerFrom(t)){
					if(getGameList().isPlayerState(p)==PlayerState.IN){
						getManager().getStats().setInt(p, getManager().getStats().getInt(Stats.WIN, p)+1, Stats.WIN);
					}
				}
				getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.TEAM_WIN.getText(t.getColor()+t.Name()));
			}
		}
	}
	
	@EventHandler
	public void SheepDeath(AddonEntityKingDeathEvent ev){
		Team t = getTeam(ev.getKiller());
		getTeams().remove(ev.getTeam());
		getTeams().put(ev.getTeam(), false);
		adi.getDrops().remove(ev.getTeam());
		adi.getDrops().put(ev.getTeam(), false);
		int g = adi.getChance().get(t)[0];
		int s = adi.getChance().get(t)[1];
		adi.getChance().remove(t);
		adi.getChance().put(t, new Integer[]{g+1,s+1});
		getManager().getStats().setInt(ev.getKiller(), getManager().getStats().getInt(Stats.SHEEPWARS_KILLED_SHEEPS, ev.getKiller())+1, Stats.SHEEPWARS_KILLED_SHEEPS);
		getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().name())+Text.SHEEPWARS_SHEEP_DEATH.getText(new String[]{ev.getTeam().Name(),ev.getKiller().getName()}));
	}
	
	@EventHandler
	public void Chat(AsyncPlayerChatEvent ev){
		ev.setCancelled(true);
		if(!getManager().isState(GameState.LobbyPhase)&&getTeamList().containsKey(ev.getPlayer())){
			if(ev.getMessage().toCharArray()[0]=='#'){
				Bukkit.broadcastMessage("§7[§c"+getTeam(ev.getPlayer()).Name()+"§7] "+ev.getPlayer().getDisplayName()+": "+ev.getMessage());
			}else{
				for(Player p : getPlayerFrom(getTeam(ev.getPlayer()))){
					p.sendMessage("§cTeam-Chat "+ev.getPlayer().getDisplayName()+": "+ev.getMessage());
				}
			}
		}else if(getManager().getState()!=GameState.LobbyPhase&&getGameList().getPlayers(PlayerState.OUT).contains(ev.getPlayer())){
			ev.setCancelled(true);
			ev.getPlayer().sendMessage(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.SPECTATOR_CHAT_CANCEL.getText());
		}else{
			Bukkit.broadcastMessage(getManager().getPermManager().getPrefix(ev.getPlayer())+ev.getPlayer().getDisplayName()+":§7 "+ev.getMessage());
		}
	}
	
	@EventHandler
	public void JoinHologram(PlayerJoinEvent ev){
		if(getManager().getState()!=GameState.LobbyPhase)return;
		if(hm==null)hm=new Hologram(getManager().getInstance());

		int win = getManager().getStats().getInt(Stats.WIN, ev.getPlayer());
		int lose = getManager().getStats().getInt(Stats.LOSE, ev.getPlayer());
		getManager().getLoc_stats().getWorld().loadChunk(getManager().getLoc_stats().getWorld().getChunkAt(getManager().getLoc_stats()));
		hm.sendText(ev.getPlayer(),getManager().getLoc_stats().add(0, 0.2, 0),new String[]{
		C.cGreen+getManager().getTyp().string()+C.mOrange+C.Bold+" Info",
		"Server: SheepWars §a"+kArcade.id,
		"Map: "+wd.getMapName(),
		" ",
		C.cGreen+getManager().getTyp().string()+C.mOrange+C.Bold+" Stats",
		"Rang: "+getManager().getStats().getRank(Stats.WIN, ev.getPlayer()),	
		"Kills: "+getManager().getStats().getInt(Stats.KILLS, ev.getPlayer()),
		"Tode: "+getManager().getStats().getInt(Stats.DEATHS, ev.getPlayer()),
		"Schaf-Kills: "+getManager().getStats().getInt(Stats.SHEEPWARS_KILLED_SHEEPS, ev.getPlayer()),
		" ",
		"Gespielte Spiele: "+(win+lose),
		"Gewonnene Spiele: "+win,
		"Verlorene Spiele: "+lose
		});
	}
	
}
