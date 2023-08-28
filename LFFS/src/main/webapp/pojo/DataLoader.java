package main.webapp.pojo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.mail.EmailException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import main.webapp.eventMgr.EventMgr;
import net.fortuna.ical4j.validate.ValidationException;

public class DataLoader {
	private static final int province_season_id = 7; //increment each year
	private static final int endYear = 2024; // to change
	private static Season season = null;
	private static Connection conn = null;

	private static final String AUTHORIZATION = "WP_Access eyJpdiI6Ikdoa0VQMkpMdkFQRUFpU0VkRXlTXC9BPT0iLCJ2YWx1ZSI6IktEMG9KRTMxRjMxMlJnKys2RSszV0FQbGxBU0pjZ2l4YnFlWGF4U00wQzRxbVFEcTJyTkVsaFwvcmxDZkVEYWZKIiwibWFjIjoiODg1YjgzMjY4ZmU2MDdhNGFlNmRmNGU4NWQxNTQwMDY2YmU2ZjU2MjY2YzRjZTA1MWRlNTU5NDIwZDQxYmNmMyJ9";

	public static void main2(String[] args) throws Exception {
		Province p = getNationaleSeason(0);
		System.out.println(p.getProvince());
		Team team = p.getTeam("BF DINANT".hashCode());
		for(Entry<Integer, List<Game>> entry : team.getGames().entrySet()) {
			System.out.println(p.getSerie(entry.getKey()).getSerie_name() + ":");
			for(Game g : entry.getValue()) {
//				Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(g.getDate() + ( (g.getTime().isEmpty()) ? "" :  " " + g.getTime()));
				System.out.println("   " + g.getDate() + " (" + g.getTime() + ") " + g.getHome_team().getName() + " vs " + g.getAway_team().getName());
			}
		}
		
//		EventMgr.subscribeTeam("BF DINANT".hashCode(), "loup_meurice001@hotmail.com");
		EventMgr.generateICSFile(team);
	}
	
	public static void main(String[] args)
			throws Exception {

		Province provNamur = (getNamurSeason(province_season_id, endYear));
		System.out.println(provNamur.getTeams().size());
		Team dinantC = null;
		
		for(Team t : provNamur.getTeams()) {
			System.out.println(t.getName());
			if(t.getName().equals("BETTER FOOT DINANT C")) {
				dinantC = t;
				break;
			}
		}
		
		for(Entry<Integer, List<Game>> entry : dinantC.getGames().entrySet()) {
			System.out.println(provNamur.getSerie(entry.getKey()).getSerie_name() + ":");
			for(Game g : entry.getValue()) {
//				Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(g.getDate() + ( (g.getTime().isEmpty()) ? "" :  " " + g.getTime()));
				System.out.println("   " + g.getDate() + " (" + g.getTime() + ") " + g.getHome_team().getName() + " vs " + g.getAway_team().getName());
			}
		}
		
//		EventMgr.subscribeTeam("BF DINANT".hashCode(), "loup_meurice001@hotmail.com");
		EventMgr.generateICSFile(dinantC);

	}

	private static Map<Integer, List<Game>> getTeamGame(Province p, String teamName) {
		for (Team t : p.getTeams()) {
			if (t.getName().equals(teamName)) {
				return t.getGames();
			}
		}

		return null;
	}

