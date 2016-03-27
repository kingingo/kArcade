package eu.epicpvp.karcade.Command;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import eu.epicpvp.karcade.kArcadeManager;
import eu.epicpvp.karcade.Game.Single.SingleGame;
import eu.epicpvp.kcore.Command.CommandHandler.Sender;
import eu.epicpvp.kcore.Language.Language;
import eu.epicpvp.kcore.Permission.PermissionType;
import eu.epicpvp.kcore.Util.AnvilGUI;
import eu.epicpvp.kcore.Util.AnvilGUI.AnvilClickEvent;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.kcore.Util.UtilPlayer;

public class CommandForceStart implements CommandExecutor{
	
	kArcadeManager Manager;
	
	public CommandForceStart(kArcadeManager Manager){
		this.Manager=Manager;
	}
	
	@eu.epicpvp.kcore.Command.CommandHandler.Command(command = "fstart", sender = Sender.PLAYER)
	public boolean onCommand(CommandSender cs, Command cmd, String arg2,String[] args) {
		if((Manager.getPermManager().hasPermission(((Player)cs), PermissionType.START_SERVER)||((Player)cs).isOp())){
			final Player p = (Player)cs;
			
			if(!(Manager.getGame() instanceof SingleGame))return false;
		    Manager.getGame().setMin_Players(1);
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
			
		}
		return false;
	}

}
