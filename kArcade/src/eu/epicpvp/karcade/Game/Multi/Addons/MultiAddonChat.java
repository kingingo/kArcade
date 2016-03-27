package eu.epicpvp.karcade.Game.Multi.Addons;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import eu.epicpvp.karcade.Game.Multi.Addons.Evemts.MultiGameAddonChatEvent;
import eu.epicpvp.kcore.Listener.kListener;

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
