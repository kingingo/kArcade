package me.kingingo.karcade.Game.Games.TroubleInMinecraft.Detective.Item;

import java.util.ArrayList;
import java.util.HashMap;

import me.kingingo.karcade.Enum.PlayerState;
import me.kingingo.karcade.Enum.Team;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.TroubleInMinecraft;
import me.kingingo.karcade.Game.Games.TroubleInMinecraft.Traitor.Shop;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.NPC.NPC;
import me.kingingo.kcore.NPC.Event.PlayerInteractNPCEvent;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilServer;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class Defibrillator implements Listener,Shop{

	ItemStack item=UtilItem.RenameItem(new ItemStack(Material.REDSTONE), "§7Defibrillator");
	TroubleInMinecraft TTT;
	HashMap<Player,Team> teams = new HashMap<>();
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
		UtilItem.SetDescriptions(i, new String[]{
				"§7Belebt einen Toten Spielerwieder."
		});
		return i;
	}

	@Override
	public void add(Player p) {
		p.getInventory().addItem(item.clone());
	}
	
	@EventHandler
	public void Update(UpdateEvent ev){
		if(UpdateType.SEC!=ev.getType())return;
		if(l.isEmpty())return;
		
		for(Player r : l){
			TTT.getGameList().addPlayer(r, PlayerState.IN);
			TTT.getManager().Clear(r);
			r.setGameMode(GameMode.SURVIVAL);
			for(Player a : UtilServer.getPlayers()){
				a.showPlayer(r);
			}
			for(Player a1 : TTT.getGameList().getPlayers(PlayerState.IN)){
				r.showPlayer(a1);
			}
			for(Player a1 : TTT.getGameList().getPlayers(PlayerState.OUT)){
				r.hidePlayer(a1);
			}
			Team t = teams.get(r);
			TTT.addTeam(r, t);
		}
		
	}
	
	@EventHandler
	public void Interact(PlayerInteractNPCEvent ev){
		Player p = ev.getPlayer();
		if(p.getItemInHand()==null||!UtilItem.ItemNameEquals(p.getItemInHand(), item))return;
		NPC npc = ev.getNpc();
		
		if(!npc.getName().equalsIgnoreCase("Unidentifiziert")){
			String name = npc.getName();
			npc.remove();
			Player r=null;
			for(Player p1 : TTT.getGameList().getPlayers(PlayerState.OUT)){
				if(p1.getName().equalsIgnoreCase(name)){
					r=p1;
					continue;
				}
			}
			
			if(r!=null){
				p.getInventory().remove(p.getItemInHand());
				l.add(r);
				p.sendMessage(Text.PREFIX_GAME.getText(TTT.getManager().getTyp().string())+Text.TTT_DETECTIVE_SHOP_DEFIBRILLATOR_WIEDERBELEBT.getText(r.getName()));
				r.sendMessage(Text.PREFIX_GAME.getText(TTT.getManager().getTyp().string())+Text.TTT_DETECTIVE_SHOP_DEFIBRILLATOR_WIEDERBELEBTER.getText(p.getName()));
			}
		}else{
			p.sendMessage(Text.PREFIX_GAME.getText(TTT.getManager().getTyp().string())+Text.TTT_DETECTIVE_SHOP_DEFIBRILLATOR_DEATH.getText());
		}
		
	}

}
