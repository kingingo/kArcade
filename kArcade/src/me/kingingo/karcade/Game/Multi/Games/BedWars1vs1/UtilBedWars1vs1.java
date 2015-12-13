package me.kingingo.karcade.Game.Multi.Games.BedWars1vs1;

import me.kingingo.karcade.Game.Multi.MultiGames;
import me.kingingo.karcade.Game.Multi.Addons.MultiAddonBedTeamKing;
import me.kingingo.karcade.Game.Multi.Games.MultiGame;
import me.kingingo.kcore.Enum.Team;
import me.kingingo.kcore.Merchant.Merchant;
import me.kingingo.kcore.Merchant.MerchantOffer;
import me.kingingo.kcore.Util.Color;
import me.kingingo.kcore.Util.InventorySize;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Villager.VillagerShop;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class UtilBedWars1vs1 {
	private static MultiAddonBedTeamKing addonBed;

	public static MultiAddonBedTeamKing getAddonBed(){
		return getAddonBed(null);
	}
	
	public static MultiAddonBedTeamKing getAddonBed(MultiGames multiGames){
		if(addonBed==null&&multiGames!=null)addonBed=new MultiAddonBedTeamKing(multiGames);
		return addonBed;
	}
	
	public static ItemStack Silber(int i){
		return UtilItem.RenameItem(new ItemStack(Material.IRON_INGOT,i), "§bSilver");
	}
	
	public static ItemStack Gold(int i){
		return UtilItem.RenameItem(new ItemStack(Material.GOLD_INGOT,i), "§bGold");
	}
	
	public static ItemStack Bronze(int i){
		return UtilItem.RenameItem(new ItemStack(Material.CLAY_BRICK,i), "§bBronze");
	}
	
	public static void setSpezialVillager(Location l,MultiGame game,EntityType e){
		l=l.add(0.5,0.5,0.5);
		VillagerShop v = new VillagerShop(game.getGames().getManager().getInstance(),e,"Spezial-Shop",l,InventorySize._27);
		
		Merchant rustung = new Merchant();
		ItemStack r1 = UtilItem.RenameItem(new ItemStack(Material.IRON_CHESTPLATE), "Spezial Eisenhemd Lvl 1");
		r1.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
		r1.addEnchantment(Enchantment.PROTECTION_FIRE, 2);
		r1.addEnchantment(Enchantment.DURABILITY, 1);
		rustung.addOffer(new MerchantOffer(Gold(15),r1));
		ItemStack r2 = UtilItem.RenameItem(new ItemStack(Material.IRON_CHESTPLATE), "Spezial Eisenhemd Lvl 2");
		r2.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
		r2.addEnchantment(Enchantment.PROTECTION_FIRE, 4);
		r2.addEnchantment(Enchantment.DURABILITY, 1);
		rustung.addOffer(new MerchantOffer(Gold(30),r2));
		ItemStack r3 = UtilItem.RenameItem(new ItemStack(Material.DIAMOND_CHESTPLATE), "Diamanthemd");
		r3.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
		rustung.addOffer(new MerchantOffer(Gold(50),r3));
		v.addShop(UtilItem.Item(new ItemStack(Material.DIAMOND_CHESTPLATE), new String[]{"§aSpezial Schutz!"}, "§cRüstung"), rustung, 10);
		
		Merchant schwert = new Merchant();
		ItemStack s1 = UtilItem.RenameItem(new ItemStack(Material.IRON_SWORD), "Spezial Schwert Lvl 1");
		s1.addEnchantment(Enchantment.DAMAGE_ALL, 2);
		s1.addEnchantment(Enchantment.FIRE_ASPECT, 1);
		schwert.addOffer(new MerchantOffer(Gold(20),s1));
		ItemStack s2 = UtilItem.RenameItem(new ItemStack(Material.IRON_SWORD), "Spezial Schwert Lvl 2");
		s2.addEnchantment(Enchantment.DAMAGE_ALL, 3);
		s2.addEnchantment(Enchantment.FIRE_ASPECT, 1);
		schwert.addOffer(new MerchantOffer(Gold(40),s2));
		v.addShop(UtilItem.Item(new ItemStack(Material.IRON_SWORD), new String[]{"§aLehre deinen Gegner Schmerz!"}, "§cSchwerter"), schwert, 12);
		
		Merchant trank = new Merchant();
		trank.addOffer(new MerchantOffer(Gold(10),UtilItem.RenameItem(new ItemStack(Material.POTION,3,(byte)8229), "Heilung II")));
		trank.addOffer(new MerchantOffer(Gold(10),UtilItem.RenameItem(new ItemStack(Material.POTION,3,(byte)8225), "Regeneration II")));
		trank.addOffer(new MerchantOffer(Gold(10),UtilItem.RenameItem(new ItemStack(Material.POTION,3,(byte)8233), "Stärke II")));
		v.addShop(UtilItem.Item(new ItemStack(Material.POTION), new String[]{"§aWillst du mit mir Drogen nehmen?"}, "§cTränke"), trank, 14);
		
		Merchant gold = new Merchant();
		gold.addOffer(new MerchantOffer(Silber(3), UtilItem.RenameItem(new ItemStack(Material.GOLDEN_APPLE), "Goldener Apfel")));
		gold.addOffer(new MerchantOffer(Gold(25), UtilItem.RenameItem(new ItemStack(Material.GOLDEN_APPLE,1,(byte)1), "Op Apfel")));
		gold.addOffer(new MerchantOffer(Gold(10), UtilItem.RenameItem(new ItemStack(Material.ENDER_PEARL), "Enderpearl")));
		v.addShop(UtilItem.Item(new ItemStack(Material.GOLDEN_APPLE), new String[]{"§aRette dich in größter Not!"}, "§cSpezial"), gold, 16);
		
		v.finish();
	}
	
	public static Team getVillagerSpawn(Team team){
		switch(team){
		case RED:return Team.VILLAGE_RED;
		case BLUE:return Team.VILLAGE_BLUE;
		case YELLOW:return Team.VILLAGE_YELLOW;
		case GREEN:return Team.VILLAGE_GREEN;
		case GRAY:return Team.VILLAGE_GRAY;
		case PINK:return Team.VILLAGE_PINK;
		case ORANGE:return Team.VILLAGE_ORANGE;
		case PURPLE:return Team.VILLAGE_PURPLE;
		default:
		return Team.VILLAGE_RED;
		}
	}
	
	public static void setVillager(Team t,MultiGame game,EntityType e){
		Location l=game.getWorldData().getLocs(game, t).get(0).add(0.5,0.3,0.5);
		VillagerShop v = new VillagerShop(game.getGames().getManager().getInstance(),e,t.getColor()+"Villager-Shop",l,InventorySize._27);
		v.setDamage(false);
		v.setMove(false);
		
		Merchant bloecke = new Merchant();
		bloecke.addOffer(new MerchantOffer(Bronze(1), new ItemStack(24,4)));
		bloecke.addOffer(new MerchantOffer(Bronze(7),new ItemStack(Material.ENDER_STONE)));
		v.addShop(UtilItem.Item(new ItemStack(24), new String[]{"§aHier findest du alles was du zum bauen brauchst"}, "§cBlöcke"), bloecke, 9);
	
		Merchant spitzhacken = new Merchant();
		ItemStack spitzhack1 = UtilItem.RenameItem(new ItemStack(Material.WOOD_PICKAXE), "Holzhacke");
		spitzhack1.addEnchantment(Enchantment.DURABILITY, 1);
		spitzhack1.addEnchantment(Enchantment.DIG_SPEED, 1);
		spitzhacken.addOffer(new MerchantOffer(Bronze(3), spitzhack1));
		ItemStack spitzhack2 = UtilItem.RenameItem(new ItemStack(Material.GOLD_PICKAXE), "Goldhacke Lvl 1");
		spitzhack2.addEnchantment(Enchantment.DURABILITY, 1);
		spitzhack2.addEnchantment(Enchantment.DIG_SPEED, 1);
		spitzhacken.addOffer(new MerchantOffer(Silber(1), spitzhack2));
		ItemStack spitzhack3 = UtilItem.RenameItem(new ItemStack(Material.GOLD_PICKAXE), "Goldhacke Lvl 2");
		spitzhack3.addEnchantment(Enchantment.DURABILITY, 1);
		spitzhack3.addEnchantment(Enchantment.DIG_SPEED, 2);
		spitzhacken.addOffer(new MerchantOffer(Silber(3), spitzhack3));
		ItemStack spitzhack4 = UtilItem.RenameItem(new ItemStack(Material.IRON_PICKAXE), "Eisenhacke");
		spitzhack4.addEnchantment(Enchantment.DURABILITY, 1);
		spitzhack4.addEnchantment(Enchantment.DIG_SPEED, 2);
		spitzhacken.addOffer(new MerchantOffer(Gold(3), spitzhack4));
		v.addShop(UtilItem.Item(new ItemStack(274), new String[]{"§aBaue Blöcke deines Gegners ab!"}, "§cSpitzhacken"), spitzhacken, 10);
		
		Merchant rustung = new Merchant();
		ItemStack r1 = UtilItem.RenameItem(UtilItem.LSetColor(new ItemStack(Material.LEATHER_HELMET), c(t.getColor())), t.getColor()+"Lederhelm");
		r1.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL,1);
		r1.addEnchantment(Enchantment.DURABILITY, 1);
		rustung.addOffer(new MerchantOffer(Bronze(1), r1));
		ItemStack r2 = UtilItem.RenameItem(UtilItem.LSetColor(new ItemStack(Material.LEATHER_CHESTPLATE), c(t.getColor())), t.getColor()+"Lederhemd");
		r2.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL,1);
		r2.addEnchantment(Enchantment.DURABILITY, 1);
		rustung.addOffer(new MerchantOffer(Bronze(1), r2));
		ItemStack r3 = UtilItem.RenameItem(UtilItem.LSetColor(new ItemStack(Material.LEATHER_LEGGINGS), c(t.getColor())), t.getColor()+"Lederhose");
		r3.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL,1);
		r3.addEnchantment(Enchantment.DURABILITY, 1);
		rustung.addOffer(new MerchantOffer(Bronze(1), r3));
		ItemStack r4 = UtilItem.RenameItem(UtilItem.LSetColor(new ItemStack(Material.LEATHER_BOOTS), c(t.getColor())), t.getColor()+"Lederschue");
		r4.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL,1);
		r4.addEnchantment(Enchantment.DURABILITY, 1);
		rustung.addOffer(new MerchantOffer(Bronze(1), r4));
		ItemStack r5 = UtilItem.RenameItem(new ItemStack(Material.CHAINMAIL_CHESTPLATE), "Kettenhemd Lvl 1");
		r5.addEnchantment(Enchantment.DURABILITY, 1);
		r5.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
		rustung.addOffer(new MerchantOffer(Silber(1),r5));
		ItemStack r6 = UtilItem.RenameItem(new ItemStack(Material.CHAINMAIL_CHESTPLATE), "Kettenhemd Lvl 2");
		r6.addEnchantment(Enchantment.DURABILITY, 1);
		r6.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
		rustung.addOffer(new MerchantOffer(Silber(3),r6));
		ItemStack r7 = UtilItem.RenameItem(new ItemStack(Material.CHAINMAIL_CHESTPLATE), "Kettenhemd Lvl 3");
		r7.addEnchantment(Enchantment.DURABILITY, 1);
		r7.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
		r7.addEnchantment(Enchantment.PROTECTION_FIRE, 2);
		rustung.addOffer(new MerchantOffer(Silber(7),r7));
		ItemStack r8 = UtilItem.RenameItem(new ItemStack(Material.IRON_CHESTPLATE), "Eisenhemd");
		r8.addEnchantment(Enchantment.DURABILITY, 1);
		r8.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
		r8.addEnchantment(Enchantment.PROTECTION_FIRE, 2);
		rustung.addOffer(new MerchantOffer(Gold(3),r8));
		v.addShop(UtilItem.Item(new ItemStack(Material.IRON_CHESTPLATE), new String[]{"§aSchütze dich vor deinen Gegnern!"}, "§cRüstung"), rustung, 11);
		
		Merchant schwerter = new Merchant();
		ItemStack s1 = UtilItem.RenameItem(new ItemStack(Material.WOOD_SWORD), "Knüppel");
		s1.addEnchantment(Enchantment.KNOCKBACK, 1);
		schwerter.addOffer(new MerchantOffer(Bronze(8), s1));
		ItemStack s2 = UtilItem.RenameItem(new ItemStack(Material.GOLD_SWORD), "Goldschwert Lvl 1");
		s2.addEnchantment(Enchantment.DURABILITY, 1);
		s2.addEnchantment(Enchantment.DAMAGE_ALL, 1);
		schwerter.addOffer(new MerchantOffer(Silber(1), s2));
		ItemStack s3 = UtilItem.RenameItem(new ItemStack(Material.GOLD_SWORD), "Goldschwert Lvl 2");
		s3.addEnchantment(Enchantment.DURABILITY, 1);
		s3.addEnchantment(Enchantment.DAMAGE_ALL, 2);
		schwerter.addOffer(new MerchantOffer(Silber(3), s3));
		ItemStack s4 = UtilItem.RenameItem(new ItemStack(Material.GOLD_SWORD), "Goldschwert Lvl 3");
		s4.addEnchantment(Enchantment.DURABILITY, 1);
		s4.addEnchantment(Enchantment.DAMAGE_ALL, 3);
		schwerter.addOffer(new MerchantOffer(Silber(7), s4));
		ItemStack s5 = UtilItem.RenameItem(new ItemStack(Material.IRON_SWORD), "Eisenschwert");
		s5.addEnchantment(Enchantment.DURABILITY, 1);
		s5.addEnchantment(Enchantment.DAMAGE_ALL, 1);
		s5.addEnchantment(Enchantment.KNOCKBACK, 1);
		schwerter.addOffer(new MerchantOffer(Gold(3), s5));
		v.addShop(UtilItem.Item(new ItemStack(Material.IRON_SWORD), new String[]{"§aGreife deine Gegner an und töte sie!"}, "§cSchwerter"), schwerter, 12);
		
		Merchant bogen = new Merchant();
		ItemStack b0 = UtilItem.RenameItem(new ItemStack(Material.BOW), "§bBogen Lvl 1");
		bogen.addOffer(new MerchantOffer(Silber(10),b0));
		ItemStack b1 = UtilItem.RenameItem(new ItemStack(Material.BOW), "Bogen Lvl 2");
		b1.addEnchantment(Enchantment.ARROW_INFINITE, 1);
		bogen.addOffer(new MerchantOffer(Gold(3),b1));
		ItemStack b2 = UtilItem.RenameItem(new ItemStack(Material.BOW), "Bogen Lvl 3");
		b2.addEnchantment(Enchantment.ARROW_INFINITE, 1);
		b2.addEnchantment(Enchantment.ARROW_DAMAGE, 1);
		bogen.addOffer(new MerchantOffer(Gold(7),b2));
		ItemStack b3 = UtilItem.RenameItem(new ItemStack(Material.BOW), "Bogen Lvl 4");
		b3.addEnchantment(Enchantment.ARROW_INFINITE, 1);
		b3.addEnchantment(Enchantment.ARROW_DAMAGE, 2);
		b3.addEnchantment(Enchantment.ARROW_KNOCKBACK, 1);
		bogen.addOffer(new MerchantOffer(Gold(13),b3));
		bogen.addOffer(new MerchantOffer(Bronze(5),UtilItem.RenameItem(new ItemStack(Material.ARROW), "Pfeil")));
		v.addShop(UtilItem.Item(new ItemStack(Material.BOW), new String[]{"§aDer fernkampf hat schon einige Kriege entschieden!"}, "§cBögen"), bogen, 13);
		
		Merchant nahrung = new Merchant();
		nahrung.addOffer(new MerchantOffer(Bronze(2), UtilItem.RenameItem(new ItemStack(Material.APPLE), "Apfel")));
		nahrung.addOffer(new MerchantOffer(Bronze(3), UtilItem.RenameItem(new ItemStack(Material.BREAD), "Brot")));
		nahrung.addOffer(new MerchantOffer(Bronze(3), UtilItem.RenameItem(new ItemStack(Material.getMaterial(364)), "Steak")));
		nahrung.addOffer(new MerchantOffer(Bronze(5), UtilItem.RenameItem(new ItemStack(Material.CAKE), "Kuchen")));
		nahrung.addOffer(new MerchantOffer(Silber(2), UtilItem.RenameItem(new ItemStack(Material.GOLDEN_APPLE), "Goldapfel")));
		v.addShop(UtilItem.Item(new ItemStack(260), new String[]{"§aAuch ein Soldat muss irgendwann was essen!"}, "§cNahrung"),nahrung, 14);
		
		Merchant kisten = new Merchant();
		kisten.addOffer(new MerchantOffer(Silber(2), UtilItem.RenameItem(new ItemStack(Material.CHEST), "Kiste")));
		v.addShop(UtilItem.Item(new ItemStack(54), new String[]{"§aDein Inventar ist nicht Unendlich, die Anzahl der Kisten schon!"}, "§cKisten"), kisten, 15);
		
		Merchant trank = new Merchant();
		trank.addOffer(new MerchantOffer(Silber(5), UtilItem.RenameItem(new ItemStack(Material.POTION,1,(byte)8261), "Heilung")));
		trank.addOffer(new MerchantOffer(Silber(5), UtilItem.RenameItem(new ItemStack(Material.POTION,1,(byte)8194), "Schnelligkeit")));
		trank.addOffer(new MerchantOffer(Gold(5), UtilItem.RenameItem(new ItemStack(Material.POTION,1,(byte)8227), "Feuerresitänz")));
		trank.addOffer(new MerchantOffer(Gold(5), UtilItem.RenameItem(new ItemStack(Material.POTION,1,(byte)8193), "Regeneration")));
		trank.addOffer(new MerchantOffer(Gold(5), UtilItem.RenameItem(new ItemStack(Material.POTION,1,(byte)8201), "Stärke")));
		v.addShop(UtilItem.Item(new ItemStack(Material.POTION), new String[]{"§aAls Soldat kann man schonmal die eine oder andere Droge gebrauchen!"}, "§cTränke"), trank, 16);
		
		Merchant spezial = new Merchant();
		spezial.addOffer(new MerchantOffer(Silber(3), UtilItem.RenameItem(new ItemStack(Material.TNT), "TNT")));
		spezial.addOffer(new MerchantOffer(Gold(1), UtilItem.RenameItem(new ItemStack(Material.FLINT_AND_STEEL), "Feuerzeug")));
		spezial.addOffer(new MerchantOffer(Bronze(5), UtilItem.RenameItem(new ItemStack(Material.LADDER), "Leiter")));
		spezial.addOffer(new MerchantOffer(Silber(3), UtilItem.RenameItem(new ItemStack(Material.getMaterial(30)), "Spinnennetz")));
		v.addShop(UtilItem.Item(new ItemStack(46), new String[]{"§aZeige deinen Gegnern wer der Chef auf dem Schlachtfeld ist!"}, "§cSpezial"), spezial, 17);
		v.finish();
	}
	
	public static DyeColor cd(String s){
		if(s.equalsIgnoreCase(Color.RED.toString()))return DyeColor.RED;
		if(s.equalsIgnoreCase(Color.YELLOW.toString()))return DyeColor.YELLOW;
		if(s.equalsIgnoreCase(Color.BLUE.toString()))return DyeColor.BLUE;
		if(s.equalsIgnoreCase(Color.GREEN.toString()))return DyeColor.GREEN;
		if(s.equalsIgnoreCase(Color.GRAY.toString()))return DyeColor.GRAY;
		if(s.equalsIgnoreCase(Color.WHITE.toString()))return DyeColor.WHITE;
		if(s.equalsIgnoreCase(Color.ORANGE.toString()))return DyeColor.ORANGE;
		if(s.equalsIgnoreCase(Color.PURPLE.toString()))return DyeColor.PURPLE;
		return DyeColor.BLACK;
	}
	
	public static org.bukkit.Color c(String s){
		if(s.equalsIgnoreCase(Color.RED.toString()))return org.bukkit.Color.RED;
		if(s.equalsIgnoreCase(Color.YELLOW.toString()))return org.bukkit.Color.YELLOW;
		if(s.equalsIgnoreCase(Color.BLUE.toString()))return org.bukkit.Color.BLUE;
		if(s.equalsIgnoreCase(Color.GREEN.toString()))return org.bukkit.Color.GREEN;
		if(s.equalsIgnoreCase(Color.GRAY.toString()))return org.bukkit.Color.GRAY;
		if(s.equalsIgnoreCase(Color.WHITE.toString()))return org.bukkit.Color.WHITE;
		if(s.equalsIgnoreCase(Color.ORANGE.toString()))return org.bukkit.Color.ORANGE;
		if(s.equalsIgnoreCase(Color.PURPLE.toString()))return org.bukkit.Color.PURPLE;
		return org.bukkit.Color.BLACK;
	}
}
