package eu.epicpvp.karcade.Game.Single.Games.DeathGames.Perk;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import eu.epicpvp.karcade.Game.Single.SingleGame;
import eu.epicpvp.kcore.Enum.PlayerState;
import eu.epicpvp.kcore.Kit.Perk;
import eu.epicpvp.kcore.Kit.Perks.Event.PerkStartEvent;
import eu.epicpvp.kcore.Translation.TranslationHandler;
import eu.epicpvp.kcore.Util.UtilEvent;
import eu.epicpvp.kcore.Util.UtilEvent.ActionType;
import eu.epicpvp.kcore.Util.UtilInv;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.kcore.Util.UtilMath;

public class PerkTeleporter extends Perk{

	private ItemStack item = UtilItem.RenameItem(new ItemStack(Material.WATCH), "§cTeleporter");
	private SingleGame game;
	
	public PerkTeleporter(SingleGame game) {
		super("Teleporter");
		this.game=game;
	}
	
	@EventHandler
	public void Start(PerkStartEvent ev){
		if(!getPerkData().getPlayers().containsKey(this))return;
		for(Player p : getPerkData().getPlayers().get(this))p.getInventory().addItem(item.clone());
	}
	
	@EventHandler
	public void Interact(PlayerInteractEvent ev){
		if(UtilEvent.isAction(ev, ActionType.RIGHT)){
			if(UtilItem.ItemNameEquals(ev.getPlayer().getItemInHand(), item)){
				UtilInv.remove(ev.getPlayer(), item.getType(), (byte)0, 1);
				ArrayList<Player> l = (ArrayList<Player>)game.getGameList().getPlayers(PlayerState.INGAME).clone();
				l.remove(ev.getPlayer());
				Player tele = l.get(UtilMath.r(l.size()));
				
				Location tele_loc = tele.getLocation();
				Location p_loc= ev.getPlayer().getLocation();
				
				ev.getPlayer().sendMessage(TranslationHandler.getText(ev.getPlayer(), "PREFIX_GAME", game.getType().getTyp())+TranslationHandler.getText(ev.getPlayer(), "ENDERGAMES_TELEPORT", tele.getName()));
				tele.sendMessage(TranslationHandler.getText(tele, "PREFIX_GAME", game.getType().getTyp())+TranslationHandler.getText(tele, "ENDERGAMES_TELEPORT", ev.getPlayer().getName()));
				
				tele.teleport(p_loc);
				ev.getPlayer().teleport(tele_loc);
			}
		}
	}

}
