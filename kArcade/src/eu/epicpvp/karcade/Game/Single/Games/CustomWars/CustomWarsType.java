package eu.epicpvp.karcade.Game.Single.Games.CustomWars;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import dev.wolveringer.dataclient.gamestats.GameType;
import lombok.Getter;
import eu.epicpvp.kcore.Enum.Team;
import eu.epicpvp.kcore.Util.UtilMath;

public enum CustomWarsType {
	_4x32(60*60*3,-1,32,64,128,new Team[]{Team.RED,Team.BLUE,Team.YELLOW,Team.GREEN},1,Arrays.asList(GameType.BedWars)),
	_8x16(60*60*3,-1,16,64,128,new Team[]{Team.RED,Team.BLUE,Team.YELLOW,Team.GREEN,Team.GRAY,Team.PINK,Team.ORANGE,Team.PURPLE},1,Arrays.asList(GameType.BedWars)),
	_8x1(60*60,50,1,4,8,new Team[]{Team.RED,Team.BLUE,Team.YELLOW,Team.GREEN,Team.GRAY,Team.PINK,Team.ORANGE,Team.PURPLE},1,Arrays.asList(GameType.BedWars)),
	_2x4(60*60,10,4,4,8,new Team[]{Team.RED,Team.BLUE},1,Arrays.asList(GameType.BedWars,GameType.SheepWars)),
	_2x8(60*60,10,8,8,16,new Team[]{Team.RED,Team.BLUE},3,Arrays.asList(GameType.BedWars,GameType.SheepWars)),
	_2x5(60*60,10,5,4,10,new Team[]{Team.RED,Team.BLUE},1,Arrays.asList(GameType.BedWars,GameType.SheepWars)),
	_2x1(60*60,10,1,2,2,new Team[]{Team.RED,Team.BLUE},1,Arrays.asList(GameType.BedWars,GameType.SheepWars)),
	_4x4(60*60,10,4,8,16,new Team[]{Team.RED,Team.BLUE,Team.YELLOW,Team.GREEN},1,Arrays.asList(GameType.BedWars,GameType.SheepWars)),
	_4x5(60*60,10,5,8,20,new Team[]{Team.RED,Team.BLUE,Team.YELLOW,Team.GREEN},1,Arrays.asList(GameType.BedWars,GameType.SheepWars)),
	_4x3(60*60,10,3,6,12,new Team[]{Team.RED,Team.BLUE,Team.YELLOW,Team.GREEN},1,Arrays.asList(GameType.BedWars,GameType.SheepWars)),
	_4x2(60*60,10,2,4,8,new Team[]{Team.RED,Team.BLUE,Team.YELLOW,Team.GREEN},1,Arrays.asList(GameType.BedWars,GameType.SheepWars));

	@Getter
	private Team[] team;
	@Getter
	private int min;
	@Getter
	private int max;
	@Getter
	private int team_size;
	@Getter
	private int drop_rate;
	@Getter
	private int rate;
	@Getter
	private int inGameTime;
	@Getter
	private List<GameType> types;
	
	private CustomWarsType(int inGameTime, int rate,int team_size,int min,int max,Team[] team, int drop_rate,List<GameType> types){
		this.max=max;
		this.inGameTime=inGameTime;
		this.min=min;
		this.drop_rate=drop_rate;
		this.team=team;
		this.team_size=team_size;
		this.rate=rate;
		this.types=types;
	}

	public static CustomWarsType random(GameType type){
		int a = 0;
		HashMap<Integer,CustomWarsType> list = new HashMap<>();
		for(CustomWarsType t : CustomWarsType.values()){
			if(!t.getTypes().contains(type))continue;
			if(t.getRate() == -1)continue;
			for(int i = 0; i < t.getRate()+1; i++){
				list.put(a,t);
				a++;
			}
		}
		
		System.out.println("[EpicPvP] CustomWarsType RANDOM "+list.size());
		return list.get(UtilMath.RandomInt(list.size(), 0));
	}
		
	public static CustomWarsType getTypeWithSize(int size){
		for(CustomWarsType type : CustomWarsType.values()){
			if(type.getTeam().length==size){
				return type;
			}
		}
		return CustomWarsType._2x4;
	}

	}
