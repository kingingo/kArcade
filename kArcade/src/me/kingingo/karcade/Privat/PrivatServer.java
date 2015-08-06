package me.kingingo.karcade.Privat;

import java.io.File;
import java.util.HashMap;

import me.kingingo.karcade.kArcade;
import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Game.Game;
import me.kingingo.karcade.Game.Single.SingleGame;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.Inventory.InventoryBase;
import me.kingingo.kcore.Inventory.InventoryPageBase;
import me.kingingo.kcore.Inventory.Item.ButtonBase;
import me.kingingo.kcore.Inventory.Item.Click;
import me.kingingo.kcore.Language.Language;
import me.kingingo.kcore.Listener.kListener;
import me.kingingo.kcore.Util.Color;
import me.kingingo.kcore.Util.InventorySize;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilServer;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PrivatServer extends kListener{

	private kArcadeManager manager;
	private HashMap<GameType,InventoryPageBase> maps;
	private InventoryBase base;
	private Game game;
	private InventoryPageBase choose_game;
	
	public PrivatServer(final kArcadeManager manager){
		super(manager.getInstance(),"PrivateServer");
		this.manager=manager;
		this.game=(Game)manager.getGame();
		this.maps=new HashMap<GameType,InventoryPageBase>();
		this.base=new InventoryBase(manager.getInstance(), InventorySize._27.getSize(), "Privat Server:");

		this.base.getMain().addButton(0, new ButtonBase(new Click(){

			@Override
			public void onClick(Player player, ActionType action, Object obj) {
				player.closeInventory();
				
				if(game.getMin_Players() <= UtilServer.getPlayers().size()){
					game.setState(GameState.LobbyPhase);
					if(game instanceof SingleGame){
						((SingleGame)game).setStart(15);
					}
				}else{
					player.sendMessage(Language.getText(player, "PREFIX")+Color.RED+"Es sind zu wenig Spieler(min. "+game.getMin_Players()+") online! Wartemodus wird neugestartet!");
				}
			}
			
		}, UtilItem.Item(new ItemStack(Material.EMERALD_BLOCK), new String[]{}, "§aSpiel starten")));
		
		this.base.getMain().addButton(9, new ButtonBase(new Click(){

			@Override
			public void onClick(Player player, ActionType action, Object obj) {
				player.closeInventory();
				game.setState(GameState.Laden);
			}
			
		}, UtilItem.Item(new ItemStack(Material.REDSTONE_BLOCK), new String[]{}, "§cSpiel stoppen")));
		
		for(GameType type : maps.keySet()){
			
			
			
		}

	}
	
	public InventoryPageBase loadMaps(GameType type){
		String folder = kArcade.FilePath+File.separator+type.getTyp();
		
		return null;
	}
}
