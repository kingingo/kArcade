package me.kingingo.karcade;

import java.io.File;

import me.kingingo.karcade.Command.CommandScan;
import me.kingingo.karcade.Command.CommandSend;
import me.kingingo.karcade.Command.CommandStart;
import me.kingingo.kcore.Client.Client;
import me.kingingo.kcore.Command.CommandHandler;
import me.kingingo.kcore.Command.Admin.CommandMuteAll;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.MySQL.MySQL;
import me.kingingo.kcore.Packet.PacketManager;
import me.kingingo.kcore.Permission.PermissionManager;
import me.kingingo.kcore.Update.Updater;
import me.kingingo.kcore.Util.UtilException;
import me.kingingo.kcore.Util.FileUtil;
import me.kingingo.kcore.Util.UtilBG;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.kcore.memory.MemoryFix;

import org.bukkit.Bukkit;
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
		try{
			long time = System.currentTimeMillis();
			loadConfig();
			mysql=new MySQL(getConfig().getString("Config.MySQL.User"),getConfig().getString("Config.MySQL.Password"),getConfig().getString("Config.MySQL.Host"),getConfig().getString("Config.MySQL.DB"),this);
			for(GameType type : GameType.values())FileUtil.DeleteFolder(new File(type.getKürzel().toLowerCase()));
			FileUtil.DeleteFolder(new File("map"));
			id=getConfig().getInt("Config.Server.ID");
			FilePath=getConfig().getString("Config.Server.FilePath");
			updater=new Updater(this);
			if(id==-1){
				c = new Client(getConfig().getString("Config.Client.Host"),getConfig().getInt("Config.Client.Port"),"TEST-SERVER",this,updater);
			}else{
				c = new Client(getConfig().getString("Config.Client.Host"),getConfig().getInt("Config.Client.Port"),"a"+id,this,updater);
			}
			cmd=new CommandHandler(this);
			cmd.register(CommandScan.class, new CommandScan(permManager));
			pManager=new PacketManager(this,c);
			permManager=new PermissionManager(this,pManager,mysql);
			manager=new kArcadeManager(this,"ArcadeManager",getConfig().getString("Config.Server.Game"),permManager,mysql,c,pManager,cmd);
			cmd.register(CommandSend.class, new CommandSend(c));
			cmd.register(CommandStart.class, new CommandStart(manager));
			cmd.register(CommandMuteAll.class, new CommandMuteAll(permManager));
			new MemoryFix(this);
			manager.DebugLog(time, this.getClass().getName());
		}catch(Exception e){
			UtilException.catchException(e, getConfig().getString("Config.Server.ID"), Bukkit.getIp(), mysql);
		}
	}
	
	public void onDisable(){
		for(Player p : UtilServer.getPlayers()){
			UtilBG.sendToServer(p, manager.getBungeeCord_Fallback_Server(),this);
		}
//		if(getConfig().getString("Config.Server.World-Save").equalsIgnoreCase("false")){
//			for(World w : Bukkit.getWorlds()){
//				w.set
//			}
//		}
		for(GameType type : GameType.values())FileUtil.DeleteFolder(new File(type.getKürzel().toLowerCase()));
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
	    getConfig().addDefault("Config.Server.World-Save", false);
	    getConfig().addDefault("Config.Server.FilePath", File.separator+"root"+File.separator+"Maps");
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