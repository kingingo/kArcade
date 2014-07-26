package me.kingingo.karcade.Game.Games.SheepWars;

import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Game.Games.TeamGame;
import me.kingingo.karcade.Game.World.WorldData;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.GameType;

public class SheepWars extends TeamGame{

	WorldData wd;
	
	public SheepWars(kArcadeManager manager){
		super(manager);	
		long t = System.currentTimeMillis();
		manager.setState(GameState.Laden);
		setItemDrop(true);
		setItemPickup(true);
		setCreatureSpawn(false);
		setBlockBurn(false);
		setBlockSpread(false);
		setCompassAddon(true);
		setDamage(true);
		setDamageEvP(false);
		setDamagePvE(true);
		setDamagePvP(true);
		setDamageTeamOther(true);
		setDamageTeamSelf(false);
		setProjectileDamage(true);
		setRespawn(true);
		setBlackFade(false);
		setFoodChange(true);
		setExplosion(true);
		setBlockBreak(true);
		setBlockPlace(true);
		wd=new WorldData(manager,GameType.SheepWars.name());
		wd.Initialize();
		manager.setWorldData(wd);
		manager.DebugLog(t, this.getClass().getName());
		manager.setState(GameState.LobbyPhase);
	}
	
}
