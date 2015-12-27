package me.kingingo.karcade.Game.Single.Games.Masterbuilders;

import java.util.HashMap;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;

import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Game.Events.GameStartEvent;
import me.kingingo.karcade.Game.Single.Addons.AddonArea;
import me.kingingo.karcade.Game.Single.Addons.AddonMainArea;
import me.kingingo.karcade.Game.Single.Addons.AddonPlaceBlockCanBreak;
import me.kingingo.karcade.Game.Single.Events.AddonAreaRestoreEvent;
import me.kingingo.karcade.Game.Single.Games.SoloGame;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.Enum.Team;
import me.kingingo.kcore.Language.Language;
import me.kingingo.kcore.Language.LanguageType;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.Title;
import me.kingingo.kcore.Util.UtilDisplay;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilMath;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.kcore.Util.UtilTime;

public class Masterbuilders extends SoloGame{
	
	private HashMap<Player, Team> area;
	private MasterbuildersType mtype;
	private AddonMainArea mainArea;
	private Buildings building;
	private ItemStack[] items_bewertung=new ItemStack[]{UtilItem.RenameItem(new ItemStack(Material.STAINED_GLASS_PANE,1,(byte)14), "§4Schlecht"),
			UtilItem.RenameItem(new ItemStack(Material.STAINED_GLASS_PANE,1,(byte)10), "§cOkey"),
			UtilItem.RenameItem(new ItemStack(Material.STAINED_GLASS_PANE,1,(byte)4), "§eGut"),
			UtilItem.RenameItem(new ItemStack(Material.STAINED_GLASS_PANE,1,(byte)13), "§ageil"),
			UtilItem.RenameItem(new ItemStack(Material.STAINED_GLASS_PANE,1,(byte)5), "§3Sehr geil"),};
	
	public Masterbuilders(kArcadeManager manager,MasterbuildersType mtype) {
		super(manager);
		registerListener();
		long l = System.currentTimeMillis();
		setTyp(GameType.MASTERBUILDERS);
		setState(GameState.Laden);
		
		setDamage(false);
		setCreatureSpawn(false);
		setExplosion(false);
		setBlockBurn(false);
		setBlockSpread(false);
		setFoodChange(false);
		setItemPickup(false);
		setItemDrop(false);
		setMax_Players(mtype.getMax());
		setMin_Players(mtype.getMin());
		
		this.mtype=mtype;
		this.area=new HashMap<>();
		getManager().DebugLog(l, this.getClass().getName());
	}

	public Team getArea(Team team){
		switch(team){
		case RED:return Team.VILLAGE_RED;
		case BLUE:return Team.VILLAGE_BLUE;
		case YELLOW:return Team.VILLAGE_YELLOW;
		case GREEN:return Team.VILLAGE_GREEN;
		case GRAY:return Team.VILLAGE_GRAY;
		case PINK:return Team.VILLAGE_PINK;
		case ORANGE:return Team.VILLAGE_ORANGE;
		case PURPLE:return Team.VILLAGE_PURPLE;
		case WHITE:return Team.VILLAGE_WHITE;
		case BLACK:return Team.VILLAGE_BLACK;
		case CYAN:return Team.VILLAGE_CYAN;
		case AQUA:return Team.VILLAGE_AQUA;
		default:
		return Team.VILLAGE_RED;
		}
	}
	
	int team=-1;
	@EventHandler
	public void lookTime(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		if(getState()!=GameState.SchutzModus)return;
		if(getStart()<0){
			setStart(20);
			this.team++;
			for(Player player : UtilServer.getPlayers())player.teleport(getWorldData().getLocs(this.mtype.getTeam()[team]).get(0).clone().add(0, 5, 0));
		}
	}
	
	@EventHandler
	public void inGame(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		if(getState()!=GameState.InGame)return;
		setStart(getStart()-1);
		for(Player p : UtilServer.getPlayers())UtilDisplay.displayTextBar(Language.getText(p, "GAME_END_IN", UtilTime.formatSeconds(getStart())), p);
		switch(getStart()){
		case 30: broadcastWithPrefix("BUILD_END_IN",UtilTime.formatSeconds(getStart()));break;
		case 15: broadcastWithPrefix("BUILD_END_IN",UtilTime.formatSeconds(getStart()));break;
		case 10: broadcastWithPrefix("BUILD_END_IN",UtilTime.formatSeconds(getStart()));break;
		case 5: broadcastWithPrefix("BUILD_END_IN",UtilTime.formatSeconds(getStart()));break;
		case 4: broadcastWithPrefix("BUILD_END_IN",UtilTime.formatSeconds(getStart()));break;
		case 3: broadcastWithPrefix("BUILD_END_IN",UtilTime.formatSeconds(getStart()));break;
		case 2: broadcastWithPrefix("BUILD_END_IN",UtilTime.formatSeconds(getStart()));break;
		case 1: broadcastWithPrefix("BUILD_END_IN",UtilTime.formatSeconds(getStart()));break;
		case 0:
			broadcastWithPrefixName("BUILD_END");
			setStart(-1);
			setState(GameState.SchutzModus);
			for(Player player : UtilServer.getPlayers()){
				player.setGameMode(GameMode.ADVENTURE);
			}
			break;
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void addonAreaRestoreEvent(AddonAreaRestoreEvent ev){
		if(GameState.InGame!=getState()){
			ev.setBuild(false);
			ev.setCancelled(true);
		}
	}
	
	@EventHandler
	public void start(GameStartEvent ev){
		this.mainArea=new AddonMainArea(getManager().getInstance());
		this.building=Buildings.values()[UtilMath.r(Buildings.values().length)];
		
		int i=0;
		for(Player player : UtilServer.getPlayers()){
			getManager().Clear(player);
			area.put(player, mtype.getTeam()[i]);
			player.teleport(getWorldData().getLocs(area.get(player)).get(0).clone().add(0, 5, 0));
			new AddonArea(getManager().getInstance(), getWorldData().getLocs(getArea(mtype.getTeam()[i])).get(0), getWorldData().getLocs(getArea(mtype.getTeam()[i])).get(1));
			
			if(Language.getLanguage(player)==LanguageType.GERMAN){
				new Title(null, "§a§l"+this.building.getGerman()).send(player);
			}else{
				new Title(null, "§a§l"+this.building.getEnglish()).send(player);
			}
			
			player.setGameMode(GameMode.CREATIVE);
		}
	}
	
}
