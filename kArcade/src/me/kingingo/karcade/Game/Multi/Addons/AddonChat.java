package me.kingingo.karcade.Game.Multi.Addons;

import me.kingingo.karcade.Game.Multi.Events.MutliGameAddonChatEvent;
import me.kingingo.kcore.Listener.kListener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class AddonChat extends kListener{
	
	public AddonChat(JavaPlugin instance){
		super(instance,"AddonChat");
	}
	
	MutliGameAddonChatEvent chat;
	@EventHandler
	public void chat(AsyncPlayerChatEvent ev){
		chat=new MutliGameAddonChatEvent(ev.getPlayer(),ev.getMessage());
		Bukkit.getPluginManager().callEvent(chat);
		if(chat.isCancelled())ev.setCancelled(true);
	}
	
}
