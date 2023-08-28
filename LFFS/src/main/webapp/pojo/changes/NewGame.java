package main.webapp.pojo.changes;

import main.webapp.pojo.Game;
import main.webapp.pojo.Room;

public class NewGame extends GameChange {
	private boolean delayed;

	public NewGame(Game prev, Game game) {
		super(prev, game);
		this.delayed = (prev != null);
		game.setIcalSequence(game.getIcalSequence() + 1);
	}
	
	
	@Override
	public String getVEvent() {
		String res = "BEGIN:VEVENT\n";
		String uid = game.getICalendarUI();
		res += "UID:" + uid + "\n";
		res += "DTSTART:" + fomatICSDate(game.getDate(), game.getTime()) + "\n";
		res += "DURATION:PT1H\n";
		res += "SEQUENCE:" + game.getIcalSequence() + "\n";
		res += "SUMMARY:" + game.getSerie().getSerie_name() + ": " + game.getHome_team().getName() + " - "
				+ game.getAway_team().getName() + "\n";
		Room r = game.getVenue() == null ? null : game.getVenue().getRoom();
		res += "LOCATION:" + (r == null ? (game.getGoogleMapsAddressForNationalLeague() == null ? "Adresse inconnue\n" : game.getGoogleMapsAddressForNationalLeague() + "\n")
				: r.getStreet() + "\\, " + (r.getStreet2() != null ? (r.getStreet2() + "\\, ") : "") + "\\n"
						+ r.getZip() + " " + r.getCity() + "\\,\\n" + r.getCountry() + "\n");
		res += "DESCRIPTION: Match de " + game.getSerie().getSerie_name() + ": " + game.getHome_team().getName() + " - "
				+ game.getAway_team().getName() + "\n";
		res += "END:VEVENT\n";

		return res;
	}

	@Override
	public String getMessage() {
		if(delayed) {
			return "Match reprogrammé en " + game.getSerie().getSerie_name() + "\n" + game.getDate() + " "
					+ game.getTime() + ": " + game.getHome_team().getName() + " - " + game.getAway_team().getName()
					+ "\n\nOuvrez la pièce jointe (.ics) pour déplacer ce match à la nouvelle date dans votre agenda.";
		}
		
		return "Nouveau match programmé en " + game.getSerie().getSerie_name() + "\n" + game.getDate() + " "
				+ game.getTime() + ": " + game.getHome_team().getName() + " - " + game.getAway_team().getName()
				+ "\n\nOuvrez la pièce jointe (.ics) pour ajouter le match à votre agenda.";
	}

	@Override
	public String getSubject() {
		return (delayed ?"[Match reprogrammé] " : "[Nouveau match programmé] ") + game.getHome_team().getName() + " - " + game.getAway_team().getName()
				+ " (" + game.getDate() + " à " + game.getTime() + ")";
	}

}
