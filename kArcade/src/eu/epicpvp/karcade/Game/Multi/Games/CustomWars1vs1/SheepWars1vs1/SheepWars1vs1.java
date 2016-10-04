package eu.epicpvp.karcade.Game.Multi.Games.CustomWars1vs1.SheepWars1vs1;

import java.io.File;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import eu.epicpvp.datenserver.definitions.dataserver.gamestats.GameState;
import eu.epicpvp.karcade.Game.Multi.MultiGames;
import eu.epicpvp.karcade.Game.Multi.Events.MultiGamePlayerJoinEvent;
import eu.epicpvp.karcade.Game.Multi.Games.CustomWars1vs1.CustomWars1vs1;
import eu.epicpvp.kcore.Kit.Shop.MultiKitShop;
import eu.epicpvp.kcore.Util.InventorySize;
import eu.epicpvp.kcore.Util.UtilEvent;
import eu.epicpvp.kcore.Util.UtilEvent.ActionType;
import eu.epicpvp.kcore.Util.UtilItem;

public class SheepWars1vs1 extends CustomWars1vs1{

	public SheepWars1vs1(MultiGames games, String Map, Location pasteLocation, File file) {
		super(games, Map, pasteLocation, file);

		setStartCountdown(31);
		if(games.getKitshop() == null){
			games.setKitshop(new MultiKitShop(games,games.getMoney(), "Kit-Shop", InventorySize._27, UtilSheepWars1vs1.getKits(games)));
		}
	}

	@EventHandler
	public void ShopOpen(PlayerInteractEvent ev){
		if(getState()==GameState.LobbyPhase&&getGameList().getPlayers().containsKey(ev.getPlayer())&&UtilEvent.isAction(ev, ActionType.RIGHT)){
			if(ev.getPlayer().getItemInHand()!=null&&UtilItem.ItemNameEquals(ev.getPlayer().getItemInHand(), UtilItem.RenameItem(new ItemStack(Material.CHEST), "§bKitShop"))){
				ev.getPlayer().openInventory(getGames().getKitshop().getInventory());
			}
		}
	}

	@EventHandler(priority=EventPriority.LOWEST)
	public void JoinSheepWars(MultiGamePlayerJoinEvent ev){
		if(ev.getGame()!=this)return;
		//Prüft ob dieser Spieler für die Arena angemeldet ist.
		if(getTeamList().containsKey(ev.getPlayer())){
			ev.getPlayer().getInventory().addItem(UtilItem.RenameItem(new ItemStack(Material.CHEST), "§bKitShop"));
		}
	}
}
