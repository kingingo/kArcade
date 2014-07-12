package me.kingingo.karcade;

import java.io.File;

import me.kingingo.karcade.Command.CommandLeiche;
import me.kingingo.karcade.Command.CommandScan;
import me.kingingo.karcade.Command.CommandSend;
import me.kingingo.karcade.Command.CommandStart;
import me.kingingo.kcore.Client.Client;
import me.kingingo.kcore.Command.CommandHandler;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.MySQL.MySQL;
import me.kingingo.kcore.Packet.PacketManager;
import me.kingingo.kcore.Permission.PermissionManager;
import me.kingingo.kcore.Update.Updater;
import me.kingingo.kcore.Util.FileUtil;
import me.kingingo.kcore.Util.UtilBG;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.kcore.memory.MemoryFix;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class kArcade extends JavaPlugin{

	public static kArcadeManager manager;
	public static PermissionManager permManager;
	public static PacketManager pManager;
	private Client c;
	private Updater updater;
	private MySQL mysql;
	public static int id;
	private CommandHandler cmd;
	public static String FilePath;
	
	public void onEnable(){
		long time = System.currentTimeMillis();
		FileUtil.DeleteFolder(new File("map"));
		loadConfig();
		id=getConfig().getInt("Config.Server.ID");
		FilePath=getConfig().getString("Config.File.Path");
		updater=new Updater(this);
		if(id==-1){
			c = new Client(getConfig().getString("Config.Client.Host"),getConfig().getInt("Config.Client.Port"),"TEST-SERVER",this,updater);
		}else{
			c = new Client(getConfig().getString("Config.Client.Host"),getConfig().getInt("Config.Client.Port"),"a"+id,this,updater);
		}
		mysql=new MySQL(getConfig().getString("Config.MySQL.User"),getConfig().getString("Config.MySQL.Password"),getConfig().getString("Config.MySQL.Host"),getConfig().getString("Config.MySQL.DB"),this);
		permManager=new PermissionManager(this,mysql);
		pManager=new PacketManager(this,c);
		manager=new kArcadeManager(this,"ArcadeManager",getConfig().getString("Config.Server.Game"),permManager,mysql,c,pManager);
		cmd=new CommandHandler(this);
		cmd.register(CommandScan.class, new CommandScan(permManager));
		cmd.register(CommandSend.class, new CommandSend(c));
		cmd.register(CommandStart.class, new CommandStart(manager));
		cmd.register(CommandLeiche.class, new CommandLeiche(this));
		new MemoryFix(this);
		manager.DebugLog(time, 21, this.getClass().getName());
	}
	
	public void onDisable(){
		for(Player p : UtilServer.getPlayers()){
			UtilBG.sendToServer(p, manager.getBungeeCord_Fallback_Server(),this);
		}
		mysql.close();
		updater.stop();
		manager.setState(GameState.Restart);
		manager.updateInfo();
		c.disconnect(true);
	}
	
	public void loadConfig()
	  {
		getConfig().addDefault("Config.Server.ID", 1);
	    getConfig().addDefault("Config.Server.Game", "OneInTheChamber");
	    getConfig().addDefault("Config.File.Path", File.separator+"root"+File.separator+"Maps");
		getConfig().addDefault("Config.MySQL.Host", "NONE");
	    getConfig().addDefault("Config.MySQL.DB", "NONE");
	    getConfig().addDefault("Config.MySQL.User", "NONE");
	    getConfig().addDefault("Config.MySQL.Password", "NONE");
	    getConfig().addDefault("Config.Client.Host", "79.133.55.5");
	    getConfig().addDefault("Config.Client.Port", 9051);
	    getConfig().options().copyDefaults(true);
	    saveConfig();
	  }
	
}
