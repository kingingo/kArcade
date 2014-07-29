package me.kingingo.karcade.Game.addons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.Getter;
import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Enum.Team;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.Util.C;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.InventorySize;
import me.kingingo.kcore.Util.UtilGear;
import me.kingingo.kcore.Util.UtilInv;
import me.kingingo.kcore.Util.UtilItem;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AddonVoteTeam implements Listener{
	
	private kArcadeManager Manager;
	private Inventory inv;
	private Team[] list;
	private int invSize=0;
	private int MaxInTeam=3;
	@Getter
	private HashMap<Team,Integer> invslot = new HashMap<>();
	@Getter
	private HashMap<Player,Team> vote = new HashMap<>();
	
	public AddonVoteTeam(kArcadeManager manager,Team[] list,InventorySize size,int MaxInTeam){
		this.Manager=manager;
		this.list=list;
		this.MaxInTeam=MaxInTeam;
		this.invSize=size.getSize();
		Bukkit.getPluginManager().registerEvents(this, this.Manager.getInstance());
	}
	
	@EventHandler
	public void Join(PlayerJoinEvent ev){
		if(Manager.getState()!=GameState.LobbyPhase)return;
		ev.getPlayer().getInventory().addItem(UtilItem.RenameItem(new ItemStack(Material.PAPER,1), C.cDAqua+"Wähle dein Team"));
	}
	
	@EventHandler
	public void Interact(PlayerInteractEvent ev){
		if(Manager.getState()!=GameState.LobbyPhase)return;
		if(this.Manager.getGame()==null)return;
		if(ev.getPlayer().getItemInHand()==null)return;
		if(UtilEvent.isAction(ev, ActionType.R)&&ev.getPlayer().getItemInHand().getType()==Material.PAPER){
			ev.getPlayer().openInventory(getVoteInv());
			ev.setCancelled(true);
		}
	}
	
	@EventHandler
	public void Inv(InventoryClickEvent ev){
		if(Manager.getState()!=GameState.LobbyPhase)return;
		if (!(ev.getWhoClicked() instanceof Player)|| ev.getInventory() == null || ev.getCursor() == null || ev.getCurrentItem() == null)return;
		if(ev.getInventory().getName().equalsIgnoreCase(C.Bold+"Vote:")){
			Player p = (Player)ev.getWhoClicked();
					ev.setCancelled(true);
					p.closeInventory();
					
					if(vote.containsKey(p)){
						p.sendMessage(Text.PREFIX_GAME.getText(Manager.getTyp().string())+Text.VOTE_TEAM_REMOVE.getText(vote.get(p).Name()));
						Team t = vote.get(p);
						vote.remove(p);
						fixItem(t);
					}
					
					for(Team t : list){
						if(UtilGear.isMat(ev.getCurrentItem(), t.getItem().getType())&&ev.getCurrentItem().getItemMeta().hasDisplayName()&&ev.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(t.getItem().getItemMeta().getDisplayName())){
							if(isTeam(t)){
								vote.put(p, t);
								fixItem(t);
								p.sendMessage(Text.PREFIX_GAME.getText(Manager.getTyp().string())+t.getColor()+Text.VOTE_TEAM_ADD.getText(t.Name()));
							}else{
								p.sendMessage(Text.PREFIX_GAME.getText(Manager.getTyp().string())+t.getColor()+Text.VOTE_TEAM_FULL.getText(t.Name()));
							}
							break;
						}
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
		ItemStack i = inv.getItem(invslot.get(t));
		List<String> l = new ArrayList<>();
		l.add("§c"+isVotet(t)+"§7/§c"+MaxInTeam);
		l.add("§7---------------");
		inTeam(t, l);
		UtilItem.SetDescriptions(i, l);
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
			inv=Bukkit.createInventory(null, invSize,C.Bold+"Vote:");
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
					inv.getItem(i).setType(Material.IRON_FENCE);
					ItemMeta im = inv.getItem(i).getItemMeta();
					im.setDisplayName(" ");
					inv.getItem(i).setItemMeta(im);
				}
			}
		}
		
		return inv;
	}
	
	@EventHandler
	  public void DropItem(PlayerDropItemEvent event)
	  {
		if(Manager.getState()!=GameState.LobbyPhase)return;
	    if ((this.Manager.getGame() == null)) {
	      return;
	    }
	    if (!UtilInv.IsItem(event.getItemDrop().getItemStack(), Material.PAPER, (byte)0)) {
	      return;
	    }

	    event.setCancelled(true);
	  }
	
}
