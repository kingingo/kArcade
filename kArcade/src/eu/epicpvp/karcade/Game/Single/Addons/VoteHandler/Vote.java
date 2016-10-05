package eu.epicpvp.karcade.Game.Single.Addons.VoteHandler;

import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import lombok.Setter;

public class Vote<T> {

	@Getter
	private ItemStack item;
	@Getter
	@Setter
	private int slot=0;
	@Getter
	private T value;
	@Getter
	@Setter
	private int votes = 0;
	@Getter
	@Setter
	private Votes instance;
	
	public Vote(T value,ItemStack item){
		this.value=value;
		this.item=item;
	}
	
	public int remove(){
		setVotes(getVotes()-1);
		return getVotes();
	}
	
	public int add(){
		setVotes(getVotes()+1);
		return getVotes();
	}
}
