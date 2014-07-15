package me.kingingo.karcade.Game.Games.TroubleInMinecraft.Traitor;

import java.util.ArrayList;

import lombok.Getter;
import me.kingingo.karcade.Enum.Team;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.TroubleInMinecraft;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.Traitor.Item.Fake_Chest;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.Traitor.Item.Medipack;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.Traitor.Item.Radar;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.Traitor.Item.Tester_Spoofer;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.PlayerStats.Stats;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilEvent.ActionType;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

public class TraitorShop implements Listener{

	@Getter
	TroubleInMinecraft TTT;
	@Getter
	Inventory shop;
	
	@Getter
	ArrayList<Shop> ShopList = new ArrayList<>();
	
	@Getter
	Medipack mp;
	@Getter
	Radar r;
	@Getter
	Fake_Chest fc;
	@Getter
	Tester_Spoofer ts;
	
	public TraitorShop(TroubleInMinecraft TTT){
		this.TTT=TTT;
		shop=Bukkit.createInventory(null, 9,"Traitor-Shop:");
		ShopList.add(ts= new Tester_Spoofer());
		ShopList.add(r= new Radar(TTT));
		ShopList.add(fc= new Fake_Chest(TTT));
		ShopList.add(mp= new Medipack(TTT));
		
		for(Shop s : ShopList){
			shop.addItem(s.getShopItem());
		}
		
		Bukkit.getPluginManager().registerEvents(this, TTT.getManager().getInstance());
	}
	
	@EventHandler
	public void OpenShop(PlayerInteractEvent ev){
		if(UtilEvent.isAction(ev, ActionType.R)&&TTT.getTeam(ev.getPlayer())==Team.TRAITOR){
			ev.getPlayer().openInventory(getShop());
		}
	}
	
	@EventHandler
	public void UseShop(InventoryClickEvent ev){
		if (!(ev.getWhoClicked() instanceof Player)|| ev.getInventory() == null || ev.getCursor() == null || ev.getCurrentItem() == null)return;
		if(ev.getInventory().getName().equalsIgnoreCase("Traitor-Shop:")){
			Player p = (Player)ev.getWhoClicked();
			ev.setCancelled(true);
			p.closeInventory();
			
			for(Shop s : ShopList){
				if(ev.getCurrentItem()==s.getShopItem()){
					int pk = s.getPunkte();
					int player_punkte = TTT.getManager().getStats().getInt(Stats.TTT_TRAITOR_PUNKTE, p);
					if(player_punkte>=pk){
						TTT.getManager().getStats().setInt(p, (player_punkte-pk), Stats.TTT_TRAITOR_PUNKTE);
						s.add(p);
						p.sendMessage(Text.PREFIX_GAME.getText(TTT.getManager().getTyp().string())+Text.TTT_TRAITOR_SHOP_BUYED.getText());
					}else{
						p.sendMessage(Text.PREFIX_GAME.getText(TTT.getManager().getTyp().string())+Text.TTT_TRAITOR_SHOP.getText());
					}
					break;
				}
			}
			
		}
	}
	
}
