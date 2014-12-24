package me.kingingo.karcade.Game.Games.SheepWars;

import lombok.Getter;
import me.kingingo.karcade.Enum.Team;
import me.kingingo.kcore.Enum.GameType;

public enum SheepWarsType {
_2("_2",GameType.SheepWars8,4,8,new Team[]{Team.RED,Team.BLUE}),
_4("_4",GameType.SheepWars16,8,16,new Team[]{Team.RED,Team.BLUE,Team.YELLOW,Team.GREEN}),
_8("_8",GameType.SheepWars16,4,8,new Team[]{Team.RED,Team.BLUE,Team.YELLOW,Team.GREEN,Team.ORANGE,Team.PURPLE,Team.GRAY,Team.WHITE});

@Getter
Team[] team;
@Getter
int min;
@Getter
int max;
@Getter
String name;
@Getter
GameType type;
private SheepWarsType(String name,GameType type,int min,int max,Team[] team){
	this.name=name;
	this.max=max;
	this.min=min;
	this.team=team;
	this.type=type;
}
	
}
