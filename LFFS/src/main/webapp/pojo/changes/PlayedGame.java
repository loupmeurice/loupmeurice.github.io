package main.webapp.pojo.changes;

import main.webapp.pojo.Game;

public class PlayedGame extends GameChange {

	public PlayedGame(Game prev, Game game) {
		super(prev, game);
	}

	@Override
	public String getMessage() {
		String res = "Nouveau résultat en " + game.getSerie().getSerie_name() + ":\n\n";
		res += game.getHome_team().getName() + " - " + game.getAway_team().getName() + "  " + game.getPrintableScore()
				+ " (" + game.getDate() + " à " + game.getTime() + ")";

		return res;
	}

	@Override
	public String getSubject() {
		String res = "[Nouveau résultat] " + game.getSerie().getSerie_name() + ": ";
		res += game.getPrintableScore() + " (" + game.getDate() + " à " + game.getTime() + ")";
		return res;
	}

}
