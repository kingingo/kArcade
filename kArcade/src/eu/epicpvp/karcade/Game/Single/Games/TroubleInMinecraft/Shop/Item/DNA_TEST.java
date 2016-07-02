package eu.epicpvp.karcade.Game.Single.Games.TroubleInMinecraft.Shop.Item;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import eu.epicpvp.karcade.Game.Single.Games.TroubleInMinecraft.TroubleInMinecraft;
import eu.epicpvp.karcade.Game.Single.Games.TroubleInMinecraft.Shop.IShop;
import eu.epicpvp.kcore.Listener.kListener;
import eu.epicpvp.kcore.NPC.Event.PlayerInteractNPCEvent;
import eu.epicpvp.kcore.Translation.TranslationHandler;
import eu.epicpvp.kcore.Util.UtilInv;
import eu.epicpvp.kcore.Util.UtilItem;
import lombok.Getter;

public class DNA_TEST extends kListener implements IShop{

	@Getter
	private TroubleInMinecraft TTT;
	private ItemStack item = UtilItem.RenameItem(new ItemStack(Material.BLAZE_ROD,1), "§aDNA-TESTER");
	private HashMap<String,String> list = new HashMap<>();
	
	public DNA_TEST(TroubleInMinecraft TTT) {
		super(TTT.getManager().getInstance(), "[DNA-TEST]");
		this.TTT=TTT;
	}

	@Override
	public int getPunkte() {
		return 2;
	}

	@Override
	public ItemStack getShopItem() {
		ItemStack i = UtilItem.RenameItem(new ItemStack(Material.BLAZE_ROD,1), "§aDNA-TESTER §7("+getPunkte()+" Punkte)");
		UtilItem.setLore(i, new String[]{
				"§7Untersucht Leichen auf genauere Informationen."
		});
		return i;
	}
	
	@EventHandler
	public void Death(PlayerDeathEvent ev){
		if(ev.getEntity() instanceof Player&&ev.getEntity().getKiller() instanceof Player){
			System.err.println("SAVE "+((Player)ev.getEntity()).getName().toLowerCase()+" AND "+ev.getEntity().getKiller().getName());
			list.put( ((Player)ev.getEntity()).getName().toLowerCase() , ev.getEntity().getKiller().getName() );
		}
	}

	@EventHandler
	public void Interact(PlayerInteractNPCEvent ev){
		if(!getTTT().getNpclist().containsKey(
				ev.getNpc())
				&&
				UtilItem.ItemNameEquals(
						ev.getPlayer().getItemInHand(), 
						item)){
			UtilInv.remove(ev.getPlayer(), ev.getPlayer().getItemInHand().getType(), ev.getPlayer().getItemInHand().getData().getData(), 1);
			System.out.println("LOOK "+ev.getNpc().getName().toLowerCase()+" ");
			if(!list.containsKey(ev.getNpc().getName().toLowerCase()))return;
			ev.getPlayer().sendMessage(TranslationHandler.getText(ev.getPlayer(), "PREFIX_GAME", TTT.getType().getTyp())+TranslationHandler.getText(ev.getPlayer(), "TTT_DNA_TEST", new String[]{"§a"+ev.getNpc().getName(),"§c"+list.get(ev.getNpc().getName().toLowerCase())}));
		}
	}
	
	@Override
	public void add(Player p) {
		p.getInventory().addItem(item.clone());
	}

}
