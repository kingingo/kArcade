package me.kingingo.karcade.Game.Multi.Addons;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;
import me.kingingo.karcade.Game.Multi.Games.MultiGame;
import me.kingingo.karcade.Game.Multi.Games.BedWars1vs1.BedWars1vs1;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.Team;
import me.kingingo.kcore.Listener.kListener;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.UtilItem;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
public class MultiAddonDropItems extends kListener {

	@Getter
	private ArrayList<MultiGame> games;
	private int i=0;
	private int drop_rate=0;
	@Setter
	@Getter
	private boolean drop_name = true;
	@Getter
	private JavaPlugin instance;
	
	public MultiAddonDropItems(JavaPlugin instance,int drop_rate){
		super(instance,"[MultiAddonDropItems]");
		this.instance=instance;
		this.drop_rate=drop_rate;
		this.games=new ArrayList<>();
	}
	
	public void clear(MultiGame game){
		for(Entity entity : game.getWorldData().getWorld().getEntities()){
			if(entity instanceof Item){
				if(game instanceof BedWars1vs1){
					if(((BedWars1vs1)game).getArea().isInArea(entity.getLocation())){
						entity.remove();
					}
				}
			}
		}
	}
	
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
	
	public String getName(){
		i++;
		return String.valueOf(i);
	}
	
	public void drop(Location loc,ItemStack item, int anzahl,String Name){
		for(int i = 0; i < anzahl; i++)loc.getWorld().dropItem(loc, UtilItem.RenameItem(item, Name));
	}
	
	public void drop(Location loc,ItemStack item, int anzahl){
		for(int i = 0; i < anzahl; i++)loc.getWorld().dropItem(loc, (drop_name ? UtilItem.RenameItem(item, getName() ) : item) );
	}
	
	@EventHandler
	public void Update(UpdateEvent ev){
		if(ev.getType()==UpdateType.SEC){
			for(MultiGame game : games){
				if(game.getState()==GameState.InGame){
					for(Location loc : game.getWorldData().getLocs(game,Team.BRONZE)){
						drop(loc,new ItemStack(Material.CLAY_BRICK),drop_rate);
					}
				}
			}
		}
		
		if(ev.getType()==UpdateType.SLOW){
			for(MultiGame game : games){
				if(game.getWorldData().existLoc(game,Team.SILBER)){
					for(Location loc : game.getWorldData().getLocs(game,Team.SILBER)) drop(loc,new ItemStack(Material.IRON_INGOT),drop_rate);
				}else{
					for(Location loc : game.getWorldData().getLocs(game,Team.BRONZE)) drop(loc,new ItemStack(Material.IRON_INGOT),drop_rate);
				}
			}
		}
		
		if(ev.getType()==UpdateType.SLOWER){
			for(MultiGame game : games){
				if(game.getWorldData().existLoc(game,Team.GOLD)){
					for(Location loc : game.getWorldData().getLocs(game,Team.GOLD)) drop(loc,new ItemStack(Material.GOLD_INGOT),drop_rate, "§bGold");
				}else{
					for(Location loc : game.getWorldData().getLocs(game,Team.BRONZE)) drop(loc,new ItemStack(Material.GOLD_INGOT),drop_rate);
				}
			}
		}
	}
	
}
