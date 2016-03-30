package eu.epicpvp.karcade.Game.Multi.Games.BedWars1vs1;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import dev.wolveringer.dataserver.gamestats.GameState;
import dev.wolveringer.dataserver.gamestats.StatsKey;
import eu.epicpvp.karcade.Game.Multi.MultiGames;
import eu.epicpvp.karcade.Game.Multi.Addons.MultiGameArenaRestore;
import eu.epicpvp.karcade.Game.Multi.Addons.Evemts.BuildType;
import eu.epicpvp.karcade.Game.Multi.Addons.Evemts.MultiAddonBedKingDeathEvent;
import eu.epicpvp.karcade.Game.Multi.Addons.Evemts.MultiGameAddonAreaRestoreEvent;
import eu.epicpvp.karcade.Game.Multi.Addons.Evemts.MultiGameAddonChatEvent;
import eu.epicpvp.karcade.Game.Multi.Events.MultiGamePlayerJoinEvent;
import eu.epicpvp.karcade.Game.Multi.Events.MultiGameStartEvent;
import eu.epicpvp.karcade.Game.Multi.Events.MultiGameStateChangeEvent;
import eu.epicpvp.karcade.Game.Multi.Games.MultiTeamGame;
import eu.epicpvp.kcore.Enum.GameCage;
import eu.epicpvp.kcore.Enum.GameStateChangeReason;
import eu.epicpvp.kcore.Enum.PlayerState;
import eu.epicpvp.kcore.Enum.Team;
import eu.epicpvp.kcore.Language.Language;
import eu.epicpvp.kcore.PacketAPI.Packets.kPacketPlayOutWorldBorder;
import eu.epicpvp.kcore.Update.UpdateType;
import eu.epicpvp.kcore.Update.Event.UpdateEvent;
import eu.epicpvp.kcore.Util.Title;
import eu.epicpvp.kcore.Util.UtilBG;
import eu.epicpvp.kcore.Util.UtilDisplay;
import eu.epicpvp.kcore.Util.UtilInv;
import eu.epicpvp.kcore.Util.UtilMap;
import eu.epicpvp.kcore.Util.UtilPlayer;
import eu.epicpvp.kcore.Util.UtilTime;
import eu.epicpvp.kcore.Util.UtilWorld;
import eu.epicpvp.kcore.Villager.VillagerShop;
import eu.epicpvp.kcore.Villager.Event.VillagerShopEvent;
import lombok.Getter;

public class BedWars1vs1 extends MultiTeamGame{

	private kPacketPlayOutWorldBorder packet;
	@Getter
	private MultiGameArenaRestore area;
	private VillagerShop red;
	private VillagerShop blue;
	private VillagerShop spezial;
	
	public BedWars1vs1(MultiGames games, String Map, Location pasteLocation,File file) {
		super(games, Map, pasteLocation);
		
		setStartCountdown(11);
		this.packet=UtilWorld.createWorldBorder(getPasteLocation(), 125*2, 25, 10);
		Location ecke1 = getPasteLocation().clone();
		ecke1.setY(255);
		ecke1.add(125, 0, 125);
		Location ecke2 = getPasteLocation().clone();
		ecke2.setY(0);
		ecke2.add(-125, 0, -125);
		this.area=new MultiGameArenaRestore(this, ecke1,ecke2);
		this.area.setOnlyBuild(true);
		this.area.setBypass(new ArrayList<>());
		this.area.setBlacklist(new ArrayList<>());
		this.area.getBlacklist().add(Material.BED);
		this.area.getBlacklist().add(Material.BED_BLOCK);
		this.area.getBypass().add(Material.getMaterial(31));
		this.area.getBypass().add(Material.getMaterial(38));
		this.area.getBypass().add(Material.getMaterial(37));
		this.area.getBypass().add(Material.BROWN_MUSHROOM);
		this.area.getBypass().add(Material.RED_MUSHROOM);
		UtilBG.setHub("versus");
		setUpdateTo("versus");
		getWorldData().loadSchematic(this, pasteLocation, file);
		
		setBlockBreak(true);
		setBlockPlace(true);
		setDropItem(true);
		setPickItem(true);
		setDropItembydeath(true);
		setFoodlevelchange(true);
		setDamagePvP(false);
		setDamage(false);
		UtilBedWars1vs1.getAddonBed(games).addMultiGame(this, new Team[]{Team.RED,Team.BLUE});
		UtilBedWars1vs1.getAddonDropItems(getGames()).getGames().add(this);
		
		red=UtilBedWars1vs1.setVillager(UtilBedWars1vs1.getVillagerSpawn(Team.RED), this, EntityType.VILLAGER);
		blue=UtilBedWars1vs1.setVillager(UtilBedWars1vs1.getVillagerSpawn(Team.BLUE), this, EntityType.VILLAGER);
		
		for(Location loc : getWorldData().getLocs(this, Team.BLACK))spezial=UtilBedWars1vs1.setSpezialVillager(loc, this, EntityType.VILLAGER);

		loadMaxTeam();
	}
	
