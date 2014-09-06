package me.kingingo.karcade.Command;

import me.kingingo.karcade.kArcadeManager;
import me.kingingo.kcore.Command.CommandHandler.Sender;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.NPC.NPC;
import me.kingingo.kcore.Permission.Permission;
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
		if((Manager.getPermManager().hasPermission(((Player)cs), Permission.START_SERVER)||((Player)cs).isOp())){
			final Player p = (Player)cs;
			
			AnvilGUI gui = new AnvilGUI( p,new AnvilGUI.AnvilClickEventHandler(){

				@Override
				public void onAnvilClick(AnvilClickEvent ev) {
					if(ev.getSlot() == AnvilGUI.AnvilSlot.OUTPUT){
				        try{
				        	Integer i = Integer.valueOf(ev.getName());
				        	Manager.setStart(i);
				        	UtilPlayer.sendMessage(p,Text.PREFIX_GAME.getText(Manager.getTyp().getTyp())+"Die Zeit wurde zu "+i+" geändert!");
				        }catch(NumberFormatException e){
				        	UtilPlayer.sendMessage(p,Text.PREFIX_GAME.getText(Manager.getTyp().getTyp())+"Das ist keine Zahl "+ev.getName());
				        }
					}
				}
				
    		}, Manager.getInstance()
    		);
    		
				 ItemStack renamed = UtilItem.RenameItem(new ItemStack(Material.NAME_TAG), "Zahl");
				 gui.setSlot(AnvilGUI.AnvilSlot.INPUT_LEFT, renamed);
				 gui.setSlot(AnvilGUI.AnvilSlot.OUTPUT, UtilItem.RenameItem(new ItemStack(Material.NAME_TAG), "§aFertig"));

				 gui.open();
			
		}
		return false;
	}

}
