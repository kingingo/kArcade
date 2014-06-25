package me.kingingo.karcade.Enum;

import me.kingingo.kcore.Util.C;

public enum Text {
FIGHT_START_IN(C.cGray+"Ihr könnt in % sekunden Kämpfen!"),
FIGHT_START(C.cGray+"Ihr könnt NUN Kämpfen!"),
GAME_END_IN(C.cGray+"Das Spiel endet in % sekunden."),
GAME_END(C.cGray+"Das Spiel wurde beendet."),
RESTART_IN(C.cGray+"Der Server Restartet in % sekunden."),
RESTART(C.cGray+"Der Server Restartet Jetzt."),
SERVER_FULL(C.cRed+"Der Server ist voll!"),
SERVER_NOT_LOBBYPHASE(C.cRed+"Der Server ist momentan nicht in der LobbyPhase!"),
SERVER_FULL_WITH_PREMIUM(C.cRed+"Der Server ist voll mit Premium Spieler!"),
KILL_BY("% wurde von % gekillt"),
GAME_EXCLUSION(C.cRed+"% wurde vom Spiel ausgeschlossen."),
VOTE_TEAM_ADD("Du bist nun in Team %"),
VOTE_TEAM_REMOVE("Du hast das Team % verlassen."),
VOTE_TEAM_FULL("Das Team % ist momentan Voll."),
VOTE_MIN("Es muessen mindestens % Spieler online sein."),
KICKED_BY_PREMIUM(C.cRed+"Du wurdest aus der Lobby gekickt weil ein Admin/Premium User den Vollen Server betretten hat."),
PREFIX(C.cDAqua+"[EpicPvP]: "+C.cGray),
TEAM_OUT("Das Team % ist ausgeschieden."),
RESTART_FROM_ADMIN("Server wurde von einen Admin §6§lRestartet§c!"),
GAME_START(C.cGray+"LOS!"),
GAME_START_IN(C.cGray+"Das Spiel startet in % sekunden."),
DEATHMATCH_START(C.cGray+"Das Deathmatch startet."),
DEATHMATCH_START_IN(C.cGray+"Das Deathmatch startet in % sekunden."),
DEATHMATCH_END(C.cGray+"Das Deathmatch ist zu ende."),
GAME_WIN(C.cAqua+"Der Spieler % hat das Spiel gewonnen!"),
SURVIVAL_GAMES_DISTRICT_WIN("Das District % mit den Spielern % und % hat Gewonnen."),
DEATHMATCH_END_IN(C.cGray+"Das Deathmatch endet in % sekunden.");

private String t;
private Text(String t){
	this.t=t;
}

public String getText(){
	return this.t;
}

public String getText(int s){
	if(this.t.contains("%"))return t.replaceAll("%", String.valueOf(s));
	return this.t;
}

public String getText(String s){
	if(this.t.contains("%"))return t.replaceAll("%", s);
	return this.t;
}

public String getText(String[] s){
	String tt=this.t;
	for(int i = 0 ; i < s.length ; i++){
		tt=tt.replaceFirst("%", s[i]);
	}
	return tt;
}

	
}
