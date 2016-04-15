package eu.epicpvp.karcade.Game.Single.Addons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import dev.wolveringer.dataserver.gamestats.GameState;
import eu.epicpvp.karcade.Game.Single.SingleGame;
import eu.epicpvp.karcade.Game.Single.Events.AddonVoteTeamPlayerChooseEvent;
import eu.epicpvp.kcore.Enum.PlayerState;
import eu.epicpvp.kcore.Enum.Team;
import eu.epicpvp.kcore.Translation.TranslationHandler;
import eu.epicpvp.kcore.Util.InventorySize;
import eu.epicpvp.kcore.Util.UtilEvent;
import eu.epicpvp.kcore.Util.UtilEvent.ActionType;
import eu.epicpvp.kcore.Util.UtilGear;
import eu.epicpvp.kcore.Util.UtilInv;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.kcore.Util.UtilPlayer;
import eu.epicpvp.kcore.Util.UtilServer;
import lombok.Getter;

public class AddonVoteTeam implements Listener{
	
	@Getter
	private SingleGame game;
	private Inventory inv;
	private Team[] list;
	private int invSize=0;
	private int MaxInTeam=3;
	@Getter
	private HashMap<Team,Integer> invslot = new HashMap<>();
	@Getter
	private HashMap<Player,Team> vote = new HashMap<>();
	
	public AddonVoteTeam(SingleGame game,Team[] list,InventorySize size,int MaxInTeam){
		this.game=game;
		this.list=list;
		this.MaxInTeam=MaxInTeam;
		this.invSize=size.getSize();
		Bukkit.getPluginManager().registerEvents(this, this.game.getManager().getInstance());
	}
	
	@EventHandler
	public void Join(PlayerJoinEvent ev){
		if(game.getState()!=GameState.LobbyPhase)return;
		ev.getPlayer().getInventory().addItem(UtilItem.RenameItem(new ItemStack(Material.PAPER,1), TranslationHandler.getText(ev.getPlayer(), "GAME_TEAM_ITEM")));
	}
	
	@EventHandler
	public void Interact(PlayerInteractEvent ev){
		if(game.getState()!=GameState.LobbyPhase)return;
		if(this.game==null)return;
		if(ev.getPlayer().getItemInHand()==null)return;
		if(UtilEvent.isAction(ev, ActionType.R)&&ev.getPlayer().getItemInHand().getType()==Material.PAPER){
			ev.getPlayer().openInventory(getVoteInv());
			ev.setCancelled(true);
		}
	}
	
	Player p;
	Team old;
	@EventHandler
	public void Inv(InventoryClickEvent ev){
		if(game.getState()!=GameState.LobbyPhase)return;
		if (!(ev.getWhoClicked() instanceof Player)|| ev.getInventory() == null || ev.getCursor() == null || ev.getCurrentItem() == null)return;
		if(ev.getInventory().getName().equalsIgnoreCase("§lVote:")){
			p = (Player)ev.getWhoClicked();
					ev.setCancelled(true);
					p.closeInventory();
					
					if(UtilServer.getPlayers().size()<=game.getMin_Players()){
						UtilPlayer.sendMessage(p,TranslationHandler.getText(p, "PREFIX_GAME",game.getType().getTyp())+TranslationHandler.getText(p, "VOTE_TEAM_MIN_PLAYER",game.getMin_Players()+1));
						return;
					}
					
					if(vote.containsKey(p)){
						UtilPlayer.sendMessage(p,TranslationHandler.getText(p, "PREFIX_GAME",game.getType().getTyp())+TranslationHandler.getText(p, "VOTE_TEAM_REMOVE",vote.get(p).Name()));
						old = vote.get(p);
						vote.remove(p);
						fixItem(old);
					}
					
					for(Team t : list){
						if(UtilGear.isMat(ev.getCurrentItem(), t.getItem().getType())&&ev.getCurrentItem().getItemMeta().hasDisplayName()&&ev.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(t.getItem().getItemMeta().getDisplayName())){
							if(isTeam(t)){
								if(isVotet(t)+1 != UtilServer.getPlayers().size()){
									vote.put(p, t);
									fixItem(t);
									Bukkit.getPluginManager().callEvent(new AddonVoteTeamPlayerChooseEvent(p, t,PlayerState.IN));
									UtilPlayer.sendMessage(p,TranslationHandler.getText(p, "PREFIX_GAME",game.getType().getTyp())+t.getColor()+TranslationHandler.getText(p, "VOTE_TEAM_ADD",t.getColor()+t.Name()));
								}
							}else{
								UtilPlayer.sendMessage(p,TranslationHandler.getText(p, "PREFIX_GAME",game.getType().getTyp())+t.getColor()+TranslationHandler.getText(p, "VOTE_TEAM_FULL",t.getColor()+t.Name()));
							}
							break;
						}
					}
					
					if(!vote.containsKey(p)){
						Bukkit.getPluginManager().callEvent(new AddonVoteTeamPlayerChooseEvent(p, old,PlayerState.OUT));
						old=null;
					}
		}
	}
	
