package main.webapp.pojo.changes;

import main.webapp.pojo.Game;

public class NonPlayedGame extends GameChange {

	//game.status = 7
	public NonPlayedGame(Game prev, Game game) {
		super(prev, game);
	}

	@Override
	public String getMessage() {
		return "Le match de " + game.getSerie().getSerie_name() + "  entre " + game.getHome_team().getName() + " et "
				+ game.getAway_team().getName() + " prévu le " + game.getDate() + " à " + game.getTime()
				+ " ne s'est finalement pas joué.\nLa ligue communiquera probablement plus de détails plus tard.";
	}

	@Override
	public String getSubject() {
		return "[Match non joué] " + game.getHome_team().getName() + " - " + game.getAway_team().getName() + " ("
				+ game.getDate() + " à " + game.getTime() + ")";
	}

}
