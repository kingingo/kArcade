package me.kingingo.karcade.Game.Games.Falldown.Brew.Items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.kingingo.karcade.Game.Games.Falldown.Falldown;
import me.kingingo.karcade.Game.Games.Falldown.Brew.BrewItem;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.TimeSpan;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.UtilFirework;
import me.kingingo.kcore.Util.UtilInv;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilMath;
import me.kingingo.kcore.Util.UtilPlayer;

import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class Sethbling extends BrewItem{

	private HashMap<Player,Long> timer = new HashMap<>();
	private HashMap<Block,Long> blocke = new HashMap<>();
	private HashMap<Block,List<Player>> blocke_list = new HashMap<>();
	
	public Sethbling(Integer[] brewing_items,Falldown falldown) {
		super(100, UtilItem.Item(new ItemStack(Material.DIODE,1), new String[]{""}, "�6Sethbling"), brewing_items, falldown);
	}
	
	@EventHandler
	public void Launch(final PlayerInteractEvent event){
		if(UtilEvent.isAction(event, ActionType.R)&&event.getPlayer().getItemInHand().hasItemMeta()&&event.getPlayer().getItemInHand().getItemMeta().hasDisplayName()){
			if(event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(getRealItem().getItemMeta().getDisplayName())){
				event.setCancelled(true);
				if(!fireEvent(event.getPlayer())){
					UtilInv.remove(event.getPlayer(),event.getPlayer().getItemInHand().getType(),event.getPlayer().getItemInHand().getData().getData(), 1);
					timer.put(event.getPlayer(), (System.currentTimeMillis()+TimeSpan.SECOND*17) );
				}
			}
		}
	}
	
	Player player;
	Location destroyPixel;
	Location location;
	Block block;
	@EventHandler
	public void Update(UpdateEvent ev){
		if(ev.getType() == UpdateType.SEC){
			for(int i = 0; i < timer.size(); i++){
				player=(Player)timer.keySet().toArray()[i];
				if(timer.get(player)<System.currentTimeMillis()){
					timer.remove(i);
				}else{
					UtilFirework.start(player.getLocation(), Color.RED, Type.BALL_LARGE);
					player.getWorld().playSound(player.getLocation(), Sound.HURT_FLESH, 1.0F, 1.0F);
					
					for(Entity entity : player.getNearbyEntities(5.0D, 5.0D, 5.0D)){
						if(entity instanceof LivingEntity)UtilPlayer.damage(((LivingEntity)entity), 20);
					}
					
					location=player.getLocation();
					for (int x = -5; x <= 5; x++) {
			            for (int y = -5; y <= 5; y++) {
			              for (int z = -5; z <= 5; z++) {
			                destroyPixel = location.clone().add(x, y, z);
			                if ((destroyPixel.distance(location) <= 5.0D) && (destroyPixel.getBlock().getType() == Material.AIR) && (UtilMath.random.nextInt(2) == 0)) {
			                	this.blocke.put(destroyPixel.getBlock(), (System.currentTimeMillis()+2000 + UtilMath.random.nextInt(1000)));
				                this.blocke_list.put(destroyPixel.getBlock(), getChunkPacketSendList(destroyPixel));
			                    for (Player player : blocke_list.get(destroyPixel.getBlock()))player.sendBlockChange(destroyPixel,Material.REDSTONE_WIRE, (byte) 0);    
				            }
			              }
			            }
			          }
				}
			}
			
			for(int i = 0; i < blocke.size() ; i++){
				block=(Block)blocke.keySet().toArray()[i];
				if(blocke.get(block)<=System.currentTimeMillis()){
					for (Player player : blocke_list.get(block))player.sendBlockChange(block.getLocation(), block.getType(), block.getData());

		            block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getType());
					blocke_list.remove(block);
					blocke.remove(block);
				}
			}
			
		}
	}
	
	private List<Player> getChunkPacketSendList(Location location){
	    List players = new ArrayList();

	    for (LivingEntity entity : location.getWorld().getLivingEntities()) {
	      if (((entity instanceof Player)) && (entity.getLocation().distance(location) <= 200.0D)) {
	        players.add((Player)entity);
	      }
	    }

	    return players;
	  }
	
}
