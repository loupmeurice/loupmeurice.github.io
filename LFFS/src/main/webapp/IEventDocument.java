package main.webapp;

import java.util.Calendar;

/**
 * Interface for iCalendar file generation
 */
public interface IEventDocument {

    /**
     * Defines the title of the event (iCalendar vEvent SUMMARY)
     *
     * @return title
     */
    String getTitle();


    /**
     * Start date of the event (iCalendar vEvent DTSTART)
     *
     * @return {@link Calendar} that is the start of the event
     */
    Calendar getDate();

    /**
     * End date of the event (iCalendar vEvent DTEND)
     *
     * @return {@link Calendar} that is the end of the event
     */
    Calendar getEndDate();

    /**
     * Event summary  (iCalendar vEvent DESCRIPTION)
     *
     * @return summary
     */
    String getSummary();

    /**
     * Is used for the iCalendar vEvent name
     *
     * @return name
     */
    String getName();

    /**
     * Copied from {@link org.hippoecm.hst.content.beans.standard.HippoDocumentBean}
     *
     * @return the uuid of the canonical handle (the physical node) and <code>null</code> if some exception happened
     */
    String getCanonicalHandleUUID();
}
