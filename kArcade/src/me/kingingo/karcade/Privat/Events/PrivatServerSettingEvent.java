package me.kingingo.karcade.Privat.Events;

import lombok.Getter;
import me.kingingo.kcore.Packet.Packets.SERVER_SETTINGS;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PrivatServerSettingEvent extends Event{
	private static HandlerList handlers = new HandlerList();
	@Getter
	private SERVER_SETTINGS setting;

	public PrivatServerSettingEvent(SERVER_SETTINGS setting){
		this.setting=setting;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
        return handlers;
    }

}