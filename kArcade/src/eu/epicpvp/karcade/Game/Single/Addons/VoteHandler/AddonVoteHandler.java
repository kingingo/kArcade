package eu.epicpvp.karcade.Game.Single.Addons.VoteHandler;

import java.util.ArrayList;
import java.util.Collections;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import eu.epicpvp.datenserver.definitions.dataserver.gamestats.GameState;
import eu.epicpvp.karcade.Game.Events.GamePreStartEvent;
import eu.epicpvp.karcade.Game.Events.GameStartEvent;
import eu.epicpvp.karcade.Game.Single.SingleGame;
import eu.epicpvp.kcore.Enum.Zeichen;
import eu.epicpvp.kcore.Hologram.nametags.NameTagMessage;
import eu.epicpvp.kcore.Hologram.nametags.NameTagType;
import eu.epicpvp.kcore.Listener.kListener;
import eu.epicpvp.kcore.Lists.kSort;
import eu.epicpvp.kcore.Util.UtilEvent;
import eu.epicpvp.kcore.Util.UtilEvent.ActionType;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.kcore.Util.UtilServer;

public class AddonVoteHandler extends kListener{

	private SingleGame game;
	private ArrayList<kSort<Votes>> votes;
	private boolean active=false;
	private NameTagMessage ntm;

	public AddonVoteHandler(SingleGame game) {
		super(game.getManager().getInstance(), "AddonVoteHandler");
		this.game=game;
		this.votes=new ArrayList<>();
	}

	public void add(Votes vote){
		if(active){
			 new Exception("VoteHandler hat schon gestartet!?").printStackTrace();
		}else{
			this.votes.add(new kSort<Votes>(vote,vote.getPriority()));
		}
	}

	@EventHandler
	public void update(PlayerVoteEvent ev){
		ntm.setLines(getNTMString());
		for(Player player : UtilServer.getPlayers()){
			if(player.isSneaking()){
				Location loc = player.getLocation();
				loc.add(0, ntm.getLineSpacing() * ntm.getLines().length, 0);
				loc.add(player.getEyeLocation().getDirection().setY(0).multiply(2));
				ntm.clear(player);
				ntm.sendToPlayer(player, loc);
			}
		}
	}

	@EventHandler
	public void move(PlayerMoveEvent ev){
		if(active && game.getState() == GameState.LobbyPhase){
			if(ev.getPlayer().isSneaking()){
				Location loc = ev.getPlayer().getLocation();
				loc.add(0, ntm.getLineSpacing() * ntm.getLines().length, 0);
				loc.add(ev.getPlayer().getEyeLocation().getDirection().setY(0).multiply(2));
				ntm.move(ev.getPlayer(), loc);
			}
		}
	}

	@EventHandler(priority=EventPriority.LOWEST)
	public void start(GameStartEvent ev){
		if(active)done();
	}

	@EventHandler
	public void preStart(GamePreStartEvent ev){
		if(ev.getGame().getMinPlayers() <= UtilServer.getPlayers().size() && active)done();

	}

	@EventHandler
	public void sneak(PlayerToggleSneakEvent ev){
		if(active && game.getState() == GameState.LobbyPhase){
			if(ev.isSneaking()){
				Location loc = ev.getPlayer().getLocation();
				loc.add(0, ntm.getLineSpacing() * ntm.getLines().length, 0);
				loc.add(ev.getPlayer().getEyeLocation().getDirection().setY(0).multiply(2));
				ntm.sendToPlayer(ev.getPlayer(), loc);

			}else{
				ntm.clear();
			}
		}
	}

	@EventHandler
	public void interact(PlayerInteractEvent ev){
		if(active && game.getState() == GameState.LobbyPhase && ev.getPlayer().getItemInHand()!=null && UtilEvent.isAction(ev, ActionType.RIGHT)){
			for(kSort<Votes> s : votes){
				if(UtilItem.ItemNameEquals(s.getObject().getVoteItem(), ev.getPlayer().getItemInHand())){
					s.getObject().open(ev.getPlayer());
					ev.setCancelled(true);
					break;
				}
			}
		}
	}

	@EventHandler
	public void quit(PlayerQuitEvent ev){
		if(active && game.getState() == GameState.LobbyPhase){
			for(kSort<Votes> s : votes)s.getObject().remove(ev.getPlayer());
		}
	}

	@EventHandler
	public void join(PlayerJoinEvent ev){
		if(active && game.getState() == GameState.LobbyPhase){
			for(kSort<Votes> s : votes)
				ev.getPlayer().getInventory().addItem(s.getObject().getVoteItem());
		}
	}

	private String[] getNTMString(){
		ArrayList<String> list = new ArrayList<>();
		list.add("§eVote List:");

		for(kSort<Votes> s : votes){
			list.add(" ");
			Votes v = s.getObject();
			list.add(v.getVoteItem().getItemMeta().getDisplayName());

			ArrayList<kSort<Vote>> vlist = v.getVoteList();
			for(kSort<Vote> ss : vlist){
				Vote vv = ss.getObject();
				list.add(vv.getItem().getItemMeta().getDisplayName()+" "+Zeichen.DOUBLE_ARROWS_R.getIcon()+" "+vv.getVotes());
			}
		}

		return list.toArray(new String[]{});
	}

	public void init(){
		active=true;
		Collections.sort(this.votes, kSort.DESCENDING);
		for(kSort<Votes> s : votes)s.getObject().init();
		ntm=new NameTagMessage(NameTagType.PACKET, new Location(Bukkit.getWorld("world"),0,90,0), getNTMString());
		logMessage("init!");
	}

	public void done(){
		Collections.sort(this.votes, kSort.DESCENDING);
		for(kSort<Votes> s : votes)
			s.getObject().done();

		HandlerList.unregisterAll(this);
		logMessage("done!");
	}
}
