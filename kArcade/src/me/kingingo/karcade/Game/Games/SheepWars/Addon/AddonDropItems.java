package me.kingingo.karcade.Game.Games.SheepWars.Addon;

import java.util.HashMap;

import lombok.Getter;
import me.kingingo.karcade.Enum.Team;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilMath;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class AddonDropItems implements Listener {

	@Getter
	JavaPlugin instance;
	@Getter
	HashMap<Team,Location> teams;
	@Getter
	HashMap<Team,Boolean> drops = new HashMap<>();
	@Getter
	HashMap<Team,Integer[]> chance = new HashMap<>();
	
	public AddonDropItems(JavaPlugin instance,HashMap<Team,Location> teams){
		this.instance=instance;
		this.teams=teams;
		for(Team t : teams.keySet()){
			drops.put(t, true);
			chance.put(t, new Integer[]{1,2});
		}
		Bukkit.getPluginManager().registerEvents(this, getInstance());
	}
	
	@EventHandler
	public void Update(UpdateEvent ev){
	if(ev.getType()!=UpdateType.SEC_2)return;
		for(Team t : getTeams().keySet()){
			if(getDrops().get(t)==true){
				DropItem(getTeams().get(t),chance.get(t)[0],chance.get(t)[1]);
			}
		}
	}
	
	public static void DropItem(Location l,int g,int s){
		int i = UtilMath.RandomInt(10,0);
		
		l.getWorld().dropItem(l, UtilItem.RenameItem(new ItemStack(Material.CLAY_BRICK,UtilMath.RandomInt(3,1)), "§bBronze"));
		
		switch(i){
		case 2:
			l.getWorld().dropItem(l, UtilItem.RenameItem(new ItemStack(Material.IRON_INGOT,UtilMath.RandomInt(s,1)), "§bSilver"));
			break;
		case 6:
			l.getWorld().dropItem(l, UtilItem.RenameItem(new ItemStack(Material.IRON_INGOT,UtilMath.RandomInt(s,1)), "§bSilver"));
			break;
		case 4:
			l.getWorld().dropItem(l, UtilItem.RenameItem(new ItemStack(Material.GOLD_INGOT,g), "§bGold"));
			break;
		default:
			break;
		}
		
	}
	
}
