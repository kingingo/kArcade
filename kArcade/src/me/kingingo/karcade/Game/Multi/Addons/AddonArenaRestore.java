package me.kingingo.karcade.Game.Multi.Addons;

import me.kingingo.karcade.Game.Multi.Events.MultiGameAddonAreaRestoreEvent;
import me.kingingo.karcade.Game.Multi.Events.MultiGameAddonAreaRestoreExplosionEvent;
import me.kingingo.kcore.Listener.kListener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class AddonArenaRestore extends kListener{

	public AddonArenaRestore(JavaPlugin instance){
		super(instance,"AddonArenaRestore");
	}
	
	MultiGameAddonAreaRestoreEvent fill;
	@EventHandler
	public void PlayerBucketFill(PlayerBucketFillEvent ev){
		fill = new MultiGameAddonAreaRestoreEvent(ev.getBlockClicked(), "PlayerBucketFillEvent"+ev.getBlockClicked().getType().name()+"  "+ev.getBlockFace().name());
		Bukkit.getPluginManager().callEvent(fill);
		ev.setCancelled(!fill.isBuild());
		fill=null;
	}
	
	MultiGameAddonAreaRestoreEvent bucket;
	@EventHandler(ignoreCancelled=false)
	public void bucket(PlayerBucketEmptyEvent ev){
		bucket = new MultiGameAddonAreaRestoreEvent(ev.getBlockClicked().getRelative(ev.getBlockFace()), "PlayerBucketEmptyEvent");
		Bukkit.getPluginManager().callEvent(bucket);
		ev.setCancelled(!bucket.isBuild());
		bucket=null;
	}
	
	MultiGameAddonAreaRestoreExplosionEvent explosion;
	@EventHandler(ignoreCancelled=false)
	public void Explose(EntityExplodeEvent ev){
		if(!ev.blockList().isEmpty()){
			explosion = new MultiGameAddonAreaRestoreExplosionEvent(ev.blockList());
			Bukkit.getPluginManager().callEvent(explosion);
			ev.setCancelled(!explosion.isBuild());
			explosion=null;
		}
	}

	MultiGameAddonAreaRestoreEvent burn;
	@EventHandler(ignoreCancelled=false)
	public void BlockBurn(BlockBurnEvent ev){
		burn = new MultiGameAddonAreaRestoreEvent(ev.getBlock(), "BlockBurnEvent");
		Bukkit.getPluginManager().callEvent(burn);
		ev.setCancelled(!burn.isBuild());
		burn=null;
	}

	MultiGameAddonAreaRestoreEvent ignite;
	@EventHandler(ignoreCancelled=false)
	public void BlockIgnite(BlockIgniteEvent ev){
		ignite = new MultiGameAddonAreaRestoreEvent(ev.getBlock(), "BlockIgniteEvent");
		Bukkit.getPluginManager().callEvent(ignite);
		ev.setCancelled(!ignite.isBuild());
		ignite=null;
	}

	MultiGameAddonAreaRestoreEvent fade;
	@EventHandler(ignoreCancelled=false)
	public void BlockFade(BlockFadeEvent ev){
		fade = new MultiGameAddonAreaRestoreEvent(ev.getBlock(), "BlockFadeEvent");
		Bukkit.getPluginManager().callEvent(fade);
		ev.setCancelled(!fade.isBuild());
		fade=null;
	}

	MultiGameAddonAreaRestoreEvent form;
	@EventHandler(ignoreCancelled=false)
	public void BlockForm(BlockFormEvent ev){
		form = new MultiGameAddonAreaRestoreEvent(ev.getBlock(), "BlockFormEvent");
		Bukkit.getPluginManager().callEvent(form);
		ev.setCancelled(!form.isBuild());
		form=null;
	}

//	MultiGameAddonAreaRestoreEvent phsics;
//	@EventHandler(ignoreCancelled=false)
//	public void BlockPhysics(BlockPhysicsEvent ev){
//		if(ev.){
//			phsics = new MultiGameAddonAreaRestoreEvent(ev.getBlock(), "BlockPhysicsEvent "+ev.getChangedType().name());
//			Bukkit.getPluginManager().callEvent(phsics);
//			ev.setCancelled(!phsics.isBuild());
//			phsics=null;
//		}
//	}
	
	public boolean check(Block to,MultiGameAddonAreaRestoreEvent from,BlockFace face){
		if(to.getRelative(face).getType()!=to.getType()){
			if(to.getRelative(face).getTypeId()<=8&&to.getRelative(face).getTypeId()<=11){
				from=new MultiGameAddonAreaRestoreEvent(to.getRelative(face), "BlockFromToEvent");
				Bukkit.getPluginManager().callEvent(from);
				from=null;
				return true;
			}
		}
		return false;
	}
	
	MultiGameAddonAreaRestoreEvent from;
	@EventHandler(ignoreCancelled=false)
	public void BlockFromTo(BlockFromToEvent ev){
		from = new MultiGameAddonAreaRestoreEvent(ev.getToBlock(), "BlockFromToEvent");
		Bukkit.getPluginManager().callEvent(from);
		ev.setCancelled(!from.isBuild());

//		if(!ev.isCancelled()&&(ev.getBlock().getType()==Material.WATER||ev.getBlock().getType()==Material.LAVA)){
//			from=null;
//			check(ev.getToBlock(),from,BlockFace.UP);
//			check(ev.getToBlock(),from,BlockFace.DOWN);
//			check(ev.getToBlock(),from,BlockFace.EAST);
//			check(ev.getToBlock(),from,BlockFace.SOUTH);
//			check(ev.getToBlock(),from,BlockFace.WEST);
//			check(ev.getToBlock(),from,BlockFace.NORTH);
//		}
		
		from=null;
	}

	MultiGameAddonAreaRestoreEvent bbreak;
	@EventHandler(ignoreCancelled=false)
	public void breakb(BlockBreakEvent ev){
		bbreak = new MultiGameAddonAreaRestoreEvent(ev.getBlock(), "BlockBreakEvent");
		Bukkit.getPluginManager().callEvent(bbreak);
		ev.setCancelled(!bbreak.isBuild());
		bbreak=null;
	}

	MultiGameAddonAreaRestoreEvent place;
	@EventHandler(ignoreCancelled=false)
	public void place(BlockPlaceEvent ev){
		place = new MultiGameAddonAreaRestoreEvent(ev.getBlock().getLocation(),ev.getBlockReplacedState(), "BlockPlaceEvent");
		Bukkit.getPluginManager().callEvent(place);
		ev.setCancelled(!place.isBuild());
		place=null;
	}
}
