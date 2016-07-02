package eu.epicpvp.karcade.Game.Single.Games.TroubleInMinecraft.Shop.Item;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import eu.epicpvp.karcade.Game.Single.Games.TroubleInMinecraft.TroubleInMinecraft;
import eu.epicpvp.karcade.Game.Single.Games.TroubleInMinecraft.Shop.IShop;
import eu.epicpvp.kcore.Enum.Team;
import eu.epicpvp.kcore.Util.UtilInv;
import eu.epicpvp.kcore.Util.UtilItem;

public class Golden_Weapon implements Listener,IShop{

	ItemStack item=UtilItem.RenameItem(new ItemStack(Material.GOLD_SWORD), "§eGolden Weapon");
	TroubleInMinecraft TTT;
	HashMap<Player,Team> teams = new HashMap<>();
	
	public Golden_Weapon(TroubleInMinecraft TTT){
		this.TTT=TTT;
		Bukkit.getPluginManager().registerEvents(this, TTT.getManager().getInstance());
	}
	
	@Override
	public int getPunkte() {
		return 5;
	}

	@Override
	public ItemStack getShopItem() {
		ItemStack i = UtilItem.RenameItem(new ItemStack(Material.GOLD_SWORD,1), "§eGolden Weapon §7("+getPunkte()+" Punkte)");
		UtilItem.setLore(i, new String[]{
				"§7Ein Goldschwert, welches einmalig ",
				"§7einen Traitor mit einem Schlag t§tet. ",
				"§7Bei Innocents zerbricht es sofort."
		});
		return i;
	}

	@Override
	public void add(Player p) {
		p.getInventory().addItem(item.clone());
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void Damage(EntityDamageByEntityEvent ev){
		if(ev.getDamager() instanceof Player && ev.getEntity() instanceof Player){
			Player attacker = (Player)ev.getDamager();
			if(attacker.getItemInHand()==null||!UtilItem.ItemNameEquals(attacker.getItemInHand(), item))return;
			Player defender = (Player)ev.getEntity();
			UtilInv.remove(attacker,attacker.getItemInHand().getType(), attacker.getItemInHand().getData().getData(), 1);
			attacker.updateInventory();
			attacker.playSound(defender.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
			if(TTT.getTeam(defender)==Team.TRAITOR){
				ev.setDamage(50);
			}else{
				ev.setDamage(0);
			}
		}
		
	}

}
