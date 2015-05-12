package me.kingingo.karcade.Game.Single.addons;

import lombok.Getter;
import me.kingingo.karcade.Enum.Team;
import me.kingingo.karcade.Game.Single.Games.TeamGame;
import me.kingingo.kcore.Listener.kListener;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.UtilItem;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class AddonDropItems extends kListener {

	@Getter
	private TeamGame instance;
	private int i=0;
	private int drop_rate=0;
	
	public AddonDropItems(TeamGame instance,int drop_rate){
		super(instance.getManager().getInstance(),"[AddonDropItems]");
		this.instance=instance;
		this.drop_rate=drop_rate;
	}
	
	//GOLD ORANGE
	//IRON PURPLE
	//BRONZE WHITE
	
	@EventHandler
	public void Interact(PlayerPickupItemEvent ev){	
		if(ev.getItem().getItemStack().getType()==Material.CLAY_BRICK){
			UtilItem.RenameItem(ev.getItem().getItemStack(), "�bBronze");
		}else if(ev.getItem().getItemStack().getType()==Material.IRON_INGOT){
			UtilItem.RenameItem(ev.getItem().getItemStack(), "�bSilver");
		}else if(ev.getItem().getItemStack().getType()==Material.GOLD_INGOT){
			UtilItem.RenameItem(ev.getItem().getItemStack(), "�bGold");
		}
	}
	
	public String getName(){
		i++;
		return String.valueOf(i);
	}
	
	public void drop(Location loc,ItemStack item, int anzahl,String Name){
		for(int i = 0; i < anzahl; i++)loc.getWorld().dropItem(loc, UtilItem.RenameItem(item, Name));
	}
	
	public void drop(Location loc,ItemStack item, int anzahl){
		for(int i = 0; i < anzahl; i++)loc.getWorld().dropItem(loc, UtilItem.RenameItem(item, getName()));
	}
	
	@EventHandler
	public void Update(UpdateEvent ev){
		
		if(ev.getType()==UpdateType.SEC){
			for(Location loc : getInstance().getWorldData().getLocs(Team.WHITE.Name())) drop(loc,new ItemStack(Material.CLAY_BRICK),drop_rate);
		}
		
		if(ev.getType()==UpdateType.SLOW){
			if(getInstance().getWorldData().ExistLoc(Team.PURPLE.Name())){
				for(Location loc : getInstance().getWorldData().getLocs(Team.PURPLE.Name())) drop(loc,new ItemStack(Material.IRON_INGOT),drop_rate);
			}else{
				for(Location loc : getInstance().getWorldData().getLocs(Team.WHITE.Name())) drop(loc,new ItemStack(Material.IRON_INGOT),drop_rate);
			}
		}
		
		if(ev.getType()==UpdateType.SLOWER){
			if(getInstance().getWorldData().ExistLoc(Team.ORANGE.Name())){
				for(Location loc : getInstance().getWorldData().getLocs(Team.ORANGE.Name())) drop(loc,new ItemStack(Material.GOLD_INGOT),drop_rate, "�bGold");
			}else{
				for(Location loc : getInstance().getWorldData().getLocs(Team.WHITE.Name())) drop(loc,new ItemStack(Material.GOLD_INGOT),drop_rate);
			}
		}
	}
	
}