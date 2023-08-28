package main.webapp.pojo;


import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Team implements Serializable{
	
	private Integer id;
	private String name;
	private String short_name;
	private Club club;
	private Map<Integer, List<Game>> games = new HashMap<Integer, List<Game>>();
	
	private ChampionshipRank rank;
	
	
	
	public void addGame(Game g) {
		List<Game> list = games.get(g.getSerie().getId());
		if(list == null) {
			list = new ArrayList<Game>();
			games.put(g.getSerie().getId(), list);
		}
		list.add(g);
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getShort_name() {
		return short_name;
	}
	public void setShort_name(String short_name) {
		this.short_name = short_name;
	}
	public Club getClub() {
		return club;
	}
	public void setClub(Club club) {
		this.club = club;
	}

	public Map<Integer, List<Game>> getGames() {
		return games;
	}

	public void setGames(Map<Integer, List<Game>> games) {
		this.games = games;
	}

	public ChampionshipRank getRank() {
		return rank;
	}

	public void setRank(ChampionshipRank rank) {
		this.rank = rank;
	}
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3307/OpenMRS2", "root", "root");
		conn.createStatement().executeQuery("select url from URL;");
	}

}
