package eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.Items;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import eu.epicpvp.karcade.Game.Single.Games.Falldown.Falldown;
import eu.epicpvp.karcade.Game.Single.Games.Falldown.Brew.BrewItem;
import eu.epicpvp.kcore.LaunchItem.LaunchItem;
import eu.epicpvp.kcore.LaunchItem.LaunchItemEvent;
import eu.epicpvp.kcore.Util.UtilEvent;
import eu.epicpvp.kcore.Util.UtilEvent.ActionType;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.kcore.Util.UtilMath;

public class Tornado extends BrewItem{

	public Tornado(Integer[] brewing_items, Falldown falldown) {
		super(100, UtilItem.Item(new ItemStack(Material.STRING,1), new String[]{"§6§7 Ein verherender Tornado verw§stet die Map ","§7   bringt deine Gegner in eine ","§7   Gef§hrliche Situation!"}, "§e§lTornado"), brewing_items, falldown);
	}
	
	public Material Block(Location loc, int radius)
	  {
		Material m = Material.ANVIL;
	    int y = loc.getBlockY();
	    int minx = loc.getBlockX() - radius;
	    int minz = loc.getBlockZ() - radius;
	    int maxx = loc.getBlockX() + radius;
	    int maxz = loc.getBlockZ() + radius;
	    int zufall = 0;
	    Location b = new Location(loc.getWorld(), 0.0D, 0.0D, 0.0D);
	    for (int x = minx; x < maxx; x++)
	      for (int z = minz; z < maxz; z++) {
	        b = new Location(loc.getWorld(), x, y, z);

	        if(b.getBlock().getType()==Material.AIR){
	        	continue;
	        }
	        
	        zufall = UtilMath.RandomInt(radius,0);
	        
	        if (zufall == 1) {
	        	m=b.getBlock().getType();
	        }
	      }

	    if(m==Material.ANVIL){
	    	loc.add(0, -1, 0);
	    	m=Block(loc,5);
	    }
	    
	    return m;
	  }
	
