package me.kingingo.karcade.Game.Single.Games.DeathGames.Perk;

import java.util.ArrayList;

import me.kingingo.karcade.Game.Single.SingleGame;
import me.kingingo.kcore.Enum.PlayerState;
import me.kingingo.kcore.Kit.Perk;
import me.kingingo.kcore.Kit.Perks.Event.PerkStartEvent;
import me.kingingo.kcore.Language.Language;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.UtilInv;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilMath;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

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
		if(UtilEvent.isAction(ev, ActionType.R)){
			if(UtilItem.ItemNameEquals(ev.getPlayer().getItemInHand(), item)){
				UtilInv.remove(ev.getPlayer(), item.getType(), (byte)0, 1);
				ArrayList<Player> l = (ArrayList<Player>)game.getGameList().getPlayers(PlayerState.IN).clone();
				l.remove(ev.getPlayer());
				Player tele = l.get(UtilMath.r(l.size()));
				
				Location tele_loc = tele.getLocation();
				Location p_loc= ev.getPlayer().getLocation();
				
				ev.getPlayer().sendMessage(Language.getText(ev.getPlayer(), "PREFIX_GAME", game.getType().getTyp())+Language.getText(ev.getPlayer(), "ENDERGAMES_TELEPORT", tele.getName()));
				tele.sendMessage(Language.getText(tele, "PREFIX_GAME", game.getType().getTyp())+Language.getText(tele, "ENDERGAMES_TELEPORT", ev.getPlayer().getName()));
				
				tele.teleport(p_loc);
				ev.getPlayer().teleport(tele_loc);
			}
		}
	}

}
