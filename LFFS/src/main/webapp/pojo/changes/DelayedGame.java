package main.webapp.pojo.changes;

import main.webapp.pojo.Game;

public class DelayedGame extends GameChange {
	
	//game.status = 6
	public DelayedGame(Game prev, Game game) {
		super(prev, game);
	}

	@Override
	public String getMessage() {
		return "Le match de " + game.getSerie().getSerie_name() + " entre " + game.getHome_team().getName() + " et "
				+ game.getAway_team().getName()
				+ " est remis.\nLa nouvelle date sera probablement communiquée plus tard par la ligue.\n\nOuvrez la pièce jointe (.ics) pour supprimer ce match de votre agenda.";
	}

	@Override
	public String getSubject() {
		return "[Match remis] " + game.getHome_team().getName() + " - " + game.getAway_team().getName();
	}

}
