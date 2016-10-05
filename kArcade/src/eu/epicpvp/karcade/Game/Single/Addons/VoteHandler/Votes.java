package eu.epicpvp.karcade.Game.Single.Addons.VoteHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import eu.epicpvp.datenclient.client.Callback;
import eu.epicpvp.kcore.Inventory.InventoryPageBase;
import eu.epicpvp.kcore.Inventory.Item.Click;
import eu.epicpvp.kcore.Inventory.Item.Buttons.ButtonBase;
import eu.epicpvp.kcore.Lists.kSort;
import eu.epicpvp.kcore.Util.InventorySize;
import eu.epicpvp.kcore.Util.UtilEvent.ActionType;
import eu.epicpvp.kcore.Util.UtilInv;
import eu.epicpvp.kcore.Util.UtilItem;
import lombok.Getter;
import lombok.Setter;

public class Votes {

	@Getter
	@Setter
	private Vote[] votes;
	@Getter
	private int priority;
	private InventoryPageBase page;
	private HashMap<Player,Vote> players;
	private Callback<ArrayList<kSort<Vote>>> done;
	@Getter
	private ItemStack voteItem;

	public Votes(Vote[] votes,ItemStack voteItem,Callback<ArrayList<kSort<Vote>>> done, int priority){
		this.priority=priority;
		this.votes=votes;
		this.players=new HashMap<>();
		this.done=done;
		this.voteItem=voteItem;
	}

	public void open(Player player){
		player.openInventory(page);
	}

	public ArrayList<kSort<Vote>> getVoteList(){
		ArrayList<kSort<Vote>> votes = new ArrayList<>();
		for(Vote vote : this.votes)votes.add(new kSort(vote,vote.getVotes()));
		Collections.sort(votes,kSort.DESCENDING);
		return votes;
	}

	public Vote done(){
		ArrayList<kSort<Vote>> votes = getVoteList();
		done.call(votes, null);
		if(votes.size() == 0){
			return null;
		}
		return votes.get(0).getObject();
	}

	public void init(){
		this.page = new InventoryPageBase(InventorySize.invSize(votes.length), "Vote:");

		switch(votes.length){
		case 2:
			for(int i = 0; i < votes.length; i++)add(i, (i==0?2:6));

			break;
		case 3:
			int slot=0;
			for(int i = 0; i < votes.length; i++)add(i, slot+=2);
			break;
		default:
			for(int i = 0; i < votes.length; i++)add(i);
			break;
		}

		this.page.fill(Material.STAINED_GLASS_PANE, 7);
		UtilInv.getBase().addPage(this.page);
	}

	protected void add(int index){
		add(index,index);
	}

	protected void add(int index,int slot){
		Vote vote = votes[index];
		vote.setInstance(this);
		vote.setSlot(slot);
		this.page.addButton(vote.getSlot(), new ButtonBase(new Click(){

			@Override
			public void onClick(Player player, ActionType type, Object object) {
				Vote v = remove(player);

				if(v==null || v.getSlot()!=vote.getSlot()){
					players.put(player, vote);
					ButtonBase button = ((ButtonBase)page.getButton(vote.getSlot()));
					button.setItemStack(setAmount(button.getItemStack(), vote.add()));
					Bukkit.getPluginManager().callEvent(new PlayerVoteEvent(player, vote));
				}
			}

		},setAmount(vote.getItem().clone(), 0)));
	}

	public Vote remove(Player player){
		if(players.containsKey(player)){
			Vote v = players.get(player);
			ButtonBase button = ((ButtonBase)page.getButton(v.getSlot()));
			button.setItemStack(setAmount(button.getItemStack(), v.remove()));
			players.remove(player);
			return v;
		}
		return null;
	}

	protected ItemStack setAmount(ItemStack item,int amount){
		return UtilItem.setLore(item, new String[]{"§cVotes: "+amount});
	}
}
