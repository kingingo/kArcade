package eu.epicpvp.karcade.Game.Single.Games.CustomWars.SheepWars;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;

import eu.epicpvp.datenserver.definitions.dataserver.gamestats.GameState;
import eu.epicpvp.datenserver.definitions.dataserver.gamestats.GameType;
import eu.epicpvp.datenserver.definitions.dataserver.gamestats.StatsKey;
import eu.epicpvp.karcade.ArcadeManager;
import eu.epicpvp.karcade.Game.Events.GameStartEvent;
import eu.epicpvp.karcade.Game.Multi.Games.CustomWars1vs1.SheepWars1vs1.UtilSheepWars1vs1;
import eu.epicpvp.karcade.Game.Single.Events.AddonEntityTeamKingDeathEvent;
import eu.epicpvp.karcade.Game.Single.Games.CustomWars.CustomWars;
import eu.epicpvp.karcade.Game.Single.Games.CustomWars.CustomWarsType;
import eu.epicpvp.kcore.Enum.Team;
import eu.epicpvp.kcore.Enum.Zeichen;
import eu.epicpvp.kcore.Kit.Kit;
import eu.epicpvp.kcore.Kit.Perk;
import eu.epicpvp.kcore.Kit.Shop.SingleKitShop;
import eu.epicpvp.kcore.Merchant.MerchantOffer;
import eu.epicpvp.kcore.Translation.TranslationHandler;
import eu.epicpvp.kcore.Util.InventorySize;
import eu.epicpvp.kcore.Util.Title;
import eu.epicpvp.kcore.Util.UtilEvent;
import eu.epicpvp.kcore.Util.UtilEvent.ActionType;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.kcore.Util.UtilScoreboard;
import eu.epicpvp.kcore.Util.UtilServer;
import eu.epicpvp.kcore.Villager.Event.VillagerAddShopEvent;
import lombok.Getter;

public class SheepWars extends CustomWars{

	private SingleKitShop kitshop;
	@Getter
	private HashMap<Player, String> kits = new HashMap();

	public SheepWars(ArcadeManager manager, CustomWarsType customType) {
		super(manager, GameType.SheepWars, customType);

		kitshop=new SingleKitShop(getManager().getInstance(),getMoney(), getManager().getPermManager(), "Kit-Shop", InventorySize._27, UtilSheepWars1vs1.getKits(this));
	}

	@EventHandler
	public void sheepDeath(AddonEntityTeamKingDeathEvent ev){
		UtilScoreboard.resetScore(getScoreboard(), "§a§l"+Zeichen.BIG_HERZ.getIcon()+" "+ev.getTeam().getColor()+ev.getTeam().getDisplayName(), DisplaySlot.SIDEBAR);
		UtilScoreboard.setScore(getScoreboard(), "§4§l"+Zeichen.MAHLZEICHEN_FETT.getIcon()+" "+ev.getTeam().getColor()+ev.getTeam().getDisplayName(), DisplaySlot.SIDEBAR, getAllPlayersFromTeam(ev.getTeam()).size());
		getTeams().remove(ev.getTeam());
		getTeams().put(ev.getTeam(), false);
		Title t = new Title("","");
		if(ev.getKiller()!=null){
			getStats().addInt(ev.getKiller(),1, StatsKey.SHEEPWARS_KILLED_SHEEPS);
		}

		for(Player player : UtilServer.getPlayers()){
			t.setSubtitle(TranslationHandler.getText(player,"SHEEPWARS_SHEEP_DEATH", ev.getTeam().getColor()+"§l"+ev.getTeam().getDisplayName()));
			t.send(player);
		}
	}

	HashMap<Team,ArrayList<Block>> block = new HashMap<>();
	@EventHandler(priority=EventPriority.HIGHEST)
	public void Break(BlockBreakEvent ev){
		if(getState()==GameState.LobbyPhase){
			ev.setCancelled(true);
			return;
		}
		if(ev.getBlock().getType()==Material.GLOWSTONE){
			Team t = getTeam(ev.getPlayer());
			if(block.containsKey(t)){
				if(block.get(t).contains(ev.getBlock())){
					block.get(t).remove(ev.getBlock());
					ev.getBlock().getWorld().dropItem(ev.getBlock().getLocation().add(0,0.1,0),new ItemStack(Material.GLOWSTONE,1));
					ev.getBlock().setTypeId(0);
				}
			}
			ev.setCancelled(true);
		}
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	public void start(GameStartEvent ev){
		HashMap<Player,String> l= new HashMap<>();
		for(Player p : getTeamList().keySet()){
			l.put(p, getTeamList().get(p).getColor());
		}

		for(Kit kit : kitshop.getKits()){
			kit.StartGame(l);
			for(Perk perk : kit.getPlayers().keySet()){
				for(Player p : kit.getPlayers().get(perk)){
					kits.put(p, kit.getName());
				}
				break;
			}
		}
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	public void Place(BlockPlaceEvent ev){
		if(getState()==GameState.LobbyPhase){
			ev.setCancelled(true);
			return;
		}
		if(ev.getBlock().getType()==Material.GLOWSTONE){
			if(!block.containsKey(getTeam(ev.getPlayer())))block.put(getTeam(ev.getPlayer()), new ArrayList<Block>());
			block.get(getTeam(ev.getPlayer())).add(ev.getBlock());
		}
	}

	@EventHandler
	public void addShop(VillagerAddShopEvent ev){
		if(ev.getItemStack().hasItemMeta()&&ev.getItemStack().getItemMeta().hasDisplayName()){
			if(ev.getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase("§cBlöcke")){
				ev.getMerchant().addOffer(new MerchantOffer(Bronze(3), new ItemStack(Material.GLOWSTONE)));
			}
		}
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	public void Explode (EntityExplodeEvent ev){
		for(Block b : ev.blockList()){
			if(b.getType()==Material.GLOWSTONE){
				b.setTypeId(0);
			}
		}
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	public void JoinHologram(PlayerJoinEvent ev){
		if(getState()!=GameState.LobbyPhase)return;
		ev.getPlayer().getInventory().addItem(UtilItem.RenameItem(new ItemStack(Material.CHEST), "§bKitShop"));
	}

	@EventHandler
	public void ShopOpen(PlayerInteractEvent ev){
		if(UtilEvent.isAction(ev, ActionType.RIGHT)){
			if(getState()!=GameState.LobbyPhase)return;
			if(ev.getPlayer().getItemInHand()!=null&&UtilItem.ItemNameEquals(ev.getPlayer().getItemInHand(), UtilItem.RenameItem(new ItemStack(Material.CHEST), "§bKitShop"))){
				ev.getPlayer().openInventory(kitshop.getInventory());
			}
		}
	}

}
