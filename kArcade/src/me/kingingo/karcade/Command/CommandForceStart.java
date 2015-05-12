package me.kingingo.karcade.Command;

import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Game.Single.SingleGame;
import me.kingingo.kcore.Command.CommandHandler.Sender;
import me.kingingo.kcore.Enum.Text;
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

public class CommandForceStart implements CommandExecutor{
	
	kArcadeManager Manager;
	
	public CommandForceStart(kArcadeManager Manager){
		this.Manager=Manager;
	}
	
	@me.kingingo.kcore.Command.CommandHandler.Command(command = "fstart", sender = Sender.PLAYER)
	public boolean onCommand(CommandSender cs, Command cmd, String arg2,String[] args) {
		if((Manager.getPermManager().hasPermission(((Player)cs), kPermission.START_SERVER)||((Player)cs).isOp())){
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
				        	UtilPlayer.sendMessage(p,Text.PREFIX_GAME.getText(Manager.getGame().getType().getTyp())+"Die Zeit wurde zu "+i+" geändert!");
				        }catch(NumberFormatException e){
				        	UtilPlayer.sendMessage(p,Text.PREFIX_GAME.getText(Manager.getGame().getType().getTyp())+"Das ist keine Zahl "+ev.getName());
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
