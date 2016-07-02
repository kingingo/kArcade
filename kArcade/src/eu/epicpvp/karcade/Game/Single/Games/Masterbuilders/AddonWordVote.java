package eu.epicpvp.karcade.Game.Single.Games.Masterbuilders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

import dev.wolveringer.dataserver.gamestats.GameState;
import dev.wolveringer.dataserver.player.LanguageType;
import eu.epicpvp.kcore.Enum.GameStateChangeReason;
import eu.epicpvp.kcore.Enum.PlayerState;
import eu.epicpvp.kcore.Listener.kListener;
import eu.epicpvp.kcore.Lists.kSort;
import eu.epicpvp.kcore.Translation.TranslationHandler;
import eu.epicpvp.kcore.Update.UpdateType;
import eu.epicpvp.kcore.Update.Event.UpdateEvent;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.kcore.Util.UtilMath;
import eu.epicpvp.kcore.Util.UtilScoreboard;
import lombok.Getter;

public class AddonWordVote extends kListener{

	@Getter
	private Masterbuilders masterbuilders;
	@Getter
	private boolean vote=false;
	private int votetime;
	@Getter
	private HashMap<Player, Buildings> votes;
	private Buildings[] buildings;
	private Scoreboard scoreENG;
	private Scoreboard scoreGER;
	
	public AddonWordVote(Masterbuilders masterbuilders) {
		super(masterbuilders.getManager().getInstance(), "AddonWordVote");
		this.masterbuilders=masterbuilders;
		this.votes=new HashMap<>();
	}

	public void start(){
		this.votetime=26;
		this.vote=true;
		this.votes.clear();
		this.buildings = Buildings.rdmArray(3);
		
		this.scoreGER=Bukkit.getScoreboardManager().getNewScoreboard();
		UtilScoreboard.addBoard(scoreGER, DisplaySlot.SIDEBAR, "§6§lClashMC.eu - Vote §e"+votetime+"sec");
		UtilScoreboard.setScore(scoreGER, " ", DisplaySlot.SIDEBAR, 64);
		UtilScoreboard.setScore(scoreGER, "§e"+buildings[0].getGerman(), DisplaySlot.SIDEBAR, 0);
		UtilScoreboard.setScore(scoreGER, "§e"+buildings[1].getGerman(), DisplaySlot.SIDEBAR, 0);
		UtilScoreboard.setScore(scoreGER, "§e"+buildings[2].getGerman(), DisplaySlot.SIDEBAR, 0);
		UtilScoreboard.setScore(scoreGER, "", DisplaySlot.SIDEBAR, -1);
		
		this.scoreENG=Bukkit.getScoreboardManager().getNewScoreboard();
		UtilScoreboard.addBoard(scoreENG, DisplaySlot.SIDEBAR, "§6§lClashMC.eu - Vote §e"+votetime+" ec");
		UtilScoreboard.setScore(scoreENG, " ", DisplaySlot.SIDEBAR, 64);
		UtilScoreboard.setScore(scoreENG, "§e"+buildings[0].getEnglish(), DisplaySlot.SIDEBAR, 0);
		UtilScoreboard.setScore(scoreENG, "§e"+buildings[1].getEnglish(), DisplaySlot.SIDEBAR, 0);
		UtilScoreboard.setScore(scoreENG, "§e"+buildings[2].getEnglish(), DisplaySlot.SIDEBAR, 0);
		UtilScoreboard.setScore(scoreENG, "", DisplaySlot.SIDEBAR, -1);
		
		for(Player player : getMasterbuilders().getGameList().getPlayers(PlayerState.INGAME)){
			player.getInventory().clear();
			player.getInventory().setItem(2,UtilItem.RenameItem(new ItemStack(Material.PAPER), "§a"+(TranslationHandler.getLanguage(player) == LanguageType.GERMAN ? buildings[0].getGerman() : buildings[0].getEnglish())));
			player.getInventory().setItem(4,UtilItem.RenameItem(new ItemStack(Material.PAPER), "§a"+(TranslationHandler.getLanguage(player) == LanguageType.GERMAN ? buildings[1].getGerman() : buildings[1].getEnglish())));
			player.getInventory().setItem(6,UtilItem.RenameItem(new ItemStack(Material.PAPER), "§a"+(TranslationHandler.getLanguage(player) == LanguageType.GERMAN ? buildings[2].getGerman() : buildings[2].getEnglish())));
			
			if(TranslationHandler.getLanguage(player)==LanguageType.GERMAN){
				player.setScoreboard(scoreGER);
			}else{
				player.setScoreboard(scoreENG);
			}
		}
	}
	
	@EventHandler
	public void update(UpdateEvent ev){
		if(ev.getType()==UpdateType.SEC && isVote()){
			if(votetime<0){
				if(!votes.isEmpty()){
					ArrayList<kSort<String>> sort = new ArrayList<>();
					for(Buildings b : buildings){
						sort.add(new kSort<String>(b.getEnglish(), getVotes(b)));
					}

					Collections.sort(sort,kSort.DESCENDING);
					getMasterbuilders().setBuilding(Buildings.get(sort.get(0).getObject()));
				}else{
					getMasterbuilders().setBuilding(buildings[UtilMath.r(buildings.length)]);
				}
				
				for(Player player : getMasterbuilders().getGameList().getPlayers(PlayerState.INGAME))player.getInventory().clear();
				vote=false;
				getMasterbuilders().setState(GameState.InGame,GameStateChangeReason.CUSTOM);
			}else{
				votetime--;
				scoreENG.getObjective(DisplaySlot.SIDEBAR).setDisplayName("§6§lClashMC.eu - Vote §e"+votetime+"sec");
				scoreGER.getObjective(DisplaySlot.SIDEBAR).setDisplayName("§6§lClashMC.eu - Vote §e"+votetime+"sec");
			}
		}
	}
	
	public int getVotes(Buildings building){
		int i = 0;
		for(Buildings b : votes.values())if(building==b)i++;
		return i;
	}
	
	@EventHandler
	public void vote(PlayerInteractEvent ev){
		if(isVote()){
			if(!votes.containsKey(ev.getPlayer()) && ev.getPlayer().getItemInHand() != null && ev.getPlayer().getItemInHand().getType()==Material.PAPER){
				votes.put(ev.getPlayer(), Buildings.get(ev.getPlayer().getItemInHand().getItemMeta().getDisplayName().replaceAll("§a", "")));
				int v = getVotes(votes.get(ev.getPlayer()));
				
				UtilScoreboard.resetScore(scoreENG, "§e"+votes.get(ev.getPlayer()).getEnglish(), DisplaySlot.SIDEBAR);
				UtilScoreboard.setScore(scoreENG, "§e"+votes.get(ev.getPlayer()).getEnglish(), DisplaySlot.SIDEBAR, v);
				
				UtilScoreboard.resetScore(scoreGER, "§e"+votes.get(ev.getPlayer()).getGerman(), DisplaySlot.SIDEBAR);
				UtilScoreboard.setScore(scoreGER, "§e"+votes.get(ev.getPlayer()).getGerman(), DisplaySlot.SIDEBAR, v);
				
				ev.getPlayer().getInventory().clear();
			}
		}
	}
}
