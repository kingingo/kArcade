package me.kingingo.karcade.Game.Games.TroubleInMinecraft;

import java.util.ArrayList;
import java.util.HashMap;

import lombok.Getter;
import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Enum.PlayerState;
import me.kingingo.karcade.Enum.Team;
import me.kingingo.karcade.Game.Events.GameStartEvent;
import me.kingingo.karcade.Game.Games.TeamGame;
import me.kingingo.karcade.Game.World.WorldData;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.Minecraft.Test;
import me.kingingo.kcore.PlayerStats.Stats;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.C;
import me.kingingo.kcore.Util.UtilDisplay;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.UtilInv;
import me.kingingo.kcore.Util.UtilMath;
import me.kingingo.kcore.Util.UtilServer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class TroubleInMinecraft extends TeamGame{

	//MINI_CHEST MHF_Chest
	
	WorldData wd;
	@Getter
	private kArcadeManager manager;
	int start=0;
	
	public TroubleInMinecraft(kArcadeManager manager) {
		super(manager);
		this.manager=manager;
		long t = System.currentTimeMillis();
		manager.setState(GameState.Laden);
		manager.setTyp(GameType.TroubleInMinecraft);
		setMin_Players(4);
		setMax_Players(12);
		setDamageTeamSelf(true);
		setDamageSelf(true);
		setItemDrop(false);
		setItemPickup(false);
		setBlockPlace(false);
		setBlockBreak(false);
		setCreatureSpawn(false);
		setHangingBreak(false);
		setBlockSpread(false);
		setDamagePvP(true);
		setExplosion(false);
		setDeathDropItems(true);
		setDamageEvP(false);
		setDamagePvE(true);
		setFoodChange(false);
		setProjectileDamage(true);
		setRespawn(true);
		wd = new WorldData(manager,GameType.TroubleInMinecraft.name());
		wd.Initialize();
		manager.setWorldData(wd);
		setSkull();
		manager.setState(GameState.LobbyPhase);
		manager.DebugLog(t, 31, this.getClass().getName());
	}

	@EventHandler
	public void Chat(PlayerChatEvent ev){
		ev.setCancelled(true);
		Bukkit.broadcastMessage(C.cGray+ev.getPlayer().getDisplayName()+": "+ev.getMessage());
	}
	
	public Team getHaveWinTeam(){
		Team t = Team.INOCCENT;
        for(Player p : getTeamList().keySet()){
        	if(getTeamList().get(p)==Team.TRAITOR){
        		t=Team.TRAITOR;
        		break;
        	}
        }
		return t;
	}
	
	@EventHandler
	public void Shot(EntityShootBowEvent ev){
		if(!ev.getBow().hasItemMeta())return;
		if(!ev.getBow().getItemMeta().hasDisplayName())return;
		if(ev.getBow().getItemMeta().getDisplayName().equalsIgnoreCase( TTT_Item.BOW_MINIGUN.getItem().getItemMeta().getDisplayName() )){
			
		}
	}
	
	@EventHandler
	public void DeathTTT(PlayerDeathEvent ev){
		if(ev.getEntity() instanceof Player){
			getManager().getStats().setInt(((Player)ev.getEntity().getKiller()),getManager().getStats().getInt(Stats.DEATHS, ((Player)ev.getEntity().getKiller()))+1, Stats.DEATHS);
			getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.TTT_DEATH.getText(new String[]{ ((Player)ev.getEntity()).getName(),getTeam(((Player)ev.getEntity())).Name() }));
			delTeam(((Player)ev.getEntity()));
			Test.spawnSleepNPC( ((Player)ev.getEntity()), ((Player)ev.getEntity()).getLocation(), ((Player)ev.getEntity()).getName());
			
			if(ev.getEntity().getKiller() instanceof Player){
				getManager().getStats().setInt(((Player)ev.getEntity().getKiller()),getManager().getStats().getInt(Stats.KILLS, ((Player)ev.getEntity().getKiller()))+1, Stats.KILLS);
			}
		}
	}
	
	@EventHandler
	public void Items(PlayerInteractEvent ev){
		if(UtilEvent.isAction(ev, ActionType.R_BLOCK)){
			if(ev.getClickedBlock().getState() instanceof Skull){
				TTT_Item t = getSkull(ev.getClickedBlock());
				ev.getClickedBlock().setType(Material.AIR);
				if(t==TTT_Item.SCHWERT_IRON||t==TTT_Item.SCHWERT_STONE||t==TTT_Item.SCHWERT_HOLZ){
					for(ItemStack i : ev.getPlayer().getInventory()){
						if(i==null||i.getType()==Material.AIR)continue;
						if(i.hasItemMeta()&&i.getItemMeta().hasDisplayName()){
							if(i.getItemMeta().getDisplayName().equalsIgnoreCase(TTT_Item.SCHWERT_HOLZ.getItem().getItemMeta().getDisplayName())){
								i.setTypeId(0);
								TTT_Item.SCHWERT_HOLZ.setBlock(ev.getClickedBlock());
							}else if(i.getItemMeta().getDisplayName().equalsIgnoreCase(TTT_Item.SCHWERT_STONE.getItem().getItemMeta().getDisplayName())){
								i.setTypeId(0);
								TTT_Item.SCHWERT_STONE.setBlock(ev.getClickedBlock());
							}else if(i.getItemMeta().getDisplayName().equalsIgnoreCase(TTT_Item.SCHWERT_IRON.getItem().getItemMeta().getDisplayName())){
								i.setTypeId(0);
								TTT_Item.SCHWERT_IRON.setBlock(ev.getClickedBlock());
							}
						}
					}
				}else if(t==TTT_Item.BOW_BOGEN||t==TTT_Item.BOW_MINIGUN||t==TTT_Item.BOW_SHOTGUN||t==TTT_Item.BOW_SNIPER){
					for(ItemStack i : ev.getPlayer().getInventory()){
						if(i==null||i.getType()==Material.AIR)continue;
						if(i.hasItemMeta()&&i.getItemMeta().hasDisplayName()){
							if(i.getItemMeta().getDisplayName().equalsIgnoreCase(TTT_Item.BOW_BOGEN.getItem().getItemMeta().getDisplayName())){
								i.setTypeId(0);
								TTT_Item.BOW_BOGEN.setBlock(ev.getClickedBlock());
							}else if(i.getItemMeta().getDisplayName().equalsIgnoreCase(TTT_Item.BOW_MINIGUN.getItem().getItemMeta().getDisplayName())){
								i.setTypeId(0);
								TTT_Item.BOW_MINIGUN.setBlock(ev.getClickedBlock());
							}else if(i.getItemMeta().getDisplayName().equalsIgnoreCase(TTT_Item.BOW_SHOTGUN.getItem().getItemMeta().getDisplayName())){
								i.setTypeId(0);
								TTT_Item.BOW_SHOTGUN.setBlock(ev.getClickedBlock());
							}else if(i.getItemMeta().getDisplayName().equalsIgnoreCase(TTT_Item.BOW_SNIPER.getItem().getItemMeta().getDisplayName())){
								i.setTypeId(0);
								TTT_Item.BOW_SNIPER.setBlock(ev.getClickedBlock());
							}
						}
					}
				}
				
				
				ev.getPlayer().getInventory().addItem(t.getItem());
			}
		}
	}
	
	public TTT_Item getSkull(Block b){
		if(b.getState() instanceof Skull){
			Skull s = (Skull)b.getState();
			switch(s.getOwner()){
			case "VareidePlays": return TTT_Item.SCHWERT_HOLZ;
			case "Nottrex": return TTT_Item.SCHWERT_STONE;
			case "BillTheBuild3r": return TTT_Item.SCHWERT_IRON;
			
			case "KlausurThaler144":return TTT_Item.BOW_MINIGUN;
			case "IntelliJ":return TTT_Item.BOW_SHOTGUN;
			case "Abmahnung":return TTT_Item.BOW_BOGEN;
			case "FallingDiamond":return TTT_Item.BOW_SNIPER;
			}
		}
		return null;
	}
	
	public void setSkull(){
		ArrayList<Location> list = getManager().getWorldData().getLocs(Team.RED.Name());
		int s_h=(list.size()/100)*20;// 20 %
		int s_s=(list.size()/100)*15;// 15 %
		int s_i=(list.size()/100)*55;// 5 %
		
		int b_mg=(list.size()/100)*10;// 10%
		int b_s=(list.size()/100)*15;// 15%
		int b_b=(list.size()/100)*25;// 25%
		int b_sn=(list.size()/100)*10;// 10%
		
		int r;
		for(int i=0; i < 20000; i++){
			if(list.isEmpty())break;
			r=UtilMath.r(list.size());
			if(s_h!=0){
				TTT_Item.SCHWERT_HOLZ.setBlock(list.get(r));
				list.remove(r);
				s_h--;
			}else if(s_s!=0){
				TTT_Item.SCHWERT_STONE.setBlock(list.get(r));
				list.remove(r);
				s_s--;
			}else if(s_i!=0){
				TTT_Item.SCHWERT_IRON.setBlock(list.get(r));
				list.remove(r);
				s_i--;
			}else if(b_mg!=0){
				TTT_Item.BOW_MINIGUN.setBlock(list.get(r));
				list.remove(r);
				b_mg--;
			}else if(b_s!=0){
				TTT_Item.BOW_SHOTGUN.setBlock(list.get(r));
				list.remove(r);
				b_s--;
			}else if(b_b!=0){
				TTT_Item.BOW_BOGEN.setBlock(list.get(r));
				list.remove(r);
				b_b--;
			}else if(b_sn!=0){
				TTT_Item.BOW_SNIPER.setBlock(list.get(r));
				list.remove(r);
				b_sn--;
			}
		}
	}
	
	@EventHandler
	public void inGame(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		if(getManager().getState()!=GameState.InGame)return;
		
		if(isInTeam(Team.INOCCENT)==0&&isInTeam(Team.DETECTIVE)==0){
			getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.TTT_WIN.getText(Team.INOCCENT.Name()));
			
			for(Player p : getTeamList().keySet()){
				if(getTeamList().get(p)==Team.INOCCENT||getTeamList().get(p)==Team.DETECTIVE){
					getManager().getStats().setInt(p,getManager().getStats().getInt(Stats.WIN, p)+1, Stats.WIN);
				}else{
					getManager().getStats().setInt(p,getManager().getStats().getInt(Stats.LOSE, p)+1, Stats.LOSE);
				}
			}
			
			getManager().setState(GameState.Restart);	
		}else if(isInTeam(Team.TRAITOR)==0){
			getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.TTT_WIN.getText(Team.TRAITOR.Name()));
			
			for(Player p : getTeamList().keySet()){
				if(getTeamList().get(p)==Team.INOCCENT||getTeamList().get(p)==Team.DETECTIVE){
					getManager().getStats().setInt(p,getManager().getStats().getInt(Stats.WIN, p)+1, Stats.WIN);
				}else{
					getManager().getStats().setInt(p,getManager().getStats().getInt(Stats.LOSE, p)+1, Stats.LOSE);
				}
			}
			
			getManager().setState(GameState.Restart);
		}
		
		for(Player p : UtilServer.getPlayers())UtilDisplay.displayTextBar(p, Text.GAME_END_IN.getText(start));
		switch(start){
		case 30: getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.GAME_END_IN.getText(start));break;
		case 20: getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.GAME_END_IN.getText(start));break;
		case 15: getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.GAME_END_IN.getText(start));break;
		case 10: getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.GAME_END_IN.getText(start));break;
		case 5: getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.GAME_END_IN.getText(start));break;
		case 4: getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.GAME_END_IN.getText(start));break;
		case 3: getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.GAME_END_IN.getText(start));break;
		case 2: getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.GAME_END_IN.getText(start));break;
		case 1: getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.GAME_END_IN.getText(start));break;
		case 0: 
			Team t = getHaveWinTeam();
			getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.TTT_WIN.getText(t.Name()));
		
			for(Player p : getTeamList().keySet()){
				if(t==Team.TRAITOR){
					if(getTeamList().get(p)==Team.TRAITOR){
						getManager().getStats().setInt(p,getManager().getStats().getInt(Stats.WIN, p)+1, Stats.WIN);
					}else{
						getManager().getStats().setInt(p,getManager().getStats().getInt(Stats.LOSE, p)+1, Stats.LOSE);
					}
				}else{
					if(getTeamList().get(p)==Team.INOCCENT||getTeamList().get(p)==Team.DETECTIVE){
						getManager().getStats().setInt(p,getManager().getStats().getInt(Stats.WIN, p)+1, Stats.WIN);
					}else{
						getManager().getStats().setInt(p,getManager().getStats().getInt(Stats.LOSE, p)+1, Stats.LOSE);
					}
				}
			}
			
			getManager().setState(GameState.Restart);
			getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.GAME_END.getText());
			break;
		}
	}
	
	@EventHandler
	public void Schutzzeit(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		if(getManager().getState()!=GameState.StartGame)return;
		for(Player p : UtilServer.getPlayers())UtilDisplay.displayTextBar(p, Text.SCHUTZZEIT_END_IN.getText(start));
		switch(start){
		case 30: getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.SCHUTZZEIT_END_IN.getText(start));break;
		case 20: getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.SCHUTZZEIT_END_IN.getText(start));break;
		case 15: getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.SCHUTZZEIT_END_IN.getText(start));break;
		case 10: getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.SCHUTZZEIT_END_IN.getText(start));break;
		case 5: getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.SCHUTZZEIT_END_IN.getText(start));break;
		case 4: getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.SCHUTZZEIT_END_IN.getText(start));break;
		case 3: getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.SCHUTZZEIT_END_IN.getText(start));break;
		case 2: getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.SCHUTZZEIT_END_IN.getText(start));break;
		case 1: getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.SCHUTZZEIT_END_IN.getText(start));break;
		case 0: 
			ArrayList<Player> plist = new ArrayList<>();
			for(Player p : UtilServer.getPlayers()){
				plist.add(p);
			}
			PlayerVerteilung(verteilung(),plist);
			getManager().setState(GameState.InGame);
			getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.SCHUTZZEIT_END.getText());
			break;
		}
	}
	
	@EventHandler
	public void Start(GameStartEvent ev){
		start=31;
		getManager().setState(GameState.StartGame);
		HashMap<String,ArrayList<Location>> list = getManager().getWorldData().getLocs();
		
		int r=0;
		for(Player p : UtilServer.getPlayers()){
			getManager().Clear(p);
			getGameList().addPlayer(p,PlayerState.IN);
		}
		
		for(Player p : getTeamList().keySet()){
			r=UtilMath.r(list.get(Team.RED.Name()).size());
			p.teleport(list.get(Team.RED.Name()).get(r));
			list.get(Team.RED.Name()).remove(r);
		}
	}

	public HashMap<Team,Integer> verteilung(){
		HashMap<Team,Integer> list = new HashMap<>();
		list.put(Team.DETECTIVE, getDetective());
		list.put(Team.TRAITOR, getTraitor());
		list.put(Team.INOCCENT,UtilServer.getPlayers().length-(getTraitor()+getDetective()));
		return list;
	}
	
	public int getTraitor(){
		switch(UtilServer.getPlayers().length){
		case 4: return 1;
		case 5: return 1;
		case 6: return 2;
		case 7: return 2;
		case 8: return 2;
		case 9: return 2;
		case 10: return 3;
		case 11: return 3;
		case 12: return 4;
		}
		return 3;
	}
	
	public int getDetective(){
		switch(UtilServer.getPlayers().length){
		case 4: return 1;
		case 5: return 1;
		case 6: return 1;
		case 7: return 1;
		case 8: return 2;
		case 9: return 2;
		case 10: return 3;
		case 11: return 4;
		case 12: return 4;
		}
		return 3;
	}
	
}
