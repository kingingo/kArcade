package me.kingingo.karcade.Game.Games.TroubleInMinecraft.Shop.Item;

import java.util.HashMap;

import me.kingingo.karcade.Enum.Team;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.TroubleInMinecraft;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.Shop.IShop;
import me.kingingo.kcore.Util.UtilItem;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class Knife implements Listener,IShop{

	ItemStack item=UtilItem.RenameItem(new ItemStack(Material.GOLD_SWORD), "§eKnife");
	TroubleInMinecraft TTT;
	HashMap<Player,Team> teams = new HashMap<>();
	
	public Knife(TroubleInMinecraft TTT){
		this.TTT=TTT;
		Bukkit.getPluginManager().registerEvents(this, TTT.getManager().getInstance());
	}
	
	@Override
	public int getPunkte() {
		return 5;
	}

	@Override
	public ItemStack getShopItem() {
		ItemStack i = UtilItem.RenameItem(new ItemStack(Material.GOLD_SWORD,1,(byte)3), "§eKnife §7("+getPunkte()+" Punkte)");
		UtilItem.SetDescriptions(i, new String[]{
				"§7Ermöglicht einmalig das ",
				"§7sofortige töten eines",
				"§7Spielers von hinten."
		});
		return i;
	}

	@Override
	public void add(Player p) {
		p.getInventory().addItem(item.clone());
	}
	
	@EventHandler
	public void Damage(PlayerInteractEntityEvent ev){
		if(ev.getRightClicked() instanceof Player){
			Player defender = (Player)ev.getPlayer();
			if(defender.getItemInHand()==null||!UtilItem.ItemNameEquals(defender.getItemInHand(), item))return;
			Player attacker = (Player)ev.getRightClicked();
			defender.getInventory().remove(defender.getItemInHand());
			defender.playSound(defender.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
			attacker.damage(50, defender);
		}
		
	}

}
