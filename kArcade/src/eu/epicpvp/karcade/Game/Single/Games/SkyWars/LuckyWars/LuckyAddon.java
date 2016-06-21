package eu.epicpvp.karcade.Game.Single.Games.SkyWars.LuckyWars;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.google.common.collect.Lists;

import eu.epicpvp.karcade.Game.Single.SingleWorldData;
import eu.epicpvp.karcade.Game.Single.Games.SkyWars.SkyWars;
import eu.epicpvp.karcade.Game.Single.Games.SkyWars.LuckyWars.Items.LuckyItem;
import eu.epicpvp.karcade.Game.Single.Games.SkyWars.LuckyWars.Items.LuckyItemListener;
import eu.epicpvp.karcade.Game.World.Event.WorldDataInitializeEvent;
import eu.epicpvp.karcade.Game.World.Event.WorldDataLoadConfigEvent;
import eu.epicpvp.kcore.Enum.Team;
import eu.epicpvp.kcore.Listener.kListener;
import eu.epicpvp.kcore.Util.UtilBlock;
import eu.epicpvp.kcore.Util.UtilLocation;
import eu.epicpvp.kcore.Util.UtilMath;
import eu.epicpvp.kcore.Util.UtilNumber;
import eu.epicpvp.kcore.Util.UtilServer;
import lombok.Getter;

public class LuckyAddon extends kListener {

	@Getter
	private SkyWars instance;
	private int playerAmount = 3;
	private int normalAmount = 8;
	private double playerDistance = 8;
	private double normalDistance = 20;
	private ArrayList<ItemStack> ignore;
	private ArrayList<LuckyItem> items;
	private double total;

	public LuckyAddon(SkyWars instance, LuckyItem[] items) {
		super(instance.getManager().getInstance(), "LuckyAddon");
		this.instance = instance;
		this.ignore = new ArrayList<>();
		this.ignore.add(new ItemStack(Material.CHEST));
		this.ignore.add(new ItemStack(Material.CARPET));
		this.items = Lists.newArrayList(items);

		for (LuckyItem item : items){
			this.total += item.getChance();
			item.setAddon(this);
		}
		
	}
	
	@EventHandler
	public void bbreak(BlockBreakEvent ev){
		if(ev.getBlock().getType()==Material.SPONGE){
			ev.getBlock().setType(Material.AIR);
			LuckyItem luckyitem = randomItem();
			Location center = UtilBlock.getBlockCenterUP(ev.getBlock().getLocation());
			for(ItemStack i : luckyitem.getItems()){
				Item item = ev.getBlock().getWorld().dropItemNaturally(center, i);
				item.setVelocity(new Vector(0.0D, 0.25D, 0.0D));
			}
		}
	}

	public LuckyItem randomItem() {
		LuckyItem returnItem = null;
		ArrayList<LuckyItem> all_items = new ArrayList<>(this.items);
		double itotal = this.total;
		double add_chance = 0;
		double chance = UtilMath.RandomDouble(0, itotal);

		for (LuckyItem item : all_items) {
			if (add_chance <= chance && (add_chance + item.getChance()) >= chance) {
				returnItem=item;
				all_items.remove(item);
				itotal -= item.getChance();
				break;
			}
			add_chance += item.getChance();
		}

		return returnItem;
	}
	
	@EventHandler
	public void loadConfig(WorldDataLoadConfigEvent ev) {
		ev.setCancelled(true);
		if (ev.getLine()[0].equalsIgnoreCase("LUCKY_PLAYER_AMOUNT")) {
			this.playerAmount = UtilNumber.toInt(ev.getLine()[1]);
		} else if (ev.getLine()[0].equalsIgnoreCase("LUCKY_NORMAL_AMOUNT")) {
			this.normalAmount = UtilNumber.toInt(ev.getLine()[1]);
		} else if (ev.getLine()[0].equalsIgnoreCase("LUCKY_NORMAL_DISTANCE")) {
			this.normalDistance = UtilNumber.toDouble(ev.getLine()[1]);
		} else if (ev.getLine()[0].equalsIgnoreCase("LUCKY_PLAYER_DISTANCE")) {
			this.playerDistance = UtilNumber.toDouble(ev.getLine()[1]);
		} else if (ev.getLine()[0].equalsIgnoreCase("LUCKY_IGNORE")) {
			String[] items = ev.getLine()[1].split(",");

			for (String item : items) {
				if (item.isEmpty())
					continue;
				if (item.contains("-")) {
					String[] i = item.split("-");
					ignore.add(new ItemStack(UtilNumber.toInt(i[0]), 1, UtilNumber.toByte(i[1])));
				} else {
					ignore.add(new ItemStack(UtilNumber.toInt(item)));
				}
			}
		} else
			ev.setCancelled(false);
	}
	
