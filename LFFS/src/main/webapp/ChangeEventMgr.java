package main.webapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections4.functors.NonePredicate;

import main.webapp.eventMgr.EventMgr;
import main.webapp.pojo.DataLoader;
import main.webapp.pojo.Game;
import main.webapp.pojo.Season;
import main.webapp.pojo.Team;
import main.webapp.pojo.changes.DelayedGame;
import main.webapp.pojo.changes.ForfaitGame;
import main.webapp.pojo.changes.GameChange;
import main.webapp.pojo.changes.NewGame;
import main.webapp.pojo.changes.NonExistingGame;
import main.webapp.pojo.changes.NonPlayedGame;
import main.webapp.pojo.changes.PlayedGame;
import main.webapp.pojo.changes.StoppedGame;

public class ChangeEventMgr {

	public static void main(String[] args) throws Exception {
		Season season = DataLoader.getSeason();
		for(Game g : season.getGames().values())
			if(g.getStatus() == Game.TO_PLAY) {
				g.setDate("2020-05-20");
				System.out.println("ok");
				break;
			}

		Season s2 = DataLoader.getSeasonFromDB();

//		Season newSeason = DataLoader.getSeasonFromLFFSWebsite();
		List<GameChange> changes = compareSeasons(s2, season);
		for (GameChange ch : changes)
			System.out.println(ch.getMessage());

		communicateChanges(changes);
		
		
		changes = compareSeasons(season, s2);
		for (GameChange ch : changes)
			System.out.println(ch.getMessage());

		communicateChanges(changes);


	}

	private static final int CHECK_TIME_MIN = 2;

	public void initThread() {
		final int ms = CHECK_TIME_MIN * 60 * 1000;

		new Thread() {

			@Override
			public void run() {
				while (true) {
					try {
						sleep(ms);
						detectChanges();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}

		}.start();
	}

	public static void detectChanges() throws Exception {
		Season s = DataLoader.getSeasonFromLFFSWebsite();
		Season currentSeason = DataLoader.getSeason();
		List<GameChange> changes = compareSeasons(currentSeason, s);
		communicateChanges(changes);
	}

	private static void communicateChanges(List<GameChange> changes) {
		for (GameChange change : changes) {
			Team home_team = change.getGame().getHome_team();
			Team away_team = change.getGame().getAway_team();
			Set<String> emails = getSubscribedUsers(home_team);
			emails.addAll(getSubscribedUsers(away_team));
			EventMgr.sendChangeEventEmail(change, emails);
		}

	}

	private static Set<String> getSubscribedUsers(Team team) {
		Set<String> res = new HashSet<String>();
		if (team == null)
			return res;

		// TODO
		res.add("violaine.sironval@gmail.com");
		return res;
	}

	private static List<GameChange> compareSeasons(Season current, Season latest) {

		Map<Integer, Game> currentGames = current.getGames();
		Map<Integer, Game> latestGames = latest.getGames();

		List<GameChange> changes = new ArrayList<GameChange>();

		for (Entry<Integer, Game> entry : currentGames.entrySet()) {
			int game_id = entry.getKey();
			Game currentGame = entry.getValue();
			Game latestGame = latestGames.get(game_id);
			if (latestGame == null) {
				// the game seems to be definitely cancelled
				NonExistingGame cancelledGame = new NonExistingGame(currentGame);
				changes.add(cancelledGame);
			} else {
				// check if game status has changed
				int currentStatus = currentGame.getStatus();
				int latestStatus = latestGame.getStatus();
				GameChange change = null;
				if (currentStatus != latestStatus) {
					// status change
					if (latestStatus == Game.TO_PLAY) {
						// match reprogrammé
						change = new NewGame(currentGame, latestGame);
					}

					if (latestStatus == Game.STOPPED) {
						// match arrêté
						change = new StoppedGame(currentGame, latestGame);
					}

					if (latestStatus == Game.DELAYED) {
						// match remis
						change = new DelayedGame(currentGame, latestGame);
					}

					if (latestStatus == Game.NOT_PLAYED) {
						// match non joué
						change = new NonPlayedGame(currentGame, latestGame);
					}

					if (latestStatus == Game.PLAYED) {
						// match joué
						change = new PlayedGame(currentGame, latestGame);
					}
				} else {
					if (currentStatus == Game.TO_PLAY) {
						// check if game info has changed (ex: date or hour change)
						String currentDate = currentGame.getDate();
						String currentTime = currentGame.getTime();
						Integer currentVenue = currentGame.getVenue_id();

						String latestDate = latestGame.getDate();
						String latestTime = latestGame.getTime();
						Integer latestVenue = latestGame.getVenue_id();

						if (!equals(currentDate, latestDate) || !equals(currentTime, latestTime)
								|| !equals(currentVenue, latestVenue)) {
							change = new NewGame(currentGame, latestGame);
						}
					}

					if (currentStatus == Game.PLAYED) {
						// check if forfait
						if (!currentGame.isAwayForfait() && !currentGame.isHomeForfait()
								&& (latestGame.isAwayForfait() || latestGame.isHomeForfait())) {
							change = new ForfaitGame(currentGame, latestGame);
						} else {
							if ((latestGame.isAwayForfait() || latestGame.isHomeForfait())
									&& (latestGame.isAwayForfait() != currentGame.isAwayForfait()
											|| latestGame.isHomeForfait() != currentGame.isHomeForfait())) {
								// le forfait initial a changé de camp
								change = new ForfaitGame(currentGame, latestGame);
							} else {
								// check if score changed
								if (!currentGame.getPrintableScore().equals(latestGame.getPrintableScore()))
									change = new PlayedGame(currentGame, latestGame);
							}

						}
					}
				}

				if (change != null)
					changes.add(change);
			}

		}

		////////////////
		for (Entry<Integer, Game> entry : latestGames.entrySet()) {
			int game_id = entry.getKey();
			Game latestGame = entry.getValue();
			Game currentGame = currentGames.get(game_id);
			if (currentGame == null) {
				// nouveau match programmé
				changes.add(new NewGame(null, latestGame));
			}
		}

		return changes;

	}

	private static boolean equals(Integer a, Integer b) {
		if (a == null && b != null)
			return false;
		if (a != null && b == null)
			return false;

		if (a == null && b == null)
			return true;

		return a.intValue() == b.intValue();

	}

	private static boolean equals(String a, String b) {
		if (a == null && b != null)
			return false;
		if (a != null && b == null)
			return false;

		if (a == null && b == null)
			return true;

		return a.equals(b);
	}

}
