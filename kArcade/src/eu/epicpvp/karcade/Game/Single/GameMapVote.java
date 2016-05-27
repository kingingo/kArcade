package eu.epicpvp.karcade.Game.Single;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;

import dev.wolveringer.dataserver.gamestats.GameState;
import eu.epicpvp.karcade.Events.RankingEvent;
import eu.epicpvp.karcade.Game.Events.GamePreStartEvent;
import eu.epicpvp.karcade.Game.Events.GameStartEvent;
import eu.epicpvp.karcade.Game.World.GameMap;
import eu.epicpvp.kcore.Events.ServerStatusUpdateEvent;
import eu.epicpvp.kcore.Inventory.InventoryPageBase;
import eu.epicpvp.kcore.Inventory.Item.Click;
import eu.epicpvp.kcore.Inventory.Item.Buttons.ButtonBase;
import eu.epicpvp.kcore.Listener.kListener;
import eu.epicpvp.kcore.Lists.kSort;
import eu.epicpvp.kcore.Permission.PermissionType;
import eu.epicpvp.kcore.Permission.Events.PlayerLoadPermissionEvent;
import eu.epicpvp.kcore.Scoreboard.Events.PlayerSetScoreboardEvent;
import eu.epicpvp.kcore.Util.InventorySize;
import eu.epicpvp.kcore.Util.UtilEvent.ActionType;
import eu.epicpvp.kcore.Util.UtilInv;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.kcore.Util.UtilNumber;
import eu.epicpvp.kcore.Util.UtilPlayer;
import eu.epicpvp.kcore.Util.UtilScoreboard;
import eu.epicpvp.kcore.Util.UtilServer;
import lombok.Getter;

public class GameMapVote extends kListener{

	@Getter
	private SingleWorldData worldData;
	@Getter
	private InventoryPageBase inventory;
	@Getter
	private InventoryPageBase forceInventory;
	@Getter
	private ArrayList<GameMap> worlds;
	@Getter
	private HashMap<String, Integer> buttons;
	@Getter
	private HashMap<Player, Integer> votes;
	@Getter
	private boolean vote;
	
	public GameMapVote(SingleWorldData worldData) {
		super(worldData.getManager().getInstance(), "GameMapVote");
		this.worldData=worldData;
		this.worlds=new ArrayList<>();
		this.buttons=new HashMap<>();
		this.votes=new HashMap<>();
		this.vote=false;
	}
	
