package me.kingingo.karcade.Enum;

import me.kingingo.kcore.Util.C;
import me.kingingo.kcore.Util.UtilItem;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum Team {
RED("RED", UtilItem.RenameItem(new ItemStack(Material.WOOL,1,(byte)14), C.cRed+"Team - Rot"),C.cRed),
YELLOW("YELLOW", UtilItem.RenameItem(new ItemStack(Material.WOOL,1,(byte)4), C.cYellow+"Team - Gelb"),C.cYellow),
BLUE("BLUE", UtilItem.RenameItem(new ItemStack(Material.WOOL,1,(byte)11), C.cBlue+"Team - Blau"),C.cBlue),
GREEN("GREEN",UtilItem.RenameItem(new ItemStack(Material.WOOL,1,(byte)5), C.cGreen+"Team - Green"),C.cGreen),

DISTRICT_1("District 1",UtilItem.RenameItem(new ItemStack(Material.WORKBENCH,1), C.cGreen+"District 1"),C.cAqua),
DISTRICT_2("District 2",UtilItem.RenameItem(new ItemStack(Material.WORKBENCH,1), C.cGreen+"District 2"),C.cAqua),
DISTRICT_3("District 3",UtilItem.RenameItem(new ItemStack(Material.WORKBENCH,1), C.cGreen+"District 3"),C.cAqua),
DISTRICT_4("District 4",UtilItem.RenameItem(new ItemStack(Material.WORKBENCH,1), C.cGreen+"District 4"),C.cAqua),
DISTRICT_5("District 5",UtilItem.RenameItem(new ItemStack(Material.WORKBENCH,1), C.cGreen+"District 5"),C.cAqua),
DISTRICT_6("District 6",UtilItem.RenameItem(new ItemStack(Material.WORKBENCH,1), C.cGreen+"District 6"),C.cAqua),
DISTRICT_7("District 7",UtilItem.RenameItem(new ItemStack(Material.WORKBENCH,1), C.cGreen+"District 7"),C.cAqua),
DISTRICT_8("District 8",UtilItem.RenameItem(new ItemStack(Material.WORKBENCH,1), C.cGreen+"District 8"),C.cAqua),
DISTRICT_9("District 9",UtilItem.RenameItem(new ItemStack(Material.WORKBENCH,1), C.cGreen+"District 9"),C.cAqua),
DISTRICT_10("District 10",UtilItem.RenameItem(new ItemStack(Material.WORKBENCH,1), C.cGreen+"District 10"),C.cAqua),
DISTRICT_11("District 11",UtilItem.RenameItem(new ItemStack(Material.WORKBENCH,1), C.cGreen+"District 11"),C.cAqua),
DISTRICT_12("District 12",UtilItem.RenameItem(new ItemStack(Material.WORKBENCH,1), C.cGreen+"District 12"),C.cAqua),

TRAITOR("Traitor",UtilItem.RenameItem(new ItemStack(Material.IRON_SWORD), C.cRed+"Traitor"),C.cRed),
INOCCENT("Inoccent",UtilItem.RenameItem(new ItemStack(Material.BOW),C.cGray+"Inoccent"), C.cGray),
DETECTIVE("Detective", UtilItem.RenameItem(new ItemStack(Material.STICK),C.cBlue+"Detective"), C.cBlue),

SOLO("SOLO",null,C.cGray);

private String n;
private ItemStack i;
private String c;
private int player;
private Team(String n,ItemStack i,String c){
	this.n=n;
	this.i=i;
	this.c=c;
}

public void setPlayer(int i){
	player=i;
}

public int getPlayer(){
	return player;
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