	public static Season getSeason(int endYear) throws Exception {
		Season res = null;
		try {
			res = getSeasonFromDB();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (res == null) {
			res = getSeasonFromLFFSWebsite();
			saveSeasonInDB(res);

		}

		return res;
	}

	public static Season getSeasonFromLFFSWebsite() throws Exception {
		Season res = new Season(endYear);
//		Province provNamur = (getNamurSeason(endYear));
//		res.addProvince(provNamur);
//		System.out.println("Namur done " + new Date());
//		Province provLiege = (getLiegeSeason(2020));
//		res.addProvince(provLiege);
//		System.out.println("Liege done " + new Date());
//		Province provHainaut = (getHainautSeason(2020));
//		res.addProvince(provHainaut);
//		System.out.println("Hainaut done " + new Date());
//		Province provLux = (getLuxembourgSeason(2020));
//		res.addProvince(provLux);
//		System.out.println("Luxembourg done " + new Date());
//		Province provBW_BXL = (getBrabantWallonBxl(2020));
//		res.addProvince(provBW_BXL);
		Province nationale = getNationaleSeason(endYear);
		res.addProvince(nationale);
		return res;
	}

	private static String test() {
		return "INSERT INTO Season (serialized, lastUpdate) VALUES(?,?);";
	}

	private static void saveSeasonInDB(Season res) {
		Connection conn = getConnection();
		String serialization = res.serialize();
		try {
			String sql = test();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, serialization);
			ps.setString(2, new Date().toString());

			// process the results
			ps.executeUpdate();
			ps.close();

			System.out.println("Season saved in db");
		} catch (SQLException se) {
			// log exception;
			se.printStackTrace();
		}

	}

