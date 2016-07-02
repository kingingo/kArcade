package eu.epicpvp.karcade.Game.Single.Games.TroubleInMinecraft.Shop.Item;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import eu.epicpvp.karcade.Game.Events.TeamAddEvent;
import eu.epicpvp.karcade.Game.Single.Games.TroubleInMinecraft.TroubleInMinecraft;
import eu.epicpvp.karcade.Game.Single.Games.TroubleInMinecraft.Shop.IShop;
import eu.epicpvp.kcore.Enum.PlayerState;
import eu.epicpvp.kcore.Enum.Team;
import eu.epicpvp.kcore.NPC.NPC;
import eu.epicpvp.kcore.NPC.Event.PlayerInteractNPCEvent;
import eu.epicpvp.kcore.Translation.TranslationHandler;
import eu.epicpvp.kcore.Update.UpdateType;
import eu.epicpvp.kcore.Update.Event.UpdateEvent;
import eu.epicpvp.kcore.Util.UtilInv;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.kcore.Util.UtilPlayer;
import eu.epicpvp.kcore.Util.UtilServer;
import lombok.Getter;

public class Defibrillator implements Listener,IShop{

	ItemStack item=UtilItem.RenameItem(new ItemStack(Material.REDSTONE), "§7Defibrillator");
	TroubleInMinecraft TTT;
	@Getter
	HashMap<String,Team> teams = new HashMap<>();
	ArrayList<Player> l = new ArrayList<>();
	
	public Defibrillator(TroubleInMinecraft TTT){
		this.TTT=TTT;
		Bukkit.getPluginManager().registerEvents(this, TTT.getManager().getInstance());
	}
	
	@Override
	public int getPunkte() {
		return 3;
	}

	@Override
	public ItemStack getShopItem() {
		ItemStack i = UtilItem.RenameItem(new ItemStack(Material.REDSTONE,1), "§cDefibrillator §7("+getPunkte()+" Punkte)");
		UtilItem.setLore(i, new String[]{
				"§7Belebt einen Toten Spielerwieder."
		});
		return i;
	}

	@EventHandler
	public void AddTeam(TeamAddEvent ev){
		if(teams.containsKey(ev.getPlayer().getName().toLowerCase()));
		teams.put(ev.getPlayer().getName().toLowerCase(), ev.getTeam());
	}
	
	@Override
	public void add(Player p) {
		p.getInventory().addItem(item.clone());
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void Place(BlockPlaceEvent ev){
		if(ev.getBlock().getType()==item.getType())ev.setCancelled(true);
	}
	
	@EventHandler
	public void Update(UpdateEvent ev){
		if(UpdateType.SEC!=ev.getType())return;
		if(l.isEmpty())return;
		
		for(Player r : l){
			TTT.getManager().Clear(r);
			r.setGameMode(GameMode.SURVIVAL);
			r.setMaxHealth(40);
			r.setHealth(40);
			r.getInventory().addItem(TTT.getMagnet().getStab());
			for(Player a : UtilServer.getPlayers()){
				a.showPlayer(r);
			}
			for(Player a1 : TTT.getGameList().getPlayers(PlayerState.INGAME)){
				r.showPlayer(a1);
			}
			for(Player a1 : TTT.getGameList().getPlayers(PlayerState.SPECTATOR)){
				r.hidePlayer(a1);
			}
			Team t = teams.get(r.getName().toLowerCase());
			TTT.addTeam(r, t);
			TTT.getGameList().addPlayer(r, PlayerState.INGAME);
		}
		
	}
	
	@EventHandler
	public void Interact(PlayerInteractNPCEvent ev){
		Player p = ev.getPlayer();
		if(p.getItemInHand()==null||!UtilItem.ItemNameEquals(p.getItemInHand(), item))return;
		NPC npc = ev.getNpc();
		
		if(!TTT.getNpclist().containsKey(npc.getEntityID())){
			String name = npc.getName();
			Player r=null;
			for(Player p1 : TTT.getGameList().getPlayers(PlayerState.SPECTATOR)){
				if(p1.getName().equalsIgnoreCase(name)){
					r=p1;
					continue;
				}
			}
			
			if(r!=null){
				UtilInv.remove(p, p.getItemInHand().getType(), p.getItemInHand().getData().getData(), 1);
				l.add(r);
				npc.despawn();
				UtilPlayer.sendMessage(p,TranslationHandler.getText(ev.getPlayer(), "PREFIX_GAME", TTT.getType().getTyp())+TranslationHandler.getText(ev.getPlayer(), "TTT_DETECTIVE_SHOP_DEFIBRILLATOR_WIEDERBELEBT", r.getName()));
				UtilPlayer.sendMessage(r,TranslationHandler.getText(r, "PREFIX_GAME", TTT.getType().getTyp())+TranslationHandler.getText(r, "TTT_DETECTIVE_SHOP_DEFIBRILLATOR_WIEDERBELEBT", p.getName()));
			}else{
				UtilPlayer.sendMessage(p,TranslationHandler.getText(ev.getPlayer(), "PREFIX_GAME", TTT.getType().getTyp())+TranslationHandler.getText(ev.getPlayer(), "TTT_DETECTIVE_SHOP_DEFIBRILLATOR_DEATH"));
			}
		}else{
			UtilPlayer.sendMessage(p,TranslationHandler.getText(ev.getPlayer(), "PREFIX_GAME", TTT.getType().getTyp())+TranslationHandler.getText(ev.getPlayer(), "TTT_DETECTIVE_SHOP_DEFIBRILLATOR_DEATH"));
		}
		
	}

}
