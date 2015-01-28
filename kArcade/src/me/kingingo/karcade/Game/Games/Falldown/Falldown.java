package me.kingingo.karcade.Game.Games.Falldown;

import lombok.Getter;
import me.kingingo.karcade.kArcadeManager;
import me.kingingo.karcade.Enum.Team;
import me.kingingo.karcade.Game.Games.SoloGame;
import me.kingingo.karcade.Game.World.WorldData;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.Enum.Text;
import me.kingingo.kcore.Game.Events.GameStartEvent;
import me.kingingo.kcore.PlayerStats.Stats;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.UtilDisplay;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilMap;
import me.kingingo.kcore.Util.UtilMath;
import me.kingingo.kcore.Util.UtilServer;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public class Falldown  extends SoloGame{

	@Getter
	private WorldData worldData;
	@Getter
	private kArcadeManager manager;
	
	public Falldown(kArcadeManager manager) {
		super(manager);
		registerListener();
		long t = System.currentTimeMillis();
		setTyp(GameType.Falldown);
		manager.setState(GameState.Laden);
		this.manager=manager;
		this.worldData=new WorldData(manager,getType());
		setWorldData(getWorldData());
		setCreatureSpawn(true);
		setMin_Players(6);
		setMax_Players(16);
		setDamage(false);
		setDamagePvP(false);
		setDamageEvP(false);
		setDamagePvE(false);
		setDamageSelf(false);
		setBlockSpread(false);
		setCreatureSpawn(false);
		setDeathDropItems(true);
		setBlockBreak(false);
		setBlockPlace(false);
		setItemDrop(true);
		setFoodChange(true);
		setCompassAddon(true);
		setItemPickup(true);
		getWorldData().Initialize();
		UtilMap.setCrystals(getWorldData().getLocs(Team.RED.Name()).get(0), 25, 5000);
		manager.setState(GameState.LobbyPhase);
		manager.DebugLog(t, this.getClass().getName());
	}
	
	//TEAM RED
	
	public void setlevel(Player player, int lvl){
		player.setLevel(player.getLevel() + lvl);
		getStats().setInt(player, player.getLevel(), Stats.POWER);
	}
	
	@EventHandler
	public void Schutzzeit(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		if(getManager().getState()!=GameState.SchutzModus)return;
		getManager().setStart( getManager().getStart()-1 );
		
		for(Player p : UtilServer.getPlayers())UtilDisplay.displayTextBar(p, Text.SCHUTZZEIT_END_IN.getText(getManager().getStart()));
		switch(getManager().getStart()){
		case 30: getManager().broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.SCHUTZZEIT_END_IN.getText(getManager().getStart()));break;
		case 20: getManager().broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.SCHUTZZEIT_END_IN.getText(getManager().getStart()));break;
		case 15: getManager().broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.SCHUTZZEIT_END_IN.getText(getManager().getStart()));break;
		case 10: getManager().broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.SCHUTZZEIT_END_IN.getText(getManager().getStart()));break;
		case 5: getManager().broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.SCHUTZZEIT_END_IN.getText(getManager().getStart()));break;
		case 4: getManager().broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.SCHUTZZEIT_END_IN.getText(getManager().getStart()));break;
		case 3: getManager().broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.SCHUTZZEIT_END_IN.getText(getManager().getStart()));break;
		case 2: getManager().broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.SCHUTZZEIT_END_IN.getText(getManager().getStart()));break;
		case 1: getManager().broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.SCHUTZZEIT_END_IN.getText(getManager().getStart()));break;
		case 0: 
			setDamage(true);
			setDamagePvP(true);
			getManager().setStart( 60*10 );
			getManager().setState(GameState.InGame);
			getManager().broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.SCHUTZZEIT_END.getText());
		break;
		}
	}
	
	public static String RandomItem() {
		String i = "";
		int high = 11;
		int low = 1;
		int z = UtilMath.RandomInt(high, low);

		if (z == 1 || z == 8) {
			i = "ARMOR";
		} else if (z ==2) {
			i = "SWORD";
		} else if (z == 3) {
			i = "BOW";
		} else if (z == 4 || z == 5) {
			i = "BRAUITEMS";
		} else if (z == 6 || z == 7 || z == 9) {
			i = "POWER";
		} else if (z == 10 || z == 11){
			i = "null";
		}

		return i;
	}
	
	public void EnchantArmor(ItemStack i, Player p) {
		int lvl = p.getLevel();

		if(lvl < 40){
			p.sendMessage(Text.PREFIX_GAME.getText(getType().getTyp()) + Text.FALLDOWN_NICHT_GENUG_POWER.getText());
			return;
		}else{
			Bukkit.getWorld(p.getWorld().getName()).playEffect(p.getLocation(),
					Effect.POTION_BREAK, 16421);
			setlevel(p, -40);
		}
		
		int type = UtilMath.RandomInt(2, 0);
		java.util.Random levelRandom = new java.util.Random();
		p.getWorld().playSound(p.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
		
		if (type == 0) {
	          i.addEnchantment(Enchantment.PROTECTION_PROJECTILE, levelRandom.nextInt(2) + 1);
	          i.addEnchantment(Enchantment.PROTECTION_FIRE, levelRandom.nextInt(2) + 1);
	        } else if (type == 1) {
	          i.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, levelRandom.nextInt(2) + 1);
	          i.addEnchantment(Enchantment.PROTECTION_FIRE, levelRandom.nextInt(2) + 1);
	        } else if (type == 2) {
	          i.addEnchantment(Enchantment.PROTECTION_PROJECTILE, levelRandom.nextInt(2) + 1);
	          i.addEnchantment(Enchantment.THORNS, levelRandom.nextInt(2) + 1);
	        }
	}
	
	public void EnchantBow(ItemStack i, Player p) {
		int lvl = p.getLevel();
		if(lvl < 40){
			p.sendMessage(Text.PREFIX_GAME.getText(getType().getTyp()) + Text.FALLDOWN_NICHT_GENUG_POWER.getText());
			return;
		}else{
			Bukkit.getWorld(p.getWorld().getName()).playEffect(p.getLocation(),
					Effect.POTION_BREAK, 16421);
			setlevel(p, -40);
		}
		
		int type = UtilMath.RandomInt(2, 0);
		java.util.Random levelRandom = new java.util.Random();
		p.getWorld().playSound(p.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
		if (type == 0) {
	        i.addEnchantment(Enchantment.ARROW_DAMAGE, levelRandom.nextInt(2) + 1);
	        i.addEnchantment(Enchantment.ARROW_INFINITE, 1);
	      } else if (type == 1) {
	        i.addEnchantment(Enchantment.ARROW_DAMAGE, levelRandom.nextInt(2) + 1);
	        i.addEnchantment(Enchantment.ARROW_INFINITE, 1);
	      } else if (type == 2) {
	        i.addEnchantment(Enchantment.ARROW_DAMAGE, levelRandom.nextInt(2) + 1);
	        i.addEnchantment(Enchantment.ARROW_KNOCKBACK, levelRandom.nextInt(2) + 1);
	      }
	}
	
	public String is(ItemStack i) {
		String item = "null";
		int id = i.getTypeId();

		if (id == 268 || id == 272 || id == 276 || id == 283 || id == 267) {
			item = "SWORD";
		} else if (id == 261) {
			item = "BOW";
		} else if (id <= 317 && id >= 298) {
			item = "ARMOR";
		}

		return item;
	}
	
	public void EnchantSword(ItemStack i, Player p) {
		int lvl = p.getLevel();

		if(lvl < 40){
			p.sendMessage(Text.PREFIX_GAME.getText(getType().getTyp()) + Text.FALLDOWN_NICHT_GENUG_POWER.getText());
			return;
		}else{
			Bukkit.getWorld(p.getWorld().getName()).playEffect(p.getLocation(),
					Effect.POTION_BREAK, 16421);
			setlevel(p, -40);
		}
		
		int type = UtilMath.RandomInt(2, 0);
		java.util.Random levelRandom = new java.util.Random();
		p.getWorld().playSound(p.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
		if (type == 0) {
	          i.addEnchantment(Enchantment.DAMAGE_UNDEAD, levelRandom.nextInt(2) + 1);
	          i.addEnchantment(Enchantment.DAMAGE_ARTHROPODS, levelRandom.nextInt(2) + 1);
	        } else if (type == 1) {
	          i.addEnchantment(Enchantment.DAMAGE_ALL, levelRandom.nextInt(2) + 1);
	          i.addEnchantment(Enchantment.KNOCKBACK, levelRandom.nextInt(2) + 1);
	        } else if (type == 2) {
	          i.addEnchantment(Enchantment.DURABILITY, levelRandom.nextInt(2) + 1);
	          i.addEnchantment(Enchantment.FIRE_ASPECT, levelRandom.nextInt(2) + 1);
	        }
		
	}
	
	public ItemStack RandomBow() {
		ItemStack i = null;
		int high = 3;
		int low = 1;
		int z = UtilMath.RandomInt(high, low);

		if (z == 1) {
			i = new ItemStack(261, 1);
		} else if (z == 2 || z == 3) {
			i = new ItemStack(262, 5);
		}

		return i;
	}

	public ItemStack RandomArmor() {
		ItemStack i = null;
		int high = 317;
		int low = 298;
		int z = UtilMath.RandomInt(high, low);
		int anzahl = UtilMath.RandomInt(2, 1);

		i = new ItemStack(z, anzahl);

		return i;
	}
	
	public ItemStack RandomBrau() {
		int a = 377; // BLAZE POWDER
		int b = 288;// FEATHER
		int c = 331;// REDSTONE
		int d = 348;// GLOWSTONE DUST
		int e = 378;// MAGMA CREAM
		
		ItemStack i = null;
		int high = 10;
		int low = 1;
		int z = UtilMath.RandomInt(high, low);
		if (z == 1 || z == 6) {
			i = new ItemStack(a, 1);
		} else if (z == 2 || z == 7) {
			i = new ItemStack(b, 1);
		} else if (z == 3 || z == 8) {
			i = new ItemStack(c, 1);
		} else if (z == 4 || z == 9) {
			i = new ItemStack(d, 1);
		} else if (z == 5 || z == 10) {
			i = new ItemStack(e, 1);
		}

		return i;
	}
	
	public ItemStack RandomSword() {
		ItemStack i = null;
		int high = 5;
		int low = 1;
		int z = UtilMath.RandomInt(high, low);

		if (z == 1) {
			i = new ItemStack(267, 1);
		} else if (z == 2) {
			i = new ItemStack(268, 1);
		} else if (z == 3) {
			i = new ItemStack(272, 1);
		} else if (z == 4) {
			i = new ItemStack(276, 1);
		} else if (z == 5) {
			i = new ItemStack(283, 1);
		}

		return i;
	}
	
	@EventHandler
	public void InGame(UpdateEvent ev){
		if(ev.getType()!=UpdateType.SEC)return;
		if(getManager().getState()!=GameState.InGame)return;
		getManager().setStart( getManager().getStart()-1 );
		
		for(Player p : UtilServer.getPlayers())UtilDisplay.displayTextBar(p, Text.GAME_END_IN.getText(getManager().getStart()));
		switch(getManager().getStart()){
		case 30: getManager().broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_END_IN.getText(getManager().getStart()));break;
		case 20: getManager().broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_END_IN.getText(getManager().getStart()));break;
		case 15: getManager().broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_END_IN.getText(getManager().getStart()));break;
		case 10: getManager().broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_END_IN.getText(getManager().getStart()));break;
		case 5: getManager().broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_END_IN.getText(getManager().getStart()));break;
		case 4: getManager().broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_END_IN.getText(getManager().getStart()));break;
		case 3: getManager().broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_END_IN.getText(getManager().getStart()));break;
		case 2: getManager().broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_END_IN.getText(getManager().getStart()));break;
		case 1: getManager().broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_END_IN.getText(getManager().getStart()));break;
		case 0: 
			setDamage(false);
			setDamagePvP(false);
			getManager().setState(GameState.Restart);
			getManager().broadcast(Text.PREFIX_GAME.getText(getType().getTyp())+Text.GAME_END.getText());
		break;
		}
	}
	
	@EventHandler
	public void Start(GameStartEvent ev){
		Location spawn = getWorldData().getLocs(Team.RED.Name()).get(0).clone();
		spawn.setY(6000);
		for(Player player : UtilServer.getPlayers()){
			player.teleport(spawn);
			getManager().Clear(player);
			player.setLevel( getStats().getInt(Stats.POWER, player) );
			player.getInventory().addItem(UtilItem.RenameItem(new ItemStack(Material.STICK), "§cMagic Stick"));
		}
		getManager().setStart(60*2);
		getManager().setState(GameState.SchutzModus);
	}
	
}
