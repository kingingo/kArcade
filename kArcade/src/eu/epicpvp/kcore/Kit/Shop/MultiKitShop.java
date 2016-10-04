package eu.epicpvp.kcore.Kit.Shop;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import eu.epicpvp.datenserver.definitions.dataserver.gamestats.GameState;
import eu.epicpvp.karcade.Game.Multi.MultiGames;
import eu.epicpvp.karcade.Game.Multi.Events.MultiGameStartEvent;
import eu.epicpvp.karcade.Game.Multi.Events.MultiGameStateChangeEvent;
import eu.epicpvp.karcade.Game.Multi.Games.MultiGame;
import eu.epicpvp.kcore.Enum.PlayerState;
import eu.epicpvp.kcore.Kit.Kit;
import eu.epicpvp.kcore.Kit.Perk;
import eu.epicpvp.kcore.Kit.Perks.Event.PerkHasPlayerEvent;
import eu.epicpvp.kcore.Kit.Perks.Event.PerkStartEvent;
import eu.epicpvp.kcore.StatsManager.StatsManager;
import eu.epicpvp.kcore.Util.InventorySize;
import lombok.Getter;

public class MultiKitShop extends KitShop{

	@Getter
	private MultiGames games;
	private HashMap<MultiGame,ArrayList<Player>> list;

	public MultiKitShop(MultiGames games,StatsManager money, String name, InventorySize size,Kit[] kits) {
		super(money.getInstance(),money, games.getManager().getPermManager(), name, size, kits);
		this.games=games;
		this.list=new HashMap<>();

		for(Kit k : getKits()){
			for(Perk perk : k.getPerks()){
				Bukkit.getPluginManager().registerEvents(perk, getPermManager().getInstance());
			}
		}
	}

	@EventHandler
	public void has(PerkHasPlayerEvent ev){
		for(MultiGame game : list.keySet()){
			if(list.get(game).contains(ev.getPlayer())){
				if(game.getState()!=GameState.InGame&&game.getState()!=GameState.DeathMatch){
					ev.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority=EventPriority.NORMAL)
	public void StateChange(MultiGameStateChangeEvent ev){
		if(ev.getTo()==GameState.Restart){
			if(list.containsKey(ev.getGame())){
				list.get(ev.getGame()).clear();
				list.remove(ev.getGame());
			}
		}
	}


	@EventHandler(priority=EventPriority.HIGHEST)
	public void Start(MultiGameStartEvent ev){
		if(!list.containsKey(ev.getGame())){
			list.put(ev.getGame(), ev.getGame().getGameList().getPlayers(PlayerState.INGAME));
		}else{
			list.get(ev.getGame()).clear();
			list.remove(ev.getGame());
			list.put(ev.getGame(), ev.getGame().getGameList().getPlayers(PlayerState.INGAME));
		}

		Bukkit.getPluginManager().callEvent(new PerkStartEvent(ev.getGame().getGameList().getPlayers(PlayerState.INGAME)));
	}
}
