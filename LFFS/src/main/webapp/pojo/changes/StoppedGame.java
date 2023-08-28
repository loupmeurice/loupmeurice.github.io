package main.webapp.pojo.changes;

import main.webapp.pojo.Game;

public class StoppedGame extends GameChange {

	//game.status = 3
	public StoppedGame(Game prev, Game game) {
		super(prev, game);
	}

	@Override
	public String getMessage() {
		return "Le match de " + game.getSerie().getSerie_name() + " entre " + game.getHome_team().getName() + " et "
				+ game.getAway_team().getName() + " prévu le " + game.getDate() + " à " + game.getTime()
				+ " a du être arrêté.\nLa ligue communiquera probablement plus de détails plus tard.";
	}

	@Override
	public String getSubject() {
		return "[Match arrêté] " + game.getHome_team().getName() + " - " + game.getAway_team().getName() + " ("
				+ game.getDate() + " à " + game.getTime() + ")";
	}

}
