package me.kingingo.karcade.Command;

import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Game.Single.SingleGame;
import me.kingingo.kcore.Command.CommandHandler.Sender;
import me.kingingo.kcore.Language.Language;
import me.kingingo.kcore.Permission.kPermission;
import me.kingingo.kcore.Util.AnvilGUI;
import me.kingingo.kcore.Util.AnvilGUI.AnvilClickEvent;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilPlayer;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandStart implements CommandExecutor{
	
	kArcadeManager Manager;
	
	public CommandStart(kArcadeManager Manager){
		this.Manager=Manager;
	}
	
	@me.kingingo.kcore.Command.CommandHandler.Command(command = "start", sender = Sender.PLAYER)
	public boolean onCommand(CommandSender cs, Command cmd, String arg2,String[] args) {
			final Player p = (Player)cs;
			if(!p.hasPermission(kPermission.START_SERVER.getPermissionToString()))return false;
			if(!(Manager.getGame() instanceof SingleGame))return false;
			
			if(p.hasPermission(kPermission.START_SERVER_SET_TIME.getPermissionToString())){
				AnvilGUI gui = new AnvilGUI(p,Manager.getInstance(), new AnvilGUI.AnvilClickEventHandler(){

					@Override
					public void onAnvilClick(AnvilClickEvent ev) {
						if(ev.getSlot() == AnvilGUI.AnvilSlot.OUTPUT){
					        try{
					        	Integer i = Integer.valueOf(ev.getName());
					        	((SingleGame)Manager.getGame()).setStart(i);
					        	UtilPlayer.sendMessage(p,Language.getText(p, "PREFIX_GAME", Manager.getGame().getType().getTyp())+Language.getText(p, "GAME_TIME_CHANGE",i));
					        }catch(NumberFormatException e){
					        	UtilPlayer.sendMessage(p,Language.getText(p, "PREFIX_GAME", Manager.getGame().getType().getTyp())+Language.getText(p, "NO_INTEGER"));
					        }
						}
					}
					
	    		});
	    		
					 ItemStack renamed = UtilItem.RenameItem(new ItemStack(Material.NAME_TAG), "Zahl");
					 gui.setSlot(AnvilGUI.AnvilSlot.INPUT_LEFT, renamed);
					 gui.setSlot(AnvilGUI.AnvilSlot.OUTPUT, UtilItem.RenameItem(new ItemStack(Material.NAME_TAG), "§aFertig"));

					 gui.open();
			}else{
				((SingleGame)Manager.getGame()).setStart(10);
	        	UtilPlayer.sendMessage(p,Language.getText(p, "PREFIX_GAME", Manager.getGame().getType().getTyp())+Language.getText(p, "GAME_TIME_CHANGE",10));
			}
		return false;
	}

}
