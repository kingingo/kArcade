package eu.epicpvp.karcade.Game.Single.Games.SideWar;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerRespawnEvent;

import eu.epicpvp.datenserver.definitions.dataserver.gamestats.GameState;
import eu.epicpvp.datenserver.definitions.dataserver.gamestats.GameType;
import eu.epicpvp.datenserver.definitions.dataserver.gamestats.StatsKey;
import eu.epicpvp.karcade.kArcade;
import eu.epicpvp.karcade.ArcadeManager;
import eu.epicpvp.karcade.Events.RankingEvent;
import eu.epicpvp.karcade.Game.Single.GameMapVote;
import eu.epicpvp.karcade.Game.Single.SingleWorldData;
import eu.epicpvp.karcade.Game.Single.Games.TeamGame;
import eu.epicpvp.kcore.Enum.PlayerState;
import eu.epicpvp.kcore.Util.UtilMath;

public class SideWar extends TeamGame{

	public SideWar(ArcadeManager manager) {
		super(manager);
		long t = System.currentTimeMillis();
		setTyp(GameType.SideWar);

		setDamage(false);
		setItemDrop(false);
		setItemPickup(false);
		setCreatureSpawn(false);
		setBlockBurn(false);
		setPlayerShearEntity(false);
		setBlockSpread(false);
		setDeathDropItems(false);

		setDamage(false);
		setDamageEvP(false);
		setDamagePvE(false);
		setDamagePvP(false);
		setTeamDamageOtherEnabled(true);
		setTeamDamageSelfEnabled(false);
		setProjectileDamage(true);
		setRespawn(true);
		setBlackFade(false);
		setFoodChange(false);
		setExplosion(false);
		setBlockBreak(true);
		setBlockPlace(true);
		setMinPlayers(8);
		setMaxPlayers(16);

		setWorldData(new SingleWorldData(manager,getType().getTyp(),getType().getShortName()));
		getWorldData().setCleanroomChunkGenerator(true);

		if(kArcade.id==-1){
			getVoteHandler().add(new GameMapVote(getWorldData(), -1));
		}else{
			if(getWorldData().loadZips().size()<3){
				getWorldData().Initialize();
			}else{
				getVoteHandler().add(new GameMapVote(getWorldData(), 3));
			}
		}

		manager.DebugLog(t, this.getClass().getName());
		setState(GameState.LobbyPhase);
	}

	@EventHandler
	public void Ranking(RankingEvent ev){
		getManager().setRanking(StatsKey.WIN);
	}

	@EventHandler
	public void RespawnLocation(PlayerRespawnEvent ev){
		 if(getGameList().isPlayerState(ev.getPlayer())==PlayerState.INGAME){
			 ev.setRespawnLocation( getWorldData().getSpawnLocations(getTeam(ev.getPlayer())).get(UtilMath.randomInteger(getWorldData().getSpawnLocations(getTeam(ev.getPlayer())).size())) );
		 }
	}

}
