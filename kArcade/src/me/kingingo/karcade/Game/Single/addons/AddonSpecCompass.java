package me.kingingo.karcade.Game.Single.addons;

import me.kingingo.karcade.Enum.PlayerState;
import me.kingingo.karcade.Game.Single.SingleGame;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.UtilGear;
import me.kingingo.kcore.Util.UtilInv;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AddonSpecCompass implements Listener{
	
	public SingleGame game;
	public Inventory inv;

	public AddonSpecCompass(SingleGame game) {
		Bukkit.getPluginManager().registerEvents(this, game.getManager().getInstance());
		this.game=game;
	}
	
	public ItemStack getCompassItem(){
		ItemStack i = new ItemStack(Material.COMPASS);
		ItemMeta im = i.getItemMeta();
		im.setDisplayName("§bCompass");
		i.setItemMeta(im);
		return i;
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	public void Interact(PlayerInteractEvent ev){
		if(game==null||!this.game.isCompassAddon()||game.getState()==GameState.LobbyPhase)return;
		if(ev.getPlayer().getItemInHand()==null)return;
		if(UtilEvent.isAction(ev, ActionType.R)&&ev.getPlayer().getItemInHand().getType()==Material.COMPASS&&game.getGameList().getPlayers(PlayerState.OUT).contains(ev.getPlayer())){
			ev.getPlayer().openInventory(getCompassInv());
			ev.setCancelled(true);
		}
	}
	
	@EventHandler
	  public void DropItem(PlayerDropItemEvent event)
	  {
	    if ((game == null) || (!game.CompassAddon)) {
	      return;
	    }
	    if (!UtilInv.IsItem(event.getItemDrop().getItemStack(), Material.COMPASS, (byte)0)) {
	      return;
	    }

	    event.setCancelled(true);
	  }
	
	@EventHandler
	public void Inv(InventoryClickEvent ev){
		if (!game.isCompassAddon()||!(ev.getWhoClicked() instanceof Player)|| ev.getInventory() == null || ev.getCursor() == null || ev.getCurrentItem() == null)return;
		if(ev.getInventory().getName().equalsIgnoreCase("§lTeleporter")){
			Player p = (Player)ev.getWhoClicked();
			if(UtilGear.isMat(p.getItemInHand(), Material.COMPASS)){
				if(game.getGameList().isPlayerState(p)==PlayerState.OUT){
					if(UtilGear.isMat(ev.getCurrentItem(), Material.SKULL_ITEM)){
						for(Player player : game.getGameList().getPlayers(PlayerState.IN)){
							if(player.getName().equalsIgnoreCase(ev.getCurrentItem().getItemMeta().getDisplayName())){
								p.teleport(player.getLocation().add(0,4,0));
								break;
							}
						}
					}
				}
			}
		}
	}
	
	public Inventory getCompassInv(){
		if(inv==null)inv=Bukkit.createInventory(null, 27,"§lTeleporter");
		inv.clear();
		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
		ItemMeta meta;
		for(Player p : game.getGameList().getPlayers(PlayerState.IN)){
			meta=skull.getItemMeta();
			meta.setDisplayName(p.getName());
			skull.setItemMeta(meta);
			inv.addItem(skull);
		}
		return inv;
	}
	
}
