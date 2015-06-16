package me.kingingo.karcade.Game.Single.Games.SkyWars;

import java.util.HashMap;

import lombok.Getter;
import me.kingingo.kcore.Enum.Team;
import me.kingingo.kcore.Util.UtilMath;

public enum SkyWarsType {
_12x1(50,1,6,12,new Team[]{Team.RED,Team.BLUE,Team.GREEN,Team.ORANGE,Team.YELLOW,Team.PINK,Team.PURPLE,Team.GRAY,Team.WHITE,Team.BLACK,Team.AQUA,Team.CYAN}),
_12x2(50,2,12,24,new Team[]{Team.RED,Team.BLUE,Team.GREEN,Team.ORANGE,Team.YELLOW,Team.PINK,Team.PURPLE,Team.GRAY,Team.WHITE,Team.BLACK,Team.AQUA,Team.CYAN});

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

private SkyWarsType(int h,int team_size,int min,int max,Team[] team){
	this.max=max;
	this.min=min;
	this.drop_rate=drop_rate;
	this.team=team;
	this.team_size=team_size;
	this.h=h;
}


public static SkyWarsType random(){
	int a = 0;
	HashMap<Integer,SkyWarsType> list = new HashMap<>();
	for(SkyWarsType t : SkyWarsType.values()){
		for(int i = 0; i < t.getH()+1; i++){
			list.put(a,t);
			a++;
		}
	}
	
	System.out.println("[EpicPvP] SkyWarsType RANDOM "+list.size());
	return list.get(UtilMath.RandomInt(list.size(), 0));
}
	
}
