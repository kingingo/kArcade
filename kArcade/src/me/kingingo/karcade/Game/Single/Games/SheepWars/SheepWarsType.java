package me.kingingo.karcade.Game.Single.Games.SheepWars;

import lombok.Getter;
import me.kingingo.kcore.Enum.Team;

public enum SheepWarsType {
//_8x1(1,4,8,new Team[]{Team.RED,Team.BLUE,Team.YELLOW,Team.GREEN,Team.GRAY,Team.PINK,Team.ORANGE,Team.PURPLE},1);
_2x4(4,4,8,new Team[]{Team.RED,Team.BLUE},1),
_2x8(8,8,16,new Team[]{Team.RED,Team.BLUE},3),
_2x5(5,4,10,new Team[]{Team.RED,Team.BLUE},1),
_4x4(4,8,16,new Team[]{Team.RED,Team.BLUE,Team.YELLOW,Team.GREEN},1),
_4x5(5,8,20,new Team[]{Team.RED,Team.BLUE,Team.YELLOW,Team.GREEN},1),
_4x3(3,6,12,new Team[]{Team.RED,Team.BLUE,Team.YELLOW,Team.GREEN},1),
_4x2(2,4,8,new Team[]{Team.RED,Team.BLUE,Team.YELLOW,Team.GREEN},1);

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

private SheepWarsType(int team_size,int min,int max,Team[] team, int drop_rate){
	this.max=max;
	this.min=min;
	this.drop_rate=drop_rate;
	this.team=team;
	this.team_size=team_size;
}
	
public static SheepWarsType getSheepWarsTypeWithSize(int size){
	for(SheepWarsType type : SheepWarsType.values()){
		if(type.getTeam().length==size){
			return type;
		}
	}
	return SheepWarsType._2x4;
}

}
