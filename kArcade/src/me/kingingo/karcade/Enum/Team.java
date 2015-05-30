package me.kingingo.karcade.Enum;

import me.kingingo.kcore.Util.UtilItem;

import me.kingingo.kcore.Util.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum Team {
RED("RED", UtilItem.RenameItem(new ItemStack(Material.WOOL,1,(byte)14), Color.RED+"Team - Rot"),Color.RED),
YELLOW("YELLOW", UtilItem.RenameItem(new ItemStack(Material.WOOL,1,(byte)4), Color.YELLOW+"Team - Gelb"),Color.YELLOW),
BLUE("BLUE", UtilItem.RenameItem(new ItemStack(Material.WOOL,1,(byte)11), Color.BLUE+"Team - Blau"),Color.BLUE),
GREEN("GREEN",UtilItem.RenameItem(new ItemStack(Material.WOOL,1,(byte)5), Color.GREEN+"Team - Grün"),Color.GREEN),
PINK("PINK",UtilItem.RenameItem(new ItemStack(Material.WOOL,1,(byte)6), Color.PINK+"Team - Pink"),Color.PINK),
GRAY("GRAY",UtilItem.RenameItem(new ItemStack(Material.WOOL,1,(byte)8), Color.GRAY+"Team - Gray"),Color.GRAY),
BLACK("BLACK", UtilItem.RenameItem(new ItemStack(Material.WOOL,1,(byte)15), Color.BLACK+"Team - Schwarz"),Color.BLACK),
WHITE("WHITE",UtilItem.RenameItem(new ItemStack(Material.WOOL,1), Color.WHITE+"Team - White"),Color.WHITE),
PURPLE("PURPLE",UtilItem.RenameItem(new ItemStack(Material.WOOL, 1,(byte)10),Color.PURPLE+"Team - Lila"),Color.PURPLE ),
ORANGE("ORANGE",UtilItem.RenameItem(new ItemStack(Material.WOOD,1,(byte)1), Color.ORANGE+"Team - Orange"),Color.ORANGE),

VILLAGE_RED("VILLAGE_RED", UtilItem.RenameItem(new ItemStack(Material.WOOL,1,(byte)14), Color.RED+"Villager - Rot"),Color.RED),
VILLAGE_YELLOW("VILLAGE_YELLOW", UtilItem.RenameItem(new ItemStack(Material.WOOL,1,(byte)4), Color.YELLOW+"Villager - Gelb"),Color.YELLOW),
VILLAGE_BLUE("VILLAGE_BLUE", UtilItem.RenameItem(new ItemStack(Material.WOOL,1,(byte)11), Color.BLUE+"Villager - Blau"),Color.BLUE),
VILLAGE_GREEN("VILLAGE_GREEN",UtilItem.RenameItem(new ItemStack(Material.WOOL,1,(byte)5), Color.GREEN+"Villager - Grün"),Color.GREEN),
VILLAGE_PINK("VILLAGE_PINK", UtilItem.RenameItem(new ItemStack(Material.WOOL,1,(byte)6), Color.PINK+"Villager - Pink"),Color.PINK),
VILLAGE_ORANGE("VILLAGE_ORANGE", UtilItem.RenameItem(new ItemStack(Material.WOOL,1,(byte)1), Color.ORANGE+"Villager - Orange"),Color.ORANGE),
VILLAGE_PURPLE("VILLAGE_PURPLE", UtilItem.RenameItem(new ItemStack(Material.WOOL,1,(byte)10), Color.PURPLE+"Villager - Lila"),Color.PURPLE),
VILLAGE_GRAY("VILLAGE_GRAY",UtilItem.RenameItem(new ItemStack(Material.WOOL,1,(byte)8), Color.GRAY+"Villager - Grau"),Color.GRAY),

SHEEP_RED("SHEEP_RED", UtilItem.RenameItem(new ItemStack(Material.WOOL,1,(byte)14), Color.RED+"Sheep - Rot"),Color.RED),
SHEEP_YELLOW("SHEEP_YELLOW", UtilItem.RenameItem(new ItemStack(Material.WOOL,1,(byte)4), Color.YELLOW+"Sheep - Gelb"),Color.YELLOW),
SHEEP_BLUE("SHEEP_BLUE", UtilItem.RenameItem(new ItemStack(Material.WOOL,1,(byte)11), Color.BLUE+"Sheep - Blau"),Color.BLUE),
SHEEP_GREEN("SHEEP_GREEN",UtilItem.RenameItem(new ItemStack(Material.WOOL,1,(byte)5), Color.GREEN+"Sheep - Grün"),Color.GREEN),
SHEEP_PINK("SHEEP_PINK", UtilItem.RenameItem(new ItemStack(Material.WOOL,1,(byte)6), Color.PINK+"Sheep - Pink"),Color.PINK),
SHEEP_ORANGE("SHEEP_ORANGE", UtilItem.RenameItem(new ItemStack(Material.WOOL,1,(byte)1), Color.ORANGE+"Sheep - Orange"),Color.ORANGE),
SHEEP_PURPLE("SHEEP_PURPLE", UtilItem.RenameItem(new ItemStack(Material.WOOL,1,(byte)10), Color.PURPLE+"Sheep - Lila"),Color.PURPLE),
SHEEP_GRAY("SHEEP_GRAY",UtilItem.RenameItem(new ItemStack(Material.WOOL,1,(byte)8), Color.GRAY+"Sheep - Grau"),Color.GRAY),


GOLD("GOLD",UtilItem.RenameItem(new ItemStack(Material.GOLD_BLOCK,1), Color.YELLOW+"Gold"),Color.YELLOW),
SILBER("SILBER",UtilItem.RenameItem(new ItemStack(Material.IRON_BLOCK,1), Color.YELLOW+"Silber"),Color.YELLOW),
BRONZE("BRONZE",UtilItem.RenameItem(new ItemStack(Material.COAL_BLOCK,1), Color.YELLOW+"Bronze"),Color.YELLOW),
DIAMOND("DIAMOND",UtilItem.RenameItem(new ItemStack(Material.DIAMOND_BLOCK,1), Color.AQUA+"Diamanten"),Color.AQUA),

DISTRICT_1("District 1",UtilItem.RenameItem(new ItemStack(Material.WORKBENCH,1), Color.GREEN+"District 1"),Color.AQUA),
DISTRICT_2("District 2",UtilItem.RenameItem(new ItemStack(Material.WORKBENCH,1), Color.GREEN+"District 2"),Color.AQUA),
DISTRICT_3("District 3",UtilItem.RenameItem(new ItemStack(Material.WORKBENCH,1), Color.GREEN+"District 3"),Color.AQUA),
DISTRICT_4("District 4",UtilItem.RenameItem(new ItemStack(Material.WORKBENCH,1), Color.GREEN+"District 4"),Color.AQUA),
DISTRICT_5("District 5",UtilItem.RenameItem(new ItemStack(Material.WORKBENCH,1), Color.GREEN+"District 5"),Color.AQUA),
DISTRICT_6("District 6",UtilItem.RenameItem(new ItemStack(Material.WORKBENCH,1), Color.GREEN+"District 6"),Color.AQUA),
DISTRICT_7("District 7",UtilItem.RenameItem(new ItemStack(Material.WORKBENCH,1), Color.GREEN+"District 7"),Color.AQUA),
DISTRICT_8("District 8",UtilItem.RenameItem(new ItemStack(Material.WORKBENCH,1), Color.GREEN+"District 8"),Color.AQUA),
DISTRICT_9("District 9",UtilItem.RenameItem(new ItemStack(Material.WORKBENCH,1), Color.GREEN+"District 9"),Color.AQUA),
DISTRICT_10("District 10",UtilItem.RenameItem(new ItemStack(Material.WORKBENCH,1), Color.GREEN+"District 10"),Color.AQUA),
DISTRICT_11("District 11",UtilItem.RenameItem(new ItemStack(Material.WORKBENCH,1), Color.GREEN+"District 11"),Color.AQUA),
DISTRICT_12("District 12",UtilItem.RenameItem(new ItemStack(Material.WORKBENCH,1), Color.GREEN+"District 12"),Color.AQUA),

TRAITOR("Traitor",UtilItem.RenameItem(new ItemStack(Material.IRON_SWORD), Color.RED+"Traitor"),Color.RED),
INOCCENT("Inoccent",UtilItem.RenameItem(new ItemStack(Material.BOW),Color.GRAY+"Inoccent"), Color.GRAY),
DETECTIVE("Detective", UtilItem.RenameItem(new ItemStack(Material.STICK),Color.BLUE+"Detective"), Color.BLUE),

SOLO("SOLO",null,Color.GRAY);

private String n;
private ItemStack i;
private String c;
private Team(String n,ItemStack i,String c){
	this.n=n;
	this.i=i;
	this.c=c;
}

public String getColor(){
	return this.c;
}

public ItemStack getItem(){
	return this.i;
}

public String Name(){
	return n;
}

}