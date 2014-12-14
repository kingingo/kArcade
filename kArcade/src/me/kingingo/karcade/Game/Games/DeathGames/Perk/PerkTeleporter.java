package me.kingingo.karcade.Game.Games.DeathGames.Perk;

import java.util.ArrayList;

import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Enum.PlayerState;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.Kit.Perk;
import me.kingingo.kcore.Kit.Perks.Event.PerkStartEvent;
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
	private kArcadeManager manager;
	
	public PerkTeleporter(kArcadeManager manager) {
		super("Teleporter");
		this.manager=manager;
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
				ArrayList<Player> l = (ArrayList<Player>)manager.getGame().getGameList().getPlayers(PlayerState.IN).clone();
				l.remove(ev.getPlayer());
				Player tele = l.get(UtilMath.r(l.size()));
				
				Location tele_loc = tele.getLocation();
				Location p_loc= ev.getPlayer().getLocation();
				
				ev.getPlayer().sendMessage(Text.PREFIX_GAME.getText(manager.getGame().getType().getTyp())+Text.ENDERGAMES_TELEPORT.getText(tele.getName()));
				tele.sendMessage(Text.PREFIX_GAME.getText(manager.getGame().getType().getTyp())+Text.ENDERGAMES_TELEPORT.getText(ev.getPlayer().getName()));
				
				tele.teleport(p_loc);
				ev.getPlayer().teleport(tele_loc);
			}
		}
	}

}
