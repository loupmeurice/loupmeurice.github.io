package main.webapp.pojo;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Serie implements Serializable {
	
	private Integer id;
	private String serie_reference;
	private String serie_name;
	private String serie_short;
	private Province province;
	
	
	private Map<Integer, Team> teams = new HashMap<Integer, Team>();
	private Map<Integer, Game> games = new HashMap<Integer, Game>();
	
	public void deleteGame(int game_id) {
		games.remove(game_id);
	}
	
	public Serie(Integer id) {
		this.id = id;
	}
	
	public Integer getId() {
		return id;
	}
	
	public boolean equals(Object o) {
		if(o instanceof Serie) {
			Serie s2 = (Serie) o;
			return id.equals(s2.id);
		}
		
		return false;
	}
	
	public int hashCode() {
		return id.hashCode();
	}
	
	
	public void setId(Integer id) {
		this.id = id;
	}
	public String getSerie_reference() {
		return serie_reference;
	}
	public void setSerie_reference(String serie_reference) {
		this.serie_reference = serie_reference;
	}
	public String getSerie_name() {
		return serie_name;
	}
	public void setSerie_name(String serie_name) {
		this.serie_name = serie_name;
	}
	public String getSerie_short() {
		return serie_short;
	}
	public void setSerie_short(String serie_short) {
		this.serie_short = serie_short;
	}
	public void addGame(Game g) {
		games.put(g.getId(), g);
		
	}
	public void addTeam(Team team) {
		teams.put(team.getId(), team);
	}
	public List<Team> getTeams() {
		return new ArrayList<Team>(teams.values());
	}
	public List<Game> getGames() {
		return new ArrayList<Game>(games.values());
	}

	public Team getTeam(Integer team_id) {
		return teams.get(team_id);
	}

	public Province getProvince() {
		return province;
	}

	public void setProvince(Province province) {
		this.province = province;
	}

}