	public int isVotet(Team t){
		int i=0;
		for(Player p : vote.keySet()){
			if(vote.get(p)==t){
				i++;
			}
		}
		return i;
	}
	
	public ItemStack fixItem(Team t){
		int sl = invslot.get(t);
		ItemStack i = inv.getItem(sl);
		List<String> l = new ArrayList<>();
		l.add("§c"+isVotet(t)+"§7/§c"+MaxInTeam);
		l.add("§7---------------");
		inTeam(t, l);
		UtilItem.SetDescriptions(i, l);
		inv.setItem(sl, i);
		return i;
	}
	
	public void inTeam(Team t,List<String> l){
		int i =1;
		for(Player p : vote.keySet()){
			if(vote.get(p)==t){
				l.add("§6"+i+".§7 "+p.getName());
				i++;
			}
		}
	}
	
	public boolean isTeam(Team t){
       if(isVotet(t) < this.MaxInTeam){
    	   return true;
        }
		return false;
	}
	
	public Inventory getVoteInv(){
		if(inv==null){
			inv=Bukkit.createInventory(null, invSize,"§lVote:");
			int slot = 0;
			if(list.length==2){
				slot=2;
				for(Team t: list){
					invslot.put(t, slot);
					inv.setItem(slot, UtilItem.SetDescriptions(t.getItem(), new String[]{"§c0§7/§c"+MaxInTeam}));
					slot=6;
				}
				
			}else if(list.length==4){
				for(Team t : list){
					slot++;
					invslot.put(t, slot);
					inv.setItem(slot, UtilItem.SetDescriptions(t.getItem(), new String[]{"§c0§7/§c"+MaxInTeam}));
					slot++;
				}
				
			}else{
				slot=0;
				for(Team t: list){
					invslot.put(t, slot);
					inv.setItem(slot, UtilItem.SetDescriptions(t.getItem(), new String[]{"§c0§7/§c"+MaxInTeam}));
					slot++;
				}
			}
			
			for(int i = 0 ; i < inv.getSize(); i++){
				if(inv.getItem(i)==null||inv.getItem(i).getType()==Material.AIR){
					if(inv.getItem(i)==null)inv.setItem(i, new ItemStack(Material.IRON_FENCE));
					inv.getItem(i).setType(Material.STAINED_GLASS_PANE);
					inv.getItem(i).setDurability((byte)7);
					ItemMeta im = inv.getItem(i).getItemMeta();
					im.setDisplayName(" ");
					inv.getItem(i).setItemMeta(im);
				}
			}
		}
		
		return inv;
	}
	
	@EventHandler
	public void Quit(PlayerQuitEvent ev){
		if(game.getState()!=GameState.LobbyPhase)return;
		if(getVote().containsKey(ev.getPlayer())){
			Team t = vote.get(ev.getPlayer());
			vote.remove(ev.getPlayer());
			fixItem(t);
		}
	}
	
	@EventHandler
	  public void DropItem(PlayerDropItemEvent event)
	  {
		if(game.getState()!=GameState.LobbyPhase)return;
	    if ((this.game == null)) {
	      return;
	    }
	    if (!UtilInv.IsItem(event.getItemDrop().getItemStack(), Material.PAPER, (byte)0)) {
	      return;
	    }

	    event.setCancelled(true);
	  }
	
}
