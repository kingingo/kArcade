package me.kingingo.karcade.Game.Games.EnderGames.Addon;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import com.google.common.collect.Lists;

import me.kingingo.karcade.Game.Games.EnderGames.EnderGames;
import me.kingingo.kcore.kListener;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;

public class AddonPlayerTeleport extends kListener{

	private int teleport_time = 30;
	private ArrayList<Player> newtp = Lists.newArrayList();
	private List<Player> tplist = Lists.newArrayList();
	private Player p1;
	private Player p2;
	private EnderGames instance;
	private Player pl1;
	private Player pl2;
	private Player pl3;
	private Player pl4;
	private Location loc1;
	private Location loc2;
	
	public AddonPlayerTeleport(EnderGames instance){
		super(instance.getManager().getInstance(),"[AddonPlayerTeleport]");
		this.instance=instance;
	}
	
	@EventHandler
	public void Teleport(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		if(instance.getManager().getState()!=GameState.InGame)return;
		teleport_time--;
		if (teleport_time == 0) {
			teleport_time = 30;
			pl1 = null;
			pl2 = null;
			pl3 = null;
			pl4 = null;

			List<Player> lasttp = Lists.newArrayList();

			if (tplist.size() >= 2) {
				int max = 1;
				for (int i = 0; i < max; i++) {
					p1 = tplist.get(0);
					p2 = tplist.get(1);
					p1.sendMessage("§6Du hast deine Position mit §5"+p2.getName()+" §6gewechselt!");
					p2.sendMessage("§6Du hast deine Position mit §5"+p1.getName()+" §6gewechselt!");
					loc1 = p1.getLocation();
					loc2 = p2.getLocation();

					if (!lasttp.contains(p1)) {
						p1.teleport(loc2);
						
						if(p1.isInsideVehicle()){
							Entity en = p1.getVehicle();
							p1.leaveVehicle();
							en.teleport(loc2);
							p1.teleport(loc2);
						}
						p1.playSound(p1.getLocation(), Sound.ENDERMAN_TELEPORT,
								3, 1);
						pl1 = p1;
						lasttp.add(p1);

						p1.leaveVehicle();
					}
					
					if (!lasttp.contains(p2)) {
						p2.teleport(loc1);
						if(p2.isInsideVehicle()){
							Entity en = p2.getVehicle();
							p2.leaveVehicle();
							en.teleport(loc1);
							p2.teleport(loc1);
						}						pl2 = p2;
						p2.playSound(p2.getLocation(), Sound.ENDERMAN_TELEPORT,
								3, 1);
						lasttp.add(p2);
						p2.leaveVehicle();

					}
				}
				newtp.clear();
				for (Player p : tplist) {
					if (p != pl1 && p != pl2 && p != pl3 && p != pl4) {
						newtp.add(p);
					}
				}

				if (pl4 != null)
					newtp.add(pl4);
				if (pl2 != null)
					newtp.add(pl2);
				if (pl1 != null)
					newtp.add(pl1);
				if (pl3 != null)
					newtp.add(pl3);
				tplist = (List<Player>)newtp;
				pl1 = null;
				pl2 = null;
				pl3 = null;
				pl4 = null;
			}
		}
	}
	
}
