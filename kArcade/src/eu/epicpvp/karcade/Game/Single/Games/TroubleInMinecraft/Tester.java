package eu.epicpvp.karcade.Game.Single.Games.TroubleInMinecraft;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import dev.wolveringer.dataserver.gamestats.GameState;
import dev.wolveringer.dataserver.gamestats.StatsKey;
import eu.epicpvp.karcade.Game.Single.Games.TroubleInMinecraft.Shop.Item.Events.TesterSpooferEvent;
import eu.epicpvp.kcore.Enum.PlayerState;
import eu.epicpvp.kcore.Enum.Team;
import eu.epicpvp.kcore.Translation.TranslationManager;
import eu.epicpvp.kcore.Update.UpdateType;
import eu.epicpvp.kcore.Update.Event.UpdateEvent;
import eu.epicpvp.kcore.Util.TimeSpan;
import eu.epicpvp.kcore.Util.UtilEvent;
import eu.epicpvp.kcore.Util.UtilEvent.ActionType;
import eu.epicpvp.kcore.Util.UtilPlayer;
import lombok.Getter;
import lombok.Setter;

public class Tester implements Listener{

	@Getter
	TroubleInMinecraft TTT;
	@Getter
	@Setter
	Location Button;
	@Getter
	@Setter
	Location[] Glass;
	@Getter
	@Setter
	Location[] Lampen;
	@Getter
	@Setter
	Location Join;
	@Getter
	@Setter
	boolean use=false;
	@Getter
	Player p;
	@Getter
	int timer=0;
	@Getter
	HashMap<Player,Long> last = new HashMap<Player,Long>();
	long l=-1;
	
	public Tester(TroubleInMinecraft TTT){
		this.TTT=TTT;
		for(Location l : Lampen){
			l.getBlock().setTypeIdAndData(Material.STAINED_GLASS.getId(), (byte)8, true);
		}
		Bukkit.getPluginManager().registerEvents(this, TTT.getManager().getInstance());
	}
	
	public Tester(TroubleInMinecraft TTT,Location Button,Location Join, Location[] Lampen, Location[] Glass){
		this.TTT=TTT;
		this.Button=Button;
		this.Lampen=Lampen;
		this.Glass=Glass;
		this.Join=Join;
		for(Location l : Lampen){
			l.getBlock().setTypeIdAndData(Material.STAINED_GLASS.getId(), (byte)8, true);
		}
		Bukkit.getPluginManager().registerEvents(this, TTT.getManager().getInstance());
	}
	
	public Tester(TroubleInMinecraft TTT,Location Button,Location Join, ArrayList<Location> Lampen, ArrayList<Location> Glass){
		this.TTT=TTT;
		this.Join=Join;
		this.Button=Button;
		int i=0;
		this.Lampen=new Location[Lampen.size()];
		for(Location loc : Lampen){
			this.Lampen[i]=loc;
			i++;
		}
		i=0;
		this.Glass=new Location[Glass.size()];
		for(Location loc : Glass){
			this.Glass[i]=loc;
			i++;
		}
		for(Location l : Lampen){
			l.getBlock().setTypeIdAndData(Material.STAINED_GLASS.getId(), (byte)8, true);
		}
		Bukkit.getPluginManager().registerEvents(this, TTT.getManager().getInstance());
	}
	
	@EventHandler
	public void Test(UpdateEvent ev){
		if(UpdateType.SEC!=ev.getType())return;
		if(!use)return;
		if(p==null){
			use=false;
			return;
		}else{
			if(timer==-1)timer=8;
			timer--;
			switch(timer){
			case 1:
				TesterSpooferEvent e = new TesterSpooferEvent(p);
				Bukkit.getPluginManager().callEvent(e);
				if(TTT.getTeam(p)==Team.TRAITOR&&!e.isCancelled()){
					for(Location l : Lampen){
						l.getBlock().setTypeIdAndData(Material.STAINED_GLASS.getId(), (byte)14, true);
					}
				}else if(TTT.getTeam(p)==Team.DETECTIVE){
					for(Location l : Lampen){
						l.getBlock().setTypeIdAndData(Material.STAINED_GLASS.getId(), (byte)11, true);
					}
				}
				break;
			case 0:
				for(Location l : Glass){
					l.getBlock().setType(Material.AIR);
				}
				for(Location l : Lampen){
					l.getBlock().setTypeIdAndData(Material.STAINED_GLASS.getId(), (byte)8, true);
				}
				last.put(p, System.currentTimeMillis()+TimeSpan.SECOND*20);
				use=false;
				p=null;
				timer=-1;
				l=System.currentTimeMillis()+(TimeSpan.SECOND*3);
				break;
			}
		}
	}
	
	@EventHandler
	public void onBlockRedstone(BlockRedstoneEvent event) {
		if (event.getBlock().getType() != Material.REDSTONE_LAMP_ON) return;
		event.setNewCurrent(50);
	}
	
	@EventHandler
	public void Interact(PlayerInteractEvent ev){
		if(UtilEvent.isAction(ev, ActionType.R_BLOCK)&&ev.getClickedBlock().getType() == Material.STONE_BUTTON){
			if(TTT.getState()!=GameState.InGame||!TTT.getGameList().getPlayers(PlayerState.IN).contains(ev.getPlayer()))return;
			if(Button.getBlockY()!=ev.getClickedBlock().getLocation().getBlockY()||Button.getBlockX()!=ev.getClickedBlock().getLocation().getBlockX()||Button.getBlockZ()!=ev.getClickedBlock().getLocation().getBlockZ())return;
			ev.setCancelled(true);
			
			if(l!=-1){
				if(l > System.currentTimeMillis()){
					UtilPlayer.sendMessage(ev.getPlayer(),TranslationManager.getText(ev.getPlayer(), "PREFIX_GAME", TTT.getType().getTyp())+TranslationManager.getText(ev.getPlayer(), "TTT_TESTER_WAS_USED"));
					return;
				}
			}
			
			if(use){
				ev.getPlayer().sendMessage(TranslationManager.getText(ev.getPlayer(), "PREFIX_GAME", TTT.getType().getTyp())+TranslationManager.getText(ev.getPlayer(), "TTT_TESTER_USED"));
			}else{
				if(last.containsKey(ev.getPlayer())){
					if(last.get(ev.getPlayer()) > System.currentTimeMillis()){
						UtilPlayer.sendMessage(ev.getPlayer(),TranslationManager.getText(ev.getPlayer(), "PREFIX_GAME", TTT.getType().getTyp())+TranslationManager.getText(ev.getPlayer(), "TTT_TESTER_TIME"));
						return;
					}
				}
				for(Location l : Glass){
					l.getBlock().setType(Material.GLASS);
				}
				timer=-1;
				p=ev.getPlayer();
				TTT.getStats().addInt(ev.getPlayer(),1, StatsKey.TTT_TESTS);
				TTT.broadcast(TranslationManager.getText(ev.getPlayer(), "PREFIX_GAME", TTT.getType().getTyp())+TranslationManager.getText(ev.getPlayer(), "TTT_TESTER_JOIN",ev.getPlayer().getName()));
				use=true;
				ev.getPlayer().teleport(Join);
			}
		}
	}
	
}
