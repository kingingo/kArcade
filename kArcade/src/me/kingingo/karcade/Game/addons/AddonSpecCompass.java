package me.kingingo.karcade.Game.addons;

import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Enum.PlayerState;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Util.C;
import me.kingingo.kcore.Util.F;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.UtilGear;
import me.kingingo.kcore.Util.UtilInv;
import me.kingingo.kcore.Util.UtilPlayer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AddonSpecCompass implements Listener{
	
	public kArcadeManager Manager;
	public Inventory inv;

	public AddonSpecCompass(kArcadeManager manager) {
		Bukkit.getPluginManager().registerEvents(this, manager.getInstance());
		this.Manager=manager;
	}
	
	public ItemStack getCompassItem(){
		ItemStack i = new ItemStack(Material.COMPASS);
		ItemMeta im = i.getItemMeta();
		im.setDisplayName("§bCompass");
		i.setItemMeta(im);
		return i;
	}

	@EventHandler
	public void Interact(PlayerInteractEvent ev){
		if(!this.Manager.getGame().isCompassAddon()||this.Manager.getGame()==null||this.Manager.getState()==GameState.LobbyPhase)return;
		if(ev.getPlayer().getItemInHand()==null)return;
		if(UtilEvent.isAction(ev, ActionType.R)&&ev.getPlayer().getItemInHand().getType()==Material.COMPASS&&Manager.getGame().getGameList().getPlayers(PlayerState.OUT).contains(ev.getPlayer())){
			ev.getPlayer().openInventory(getCompassInv());
			ev.setCancelled(true);
		}
	}
	
	@EventHandler
	  public void DropItem(PlayerDropItemEvent event)
	  {
	    if ((this.Manager.getGame() == null) || (!this.Manager.getGame().CompassAddon)) {
	      return;
	    }
	    if (!UtilInv.IsItem(event.getItemDrop().getItemStack(), Material.COMPASS, (byte)0)) {
	      return;
	    }

	    event.setCancelled(true);

	    UtilPlayer.message(event.getPlayer(), F.main("Game", "You cannot drop " + F.item("Target Compass") + "."));
	  }
	
	@EventHandler
	public void Inv(InventoryClickEvent ev){
		if (!Manager.getGame().isCompassAddon()||!(ev.getWhoClicked() instanceof Player)|| ev.getInventory() == null || ev.getCursor() == null || ev.getCurrentItem() == null)return;
		if(ev.getInventory().getName().equalsIgnoreCase(C.Bold+"Teleporter")){
			Player p = (Player)ev.getWhoClicked();
			if(UtilGear.isMat(p.getItemInHand(), Material.COMPASS)){
				if(Manager.getGame().getGameList().isPlayerState(p)==PlayerState.OUT){
					if(UtilGear.isMat(ev.getCurrentItem(), Material.SKULL_ITEM)){
						for(Player player : Manager.getGame().getGameList().getPlayers(PlayerState.IN)){
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
		if(inv==null)inv=Bukkit.createInventory(null, 27,C.Bold+"Teleporter");
		inv.clear();
		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
		ItemMeta meta;
		for(Player p : this.Manager.getGame().getGameList().getPlayers(PlayerState.IN)){
			meta=skull.getItemMeta();
			meta.setDisplayName(p.getName());
			skull.setItemMeta(meta);
			inv.addItem(skull);
		}
		return inv;
	}
	
}
