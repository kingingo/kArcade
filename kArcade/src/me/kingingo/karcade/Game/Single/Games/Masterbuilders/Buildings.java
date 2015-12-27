package me.kingingo.karcade.Game.Single.Games.Masterbuilders;

import lombok.Getter;

public enum Buildings {
TANK("Panzer","Tank"),
HOUSE("Haus","House");

@Getter
private String german;
@Getter
private String english;

private Buildings(String german,String english){
	this.german=german;
	this.english=english;
}
}
