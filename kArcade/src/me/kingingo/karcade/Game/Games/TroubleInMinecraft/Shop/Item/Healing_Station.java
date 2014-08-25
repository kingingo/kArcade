package me.kingingo.karcade.Game.Games.TroubleInMinecraft.Shop.Item;

import java.util.ArrayList;
import java.util.HashMap;

import me.kingingo.karcade.Enum.PlayerState;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.TroubleInMinecraft;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.Shop.IShop;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilMath;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class Healing_Station implements Listener,IShop {

	TroubleInMinecraft TTT;
	ItemStack item = UtilItem.RenameItem(new ItemStack(Material.BEACON), "§dHealing Station");
	HashMap<Block,Double> stations = new HashMap<>();
	
	public Healing_Station(TroubleInMinecraft TTT){
		this.TTT=TTT;
		TTT.getBlockPlaceAllow().add(Material.BEACON);
		Bukkit.getPluginManager().registerEvents(this, TTT.getManager().getInstance());
	}

	@Override
	public int getPunkte() {
		return 2;
	}

	@Override
	public ItemStack getShopItem() {
		ItemStack i = UtilItem.RenameItem(new ItemStack(Material.BEACON,1,(byte)3), "§dHealing Station §7("+getPunkte()+" Punkte)");
		UtilItem.SetDescriptions(i, new String[]{
				"§7Heilt andere Spieler in der nähe der Heal-Station."
		});
		return i;
	}

	@Override
	public void add(Player p) {
		p.getInventory().addItem(item.clone());
	}
	
	double d;
	ArrayList<Block> l = new ArrayList<>();
	HashMap<Block,Double> l1 = new HashMap<>();
	@EventHandler
	public void Updater(UpdateEvent ev){
		if(UpdateType.SEC != ev.getType())return;
		if(stations.isEmpty())return;
		if(!l.isEmpty())l.clear();
		if(!l1.isEmpty())l1.clear();
		for(Block b : stations.keySet()){
			d=stations.get(b);
			if(d<=0.0){
				b.setType(Material.AIR);
				for(int i = 0; i<10; i++){
					b.getLocation().getWorld().playEffect(b.getLocation().add(UtilMath.r(4),UtilMath.r(3),UtilMath.r(4)), Effect.FLAME, -10);
				}
				l.add(b);
				continue;
			}
			for(int i = 0; i<10; i++){
				b.getLocation().getWorld().playEffect(b.getLocation().add(UtilMath.r(4),UtilMath.r(3),UtilMath.r(4)), Effect.HEART, -10);
			}
			
			for(Player p : TTT.getGameList().getPlayers(PlayerState.IN)){
				if(b.getLocation().distance(p.getLocation()) < 3&&((CraftPlayer)p).getHealth()!=((CraftPlayer)p).getMaxHealth()){
					d=d-0.4;
					if(((CraftPlayer)p).getHealth()+0.4>((CraftPlayer)p).getMaxHealth()){
						p.setHealth( ((CraftPlayer)p).getMaxHealth() );
					}else{
						p.setHealth( ((CraftPlayer)p).getHealth()+0.4 );
					}
				}
			}
			l1.put(b, d);
		}
		
		for(Block b : l1.keySet()){
			stations.remove(b);
			stations.put(b, l1.get(b));
		}
		
		for(Block b : l){
			stations.remove(b);
		}
		l.clear();
	}
	
	@EventHandler
	public void Block(BlockPlaceEvent ev){
		Block b = ev.getBlock();
		Player p = ev.getPlayer();
		if(p.getItemInHand()==null||!UtilItem.ItemNameEquals(p.getItemInHand(), item))return;
		stations.put(b, 40.0);
	}
	
}