	@EventHandler
	public void load(WorldDataInitializeEvent ev){
		init();
	}

	public void init() {
		long time = System.currentTimeMillis();
		setNormalIsland();

		for (Team playerTeam : instance.getSkyWarsType().getTeam()) setPlayerIsland(playerTeam);
		
		for(LuckyItem item : items)
			if(item instanceof LuckyItemListener)
				((LuckyItemListener)item).register();
			
		for(Player player : UtilServer.getPlayers())player.setResourcePack("http://luckywars.clashmc.de/LuckyWars.zip");
		logMessage("Zeit " + (System.currentTimeMillis() - time) + "ms");
	}

	public void setNormalIsland() {
		SingleWorldData worldData = instance.getWorldData();

		if(worldData.getMap().getLocations().containsKey(Team.DIAMOND)){
			Location loc1 = worldData.getLocs(Team.DIAMOND).get(0);
			Location loc2 = worldData.getLocs(Team.DIAMOND).get(1);

			setIsland(Team.DIAMOND, this.normalAmount, this.normalDistance, loc1, loc2);
		}else{
			logMessage("Team DIAMOND is EMPTY! Cannot place normal LuckyBlocks");
		}
	}

	public void setPlayerIsland(Team playerTeam) {
		SingleWorldData worldData = instance.getWorldData();

		Location loc1 = worldData.getLocs(Team.getTeamToVillageTeam(playerTeam)).get(0);
		Location loc2 = worldData.getLocs(Team.getTeamToVillageTeam(playerTeam)).get(1);

		setIsland(playerTeam, this.playerAmount, this.playerDistance, loc1, loc2);
	}

	public void setIsland(Team t, int amount, double distance, Location ecke1, Location ecke2) {
		int[][] MinMax = UtilLocation.getMinMax(ecke1, ecke2);

		ArrayList<Location> ground = new ArrayList<>();
		for (int x = MinMax[UtilLocation.X][UtilLocation.Min]; x < MinMax[UtilLocation.X][UtilLocation.Max]; x++) {
			for (int z = MinMax[UtilLocation.Z][UtilLocation.Min]; z < MinMax[UtilLocation.Z][UtilLocation.Max]; z++) {
				Block block = UtilBlock.getHighest(ecke1.getWorld(), x, z, ignore);

				if (block != null) {
					ground.add(block.getLocation().add(0, 1, 0));
				}
			}
		}
		ArrayList<Location> placed = new ArrayList<>();

		for (int i = 0; i < amount; i++) {
			if (ground.isEmpty())
				break;
			ArrayList<Location> distances = new ArrayList<>(ground);

			for (Location loc : placed) {
				for (Location dis : ground) {
					if (dis.distance(loc) < distance) {
						distances.remove(dis);
					}
				}
			}
			Location location;
			if (distances.isEmpty()) {
				location = ground.get(UtilMath.r(ground.size()));
			} else {
				location = distances.get(UtilMath.r(distances.size()));
			}

			ground.remove(location);
			placed.add(location);
			location.getBlock().setType(Material.SPONGE);
			logMessage(" DIS:" + distances.size() + " I:" + i + " G:" + ground.size());
		}
		logMessage("A:" + amount + " D:" + distance + " G:" + ground.size() + " P:" + placed.size() + " T:" + t.Name());
	}

}
