package eu.epicpvp.karcade.Game.Single.Games.SkyWars.LuckyWars.Items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import eu.epicpvp.kcore.Util.UtilPlayer;

public class LuckyPotion extends LuckyItem{

	public LuckyPotion(PotionType type,boolean splash, int sec, int staerke, int amount, double chance) {
		super(chance);
		Potion potion = new Potion(type);
		potion.setSplash(splash);
		
		ItemStack item = potion.toItemStack(amount);
		PotionMeta pm = (PotionMeta) item.getItemMeta();
		pm.clearCustomEffects();
		pm.addCustomEffect(UtilPlayer.getPotionEffect(type.getEffectType(), sec, staerke-1), true);
		item.setItemMeta(pm);
		
		setItem(item);
	}

}
