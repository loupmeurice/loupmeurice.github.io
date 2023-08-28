package main.webapp.pojo;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Referee  implements Serializable {
	private Integer id;
	private String firstName;
	private String lastName;
	
	private List<Game> games = new ArrayList<Game>();
	
	public void addGame(Game g) {
		games.add(g);
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public List<Game> getGames() {
		return games;
	}

}
