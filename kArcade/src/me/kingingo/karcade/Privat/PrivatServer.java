package me.kingingo.karcade.Privat;

import java.io.File;

import me.kingingo.karcade.kArcade;
import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Enum.GameStateChangeReason;
import me.kingingo.karcade.Privat.Events.PrivatServerSettingEvent;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Listener.kListener;
import me.kingingo.kcore.Packet.Events.PacketReceiveEvent;
import me.kingingo.kcore.Packet.Packets.SERVER_READY;
import me.kingingo.kcore.Packet.Packets.SERVER_SETTINGS;
import me.kingingo.kcore.Util.UtilFile;
import me.kingingo.kcore.kConfig.kConfig;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

public class PrivatServer extends kListener{

	private kArcadeManager manager;
	private File pfad;

	public PrivatServer(kArcadeManager manager){
		super(manager.getInstance(),"PrivateServer");
		this.manager=manager;
		String folder="plugins"+File.separator+manager.getInstance().getPlugin(manager.getInstance().getClass()).getName();
		new File(folder).mkdir();
		this.pfad=new File(folder,"privat.yml");
	
		if(UtilFile.existPath(pfad)){
			kConfig config = new kConfig(pfad);
			if(config.isString("PACKET")){
				SERVER_SETTINGS setting = new SERVER_SETTINGS(config.getString("PACKET"));
				manager.getGame().setApublic(setting.isApublic());
				Bukkit.getPluginManager().callEvent(new PrivatServerSettingEvent(setting));
				manager.getPacketManager().SendPacket(setting.getHub(), new SERVER_READY(setting.getPlayer(),"a"+kArcade.id));
			}
			config.set("PACKET", null);
			config.save();
			manager.getGame().updateInfo();
		}
	}
	
	@EventHandler
	public void Receive(PacketReceiveEvent ev){
		if(ev.getPacket() instanceof SERVER_SETTINGS){
			SERVER_SETTINGS setting = (SERVER_SETTINGS)ev.getPacket();
			manager.getInstance().getConfig().set("Config.Server.Game", setting.getGame());
			manager.getInstance().saveConfig();
			kConfig config = new kConfig(pfad);
			config.set("PACKET", setting.toString());
			config.save();

			manager.getGame().setState(GameState.Restart, GameStateChangeReason.CHANGE_TYPE);
		}
	}
	
}
