package main.webapp.eventMgr;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;

import main.webapp.pojo.DataLoader;
import main.webapp.pojo.Game;
import main.webapp.pojo.Room;
import main.webapp.pojo.Season;
import main.webapp.pojo.Serie;
import main.webapp.pojo.Team;
import main.webapp.pojo.changes.GameChange;
import main.webapp.pojo.changes.NewGame;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.validate.ValidationException;

public class EventMgr {

	public static void main(String[] args) throws ValidationException, EmailException, IOException {
		Team t = new Team();
		t.setName("DEPORTIVO NEFFE AA");
		String msg = "Vous trouverez ci-joint le calendrier de l'équipe de " + t.getName()
				+ ".\n\nCe calendrier contient les informations des matchs auxquels votre équipe participera cette saison (matchs de championnat, coupe et amicaux).\n\nEn ouvrant la pièce jointe (.ics), vous rajouterez automatiquement tous les matchs à votre agenda.\n De plus, vous serez tenu au courant des éventuels changements liés à votre équipe (match déplacé, reporté, annulé, changement d'adresse, ...).\n\nBonne saison ami sportif!";
		String subject = "Calendrier de " + t.getName();
		Set<String> emails = new HashSet<String>();
//		emails.add("loup_meurice001@hotmail.com");
		emails.add("violaine.sironval@gmail.com");
		sendSimpleMail(subject, msg, "C:/Users/lmeurice/Desktop/test.ics", emails);
	}
	
	public static void generateICSFile(Team team) throws IOException {
		if (team != null) {
			System.out.println("want to subscribe to " + team.getName() + " calendar");
			List<String> events = new ArrayList<String>();
			for (Entry<Integer, List<Game>> entry : team.getGames().entrySet()) {
				Integer serie_id = entry.getKey();
				List<Game> games = entry.getValue();
				for (Game game : games) {
					if (game.getStatus() == Game.TO_PLAY)
						events.add(getVEvent(game));
				}
			}

			String filePath = createCalendarICSFile(events);
			System.out.println("ICS File: " + filePath);

		}
	}

	public static void subscribeTeam(int team_id, String email)
			throws Exception {

		Season s = DataLoader.getSeason();
		Team team = s.getTeam(team_id);

		if (team != null) {
			System.out.println("want to subscribe to " + team.getName() + " calendar");
			List<String> events = new ArrayList<String>();
			for (Entry<Integer, List<Game>> entry : team.getGames().entrySet()) {
				Integer serie_id = entry.getKey();
				List<Game> games = entry.getValue();
				for (Game game : games) {
					System.out.println(game.getDate());
					if (game.getStatus() == Game.TO_PLAY)
						events.add(getVEvent(game));
				}
			}

			if (events.size() > 0)
				sendEmail(team, events, email);

		}

	}

	private static void sendEmail(Team team, List<String> events, String email)
			throws IOException, ValidationException, EmailException {
		String filePath = createCalendarICSFile(events);

		String msg = "Vous trouverez ci-joint le calendrier de l'équipe de " + team.getName()
				+ ".\n\nCe calendrier contient les informations des matchs auxquels votre équipe participera cette saison (matchs de championnat, coupe et amicaux).\n\nEn ouvrant la pièce jointe (.ics), vous rajouterez automatiquement tous les matchs à votre agenda.\n De plus, vous serez tenu au courant des éventuels changements liés à votre équipe (match déplacé, reporté, annulé, changement d'adresse, ...).\n\nBonne saison ami sportif!";
		String subject = "Calendrier de " + team.getName();
		Set<String> emails = new HashSet<String>();
		emails.add(email);
		sendSimpleMail(subject, msg, filePath, emails);

	}

	private static void sendSimpleMail(String subject, String message, String icsFile, Set<String> receivers)
			throws EmailException, ValidationException, IOException {

		MultiPartEmail email = new MultiPartEmail();
		String authuser = "meuriceloup@gmail.com";
		String authpwd = "PASSWORD";
		email.setSmtpPort(587);
		email.setAuthenticator(new DefaultAuthenticator(authuser, authpwd));
		email.setDebug(true);
		email.setHostName("smtp.gmail.com");
		email.getMailSession().getProperties().put("mail.smtps.auth", "true");
		email.getMailSession().getProperties().put("mail.debug", "false");
		email.getMailSession().getProperties().put("mail.smtps.port", "587");
		email.getMailSession().getProperties().put("mail.smtps.socketFactory.port", "587");
		email.getMailSession().getProperties().put("mail.smtps.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		email.getMailSession().getProperties().put("mail.smtps.socketFactory.fallback", "false");
		email.getMailSession().getProperties().put("mail.smtp.starttls.enable", "true");
		email.setFrom("meuriceloup@gmail.com", "LFFS");
		email.setSubject(subject);
		email.setMsg(message);

		for (String receiver : receivers)
			try {
				email.addBcc(receiver);
			} catch (Exception e) {
				// invalid email address
			}

		if (icsFile != null) {
			EmailAttachment attachment = new EmailAttachment();
			attachment.setPath(icsFile);
			attachment.setDisposition(EmailAttachment.ATTACHMENT);
			attachment.setDescription("Calendar");
			attachment.setName("calendar.ics");
			email.attach(attachment);
		}
		email.send();
		System.out.println("email sent");
	}

	public static String createCalendarICSFile(List<String> events) throws IOException {
		String calendarICS = createCalendarICSContent(events);
		File file = File.createTempFile("LFFSCalendar", ".ics");
		Files.write(Paths.get(file.getAbsolutePath()), calendarICS.getBytes());
		System.out.println(file.getAbsolutePath());
		return file.getAbsolutePath();
	}

	private static String createCalendarICSContent(List<String> events) {
		String content = "BEGIN:VCALENDAR\nVERSION:2.0\nPRODID:-//LFFS//Calendar//EN\nCALSCALE:GREGORIAN\nMETHOD:REQUEST\n";

		for (String event : events)
			content += event;

		content += "END:VCALENDAR";
		return content;
	}

	private static String getVEvent(Game game) {
		return new NewGame(null, game).getVEvent();
	}

	public static void sendChangeEventEmail(GameChange change, Set<String> emails) {
		if (emails.size() == 0)
			return;

		try {
			String icsFile = change.getICSFile();
			sendSimpleMail(change.getSubject(), change.getMessage(), icsFile, emails);
		} catch (ValidationException | EmailException | IOException e) {
			e.printStackTrace();
		}

	}

}
