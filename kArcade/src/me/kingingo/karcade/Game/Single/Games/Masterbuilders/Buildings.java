package me.kingingo.karcade.Game.Single.Games.Masterbuilders;

import lombok.Getter;

public enum Buildings {
	HOUSE("Haus","House"),
	CAKE("Kuchen","Cake"),
	CELL_PHONE("Handy","Cell Phone"),
	CASTLE("Burg","Castle"),
	SWORD("Schwert","Sword"),
	ROSE("Rose","Rose"),
	Dragon("Drachen","Dragon"),
	CHEESE("Kaese","Cheese"),
	BOOK("Buch","Book"),
	WATCH("Uhr","Watch"),
	STAR("Stern","Star"),
	BED("Bett","Bed"),
	BOAT("Boot","Boat"),
	COMPUTER_MONITOR("Computer Monitor","Computer Monitor"),
	COMPUTER_KEYBOARD("Computer Tastatur","Computer Keyboard"),
	PISTOL("Pistole","Pistol"),
	FANTASY_TREE("Fantasie Baum","Fantasy Tree"),
	COMPUTER_MOUSE("Computer Maus","Computer Mouse"),
	AEROPLANE("Flugzeug","Aeroplane"),
	PIG("Schwein","Pig"),
	BRIDGE("Bruecke","Bridge"),
	CAR("Auto","Car"),
	TRUCK("Lastwagen","Truck"),
	LIVING_ROOM("Wohnzimmer","Living Room"),
	SNOWMAN("Schneemann","Snowman"),
	CHOCOLATE_BAR("Schokoladentafel","Chocolate Bar"),
	TRAIN("Zug","Train"),
	ORNAMENT("Ornament","Ornament"),
	BURGER("Burger","Burger"),
	FARM("Bauernhof","Farm"),
	SHIPWRECK("Shiffswrack","Shipwreck"),
	STATUE("Statue","Statue"),
	TANK("Panzer","Tank"),
	SPACESHIP("Raumschiff","Spaceship"),
	MUSIC_INSTRUMENT("Musik Instrument","Music Instrument"),
	TREEHOUSE("Baumhaus","Treehouse"),
	POT_OF_GOLD("Topf mit Gold","Pot of Gold"),
	FRUIT("Frucht","Fruit"),
	SAILING_SHIP("Segelschiff","Sailing Ship"),
	ROLLER_COASTER("Achterbahn","Roller-Coaster"),
	PLATE("Teller","Plate"),
	ANGEL("Engel","Angel"),
	TOASTER("Toaster","Toaster"),
	NINJA("Ninja","Ninja"),
	BUS("Bus","Bus"),
	BEACH("Strand","Beach"),
	ALIEN("Alien","Alien"),
	COMPUTER("Computer","Computer"),
	MICROPHONE("Mikrofon","Microphone"),
	PIZZA("Pizza","Pizza");

@Getter
private String german;
@Getter
private String english;

private Buildings(String german,String english){
	this.german=german;
	this.english=english;
}
}
