package me.kingingo.karcade;

import java.io.File;

import me.kingingo.karcade.Command.CommandForceStart;
import me.kingingo.karcade.Command.CommandScan;
import me.kingingo.karcade.Command.CommandSend;
import me.kingingo.karcade.Command.CommandStart;
import me.kingingo.karcade.Game.Multi.MultiGames;
import me.kingingo.karcade.Game.Multi.Games.MultiGame;
import me.kingingo.kcore.AACHack.AACHack;
import me.kingingo.kcore.Client.Client;
import me.kingingo.kcore.Command.CommandHandler;
import me.kingingo.kcore.Command.Admin.CommandCMDMute;
import me.kingingo.kcore.Command.Admin.CommandChatMute;
import me.kingingo.kcore.Command.Admin.CommandToggle;
import me.kingingo.kcore.Command.Admin.CommandTppos;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.Language.Language;
import me.kingingo.kcore.Listener.BungeeCordFirewall.BungeeCordFirewallListener;
import me.kingingo.kcore.Listener.Command.ListenerCMD;
import me.kingingo.kcore.MySQL.MySQL;
import me.kingingo.kcore.Packet.PacketManager;
import me.kingingo.kcore.Permission.GroupTyp;
import me.kingingo.kcore.Permission.PermissionManager;
import me.kingingo.kcore.Update.Updater;
import me.kingingo.kcore.Util.UtilBG;
import me.kingingo.kcore.Util.UtilException;
import me.kingingo.kcore.Util.UtilFile;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.kcore.Util.UtilWorld;
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
	public static long start_time;
	
	public void onEnable(){
		try{
			start_time = System.currentTimeMillis();
			loadConfig();
			
			mysql=new MySQL(getConfig().getString("Config.MySQL.User"),getConfig().getString("Config.MySQL.Password"),getConfig().getString("Config.MySQL.Host"),getConfig().getString("Config.MySQL.DB"),this);
			Language.load(mysql);
			UtilFile.DeleteFolder(new File("schematics"));
			for(GameType type : GameType.values())UtilFile.DeleteFolder(new File(type.getKürzel()));
			
			id=getConfig().getInt("Config.Server.ID");
			FilePath=getConfig().getString("Config.Server.FilePath");
			saveConfig();
			updater=new Updater(this);
			if(id==-1){
				c = new Client(getConfig().getString("Config.Client.Host"),getConfig().getInt("Config.Client.Port"),"TEST-SERVER",this,updater);
			}else{
				c = new Client(getConfig().getString("Config.Client.Host"),getConfig().getInt("Config.Client.Port"),"a"+id,this,updater);
			}
			cmd=new CommandHandler(this);
			cmd.register(CommandScan.class, new CommandScan(permManager));
			pManager=new PacketManager(this,c);
			permManager=new PermissionManager(this,GroupTyp.GAME,pManager,mysql);
			manager=new kArcadeManager(this,"ArcadeManager",getConfig().getString("Config.Server.Game"),permManager,mysql,c,pManager,cmd);
			cmd.register(CommandSend.class, new CommandSend(c));
			cmd.register(CommandStart.class, new CommandStart(manager));
			cmd.register(CommandCMDMute.class, new CommandCMDMute(this));
			cmd.register(CommandChatMute.class, new CommandChatMute(this));
			cmd.register(CommandToggle.class, new CommandToggle(this));
			cmd.register(CommandForceStart.class, new CommandForceStart(manager));
			cmd.register(CommandTppos.class, new CommandTppos());
			UtilServer.createLagListener(cmd);
			new MemoryFix(this);
			new AACHack("A"+id,mysql, pManager);
			new ListenerCMD(this);
			new BungeeCordFirewallListener(mysql, "a"+id);
			if( !getConfig().getBoolean("Config.Server.World-Save") )UtilWorld.setSave(false);
			manager.DebugLog(start_time, this.getClass().getName());
		}catch(Exception e){
			UtilException.catchException(e, getConfig().getString("Config.Server.ID"), Bukkit.getIp(), mysql);
		}
	}
	
	public void onDisable(){
		for(Player p : UtilServer.getPlayers()){
			UtilBG.sendToServer(p,this);
		}
		for(GameType type : GameType.values())UtilFile.DeleteFolder(new File(type.getKürzel().toLowerCase()));
		mysql.close();
		updater.stop();
		if(manager.getGame() instanceof MultiGames){
			for(MultiGame game : ((MultiGames)manager.getGame()).getGames().values()){
				game.setState(GameState.Restart);
				game.updateInfo();
			}
		}else{
			manager.getGame().updateInfo();
		}
		c.disconnect(true);
	}
	
	public void loadConfig()
	  {
		getConfig().addDefault("Config.Server.ID", 1);
	    getConfig().addDefault("Config.Server.Game", "OneInTheChamber");
	    getConfig().addDefault("Config.Server.World-Save", false);
	    getConfig().addDefault("Config.Server.Word-Parser", false);
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