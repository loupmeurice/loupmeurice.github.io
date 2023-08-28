package main.webapp.pojo.changes;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import main.webapp.eventMgr.EventMgr;
import main.webapp.pojo.Game;
import main.webapp.pojo.Room;

public abstract class GameChange {
	protected Game game;
	
	protected GameChange(Game prev, Game cur) {
		if(prev != null)
			cur.setIcalSequence(prev.getIcalSequence());
		this.setGame(cur);
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}
	
	public String getVEvent() {
		return null;
	}
	
	public abstract String getMessage();
	
	public abstract String getSubject();
	
	/**
	 * 
	 * @param date yyyy-mm-dd
	 * @param time hh:mm:ss
	 * @return
	 */
	protected static String fomatICSDate(String date, String time) {
		try {
			Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date + " " + time);
			DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
			return dateFormat.format(d);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}

	}

	public String getICSFile() {
		String event = getVEvent();
		if(event == null)
			return null;
		
		List<String> events = new ArrayList<String>();
		events.add(event);
		String filePath;
		try {
			filePath = EventMgr.createCalendarICSFile(events);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return filePath;
	}


}
