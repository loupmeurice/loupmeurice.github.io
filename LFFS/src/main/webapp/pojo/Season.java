package main.webapp.pojo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class Season implements Serializable {
	private int endYear;

	public Season(int endYear) {
		this.endYear = endYear;
	}

	private Map<String, Province> provinces = new TreeMap<String, Province>();

	public void addProvince(Province prov) {
		provinces.put(prov.getProvince(), prov);
	}

	public List<Province> getProvinces() {
		return new ArrayList<Province>(provinces.values());
	}

	public int getEndYear() {
		return endYear;
	}

	public void setEndYear(int endYear) {
		this.endYear = endYear;
	}

	public static Season deserialize(String s) {

		try {
			byte[] data = Base64.getDecoder().decode(s);
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
			Season o = (Season) ois.readObject();
			ois.close();
			return o;
		} catch (Exception | Error e) {
			e.printStackTrace();
			return null;
		}
	}

	/** Write the object to a Base64 string. */
	public String serialize() {
		System.out.println("Season serialization...");
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(this);
			oos.close();
			String res = Base64.getEncoder().encodeToString(baos.toByteArray());
			System.out.println("Season serialized");
			return res;
		} catch (Exception | Error e) {
			e.printStackTrace();
			return null;
		}
	}

	public Team getTeam(int team_id) {
		for (Province p : provinces.values()) {
			Team t = p.getTeam(team_id);
			if (t != null)
				return t;
		}

		return null;

	}

	public List<Team> getTeams() {
		TreeMap<String, Team> res = new TreeMap<String, Team>();
		List<Team> teams = new ArrayList<Team>();
		for (Province p : provinces.values())
			for(Team t : p.getTeams())
				res.put(t.getName(), t);
		return new ArrayList<Team>(res.values());

	}

	public Map<Integer, Game> getGames() {
		Map<Integer, Game> res = new HashMap<Integer, Game>();
		for (Province p : getProvinces()) {
			for (Serie s : p.getSeries())
				for (Game g : s.getGames())
					res.put(g.getId(), g);
		}

		return res;
	}

}
