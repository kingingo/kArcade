package me.kingingo.karcade.Game.Games.SheepWars.Addon;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import me.kingingo.karcade.Enum.Team;
import me.kingingo.karcade.Game.Games.SheepWars.SheepWars;
import me.kingingo.kcore.kListener;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilMath;

public class AddonDropItems extends kListener {

	@Getter
	private SheepWars instance;
	
	public AddonDropItems(SheepWars instance){
		super(instance.getManager().getInstance(),"[AddonDropItems]");
		this.instance=instance;
	}
	
	//GOLD ORANGE
	//IRON PURPLE
	//BRICK WHITE
	
	@EventHandler
	public void Interact(PlayerPickupItemEvent ev){	
		if(ev.getItem().getItemStack().getType()==Material.CLAY_BRICK){
			UtilItem.RenameItem(ev.getItem().getItemStack(), "§bBronze");
		}else if(ev.getItem().getItemStack().getType()==Material.IRON_INGOT){
			UtilItem.RenameItem(ev.getItem().getItemStack(), "§bSilver");
		}else if(ev.getItem().getItemStack().getType()==Material.GOLD_INGOT){
			UtilItem.RenameItem(ev.getItem().getItemStack(), "§bGold");
		}
	}
	
	public static String Farbe(){
		switch(UtilMath.RandomInt(9,0)){
		case 0:
			return "§r";
		case 1:
			return "§a";
		case 2:
			return "§c";
		case 3:
			return "§d";
		case 4:
			return "§l";
		case 6:
			return "§e";
		case 7:
			return "§m";
		case 8:
			return "§n";
		case 9:
			return "§k";
		case 10:
			return "§1";
		case 11:
			return "§2";
		case 12:
			return "§3";
		default:
			return "§4";
		}
	}
	
	public void drop(Location loc,ItemStack item,String name, int anzahl){
		for(int i = 0; i < anzahl; i++)loc.getWorld().dropItem(loc, UtilItem.RenameItem(item, name+Farbe()));
	}
	
	@EventHandler
	public void Update(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC){
			for(Location loc : getInstance().getManager().getWorldData().getLocs(Team.WHITE.Name()))drop(loc,new ItemStack(Material.CLAY_BRICK),"§bBronze",UtilMath.RandomInt(8, 3));
		}else if(ev.getType()!=UpdateType.SEC_2){
			if(getInstance().getManager().getWorldData().ExistLoc(Team.PURPLE.Name())){
				for(Location loc : getInstance().getManager().getWorldData().getLocs(Team.PURPLE.Name()))drop(loc,new ItemStack(Material.IRON_INGOT),"§bSilver",UtilMath.RandomInt(5, 2));
			}else{
				for(Location loc : getInstance().getManager().getWorldData().getLocs(Team.WHITE.Name()))drop(loc,new ItemStack(Material.IRON_INGOT),"§bSilver",UtilMath.RandomInt(5, 2));
			}
		}else if(ev.getType()!=UpdateType.SLOW){
			if(getInstance().getManager().getWorldData().ExistLoc(Team.PURPLE.Name())){
				for(Location loc : getInstance().getManager().getWorldData().getLocs(Team.ORANGE.Name()))drop(loc,new ItemStack(Material.GOLD_INGOT),"§bGold",UtilMath.RandomInt(2, 1));
			}else{
				for(Location loc : getInstance().getManager().getWorldData().getLocs(Team.WHITE.Name()))drop(loc,new ItemStack(Material.GOLD_INGOT),"§bGold",UtilMath.RandomInt(2, 1));
			}
		}
	}
	
}
