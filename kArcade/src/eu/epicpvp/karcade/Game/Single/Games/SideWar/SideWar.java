package eu.epicpvp.karcade.Game.Single.Games.SideWar;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerRespawnEvent;

import dev.wolveringer.dataserver.gamestats.GameState;
import dev.wolveringer.dataserver.gamestats.GameType;
import dev.wolveringer.dataserver.gamestats.StatsKey;
import eu.epicpvp.karcade.kArcade;
import eu.epicpvp.karcade.kArcadeManager;
import eu.epicpvp.karcade.Events.RankingEvent;
import eu.epicpvp.karcade.Game.Single.GameMapVote;
import eu.epicpvp.karcade.Game.Single.SingleWorldData;
import eu.epicpvp.karcade.Game.Single.Games.TeamGame;
import eu.epicpvp.kcore.Enum.PlayerState;
import eu.epicpvp.kcore.Util.UtilMath;

public class SideWar extends TeamGame{

	public SideWar(kArcadeManager manager) {
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
		setDamageTeamOther(true);
		setDamageTeamSelf(false);
		setProjectileDamage(true);
		setRespawn(true);
		setBlackFade(false);
		setFoodChange(false);
		setExplosion(false);
		setBlockBreak(true);
		setBlockPlace(true);
		setMin_Players(8);
		setMax_Players(16);
		
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
		 if(getGameList().isPlayerState(ev.getPlayer())==PlayerState.IN){
			 ev.setRespawnLocation( getWorldData().getLocs(getTeam(ev.getPlayer())).get(UtilMath.r(getWorldData().getLocs(getTeam(ev.getPlayer())).size())) );
		 }
	}

}
