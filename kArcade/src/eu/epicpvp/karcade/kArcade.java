package eu.epicpvp.karcade;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import dev.wolveringer.client.connection.ClientType;
import dev.wolveringer.dataserver.gamestats.GameState;
import dev.wolveringer.dataserver.gamestats.GameType;
import eu.epicpvp.karcade.Command.CommandForceStart;
import eu.epicpvp.karcade.Command.CommandScan;
import eu.epicpvp.karcade.Command.CommandStart;
import eu.epicpvp.karcade.Game.Multi.MultiGames;
import eu.epicpvp.karcade.Game.Multi.Games.MultiGame;
import eu.epicpvp.kcore.AACHack.AACHack;
import eu.epicpvp.kcore.Command.CommandHandler;
import eu.epicpvp.kcore.Command.Admin.CommandCMDMute;
import eu.epicpvp.kcore.Command.Admin.CommandChatMute;
import eu.epicpvp.kcore.Command.Admin.CommandToggle;
import eu.epicpvp.kcore.Command.Admin.CommandTppos;
import eu.epicpvp.kcore.Listener.AntiCrashListener.AntiCrashListener;
import eu.epicpvp.kcore.Listener.BungeeCordFirewall.BungeeCordFirewallListener;
import eu.epicpvp.kcore.Listener.Command.ListenerCMD;
import eu.epicpvp.kcore.MySQL.MySQL;
import eu.epicpvp.kcore.Permission.PermissionManager;
import eu.epicpvp.kcore.Permission.Group.GroupTyp;
import eu.epicpvp.kcore.Update.Updater;
import eu.epicpvp.kcore.Util.UtilBG;
import eu.epicpvp.kcore.Util.UtilException;
import eu.epicpvp.kcore.Util.UtilFile;
import eu.epicpvp.kcore.Util.UtilServer;
import eu.epicpvp.kcore.Util.UtilWorld;

public class kArcade extends JavaPlugin{

	public static kArcadeManager manager;
	public static PermissionManager permissionManager;
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
			UtilFile.DeleteFolder(new File("schematics"));
			UtilFile.DeleteFolder(new File("void"));
			for(GameType type : GameType.values())UtilFile.DeleteFolder(new File(type.getShortName()));
			UtilFile.DeleteFolder(new File(".0,AIR"));
			UtilFile.DeleteFolder(new File("world_nether"));
			deleteOldWorlds();
			
			id=getConfig().getInt("Config.Server.ID");
			FilePath=getConfig().getString("Config.Server.FilePath");
			saveConfig();
			updater=new Updater(this);
			UtilServer.createClient(this,(id==-1? ClientType.OTHER : ClientType.ACARDE), getConfig().getString("Config.Client.Host"), getConfig().getInt("Config.Client.Port"), (id==-1 ? "Test-Server" : "a"+id));

			permissionManager=new PermissionManager(this,GroupTyp.GAME);
			cmd=new CommandHandler(this);
			cmd.register(CommandScan.class, new CommandScan(permissionManager));
			manager=new kArcadeManager(this,"ArcadeManager",getConfig().getString("Config.Server.Game"),permissionManager,mysql,UtilServer.getClient(),cmd);
			cmd.register(CommandStart.class, new CommandStart(manager));
			cmd.register(CommandCMDMute.class, new CommandCMDMute(this));
			cmd.register(CommandChatMute.class, new CommandChatMute(this));
			cmd.register(CommandToggle.class, new CommandToggle(this));
			cmd.register(CommandForceStart.class, new CommandForceStart(manager));
			cmd.register(CommandTppos.class, new CommandTppos());
			UtilServer.createLagListener(cmd);
			new AACHack("A"+id,mysql, UtilServer.getClient());
			new ListenerCMD(this);

			new AntiCrashListener(UtilServer.getClient(),this.mysql);
			new BungeeCordFirewallListener(this,UtilServer.getCommandHandler());
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
		for(GameType type : GameType.values())UtilFile.DeleteFolder(new File(type.getShortName().toLowerCase()));
		if(manager.getGame() instanceof MultiGames){
			for(MultiGame game : ((MultiGames)manager.getGame()).getGames().values()){
				game.setState(GameState.Restart);
				game.updateInfo();
			}
		}else{
			manager.getGame().updateInfo();
		}
		UtilServer.disable();
	}

	public void deleteOldWorlds(){
		File folder = UtilFile.getFolder();
		System.err.println("folder: "+folder.getAbsolutePath());
		for(File file : folder.listFiles()){
			if(file.getName().startsWith("worldData_")){
				System.err.println("DELETE: "+file.getName());
				UtilFile.DeleteFolder(file);
			}
		}
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