package main.webapp.pojo.changes;

import main.webapp.pojo.Game;

public class ForfaitGame extends GameChange {

	//game.status = 2 AND game.hasForfait()
	public ForfaitGame(Game prev, Game game) {
		super(prev, game);
	}

	@Override
	public String getMessage() {
		return "Le match de " + game.getSerie().getSerie_name() + " entre " + game.getHome_team().getName() + " et "
				+ game.getAway_team().getName() + " prévu le " + game.getDate() + " à " + game.getTime()
				+ " a finalement découlé sur un forfait: " + game.getPrintableScore();
	}

	@Override
	public String getSubject() {
		String res = "[Forfait] " + game.getSerie().getSerie_name() + ": ";
		res += game.getPrintableScore() + " (" + game.getDate() + " à " + game.getTime() + ")";
		return res;
	}

}
