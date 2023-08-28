package main.webapp.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Game implements Serializable {
	//Match à venir
	public static final int TO_PLAY = 1;
	//Match ayant été joué
	public static final int PLAYED = 2;
	//Match arrêté (ex: bagarre)
	public static final int STOPPED = 3;
	//Match remis
	public static final int DELAYED = 6;
	
	//Le match n'a finalement pas été joué
	public static final int NOT_PLAYED = 7;
	public static final int UNKOWN_STATUS = 8;

	private int icalSequence = 0;

	private String reference;
	private Integer id;
	private Integer week;
	private Integer serie_id;
	private Serie serie;

	private Team home_team;
	private Team away_team;
	private Integer home_score;
	private Integer away_score;
	private Integer home_penalty;
	private Integer away_penalty;
	private boolean homeForfait = false;
	private boolean awayForfait = false;
	private boolean bye = false;
	private Integer status;

	private String date;
	private String time;
	private Integer venue_id;
	private Venue venue;
	private List<Referee> refs = new ArrayList<Referee>();
	private String googleMapsAddressForNationalLeague;

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getWeek() {
		return week;
	}

	public void setWeek(Integer week) {
		this.week = week;
	}

	public Integer getSerie_id() {
		return serie_id;
	}

	public void setSerie_id(Integer serie_id) {
		this.serie_id = serie_id;
	}

	public Team getHome_team() {
		return home_team;
	}

	public void setHome_team(Team home_team) {
		this.home_team = home_team;
	}

	public Team getAway_team() {
		return away_team;
	}

	public void setAway_team(Team away_team) {
		this.away_team = away_team;
	}

	public Integer getHome_score() {
		return home_score;
	}

	public void setHome_score(Integer home_score) {
		this.home_score = home_score;
	}

	public Integer getAway_score() {
		return away_score;
	}

	public void setAway_score(Integer away_score) {
		this.away_score = away_score;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public Integer getVenue_id() {
		return venue_id;
	}

	public void setVenue_id(Integer venue_id) {
		this.venue_id = venue_id;
	}

	public Venue getVenue() {
		return venue;
	}

	public void setVenue(Venue venue) {
		this.venue = venue;
	}

	public List<Referee> getRefs() {
		return refs;
	}

	public void setRefs(List<Referee> refs) {
		this.refs = refs;
	}

	public Serie getSerie() {
		return serie;
	}

	public void setSerie(Serie serie) {
		this.serie = serie;
	}

	public boolean isAwayForfait() {
		return awayForfait;
	}

	public void setAwayForfait(boolean awayForfait) {
		this.awayForfait = awayForfait;
	}

	public boolean isHomeForfait() {
		return homeForfait;
	}

	public void setHomeForfait(boolean homeForfait) {
		this.homeForfait = homeForfait;
	}

	public boolean isBye() {
		return bye;
	}

	public void setBye(boolean bye) {
		this.bye = bye;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getICalendarUI() {
		return "LFFS_" + getId();
	}

	public Integer getHome_penalty() {
		return home_penalty;
	}

	public void setHome_penalty(Integer home_penalty) {
		this.home_penalty = home_penalty;
	}

	public Integer getAway_penalty() {
		return away_penalty;
	}

	public void setAway_penalty(Integer away_penalty) {
		this.away_penalty = away_penalty;
	}

	public boolean hasPenalty() {
		return this.away_penalty != null && this.home_penalty != null;
	}

	public String getPrintableScore() {
		if (status == null)
			return "? - ?";

		switch (status) {
		case TO_PLAY:
			return "(À VENIR)";
		case PLAYED:
			if (awayForfait && homeForfait)
				return "FF 0 - 0 FF";
			if (awayForfait)
				return "5 - 0 FF";
			if (homeForfait)
				return "FF 0 - 5";

			return home_score + " - " + away_score
					+ (hasPenalty() ? " (TB:" + home_penalty + " - " + away_penalty + ")" : "");
		case STOPPED:
			return "(ARRÊTÉ)";
		case DELAYED:
			return "(REPORTÉ)";
		case NOT_PLAYED:
			return "(NON JOUÉ)";
		default:
			return "? - ?";

		}

	}

	public int getIcalSequence() {
		return icalSequence;
	}

	public void setIcalSequence(int icalSequence) {
		this.icalSequence = icalSequence;
	}

	public String getGoogleMapsAddressForNationalLeague() {
		return googleMapsAddressForNationalLeague;
	}

	public void setGoogleMapsAddressForNationalLeague(String googleMapsAddressForNationalLeague) {
		this.googleMapsAddressForNationalLeague = googleMapsAddressForNationalLeague;
	}

}