	@EventHandler
	public void use(PlayerInteractEvent ev){
		if(getWorldData().getManager().getGame().getState() == GameState.LobbyPhase){
			if(ev.getPlayer().getItemInHand()!=null){
				if(ev.getPlayer().getItemInHand().getType()==Material.EMPTY_MAP){
					if(vote){
						ev.setCancelled(true);
						ev.getPlayer().openInventory(getInventory());
					}else{
						ev.setCancelled(true);
						ev.getPlayer().getItemInHand().setType(Material.AIR);
					}
				}else if(ev.getPlayer().getItemInHand().getType()==Material.GLOWSTONE_DUST){
					if(vote){
						ev.setCancelled(true);
						ev.getPlayer().openInventory(getForceInventory());
					}else{
						ev.setCancelled(true);
						ev.getPlayer().getItemInHand().setType(Material.AIR);
					}
				}
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void sendPacket(ServerStatusUpdateEvent ev){
		if(vote){
			ev.getPacket().setMots("Voting...");
		}
	}
	
	@EventHandler
	public void Ranking(RankingEvent ev){
		if(vote){
			getWorldData().getManager().getGame().setSet_default_scoreboard(false);
		}
	}
	
	@EventHandler
	public void loadPerm(PlayerLoadPermissionEvent ev){
		if(getWorldData().getManager().getGame().getState() == GameState.LobbyPhase && vote){
			if(ev.getPlayer().isOp())ev.getPlayer().getInventory().addItem(UtilItem.RenameItem(new ItemStack(Material.GLOWSTONE_DUST), "§cForce Map"));
		}
	}
	
	@EventHandler
	public void join(PlayerJoinEvent ev){
		if(getWorldData().getManager().getGame().getState() == GameState.LobbyPhase && vote){
			if(((SingleGame)getWorldData().getManager().getGame()).getStart() < 5){
				((SingleGame)getWorldData().getManager().getGame()).setStart(10);
			}
			
			ev.getPlayer().getInventory().addItem(UtilItem.RenameItem(new ItemStack(Material.EMPTY_MAP), "§eMap Vote"));
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void setBoard(PlayerSetScoreboardEvent ev){
		if(getWorldData().getManager().getGame().getState() == GameState.LobbyPhase && vote)setBoard(ev.getPlayer());
	}

	@EventHandler(priority=EventPriority.LOWEST)
	public void start(GameStartEvent ev){
		if(vote){
			((SingleGame)getWorldData().getManager().getGame()).setStart(10);
			((SingleGame)getWorldData().getManager().getGame()).setState(GameState.LobbyPhase);
			ev.setCancelled(true);
		}
	}
	
	@EventHandler
	public void preStart(GamePreStartEvent ev){
		if(ev.getGame().getMin_Players() <= UtilServer.getPlayers().size() && vote){
			close();
		}
	}
	
	public GameMap close(){
		vote=false;
		inventory.closeInventory();

		ArrayList<kSort> auswertung = new ArrayList<>();
		
		for(GameMap map : worlds){
			auswertung.add(new kSort(map.getMapName(), getAmount(((ButtonBase)inventory.getButton(buttons.get(map.getItem().getItemMeta().getDisplayName()))).getItemStack()) ));
		}
		Collections.sort(auswertung,kSort.DESCENDING);
		GameMap map = null;
		
		for(GameMap m : worlds){
			if(m.getMapName().equalsIgnoreCase(auswertung.get(0).getName())){
				map=m;
				break;
			}
		}
		
		worlds.remove(map);
		for(GameMap m : worlds)getWorldData().Uninitialize(m);
		
		if(map==null){
			logMessage("Close() Map ist null!?"); 
		}
		
		getWorldData().Initialize(map);
		getWorldData().setMap(map);
		getWorldData().getManager().getGame().setSet_default_scoreboard(true);
		for(Player player : UtilServer.getPlayers()){
			UtilInv.remove(player.getInventory(), Material.EMPTY_MAP, (byte)0, 1);
			UtilInv.remove(player.getInventory(), Material.GLOWSTONE_DUST, (byte)0, 1);
		}
		return map;
	}
	
	public void updateBoard(ButtonBase base){
		for(Player player : UtilServer.getPlayers()){
			if(player.getScoreboard() != null){
				if(player.getScoreboard().getObjective(DisplaySlot.SIDEBAR)!=null){
					UtilScoreboard.resetScore(player.getScoreboard(), base.getItemStack().getItemMeta().getDisplayName(), DisplaySlot.SIDEBAR);
					UtilScoreboard.setScore(player.getScoreboard(), base.getItemStack().getItemMeta().getDisplayName(), DisplaySlot.SIDEBAR, getAmount(base.getItemStack()) );
				}
			}
		}
	}
	
	public void setBoard(Player player){
		UtilScoreboard.addBoard(player.getScoreboard(), DisplaySlot.SIDEBAR, UtilScoreboard.getScoreboardDisplayname()+" §8- §c§LMAP VOTE");
		UtilScoreboard.setScore(player.getScoreboard(), " ", DisplaySlot.SIDEBAR, 100);
		for(GameMap map : worlds){
    		UtilScoreboard.setScore(player.getScoreboard(), map.getItem().getItemMeta().getDisplayName(), DisplaySlot.SIDEBAR, getAmount(((ButtonBase)inventory.getButton(buttons.get(map.getItem().getItemMeta().getDisplayName()))).getItemStack()) );
		}
		UtilScoreboard.setScore(player.getScoreboard(), "  ", DisplaySlot.SIDEBAR, -100);
	}
	
	public ItemStack setAmount(ItemStack item,int amount){
		return UtilItem.SetDescriptions(item, new String[]{"§cVotes: "+amount});
	}
	
	public int getAmount(ItemStack item){
		if(item.hasItemMeta()){
			if(!item.getItemMeta().hasLore()){
				item=UtilItem.SetDescriptions(item, new String[]{"§cVotes: 0"});
			}
		}
		
		return UtilNumber.toInt(item.getItemMeta().getLore().get(0).split(" ")[1]);
	}
	
	public void Initialize(int map_amount){

		vote=true;
		GameMapVote instance = this;
		File[] files;
		try {
			files = (map_amount == -1 ? getWorldData().toFiles() : getWorldData().randomMaps(map_amount));
			
			UtilServer.getServer().getScheduler().runTaskAsynchronously(getWorldData().getManager().getInstance(), new Runnable()
		    {
		      public void run()
		      {
		  		File[] files2 = new File[files.length];
		  		int i = 0;
		        for(File file : files){
		        	files2[i]=getWorldData().UnzipWorld(file);
		        	i++;
		        }
		        
		        UtilServer.getServer().getScheduler().runTask(getWorldData().getManager().getInstance(), new Runnable()
		        {
		          public void run(){
		        	GameMap map;
		        	for(File file : files2){
		        		map=new GameMap(null, file, instance.getWorldData());
		        		instance.getWorldData().LoadWorldConfig(map);
		        		instance.getWorlds().add(map);
		        	}
		        	
		        	inventory=new InventoryPageBase(InventorySize.invSize(worlds.size()), "§eMap Vote");
		        	forceInventory=new InventoryPageBase(InventorySize.invSize(worlds.size()), "§cForce Map");
		        	UtilInv.getBase().addPage(inventory);
		        	UtilInv.getBase().addPage(forceInventory);
		        	
		        	Click forceClick = new Click(){

						@Override
						public void onClick(Player player, ActionType action, Object o) {
							GameMap map = null;
							for(GameMap m : worlds){
								if(m.getItem().getItemMeta().getDisplayName().equalsIgnoreCase(((ItemStack)o).getItemMeta().getDisplayName())){
									map=m;
									break;
								}
							}
							
							if(map!=null){
								vote=false;
								worlds.remove(map);
								for(GameMap m : worlds)getWorldData().Uninitialize(m);
								
								getWorldData().Initialize(map);
								getWorldData().setMap(map);
								getWorldData().getManager().getGame().setSet_default_scoreboard(true);
								for(Player p : UtilServer.getPlayers()){
									UtilInv.remove(p.getInventory(), Material.EMPTY_MAP, (byte)0, 1);
									UtilInv.remove(p.getInventory(), Material.GLOWSTONE_DUST, (byte)0, 1);
									UtilPlayer.setScoreboardGemsAndCoins(p, getWorldData().getManager().getGame().getMoney());
								}
								player.closeInventory();
								inventory.closeInventory();
								forceInventory.closeInventory();
							}
						}
						
					};
		        	
					Click click = new Click(){

						@Override
						public void onClick(Player player, ActionType action, Object o) {
							if(action == ActionType.R || action == ActionType.L){
								ButtonBase button;
								int a;
								if(votes.containsKey(player)){
									button = ((ButtonBase)inventory.getButton(votes.get(player)));
									
									a = getAmount(button.getItemStack());
									
									if(player.hasPermission(PermissionType.MAP_TRIBLE_VOTE.getPermissionToString())){
										a-=3;
									}else if(player.hasPermission(PermissionType.MAP_DOUBLE_VOTE.getPermissionToString())){
										a-=2;
									}else{
										a--;
									}
									
									if(a<0)a=0;
									button.setItemStack(setAmount(button.getItemStack(), a));
									
									button.refreshItemStack();
									updateBoard(button);
									votes.remove(player);
								}
								button = ((ButtonBase)inventory.getButton(buttons.get( ((ItemStack)o).getItemMeta().getDisplayName() )));
								
								a = getAmount(button.getItemStack());
								
								if(player.hasPermission(PermissionType.MAP_TRIBLE_VOTE.getPermissionToString())){
									a+=3;
								}else if(player.hasPermission(PermissionType.MAP_DOUBLE_VOTE.getPermissionToString())){
									a+=2;
								}else{
									a++;
								}
								
								button.setItemStack(setAmount(button.getItemStack(), a));
								button.refreshItemStack();
								updateBoard(button);
								votes.put(player, button.getSlot());
							}
						}
						
					};
					
					if(worlds.size()==5){
						inventory.addButton(0, new ButtonBase(click, worlds.get(0).getItem()));
						buttons.put(worlds.get(0).getItem().getItemMeta().getDisplayName(), 0);
						inventory.addButton(2, new ButtonBase(click, worlds.get(1).getItem()));
						buttons.put(worlds.get(1).getItem().getItemMeta().getDisplayName(), 2);
						inventory.addButton(4, new ButtonBase(click, worlds.get(2).getItem()));
						buttons.put(worlds.get(2).getItem().getItemMeta().getDisplayName(), 4);
						inventory.addButton(6, new ButtonBase(click, worlds.get(3).getItem()));
						buttons.put(worlds.get(3).getItem().getItemMeta().getDisplayName(), 6);
						inventory.addButton(8, new ButtonBase(click, worlds.get(4).getItem()));
						buttons.put(worlds.get(4).getItem().getItemMeta().getDisplayName(), 8);
						
						forceInventory.addButton(0, new ButtonBase(forceClick, worlds.get(0).getItem()));
						forceInventory.addButton(2, new ButtonBase(forceClick, worlds.get(1).getItem()));
						forceInventory.addButton(4, new ButtonBase(forceClick, worlds.get(2).getItem()));
						forceInventory.addButton(6, new ButtonBase(forceClick, worlds.get(3).getItem()));
						forceInventory.addButton(8, new ButtonBase(forceClick, worlds.get(4).getItem()));
					}else if(worlds.size()==4){
						inventory.addButton(1, new ButtonBase(click, worlds.get(0).getItem()));
						buttons.put(worlds.get(0).getItem().getItemMeta().getDisplayName(), 1);
						inventory.addButton(3, new ButtonBase(click, worlds.get(1).getItem()));
						buttons.put(worlds.get(1).getItem().getItemMeta().getDisplayName(), 3);
						inventory.addButton(5, new ButtonBase(click, worlds.get(2).getItem()));
						buttons.put(worlds.get(2).getItem().getItemMeta().getDisplayName(), 5);
						inventory.addButton(7, new ButtonBase(click, worlds.get(3).getItem()));
						buttons.put(worlds.get(3).getItem().getItemMeta().getDisplayName(), 7);
						
						forceInventory.addButton(1, new ButtonBase(forceClick, worlds.get(0).getItem()));
						forceInventory.addButton(3, new ButtonBase(forceClick, worlds.get(1).getItem()));
						forceInventory.addButton(5, new ButtonBase(forceClick, worlds.get(2).getItem()));
						forceInventory.addButton(7, new ButtonBase(forceClick, worlds.get(3).getItem()));
					}else if(worlds.size()==3){
						inventory.addButton(2, new ButtonBase(click, worlds.get(0).getItem()));
						buttons.put(worlds.get(0).getItem().getItemMeta().getDisplayName(), 2);
						inventory.addButton(4, new ButtonBase(click, worlds.get(1).getItem()));
						buttons.put(worlds.get(1).getItem().getItemMeta().getDisplayName(), 4);
						inventory.addButton(6, new ButtonBase(click, worlds.get(2).getItem()));
						buttons.put(worlds.get(2).getItem().getItemMeta().getDisplayName(), 6);
						
						forceInventory.addButton(2, new ButtonBase(forceClick, worlds.get(0).getItem()));
						forceInventory.addButton(4, new ButtonBase(forceClick, worlds.get(1).getItem()));
						forceInventory.addButton(6, new ButtonBase(forceClick, worlds.get(2).getItem()));
					}else{
						int slot = 0;
						for(GameMap gmap : worlds){
							inventory.addButton(slot, new ButtonBase(click, gmap.getItem()));
							buttons.put(gmap.getItem().getItemMeta().getDisplayName(), slot);
							forceInventory.addButton(slot, new ButtonBase(forceClick, gmap.getItem()));
							slot++;
						}
					}

					inventory.fill(Material.STAINED_GLASS_PANE, 7);
					forceInventory.fill(Material.STAINED_GLASS_PANE, 7);
		          }
		        });
		      }
		    });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@EventHandler
	public void quit(PlayerQuitEvent ev){
		if(getWorldData().getManager().getGame().getState() == GameState.LobbyPhase && vote){
			if(votes.containsKey(ev.getPlayer())){
				ButtonBase button = ((ButtonBase)inventory.getButton(votes.get(ev.getPlayer())));
				button.getItemStack().setAmount(button.getItemStack().getAmount()-1);
				button.refreshItemStack();
				votes.remove(ev.getPlayer());
			}
		}
	}
}
