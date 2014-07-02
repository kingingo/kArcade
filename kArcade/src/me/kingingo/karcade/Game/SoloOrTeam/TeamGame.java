package me.kingingo.karcade.Game.SoloOrTeam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Enum.PlayerState;
import me.kingingo.karcade.Enum.Team;
import me.kingingo.karcade.Game.Game;
import me.kingingo.karcade.Game.addons.SpecCompass;
import me.kingingo.karcade.Game.addons.VoteTeam;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Util.UtilMath;
import me.kingingo.kcore.Util.UtilServer;

import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class TeamGame extends Game{
	
	@Getter
	HashMap<Player,Team> TeamList = new HashMap<>();
	@Getter
	@Setter
	private VoteTeam VoteTeam;
	
	public TeamGame(kArcadeManager manager) {
		super(manager);
	}
	
	public void addTeam(Player p, Team t){
		TeamList.put(p, t);
	}

	public void delTeam(Player p){
		if(!TeamList.containsKey(p)){
			TeamList.remove(p);
		}
	}
	
	public Team lastTeam(){
		Team t=null;
		for(Player p : TeamList.keySet()){
			t=TeamList.get(p);
			for(Player p1 : TeamList.keySet()){
				if(TeamList.get(p1)!=t){
					t=null;
					break;
				}
			}
		}
		return t;
	}
	
	public boolean islastTeam(){
		if(TeamList.size() < 1){
			return true;
		}
		Team t;
		for(Player p : TeamList.keySet()){
			t=TeamList.get(p);
			for(Player p1 : TeamList.keySet()){
				if(TeamList.get(p1)!=t){
					return false;
				}
			}
		}
		return true;
	}
	
	public Team getTeam(Player p){
		if(TeamList.containsKey(p)){
			return TeamList.get(p);
		}
		return null;
	}
	
	public List<Player> getPlayerFrom(Team t){
		List<Player> list = new ArrayList<>();
		for(Player p : TeamList.keySet()){
			if(TeamList.get(p)==t){
				list.add(p);
			}
		}
		return list;
	}
	
	public int isInTeam(Team t){
		int i=0;
		
		for(Player p : TeamList.keySet()){
			if(TeamList.get(p)==t){
				i++;
			}
		}
		
		return i;
	}
	
	public int r(int i){
		if(i==1)return 0;
		return UtilMath.RandomInt(i, 0);
	}
	
	public void PlayerVerteilung(Team[] t,ArrayList<Player> list){
		int r;
		Player p;
		
		for(int c = 1; c <= 2000; c++){
			if(list.isEmpty())break;
			r=r(list.size());
			p=list.get(r);
			if(TeamList.containsKey(p))continue;
			for(Team team : t){
				if(isInTeam(team)>=team.getPlayer())continue;
				TeamList.put(p, team);
			}
		}
		
//		if(t.length==4){
//			
//			int rCount=0;
//            int gCount=0;
//            int grCount=0;
//            int bCount=0;
//            
//            if (UtilServer.getPlayers().length%2 == 0) {
//                //GERADE
//                rCount= UtilServer.getPlayers().length/4;
//                gCount = UtilServer.getPlayers().length/4;
//                grCount = UtilServer.getPlayers().length/4;
//                bCount= UtilServer.getPlayers().length/4;
//            }else{
//                //UNGERADE
//                rCount = UtilServer.getPlayers().length/4;
//                bCount = UtilServer.getPlayers().length/4;
//                gCount = UtilServer.getPlayers().length/4;
//                grCount = UtilServer.getPlayers().length/4;
//                bCount++;
//            }
//            System.out.println("LIST:"+list.size()+"R:"+rCount+" B:"+bCount+" G:"+gCount+" GR:"+grCount);
//			int r = 0;
//			HashMap<Player,Team> vote = VoteTeam.getVote();
//			TeamList=vote;
//			for(int c = 1; c <= 2000; c++){
//				if(list.isEmpty())break;
//				r=r(list.size());
//				if(TeamList.containsKey(list.get(r))){
//					list.remove(r);
//					continue;
//				}
//				if(rCount==isInTeam(Team.RED)){
//					TeamList.put(list.get(r), Team.RED);
//					list.remove(r);
//				}else if(gCount==isInTeam(Team.YELLOW)){
//					TeamList.put(list.get(r), Team.YELLOW);
//					list.remove(r);
//				}else if(grCount==isInTeam(Team.GREEN)){
//					TeamList.put(list.get(r), Team.GREEN);
//					list.remove(r);
//				}else if(bCount==isInTeam(Team.BLUE)){
//					TeamList.put(list.get(r), Team.BLUE);
//					list.remove(r);
//				}
//			}
//		}else if(t.length==2){
//			
//			int rCount=0;
//            int bCount=0;
//            
//            if (UtilServer.getPlayers().length%2 == 0) {
//                //GERADE
//                rCount= UtilServer.getPlayers().length/2;
//                bCount = UtilServer.getPlayers().length/2;
//            }else{
//                //UNGERADE
//                rCount = UtilServer.getPlayers().length/2;
//                bCount = UtilServer.getPlayers().length/2;
//                bCount++;
//            }
//			int r = 0;
//			HashMap<Player,Team> vote = VoteTeam.getVote();
//			TeamList=vote;
//			for(int c = 1; c <= 2000; c++){
//				if(list.isEmpty())break;
//				r=UtilMath.r(list.size());
//				if(TeamList.containsKey(list.get(r))){
//					list.remove(r);
//					continue;
//				}
//				if(rCount==isInTeam(Team.RED)){
//					TeamList.put(list.get(r), Team.RED);
//					list.remove(r);
//				}else if(bCount==isInTeam(Team.BLUE)){
//					TeamList.put(list.get(r), Team.BLUE);
//					list.remove(r);
//				}
//			}
//		}else if(t.length==12){
//			
//			int dis_1_Count=0;
//            int dis_2_Count=0;
//            int dis_3_Count=0;
//            int dis_4_Count=0;
//            int dis_5_Count=0;
//            int dis_6_Count=0;
//            int dis_7_Count=0;
//            int dis_8_Count=0;
//            int dis_9_Count=0;
//            int dis_10_Count=0;
//            int dis_11_Count=0;
//            int dis_12_Count=0;
//            
//            if (UtilServer.getPlayers().length%2 == 0) {
//                //GERADE
//                dis_1_Count= UtilServer.getPlayers().length/6;
//                dis_2_Count = UtilServer.getPlayers().length/6;
//                dis_3_Count= UtilServer.getPlayers().length/6;
//                dis_4_Count = UtilServer.getPlayers().length/6;
//                dis_5_Count= UtilServer.getPlayers().length/6;
//                dis_6_Count = UtilServer.getPlayers().length/6;
//                dis_7_Count= UtilServer.getPlayers().length/6;
//                dis_8_Count = UtilServer.getPlayers().length/6;
//                dis_9_Count= UtilServer.getPlayers().length/6;
//                dis_10_Count = UtilServer.getPlayers().length/62;
//                dis_11_Count= UtilServer.getPlayers().length/6;
//                dis_12_Count = UtilServer.getPlayers().length/6;
//            }else{
//                //UNGERADE
//            	dis_1_Count= UtilServer.getPlayers().length/6;
//                dis_2_Count = UtilServer.getPlayers().length/6;
//                dis_3_Count= UtilServer.getPlayers().length/6;
//                dis_4_Count = UtilServer.getPlayers().length/6;
//                dis_5_Count= UtilServer.getPlayers().length/6;
//                dis_6_Count = UtilServer.getPlayers().length/6;
//                dis_7_Count= UtilServer.getPlayers().length/6;
//                dis_8_Count = UtilServer.getPlayers().length/6;
//                dis_9_Count= UtilServer.getPlayers().length/6;
//                dis_10_Count = UtilServer.getPlayers().length/6;
//                dis_11_Count= UtilServer.getPlayers().length/6;
//                dis_12_Count = UtilServer.getPlayers().length/6;
//                dis_12_Count--;
//            }
//			int r = 0;
//			HashMap<Player,Team> vote = VoteTeam.getVote();
//			TeamList=vote;
//			for(int c = 1; c <= 2000; c++){
//				if(list.isEmpty())break;
//				r=UtilMath.r(list.size());
//				if(TeamList.containsKey(list.get(r))){
//					list.remove(r);
//					continue;
//				}
//				if(dis_1_Count==isInTeam(Team.DISTRICT_1)){
//					TeamList.put(list.get(r), Team.DISTRICT_1);
//					list.remove(r);
//				}else if(dis_2_Count==isInTeam(Team.DISTRICT_2)){
//					TeamList.put(list.get(r), Team.DISTRICT_2);
//					list.remove(r);
//				}else if(dis_3_Count==isInTeam(Team.DISTRICT_3)){
//					TeamList.put(list.get(r), Team.DISTRICT_3);
//					list.remove(r);
//				}else if(dis_4_Count==isInTeam(Team.DISTRICT_3)){
//					TeamList.put(list.get(r), Team.DISTRICT_3);
//					list.remove(r);
//				}else if(dis_5_Count==isInTeam(Team.DISTRICT_4)){
//					TeamList.put(list.get(r), Team.DISTRICT_4);
//					list.remove(r);
//				}else if(dis_6_Count==isInTeam(Team.DISTRICT_5)){
//					TeamList.put(list.get(r), Team.DISTRICT_5);
//					list.remove(r);
//				}else if(dis_7_Count==isInTeam(Team.DISTRICT_6)){
//					TeamList.put(list.get(r), Team.DISTRICT_6);
//					list.remove(r);
//				}else if(dis_8_Count==isInTeam(Team.DISTRICT_7)){
//					TeamList.put(list.get(r), Team.DISTRICT_7);
//					list.remove(r);
//				}else if(dis_9_Count==isInTeam(Team.DISTRICT_9)){
//					TeamList.put(list.get(r), Team.DISTRICT_9);
//					list.remove(r);
//				}else if(dis_10_Count==isInTeam(Team.DISTRICT_10)){
//					TeamList.put(list.get(r), Team.DISTRICT_10);
//					list.remove(r);
//				}else if(dis_11_Count==isInTeam(Team.DISTRICT_11)){
//					TeamList.put(list.get(r), Team.DISTRICT_11);
//					list.remove(r);
//				}else if(dis_12_Count==isInTeam(Team.DISTRICT_12)){
//					TeamList.put(list.get(r), Team.DISTRICT_12);
//					list.remove(r);
//				}
//			}
//		}
	}
	
	@EventHandler
	public void Quit(PlayerQuitEvent ev){
		if(TeamList.containsKey(ev.getPlayer())){
			TeamList.remove(ev.getPlayer());
		}
		
		if(getManager().isState(GameState.Restart)||getManager().isState(GameState.LobbyPhase))return;
		
		getGameList().addPlayer(ev.getPlayer(), PlayerState.OUT);
		if(islastTeam()||getGameList().getPlayers(PlayerState.IN).size()<1){
			getManager().setState(GameState.Restart);
		}
	}
	
	@EventHandler
	public void Funk(PlayerRespawnEvent ev){
		if(getGameList().isPlayerState(ev.getPlayer())==PlayerState.OUT){
			SetSpectator(ev.getPlayer());
		}
	}
	
	public void SetSpectator(Player player)
	  {
	    getManager().Clear(player);
	    player.teleport(UtilServer.getPlayers()[UtilMath.RandomInt(UtilServer.getPlayers().length, 0)].getLocation().add(0.0D,3.5D,0.0D));
	    player.setGameMode(GameMode.CREATIVE);
	    player.setFlying(true);
	    player.setFlySpeed(0.1F);
	    ((CraftPlayer)player).getHandle().k = false;
	    if(getCompass()==null)setCompass(new SpecCompass(getManager()));
	    player.getInventory().addItem(getCompass().getCompassItem());
	    
	    if(islastTeam()||getGameList().getPlayers(PlayerState.IN).size()<1){
			getManager().setState(GameState.Restart);
		}
	    
	  }
	
	@EventHandler
	public void EntityDamageByEntity(EntityDamageByEntityEvent ev){
		if(!getManager().isState(GameState.InGame))return;
		if((ev.getEntity() instanceof Player && ev.getDamager() instanceof Player)){
			if(!DamageTeamSelf&&getTeam((Player)ev.getDamager())==getTeam((Player)ev.getEntity())){
				ev.setCancelled(true);
			}else if(!DamageTeamOther&&getTeam((Player)ev.getDamager())!=getTeam((Player)ev.getEntity())){
				ev.setCancelled(true);
			}
		}
	}
	
}
