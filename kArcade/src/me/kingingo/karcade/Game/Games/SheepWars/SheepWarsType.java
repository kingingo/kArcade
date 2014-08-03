package me.kingingo.karcade.Game.Games.SheepWars;

import lombok.Getter;
import me.kingingo.karcade.Enum.Team;

public enum SheepWarsType {
_2("_2",2,8,new Team[]{Team.RED,Team.BLUE}),
_3("_3",6,12,new Team[]{Team.RED,Team.BLUE,Team.GREEN}),
_4("_4",8,16,new Team[]{Team.RED,Team.BLUE,Team.YELLOW,Team.GREEN}),
_6("_6",12,24,new Team[]{Team.RED,Team.BLUE,Team.YELLOW,Team.GREEN,Team.GRAY,Team.WHITE}),
_8("_8",16,32,new Team[]{Team.RED,Team.BLUE,Team.YELLOW,Team.GREEN,Team.GRAY,Team.WHITE,Team.ORANGE,Team.PURPLE});

@Getter
Team[] team;
@Getter
int min;
@Getter
int max;
@Getter
String name;
private SheepWarsType(String name,int min,int max,Team[] team){
	this.name=name;
	this.max=max;
	this.min=min;
	this.team=team;
}
	
}
