package main.webapp.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Province implements Serializable {
	private int startYear;
	private int endYear;
	private String province;

	private Map<Integer, Club> clubs = new HashMap<Integer, Club>();
	private Map<Integer, Referee> referees = new HashMap<Integer, Referee>();
	private Map<Integer, Serie> series = new HashMap<Integer, Serie>();
	private Map<Integer, Team> teams = new HashMap<Integer, Team>();
	private Map<Integer, Venue> venues = new HashMap<Integer, Venue>();

	public Province(int startYear, int endYear, String province) {
		this.startYear = startYear;
		this.endYear = endYear;
		this.province = province;
	}

	public List<Team> getTeams() {
		return new ArrayList<Team>(teams.values());
	}

	public void addClub(Club c) {
		clubs.put(c.getId(), c);
	}

	public Club getClub(Integer club_id) {
		return clubs.get(club_id);
	}

	public void addReferee(Referee ref) {
		referees.put(ref.getId(), ref);

	}

	public Map<Integer, Referee> getReferees() {
		return referees;
	}

	public void setReferees(Map<Integer, Referee> referees) {
		this.referees = referees;
	}

	public Referee getReferee(Integer ref_id) {
		return referees.get(ref_id);
	}

	public List<Serie> getSeries() {
		return new ArrayList<Serie>(series.values());
	}

	public int getStartYear() {
		return startYear;
	}

	public void setStartYear(int startYear) {
		this.startYear = startYear;
	}

	public int getEndYear() {
		return endYear;
	}

	public void setEndYear(int endYear) {
		this.endYear = endYear;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public Team getTeam(Integer team_id) {
		return teams.get(team_id);
	}

	public void addTeam(Team team) {
		teams.put(team.getId(), team);
	}

	public Venue getVenue(Integer venue_id) {
		return venues.get(venue_id);
	}

	public void addVenue(Venue v) {
		venues.put(v.getId(), v);

	}

	public List<Venue> getVenues() {
		return new ArrayList<Venue>(venues.values());
	}

	public void addSerie(Serie serie) {
		series.put(serie.getId(), serie);
		serie.setProvince(this);

	}

	public Serie getSerie(Integer serie_id) {
		return series.get(serie_id);
	}

	public String getCodeName() {
		return province.replaceAll("\\s+","");
	}

}