	public static Season getSeasonFromDB() {
		Season res = null;
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		Connection conn = getConnection();

		Statement stmt;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT serialized FROM Season order by id desc limit 1;");
			if (rs.next()) {
				String serializedSeason = rs.getString("serialized");
				res = Season.deserialize(serializedSeason);

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return res;

	}

	public static Connection getConnection() {
		if (conn == null) {

			try {
				// db parameters
				String url = "jdbc:sqlite:C:/Users/lmeurice/eclipse-workspace-ee/App/src/main/resources/database.db";
				// create a connection to the database
				conn = DriverManager.getConnection(url);

				System.out.println("Connection to SQLite has been established.");

			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}
		return conn;
	}

	public static List<Game> getGaesAccordingToReferee(Integer ref_id, Season season) {

		List<Game> res = new ArrayList<Game>();
		for (Province p : season.getProvinces()) {
			Referee ref = p.getReferee(ref_id);
			if (ref != null)
				res.addAll(ref.getGames());
		}

		return res;

	}

	private static Ranking parseRanking(String rank, Serie s, Province p) {
		if (rank == null)
			return null;

		try {
			final JSONObject obj = new JSONObject(rank);
			Ranking res = new Ranking(s);
			JSONArray teams = getJSONArray(obj, "elements");
			if (teams != null) {
				for (int i = 0; i < teams.length(); i++) {
					if (teams.get(i) instanceof JSONObject) {
						JSONObject team = teams.getJSONObject(i);

						Integer position = getInt(team, "position");
						Integer team_id = getInt(team, "team_id");

						Team t = p.getTeam(team_id);
						if (t == null) {
							// equipe forfaite
							t = new Team();
							t.setId(team_id);
							t.setName(getString(team, "team_name"));
							t.setShort_name(getString(team, "team_short_name"));
							p.addTeam(t);

							int club_id = getInt(team, "club_id");
							Club club = p.getClub(club_id);
							if (club == null) {
								// le club n'est composé que d'une seule équipe qui est forfait général
								club = new Club();
								club.setId(club_id);
								club.setName(t.getName());
								p.addClub(club);
								club.addTeam(t);
							}

						}

						Team t2 = s.getTeam(team_id);
						if (t2 == null) {
							// cas ou une equipe est forfait général
							s.addTeam(t);
						}

						Integer played = getInt(team, "played");
						Integer wins = getInt(team, "wins");
						Integer losses = getInt(team, "losses");
						Integer draws = getInt(team, "draws");
						Integer forfeits = getInt(team, "forfeits");
						Integer goal_for = getInt(team, "score_for");
						Integer goal_against = getInt(team, "score_against");
						Integer points = getInt(team, "points");
						Integer wins_home = getInt(team, "wins_home");
						Integer wins_away = getInt(team, "wins_away");
						Integer losses_home = getInt(team, "losses_home");
						Integer losses_away = getInt(team, "losses_away");
						Integer draws_home = getInt(team, "draws_home");
						Integer draws_away = getInt(team, "draws_away");
						Integer forfeits_home = getInt(team, "forfeits_home");
						Integer forfeits_away = getInt(team, "forfeits_away");
						Integer score_for_home = getInt(team, "score_for_home");
						Integer score_for_away = getInt(team, "score_for_away");
						Integer score_against_home = getInt(team, "score_against_home");
						Integer score_against_away = getInt(team, "score_against_away");

						ChampionshipRank cr = new ChampionshipRank();
						cr.setPosition(position);
						cr.setTeam(t);
						cr.setSerie(s);
						cr.setPlayed(played);
						cr.setWins(wins);
						cr.setLosses(losses);
						cr.setDraws(draws);
						cr.setForfeits(forfeits);
						cr.setGoal_for(goal_for);
						cr.setGoal_against(goal_against);
						cr.setPoints(points);
						cr.setWins_home(wins_home);
						cr.setWins_away(wins_away);
						cr.setLosses_home(losses_home);
						cr.setLosses_away(losses_away);
						cr.setDraws_home(draws_home);
						cr.setDraws_away(draws_away);
						cr.setForfeits_home(forfeits_home);
						cr.setForfeits_away(forfeits_away);
						cr.setScore_for_home(score_for_home);
						cr.setScore_for_away(score_for_away);
						cr.setScore_against_home(score_against_home);
						cr.setScore_against_away(score_against_away);

						t.setRank(cr);
						res.addTeam(t);

					}
				}

			}

			return res;

		} catch (Exception | Error e) {
			e.printStackTrace();
		}
		return null;
	}

	private static Province parseProvince(String season, int startYear, int endYear, String province) {
		if (season == null)
			return null;
		try {
			final JSONObject obj = new JSONObject(season);
			Province res = new Province(startYear, endYear, province);
			JSONArray games = getJSONArray(obj, "elements");
			if (games != null) {
				for (int i = 0; i < games.length(); i++) {
					if (games.get(i) instanceof JSONObject) {
						JSONObject game = games.getJSONObject(i);
						parseGame(game, res);
						System.out.println(i + "/" + games.length());
					}
				}

			}

			retrieveGameInfo(res);
			retrieveSeriesRanking(res);

			return res;

		} catch (Exception | Error e) {
			e.printStackTrace();
		}
		return null;

	}

	private static String getString(JSONObject o, String field) {
		return o.has(field) && !o.isNull(field) ? o.getString(field) : null;
	}

	private static Integer getInt(JSONObject o, String field) {
		return o.has(field) && !o.isNull(field) ? o.getInt(field) : null;
	}

	private static JSONArray getJSONArray(JSONObject o, String field) {
		return o.has(field) ? o.getJSONArray(field) : null;
	}

	private static void parseGame(JSONObject game, Province province) {
		if (game != null) {
			String reference = getString(game, "reference");
			Integer id = getInt(game, "id");
			Integer week = getInt(game, "week");
			Integer serie_id = getInt(game, "serie_id");
			String date = getString(game, "date");
			String time = getString(game, "time");
			String serie_ref = getString(game, "serie_reference");
			String serie_name = getString(game, "serie_name");
			String serie_short_name = getString(game, "serie_short_name");

			String home_team_name = getString(game, "home_team_name");
			String home_team_short_name = getString(game, "home_team_short_name");
			Integer home_team_id = getInt(game, "home_team_id");
			Integer home_club_id = getInt(game, "home_club_id");
			String home_club_name = getString(game, "home_club_name");

			String away_team_name = getString(game, "away_team_name");
			String away_team_short_name = getString(game, "away_team_short_name");
			Integer away_team_id = getInt(game, "away_team_id");
			Integer away_club_id = getInt(game, "away_club_id");
			String away_club_name = getString(game, "away_club_name");

			Club home_club = province.getClub(home_club_id);
			if (home_club_id != null && home_club == null) {
				home_club = new Club();
				home_club.setId(home_club_id);
				home_club.setName(home_club_name);
				province.addClub(home_club);
			}

			Club away_club = province.getClub(away_club_id);
			if (away_club_id != null && away_club == null) {
				away_club = new Club();
				away_club.setId(away_club_id);
				away_club.setName(away_club_name);
				province.addClub(away_club);
			}

			Integer home_score = getInt(game, "home_score");
			Integer away_score = getInt(game, "away_score");
			Integer home_penalty = getInt(game, "home_penalties");
			Integer away_penalty = getInt(game, "away_penalties");

			Integer away_forfait = getInt(game, "away_forfeit_status_id");
			Integer home_forfait = getInt(game, "home_forfeit_status_id");

			Integer game_status = getInt(game, "game_status_id");

			JSONArray referees = getJSONArray(game, "referees");
			List<Referee> refs = new ArrayList<Referee>();
			if (referees != null) {
				for (int j = 0; j < referees.length(); j++) {
					if (referees.get(j) instanceof JSONObject) {
						JSONObject r = referees.getJSONObject(j);
						Integer ref_id = getInt(r, "member_id");
						String ref_firstname = getString(r, "firstname");
						String ref_lastname = getString(r, "surname");

						Referee ref = province.getReferee(ref_id);
						if (ref_id != null && ref == null) {

							ref = new Referee();
							ref.setId(ref_id);
							ref.setFirstName(ref_firstname);
							ref.setLastName(ref_lastname);
							province.addReferee(ref);
						}

						if (ref != null) {
							refs.add(ref);
						}
					}

				}
			}

			Integer venue_id = getInt(game, "venue_id");

			Game g = new Game();
			g.setId(id);
			g.setReference(reference);
			g.setWeek(week);
			g.setSerie_id(serie_id);
			g.setAway_score(away_score);
			g.setHome_score(home_score);
			g.setHome_penalty(home_penalty);
			g.setAway_penalty(away_penalty);
			g.setStatus(game_status);
			g.setRefs(refs);
			for (Referee ref : refs)
				ref.addGame(g);

			if (home_forfait != null && home_forfait == 1)
				g.setHomeForfait(true);
			if (away_forfait != null && away_forfait == 1)
				g.setAwayForfait(true);

			g.setBye(getString(game, "free_away_team") != null || getString(game, "free_home_team") != null);

			g.setDate(date);
			g.setTime(time);
			g.setVenue_id(venue_id);

			Team home_team = province.getTeam(home_team_id);
			if (home_team_id != null && home_team == null) {
				home_team = new Team();
				home_team.setId(home_team_id);
				home_team.setName(home_team_name);
				home_team.setShort_name(home_team_short_name);
				province.addTeam(home_team);
			}

			Team away_team = province.getTeam(away_team_id);
			if (away_team_id != null && away_team == null) {

				away_team = new Team();
				away_team.setId(away_team_id);
				away_team.setName(away_team_name);
				away_team.setShort_name(away_team_short_name);
				province.addTeam(away_team);
			}

			g.setHome_team(home_team);
			g.setAway_team(away_team);

			System.out.println("match:" + home_team_name + "==>" + away_team_name);
			System.out.println("match--:" + home_team + "::" + away_team);

			Venue v = province.getVenue(venue_id);
			if (venue_id != null && v == null) {
				v = new Venue();
				v.setId(venue_id);
				province.addVenue(v);
			}
			g.setVenue_id(venue_id);
			g.setVenue(v);

			Serie serie = province.getSerie(serie_id);
			if (serie_id != null && serie == null) {
				serie = new Serie(serie_id);
				serie.setSerie_name(serie_name);
				serie.setSerie_short(serie_short_name);
				serie.setSerie_reference(serie_ref);
				province.addSerie(serie);
			}

			g.setSerie(serie);
			if (serie != null) {
				serie.addGame(g);

				if (home_team != null) {
					serie.addTeam(home_team);
					home_team.addGame(g);
				}

				if (away_team != null) {
					serie.addTeam(away_team);
					away_team.addGame(g);
				}
			}

			if (home_team != null && home_club != null) {
				home_club.addTeam(home_team);
				home_team.setClub(home_club);
			}

			if (away_team != null && away_club != null) {
				away_club.addTeam(away_team);
				away_team.setClub(away_club);
			}

		}

	}
	
	public static Province getNationaleSeason(int endYear) throws IOException, ParseException {
		Province province = new Province(endYear - 1, endYear, "Nationale");
		
		for(int week = 1; week <= 53; week++) {
			Document doc = Jsoup.connect("https://automaticresults.azurewebsites.net/bzvb.cshtml?weeklist=" + week).get();
			Elements elements = doc.select("td.wedstr");
			Set<Element> divisions = new HashSet<Element>();
			for(int i = 0; i < elements.size(); i++) {
				divisions.add(elements.get(i).closest("tbody"));
			}
			
			for(Element div : divisions) {
				String serieName = (div.selectFirst("th.klassement").text());
				Serie serie = province.getSerie(serieName.hashCode());
				if(serie == null) {
					serie = new Serie(serieName.hashCode());
					serie.setSerie_name(serieName);
					province.addSerie(serie);
				}
				Elements rencontres = div.select("> tr");
				for(int i = 2; i < rencontres.size(); i++) {
					Element rencontre = rencontres.get(i);
					Elements details = rencontre.select("> td");
					String date = details.get(2).text();
					String heure = details.get(3).text();
					if(heure.trim().isEmpty())
						heure = "0000";
					if(heure.length() == 4) {
						heure = heure.charAt(0) + "" + heure.charAt(1) + ":" + heure.charAt(2) + "" + heure.charAt(3) + ":00";
					}
					
					String ref = details.get(4).text();
					String address = (ref != null && !ref.trim().isEmpty() ? "http://www.automaticresults.be/maps/zaal.aspx?pr=10&id=" + ref: null);
					
					String home_team = details.get(5).text();
					String away_team = details.get(6).text();
					String score = details.get(7).text();
					
					String arbitres = details.get(9).text();
					System.out.println(date + "(" + heure + "): " + home_team + " vs " + away_team + " referees:" + arbitres);
				    Game game = new Game();
				    game.setId((date + ":" + home_team + " vs " + away_team).hashCode());
				    Date d = new SimpleDateFormat("dd-MM-yy").parse(date);
				    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");  
				    date = dateFormat.format(d); 
				    game.setStatus(Game.TO_PLAY);
				    game.setSerie(serie);
				    game.setDate(date);
				    game.setTime(heure);
				    game.setGoogleMapsAddressForNationalLeague(address);
				    Referee referee = province.getReferee(arbitres.hashCode());
				    if(referee == null) {
				    	referee = new Referee();
				    	referee.setLastName(arbitres);
				    	referee.setFirstName("");
				    	referee.setId(arbitres.hashCode());
				    	province.addReferee(referee);
				    }
				    
				    Team homeTeam = province.getTeam(home_team.hashCode());
				    if(homeTeam == null) {
				    	homeTeam = new Team();
				    	homeTeam.setId(home_team.hashCode());
				    	homeTeam.setName(home_team);
				    	province.addTeam(homeTeam);
				    }
				    Team awayTeam = province.getTeam(away_team.hashCode());
				    if(awayTeam == null) {
				    	awayTeam = new Team();
				    	awayTeam.setId(away_team.hashCode());
				    	awayTeam.setName(away_team);
				    	province.addTeam(awayTeam);
				    }
				    
				    serie.addTeam(homeTeam);
				    serie.addTeam(awayTeam);
				    
				    game.setHome_team(homeTeam);
				    game.setAway_team(awayTeam);
				    homeTeam.addGame(game);
				    awayTeam.addGame(game);
				}
			}
		}
		
		return province;
	}
	

	public static Province getNamurSeason(int season_id, int endYear) {
		String provStr = getProvinceSeason("" + season_id, "5", endYear);
		return parseProvince(provStr, endYear - 1, endYear, "Namur");
	}

	public static Province getLiegeSeason(int season_id, int endYear) {
		String provStr = getProvinceSeason("" + season_id, "3", endYear);
		return parseProvince(provStr, endYear - 1, endYear, "Liège");
	}

	public static Province getHainautSeason(int season_id, int endYear) {
		String provStr = getProvinceSeason("" + season_id, "2", endYear);
		return parseProvince(provStr, endYear - 1, endYear, "Hainaut");
	}

	public static Province getLuxembourgSeason(int season_id, int endYear) {
		String provStr = getProvinceSeason("" + season_id, "4", endYear);
		return parseProvince(provStr, endYear - 1, endYear, "Luxembourg");
	}

	public static Province getBrabantWallonBxl(int season_id, int endYear) {
		String provStr = getProvinceSeason("" + season_id, "1", endYear);
		return parseProvince(provStr, endYear - 1, endYear, "BW-BXL");
	}

	private static String getProvinceSeason(String season_id, String organization_id, int endYear) {
		int startYear = endYear - 1;
		String url = "https://gestion.lffs.eu/lms_league_ws/public/api/v1/game/byMyLeague?with_referees=true&no_forfeit=true&season_id=3&sort[0]=date&sort[1]=time&organization_id="
				+ organization_id + "&season_id=" + season_id + "&start_date=" + startYear + "-08-01&end_date="
				+ endYear + "-12-01";
		System.out.println(url);
		return execute(url);

	}

	public static void retrieveGameInfo(Province p) {
		int i = 0;
		List<Venue> venues = p.getVenues();
		for (Venue v : venues) {
			String info = getGameInfo(v.getId());
			parseVenue(info, v);
			i++;
		}

	}

	public static void retrieveSeriesRanking(Province p) {
		int i = 0;
		for (Serie s : p.getSeries()) {
			getRanking(s, p);
			i++;
		}
	}

	private static void parseVenue(String info, Venue v) {
		if (v == null || info == null)
			return;

		try {
			final JSONObject obj = new JSONObject(info);

			if (obj.has("data")) {
				JSONObject data = obj.getJSONObject("data");
				String name = getString(data, "name");
				String phone = getString(data, "phone");
				String mobile = getString(data, "mobile");
				String street = getString(data, "street");
				String street2 = getString(data, "street2");
				String zip = getString(data, "zip");
				String city = getString(data, "city");
				String country = getString(data, "country");
				String lng = getString(data, "lng");
				String lat = getString(data, "lat");
				Room room = new Room();
				room.setName(name);
				room.setPhone(phone);
				room.setMobile(mobile);
				room.setStreet(street);
				room.setStreet2(street2);
				room.setZip(zip);
				room.setCity(city);
				room.setCountry(country);
				room.setLng(lng);
				room.setLat(lat);
				v.setRoom(room);
			}

		} catch (Exception | Error e) {
			e.printStackTrace();
		}

	}

	public static String getGameInfo(Integer venue_id) {
		String url = "https://gestion.lffs.eu/lms_league_ws/public/api/v1/venue/" + venue_id;
		return execute(url);
	}

	private static Ranking getRanking(Serie s, Province p) {
		String url = "https://gestion.lffs.eu/lms_league_ws/public/api/v1/ranking/byMyLeague?serie_id=" + s.getId();
		String rank = execute(url);
		Ranking r = parseRanking(rank, s, p);
		return r;
	}

	private static String execute(String url) {
		HttpGet request = new HttpGet(url);
		request.setHeader(HttpHeaders.AUTHORIZATION, AUTHORIZATION);

		HttpClient client = HttpClientBuilder.create().build();
		HttpResponse response;
		try {
			response = client.execute(request);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				return EntityUtils.toString(entity, "UTF-8");
			}
		} catch (Exception | Error e) {
			e.printStackTrace();
		}

		return null;

	}

	public static synchronized Season getSeason() throws Exception {
		if (season == null) {
			season = getSeason(endYear);
		}
		return season;
	}

	public static void setSeason(Season s) {
		season = s;
	}

}
