package me.kingingo.karcade.Game.Single.Games.BedWars;

import java.util.HashMap;

import lombok.Getter;
import me.kingingo.kcore.Enum.Team;
import me.kingingo.kcore.Util.UtilMath;

public enum BedWarsType {
_8x1(50,1,4,8,new Team[]{Team.RED,Team.BLUE,Team.YELLOW,Team.GREEN,Team.GRAY,Team.PINK,Team.ORANGE,Team.PURPLE},1),
_2x4(10,4,4,8,new Team[]{Team.RED,Team.BLUE},1),
_2x8(10,8,8,16,new Team[]{Team.RED,Team.BLUE},3),
_2x5(10,5,4,10,new Team[]{Team.RED,Team.BLUE},1),
_4x4(10,4,8,16,new Team[]{Team.RED,Team.BLUE,Team.YELLOW,Team.GREEN},1),
_4x5(10,5,8,20,new Team[]{Team.RED,Team.BLUE,Team.YELLOW,Team.GREEN},1),
_4x3(10,3,6,12,new Team[]{Team.RED,Team.BLUE,Team.YELLOW,Team.GREEN},1),
_4x2(10,2,4,8,new Team[]{Team.RED,Team.BLUE,Team.YELLOW,Team.GREEN},1);

@Getter
Team[] team;
@Getter
int min;
@Getter
int max;
@Getter
int team_size;
@Getter
int drop_rate;
@Getter
int h;

private BedWarsType(int h,int team_size,int min,int max,Team[] team, int drop_rate){
	this.max=max;
	this.min=min;
	this.drop_rate=drop_rate;
	this.team=team;
	this.team_size=team_size;
	this.h=h;
}


public static BedWarsType random(){
	int a = 0;
	HashMap<Integer,BedWarsType> list = new HashMap<>();
	for(BedWarsType t : BedWarsType.values()){
		for(int i = 0; i < t.getH()+1; i++){
			list.put(a,t);
			a++;
		}
	}
	
	System.out.println("[EpicPvP] BedWarsType RANDOM "+list.size());
	return list.get(UtilMath.RandomInt(list.size(), 0));
}
	
}
