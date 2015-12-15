package me.kingingo.karcade.Game.Multi.Games.OneInTheChamber;

import java.io.File;

import me.kingingo.karcade.Game.Multi.MultiGames;
import me.kingingo.karcade.Game.Multi.Addons.MultiGameArenaRestore;
import me.kingingo.karcade.Game.Multi.Games.MultiSoloGame;
import me.kingingo.kcore.PacketAPI.Packets.kPacketPlayOutWorldBorder;
import me.kingingo.kcore.Util.UtilBG;
import me.kingingo.kcore.Util.UtilWorld;

import org.bukkit.Location;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class OneInTheChamber extends MultiSoloGame{

	private MultiGameArenaRestore area;
	private kPacketPlayOutWorldBorder packet;

	public OneInTheChamber(MultiGames games, String Map,Location pasteLocation,File file) {
		super(games, Map, pasteLocation);
		this.packet=UtilWorld.createWorldBorder(getPasteLocation(), 125*2, 25, 10);
		UtilBG.setHub("versus");
		setUpdateTo("versus");
		Location ecke1 = getPasteLocation().clone();
		ecke1.setY(255);
		ecke1.add(125, 0, 125);
		Location ecke2 = getPasteLocation().clone();
		ecke2.setY(0);
		ecke2.add(-125, 0, -125);
		this.area=new MultiGameArenaRestore(this, ecke1,ecke2);
		getWorldData().loadSchematic(this, pasteLocation, file);
		
		setBlockBreak(true);
		setBlockPlace(true);
		setDropItem(false);
		setPickItem(false);
		setDropItembydeath(false);
		setFoodlevelchange(true);
		setDamagePvP(false);
		setDamage(false);
		getEntityDamage().add(DamageCause.FALL);
	}

}