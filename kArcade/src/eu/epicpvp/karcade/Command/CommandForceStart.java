package eu.epicpvp.karcade.Command;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import eu.epicpvp.karcade.ArcadeManager;
import eu.epicpvp.karcade.Game.Single.SingleGame;
import eu.epicpvp.kcore.Command.CommandHandler.Sender;
import eu.epicpvp.kcore.Permission.PermissionType;
import eu.epicpvp.kcore.Translation.TranslationHandler;
import eu.epicpvp.kcore.Util.AnvilGUI;
import eu.epicpvp.kcore.Util.AnvilGUI.AnvilClickEvent;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.kcore.Util.UtilPlayer;

public class CommandForceStart implements CommandExecutor {

	ArcadeManager manager;

	public CommandForceStart(ArcadeManager Manager) {
		this.manager = Manager;
	}

	@eu.epicpvp.kcore.Command.CommandHandler.Command(command = "fstart", sender = Sender.PLAYER)
	public boolean onCommand(CommandSender cs, Command cmd, String arg2, String[] args) {
		if ((manager.getPermManager().hasPermission(((Player) cs), PermissionType.START_SERVER) || ((Player) cs).isOp())) {
			final Player p = (Player) cs;

			if (!(manager.getGame() instanceof SingleGame))
				return false;
			manager.getGame().setMinPlayers(1);
			AnvilGUI gui = new AnvilGUI(p, manager.getInstance(), new AnvilGUI.AnvilClickEventHandler() {

				@Override
				public void onAnvilClick(AnvilClickEvent ev) {
					if (ev.getSlot() == AnvilGUI.AnvilSlot.OUTPUT) {
						try {
							Integer i = Integer.valueOf(ev.getName());
							((SingleGame) manager.getGame()).setStart(i);
							UtilPlayer.sendMessage(p, TranslationHandler.getText(p, "PREFIX_GAME", manager.getGame().getType().getTyp()) + TranslationHandler.getText(p, "GAME_TIME_CHANGE", i));
						} catch (NumberFormatException e) {
							((SingleGame) manager.getGame()).setStart(16);
							UtilPlayer.sendMessage(p, TranslationHandler.getText(p, "PREFIX_GAME", manager.getGame().getType().getTyp()) + TranslationHandler.getText(p, "NO_INTEGER"));
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
