package me.kingingo.karcade.Game.Games.CaveWars;

import lombok.Getter;
import me.kingingo.karcade.Enum.Team;

public enum CaveWarsType {
_2x4(4,4,8,new Team[]{Team.RED,Team.BLUE}),
_2x8(8,8,16,new Team[]{Team.RED,Team.BLUE}),
_4x4(4,8,16,new Team[]{Team.RED,Team.BLUE,Team.YELLOW,Team.GREEN}),
_4x5(5,8,20,new Team[]{Team.RED,Team.BLUE,Team.YELLOW,Team.GREEN}),
_4x3(3,4,8,new Team[]{Team.RED,Team.BLUE,Team.YELLOW,Team.GREEN}),
_4x2(2,4,8,new Team[]{Team.RED,Team.BLUE,Team.YELLOW,Team.GREEN});

@Getter
Team[] team;
@Getter
int min;
@Getter
int max;
@Getter
int team_size;
private CaveWarsType(int team_size,int min,int max,Team[] team){
	this.max=max;
	this.min=min;
	this.team=team;
	this.team_size=team_size;
}
	
}