	public void createTornado(final Location l,int time) {
		final ArrayList<Material> material = new ArrayList<Material>();
		
		class tEntity {
	        
	         Entity entity;
	        
	         private float ticker_vertical  = 0.0f;
	         private float ticker_horisontal = (float) (Math.random() *  2 * Math.PI);
	        
	         public tEntity(Entity e) {
	        	 entity=e;
			 }
				
	         public Entity getEntity() {
	        	 return entity;
	         }
	        
	         public void setVelocity(Vector v) {
	             entity.setVelocity(v);
	         }
	        
	         public Location getLocation(){
	        	 return entity.getLocation();
	         }
	         
	         public float verticalTicker() {
	             if(ticker_vertical < 1.0f) {
	                 ticker_vertical += 0.05f;
	             }
	            
	             return ticker_vertical;
	         }
	        
	         public float horisontalTicker() {
	             return (ticker_horisontal += 0.8f);
	         }
	     }
		
	     class VortexBlock {
	        
	         FallingBlock entity;
	        
	         private float ticker_vertical  = 0.0f;
	         private float ticker_horisontal = (float) (Math.random() *  2 * Math.PI);
	        
	         public VortexBlock(Location l,Material m) {
	        	 entity = l.getWorld().spawnFallingBlock(l, m,(byte) 0);
			}
	        
	         public void setVelocity(Vector v) {
	             entity.setVelocity(v);
	         }
	        
	         public float verticalTicker() {
	             if(ticker_vertical < 1.0f) {
	                 ticker_vertical += 0.05f;
	             }
	            
	             return ticker_vertical;
	         }
	        
	         public float horisontalTicker() {
	             return (ticker_horisontal += 0.8f);
	         }
	     }
	     
	     final int id = new BukkitRunnable() {
	        
	         private ArrayList<VortexBlock> blocks = new ArrayList<VortexBlock>();
	         private ArrayList<tEntity> te = new ArrayList<tEntity>();
	         private ArrayList<tEntity> no = new ArrayList<tEntity>();
	         private boolean search=true;
	         int id = 0;
	         
	         public void run() {
	            
	        	 if(search){
	        		 for(Entity e : getFalldown().getNearPlayers(13,l,false)){
		            	 te.add(new tEntity(e));
		             }
	        		 search=false;
	        	 }
	        	 
	             for(int i = 0 ; i < 10 ; i++) {
	                 if(blocks.size() >= 200) {
	                     VortexBlock vb = blocks.get(i);
	                     vb.entity.remove();
	                     blocks.remove(vb);
	                 }
	                 
	                 Material m = null;
	                 
	                 if(material.size()!=10){
	                	 m=Block(l, 5);
		                 material.add(m); 
	                 }else{
	                	 m=material.get(UtilMath.RandomInt(10,0));
	                 }
	                 
	                 for(Entity e : getFalldown().getNearPlayers(5, l,false)){
	                	 boolean a = true;
	                	 boolean o = true;
	                	 tEntity t =null;
	                	 for(tEntity e1:te){
	                		 if(e1.getEntity()==e){
	                			 a=false;
	                			 t=e1;
	                			 for(tEntity oi : no){
	                				 if(oi.getEntity()==e){
	                					 o=false;
	                				 }
	                			 }
	                			 continue;
	                		 }
	                	 }
	                	 
	                	 if(!o && !a){
	                		 te.remove(t);
	                		 no.remove(t);
	                		 a=true;
	                		 o=true;
	                	 }
	                	 
	                	 if(a && o){
	                		 te.add(new tEntity(e));
	                	 }
	                	 
	                 }
	                 id++;
	                 blocks.add(new VortexBlock(l,m));
	             }
	            

	             for(VortexBlock vb : blocks) {
	                 double radius = Math.sin(vb.verticalTicker()) * 2;
	                
	                 float horisontal = vb.horisontalTicker();
	                
	                 vb.setVelocity(new Vector(radius * Math.cos(horisontal), 0.5D, radius * Math.sin(horisontal)));
	             }
	             
	             for(tEntity e : te){
	            	 
	            	 if(no.contains(e)){
	            		 continue;
	            	 }
	            	 
	            	 if(e.getLocation().getY()>=l.getY()+45){
	            		 no.add(e);
	            		 continue;
	            	 }
	            	 
	            	 double radius = Math.sin(e.verticalTicker()) * 2;
		                
	                 float horisontal = e.horisontalTicker();
	                 
	                 e.setVelocity(new Vector(radius * Math.cos(horisontal), 0.5D, radius * Math.sin(horisontal))); 
	             }
	              
	         }
	     }.runTaskTimer(getFalldown().getManager().getInstance(), 5L, 5L).getTaskId();
	    

	     new BukkitRunnable() {
	         public void run() {
	             Bukkit.getServer().getScheduler().cancelTask(id);
	         }
	     }.runTaskLater(getFalldown().getManager().getInstance(), 20L * time);
	 }
	
	@EventHandler
	public void Launch(final PlayerInteractEvent event){
		if(UtilEvent.isAction(event, ActionType.R)&&event.getPlayer().getItemInHand().hasItemMeta()&&event.getPlayer().getItemInHand().getItemMeta().hasDisplayName()){
			if(event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(getRealItem().getItemMeta().getDisplayName())){
				event.setCancelled(true);
				
				if(!fireEvent(event.getPlayer())){
					LaunchItem item = new LaunchItem(event.getPlayer(),4,new LaunchItem.LaunchItemEventHandler(){
						@Override
						public void onLaunchItem(LaunchItemEvent ev) {
							createTornado(ev.getItem().getDroppedItem()[0].getLocation(), 30);
						}
					});
					getFalldown().getIlManager().LaunchItem(item);
				}
			}
		}
	}
}
