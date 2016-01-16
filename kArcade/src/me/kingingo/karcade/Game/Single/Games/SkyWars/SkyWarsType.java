package me.kingingo.karcade.Game.Single.Games.SkyWars;

import java.util.HashMap;

import lombok.Getter;
import me.kingingo.kcore.Enum.Team;
import me.kingingo.kcore.Util.UtilMath;

public enum SkyWarsType {
_32x4(0,4,1,128,new Team[]{Team.TEAM_1,Team.TEAM_2,Team.TEAM_3,Team.TEAM_4,Team.TEAM_5,Team.TEAM_6,Team.TEAM_7,Team.TEAM_8,Team.TEAM_9,Team.TEAM_10,Team.TEAM_11,Team.TEAM_12,Team.TEAM_13,Team.TEAM_14,Team.TEAM_15,Team.TEAM_16,Team.TEAM_17,Team.TEAM_18,Team.TEAM_19,Team.TEAM_20,Team.TEAM_21,Team.TEAM_22,Team.TEAM_23,Team.TEAM_24,Team.TEAM_25,Team.TEAM_26,Team.TEAM_27,Team.TEAM_28,Team.TEAM_29,Team.TEAM_30,Team.TEAM_31,Team.TEAM_32}),

_8x1(50,1,4,8,new Team[]{Team.RED,Team.BLUE,Team.GREEN,Team.ORANGE,Team.YELLOW,Team.WHITE,Team.PURPLE,Team.GRAY}),
_8x2(50,2,8,16,new Team[]{Team.RED,Team.BLUE,Team.GREEN,Team.ORANGE,Team.YELLOW,Team.WHITE,Team.PURPLE,Team.GRAY}),
_12x1(50,1,6,12,new Team[]{Team.RED,Team.BLUE,Team.GREEN,Team.ORANGE,Team.YELLOW,Team.PINK,Team.PURPLE,Team.GRAY,Team.WHITE,Team.BLACK,Team.AQUA,Team.CYAN}),
_12x2(50,2,12,24,new Team[]{Team.RED,Team.BLUE,Team.GREEN,Team.ORANGE,Team.YELLOW,Team.PINK,Team.PURPLE,Team.GRAY,Team.WHITE,Team.BLACK,Team.AQUA,Team.CYAN});

@Getter
private Team[] team;
@Getter
private int min;
@Getter
private int max;
@Getter
private int team_size;
@Getter
private int h;

private SkyWarsType(int h,int team_size,int min,int max,Team[] team){
	this.max=max;
	this.min=min;
	this.team=team;
	this.team_size=team_size;
	this.h=h;
}

public static SkyWarsType getSkyWarsTypeWithSize(int size){
	for(SkyWarsType type : SkyWarsType.values()){
		if(type.getTeam().length==size){
			return type;
		}
	}
	return SkyWarsType._8x1;
}

public static SkyWarsType random(){
	int a = 0;
	HashMap<Integer,SkyWarsType> list = new HashMap<>();
	for(SkyWarsType t : SkyWarsType.values()){
		if(t==_32x4)continue;
		for(int i = 0; i < t.getH()+1; i++){
			list.put(a,t);
			a++;
		}
	}
	
	System.out.println("[EpicPvP] SkyWarsType RANDOM "+list.size());
	return list.get(UtilMath.RandomInt(list.size(), 0));
}
	
}
