package me.kingingo.karcade.Game.Games.TroubleInMinecraft.Traitor;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface Shop {
public int getPunkte();
public ItemStack getShopItem();
public void add(Player p);
}
