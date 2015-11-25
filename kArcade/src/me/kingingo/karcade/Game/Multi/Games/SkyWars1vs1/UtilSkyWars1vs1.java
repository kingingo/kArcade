package me.kingingo.karcade.Game.Multi.Games.SkyWars1vs1;

import java.util.ArrayList;
import java.util.HashMap;

import me.kingingo.karcade.Game.Multi.Games.MultiGame;
import me.kingingo.kcore.Enum.Team;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilMath;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class UtilSkyWars1vs1 {
	
	public static ItemStack Sonstiges(){
		try{
			switch(UtilMath.r(39)){
			case 0: return new ItemStack(Material.ENDER_PEARL,UtilMath.RandomInt(4, 2));
			case 1: return new ItemStack(Material.GOLDEN_APPLE,1);
			case 2: return new ItemStack(Material.ARROW,UtilMath.RandomInt(64,32));
			case 3: return new ItemStack(Material.ARROW,UtilMath.RandomInt(64,32));
			case 4: return new ItemStack(Material.ARROW,UtilMath.RandomInt(64,32));
			case 5: return new ItemStack(Material.ARROW,UtilMath.RandomInt(64,32));
			case 6: return new ItemStack(Material.FISHING_ROD);
			case 7: return new ItemStack(Material.FISHING_ROD);
			case 8: return new ItemStack(Material.FISHING_ROD);
			case 9: return new ItemStack(Material.TNT);
			case 10: return new ItemStack(Material.TNT);
			case 11: return new ItemStack(Material.TNT);
			case 12:return new ItemStack(Material.POTION,UtilMath.RandomInt(3, 1),(short)16421);
			case 13:return new ItemStack(Material.POTION,UtilMath.RandomInt(3, 1),(short)16417);
			case 14:return new ItemStack(Material.POTION,UtilMath.RandomInt(3, 1),(short)16389);
			case 15:return new ItemStack(Material.POTION,UtilMath.RandomInt(3, 1),(short)16385);
			case 16:return new ItemStack(Material.POTION,UtilMath.RandomInt(3, 1),(short)16387);
			case 17:return new ItemStack(Material.POTION,UtilMath.RandomInt(3, 1),(short)16421);
			case 18:return new ItemStack(Material.POTION,UtilMath.RandomInt(3, 1),(short)16417);
			case 19:return new ItemStack(Material.POTION,UtilMath.RandomInt(3, 1),(short)16389);
			case 20:return new ItemStack(Material.POTION,UtilMath.RandomInt(3, 1),(short)16385);
			case 21:return new ItemStack(Material.POTION,UtilMath.RandomInt(3, 1),(short)16387);
			case 22:return new ItemStack(Material.SNOW_BALL,UtilMath.RandomInt(32,10));
			case 23:return new ItemStack(Material.SNOW_BALL,UtilMath.RandomInt(32,10));
			case 24:return new ItemStack(Material.SNOW_BALL,UtilMath.RandomInt(32,10));
			case 25:return new ItemStack(Material.COOKED_BEEF,UtilMath.RandomInt(12,8));
			case 26:return new ItemStack(Material.COOKED_CHICKEN,UtilMath.RandomInt(12,8));
			case 27:return new ItemStack(Material.COOKED_FISH,UtilMath.RandomInt(12,8));
			case 28:return new ItemStack(Material.COOKED_RABBIT,UtilMath.RandomInt(12,8));
			case 29:return new ItemStack(Material.BREAD,UtilMath.RandomInt(12,8));
			case 30:return new ItemStack(Material.COOKED_BEEF,UtilMath.RandomInt(12,8));
			case 31:return new ItemStack(Material.COOKED_CHICKEN,UtilMath.RandomInt(12,8));
			case 32:return new ItemStack(Material.COOKED_FISH,UtilMath.RandomInt(12,8));
			case 33:return new ItemStack(Material.COOKED_RABBIT,UtilMath.RandomInt(12,8));
			case 34:return new ItemStack(Material.BREAD,UtilMath.RandomInt(12,8));
			case 35: return new ItemStack(Material.ENDER_PEARL,UtilMath.RandomInt(4, 2));
			case 36: return new ItemStack(Material.ENDER_PEARL,UtilMath.RandomInt(4, 2));
			default: return new ItemStack(Material.STICK);
			}
		}catch(NullPointerException e){
			System.err.println("Error: ");
			e.printStackTrace();
			return new ItemStack(Material.BREAD,UtilMath.RandomInt(12,8));
		}
	}
	
	public static ItemStack Tools(){
		switch(UtilMath.r(5)){
		case 0: return new ItemStack(Material.DIAMOND_SWORD);
		case 1: return new ItemStack(Material.IRON_SWORD);
		case 2: return new ItemStack(Material.DIAMOND_AXE);
		case 3: return new ItemStack(Material.DIAMOND_PICKAXE);
		case 4: return new ItemStack(Material.BOW);
		default: return new ItemStack(Material.WOOD_SWORD);
		}
	}
	
	public static ItemStack DiaR�stung(){
		switch(UtilMath.r(4)){
		case 0: return new ItemStack(Material.DIAMOND_HELMET);
		case 1: return new ItemStack(Material.DIAMOND_CHESTPLATE);
		case 2: return new ItemStack(Material.DIAMOND_LEGGINGS);
		case 3: return new ItemStack(Material.DIAMOND_BOOTS);
		default:
			return new ItemStack(Material.LEATHER_HELMET);
		}
	}
	
	public static ItemStack IronR�stung(){
		switch(UtilMath.r(4)){
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
		int r = UtilMath.r(1000);
		if(r>=150&&r<=155){
			item=UtilItem.RenameItem(new ItemStack(Material.SLIME_BALL), "�bX");
			item.addUnsafeEnchantment(Enchantment.KNOCKBACK, 5);
			return item;
		}
		
		switch(UtilMath.r(6)){
		case 0: item = Sonstiges();break;
		case 1: item = IronR�stung();break;
		case 2: item = DiaR�stung();break;
		case 3: item = Tools();break;
		case 4: item = Sonstiges();break;
		case 5: item = Sonstiges();break;
		default: item = new ItemStack(Material.APPLE);break;
		}
		
		if(item.getType()==Material.BOW){
			r=UtilMath.r(100);
			if(r>=0&&r<=40){
				item.addEnchantment(Enchantment.ARROW_DAMAGE, 3);
			}else if(r>=40&&r<=60){
				item.addEnchantment(Enchantment.ARROW_FIRE, 1);
			}
		}else if(item.getType()==Material.IRON_CHESTPLATE){
			r=UtilMath.r(100);
			if(r>=0&&r<=30){
				item.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
			}else if(r>=30&&r<=50){
				item.addEnchantment(Enchantment.PROTECTION_FIRE, 4);
			}
		}else if(item.getType()==Material.DIAMOND_CHESTPLATE){
			r=UtilMath.r(100);
			if(r>=0&&r<=40){
				item.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
			}else if(r>=30&&r<=50){
				item.addEnchantment(Enchantment.PROTECTION_FIRE, 4);
			}
		}else if(item.getType()==Material.IRON_SWORD){
			item.addEnchantment(Enchantment.DAMAGE_ALL, 3);
		}else if(item.getType()==Material.DIAMOND_SWORD){
			r=UtilMath.r(100);
			if(r>=0&&r<=40){
				item.addEnchantment(Enchantment.DAMAGE_ALL, 2);
			}else if(r>=40&&r<=60){
				item.addEnchantment(Enchantment.DAMAGE_ALL, 1);
				item.addEnchantment(Enchantment.FIRE_ASPECT, 1);
				item.addEnchantment(Enchantment.KNOCKBACK, 1);
			}
		}else if(item.getType()==Material.DIAMOND_AXE){
			item.addEnchantment(Enchantment.DIG_SPEED, 3);
		}else if(item.getType()==Material.DIAMOND_PICKAXE){
			item.addEnchantment(Enchantment.DIG_SPEED, 3);
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
	
	public static Material rdmBlock(){
		switch(UtilMath.r(3)){
		case 0:return Material.STONE;
		case 1:return Material.DIRT;
		case 2:return Material.WOOD;
		default: return Material.BEDROCK;
		}
	}
	
	public static ItemStack rdmFood(){
		switch(UtilMath.r(4)){
		case 0:return new ItemStack(Material.COOKED_BEEF,UtilMath.RandomInt(16, 8));
		case 1:return new ItemStack(Material.BREAD,UtilMath.RandomInt(16, 8));
		case 2:return new ItemStack(Material.CAKE);
		case 3:return new ItemStack(Material.CARROT,UtilMath.RandomInt(16, 8));
		default: return new ItemStack(Material.GOLDEN_CARROT,UtilMath.RandomInt(16, 8));
		}
	}
	
	public static Material rdmHelm(){
		switch(UtilMath.r(4)){
		case 0:return Material.DIAMOND_HELMET;
		case 1:return Material.GOLD_HELMET;
		case 2:return Material.IRON_HELMET;
		case 3:return Material.CHAINMAIL_HELMET;
		default: return Material.LEATHER_HELMET;
		}
	}
	
	public static Material rdmChestplate(){
		switch(UtilMath.r(4)){
		case 0:return Material.DIAMOND_CHESTPLATE;
		case 1:return Material.GOLD_CHESTPLATE;
		case 2:return Material.IRON_CHESTPLATE;
		case 3:return Material.CHAINMAIL_CHESTPLATE;
		default: return Material.LEATHER_CHESTPLATE;
		}
	}
	
	public static Material rdmLeggings(){
		switch(UtilMath.r(4)){
		case 0:return Material.DIAMOND_LEGGINGS;
		case 1:return Material.GOLD_LEGGINGS;
		case 2:return Material.IRON_LEGGINGS;
		case 3:return Material.CHAINMAIL_LEGGINGS;
		default: return Material.LEATHER_LEGGINGS;
		}
	}
	
	public static Material rdmTool(){
		switch(2){
		case 0: return Material.IRON_PICKAXE;
		case 1: return Material.IRON_AXE;
		default: return Material.DIAMOND_PICKAXE;
		}
	}
	
	public static Material rdmBoots(){
		switch(UtilMath.r(4)){
			case 0:return Material.DIAMOND_BOOTS;
			case 1:return Material.GOLD_BOOTS;
			case 2:return Material.IRON_BOOTS;
			case 3:return Material.CHAINMAIL_BOOTS;
			default: return Material.LEATHER_BOOTS;
		}
	}
	
	public static ItemStack rdmPotion(){
		switch(UtilMath.r(5)){
			case 0:return new ItemStack(Material.POTION,UtilMath.RandomInt(3, 1),(short)16421);
			case 1:return new ItemStack(Material.POTION,UtilMath.RandomInt(3, 1),(short)16417);
			case 2:return new ItemStack(Material.POTION,UtilMath.RandomInt(3, 1),(short)16389);
			case 3:return new ItemStack(Material.POTION,UtilMath.RandomInt(3, 1),(short)16385);
			case 4:return new ItemStack(Material.POTION,UtilMath.RandomInt(3, 1),(short)16387);
			default: return new ItemStack(Material.POTION,1);
		}
	}
	
	public static void loadWorld(MultiGame game,HashMap<Chest,ArrayList<String>> template,HashMap<String,Integer> template_type){
		int i=0;
		Chest[] chests;
		for(Team t : game.getWorldData().getTeams(game).keySet()){
			chests=new Chest[game.getWorldData().getLocs(game,UtilSkyWars1vs1.getChestSpawn(t)).size()];
			
			for(Location loc : game.getWorldData().getLocs(game,UtilSkyWars1vs1.getChestSpawn(t))){
				loc.getBlock().setType(Material.CHEST);
				chests[i]=((Chest)loc.getBlock().getState());
				i++;
			}
			i=0;
			
			UtilSkyWars1vs1.fillIslandChests(t,chests,template,template_type);
		}
		
		Chest chest;
		for(Location loc : game.getWorldData().getLocs(game,Team.VILLAGE_RED)){
			loc.getBlock().setType(Material.CHEST);
			if(loc.getBlock().getState() instanceof Chest){
				chest=(Chest)loc.getBlock().getState();
				for (int nur = 0; nur < UtilMath.RandomInt(6,3); nur++) {
					chest.getInventory().setItem(UtilSkyWars1vs1.emptySlot(chest.getInventory()), UtilSkyWars1vs1.rdmItem()); 
				}
			}
		}
	}
	
	public static void fillIslandChests(Team t,Chest[] chests,HashMap<Chest,ArrayList<String>> template,HashMap<String,Integer> template_type){
		//SWORD BLOCK HELM CHESTPLATE LEGGINGS BOOTS BOW ARROW POTION FOOD SNOWBALL EGG WEB LAVA-BUCKET WATER-BUCKET
		template.clear();
		template_type.clear();
		template_type.put("SWORD",3);
		template_type.put("BLOCK",3);
		template_type.put("HELM",2);
		template_type.put("CHESTPLATE",2);
		template_type.put("LEGGINGS",2);
		template_type.put("BOOTS",2);
		template_type.put("ARROW",1);
		template_type.put("POTION",4);
		template_type.put("FOOD",3);
		template_type.put("SNOWBALL",3);
		template_type.put("EGG",3);
		template_type.put("WEB",3);
		template_type.put("LAVA-BUCKET",1);
		template_type.put("WATER-BUCKET",1);
		template_type.put("TNT",1);
		template_type.put("FIRE",1);
		template_type.put("BOW",1);
		template_type.put("TOOL",2);
		
		for(Chest chest : chests)template.put(chest, new ArrayList<String>());
		
		if(UtilMath.r(100)>70){
			add(template,template_type, (Chest)template.keySet().toArray()[UtilMath.r(template.size())] ,"BOW");
			add(template,template_type, (Chest)template.keySet().toArray()[UtilMath.r(template.size())] ,"ARROW");
		}else{
			template_type.remove("BOW");
			template_type.remove("ARROW");
		}

		add(template,template_type, (Chest)template.keySet().toArray()[UtilMath.r(template.size())] ,"SWORD");
		add(template,template_type, (Chest)template.keySet().toArray()[UtilMath.r(template.size())] ,"BLOCK");
		add(template,template_type, (Chest)template.keySet().toArray()[UtilMath.r(template.size())] ,"TOOL");
		add(template,template_type, (Chest)template.keySet().toArray()[UtilMath.r(template.size())] ,"POTION");
		add(template,template_type, (Chest)template.keySet().toArray()[UtilMath.r(template.size())] ,"CHESTPLATE");
		add(template,template_type, (Chest)template.keySet().toArray()[UtilMath.r(template.size())] ,"FOOD");
		
		int r;
		ArrayList<String> list;
		for(Chest chest : template.keySet()){
			list=template.get(chest);
			r=UtilMath.RandomInt(8, 5);
			if(r<=list.size())continue;
			for(int i = 0; i < (r-list.size()); i++){
				add(template,template_type,chest, (String)template_type.keySet().toArray()[UtilMath.r(template_type.size())]);
			}
		}
		
		for(Chest chest : template.keySet()){
			for(String i : template.get(chest)){
				switch(i){
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
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(Material.ARROW,UtilMath.RandomInt(16, 8)));
					break;
				case "BLOCK":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(rdmBlock(),UtilMath.RandomInt(32, 16)));
					break;
				case "POTION":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(rdmPotion()));
					break;
				case "FOOD":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , rdmFood());
					break;
				case "SNOWBALL":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(Material.SNOW_BALL,UtilMath.RandomInt(16, 8)));
					break;
				case "EGG":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(Material.EGG,UtilMath.RandomInt(16, 8)));
					break;
				case "WEB":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(Material.WEB,UtilMath.RandomInt(16, 8)));
					break;
				case "LAVA-BUCKET":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(Material.LAVA_BUCKET));
					break;
				case "WATER-BUCKET":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(Material.WATER_BUCKET));
					break;
				case "TNT":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(Material.TNT,UtilMath.RandomInt(3, 1)));
					break;
				case "FIRE":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(Material.FLINT_AND_STEEL));
					break;
				case "TOOL":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(rdmTool()));
					break;
				default:
					System.out.println("Island Chest FAIL: "+i);
					break;
				}
			}
			template.get(chest).clear();
		}
		
		for(Chest chest : template.keySet()){
			for(ItemStack item : chest.getInventory().getContents()){
				if(item!=null&&item.getType()!=Material.AIR){
					if(item.getType()==Material.BOW){
						switch(UtilMath.r(3)){
						case 0:item.addEnchantment(Enchantment.ARROW_DAMAGE, 1);break;
						case 1:item.addEnchantment(Enchantment.ARROW_DAMAGE, 3);break;
						case 2:item.addEnchantment(Enchantment.ARROW_DAMAGE, 1);item.addEnchantment(Enchantment.ARROW_KNOCKBACK, 1);break;
						default:break;
						}
					}else if(UtilItem.isSword(item)){
						if(item.getType()==Material.WOOD_SWORD){
							item.addEnchantment(Enchantment.DAMAGE_ALL, 2);
						}else if(item.getType()==Material.DIAMOND_SWORD){
							r=UtilMath.r(100);
							if(r>=50&&r<=55){
								item.addEnchantment(Enchantment.FIRE_ASPECT, 1);
							}else if(r>=0&&r<=40){
								item.addEnchantment(Enchantment.DAMAGE_ALL, 1);
							}
						}else if(item.getType()==Material.GOLD_SWORD){
							r=UtilMath.r(100);
							item.addEnchantment(Enchantment.DURABILITY, 1);
							if(r>=0&&r<=60){
								item.addEnchantment(Enchantment.DAMAGE_ALL, 2);
							}else if(r>=60&&r<=100){
								item.addEnchantment(Enchantment.KNOCKBACK, 1);
							}
						}else if(item.getType()==Material.STONE_SWORD){
							item.addEnchantment(Enchantment.DAMAGE_ALL, 1);
						}else if(item.getType()==Material.IRON_SWORD){
							r=UtilMath.r(100);
							if(r>=40&&r<=80){
								item.addEnchantment(Enchantment.DAMAGE_ALL, 2);
							}
						}
					}else if(UtilItem.isArmor(item)){
						if(UtilItem.isGoldArmor(item)){
							r=UtilMath.r(100);
							if(r>=0&&r<=60){
								item.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
							}else{
								item.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
							}
						}else if(UtilItem.isChainmailArmor(item)){
							if(UtilItem.isChestplate(item)){
								r=UtilMath.r(100);
								if(r>=60&&r<=80){
									item.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
								}
							}
							item.addEnchantment(Enchantment.PROTECTION_FIRE, 3);
						}else if(UtilItem.isIronArmor(item)){
							r=UtilMath.r(100);
							if(r>=60&&r<=80){
								item.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
							}
							item.addEnchantment(Enchantment.PROTECTION_PROJECTILE, 3);
						}
					}
				}
			}
		}
	}
	
	public static int emptySlot(Inventory inv){
		int slot=0;
		for(int i = 0 ; i<2000; i++){
			slot=UtilMath.r(inv.getSize());
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
