package eu.epicpvp.karcade.Game.Multi.Games.SurvivalGames1vs1;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import eu.epicpvp.karcade.Game.Multi.Games.MultiGame;
import eu.epicpvp.kcore.Enum.Team;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.kcore.Util.UtilMath;
import lombok.Getter;

public class UtilSurvivalGames1vs1 {
	@Getter
	private static ItemStack enderchest_compass=UtilItem.RenameItem(new ItemStack(Material.COMPASS), "§bCompass §7§b Enderchest"); //Der Kompass zeigt immer auf die E-Chest
	
	public static void loadEnderChest(SurvivalGames1vs1 game){
		if(game.getEnderchest_loc()!=null){
			game.getEnderchest_loc().getWorld().playSound(game.getEnderchest_loc(), Sound.BLAZE_HIT, 1f, 1f);
			game.getEnderchest_loc().getWorld().playEffect(game.getEnderchest_loc(), Effect.ENDER_SIGNAL, 1);
			game.getEnderchest_loc().getBlock().setType(Material.AIR);
		}
		
		if(game.getEnderchest_inv()==null){
			game.setEnderchest_inv(Bukkit.createInventory(null, 27));

			game.getEnderchest_inv().setItem(emptySlot(game.getEnderchest_inv()), new ItemStack(Material.ENDER_PEARL));
			game.getEnderchest_inv().setItem(emptySlot(game.getEnderchest_inv()), new ItemStack(Material.ENDER_PEARL));
			game.getEnderchest_inv().setItem(emptySlot(game.getEnderchest_inv()), new ItemStack(Material.ENDER_PEARL));
			
			if(UtilMath.randomInteger(100)>50){
				game.getEnderchest_inv().setItem(emptySlot(game.getEnderchest_inv()), new ItemStack(Material.IRON_SWORD));
			}
		}
		
		ArrayList<Location> locs = (ArrayList<Location>) game.getWorldData().getLocs(game, Team.VILLAGE_BLACK).clone();
		locs.remove(game.getEnderchest_loc());
		game.setEnderchest_loc(locs.get(UtilMath.randomInteger(locs.size())));
		locs.clear();
		locs=null;
		
		game.getEnderchest_loc().getWorld().spawnFallingBlock(game.getEnderchest_loc().clone().add(0, 6, 0), Material.ENDER_CHEST, (byte)0);
		game.getEnderchest_loc().getWorld().playSound(game.getEnderchest_loc().clone().add(0, 4, 0), Sound.BLAZE_HIT, 1f, 1f);
		game.getEnderchest_loc().getWorld().playEffect(game.getEnderchest_loc().clone().add(0, 4, 0), Effect.ENDER_SIGNAL, 1);
		
		for(Player player : game.getGameList().getPlayers().keySet())player.setCompassTarget(game.getEnderchest_loc());
		game.setEnderchest_time(System.currentTimeMillis());
	}
	
