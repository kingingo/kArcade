package me.kingingo.karcade.Game.Games.TroubleInMinecraft.Shop.Item;

import java.util.HashMap;

import me.kingingo.karcade.Enum.Team;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.TTT_Item;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.TroubleInMinecraft;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.Shop.IShop;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.ItemFake.ItemFake;
import me.kingingo.kcore.ItemFake.Events.ItemFakePickupEvent;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilMath;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class Fake_Chest implements Listener,IShop {

	ItemStack item = UtilItem.RenameItem(new ItemStack(Material.SKULL_ITEM,1,(byte)3), "Fake-Chest");
	HashMap<ItemFake,Player> list = new HashMap<>();
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
			ev.setCancelled(true);
			ev.getPlayer().getItemInHand().setType(Material.AIR);
			ItemFake k = i.setItemFake(ev.getBlock().getLocation(),TTT.getManager().getInstance());
			list.put(k,ev.getPlayer());
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
	
	public TTT_Item getItemFake(Item item){
//		if(b.getState() instanceof Skull){
//			Skull s = (Skull)b.getState();
//			if(!s.hasOwner())return null;
//			switch(s.getOwner()){
//			case "VareidePlays": return TTT_Item.SCHWERT_HOLZ;
//			case "Nottrex": return TTT_Item.SCHWERT_STONE;
//			case "BillTheBuild3r": return TTT_Item.SCHWERT_IRON;
//			
//			case "KlausurThaler144":return TTT_Item.BOW_MINIGUN;
//			case "IntelliJ":return TTT_Item.BOW_SHOTGUN;
//			case "Abmahnung":return TTT_Item.BOW_BOGEN;
//			case "FallingDiamond":return TTT_Item.BOW_SNIPER;
//			}
//		}
//		return null;
		
		switch(item.getItemStack().getItemMeta().getDisplayName()){
		case "Holzschwert":return TTT_Item.SCHWERT_HOLZ;
		case "Steinschwert":return TTT_Item.SCHWERT_STONE;
		case "Eisenschwert":return TTT_Item.SCHWERT_IRON;
		case "Bogen":return TTT_Item.BOW_BOGEN;
		case "§cMinigun":return TTT_Item.BOW_MINIGUN;
		case "§aShotgun":return TTT_Item.BOW_SHOTGUN;
		case "§eSniper":return TTT_Item.BOW_SNIPER;
		default:
			return TTT_Item.SCHWERT_HOLZ;
		}
	}
	
	@EventHandler
	public void Damage(EntityDamageEvent ev){
		if(ev.getEntity() instanceof Player){
			if(ev.getCause()==DamageCause.BLOCK_EXPLOSION)ev.setDamage(0);
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void PickupItemFake(ItemFakePickupEvent ev){
		if(!list.containsKey(ev.getItemfake()))return;
		if(TTT.getTeam(ev.getPlayer())==Team.TRAITOR){
			ev.getPlayer().sendMessage(Text.PREFIX_GAME.getText(TTT.getManager().getTyp().string())+"Dieses Item ist ein Fake-Item.");
			return;
		}
		TTT_Item t = getItemFake(ev.getItem());
		boolean b = false;
		
		if(t.getTyp().equalsIgnoreCase("SCHWERT")){
			for(ItemStack i : ev.getPlayer().getInventory()){
				if(i==null||i.getType()==Material.AIR)continue;
				if(i.getType()==Material.WOOD_SWORD){
					b=true;
					break;
				}else if(i.getType()==Material.STONE_SWORD){
					b=true;
					break;
				}else if(i.getType()==Material.IRON_SWORD){
					b=true;
					break;
				}
			}
			
			if(!b){
					ev.getItemfake().remove();
					ev.getPlayer().damage(50);
					ev.getItemfake().getLocation().getWorld().createExplosion(ev.getItemfake().getLocation(), 1.0F, false);
			}
		}else if(t.getTyp().equalsIgnoreCase("BOW")){
			for(ItemStack i : ev.getPlayer().getInventory()){
				if(i==null||i.getType()==Material.AIR)continue;
				if(i.getType()==Material.BOW){
					b=true;
					break;
				}
			}
			
			if(!b){
					ev.getItemfake().remove();
					ev.getPlayer().damage(50);
					ev.getItemfake().getLocation().getWorld().createExplosion(ev.getItemfake().getLocation(), 1.0F, false);
			}
		}
	}
	
//	@EventHandler(priority=EventPriority.LOWEST)
//	public void Use(PlayerInteractEvent ev){
//		if(UtilEvent.isAction(ev, ActionType.R_BLOCK)){
//			if(list.containsKey(ev.getClickedBlock())&&ev.getClickedBlock().getState() instanceof Skull){
//				if(TTT.getTeam(ev.getPlayer())!=Team.TRAITOR){
//					ev.getClickedBlock().setTypeId(0);
//					ev.getPlayer().damage(50);
//					ev.getClickedBlock().getWorld().createExplosion(ev.getClickedBlock().getLocation(), 1.0F, false);
//				}else{
//					ev.getPlayer().sendMessage(Text.PREFIX_GAME.getText(TTT.getManager().getTyp().string())+"Diese Chest ist eine Fake-Chest.");
//				}
//				ev.setCancelled(true);
//			}
//		}
//	}
	
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
