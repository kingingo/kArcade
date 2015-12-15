package me.kingingo.karcade.Game.Multi.Games.SideWars;

import java.io.File;
import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;
import me.kingingo.karcade.Game.Multi.MultiGames;
import me.kingingo.karcade.Game.Multi.Addons.MultiGameArenaRestore;
import me.kingingo.karcade.Game.Multi.Games.MultiTeamGame;
import me.kingingo.kcore.Enum.Team;
import me.kingingo.kcore.PacketAPI.Packets.kPacketPlayOutWorldBorder;
import me.kingingo.kcore.Util.UtilBG;
import me.kingingo.kcore.Util.UtilWorld;

import org.bukkit.Location;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class SideWars extends MultiTeamGame{

	private MultiGameArenaRestore area;
	private kPacketPlayOutWorldBorder packet;
	@Getter
	@Setter
	private char Achse;
	@Getter
	@Setter
	private boolean rotkleiner;
	@Getter
	@Setter
	private boolean blaukleiner;
	@Getter
	@Setter
	private int[][][] MinMax;
	@Getter
	private HashMap<Location,Byte> block = new HashMap<Location,Byte>();

	public SideWars(MultiGames games, String Map,Location pasteLocation,File file) {
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