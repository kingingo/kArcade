package me.kingingo.karcade.Game.Games.Falldown;

import lombok.Getter;
import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Game.Games.SoloGame;
import me.kingingo.karcade.Game.World.WorldData;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.GameType;

import org.bukkit.Material;

public class Falldown  extends SoloGame{

	@Getter
	private WorldData worldData;
	@Getter
	private kArcadeManager manager;
	
	public Falldown(kArcadeManager manager) {
		super(manager);
		registerListener();
		long t = System.currentTimeMillis();
		setTyp(GameType.Falldown);
		manager.setState(GameState.Laden);
		this.manager=manager;
		this.worldData=new WorldData(manager,getType());
		setWorldData(getWorldData());
		setCreatureSpawn(true);
		setMin_Players(6);
		setMax_Players(16);
		setDamage(true);
		setDamagePvP(true);
		setDamageEvP(false);
		setDamagePvE(false);
		setDamageSelf(false);
		setBlockSpread(false);
		setCreatureSpawn(false);
		setDeathDropItems(true);
		setBlockBreak(false);
		setBlockPlace(false);
		setItemDrop(true);
		getBlockBreakDeny().add(Material.ENDER_CHEST);
		getBlockBreakDeny().add(Material.CHEST);
		setFoodChange(true);
		setCompassAddon(true);
		setItemPickup(true);
		getWorldData().Initialize();
		manager.setState(GameState.LobbyPhase);
		manager.DebugLog(t, this.getClass().getName());
	}

	
	
}
