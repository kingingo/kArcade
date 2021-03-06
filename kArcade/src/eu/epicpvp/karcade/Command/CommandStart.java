package eu.epicpvp.karcade.Command;

import eu.epicpvp.datenserver.definitions.dataserver.gamestats.GameState;
import eu.epicpvp.datenserver.definitions.dataserver.player.LanguageType;
import eu.epicpvp.karcade.ArcadeManager;
import eu.epicpvp.karcade.Game.Single.SingleGame;
import eu.epicpvp.kcore.Command.CommandHandler.Sender;
import eu.epicpvp.kcore.Permission.PermissionType;
import eu.epicpvp.kcore.Translation.TranslationHandler;
import eu.epicpvp.kcore.Util.AnvilGUI;
import eu.epicpvp.kcore.Util.AnvilGUI.AnvilClickEvent;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.kcore.Util.UtilPlayer;
import eu.epicpvp.kcore.Util.UtilServer;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandStart implements CommandExecutor{
	static {
		TranslationHandler.registerFallback(LanguageType.GERMAN, "arcade.command.start.minplayer", "§cEs sind zu wenig Spieler (min. %s0) online!");
		TranslationHandler.registerFallback(LanguageType.ENGLISH, "arcade.command.start.minplayer", "§cNot enough players (min. %s0) online!");
	}

	ArcadeManager Manager;

	public CommandStart(ArcadeManager Manager){
		this.Manager=Manager;
	}

	@eu.epicpvp.kcore.Command.CommandHandler.Command(command = "start", sender = Sender.PLAYER)
	public boolean onCommand(CommandSender cs, Command cmd, String arg2,String[] args) {
			final Player p = (Player)cs;
			if(!p.hasPermission(PermissionType.START_SERVER.getPermissionToString()))return false;
			if(!(Manager.getGame() instanceof SingleGame))return false;

			if(p.hasPermission(PermissionType.START_SERVER_SET_TIME.getPermissionToString())){
				AnvilGUI gui = new AnvilGUI(p,Manager.getInstance(), new AnvilGUI.AnvilClickEventHandler(){

					@Override
					public void onAnvilClick(AnvilClickEvent ev) {
						if(ev.getSlot() == AnvilGUI.AnvilSlot.OUTPUT){
					        try{
					        	Integer i = Integer.valueOf(ev.getName());
					        	((SingleGame)Manager.getGame()).setStart(i);
					        	UtilPlayer.sendMessage(p,TranslationHandler.getText(p, "PREFIX_GAME", Manager.getGame().getType().getTyp())+TranslationHandler.getText(p, "GAME_TIME_CHANGE",i));
					        }catch(NumberFormatException e){
					        	UtilPlayer.sendMessage(p,TranslationHandler.getText(p, "PREFIX_GAME", Manager.getGame().getType().getTyp())+TranslationHandler.getText(p, "NO_INTEGER"));
					        }
						}
					}

	    		});

					 ItemStack renamed = UtilItem.RenameItem(new ItemStack(Material.NAME_TAG), "Zahl");
					 gui.setSlot(AnvilGUI.AnvilSlot.INPUT_LEFT, renamed);
					 gui.setSlot(AnvilGUI.AnvilSlot.OUTPUT, UtilItem.RenameItem(new ItemStack(Material.NAME_TAG), "§aFertig"));

					 gui.open();
			}else{
				SingleGame game = (SingleGame) Manager.getGame();
				if (game.getState() != GameState.LobbyPhase) {
					p.sendMessage("§cDas Spiel ist nicht in der Lobby-Phase!");
				}
				if(game.getMinPlayers() <= UtilServer.getPlayers().size()){
					if (game.getStart() > 16) {
						game.setStart(16);
						UtilPlayer.sendMessage(p, TranslationHandler.getText(p, "PREFIX_GAME", game.getType().getTyp()) + TranslationHandler.getText(p, "GAME_TIME_CHANGE", 15));
					} else {
						p.sendMessage("§cDas Spiel startet bereits gleich.");
					}
				}else{
		        	UtilPlayer.sendMessage(p,TranslationHandler.getText(p, "PREFIX_GAME", game.getType().getTyp())+TranslationHandler.getText(p, "arcade.command.start.minplayer",game.getMinPlayers()));
				}
			}
		return false;
	}

}