	Team t;
	HashMap<Team,ArrayList<Block>> block = new HashMap<>();
	@EventHandler(priority=EventPriority.HIGHEST)
	public void MultiGameAddonAreaRestore(MultiGameAddonAreaRestoreEvent ev){
		if(area.isInArea(ev.getLocation())){
			ev.setGame(this);
			if(ev.getReplacedState().getBlock().getType()==Material.GLOWSTONE){
				if(ev.getBuildType()==BuildType.BREAK){
					t = getTeam(ev.getPlayer());
					if(block.containsKey(t)){
						if(block.get(t).contains(ev.getReplacedState().getBlock())){
							block.get(t).remove(ev.getReplacedState().getBlock());
							ev.getReplacedState().getBlock().getWorld().dropItem(ev.getReplacedState().getBlock().getLocation().add(0,0.1,0),new ItemStack(Material.GLOWSTONE,1));
							ev.getReplacedState().getBlock().setTypeId(0);
						}
					}
					ev.setBuild(false);
				}else if(ev.getBuildType()==BuildType.PLACE){
					if(!block.containsKey(getTeam(ev.getPlayer())))block.put(getTeam(ev.getPlayer()), new ArrayList<Block>());
					block.get(getTeam(ev.getPlayer())).add(ev.getReplacedState().getBlock());
					ev.getReplacedState().getBlock().getDrops().clear();
				}
			}
		}
	}
	
	@EventHandler
	public void SheepDeath(MultiAddonBedKingDeathEvent ev){
		if(ev.getGame() == this){
			Title t = new Title("","");
			if(ev.getKiller()!=null){
				getGames().getStats().addInt(ev.getKiller(), 1, StatsKey.BEDWARS_ZERSTOERTE_BEDs);
			}
			
			for(Player player : getGameList().getPlayers().keySet()){
				t.setSubtitle(Language.getText(player,"BEDWARS_BED_BROKE", ev.getTeam().getColor()+"§l"+ev.getTeam().Name()));
				t.send(player);
			}
		}
	}
	
