package me.kingingo.karcade.Game.Games.TroubleInMinecraft;

import java.util.ArrayList;
import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;
import me.kingingo.karcade.Enum.PlayerState;
import me.kingingo.karcade.Enum.Team;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.Shop.Item.Tester_Spoofer;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.Shop.Item.Events.TesterSpooferEvent;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.TimeSpan;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilEvent.ActionType;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.player.PlayerInteractEvent;

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
				last.put(p, System.currentTimeMillis()+TimeSpan.MINUTE);
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
			if(!TTT.getGameList().getPlayers(PlayerState.IN).contains(ev.getPlayer()))return;
			if(Button.getBlockY()!=ev.getClickedBlock().getLocation().getBlockY()||Button.getBlockX()!=ev.getClickedBlock().getLocation().getBlockX()||Button.getBlockZ()!=ev.getClickedBlock().getLocation().getBlockZ())return;
			ev.setCancelled(true);
			
			if(l!=-1){
				if(l > System.currentTimeMillis()){
					ev.getPlayer().sendMessage(Text.PREFIX_GAME.getText(TTT.getManager().getTyp().string())+Text.TTT_TESTER_WAS_USED.getText());
					return;
				}
			}
			
			if(use){
				ev.getPlayer().sendMessage(Text.PREFIX_GAME.getText(TTT.getManager().getTyp().string())+Text.TTT_TESTER_USED.getText());
			}else{
				if(last.containsKey(ev.getPlayer())){
					if(last.get(ev.getPlayer()) > System.currentTimeMillis()){
						ev.getPlayer().sendMessage(Text.PREFIX_GAME.getText(TTT.getManager().getTyp().string())+Text.TTT_TESTER_TIME.getText());
						return;
					}
				}
				for(Location l : Glass){
					l.getBlock().setType(Material.GLASS);
				}
				timer=-1;
				p=ev.getPlayer();
				TTT.getManager().broadcast(Text.PREFIX_GAME.getText(TTT.getManager().getTyp().string())+Text.TTT_TESTER_JOIN.getText(ev.getPlayer().getName()));
				use=true;
				ev.getPlayer().teleport(Join);
			}
		}
	}
	
}
