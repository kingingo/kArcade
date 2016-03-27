package eu.epicpvp.karcade.Game.Single.Games.TroubleInMinecraft.Shop;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface IShop {
public int getPunkte();
public ItemStack getShopItem();
public void add(Player p);
}
