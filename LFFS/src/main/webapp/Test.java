package main.webapp;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.commons.mail.SimpleEmail;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.util.FixedUidGenerator;
import net.fortuna.ical4j.util.RandomUidGenerator;
import net.fortuna.ical4j.util.UidGenerator;
import net.fortuna.ical4j.validate.ValidationException;

public class Test {

	public static void main(String[] args) throws EmailException, ValidationException, IOException {
		// https://myaccount.google.com/lesssecureapps
//		String icsFile = generateEvent();
		sendSimpleMail("C:/Users/lmeurice/Desktop/test.ics");
	}


	private static VEvent getEvent(int year, int month, int day, int hour, int min, String description) {
		// start time
		java.util.Calendar startCal = java.util.Calendar.getInstance();
		startCal.set(year, month, day, hour, min);


		String subject = "Meeting Subject";
		String location = "Location - Mumbai";

		String hostEmail = "admin@javaxp.com";

		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyyMMdd'T'hhmmss'Z'");
		String strDate = sdFormat.format(startCal.getTime());

		net.fortuna.ical4j.model.Date startDt = null;
		try {
			startDt = new net.fortuna.ical4j.model.Date(strDate, "yyyyMMdd'T'hhmmss'Z'");
		} catch (ParseException e) {
			e.printStackTrace();
		}


//						Dur dur = new Dur(0, 0, min, 0);

		// Creating a meeting event
		VEvent meeting = new VEvent(startDt, subject);

		meeting.getProperties().add(new Location(location));
		meeting.getProperties().add(new Description());
		RandomUidGenerator  g = new RandomUidGenerator();
		
		
//		e7382d72-2fb4-4534-ae7a-e8d0d27fc845
//		Uid uid = g.generateUid();
		Uid uid = new Uid("e7382d72-2fb4-4534-ae7a-e8d0d27fc845");
		System.out.println(uid);
//		if(true)
//			System.exit(0);
		
		System.out.println(uid.getValue());
		
		meeting.getProperties().add(uid);

		try {
			((Property) meeting.getProperties().getProperty(Property.DESCRIPTION)).setValue(description);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		try {
			meeting.getProperties().add(new Organizer("MAILTO:" + hostEmail));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		return meeting;

	}

	private static String generateEvent() {
		// Initilize values
		String calFile = "C:/Users/lmeurice/Desktop/calendar.ics";
		// Creating a new calendar
		net.fortuna.ical4j.model.Calendar calendar = new net.fortuna.ical4j.model.Calendar();
		calendar.getProperties().add(new ProdId("-//Ben Fortuna//iCal4j 1.0//EN"));
		calendar.getProperties().add(Version.VERSION_2_0);
		calendar.getProperties().add(CalScale.GREGORIAN);

		VEvent meeting = getEvent(2020, 5, 23, 20, 45, "description 1");
//		VEvent meeting2 = getEvent(2020, 5, 2, 15, 30, "description 2");

		calendar.getComponents().add(meeting);
//		calendar.getComponents().add(meeting2);

		FileOutputStream fout = null;

		try {
			fout = new FileOutputStream(calFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		CalendarOutputter outputter = new CalendarOutputter();
		outputter.setValidating(false);

		try {
			outputter.output(calendar, fout);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ValidationException e) {
			e.printStackTrace();
		}

		System.out.println(meeting);
		return calFile;
	}

	private static void sendSimpleMail(String icsFile) throws EmailException, ValidationException, IOException {

		EmailAttachment attachment = new EmailAttachment();
		attachment.setPath(icsFile);
		attachment.setDisposition(EmailAttachment.ATTACHMENT);
		attachment.setDescription("Calendar");
		attachment.setName("calendar.ics");

		MultiPartEmail email = new MultiPartEmail();
		String authuser = "meuriceloup@gmail.com";
		String authpwd = "titinne88";
		email.setSmtpPort(587);
		email.setAuthenticator(new DefaultAuthenticator(authuser, authpwd));
		email.setDebug(true);
		email.setHostName("smtp.gmail.com");
		email.getMailSession().getProperties().put("mail.smtps.auth", "true");
		email.getMailSession().getProperties().put("mail.debug", "true");
		email.getMailSession().getProperties().put("mail.smtps.port", "587");
		email.getMailSession().getProperties().put("mail.smtps.socketFactory.port", "587");
		email.getMailSession().getProperties().put("mail.smtps.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		email.getMailSession().getProperties().put("mail.smtps.socketFactory.fallback", "false");
		email.getMailSession().getProperties().put("mail.smtp.starttls.enable", "true");
		email.setFrom("meuriceloup@gmail.com", "SenderName");
		email.setSubject("TestMail");
		email.setMsg("This is a test mail?");
		email.addTo("loup_meurice001@hotmail.com", "ToName");

		email.attach(attachment);
		email.send();
		System.out.println("email sent");
	}

}
