package me.kingingo.karcade.Game.Games.SheepWars;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Enum.PlayerState;
import me.kingingo.karcade.Enum.Team;
import me.kingingo.karcade.Events.WorldLoadEvent;
import me.kingingo.karcade.Game.Events.GameStartEvent;
import me.kingingo.karcade.Game.Games.TeamGame;
import me.kingingo.karcade.Game.World.WorldData;
import me.kingingo.karcade.Game.addons.AddonQuadratGrenze;
import me.kingingo.karcade.Game.addons.AddonVoteTeam;
import me.kingingo.karcade.Util.UtilSchematic;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.Pet.PetManager;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.C;
import me.kingingo.kcore.Util.UtilDisplay;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilLocation;
import me.kingingo.kcore.Util.UtilMath;
import me.kingingo.kcore.Util.UtilServer;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class Rush_Example extends TeamGame{

	AddonQuadratGrenze wand;
	UtilSchematic schematic;
	Location red;
	Location green;
	Location yellow;
	Location blue;
	Location drop_red;
	Location drop_yellow;
	Location drop_green;
	Location drop_blue;
	HashMap<Team,ArrayList<Location>> villager = new HashMap<>();
	WorldData wd;
	PetManager PetManager;
	long t;
	
	public Rush_Example(kArcadeManager manager) {
		super(manager);
		t = System.currentTimeMillis();
		manager.setTyp(GameType.Rush);
		manager.setState(GameState.Laden);
		manager.setTyp(GameType.Rush);
		setMin_Players(1);
		setMax_Players(16);
		setCompassAddon(true);
		setDamageTeamSelf(false);
		setDamagePvE(true);
		getManager().setStart(3601);
		setItemDrop(true);
		setItemPickup(true);
		getBlockBreakAllow().add(Material.BEDROCK);
		getAllowSpawnCreature().add(CreatureType.SHEEP);
		getAllowSpawnCreature().add(CreatureType.VILLAGER);
		setVoteTeam(new AddonVoteTeam(manager,new Team[]{Team.RED,Team.YELLOW,Team.GREEN,Team.BLUE},9,4));
		wd = new WorldData(manager,GameType.Rush.name());
		wd.Initialize();
		manager.setWorldData(wd);
	}
	
	public void onDisable(){
		getManager().setState(GameState.Restart);
	} 
	
	@EventHandler
	public void Death(EntityDeathEvent ev){
		Entity e = ev.getEntity();
		if(e.getType()==EntityType.SHEEP){
			Sheep s = (Sheep)e;
			Team t = getColorTeam(s.getColor());
			ev.setDroppedExp(0);
			ev.getDrops().clear();
			switch(t){
			case RED:
				red=null;
				for(Player p : getPlayerFrom(Team.RED)){
					getGameList().addPlayer(p, PlayerState.OUT);
				}
				getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.TEAM_OUT.getText("RED"));
				break;
			case YELLOW:
				yellow=null;
				for(Player p : getPlayerFrom(Team.YELLOW)){
					getGameList().addPlayer(p, PlayerState.OUT);
				}
				getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.TEAM_OUT.getText("YELLOW"));
				break;
			case GREEN:
				green=null;
				for(Player p : getPlayerFrom(Team.GREEN)){
					getGameList().addPlayer(p, PlayerState.OUT);
				}
				getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.TEAM_OUT.getText("GREEN"));
				break;
			case BLUE:
				blue=null;
				for(Player p : getPlayerFrom(Team.BLUE)){
					getGameList().addPlayer(p, PlayerState.OUT);
				}
				getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.TEAM_OUT.getText("BLUE"));
				break;
			default:
				
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void Damage(EntityDamageByEntityEvent ev){
		if(ev.getEntity() instanceof Sheep && ev.getDamager() instanceof Player){
			Sheep s = (Sheep)ev.getEntity();
			Player p = (Player)ev.getDamager();
			Team t = getColorTeam(s.getColor());
			if(t==null){
				ev.setCancelled(true);
			}else
			if(t!=null&&t==getTeam(p)){
				ev.setCancelled(true);
			}else{
				s.setVelocity(new Vector(0,0,0));
				ev.setCancelled(false);
			}
		}
	}
	
	public Team getColorTeam(DyeColor c){
		switch(c){
		case RED:return Team.RED;
		case YELLOW:return Team.YELLOW;
		case BLUE:return Team.BLUE;
		case GREEN: return Team.GREEN;
		}
		
		return null;
	}
	
	@EventHandler
	public void World(WorldLoadEvent ev){
		schematic = new UtilSchematic();
		wand = new AddonQuadratGrenze(getManager(),wd.getLocs().get(Team.SOLO.Name()).get(0),500);
		setPlate();
		getManager().setState(GameState.LobbyPhase);
		getManager().DebugLog(t, this.getClass().getName());
	}
	
	public void SpawnSheeps(){
		if(PetManager==null)PetManager=new PetManager(getManager().getInstance());
		Sheep y = (Sheep)PetManager.AddPetWithOutOwner(C.cYellow+"Gelbes-Schaf",EntityType.SHEEP, yellow.add(0, 2.5, 0));
		y.setColor(DyeColor.YELLOW);
		y.setMaxHealth(100);
		y.setHealth(100);
		Sheep b = (Sheep)PetManager.AddPetWithOutOwner(C.cBlue+"Blaues-Schaf",EntityType.SHEEP, blue.add(0, 2.5, 0));
		b.setColor(DyeColor.BLUE);
		b.setMaxHealth(100);
		b.setHealth(100);
		Sheep g = (Sheep)PetManager.AddPetWithOutOwner(C.cGreen+"Gruenes-Schaf",EntityType.SHEEP, green.add(0, 2.5, 0));
		g.setColor(DyeColor.GREEN);
		g.setMaxHealth(100);
		g.setHealth(100);
		Sheep r = (Sheep)PetManager.AddPetWithOutOwner(C.cRed+"Rotes-Schaf",EntityType.SHEEP, red.add(0, 2.5, 0));
		r.setColor(DyeColor.RED);
		r.setMaxHealth(100);
		r.setHealth(100);
	}
	
	public void setPlate(){
		ArrayList<Location> list = UtilLocation.LocWithBorder(Bukkit.getWorld("map"),4,70,160,wand.MaxX()-16,wand.MinX()+16,wand.MaxZ()-16,wand.MinZ()+16,90,70);
		List<Block> blist;
		HashMap<String,ArrayList<Location>> locs=wd.getLocs();
		for(Location loc : list){
			if(red==null){
				red=loc;
				if(schematic==null)schematic = new UtilSchematic();
				schematic.pastePlate(red, new File(File.separator+"root"+File.separator+"Maps"+File.separator+"schematics"+File.separator+"rush.schematic"));
				blist=UtilLocation.getScans(20,red);
				for(Block b : blist){
					if(b.getType()==Material.WOOL){
						if(!locs.containsKey(Team.RED.Name()))locs.put(Team.RED.Name(), new ArrayList<Location>());
						b.setType(Material.AIR);
						locs.get(Team.RED.Name()).add(b.getLocation().add(0,1,0));
					}else if(b.getType()==Material.MELON_BLOCK){
						b.setType(Material.AIR);
						drop_red=b.getLocation().add(0, -2, 0);
					}else if(b.getType()==Material.GLOWSTONE){
						b.setType(Material.AIR);
						if(!villager.containsKey(Team.RED))villager.put(Team.RED, new ArrayList<Location>());
						villager.get(Team.RED).add(b.getLocation());
					}
				}
				for(Block b : blist){
					if(b.getType()==Material.SPONGE){
						b.setType(Material.WOOL);
						b.setData((byte)14);
					}
				}
			}else if(green==null){
				green=loc;
				schematic.pastePlate(loc, new File(File.separator+"root"+File.separator+"Maps"+File.separator+"schematics"+File.separator+"rush.schematic"));
				blist=UtilLocation.getScans(20,loc);
				for(Block b : blist){
					if(b.getType()==Material.WOOL){
						if(!locs.containsKey(Team.GREEN.Name()))locs.put(Team.GREEN.Name(), new ArrayList<Location>());
						b.setType(Material.AIR);
						locs.get(Team.GREEN.Name()).add(b.getLocation().add(0,1,0));
					}else if(b.getType()==Material.MELON_BLOCK){
						b.setType(Material.AIR);
						drop_green=b.getLocation().add(0, -2, 0);
					}else if(b.getType()==Material.GLOWSTONE){
						b.setType(Material.AIR);
						if(!villager.containsKey(Team.GREEN))villager.put(Team.GREEN, new ArrayList<Location>());
						villager.get(Team.GREEN).add(b.getLocation());
					}
				}
				for(Block b : blist){
					if(b.getType()==Material.SPONGE){
						b.setType(Material.WOOL);
						b.setData((byte)5);
					}
				}
			}else if(yellow==null){
				yellow=loc;
				schematic.pastePlate(loc, new File(File.separator+"root"+File.separator+"Maps"+File.separator+"schematics"+File.separator+"rush.schematic"));
				blist=UtilLocation.getScans(20,loc);
				for(Block b : blist){
					if(b.getType()==Material.WOOL){
						if(!locs.containsKey(Team.YELLOW.Name()))locs.put(Team.YELLOW.Name(), new ArrayList<Location>());
						b.setType(Material.AIR);
						locs.get(Team.YELLOW.Name()).add(b.getLocation().add(0,1,0));
					}else if(b.getType()==Material.MELON_BLOCK){
						b.setType(Material.AIR);
						drop_yellow=b.getLocation().add(0, -2, 0);
					}else if(b.getType()==Material.GLOWSTONE){
						b.setType(Material.AIR);
						if(!villager.containsKey(Team.YELLOW))villager.put(Team.YELLOW, new ArrayList<Location>());
						villager.get(Team.YELLOW).add(b.getLocation());
					}
				}
				for(Block b : blist){
					if(b.getType()==Material.SPONGE){
						b.setType(Material.WOOL);
						b.setData((byte)4);
					}
				}
			}else if(blue==null){
				blue=loc;
				schematic.pastePlate(loc, new File(File.separator+"root"+File.separator+"Maps"+File.separator+"schematics"+File.separator+"rush.schematic"));
				blist=UtilLocation.getScans(20,loc);
				for(Block b : blist){
					if(b.getType()==Material.WOOL){
						if(!locs.containsKey(Team.BLUE.Name()))locs.put(Team.BLUE.Name(), new ArrayList<Location>());
						b.setType(Material.AIR);
						locs.get(Team.BLUE.Name()).add(b.getLocation().add(0,1,0));
					}else if(b.getType()==Material.MELON_BLOCK){
						b.setType(Material.AIR);
						drop_blue=b.getLocation().add(0, -2, 0);
					}else if(b.getType()==Material.GLOWSTONE){
						b.setType(Material.AIR);
						if(!villager.containsKey(Team.BLUE))villager.put(Team.BLUE, new ArrayList<Location>());
						villager.get(Team.BLUE).add(b.getLocation());
					}
				}
				for(Block b : blist){
					if(b.getType()==Material.SPONGE){
						b.setType(Material.WOOL);
						b.setData((byte)11);
					}
				}
			}
		}
		wd.setLocs(locs);
	}
	
	@EventHandler
	public void Drop(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		DropItem(drop_red);
		DropItem(drop_blue);
		DropItem(drop_green);
		DropItem(drop_yellow);
	}
	
	public static void DropItem(Location l){
		int i = UtilMath.RandomInt(10,0);
		l.getWorld().dropItem(l, UtilItem.RenameItem(new ItemStack(Material.CLAY_BRICK,UtilMath.RandomInt(3,1)), "§bBronze"));
		switch(i){
		case 2:
			l.getWorld().dropItem(l, UtilItem.RenameItem(new ItemStack(Material.IRON_INGOT,UtilMath.RandomInt(2,1)), "§bSilver"));
			break;
		case 6:
			l.getWorld().dropItem(l, UtilItem.RenameItem(new ItemStack(Material.IRON_INGOT,UtilMath.RandomInt(2,1)), "§bSilver"));
			break;
		case 4:
			l.getWorld().dropItem(l, UtilItem.RenameItem(new ItemStack(Material.GOLD_INGOT,1), "§bGold"));
			break;
		default:
			break;
		}
	}
	
	@EventHandler
	public void Respawn(PlayerRespawnEvent ev){
		Team t = getTeam(ev.getPlayer());
		HashMap<String,ArrayList<Location>> l = wd.getLocs();
		ev.setRespawnLocation(l.get(t.Name()).get(UtilMath.r(l.get(t.Name()).size())));
	}
	
	public void Compass(Team t,Player p){
		ArrayList<Location> locs = new ArrayList<>();
		if(getTeam(p)==Team.RED){
			if(yellow!=null)locs.add(yellow);
			if(blue!=null)locs.add(blue);
			if(green!=null)locs.add(green);
			p.setCompassTarget(UtilLocation.Distance(locs, p.getLocation()));
		}else if(getTeam(p)==Team.YELLOW){
			if(red!=null)locs.add(red);
			if(blue!=null)locs.add(blue);
			if(green!=null)locs.add(green);
			p.setCompassTarget(UtilLocation.Distance(locs, p.getLocation()));
		}else if(getTeam(p)==Team.BLUE){
			if(yellow!=null)locs.add(yellow);
			if(red!=null)locs.add(red);
			if(green!=null)locs.add(green);
			p.setCompassTarget(UtilLocation.Distance(locs, p.getLocation()));
		}else if(getTeam(p)==Team.GREEN){
			if(yellow!=null)locs.add(yellow);
			if(blue!=null)locs.add(blue);
			if(red!=null)locs.add(red);
			p.setCompassTarget(UtilLocation.Distance(locs, p.getLocation()));
		}
	}
	
	@EventHandler
	public void Compass(UpdateEvent ev){
		if(ev.getType()==UpdateType.SEC)return;
		for(Player p : UtilServer.getPlayers()){
			if(p.getItemInHand()!=null&&p.getItemInHand().getType()==Material.COMPASS){
				Compass(getTeam(p),p);
			}
		}
	}
	
	@EventHandler
	public void Countdown(UpdateEvent ev){
	if(ev.getType()!=UpdateType.SEC)return;
	if(getManager().getState()!=GameState.InGame)return;
	getManager().setStart(getManager().getStart()-1);
	for(Player p : UtilServer.getPlayers()){
		UtilDisplay.displayTextBar(p, Text.GAME_END_IN.getText(getManager().getStart()));
	}
	switch(getManager().getStart()){
	   case 15:
		   getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.GAME_END_IN.getText(getManager().getStart()));
		   break;
	   case 10:
		   getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.GAME_END_IN.getText(getManager().getStart()));
		   break;
	   case 5:
		   getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.GAME_END_IN.getText(getManager().getStart()));
		   break;
	   case 4:
		   getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.GAME_END_IN.getText(getManager().getStart()));
		   break;
	   case 3:
		   getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.GAME_END_IN.getText(getManager().getStart()));
		   break;
	   case 2:
		   getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.GAME_END_IN.getText(getManager().getStart()));
		   break;
	   case 1:
		   getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.GAME_END_IN.getText(getManager().getStart()));
		   break;
	   case 0:
		   getManager().broadcast(Text.PREFIX_GAME.getText(getManager().getTyp().string())+Text.GAME_END.getText());
		   onDisable();
		   break;
	   }
	}
	
	public void BukkitVillager(Location loc){
		ItemStack[] s = new ItemStack[3];
		ItemStack[] b = new ItemStack[3];
		s[0]=UtilItem.RenameItem(new ItemStack(Material.GOLD_INGOT,3), "§bGold");
		b[0]=new ItemStack(Material.LAVA_BUCKET);
		s[1]=UtilItem.RenameItem(new ItemStack(Material.GOLD_INGOT,3), "§bGold");
		b[1]=new ItemStack(Material.WATER_BUCKET);
		s[2]=UtilItem.RenameItem(new ItemStack(Material.IRON_INGOT,7), "§bSilver");
		b[2]=new ItemStack(Material.ENDER_PEARL);
		SpawnVillager(loc,"Villager",b,s);
	}
	
	public void OPVillager(Location loc){
		ItemStack[] s = new ItemStack[2];
		ItemStack[] b = new ItemStack[2];
		s[0]=new ItemStack(Material.GOLD_INGOT,20);
		b[0]=new ItemStack(Material.GOLDEN_APPLE,1,(byte)1);
		s[1]=UtilItem.RenameItem(new ItemStack(Material.GOLD_INGOT,3), "§bGold");
		b[1]=new ItemStack(Material.FISHING_ROD);
		SpawnVillager(loc,"Villager",b,s);
	}
	
	public void LadderVillager(Location loc){
		ItemStack[] s = new ItemStack[2];
		ItemStack[] b = new ItemStack[2];
		s[0]=UtilItem.RenameItem(new ItemStack(Material.IRON_INGOT,2), "§bSilver");
		b[0]=new ItemStack(Material.LADDER);
		s[1]=UtilItem.RenameItem(new ItemStack(Material.GOLD_INGOT,2), "§bGold");
		b[1]=new ItemStack(Material.VINE);
		SpawnVillager(loc,"Villager",b,s);
	}
	
	public void PotionVillager(Location loc){
		ItemStack[] s = new ItemStack[5];
		ItemStack[] b = new ItemStack[5];
		s[0]=UtilItem.RenameItem(new ItemStack(Material.GOLD_INGOT,6), "§bGold");
		b[0]=new ItemStack(Material.POTION,1,(byte)8229);
		s[1]=UtilItem.RenameItem(new ItemStack(Material.IRON_INGOT,6), "§bSilver");
		b[1]=new ItemStack(Material.POTION,1,(byte)8193);
		s[2]=UtilItem.RenameItem(new ItemStack(Material.IRON_INGOT,6), "§bSilver");
		b[2]=new ItemStack(Material.POTION,1,(byte) 8201);
		s[3]=UtilItem.RenameItem(new ItemStack(Material.IRON_INGOT,6), "§bSilver");
		b[3]=new ItemStack(Material.POTION,1,(byte) 8194);
		s[4]=UtilItem.RenameItem(new ItemStack(Material.IRON_INGOT,6), "§bSilver");
		b[4]=new ItemStack(Material.POTION,1,(byte) 8195);
		SpawnVillager(loc,"Villager",b,s);
	}
	
	public void bowVillager(Location loc){
		ItemStack[] s = new ItemStack[6];
		ItemStack[] b = new ItemStack[6];
		s[0]=UtilItem.RenameItem(new ItemStack(Material.GOLD_INGOT,3), "§bGold");
		b[0]=UtilItem.RenameItem(new ItemStack(Material.BOW), "§aBow");
		ItemStack bow1 = UtilItem.RenameItem(new ItemStack(Material.BOW), "§aBow");
		bow1.addEnchantment(Enchantment.ARROW_DAMAGE, 1);
		s[1]=UtilItem.RenameItem(new ItemStack(Material.GOLD_INGOT,7), "§bGold");
		b[1]=bow1;
		ItemStack bow = UtilItem.RenameItem(new ItemStack(Material.BOW), "§aBow");
		bow.addEnchantment(Enchantment.ARROW_DAMAGE, 2);
		s[2]=new ItemStack(Material.GOLD_INGOT,13);
		b[2]=bow;
		s[3]=UtilItem.RenameItem(new ItemStack(Material.GOLD_INGOT), "§bGold");
		b[3]= new ItemStack(Material.ARROW);
		s[4]=UtilItem.RenameItem(new ItemStack(Material.CLAY_BRICK,4), "§bBronze");
		b[4]=new ItemStack(Material.getMaterial(364),2);
		s[5]=UtilItem.RenameItem(new ItemStack(Material.IRON_INGOT), "§bSilver");
		b[5]=UtilItem.RenameItem(new ItemStack(Material.GOLDEN_APPLE), "§aGoldenApple");
		SpawnVillager(loc,"Villager",b,s);
	}
	
	public void SwordVillager(Location loc){
		ItemStack[] s = new ItemStack[6];
		ItemStack[] b = new ItemStack[6];
		ItemStack goldsword = UtilItem.RenameItem(new ItemStack(Material.GOLD_SWORD), "§cSword");
		goldsword.addEnchantment(Enchantment.DAMAGE_ALL,1);
		s[0]=UtilItem.RenameItem(new ItemStack(Material.IRON_INGOT),"§bSilver");
		b[0]=goldsword;
		ItemStack goldsword1 = UtilItem.RenameItem(new ItemStack(Material.GOLD_SWORD), "§cSword");
		goldsword1.addEnchantment(Enchantment.DAMAGE_ALL, 2);
		s[1]=UtilItem.RenameItem(new ItemStack(Material.IRON_INGOT,3),"§bSilver");
		b[1]=goldsword1;
		ItemStack goldsword2 = UtilItem.RenameItem(new ItemStack(Material.GOLD_SWORD), "§cSword");
		goldsword2.addEnchantment(Enchantment.DAMAGE_ALL, 3);
		goldsword2.addEnchantment(Enchantment.KNOCKBACK, 1);
		s[2]=UtilItem.RenameItem(new ItemStack(Material.IRON_INGOT,7), "§bSilver");
		b[2]=goldsword2;
		s[3]=UtilItem.RenameItem(new ItemStack(Material.IRON_INGOT,3), "§bSilver");
		b[3]= new ItemStack(Material.TNT);
		s[4]=UtilItem.RenameItem(new ItemStack(Material.GOLD_INGOT), "§bGold");
		b[4]=new ItemStack(Material.FLINT_AND_STEEL);
		s[5]=UtilItem.RenameItem(new ItemStack(Material.IRON_INGOT), "§bSilver");
		b[5]= new ItemStack(Material.CHEST);
		SpawnVillager(loc,"Villager",b,s);
	}
	
	public void BlockVillager(Location loc){
		ItemStack[] s = new ItemStack[6];
		ItemStack[] b = new ItemStack[6];
		s[0]=UtilItem.RenameItem(new ItemStack(Material.CLAY_BRICK), "§bBronze");
		b[0]=new ItemStack(Material.SANDSTONE,4);
		s[1]=UtilItem.RenameItem(new ItemStack(Material.CLAY_BRICK,4), "§bBronze");
		b[1]=new ItemStack(Material.GLOWSTONE,2);
		s[2]=UtilItem.RenameItem(new ItemStack(Material.CLAY_BRICK,4), "§bBronze");
		b[2]=new ItemStack(Material.ENDER_STONE);
		ItemStack pick = UtilItem.RenameItem(new ItemStack(Material.WOOD_PICKAXE), "§5Pickaxe");
		pick.addEnchantment(Enchantment.DIG_SPEED, 1);
		s[3]=UtilItem.RenameItem(new ItemStack(Material.CLAY_BRICK,4), "§bBronze");
		b[3]=pick;
		ItemStack stonepick = UtilItem.RenameItem(new ItemStack(Material.STONE_PICKAXE), "§5Pickaxe");
		stonepick.addEnchantment(Enchantment.DIG_SPEED, 2);
		s[4]=UtilItem.RenameItem(new ItemStack(Material.IRON_INGOT,2), "§bSilver");
		b[4]=stonepick;
		ItemStack ironpick = UtilItem.RenameItem(new ItemStack(Material.IRON_PICKAXE), "§5Pickaxe");
		ironpick.addEnchantment(Enchantment.DIG_SPEED,3);
		s[5]=UtilItem.RenameItem(new ItemStack(Material.GOLD_INGOT), "§bGold");
		b[5]=ironpick;
		SpawnVillager(loc,"Villager",b,s);
	}
	
	public void ArmorVillager(Location loc){
		ItemStack[] s = new ItemStack[6];
		ItemStack[] b = new ItemStack[6];
		ItemStack helm=UtilItem.RenameItem(new ItemStack(Material.LEATHER_HELMET), "§7Armor");
		helm.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
		s[0]=UtilItem.RenameItem(new ItemStack(Material.CLAY_BRICK), "§bBronze");
		b[0]=helm;
		ItemStack leggings = UtilItem.RenameItem(new ItemStack(Material.LEATHER_LEGGINGS),"§7Armor");
		leggings.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
		s[1]=UtilItem.RenameItem(new ItemStack(Material.CLAY_BRICK), "§bBronze");
		b[1]=leggings;
		ItemStack boots=UtilItem.RenameItem(new ItemStack(Material.LEATHER_BOOTS), "§7Armor");
		boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL,1);
		s[2]=UtilItem.RenameItem(new ItemStack(Material.CLAY_BRICK), "§bBronze");
		b[2]=boots;
		ItemStack chestplate = UtilItem.RenameItem(new ItemStack(Material.CHAINMAIL_CHESTPLATE), "§eChestplate");
		chestplate.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
		s[3]=UtilItem.RenameItem(new ItemStack(Material.IRON_INGOT), "§bSilver");
		b[3]=chestplate;
		ItemStack chestplate1 = UtilItem.RenameItem(new ItemStack(Material.CHAINMAIL_CHESTPLATE), "§eChestplate");
		chestplate1.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
		s[4]=UtilItem.RenameItem(new ItemStack(Material.IRON_INGOT,3), "§bSilver");
		b[4]=chestplate1;
		ItemStack chestplate2 = UtilItem.RenameItem(new ItemStack(Material.CHAINMAIL_CHESTPLATE), "§eChestplate");
		chestplate2.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
		s[5]=UtilItem.RenameItem(new ItemStack(Material.IRON_INGOT,7), "§bSilver");
		b[5]=chestplate2;
		SpawnVillager(loc,"Villager",b,s);
	}
	
	public void SpawnVillager(Location loc,String name,ItemStack[] s, ItemStack[] b){
		String add = "";
		String sell="";
		for(int i = 0 ; i < s.length ; i++){
			sell="sell:{id:"+s[i].getTypeId()+",Count:"+s[i].getAmount()+",Damage:"+s[i].getDurability()+",tag:{"+Enchant(s[i])+display(s[i])+"}}";
			add=add+"{maxUses:"+99999+",buy:{id:"+b[i].getTypeId()+",Count:"+b[i].getAmount()+",Damage:"+b[i].getDurability()+",tag:{"+Enchant(b[i])+display(b[i])+"}},"+sell+"},";	
		}
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon Villager "+loc.getBlockX()+" "+loc.getBlockY()+" "+loc.getBlockZ()+" {CustomName:"+name+",Offers:{Recipes:["+add.substring(0, add.length()-1)+"]}}");
	}
	
	public String display(ItemStack i){
		if(!i.getItemMeta().hasDisplayName())return "display:{Name:Item}";
		return "display:{Name:"+i.getItemMeta().getDisplayName()+"}";
	}
	
	public String Enchant(ItemStack I){
		if(I.getEnchantments().isEmpty())return "";
		String e = "ench:[";
		
		for(Enchantment ench : I.getEnchantments().keySet()){
			e=e+"{id:"+ench.getId()+",lvl:"+I.getEnchantments().get(ench)+"},";
		}
		
		e=e.substring(0, e.length()-1)+"],";
		return e;
	}
	
	public HashMap<Team,Integer> verteilung(){
		HashMap<Team,Integer> list = new HashMap<>();
		Team[] t = new Team[]{Team.GREEN,Team.YELLOW,Team.RED,Team.BLUE};
		Integer[] i = new Integer[t.length];
		
		 for(Team m : t){
      	   list.put(m,UtilServer.getPlayers().length/t.length);
         }
		
		 if (!((UtilServer.getPlayers().length%2) == 0)){
          list.put(t[0], UtilServer.getPlayers().length/t.length+1);
         }
		
		return list;
	}
	
	@EventHandler
	public void Start(GameStartEvent ev){
		getManager().setState(GameState.InGame);
		HashMap<String,ArrayList<Location>> list = getManager().getWorldData().getLocs();
		ArrayList<Player> plist = new ArrayList<>();
		long time = System.currentTimeMillis();
		int r=0;
		for(Player p : UtilServer.getPlayers()){
			getManager().Clear(p);
			p.getInventory().addItem(new ItemStack(Material.COMPASS));
			getGameList().addPlayer(p,PlayerState.IN);
			plist.add(p);
		}
		PlayerVerteilung(verteilung(),plist);
		
		for(Player p : getTeamList().keySet()){
			r=UtilMath.r(list.get(getTeamList().get(p).Name()).size());
			p.teleport(list.get(getTeamList().get(p).Name()).get(r));
			list.get(getTeamList().get(p).Name()).remove(r);
		}
		SpawnSheeps();
		int i =0;
		
		for(Team t : villager.keySet()){
			for(Location loc : villager.get(t)){
				switch(i){
				case 0:ArmorVillager(loc);break;
				case 1:BlockVillager(loc);break;
				case 2:SwordVillager(loc);break;
				case 3:BukkitVillager(loc);break;
				case 4:LadderVillager(loc);break;
				case 5:OPVillager(loc);break;
				case 6:PotionVillager(loc);break;
				case 7:bowVillager(loc);break;
				}
				i++;
			}
			i=0;
		}
		
		getManager().DebugLog(time, this.getClass().getName());
	}
	

}
