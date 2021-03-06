package eu.epicpvp.karcade.Game.Single.Games.TroubleInMinecraft.Shop.Item;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import eu.epicpvp.karcade.Game.Single.Games.TroubleInMinecraft.TTT_Item;
import eu.epicpvp.karcade.Game.Single.Games.TroubleInMinecraft.TroubleInMinecraft;
import eu.epicpvp.karcade.Game.Single.Games.TroubleInMinecraft.Shop.IShop;
import eu.epicpvp.kcore.Enum.PlayerState;
import eu.epicpvp.kcore.Enum.Team;
import eu.epicpvp.kcore.ItemFake.ItemFake;
import eu.epicpvp.kcore.ItemFake.Events.ItemFakePickupEvent;
import eu.epicpvp.kcore.LaunchItem.LaunchItem;
import eu.epicpvp.kcore.LaunchItem.LaunchItemEvent;
import eu.epicpvp.kcore.Translation.TranslationHandler;
import eu.epicpvp.kcore.Util.UtilEvent;
import eu.epicpvp.kcore.Util.UtilEvent.ActionType;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.kcore.Util.UtilMath;
import eu.epicpvp.kcore.Util.UtilPlayer;

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
	public void Launch(final PlayerInteractEvent event){
		if(UtilEvent.isAction(event, ActionType.RIGHT)&&event.getPlayer().getItemInHand().hasItemMeta()&&event.getPlayer().getItemInHand().getItemMeta().hasDisplayName()){
			if(event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase("Fake-Chest")){
				event.setCancelled(true);
				LaunchItem item = new LaunchItem(event.getPlayer(),4,new LaunchItem.LaunchItemEventHandler(){
					@Override
					public void onLaunchItem(LaunchItemEvent ev) {
						TTT_Item i = getSkull();
						ItemFake k = i.setItemFake(ev.getItem().getDroppedItem()[0].getLocation());
						list.put(k, ev.getItem().getPlayer());
					}
				});
				TTT.getIlManager().LaunchItem(item);
			}
		}
	}
	
//	@EventHandler
//	public void set(BlockPlaceEvent ev){
//		if(ev.getBlock().getType()==Material.SKULL){
//			TTT_Item i = getSkull();
//			ev.getBlock().setTypeId(0);
//			ItemFake k = i.setItemFake(ev.getBlock().getLocation(),TTT.getManager().getInstance());
//			list.put(k,ev.getPlayer());
//		}
//	}
	
	public TTT_Item getSkull(){
		TTT_Item i = null;
		switch(UtilMath.RandomInt(6, 0)){
		case 0:i=TTT_Item.SCHWERT_HOLZ;
		case 1:i=TTT_Item.SCHWERT_IRON;
		case 2:i=TTT_Item.SCHWERT_STONE;
		//case 3:i=TTT_Item.BOW_BOGEN;
		case 4:i=TTT_Item.BOW_MINIGUN;
		case 5:i=TTT_Item.BOW_SHOTGUN;
		case 6:i=TTT_Item.BOW_SNIPER;
		}
		return i;
	}
	
	public TTT_Item getItemFake(Item item){
		if(!item.getItemStack().hasItemMeta())return null;
		if(!item.getItemStack().getItemMeta().hasDisplayName())return null;
		
		switch(item.getItemStack().getItemMeta().getDisplayName()){
		case "§7Holzschwert":return TTT_Item.SCHWERT_HOLZ;
		case "§8Steinschwert":return TTT_Item.SCHWERT_STONE;
		case "§bEisenschwert":return TTT_Item.SCHWERT_IRON;
		case "§7Pfeile":return TTT_Item.ARROW;
		//case "§7Bogen":return TTT_Item.BOW_BOGEN;
		case "§cMinigun":return TTT_Item.BOW_MINIGUN;
		case "§aShotgun":return TTT_Item.BOW_SHOTGUN;
		case "§eSniper":return TTT_Item.BOW_SNIPER;
		default:
			return null;
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
		if(TTT.getGameList().isPlayerState(ev.getPlayer())==PlayerState.SPECTATOR)return;
		if(TTT.getTeam(ev.getPlayer())==Team.TRAITOR){
			UtilPlayer.sendMessage(ev.getPlayer(),TranslationHandler.getText(ev.getPlayer(), "PREFIX_GAME", TTT.getType().getTyp())+"Dieses Item ist ein Fake-Item.");
			ev.setCancelled(true);
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
					ev.getPlayer().damage(50);
					ev.getItemfake().getLocation().getWorld().createExplosion(ev.getItemfake().getLocation(), 1.0F, false);
					ev.getItemfake().remove();
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
					ev.getPlayer().damage(50);
					ev.getItemfake().getLocation().getWorld().createExplosion(ev.getItemfake().getLocation(), 1.0F, false);
					ev.getItemfake().remove();
			}
		}
	}
	
	public ItemStack getShopItem(){
		ItemStack i = UtilItem.RenameItem(new ItemStack(Material.SKULL_ITEM,1,(byte)3), "§cFake-Chest §7("+getPunkte()+" Punkte)");
		UtilItem.setLore(i, new String[]{
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
