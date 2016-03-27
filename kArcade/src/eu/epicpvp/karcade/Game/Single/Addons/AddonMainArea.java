package eu.epicpvp.karcade.Game.Single.Addons;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.plugin.java.JavaPlugin;

import eu.epicpvp.karcade.Game.Multi.Addons.Evemts.BuildType;
import eu.epicpvp.karcade.Game.Single.Events.AddonAreaRestoreEvent;
import eu.epicpvp.karcade.Game.Single.Events.AddonAreaRestoreExplosionEvent;
import eu.epicpvp.kcore.Listener.kListener;

public class AddonMainArea extends kListener{
	
	public AddonMainArea(JavaPlugin instance){
		super(instance,"AddonMainArea");
	}
	
	AddonAreaRestoreEvent fill;
	@EventHandler
	public void PlayerBucketFill(PlayerBucketFillEvent ev){
		fill = new AddonAreaRestoreEvent(ev.getPlayer(),BuildType.PLACE,ev.getBlockClicked(), "PlayerBucketFillEvent"+ev.getBlockClicked().getType().name()+"  "+ev.getBlockFace().name());
		Bukkit.getPluginManager().callEvent(fill);
		ev.setCancelled(!fill.isBuild());
		fill=null;
	}
	
	AddonAreaRestoreEvent bucket;
	@EventHandler(ignoreCancelled=false)
	public void bucket(PlayerBucketEmptyEvent ev){
		bucket = new AddonAreaRestoreEvent(ev.getPlayer(),BuildType.PLACE,ev.getBlockClicked().getRelative(ev.getBlockFace()), "PlayerBucketEmptyEvent");
		Bukkit.getPluginManager().callEvent(bucket);
		ev.setCancelled(!bucket.isBuild());
		bucket=null;
	}
	
	AddonAreaRestoreExplosionEvent explosion;
	@EventHandler(ignoreCancelled=false)
	public void Explose(EntityExplodeEvent ev){
		if(!ev.blockList().isEmpty()){
			explosion = new AddonAreaRestoreExplosionEvent(ev.blockList());
			Bukkit.getPluginManager().callEvent(explosion);
			ev.setCancelled(!explosion.isBuild());
			explosion=null;
		}
	}

	AddonAreaRestoreEvent burn;
	@EventHandler(ignoreCancelled=false)
	public void BlockBurn(BlockBurnEvent ev){
		burn = new AddonAreaRestoreEvent(null,BuildType.PLACE,ev.getBlock(), "BlockBurnEvent");
		Bukkit.getPluginManager().callEvent(burn);
		ev.setCancelled(!burn.isBuild());
		burn=null;
	}

	AddonAreaRestoreEvent ignite;
	@EventHandler(ignoreCancelled=false)
	public void BlockIgnite(BlockIgniteEvent ev){
		ignite = new AddonAreaRestoreEvent(ev.getPlayer(),BuildType.PLACE,ev.getBlock(), "BlockIgniteEvent");
		Bukkit.getPluginManager().callEvent(ignite);
		ev.setCancelled(!ignite.isBuild());
		ignite=null;
	}

	AddonAreaRestoreEvent fade;
	@EventHandler(ignoreCancelled=false)
	public void BlockFade(BlockFadeEvent ev){
		fade = new AddonAreaRestoreEvent(null,BuildType.PLACE,ev.getBlock(), "BlockFadeEvent");
		Bukkit.getPluginManager().callEvent(fade);
		ev.setCancelled(!fade.isBuild());
		fade=null;
	}

	AddonAreaRestoreEvent form;
	@EventHandler(ignoreCancelled=false)
	public void BlockForm(BlockFormEvent ev){
		form = new AddonAreaRestoreEvent(null,BuildType.PLACE,ev.getBlock(), "BlockFormEvent");
		Bukkit.getPluginManager().callEvent(form);
		ev.setCancelled(!form.isBuild());
		form=null;
	}

//	AddonAreaRestoreEvent phsics;
//	@EventHandler(ignoreCancelled=false)
//	public void BlockPhysics(BlockPhysicsEvent ev){
//		if(ev.){
//			phsics = new AddonAreaRestoreEvent(ev.getBlock(), "BlockPhysicsEvent "+ev.getChangedType().name());
//			Bukkit.getPluginManager().callEvent(phsics);
//			ev.setCancelled(!phsics.isBuild());
//			phsics=null;
//		}
//	}
	
//	public boolean check(Player player,Block to,AddonAreaRestoreEvent from,BlockFace face){
//		if(to.getRelative(face).getType()!=to.getType()){
//			if(to.getRelative(face).getTypeId()<=8&&to.getRelative(face).getTypeId()<=11){
//				from=new AddonAreaRestoreEvent(player,BuildType.BREAK,to.getRelative(face), "BlockFromToEvent");
//				Bukkit.getPluginManager().callEvent(from);
//				from=null;
//				return true;
//			}
//		}
//		return false;
//	}
	
	AddonAreaRestoreEvent from;
	@EventHandler(ignoreCancelled=false)
	public void BlockFromTo(BlockFromToEvent ev){
		from = new AddonAreaRestoreEvent(null,BuildType.PLACE,ev.getToBlock(), "BlockFromToEvent");
		Bukkit.getPluginManager().callEvent(from);
		ev.setCancelled(!from.isBuild());
		from=null;
	}

	AddonAreaRestoreEvent bbreak;
	@EventHandler(ignoreCancelled=false)
	public void breakb(BlockBreakEvent ev){
		bbreak = new AddonAreaRestoreEvent(ev.getPlayer(),BuildType.BREAK,ev.getBlock(), "BlockBreakEvent");
		Bukkit.getPluginManager().callEvent(bbreak);
		ev.setCancelled(!bbreak.isBuild());
		bbreak=null;
	}

	AddonAreaRestoreEvent place;
	@EventHandler(ignoreCancelled=false)
	public void place(BlockPlaceEvent ev){
		place = new AddonAreaRestoreEvent(ev.getPlayer(),BuildType.PLACE,ev.getBlock().getLocation(),ev.getBlockReplacedState(), "BlockPlaceEvent");
		Bukkit.getPluginManager().callEvent(place);
		ev.setCancelled(!place.isBuild());
		place=null;
	}
}
