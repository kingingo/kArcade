package me.kingingo.karcade;

import java.io.File;

import me.kingingo.karcade.Game.World.WorldParser;
import me.kingingo.kcore.Client.Client;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.MySQL.MySQL;
import me.kingingo.kcore.Permission.Permission;
import me.kingingo.kcore.Permission.PermissionManager;
import me.kingingo.kcore.Update.Updater;
import me.kingingo.kcore.Util.FileUtil;
import me.kingingo.kcore.memory.MemoryFix;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class kArcade extends JavaPlugin{

	public static kArcadeManager manager;
	public static PermissionManager permManager;
	private Client c;
	private Updater updater;
	private MySQL mysql;
	
	public void onEnable(){
		long time = System.currentTimeMillis();
		FileUtil.DeleteFolder(new File("map"));
		loadConfig();
		updater=new Updater(this);
		c = new Client(getConfig().getInt("Config.Client.Port"),getConfig().getString("Config.Client.Host"),"SERVER/"+Bukkit.getIp()+":"+Bukkit.getPort(),this,updater);
		mysql=new MySQL(getConfig().getString("Config.MySQL.User"),getConfig().getString("Config.MySQL.Password"),getConfig().getString("Config.MySQL.Host"),getConfig().getString("Config.MySQL.DB"),this);
		permManager=new PermissionManager(this,mysql);
		manager=new kArcadeManager(this,"ArcadeManager",getConfig().getString("Config.Game"),permManager);
		new MemoryFix(this);
		manager.DebugLog(time, 21, this.getClass().getName());
	}
	
	public void onDisable(){
		mysql.close();
		c.disconnect(true);
		updater.stop();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd,
			String commandlabel, String[] args) {
		final Player p = (Player) sender;
		
		if(cmd.getName().equalsIgnoreCase("scan")&& (permManager.hasPermission(p, Permission.ALL_PERMISSION)||p.isOp())){
			WorldParser.Scan(p, args[0]);
		}
		
		if(cmd.getName().equalsIgnoreCase("state")){
			switch(Integer.valueOf(args[0])){
			case 0:
				manager.setState(GameState.DeathMatch);
			case 1:
				manager.setState(GameState.InGame);
			case 2:
				manager.setState(GameState.LobbyPhase);
			case 3:
				manager.setState(GameState.StartGame);
			case 4:
				manager.setState(GameState.Restart);
			}
		}
		
		return false;
	}
	
	public void loadConfig()
	  {
		getConfig().addDefault("Config.MySQL.Host", "NONE");
	    getConfig().addDefault("Config.MySQL.DB", "NONE");
	    getConfig().addDefault("Config.MySQL.User", "NONE");
	    getConfig().addDefault("Config.MySQL.Password", "NONE");
	    getConfig().addDefault("Config.Client.Host", "79.133.55.5");
	    getConfig().addDefault("Config.Client.Port", 9051);
	    getConfig().addDefault("Config.Game", "OneInTheChamber");
	    getConfig().options().copyDefaults(true);
	    saveConfig();
	  }
	
}
