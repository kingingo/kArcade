package me.kingingo.karcade.Game.Multi.Addons;

import me.kingingo.karcade.Game.Multi.Addons.Evemts.MultiGameAddonChatEvent;
import me.kingingo.kcore.Listener.kListener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class MultiAddonChat extends kListener{
	
	public MultiAddonChat(JavaPlugin instance){
		super(instance,"AddonChat");
	}
	
	MultiGameAddonChatEvent chat;
	@EventHandler
	public void chat(AsyncPlayerChatEvent ev){
		chat=new MultiGameAddonChatEvent(ev.getPlayer(),ev.getMessage());
		Bukkit.getPluginManager().callEvent(chat);
		if(chat.isCancelled())ev.setCancelled(true);
	}
	
}
