package eu.epicpvp.karcade.Game.Single.Games.Masterbuilders;

import eu.epicpvp.kcore.Enum.Team;
import eu.epicpvp.kcore.Util.UtilMath;
import lombok.Getter;

public enum MasterbuildersType {
_12x1(3,12,new Team[]{Team.RED,Team.BLUE,Team.GREEN,Team.ORANGE,Team.YELLOW,Team.PINK,Team.PURPLE,Team.GRAY,Team.WHITE,Team.BLACK,Team.AQUA,Team.CYAN});
//_6x1(3,6,new Team[]{Team.RED,Team.BLUE,Team.GREEN,Team.ORANGE,Team.YELLOW,Team.GRAY});

@Getter
private Team[] team;
@Getter
private int min;
@Getter
private int max;
private MasterbuildersType(int min,int max,Team[] team){
	this.max=max;
	this.min=min;
	this.team=team;
}

public static MasterbuildersType getSkyWarsTypeWithSize(int size){
	for(MasterbuildersType type : values()){
		if(type.getTeam().length==size){
			return type;
		}
	}
	return MasterbuildersType._12x1;
}

public static MasterbuildersType random(){
	return values()[UtilMath.r(values().length)];
}
	
}
