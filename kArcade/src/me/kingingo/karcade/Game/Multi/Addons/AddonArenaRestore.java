package me.kingingo.karcade.Game.Multi.Addons;

import me.kingingo.karcade.Game.Multi.Events.MultiGameAddonAreaRestoreEvent;
import me.kingingo.karcade.Game.Multi.Events.MultiGameAddonAreaRestoreExplosionEvent;
import me.kingingo.kcore.Listener.kListener;

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
import org.bukkit.plugin.java.JavaPlugin;

public class AddonArenaRestore extends kListener{

	public AddonArenaRestore(JavaPlugin instance){
		super(instance,"AddonArenaRestore");
	}
	
	MultiGameAddonAreaRestoreEvent bucket;
	@EventHandler
	public void bucket(PlayerBucketEmptyEvent ev){
		bucket = new MultiGameAddonAreaRestoreEvent(ev.getBlockClicked().getRelative(ev.getBlockFace()), "PlayerBucketEmptyEvent");
		Bukkit.getPluginManager().callEvent(bucket);
		ev.setCancelled(!bucket.isBuild());
		bucket=null;
	}
	
	MultiGameAddonAreaRestoreExplosionEvent explosion;
	@EventHandler
	public void Explose(EntityExplodeEvent ev){
		if(!ev.blockList().isEmpty()){
			explosion = new MultiGameAddonAreaRestoreExplosionEvent(ev.blockList());
			Bukkit.getPluginManager().callEvent(explosion);
			ev.setCancelled(!explosion.isBuild());
			explosion=null;
		}
	}

	MultiGameAddonAreaRestoreEvent burn;
	@EventHandler
	public void BlockBurn(BlockBurnEvent ev){
		burn = new MultiGameAddonAreaRestoreEvent(ev.getBlock(), "BlockBurnEvent");
		Bukkit.getPluginManager().callEvent(burn);
		ev.setCancelled(!burn.isBuild());
		burn=null;
	}

	MultiGameAddonAreaRestoreEvent ignite;
	@EventHandler
	public void BlockIgnite(BlockIgniteEvent ev){
		ignite = new MultiGameAddonAreaRestoreEvent(ev.getBlock(), "BlockIgniteEvent");
		Bukkit.getPluginManager().callEvent(ignite);
		ev.setCancelled(!ignite.isBuild());
		ignite=null;
	}

	MultiGameAddonAreaRestoreEvent fade;
	@EventHandler
	public void BlockFade(BlockFadeEvent ev){
		fade = new MultiGameAddonAreaRestoreEvent(ev.getBlock(), "BlockFadeEvent");
		Bukkit.getPluginManager().callEvent(fade);
		ev.setCancelled(!fade.isBuild());
		fade=null;
	}

	MultiGameAddonAreaRestoreEvent form;
	@EventHandler
	public void BlockForm(BlockFormEvent ev){
		form = new MultiGameAddonAreaRestoreEvent(ev.getBlock(), "BlockFormEvent");
		Bukkit.getPluginManager().callEvent(form);
		ev.setCancelled(!form.isBuild());
		form=null;
	}

	MultiGameAddonAreaRestoreEvent from;
	@EventHandler
	public void BlockFromTo(BlockFromToEvent ev){
		from = new MultiGameAddonAreaRestoreEvent(ev.getToBlock(), "BlockFromToEvent");
		Bukkit.getPluginManager().callEvent(from);
		ev.setCancelled(!from.isBuild());
		from=null;
	}

	MultiGameAddonAreaRestoreEvent bbreak;
	@EventHandler
	public void breakb(BlockBreakEvent ev){
		bbreak = new MultiGameAddonAreaRestoreEvent(ev.getBlock(), "BlockBreakEvent");
		Bukkit.getPluginManager().callEvent(bbreak);
		ev.setCancelled(!bbreak.isBuild());
		bbreak=null;
	}

	MultiGameAddonAreaRestoreEvent place;
	@EventHandler
	public void place(BlockPlaceEvent ev){
		place = new MultiGameAddonAreaRestoreEvent(ev.getBlock().getLocation(),ev.getBlockReplacedState(), "BlockPlaceEvent");
		Bukkit.getPluginManager().callEvent(place);
		ev.setCancelled(!place.isBuild());
		place=null;
	}
}
