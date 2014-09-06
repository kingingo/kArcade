package me.kingingo.karcade.Enum;

import lombok.Getter;

public enum GameStateChangeReason {
GAME_END("Game Endet"),
LAST_PLAYER("Es ist nur noch ein Spieler Online!"),
CUSTOM("Plugin"),
LAST_TEAM("Es ist nun noch ein Team da!");

@Getter
String name="";
private GameStateChangeReason(String name){
	this.name=name;
}

}