	public static ItemStack Sonstiges(){
		try{
			switch(UtilMath.randomInteger(29)){
			case 0: return new ItemStack(Material.ENDER_PEARL,UtilMath.RandomInt(2,1));
			case 1: return new ItemStack(Material.GOLDEN_APPLE,1);
			case 2: return new ItemStack(Material.ARROW,UtilMath.RandomInt(10,2));
			case 3: return new ItemStack(Material.ARROW,UtilMath.RandomInt(10,2));
			case 4: return new ItemStack(Material.ARROW,UtilMath.RandomInt(10,2));
			case 5: return new ItemStack(Material.ARROW,UtilMath.RandomInt(10,2));
			case 6: return new ItemStack(Material.FISHING_ROD);
			case 7: return new ItemStack(Material.FISHING_ROD);
			case 8: return new ItemStack(Material.FISHING_ROD);
			case 9: return new ItemStack(Material.TNT);
			case 10: return new ItemStack(Material.TNT);
			case 11: return new ItemStack(Material.TNT);
			case 12:return new ItemStack(Material.POTION,UtilMath.RandomInt(2, 1),(short)16389);
			case 13:return new ItemStack(Material.POTION,UtilMath.RandomInt(2, 1),(short)16421);
			case 14:return new ItemStack(Material.POTION,UtilMath.RandomInt(2, 1),(short)8229);
			case 15:return new ItemStack(Material.POTION,UtilMath.RandomInt(2, 1),(short)8197);
			case 16:return new ItemStack(Material.SNOW_BALL,UtilMath.RandomInt(4,2));
			case 17:return new ItemStack(Material.SNOW_BALL,UtilMath.RandomInt(4,2));
			case 18:return new ItemStack(Material.SNOW_BALL,UtilMath.RandomInt(4,2));
			case 19:return new ItemStack(Material.COOKED_BEEF,UtilMath.RandomInt(4,2));
			case 20:return new ItemStack(Material.COOKED_CHICKEN,UtilMath.RandomInt(4,2));
			case 21:return new ItemStack(Material.COOKED_FISH,UtilMath.RandomInt(4,2));
			case 22:return new ItemStack(Material.COOKED_RABBIT,UtilMath.RandomInt(4,2));
			case 23:return new ItemStack(Material.BREAD,UtilMath.RandomInt(4,2));
			case 24:return new ItemStack(Material.COOKED_BEEF,UtilMath.RandomInt(4,2));
			case 25:return new ItemStack(Material.COOKED_CHICKEN,UtilMath.RandomInt(4,2));
			case 26:return new ItemStack(Material.COOKED_FISH,UtilMath.RandomInt(4,2));
			case 27:return new ItemStack(Material.COOKED_RABBIT,UtilMath.RandomInt(4,2));
			case 28:return new ItemStack(Material.BREAD,UtilMath.RandomInt(4,2));
			default: return new ItemStack(Material.STICK);
			}
		}catch(NullPointerException e){
			System.err.println("Error: ");
			e.printStackTrace();
			return new ItemStack(Material.BREAD,UtilMath.RandomInt(4,2));
		}
	}
	
	public static ItemStack Tools(){
		switch(UtilMath.randomInteger(7)){
		case 0: return new ItemStack(Material.WOOD_SWORD);
		case 1: return new ItemStack(Material.GOLD_SWORD);
		case 2: return new ItemStack(Material.GOLD_AXE);
		case 3: return new ItemStack(Material.WOOD_AXE);
		case 4: return new ItemStack(Material.STONE_AXE);
		case 5: return new ItemStack(Material.STONE_SWORD);
		case 6: return new ItemStack(Material.BOW);
		default: return new ItemStack(Material.WOOD_SWORD);
		}
	}
	
	public static ItemStack IronRuestung(){
		switch(UtilMath.randomInteger(4)){
		case 0: return new ItemStack(Material.IRON_HELMET);
		case 1: return new ItemStack(Material.IRON_CHESTPLATE);
		case 2: return new ItemStack(Material.IRON_LEGGINGS);
		case 3: return new ItemStack(Material.IRON_BOOTS);
		default:
			return new ItemStack(Material.LEATHER_HELMET);
		}
	}
	
	public static ItemStack rdmItem(){
		ItemStack item;
		switch(UtilMath.randomInteger(3)){
		case 0: item = IronRuestung();break;
		case 1: item = Tools();break;
		case 2: item = Sonstiges();break;
		default: item = new ItemStack(Material.APPLE);break;
		}
		return item;
	}
	
	public static Team getChestSpawn(Team team){
		switch(team){
		case RED:return Team.SHEEP_RED;
		case BLUE:return Team.SHEEP_BLUE;
		case YELLOW:return Team.SHEEP_YELLOW;
		case GREEN:return Team.SHEEP_GREEN;
		case GRAY:return Team.SHEEP_GRAY;
		case PINK:return Team.SHEEP_PINK;
		case ORANGE:return Team.SHEEP_ORANGE;
		case PURPLE:return Team.SHEEP_PURPLE;
		case WHITE:return Team.SHEEP_WHITE;
		case BLACK:return Team.SHEEP_BLACK;
		case CYAN:return Team.SHEEP_CYAN;
		case AQUA:return Team.SHEEP_AQUA;
		default:
		return Team.SHEEP_RED;
		}
	}
	
