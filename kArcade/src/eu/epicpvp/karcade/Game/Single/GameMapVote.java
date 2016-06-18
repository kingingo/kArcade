package eu.epicpvp.karcade.Game.Single;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import dev.wolveringer.client.Callback;
import eu.epicpvp.karcade.Game.Single.Addons.VoteHandler.Vote;
import eu.epicpvp.karcade.Game.Single.Addons.VoteHandler.Votes;
import eu.epicpvp.karcade.Game.World.GameMap;
import eu.epicpvp.kcore.Lists.kSort;
import eu.epicpvp.kcore.Util.UtilInv;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.kcore.Util.UtilServer;

public class GameMapVote extends Votes{

	public GameMapVote(SingleWorldData worldData, int map_amount) {
		super(null,UtilItem.RenameItem(new ItemStack(Material.EMPTY_MAP), "§eMap Vote") ,new Callback<ArrayList<kSort<Vote>>>() {

			@Override
			public void call(ArrayList<kSort<Vote>> votes, Throwable exception) {
				GameMap map = ((Vote<GameMap>)votes.get(0).getObject()).getValue();
				votes.remove(0);
				
				for(kSort<Vote> v : votes)worldData.Uninitialize(((Vote<GameMap>)v.getObject()).getValue());
				
				worldData.Initialize(map);
				worldData.setMap(map);
				worldData.getManager().getGame().setSet_default_scoreboard(true);
				worldData.log("The Map "+worldData.getMap().getMapName()+" used..");
			}
		}, 0);
		
		
		try {
			ArrayList<Vote<GameMap>> votes = new ArrayList<>();
			File[] files = (map_amount == -1 ? worldData.toFiles() : worldData.randomMaps(map_amount));
			
			UtilServer.getServer().getScheduler().runTaskAsynchronously(worldData.getManager().getInstance(), new Runnable()
		    {
		      public void run()
		      {
		  		File[] files2 = new File[files.length];
		        for(int i = 0; i < files.length; i++)
		        	files2[i]=worldData.UnzipWorld(files[i]);
		        
		        UtilServer.getServer().getScheduler().runTask(worldData.getManager().getInstance(), new Runnable()
		        {
		          public void run(){
		        	for(File file : files2){
		        		GameMap map = worldData.LoadWorldConfig(new GameMap(null, file, worldData));
		        		votes.add(new Vote<GameMap>(map,map.getItem()));
		        	}
		        	
		        	setVotes(votes.toArray(new Vote[]{}));
		        	
		        	((SingleGame)worldData.getManager().getGame()).getVoteHandler().init();
		          }
		        });
		      }
		    });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
