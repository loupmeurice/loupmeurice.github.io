package main.webapp.pojo.changes;

import main.webapp.pojo.Game;
import main.webapp.pojo.Room;

public class NonExistingGame extends GameChange {

	//le match a disparu des archives (ex: forfait général)
	public NonExistingGame(Game game) {
		super(null, game);
		game.setIcalSequence(game.getIcalSequence() + 1);
	}
	
	@Override
	public String getVEvent() {
		String res = "BEGIN:VEVENT\n";
		String uid = game.getICalendarUI();
		res += "UID:" + uid + "\n";
		res += "METHOD:CANCEL\n";
		res += "STATUS:CANCELLED\n";
		res += "DTSTART:" + fomatICSDate(game.getDate(), game.getTime()) + "\n";
		res += "DURATION:PT1H\n";
		res += "SEQUENCE:" + game.getIcalSequence() + "\n";
		res += "SUMMARY:" + game.getSerie().getSerie_name() + ": " + game.getHome_team().getName() + " - "
				+ game.getAway_team().getName() + "\n";
		Room r = game.getVenue() == null ? null : game.getVenue().getRoom();
		res += "LOCATION:" + (r == null ? "Adresse inconnue"
				: r.getStreet() + "\\, " + (r.getStreet2() != null ? (r.getStreet2() + "\\, ") : "") + "\\n"
						+ r.getZip() + " " + r.getCity() + "\\,\\n" + r.getCountry() + "\n");
		res += "DESCRIPTION: Match de " + game.getSerie().getSerie_name() + ": " + game.getHome_team().getName() + " - "
				+ game.getAway_team().getName() + "\n";
		res += "END:VEVENT\n";

		return res;
	}

	@Override
	public String getMessage() {
		return "Le match de " + game.getSerie().getSerie_name() + " entre " + game.getHome_team().getName() + " et "
				+ game.getAway_team().getName() + " prévu le " + game.getDate() + " à " + game.getTime()
				+ " semble avoir été définitivement annulé. Cela pourrait être dû au forfait général d'une des deux équipes.\n\nOuvrez la pièce jointe (.ics) pour supprimer ce match de votre agenda.";
	}

	@Override
	public String getSubject() {
		return "[Match annulé] " + game.getHome_team().getName() + " - " + game.getAway_team().getName() + " ("
				+ game.getDate() + " à " + game.getTime() + ")";
	}

}
