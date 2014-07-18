package me.kingingo.karcade.Game.Games.TroubleInMinecraft.Detective.Item;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import me.kingingo.karcade.Enum.PlayerState;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.TroubleInMinecraft;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.Traitor.Shop;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilParticle;
import me.kingingo.kcore.Util.UtilParticle.ParticleType;

public class Healing_Station implements Listener,Shop {

	TroubleInMinecraft TTT;
	ItemStack item = UtilItem.RenameItem(new ItemStack(Material.BEACON), "§dHealing_Station");
	ArrayList<Block> stations = new ArrayList<>();
	
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
		ItemStack i = UtilItem.RenameItem(new ItemStack(Material.BEACON,1,(byte)3), "§dHealing_Station §7("+getPunkte()+" Punkte)");
		UtilItem.SetDescriptions(i, new String[]{
				"§7Heilt andere Spieler in der nähe der Heal-Station."
		});
		return i;
	}

	@Override
	public void add(Player p) {
		p.getInventory().addItem(item.clone());
	}
	
	@EventHandler
	public void Updater(UpdateEvent ev){
		if(UpdateType.SEC != ev.getType())return;
		if(stations.isEmpty())return;
		
		for(Block b : stations){

			for(int i = 0; i<10; i++){
				UtilParticle.PlayParticle(ParticleType.HEART,b.getLocation(),2, 2, 2, 5, 2);
			}
			
			for(Player p : TTT.getGameList().getPlayers(PlayerState.IN)){
				if(b.getLocation().distance(p.getLocation()) < 7){
					if(((CraftPlayer)p).getHealth()+1>((CraftPlayer)p).getHealth()){
						p.setHealth( ((CraftPlayer)p).getMaxHealth() );
					}else{
						p.setHealth( ((CraftPlayer)p).getHealth()+1 );
					}
				}
			}
		}
	}
	
	@EventHandler
	public void Block(BlockPlaceEvent ev){
		Block b = ev.getBlock();
		Player p = ev.getPlayer();
		if(p.getItemInHand()==null||UtilItem.ItemNameEquals(p.getItemInHand(), item))return;
		stations.add(b);
	}
	
}
