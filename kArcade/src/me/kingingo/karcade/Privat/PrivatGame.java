package me.kingingo.karcade.Privat;

import java.io.File;
import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;
import me.kingingo.karcade.kArcade;
import me.kingingo.karcade.Game.Single.Games.BedWars.BedWarsType;
import me.kingingo.karcade.Game.Single.Games.SheepWars.SheepWarsType;
import me.kingingo.karcade.Game.Single.Games.SkyWars.SkyWarsType;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.Inventory.InventoryPageBase;
import me.kingingo.kcore.Inventory.Item.Click;
import me.kingingo.kcore.Inventory.Item.Buttons.ButtonBase;
import me.kingingo.kcore.Util.InventorySize;
import me.kingingo.kcore.Util.UtilEvent.ActionType;
import me.kingingo.kcore.Util.UtilItem;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PrivatGame{

	@Getter
	private GameType type;
	@Getter
	private BedWarsType bedWarsType=BedWarsType._2x4;
	@Getter
	private SheepWarsType sheepWarsType=SheepWarsType._2x4;
	@Getter
	private SkyWarsType skyWarsType=SkyWarsType._12x1;
	@Getter
	private InventoryPageBase choose;
	@Getter
	private ArrayList<String> maps = new ArrayList<>();
	@Getter
	@Setter
	private String map="NONE";
	
	public PrivatGame(GameType type){
		this.type=type;
		
		if(type==GameType.BedWars)isBedWarsType();
		if(type==GameType.SheepWars)isSheepWars();
		if(type==GameType.SkyWars)isSkyWarsType();
	}
	
	 public void LoadFiles(String gameName){
		    File folder = new File(kArcade.FilePath+File.separator+gameName);
		    if (!folder.exists()) folder.mkdirs();
		    System.out.println("Suche Maps in: " + folder);

		    for (File file : folder.listFiles())
		    {
		      if (file.isFile())
		      {
		        String name = file.getName();

		        if (name.length() >= 5)
		        {
		          name = name.substring(name.length() - 4, name.length());

		          if (!file.getName().equals(".zip"))
		          {
		            maps.add(kArcade.FilePath+File.separator+gameName+File.separator+file.getName().substring(0, file.getName().length() - 4)+".zip");
		          }
		        }
		      }
		    }
		    for (String map : maps) {
		      System.out.println("Maps: " + map);
		    }
		  }
	
	public void loadMaps(){
		if(type==GameType.BedWars){
			LoadFiles(type.getTyp()+bedWarsType.getTeam().length);
		}else if(type==GameType.SheepWars){
			LoadFiles(type.getTyp()+sheepWarsType.getTeam().length);
		}else if(type==GameType.SkyWars){
			LoadFiles(type.getTyp()+skyWarsType.getTeam().length);
		}else{
			LoadFiles(type.getTyp());
		}
	}
	
	public void isBedWarsType(){
		InventorySize size=null;
		for(InventorySize i : InventorySize.values()){
			if(i.getSize()>=BedWarsType.values().length){
				size=i;
				break;
			}
		}
		
		choose = new InventoryPageBase(size.getSize(), "BedWars Type:");
		
		for(BedWarsType type : BedWarsType.values()){
			choose.addButton(new ButtonBase(new Click(){

				@Override
				public void onClick(Player player, ActionType ac, Object obj) {
					if(obj instanceof ItemStack){
						if( ((ItemStack)obj).hasItemMeta() ){
							if(((ItemStack)obj).getItemMeta().hasDisplayName()){
								if(((ItemStack)obj).getItemMeta().getDisplayName().contains("§bTeams ")){
									int size = Integer.valueOf(((ItemStack)obj).getItemMeta().getDisplayName().split(" ")[1]);
									bedWarsType=BedWarsType.getBedWarsTypeWithSize(size);
								}
							}
						}
					}
				}
				
			}, UtilItem.RenameItem(new ItemStack(Material.BED), "§bTeams "+type.getTeam().length)));
		}
	}
	
	public void isSkyWarsType(){
		InventorySize size=null;
		for(InventorySize i : InventorySize.values()){
			if(i.getSize()>=SkyWarsType.values().length){
				size=i;
				break;
			}
		}
		
		choose = new InventoryPageBase(size.getSize(), "SkyWars Type:");
		
		for(SkyWarsType type : SkyWarsType.values()){
			choose.addButton(new ButtonBase(new Click(){

				@Override
				public void onClick(Player player, ActionType ac, Object obj) {
					if(obj instanceof ItemStack){
						if( ((ItemStack)obj).hasItemMeta() ){
							if(((ItemStack)obj).getItemMeta().hasDisplayName()){
								if(((ItemStack)obj).getItemMeta().getDisplayName().contains("§bTeams ")){
									int size = Integer.valueOf(((ItemStack)obj).getItemMeta().getDisplayName().split(" ")[1]);
									skyWarsType=SkyWarsType.getSkyWarsTypeWithSize(size);
								}
							}
						}
					}
				}
				
			}, UtilItem.RenameItem(new ItemStack(Material.ANVIL), "§bTeams "+type.getTeam().length)));
		}
	}
	
	public void isSheepWars(){
		InventorySize size=null;
		for(InventorySize i : InventorySize.values()){
			if(i.getSize()>=SheepWarsType.values().length){
				size=i;
				break;
			}
		}
		
		choose = new InventoryPageBase(size.getSize(), "SheepWars Type:");
		
		for(SheepWarsType type : SheepWarsType.values()){
			choose.addButton(new ButtonBase(new Click(){

				@Override
				public void onClick(Player player, ActionType ac, Object obj) {
					if(obj instanceof ItemStack){
						if( ((ItemStack)obj).hasItemMeta() ){
							if(((ItemStack)obj).getItemMeta().hasDisplayName()){
								if(((ItemStack)obj).getItemMeta().getDisplayName().contains("§bTeams ")){
									int size = Integer.valueOf(((ItemStack)obj).getItemMeta().getDisplayName().split(" ")[1]);
									sheepWarsType=SheepWarsType.getSheepWarsTypeWithSize(size);
								}
							}
						}
					}
				}
				
			}, UtilItem.RenameItem(new ItemStack(Material.WOOL), "§bTeams "+type.getTeam().length)));
		}
	}
	
}