	@EventHandler
	public void VillagerShop(VillagerShopEvent ev){
		if(getGameList().getPlayers().containsKey(ev.getPlayer()) && getGameList().isPlayerState(ev.getPlayer())==PlayerState.OUT){
			ev.setCancelled(true);
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void Join(MultiGamePlayerJoinEvent ev){
		if(ev.getGame()!=this)return;
		//Pr§ft ob dieser Spieler f§r die Arena angemeldet ist.
		if(getTeamList().containsKey(ev.getPlayer())){
			//Spieler wird zu der Location des Teams teleportiert
			
			setTimer(-1);
			GameCage gcase = GameCage.getGameCase(ev.getPlayer(), getGames().getManager().getMysql());
			UtilMap.makeQuadrat(null,getWorldData().getLocs(this, getTeamList().get(ev.getPlayer())).get(0).clone().add(0,10, 0), 2, 5,gcase.getGround((byte)UtilInv.GetData(getTeam(ev.getPlayer()).getItem())),gcase.getWall((byte)UtilInv.GetData(getTeam(ev.getPlayer()).getItem())));
			ev.getPlayer().teleport( getGames().getWorldData().getLocs(this, getTeamList().get(ev.getPlayer())).get(0).clone().add(0, 12, 0) );
			ev.setCancelled(true);
			updateInfo();
		}
	}
	
	@EventHandler
	public void inGame(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		if(getState()!=GameState.InGame)return;
		setTimer(getTimer()-1);
		if(getTimer()<0)setTimer((60*15)+1);
		
		for(Player p : getGameList().getPlayers().keySet())UtilDisplay.displayTextBar(Language.getText(p, "GAME_END_IN", UtilTime.formatSeconds(getTimer())), p);
		
		switch(getTimer()){
		case 30: broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getTimer()));break;
		case 15: broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getTimer()));break;
		case 10: broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getTimer()));break;
		case 5: broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getTimer()));break;
		case 4: broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getTimer()));break;
		case 3: broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getTimer()));break;
		case 2: broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getTimer()));break;
		case 1: broadcastWithPrefix("GAME_END_IN", UtilTime.formatSeconds(getTimer()));break;
		case 0:
			broadcastWithPrefix(Language.getText("GAME_END"));
			setState(GameState.Restart,GameStateChangeReason.GAME_END);
			break;
		}
	}
	
	Player v;
	Player a;
	@EventHandler
	public void DeathBedWars(PlayerDeathEvent ev){
		if(ev.getEntity() instanceof Player && getGameList().getPlayers().containsKey( ((Player)ev.getEntity()) )){
			v = (Player)ev.getEntity();
			UtilPlayer.RespawnNow(v, getGames().getManager().getInstance());
			
			if(!UtilBedWars1vs1.getAddonBed().getGames_boolean().get(this).contains(getTeam(v))){
				getGameList().addPlayer(v, PlayerState.OUT);
				getGames().getStats().addInt(v, 1, StatsKey.LOSE);
			}
			
			if(ev.getEntity().getKiller() instanceof Player){
				a = (Player)ev.getEntity().getKiller();
				getGames().getStats().addInt(a,1, StatsKey.KILLS);
				broadcastWithPrefix("KILL_BY", new String[]{v.getName(),a.getName()});
				return;
			}
			broadcastWithPrefix("DEATH", v.getName());
			getGames().getStats().addInt(v,1, StatsKey.DEATHS);
		}
	}
	
	@EventHandler
	public void RespawnLocation(PlayerRespawnEvent ev){
		 if(getGameList().getPlayers().containsKey(ev.getPlayer())&&getGameList().isPlayerState(ev.getPlayer())==PlayerState.IN){
			 ev.setRespawnLocation( getWorldData().getLocs(this,getTeam(ev.getPlayer())).get(0) );
		 }
	}
	
	@EventHandler
	public void chat(MultiGameAddonChatEvent ev){
		if(getGameList().getPlayers().containsKey(ev.getPlayer())){
			ev.setCancelled(true);
			
			for(Player player : getGameList().getPlayers().keySet())
				player.sendMessage(getTeam(ev.getPlayer()).getColor()+ev.getPlayer().getName()+"§8 § §7"+ev.getMessage());
		}
	}
	
	@EventHandler
	public void MultiGameStateChangeBedWars(MultiGameStateChangeEvent ev){
		if(ev.getGame()==this&&ev.getTo()==GameState.Restart){
			ArrayList<Player> list = getGameList().getPlayers(PlayerState.IN);
			if(list.size()==1){
				Player p = list.get(0);
				getGames().getMoney().addInt(p, 12, StatsKey.COINS);
				getGames().getStats().addInt(p, 1, StatsKey.WIN);
				broadcastWithPrefix("GAME_WIN", p.getName());
				new Title("§6§lGEWONNEN").send(p);
			}else if(list.size()==2){
				Player p = list.get(0);
				Player p1 = list.get(1);
				getGames().getMoney().addInt(p, 12, StatsKey.COINS);
				getGames().getStats().addInt(p, 1, StatsKey.WIN);
				getGames().getMoney().addInt(p1, 12, StatsKey.COINS);
				getGames().getStats().addInt(p1, 1, StatsKey.WIN);
				new Title("§6§lGEWONNEN").send(p);
				new Title("§6§lGEWONNEN").send(p1);
			}
		}
	}
	
	@EventHandler
	public void lobby(MultiGameStateChangeEvent ev){
		if(ev.getGame()!=this)return;
		if(ev.getTo()==GameState.Restart){
			if(area!=null)area.restore();
			UtilBedWars1vs1.getAddonBed().refreshMultiGame(this, new Team[]{Team.RED,Team.BLUE});
			setDamagePvP(false);
			setDamage(false);
		}
	}
	
	@EventHandler(priority=EventPriority.NORMAL)
	public void start(MultiGameStartEvent ev){
		if(ev.getGame() == this){
			UtilMap.makeQuadrat(null,getWorldData().getLocs(this, Team.RED).get(0).clone().add(0,10, 0), 2, 5, new ItemStack(Material.AIR,1),null);
			UtilMap.makeQuadrat(null,getWorldData().getLocs(this, Team.BLUE).get(0).clone().add(0,10, 0), 2, 5, new ItemStack(Material.AIR,1),null);
			
			for(Player player : getGameList().getPlayers().keySet()){
				player.closeInventory();
				getGames().getManager().Clear(player);
				UtilPlayer.sendPacket(player, this.packet);
			}

			red.spawn();
			blue.spawn();
			if(spezial!=null)spezial.spawn();
			
			setDamagePvP(true);
			setDamage(true);
			setState(GameState.InGame);
		}
	}
}