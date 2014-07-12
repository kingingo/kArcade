package me.kingingo.karcade.Game.Games.TroubleInMinecraft;

import java.util.ArrayList;
import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;
import me.kingingo.karcade.kArcade;
import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Enum.PlayerState;
import me.kingingo.karcade.Enum.Team;
import me.kingingo.karcade.Events.RankingEvent;
import me.kingingo.karcade.Events.WorldLoadEvent;
import me.kingingo.karcade.Game.Events.GameStartEvent;
import me.kingingo.karcade.Game.Games.TeamGame;
import me.kingingo.karcade.Game.World.WorldData;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.Hologram.Hologram;
import me.kingingo.kcore.PlayerStats.Stats;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.C;
import me.kingingo.kcore.Util.UtilDisplay;
import me.kingingo.kcore.Util.UtilEvent;
import me.kingingo.kcore.Util.UtilTime;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.UtilMath;
import me.kingingo.kcore.Util.UtilServer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class TroubleInMinecraft extends TeamGame{
	
	WorldData wd;
	@Getter
	private kArcadeManager manager;
	Tester tester;
	Hologram hm;
	
	public TroubleInMinecraft(kArcadeManager manager) {
		super(manager);
		this.manager=manager;
		long t = System.currentTimeMillis();
		manager.setState(GameState.Laden);
		manager.setTyp(GameType.TroubleInMinecraft);
		setMin_Players(1);
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
		manager.setState(GameState.LobbyPhase);
		manager.DebugLog(t, 84, this.getClass().getName());
	}

	//RED BLOCK = PLAYER SPAWN
	//BLUE = BUTTON
	//GREEN = JOIN
	//MELON = LAMPEN
	//GRAY = GLASS
	
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
			delTeam(((Player)ev.getEntity()));
			
			getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.TTT_DEATH.getText(new String[]{ ((Player)ev.getEntity()).getName(),getTeam(((Player)ev.getEntity())).Name() }));
			
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
				boolean b = false;
				Inventory inv = ev.getPlayer().getInventory();
				ev.getClickedBlock().setType(Material.AIR);
				if(t.getN().equalsIgnoreCase(TTT_Item.SCHWERT_HOLZ.getN())){
					if(inv.contains(TTT_Item.SCHWERT_HOLZ.getItem())){
						for(int i = 0 ; i < inv.getSize(); i++){
							if(inv.getItem(i)==null||inv.getItem(i).getType()==Material.AIR)continue;
							if(inv.getItem(i).getType()==TTT_Item.SCHWERT_HOLZ.getItem().getType()){
								inv.setItem(i, t.getItem());
								b=true;
								TTT_Item.SCHWERT_HOLZ.setBlock(ev.getClickedBlock());
							}
						}
					}else if(inv.contains(TTT_Item.SCHWERT_IRON.getItem())){
						for(int i = 0 ; i < inv.getSize(); i++){
							if(inv.getItem(i)==null||inv.getItem(i).getType()==Material.AIR)continue;
							if(inv.getItem(i).getType()==TTT_Item.SCHWERT_IRON.getItem().getType()){
								inv.setItem(i, t.getItem());
								b=true;
								TTT_Item.SCHWERT_IRON.setBlock(ev.getClickedBlock());
							}
						}
					}else if(inv.contains(TTT_Item.SCHWERT_STONE.getItem())){
						for(int i = 0 ; i < inv.getSize(); i++){
							if(inv.getItem(i)==null||inv.getItem(i).getType()==Material.AIR)continue;
							if(inv.getItem(i).getType()==TTT_Item.SCHWERT_STONE.getItem().getType()){
								inv.setItem(i, t.getItem());
								b=true;
								TTT_Item.SCHWERT_STONE.setBlock(ev.getClickedBlock());
							}
						}
					}
					
//					for(int i = 0 ; i < inv.getSize(); i++){
//						if(inv.getItem(i)==null||inv.getItem(i).getType()==Material.AIR)continue;
//						if(inv.getItem(i).hasItemMeta()&&inv.getItem(i).getItemMeta().hasDisplayName()){
//							if(inv.getItem(i).getItemMeta().getDisplayName().equalsIgnoreCase(TTT_Item.SCHWERT_HOLZ.getItem().getItemMeta().getDisplayName())){
//								inv.setItem(1,t.getItem());
//								b=true;
//								TTT_Item.SCHWERT_HOLZ.setBlock(ev.getClickedBlock());
//							}else if(inv.getItem(i).getItemMeta().getDisplayName().equalsIgnoreCase(TTT_Item.SCHWERT_STONE.getItem().getItemMeta().getDisplayName())){
//								inv.setItem(1,t.getItem());
//								b=true;
//								TTT_Item.SCHWERT_STONE.setBlock(ev.getClickedBlock());
//							}else if(inv.getItem(i).getItemMeta().getDisplayName().equalsIgnoreCase(TTT_Item.SCHWERT_IRON.getItem().getItemMeta().getDisplayName())){
//								inv.setItem(1,t.getItem());
//								b=true;
//								TTT_Item.SCHWERT_IRON.setBlock(ev.getClickedBlock());
//							}
//						}
//					}
				}else if(t.getN().equalsIgnoreCase(TTT_Item.BOW_BOGEN.getN())){
					if(inv.contains(TTT_Item.BOW_BOGEN.getItem())){
						for(int i = 0 ; i < inv.getSize(); i++){
							if(inv.getItem(i)==null||inv.getItem(i).getType()==Material.AIR)continue;
							if(inv.getItem(i).getType()==TTT_Item.BOW_BOGEN.getItem().getType()){
								inv.setItem(i, t.getItem());
								b=true;
								TTT_Item.BOW_BOGEN.setBlock(ev.getClickedBlock());
							}
						}
					}else if(inv.contains(TTT_Item.BOW_MINIGUN.getItem())){
						for(int i = 0 ; i < inv.getSize(); i++){
							if(inv.getItem(i)==null||inv.getItem(i).getType()==Material.AIR)continue;
							if(inv.getItem(i).getType()==TTT_Item.BOW_MINIGUN.getItem().getType()){
								inv.setItem(i, t.getItem());
								b=true;
								TTT_Item.BOW_MINIGUN.setBlock(ev.getClickedBlock());
							}
						}
					}if(inv.contains(TTT_Item.BOW_SHOTGUN.getItem())){
						for(int i = 0 ; i < inv.getSize(); i++){
							if(inv.getItem(i)==null||inv.getItem(i).getType()==Material.AIR)continue;
							if(inv.getItem(i).getType()==TTT_Item.BOW_SHOTGUN.getItem().getType()){
								inv.setItem(i, t.getItem());
								b=true;
								TTT_Item.BOW_SHOTGUN.setBlock(ev.getClickedBlock());
							}
						}
					}if(inv.contains(TTT_Item.BOW_SNIPER.getItem())){
						for(int i = 0 ; i < inv.getSize(); i++){
							if(inv.getItem(i)==null||inv.getItem(i).getType()==Material.AIR)continue;
							if(inv.getItem(i).getType()==TTT_Item.BOW_SNIPER.getItem().getType()){
								inv.setItem(i, t.getItem());
								b=true;
								TTT_Item.BOW_SNIPER.setBlock(ev.getClickedBlock());
							}
						}
					}
				}
				
				
				if(!b)ev.getPlayer().getInventory().addItem(t.getItem());
				ev.getPlayer().updateInventory();
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
	
	public void setSkull(ArrayList<Location> list){
		int s_h=(int)(list.size()*0.2);// 20 %
		int s_s=(int)(list.size()*0.15);// 15 %
		int s_i=(int)(list.size()*0.05);// 5 %
		
		int b_mg=(int)(list.size()*0.1);// 10%
		int b_s=(int)(list.size()*0.15);// 15%
		int b_b=(int)(list.size()*0.25);// 25%
		int b_sn=(int)(list.size()*0.10);// 10%
		
		System.out.println("S_H "+s_h);
		System.out.println("S_s "+s_s);
		System.out.println("S_i "+s_i);
		
		System.out.println("b_mg "+b_mg);
		System.out.println("b_s "+b_s);
		System.out.println("b_b "+b_b);
		System.out.println("b_sn "+b_sn);	
		
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
			}else{
				if(UtilMath.r(1)==0){
					TTT_Item.SCHWERT_HOLZ.setBlock(list.get(r));
				}else{
					TTT_Item.BOW_BOGEN.setBlock(list.get(r));
				}
				list.remove(r);
			}
		}
		
		
	}
	
	@EventHandler
	public void Ranking(RankingEvent ev){
		getManager().setRanking(Stats.TM_KARMA);
	}
	
	@EventHandler
	public void inGame(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		if(getManager().getState()!=GameState.InGame)return;
		getManager().setStart(getManager().getStart()-1);
//		if(isInTeam(Team.INOCCENT)==0&&isInTeam(Team.DETECTIVE)==0){
//			getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.TTT_WIN.getText(Team.INOCCENT.Name()));
//			
//			for(Player p : getTeamList().keySet()){
//				if(getTeamList().get(p)==Team.INOCCENT||getTeamList().get(p)==Team.DETECTIVE){
//					getManager().getStats().setInt(p,getManager().getStats().getInt(Stats.WIN, p)+1, Stats.WIN);
//				}else{
//					getManager().getStats().setInt(p,getManager().getStats().getInt(Stats.LOSE, p)+1, Stats.LOSE);
//				}
//			}
//			
//			getManager().setState(GameState.Restart);	
//		}else if(isInTeam(Team.TRAITOR)==0){
//			getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.TTT_WIN.getText(Team.TRAITOR.Name()));
//			
//			for(Player p : getTeamList().keySet()){
//				if(getTeamList().get(p)==Team.INOCCENT||getTeamList().get(p)==Team.DETECTIVE){
//					getManager().getStats().setInt(p,getManager().getStats().getInt(Stats.WIN, p)+1, Stats.WIN);
//				}else{
//					getManager().getStats().setInt(p,getManager().getStats().getInt(Stats.LOSE, p)+1, Stats.LOSE);
//				}
//			}
//			
//			getManager().setState(GameState.Restart);
//		}
		
		for(Player p : UtilServer.getPlayers())UtilDisplay.displayTextBar(p, Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())));
		switch(getManager().getStart()){
		case 30: getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())));break;
		case 20: getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())));break;
		case 15: getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())));break;
		case 10: getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())));break;
		case 5: getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())));break;
		case 4: getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())));break;
		case 3: getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())));break;
		case 2: getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())));break;
		case 1: getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.GAME_END_IN.getText(UtilTime.formatSeconds(getManager().getStart())));break;
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
	public void JoinHologram(PlayerJoinEvent ev){
		if(getManager().getState()!=GameState.LobbyPhase)return;
		if(hm==null)hm=new Hologram(getManager().getInstance());

		int win = getManager().getStats().getInt(Stats.WIN, ev.getPlayer());
		int lose = getManager().getStats().getInt(Stats.LOSE, ev.getPlayer());
		getManager().getLoc_stats().getWorld().loadChunk(getManager().getLoc_stats().getWorld().getChunkAt(getManager().getLoc_stats()));
		hm.sendText(ev.getPlayer(),getManager().getLoc_stats().add(0, 0.5, 0),new String[]{
		C.cGreen+getManager().getTyp().string()+C.mOrange+C.Bold+" Info",
		"Server: TroubleInMinecraft §a"+kArcade.id,
		"Map: "+wd.getMapName(),
		" ",
		C.cGreen+getManager().getTyp().string()+C.mOrange+C.Bold+" Stats",
		"Rang: "+getManager().getStats().getRank(Stats.WIN, ev.getPlayer()),	
		"Kills: "+getManager().getStats().getInt(Stats.KILLS, ev.getPlayer()),
		"Tode: "+getManager().getStats().getInt(Stats.DEATHS, ev.getPlayer()),
		"Karma: "+getManager().getStats().getInt(Stats.TM_KARMA, ev.getPlayer()),
		"Tests: "+getManager().getStats().getInt(Stats.TM_TESTS, ev.getPlayer()),
		" ",
		"Gespielte Spiele: "+(win+lose),
		"Gewonnene Spiele: "+win,
		"Verlorene Spiele: "+lose
		});
	}
	
	@EventHandler
	public void Schutzzeit(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		if(getManager().getState()!=GameState.StartGame)return;
		getManager().setStart(getManager().getStart()-1);
		for(Player p : UtilServer.getPlayers())UtilDisplay.displayTextBar(p, Text.SCHUTZZEIT_END_IN.getText(getManager().getStart()));
		switch(getManager().getStart()){
		case 30: getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.SCHUTZZEIT_END_IN.getText(getManager().getStart()));break;
		case 20: getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.SCHUTZZEIT_END_IN.getText(getManager().getStart()));break;
		case 15: getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.SCHUTZZEIT_END_IN.getText(getManager().getStart()));break;
		case 10: getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.SCHUTZZEIT_END_IN.getText(getManager().getStart()));break;
		case 5: getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.SCHUTZZEIT_END_IN.getText(getManager().getStart()));break;
		case 4: getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.SCHUTZZEIT_END_IN.getText(getManager().getStart()));break;
		case 3: getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.SCHUTZZEIT_END_IN.getText(getManager().getStart()));break;
		case 2: getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.SCHUTZZEIT_END_IN.getText(getManager().getStart()));break;
		case 1: getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.SCHUTZZEIT_END_IN.getText(getManager().getStart()));break;
		case 0: 
			getManager().setStart(60*20);
			ArrayList<Player> plist = new ArrayList<>();
			for(Player p : UtilServer.getPlayers()){
				plist.add(p);
			}
			PlayerVerteilung(verteilung(),plist);
			getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.SCHUTZZEIT_END.getText());
			getManager().setState(GameState.InGame);
			break;
		}
	}
	
	@EventHandler
	public void World(WorldLoadEvent ev){
		HashMap<String,ArrayList<Location>> list = getManager().getWorldData().getLocs();
		tester = new Tester(this, list.get(Team.BLUE.Name()).get(0),list.get(Team.GREEN.Name()).get(0), list.get(Team.SOLO.Name()), list.get(Team.GRAY.Name()));
		setSkull(list.get(Team.YELLOW.Name()));
	}
	
	@EventHandler
	public void Start(GameStartEvent ev){
		getManager().setStart(31);
		getManager().setState(GameState.StartGame);
		ArrayList<Location> list = getManager().getWorldData().getLocs(Team.RED.Name());
		
		int r=0;
		for(Player p : UtilServer.getPlayers()){
			getManager().Clear(p);
			r=UtilMath.r(list.size());
			p.teleport(list.get(r));
			list.remove(r);
			getGameList().addPlayer(p,PlayerState.IN);
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
		case 12: return 3;
		case 13: return 3;
		case 14: return 4;
		case 15: return 4;
		case 16: return 5;
		case 17: return 5;
		case 18: return 5;
		case 19: return 6;
		case 20: return 6;
		case 21: return 7;
		case 22: return 7;
		case 23: return 8;
		case 24: return 8;
		}
		return -1;
	}
	
	public int getDetective(){
		switch(UtilServer.getPlayers().length){
		case 4: return 1;
		case 5: return 1;
		case 6: return 1;
		case 7: return 1;
		case 8: return 1;
		case 9: return 1;
		case 10: return 2;
		case 11: return 2;
		case 12: return 2;
		case 13: return 2;
		case 14: return 2;
		case 15: return 3;
		case 16: return 3;
		case 17: return 3;
		case 18: return 3;
		case 19: return 4;
		case 20: return 4;
		case 21: return 5;
		case 22: return 5;
		case 23: return 6;
		case 24: return 6;
		}
		return -1;
	}
	
}
