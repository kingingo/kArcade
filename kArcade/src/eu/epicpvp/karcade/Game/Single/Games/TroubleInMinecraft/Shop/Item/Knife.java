package eu.epicpvp.karcade.Game.Single.Games.TroubleInMinecraft.Shop.Item;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import eu.epicpvp.karcade.Game.Single.Games.TroubleInMinecraft.TroubleInMinecraft;
import eu.epicpvp.karcade.Game.Single.Games.TroubleInMinecraft.Shop.IShop;
import eu.epicpvp.kcore.Enum.Team;
import eu.epicpvp.kcore.Util.UtilInv;
import eu.epicpvp.kcore.Util.UtilItem;

public class Knife implements Listener,IShop{

	ItemStack item=UtilItem.RenameItem(new ItemStack(Material.SHEARS), "§eKnife");
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
		ItemStack i = UtilItem.RenameItem(new ItemStack(Material.SHEARS,1), "§eKnife §7("+getPunkte()+" Punkte)");
		UtilItem.SetDescriptions(i, new String[]{
				"§7Erm§glicht einmalig das ",
				"§7sofortige t§ten eines Spieler's."
		});
		return i;
	}

	@Override
	public void add(Player p) {
		p.getInventory().addItem(item.clone());
	}
	
	@EventHandler
	public void Damage(EntityDamageByEntityEvent ev){
		if(ev.getEntity() instanceof Player&&ev.getDamager() instanceof Player){
			Player attacker = (Player)ev.getDamager();
			if(attacker.getItemInHand()==null||!UtilItem.ItemNameEquals(attacker.getItemInHand(), item))return;
			Player defender = (Player)ev.getEntity();
			UtilInv.remove(attacker, attacker.getItemInHand().getType(), attacker.getItemInHand().getData().getData(), 1);
			attacker.playSound(defender.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
			ev.setDamage(50);
		}
		
	}

}
