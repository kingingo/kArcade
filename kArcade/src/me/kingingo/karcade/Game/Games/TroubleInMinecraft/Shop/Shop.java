package me.kingingo.karcade.Game.Games.TroubleInMinecraft.Shop;

import lombok.Getter;
import me.kingingo.karcade.Enum.Team;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.TroubleInMinecraft;
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

public class Shop implements Listener{

	@Getter
	TroubleInMinecraft TTT;
	@Getter
	Inventory shop;
	
	@Getter
	IShop[] ShopList;
	@Getter
	String Name;
	@Getter
	Stats punkte;
	@Getter
	Team t;
	
	public Shop(TroubleInMinecraft TTT,String Name,Stats punkte,Team t,IShop[] ShopList){
		this.TTT=TTT;
		this.t=t;
		this.punkte=punkte;
		this.Name=Name;
		shop=Bukkit.createInventory(null, 9,Name);
		this.ShopList=ShopList;
		
		for(IShop s : ShopList){
			shop.addItem(s.getShopItem());
		}
		
		Bukkit.getPluginManager().registerEvents(this, TTT.getManager().getInstance());
	}
	
	@EventHandler
	public void OpenShop(PlayerInteractEvent ev){
		if(UtilEvent.isAction(ev, ActionType.R)&&ev.getPlayer().getItemInHand()!=null&&ev.getPlayer().getItemInHand().getType()==Material.STICK&&TTT.getTeam(ev.getPlayer())==t){
			ev.getPlayer().openInventory(getShop());
		}
	}
	
	@EventHandler
	public void UseShop(InventoryClickEvent ev){
		if (!(ev.getWhoClicked() instanceof Player)|| ev.getInventory() == null || ev.getCursor() == null || ev.getCurrentItem() == null)return;
		if(ev.getInventory().getName().equalsIgnoreCase(Name)){
			Player p = (Player)ev.getWhoClicked();
			ev.setCancelled(true);
			p.closeInventory();
			
			for(IShop s : ShopList){
				if(UtilItem.ItemNameEquals(ev.getCurrentItem(), s.getShopItem())){
					int pk = s.getPunkte();
					int player_punkte = TTT.getManager().getStats().getInt(getPunkte(), p);
					if(player_punkte>=pk){
						TTT.getManager().getStats().setInt(p, (player_punkte-pk), getPunkte());
						s.add(p);
						p.sendMessage(Text.PREFIX_GAME.getText(TTT.getManager().getTyp().string())+Text.TTT_SHOP_BUYED.getText());
					}else{
						p.sendMessage(Text.PREFIX_GAME.getText(TTT.getManager().getTyp().string())+Text.TTT_SHOP.getText());
					}
					break;
				}
			}
			
		}
	}
	
}
