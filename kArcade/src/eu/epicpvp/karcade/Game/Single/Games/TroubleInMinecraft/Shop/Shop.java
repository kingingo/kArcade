package eu.epicpvp.karcade.Game.Single.Games.TroubleInMinecraft.Shop;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import eu.epicpvp.datenserver.definitions.dataserver.gamestats.StatsKey;
import eu.epicpvp.karcade.Game.Single.Games.TroubleInMinecraft.TroubleInMinecraft;
import eu.epicpvp.kcore.Enum.Team;
import eu.epicpvp.kcore.Translation.TranslationHandler;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.kcore.Util.UtilPlayer;
import lombok.Getter;

public class Shop implements Listener{

	@Getter
	TroubleInMinecraft TTT;
	@Getter
	Inventory shop;
	@Getter
	ItemStack shop_item;
	@Getter
	IShop[] ShopList;
	@Getter
	String Name;
	@Getter
	StatsKey punkte;
	@Getter
	Team t;

	public Shop(TroubleInMinecraft TTT,ItemStack shop_item,String Name,StatsKey punkte,Team t,IShop[] ShopList){
		this.TTT=TTT;
		this.shop_item=shop_item;
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

	@EventHandler(priority=EventPriority.HIGHEST)
	public void OpenShop(PlayerInteractEvent ev){
		if(ev.getAction()==Action.PHYSICAL)return;
		if(UtilItem.ItemNameEquals(ev.getPlayer().getItemInHand(), getShop_item())){
			if(TTT.getTeam(ev.getPlayer())!=t){
				return;
			}
			ev.getPlayer().openInventory(getShop());
			ev.setCancelled(true);
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
					int player_punkte = TTT.getStats().getInt(getPunkte(), p);
					if(player_punkte>=pk){
						TTT.getStats().setInt(p, (player_punkte-pk), getPunkte());
						s.add(p);
						UtilPlayer.sendMessage(p,TranslationHandler.getText(p, "PREFIX_GAME", TTT.getType().getTyp())+TranslationHandler.getText(p, "TTT_SHOP_BUYED"));
					}else{
						UtilPlayer.sendMessage(p,TranslationHandler.getText(p, "PREFIX_GAME", TTT.getType().getTyp())+TranslationHandler.getText(p, "TTT_SHOP"));
					}
					break;
				}
			}

		}
	}

}