	public static ItemStack rdmFood(){
		switch(UtilMath.randomInteger(6)){
		case 0:return new ItemStack(Material.COOKED_BEEF,UtilMath.RandomInt(3, 2));
		case 1:return new ItemStack(Material.BREAD,UtilMath.RandomInt(3, 2));
		case 2:return new ItemStack(Material.CARROT,UtilMath.RandomInt(3, 2));
		case 3: return new ItemStack(Material.GOLDEN_CARROT,UtilMath.RandomInt(3, 2));
		case 4: return new ItemStack(Material.APPLE,UtilMath.RandomInt(3, 2));
		case 5: return new ItemStack(Material.CAKE,1);
		default: return new ItemStack(Material.GOLDEN_CARROT,UtilMath.RandomInt(3, 2));
		}
	}
	
	public static Material rdmHelm(){
		switch(UtilMath.randomInteger(4)){
		case 0:return Material.GOLD_HELMET;
		case 1:return Material.IRON_HELMET;
		case 2:return Material.CHAINMAIL_HELMET;
		default: return Material.LEATHER_HELMET;
		}
	}
	
	public static Material rdmChestplate(){
		switch(UtilMath.randomInteger(4)){
		case 0:return Material.GOLD_CHESTPLATE;
		case 1:return Material.IRON_CHESTPLATE;
		case 2:return Material.CHAINMAIL_CHESTPLATE;
		default: return Material.LEATHER_CHESTPLATE;
		}
	}
	
	public static Material rdmLeggings(){
		switch(UtilMath.randomInteger(4)){
		case 0:return Material.GOLD_LEGGINGS;
		case 1:return Material.IRON_LEGGINGS;
		case 2:return Material.CHAINMAIL_LEGGINGS;
		default: return Material.LEATHER_LEGGINGS;
		}
	}
	
	public static Material rdmBoots(){
		switch(UtilMath.randomInteger(4)){
			case 0:return Material.GOLD_BOOTS;
			case 1:return Material.IRON_BOOTS;
			case 2:return Material.CHAINMAIL_BOOTS;
			default: return Material.LEATHER_BOOTS;
		}
	}
	
	public static ItemStack rdmPotion(){
		switch(UtilMath.randomInteger(4)){
			case 0:return new ItemStack(Material.POTION,1,(short)16421);
			case 1:return new ItemStack(Material.POTION,1,(short)16389);
			case 2:return new ItemStack(Material.POTION,1,(short)8229);
			case 3:return new ItemStack(Material.POTION,1,(short)8197);
			default: return new ItemStack(Material.POTION,1);
		}
	}
	
	public static void loadWorld(MultiGame game,HashMap<Chest,ArrayList<String>> template,HashMap<String,Integer> template_type){
		int i=0;
		Chest[] chests;
		Team[] teams = new Team[]{Team.RED,Team.BLUE};
		
		for(Team t : teams){
			if(game.getWorldData().getTeams(game).get(t).isEmpty())continue;
			chests=new Chest[game.getWorldData().getLocs(game,UtilSurvivalGames1vs1.getChestSpawn(t)).size()];

			i=0;
			for(Location loc : game.getWorldData().getLocs(game,UtilSurvivalGames1vs1.getChestSpawn(t))){
				if(!(loc.getBlock().getState() instanceof Chest)){
					loc.getBlock().setType(Material.CHEST);
				}
				chests[i]=((Chest)loc.getBlock().getState());
				chests[i].getInventory().clear();
				i++;
			}
			
			if(i!=0)fillPlayerChest(chests,template,template_type);
		}
		
		i=0;
		chests=new Chest[game.getWorldData().getLocs(game,Team.VILLAGE_GREEN).size()];
		for(Location loc : game.getWorldData().getLocs(game,Team.VILLAGE_GREEN)){
			if(!(loc.getBlock().getState() instanceof Chest)){
				loc.getBlock().setType(Material.CHEST);
			}
			if(loc.getBlock().getState() instanceof Chest){
				chests[i]=((Chest)loc.getBlock().getState());
				i++;
			}
		}
		
		fillChest(chests, template, template_type);
	}
	
