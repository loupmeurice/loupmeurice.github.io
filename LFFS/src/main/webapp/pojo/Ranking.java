package main.webapp.pojo;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Ranking  implements Serializable {
	private Serie serie;
	
	private Map<Integer, Team> teams = new TreeMap<Integer, Team>();

	public Ranking(Serie s) {
		this.serie = s;
	}
	
	public void addTeam(Team t) {
		teams.put(t.getRank().getPosition(), t);
	}
	
	public List<Team> getTeams() {
		return new ArrayList<Team>(teams.values());
	}

	public Serie getSerie() {
		return serie;
	}

	public void setSerie(Serie serie) {
		this.serie = serie;
	}



}
