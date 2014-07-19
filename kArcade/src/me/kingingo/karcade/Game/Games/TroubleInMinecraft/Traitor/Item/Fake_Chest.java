package me.kingingo.karcade.Game.Games.TroubleInMinecraft.Traitor.Item;

import java.util.HashMap;

import me.kingingo.karcade.Enum.Team;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.TTT_Item;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.TroubleInMinecraft;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.Traitor.Shop;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilMath;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class Fake_Chest implements Listener,Shop {

	ItemStack item = UtilItem.RenameItem(new ItemStack(Material.SKULL_ITEM,1,(byte)3), "Fake-Chest");
	HashMap<Block,Player> list = new HashMap<>();
	TroubleInMinecraft TTT;
	
	public Fake_Chest(TroubleInMinecraft TTT){
		this.TTT=TTT;
		TTT.getBlockPlaceAllow().add(Material.SKULL);
		TTT.getBlockPlaceAllow().add(Material.SKULL_ITEM);
		Bukkit.getPluginManager().registerEvents(this, TTT.getManager().getInstance());
	}
	
	public int getPunkte(){
		return 4;
	}
	
	@EventHandler
	public void set(BlockPlaceEvent ev){
		if(ev.getBlock().getType()==Material.SKULL){
			TTT_Item i = getSkull();
			i.setBlock(ev.getBlock());
			list.put(ev.getBlock(),ev.getPlayer());
		}
	}
	
	public TTT_Item getSkull(){
		TTT_Item i = null;
		switch(UtilMath.RandomInt(6, 0)){
		case 0:i=TTT_Item.SCHWERT_HOLZ;
		case 1:i=TTT_Item.SCHWERT_IRON;
		case 2:i=TTT_Item.SCHWERT_STONE;
		case 3:i=TTT_Item.BOW_BOGEN;
		case 4:i=TTT_Item.BOW_MINIGUN;
		case 5:i=TTT_Item.BOW_SHOTGUN;
		case 6:i=TTT_Item.BOW_SNIPER;
		}
		return i;
	}
	
	@EventHandler
	public void Damage(EntityDamageEvent ev){
		if(ev.getEntity() instanceof Player){
			if(ev.getCause()==DamageCause.BLOCK_EXPLOSION)ev.setDamage(0);
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void Use(PlayerInteractEvent ev){
		if(UtilEvent.isAction(ev, ActionType.R_BLOCK)){
			if(list.containsKey(ev.getClickedBlock())&&ev.getClickedBlock().getState() instanceof Skull){
				if(TTT.getTeam(ev.getPlayer())!=Team.TRAITOR){
					ev.getClickedBlock().setTypeId(0);
					ev.getPlayer().damage(50);
					ev.getClickedBlock().getWorld().createExplosion(ev.getClickedBlock().getLocation(), 1.0F, false);
				}else{
					ev.getPlayer().sendMessage(Text.PREFIX_GAME.getText(TTT.getManager().getTyp().string())+"Diese Chest ist eine Fake-Chest.");
				}
				ev.setCancelled(true);
			}
		}
	}
	
	public ItemStack getShopItem(){
		ItemStack i = UtilItem.RenameItem(new ItemStack(Material.SKULL_ITEM,1,(byte)3), "§cFake-Chest §7("+getPunkte()+" Punkte)");
		UtilItem.SetDescriptions(i, new String[]{
				"§7Wenn man dieses Fake-Item",
				"§7aufnimmt stirbt man sofort."
		});
		return i;
	}

	@Override
	public void add(Player p) {
		p.getInventory().addItem(item);
	}
	
	
}
