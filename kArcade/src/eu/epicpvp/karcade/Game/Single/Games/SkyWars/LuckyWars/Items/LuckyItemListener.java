package eu.epicpvp.karcade.Game.Single.Games.SkyWars.LuckyWars.Items;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import lombok.Setter;

public class LuckyItemListener extends LuckyItem implements Listener{

	@Getter
	@Setter
	private boolean registered =false;
	
	public LuckyItemListener(ItemStack item, double chance) {
		super(item, chance);
	}
	
	public void register(){
		if(isRegistered())return;
		Bukkit.getPluginManager().registerEvents(this, getAddon().getInstance().getManager().getInstance());
		setRegistered(true);
	}
	
	public void unregister(){
		if(!isRegistered())return;
		HandlerList.unregisterAll(this);
		setRegistered(false);
	}

}
