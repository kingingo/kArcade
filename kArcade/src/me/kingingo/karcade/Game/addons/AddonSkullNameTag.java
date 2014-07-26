package me.kingingo.karcade.Game.addons;

import java.util.HashMap;

import me.kingingo.karcade.kArcadeManager;
import me.kingingo.kcore.Hologram.Hologram;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class AddonSkullNameTag implements Listener{

	kArcadeManager Manager;
	Hologram hm;
	HashMap<Player,Block> l = new HashMap<>();
	
	public AddonSkullNameTag(kArcadeManager manager,Hologram hm){
		this.Manager=manager;
		this.hm=hm;
		Bukkit.getPluginManager().registerEvents(this, manager.getInstance());
	}
//	
//	Block t;
//	Skull s;
//	@EventHandler
//	public void Update(UpdateEvent ev){
//		if(UpdateType.FASTER!=ev.getType())return;
//		for(Player p : UtilServer.getPlayers()){
//			t=p.getTargetBlock(null, 7);
//			if(l.containsKey(p)){
//				if(t==l.get(p))continue;
//				
//				hm.RemoveText(p);
//			}
//			if(t.getType()==Material.SKULL){
//				Skull s = (Skull)t.getState();
//				hm.sendText(p, t.getLocation().add(0,1,0), "§6"+s.getOwner());
//				l.put(p, t);
//			}
//		}
//	}
	
}
