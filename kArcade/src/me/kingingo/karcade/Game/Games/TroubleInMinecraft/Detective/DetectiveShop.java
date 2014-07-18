package me.kingingo.karcade.Game.Games.TroubleInMinecraft.Detective;

import java.util.ArrayList;

import lombok.Getter;
import me.kingingo.karcade.Enum.Team;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.TroubleInMinecraft;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.Detective.Item.Defibrillator;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.Detective.Item.Golden_Weapon;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.Detective.Item.Healing_Station;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.Traitor.Shop;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.Traitor.Item.Fake_Chest;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.Traitor.Item.Medipack;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.Traitor.Item.Radar;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.Traitor.Item.Tester_Spoofer;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.PlayerStats.Stats;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.UtilItem;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

public class DetectiveShop implements Listener{

	@Getter
	TroubleInMinecraft TTT;
	@Getter
	Inventory shop;
	
	@Getter
	ArrayList<Shop> ShopList = new ArrayList<>();
	
	@Getter
	Defibrillator db;
	@Getter
	Golden_Weapon gw;
	@Getter
	Healing_Station hs;
	
	public DetectiveShop(TroubleInMinecraft TTT){
		this.TTT=TTT;
		shop=Bukkit.createInventory(null, 9,"Detective-Shop:");
		
		ShopList.add(db= new Defibrillator(TTT));
		ShopList.add(gw = new Golden_Weapon(TTT));
		ShopList.add(hs=new Healing_Station(TTT));
		
		for(Shop s : ShopList){
			shop.addItem(s.getShopItem());
		}
		
		Bukkit.getPluginManager().registerEvents(this, TTT.getManager().getInstance());
	}
	
	@EventHandler
	public void OpenShop(PlayerInteractEvent ev){
		if(UtilEvent.isAction(ev, ActionType.R)&&ev.getPlayer().getItemInHand()!=null&&ev.getPlayer().getItemInHand().getType()==Material.STICK&&TTT.getTeam(ev.getPlayer())==Team.DETECTIVE){
			ev.getPlayer().openInventory(getShop());
		}
	}
	
	@EventHandler
	public void UseShop(InventoryClickEvent ev){
		if (!(ev.getWhoClicked() instanceof Player)|| ev.getInventory() == null || ev.getCursor() == null || ev.getCurrentItem() == null)return;
		if(ev.getInventory().getName().equalsIgnoreCase("Detective-Shop:")){
			Player p = (Player)ev.getWhoClicked();
			ev.setCancelled(true);
			p.closeInventory();
			
			for(Shop s : ShopList){
				if(UtilItem.ItemNameEquals(ev.getCurrentItem(), s.getShopItem())){
					int pk = s.getPunkte();
					int player_punkte = TTT.getManager().getStats().getInt(Stats.TTT_DETECTIVE_PUNKTE, p);
					if(player_punkte>=pk){
						TTT.getManager().getStats().setInt(p, (player_punkte-pk), Stats.TTT_DETECTIVE_PUNKTE);
						s.add(p);
						p.sendMessage(Text.PREFIX_GAME.getText(TTT.getManager().getTyp().string())+Text.TTT_DETECTIVE_SHOP_BUYED.getText());
					}else{
						p.sendMessage(Text.PREFIX_GAME.getText(TTT.getManager().getTyp().string())+Text.TTT_DETECTIVE_SHOP.getText());
					}
					break;
				}
			}
			
		}
	}
	
}
