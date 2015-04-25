package me.kingingo.karcade.Game.Games.Falldown.Brew.Items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import me.kingingo.karcade.Game.Games.Falldown.Falldown;
import me.kingingo.karcade.Game.Games.Falldown.Brew.BrewItem;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilFirework;
import me.kingingo.kcore.Util.UtilInv;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilMath;
import me.kingingo.kcore.Util.UtilPlayer;
import me.kingingo.kcore.Util.UtilEvent.ActionType;

public class Sethbling extends BrewItem{

	private HashMap<Player,Integer> timer = new HashMap<>();
	
	public Sethbling(ItemStack[] brewing_items,Falldown falldown) {
		super(100, UtilItem.Item(new ItemStack(Material.DIODE,1), new String[]{""}, "§6Sethbling"), brewing_items, falldown);
	}
	
	@EventHandler
	public void Launch(final PlayerInteractEvent event){
		if(UtilEvent.isAction(event, ActionType.R)&&event.getPlayer().getItemInHand().hasItemMeta()&&event.getPlayer().getItemInHand().getItemMeta().hasDisplayName()){
			if(event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(getRealItem().getItemMeta().getDisplayName())){
				event.setCancelled(true);
				if(!fireEvent(event.getPlayer())){
					UtilInv.remove(event.getPlayer(),event.getPlayer().getItemInHand().getType(),event.getPlayer().getItemInHand().getData().getData(), 1);
					timer.put(event.getPlayer(), 15);
				}
			}
		}
	}
	
	Player player;
	Location destroyPixel;
	Location location;
	@EventHandler
	public void Update(UpdateEvent ev){
		if(ev.getType() == UpdateType.SEC){
			for(int i = 0; i < timer.size(); i++){
				player=(Player)timer.keySet().toArray()[i];
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
		                //  Util.replaceBlock(ToolSethbling.this.falldown.getPathbreaking(), destroyPixel.getBlock(), new ItemData(Material.REDSTONE_WIRE, (byte)0), 2000, 1000, Effect.STEP_SOUND, null);
		                }
		              }
		            }
		          }
				
			}
		}
	}
	
//	private void replaceBlock(final Block block, Material material, int regenerationStatic, int regenerationRandom, final Effect regenerationEffect, final Sound regenerationSound) {
//	    Random regenerationRandomObject = new Random();
//	    final List<Player> players = getChunkPacketSendList(block.getLocation());
//	    for (Player player : players) {
//	      player.sendBlockChange(block.getLocation(), material, (byte) 0);
//	    }
//
//	    if ((regenerationStatic > 0) && (regenerationRandom > 0))
//	      new ScheduleTask(getFalldown().getManager().getInstance())
//	      {
//	        public void run()
//	        {
//	          if (regenerationEffect != null) {
//	            int data = 0;
//
//	            if (regenerationEffect == Effect.STEP_SOUND) {
//	              data = block.getTypeId();
//	            }
//
//	            block.getWorld().playEffect(block.getLocation(), regenerationEffect, data);
//	          }
//	          if (regenerationSound != null) {
//	            block.getWorld().playSound(block.getLocation(), regenerationSound, 1.0F, 1.0F);
//	          }
//
//	          for (Player player : players)
//	            player.sendBlockChange(block.getLocation(), block.getType(), block.getData());
//	        }
//	      }
//	      .run(true, regenerationStatic + regenerationRandomObject.nextInt(regenerationRandom));
//	  }
	
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
