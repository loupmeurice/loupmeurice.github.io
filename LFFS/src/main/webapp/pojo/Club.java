package main.webapp.pojo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Club implements Serializable {

	private Integer id;
	private String name;

	private Map<Integer, Team> teams = new HashMap<Integer, Team>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void addTeam(Team team) {
		teams.put(team.getId(), team);
	}

}