	public static void fillChest(Chest[] chests,HashMap<Chest,ArrayList<String>> template,HashMap<String,Integer> template_type){
		//SWORD BLOCK HELM CHESTPLATE LEGGINGS BOOTS BOW ARROW POTION FOOD SNOWBALL EGG WEB LAVA-BUCKET WATER-BUCKET
		template.clear();
		template_type.clear();
		template_type.put("SWORD",4);
		template_type.put("HELM",6);
		template_type.put("CHESTPLATE",6);
		template_type.put("LEGGINGS",6);
		template_type.put("BOOTS",6);
		template_type.put("ARROW",5);
		template_type.put("POTION",3);
		template_type.put("FOOD",15);
		template_type.put("SNOWBALL",8);
		template_type.put("EGG",8);
		template_type.put("WEB",8);
		template_type.put("TNT",4);
		template_type.put("FIRE",3);
		template_type.put("BOW",3);
		template_type.put("ENDERPEARL",1);
		template_type.put("ANGLE",1);
		
		for(Chest chest : chests){
			chest.getInventory().clear();
			template.put(chest, new ArrayList<String>());
		}
		
		if(UtilMath.randomInteger(100)>=94){
			add(template,template_type, (Chest)template.keySet().toArray()[UtilMath.randomInteger(template.size())] ,"ENDERPEARL");
		}else{
			template_type.remove("ENDERPEARL");
		}
		
		if(UtilMath.randomInteger(100)>=80){
			add(template,template_type, (Chest)template.keySet().toArray()[UtilMath.randomInteger(template.size())] ,"BOW");
		}else{
			template_type.remove("BOW");
		}
		
		if(UtilMath.randomInteger(100)>50){
			add(template,template_type, (Chest)template.keySet().toArray()[UtilMath.randomInteger(template.size())] ,"TNT");
			add(template,template_type, (Chest)template.keySet().toArray()[UtilMath.randomInteger(template.size())] ,"FIRE");
		}else{
			add(template,template_type, (Chest)template.keySet().toArray()[UtilMath.randomInteger(template.size())] ,"TNT");
			add(template,template_type, (Chest)template.keySet().toArray()[UtilMath.randomInteger(template.size())] ,"FIRE");
		}

		add(template,template_type, (Chest)template.keySet().toArray()[UtilMath.randomInteger(template.size())] ,"ARROW");
		add(template,template_type, (Chest)template.keySet().toArray()[UtilMath.randomInteger(template.size())] ,"POTION");
		add(template,template_type, (Chest)template.keySet().toArray()[UtilMath.randomInteger(template.size())] ,"WEB");
		add(template,template_type, (Chest)template.keySet().toArray()[UtilMath.randomInteger(template.size())] ,"EGG");
		add(template,template_type, (Chest)template.keySet().toArray()[UtilMath.randomInteger(template.size())] ,"SNOWBALL");
		add(template,template_type, (Chest)template.keySet().toArray()[UtilMath.randomInteger(template.size())] ,"FOOD");
		
		int r;
		ArrayList<String> list;
		for(Chest chest : template.keySet()){
			list=template.get(chest);
			r=UtilMath.RandomInt(8, 5);
			if(r<=list.size())continue;
			for(int i = 0; i < (r-list.size()); i++){
				if(template_type.size()==0)break;
				add(template,template_type,chest, (String)template_type.keySet().toArray()[UtilMath.randomInteger(template_type.size())]);
			}
		}
		
		list=null;
		
		for(Chest chest : template.keySet()){
			for(String i : template.get(chest)){
				switch(i){
				case "ANGLE":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(Material.FISHING_ROD) );
					break;
				case "SWORD":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(UtilItem.rdmSchwert()) );
					break;
				case "HELM":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(rdmHelm()));
					break;
				case "CHESTPLATE":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(rdmChestplate()));
					break;
				case "LEGGINGS":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(rdmLeggings()));
					break;
				case "BOOTS":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(rdmBoots()));
					break;
				case "BOW":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(Material.BOW));
					break;
				case "ARROW":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(Material.ARROW,UtilMath.RandomInt(5, 2)));
					break;
				case "POTION":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(rdmPotion()));
					break;
				case "FOOD":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , rdmFood());
					break;
				case "SNOWBALL":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(Material.SNOW_BALL,UtilMath.RandomInt(4, 2)));
					break;
				case "EGG":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(Material.EGG,UtilMath.RandomInt(4, 2)));
					break;
				case "WEB":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(Material.WEB,UtilMath.RandomInt(4, 2)));
					break;
				case "LAVA-BUCKET":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(Material.LAVA_BUCKET));
					break;
				case "WATER-BUCKET":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(Material.WATER_BUCKET));
					break;
				case "TNT":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(Material.TNT,UtilMath.RandomInt(2,1)));
					break;
				case "FIRE":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(Material.FLINT_AND_STEEL));
					break;
				case "ENDERPEARL":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(Material.ENDER_PEARL));
					break;
				default:
					System.out.println("[UtilSurvivalGames1vs1]: Chest FAIL: "+i);
					break;
				}
			}
			template.get(chest).clear();
		}
	}
	
	public static void fillPlayerChest(Chest[] chests,HashMap<Chest,ArrayList<String>> template,HashMap<String,Integer> template_type){
		//SWORD BLOCK HELM CHESTPLATE LEGGINGS BOOTS BOW ARROW POTION FOOD SNOWBALL EGG WEB LAVA-BUCKET WATER-BUCKET
		template.clear();
		template_type.clear();
		template_type.put("SWORD",1);
		template_type.put("ARROW",1);
		template_type.put("POTION",1);
		template_type.put("FOOD",4);
		template_type.put("SNOWBALL",1);
		template_type.put("EGG",1);
		template_type.put("WEB",1);
		template_type.put("TNT",1);
		template_type.put("FIRE",1);
		template_type.put("BOW",1);
		template_type.put("HELM",1);
		template_type.put("CHESTPLATE",1);
		template_type.put("LEGGINGS",1);
		template_type.put("BOOTS",1);
		template_type.put("ANGLE",1);
		
		for(Chest chest : chests){
			chest.getInventory().clear();
			template.put(chest, new ArrayList<String>());
		}
		
		for(int i = 0 ; i<1; i++){
			switch(UtilMath.randomInteger(4)){
			case 0:
				if(template_type.containsKey("HELM")){
					add(template,template_type, (Chest)template.keySet().toArray()[UtilMath.randomInteger(template.size())] ,"HELM");
					template_type.remove("HELM");
				}else{
					add(template,template_type, (Chest)template.keySet().toArray()[UtilMath.randomInteger(template.size())] ,"CHESTPLATE");
					template_type.remove("CHESTPLATE");
				}
				break;
			case 1:
				if(template_type.containsKey("CHESTPLATE")){
					add(template,template_type, (Chest)template.keySet().toArray()[UtilMath.randomInteger(template.size())] ,"CHESTPLATE");
					template_type.remove("CHESTPLATE");
				}else{
					add(template,template_type, (Chest)template.keySet().toArray()[UtilMath.randomInteger(template.size())] ,"HELM");
					template_type.remove("HELM");
				}
				break;
			case 2:
				if(template_type.containsKey("LEGGINGS")){
					add(template,template_type, (Chest)template.keySet().toArray()[UtilMath.randomInteger(template.size())] ,"LEGGINGS");
					template_type.remove("LEGGINGS");
				}else{
					add(template,template_type, (Chest)template.keySet().toArray()[UtilMath.randomInteger(template.size())] ,"BOOTS");
					template_type.remove("BOOTS");
				}
				break;
			case 3:
				if(template_type.containsKey("BOOTS")){
					add(template,template_type, (Chest)template.keySet().toArray()[UtilMath.randomInteger(template.size())] ,"BOOTS");
					template_type.remove("BOOTS");
				}else{
					add(template,template_type, (Chest)template.keySet().toArray()[UtilMath.randomInteger(template.size())] ,"LEGGINGS");
					template_type.remove("LEGGINGS");
				}
				break;
			}
		}

		template_type.remove("HELM");
		template_type.remove("CHESTPLATE");
		template_type.remove("LEGGINGS");
		template_type.remove("BOOTS");
		
		if(UtilMath.randomInteger(100)>=94){
			add(template,template_type, (Chest)template.keySet().toArray()[UtilMath.randomInteger(template.size())] ,"BOW");
			add(template,template_type, (Chest)template.keySet().toArray()[UtilMath.randomInteger(template.size())] ,"ARROW");
		}else{
			template_type.remove("BOW");
			template_type.remove("ARROW");
		}
		
		if(UtilMath.randomInteger(100)>=81){
			add(template,template_type, (Chest)template.keySet().toArray()[UtilMath.randomInteger(template.size())] ,"FIRE");
		}else{
			template_type.remove("FIRE");
		}
		
		add(template,template_type, (Chest)template.keySet().toArray()[UtilMath.randomInteger(template.size())] ,"SWORD");
		add(template,template_type, (Chest)template.keySet().toArray()[UtilMath.randomInteger(template.size())] ,"POTION");
		add(template,template_type, (Chest)template.keySet().toArray()[UtilMath.randomInteger(template.size())] ,"FOOD");
		
		int r;
		ArrayList<String> list;
		for(Chest chest : template.keySet()){
			list=template.get(chest);
			r=UtilMath.RandomInt(6, 4);
			if(r<=list.size())continue;
			for(int i = 0; i < (r-list.size()); i++){
				if(template_type.size()==0)break;
				add(template,template_type,chest, (String)template_type.keySet().toArray()[UtilMath.randomInteger(template_type.size())]);
			}
		}
		
		list=null;
		
		for(Chest chest : template.keySet()){
			for(String i : template.get(chest)){
				switch(i){
				case "ANGLE":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack( Material.FISHING_ROD ) );
					break;
				case "SWORD":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack( (UtilMath.randomInteger(1)==0?Material.WOOD_SWORD:Material.WOOD_AXE) ) );
					break;
				case "HELM":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(Material.LEATHER_HELMET));
					break;
				case "CHESTPLATE":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(Material.LEATHER_CHESTPLATE));
					break;
				case "LEGGINGS":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(Material.LEATHER_LEGGINGS));
					break;
				case "BOOTS":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(Material.LEATHER_BOOTS));
					break;
				case "BOW":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(Material.BOW));
					break;
				case "ARROW":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(Material.ARROW,UtilMath.RandomInt(4, 2)));
					break;
				case "POTION":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(rdmPotion()));
					break;
				case "FOOD":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , rdmFood());
					break;
				case "SNOWBALL":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(Material.SNOW_BALL,UtilMath.RandomInt(4, 2)));
					break;
				case "EGG":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(Material.EGG,UtilMath.RandomInt(4, 2)));
					break;
				case "WEB":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(Material.WEB,UtilMath.RandomInt(5, 2)));
					break;
				case "LAVA-BUCKET":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(Material.LAVA_BUCKET));
					break;
				case "WATER-BUCKET":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(Material.WATER_BUCKET));
					break;
				case "TNT":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(Material.TNT,UtilMath.RandomInt(2,1)));
					break;
				case "FIRE":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(Material.FLINT_AND_STEEL));
					break;
				default:
					System.out.println("[UtilSurvivalGames1vs1]: Player Chest FAIL: "+i);
					break;
				}
			}
			template.get(chest).clear();
		}
	}
	
	public static int emptySlot(Inventory inv){
		int slot=0;
		for(int i = 0 ; i<2000; i++){
			slot=UtilMath.randomInteger(inv.getSize());
			if(inv.getItem(slot)==null||inv.getItem(slot).getType()==Material.AIR){
				return slot;
			}
		}
		System.out.println("NOT FIND A EMPTY SLOT");
		return 0;
	}
	
	public static void add(HashMap<Chest,ArrayList<String>> template,HashMap<String,Integer> template_type,Chest g,String type){
		template.get(g).add(type);
		int a=template_type.get(type);
		a--;
		template_type.remove(type);
		if(a!=0)template_type.put(type, a);
		a=0;
	}
}
